package winter.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import winter.utils.UrlUtil;

public class FrontController extends HttpServlet {

    private final Map<String, Mapping> urlMappings = new HashMap<>();
    private static Logger logger = Logger.getLogger(FrontController.class.getName());

    // Getters & Setters
    private Map<String, Mapping> getUrlMappings() {
        return this.urlMappings;
    }

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();

        try {
            AnnotationScanner.scanControllers(servletContext, this.getUrlMappings());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred during initialization", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            processRequest(req, resp);
        } catch (ServletException e) {
            handleException(e, resp, "Servlet error occurred while processing GET request");
        } catch (IOException e) {
            handleException(e, resp, "I/O error occurred while processing GET request");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            processRequest(req, resp);
        } catch (ServletException e) {
            handleException(e, resp, "Servlet error occurred while processing POST request");
        } catch (IOException e) {
            handleException(e, resp, "I/O error occurred while processing POST request");
        }
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURL = req.getRequestURL().toString();
        String targetURL = UrlUtil.extractTargetURL(requestURL);

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        // Print request URL information
        HtmlElementBuilder.printRequestInfo(out, requestURL);

        try {
            Mapping mapping = urlMappings.get(targetURL);

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
                throw new InvalidReturnTypeException("Return type should be either String or ModelView");
            }
        } catch (MappingNotFoundException e) {
            HtmlElementBuilder.printError(out, "Mapping not found for '" + targetURL + "'");
        } catch (Exception e) {
            HtmlElementBuilder.printError(out, e);
        } finally {
            out.close();
        }
    }

    private void handleException(Exception e, HttpServletResponse resp, String message) {
        logger.log(Level.SEVERE, message, e);

        try {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
        } catch (IOException ioException) {
            logger.log(Level.SEVERE, "Error sending error response to client", ioException);
        }
    }

}
