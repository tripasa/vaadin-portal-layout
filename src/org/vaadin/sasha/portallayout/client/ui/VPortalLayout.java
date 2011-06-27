package org.vaadin.sasha.portallayout.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vaadin.sasha.portallayout.client.PortalDropController;
import org.vaadin.sasha.portallayout.client.dnd.PickupDragController;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderInformation.FloatSize;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;

/**
 * Client-side implementation of the portal layout. 
 * @author p4elkin
 */
public class VPortalLayout extends ComplexPanel implements Paintable, Container {

  public static final String PORTLET_POSITION_UPDATED = "COMPONENT_ADDED";
  
  public static final String COMPONENT_REMOVED = "COMPONENT_REMOVED";
  
  public static final String PAINTABLE_MAP_PARAM = "PAINTABLE";
  
  public static final String AREA_INDEX_MAP_PARAM = "AREA_INDEX";
  
  public static final String PORTLET_POSITION_MAP_PARAM = "PORTLET_POSITION";
  
  public static final String CLASSNAME = "v-portallayout";
  
  private final static PickupDragController cs_dragControl = new PickupDragController(RootPanel.get(), false);
  
  protected final Map<Widget, Portlet> widgetToPortletContainer = new HashMap<Widget, Portlet>();
  
  private final List<PortalArea> areas = new ArrayList<PortalArea>();
  
  private final Element root = Document.get().createDivElement();
  
  protected String paintableId;

  protected ApplicationConnection client;
  
  protected PortalDropController dropController;
    
  private boolean isRendering = false;
  
  public static PickupDragController getDragController()
  {
    return cs_dragControl;
  }
  
  public VPortalLayout() {
    super();
    root.setClassName(CLASSNAME);
    root.getStyle().setProperty("overflow", "hidden");
    setElement(root);
    dropController = new PortalDropController(this);
    cs_dragControl.registerDropController(dropController);
  }

  public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
    if (client.updateComponent(this, uidl, true)) {
      return;
    }
   
    isRendering = true;
    
    int cols = uidl.getIntAttribute("cols");
    
    updatePortalAreas(cols);
    this.client = client;
    paintableId = uidl.getId();
    
    int areaIdx = 0;
    for (final Iterator<Object> areaIt = uidl.getChildIterator(); areaIt.hasNext();)
    {
      final UIDL areaUidl = (UIDL)areaIt.next();
      if ("area".equals(areaUidl.getTag()))
      {
        final PortalArea area = areas.get(areaIdx);
        for (final Iterator<Object> it = areaUidl.getChildIterator(); it.hasNext();)
        {
          final UIDL childUIDL = (UIDL) it.next();
          final Paintable child = client.getPaintable(childUIDL);
          
          Widget widget = (Widget) child;
          Portlet portlet = findOrCreatePortlet(widget);      
          
          setPortletPositionInArea(portlet, areaIdx);
          
          /// Do I have to check for caching!?!?!?
          FloatSize floatSize = Util.parseRelativeSize(childUIDL);
          
          portlet.setHeightRelative(floatSize != null);
          
          //if (!portlet.isHeightRelative())
         // {
            portlet.renderContent(childUIDL, client);
            portlet.updateSize(area.getOffsetWidth(), widget.getOffsetHeight());
          //}
        }
        ++areaIdx;
      }
    }
    
