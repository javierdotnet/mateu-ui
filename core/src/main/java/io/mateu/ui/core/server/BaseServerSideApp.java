package io.mateu.ui.core.server;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.ColumnAlignment;
import io.mateu.ui.core.client.views.AbstractListView;
import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.shared.BaseService;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.FileLocator;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.fop.apps.*;
import org.apache.poi.hssf.usermodel.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.SAXException;

import javax.sql.DataSource;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by miguel on 7/1/17.
 */
public abstract class BaseServerSideApp implements ServerSideApp {

    private static DataSource jdbcDataSource;
    private static long fileId;


    @Override
    public BaseService getBaseServiceImpl() {
        return null;
    }

    @Override
    public Object[][] select(String sql)throws Throwable {
        final Object[][][] r = {null};

        notransact(new SQLTransaction() {
            @Override
            public void run(Connection conn)throws Exception {

                System.out.println("sql: " + sql); //prettySQLFormat(sql));

                Statement s = conn.createStatement();
                ResultSet rs = s.executeQuery(sql);
                if (rs != null) {
                    ResultSetMetaData rsmd = rs.getMetaData();
                    List<Object[]> aux = new ArrayList<Object[]>();
                    int fila = 0;
                    while (rs.next()) {
                        if (fila > 0 && fila % 1000 == 0) System.out.println("filas =" + fila + ":SQL>>>" + sql.replaceAll("\\n", " ") + "<<<SQL");
                        fila++;
                        Object[] f = new Object[rsmd.getColumnCount()];
                        for (int i = 0; i < rsmd.getColumnCount(); i++) {
                            f[i] = rs.getObject(i + 1);
                        }
                        aux.add(f);
                    }
                    r[0] = aux.toArray(new Object[0][0]);
                }

            }
        });

        return r[0];
    }

    @Override
    public void execute(String sql)throws Throwable {

        transact(new SQLTransaction() {
            @Override
            public void run(Connection conn)throws Exception {

                Statement s = conn.createStatement();
                s.execute(sql);

            }
        });

    }

    @Override
    public Object selectSingleValue(String sql)throws Throwable {
        Object o = null;
        Object[][] r = select(sql);
        if (r.length > 0 && r[0].length > 0) o = r[0][0];
        return o;
    }

    @Override
    public void update(String sql)throws Throwable {

        transact(new SQLTransaction() {
            @Override
            public void run(Connection conn)throws Exception {
                Statement s = conn.createStatement();
                s.executeUpdate(sql);
            }
        });

    }

