package org.vaadin.sasha.portallayout.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;

/**
 * Portlet header. COntains the controls for the basic operations with 
 * portlet like closing, collapsing and pinning. 
 * @author p4elkin
 */
public class PortletHeader extends HTML{
  
  /**
   * Class name for styling the element. 
   */
  public static final String CLASSNAME_SUFFIX = "-header";
  
  /**
   * 
   */
  private static final String CLOSEBOX_CLASSNAME_SUFFIX = "-closebox";
  
  private Element closeElement;
  /**
   * Portlet to which this header belongs.
   */
  private final Portlet parentPortlet;
  
  /**
   * Constructor.
   * @param parent Portlet to which this header belongs.
   */
  public PortletHeader(final String caption, final Portlet parent) {
    super(caption);
    setStyleName(getClassName());
    
    closeElement = DOM.createDiv();
    closeElement.addClassName(getClassName() + CLOSEBOX_CLASSNAME_SUFFIX);
    getElement().appendChild(closeElement);
    parentPortlet = parent;
    sinkEvents(Event.MOUSEEVENTS | Event.TOUCHEVENTS | Event.ONCLICK
        | Event.ONLOSECAPTURE);
  }

  @Override
  public void onBrowserEvent(Event event) {
    super.onBrowserEvent(event);
    boolean bubble = true;
    final int type = event.getTypeInt();
    if (type != Event.ONCLICK ||
        !getElement().isOrHasChild(DOM.eventGetTarget(event)))
      return;
  }
  
  public static String getClassName()
  {
    return Portlet.getClassName() + CLASSNAME_SUFFIX;
  }
}
