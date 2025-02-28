package winter.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.google.gson.Gson;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import winter.controller.FrontController;
import winter.data.Mapping;
import winter.data.MappingMethod;
import winter.data.client.JsonString;
import winter.data.client.ModelView;
import winter.data.enumdata.RequestVerb;
import winter.exception.AnnotationNotFoundException;
import winter.exception.DuplicateMappingException;
import winter.exception.InvalidPackageNameException;
import winter.exception.InvalidRequestVerbException;
import winter.exception.InvalidReturnTypeException;
import winter.exception.MappingNotFoundException;
import winter.exception.PackageProviderNotFoundException;
import winter.util.AnnotationScanner;
import winter.util.ExceptionHandler;
import winter.util.ReflectionUtil;
import winter.util.UrlUtil;

@MultipartConfig
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
        RequestVerb requestVerb = RequestVerb.GET;

        try {
            processRequest(req, resp, requestVerb);
        } catch (ServletException e) {
            ExceptionHandler.handleException(
                    new ServletException("Servlet error occurred while processing GET request", e),
                    Level.SEVERE, resp);
        } catch (IOException e) {
            ExceptionHandler.handleException(new IOException("I/O error occurred while processing GET request", e),
                    Level.SEVERE, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestVerb requestVerb = RequestVerb.POST;

        try {
            processRequest(req, resp, requestVerb);
        } catch (ServletException e) {
            ExceptionHandler.handleException(
                    new ServletException("Servlet error occurred while processing POST request", e),
                    Level.SEVERE, resp);
        } catch (IOException e) {
            ExceptionHandler.handleException(new IOException("I/O error occurred while processing POST request", e),
                    Level.SEVERE, resp);
        }
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse resp, RequestVerb requestVerb)
            throws ServletException, IOException {
        ExceptionHandler.handleInitException(resp, this.getInitException());

        // Stop the method execution if an error occurred during initialization
        if (this.getInitException() != null) {
            return;
        }

        String targetURL = UrlUtil.extractTargetURL(req);
        resp.setContentType("text/html");

        try (PrintWriter out = resp.getWriter()) {
            try {
                handleRequest(req, resp, targetURL, out, requestVerb);
            } catch (MappingNotFoundException | InvalidReturnTypeException e) {
                ExceptionHandler.handleException(e, Level.WARNING, resp);
            } catch (InvalidRequestVerbException | AnnotationNotFoundException e) {
                ExceptionHandler.handleException(e, Level.SEVERE, resp);
            } catch (ReflectiveOperationException e) {
                ExceptionHandler.handleException(
                        new ReflectiveOperationException("An error occurred while processing the requested URL", e),
                        Level.SEVERE, resp);
            } catch (Exception e) {
                ExceptionHandler.handleException(new Exception("An unexpected error occurred", e), Level.SEVERE, resp);
            }
        }
    }

    private void handleRequest(HttpServletRequest req, HttpServletResponse resp, String targetURL, PrintWriter out,
            RequestVerb requestVerb)
            throws MappingNotFoundException, AnnotationNotFoundException,
            ReflectiveOperationException,
            InvalidReturnTypeException, ServletException,
            IOException, InvalidRequestVerbException {

        Mapping mapping = urlMappings.get(targetURL);

        if (mapping == null) {
            throw new MappingNotFoundException("Resource not found for URL: " + targetURL);
        }

        MappingMethod mappingMethod = mapping.getMethod(requestVerb);

        if (mappingMethod == null) {
            throw new InvalidRequestVerbException("Access denied for the specified URL");
        }

        Object result = ReflectionUtil.invokeControllerMethod(mapping.getClassName(), mappingMethod, req);
        Gson gson = new Gson();

        if (mappingMethod.isRest()) {
            resp.setContentType("application/json");
        }

        if (result instanceof String) {
            if (mappingMethod.isRest()) {
                out.print(gson.toJson(new JsonString(result.toString())));
            } else {
                out.print(result.toString());
            }
        } else if (result instanceof ModelView modelView) {
            modelView.setRequestAttributes(req);
            HttpServletRequest dispatchRequest = req;
            String redirectionUrl = modelView.getJspUrl();

            // Form validation handling
            if (Boolean.TRUE.equals(req.getAttribute("hasError"))) {
                dispatchRequest = new HttpServletRequestWrapper(req) {
                    @Override
                    public String getMethod() {
                        return "GET";
                    }
                };
                redirectionUrl = (String) req.getAttribute("errorUrl");
            }

            if (mappingMethod.isRest()) {
                out.print(modelView.getJsonData());
            } else {
                req.getRequestDispatcher(redirectionUrl).forward(dispatchRequest, resp);
            }
        } else {
            throw new InvalidReturnTypeException("Controller return type should be either String or ModelView");
        }
    }
}
