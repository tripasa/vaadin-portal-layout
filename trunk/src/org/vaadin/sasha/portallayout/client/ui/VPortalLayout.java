package org.vaadin.sasha.portallayout.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
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
  
  private List<FlowPanel> columns = new ArrayList<FlowPanel>();
  
  private Element root = Document.get().createDivElement();
  
  protected String paintableId;

  protected ApplicationConnection client;
  
  public VPortalLayout() {
    super();
    
    root.setClassName(CLASSNAME);
    root = Document.get().createDivElement();
    root.getStyle().setProperty("overflow", "hidden");
    
    setElement(root);
  }

  public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
    if (client.updateComponent(this, uidl, true)) {
      return;
    }
   
    int cols = uidl.getIntAttribute("cols");
    updateColumnLayout(cols);
    
    this.client = client;
    paintableId = uidl.getId();
    
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
        if (!columns.isEmpty())
        {
          columns.get(0).add(c);
        }
      }
      c.setWidth(columns.get(0).getOffsetWidth() + "px");
      c.updateWidgetSize();
      c.renderChild(childUIDL, client, -1);
      c.setContainerSize(c.getWidgetSize().getWidth(), c.getWidgetSize().getHeight());
    }
  }

  
  private void updateColumnLayout(int cols) {
    
    while (columns.size() < cols)
    {
        final FlowPanel column = new FlowPanel();
        column.getElement().getStyle().setProperty("float", "left");
        column.getElement().getStyle().setProperty("border", "1px solid red");
        getChildren().add(column);
     
        root.appendChild(column.getElement());
        
        adopt(column);
        
        columns.add(column);
    }
    
    updateColumnSize();
  }

  @Override
  public void setWidth(String width)
  {
    super.setWidth(width);
    updateColumnSize();
  }
  
  private void updateColumnSize()
  {
    if (columns.size() < 1)
      return;
      
    int totalWidth = ((DivElement)root).getClientWidth();
    int totalHeight = ((DivElement)root).getClientHeight();
    
    double sharedWidth = totalWidth / columns.size() - 3;
    
    for (FlowPanel e : columns)
    {
      e.setWidth(sharedWidth + "px");
      e.setHeight(totalHeight + "px");
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
