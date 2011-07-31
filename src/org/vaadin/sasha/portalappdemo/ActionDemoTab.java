package org.vaadin.sasha.portalappdemo;

import java.io.File;
import java.io.FileFilter;

import org.vaadin.sasha.portallayout.PortalLayout;
import org.vaadin.sasha.portallayout.PortalLayout.ToolbarAction;
import org.vaadin.youtubeplayer.YouTubePlayer;

import com.vaadin.terminal.FileResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class ActionDemoTab extends Panel {
    
    private final PortalLayout videoPortal = new PortalLayout();
    
    private final PortalLayout imagePortal = new PortalLayout();
    
    private final PortalLayout miscPortal = new PortalLayout();
    
    private final HorizontalLayout layout = new HorizontalLayout();
    
    private boolean init = false;
    
    private int currentDisplayedImage = -1;

    private File[] files; 
    
    public ActionDemoTab() {
        super();
        setSizeFull();
        layout.setWidth("100%");
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);
        buildPortals();
    }

    private void buildPortals() {
        videoPortal.setHeight("500px");
        imagePortal.setHeight("500px");
        miscPortal.setHeight("500px");
        layout.addComponent(videoPortal);
        layout.addComponent(imagePortal);
        layout.addComponent(miscPortal);
        layout.setExpandRatio(videoPortal, 1f);
        layout.setExpandRatio(imagePortal, 1f);
        layout.setExpandRatio(miscPortal, 1f);
    }
    

    public void construct() {
        if (init)
            return;
        init = true;
        createVideoContents();
        createImageContents();
    }
    
    private void createImageContents() {
        String fullPath = getApplication().getContext().getBaseDirectory() + "/sample_pictures";
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
        image.setSource(new FileResource(files[currentDisplayedImage], getApplication()));
        imagePortal.addComponent(image);
        imagePortal.setComponentCaption(image, files[currentDisplayedImage].getName());
        imagePortal.addAction(image, new ToolbarAction(new ThemeResource("arrow_right.png")) {
            @Override
            public void execute() {
                final File next = getNextFile();
                final PortalLayout parent = (PortalLayout) image.getParent();  
                parent.setComponentCaption(image, next.getName());
                image.setSource(new FileResource(next, getApplication()));
            }
        });
        imagePortal.addAction(image, new ToolbarAction(new ThemeResource("arrow_left.png")) {
            @Override
            public void execute() {
                final File prev = getPrevFile();
                final PortalLayout parent = (PortalLayout) image.getParent();
                parent.setComponentCaption(image, prev.getName());
                image.setSource(new FileResource(prev, getApplication()));
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
        pl.setWidth("100%");
        pl.setHeight("350px");
        pl.setVideoId("QrzGpVOPcTI");
        pl.setImmediate(true);
        videoPortal.addComponent(pl);
        videoPortal.setComponentCaption(pl, "Joy Division - Disorder");
        videoPortal.addAction(pl, new ToolbarAction(new ThemeResource("stop.png")) {
            @Override
            public void execute() {
                pl.stop();
                final Notification n = new Notification("Stop! If didn't stop - DO NOT use YouTube add-on and FF!");
                n.setDelayMsec(1000);
                getWindow().showNotification(n);
            }
        });
        
        videoPortal.addAction(pl, new ToolbarAction(new ThemeResource("pause.png")) {
            @Override
            public void execute() {
                pl.pause();
                final Notification n = new Notification("Pause! If didn't pause - DO NOT use YouTube add-on and FF!");
                n.setDelayMsec(1000);
                getWindow().showNotification(n);
            }
        });
        
        videoPortal.addAction(pl, new ToolbarAction(new ThemeResource("play.png")) {
            @Override
            public void execute() {
                pl.requestRepaint();
                pl.play();
                final Notification n = new Notification("Play! If didn't start - DO NOT use YouTube add-on and FF!");
                n.setDelayMsec(1000);
                getWindow().showNotification(n);
            }
        });
    }

}
