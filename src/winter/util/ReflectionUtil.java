package winter.util;

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
import winter.annotation.methodlevel.RequestParam;
import winter.data.File;
import winter.data.FormData;
import winter.data.MappingMethod;
import winter.data.ObjectRequestParameter;
import winter.data.Session;
import winter.exception.AnnotationNotFoundException;
import winter.exception.InvalidFormDataException;

public class ReflectionUtil extends Utility {
    public static Object invokeControllerMethod(String className, MappingMethod mappingMethod, HttpServletRequest req)
            throws AnnotationNotFoundException, IOException, ReflectiveOperationException, ServletException {

        String methodName = mappingMethod.getMethod().getName();

        try {
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
        } catch (ReflectiveOperationException | NumberFormatException e) {
            String message = "Error invoking method: " + methodName;
            throw new ReflectiveOperationException(message, e);
        }
    }

    private static void injectSession(Object object, HttpSession httpSession)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, IllegalArgumentException,
            SecurityException {
        Class<?> clazz = object.getClass();
        String attrName = hasSession(clazz);

        if (attrName != null) {
            Method sessionSetterMethod = clazz.getDeclaredMethod(getSetterName(attrName), Session.class);

            // Creates the framework session abstraction object
            Session winterSession = new Session(httpSession);
            sessionSetterMethod.invoke(object, winterSession);
        }
    }

    private static String hasSession(Class<?> clazz) {
        Field[] attributes = clazz.getDeclaredFields();

        for (Field attribute : attributes) {
            if (attribute.getType() == Session.class) {
                return attribute.getName();
            }
        }

        return null;
    }

    private static Object[] initializeMethodArguments(Parameter[] methodParams, HttpServletRequest req)
            throws AnnotationNotFoundException, IOException, ReflectiveOperationException, ServletException {

        List<Object> args = new ArrayList<>();
        FormData formData = new FormData(methodParams);
        boolean hasError = false;

        for (Parameter param : methodParams) {
            // @RequestParam is required as parameter names are positional without
            // additional configurations
            String requestParamName = param.getAnnotation(RequestParam.class).name();
            Class<?> paramType = param.getType();
            Object paramValue = null;

            if (paramType == String.class) {
                paramValue = req.getParameter(requestParamName);

                try {
                    DatatypeUtil.validateRequestParamConstraints(param, (String) paramValue);
                } catch (InvalidFormDataException e) {
                    hasError = true;
                    formData.setErrorMessage(requestParamName, e.getMessage());
                }
            } else if (paramType == File.class) {
                paramValue = new File(req.getPart(requestParamName));
            } else {
                paramValue = createObjectInstance(paramType, requestParamName, req);
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

    private static Object createObjectInstance(Class<?> objType, String requestParamName, HttpServletRequest req)
            throws ReflectiveOperationException {
        Object objectInstance = objType.getDeclaredConstructor().newInstance();
        ObjectRequestParameter objRequestParameter = new ObjectRequestParameter(objType, requestParamName, req);
        setObjectAttributes(
                objectInstance, objRequestParameter);
        return objectInstance;
    }

    private static void setObjectAttributes(Object instance, ObjectRequestParameter objRequestParameter)
            throws ReflectiveOperationException {

        Class<?> objType = objRequestParameter.getObjType();
        String[] attrNames = objRequestParameter.getAttrNames();
        String[] attrValues = objRequestParameter.getValues();

        for (int i = 0; i < attrNames.length; i++) {
            String attrName = attrNames[i];
            String attrValue = attrValues[i];
            String setterName = getSetterName(attrName);

            try {
                Class<?> attrType = objType.getDeclaredField(attrName).getType();
                Method attrSetterMethod = objType.getDeclaredMethod(setterName, attrType);
                Object value = DatatypeUtil.parseObject(attrType, attrValue);
                attrSetterMethod.invoke(instance, value);
            } catch (ReflectiveOperationException | NumberFormatException e) {
                String message = "Error setting attribute: " + attrName;
                throw new ReflectiveOperationException(message, e);
            }
        }
    }

    protected static String getSetterName(String attrName) {
        return "set" + Character.toUpperCase(attrName.charAt(0)) + attrName.substring(1);
    }
}
