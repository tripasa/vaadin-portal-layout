package org.vaadin.sasha.portallayout.client.ui;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderInformation.FloatSize;
import com.vaadin.terminal.gwt.client.RenderInformation.Size;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.ValueMap;

/**
 * The class representing the portlet in the portal. Basically has the header
 * with portlet controls and caption and the widget which plays the role of the
 * portlet contents.
 * 
 * @author p4elkin
 */
public class Portlet extends ComplexPanel implements PortalObjectSizeHandler {

    /**
     * Enumeration for the lock states of the Portlet.
     * 
     * @author p4elkin
     */
    public enum PortletLockState {
        PLS_NOT_SET, PLS_LOCKED, PLS_NOT_LOCKED;
    }

    /**
     * Style name used for the portlets.
     */
    private final static String CLASSNAME = "v-portlet";

    /**
     * Wrapper style name.
     */
    private static final String WRAPPER_CLASSNAME = "-wrapper";

    /**
     * Content DIV style name.
     */
    private static final String CONTENT_CLASSNAME = "-content";

    /**
     * Size information of the portlet wrapper element.
     */
    private Size containerSizeInfo = new Size(0, 0);

    /**
     * Size information of the portlet contents (how much space should be used
     * for the non-collapsed widget in the protlet).
     */
    private Size contentSizeInfo = new Size(0, 0);

    /**
     * Header object that both serves as an area for the draggable part of the
     * portlet and holds the controls of the portal and its caption.
     */
    private PortletHeader header;

    /**
     * Vaadin widget contained in the portlet.
     */
    private Widget content;

    /**
     * Wrapper around the contents.
     */
    private Element containerElement;

    /**
     * Element that holds contents.
     */
    private Element contentDiv;

    /**
     * The portal which currently holds this portlet.
     */
    private VPortalLayout parentPortal = null;

    /**
     * The flag that indicates that height of the portlet should be calculated
     * in the relative style.
     */
    private boolean isHeightRelative = false;

    /**
     * Relative size. Null, if portlet has fixed size. Anyway - only height
     * matters. Width is always 100% as portlet should fit width of the portal.
     */
    private FloatSize relativeSize;

    /**
     * Flag indicating that this portlet is collapsed (only header is visible).
     */
    private boolean isCollapsed = false;

    /**
     * Flag indicatibg if the protlet can be dragged somewhere.
     */
    private PortletLockState isLocked = PortletLockState.PLS_NOT_SET;

    /**
     * Server-side communication channel.
     */
    private ApplicationConnection client;

    /**
   * 
   */
    private int headerHeight = -1;

    /**
     * Constructor.
     * 
     * @param widget
     *            The contents of the portlets.
     * @param parent
     *            Parent layout.
     */
    public Portlet(Widget widget, final ApplicationConnection client,
            VPortalLayout parent) {
        super();

        this.client = client;

        parentPortal = parent;
        content = widget;

        containerElement = DOM.createDiv();

        header = new PortletHeader(this);
        header.getElement().getStyle().setFloat(Style.Float.LEFT);
        add(header, containerElement);

        contentDiv = DOM.createDiv();
        contentDiv.addClassName(CLASSNAME + CONTENT_CLASSNAME);
        contentDiv.getStyle().setFloat(Style.Float.LEFT);

        containerElement.appendChild(contentDiv);
        setElement(containerElement);
        setStyleName(CLASSNAME);
        containerElement.addClassName(CLASSNAME + WRAPPER_CLASSNAME);

        add(content, contentDiv);
    }

    /**
     * Paint the contents.
     * 
     * @param uidl
     * @param client
     */
    public void renderContent(UIDL uidl) {
        if (content == null || !(content instanceof Paintable))
            return;
        ((Paintable) content).updateFromUIDL(uidl, client);
    }

    /**
     * Set the new sizes of the contents and wrappers.
     * 
     * @param width
     *            The new width.
     * @param height
     *            The new height.
     */
    protected void setContainerElementSizes(int width, int height) {
        containerSizeInfo.setWidth(width);
        containerSizeInfo.setHeight(height);
        updatePortletDOMSize();
    }

    /**
   * 
   */
    public void updateContentSizeInfoFromDOM() {
        contentSizeInfo.setWidth(Util.getRequiredWidth(content));
        contentSizeInfo.setHeight(Util.getRequiredHeight(content));
    }

    /**
     * Set the wrapper element size.
     * 
     * @param width
     *            New width.
     * @param height
     *            New height.
     */
    protected void updatePortletDOMSize() {
        containerElement.getStyle().setPropertyPx("width",
                containerSizeInfo.getWidth());
        containerElement.getStyle().setPropertyPx("height",
                containerSizeInfo.getHeight());
    }

    /**
     * Convenience method needed sometimes for easier passing the contents to
     * the server side.
     * 
     * @return Paintable cast of the contents, null if contents do not implement
     *         Paintable (most likely they do, but everything may happen).
     */
    public Paintable getContentAsPaintable() {
        return (content == null || !(content instanceof Paintable)) ? null
                : (Paintable) content;
    }

    /**
     * Regular contents access method.
     * 
     * @return Widget contained in the portlet.
     */
    public Widget getContent() {
        return content;
    }

    /**
     * Set the contained widget
     * 
     * @param content
     *            New contents.
     */
    public void setContent(Widget content) {
        this.content = content;
        updateContentSizeInfoFromDOM();
    }

    /**
     * Access the header of the portlet.
     * 
     * @return The header widget.
     */
    public Widget getDraggableArea() {
        return header.getDraggableArea();
    }

    /**
     * Access the portal that holds this portlet.
     * 
     * @return Parent portal.
     */
    public VPortalLayout getParentPortal() {
        return parentPortal;
    }

