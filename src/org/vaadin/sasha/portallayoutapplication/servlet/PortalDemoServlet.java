package org.vaadin.sasha.portallayoutapplication.servlet;

import java.io.BufferedWriter;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.ApplicationServlet;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class PortalDemoServlet extends ApplicationServlet {

  @Override
  protected void writeAjaxPageHtmlVaadinScripts(Window window,
      String themeName, Application application, BufferedWriter page,
      String appUrl, String themeUri, String appId, HttpServletRequest request)
      throws ServletException, IOException {
    super.writeAjaxPageHtmlVaadinScripts(window, themeName, application, page,
        appUrl, themeUri, appId, request);
  }
}
