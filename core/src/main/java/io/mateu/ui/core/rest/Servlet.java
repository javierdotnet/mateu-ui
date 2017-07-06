package io.mateu.ui.core.rest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Created by miguel on 6/6/17.
 */
@WebServlet(urlPatterns = {"/servidor/*"})
public class Servlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        System.out.println("uri:" + req.getRequestURI());
        System.out.println("url:" + req.getRequestURL());

        resp.addHeader("Access-Control-Allow-Origin", "*");
        System.out.println("method: " + req.getMethod());
        if ("OPTIONS".equals(req.getMethod())) {
            resp.addHeader("Access-Control-Allow-Methods", req.getHeader("Access-Control-Request-Method"));
            resp.addHeader("Access-Control-Allow-Headers", req.getHeader("Access-Control-Request-Headers"));
            resp.addHeader("Access-Control-Max-Age", "86400");
        } else {
            resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            resp.setHeader("Pragma", "no-cache");
            resp.setHeader("Expires", "0");
            String body = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

            System.out.println(body);

            /*

            Map<String, Object> m = Helper.fromJson(body);

            System.out.println("m:" + Helper.toJson(m));

            String uri = req.getRequestURI();
            try {

                if (uri.endsWith("combopax")) {
                    resp.getWriter().print(Logic.comboPax(m));
                } else if (uri.endsWith("checkdispo")) {
                    resp.getWriter().print(Logic.checkDisponibilidad(m));
                } else if (uri.endsWith("entradas")) {
                    resp.getWriter().print(Logic.getEntradasAlternativas(m));
                } else if (uri.endsWith("confirmar")) {
                    resp.getWriter().print(Logic.confirmar(m));
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new ServletException(e);
            }

            */

            resp.getWriter().print("Hola!!!");

        }

    }
}