    @Override
    public int getNumberOfRows(String sql) {
        int total = 0;
        if (!Utils.isEmpty(sql)) {
            try {

                if (sql.contains("/*noenelcount*/")) {
                    String sqlx = "";
                    boolean z = true;
                    for (String s : sql.split("/\\*noenelcount\\*/")) {
                        if (z) sqlx += s;
                        z = !z;
                    }
                    sql = sqlx;
                }

                sql = sql.replaceAll("aquilapaginacion", "");

                String aux = "select count(*) from (" + sql + ") x";
                total = ((Long) selectSingleValue(aux)).intValue();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return total;
    }

    @Override
    public Object[][] selectPage(String sql, int desdeFila, int numeroFilas)throws Throwable {
        return select(sql + " LIMIT " + numeroFilas + " OFFSET " + desdeFila);
    }

    public Connection getConn()throws Throwable {
        return getDataSource().getConnection();
    }

    public DataSource getDataSource()throws Throwable {
        if (jdbcDataSource == null) {
            jdbcDataSource = getJdbcDataSource();
            System.out.println("Datasource available");
        }
        return jdbcDataSource;
    }

    public abstract DataSource getJdbcDataSource()throws Throwable;


    @Override
    public void runSqlBatch(String b)throws Throwable {

        long t0 = new Date().getTime();

        transact(new SQLTransaction() {

            @Override
            public void run(Connection conn)throws Exception {

                String bx = b.replaceAll("\\/\\*.*\\*\\/", "");

                Statement s = conn.createStatement();
                for (String l : bx.split("\\;")) {
                    s.executeUpdate(l.trim());
                }

            }
        });

        System.out.println("sql batch run in " + (new Date().getTime() - t0) + " ms.");

    }

    @Override
    public FileLocator upload(String fileName, byte[] bytes, boolean temporary)throws Throwable {
        long id = fileId++;
        String extension = ".tmp";
        if (fileName == null || "".equals(fileName.trim())) fileName = "" + id;
        if (fileName.lastIndexOf(".") < fileName.length() - 1) {
            extension = fileName.substring(fileName.lastIndexOf("."));
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }
        File temp = File.createTempFile(fileName, extension);
        Utils.write(temp, bytes);
        return new FileLocator(id, temp.getName(), temp.getAbsolutePath(), temp.getAbsolutePath());
    }

    @Override
    public void notransact(SQLTransaction t)throws Throwable {
        Connection conn = null;
        try {

            conn = getConn();

            t.run(conn);

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                conn.close();
            }
            throw e;
        }

    }

    @Override
    public void transact(SQLTransaction t)throws Throwable {

        Connection conn = null;

        try {

            conn = getConn();
            //System.out.println("Sql Update: " + sql);
            if (conn.getAutoCommit()) {
                //System.out.println("Sentencia con autocommit desactivado...");
                conn.setAutoCommit(false);
                try {

                    t.run(conn);

                    conn.commit();
                } catch (Exception e) {
                    conn.rollback();
                    e.printStackTrace();
                }
                conn.setAutoCommit(true);
            } else {
                //System.out.println("Sentencia sin autocommit ...");
                try {

                    t.run(conn);

                    conn.commit();
                } catch (Exception e) {
                    conn.rollback();
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                conn.close();
            }
            throw e;
        }

    }

    @Override
    public Data getFromSql(String sql)throws Throwable {
        final Data[] r = {null};

        notransact(new SQLTransaction() {
            @Override
            public void run(Connection conn)throws Exception {

                System.out.println("sql: " + sql); //prettySQLFormat(sql));

                Statement s = conn.createStatement();
                ResultSet rs = s.executeQuery(sql);
                if (rs != null) {
                    ResultSetMetaData rsmd = rs.getMetaData();
                    while (rs.next()) {
                        if (r[0] == null) {
                            r[0] = new Data();

                            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                                r[0].set(rsmd.getColumnName(i).toLowerCase(), rs.getObject(i));
                                if ("id".equalsIgnoreCase(rsmd.getColumnName(i))) r[0].set("_id", rs.getObject(i));
                            }

                        }
                        break;
                    }
                }

            }
        });

        return r[0];

    }

    @Override
    public void setToSql(Data data, String tableName)throws Throwable {

        if (data != null) {

            transact(new SQLTransaction() {
                @Override
                public void run(Connection conn)throws Exception {

                    String sql = "";

                    if (data.get("_id") == null) {
                        sql += "insert into " + tableName + " (";
                        int pos = 0;
                        Collection<String> ns = data.getPropertyNames();
                        for (String n : data.getPropertyNames()) {
                            if (!n.startsWith("_") && !"id".equalsIgnoreCase(n)) {
                                sql += n;
                                if (pos < ns.size() - 1) sql += ",";
                            }
                            pos++;
                        }
                        sql+= ") values (";
                        pos = 0;
                        for (String n : ns) {
                            if (!n.startsWith("_") && !"id".equalsIgnoreCase(n)) {
                                Object v = data.get(n);
                                if (v == null) {
                                    sql += "null";
                                } else {
                                    if (v instanceof String) sql += "'" + ((String)v).replaceAll("'", "''") + "'";
                                    else sql += v;
                                }
                                if (pos < ns.size() - 1) sql += ",";
                            }
                            pos++;
                        }
                        sql += ")";
                    } else {
                        sql += "update " + tableName + " set ";
                        int pos = 0;
                        Collection<String> ns = data.getPropertyNames();
                        for (String n : ns) {
                            if (!n.startsWith("_") && !"id".equalsIgnoreCase(n)) {
                                sql += n;
                                sql += "=";
                                Object v = data.get(n);
                                if (v == null) {
                                    sql += "null";
                                } else {
                                    if (v instanceof String) sql += "'" + ((String)v).replaceAll("'", "''") + "'";
                                    else sql += v;
                                }
                                if (pos < ns.size() - 1) sql += ",";
                            }
                            pos++;
                        }
                        sql += " where id = " + data.get("_id");
                    }

                    System.out.println("setToSql: " + sql);


                    Statement s = conn.createStatement();
                    s.execute(sql);

                }
            });

        }

    }

    @Override
    public URL dump(Data parameters)throws Throwable {
        AbstractListView lv = null;
        if (parameters.containsKey("_metadata")) lv = (AbstractListView) Class.forName(parameters.getString("_listview")).getDeclaredConstructor(Data.class).newInstance((Data)parameters.get("_metadata"));
        else lv = (AbstractListView) Class.forName(parameters.getString("_listview")).newInstance();

        if ("excel".equalsIgnoreCase(parameters.get("_format"))) {
            return listToExcel(parameters, lv);
        } else {
            return listToPdf(parameters, lv);
        }
    }

    public URL listToPdf(Data parameters, AbstractListView view)throws Throwable {

        String xslfo = ServerSideHelper.getServerSideApp().getXslfoForListing();

        long t0 = new Date().getTime();


        try {


            Document xml = new Document();
            Element arrel = new Element("root");
            xml.addContent(arrel);


            Element cab = new Element("header");
            arrel.addContent(cab);

            Element lineas = new Element("lines");
            arrel.addContent(lineas);

            int xx = 1;
            int pixels = 0;

            for (AbstractColumn c : view.getColumns()){
                String alineado = "left";
                Element aux = new Element("column");
                cab.addContent(aux);
                aux.setAttribute("label", c.getLabel());
                aux.setAttribute("width", "" +  c.getWidth() / 1.5);
                if (ColumnAlignment.CENTER.equals(c.getAlignment())) alineado = "center";
                if (ColumnAlignment.RIGHT.equals(c.getAlignment())) alineado = "right";
                aux.setAttribute("align", alineado);
                pixels += c.getWidth();
                //System.out.println("clase:" + c.getClase());
            }

            String ancho = "21cm";
            String alto = "29.7cm";
            if (pixels > 750){
                alto = "21cm";
                ancho = "29.7cm";
            }
            arrel.setAttribute("width", ancho);
            arrel.setAttribute("height", alto);



            Data[] r = new Data[1];
            Exception[] ex = new Exception[1];

            /*

            view.rpc(parameters, new AsyncCallback<Data>() {
                @Override
                public void onFailure(Throwable caught) {
                    caught.printStackTrace();
                    ex[0] = new Exception(caught);
                }

                @Override
                public void onSuccess(Data result) {
                    r[0] = result;
                }
            });

            synchronized(r){
                r.wait(120000);
            }
            */

            view.rpc(parameters, new AsyncCallback<Data>() {
                @Override
                public void onFailure(Throwable caught) {
                    caught.printStackTrace();
                }

                @Override
                public void onSuccess(Data result) {
                    r[0] = result;
                }
            });

            if (ex[0] != null) throw ex[0];

            List<Data> data = r[0].getList("_data");


            for (Data x : data){

                Element linea = new Element("line");
                lineas.addContent(linea);

                for (AbstractColumn c : view.getColumns()){

                    Element cell = new Element("cell");
                    linea.addContent(cell);
                    Object v = x.get(c.getId());
                    String text = "";
                    if (v != null) text += v;
                    if (v instanceof Double){
                        DecimalFormat dfm = new DecimalFormat("#0.00");
                        text = dfm.format(((Double)v));
                    }
                    cell.setText(text);

                }

            }

            if (data.size() >= 5000) {
                Element linea = new Element("line");
                lineas.addContent(linea);

                Element txt = new Element("cell");
                linea.addContent(txt);

                txt.setText("HAY MAS DE 5000 LINEAS. CONTACTA CON EL DEPARTAMENTO DE DESARROLLO SI QUIERES EL EXCEL COMPLETO...");
            }

            try {
                String archivo = UUID.randomUUID().toString();

                File temp = (System.getProperty("tmpdir") == null)?File.createTempFile(archivo, ".pdf"):new File(new File(System.getProperty("tmpdir")), archivo + ".pdf");


                System.out.println("java.io.tmpdir=" + System.getProperty("java.io.tmpdir"));
                System.out.println("Temp file : " + temp.getAbsolutePath());

                FileOutputStream fileOut = new FileOutputStream(temp);
                String sxml = new XMLOutputter(Format.getPrettyFormat()).outputString(xml);
                System.out.println("xslfo=" + xslfo);
                System.out.println("xml=" + sxml);
                fileOut.write(fop(new StreamSource(new StringReader(xslfo)), new StreamSource(new StringReader(sxml))));
                fileOut.close();

                String baseUrl = System.getProperty("tmpurl");
                if (baseUrl == null) {
                    return temp.toURI().toURL();
                }
                return new URL(baseUrl + "/" + temp.getName());

            } catch (IOException e) {
                e.printStackTrace();
            }


        } catch (Exception e1) {
            e1.printStackTrace();
        }


        return null;
    }

    public static byte[] fop(Source xslfo, Source xml) throws IOException, SAXException {
        long t0 = new Date().getTime();


// Step 1: Construct a FopFactory by specifying a reference to the configuration file
// (reuse if you plan to render multiple documents!)

        FopFactoryBuilder builder = new FopFactoryBuilder(new File(".").toURI());
        builder.setStrictFOValidation(false);
        builder.setBreakIndentInheritanceOnReferenceAreaBoundary(true);
        builder.setSourceResolution(96); // =96dpi (dots/pixels per Inch)
        FopFactory fopFactory = builder.build();
        //FopFactory fopFactory = FopFactory.newInstance(new File("C:/Temp/fop.xconf"));


        // Step 2: Set up output stream.
// Note: Using BufferedOutputStream for performance reasons (helpful with FileOutputStreams).
        //OutputStream out = new BufferedOutputStream(new FileOutputStream(new File("C:/Temp/myfile.pdf")));
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
             // Step 3: Construct fop with desired output format
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

            // Step 4: Setup JAXP using identity transformer
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(xslfo); // identity transformer

		    /*
		    StreamResult xmlOutput = new StreamResult(new StringWriter());
		    //transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		    //transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		    transformer.transform(src, xmlOutput);

		    System.out.println(xmlOutput.getWriter().toString());
		    */

            // Step 5: Setup input and output for XSLT transformation
            // Setup input stream
            //Source src = new StreamSource(new StringReader(xml));

            // Resulting SAX events (the generated FO) must be piped through to FOP
            Result res = new SAXResult(fop.getDefaultHandler());

            // Step 6: Start XSLT transformation and FOP processing
            transformer.transform(xml, res);

        } catch (FOPException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } finally {
            //Clean-up
            out.close();
        }

        return out.toByteArray();
    }


