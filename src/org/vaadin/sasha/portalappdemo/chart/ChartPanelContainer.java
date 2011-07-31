package org.vaadin.sasha.portalappdemo.chart;

import org.vaadin.sasha.portallayout.PortalLayout;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ChartPanelContainer extends VerticalLayout {

    private final Panel portalContainer = new Panel();

    private final HorizontalLayout panelLayout = new HorizontalLayout();

    public ChartPanelContainer() {
        super();
        setSizeFull();
        buildMainPortal();
        // Panel bottomPanel = new Panel();
        // bottomPanel.setSizeFull();
        // bottomPanel.getContent().setWidth("100%");
        // PortalLayout bottomPortal = new PortalLayout();
        // bottomPortal.setHeight("250px");
        // bottomPortal.setWidth("70%");
        // bottomPanel.getContent().addComponent(bottomPortal);
        // ((VerticalLayout)bottomPanel.getContent()).setComponentAlignment(bottomPortal,
        // Alignment.BOTTOM_CENTER);
        // addComponent(bottomPanel);
        // setExpandRatio(bottomPanel, 1f);
    }

    private Component textText() {
        TextArea tx = new TextArea();
        tx.setSizeFull();
        tx.setHeight("200px");
        return tx;
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
            // portal.addComponent(chart);
            portal.addComponent(chart);
            // portal.setComponentCaption(chart,
            // ChartUtil.getChartCaptionByIndex(i));

            chart = ChartUtil.getChartByIndex(i + 1);
            // portal.addComponent(chart);
            portal.addComponent(chart);
            // portal.setComponentCaption(chart,
            // ChartUtil.getChartCaptionByIndex(i+1));

            chart = ChartUtil.getChartByIndex(i + 2);
            // portal.addComponent(chart);
            portal.addComponent(chart);
            // portal.setComponentCaption(chart,
            // ChartUtil.getChartCaptionByIndex(i+2));

            chart = ChartUtil.getChartByIndex(i + 3);
            // portal.addComponent(chart);
            portal.addComponent(textText());
            // portal.setComponentCaption(chart,
            // ChartUtil.getChartCaptionByIndex(i+3));

            chart = ChartUtil.getChartByIndex(i + 4);
            // portal.addComponent(chart);
            portal.addComponent(textText());
            // portal.setComponentCaption(chart,
            // ChartUtil.getChartCaptionByIndex(i+4));

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
