package io.mateu.ui.core.server;

import io.mateu.ui.core.shared.BaseService;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.FileLocator;
import org.hsqldb.jdbc.JDBCDataSource;
import org.reflections.Reflections;

import javax.sql.DataSource;
import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.*;

/**
 * Created by miguel on 1/1/17.
 */
public class ServerSideHelper {

    private static ServerSideApp serverSideApp;

    public static ServerSideApp getServerSideApp() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (serverSideApp == null) {
            serverSideApp = (ServerSideApp) Class.forName(Utils.read(EditorViewControllerRegistry.class.getResourceAsStream("/META-INF/services/io.mateu.ui.core.server.ServerSideApp"))).newInstance();
        }
        return serverSideApp;
    }

    public static <T> T findImplementation(Class<T> serviceClass) {

        T s = null;

            String pn = serviceClass.getPackage().getName();
            String[] pnts = pn.split("\\.");
            if (pnts.length > 2) {
                pn = pnts[0] + "." + pnts[1];
            }
            Reflections reflections = new Reflections(pn);
            Set<Class<? extends T>> r = reflections.getSubTypesOf(serviceClass);
            if (!r.isEmpty()) try {
                s = r.iterator().next().newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            return s;
    }
}
