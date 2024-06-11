package winter.utils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;

import jakarta.servlet.ServletContext;
import winter.data.Mapping;
import winter.exceptions.DuplicateMappingException;
import winter.exceptions.InvalidPackageNameException;
import winter.exceptions.PackageProviderNotFoundException;
import winter.annotations.*;

public class AnnotationScanner extends Utility {
    public static Map<String, Mapping> scanControllers(ServletContext servletContext, Map<String, Mapping> urlMappings)
            throws PackageProviderNotFoundException, InvalidPackageNameException, URISyntaxException, IOException,
            ClassNotFoundException {
        String controllersPackage = servletContext.getInitParameter("ControllersPackage");

        if (controllersPackage == null) {
            throw new PackageProviderNotFoundException("No package provider was found in the configuration file");
        }

        if (!PackageNameValidator.isValidPackageName(controllersPackage)) {
            throw new InvalidPackageNameException("Invalid provided package name in the configuration file");
        }

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(controllersPackage.replace(".", "/"));

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            scanControllers(resource, controllersPackage, urlMappings);
        }

        return urlMappings;
    }

    private static void scanControllers(URL directory, String packageName, Map<String, Mapping> urlMappings)
            throws URISyntaxException, IOException, ClassNotFoundException {
        if (!packageName.endsWith(".")) {
            packageName += ".";
        }

        for (String fileName : DirectoryScanner.listFiles(directory)) {
            if (fileName.endsWith(".class")) {
                processClassFile(packageName, fileName, urlMappings);
            } else {
                processSubdirectory(directory, packageName, fileName, urlMappings);
            }
        }
    }

    private static void processClassFile(String packageName, String fileName,
            Map<String, Mapping> urlMappings)
            throws ClassNotFoundException {
        String className = packageName + fileName.substring(0, fileName.length() - 6);
        Class<?> clazz = Class.forName(className);

        if (clazz.isAnnotationPresent(Controller.class)) {
            processControllerMethods(clazz, urlMappings);
        }
    }

    private static void processControllerMethods(Class<?> clazz, Map<String, Mapping> urlMappings)
            throws DuplicateMappingException {
        Method[] methods = clazz.getMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(GetMapping.class)) {
                String sURL = method.getAnnotation(GetMapping.class).value();
                String sMethod = method.getName();
                Class<?>[] methodParamTypes = method.getParameterTypes();

                if (urlMappings.putIfAbsent(sURL, new Mapping(clazz.getName(), sMethod, methodParamTypes)) != null) {
                    throw new DuplicateMappingException("Duplicate mapping found for URL: " + sURL);
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    private static void processSubdirectory(URL directory, String packageName, String subdirectoryName,
            Map<String, Mapping> urlMappings)
            throws URISyntaxException, IOException, ClassNotFoundException {
        URL potentialSubDirURL = new URL(directory.toString() + "/" + subdirectoryName);
        URI subDirURI = potentialSubDirURL.toURI();

        if (subDirURI.getScheme() != null && subDirURI.getPath() != null) {
            scanControllers(potentialSubDirURL, packageName + subdirectoryName + ".", urlMappings);
        }
    }
}