package io.mateu.ui;

import org.eclipse.jetty.server.Dispatcher;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class MiContextListener  implements ServletContextListener {


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
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
}
