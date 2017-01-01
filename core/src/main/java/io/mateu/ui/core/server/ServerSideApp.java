package io.mateu.ui.core.server;

import org.hsqldb.jdbc.JDBCDataSource;

/**
 * Created by miguel on 1/1/17.
 */
public interface ServerSideApp {

    public JDBCDataSource getJdbcDataSource() throws Exception;


}
