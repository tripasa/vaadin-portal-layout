package org.vaadin.sasha.portallayout.client.ui;

import org.vaadin.sasha.portallayout.client.dnd.util.DOMUtil;

import com.google.gwt.user.client.ui.SimplePanel;

/**
 * 
 * @author p4elkin
 *
 */
public class PortalDropPositioner extends SimplePanel implements SizeHandler {

  /**
   * 
   */
  private static final String CLASS_NAME = "v-portallayout-positioner";
  
  /**
   * 
   */
  private final SimplePanel internalContent = new SimplePanel();
  
  /**
   * 
   */
  private final Portlet portlet;
  
  /**
   * Constructor
   */
  public PortalDropPositioner(final Portlet portlet) {
    super();
    setStyleName(CLASS_NAME);
    this.portlet = portlet;
    getElement().getStyle().setPropertyPx("marginTop", portlet.getSpacing());
    setSizes(portlet.getOffsetWidth(), portlet.getRequiredHeight());
    setWidget(internalContent);
  }

  @Override
  public boolean isHeightRelative() {
    assert portlet != null;
    return portlet.isHeightRelative();
  }

  @Override
  public float getRealtiveHeight() {
    assert portlet != null;
    return portlet.getRealtiveHeight();
  }

  @Override
  public int getRequiredHeight() {
    assert portlet != null;
    return portlet.getRequiredHeight();
  }

  @Override
  public void setRealtiveHeightValue(float heightValue) {
    // NOP
  }

  @Override
  public void setSizes(int width, int height) {
    System.out.println("Setting positioner sizes " + width + " " + height);
    int innerWidth = width - DOMUtil.getHorizontalBorders(this);
    int innerHeight = height - DOMUtil.getVerticalBorders(this);
    
    internalContent.setPixelSize(innerWidth, innerHeight);
  }
 
}
