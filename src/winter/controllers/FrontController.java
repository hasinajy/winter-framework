package winter.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import winter.data.Mapping;
import winter.data.ModelView;
import winter.exceptions.InvalidReturnTypeException;
import winter.exceptions.MappingNotFoundException;
import winter.utils.AnnotationScanner;
import winter.utils.HtmlElementBuilder;
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
        HtmlElementBuilder.printRequestInfo(out, requestURL);

        try {
            Mapping mapping = URLMappings.get(targetURL);

            if (mapping == null)
                throw new MappingNotFoundException();

            String className = mapping.getClassName();
            String methodName = mapping.getMethodName();
            Object result = ReflectionUtil.invokeControllerMethod(className, methodName, new Class<?>[] {});

            if (result instanceof String) {
                HtmlElementBuilder.printTargetControllerInfo(out, targetURL, className, methodName, result.toString());
            } else if (result instanceof ModelView) {
                ModelView modelView = (ModelView) result;
                modelView.setRequestAttributes(req);
                req.getRequestDispatcher(modelView.getJspUrl()).forward(req, resp);
            } else {
                throw new InvalidReturnTypeException("Return type should be either String or ModelView.");
            }
        } catch (MappingNotFoundException e) {
            HtmlElementBuilder.printError(out, "Mapping not found for '" + targetURL + "'.");
        } catch (Exception e) {
            HtmlElementBuilder.printError(out, e);
        } finally {
            out.close();
        }
    }

}
