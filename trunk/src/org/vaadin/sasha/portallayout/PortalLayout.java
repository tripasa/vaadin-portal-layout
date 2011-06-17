package org.vaadin.sasha.portallayout;

import java.util.Map;

import org.vaadin.sasha.portallayout.client.ui.VPortalLayout;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.ClientWidget;

/**
 * Layout that presents its contents in a portal style. 
 * @author p4elkin
 */
@SuppressWarnings("serial")
@ClientWidget(VPortalLayout.class)
public class PortalLayout extends AbstractComponent{
  
  public PortalLayout() {
    super();
  }
  
  @Override
  public void paintContent(PaintTarget target) throws PaintException {
    super.paintContent(target);
  }

  /**
   * Receive and handle events and other variable changes from the client.
   * {@inheritDoc}
   */
  @Override
  public void changeVariables(Object source, Map<String, Object> variables) {
    super.changeVariables(source, variables);
  }


}
