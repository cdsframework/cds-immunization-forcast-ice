package org.cdsframework.cds.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MonitorServlet extends HttpServlet {

  private static StringBuffer log = new StringBuffer();

  public static void logStatus(String s) {
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss zz");
    log.append(sdf.format(new Date()));
    log.append(" | ");
    log.append(s);
    log.append("\n");
  }

  static {
    logStatus("Monitor instantiated");
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    resp.setContentType("text/html");
    PrintWriter out = new PrintWriter(resp.getOutputStream());
    out.println("<html>");
    out.println("  <head><title>Monitor</title></head>");
    out.println("  <body>");
    out.println("    <h1>Monitor</h1>");
    out.println("    <pre>" + log + "</pre>");
    out.println("  </body>");
    out.println("</html>");
    out.flush();
  }
}
