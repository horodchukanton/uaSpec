package com.anykey.uaspec.WebServer;

/**
 * Created by anykey 10.05.2015
 **/

import com.anykey.uaspec.utils.Globals;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class HttpServer {
    public HttpServer(String root, int port) throws Exception {

        System.out.println("Using web root:" + root);

        Server server = new Server();

        ServerConnector http = new ServerConnector(server);
        http.setPort(port);
        http.setIdleTimeout(30000);

        server.addConnector(http);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        ServletHolder holderMonitor = new ServletHolder("monitor", MonitorServlet.class);
        context.addServlet(holderMonitor, "/monitor");

        ServletHolder holderCom = new ServletHolder("com", ComServlet.class);
        context.addServlet(holderCom, "/com");

//        ServletHolder holderOutput = new ServletHolder("output", OutputServlet.class);
//        context.addServlet(holderOutput, "/output");

        ServletHolder holderPwd = new ServletHolder("default", DefaultServlet.class);
        holderPwd.setInitParameter("resourceBase", root);

        holderPwd.setInitParameter("dirAllowed", "true");
        context.addServlet(holderPwd, "/");

        server.start();
        server.join();
    }

    @SuppressWarnings("serial")
    public static class MonitorServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request,
                             HttpServletResponse response) throws ServletException,
                IOException {
            long millis = System.currentTimeMillis();
            MonitorWriter writer = new MonitorWriter();

            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            response.setCharacterEncoding("UTF-8");

            response.getWriter().println(writer.getResponse(request, response));
            //response.getWriter().println(writer.getResponse(request));
            if (Globals.isDebug())
            System.out.println("Time taken to return monitor string:" + (System.currentTimeMillis() - millis));
        }

        protected void doPost(HttpServletRequest request,
                              HttpServletResponse response) throws ServletException, IOException {
            long millis = System.currentTimeMillis();
            MonitorWriter writer = new MonitorWriter();
            writer.setMonitorSettings(request);
            if (Globals.isDebug())
            System.out.println("Time taken to add a point: " + (System.currentTimeMillis() - millis));
        }
    }

    @SuppressWarnings("serial")
    public static class ComServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request,
                             HttpServletResponse response) throws ServletException,
                IOException {
            ComWriter writer = new ComWriter();

            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println(writer.getResponse(request));
        }
    }

//    @SuppressWarnings("serial")
//    public static class OutputServlet extends HttpServlet {
//        @Override
//        protected void doGet(HttpServletRequest request,
//                             HttpServletResponse response) throws ServletException,
//                IOException {
//            OutputWriter writer = new OutputWriter();
//
//            response.setContentType("text/html");
//            response.setStatus(HttpServletResponse.SC_OK);
//            response.setCharacterEncoding("UTF-8");
//
//            response.getWriter().println(writer.getResponse(request));
//        }
//
//        protected void doPost(HttpServletRequest request,
//                              HttpServletResponse response) throws ServletException, IOException {
//            doGet(request, response);
//        }
//    }

    public static class CommandServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request,
                             HttpServletResponse response) throws ServletException,
                IOException {
            CommandReader writer = new CommandReader();

            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            response.setCharacterEncoding("UTF-8");

            response.getWriter().println(writer.getResponse(request));
        }

        protected void doPost(HttpServletRequest request,
                              HttpServletResponse response) throws ServletException, IOException {
            doGet(request, response);
        }
    }
}