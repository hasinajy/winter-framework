package winter.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import winter.data.Mapping;
import winter.utils.AnnotationScanner;

public class FrontController extends HttpServlet {

  private HashMap<String, Mapping> URLMappings = new HashMap<>();

  @Override
  public void init() throws ServletException {
    ServletContext servletContext = getServletContext();

    try {
      this.URLMappings = AnnotationScanner.scanControllers(servletContext);
    } catch (Exception e) {
      System.err.println(e.getMessage());
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
    String[] splitRequest = requestURL.split("/");
    String targetURL = splitRequest[splitRequest.length - 1];

    PrintWriter out = resp.getWriter();
    out.println("<b>Request URL</b>: " + requestURL);

    try {
      String className = this.URLMappings.get(targetURL).getClassName();
      String methodName = this.URLMappings.get(targetURL).getMethodName();

      out.println("<br/><br/><b>Target Controller:</b>");
      out.println("<br/>- <b>Target Mapping:</b> " + targetURL);
      out.println("<br/>- <b>Controller:</b> " + className);
      out.println("<br/>- <b>Method:</b> " + methodName);

      Class<?> clazz = Class.forName(className);
      Method method = clazz.getDeclaredMethod(methodName, new Class<?>[] {});

      out.println("<br/>- <b>Returned value:</b> "
      + method.invoke(clazz.getDeclaredConstructor().newInstance()).toString());
    } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
        | InvocationTargetException e) {
      e.printStackTrace(out);
    } catch (Exception e) {
      out.println("<br/>>>> Mapping not found.");
      e.printStackTrace(out);
    }
  }

}
