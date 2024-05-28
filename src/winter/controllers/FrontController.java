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
import winter.utils.ReflectionUtil;
import winter.utils.URLUtil;

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
        String targetURL = URLUtil.extractTargetURL(requestURL);

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        // Print request URL information
        Printer.printRequestInfo(out, requestURL);

        try {
            String className = URLMappings.get(targetURL).getClassName();
            String methodName = URLMappings.get(targetURL).getMethodName();
            Object result = ReflectionUtil.invokeControllerMethod(className, methodName, new Class<?>[] {});

            Printer.printTargetControllerInfo(out, targetURL, className, methodName, result.toString());
        } catch (MappingNotFoundException e) {
            Printer.printError(out, "Mapping not found for '" + targetURL + "'.", false);
        } catch (Exception e) {
            Printer.printError(out, e.getMessage(), true);
            e.printStackTrace(out);
        } finally {
            out.close();
        }
    }

}
