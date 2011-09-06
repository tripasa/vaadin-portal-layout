package org.vaadin.sasha.portallayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.vaadin.sasha.portallayout.client.ui.AnimationType;
import org.vaadin.sasha.portallayout.client.ui.PortalConst;
import org.vaadin.sasha.portallayout.client.ui.VPortalLayout;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickNotifier;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.ThemeResource;
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
public class PortalLayout extends AbstractLayout implements SpacingHandler, LayoutClickNotifier {

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

        public String addAction(final ToolbarAction action) {
            if (actions == null)
                actions = new LinkedHashMap<String, ToolbarAction>();
            final String randomId = "TB_ACTION" + Math.random();
            actions.put(randomId, action);
            return randomId;
        }

        public Map<String, ToolbarAction> getActions() {
            return actions;
        }

        ToolbarAction getActionById(final String id) {
            return actions.get(id);
        }

        public void removeAction(final String actionId) {
            actions.remove(actionId);
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
     * 
     */
    private final Map<AnimationType, Boolean> animationModeMap = new HashMap<AnimationType, Boolean>();
    
    /**
     * 
     */
    private final Map<AnimationType, Integer> animationSpeedMap = new HashMap<AnimationType, Integer>();
    
    /**
     * 
     */
    private final List<PortletCollapseListener> collapseListeners = new ArrayList<PortalLayout.PortletCollapseListener>();
    
    /**
     * 
     */
    private final List<PortletCloseListener> closeListeners = new ArrayList<PortletCloseListener>();
    
    /**
     * 
     */
    private final List<PortalNavigationListener> navigationListeners = new ArrayList<PortalNavigationListener>();
    /**
     * Constructor
     */
    public PortalLayout() {
        super();
        setSizeFull();
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        target.addAttribute("spacing", isSpacingEnabled);
        target.addAttribute(PortalConst.PORTAL_COMMUNICATIVE, isCommunicative);
        for (final AnimationType at : Arrays.asList(AnimationType.values())) {
            target.addAttribute(at.toString(), shouldAnimate(at));
            target.addAttribute(at.toString() + "-SPEED", getAnimationSpeed(at));
        }
        final Iterator<Component> it = components.iterator();
        while (it.hasNext()) {
            final Component childComponent = it.next();
            final ComponentDetails childComponentDetails = componentToDetails
                    .get(childComponent);

            target.startTag("portlet");
            target.addAttribute(PortalConst.PORTLET_CLOSABLE, childComponentDetails.isClosable());
            target.addAttribute(PortalConst.PORTLET_LOCKED, childComponentDetails.isLocked());
            target.addAttribute(PortalConst.PORTLET_COLLAPSED, childComponentDetails.isCollapsed());
            target.addAttribute(PortalConst.PORTLET_COLLAPSIBLE, childComponentDetails.isCollapsible());

            final Map<String, ToolbarAction> actions = childComponentDetails.getActions();
            if (actions != null && actions.entrySet().size() > 0) {
                final Iterator<?> actionIt = actions.entrySet().iterator();
                final String[] ids = new String[actions.entrySet().size()];
                final String[] iconUrls = new String[actions.entrySet().size()];
                int pos = 0;
                while (actionIt.hasNext()) {
                    final Map.Entry<?, ?> entry = (Entry<?, ?>) actionIt.next();
                    final String id = (String) entry.getKey();
                    final ThemeResource r = ((ToolbarAction) entry.getValue()).getIcon();
                    final String icon = "theme://" + r.getResourceId();
                    ids[pos] = id;
                    iconUrls[pos++] = icon;
                }
                target.addAttribute(PortalConst.PORTLET_ACTION_IDS, ids);
                target.addAttribute(PortalConst.PORTLET_ACTION_ICONS,
                        iconUrls);
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
        
        if (variables.containsKey(PortalConst.PORTLET_ACTION_TRIGGERED)) {
            final Map<String, Object> portletParameters = (Map<String, Object>) variables
                    .get(PortalConst.PORTLET_ACTION_TRIGGERED);
            final Component component = (Component) portletParameters
                    .get(PortalConst.PAINTABLE_MAP_PARAM);
            final String actionId = (String) portletParameters
                    .get(PortalConst.PORTLET_ACTION_ID);
            onActionTriggered(component, actionId);
        }

        if (variables.containsKey(PortalConst.PORTLET_POSITION_UPDATED)) {
            final Map<String, Object> portletParameters = (Map<String, Object>) variables.get(PortalConst.PORTLET_POSITION_UPDATED);
            final Component component = (Component) portletParameters.get(PortalConst.PAINTABLE_MAP_PARAM);
            final Integer portletPosition = (Integer) portletParameters.get(PortalConst.PORTLET_POSITION);
            onComponentPositionUpdated(component, portletPosition);
        }

        if (variables.containsKey(PortalConst.PORTLET_COLLAPSE_STATE_CHANGED)) {
            final Map<String, Object> params = (Map<String, Object>) variables.get(PortalConst.PORTLET_COLLAPSE_STATE_CHANGED);

            onPortletCollapsed(
                    (Component) params.get(PortalConst.PAINTABLE_MAP_PARAM),
                    (Boolean) params.get(PortalConst.PORTLET_COLLAPSED));
        }

        if (variables.containsKey(PortalConst.PORTLET_REMOVED)) {
            final Component child = (Component) variables.get(PortalConst.PORTLET_REMOVED);
            doComponentRemoveLogic(child);
        }
    }

    private void onActionTriggered(final Component component, final String actionId) {
        final ComponentDetails details = componentToDetails.get(component);
        if (details == null)
            throw new IllegalArgumentException(
                    "Wrong Component! Action Trigger Failed!");
        final ToolbarAction action = details.getActionById(actionId);
        action.execute(new Context(this, component));
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
        fireCollapseEvent(component);
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
    private void onComponentPositionUpdated(final Component component,
            int newPosition) {

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

    private ComponentDetails getDetails(final Component c) {
        return componentToDetails.get(c);
    }

    @Override
    public void replaceComponent(final Component oldComponent,
            final Component newComponent) {
        int position = components.indexOf(oldComponent);
        if (position < 0)
            throw new IllegalArgumentException(
                    "Portal does not contain the portlet. Replacement failed.");
        componentToDetails.put(newComponent, componentToDetails.get(oldComponent));
        removeComponent(oldComponent);
        doComponentAddLogic(newComponent, position);
    }

    @Override
    public Iterator<Component> getComponentIterator() {
        return Collections.unmodifiableCollection(components).iterator();
    }

    private void doComponentRemoveLogic(final Component c) {
        componentToDetails.remove(c);
        components.remove(c);
    }

    @Override
    public void removeComponent(Component c) {
        doComponentRemoveLogic(c);
        super.removeComponent(c);
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
        final ComponentDetails details = c.getParent() instanceof PortalLayout ? ((PortalLayout) c
                .getParent()).getDetails(c) : new ComponentDetails();
        componentToDetails.put(c, details);
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

    /**
     * Add an action to the components' portlet.
     * 
     * @param c
     *            Component.
     * @param action
     *            Action to be performed.
     * @return New action id. Action can be removed using this id.
     */
    public String addAction(final Component c, final ToolbarAction action) {
        final ComponentDetails details = componentToDetails.get(c);
        if (details == null)
            throw new IllegalArgumentException("Component does not belong to this portal!");
        return details.addAction(action);
    }

    /**
     * Remove action by its ID.
     * 
     * @param c
     *            Component.
     * @param actionId
     *            ID of action to be removed.
     */
    public void removeAction(final Component c, String actionId) {
        final ComponentDetails details = componentToDetails.get(c);
        if (details == null)
            throw new IllegalArgumentException(
                    "Component does not belong to this portal!");
        details.removeAction(actionId);
    }

    public boolean shouldAnimate(final AnimationType animationType) {
        Boolean result = animationModeMap.get(animationType);
        return result == null || result;
    }
    
    public void setAnimationMode(final AnimationType animationType, boolean animate) {
        animationModeMap.put(animationType, animate);
        requestRepaint();
    }
    
    public void setAnimationSpeed(final AnimationType animationType, int speed) {
        animationSpeedMap.put(animationType, speed);
        requestRepaint();
    }
    
    public int getAnimationSpeed(final AnimationType animationType) {
        Integer speed = animationSpeedMap.get(animationType);
        if (speed == null) {
            switch (animationType) {
            case AT_ATTACH:
                return PortalConst.DEFAULT_ATTACH_SPEED;
            case AT_CLOSE:
                return PortalConst.DEFAULT_CLOSE_SPEED;
            case AT_COLLAPSE:
                return PortalConst.DEFAULT_CLOSE_SPEED;
            }
        }
        return speed;
    }
    
    public void addListener(LayoutClickListener listener) {
        addListener(CLICK_EVENT, LayoutClickEvent.class, listener,
                LayoutClickListener.clickMethod);
    }

    public void removeListener(LayoutClickListener listener) {
        removeListener(CLICK_EVENT, LayoutClickEvent.class, listener);
    }
    
    /**
     * Get caption of the portlet containing this component.
     * 
     * @param c
     *            Component
     * @return Caption.
     * @deprecated
     */
    public String getComponentCaption(Component c) {
        return c.getCaption();
    }
    
    /**
     * Set caption of the portlet containing this component.
     * 
     * @param c
     *            Component.
     * @param caption
     *            Caption.
     *@deprecated use components setCation method instead
     */
    public void setComponentCaption(final Component c, final String caption) {
        c.setCaption(caption);
    }
    
    public static class Context {
        
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
    
    public interface PortletCollapseListener {
        void portletCollapseStateChanged(final Context context);
    }
    
    public interface PortletCloseListener {
        void portletClosed(final Context context);
    }
    
    public interface PortalNavigationListener {
        void portletEnetered(final Context context);
        void portletLeft(final Context context);
    }
    
    public void addCloseListener(final PortletCloseListener listener) {
        closeListeners.add(listener);
    }
    
    public void removeCloseListener(final PortletCloseListener listener) {
        closeListeners.remove(listener);
    }
    
    public void addCollapseListener(final PortletCollapseListener listener) {
        collapseListeners.add(listener);
    }
    
    public void removeCollapseListener(final PortletCollapseListener listener) {
        collapseListeners.remove(listener);
    }
    
    public void addNavigationListener(final PortalNavigationListener listener) {
        navigationListeners.add(listener);
    }
    
    public void removeNavigationListener(final PortalNavigationListener listener) {
        navigationListeners.remove(listener);
    }
    
    void fireCloseEvent(final Component c) {
        final Collection<PortletCloseListener> listeners = Collections.unmodifiableCollection(closeListeners);
        final Context context = new Context(this, c); 
        for (final PortletCloseListener l : listeners) {
            l.portletClosed(context);
        }
    }
    
    void fireCollapseEvent(final Component c) {
        final Collection<PortletCollapseListener> listeners = Collections.unmodifiableCollection(collapseListeners);
        final Context context = new Context(this, c); 
        for (final PortletCollapseListener l : listeners) {
            l.portletCollapseStateChanged(context);
        }
    }
    
    void fireNavigationEvent(final Component c, boolean entered) {
        final Collection<PortalNavigationListener> listeners = Collections.unmodifiableCollection(navigationListeners);
        final Context context = new Context(this, c); 
        for (final PortalNavigationListener l : listeners) {
            if (entered)
                l.portletEnetered(context);
            else
                l.portletLeft(context);
        }
    }
}

