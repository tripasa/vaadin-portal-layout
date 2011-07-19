package org.vaadin.sasha.portallayout.client.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.vaadin.sasha.portallayout.client.dnd.PickupDragController;
import org.vaadin.sasha.portallayout.client.ui.Portlet.PortletLockState;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.EventId;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderInformation.FloatSize;
import com.vaadin.terminal.gwt.client.RenderInformation.Size;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.ui.LayoutClickEventHandler;
import com.vaadin.terminal.gwt.client.ui.VMarginInfo;
import com.vaadin.terminal.gwt.client.ui.layout.CellBasedLayout.Spacing;
import com.vaadin.terminal.gwt.client.ui.layout.Margins;

/**
 * Client-side implementation of the portal layout.
 * 
 * @author p4elkin
 */
public class VPortalLayout extends FlowPanel implements Paintable, Container {

  /**
   * 
   */
  public static final String PORTLET_POSITION_UPDATED = "COMPONENT_ADDED";

  /**
   * 
   */
  public static final String COMPONENT_REMOVED = "COMPONENT_REMOVED";

  /**
   * 
   */
  public static final String PAINTABLE_MAP_PARAM = "PAINTABLE";

  /**
   * 
   */
  public static final String PORTLET_COLLAPSED = "PORTLET_COLLAPSED";

  /**
   * 
   */
  public static final String PORTLET_COLLAPSE_STATE_CHANGED = "PORTLET_COLLAPSE_STATE_CHANGE";

  /**
   * 
   */
  public static final String PORTLET_CAPTION = "PORTLET_CAPTION";

  /**
   * 
   */
  public static final String PORTLET_CLOSABLE = "PORTLET_CLOSABLE";

  /**
   * 
   */
  public static final String PORTLET_COLLAPSIBLE = "PORTLET_COLLAPSIBLE";

  /**
   * 
   */
  public static final String PORTLET_LOCKED = "PORTLET_LOCKED";

  /**
   * 
   */
  public static final String PORTLET_POSITION = "PORTLET_POSITION";

  /**
   * 
   */
  public static final String PORTAL_COMMUNICATIVE = "PORTAL_COMMUNCATIVE";
  
  /**
   * Basic style name.
   */
  public static final String CLASSNAME = "v-portallayout";

  /**
   * 
   */
  public static final String STYLENAME_SPACING = CLASSNAME + "-spacing";

  /**
   * The common drag controller for all the portals in the application. Having
   * this drag controller static allows us to drag portlets between all the
   * possible portals.
   */
  private final static PickupDragController commonDragController = new PickupDragController(
      RootPanel.get(), false);

  /**
   * 
   */
  private PickupDragController localDragController = null;
  
  /**
   * 
   */
  private boolean isCommunicative = true;
  
  /**
   * The mapping between the portlets and their contents.
   */
  protected final Map<Widget, Portlet> widgetToPortletContainer = new HashMap<Widget, Portlet>();

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
   * Portal size info.
   */
  private Size actualSizeInfo = new Size(0, 0);

  /**
   * 
   */
  private Size sizeInfoFromUidl = null;
  /**
   * Total height required for rendering fixed sized portlets and headers of
   * relative heighted portlets.
   */
  private int consumedHeight;

  /**
   * Flag indicating that spacing must be enabled.
   */
  private boolean isSpacingEnabled = false;

  /**
   * Info about portlet spacing.
   */
  protected final Spacing computedSpacing = new Spacing(0, 0);

  /**
   * Info about portlet spacing.
   */
  protected final Spacing activeSpacing = new Spacing(0, 0);

  /**
   * 
   */
  private final Element contentElement = DOM.createDiv();
  /**
   * 
   */
  private float sumRelativeHeight = 0f;

  private VMarginInfo marginInfo = null;
  
  private Margins margins = new Margins(0,0,0,0);
  
  /**
   * Get the Common PickupDragController that should wire all the portals
   * together.
   * 
   * @return PickupDragController.
   */
  public PickupDragController getDragController() {
    if (isCommunicative)
      return commonDragController;
    if (localDragController == null)
      localDragController = new PickupDragController(RootPanel.get(), false);
    return localDragController;
  }

  private LayoutClickEventHandler clickEventHandler = new LayoutClickEventHandler(
      this, EventId.LAYOUT_CLICK) {

    @Override
    protected Paintable getChildComponent(Element element) {
      return getComponent(element);
    }

    @Override
    protected <H extends EventHandler> HandlerRegistration registerHandler(
        H handler, Type<H> type) {
      return addDomHandler(handler, type);
    }
  };

