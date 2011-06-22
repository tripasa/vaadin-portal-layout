package org.vaadin.sasha.portallayout.client.ui;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

public class Portlet extends FlowPanel
{
  private HTML header;
  
  private Widget content;
  
  public Portlet(Widget widget)
  {
    super();
    content = widget;
    header = new HTML("Drag Me");
    header.getElement().getStyle().setBackgroundColor("#DED");
    header.getElement().getStyle().setFloat(Style.Float.LEFT);
    add(header);
    add(content);
  }
  
  public void renderContent(UIDL uidl, ApplicationConnection client)
  {
    if (content == null ||
        !(content instanceof Paintable))
      return;
    ((Paintable)content).updateFromUIDL(uidl, client);
  }
  
  public void updateSize(int width, int height)
  {
    setWidth(width + "px");
    setHeight(height + header.getOffsetHeight() + "px");
    header.setWidth(width + "px");
    if (content != null)
    {
      content.setWidth(width + "px");
      content.setHeight(height + "px");
    }
  }
  
  public int getContentWidth()
  {
    return content == null ? 0 :content.getOffsetWidth();
  }
  
  public int getContentHeight()
  {
    return content == null ? 0 : content.getOffsetHeight();
  }
  
  public HTML getHeader()
  {
    return header;
  }
}
