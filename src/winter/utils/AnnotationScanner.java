package winter.utils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;

import jakarta.servlet.ServletContext;
import winter.data.Mapping;
import winter.annotations.*;

public class AnnotationScanner {

    public static HashMap<String, Mapping> scanControllers(ServletContext servletContext)
            throws URISyntaxException, IOException, ClassNotFoundException {
        String controllersPackage = servletContext.getInitParameter("ControllersPackage");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(controllersPackage.replace(".", "/"));

        HashMap<String, Mapping> URLMappings = new HashMap<>();

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            scanControllers(resource, controllersPackage, URLMappings);
        }

        return URLMappings;
    }

    @SuppressWarnings("deprecation")
    private static void scanControllers(URL directory, String packageName, HashMap<String, Mapping> URLMappings)
            throws URISyntaxException, IOException, ClassNotFoundException {
        if (!packageName.endsWith(".")) {
            packageName += ".";
        }

        for (String fileName : DirectoryScanner.listFiles(directory)) {
            if (fileName.endsWith(".class")) {
                String className = packageName + fileName.substring(0, fileName.length() - 6);
                Class<?> clazz = Class.forName(className);

                if (clazz.isAnnotationPresent(Controller.class)) {
                    Method[] methods = clazz.getMethods();

                    for (Method method : methods) {
                        if (method.isAnnotationPresent(GetMapping.class)) {
                            String sURL = method.getAnnotation(GetMapping.class).value();
                            String sMethod = method.getName();

                            URLMappings.put(sURL, new Mapping(className, sMethod));
                        }
                    }
                }
            } else {
                URL potentialSubDirURL = new URL(directory.toString() + "/" + fileName);

                try {
                    URI subDirURI = potentialSubDirURL.toURI();

                    if (subDirURI.getScheme() != null && subDirURI.getPath() != null) {
                        scanControllers(potentialSubDirURL, packageName + fileName + ".", URLMappings);
                    }
                } catch (URISyntaxException e) {
                    throw e;
                }
            }
        }
    }

}