package io.github.company;

import java.util.concurrent.ExecutionException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import io.github.company.servlets.ConnectServlet;
import io.github.company.servlets.DisconnectServlet;
import io.github.company.servlets.QueryServlet;

public class CompanyServer {
    private static int port = 3000;
    private Server server;

    public CompanyServer() throws InterruptedException, ExecutionException {
        // Create Jetty server
        server = new Server(port);

        // Create context handler for servlets
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Add CORS filter
        context.addFilter(new FilterHolder(new CORSFilter()), "/*", null);

        // Add servlets
        context.addServlet(new ServletHolder(new ConnectServlet()), "/connect");
        context.addServlet(new ServletHolder(new QueryServlet("SELECT * FROM SAMPLE.DEPARTMENT")), "/departments");
        context.addServlet(new ServletHolder(new QueryServlet("SELECT * FROM SAMPLE.DEPARTMENT WHERE DEPTNO = ?")), "/departments/*");
        context.addServlet(new ServletHolder(new QueryServlet("SELECT * FROM SAMPLE.EMPLOYEE")), "/employees");
        context.addServlet(new ServletHolder(new QueryServlet("SELECT * FROM SAMPLE.EMPLOYEE WHERE EMPNO = ?")), "/employees/*");
        context.addServlet(new ServletHolder(new QueryServlet("SELECT * FROM SAMPLE.SALES")), "/sales");
        context.addServlet(new ServletHolder(new QueryServlet("SELECT * FROM SAMPLE.SALES WHERE SALES_PERSON = ?")), "/sales/*");
        context.addServlet(new ServletHolder(new DisconnectServlet()), "/disconnect");
    }

    public void start() throws Exception {
        System.out.println("Started company server on port " + String.valueOf(CompanyServer.port) + "...");
        server.start();
        server.join();
    }

    public void stop() throws Exception {
        System.out.println("Stopped company server on port " + String.valueOf(CompanyServer.port) + "...");
        server.stop();
        server.join();
    }
}
