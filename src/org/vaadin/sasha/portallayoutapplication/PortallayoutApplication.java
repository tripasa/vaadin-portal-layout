package org.vaadin.sasha.portallayoutapplication;

import org.vaadin.henrik.drawer.Drawer;
import org.vaadin.sasha.portallayout.PortalLayout;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.incubator.dashlayout.ui.HorDashLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class PortallayoutApplication extends Application {

  boolean debugMode = false;

  private Window mainWindow;

  private boolean flag = true;
  
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

    if (debugMode) {
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
    ((VerticalLayout) bottomPanel.getContent()).setMargin(false);
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
    ((VerticalLayout) sidePanel.getContent()).setMargin(false);
    sidePanel.getContent().setWidth("350px");
    sidePanel.getContent().setHeight("100%");
    sidePanel.addComponent(sidePortal);

    windowLayout.addComponent(mainPanel);
    windowLayout.setExpandRatio(mainPanel, 1.0f);

    windowLayout.addComponent(sidePanel);

    //overrideWindowContentWIthTestData();
    
    setMainWindow(mainWindow);
  }

  private void testGrid() {
    HorDashLayout l;
    final HorizontalLayout layout = new HorizontalLayout();
    layout.setSizeFull();
    mainWindow.setContent(layout);

    Table table1 = createTableTest();
    Table table2 = createTableTest();
    layout.addComponent(table1);
    layout.addComponent(table2);
  }

  public Table createTableTest() {
    final Table table = new Table("", new TestIndexedContainer());
    table.setSelectable(true);
    table.setWidth("100%");
    table.setHeight("300px");
    return table;
  }

  public class TestIndexedContainer extends IndexedContainer
  {
    public TestIndexedContainer() {
      super();
      addContainerProperty("test1", String.class, "0");
      addContainerProperty("test2", String.class, "0");
      Item item = getItem(addItem());
      item.getItemProperty("test1").setValue("test1");
      item.getItemProperty("test2").setValue("test2");
    }
  }

  private void overrideWindowContentWIthTestData() {
    final VerticalLayout layout = new VerticalLayout();
    layout.setSpacing(true);
    layout.setWidth("100%");
    
    final Drawer dr = new Drawer();
    dr.setWidth("100%");
    dr.setDrawerHeight(500);
    
    final VerticalLayout nestedLayout = new VerticalLayout();
    nestedLayout.setWidth("100%");
    //nestedLayout.setHeight("50%");
    
    TextArea ta = new TextArea();
    ta.setWidth("200px");
    
    nestedLayout.addComponent(createTableTest());
    dr.setDrawerComponent(nestedLayout);
    
    layout.addComponent(dr);
    
    final Button b = new Button("add new");
    b.addListener(new Button.ClickListener() {      
      @Override
      public void buttonClick(ClickEvent event) {
        nestedLayout.addComponent(new Label("asdljslkajdaksj"));
        layout.setSpacing(!layout.isSpacing());
      }
    });
    layout.addComponent(b);
    layout.addComponent(new Button("addasdad"));
    mainWindow.setContent(layout);
  }

  private void addPortletWithContents(final ComponentContainer portal) {
    if (flag)
    {
      final Panel vl = new Panel();
      vl.setHeight("200px");
      vl.setWidth("100%");

      vl.getContent().setHeight("100%");
      vl.getContent().setWidth("100%");
      final TextField tf = new TextField();
      tf.setImmediate(true);

      
      TextArea text = new TextArea();
      text.setSizeFull();
      tf.setWidth("100%");
      vl.addComponent(tf);

      vl.addComponent(text);
      portal.addComponent(vl);
      ((PortalLayout)portal).setComponentCaption(vl, "Layout in portal");
      ((PortalLayout)portal).setCollapsible(vl, false);
    }
    else
    {
      Component c = createTableTest();
      portal.addComponent(c);
      ((PortalLayout)portal).setComponentCaption(c, "Table dummy");
      ((PortalLayout)portal).setClosable(c, false);
    }
    flag = !flag;
    
  }

}
