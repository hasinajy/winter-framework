package winter.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
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
import winter.utils.DirectoryUtil;

public class FrontController extends HttpServlet {
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

      out.println("<br/><br/>" + "<b>List of annotated controllers</b>:");
      for (String controllerName : controllers) {
        out.println("<br/>" + "- " + controllerName);
      }
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
          this.controllers.add(className);
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
