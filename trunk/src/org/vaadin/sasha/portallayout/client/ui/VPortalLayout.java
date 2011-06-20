package org.vaadin.sasha.portallayout.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.layout.CellBasedLayout;
import com.vaadin.terminal.gwt.client.ui.layout.ChildComponentContainer;

/**
 * Client-side implementation of the portal layout. 
 * @author p4elkin
 */
public class VPortalLayout extends ComplexPanel implements Paintable, Container {

  public static final String CLASSNAME = "v-portallayout";

  protected Map<Widget, ChildComponentContainer> widgetToComponentContainer = 
    new HashMap<Widget, ChildComponentContainer>();
  
  private List<DivElement> columns = new ArrayList<DivElement>();
  
  private Element root = Document.get().createDivElement();
  
  protected String paintableId;

  protected ApplicationConnection client;

  protected List<Panel> childPanels = new LinkedList<Panel>();
  
  public VPortalLayout() {
    super();
    
    root.setClassName(CLASSNAME);
    root = Document.get().createDivElement();
    root.getStyle().setProperty("width", "100px");
    root.getStyle().setProperty("height", "100px");
    root.getStyle().setProperty("overflow", "hidden");
    root.getStyle().setBorderStyle(BorderStyle.DASHED);
    root.getStyle().setBorderColor("black");    
    
    setElement(root);
  }

  public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
    if (client.updateComponent(this, uidl, true)) {
      return;
    }
   
    int cols = uidl.getIntAttribute("cls");
    updateColumnLayout(cols);
    
    this.client = client;
    paintableId = uidl.getId();
    
    int count = uidl.getChildCount();
    Iterator<Object> it = uidl.getChildIterator();
    while (it.hasNext())
    {
      final UIDL childUIDL = (UIDL) it.next();
      final Paintable child = client.getPaintable(childUIDL);
      
      Widget widget = (Widget) child;
      ChildComponentContainer c = widgetToComponentContainer.get(widget);
      
      if (c == null)
      {
        c = new ChildComponentContainer(widget, CellBasedLayout.ORIENTATION_VERTICAL);
        widgetToComponentContainer.put(widget, c);
        getChildren().add(c);
        getElement().appendChild(c.getElement());
        adopt(c);
      }
      c.updateWidgetSize();
      c.renderChild(childUIDL, client, -1);
      c.setContainerSize(c.getWidgetSize().getWidth(), c.getWidgetSize().getHeight());
    }
  }

  
  private void updateColumnLayout(int cols) {
    
    if (cols == 0)
      cols = 5;
    int totalWidth = ((DivElement)root).getClientWidth();
    int totalHeight = ((DivElement)root).getClientHeight();
    
    while (columns.size() < cols) {
        final DivElement column = Document.get().createDivElement();
        column.getStyle().setBorderColor("red");
        column.getStyle().setBorderStyle(BorderStyle.DASHED);
        column.getStyle().setBorderWidth(3, Style.Unit.PX);
        root.appendChild(column);
        columns.add(column);
    }
    
    double sharedWidth = totalWidth / columns.size();
    
    for (DivElement e : columns)
    {
      e.getStyle().setHeight(totalHeight, Style.Unit.PX);
      e.getStyle().setWidth(sharedWidth, Style.Unit.PX);
    }
  }

  @Override
  public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
  }

  @Override
  public boolean hasChildComponent(Widget component) {
    return widgetToComponentContainer.containsKey(component);
  }

  @Override
  public void updateCaption(Paintable component, UIDL uidl) {    
  }

  @Override
  public boolean requestLayout(Set<Paintable> children) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public RenderSpace getAllocatedSpace(Widget child) {
    return null;
  }
}
