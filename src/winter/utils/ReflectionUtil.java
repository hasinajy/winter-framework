package winter.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
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
                String reqValue = null;

                if (param.isAnnotationPresent(RequestParam.class)) {
                    String reqParamValue = param.getAnnotation(RequestParam.class).name();
                    reqValue = req.getParameter(reqParamValue);
                }

                args.add(reqValue);
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
}
