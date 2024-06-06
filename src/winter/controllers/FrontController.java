package winter.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import winter.exceptions.DuplicateMappingException;
import winter.exceptions.InvalidReturnTypeException;
import winter.exceptions.MappingNotFoundException;
import winter.utils.AnnotationScanner;
import winter.utils.HtmlElementBuilder;
import winter.utils.ReflectionUtil;
import winter.utils.UrlUtil;

public class FrontController extends HttpServlet {
    private final Map<String, Mapping> urlMappings = new HashMap<>();
    private final List<Exception> initExceptions = new ArrayList<>();
    private static Logger logger = Logger.getLogger(FrontController.class.getName());

    // Getters & Setters
    private Map<String, Mapping> getUrlMappings() {
        return this.urlMappings;
    }

    private List<Exception> getInitExceptions() {
        return this.initExceptions;
    }

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();

        try {
            AnnotationScanner.scanControllers(servletContext, this.getUrlMappings());
        } catch (DuplicateMappingException e) {
            this.getInitExceptions().add(e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred during initialization", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            processRequest(req, resp);
        } catch (ServletException e) {
            handleException(e, Level.SEVERE, resp, "Servlet error occurred while processing GET request");
        } catch (IOException e) {
            handleException(e, Level.SEVERE, resp, "I/O error occurred while processing GET request");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            processRequest(req, resp);
        } catch (ServletException e) {
            handleException(e, Level.SEVERE, resp, "Servlet error occurred while processing POST request");
        } catch (IOException e) {
            handleException(e, Level.SEVERE, resp, "I/O error occurred while processing POST request");
        }
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String targetURL = UrlUtil.extractTargetURL(req);

        resp.setContentType("text/html");

        try (PrintWriter out = resp.getWriter()) {
            HtmlElementBuilder.printRequestInfo(out, req.getRequestURL().toString());

            try {
                handleRequest(out, req, resp, targetURL);
            } catch (MappingNotFoundException | InvalidReturnTypeException e) {
                handleException(e, Level.WARNING, resp, e.getMessage());
            } catch (ReflectiveOperationException e) {
                handleException(e, Level.SEVERE, resp, "An error occurred while processing the requested URL");
            } catch (Exception e) {
                handleException(e, Level.SEVERE, resp, "An unexpected error occurred");
            }
        }
    }

    private void handleInitExceptions(HttpServletResponse resp) {
        for (Exception e: this.getInitExceptions()) {
            if (e instanceof DuplicateMappingException) {
                handleException(e, Level.SEVERE, resp, e.getMessage());
            }
        }
    }

    private void handleRequest(PrintWriter out, HttpServletRequest req, HttpServletResponse resp, String targetURL)
            throws MappingNotFoundException, ReflectiveOperationException, InvalidReturnTypeException, ServletException,
            IOException {
        Mapping mapping = urlMappings.get(targetURL);

        if (mapping == null) {
            throw new MappingNotFoundException("Mapping not found for '" + targetURL + "'");
        }

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
    }

    private void handleException(Exception e, Level level, HttpServletResponse resp, String message) {
        logger.log(level, e.getMessage(), e);

        try (PrintWriter out = resp.getWriter()) {
            HtmlElementBuilder.printError(out, message);
        } catch (IOException ioException) {
            logger.log(Level.SEVERE, "Error sending error response to client", ioException);
        }
    }
}
