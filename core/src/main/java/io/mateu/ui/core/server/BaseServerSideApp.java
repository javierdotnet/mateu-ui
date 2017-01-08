package io.mateu.ui.core.server;

import io.mateu.ui.core.shared.BaseService;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.FileLocator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by miguel on 7/1/17.
 */
public abstract class BaseServerSideApp implements ServerSideApp {

    private static DataSource jdbcDataSource;


    @Override
    public BaseService getBaseServiceImpl() {
        return null;
    }

    @Override
    public Object[][] select(String sql) throws Exception {
        final Object[][][] r = {null};

        notransact(new SQLTransaction() {
            @Override
            public void run(Connection conn) throws Exception {

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
    public void execute(String sql) throws Exception {

        transact(new SQLTransaction() {
            @Override
            public void run(Connection conn) throws Exception {

                Statement s = conn.createStatement();
                s.execute(sql);

            }
        });

    }

    @Override
    public Object selectSingleValue(String sql) throws Exception {
        Object o = null;
        Object[][] r = select(sql);
        if (r.length > 0 && r[0].length > 0) o = r[0][0];
        return o;
    }

    @Override
    public void update(String sql) throws Exception {

        transact(new SQLTransaction() {
            @Override
            public void run(Connection conn) throws Exception {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return total;
    }

    @Override
    public Object[][] selectPage(String sql, int desdeFila, int numeroFilas) throws Exception {
        return select(sql + " LIMIT " + numeroFilas + " OFFSET " + desdeFila);
    }

    public Connection getConn() throws Exception {
        return getDataSource().getConnection();
    }

    public DataSource getDataSource() throws Exception {
        if (jdbcDataSource == null) {
            jdbcDataSource = getJdbcDataSource();
            System.out.println("Datasource available");
        }
        return jdbcDataSource;
    }

    public abstract DataSource getJdbcDataSource() throws Exception;


    @Override
    public void runSqlBatch(String b) throws Exception {

        long t0 = new Date().getTime();

        transact(new SQLTransaction() {

            @Override
            public void run(Connection conn) throws Exception {

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
    public FileLocator upload(byte[] bytes) {
        return null;
    }

    @Override
    public void notransact(SQLTransaction t) throws Exception {
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
    public void transact(SQLTransaction t) throws Exception {

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
    public Data getFromSql(String sql) throws Exception {
        final Data[] r = {null};

        notransact(new SQLTransaction() {
            @Override
            public void run(Connection conn) throws Exception {

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
    public void setToSql(Data data, String tableName) throws Exception {

        if (data != null) {

            transact(new SQLTransaction() {
                @Override
                public void run(Connection conn) throws Exception {

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


                    Statement s = conn.createStatement();
                    s.execute(sql);

                }
            });

        }

    }

}
