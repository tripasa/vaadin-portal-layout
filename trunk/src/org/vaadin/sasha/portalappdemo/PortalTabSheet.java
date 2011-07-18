package org.vaadin.sasha.portalappdemo;

import org.vaadin.sasha.portalappdemo.chart.ChartPanelContainer;

import com.vaadin.ui.TabSheet;

@SuppressWarnings("serial")
public class PortalTabSheet extends TabSheet{
  
  private final VideoPanelContainer videoTabPanel = new VideoPanelContainer();
 
  private final ChartPanelContainer chartTabPanel = new ChartPanelContainer();
  
  private final DashBoardPanel dashTab = new DashBoardPanel();
  
  /**
   * Constructor
   */
  public PortalTabSheet() {
    super();
    addTab(videoTabPanel, "Video Portal", null);
    addTab(chartTabPanel, "Chart Portal", null);
    addTab(dashTab, "Fixed Dash Board", null);
    addListener(new SelectedTabChangeListener() {
      
      @Override
      public void selectedTabChange(SelectedTabChangeEvent event) {
        if (getSelectedTab() != null &&
            getSelectedTab().equals(dashTab))
        {
          dashTab.populateTree();
        }
      }
    });
  }
  
  
}
