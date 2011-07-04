package org.vaadin.sasha.portallayout.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * Portlet header. COntains the controls for the basic operations with 
 * portlet like closing, collapsing and pinning. 
 * @author p4elkin
 */
public class PortletHeader extends ComplexPanel {
  
  /**
   * Class name for styling the element. 
   */
  public static final String CLASSNAME_SUFFIX = "-header";
  
  /**
   * Class name suffix for the closing button
   */
  private static final String CLOSEBUTTON_CLASSNAME_SUFFIX = "-closebox";
  
  /**
   * Class name suffix for the button bar.
   */
  private static final String BUTTONBAR_CLASSNAME_SUFFIX = "-buttonbar";
  
  /**
   * Class name suffix for the caption wrapper
   */
  private static final String CAPTIONWRAPPER_CLASSNAME_SUFFIX = "-captionwrap";
  
  /**
   * Element that holds the caption
   */
  private final Element captionWrapper = Document.get().createDivElement();
  
  /**
   * Portlet to which this header belongs.
   */
  private final Portlet parentPortlet;
  
  /**
   * 
   */
  private final HTML captionWrapperHtml = new HTML();
  
  private final Element closeBox = Document.get().createDivElement();
  
  /**
   * Close button.
   */
  private Button closeButton = new Button();

  /**
   * Collapse button.
   */
  private Button collapseButton = new Button();
  
  /**
   * Handler for close button click.
   */
  private ClickHandler closeButtonClickHandler = new ClickHandler() {
    @Override
    public void onClick(ClickEvent event) {      
      parentPortlet.close();
    }
  }; 
  

  /**
   * Handler for collapse button click.
   */
  private ClickHandler collapseButtonClickHandler = new ClickHandler() {
    @Override
    public void onClick(ClickEvent event) {      
      parentPortlet.toggleCollapseState();
    }
  };
  
  /**
   * Constructor.
   * @param parent Portlet to which this header belongs.
   */
  public PortletHeader(final String caption, final Portlet parent) {
    super();
    setElement(captionWrapper);
    captionWrapper.setClassName(getClassName());
    captionWrapper.appendChild(closeBox);
    setCaption(caption);
    parentPortlet = parent;
    closeButton.addClickHandler(closeButtonClickHandler);
    collapseButton.addClickHandler(collapseButtonClickHandler);
    
    add(captionWrapperHtml, (com.google.gwt.user.client.Element) captionWrapper);
    add(collapseButton, (com.google.gwt.user.client.Element) closeBox);
    add(closeButton, (com.google.gwt.user.client.Element) closeBox);
    
    closeButton.setStyleName(getClassName() + CLOSEBUTTON_CLASSNAME_SUFFIX);
    collapseButton.setStyleName(getClassName() + CLOSEBUTTON_CLASSNAME_SUFFIX);
    closeBox.setClassName(getClassName() + BUTTONBAR_CLASSNAME_SUFFIX);
    captionWrapperHtml.addStyleName(getClassName() + CAPTIONWRAPPER_CLASSNAME_SUFFIX);
    
  }
  
  public void setCaption(final String caption)
  {
    captionWrapperHtml.getElement().setInnerHTML(caption);
  }
  
  @Override
  public void setWidth(String width) {
    super.setWidth(width);
    getElement().getStyle().getWidth();
    setWidth(width);
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