    for (final Iterator<Portlet> it = widgetToPortletContainer.values().iterator(); it.hasNext();) 
    {
      final Portlet portlet = it.next();
    }
  }

  private Portlet findOrCreatePortlet(Widget widget) {
    Portlet result = widgetToPortletContainer.get(widget);
    if (result == null)
      result = createPortlet(widget);
    return result;
  }

  private final Portlet createPortlet(Widget widget) {
    final Portlet result = new Portlet(widget);
    cs_dragControl.makeDraggable(result, result.getHeader());
    widgetToPortletContainer.put(widget, result);
    return result;
  }

  private void setPortletPositionInArea(Portlet portlet, int i) {
    
    if (i >= areas.size())
      throw new IllegalArgumentException("Invalid Area Index!");
    
    final PortalArea currentArea = portlet.getParentArea();
    final PortalArea newArea = areas.get(i);
    
    if (currentArea == null ||
        !currentArea.equals(newArea))
    {
      portlet.removeFromParent();
      portlet.setParentArea(newArea);
      newArea.addPortlet(portlet);
    }
  }

  /**
   * Append areas if needed. Recalculate sizes.
   * @param cols Current number of the portal areas.
   */
  private void updatePortalAreas(int cols) {
    
    while (areas.size() < cols)
    {
        final PortalArea column = new PortalArea(this);
        appendToRootElement(column);
        areas.add(column);
    }
    
    recalculatePortalAreaSizes();
  }
  
  /// Recalculate the size of the portal areas
  private void recalculatePortalAreaSizes()
  {
    if (areas.size() < 1)
      return;
      
    int totalWidth = ((DivElement)root).getClientWidth();
    int totalHeight = ((DivElement)root).getClientHeight();
        
    double sharedWidth = totalWidth / areas.size() - 3;
    
    for (final PortalArea e : areas)
    {
      e.setSize((sharedWidth - 2) + "px", (totalHeight - 2) + "px");
    }

  }
  
  /**
   * Add widget to the root panel
   * @param widget The widget to be added.
   */
  void appendToRootElement(final Widget widget)
  {
    getChildren().add(widget);
    root.appendChild(widget.getElement());   
    adopt(widget);
  }
  
  /**
   * Mostly for DnD. Given mouse coordinates -find the corresponding area. 
   * @param mouseX Mouse x position.
   * @param mouseY Mouse y position.
   * @return Found PortalArea. NULL if nothing was found.
   */
  public PortalArea getColumnByMousePosition(int mouseX, int mouseY) {
    if (mouseX < getAbsoluteLeft() ||
        mouseY < getAbsoluteTop() ||
        mouseX > getAbsoluteLeft() + getOffsetWidth() ||
        mouseY > getAbsoluteTop() + getOffsetHeight()
        )
      return null;
    
    int mouseXOffset = mouseX - getAbsoluteLeft();
    int columnIdx = (int)Math.floor(Double.valueOf(mouseXOffset * areas.size()) / ((double)getOffsetWidth()));
    
    assert columnIdx >= 0 &&
           columnIdx < areas.size();
      
    return areas.get(columnIdx);
  }

  /**
   * The portlet might have been moved from one portal to another. 
   * Do everything about detaching here.
   * @param portlet The portlet that has been removed.
   */
  public void handlePortletRemoved(Portlet portlet) {
    final Paintable child = portlet.getContentAsPaintable();
    if (child == null)
      return;
    portlet.setParentArea(null);
    widgetToPortletContainer.remove(portlet.getContent());
    client.updateVariable(paintableId, COMPONENT_REMOVED, child, true);
  }

  /**
   * The portlet might have been moved from one portal to another or 
   * just drag to some other place inside the portal.  
   * Do everything about attaching here.
   * @param portlet The portlet that either was added to the portal or changed its position.
   * @param newArea New Panel that will keep the protlet.
   * @param newPosition New position of the portlet.
   */
  public void handlePortletPositionUpdated(Portlet portlet, int newPosition, final PortalArea newArea) {
    final Paintable child = portlet.getContentAsPaintable();
    if (child == null)
      return;
    portlet.setParentArea(newArea);
    widgetToPortletContainer.put(portlet.getContent(), portlet);
    final Map<String, Object> params = new HashMap<String, Object>();
    params.put(PAINTABLE_MAP_PARAM, child);
    params.put(AREA_INDEX_MAP_PARAM, areas.indexOf(newArea));
    params.put(PORTLET_POSITION_MAP_PARAM, newPosition);
    client.updateVariable(paintableId, PORTLET_POSITION_UPDATED, params, true);
  }
  
  @Override
  public void setWidth(String width)
  {
    super.setWidth(width);
    recalculatePortalAreaSizes();
    for (Iterator<Portlet> it = widgetToPortletContainer.values().iterator(); it.hasNext();)
    {
      Portlet p = it.next();
      p.updateSize(areas.get(0).getOffsetWidth(), p.getContent().getOffsetHeight());
    } 
  }
  
  @Override
  public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
    final Portlet portlet = widgetToPortletContainer.remove(oldComponent);
    if (portlet == null) {
      return;
    }
    portlet.setContent(newComponent);
    client.unregisterPaintable((Paintable) oldComponent);
    widgetToPortletContainer.put(newComponent, portlet);
  }

  @Override
  public boolean hasChildComponent(Widget component) {
    return widgetToPortletContainer.containsKey(component);
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
    Portlet c = widgetToPortletContainer.get(child);
    if (c == null)
      return null;
    return new RenderSpace(/*c.getContentWidth()*/500, 600/*c.getContentHeight()*/);
  }
}
