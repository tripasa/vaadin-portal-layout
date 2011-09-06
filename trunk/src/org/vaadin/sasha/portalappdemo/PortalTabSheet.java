package org.vaadin.sasha.portalappdemo;

import org.vaadin.sasha.portalappdemo.chart.ChartPanelContainer;

import com.vaadin.ui.TabSheet;

@SuppressWarnings("serial")
public class PortalTabSheet extends TabSheet {

    private final VideoPanelContainer videoTabPanel = new VideoPanelContainer();

    private final ChartPanelContainer chartTabPanel = new ChartPanelContainer();

    private final DashBoardPanel dashTab = new DashBoardPanel();

    private final ActionDemoTab actionTab = new ActionDemoTab();
    /**
     * Constructor
     */
    public PortalTabSheet() {
        super();
        //addTab(videoTabPanel, "Video Portal", null);
        //addTab(chartTabPanel, "Chart Portal", null);
        //addTab(dashTab, "Fixed Dash Board", null);
        addTab(actionTab, "Portlets With Actions", null);
        addListener(new SelectedTabChangeListener() {

            @Override
            public void selectedTabChange(SelectedTabChangeEvent event) {
                if (getSelectedTab() != null) {
                    if (getSelectedTab().equals(dashTab)) {
                        dashTab.populateTree();
                    } else if (getSelectedTab().equals(actionTab)) {
                        actionTab.construct();
                    }
                }
            }
        });
    }

}
