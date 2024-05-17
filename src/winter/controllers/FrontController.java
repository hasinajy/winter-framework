package winter.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import winter.annotations.*;
import winter.data.Mapping;
import winter.utils.DirectoryUtil;

public class FrontController extends HttpServlet {
  private HashMap<String, Mapping> URLMappings = new HashMap<>();

  @Override
  public void init() throws ServletException {
    ServletContext servletContext = getServletContext();

    try {
      scanControllers(servletContext);
    } catch (Exception e) {
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    processRequest(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    processRequest(req, resp);
  }

  private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String requestURL = req.getRequestURL().toString();

    PrintWriter out = resp.getWriter();
    out.println("<b>Request URL</b>: " + requestURL);
  }

  private void scanControllers(ServletContext servletContext)
      throws URISyntaxException, IOException, ClassNotFoundException {
    String controllersPackage = servletContext.getInitParameter("ControllersPackage");
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    Enumeration<URL> resources = classLoader.getResources(controllersPackage.replace(".", "/"));

    while (resources.hasMoreElements()) {
      URL resource = resources.nextElement();
      scanControllers(resource, controllersPackage);
    }
  }

  @SuppressWarnings("deprecation")
  private void scanControllers(URL directory, String packageName)
      throws URISyntaxException, IOException, ClassNotFoundException {
    if (!packageName.endsWith(".")) {
      packageName += ".";
    }

    for (String fileName : DirectoryUtil.listFiles(directory)) {
      if (fileName.endsWith(".class")) {
        String className = packageName + fileName.substring(0, fileName.length() - 6);
        Class<?> clazz = Class.forName(className);

        if (clazz.isAnnotationPresent(Controller.class)) {
          Method[] methods = clazz.getMethods();

          for (Method method: methods) {
            if (method.isAnnotationPresent(GetMapping.class)) {
              String sURL = method.getAnnotation(GetMapping.class).value();
              String sMethod = method.getName();

              this.URLMappings.put(sURL, new Mapping(className, sMethod));
            }
          }
        }
      } else {
        URL potentialSubDirURL = new URL(directory.toString() + "/" + fileName);

        try {
          URI subDirURI = potentialSubDirURL.toURI();

          if (subDirURI.getScheme() != null && subDirURI.getPath() != null) {
            scanControllers(potentialSubDirURL, packageName + fileName + ".");
          }
        } catch (URISyntaxException e) {
          throw e;
        }
      }
    }
  }
}
