package winter.service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import winter.data.MappingMethod;
import winter.data.ObjectRequestParameter;
import winter.data.annotation.http.RequestParam;
import winter.data.client.FormData;
import winter.data.exception.annotation.AnnotationNotFoundException;
import winter.data.exception.client.InvalidFormDataException;
import winter.data.servletabstraction.File;
import winter.data.servletabstraction.Session;
import winter.util.DataUtil;

/**
 * Service class for handling controller method invocation in the Winter
 * framework.
 * <p>
 * This class manages the execution of controller methods, including
 * authentication checks,
 * session injection, and parameter initialization from HTTP requests. It
 * processes primitive
 * types, files, and complex objects, handling validation and errors
 * appropriately.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
public class ControllerHandler {

    /**
     * The form data extracted from the request for validation and error reporting.
     */
    private FormData formData = null;

    /**
     * Invokes a controller method with arguments derived from an HTTP request.
     * <p>
     * Loads the specified class, checks authentication, injects the session if
     * required,
     * initializes method arguments, and invokes the method. Errors are wrapped in
     * appropriate
     * exceptions for upstream handling.
     * </p>
     *
     * @param className     the fully qualified name of the controller class
     * @param mappingMethod the mapping method containing the method to invoke
     * @param req           the HTTP request providing parameters and session data
     * @return the result of the controller method invocation
     * @throws AnnotationNotFoundException  if a required annotation is missing
     * @throws IOException                  if an I/O error occurs (e.g., file
     *                                      handling)
     * @throws ReflectiveOperationException if reflection-related errors occur
     *                                      (e.g., class or method not found)
     * @throws ServletException             if a servlet-related error occurs
     * @throws IllegalAccessException       if authentication fails or access is
     *                                      denied
     */
    public Object invokeControllerMethod(String className, MappingMethod mappingMethod, HttpServletRequest req)
            throws AnnotationNotFoundException, IOException, ReflectiveOperationException, ServletException {

        String methodName = mappingMethod.getMethod().getName();

        try {
            Object authSession = req.getSession().getAttribute("auth");

            if (!mappingMethod.hasAuth((String) authSession)) {
                throw new IllegalAccessException();
            }

            Class<?> clazz = Class.forName(className);
            Method method = mappingMethod.getMethod();
            Object[] args = initializeMethodArguments(method.getParameters(), req);

            // Inject session if it's defined
            Object instanceObject = clazz.getDeclaredConstructor().newInstance();
            injectSession(instanceObject, req.getSession());

            // Invoke the controller method
            return method.invoke(instanceObject, args);
        } catch (ClassNotFoundException e) {
            String message = "Class not found: " + className;
            throw new ReflectiveOperationException(message, e);
        } catch (NoSuchMethodException e) {
            String message = "Method not found: " + methodName;
            throw new ReflectiveOperationException(message, e);
        } catch (AnnotationNotFoundException e) {
            throw e;
        } catch (IllegalAccessException e) {
            throw new IllegalAccessException("Permission denied");
        } catch (ReflectiveOperationException | NumberFormatException e) {
            String message = "Error invoking method: " + methodName;
            throw new ReflectiveOperationException(message, e);
        }
    }

    /**
     * Injects a Winter {@link Session} into an object if a matching field exists.
     * <p>
     * Checks the object’s class for a {@link Session} field and invokes its setter
     * with a wrapped {@link HttpSession}.
     * </p>
     *
     * @param object      the object to inject the session into
     * @param httpSession the HTTP session to wrap
     * @throws NoSuchMethodException     if the setter method for the session field
     *                                   is not found
     * @throws IllegalAccessException    if the setter method is inaccessible
     * @throws InvocationTargetException if the setter invocation fails
     * @throws IllegalArgumentException  if the session type is incompatible
     * @throws SecurityException         if a security manager prevents access
     */
    private void injectSession(Object object, HttpSession httpSession)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, IllegalArgumentException,
            SecurityException {
        Class<?> clazz = object.getClass();
        String attrName = hasSession(clazz);

        if (attrName != null) {
            Method sessionSetterMethod = clazz.getDeclaredMethod(DataUtil.getSetterName(attrName), Session.class);

            // Creates the framework session abstraction object
            Session winterSession = new Session(httpSession);
            sessionSetterMethod.invoke(object, winterSession);
        }
    }

    /**
     * Checks if a class has a {@link Session} field.
     *
     * @param clazz the class to inspect
     * @return the name of the session field if found, null otherwise
     */
    private String hasSession(Class<?> clazz) {
        Field[] attributes = clazz.getDeclaredFields();

        for (Field attribute : attributes) {
            if (attribute.getType() == Session.class) {
                return attribute.getName();
            }
        }

        return null;
    }

    /**
     * Initializes method arguments from an HTTP request.
     * <p>
     * Processes method parameters annotated with {@link RequestParam}, handling
     * primitive types,
     * {@link File} objects, and complex objects. Sets error attributes on the
     * request if validation fails.
     * </p>
     *
     * @param methodParams the parameters of the method to initialize
     * @param req          the HTTP request providing parameter values
     * @return an array of initialized argument objects
     * @throws AnnotationNotFoundException  if a required {@link RequestParam}
     *                                      annotation is missing
     * @throws IOException                  if an I/O error occurs (e.g., file part
     *                                      reading)
     * @throws ReflectiveOperationException if reflection fails during object
     *                                      creation
     * @throws ServletException             if a servlet-related error occurs (e.g.,
     *                                      part retrieval)
     */
    private Object[] initializeMethodArguments(Parameter[] methodParams, HttpServletRequest req)
            throws AnnotationNotFoundException, IOException, ReflectiveOperationException, ServletException {

        List<Object> args = new ArrayList<>();
        this.formData = new FormData(methodParams, req);
        boolean hasError = false;

        for (Parameter param : methodParams) {
            // @RequestParam is required as parameter names are positional without
            // additional configurations
            String requestParamName = param.getAnnotation(RequestParam.class).value();
            Class<?> paramType = param.getType();
            Object paramValue = null;

            if (DataUtil.isPrimitive(paramType)) {
                try {
                    paramValue = DataUtil.parseObject(paramType, formData.getValue(requestParamName, false));
                    DataUtil.validateRequestParamConstraints(param, paramValue);
                } catch (NumberFormatException | InvalidFormDataException e) {
                    hasError = true;
                    formData.setErrorMessage(requestParamName, e.getMessage());
                    paramValue = DataUtil.parseObject(paramType, "0");
                }
            } else if (paramType == File.class) {
                paramValue = new File(req.getPart(requestParamName));
            } else {
                try {
                    paramValue = createObjectParameterInstance(paramType, req, requestParamName);
                } catch (Exception e) {
                    hasError = true;
                    paramValue = DataUtil.parseObject(paramType, null);
                }
            }

            if (paramValue == null && param.getAnnotation(RequestParam.class).required()) {
                hasError = true;
                formData.setErrorMessage(requestParamName, "Field cannot be empty");
            }

            args.add(paramValue);
        }

        if (hasError) {
            req.setAttribute("hasError", true);
            req.setAttribute("formData", formData);
        }

        return args.toArray();
    }

    /**
     * Creates an instance of a complex object parameter from request data.
     * <p>
     * Instantiates the object and sets its attributes based on request parameters
     * prefixed with the given name.
     * </p>
     *
     * @param objType   the class type of the object to create
     * @param req       the HTTP request providing parameter values
     * @param objPrefix the prefix for parameter names (e.g., "user" for
     *                  "user.name")
     * @return the populated object instance
     * @throws InvalidFormDataException     if validation of object attributes fails
     * @throws ReflectiveOperationException if instantiation or attribute setting
     *                                      fails
     */
    private Object createObjectParameterInstance(Class<?> objType, HttpServletRequest req, String objPrefix)
            throws InvalidFormDataException, ReflectiveOperationException {
        Object objectInstance = objType.getDeclaredConstructor().newInstance();
        ObjectRequestParameter objRequestParameter = new ObjectRequestParameter(objType, req, objPrefix);
        setObjectAttributes(objectInstance, objRequestParameter);
        return objectInstance;
    }

    /**
     * Sets attributes of an object based on request parameter values.
     * <p>
     * Uses reflection to invoke setters for each field, validating the values and
     * updating the form data with errors if validation fails.
     * </p>
     *
     * @param instance            the object instance to populate
     * @param objRequestParameter the request parameter data for the object
     * @throws InvalidFormDataException     if any attribute validation fails
     * @throws ReflectiveOperationException if setter invocation fails
     */
    private void setObjectAttributes(Object instance, ObjectRequestParameter objRequestParameter)
            throws InvalidFormDataException, ReflectiveOperationException {

        Class<?> objType = objRequestParameter.getObjType();
        boolean hasError = false;

        for (Field field : objType.getDeclaredFields()) {
            String attrName = field.getName();
            String attrValue = objRequestParameter.getValues().get(attrName);
            Method attrSetterMethod = null;
            Class<?> attrType = field.getType();
            Object value = null;

            try {
                attrSetterMethod = DataUtil.getSetterMethod(objType, attrName);
                formData.setValue(objRequestParameter.getObjPrefix() + "." + attrName, attrValue);
                value = DataUtil.parseObject(attrType, attrValue);
                DataUtil.validateRequestParamConstraints(field, value.toString());
                attrSetterMethod.invoke(instance, value);
            } catch (NumberFormatException | InvalidFormDataException e) {
                hasError = true;
                formData.setErrorMessage(objRequestParameter.getObjPrefix() + "." + attrName, e.getMessage());
            } catch (ReflectiveOperationException e) {
                throw new ReflectiveOperationException("An error occurred while setting object attributes", e);
            }
        }

        if (hasError) {
            throw new InvalidFormDataException();
        }
    }
}