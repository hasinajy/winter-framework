package winter.service;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;

import jakarta.servlet.ServletContext;
import winter.FrontController;
import winter.data.Mapping;
import winter.data.MappingMethod;
import winter.data.annotation.Controller;
import winter.data.annotation.http.UrlMapping;
import winter.data.exception.annotation.DuplicateMappingException;
import winter.data.exception.initialization.InvalidPackageNameException;
import winter.data.exception.initialization.PackageProviderNotFoundException;
import winter.util.DataUtil;
import winter.util.DirectoryScanner;

public class ControllerScanner {
    private String packageName;

    public void scanControllers(ServletContext servletContext)
            throws PackageProviderNotFoundException, InvalidPackageNameException, URISyntaxException, IOException,
            ClassNotFoundException {

        packageName = servletContext.getInitParameter("ControllersPackage");

        if (packageName == null) {
            throw new PackageProviderNotFoundException("No package provider was found from the configurations");
        }

        if (!DataUtil.isValidPackageName(packageName)) {
            throw new InvalidPackageNameException("Invalid package name from the configurations");
        }

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(packageName.replace(".", "/"));

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            scanControllers(resource);
        }
    }

    private void scanControllers(URL resource)
            throws URISyntaxException, IOException, ClassNotFoundException {

        if (!packageName.endsWith(".")) {
            packageName += ".";
        }

        for (String fileName : DirectoryScanner.listFiles(resource)) {
            if (fileName.endsWith(".class")) {
                processClassFile(fileName);
            } else {
                processSubdirectory(resource, fileName);
            }
        }
    }

    private void processClassFile(String fileName)
            throws ClassNotFoundException {

        String className = packageName + fileName.substring(0, fileName.length() - 6);
        Class<?> clazz = Class.forName(className);

        if (clazz.isAnnotationPresent(Controller.class)) {
            processControllerMethods(clazz);
        }
    }

    private void processControllerMethods(Class<?> clazz)
            throws DuplicateMappingException {

        Map<String, Mapping> urlMappings = FrontController.getUrlMappings();
        Method[] methods = clazz.getMethods();

        for (Method method : methods) {
            UrlMapping urlMappingAnnotation = method.getAnnotation(UrlMapping.class);

            if (urlMappingAnnotation != null) {
                String url = urlMappingAnnotation.value();
                MappingMethod mappingMethod = new MappingMethod(method);
                mappingMethod.setAuth(clazz);

                Mapping mapping = new Mapping();
                mapping.setClassName(clazz.getName());
                mapping.addMethod(mappingMethod);
                mapping = urlMappings.putIfAbsent(url, mapping);

                if (mapping != null) {
                    mapping.addMethod(mappingMethod);
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void processSubdirectory(URL parentPath, String subdirectoryName)
            throws URISyntaxException, IOException, ClassNotFoundException {

        URL potentialSubDirURL = new URL(parentPath.toString() + "/" + subdirectoryName);
        URI subDirURI = potentialSubDirURL.toURI();

        if (subDirURI.getScheme() != null && subDirURI.getPath() != null) {
            // Save the current package name
            String originalPackageName = packageName;

            // Append subdirectory name
            packageName += subdirectoryName + ".";

            // Scan the subdirectory
            scanControllers(potentialSubDirURL);

            // Restore the original package name
            packageName = originalPackageName;
        }
    }
}