package org.vaadin.sasha.portallayoutapplication;

import com.vaadin.ui.TabSheet;

@SuppressWarnings("serial")
public class PortalTabSheet extends TabSheet{
  
  private VideoPanelContainer chartTabLayout = new VideoPanelContainer();
 
  /**
   * Constructor
   */
  public PortalTabSheet() {
    super();
    addTab(chartTabLayout, "Portal with charts", null);
  }
  
  
}
