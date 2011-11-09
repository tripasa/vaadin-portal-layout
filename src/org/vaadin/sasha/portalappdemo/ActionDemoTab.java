package org.vaadin.sasha.portalappdemo;

import org.vaadin.sasha.portalappdemo.chart.ChartUtil;
import org.vaadin.sasha.portallayout.PortalLayout;
import org.vaadin.sasha.portallayout.PortalLayout.PortletCloseListener;
import org.vaadin.sasha.portallayout.PortalLayout.PortletClosedEvent;
import org.vaadin.sasha.portallayout.PortalLayout.PortletCollapseEvent;
import org.vaadin.sasha.portallayout.PortalLayout.PortletCollapseListener;
import org.vaadin.sasha.portallayout.ToolbarAction;
import org.vaadin.sasha.portallayout.event.Context;
import org.vaadin.teemu.ratingstars.RatingStars;
import org.vaadin.youtubeplayer.YouTubePlayer;

import com.vaadin.Application;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class ActionDemoTab extends Panel implements PortletCloseListener, PortletCollapseListener {
   
    public class DemoPortal extends PortalLayout {
        
        public DemoPortal() {
            setWidth("100%");
            setHeight("800px");
            addCloseListener(ActionDemoTab.this);
            addCollapseListener(ActionDemoTab.this);
            setMargin(true);
        }
    }
    
    private final PortalLayout videoPortal = new DemoPortal() {
        public void addComponent(Component c, int position) {
            super.addComponent(c, position);
            clearStyleNames(c);
            addStyleName(c, "red");
        };
    };
    
    private final PortalLayout imagePortal = new DemoPortal()  {
        public void addComponent(Component c, int position) {
            super.addComponent(c, position);
            clearStyleNames(c);
            addStyleName(c, "green");
        };
    };
    
    private final PortalLayout miscPortal = new DemoPortal()  {
        public void addComponent(Component c, int position) {
            super.addComponent(c, position);
            clearStyleNames(c);
            addStyleName(c, "yellow");
        };
    };
    
    private final Application app;
    
    private final GridLayout layout = new GridLayout(3, 1);
    
    private boolean init = false;
    
    public ActionDemoTab(Application app) {
        super();
        this.app = app;
        setSizeFull();
        setContent(layout);
        layout.setWidth("100%");
        layout.setMargin(true);
        layout.setSpacing(true);
        buildPortals();
        construct();
    }

    private void buildPortals() {
        layout.addComponent(videoPortal, 0, 0);
        layout.addComponent(imagePortal, 1, 0);
        layout.addComponent(miscPortal, 2, 0);
    }
    

    public void construct() {
        if (init)
            return;
        init = true;
        createVideoContents();
        createImageContents();
        createMiscContents();
    }
    
    private void createMiscContents() {
        final Component chart = ChartUtil.getChartByIndex(1);
        miscPortal.addComponent(chart);
        chart.setCaption(ChartUtil.getChartCaptionByIndex(1));
        chart.setIcon(new ThemeResource("chart.png"));
    }

    private void createImageContents() {
        final PortalImage image = new PortalImage(app);
        imagePortal.addComponent(image);
        imagePortal.setHeaderWidget(image, new RatingStars());
        imagePortal.addAction(image, new ToolbarAction(new ThemeResource("arrow_right.png")) {
            @Override
            public void execute(final Context context) {
                if (!image.isEmpty()) {
                    image.showNextFile();
                }
            }
        });
        imagePortal.addAction(image, new ToolbarAction(new ThemeResource("arrow_left.png")) {
            @Override
            public void execute(final Context context) {
                if (!image.isEmpty()) {
                    image.showPrevFile();
                }
            }
        });
    }
    
    private void createVideoContents() {
        final YouTubePlayer pl = new YouTubePlayer();
        pl.setHeight("100%");
        pl.setVideoId("QrzGpVOPcTI");
        pl.setImmediate(true);
        videoPortal.addComponent(pl);
        pl.setCaption("Joy Division - Disorder");
        pl.setIcon(new ThemeResource("video.png"));
        videoPortal.addAction(pl, new ToolbarAction(new ThemeResource("stop.png")) {
            @Override
            public void execute(final Context context) {
                pl.stop();
                final Notification n = new Notification("Stop! If didn't stop - DO NOT use YouTube add-on and FF!");
                n.setDelayMsec(1000);
                getWindow().showNotification(n);
            }
        });
        
        videoPortal.addAction(pl, new ToolbarAction(new ThemeResource("pause.png")) {
            @Override
            public void execute(final Context context) {
                pl.pause();
                final Notification n = new Notification("Pause! If didn't pause - DO NOT use YouTube add-on and FF!");
                n.setDelayMsec(1000);
                getWindow().showNotification(n);
            }
        });
        
        videoPortal.addAction(pl, new ToolbarAction(new ThemeResource("play.png")) {
            @Override
            public void execute(final Context context) {
                pl.requestRepaint();
                pl.play();
                final Notification n = new Notification("Play! If didn't start - DO NOT use YouTube add-on and FF!");
                n.setDelayMsec(1000);
                getWindow().showNotification(n);
            }
        });
    }


    @Override
    public void portletCollapseStateChanged(PortletCollapseEvent event) {
        final Context context = event.getContext();
        getWindow().showNotification(context.getComponent().getCaption() + "collapsed " + 
                context.getPortal().isCollapsed(context.getComponent()));
    }

    @Override
    public void portletClosed(PortletClosedEvent event) {
        getWindow().showNotification(event.getContext().getComponent().getCaption() + "closed");
    }

}
