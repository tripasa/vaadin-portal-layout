package org.vaadin.sasha.portalappdemo;

import com.vaadin.Application;
import com.vaadin.ui.TabSheet;

@SuppressWarnings("serial")
public class PortalTabSheet extends TabSheet {

    private final VideoPanelContainer videoTabPanel = new VideoPanelContainer();

    private final DashBoardPanel dashTab = new DashBoardPanel();

    private final ActionDemoTab actionTab;
    /**
     * Constructor
     */
    public PortalTabSheet(Application app) {
        super();
        actionTab = new ActionDemoTab(app);
        addTab(actionTab, "Portal In Action", null);
        addTab(dashTab, "Fixed Dash Board", null);
        addTab(videoTabPanel, "Video Portal", null);
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
