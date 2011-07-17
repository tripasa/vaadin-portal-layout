package org.vaadin.sasha.portallayout.client.ui;

import org.vaadin.sasha.portallayout.client.PortalDropController;
import org.vaadin.sasha.portallayout.client.dnd.util.DOMUtil;

import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Dummy wire frame widget that is displayed when the portlet is dragged within
 * the portal.
 * 
 * @author p4elkin
 */
public class PortalDropPositioner extends SimplePanel implements PortalObjectSizeHandler {

  /**
   * Basic style name for the widget.
   */
  private static final String CLASS_NAME = "v-portallayout-positioner";

  /**
   * Internal contents.
   */
  private final SimplePanel internalContent = new SimplePanel();

  /**
   * 
   */
  private final Portlet portlet;

  /**
   * Constructor
   */
  public PortalDropPositioner(final Portlet portlet,
      final PortalDropController controller) {
    super();
    setStyleName(CLASS_NAME);
    this.portlet = portlet;
    setWidgetSizes(portlet.getOffsetWidth(), portlet.getRequiredHeight());
    setWidget(internalContent);
  }

  @Override
  public boolean isHeightRelative() {
    assert portlet != null;
    return portlet.isHeightRelative();
  }

  @Override
  public float getRealtiveHeightValue() {
    assert portlet != null;
    return portlet.getRealtiveHeightValue();
  }

  @Override
  public int getRequiredHeight() {
    assert portlet != null;
    return portlet.getRequiredHeight();
  }

  @Override
  public void setWidgetSizes(int width, int height) {
    int innerWidth = width - DOMUtil.getHorizontalBorders(this);
    int innerHeight = height - DOMUtil.getVerticalBorders(this);

    internalContent.setPixelSize(innerWidth, innerHeight);
  }

  @Override
  public void setWidgetHeight(int height) {
    int innerHeight = height - DOMUtil.getVerticalBorders(this);
    internalContent.getElement().getStyle().setPropertyPx("height", innerHeight);
  }

  @Override
  public void setWidgetWidth(int width) {
    int innerWidth = width - DOMUtil.getHorizontalBorders(this);
    internalContent.getElement().getStyle().setPropertyPx("width", innerWidth);

  }

  @Override
  public void setSpacingValue(int spacing) {
    getElement().getStyle().setPropertyPx("marginTop", spacing);
  }

  @Override
  public Portlet getPortalObjectReference() {
    assert portlet != null;
    return portlet;
  }

}