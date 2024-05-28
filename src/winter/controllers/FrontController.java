package winter.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import winter.data.Mapping;
import winter.utils.AnnotationScanner;
import winter.utils.Printer;

public class FrontController extends HttpServlet {

    private HashMap<String, Mapping> URLMappings = new HashMap<>();

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();

        try {
            this.URLMappings = AnnotationScanner.scanControllers(servletContext);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

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
        String[] splitRequest = requestURL.split("/");
        String targetURL = splitRequest[splitRequest.length - 1];

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        Printer.printList(out, "URL Information", new String[] { "Request URL" }, new String[] { requestURL });

        try {
            String className = this.URLMappings.get(targetURL).getClassName();
            String methodName = this.URLMappings.get(targetURL).getMethodName();
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getDeclaredMethod(methodName, new Class<?>[] {});

            Printer.printList(out, "Target Controller",
                    new String[] { "Target Mapping", "Controller", "Method", "Returned Value" },
                    new String[] { targetURL, className, methodName,
                            method.invoke(clazz.getDeclaredConstructor().newInstance()).toString() });
        } catch (Exception e) {
            Printer.printError(out, "Mapping not found for " + "'" + targetURL + "'.", false);
        }
    }

}
