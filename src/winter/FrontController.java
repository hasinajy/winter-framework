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

/**
 * The central servlet managing HTTP requests and responses in the Winter
 * framework.
 * <p>
 * This servlet acts as the front controller, initializing controller mappings,
 * processing incoming GET and POST requests, and dispatching them to
 * appropriate
 * controller methods. It handles multipart requests, supports RESTful
 * responses,
 * and manages errors via {@link ExceptionHandler}. It uses
 * {@link ControllerScanner}
 * for initialization and {@link ControllerHandler} for method invocation.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
@MultipartConfig
public class FrontController extends HttpServlet {

    /** Stores any exception that occurs during servlet initialization. */
    private static Exception initException = null;

    /** The map of URL paths to their corresponding {@link Mapping} objects. */
    private static final Map<String, Mapping> URL_MAPPINGS = new HashMap<>();

    /** The handler for processing and logging exceptions. */
    private static final ExceptionHandler exceptionHandler = new ExceptionHandler();

    /**
     * Provides access to the URL mappings registered during initialization.
     *
     * @return the map of URL paths to {@link Mapping} objects
     */
    public static Map<String, Mapping> getUrlMappings() {
        return FrontController.URL_MAPPINGS;
    }

    /**
     * Gets the exception that occurred during servlet initialization, if any.
     *
     * @return the initialization exception, or null if none occurred
     */
    private Exception getInitException() {
        return FrontController.initException;
    }

    /**
     * Sets the exception that occurred during servlet initialization.
     *
     * @param e the exception to store
     */
    private static void setInitException(Exception e) {
        FrontController.initException = e;
    }

    /**
     * Initializes the servlet by scanning for controllers and registering their
     * mappings.
     * <p>
     * Uses {@link ControllerScanner} to scan the servlet context for controllers
     * and populate
     * {@link #URL_MAPPINGS}. Any initialization errors are stored in
     * {@link #initException}.
     * </p>
     *
     * @throws ServletException if an unrecoverable initialization error occurs
     */
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

    /**
     * Handles HTTP GET requests by processing them with the GET verb.
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs during response writing
     */
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

    /**
     * Handles HTTP POST requests by processing them with the POST verb.
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs during response writing
     */
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

    /**
     * Processes an HTTP request by delegating to the appropriate controller method.
     * <p>
     * Checks for initialization errors, extracts the target URL mapping, and
     * handles the request
     * using {@link #handleRequest}. Exceptions are caught and delegated to
     * {@link #exceptionHandler}.
     * </p>
     *
     * @param req         the HTTP request
     * @param resp        the HTTP response
     * @param requestVerb the HTTP verb (e.g., GET, POST)
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs during response writing
     */
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

    /**
     * Handles the core request logic by invoking the mapped controller method and
     * processing its result.
     * <p>
     * Retrieves the {@link Mapping} for the target URL, validates the verb, invokes
     * the method via
     * {@link ControllerHandler}, and processes the result (e.g., rendering JSP or
     * returning JSON).
     * Supports RESTful responses and form validation error handling.
     * </p>
     *
     * @param req           the HTTP request
     * @param resp          the HTTP response
     * @param targetMapping the extracted URL mapping path
     * @param out           the response writer
     * @param requestVerb   the HTTP verb (e.g., GET, POST)
     * @throws MappingNotFoundException     if no mapping exists for the target URL
     * @throws AnnotationNotFoundException  if a required annotation is missing
     * @throws ReflectiveOperationException if reflection fails during method
     *                                      invocation
     * @throws InvalidReturnTypeException   if the method returns an unsupported
     *                                      type
     * @throws ServletException             if a servlet-specific error occurs
     * @throws IOException                  if an I/O error occurs during response
     *                                      writing
     * @throws InvalidRequestVerbException  if the verb is not supported for the
     *                                      mapping
     */
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