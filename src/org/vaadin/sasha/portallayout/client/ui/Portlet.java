package org.vaadin.sasha.portallayout.client.ui;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderInformation.FloatSize;
import com.vaadin.terminal.gwt.client.RenderInformation.Size;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;

/**
 * The class representing the portlet in the portal.
 * Basically has the header with portlet controls and caption
 * and the widget which plays the role of the portlet contents. 
 * @author p4elkin
 */
public class Portlet extends ComplexPanel {
  
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
   * Size of the portlet wrapper element.
   */
  private Size sizeInfo = new Size(0, 0);

  /**
   * Header object that both serves as an area for 
   * the draggable part of the portlet and holds the controls 
   * of the portal and its caption.
   */
  private PortletHeader header;

  /**
   * Vaadin widget contained in the portlet.
   */
  private Widget content;

  /**
   * Wrapper around the contents.
   */
  private Element wrapperElement;

  /**
   * Element that holds contents.
   */
  private Element contentDiv;

  /**
   * The portal which currently holds this portlet.
   */
  private VPortalLayout parentPortal = null;

  /**
   * The flag that indicates that height of the portlet 
   * should be calculated in the relative style.
   */
  private boolean isHeightRelative = false;

  /**
   * Relative size. Null, if portlet has fixed size. Anyway - only height matters.
   * Width is always 100% as portlet should fit width of the portal.
   */
  private FloatSize relativeSize;
  
  /**
   * Flag indicating that this portlet is collapsed (only header is visible).
   */
  private boolean isCollapsed = false;

  private ApplicationConnection client;
  /**
   * Constructor.
   * @param widget The contents of the portlets.
   */
  public Portlet(final Widget widget, final ApplicationConnection client) {
    this(widget, client, null);
    this.client = client;
  }
  
  /**
   * Constructor.
   * @param widget The contents of the portlets.
   * @param parent Parent layout.
   */
  public Portlet(Widget widget, final ApplicationConnection client, VPortalLayout parent) {
    super();
    
    this.client = client;
    
    parentPortal = parent;
    content = widget;
    
    wrapperElement = DOM.createDiv();
    
    header = new PortletHeader("Drag Me", this);
    header.getElement().getStyle().setFloat(Style.Float.LEFT);
    add(header, wrapperElement);
    
    contentDiv = DOM.createDiv();
    contentDiv.addClassName(CLASSNAME + CONTENT_CLASSNAME);
    contentDiv.getStyle().setFloat(Style.Float.LEFT);
    
    wrapperElement.appendChild(contentDiv);
    setElement(wrapperElement);
    setStyleName(CLASSNAME);
    wrapperElement.addClassName(CLASSNAME + WRAPPER_CLASSNAME);
    
    add(content, contentDiv);
  }

  /**
   * Check if the contents height should be relatively sized.
   * @return true if the height of the contents is relative.
   */
  public boolean isHeightRelative() {
    return isHeightRelative;
  }

  /**
   * Set the value of the relativity flag of the contents height.
   * @param isHeightRelative
   */
  public void setHeightRelative(boolean isHeightRelative) {
    this.isHeightRelative = isHeightRelative;
  }

  /**
   * Paint the contents.
   * @param uidl
   * @param client
   */
  public void renderContent(UIDL uidl) {
    if (content == null || 
        !(content instanceof Paintable))
      return;
    ((Paintable) content).updateFromUIDL(uidl, client);
  }

  /**
   * Set the new sizes of the contents and wrappers.
   * @param width The new width.
   * @param height The new height.
   */
  public void setSizes(int width, int height) {
    sizeInfo.setWidth(width);
    sizeInfo.setHeight(height);
    setPortletDOMSize(width, height);
  }

  /**
   * Set the wrapper element size.
   * @param width New width.
   * @param height New height.
   */
  public void setPortletDOMSize(int width, int height) {
    contentDiv.getStyle().setPropertyPx("width", width);
    contentDiv.getStyle().setPropertyPx("height", height);
    wrapperElement.getStyle().setPropertyPx("width", width);
    wrapperElement.getStyle().setPropertyPx("height", height + header.getOffsetHeight());
  }

