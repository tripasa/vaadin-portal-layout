package org.vaadin.sasha.portallayoutapplication;

import org.vaadin.sasha.portallayout.PortalLayout;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class PortallayoutApplication extends Application {
  
  boolean debugMode = false;
  
  private Window mainWindow;
  
  @Override
  public void init() {

    setTheme("portallayouttheme");
    
    mainWindow = new Window("Portallayout Application");
    
    HorizontalLayout windowLayout = new HorizontalLayout();
    windowLayout.setSizeFull();
    
    
    mainWindow.setContent(windowLayout);

    final Panel mainPanel = new Panel("Column Styled Portal");
    mainPanel.addStyleName("light");
    mainPanel.setSizeFull();
    mainPanel.setContent(new HorizontalLayout());
    
    final HorizontalLayout ll = (HorizontalLayout) mainPanel.getContent();

    ll.setMargin(false);
    ll.setWidth("100%");
    final PortalLayout widePortal = new PortalLayout();
    addPortletWithContents(widePortal);

    ll.setSpacing(true);
    ll.addComponent(widePortal);
    ll.setExpandRatio(widePortal, 0.5f);
    for (int i = 0; i < 4; ++i) {
      final PortalLayout p = new PortalLayout();
      addPortletWithContents(p);
      ll.addComponent(p);
      ll.setExpandRatio(p, 0.3f);
    }

    

    if (debugMode)
    {
      Button b = new Button("Add new");
      addPortletWithContents(widePortal);

      b.addListener(new Button.ClickListener() {
        @Override
        public void buttonClick(ClickEvent event) {
          addPortletWithContents(widePortal);
        }
      });
      ll.addComponent(b);
    }

    final Panel bottomPanel = new Panel("Bottom Portal");
    bottomPanel.addStyleName("light");
    bottomPanel.setWidth("100%");
    ((VerticalLayout)bottomPanel.getContent()).setMargin(false);
    bottomPanel.getContent().setWidth("100%");
    bottomPanel.getContent().setHeight("260px");
    
    
    final PortalLayout bottomPortlet = new PortalLayout();
    bottomPortlet.setHeight("260px");
    bottomPanel.getContent().addComponent(bottomPortlet);

    final PortalLayout sidePortal = new PortalLayout();
    
    final Panel sidePanel = new Panel("Side Portal");
    sidePanel.addStyleName("light");
    sidePanel.setWidth("350px");
    sidePanel.setHeight("100%");
    ((VerticalLayout)sidePanel.getContent()).setMargin(false);
    sidePanel.getContent().setWidth("350px");
    sidePanel.getContent().setHeight("100%");
    sidePanel.addComponent(sidePortal);

    
    windowLayout.addComponent(mainPanel);
    windowLayout.setExpandRatio(mainPanel, 1.0f);
    
    windowLayout.addComponent(sidePanel);
    
//   overrideWindowContentWIthTestData();
    setMainWindow(mainWindow);
  }

  private void testGrid() {
    final GridLayout layout = new GridLayout(3, 3);
    layout.setSizeFull();
    layout.addComponent(new MenuBar(), 0, 0, 2, 0);
    mainWindow.setContent(layout);
  }

  private void overrideWindowContentWIthTestData() {
    Panel p = new Panel();
    //p.getContent().setSizeFull();
    p.setSizeFull();
    p.getContent().setWidth("100%");
//    for (int i = 0; i < 100; ++i)
//      p.addComponent(new Button("test")); 
    //WeeLayout vl = new WeeLayout(WeeLayout.Direction.VERTICAL);
    PortalLayout vl = new PortalLayout();
    vl.setSizeFull();
    addPortletWithContents(vl);
    addPortletWithContents(vl);
    addPortletWithContents(vl);
    p.addComponent(vl);
    //mainWindow.setContent(p);
   
    //p.setContent(vl);
    mainWindow.setContent(p);

    //vl.setWidth("100%");
    //vl.addComponent();
  }

  private void addPortletWithContents(final ComponentContainer portal) {
    final Panel vl = new Panel();
    vl.setHeight("200px");
    vl.setWidth("100%");
    
    vl.getContent().setHeight("100%");
    vl.getContent().setWidth("100%");
    final TextField tf = new TextField();
    tf.setImmediate(true);

    IndexedContainer container = new IndexedContainer();
    container.addContainerProperty("testProp1", String.class, "0");
    container.addContainerProperty("testProp2", String.class, "0");

    Item item = container.getItem(container.addItem());
   
    item.getItemProperty("testProp1").setValue("test1");
    item.getItemProperty("testProp2").setValue("test2");
    
    TextArea text = new TextArea();
    text.setSizeFull();
    tf.setWidth("100%");
    vl.addComponent(tf);

    vl.addComponent(text);
    portal.addComponent(vl);
  }

}
