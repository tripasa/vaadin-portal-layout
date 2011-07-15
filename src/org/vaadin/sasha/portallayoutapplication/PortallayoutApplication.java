package org.vaadin.sasha.portallayoutapplication;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class PortallayoutApplication extends Application {

  boolean debugMode = false;

  private Window mainWindow;

  @Override
  public void init() {

    setTheme("portallayouttheme");
    mainWindow = new Window("Portallayout Application");
    testInit();
    setMainWindow(mainWindow);
  }

  public void testInit() {
    HorizontalLayout windowLayout = new HorizontalLayout();
    windowLayout.setSizeFull();

    mainWindow.setContent(windowLayout);

    PortalTabSheet tabs = new PortalTabSheet();
    tabs.setSizeFull();
    windowLayout.addComponent(tabs);
    windowLayout.setExpandRatio(tabs, 1.0f);

   // windowLayout.addComponent(sidePanel);

  }

}
