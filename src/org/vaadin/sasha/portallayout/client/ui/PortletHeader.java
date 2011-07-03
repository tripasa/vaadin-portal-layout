package org.vaadin.sasha.portallayout.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Portlet header. COntains the controls for the basic operations with 
 * portlet like closing, collapsing and pinning. 
 * @author p4elkin
 */
public class PortletHeader extends SimplePanel {
  
  /**
   * Class name for styling the element. 
   */
  public static final String CLASSNAME_SUFFIX = "-header";
  
  /**
   * Class name suffix for the closing button
   */
  private static final String CLOSEBOX_CLASSNAME_SUFFIX = "-closebox";
  
  /**
   * Class name suffix for the caption wrapper
   */
  private static final String CAPTIONWRAPPER_CLASSNAME_SUFFIX = "-captionwrap";
  
  /**
   * Element that holds the caption
   */
  private final Element captionWrapper = Document.get().createDivElement();
  /*
   * 
   */
  private Element closeElement;
  /**
   * Portlet to which this header belongs.
   */
  private final Portlet parentPortlet;
  
  private final HTML captionWrapperHtml = new HTML();
  /**
   * Constructor.
   * @param parent Portlet to which this header belongs.
   */
  public PortletHeader(final String caption, final Portlet parent) {
    super(Document.get().createDivElement());
    
    setStyleName(getClassName());
    
    closeElement = Document.get().createDivElement();
    parentPortlet = parent;

    getElement().appendChild(closeElement);
    setWidget(captionWrapperHtml);    

    setCaption(caption);
    
    captionWrapperHtml.addMouseDownHandler(new MouseDownHandler() {
      @Override
      public void onMouseDown(MouseDownEvent event) {
        System.out.println("Header handler activated!");
        event.stopPropagation();
        event.preventDefault();
      }
    });
    
    closeElement.addClassName(getClassName() + CLOSEBOX_CLASSNAME_SUFFIX);
    captionWrapperHtml.getElement().addClassName(getClassName() + CAPTIONWRAPPER_CLASSNAME_SUFFIX);
    DOM.sinkEvents((com.google.gwt.user.client.Element) closeElement, Event.MOUSEEVENTS | Event.TOUCHEVENTS | Event.ONCLICK | Event.ONLOSECAPTURE);
  }
  
  public void setCaption(final String caption)
  {
    captionWrapperHtml.setHTML(caption);
  }
  
  @Override
  public void onBrowserEvent(Event event) {
    super.onBrowserEvent(event);
    if (event.getTypeInt() == Event.ONMOUSEDOWN &&
        DOM.eventGetTarget(event).equals(closeElement))
    {
      System.out.println("onBrowser handler activated!");
      event.stopPropagation();
    }
  }
  
  @Override
  public void setWidth(String width) {
    super.setWidth(width);
    getElement().getStyle().getWidth();
    captionWrapperHtml.setWidth(width);
  }
  
  public Widget getDraggableArea()
  {
    return captionWrapperHtml;
  }
  
  public static String getClassName()
  {
    return Portlet.getClassName() + CLASSNAME_SUFFIX;
  }
}