  /**
   * Constructor.
   */
  public VPortalLayout() {
    super();

    setStyleName(CLASSNAME);
    getElement().getStyle().setProperty("overflow", "hidden");
    
    //contentElement.getStyle().setProperty("overflow", "hidden");
    //getElement().appendChild(contentElement);
    
    dropController = new PortalDropController(this);
    getDragController().registerDropController(dropController);

    Style style = stub.getStyle();
    style.setProperty("width", "0px");
    style.setProperty("height", "0px");
    style.setProperty("clear", "both");
    style.setProperty("overflow", "hidden");
    //contentElement.appendChild(stub);

  }

  public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
    if (client.updateComponent(this, uidl, true)) {
      return;
    }
    
    isRendering = true;
    sizeInfoFromUidl = null;
    this.client = client;
    paintableId = uidl.getId();
    updateSpacingInfoFromUidl(uidl);
    clickEventHandler.handleEventHandlerRegistration(client);
   // System.out.println("Update from UIDL " + getElement().getClientHeight() + " " + parsePixel(uidl.getStringAttribute("height")));
    
    final FloatSize relaiveSize = Util.parseRelativeSize(uidl);
    if (relaiveSize == null ||
        relaiveSize.getHeight() == -1)
      sizeInfoFromUidl = new Size(parsePixel(uidl.getStringAttribute("width")), parsePixel(uidl.getStringAttribute("height")));
    
    actualSizeInfo.setHeight(getElement().getClientHeight());
    actualSizeInfo.setWidth(getElement().getClientWidth());
    int pos = 0;
    final Map<Portlet, UIDL> realtiveSizePortletUIDLS = new HashMap<Portlet, UIDL>();
    for (final Iterator<Object> it = uidl.getChildIterator(); it.hasNext(); ++pos) {
      final UIDL itUidl = (UIDL) it.next();
      if (itUidl.getTag().equals("portlet")) {
        final String portletCaption = itUidl
            .getStringAttribute(PORTLET_CAPTION);

        final Boolean isClosable = itUidl.getBooleanAttribute(PORTLET_CLOSABLE);
        final Boolean isCollapsible = itUidl
            .getBooleanAttribute(PORTLET_COLLAPSIBLE);
        final Boolean isLocked = itUidl.getBooleanAttribute(PORTLET_LOCKED);
        final Boolean isCollapsed = itUidl
            .getBooleanAttribute(PORTLET_COLLAPSED);

        final UIDL childUidl = (UIDL) itUidl.getChildUIDL(0);
        final Paintable child = client.getPaintable(childUidl);
        final Widget widget = (Widget) child;
        final Portlet portlet = findOrCreatePortlet(widget);

        updatePortletInPosition(portlet, pos);

        setLock(portlet, isLocked);
        portlet.setCaption(portletCaption);
        portlet.setClosable(isClosable);
        portlet.setCollapsible(isCollapsible);

        if (!isCollapsed.equals(portlet.isCollapsed()))
          portlet.toggleCollapseState();

        if (!Util.isCached(childUidl))
          portlet.tryDetectRelativeHeight(childUidl);

        if (portlet.isHeightRelative())
          realtiveSizePortletUIDLS.put(portlet, childUidl);
        else {
          portlet.renderContent(childUidl);
          portlet.updateContentSizeInfoFromDOM();
        }

      }
    }

    recalculateLayoutAndPortletSizes();

    for (final Portlet p : realtiveSizePortletUIDLS.keySet()) {
      final UIDL relUidl = realtiveSizePortletUIDLS.get(p);
      p.renderContent(relUidl);
    }
    
