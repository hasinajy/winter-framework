package winter.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import winter.data.Mapping;
import winter.data.ModelView;
import winter.exceptions.DuplicateMappingException;
import winter.exceptions.InvalidPackageNameException;
import winter.exceptions.InvalidReturnTypeException;
import winter.exceptions.MappingNotFoundException;
import winter.exceptions.PackageProviderNotFoundException;
import winter.utils.AnnotationScanner;
import winter.utils.ExceptionHandler;
import winter.utils.HtmlElementBuilder;
import winter.utils.ReflectionUtil;
import winter.utils.UrlUtil;

public class FrontController extends HttpServlet {
    private final Map<String, Mapping> urlMappings = new HashMap<>();
    private static Exception initException = null;

    // Getters & Setters
    private Map<String, Mapping> getUrlMappings() {
        return this.urlMappings;
    }

    private Exception getInitException() {
        return FrontController.initException;
    }

    private static void setInitException(Exception e) {
        FrontController.initException = e;
    }
    
    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();

        try {
            AnnotationScanner.scanControllers(servletContext, this.getUrlMappings());
        } catch (PackageProviderNotFoundException | InvalidPackageNameException | DuplicateMappingException e) {
            FrontController.setInitException(e);
        } catch (Exception e) {
            FrontController.setInitException(new Exception("An error occurred during initialization", e));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            processRequest(req, resp);
        } catch (ServletException e) {
            ExceptionHandler.handleException(new ServletException("Servlet error occurred while processing GET request", e),
                    Level.SEVERE, resp);
        } catch (IOException e) {
            ExceptionHandler.handleException(new IOException("I/O error occurred while processing GET request", e), Level.SEVERE, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            processRequest(req, resp);
        } catch (ServletException e) {
            ExceptionHandler.handleException(new ServletException("Servlet error occurred while processing POST request", e),
                    Level.SEVERE, resp);
        } catch (IOException e) {
            ExceptionHandler.handleException(new IOException("I/O error occurred while processing POST request", e), Level.SEVERE, resp);
        }
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ExceptionHandler.handleInitException(resp, this.getInitException());

        // Stop the method execution if an error occurred during initialization
        if (this.getInitException() != null) {
            return;
        }

        String targetURL = UrlUtil.extractTargetURL(req);
        resp.setContentType("text/html");

        try (PrintWriter out = resp.getWriter()) {
            HtmlElementBuilder.printRequestInfo(out, req.getRequestURL().toString());

            try {
                handleRequest(req, resp, targetURL, out);
            } catch (MappingNotFoundException | InvalidReturnTypeException e) {
                ExceptionHandler.handleException(e, Level.WARNING, resp);
            } catch (ReflectiveOperationException e) {
                ExceptionHandler.handleException(
                        new ReflectiveOperationException("An error occurred while processing the requested URL", e),
                        Level.SEVERE, resp);
            } catch (Exception e) {
                ExceptionHandler.handleException(new Exception("An unexpected error occurred", e), Level.SEVERE, resp);
            }
        }
    }

    private void handleRequest(HttpServletRequest req, HttpServletResponse resp, String targetURL, PrintWriter out)
            throws MappingNotFoundException, ReflectiveOperationException, InvalidReturnTypeException, ServletException,
            IOException {
        Mapping mapping = urlMappings.get(targetURL);

        if (mapping == null) {
            throw new MappingNotFoundException("Resource not found for URL: " + targetURL);
        }

        String className = mapping.getClassName();
        String methodName = mapping.getMethodName();
        Object result = ReflectionUtil.invokeControllerMethod(mapping, req);

        if (result instanceof String) {
            HtmlElementBuilder.printTargetControllerInfo(out, targetURL, className, methodName, result.toString());
        } else if (result instanceof ModelView) {
            ModelView modelView = (ModelView) result;
            modelView.setRequestAttributes(req);
            req.getRequestDispatcher(modelView.getJspUrl()).forward(req, resp);
        } else {
            throw new InvalidReturnTypeException("Controller return type should be either String or ModelView");
        }
    }
}
