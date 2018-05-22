package io.mateu.ui;

import com.google.common.base.Strings;
import org.eclipse.jetty.server.Dispatcher;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

@WebListener
public class MiContextListener  implements ServletContextListener {

    public static boolean propertiesLoaded = false;

    static {
        loadProperties();
    }


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        if (Strings.isNullOrEmpty(System.getProperty("tmpdir"))) {
            System.setProperty("tmpdir", servletContextEvent.getServletContext().getRealPath("/tmp"));
        }

        if (Strings.isNullOrEmpty(System.getProperty("tmpurl"))) {
            System.setProperty("tmpurl", servletContextEvent.getServletContext().getContextPath() + "/tmp");
        }

        ServletContext c = servletContextEvent.getServletContext();
        for (String s : c.getServletRegistrations().keySet()) {
            System.out.println("reg:" + s + ":" + c.getServletRegistration(s).getClassName());
            for (String m : c.getServletRegistration(s).getMappings()) {
                System.out.println("mapping:" + m);
            }
        }
        for (String s : c.getResourcePaths("/")) {
            System.out.println("path:" + s);
        }

        for (String s : new String[]{"/resources/pickupconfirmation/q?&p=wedwedwdwed", "/resources/fugfgy/hotel/available?dd=221"}) {
            System.out.println("dispatcher(" + s + "):" + c.getRequestDispatcher(s).getClass().getName());
        }

        System.out.println(c.getRequestDispatcher("/resources/pickupconfirmation/q?&p=wedwedwdwed"));

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }


    public static void loadProperties() {
        if (!propertiesLoaded) {
            System.out.println("Loading properties from MiContextListener...");
            propertiesLoaded = true;
            InputStream s = null;
            try {
                if (System.getProperty("appconf") != null) {
                    System.out.println("Loading properties from file " + System.getProperty("appconf"));
                    s = new FileInputStream(System.getProperty("appconf"));
                } else {
                    s = MiContextListener.class.getResourceAsStream("/appconf.properties");
                    System.out.println("Loading properties classpath /appconf.properties");
                }

                if (s != null) {

                    Properties p = new Properties();
                    p.load(s);

                    for (Map.Entry<Object, Object> e : p.entrySet()) {
                        System.out.println("" + e.getKey() + "=" + e.getValue());
                        if (System.getProperty("" + e.getKey()) == null) {
                            System.setProperty("" + e.getKey(), "" + e.getValue());
                            System.out.println("property fixed");
                        } else {
                            System.out.println("property " + e.getKey() + " is already set with value " + System.getProperty("" + e.getKey()));
                        }
                    }

                } else {
                    System.out.println("No appconf. Either set -Dappconf=xxxxxx.properties or place an appconf.properties file in your classpath.");
                }

            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        } else {
            System.out.println("Properties already loaded");
        }
    }

}
