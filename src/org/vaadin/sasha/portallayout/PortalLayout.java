package org.vaadin.sasha.portallayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.vaadin.sasha.portallayout.client.ui.PortalArea;
import org.vaadin.sasha.portallayout.client.ui.VPortalLayout;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.VConsole;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.ClientWidget;
import com.vaadin.ui.ClientWidget.LoadStyle;
import com.vaadin.ui.Component;

/**
 * Layout that presents its contents in a portal style.
 * 
 * @author p4elkin
 */
@SuppressWarnings("serial")
@ClientWidget(value = VPortalLayout.class, loadStyle = LoadStyle.EAGER)
public class PortalLayout extends AbstractLayout {

  /**
   * The components each of which is represented with the portlet inside the
   * portal
   */
  private Map<Component, Integer> portletContentsToAreaIndex = new HashMap<Component, Integer>();

  /**
   * Structure maintaining the positions of the portlets.
   */
  private List<List<Component>> portalAreas = new ArrayList<List<Component>>();

  /**
   * The number of the areas that the portal has
   */
  private int areaCount = 0;

  // / Constructor
  public PortalLayout(int areaCount) {
    super();
    setPortalAreaCount(areaCount);
    setWidth("100%");
    setHeight("100%");
  }

  /**
   * Set the numebr of columns in the portal
   * 
   * @param columnCount
   *          New column number
   */
  private void setPortalAreaCount(int columnCount) {
    if (areaCount == columnCount)
      return;
    if (areaCount > columnCount)
      throw new IllegalArgumentException("Currently forbid shrinking areas!");
    else
      increaseWithNulls(columnCount);
    this.areaCount = columnCount;
    requestRepaint();
  }

  /**
   * Ensures that area container has the right amount of elements, even th
   * 
   * @param areaCount
   *          New size of the area container.
   */
  private void increaseWithNulls(int areaCount) {
    while (this.areaCount++ < areaCount)
      portalAreas.add(null);
  }

  @Override
  public void paintContent(PaintTarget target) throws PaintException {
    target.addAttribute("cols", areaCount);
    super.paintContent(target);
    for (int areaIdx = 0; areaIdx < areaCount; ++areaIdx) {
      final List<Component> areaContents = portalAreas.get(areaIdx);
      target.startTag("area");
      if (areaContents != null)
        for (final Component c : areaContents)
          c.paint(target);
      target.endTag("area");
    }
  }

  /**
   * Receive and handle events and other variable changes from the client.
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public void changeVariables(Object source, Map<String, Object> variables) {
    super.changeVariables(source, variables);

    if (variables.containsKey(VPortalLayout.COMPONENT_REMOVED)) {
      Component removedPortlet = (Component) variables
          .get(VPortalLayout.COMPONENT_REMOVED);
      portletContentsToAreaIndex.remove(removedPortlet);
    }

    if (variables.containsKey(VPortalLayout.PORTLET_POSITION_UPDATED)) {
      Map<String, Object> newPortlet = (Map<String, Object>) variables
          .get(VPortalLayout.PORTLET_POSITION_UPDATED);
      final Component component = (Component) newPortlet
          .get(VPortalLayout.PAINTABLE_MAP_PARAM);
      Integer areaIndex = (Integer) newPortlet
          .get(VPortalLayout.AREA_INDEX_MAP_PARAM);
      Integer portletPosition = (Integer) newPortlet
          .get(VPortalLayout.PORTLET_POSITION_MAP_PARAM);
      componentPositionUpdated(component, areaIndex, portletPosition);
    }
  }

  private void componentPositionUpdated(Component component, Integer areaIndex,
      Integer portletPosition) {
    moveComponent(component, areaIndex, portletPosition);
  }

  @Override
  public void replaceComponent(Component oldComponent, Component newComponent) {
  }

  @Override
  public Iterator<Component> getComponentIterator() {
    return Collections.unmodifiableCollection(
        portletContentsToAreaIndex.keySet()).iterator();
  }

  @Override
  public void setWidth(float width, int unit) {
    super.setWidth(width, unit);
  }

  public void addComponent(Component c, int areaIndex, int position) {
    addComponentLogically(c, areaIndex, position);
    requestRepaint();
  }

  private void addComponentLogically(Component c, int areaIndex, int position) {
    if (areaCount <= areaIndex)
      throw new IllegalArgumentException("Wrong index of the area");

    if (portletContentsToAreaIndex.get(c) != null)
      throw new IllegalArgumentException("Already added!");

    final List<Component> area = getAreaByIndexOrCreate(areaIndex);

    if (area.size() < position)
      throw new IllegalArgumentException(
          "Wrong component position - it shouldn't be bigger then the size of the area");

    super.addComponent(c);
    portletContentsToAreaIndex.put(c, areaIndex);

    area.add(position, c);
  }

  private final List<Component> getAreaByIndexOrCreate(int areaIndex)
  {
    List<Component> result = portalAreas.get(areaIndex);

    if (result == null) {
      result = new LinkedList<Component>();
      portalAreas.add(areaIndex, result);
    }
    return result;
  }
  
  private void moveComponent(Component c, int areaIndex, int position) {
    Integer currentAreaIndex = portletContentsToAreaIndex.get(c);
    
    if (currentAreaIndex == null)
    {
      addComponentLogically(c, areaIndex, position);
      return;
    }

    final List<Component> currentArea = portalAreas.get(currentAreaIndex);
    
    int currentPosition = currentArea.indexOf(c);
    
    if (currentAreaIndex.equals(areaIndex) &&
        position == currentPosition)
      return;
      
    currentArea.remove(currentPosition);
    
    final List<Component> newArea = getAreaByIndexOrCreate(areaIndex);
    if (position > newArea.size()) {
      position = newArea.size();
    }
    newArea.add(position, c);
    portletContentsToAreaIndex.put(c, areaIndex);
  }

  public void addComponent(Component c) {
    addComponent(c, 0, 0);
  }
}