    public URL listToExcel(Data parameters, AbstractListView view)throws Throwable {
        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFCellStyle cellStyleDate = workbook.createCellStyle();
        cellStyleDate.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));

        HSSFSheet sheet = workbook.createSheet("Report");

        HSSFRow row = null;
        HSSFCell cell = null;

        //LINEA SUPERIOR
        int numfila = 0;
        int numcol = 0;

        numcol = 0;
        row = sheet.createRow(numfila++);

        for (AbstractColumn c : view.getColumns()){
            cell = row.createCell(numcol++);
            cell.setCellValue(c.getLabel());
        }

		Data[] r = new Data[1];
		Exception[] ex = new Exception[1];


/*
		view.rpc(parameters, new AsyncCallback<Data>() {
            @Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
                ex[0] = new Exception(caught);
                synchronized(r){
                    r.notify();
                }
            }

            @Override
            public void onSuccess(Data result) {
                r[0] = result;
                synchronized(r){
                    r.notify();
                }
            }
        });

        synchronized(r){
            r.wait(120000);
        }
 */

        view.rpc(parameters, new AsyncCallback<Data>() {
            @Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
                ex[0] = new Exception(caught);
            }

            @Override
            public void onSuccess(Data result) {
                r[0] = result;
            }
        });

		if (ex[0] != null) throw ex[0];

		List<Data> data = r[0].getList("_data");

        for (Data x : data){

            row = sheet.createRow(numfila++);

            numcol = 0;
            for (AbstractColumn c : view.getColumns()){
                cell = row.createCell(numcol++);

                Object o = x.get(c.getId());

                if (o == null) cell.setCellValue((String) null);
				else if (o instanceof String) cell.setCellValue((String) o);
				else if (o instanceof Double) cell.setCellValue((Double)o);
				else if (o instanceof BigDecimal) cell.setCellValue(((BigDecimal)o).doubleValue());
				else if (o instanceof Integer) cell.setCellValue(new Double((Integer) o));
				else if (o instanceof BigInteger) cell.setCellValue(((BigInteger) o).doubleValue());
				else if (o instanceof Long) cell.setCellValue(new Double((Long) o));
				else if (o instanceof Date) {
					  cell.setCellStyle(cellStyleDate);
					  cell.setCellValue((Date) o);
				} else if (o instanceof Boolean) cell.setCellValue((Boolean)o);
				else cell.setCellValue("" + o);

            }

            if (numfila >= 65530) break;

        }

        if (data.size() >= 65530) {
            row = sheet.createRow(numfila++);
            cell = row.createCell(0);
            cell.setCellValue("HAY MAS DE 65535 LINEAS. CONTACTA CON EL DEPARTAMENTO DE DESARROLLO SI QUIERES EL EXCEL COMPLETO...");
        }

        try {
            String archivo = UUID.randomUUID().toString();

            File temp = (System.getProperty("tmpdir") == null)?File.createTempFile(archivo, ".xls"):new File(new File(System.getProperty("tmpdir")), archivo + ".xls");


            System.out.println("java.io.tmpdir=" + System.getProperty("java.io.tmpdir"));
            System.out.println("Temp file : " + temp.getAbsolutePath());

            System.out.println(System.getProperty("java.io.tmpdir"));

            FileOutputStream fileOut = new FileOutputStream(temp);
            workbook.write(fileOut);
            fileOut.close();

            String baseUrl = System.getProperty("tmpurl");
            if (baseUrl == null) {
                return temp.toURI().toURL();
            }
            return new URL(baseUrl + "/" + temp.getName());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getXslfoForListing()throws Throwable {
        return Resources.toString(Resources.getResource(BaseServerSideApp.class, "listing.xsl"), Charsets.UTF_8);
    }
}
