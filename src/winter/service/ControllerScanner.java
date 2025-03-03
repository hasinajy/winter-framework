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

/**
 * Service class responsible for scanning and registering controllers in the
 * Winter framework.
 * <p>
 * This class scans a specified package for classes annotated with
 * {@link Controller}, processes their
 * methods annotated with {@link UrlMapping}, and registers the mappings in
 * {@link FrontController}.
 * It supports recursive scanning of subdirectories and validates package
 * configurations.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
public class ControllerScanner {

    /** The base package name to scan for controllers. */
    private String packageName;

    /**
     * Scans the servlet context for controllers within the configured package.
     * <p>
     * Retrieves the package name from the servlet context's initialization
     * parameter
     * "ControllersPackage", validates it, and scans the corresponding resources for
     * controller classes. The results are registered in {@link FrontController}'s
     * URL mappings.
     * </p>
     *
     * @param servletContext the servlet context providing configuration
     * @throws PackageProviderNotFoundException if the "ControllersPackage"
     *                                          parameter is not set
     * @throws InvalidPackageNameException      if the package name is invalid
     * @throws URISyntaxException               if a URL cannot be converted to a
     *                                          URI
     * @throws IOException                      if an I/O error occurs while
     *                                          scanning resources
     * @throws ClassNotFoundException           if a class file cannot be loaded
     */
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

    /**
     * Scans a specific resource URL for controller classes.
     * <p>
     * Processes all files and subdirectories under the given resource URL, loading
     * classes and checking for {@link Controller} annotations.
     * </p>
     *
     * @param resource the URL of the resource directory to scan
     * @throws URISyntaxException     if a subdirectory URL cannot be converted to a
     *                                URI
     * @throws IOException            if an I/O error occurs while reading the
     *                                resource
     * @throws ClassNotFoundException if a class file cannot be loaded
     */
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

    /**
     * Processes a single class file to check for controller annotations.
     * <p>
     * Loads the class from the file name and, if annotated with {@link Controller},
     * processes its methods for URL mappings.
     * </p>
     *
     * @param fileName the name of the class file (e.g., "MyController.class")
     * @throws ClassNotFoundException if the class cannot be found or loaded
     */
    private void processClassFile(String fileName)
            throws ClassNotFoundException {

        String className = packageName + fileName.substring(0, fileName.length() - 6);
        Class<?> clazz = Class.forName(className);

        if (clazz.isAnnotationPresent(Controller.class)) {
            processControllerMethods(clazz);
        }
    }

    /**
     * Processes methods of a controller class for URL mappings.
     * <p>
     * Scans the class's methods for {@link UrlMapping} annotations, constructs
     * mappings, and registers them in {@link FrontController}'s URL mappings.
     * Multiple methods for the same URL are grouped together.
     * </p>
     *
     * @param clazz the controller class to process
     * @throws DuplicateMappingException if a mapping conflict occurs (not currently
     *                                   thrown)
     */
    private void processControllerMethods(Class<?> clazz)
            throws DuplicateMappingException {

        Map<String, Mapping> urlMappings = FrontController.getUrlMappings();
        Method[] methods = clazz.getMethods();

        for (Method method : methods) {
            UrlMapping urlMappingAnnotation = method.getAnnotation(UrlMapping.class);

            if (urlMappingAnnotation != null) {
                String url = DataUtil.getUrlMapping(clazz, method);
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

    /**
     * Processes a subdirectory by recursively scanning its contents.
     * <p>
     * Constructs a URI for the subdirectory, appends it to the package name,
     * and recursively scans it for controllers before restoring the original
     * package name.
     * </p>
     *
     * @param parentPath       the URL of the parent directory
     * @param subdirectoryName the name of the subdirectory
     * @throws URISyntaxException     if the subdirectory URI is malformed
     * @throws IOException            if an I/O error occurs while scanning the
     *                                subdirectory
     * @throws ClassNotFoundException if a class file in the subdirectory cannot be
     *                                loaded
     */
    private void processSubdirectory(URL parentPath, String subdirectoryName)
            throws URISyntaxException, IOException, ClassNotFoundException {

        URI parentURI = parentPath.toURI();
        URI potentialSubDirURI = parentURI.resolve(subdirectoryName + "/");

        if (potentialSubDirURI.getScheme() != null && potentialSubDirURI.getPath() != null) {
            // Save the current package name
            String originalPackageName = packageName;

            // Append subdirectory name
            packageName += subdirectoryName + ".";

            // Scan the subdirectory
            scanControllers(potentialSubDirURI.toURL());

            // Restore the original package name
            packageName = originalPackageName;
        }
    }
}