package winter.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import winter.annotations.Controller;

public class FrontController extends HttpServlet {
  private boolean isScanned = false;
  private ArrayList<String> controllers = new ArrayList<>();

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

    ServletContext servletContext = getServletContext();
    try {
      scanControllers(servletContext);
    } catch (URISyntaxException uri_e) {
      out.println("URI_E");
      out.println(uri_e.getMessage());
    } catch (IOException io_e) {
      out.println("IO_E");
      out.println(io_e.getMessage());
    } catch (ClassNotFoundException cnf_e) {
      out.println("CNF_E");
      out.println(cnf_e.getMessage());
    } catch (Exception e) {
      out.println("G_E");
      out.println(e.getMessage());
    }
  }

  private void scanControllers(ServletContext servletContext)
      throws URISyntaxException, IOException, ClassNotFoundException {
    if (isScanned) {
      return;
    }

    String controllersPackage = servletContext.getInitParameter("ControllersPackage");
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    Enumeration<URL> resources = classLoader.getResources(controllersPackage.replace(".", "/"));

    while (resources.hasMoreElements()) {
      URL resource = resources.nextElement();
      scanControllers(resource, controllersPackage);
    }

    this.isScanned = true;
  }

  private void scanControllers(URL directory, String packageName)
      throws URISyntaxException, IOException, ClassNotFoundException {
    if (!directory.toURI().toString().endsWith("/")) {
      packageName += ".";
    }

    for (String fileName : listFiles(directory)) {
      if (fileName.endsWith(".class")) {
        String className = packageName + fileName.substring(0, fileName.length() - 6);
        Class<?> clazz = Class.forName(className);
        if (clazz.isAnnotationPresent(Controller.class)) {
          this.controllers.add(className);
        }
      }
    }
  }

  private static ArrayList<String> listFiles(URL directory) throws IOException {
    ArrayList<String> fileNames = new ArrayList<>();
    try (var in = directory.openStream();
        var reader = new BufferedReader(new InputStreamReader(in))) {
      String line;
      while ((line = reader.readLine()) != null) {
        fileNames.add(line);
      }
    }
    return fileNames;
  }
}
