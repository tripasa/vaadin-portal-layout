package org.vaadin.sasha.portallayout.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

/**
 * Client-side implementation of the portal layout. 
 * @author p4elkin
 */
public class VPortalLayout extends ComplexPanel implements Paintable {

  public static final String CLASSNAME = "v-portallayout";

  private Element root;
  
  protected String paintableId;

  protected ApplicationConnection client;

  public VPortalLayout() {
    super();
    //setStyleName(CLASSNAME);
    Element main = Document.get().createDivElement();
    
    main.setClassName(CLASSNAME);
    root = Document.get().createDivElement();
    root.getStyle().setBackgroundColor("red");
    root.getStyle().setProperty("width", "100px");
    root.getStyle().setProperty("height", "100px");
    root.getStyle().setProperty("overflow", "hidden");
    
    main.getStyle().setBackgroundColor("red");
    main.getStyle().setProperty("width", "100px");
    main.getStyle().setProperty("height", "100px");
    main.getStyle().setProperty("overflow", "hidden");
    main.appendChild(root);
    
    setElement(main);
  }

  public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
    if (client.updateComponent(this, uidl, true)) {
      // If client.updateComponent returns true there has been no changes and we
      // do not need to update anything.
      return;
    }
    
    // Save reference to server connection object to be able to send
    // user interaction later
    this.client = client;

    // Save the client side identifier (paintable id) for the widget
    paintableId = uidl.getId();
  }
}
