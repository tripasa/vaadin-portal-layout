package org.vaadin.sasha.portallayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.vaadin.sasha.portallayout.client.ui.VPortalLayout;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickNotifier;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.EventId;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.ClientWidget;
import com.vaadin.ui.ClientWidget.LoadStyle;
import com.vaadin.ui.Component;
import com.vaadin.ui.Layout.SpacingHandler;

/**
 * Layout that presents its contents in a portal style.
 * 
 * @author p4elkin
 */
@SuppressWarnings("serial")
@ClientWidget(value = VPortalLayout.class, loadStyle = LoadStyle.EAGER)
public class PortalLayout extends AbstractLayout implements SpacingHandler,
        LayoutClickNotifier {

    public abstract class ToolbarAction {
        private String iconFileName;
        public abstract void execute();
        public ToolbarAction(final String icon) {
            this.iconFileName = icon;
        }
        public String getIconFileName() {
            return iconFileName;
        }
    }
    
    /**
     * Helper class that holds Portlet information about the object.
     * 
     * @author p4elkin
     */
    public class ComponentDetails implements Serializable {

        private boolean isLocked = false;

        private boolean isCollapsed = false;

        private boolean isClosable = true;

        private boolean isCollapsible = true;

        private String caption;

        private Map<String, ToolbarAction> actions;
        
        public ComponentDetails() {
        }

        public boolean isLocked() {
            return isLocked;
        }

        public void setLocked(boolean isLocked) {
            this.isLocked = isLocked;
        }

        public boolean isCollapsed() {
            return isCollapsed;
        }

        public void setCollapsed(boolean isCollapsed) {
            this.isCollapsed = isCollapsed;
        }

        public boolean isClosable() {
            return isClosable;
        }

        public void setClosable(boolean isClosable) {
            this.isClosable = isClosable;
        }

        public boolean isCollapsible() {
            return isCollapsible;
        }

        public void setCollapsible(boolean isCollapsible) {
            this.isCollapsible = isCollapsible;
        }

        public String getCaption() {
            return caption == null ? "" : caption;
        }

        public void setCaption(final String caption) {
            this.caption = caption;
        }

        public String addAction(final ToolbarAction action) {
            if (actions == null)
                actions = new HashMap<String, ToolbarAction>();
            actions.put("", action);
            return "";
        }
        
        public Map<String, ToolbarAction> getActions() {
            return actions;
        }
    }

    /**
     * Identifier for the click event.
     */
    private static final String CLICK_EVENT = EventId.LAYOUT_CLICK;

    /**
     * The flag indicating that spacing is enabled.
     */
    private boolean isSpacingEnabled = true;

    /**
     * Flag indicating whether this portal can accept portlets from other
     * portals and its portlets can be dragged to the other Portals.
     */
    private boolean isCommunicative = true;

    /**
     * Mapping between the components and their details.
     */
    private Map<Component, ComponentDetails> componentToDetails = new HashMap<Component, ComponentDetails>();

    /**
     * List of components in the order of their appearance in the Portal.
     */
    private List<Component> components = new ArrayList<Component>();

    /**
     * Constructor
     */
    public PortalLayout() {
        super();
        setWidth("100%");
        setHeight("100%");
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        target.addAttribute("spacing", isSpacingEnabled);
        target.addAttribute(VPortalLayout.PORTAL_COMMUNICATIVE, isCommunicative);
        final Iterator<Component> it = components.iterator();
        while (it.hasNext()) {
            final Component childComponent = it.next();
            final ComponentDetails childComponentDetails = componentToDetails
                    .get(childComponent);

            target.startTag("portlet");
            target.addAttribute(VPortalLayout.PORTLET_CAPTION,
                    childComponentDetails.getCaption());
            target.addAttribute(VPortalLayout.PORTLET_CLOSABLE,
                    childComponentDetails.isClosable());
            target.addAttribute(VPortalLayout.PORTLET_LOCKED,
                    childComponentDetails.isLocked());
            target.addAttribute(VPortalLayout.PORTLET_COLLAPSED,
                    childComponentDetails.isCollapsed());
            target.addAttribute(VPortalLayout.PORTLET_COLLAPSIBLE,
                    childComponentDetails.isCollapsible());

            final Map<String, ToolbarAction> actions = childComponentDetails.getActions();
            if (actions != null) {
                final Iterator<?> actionIt = actions.entrySet().iterator();
                final Map<String, String> actionMap = new HashMap<String, String>();
                while (actionIt.hasNext()) {
                    final Map.Entry<?, ?> entry = (Entry<?, ?>) actionIt.next();
                    final String id = (String)entry.getKey();
                    final String icon = ((ToolbarAction)entry.getValue()).getIconFileName();
                    actionMap.put(id, icon);
                }
                if (!actionMap.isEmpty())
                    target.addAttribute(VPortalLayout.PORTLET_ACTIONS, actionMap);
            }
            childComponent.paint(target);
            target.endTag("portlet");
        }
    }

    /**
     * Check if portlet containing this component is collapsed.
     * 
     * @param c
     *            Component
     * @return true if the portlet is collapsed
     */
    public boolean isCollapsed(Component c) {
        final ComponentDetails details = componentToDetails.get(c);

        if (details == null)
            throw new IllegalArgumentException(
                    "Portal doesn not contain this component!");

        return details.isCollapsed();
    }

    /**
     * Check if the portlet containing this component can be collapsed.
     * 
     * @param c
     *            Component
     * @return true if can be collpsed
     */
    public boolean isCollapsible(Component c) {
        final ComponentDetails details = componentToDetails.get(c);

        if (details == null)
            throw new IllegalArgumentException(
                    "Portal doesn not contain this component!");

        return details.isCollapsible();
    }

    /**
     * Check if the portlet containing this component can be closed.
     * 
     * @param c
     *            Component
     * @return true if portlet can be closed.
     */
    public boolean isClosable(Component c) {
        final ComponentDetails details = componentToDetails.get(c);

        if (details == null)
            throw new IllegalArgumentException(
                    "Portal doesn not contain this component!");

        return details.isClosable();
    }

    /**
     * Check if the portlet containing this component is locked (cannot be
     * dragged).
     * 
     * @param c
     *            Component
     * @return true if portlet is locked.
     */
    public boolean isLocked(Component c) {
        final ComponentDetails details = componentToDetails.get(c);

        if (details == null)
            throw new IllegalArgumentException(
                    "Portal doesn not contain this component!");

        return details.isLocked();
    }

    /**
     * Get caption of the portlet containing this component.
     * 
     * @param c
     *            Component
     * @return Caption.
     */
    public String getComponentCaption(Component c) {
        final ComponentDetails details = componentToDetails.get(c);

        if (details == null)
            throw new IllegalArgumentException(
                    "Portal doesn not contain this component!");

        return details.getCaption();
    }

    /**
     * Set caption of the portlet containing this component.
     * 
     * @param c
     *            Component.
     * @param caption
     *            Caption.
     */
    public void setComponentCaption(final Component c, final String caption) {
        final ComponentDetails details = componentToDetails.get(c);

        if (details == null)
            throw new IllegalArgumentException(
                    "Portal doesn not contain this component!");

        final String currentCaption = details.getCaption();

        if (currentCaption != null && caption != null
                && caption.equals(currentCaption))
            return;

        details.setCaption(caption);
        requestRepaint();
    }

    /**
     * Make portlet closable or not closable.
     * 
     * @param c
     *            Component.
     * @param closable
     *            true if portlet can be closed.
     */
    public void setClosable(final Component c, boolean closable) {
        final ComponentDetails details = componentToDetails.get(c);

        if (details == null)
            throw new IllegalArgumentException(
                    "Portal doesn not contain this component!");

        if (details.isClosable() != closable) {
            details.setClosable(closable);
            requestRepaint();
        }
    }

    /**
     * Set lock state of the portlet containing this component.
     * 
     * @param c
     *            Component.
     * @param isLocked
     *            true if locked.
     */
    public void setLocked(final Component c, boolean isLocked) {
        final ComponentDetails details = componentToDetails.get(c);

        if (details == null)
            throw new IllegalArgumentException(
                    "Portal doesn not contain this component!");

        if (isLocked != details.isLocked()) {
            details.setLocked(isLocked);
            requestRepaint();
        }
    }

    /**
     * 
     * @param c
     * @param isCollapsible
     */
    public void setCollapsible(final Component c, boolean isCollapsible) {
        final ComponentDetails details = componentToDetails.get(c);

        if (details == null)
            throw new IllegalArgumentException(
                    "Portal doesn not contain this component!");

        if (isCollapsible != details.isCollapsible()) {
            details.setCollapsible(isCollapsible);
            requestRepaint();
        }
    }

    /**
     * Set collapse state of the portlet.
     * 
     * @param c
     *            Component.
     * @param isCollapsed
     *            true if portlet is collapsed.
     */
    public void setCollapsed(final Component c, boolean isCollapsed) {
        final ComponentDetails details = componentToDetails.get(c);

        if (details == null)
            throw new IllegalArgumentException(
                    "Portal doesn not contain this component!");

        if (isCollapsed != details.isCollapsed()) {
            details.setCollapsed(isCollapsed);
            requestRepaint();
        }
    }

    /**
     * Check if portal accepts portlets from other portals and its portlets can
     * be dragged to other portals.
     * 
     * @return true if can share portlets.
     */
    public boolean isCommunicative() {
        return isCommunicative;
    }

    /**
     * Set if portal accepts portlets from other portals and its portlets can be
     * dragged to other portals.
     * 
     * @param isCommunicative
     *            true if portlets can be dragged to other portals.
     */
    public void setCommunicative(boolean isCommunicative) {
        this.isCommunicative = isCommunicative;
    }

    /**
     * Receive and handle events and other variable changes from the client.
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {

        super.changeVariables(source, variables);

        if (variables.containsKey(VPortalLayout.PORTLET_POSITION_UPDATED)) {
            Map<String, Object> newPortlet = (Map<String, Object>) variables
                    .get(VPortalLayout.PORTLET_POSITION_UPDATED);

            final Component component = (Component) newPortlet
                    .get(VPortalLayout.PAINTABLE_MAP_PARAM);

            Integer portletPosition = (Integer) newPortlet
                    .get(VPortalLayout.PORTLET_POSITION);

            onComponentPositionUpdated(component, portletPosition);

            setCollapsed(component,
                    (Boolean) newPortlet.get(VPortalLayout.PORTLET_COLLAPSED));
            setClosable(component,
                    (Boolean) newPortlet.get(VPortalLayout.PORTLET_CLOSABLE));
            setCollapsible(component,
                    (Boolean) newPortlet.get(VPortalLayout.PORTLET_COLLAPSIBLE));
            setComponentCaption(component,
                    (String) newPortlet.get(VPortalLayout.PORTLET_CAPTION));

        }

        if (variables.containsKey(VPortalLayout.PORTLET_COLLAPSE_STATE_CHANGED)) {
            final Map<String, Object> params = (Map<String, Object>) variables
                    .get(VPortalLayout.PORTLET_COLLAPSE_STATE_CHANGED);

            onPortletCollapsed(
                    (Component) params.get(VPortalLayout.PAINTABLE_MAP_PARAM),
                    (Boolean) params.get(VPortalLayout.PORTLET_COLLAPSED));
        }

        if (variables.containsKey(VPortalLayout.COMPONENT_REMOVED)) {
            final Component child = (Component) variables
                    .get(VPortalLayout.COMPONENT_REMOVED);
            doComponentRemoveLogic(child);
        }
    }

    /**
     * Handler that should be invoked when the components collapse state
     * changes.
     * 
     * @param component
     *            Component which collapse state has changed.
     * @param isCollapsed
     *            True if the portlet was collapsed, false - expanded.
     */
    private void onPortletCollapsed(final Component component,
            Boolean isCollapsed) {
        final ComponentDetails details = componentToDetails.get(component);

        if (details == null)
            throw new IllegalArgumentException(
                    "Portal doesn not contain this component!");

        details.setCollapsed(isCollapsed);
    }

    /**
     * Handler that should be invoked when the components position in the portal
     * was changed.
     * 
     * @param component
     *            Component whose position was updated
     * @param newPosition
     *            New position of the component.
     */
    private void onComponentPositionUpdated(Component component, int newPosition) {

        // The client side reported that portlet is no longer there - remove
        // component if so.
        if (newPosition == -1) {
            removeComponent(component);
            return;
        }

        int oldPosition = components.indexOf(component);

        if (oldPosition == -1) {
            doComponentAddLogic(component, newPosition);
            return;
        }

        // / Component is in the right position - nothing to do.
        if (newPosition == oldPosition)
            return;

        components.remove(component);
        components.add(newPosition, component);
    }

    @Override
    public void replaceComponent(Component oldComponent, Component newComponent) {
    }

    @Override
    public Iterator<Component> getComponentIterator() {
        return Collections.unmodifiableCollection(components).iterator();
    }

    private void doComponentRemoveLogic(final Component c) {
        componentToDetails.remove(c);
        components.remove(c);
    }

    public void addComponent(Component c) {
        addComponent(c, components.size());
    }

    public void addComponent(Component c, int position) {
        doComponentAddLogic(c, position);
        requestRepaint();
    }

    private void doComponentAddLogic(final Component c, int position) {
        int index = components.indexOf(c);

        if (index != -1)
            throw new IllegalArgumentException(
                    "Component has already been added to the portal!");

        c.setWidth("100%");
        componentToDetails.put(c, new ComponentDetails());

        if (position == components.size())
            components.add(c);
        else
            components.add(position, c);

        super.addComponent(c);
    }

    @Override
    public void setSpacing(boolean enabled) {
        isSpacingEnabled = enabled;
        requestRepaint();
    }

    @Override
    public boolean isSpacingEnabled() {
        return isSpacingEnabled;
    }

    @Override
    public boolean isSpacing() {
        return isSpacingEnabled;
    }

    public void addAction(final Component c, final ToolbarAction action) {
        final ComponentDetails details = componentToDetails.get(c);
        if (details == null)
            throw new IllegalArgumentException("Component does not belong to this portal!");
        details.addAction(action);
    }
    
    public void addListener(LayoutClickListener listener) {
        addListener(CLICK_EVENT, LayoutClickEvent.class, listener,
                LayoutClickListener.clickMethod);
    }

    public void removeListener(LayoutClickListener listener) {
        removeListener(CLICK_EVENT, LayoutClickEvent.class, listener);
    }
}
