package org.vaadin.sasha.portallayout.client.ui;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.vaadin.sasha.portallayout.client.dnd.PickupDragController;
import org.vaadin.sasha.portallayout.client.dnd.util.DOMUtil;
import org.vaadin.sasha.portallayout.client.ui.Portlet.PortletLockState;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.EventId;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderInformation.FloatSize;
import com.vaadin.terminal.gwt.client.RenderInformation.Size;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.StyleConstants;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.ui.LayoutClickEventHandler;
import com.vaadin.terminal.gwt.client.ui.VMarginInfo;
import com.vaadin.terminal.gwt.client.ui.layout.CellBasedLayout.Spacing;

/**
 * Client-side implementation of the portal layout.
 * 
 * @author p4elkin
 */
@SuppressWarnings("unused")
public class VPortalLayout extends SimplePanel implements Paintable, Container {

    /**
     * Parameter sent to server in case a potlet is added from other portal.
     */
    public static final String PORTLET_POSITION_UPDATED = "COMPONENT_ADDED";

    /**
     * Parameter sent to server in case a potlet is moved to other portal.
     */
    public static final String COMPONENT_REMOVED = "COMPONENT_REMOVED";

    /**
     * PID sent to server.
     */
    public static final String PAINTABLE_MAP_PARAM = "PAINTABLE";

    /**
     * Client-server parameter indicating that the portal is collapsed.
     */
    public static final String PORTLET_COLLAPSED = "PORTLET_COLLAPSED";

    /**
     * Client-server parameter indicating that the portal was
     * collapsed/expanded.
     */
    public static final String PORTLET_COLLAPSE_STATE_CHANGED = "PORTLET_COLLAPSE_STATE_CHANGE";

    /**
     * Parameter received from server, true if portlet is closable.
     */
    public static final String PORTLET_CLOSABLE = "PORTLET_CLOSABLE";

    /**
     * Parameter received from server, true if portlet is callapsible.
     */
    public static final String PORTLET_COLLAPSIBLE = "PORTLET_COLLAPSIBLE";

    /**
     * Parameter received from server, true if portlet is locked (cannot be
     * dragged).
     */
    public static final String PORTLET_LOCKED = "PORTLET_LOCKED";

    /**
     * Parameter received from server, portlet position.
     */
    public static final String PORTLET_POSITION = "PORTLET_POSITION";

    /**
     * 
     */
    public static final String PORTLET_ACTION_IDS = "PORTLET_ACTION_IDS";
    
    /**
     * 
     */
    public static final String PORTLET_ACTION_ICONS = "PORTLET_ACTION_ICONS";
    
    /**
     *  
     */
    public static final String PORTLET_ACTION_TRIGGERED = "PORTLET_ACTION_TRIGGERED";
    
    /**
     * 
     */
    public static final String PORTLET_ACTION_ID = "PORTLET_ACTION_ID";
    
    /**
     * Client-server parameter that sets this portals' ability to share
     * portlets.
     */
    public static final String PORTAL_COMMUNICATIVE = "PORTAL_COMMUNCATIVE";

    /**
     * Basic style name.
     */
    public static final String CLASSNAME = "v-portallayout";

    /**
     * Spacing style prefix.
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
     * Controller used in case this portal should not share its portlets.
     */
    private PickupDragController localDragController = null;

    /**
     * Flag indicating that portal can share portlets with other portals.
     */
    private boolean isCommunicative = true;

    /**
     * The mapping between the portlets and their contents.
     */
    protected final Map<Widget, Portlet> widgetToPortletContainer = new HashMap<Widget, Portlet>();

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
     * The sizes that
     */
    private Size sizeInfoFromUidl = null;

    /**
     * Total height required for rendering fixed sized portlets and headers of
     * relative height portlets.
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
    private final Element marginWrapper = DOM.createDiv();

    /**
     * Total sum of the height percents that portlets with relative height are
     * trying to consume.
     */
    private float sumRelativeHeight = 0f;

