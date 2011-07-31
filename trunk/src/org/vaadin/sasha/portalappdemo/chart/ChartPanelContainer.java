package org.vaadin.sasha.portalappdemo.chart;

import org.vaadin.sasha.portallayout.PortalLayout;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ChartPanelContainer extends VerticalLayout {

    private final Panel portalContainer = new Panel();

    private final HorizontalLayout panelLayout = new HorizontalLayout();

    public ChartPanelContainer() {
        super();
        setSizeFull();
        buildMainPortal();
    }

    private void buildMainPortal() {
        portalContainer.setContent(panelLayout);
        portalContainer.setSizeFull();

        panelLayout.setWidth("100%");
        panelLayout.setSpacing(true);
        panelLayout.setMargin(true);

        for (int i = 0; i < 10; i = i + 2) {
            final PortalLayout portal = new PortalLayout();
            portal.setHeight("500px");
            portal.setMargin(true);
            Component chart = ChartUtil.getChartByIndex(i);
            portal.addComponent(chart);
            portal.setComponentCaption(chart,
            ChartUtil.getChartCaptionByIndex(i));

            chart = ChartUtil.getChartByIndex(i + 1);
            portal.addComponent(chart);
            portal.setComponentCaption(chart,
            ChartUtil.getChartCaptionByIndex(i+1));

            panelLayout.addComponent(portal);
            if (i == 1)
                panelLayout.setExpandRatio(portal, 1.5f);
            else if (i == 2)
                panelLayout.setExpandRatio(portal, 2f);
            else
                panelLayout.setExpandRatio(portal, 0.5f);
        }
        addComponent(portalContainer);
        setExpandRatio(portalContainer, 2.5f);
    }
}
