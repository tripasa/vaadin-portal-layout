package org.vaadin.sasha.portallayout.client.ui;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderInformation.Size;
import com.vaadin.terminal.gwt.client.UIDL;

public class Portlet extends ComplexPanel {
  private Size wrapperSize = new Size(0, 0);

  private Size contentSize = new Size(0, 0);

  private int headerWidthPx = 0;

  private int headerHeightPx = 0;

  private HTML header;

  private Widget content;

  private Element wrapperElement;

  private Element contentDiv;

  private PortalArea parentArea = null;

  private boolean isHeightRelative = false;

  public Portlet(Widget widget) {
    this(widget, null);
  }

  public Portlet(Widget widget, PortalArea parent) {
    super();

    parentArea = parent;
    content = widget;

    wrapperElement = DOM.createDiv();

    setElement(wrapperElement);

    contentDiv = DOM.createDiv();

    header = new HTML("Drag Me");

    header.getElement().getStyle().setBackgroundColor("#DED");
    header.getElement().getStyle().setFloat(Style.Float.LEFT);

    wrapperElement.appendChild(contentDiv);

    add(header, contentDiv);
    add(content, contentDiv);
  }

  public boolean isHeightRelative() {
    return isHeightRelative;
  }

  public void setHeightRelative(boolean isHeightRelative) {
    this.isHeightRelative = isHeightRelative;
  }

  public void renderContent(UIDL uidl, ApplicationConnection client) {
    if (content == null || !(content instanceof Paintable))
      return;
    ((Paintable) content).updateFromUIDL(uidl, client);
  }

  public void updateSize(int width, int height) {
    setWidth(width + "px");
    setHeight(height + header.getOffsetHeight() + "px");
    header.setWidth(width + "px");
    if (content != null) {
      content.setWidth(width + "px");
      content.setHeight(height + "px");
    }
  }

  public Paintable getContentAsPaintable() {
    if (content == null || !(content instanceof Paintable))
      return null;
    return (Paintable) content;

  }

  public Widget getContent() {
    return content;
  }

  public void setContent(Widget content) {
    this.content = content;
  }

  public int getContentWidth() {
    return content == null ? 0 : content.getOffsetWidth();
  }

  public int getContentHeight() {
    return content == null ? 0 : content.getOffsetHeight();
  }

  public HTML getHeader() {
    return header;
  }

  @Override
  public void removeFromParent() {
    super.removeFromParent();
  }

  public PortalArea getParentArea() {
    return parentArea;
  }

  public int getPositionInArea() {
    final PortalArea area = getParentArea();
    if (area == null)
      return -1;
    return area.getWidgetIndex(this);
  }

  public void setParentArea(PortalArea area) {
    this.parentArea = area;
  }

  public VPortalLayout getParentPortal() {
    return parentArea == null ? null : parentArea.getParentPortal();
  }

}
