package org.vaadin.sasha.portallayoutapplication.chart;

import org.vaadin.sasha.portallayout.PortalLayout;

import com.vaadin.ui.Alignment;
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
    buildPortal();
    PortalLayout topPortal = new PortalLayout();
    topPortal.setHeight("250px");
    topPortal.setWidth("70%");
    addComponent(topPortal);
    setComponentAlignment(topPortal, Alignment.BOTTOM_CENTER);
  }

  
  private void buildPortal() {
    portalContainer.setContent(panelLayout);
    portalContainer.setSizeFull();
    
    panelLayout.setWidth("100%");
    panelLayout.setSpacing(true);
    panelLayout.setMargin(true);
    
    for (int i = 0; i < 6; i=i+2)
    {
      final PortalLayout portal = new PortalLayout();
      portal.setHeight("500px");
      Component chart = ChartUtil.getChartByIndex(i); 
      portal.addComponent(chart);
      portal.setComponentCaption(chart, ChartUtil.getChartCaptionByIndex(i));
      
      chart = ChartUtil.getChartByIndex(i+1); 
      portal.addComponent(chart);
      portal.setComponentCaption(chart, ChartUtil.getChartCaptionByIndex(i+1));
      
      panelLayout.addComponent(portal);
      panelLayout.setExpandRatio(portal, 1f);
    }
    addComponent(portalContainer);
    setExpandRatio(portalContainer, 1f);
  }
}