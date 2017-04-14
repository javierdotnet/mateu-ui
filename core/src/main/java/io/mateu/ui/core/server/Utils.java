package io.mateu.ui.core.server;

import com.google.common.io.Files;
import io.mateu.ui.core.shared.Data;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Collection;

/**
 * Created by miguel on 7/1/17.
 */
public class Utils {

    public static boolean isEmpty(String s) {
        return s == null || "".equals(s.trim());
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

    public static Data getFromSql(String sql) throws Throwable {
        final Data[] r = {null};

        ServerSideHelper.getServerSideApp().notransact(new SQLTransaction() {
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

    public static void setToSql(Data data, String tableName) throws Throwable {

        if (data != null) {

            ServerSideHelper.getServerSideApp().transact(new SQLTransaction() {
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

                }
            });

        }

    }

    public static void write(String fn, byte[] bytes) {
        File f = new File(fn);
        write(f, bytes);
    }

    public static void write(File f, byte[] bytes) {
        System.out.println("writing " + f.getAbsolutePath());
        FileOutputStream w;
        try {
            if (!f.getParentFile().exists()) f.getParentFile().mkdirs();
            w = new FileOutputStream(f);
            w.write(bytes);
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] readBytes(String fn) throws IOException {
        return readBytes(new File(fn));
    }

    public static byte[] readBytes(File f) throws IOException {
        System.out.println("writing " + f.getAbsolutePath());
        return Files.asByteSource(f).read();
    }

}
