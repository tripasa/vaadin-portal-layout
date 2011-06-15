package org.vaadin.sasha.portallayoutapplication;

import org.vaadin.sasha.portallayout.PortalLayout;

import com.vaadin.Application;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class PortallayoutApplication extends Application {
	@Override
	public void init() {
	  VerticalLayout ll = new VerticalLayout();
	  CssLayout l = new CssLayout();
	  
		Window mainWindow = new Window("Portallayout Application");
		PortalLayout label = new PortalLayout();
		mainWindow.addComponent(label);
		setMainWindow(mainWindow);
	}
	
	
}
