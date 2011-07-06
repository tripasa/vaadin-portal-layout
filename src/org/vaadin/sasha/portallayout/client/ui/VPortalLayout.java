package org.vaadin.sasha.portallayout.client.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vaadin.sasha.portallayout.client.PortalDropController;
import org.vaadin.sasha.portallayout.client.dnd.PickupDragController;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderInformation.Size;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;

/**
 * Client-side implementation of the portal layout.
 * 
 * @author p4elkin
 */
public class VPortalLayout extends FlowPanel implements Paintable, Container {

  public static final String PORTLET_POSITION_UPDATED = "COMPONENT_ADDED";

  public static final String COMPONENT_REMOVED = "COMPONENT_REMOVED";

  public static final String PAINTABLE_MAP_PARAM = "PAINTABLE";

  public static final String PORTLET_POSITION_MAP_PARAM = "PORTLET_POSITION";

  public static final String PORTLET_COLLAPSED = "PORTLET_COLLAPSED";
  
  public static final String PORTLET_COLLAPSE_STATE_CHANGED = "PORTLET_COLLAPSE_STATE_CHANGE";
  
  /**
   * Basic style name.
   */
  public static final String CLASSNAME = "v-portallayout";

  /**
   * The common drag controller for all the portals in the application. Having
   * this drag controller static alloes us to drag portlets between all the
   * possible portals.
   */
  private final static PickupDragController cs_dragControl = new PickupDragController(
      RootPanel.get(), false);

  /**
   * The mapping between the portlets and their contents.
   */
  protected final Map<Widget, Portlet> widgetToPortletContainer = new HashMap<Widget, Portlet>();

  /**
   * The list of the portlets that should be rendered after the ones that have
   * their height specified.
   */
  private List<Portlet> relativeHeightPortlets = new LinkedList<Portlet>();

  /**
   * CellBasedLayout-like stub in the end of the panel for the easier addition
   * portlets into the end of the panel
   */
  private final DivElement stub = Document.get().createDivElement();

  /**
   * Id of the current paintable.
   */
  protected String paintableId;

  /**
   * Server side communication interface.
   */
  protected ApplicationConnection client;

  /**
   * Object controlling the way of how the portlets are being dropped on the
   * portal.
   */
  protected PortalDropController dropController;

  /**
   * The maximum number of portlets that this portla can hold.d
   */
  private int capacity;

  /**
   * This component is currently being painted.
   */
  private boolean isRendering = false;

  /**
   * Temporary variable that causes e.g. borders of the widgets to be rendered
   * if set.
   */
  private boolean debugMode = false;

  /**
   * 
   */
  private int width;

  /**
   * 
   */
  private int height;

  /**
   * Total height required for rendering fixed sized portlets and headers of
   * relative heighted portlets.
   */
  private int consumedHeightCache;

  /**
   * Get the Common PickupDragController that should wire all the portals
   * together.
   * 
   * @return PickupDragController.
   */
  public static PickupDragController getDragController() {
    return cs_dragControl;
  }

  /**
   * Constructor.
   */
  public VPortalLayout() {
    super();
    
    getElement().setClassName(CLASSNAME);
    getElement().getStyle().setProperty("overflow", "hidden");
    if (debugMode)
      getElement().getStyle().setProperty("border", "1px solid");
    dropController = new PortalDropController(this);
    getDragController().registerDropController(dropController);

    Style style = stub.getStyle();
    style.setProperty("width", "0px");
    style.setProperty("height", "0px");
    style.setProperty("clear", "both");
    style.setProperty("overflow", "hidden");
    getElement().appendChild(stub);

  }

  public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
    if (client.updateComponent(this, uidl, true)) {
      return;
    }

    isRendering = true;

    this.client = client;
    paintableId = uidl.getId();

    int pos = 0;
    relativeHeightPortlets.clear();
    for (final Iterator<Object> it = uidl.getChildIterator(); it.hasNext(); ++pos) {
      final UIDL childUIDL = (UIDL) it.next();
      final Paintable child = client.getPaintable(childUIDL);
      final Widget widget = (Widget) child;
      final Portlet portlet = findOrCreatePortlet(widget);

      updatePortletInPosition(portlet, pos);

      portlet.renderContent(childUIDL);

      if (portlet.tryDetectRelativeHeight(childUIDL))
        relativeHeightPortlets.add(portlet);

    }

    width = getElement().getClientWidth();
    height = getElement().getClientHeight();

