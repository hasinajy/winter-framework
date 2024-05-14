package winter.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet {
    private boolean isScanned = false;
    private ArrayList<String> controllers = null;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURL = req.getRequestURL().toString();

        PrintWriter out = resp.getWriter();
        out.println("Request URL: " + requestURL);
    }

    private void scanControllers(ServletContext servletContext) throws URISyntaxException, IOException, ClassNotFoundException {
        if (isScanned) {
            return;
        }

        String controllersPackage = servletContext.getInitParameter("ControllersPackage");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(controllersPackage.replace(".", "/"));

        while(resources.hasMoreElements()) {
          URL resource = resources.nextElement();
          scanControllers(resource, controllersPackage);
        }
    }
    }
}