  /**
   * Set wrapper element DOM height in pixels. 
   * @param height New height.
   */
  public void setHeightPx(int height) {
    wrapperElement.getStyle().setPropertyPx("height", height);
  }

  /**
   * Set wrapper element DOM width in pixels. 
   * @param height New width.
   */
  public void setWidthPx(int width) {
    wrapperElement.getStyle().setPropertyPx("width", width);
  }
  
  /**
   * Convenience method needed sometimes 
   * for easier passing the contents to the server side.
   * @return Paintable cast of the contents, null if contents do not implement Paintable 
   * (most likely they do, but everything may happen).
   */
  public Paintable getContentAsPaintable() {
    return (content == null || !(content instanceof Paintable)) ? null : (Paintable) content;
  }

  /**
   * Regular contents access method.
   * @return Widget contained in the portlet.
   */
  public Widget getContent() {
    return content;
  }

  /**
   * Set the contained widget
   * @param content New contents.
   */
  public void setContent(Widget content) {
    this.content = content;
  }

  /**
   * Access the header of the portlet.
   * @return The header widget.
   */
  public Widget getDraggableArea() {
    return header.getDraggableArea();
  }

  /**
   * Access the portal that holds this portlet.
   * @return Parent portal.
   */
  public VPortalLayout getParentPortal() {
    return parentPortal;
  }

  /**
   * Set the new parent of this portlet.
   * @param portal New parent portal.
   */
  public void setParentPortal(VPortalLayout portal) {
    this.parentPortal = portal;
  }
  
  /**
   * Get the index of this portlet in
   * the parent portal.
   * @return Portlet index.
   */
  public int getPosition() {
    final VPortalLayout portal = getParentPortal();
    return portal == null ? -1 : portal.getWidgetIndex(this);
  }
  
  /**
   * Parse uidl and extract info about relative height of the portlet.
   * @param uidl UIDL message.
   * @return true if height is relative. false - otherwise.
   */
  public boolean tryDetectRelativeHeight(final UIDL uidl)
  {
    relativeSize = Util.parseRelativeSize(uidl);
    setHeightRelative(relativeSize != null && relativeSize.getHeight() > 0);
    return isHeightRelative;
  }
  
  /**
   * Check if portlet is collapsed.
   * @return true if portlet is collapsed and only its header is visible.
   */
  public boolean isCollapsed() {
    return isCollapsed;
  }

  /**
   * Collapse/expand portlet.
   * @param isCollapsed True - if portlet must be collapsed.
   */
  public void setCollapsed(boolean isCollapsed) {
    this.isCollapsed = isCollapsed;
  }
  
  /**
   * Close this portlet and notify parent about it.
   */
  public void close()
  {
    removeFromParent();
    parentPortal.onPortalClose(this);
  }
  
  /**
   * Returns the height required for rendering of this portlet.
   * @return Height in pixels.
   */
  public int getRequiredHeight()
  {
    int result = Util.getRequiredHeight(header.getElement());
    if (!isHeightRelative)
      result += Util.getRequiredHeight(content.getElement());
    return result;
  }

  /**
   * Change collapse state - if collapsed then expand 
   * otherwise - collapse. Update size info as well.
   */
  public void toggleCollapseState() {
    setCollapsed(!isCollapsed);
    contentDiv.getStyle().setProperty("visibility", isCollapsed ? "hidden" : "visible");
    setSizes(sizeInfo.getWidth(), isCollapsed ? 0 : content.getOffsetHeight());
    //client.handleComponentRelativeSize(content);
    //client.runDescendentsLayout((HasWidgets) content);
    parentPortal.onPortalCollapseStateChanged(this);  
  }

  /**
   * Get information about the size of this portlet. Size info should be formed 
   * from 
   * @return
   */
  public Size getSizeInfo() {
    return sizeInfo;
  }
  
  /**
   * Get the name of the CSS objects related to portlets.
   * @return Name of CSS class.
   */
  public static String getClassName()
  {
    return CLASSNAME;
  }
}
