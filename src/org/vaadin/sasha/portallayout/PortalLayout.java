package org.vaadin.sasha.portallayout;

import java.util.Iterator;
import java.util.Map;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;

/**
 * Layout that presents its contents in a portal style. 
 * @author p4elkin
 */
@SuppressWarnings("serial")
@com.vaadin.ui.ClientWidget(org.vaadin.sasha.portallayout.client.ui.VPortalLayout.class)
public class PortalLayout extends AbstractLayout implements Layout {

  private String message = "Click here.";

  private int clicks = 0;

  @Override
  public void paintContent(PaintTarget target) throws PaintException {
    super.paintContent(target);

    // Paint any component specific content by setting attributes
    // These attributes can be read in updateFromUIDL in the widget.
    target.addAttribute("clicks", clicks);
    target.addAttribute("message", message);

    // We could also set variables in which values can be returned
    // but declaring variables here is not required
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
    
  }

  @Override
  public Iterator<Component> getComponentIterator() {
    return null;
  }

}
