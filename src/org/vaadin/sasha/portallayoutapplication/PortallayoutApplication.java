package org.vaadin.sasha.portallayoutapplication;


import org.vaadin.sasha.portallayout.PortalLayout;

import com.vaadin.Application;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class PortallayoutApplication extends Application {
	@Override
	public void init() {
	  AbsoluteLayout llll;
	  GridLayout grid;
	  CssLayout l = new CssLayout();
	  l.setSizeFull();
	  DropHandler handler;
	  DragAndDropWrapper w;
	  
		Window mainWindow = new Window("Portallayout Application");
		mainWindow.getContent().setSizeFull();
		
		VerticalLayout ll = (VerticalLayout) mainWindow.getContent();
		
		final PortalLayout portal = new PortalLayout(5);
    Button b = new Button();
    b.addListener(new Button.ClickListener() {
      @Override
      public void buttonClick(ClickEvent event) {
        final TextField tf = new TextField();
        tf.setWidth("200px");
        portal.addComponent(tf);
      }
    });
    
    ll.addComponent(b);
    ll.addComponent(portal);
    ll.setExpandRatio(portal, 1.0f);
    setMainWindow(mainWindow);
    
		//mainWindow.addComponent(ll);
    //ll.addComponent(new Button());
	}
	
	
}
