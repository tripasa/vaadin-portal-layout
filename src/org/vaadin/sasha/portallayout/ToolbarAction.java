package org.vaadin.sasha.portallayout;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Component;

/**
 * Action class that represents a toolbutton on the client side that
 * performs an action.
 * 
 * @author p4elkin
 */
public abstract class ToolbarAction {

    private final ThemeResource icon;
    
    public ToolbarAction(final ThemeResource icon) {
        this.icon = icon;
    }
    
    public ThemeResource getIcon() {
        return icon;
    }
    
    public abstract void execute(final ActionContext context);

    public static class ActionContext {
        
        private final Component component;

        private final PortalLayout portal;
        
        public ActionContext(final PortalLayout portal, final Component c) {
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
}

