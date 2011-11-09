package org.vaadin.sasha.portalappdemo;

import java.io.File;
import java.io.FileFilter;

import com.vaadin.Application;
import com.vaadin.terminal.FileResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Embedded;

@SuppressWarnings("serial")
public class PortalImage extends Embedded {

    private int currentDisplayedImage = -1;
    
    private File[] files; 
    
    private Application app;
    
    public PortalImage(final Application app) {
        super();
        this.app = app;
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
        if (!isEmpty()) {        
            currentDisplayedImage = 0;
            setCaption(files[currentDisplayedImage].getName());
            setIcon(new ThemeResource("picture.png"));
        }
        setWidth("100%");
        setHeight("400px");
    }
    
    public boolean isEmpty() {
        return files == null || files.length == 0;
    }
    public void showNextFile() {
        final File next = getNextFile();
        setCaption(next.getName());
        setSource(new FileResource(next, app));
    }

    public void showPrevFile() {
        final File prev = getPrevFile();
        setCaption(prev.getName());
        setSource(new FileResource(prev, app));
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
}
