package org.vaadin.sasha.portalappdemo;

import java.io.File;
import java.io.FileFilter;

import org.vaadin.sasha.portallayout.PortalLayout;
import org.vaadin.sasha.portallayout.PortalLayout.Context;
import org.vaadin.sasha.portallayout.ToolbarAction;
import org.vaadin.youtubeplayer.YouTubePlayer;

import com.vaadin.terminal.FileResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class ActionDemoTab extends Panel {
    
    private final PortalLayout videoPortal = new PortalLayout();
    
    private final PortalLayout imagePortal = new PortalLayout();
    
    private final PortalLayout miscPortal = new PortalLayout();
    
    private final HorizontalLayout layout = new HorizontalLayout();
    
    private TextArea tx1 = new TextArea("test1");
    
    private TextArea tx2 = new TextArea("test2");
    
    private boolean init = false;
    
    private int currentDisplayedImage = -1;

    private boolean replacementFlag = true;
    
    private File[] files; 
    
    public ActionDemoTab() {
        super();
        setSizeFull();
        layout.setWidth("100%");
        layout.setMargin(true);
        layout.setSpacing(true);
        final Button b = new Button("replace");
        b.addListener(new ClickListener() {
            
            @Override
            public void buttonClick(ClickEvent event) {
                if (replacementFlag)
                    imagePortal.replaceComponent(tx1, tx2);
                else
                    imagePortal.replaceComponent(tx2, tx1);
                replacementFlag = !replacementFlag;
            }
        });
        setContent(layout);
        layout.addComponent(b);
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
        
        tx1.setWidth("100%");
        tx1.setHeight("300px");
        tx2.setWidth("100%");
        tx2.setHeight("300px");
        tx1.setValue("test1");
        tx2.setValue("test2");
        imagePortal.addComponent(tx1);
        imagePortal.addComponent(new Button("b"));
        imagePortal.addComponent(new TextField("TF test"));
        TextArea l = new TextArea();
        l.setSizeFull();
        l.setCaption("test");
        l.setValue("sadjdklsajkljfklahdkflhlkfhlkdhlfhdlkf");
        imagePortal.addComponent(l);
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
        image.setCaption(files[currentDisplayedImage].getName());
        imagePortal.addAction(image, new ToolbarAction(new ThemeResource("arrow_right.png")) {
            @Override
            public void execute(final Context context) {
                final File next = getNextFile();
                image.setCaption(next.getName());
                image.setSource(new FileResource(next, getApplication()));
            }
        });
        imagePortal.addAction(image, new ToolbarAction(new ThemeResource("arrow_left.png")) {
            @Override
            public void execute(final Context context) {
                final File prev = getPrevFile();
                image.setCaption(prev.getName());
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
        pl.setCaption("Joy Division - Disorder");
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

}
