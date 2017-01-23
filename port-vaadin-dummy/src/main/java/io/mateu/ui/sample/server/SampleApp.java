package io.mateu.ui.sample.server;

import io.mateu.ui.core.server.BaseServerSideApp;
import io.mateu.ui.core.server.ServerSideApp;
import io.mateu.ui.core.server.Utils;
import org.hsqldb.jdbc.JDBCDataSource;

import javax.sql.DataSource;
import java.util.Date;

/**
 * Created by miguel on 1/1/17.
 */
public class SampleApp extends BaseServerSideApp implements ServerSideApp {
    private static JDBCDataSource jdbcDataSource;
    private static boolean populated;

    public DataSource getJdbcDataSource() throws Exception {
        if (jdbcDataSource == null) {
            System.out.println("Creating datasource");
            jdbcDataSource = new JDBCDataSource();
            jdbcDataSource.setDatabaseName("mem:.");
            jdbcDataSource.setUser("SA");
            jdbcDataSource.setPassword("");

            System.out.println("Datasource available");
        }
        if (!populated) {
            populated = true;
            System.out.println("populating database");
            long t0 = new Date().getTime();
            populate(jdbcDataSource);
            System.out.println("database populated in " + (new Date().getTime() - t0) + " ms.");
        }
        return jdbcDataSource;
    }


    private void populate(JDBCDataSource jdbcDataSource) throws Exception {

        runSqlBatch(Utils.read(SampleApp.class.getResourceAsStream("data.sql")));

    }
}
