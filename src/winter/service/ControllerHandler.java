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

public class ControllerHandler {
    private FormData formData = null;

    public Object invokeControllerMethod(String className, MappingMethod mappingMethod, HttpServletRequest req)
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

    private String hasSession(Class<?> clazz) {
        Field[] attributes = clazz.getDeclaredFields();

        for (Field attribute : attributes) {
            if (attribute.getType() == Session.class) {
                return attribute.getName();
            }
        }

        return null;
    }

    private Object[] initializeMethodArguments(Parameter[] methodParams, HttpServletRequest req)
            throws AnnotationNotFoundException, IOException, ReflectiveOperationException, ServletException {

        List<Object> args = new ArrayList<>();
        this.formData = new FormData(methodParams, req);
        boolean hasError = false;

        for (Parameter param : methodParams) {
            // @RequestParam is required as parameter names are positional without
            // additional configurations
            String requestParamName = param.getAnnotation(RequestParam.class).name();
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

    private Object createObjectParameterInstance(Class<?> objType, HttpServletRequest req, String objPrefix)
            throws InvalidFormDataException, ReflectiveOperationException {
        Object objectInstance = objType.getDeclaredConstructor().newInstance();
        ObjectRequestParameter objRequestParameter = new ObjectRequestParameter(objType, req, objPrefix);
        setObjectAttributes(
                objectInstance, objRequestParameter);
        return objectInstance;
    }

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
