package org.vaadin.sasha.portallayoutapplication;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.vaadin.sasha.portallayout.PortalLayout;
import org.vaadin.youtubeplayer.YouTubePlayer;

import com.invient.vaadin.charts.Color.RGB;
import com.invient.vaadin.charts.InvientCharts;
import com.invient.vaadin.charts.InvientCharts.DecimalPoint;
import com.invient.vaadin.charts.InvientCharts.SeriesType;
import com.invient.vaadin.charts.InvientCharts.XYSeries;
import com.invient.vaadin.charts.InvientChartsConfig;
import com.invient.vaadin.charts.InvientChartsConfig.PieConfig;
import com.invient.vaadin.charts.InvientChartsConfig.PieDataLabel;
import com.invient.vaadin.charts.InvientChartsConfig.PointConfig;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class VideoPanelContainer extends HorizontalSplitPanel
{
  //private final Map<> videoIds = new String[] {"eqfiHfDmOnw", "oZDiOuJR5HM", "51Bpx63wkbA", "Bz8iEJeh26E"};
  
  private final Map<String, String> initialVideoIds = new HashMap<String, String>();
  
  private final Panel rightPanel = new Panel();
  
  private final Panel mainPanel = new Panel();
  
  final PortalLayout rightPortal = new PortalLayout();
  
  /**
   * Constructor
   */
  public VideoPanelContainer() {
    super();
    setSizeFull();
    bundleStartVideos();
    setSplitPosition(80);
    buildContent();
  }

  private void bundleStartVideos() {
    initialVideoIds.put("eqfiHfDmOnw", "Interpol - Evil");
    initialVideoIds.put("oZDiOuJR5HM", "Sick Of It All - Death Or Jail");
    initialVideoIds.put("51Bpx63wkbA", "Royksopp - Happy Up There");
    initialVideoIds.put("Bz8iEJeh26E", "Sigur Ros - Glosoli");
  }

  private void buildContent() {
   buildMainPanel();    
   buildRightPanel();
  }
  
  private void buildRightPanel() {
    VerticalLayout sideLayout = new VerticalLayout();
    sideLayout.setSizeFull();
    rightPortal.setSizeFull();
    for (final String str : initialVideoIds.keySet())
      addVideo(str, initialVideoIds.get(str));
    sideLayout.addComponent(new ArtistCombo());
    sideLayout.addComponent(rightPortal);
    sideLayout.setExpandRatio(rightPortal, 1f);
    addComponent(sideLayout);
  }

  private void addVideo(String str, String name) {
    final YouTubePlayer pl = new YouTubePlayer();
    pl.setWidth("100%");
    pl.setHeight("100%");
    pl.setVideoId(str);
    rightPortal.addComponent(pl);
    rightPortal.setComponentCaption(pl, name);
  }

  private void buildMainPanel() {
    mainPanel.setSizeFull();
    final HorizontalLayout mainPanelLayout = new HorizontalLayout();
    mainPanelLayout.setSizeFull();
    mainPanel.setContent(mainPanelLayout);

    mainPanelLayout.setMargin(false);
    mainPanelLayout.setWidth("100%");
    final PortalLayout widePortal = new PortalLayout();

    widePortal.setSizeFull();
    mainPanelLayout.setSpacing(true);
    mainPanelLayout.addComponent(widePortal);
    mainPanelLayout.setExpandRatio(widePortal, 1f);
    addComponent(mainPanel);
  }

  private boolean flag = false;
  
  private void addPortletWithContents(final ComponentContainer portal) {
    TextArea tx = new TextArea();
    final Panel ppp = new Panel();
    ppp.setWidth("100%");
    YouTubePlayer pl = new YouTubePlayer();
    pl.setVideoId("eqfiHfDmOnw");
    pl.setWidth("100%");
    pl.setHeight("300px");
    Button tets = new Button("asdas");
    tets.addListener(new Button.ClickListener() {
      
      @Override
      public void buttonClick(ClickEvent event) {
        ppp.addComponent(new Button("adsasds"));
      }
    });
    ppp.addComponent(pl);
    ppp.addComponent(tets);
    tx.setSizeFull();
      portal.addComponent(flag ? pl : tx);
    flag = !flag;
  }
  
  
  
  private Component createPie()
  {
    InvientChartsConfig chartConfig = new InvientChartsConfig();
    chartConfig.getGeneralChartConfig().setType(SeriesType.PIE);
    chartConfig.getTitle().setText(
            "Browser market shares at a specific website, 2010");

    PieConfig pieCfg = new PieConfig();
    pieCfg.setAllowPointSelect(true);
    pieCfg.setCursor("pointer");
    pieCfg.setDataLabel(new PieDataLabel());
    pieCfg.getDataLabel().setEnabled(true);
    pieCfg.getDataLabel()
            .setFormatterJsFunc(
                    "function() {"
                            + " return '<b>'+ this.point.name +'</b>: '+ this.y +' %';"
                            + "}");
    pieCfg.getDataLabel().setConnectorColor(new RGB(0, 0, 0));

    chartConfig.addSeriesConfig(pieCfg);

    InvientCharts chart = new InvientCharts(chartConfig);

    XYSeries series = new XYSeries("Browser Share");
    LinkedHashSet<DecimalPoint> points = new LinkedHashSet<DecimalPoint>();
    points.add(new DecimalPoint(series, "Firefox", 45.0));
    points.add(new DecimalPoint(series, "IE", 26.8));
    PointConfig config = new PointConfig(true);
    points.add(new DecimalPoint(series, "Chrome", 12.8, config));
    points.add(new DecimalPoint(series, "Safari", 8.5));
    points.add(new DecimalPoint(series, "Opera", 6.2));
    points.add(new DecimalPoint(series, "Others", 0.7));

    series.setSeriesPoints(points);
    chart.addSeries(series);

    chart.setWidth("100%");
    chart.setHeight("400px");
    
    return chart;
  }
  
  public Table createTableTest() {
    final Table table = new Table("", new TestIndexedContainer());
    table.setSelectable(true);
    table.setWidth("100%");
    table.setHeight("300px");
    return table;
  }

  public class TestIndexedContainer extends IndexedContainer {
    public TestIndexedContainer() {
      super();
      addContainerProperty("test1", String.class, "0");
      addContainerProperty("test2", String.class, "0");
      Item item = getItem(addItem());
      item.getItemProperty("test1").setValue("test1");
      item.getItemProperty("test2").setValue("test2");
    }
  }
  
  private class ArtistCombo extends ComboBox
  {
    private Map<String, String> artistVideos = new HashMap<String, String>();
    
    public ArtistCombo() {
      super();
      setWidth("100%");
      setImmediate(true);
      bundleArtists();
      for (final String str : artistVideos.keySet())
        addItem(str);
      rightPanel.addComponent(new Button());
      addListener(new ValueChangeListener() {
        @Override
        public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
          if (event.getProperty().getValue() == null)
            return;
          String name = event.getProperty().getValue().toString();
          addVideo(artistVideos.get(name), name);
        }
      });
    }

    private void bundleArtists() {
      artistVideos.put("Editors - Munich", "oVLdaa4Wplo");
      artistVideos.put("Joy Division - Disorder", "QrzGpVOPcTI");
      artistVideos.put("The Killers - When You Were Young", "ff0oWESdmH0");
      artistVideos.put("Interpol - Evil", "eqfiHfDmOnw");
      artistVideos.put("Sick Of It All - Death Or Jail", "oZDiOuJR5HM");
      artistVideos.put("Royksopp - Happy Up There", "51Bpx63wkbA");
      artistVideos.put("Sigur Ros - Glosoli", "Bz8iEJeh26E");
    }
  }
}

