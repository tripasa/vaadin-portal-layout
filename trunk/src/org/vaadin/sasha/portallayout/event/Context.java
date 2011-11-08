package org.vaadin.sasha.portallayout.event;

import org.vaadin.sasha.portallayout.PortalLayout;
import com.vaadin.ui.Component;

public class Context {
    
    private final Component component;

    private final PortalLayout portal;
    
    public Context(final PortalLayout portal, final Component c) {
        this.portal = portal;
        this.component = c;
    }
    
    public Component getComponent() {
        return component;
    }
    
    public PortalLayout getPortal() {
        return portal;
    }
}