    /**
     * Set the new parent of this portlet.
     * 
     * @param portal
     *            New parent portal.
     */
    public void setParentPortal(VPortalLayout portal) {
        this.parentPortal = portal;
    }

    /**
     * Get the index of this portlet in the parent portal.
     * 
     * @return Portlet index.
     */
    public int getPosition() {
        return parentPortal == null ? -1 : parentPortal.getChildPosition(this);
    }

    /**
     * Parse uidl and extract info about relative height of the portlet.
     * 
     * @param uidl
     *            UIDL message.
     * @return true if height is relative. false - otherwise.
     */
    public boolean tryDetectRelativeHeight(final UIDL uidl) {
        relativeSize = Util.parseRelativeSize(uidl);
        isHeightRelative = relativeSize != null && relativeSize.getHeight() > 0;
        return isHeightRelative;
    }

    /**
     * Check if portlet is collapsed.
     * 
     * @return true if portlet is collapsed and only its header is visible.
     */
    public boolean isCollapsed() {
        return isCollapsed;
    }

    /**
     * Collapse/expand portlet.
     * 
     * @param isCollapsed
     *            True - if portlet must be collapsed.
     */
    public void setCollapsed(boolean isCollapsed) {
        this.isCollapsed = isCollapsed;
    }

    /**
     * Set portlet draggable flag.
     * 
     * @param isLocked
     *            true if portlet cannot be dragged.
     */
    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked ? PortletLockState.PLS_LOCKED
                : PortletLockState.PLS_NOT_LOCKED;
    }

    /**
     * Check if the portlet is locked.
     * 
     * @return true if portlet is not draggable, false - otherwise.
     */
    public boolean isLocked() {
        return isLocked == PortletLockState.PLS_LOCKED;
    }

    /**
     * 
     * @return
     */
    public PortletLockState getLockState() {
        return isLocked;
    }

    /**
     * Close this portlet and notify parent about it. TODO - implement observer!
     */
    public void close() {
        removeFromParent();
        parentPortal.onPortletClose(this);
    }

    /**
     * Change collapse state - if collapsed then expand otherwise - collapse.
     * Update size info as well. TODO - implement observer!
     */
    public void toggleCollapseState() {
        setCollapsed(!isCollapsed);
        setContainerElementSizes(getOffsetWidth(), getRequiredHeight());
        updateCollapseStyle();
        parentPortal.onPortletCollapseStateChanged(this);
    }

    /**
     * Get information about the size of this portlet
     * (both contents and header).
     * @return Size information of the wrapping container element.
     */
    public Size getContainerSizeInfo() {
        return containerSizeInfo;
    }

    /**
     * Get size information about portlet contents.
     * @return Content size information.
     */
    public Size getContentSizeInfo() {
        return contentSizeInfo;
    }

    /**
     * Get the name of the CSS objects related to portlets.
     * 
     * @return Name of CSS class.
     */
    public static String getClassName() {
        return CLASSNAME;
    }

    public void updateCollapseStyle() {
        contentDiv.getStyle().setProperty("visibility",
                isCollapsed ? "hidden" : "visible");
        contentDiv.getStyle().setPropertyPx("height",
                isCollapsed ? 0 : contentSizeInfo.getHeight());
    }

    public int getSpacing() {
        return parentPortal.getVerticalSpacing();
    }

    public void setCaption(final String portletCaption) {
        header.setCaption(portletCaption);
    }

    public void setClosable(boolean closable) {
        header.setClosable(closable);
    }

    public void setCollapsible(Boolean isCollapsible) {
        header.setCollapsible(isCollapsible);
    }

    public boolean isClosable() {
        return header.isClosable();
    }

    public boolean isCollapsible() {
        return header.isCollapsible();
    }

    public String getCaption() {
        return header.getCaption();
    }

    /**
     * 
     * @return
     */
    @Override
    public int getRequiredHeight() {
        int result = getHeaderHeight();
        if (!isCollapsed && !isHeightRelative)
            result += contentSizeInfo.getHeight();
        return result;
    }

    /**
   * 
   */
    @Override
    public float getRealtiveHeightValue() {
        if (relativeSize != null && !isCollapsed)
            return relativeSize.getHeight();
        return 0f;
    }

    /**
     * Check if the contents height should be relatively sized.
     * 
     * @return true if the height of the contents is relative.
     */
    @Override
    public boolean isHeightRelative() {
        return isHeightRelative;
    }

    @Override
    public void setWidgetSizes(int width, int height) {
        contentSizeInfo.setWidth(width);
        /**
         * Only relative height portlet contents follow the size of their
         * wrapper. The fixed sized portlet knows it's height from DOM.
         */
        if (isHeightRelative)
            contentSizeInfo.setHeight(height - header.getOffsetHeight());
        setContainerElementSizes(width, height);
    }

    @Override
    public void setWidgetHeight(int height) {
        if (isHeightRelative)
            contentSizeInfo.setHeight(height - header.getOffsetHeight());
        containerSizeInfo.setHeight(height);
        updatePortletDOMSize();
    }

    /**
     * 
     */
    @Override
    public void setWidgetWidth(int width) {
        contentSizeInfo.setWidth(width);
        containerSizeInfo.setWidth(width);
        updatePortletDOMSize();
    }

    @Override
    public void setSpacingValue(int spacing) {
        containerElement.getStyle().setPropertyPx("paddingTop", spacing);
    }

    @Override
    public Portlet getPortalObjectReference() {
        return this;
    }

    private int getHeaderHeight() {
        if (headerHeight == -1)
            headerHeight = header.getOffsetHeight();
        return headerHeight;
    }

    public void updateActions(final ValueMap actions) {
        header.updateActions(actions);
    }

    public void onActionTriggered(final String actionId) {
        parentPortal.onActionTriggered(this, actionId);
    }
}
