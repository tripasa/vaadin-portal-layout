package org.vaadin.sasha.portallayoutapplication;

import org.vaadin.sasha.portallayout.PortalLayout;

import com.vaadin.Application;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class PortallayoutApplication extends Application {
	@Override
	public void init() {
	  AbsoluteLayout llll;
	  GridLayout grid;
	  VerticalLayout ll = new VerticalLayout();
	  CssLayout l = new CssLayout();
	  l.setSizeFull();
	  DropHandler handler;
	  DragAndDropWrapper w;
	  
		Window mainWindow = new Window("Portallayout Application");
		mainWindow.getContent().setSizeFull();
		PortalLayout label = new PortalLayout();
		mainWindow.addComponent(new Button());
		mainWindow.addComponent(label);
		setMainWindow(mainWindow);
	}
	
	
}
