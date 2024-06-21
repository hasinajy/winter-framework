package winter.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import winter.annotations.RequestParam;
import winter.data.Mapping;

public class ReflectionUtil extends Utility {
    public static Object invokeControllerMethod(Mapping mapping, HttpServletRequest req)
            throws ReflectiveOperationException {
        String className = mapping.getClassName();
        String methodName = mapping.getMethodName();

        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getDeclaredMethod(methodName, mapping.getMethodParamTypes());
            List<Object> args = new ArrayList<>();
            Parameter[] methodParams = method.getParameters();

            for (Parameter param : methodParams) {
                String objName = null;

                if (param.isAnnotationPresent(RequestParam.class)) {
                    objName = param.getAnnotation(RequestParam.class).name();
                } else {
                    // TODO: Use library to get parameter name or use `-parameter` during
                    // compilation
                    objName = param.getName();
                }

                Class<?> argClass = param.getType();
                Object arg = argClass.getDeclaredConstructor().newInstance();

                String[] objParamNames = getObjectParameters(objName, req.getParameterNames());
                String[] objAttrNames = getAttributeNames(objParamNames);
                String[] attrValues = getAttributeValues(objParamNames, req);

                setObjectAttributes(argClass, arg, objAttrNames, attrValues);
                args.add(argClass.cast(arg));
            }

            return method.invoke(clazz.getDeclaredConstructor().newInstance(), args.toArray());
        } catch (ClassNotFoundException e) {
            String message = "Class not found: " + className;
            throw new ReflectiveOperationException(message, e);
        } catch (NoSuchMethodException e) {
            String message = "Method not found: " + methodName;
            throw new ReflectiveOperationException(message, e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            String message = "Error invoking method: " + methodName;
            throw new ReflectiveOperationException(message, e);
        }
    }

    private static void setObjectAttributes(Class<?> classType, Object instance, String[] attrNames, String[] attrValues)
            throws ReflectiveOperationException {
        for (int i = 0; i < attrNames.length; i++) {
            String attrName = attrNames[i];
            String attrValue = attrValues[i];
            String setterName = getSetterName(attrName);

            try {
                Class<?> clazz = classType.getDeclaredField(attrName).getType();
                Method setter = classType.getDeclaredMethod(setterName, clazz);
                Object value = convertAttributeValue(attrValue, clazz);
                setter.invoke(instance, value);
            } catch (ReflectiveOperationException | NumberFormatException e) {
                String message = "Error setting attribute: " + attrName;
                throw new ReflectiveOperationException(message, e);
            }
        }
    }

    private static Object convertAttributeValue(String attrValue, Class<?> clazz) {
        if (attrValue == null) {
            return null;
        } else if (clazz == int.class || clazz == Integer.class) {
            return Integer.parseInt(attrValue);
        } else if (clazz == double.class || clazz == Double.class) {
            return Double.parseDouble(attrValue);
        } else if (clazz == float.class || clazz == Float.class) {
            return Float.parseFloat(attrValue);
        } else {
            return attrValue;
        }
    }

    private static String getSetterName(String attrName) {
        return "set" + Character.toUpperCase(attrName.charAt(0)) + attrName.substring(1);
    }

    private static String[] getAttributeValues(String[] paramNames, HttpServletRequest req) {
        List<String> attributeValues = new ArrayList<>();

        for (String paramName : paramNames) {
            attributeValues.add(req.getParameter(paramName));
        }

        return attributeValues.toArray(new String[0]);
    }

    private static String[] getAttributeNames(String[] objParamNames) {
        List<String> attributeNames = new ArrayList<>();

        for (String paramName : objParamNames) {
            attributeNames.add(paramName.split("\\.")[1]);
        }

        return attributeNames.toArray(new String[0]);
    }

    private static String[] getObjectParameters(String objName, Enumeration<String> paramNames) {
        List<String> objParamNames = new ArrayList<>();

        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();

            if (paramName.matches(objName + ".*")) {
                objParamNames.add(paramName);
            }
        }

        return objParamNames.toArray(new String[0]);
    }
}
