package org.vaadin.sasha.portallayout.client.ui;

import org.vaadin.sasha.portallayout.client.PortalDropController;
import org.vaadin.sasha.portallayout.client.dnd.util.DOMUtil;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dummy wire frame widget that is displayed 
 * when the portlet is dragged within the portal.
 * @author p4elkin
 */
public class PortalDropPositioner extends SimplePanel implements RealtiveHeightCapable {

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
  public PortalDropPositioner(final Portlet portlet, final PortalDropController controller) {
    super();
    setStyleName(CLASS_NAME);
    this.portlet = portlet;
    int spacing = ((VPortalLayout)controller.getDropTarget()).getSpacingInfo().vSpacing;
    System.out.println("Spacing" + spacing);
    int position = portlet.getPosition();
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
    int innerWidth = width - DOMUtil.getHorizontalBorders(this);
    int innerHeight = height - DOMUtil.getVerticalBorders(this);
    
    internalContent.setPixelSize(innerWidth, innerHeight);
  }

    public Widget getPortlet() {
        assert portlet != null;
        return portlet;
    }
 
}