    /**
     * 
     */
    private FlowPanel portalContent = new FlowPanel();
    
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
            localDragController = new PickupDragController(RootPanel.get(),
                    false);
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
        getElement().appendChild(marginWrapper);
        setStyleName(CLASSNAME);
        setWidget(portalContent);
        
        getElement().getStyle().setProperty("overflow", "hidden");
        marginWrapper.getStyle().setProperty("overflow", "hidden");
        
        dropController = new PortalDropController(this);
        getDragController().registerDropController(dropController);
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
        updateMarginsFromUidl(uidl);
        clickEventHandler.handleEventHandlerRegistration(client);
        final FloatSize relaiveSize = Util.parseRelativeSize(uidl);
        if (relaiveSize == null || relaiveSize.getHeight() == -1)
            sizeInfoFromUidl = new Size(
                    parsePixel(uidl.getStringAttribute("width")) - getHorizontalMargins(),
                    parsePixel(uidl.getStringAttribute("height")) - getVerticalMargins());

        actualSizeInfo.setHeight(getElement().getClientHeight() - getVerticalMargins());
        actualSizeInfo.setWidth(getElement().getClientWidth() - getHorizontalMargins());
        
        int pos = 0;
        final Map<Portlet, UIDL> realtiveSizePortletUIDLS = new HashMap<Portlet, UIDL>();
        final Map<Widget, Portlet> oldMap = new HashMap<Widget, Portlet>(widgetToPortletContainer);
        for (final Iterator<Object> it = uidl.getChildIterator(); it.hasNext(); ++pos) {
            final UIDL itUidl = (UIDL) it.next();
            if (itUidl.getTag().equals("portlet")) {

                final Boolean isClosable = itUidl.getBooleanAttribute(PORTLET_CLOSABLE);
                final Boolean isCollapsible = itUidl.getBooleanAttribute(PORTLET_COLLAPSIBLE);
                final Boolean isLocked = itUidl.getBooleanAttribute(PORTLET_LOCKED);
                final Boolean isCollapsed = itUidl.getBooleanAttribute(PORTLET_COLLAPSED);

                final UIDL childUidl = (UIDL) itUidl.getChildUIDL(0);
                final Paintable child = client.getPaintable(childUidl);
                final Widget widget = (Widget) child;
                
                if (oldMap.containsKey(widget))
                    oldMap.remove(widget);
                
                final Portlet portlet = findOrCreatePortlet(widget);
                updatePortletInPosition(portlet, pos);

                setLock(portlet, isLocked);
                portlet.setClosable(isClosable);
                portlet.setCollapsible(isCollapsible);
                
                if (itUidl.hasAttribute(PORTLET_ACTION_IDS)) {
                    final String[] actions = itUidl.getStringArrayAttribute(PORTLET_ACTION_IDS);
                    final String[] icons = itUidl.getStringArrayAttribute(PORTLET_ACTION_ICONS);
                    assert icons.length == actions.length;
                    final Map<String, String> idToIconUrl = new LinkedHashMap<String, String>();
                    for (int i = 0; i < actions.length; ++i)
                        idToIconUrl.put(actions[i], client.translateVaadinUri(icons[i]));
                    portlet.updateActions(idToIconUrl);
                }
                
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
        
        for (final Widget w : oldMap.keySet()) {
            final Portlet p = oldMap.get(w);
            portalContent.remove(p);
            widgetToPortletContainer.remove(w);
            client.unregisterPaintable((Paintable)w);
        }
    }

    private int getClientWidth() {
        return marginWrapper.getOffsetWidth() - getHorizontalMargins();
    }

    private int getClientHeight() {
        return marginWrapper.getOffsetHeight() - getVerticalMargins();
    }
    
    private int getVerticalMargins() {
        return DOMUtil.getVerticalPadding(marginWrapper);
    }

    private int getHorizontalMargins() {
        return DOMUtil.getHorizontalPadding(marginWrapper);
    }

    private void updateMarginsFromUidl(UIDL uidl) {
        VMarginInfo marginInfo = new VMarginInfo(
                uidl.getIntAttribute("margins"));
        setStyleName(marginWrapper, CLASSNAME + "-" + StyleConstants.MARGIN_TOP, 
                marginInfo.hasTop());
        setStyleName(marginWrapper, CLASSNAME + "-" + StyleConstants.MARGIN_RIGHT,
                marginInfo.hasRight());
        setStyleName(marginWrapper, CLASSNAME + "-" + StyleConstants.MARGIN_BOTTOM,
                marginInfo.hasBottom());
        setStyleName(marginWrapper, CLASSNAME + "-" + StyleConstants.MARGIN_LEFT,
                marginInfo.hasLeft());
    }

    @Override
    protected Element getContainerElement() {
        return marginWrapper;
    }
    
    private Iterator<Widget> getPortalContentIterator() {
        return portalContent.iterator();
    }
    
    private void updateCommunicationAbility(final UIDL uidl) {
        Boolean canCommunicate = uidl.getBooleanAttribute(PORTAL_COMMUNICATIVE);
        final PickupDragController currentController = getDragController();
        if (canCommunicate != isCommunicative) {
            currentController.unregisterDropController(dropController);
            isCommunicative = canCommunicate;
            final PickupDragController newController = getDragController();
            newController.registerDropController(dropController);
            for (final Portlet portlet : widgetToPortletContainer.values())
                if (!portlet.isLocked()) {
                    currentController.makeNotDraggable(portlet);
                    newController.makeDraggable(portlet,
                            portlet.getDraggableArea());
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
     * Check if spacing was enabled/disabled on the server side and update
     * spacing info accordingly.
     * 
     * @param uidl
     *            Server payload.
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
        int newHeight = recalculateLayout();
        
        final Set<PortalObjectSizeHandler> objSet = getPortletSet();
        final PortalDropPositioner p = dropController.getDummy();
        if (p != null)
            objSet.add(p);
        calculatePortletSizes(objSet);
    }

    public Set<PortalObjectSizeHandler> getPortletSet() {
        final Collection<Portlet> portlets = widgetToPortletContainer.values();
        final Set<PortalObjectSizeHandler> objSet = new HashSet<PortalObjectSizeHandler>();
        for (final Portlet p : portlets) {
            objSet.add(p);
        }
        return objSet;
    }

    public void setContainerHeight(int newHeight) {
        int oldHeight = getClientHeight();
        setDOMHeight(newHeight + getVerticalMargins());
        if (newHeight != oldHeight) {
            Util.notifyParentOfSizeChange(this, false);
        }
    }
    
    public int recalculateLayout() {
        consumedHeight = 0;
        sumRelativeHeight = 0;

        int contentsSize = getChildCount(); 
        final Iterator<Widget> it = getPortalContentIterator();
        
        while (it.hasNext()) {
            final PortalObjectSizeHandler p = (PortalObjectSizeHandler)it.next();
            final Portlet corresponingPortlet = p.getPortalObjectReference();
            int currentPortletIndex = portalContent.getWidgetIndex(corresponingPortlet); 
            if (currentPortletIndex != -1 &&
                currentPortletIndex != portalContent.getWidgetIndex(p))
                continue;

            consumedHeight += p.getRequiredHeight();

            if (p.isHeightRelative())
                sumRelativeHeight += p.getRealtiveHeightValue();
        }
        consumedHeight += (contentsSize - 1) * activeSpacing.vSpacing;
        int newHeight = 0;
        if (sizeInfoFromUidl != null && consumedHeight < sizeInfoFromUidl.getHeight()) 
            newHeight = sizeInfoFromUidl.getHeight();
        else
            newHeight = Math.max(actualSizeInfo.getHeight(), consumedHeight);
        setContainerHeight(newHeight);
        return newHeight;
    }
    
    /**
     * Calculate and accordingly update the size info of the portlets. In case
     * of relative height portlets the contents need to be re-laid out.
     */
    public void calculatePortletSizes(final Set<PortalObjectSizeHandler> objSet) {
        int totalWidth = getClientWidth();
        final Iterator<PortalObjectSizeHandler> it = objSet.iterator();
        while (it.hasNext()) {
            final PortalObjectSizeHandler portalObject = (PortalObjectSizeHandler)it.next();

            int newWidth = totalWidth;
            int newHeight = portalObject.getRequiredHeight();

            if (portalObject.isHeightRelative()) {
                newHeight += getRealtiveHeightPortletPxValue(portalObject);
            }

            int position = getChildPosition(portalObject);
            portalObject.setSpacingValue(position == 0 ? 0 : activeSpacing.vSpacing);
            portalObject.setWidgetSizes(newWidth, newHeight);
        }
        if (client != null)
            client.runDescendentsLayout(this);
    }

    public int getResidualHeight() {
        return getClientHeight() - consumedHeight - getVerticalMargins();
    }
    
    public int getRealtiveHeightPortletPxValue(final PortalObjectSizeHandler portalObject) {
        int residualHeight = getResidualHeight();
        float relativeHeightRatio = normalizedRealtiveRatio();
        float newRealtiveHeight = relativeHeightRatio * portalObject.getRealtiveHeightValue();
        return (int) (residualHeight * newRealtiveHeight / 100);
    }
    
    private void setDOMHeight(int height) {
        getElement().getStyle().setPropertyPx("height", height);
        marginWrapper.getStyle().setPropertyPx("height", height);
        portalContent.getElement().getStyle().setPropertyPx("height", height);
    }

    public PortalObjectSizeHandler getChildAt(int i) {
        return (PortalObjectSizeHandler)portalContent.getWidget(i);
    }
    
    public int getChildCount() {
        return portalContent.getWidgetCount();
    }

    public int getChildPosition(final PortalObjectSizeHandler child) {
        return portalContent.getWidgetIndex(child);
    }
    
    public FlowPanel getContentPanel() {
        return portalContent;
    }
    
    
    /**
     * Calculated a ratio that would be used in the calculation of how much
     * height the relative sized portlet can consume.
     * 
     * @return 1 if the sum of the relative heights is less or equal to 100, 100
     *         / SUM if sum is more than 100.
     */
    private float normalizedRealtiveRatio() {
        float result = 0;
        if (sumRelativeHeight != 0f)
            result = (sumRelativeHeight <= 100) ? 1 : 100 / sumRelativeHeight;
        return result;
    }

    /**
     * Search for the portlet corresponding to the widget. If it does not exist
     * - create it.
     * 
     * @param widget
     *            Search criteria.
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
     *            Contents for the new portlet.
     * @return Created portlet.
     */
    private final Portlet createPortlet(Widget widget) {
        final Portlet result = new Portlet(widget, client, this);
        widgetToPortletContainer.put(widget, result);
        return result;
    }

    /**
     * If the portlet was closed from somewhere outside - this method should be
     * called, so portal would do all the required logic on client and server
     * sides.
     * 
     * @param portlet
     *            The portlet that has been closed.
     */
    public void onPortletClose(final Portlet portlet) {
        onPortletMovedOut(portlet);
        recalculateLayoutAndPortletSizes();
    }

    public void onActionTriggered(final Portlet portlet, String key) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put(PAINTABLE_MAP_PARAM, portlet.getContentAsPaintable());
        params.put(PORTLET_ACTION_ID, key);
        client.updateVariable(paintableId, PORTLET_ACTION_TRIGGERED, params, true);
    }
    
    /**
     * Handler for the portlet collapse state toggle.
     * 
     * @param portlet
     *            Target portlet.
     */
    public void onPortletCollapseStateChanged(final Portlet portlet) {
        final Map<String, Object> params = new HashMap<String, Object>();

        params.put(PAINTABLE_MAP_PARAM, portlet.getContentAsPaintable());
        params.put(PORTLET_COLLAPSED, portlet.isCollapsed());

        client.updateVariable(paintableId, PORTLET_COLLAPSE_STATE_CHANGED,
                params, true);

        recalculateLayoutAndPortletSizes();
    }

    private void updatePortletInPosition(Portlet portlet, int i) {
        int currentPosition = getChildPosition(portlet);
        if (i != currentPosition) {
            portlet.removeFromParent();
            addToRootElement(portlet, i);
        }
    }

    /**
     * Add widget to the root panel
     * 
     * @param widget
     *            The widget to be added.
     */
    public void addToRootElement(final PortalObjectSizeHandler widget,
            int position) {
        portalContent.insert(widget, position);
        if (isSpacingEnabled)
            widget.setSpacingValue(position == 0 ? 0 : activeSpacing.vSpacing);
    }

    /**
     * The portlet might have been moved from one portal to another. Do
     * everything about detaching here.
     * 
     * @param portlet
     *            The portlet that has been removed.
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
     * to some other place inside the portal. Do everything about attaching
     * here.
     * 
     * @param portlet
     *            The portlet that either was added to the portal or changed its
     *            position.
     * @param newPosition
     *            New position of the portlet.
     */
    public void onPortletPositionUpdated(Portlet portlet, int newPosition) {
        final Paintable child = portlet.getContentAsPaintable();
        if (child == null)
            return;

        portlet.setParentPortal(this);
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put(PAINTABLE_MAP_PARAM, child);
        params.put(PORTLET_POSITION, newPosition);
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

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        int widthPx = parsePixel(width);
        actualSizeInfo.setWidth(widthPx - getHorizontalMargins());
        int intWidth = actualSizeInfo.getWidth();
        Iterator<Widget> it = getPortalContentIterator();
        while (it.hasNext()) {
            PortalObjectSizeHandler p = (PortalObjectSizeHandler) it.next();
            p.setWidgetWidth(intWidth);
        }
        if (client != null)
            client.runDescendentsLayout(this);
    }

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        int heightPx = parsePixel(height);
        actualSizeInfo.setHeight(heightPx - getVerticalMargins());
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
        final Widget widget = (Widget)component;
        final Portlet portlet = widgetToPortletContainer.get(component);
        if (portlet != null) {
            portlet.updateCaption(uidl);
        }
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
         * Due to the logic of the portal layout realtive height portlets
         * consume the according amount of free space (if 50% portlet height is
         * specified - a half of free space goes to that portlet). Here we
         * ensure that the rendering routines get the correct information about
         * how much space can be consumed.
         */
        if (portlet.isHeightRelative())
            height = (int) (((float) height) * 100 / portlet.getRealtiveHeightValue());
        return new RenderSpace(actualSizeInfo.getWidth(), height);
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

    public int getVerticalSpacing() {
        return activeSpacing.vSpacing;
    }

    /**
     * Takes a String value e.g. "12px" and parses that to int 12
     * 
     * @param String
     *            value with "px" ending
     * @return int the value from the string before "px", converted to int
     */
    public static int parsePixel(String value) {
        if (value == null || 
            value.equals("")) {
            return 0;
        }
        Float ret = Float.parseFloat(value.length() > 2 ? value.substring(0, value.length() - 2) : value); 
        return (int) Math.ceil(ret);
    }

    private static DivElement measurement;

    private static DivElement helper;

    static {
        helper = Document.get().createDivElement();
        helper.setInnerHTML("<div style=\"position:absolute;top:0;left:0;height:0;visibility:hidden;overflow:hidden;\">"
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

    private static class CollapseAnimation extends Animation {

        @Override
        protected void onUpdate(double progress) {
            
        }
        
    }
}