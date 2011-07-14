package org.vaadin.sasha.portallayout.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
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
   * Class name suffix for the close button
   */
  private static final String CLOSEBUTTON_CLASSNAME_SUFFIX = "-close";

  /**
   * Class name suffix for the collapse button
   */
  private static final String COLLAPSEBUTTON_CLASSNAME_SUFFIX = "-collapse";

  /**
   * Class name suffix for the collapse button
   */
  private static final String BUTTON_CLASSNAME_SUFFIX = "-button";
  
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

  /**
   * 
   */
  private final Element buttonBar = Document.get().createDivElement();
  
  /**
   * Close button.
   */
  private Button closeButton = new Button();

  /**
   * Collapse button.
   */
  private Button collapseButton = new Button();
  
  /**
   * 
   */
  private boolean closable = true;
  
  /**
   * 
   */
  private boolean collapsible = true;
  
  /**
   * 
   */
  private String caption;
  
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
   * Mouse down handler.
   */
  private MouseDownHandler mouseDownHandler = new MouseDownHandler() {
    
    @Override
    public void onMouseDown(MouseDownEvent event) {
      closeButton.getElement().focus();
    }
  };
  
  /**
   * Constructor.
   * @param parent Portlet to which this header belongs.
   */
  public PortletHeader(final Portlet parent) {
    super();
    setElement(captionWrapper);
    setCaption("");
    captionWrapper.setClassName(getClassName());
    captionWrapper.appendChild(buttonBar);
    parentPortlet = parent;
    
    closeButton.addClickHandler(closeButtonClickHandler);
    collapseButton.addClickHandler(collapseButtonClickHandler);
    captionWrapperHtml.addMouseDownHandler(mouseDownHandler);
    
    add(captionWrapperHtml, (com.google.gwt.user.client.Element) captionWrapper);
    add(collapseButton, (com.google.gwt.user.client.Element) buttonBar);
    add(closeButton, (com.google.gwt.user.client.Element) buttonBar);
    
    closeButton.setStyleName(getClassName() + BUTTON_CLASSNAME_SUFFIX);
    closeButton.addStyleName(getClassName() + BUTTON_CLASSNAME_SUFFIX + CLOSEBUTTON_CLASSNAME_SUFFIX);
    collapseButton.setStyleName(getClassName() + BUTTON_CLASSNAME_SUFFIX);
    collapseButton.addStyleName(getClassName() + BUTTON_CLASSNAME_SUFFIX + COLLAPSEBUTTON_CLASSNAME_SUFFIX);
    
    
    buttonBar.setClassName(getClassName() + BUTTONBAR_CLASSNAME_SUFFIX);
    captionWrapperHtml.addStyleName(getClassName() + CAPTIONWRAPPER_CLASSNAME_SUFFIX);
  }
  
  public void setCaption(final String caption)
  {
    this.caption = caption;
    if (caption == null ||
        caption.isEmpty())
    {
      captionWrapperHtml.getElement().setInnerHTML("&nbsp");
    }
    else
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

  public void setClosable(boolean closable) {
    this.closable = closable;
    closeButton.setVisible(closable);
  }
  
  public void setCollapsible(boolean isCollapsible)
  {
    this.collapsible = isCollapsible;
    collapseButton.setVisible(isCollapsible);
  }

  public boolean isClosable() {
    return closable;
  }

  public boolean isCollapsible() {
    return collapsible;
  }

  public String getCaption() {
    return caption;
  }
}
