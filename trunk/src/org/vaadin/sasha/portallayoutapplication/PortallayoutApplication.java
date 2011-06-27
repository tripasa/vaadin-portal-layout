package org.vaadin.sasha.portallayoutapplication;


import org.vaadin.sasha.portallayout.PortalLayout;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class PortallayoutApplication extends Application {
	@Override
	public void init() {

		Window mainWindow = new Window("Portallayout Application");
		mainWindow.getContent().setSizeFull();
		
		final VerticalLayout ll = (VerticalLayout) mainWindow.getContent();
		
		final PortalLayout portal = new PortalLayout(6);
		
		//final PortalLayout portal1 = new PortalLayout(1);
		
    Button b = new Button("Show Parent");
    

    appendVL(portal);
    
    /*IndexedContainer container = new IndexedContainer();
    container.addContainerProperty("testProp1", String.class, "0");
    container.addContainerProperty("testProp2", String.class, "0");
    
    Table table = new Table();
    table.setSizeUndefined();
    table.setWidth("1900px");
    table.setContainerDataSource(container);
    table.setVisibleColumns(new String[] {"testProp2","testProp1"});*/
    
    b.addListener(new Button.ClickListener() {
      @Override
      public void buttonClick(ClickEvent event) {
        appendVL(portal);
      }
    });
    
    //ll.addComponent(table);
    ll.addComponent(b);
    
    ll.addComponent(portal);
    //ll.addComponent(portal1);
    ll.setExpandRatio(portal, 1.0f);
    //ll.setExpandRatio(portal1, 1.0f);
    setMainWindow(mainWindow);
	}
	
  private void appendVL(final PortalLayout portal) {
    final VerticalLayout vl = new VerticalLayout();
    vl.setWidth("100%");
    vl.setHeight("200px");
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
  
}
