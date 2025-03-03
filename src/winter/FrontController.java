package winter;

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
import winter.FrontController;
import winter.data.Mapping;
import winter.data.MappingMethod;
import winter.data.client.JsonString;
import winter.data.client.ModelView;
import winter.data.enumdata.RequestVerb;
import winter.data.exception.annotation.AnnotationNotFoundException;
import winter.data.exception.annotation.DuplicateMappingException;
import winter.data.exception.client.InvalidRequestVerbException;
import winter.data.exception.client.MappingNotFoundException;
import winter.data.exception.initialization.InvalidPackageNameException;
import winter.data.exception.initialization.PackageProviderNotFoundException;
import winter.data.exception.internal.InvalidReturnTypeException;
import winter.service.ControllerScanner;
import winter.service.ExceptionHandler;
import winter.service.ControllerHandler;
import winter.util.DataUtil;

@MultipartConfig
public class FrontController extends HttpServlet {
    private static Exception initException = null;
    private static final Map<String, Mapping> URL_MAPPINGS = new HashMap<>();
    private static final ExceptionHandler exceptionHandler = new ExceptionHandler();

    public static Map<String, Mapping> getUrlMappings() {
        return FrontController.URL_MAPPINGS;
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
        ControllerScanner controllerScanner = new ControllerScanner();

        try {
            controllerScanner.scanControllers(servletContext);
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
            exceptionHandler.handleException(
                    new ServletException("Servlet error occurred while processing GET request", e),
                    Level.SEVERE, resp);
        } catch (IOException e) {
            exceptionHandler.handleException(new IOException("I/O error occurred while processing GET request", e),
                    Level.SEVERE, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestVerb requestVerb = RequestVerb.POST;

        try {
            processRequest(req, resp, requestVerb);
        } catch (ServletException e) {
            exceptionHandler.handleException(
                    new ServletException("Servlet error occurred while processing POST request", e),
                    Level.SEVERE, resp);
        } catch (IOException e) {
            exceptionHandler.handleException(new IOException("I/O error occurred while processing POST request", e),
                    Level.SEVERE, resp);
        }
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse resp, RequestVerb requestVerb)
            throws ServletException, IOException {

        exceptionHandler.handleInitException(resp, this.getInitException());

        // Stop the method execution if an error occurred during initialization
        if (this.getInitException() != null) {
            return;
        }

        String targetMapping = DataUtil.extractURIMapping(req);
        resp.setContentType("text/html");

        try (PrintWriter out = resp.getWriter()) {
            try {
                handleRequest(req, resp, targetMapping, out, requestVerb);
            } catch (MappingNotFoundException | InvalidReturnTypeException e) {
                exceptionHandler.handleException(e, Level.WARNING, resp);
            } catch (IllegalAccessException | InvalidRequestVerbException | AnnotationNotFoundException e) {
                exceptionHandler.handleException(e, Level.SEVERE, resp);
            } catch (ReflectiveOperationException e) {
                exceptionHandler.handleException(
                        new ReflectiveOperationException("An error occurred while processing the requested URL", e),
                        Level.SEVERE, resp);
            } catch (Exception e) {
                exceptionHandler.handleException(new Exception("An unexpected error occurred", e), Level.SEVERE, resp);
            }
        }
    }

    private void handleRequest(HttpServletRequest req, HttpServletResponse resp, String targetMapping, PrintWriter out,
            RequestVerb requestVerb)
            throws MappingNotFoundException, AnnotationNotFoundException,
            ReflectiveOperationException,
            InvalidReturnTypeException, ServletException,
            IOException, InvalidRequestVerbException {

        Mapping mapping = FrontController.URL_MAPPINGS.get(targetMapping);

        if (mapping == null) {
            throw new MappingNotFoundException("Resource not found for: " + targetMapping);
        }

        MappingMethod mappingMethod = mapping.getMethod(requestVerb);

        if (mappingMethod == null) {
            throw new InvalidRequestVerbException("Access denied for the specified URL");
        }

        Object result = new ControllerHandler().invokeControllerMethod(mapping.getClassName(), mappingMethod, req);
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
