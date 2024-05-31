package winter.utils;

import java.lang.reflect.Method;

public class ReflectionUtil {
    
    public static Object invokeControllerMethod(String className, String methodName, Class<?>[] args) throws Exception {
        Class<?> clazz = Class.forName(className);
        Method method = clazz.getDeclaredMethod(methodName, args);
        return method.invoke(clazz.getDeclaredConstructor().newInstance());
    }

}
