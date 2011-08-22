package org.vaadin.sasha.portallayout.client.ui;

import java.util.Map;
import java.util.Set;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderInformation.FloatSize;
import com.vaadin.terminal.gwt.client.RenderInformation.Size;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;

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
     * for the non-collapsed widget in the portlet).
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
     * 
     */
    private final ContentCollapseAnimation animation;
 
    /**
     * 
     */
    private final FadeAnimation fadeAnimation;

    /**
     * Relative size. Null, if portlet has fixed size. Anyway - only height
     * matters. Width is always 100% as portlet should fit width of the portal.
     */
    private FloatSize relativeSize;

    /**
     * Flag indicatibg if the protlet can be dragged somewhere.
     */
    private PortletLockState isLocked = PortletLockState.PLS_NOT_SET;

    /**
     * Server-side communication channel.
     */
    private ApplicationConnection client;
 
    /**
     * Flag indicating that this portlet is collapsed (only header is visible).
     */
    private boolean isCollapsed = false;
    
    /**
     * The flag that indicates that height of the portlet should be calculated
     * in the relative style.
     */
    private boolean isHeightRelative = false;
    
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
        this.animation = new ContentCollapseAnimation();
        this.fadeAnimation = new FadeAnimation();
        parentPortal = parent;
        content = widget;

        containerElement = DOM.createDiv();

        header = new PortletHeader(this, client);
        header.getElement().getStyle().setFloat(Style.Float.LEFT);
        add(header, containerElement);

        contentDiv = DOM.createDiv();
        contentDiv.addClassName(CLASSNAME + CONTENT_CLASSNAME);
        contentDiv.getStyle().setFloat(Style.Float.LEFT);
        contentDiv.getStyle().setOverflow(Overflow.HIDDEN);

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

    @Override
    public void setWidgetSizes(int width, int height) {
        setContainerElementSizes(width, height);
        if (isHeightRelative)
            setContentElementSizes(width, height - header.getOffsetHeight());
        
    }
    
    private void setContentElementSizes(int width, int height) {
        contentSizeInfo.setWidth(width);
        contentSizeInfo.setHeight(height);
        updateContentDOMSize();
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
        updateContainerDOMSize();
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
    protected void updateContainerDOMSize() {
        containerElement.getStyle().setPropertyPx("width", containerSizeInfo.getWidth());
        containerElement.getStyle().setPropertyPx("height", containerSizeInfo.getHeight());
    }
    
    protected void updateContentDOMSize() {
        contentDiv.getStyle().setPropertyPx("width", contentSizeInfo.getWidth());
        contentDiv.getStyle().setPropertyPx("height", contentSizeInfo.getHeight());
    }

    /**
     * Convenience method needed sometimes for easier passing the contents to
     * the server side.
     * 
     * @return Paintable cast of the contents, null if contents do not implement
     *         Paintable (most likely they do, but everything may happen).
     */
    public Paintable getContentAsPaintable() {
        return (content == null || !(content instanceof Paintable)) ? null : (Paintable) content;
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
     * Close this portlet and notify parent about it. 
     */
    public void close() {
        fadeAnimation.start(false,  parentPortal.shouldAnimate(AnimationType.AT_CLOSE) ? 
                parentPortal.getAnimationSpeed(AnimationType.AT_CLOSE) : 0);
    }

    /**
     * Change collapse state - if collapsed then expand otherwise - collapse.
     * Update size info as well. 
     */
    public void toggleCollapseState() {
        animation.start(parentPortal.shouldAnimate(AnimationType.AT_COLLAPSE) ? 
                parentPortal.getAnimationSpeed(AnimationType.AT_COLLAPSE) : 0);
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

    public int getSpacing() {
        return parentPortal.getVerticalSpacing();
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

    /**
     * 
     * @return
     */
    @Override
    public int getRequiredHeight() {
        int result = header.getOffsetHeight();
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

    /**
     * 
     */
    @Override
    public void setWidgetWidth(int width) {
        contentSizeInfo.setWidth(width);
        containerSizeInfo.setWidth(width);
        updateContainerDOMSize();
    }

    @Override
    public void setSpacingValue(int spacing) {
        containerElement.getStyle().setPropertyPx("paddingTop", spacing);
    }

    @Override
    public Portlet getPortalObjectReference() {
        return this;
    }

    public void updateActions(final Map<String, String> actions) {
        header.updateActions(actions);
    }

    public void onActionTriggered(final String actionId) {
        parentPortal.onActionTriggered(this, actionId);
    }

    public void updateCaption(final UIDL uidl) {
        header.updateCaption(uidl);
    }

    public void blur() {
        content.getElement().blur();
    }
    

    private class ContentCollapseAnimation extends Animation {

        private int height;
        
        @Override
        protected void onStart() {
            super.onStart();
        }
        
        public void start(int speed) {
            cancel();
            double duration = 0;
            if (!isCollapsed && isHeightRelative) {
                height = parentPortal.getRealtiveHeightPortletPxValue(Portlet.this);
                setCollapsed(!isCollapsed);
            } else {
                setCollapsed(!isCollapsed);
                parentPortal.recalculateLayout();
                final Set<PortalObjectSizeHandler> portletSet = parentPortal.getPortletSet();
                if (isHeightRelative) {
                    parentPortal.calculatePortletSizes(portletSet);
                    height = parentPortal.getRealtiveHeightPortletPxValue(Portlet.this);
                } else {
                    portletSet.remove(Portlet.this);
                    parentPortal.calculatePortletSizes(portletSet);
                    height = content.getOffsetHeight();
                }
            }
            if (speed > 0) {
                duration = (double)height / (double)speed * 1000d;
            }
            run((int)duration);
        }
        
        @Override
        protected void onUpdate(double progress) {
            double heightValue = isCollapsed ?  (1 - progress) * height : progress * height;
            contentDiv.getStyle().setProperty("height",  heightValue + "px");
            setContainerElementSizes(getOffsetWidth(), header.getOffsetHeight() + (int)heightValue);
            Util.notifyParentOfSizeChange(parentPortal, true);
        }

        @Override
        protected void onComplete() {
            super.onComplete();
            header.toggleCollapseStyles(isCollapsed);
            parentPortal.onPortletCollapseStateChanged(Portlet.this);
        }

    }
    
    
    @Override
    protected void onAttach() {
        super.onAttach();
        if (parentPortal.shouldAnimate(AnimationType.AT_ATTACH))
            fadeAnimation.start(true, parentPortal.getAnimationSpeed(AnimationType.AT_ATTACH));
    }
    
    protected class FadeAnimation extends Animation {

        private boolean fadeIn;

        
        public void start(boolean isOpening, int speed) {
            this.fadeIn = isOpening;
            if (fadeIn) {
                getElement().getStyle().setOpacity(0);
            }
            run(speed);
        }

        @Override
        protected void onUpdate(double progress) {
            final String msOpacityPrpertyValue = "progid:DXImageTransform.Microsoft.Alpha(Opacity=";
            if (fadeIn) {
                if (BrowserInfo.get().isIE8()) {
                    getElement().getStyle().setProperty(
                            "filter",
                            msOpacityPrpertyValue + (int) (progress * 100) + ")");
                } else {
                    getElement().getStyle().setOpacity(progress);
                }
            } else {
                if (BrowserInfo.get().isIE8()) {
                    getElement().getStyle().setProperty(
                            "filter",
                            msOpacityPrpertyValue + (int) ((1 - progress) * 100) + ")");
                } else {
                    getElement().getStyle().setOpacity(1 - progress);
                }
            }
        }

        @Override
        public void onComplete() {
            super.onComplete();
            getElement().getStyle().clearOpacity();
            if (!fadeIn) {
                removeFromParent();
                parentPortal.onPortletClose(Portlet.this);
            }
        }
    }
}
