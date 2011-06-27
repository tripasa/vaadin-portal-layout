package org.vaadin.sasha.portallayout.client.ui;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;

public class PortalArea extends FlowPanel {

  private final VPortalLayout parent;
  
  public PortalArea(final VPortalLayout parent) {
    super();
    this.parent = parent;
    getElement().getStyle().setFloat(Style.Float.LEFT);
    getElement().getStyle().setProperty("border", "solid 1px");
  }

  public VPortalLayout getParentPortal()
  {
    return parent;
  }
  
  public void addPortlet(Portlet portlet) {
    add(portlet);
  }

}