    updateCommunicationAbility(uidl);
  }

  private void updateCommunicationAbility(final UIDL uidl) {
    Boolean canCommunicate = uidl.getBooleanAttribute(PORTAL_COMMUNICATIVE);
    final PickupDragController currentController = getDragController();
    if (canCommunicate != isCommunicative)
    {
      currentController.unregisterDropController(dropController);
      isCommunicative = canCommunicate;
      final PickupDragController  newController = getDragController();
      newController.registerDropController(dropController);
      for (final Portlet portlet : widgetToPortletContainer.values())
        if (!portlet.isLocked())
        {
          currentController.makeNotDraggable(portlet);
          newController.makeDraggable(portlet, portlet.getDraggableArea());
        }
    }
  }

  private void setLock(final Portlet portlet, boolean isLocked) {

    PortletLockState formerLockState = portlet.getLockState();
    portlet.setLocked(isLocked);

    if (!isLocked && formerLockState != PortletLockState.PLS_NOT_LOCKED)
      getDragController().makeDraggable(portlet,
          portlet.getDraggableArea());
    else if (isLocked && formerLockState == PortletLockState.PLS_NOT_LOCKED)
      getDragController().makeNotDraggable(portlet);
  }

  /**
   * Check if spacing was enabled/disabled on the server side and update spacing
   * info accordingly.
   * 
   * @param uidl
   *          Server payload.
   */
  private void updateSpacingInfoFromUidl(final UIDL uidl) {
    boolean newSpacingEnabledState = uidl.getBooleanAttribute("spacing");
    if (isSpacingEnabled != newSpacingEnabledState) {
      isSpacingEnabled = newSpacingEnabledState;
      activeSpacing.vSpacing = isSpacingEnabled ? computedSpacing.vSpacing : 0;
    }
  }

  /**
   * Calculate height consumed by the fixed sized portlets and distribute the
   * remaining height between the relative sized portlets. When the relative
   * height comes to consideration - if the total sum of percentages overflows
   * 100, that value is normalized, so every relative height portlet would get
   * its piece of space.
   */
  public void recalculateLayoutAndPortletSizes() {
    consumedHeight = 0;
    sumRelativeHeight = 0;

    for (final Widget p : getChildren()) {
      final PortalObjectSizeHandler sizeHandlerCast = (PortalObjectSizeHandler) p;
      final Portlet corresponingPortlet = sizeHandlerCast
          .getPortalObjectReference();

      if (getChildren().contains(corresponingPortlet)
         && getWidgetIndex(corresponingPortlet) != getWidgetIndex((Widget) sizeHandlerCast))
     continue;

    consumedHeight += sizeHandlerCast.getRequiredHeight();

      if (sizeHandlerCast.isHeightRelative())
        sumRelativeHeight += sizeHandlerCast.getRealtiveHeightValue();
    }

    //System.out.println("Time elapsed fixed height: " + (System.currentTimeMillis() - time));
    // / TODO set proper calcs after the padding problem fixed
    if (getChildren().size() > 1)
      consumedHeight += (getChildren().size() - 1)
          * activeSpacing.vSpacing;

    //System.out.println("Size info " + actualSizeInfo.getHeight() + " consumed " + consumedHeight + " offset " + getOffsetHeight());
    int newHeight = 0;
    if (sizeInfoFromUidl != null &&
        consumedHeight < sizeInfoFromUidl.getHeight())
      newHeight = sizeInfoFromUidl.getHeight();
    else
      newHeight = Math.max(actualSizeInfo.getHeight(), consumedHeight);


    if (newHeight != getOffsetHeight()) {
      getElement().getStyle().setPropertyPx("height", newHeight);
      Scheduler.get().scheduleDeferred(new ScheduledCommand() {
        @Override
        public void execute() {
          //System.out.println("Size change!");
          Util.notifyParentOfSizeChange(VPortalLayout.this, false);
          
        }
      });
    }

    calculatePortletSizes();
    //System.out.println("Time elapsed total: " + (System.currentTimeMillis() - time));
  }

  /**
   * Calculate and accordingly update the size info of the portlets. In case of
   * relative height portlets the contents need to be re-laid out.
   */
  private void calculatePortletSizes() {
    int totalHeight = getOffsetHeight();
    int totalWidth = getOffsetWidth();

    int residualHeight = totalHeight - consumedHeight;

    float relativeHeightRatio = normalizedRealtiveRatio();

    for (final Widget p : getChildren()) {
      if (!(p instanceof PortalObjectSizeHandler))
        continue;

      final PortalObjectSizeHandler sizeHandler = (PortalObjectSizeHandler) p;

      int newWidth = totalWidth;
      int newHeight = sizeHandler.getRequiredHeight();

      if (sizeHandler.isHeightRelative()) {
        float newRealtiveHeight = relativeHeightRatio
            * sizeHandler.getRealtiveHeightValue();
        newHeight += (int) (residualHeight * newRealtiveHeight / 100);
      }

      int position = getWidgetIndex((Widget) sizeHandler);
      sizeHandler.setSpacingValue(position == 0 ? 0 : activeSpacing.vSpacing);
      sizeHandler.setWidgetSizes(newWidth, newHeight);
    }
    if (client != null)
      client.runDescendentsLayout(this);
  }

  /**
   * Calculated a ratio that would be used in the calculation of how much height
   * the relative sized portlet can consume.
   * 
   * @return 1 if the sum of the relative heights is less or equal to 100, 100 /
   *         SUM if sum is more than 100.
   */
  private float normalizedRealtiveRatio() {
    float result = 0;
    if (sumRelativeHeight != 0f)
      result = (sumRelativeHeight <= 100) ? 1 : 100 / sumRelativeHeight;
    return result;
  }

  /**
   * Search for the portlet corresponding to the widget. If it does not exist -
   * create it.
   * 
   * @param widget
   *          Search criteria.
   * @return Found or created portlet.
   */
  private Portlet findOrCreatePortlet(Widget widget) {
    Portlet result = widgetToPortletContainer.get(widget);
    if (result == null)
      result = createPortlet(widget);
    return result;
  }

  /**
   * Create new portlet, make it draggable and save it in the map.
   * 
   * @param widget
   *          Contents for the new portlet.
   * @return Created portlet.
   */
  private final Portlet createPortlet(Widget widget) {
    final Portlet result = new Portlet(widget, client, this);
    widgetToPortletContainer.put(widget, result);
    return result;
  }

  /**
   * If the portlet was closed somewhere outsuide - this method should be
   * called, so portal would do all the required logic on client and server
   * sides.
   * 
   * @param portlet
   *          The portlet that has been closed.
   */
  public void onPortletClose(final Portlet portlet) {
    onPortletMovedOut(portlet);
    recalculateLayoutAndPortletSizes();
  }

  /**
   * Handler for the portlet collapse state toggle.
   * 
   * @param portlet
   *          Target portlet.
   */
  public void onPortletCollapseStateChanged(final Portlet portlet) {
    final Map<String, Object> params = new HashMap<String, Object>();

    params.put(PAINTABLE_MAP_PARAM, portlet.getContentAsPaintable());
    params.put(PORTLET_COLLAPSED, portlet.isCollapsed());

    client.updateVariable(paintableId, PORTLET_COLLAPSE_STATE_CHANGED, params,
        true);

    recalculateLayoutAndPortletSizes();
  }

  private void updatePortletInPosition(Portlet portlet, int i) {
    int currentPosition = getWidgetIndex(portlet);
    if (i != currentPosition) {
      portlet.removeFromParent();
      addToRootElement(portlet, i);
    }
  }

  /**
   * Add widget to the root panel
   * 
   * @param widget
   *          The widget to be added.
   */
  public void addToRootElement(final Widget widget, int position) {
    super.insert(widget, position);
    if (widget instanceof PortalObjectSizeHandler)
      ((PortalObjectSizeHandler)widget).setSpacingValue(position == 0 ? 0 : activeSpacing.vSpacing);
  }

  /**
   * The portlet might have been moved from one portal to another. Do everything
   * about detaching here.
   * 
   * @param portlet
   *          The portlet that has been removed.
   */
  public void onPortletMovedOut(Portlet portlet) {
    final Paintable child = portlet.getContentAsPaintable();
    if (child == null)
      return;
    widgetToPortletContainer.remove(portlet.getContent());
    client.updateVariable(paintableId, COMPONENT_REMOVED, child, true);
    recalculateLayoutAndPortletSizes();
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
  public void onPortletPositionUpdated(Portlet portlet, int newPosition) {
    final Paintable child = portlet.getContentAsPaintable();
    if (child == null)
      return;

    portlet.setParentPortal(this);

    final Map<String, Object> params = new HashMap<String, Object>();
    params.put(PAINTABLE_MAP_PARAM, child);
    params.put(PORTLET_POSITION, newPosition);
    params.put(PORTLET_COLLAPSED, portlet.isCollapsed());
    params.put(PORTLET_CLOSABLE, portlet.isClosable());
    params.put(PORTLET_COLLAPSIBLE, portlet.isCollapsible());
    if (!portlet.getCaption().isEmpty())
      params.put(PORTLET_CAPTION, portlet.getCaption());
    client.updateVariable(paintableId, PORTLET_POSITION_UPDATED, params, true);
    widgetToPortletContainer.put(portlet.getContent(), portlet);
  }

  /**
   * Get the number of the portlets currently contained in the protal.
   * 
   * @return The number of portlets.
   */
  public int getPortletCount() {
    return widgetToPortletContainer.size();
  }

  /**
   * Check if heightCahe is valid.
   */
  public boolean isHeightCacheValid() {
    return consumedHeight > 0;
  }

  /**
   * Set the value of consumed height cache so everybody knows it's not valid.
   */
  public void invalidateConsumedHeigthCache() {
    consumedHeight = -1;
  }

  @Override
  public void setWidth(String width) {
    super.setWidth(width);
    int widthPx = parsePixel(width);
    actualSizeInfo.setWidth(widthPx);
    int intWidth = getOffsetWidth();
    for (Iterator<Portlet> it = widgetToPortletContainer.values().iterator(); it
        .hasNext();) {
      Portlet p = (Portlet) it.next();
      p.setWidgetWidth(intWidth);
      // Util.setWidthExcludingPaddingAndBorder(p, "100%", 0);
      // if (client != null)
      // client.handleComponentRelativeSize(p);
    }
    if (client != null)
      client.runDescendentsLayout(this);
  }

  @Override
  public void setHeight(String height) {
    super.setHeight(height);
    actualSizeInfo.setHeight(parsePixel(height));
    recalculateLayoutAndPortletSizes();
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
    // / Captions not supported.
  }

  @Override
  public boolean requestLayout(Set<Paintable> children) {
    recalculateLayoutAndPortletSizes();
    return false;
  }

  @Override
  public RenderSpace getAllocatedSpace(Widget child) {

    final Portlet portlet = widgetToPortletContainer.get(child);
    final Size sizeInfo = portlet.getContentSizeInfo();

    int height = sizeInfo.getHeight();
    /**
     * Due to the logic of the portal layout realtive height portlets consume
     * the according amount of free space (if 50% portlet height is specified -
     * a half of free space goes to that portlet). Here we ensure that the
     * rendering routines get the correct information about how much space can
     * be consumed.
     */
    if (portlet.isHeightRelative())
      height = (int) (((float) height) * 100 / portlet.getRealtiveHeightValue());
    return new RenderSpace(sizeInfo.getWidth(), height);
  }

  @Override
  public void setStyleName(String style) {
    super.setStyleName(style);
    measureSpacing();
  }

  public void setCapacity(int capacity) {
    this.capacity = capacity;
  }

  public int getCapacity() {
    return capacity;
  }

  public Spacing getSpacingInfo() {
    return activeSpacing;
  }

  public int getVSacing()
  {
    return activeSpacing.vSpacing;
  }
  /**
   * Takes a String value e.g. "12px" and parses that to int 12
   * 
   * @param String
   *          value with "px" ending
   * @return int the value from the string before "px", converted to int
   */
  public static int parsePixel(String value) {
    if (value == "" || value == null) {
      return 0;
    }
    Float ret;
    if (value.length() > 2) {
      ret = Float.parseFloat(value.substring(0, value.length() - 2));
    } else {
      ret = Float.parseFloat(value);
    }
    return (int) Math.ceil(ret);
  }

  private static DivElement measurement;

  private static DivElement helper;

  static {
    helper = Document.get().createDivElement();
    helper
        .setInnerHTML("<div style=\"position:absolute;top:0;left:0;height:0;visibility:hidden;overflow:hidden;\">"
            + "<div style=\"width:0;height:0;visibility:hidden;overflow:hidden;\">"
            + "</div></div>"
            + "<div style=\"position:absolute;height:0;overflow:hidden;\"></div>");
    NodeList<Node> childNodes = helper.getChildNodes();
    measurement = (DivElement) childNodes.getItem(1);
  }

  protected boolean measureSpacing() {
    if (!isAttached()) {
      return false;
    }

    // Measure spacing (actually CSS padding)
    measurement.setClassName(STYLENAME_SPACING);
    getElement().appendChild(helper);

    computedSpacing.vSpacing = measurement.getOffsetWidth();
    computedSpacing.hSpacing = measurement.getOffsetWidth();
    getElement().removeChild(helper);
    return true;
  }
  
  private Paintable getComponent(Element element) {
    return Util.getPaintableForElement(client, this, element);
  }
  
  
  private void updateMargins() {
// TODO!
    //margin = CSSUtil.collectMargin(getElement());
//    if (marginInfo.hasTop()) {
//        getElement().getStyle().setProperty("marginTop", "");
//    } else {
//        getElement().getStyle().setProperty("marginTop", "0");
//        margin[0] = 0;
//    }
//    if (marginInfo.hasRight()) {
//        getElement().getStyle().setProperty("marginRight", "");
//    } else {
//        getElement().getStyle().setProperty("marginRight", "0");
//        margin[1] = 0;
//    }
//    if (marginInfo.hasBottom()) {
//        getElement().getStyle().setProperty("marginBottom", "");
//    } else {
//        getElement().getStyle().setProperty("marginBottom", "0");
//        margin[2] = 0;
//    }
//    if (marginInfo.hasLeft()) {
//        getElement().getStyle().setProperty("marginLeft", "");
//    } else {
//        getElement().getStyle().setProperty("marginLeft", "0");
//        margin[3] = 0;
//    }
  }
}