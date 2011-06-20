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
  
  private List<Component> components = new LinkedList<Component>();
  
  private Map<Integer, List<Component>> columns = new HashMap<Integer, List<Component>>();
  
  private int columnCount = 0;
  
  public PortalLayout(int columncount) {
    super();
    setWidth("95%");
    setHeight("95%");
    setColumnCount(columnCount);
  }
  
  private void setColumnCount(int columnCount) {
    this.columnCount = columnCount;
    requestRepaint();
  }

  @Override
  public void paintContent(PaintTarget target) throws PaintException {
    target.addAttribute("cls", columnCount);
    super.paintContent(target);  
    for (final Component c : components)
      c.paint(target);
  }

  /**
   * Receive and handle events and other variable changes from the client.
   * {@inheritDoc}
   */
  @Override
  public void changeVariables(Object source, Map<String, Object> variables) {
    super.changeVariables(source, variables);
  }

  @Override
  public void replaceComponent(Component oldComponent, Component newComponent) {
    // TODO Auto-generated method stub
  }

  @Override
  public Iterator<Component> getComponentIterator() {
    return Collections.unmodifiableCollection(components).iterator();
  }

  @Override
  public void addComponent(Component c) {
    addComponent(c, 0);
  }

    @Override
    public void setWidth(float width, int unit) {
      // TODO Auto-generated method stub
      super.setWidth(width, unit);
    }
  
  public void addComponent(Component c, int columnIdx)
  {
    //if (columns.size() < columnIdx)
    //  throw new IllegalArgumentException();
    super.addComponent(c);
   // List<Component> columnComponents = columns.get(columnIdx);
//    columnComponents.add(c);
    components.add(c);
    requestRepaint();
  }
}
