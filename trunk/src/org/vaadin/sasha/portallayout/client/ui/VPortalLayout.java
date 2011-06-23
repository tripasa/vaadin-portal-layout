package org.vaadin.sasha.portallayout.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vaadin.sasha.portallayout.PortalLayout;
import org.vaadin.sasha.portallayout.client.PortalDropController;
import org.vaadin.sasha.portallayout.client.dnd.PickupDragController;
import org.vaadin.sasha.portallayout.client.dnd.drop.FlowPanelDropController;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.UIDL;

/**
 * Client-side implementation of the portal layout. 
 * @author p4elkin
 */
public class VPortalLayout extends ComplexPanel implements Paintable, Container {

  public static final String COMPONENT_ADDED = "componentAdded";
  
  public static final String COMPONENT_REMOVED = "componentRemoved";
  
  public static final String CLASSNAME = "v-portallayout";
  
  private final static PickupDragController cs_dragControl = new PickupDragController(RootPanel.get(), false);
  
  protected Map<Widget, Portlet> widgetToComponentContainer = new HashMap<Widget, Portlet>();
  
  private List<FlowPanel> columns = new ArrayList<FlowPanel>();
  
  private Element root = Document.get().createDivElement();
  
  protected String paintableId;

  protected ApplicationConnection client;
  
  protected PortalDropController dropController;
    
  public VPortalLayout() {
    super();
    
    root.setClassName(CLASSNAME);
    root = Document.get().createDivElement();
    root.getStyle().setProperty("overflow", "hidden");
    
    setElement(root);
    
    dropController = new PortalDropController(this);
    cs_dragControl.registerDropController(dropController);
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
      Portlet c = widgetToComponentContainer.get(widget);      
      if (c == null)
      {
        c = new Portlet(widget, this);
        cs_dragControl.makeDraggable(c, c.getHeader());
        widgetToComponentContainer.put(widget, c);
        if (!columns.isEmpty())
          columns.get(0).add(c);
      }      
      c.renderContent(childUIDL, client);
      c.updateSize(columns.get(0).getOffsetWidth(), widget.getOffsetHeight());
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
    return false;
  }

  @Override
  public RenderSpace getAllocatedSpace(Widget child) {
    Portlet c = widgetToComponentContainer.get(child);
    if (c == null)
      return null;
    return new RenderSpace(c.getContentWidth(), c.getContentHeight());
  }

  public FlowPanel getColumnByMousePosition(int mouseX, int mouseY) {
    if (mouseX < getAbsoluteLeft() ||
        mouseY < getAbsoluteTop() ||
        mouseX > getAbsoluteLeft() + getOffsetWidth() ||
        mouseY > getAbsoluteTop() + getOffsetHeight()
        )
      return null;
    
    int mouseXOffset = mouseX - getAbsoluteLeft();
    int columnIdx = (int)Math.floor(Double.valueOf(mouseXOffset * columns.size()) / ((double)getOffsetWidth()));
    
    assert columnIdx >= 0 &&
           columnIdx < columns.size();
      
    return columns.get(columnIdx);
  }

  public void handlePortletRemoved(Portlet portlet) {
    final Paintable child = portlet.getContentAsPaintable();
    if (child == null)
      return;
    portlet.setParentPortal(null);
    widgetToComponentContainer.remove(portlet.getContent());
    client.updateVariable(paintableId, COMPONENT_REMOVED, child, true);
  }

  public void handlePortletAdded(Portlet portlet) {
    final Paintable child = portlet.getContentAsPaintable();
    if (child == null)
      return;
    portlet.setParentPortal(this);
    widgetToComponentContainer.put(portlet.getContent(), portlet);
    client.updateVariable(paintableId, COMPONENT_ADDED, child, true);
  }
}
