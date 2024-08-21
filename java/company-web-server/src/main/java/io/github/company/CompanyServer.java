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
    private Server server;

    public CompanyServer() throws InterruptedException, ExecutionException {
        // Create Jetty server on port 8080
        server = new Server(8080);

        // Create context handler for servlets
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Add CORS filter
        context.addFilter(new FilterHolder(new CORSFilter()), "/*", null);

        // Add servlets
        context.addServlet(new ServletHolder(new ConnectServlet()), "/connect");
        context.addServlet(new ServletHolder(new QueryServlet("SELECT * FROM SAMPLE.DEPARTMENT")), "/departments");
        context.addServlet(new ServletHolder(new QueryServlet("SELECT * FROM SAMPLE.EMPLOYEE")), "/employees");
        context.addServlet(new ServletHolder(new QueryServlet("SELECT * FROM SAMPLE.SALES")), "/sales");
        context.addServlet(new ServletHolder(new DisconnectServlet()), "/disconnect");
    }

    public void start() throws Exception {
        System.out.println("Starting company server...");
        server.start();
        server.join();
    }

    public void stop() throws Exception {
        System.out.println("Stopping company server...");
        server.stop();
        server.join();
    }
}
