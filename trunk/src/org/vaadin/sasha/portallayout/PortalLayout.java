package org.vaadin.sasha.portallayout;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.vaadin.sasha.portallayout.client.ui.VPortalLayout;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.ClientWidget;
import com.vaadin.ui.ClientWidget.LoadStyle;
import com.vaadin.ui.Component;
import com.vaadin.ui.Layout.SpacingHandler;

/**
 * Layout that presents its contents in a portal style.
 * @author p4elkin
 */
@SuppressWarnings("serial")
@ClientWidget(value = VPortalLayout.class, loadStyle = LoadStyle.EAGER)
public class PortalLayout extends AbstractLayout implements SpacingHandler {
  
  /**
   * The flag indicating that spacing is enabled.
   */
  private boolean isSpacingEnabled = true;
  
  /**
   * The components each of which is represented with the portlet inside the
   * portal
   */
  private List<Component> components = new LinkedList<Component>();

  /**
   * Captions of the corresponding portlets
   */
  private Map<Component, String> componentCaptions = new HashMap<Component, String>();
  
  /**
   * The map containing the collapse states of the components.
   */
  private Map<Component, Boolean> collapseStateMap = new HashMap<Component, Boolean>();
  
  /**
   * 
   */
  private Map<Component, Boolean> closableMap = new HashMap<Component, Boolean>();
  
  /**
   * 
   */
  private Map<Component, Boolean> collapsibleMap = new HashMap<Component, Boolean>();
  
  /// Constructor
  public PortalLayout() {
    super();
    setWidth("100%");
    setHeight("300px");
  }

  @Override
  public void paintContent(PaintTarget target) throws PaintException {
    super.paintContent(target);
    target.addAttribute("spacing", isSpacingEnabled);
    for (final Component c : components)
    {
      target.startTag("portlet");
      
      target.addAttribute(VPortalLayout.PORTLET_CAPTION, getComponentCaption(c));
      target.addAttribute(VPortalLayout.PORTLET_CLOSABLE, isClosable(c));
      target.addAttribute(VPortalLayout.PORTLET_COLLAPSIBLE, isCollapsible(c));
      c.paint(target);
      target.endTag("portlet");
    }
  }

  public boolean isCollapsible(Component c) {
    Boolean result = collapsibleMap.get(c);
    return result == null || result;
  }

  public boolean isClosable(Component c) {
    Boolean result = closableMap.get(c); 
    return result == null || result;
  }

  private String getComponentCaption(Component c) {
    final String caption = componentCaptions.get(c);
    return caption == null ? "" : caption;
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
      Map<String, Object> newPortlet = (Map<String, Object>) variables.get(VPortalLayout.PORTLET_POSITION_UPDATED);
      
      final Component component = (Component) newPortlet
          .get(VPortalLayout.PAINTABLE_MAP_PARAM);
      
      Integer portletPosition = (Integer) newPortlet
          .get(VPortalLayout.PORTLET_POSITION_MAP_PARAM);
      
      onComponentPositionUpdated(component, portletPosition);
      
      setClosable(component, (Boolean)newPortlet.get(VPortalLayout.PORTLET_CLOSABLE));
      setCollapsible(component, (Boolean)newPortlet.get(VPortalLayout.PORTLET_COLLAPSIBLE));
      setComponentCaption(component, (String)newPortlet.get(VPortalLayout.PORTLET_CAPTION));
    }
    
    if (variables.containsKey(VPortalLayout.PORTLET_COLLAPSE_STATE_CHANGED))
    {
      final Map<String, Object> params = (Map<String, Object>) variables.get(VPortalLayout.PORTLET_COLLAPSE_STATE_CHANGED);
      
      onPortletCollapsed((Component)params.get(VPortalLayout.PAINTABLE_MAP_PARAM), 
          (Boolean)params.get(VPortalLayout.PORTLET_COLLAPSED));
    }
    
    if (variables.containsKey(VPortalLayout.COMPONENT_REMOVED))
    {
      final Component child = (Component) variables.get(VPortalLayout.COMPONENT_REMOVED);
      doComponentRemoveLogic(child);
    }
  }

  /**
   * Handler that should be invoked when the components collapse state changes.
   * @param component Component which collapse state has changed.
   * @param isCollapsed True if the portlet was collapsed, false - expanded.
   */
  private void onPortletCollapsed(final Component component, Boolean isCollapsed) {
    collapseStateMap.put(component, isCollapsed);
    if (!isCollapsed)
      requestRepaint();
  }

  /**
   * Handler that should be invoked when the components position in the portal 
   * was changed.
   * @param component Component whose position was updated
   * @param portletPosition New position of the component.
   */
  private void onComponentPositionUpdated(Component component, Integer portletPosition) {
    moveComponent(component, portletPosition);
  }

  @Override
  public void replaceComponent(Component oldComponent, Component newComponent) {
  }

  @Override
  public Iterator<Component> getComponentIterator() {
    return Collections.unmodifiableCollection(components).iterator();
  }

  @Override
  public void setWidth(float width, int unit) {
    super.setWidth(width, unit);
  }
  
  @Override
  public void removeComponent(Component c) {
    doComponentRemoveLogic(c);
    super.removeComponent(c);
  }


  private void doComponentRemoveLogic(final Component c)
  {
    components.remove(c);
  }
  
  
  private void moveComponent(Component c, int position) {
    /// The client side reported that portlet is no longer there - remove component if so.
    if (position == -1)
    {
      removeComponent(c);
      return;
    }
    
    int currentComponentPosition = components.indexOf(c);
    
    /// Component is in the right position - nothing to do. 
    if (position == currentComponentPosition)
      return;
    
    /// Component has been added from other portal. We have to add in such case.
    if (currentComponentPosition == -1)
    {
      addComponent(c, position);
      return;
    }
    
    /// Update component position in the list.
    components.remove(c);
    components.add(position, c);
  }
  
  public void addComponent(Component c) {  
    addComponent(c, 0);
  }
  
  public void addComponent(Component c, int position) {
    doComponentAddLogic(c, position);
    super.addComponent(c);
    requestRepaint();
  }
  
  public void setComponentCaption(final Component c, final String caption)
  {
    if (!components.contains(c))
      throw new IllegalArgumentException("Component is not attached to the portal!");
    componentCaptions.put(c, caption);
    requestRepaint();
  }
  
  public void setClosable(final Component c, boolean closable)
  {
    Boolean currentState = closableMap.get(c);
    if (currentState == null ||
        !currentState.equals(closable))
    {
      closableMap.put(c, closable);
      requestRepaint();
    }
  }
  
  public void setCollapsible(final Component c, boolean collapsible)
  {
    Boolean currentState = collapsibleMap.get(c);
    if (currentState == null ||
        !currentState.equals(collapsible))
    {
      collapsibleMap.put(c, collapsible);
      requestRepaint();
    }    
  }
  
  private void doComponentAddLogic(final Component c, int position) {

    if (components.indexOf(c) != -1)
      throw new IllegalArgumentException("Already added!");
    components.add(position, c);
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
}
