package org.vaadin.sasha.portallayoutapplication;


import org.vaadin.sasha.portallayout.PortalLayout;

import com.vaadin.Application;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

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
		
		final VerticalLayout ll = (VerticalLayout) mainWindow.getContent();
		
		final PortalLayout portal = new PortalLayout(5);
		
		final PortalLayout portal1 = new PortalLayout(5);
		
    Button b = new Button();
    b.addListener(new Button.ClickListener() {
      @Override
      public void buttonClick(ClickEvent event) {
        Panel vl = new Panel();
        vl.setWidth("100px");
        vl.setHeight("300px");
        final TextField tf = new TextField();
        final TextField tf1 = new TextField();
        final TextField tf2 = new TextField();
        final TextField tf3 = new TextField();
        
        tf.setWidth("100%");
        tf1.setWidth("100%");
        tf2.setWidth("100%");
        tf3.setWidth("100%");
        
        vl.addComponent(tf);
        vl.addComponent(tf1);
        vl.addComponent(tf2);
        vl.addComponent(tf3);
        portal.addComponent(vl);
      }
    });
    
    ll.addComponent(b);
    ll.addComponent(portal);
    ll.addComponent(portal1);
    ll.setExpandRatio(portal, 1.0f);
    ll.setExpandRatio(portal1, 1.0f);
    setMainWindow(mainWindow);
    
		//mainWindow.addComponent(ll);
    //ll.addComponent(new Button());
	}
	
	
}
