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

/**
 * Layout that presents its contents in a portal style.
 * @author p4elkin
 */
@SuppressWarnings("serial")
@ClientWidget(value = VPortalLayout.class, loadStyle = LoadStyle.EAGER)
public class PortalLayout extends AbstractLayout {

  /**
   * The components each of which is represented with the portlet inside the
   * portal
   */
  private List<Component> components = new LinkedList<Component>();

  /**
   * The map containing the collapse states of the components.
   */
  private Map<Component, Boolean> collapseMap = new HashMap<Component, Boolean>();
  
  /// Constructor
  public PortalLayout() {
    super();
    setWidth("100%");
    setHeight("450px");
  }

  @Override
  public void paintContent(PaintTarget target) throws PaintException {
    super.paintContent(target);
    for (final Component c : components)
     c.paint(target);
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
    collapseMap.put(component, isCollapsed);
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
  
  private void doComponentAddLogic(final Component c, int position) {

    if (components.indexOf(c) != -1)
      throw new IllegalArgumentException("Already added!");
    components.add(position, c);
  }
}
