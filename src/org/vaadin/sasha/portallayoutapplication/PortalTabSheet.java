package org.vaadin.sasha.portallayoutapplication;

import org.vaadin.sasha.portallayoutapplication.chart.ChartPanelContainer;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;

@SuppressWarnings("serial")
public class PortalTabSheet extends TabSheet{
  
  private VideoPanelContainer videoTabPanel = new VideoPanelContainer();
 
  private ChartPanelContainer chartTabPanel = new ChartPanelContainer();
  
  private DashBoardPanel dashTab = new DashBoardPanel();
  
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
