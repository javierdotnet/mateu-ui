package io.mateu.ui.core.server;

import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.FileLocator;
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

    private static ServerSideApp serverSideApp;

    public static ServerSideApp getServerSideApp() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (serverSideApp == null) {
            serverSideApp = (ServerSideApp) Class.forName(Utils.read(EditorViewControllerRegistry.class.getResourceAsStream("/META-INF/services/io.mateu.ui.serversideapp"))).newInstance();
        }
        return serverSideApp;
    }

}
