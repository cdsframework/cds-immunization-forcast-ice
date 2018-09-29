package org.cdsframework.cds.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.tch.fc.model.Service;
import org.tch.fc.model.Software;

public class MonitorServlet extends HttpServlet {

  private static StringBuffer log = new StringBuffer();
  private static Software software = null;

  public static Software getSoftware() {
    return software;
  }

  private static final String PARAM_SERVICE_TYPE = "serviceType";
  private static final String PARAM_URL = "url";
  private static final String PARAM_USERID = "userid";
  private static final String PARAM_FACILITYID = "facilityid";
  private static final String PARAM_PASSWORD = "password";
  private static final String PARAM_ACTION = "action";
  private static final String ACTION_SAVE = "Save";

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
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    doGet(req, resp);
  }


  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String action = req.getParameter(PARAM_ACTION);
    if (action != null) {
      if (action.equals(ACTION_SAVE)) {
        String serviceType = req.getParameter(PARAM_SERVICE_TYPE);
        String url = req.getParameter(PARAM_URL);
        String userid = req.getParameter(PARAM_USERID);
        String facilityid = req.getParameter(PARAM_FACILITYID);
        String password = req.getParameter(PARAM_PASSWORD);
        Service service = Service.getService(serviceType);
        software = new Software();
        software.setService(service);
        software.setServiceUrl(url);
        software.setServiceUserid(userid);
        software.setServiceFacilityid(facilityid);
        software.setServicePassword(password);
        logStatus("Update connection to: " + url);
      }
    }
    resp.setContentType("text/html");
    PrintWriter out = new PrintWriter(resp.getOutputStream());
    out.println("<html>");
    out.println("  <head><title>Setup and Monitor</title></head>");
    out.println("  <body>");
    out.println("    <h2>Setup</h2>");
    if (software == null) {
      software = new Software();
    }
    out.println("    <form method=\"POST\" action=\"monitor\">");
    out.println("    <table>");
    out.println("      <tr>");
    out.println("        <th>Software Type</th>");
    out.println("        <td>");
    out.println("          <select name=\"" + PARAM_SERVICE_TYPE + "\">");
    for (Service service : Service.values()) {
      out.println("            <option value=\"" + service.getServiceType() + "\""
          + (service.equals(software.getService()) ? "" : " checked=\"true\"") + ">"
          + service.getLabel() + "</option>");
    }
    out.println("          </select>");
    out.println("        </td>");
    out.println("      </tr>");
    out.println("      <tr>");
    out.println("        <th>Service URL</th>");
    out.println("        <td><input type=\"text\" name=\"" + PARAM_URL + "\" size=\"60\" value=\""
        + software.getServiceUrl() + "\"/></td>");
    out.println("      </tr>");
    out.println("      <tr>");
    out.println("        <th>User Id</th>");
    out.println("        <td><input type=\"text\" name=\"" + PARAM_USERID
        + "\" size=\"20\" value=\"" + software.getServiceUserid() + "\"/></td>");
    out.println("      </tr>");
    out.println("      <tr>");
    out.println("        <th>Password</th>");
    out.println("        <td><input type=\"text\" name=\"" + PARAM_PASSWORD
        + "\" size=\"20\" value=\"" + software.getServicePassword() + "\"/></td>");
    out.println("      </tr>");
    out.println("      <tr>");
    out.println("        <th>Facility Id</th>");
    out.println("        <td><input type=\"text\" name=\"" + PARAM_FACILITYID
        + "\" size=\"20\" value=\"" + software.getServiceFacilityid() + "\"/></td>");
    out.println("      </tr>");
    out.println("      <tr>");
    out.println("        <td colspan=\"2\" align=\"right\"><input type=\"submit\" name=\""
        + PARAM_ACTION + "\" value=\"" + ACTION_SAVE + "\"/></td>");
    out.println("      </tr>");
    out.println("    </table>");
    out.println("    </form>");
    out.println("    <h2>Monitor</h2>");
    out.println("    <pre>" + log + "</pre>");
    out.println("  </body>");
    out.println("</html>");
    out.flush();
  }
}
