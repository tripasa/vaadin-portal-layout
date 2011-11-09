package org.vaadin.sasha.portalappdemo;

import com.vaadin.Application;
import com.vaadin.ui.TabSheet;

@SuppressWarnings("serial")
public class PortalTabSheet extends TabSheet {

    private final ActionDemoTab actionTab;
    /**
     * Constructor
     */
    public PortalTabSheet(Application app) {
        super();
        actionTab = new ActionDemoTab(app);
        addTab(actionTab, "Portal In Action", null);
        addListener(new SelectedTabChangeListener() {

            @Override
            public void selectedTabChange(SelectedTabChangeEvent event) {
                if (getSelectedTab() != null) {
                    if (getSelectedTab().equals(actionTab)) {
                        actionTab.construct();
                    }
                }
            }
        });
    }

}
