package io.mateu.ui.sample.server;

import io.mateu.ui.core.server.BaseServerSideApp;
import io.mateu.ui.core.server.ServerSideApp;
import io.mateu.ui.core.server.Utils;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.FileLocator;
import io.mateu.ui.core.shared.UserData;
import org.hsqldb.jdbc.JDBCDataSource;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by miguel on 1/1/17.
 */
public class SampleApp extends BaseServerSideApp implements ServerSideApp {
    private static JDBCDataSource jdbcDataSource;
    private static boolean populated;

    public DataSource getJdbcDataSource() throws Throwable {
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


    private void populate(JDBCDataSource jdbcDataSource) throws Throwable {

        runSqlBatch(Utils.read(SampleApp.class.getResourceAsStream("data.sql")));
        runSqlBatch(Utils.read(SampleApp.class.getResourceAsStream("sacramento.sql")));

    }

    @Override
    public UserData authenticate(String login, String password) throws Exception {
        if (login == null || "".equals(login.trim())) throw new Exception("Login can not be empty");
        else if (password == null || "".equals(password.trim())) throw new Exception("Password can not be empty");
        else if (!password.equalsIgnoreCase(login)) throw new Exception("Wrong password");
        else {
            UserData d = new UserData();
            d.setLogin(login);
            d.setName("Miguel Pérez");
            d.setEmail("miguelperezcolom@gmail.com");
            d.setPermissions(Arrays.asList(1, 2, 3));
            d.setData(new Data());
            return d;
        }
    }

    @Override
    public void changePassword(String login, String oldPassword, String newPassword) throws Exception {
        if (!login.equals(oldPassword)) throw new Exception("Present password is wrong");
    }

    @Override
    public void updateProfile(String login, String name, String email, FileLocator foto) throws Exception {

    }

    @Override
    public void updateFoto(String login, FileLocator foto) {

    }
}
