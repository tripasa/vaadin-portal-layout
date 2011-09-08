package org.vaadin.sasha.portallayout.client.ui;

import org.vaadin.sasha.portallayout.client.dnd.util.DOMUtil;

import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Dummy wire frame widget that is displayed when the portlet is dragged within
 * the portal.
 * 
 * @author p4elkin
 */
public class PortalDropPositioner extends SimplePanel implements PortalObject {

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
    public PortalDropPositioner(final Portlet portlet) {
        super();
        setStyleName(CLASS_NAME);
        this.portlet = portlet;
        int width = portlet.getContainerSizeInfo().getWidth();
        int height = portlet.getRequiredHeight();
        setWidgetSizes(width, height);
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
    public void setWidgetWidth(int width) {
        int innerWidth = width - DOMUtil.getHorizontalBorders(this);
        internalContent.getElement().getStyle().setPropertyPx("width", innerWidth);

    }

    @Override
    public void setSpacingValue(int spacing) {
        getElement().getStyle().setPropertyPx("marginTop", spacing);
    }

    @Override
    public Portlet getPortletRef() {
        assert portlet != null;
        return portlet;
    }

}
