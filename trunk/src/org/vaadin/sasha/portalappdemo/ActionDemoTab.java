package org.vaadin.sasha.portalappdemo;

import java.io.File;
import java.io.FileFilter;

import org.vaadin.sasha.portalappdemo.chart.ChartUtil;
import org.vaadin.sasha.portallayout.PortalLayout;
import org.vaadin.sasha.portallayout.PortalLayout.PortletCloseListener;
import org.vaadin.sasha.portallayout.PortalLayout.PortletClosedEvent;
import org.vaadin.sasha.portallayout.PortalLayout.PortletCollapseListener;
import org.vaadin.sasha.portallayout.PortalLayout.PortletCollapseEvent;
import org.vaadin.sasha.portallayout.ToolbarAction;
import org.vaadin.sasha.portallayout.event.Context;
import org.vaadin.youtubeplayer.YouTubePlayer;

import com.vaadin.Application;
import com.vaadin.terminal.FileResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class ActionDemoTab extends Panel implements PortletCloseListener, PortletCollapseListener {
    
    private final Application app;
    
    private final PortalLayout videoPortal = new PortalLayout() {
        public void addComponent(Component c, int position) {
            super.addComponent(c, position);
            clearStyleNames(c);
            addStyleName(c, "red");
        };
    };
    
    private final PortalLayout imagePortal = new PortalLayout()  {
        public void addComponent(Component c, int position) {
            super.addComponent(c, position);
            clearStyleNames(c);
            addStyleName(c, "green");
        };
    };
    
    private final PortalLayout miscPortal = new PortalLayout()  {
        public void addComponent(Component c, int position) {
            super.addComponent(c, position);
            clearStyleNames(c);
            addStyleName(c, "yellow");
        };
    };
    
    private final GridLayout layout = new GridLayout(3, 1);
    
    private boolean init = false;
    
    private int currentDisplayedImage = -1;
    
    private File[] files; 
    
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
        
        imagePortal.setWidth("100%");
        videoPortal.setWidth("100%");
        miscPortal.setWidth("100%");

        imagePortal.setHeight("800px");
        videoPortal.setHeight("800px");
        miscPortal.setHeight("800px");
        
        imagePortal.addCloseListener(this);
        imagePortal.addCollapseListener(this);
        
        miscPortal.addCloseListener(this);
        miscPortal.addCollapseListener(this);
        
        videoPortal.addCloseListener(this);
        videoPortal.addCollapseListener(this);
        
        miscPortal.setMargin(true);
        imagePortal.setMargin(true);
        videoPortal.setMargin(true);
    }
    

    public void construct() {
        if (init)
            return;
        init = true;
        createVideoContents();
        createImageContents();
        createMisc();
    }
    
    private void createMisc() {
        final Component chart = ChartUtil.getChartByIndex(1);
        miscPortal.addComponent(chart);
        chart.setCaption(ChartUtil.getChartCaptionByIndex(1));
        chart.setIcon(new ThemeResource("chart.png"));
    }

    private void createImageContents() {
        String fullPath = app.getContext().getBaseDirectory() + "/sample_pictures";
        File dir = new File(fullPath);
        files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String path = pathname.getAbsolutePath();
                int mid = path.lastIndexOf(".");
                String ext = path.substring(mid + 1, path.length());
                return "jpg".equals(ext);
            }
        });
        
        if (files.length == 0)
            return;
        final Embedded image = new Embedded();
        currentDisplayedImage = 0;
        image.setWidth("100%");
        image.setHeight("400px");
        image.setIcon(new ThemeResource("arrow_left.png"));
        image.setSource(new FileResource(files[currentDisplayedImage], app));
        imagePortal.addComponent(image);
        image.setCaption(files[currentDisplayedImage].getName());
        image.setIcon(new ThemeResource("picture.png"));
        imagePortal.addAction(image, new ToolbarAction(new ThemeResource("arrow_right.png")) {
            @Override
            public void execute(final Context context) {
                final File next = getNextFile();
                image.setCaption(next.getName());
                image.setSource(new FileResource(next, app));
            }
        });
        imagePortal.addAction(image, new ToolbarAction(new ThemeResource("arrow_left.png")) {
            @Override
            public void execute(final Context context) {
                final File prev = getPrevFile();
                image.setCaption(prev.getName());
                image.setSource(new FileResource(prev, app));
            }
        });
    }
    
    private File getNextFile() {
        if (files == null ||
            files.length == 0)
            return null;
        currentDisplayedImage = ++currentDisplayedImage % files.length;
        return files[currentDisplayedImage];
    }

    private File getPrevFile() {
        if (files == null ||
            files.length == 0)
            return null;
        currentDisplayedImage = currentDisplayedImage == 0 ? 
                files.length - 1 : --currentDisplayedImage;
        return files[currentDisplayedImage];
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