    recalculateConsumedHeight();
    client.handleComponentRelativeSize(this);
  }

  public void recalculateConsumedHeight() {
    consumedHeightCache = 0;
    for (final Widget p : getChildren())
      consumedHeightCache += Util.getRequiredHeight(p);
    System.out.println("New height " + Math.max(height, consumedHeightCache) + " required " + Util.getRequiredHeight(getElement()));
    getElement().getStyle().setPropertyPx("height", Math.max(height, consumedHeightCache) + 200);
  }

  private Portlet findOrCreatePortlet(Widget widget) {
    Portlet result = widgetToPortletContainer.get(widget);
    if (result == null)
      result = createPortlet(widget);
    return result;
  }

  private final Portlet createPortlet(Widget widget) {
    final Portlet result = new Portlet(widget, client, this);
    cs_dragControl.makeDraggable(result, result.getDraggableArea());
    widgetToPortletContainer.put(widget, result);
    return result;
  }

  public void onPortalClose(final Portlet portlet) {
    handlePortletRemoved(portlet);
  }
  
  public void onPortalCollapseStateChanged(final Portlet portlet)
  {
    final Map<String, Object> params = new HashMap<String, Object>();
    
    params.put(PAINTABLE_MAP_PARAM, portlet.getContentAsPaintable());
    params.put(PORTLET_COLLAPSED, portlet.isCollapsed());
    
    client.updateVariable(paintableId, PORTLET_COLLAPSE_STATE_CHANGED, params, true);
  }
  
  private void updatePortletInPosition(Portlet portlet, int i) {
    portlet.removeFromParent();
    appendToRootElement(portlet, i);
  }

  /**
   * Add widget to the root panel
   * 
   * @param widget
   *          The widget to be added.
   */
  public void appendToRootElement(final Widget widget, int position) {
    super.insert(widget, position);
  }

  /**
   * The portlet might have been moved from one portal to another. Do everything
   * about detaching here.
   * 
   * @param portlet
   *          The portlet that has been removed.
   */
  public void handlePortletRemoved(Portlet portlet) {
    final Paintable child = portlet.getContentAsPaintable();
    if (child == null)
      return;
    widgetToPortletContainer.remove(portlet.getContent());
    client.updateVariable(paintableId, COMPONENT_REMOVED, child, false);
    client.sendPendingVariableChanges();
  }

  /**
   * The portlet might have been moved from one portal to another or just drag
   * to some other place inside the portal. Do everything about attaching here.
   * 
   * @param portlet
   *          The portlet that either was added to the portal or changed its
   *          position.
   * @param newPosition
   *          New position of the portlet.
   */
  public void handlePortletPositionUpdated(Portlet portlet, int newPosition) {
    final Paintable child = portlet.getContentAsPaintable();
    if (child == null)
      return;

    portlet.setParentPortal(this);
    
    final Map<String, Object> params = new HashMap<String, Object>();
    params.put(PAINTABLE_MAP_PARAM, child);
    params.put(PORTLET_POSITION_MAP_PARAM, newPosition);
    params.put(PORTLET_COLLAPSED, portlet.isCollapsed());
    
    client.updateVariable(paintableId, PORTLET_POSITION_UPDATED, params, true);
    client.runDescendentsLayout((HasWidgets) child);
    
    widgetToPortletContainer.put(portlet.getContent(), portlet);
    recalculateConsumedHeight();
  }

  /**
   * Get the number of the portlets currently contained in the protal.
   * 
   * @return The number of portlets.
   */
  public int getPortletCount() {
    return widgetToPortletContainer.size();
  }

  public int getConsumedHeightCache()
  {
    if (!isHeightCacheValid())
      recalculateConsumedHeight();
    return consumedHeightCache;
  }
  /**
   * Check if heightCahe is va
   */
  public boolean isHeightCacheValid() {
    return consumedHeightCache > 0;
  }

  /**
   * Set the value of consumed height cache so everybody knows it's not valid.
   */
  public void invalidateConsumedHeigthCache() {
    consumedHeightCache = -1;
  }

  @Override
  public void setWidth(String width) {
    super.setWidth(width);
    for (Iterator<Portlet> it = widgetToPortletContainer.values().iterator(); it
        .hasNext();) {
      Portlet p = it.next();
      p.setSizes(getOffsetWidth(), p.getContent().getOffsetHeight());
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
    final Portlet portlet = widgetToPortletContainer.get(child);
    final Size sizeInfo = portlet.getSizeInfo();
    return new RenderSpace(sizeInfo.getWidth(), sizeInfo.getHeight());
  }

  public void setCapacity(int capacity) {
    this.capacity = capacity;
  }

  public int getCapacity() {
    return capacity;
  }

}
