package io.mateu.ui.core.server;

import io.mateu.ui.core.shared.Data;
import org.hsqldb.jdbc.JDBCDataSource;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by miguel on 1/1/17.
 */
public class ServerSideHelper {

    private static JDBCDataSource jdbcDataSource;

    public static Object[][] select(String sql) throws Exception {
        Object[][] r = null;
        Connection conn = null;
        try {

            conn = getConn();

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
                r = aux.toArray(new Object[0][0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                conn.close();
            }
            throw e;
        }

        return r;
    }

    public static void execute(String sql) throws Exception {
        Connection conn = null;

        try {

            conn = getConn();
            System.out.println("Sql execute: " + sql);
            if (conn.getAutoCommit()) {
                //System.out.println("Sentencia con autocommit desactivado...");
                conn.setAutoCommit(false);
                try {
                    Statement s = conn.createStatement();
                    s.execute(sql);
                    conn.commit();
                } catch (Exception e) {
                    conn.rollback();
                    e.printStackTrace();
                }
                conn.setAutoCommit(true);
            } else {
                //System.out.println("Sentencia sin autocommit ...");
                try {
                    Statement s = conn.createStatement();
                    s.execute(sql);
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

    public static Object selectSingleValue(String sql) throws Exception {
        Object o = null;
        Object[][] r = select(sql);
        if (r.length > 0 && r[0].length > 0) o = r[0][0];
        return o;
    }

    public static void update(String sql) throws Exception {
        Connection conn = null;

        try {

            conn = getConn();
            //System.out.println("Sql Update: " + sql);
            if (conn.getAutoCommit()) {
                //System.out.println("Sentencia con autocommit desactivado...");
                conn.setAutoCommit(false);
                try {
                    Statement s = conn.createStatement();
                    s.executeUpdate(sql);
                    conn.commit();
                } catch (Exception e) {
                    conn.rollback();
                    e.printStackTrace();
                }
                conn.setAutoCommit(true);
            } else {
                //System.out.println("Sentencia sin autocommit ...");
                try {
                    Statement s = conn.createStatement();
                    s.executeUpdate(sql);
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

    public static int getNumberOfRows(String sql) {
        int total = 0;
        if (!isEmpty(sql)) {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return total;
    }

    public static Object[][] selectPage(String sql, int desdeFila, int numeroFilas) throws Exception {
        return select(sql + " LIMIT " + numeroFilas + " OFFSET " + desdeFila);
    }


    private static boolean isEmpty(String s) {
        return s == null || "".equals(s.trim());
    }

    public static Connection getConn() throws Exception {
        return getDataSource().getConnection();
    }

    public static  DataSource getDataSource() throws Exception {
        if (jdbcDataSource == null) {
            jdbcDataSource = ((ServerSideApp)Class.forName(ServerSideHelper.read(EditorViewControllerRegistry.class.getResourceAsStream("/META-INF/services/io.mateu.ui.serversideapp"))).newInstance()).getJdbcDataSource();
            System.out.println("Datasource available");
        }
        return jdbcDataSource;
    }

    public static void runSqlBatch(String b) throws Exception {

        long t0 = new Date().getTime();

        b = b.replaceAll("\\/\\*.*\\*\\/", "");

        Connection conn = null;

        try {

            conn = getConn();
            //System.out.println("Sql Update: " + sql);
            if (conn.getAutoCommit()) {
                //System.out.println("Sentencia con autocommit desactivado...");
                conn.setAutoCommit(false);
                try {
                    Statement s = conn.createStatement();
                    for (String l : b.split("\\;")) {
                        s.executeUpdate(l.trim());
                    }
                    conn.commit();
                } catch (Exception e) {
                    conn.rollback();
                    e.printStackTrace();
                }
                conn.setAutoCommit(true);
            } else {
                //System.out.println("Sentencia sin autocommit ...");
                try {
                    Statement s = conn.createStatement();
                    for (String l : b.split("\\;")) {
                        s.executeUpdate(l.trim());
                    }
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

        System.out.println("sql batch run in " + (new Date().getTime() - t0) + " ms.");

    }

    public static String read(InputStream is) {
        return read(is, "utf-8");
    }

    private static String read(InputStream is, String encoding) {
        StringBuffer s = new StringBuffer();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, encoding));
            String l = null;
            boolean primeraLinea = true;
            while ((l = br.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                } else {
                    s.append("\n");
                }
                s.append(l);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s.toString();
    }

    public static Data getFromSql(String sql) throws Exception {
        Data r = null;
        Connection conn = null;
        try {

            conn = getConn();

            System.out.println("sql: " + sql); //prettySQLFormat(sql));

            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(sql);
            if (rs != null) {
                ResultSetMetaData rsmd = rs.getMetaData();
                while (rs.next()) {
                    if (r == null) {
                        r = new Data();

                        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                            r.set(rsmd.getColumnName(i).toLowerCase(), rs.getObject(i));
                            if ("id".equalsIgnoreCase(rsmd.getColumnName(i))) r.set("_id", rs.getObject(i));
                        }

                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                conn.close();
            }
            throw e;
        }

        return r;

    }

    public static void setToSql(Data data, String tableName) throws Exception {

        if (data != null) {

            Connection conn = null;

            try {

                conn = getConn();

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
                        if (!n.startsWith("_")) {
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

                if (conn.getAutoCommit()) {
                    //System.out.println("Sentencia con autocommit desactivado...");
                    conn.setAutoCommit(false);
                    try {
                        Statement s = conn.createStatement();
                        s.execute(sql);
                        conn.commit();
                    } catch (Exception e) {
                        conn.rollback();
                        e.printStackTrace();
                    }
                    conn.setAutoCommit(true);
                } else {
                    //System.out.println("Sentencia sin autocommit ...");
                    try {
                        Statement s = conn.createStatement();
                        s.execute(sql);
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

    }
}
