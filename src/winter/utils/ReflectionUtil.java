package winter.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import jakarta.servlet.http.HttpServletRequest;
import winter.data.Mapping;

public class ReflectionUtil extends Utility {
    public static Object invokeControllerMethod(Mapping mapping, HttpServletRequest req)
            throws ReflectiveOperationException {
        String className = mapping.getClassName();
        String methodName = mapping.getMethodName();
        
        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getDeclaredMethod(methodName);
            Object instance = clazz.getDeclaredConstructor().newInstance();
            return method.invoke(instance);
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
