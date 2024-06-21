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

                String[] objParamNames = getObjParamNames(objName, req.getParameterNames());
                String[] objAttrNames = getObjAttrNames(objParamNames);
                String[] attrValues = getAttrValues(objParamNames, req);

                setAttrValues(argClass, arg, objAttrNames, attrValues);
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

    private static void setAttrValues(Class<?> classType, Object instance, String[] attrNames, String[] attrValues)
            throws ReflectiveOperationException {
        for (int i = 0; i < attrNames.length; i++) {
            String attrName = attrNames[i];
            String attrValue = attrValues[i];
            String setterName = "set" + Character.toUpperCase(attrName.charAt(0)) + attrName.substring(1);

            try {
                Class<?> clazz = classType.getDeclaredField(attrName).getType();
                Method setter = classType.getDeclaredMethod(setterName, clazz);

                if (clazz == int.class) {
                    int value = (attrValue == null) ? 0 : Integer.parseInt(attrValue);
                    setter.invoke(instance, value);
                } else if (clazz == double.class) {
                    double value = (attrValue == null) ? 0 : Double.parseDouble(attrValue);
                    setter.invoke(instance, value);
                } else if (clazz == float.class) {
                    float value = (attrValue == null) ? 0 : Float.parseFloat(attrValue);
                    setter.invoke(instance, value);
                } else {
                    setter.invoke(instance, clazz.cast(attrValue));
                }
            } catch (NoSuchFieldException e) {
                String message = "Field not found: " + attrName;
                throw new ReflectiveOperationException(message, e);
            } catch (NoSuchMethodException e) {
                String message = "Method not found: " + setterName;
                throw new ReflectiveOperationException(message, e);
            } catch (SecurityException
                    | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException e) {
                String message = "Error invoking method: " + setterName;
                throw new ReflectiveOperationException(message, e);
            }
        }
    }

    private static String[] getAttrValues(String[] paramNames, HttpServletRequest req) {
        List<String> attributeValues = new ArrayList<>();

        for (String paramName : paramNames) {
            attributeValues.add(req.getParameter(paramName));
        }

        return attributeValues.toArray(new String[0]);
    }

    private static String[] getObjAttrNames(String[] objParamNames) {
        List<String> attributeNames = new ArrayList<>();

        for (String paramName : objParamNames) {
            attributeNames.add(paramName.split("\\.")[1]);
        }

        return attributeNames.toArray(new String[0]);
    }

    private static String[] getObjParamNames(String objName, Enumeration<String> paramNames) {
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
