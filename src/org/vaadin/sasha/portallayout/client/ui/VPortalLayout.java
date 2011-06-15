package org.vaadin.sasha.portallayout.client.ui;

import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Client-side implementation of the portal layout. 
 * @author p4elkin
 */
public class VPortalLayout extends SimplePanel implements Paintable {

  /** Set the CSS class name to allow styling. */
  public static final String CLASSNAME = "v-portallayout";

  /** The client side widget identifier */
  protected String paintableId;

  /** Reference to the server connection object. */
  protected ApplicationConnection client;

  /**
   * The constructor should first call super() to initialize the component and
   * then handle any initialization relevant to Vaadin.
   */
  public VPortalLayout() {
    // TODO This example code is extending the GWT Widget class so it must set a
    // root element.
    // Change to a proper element or remove this line if extending another
    // widget.
    setElement(Document.get().createDivElement());

    // This method call of the Paintable interface sets the component
    // style name in DOM tree
    setStyleName(CLASSNAME);

    // Tell GWT we are interested in receiving click events
    sinkEvents(Event.ONCLICK);
  }

  /**
   * Called whenever an update is received from the server
   */
  public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
    // This call should be made first.
    // It handles sizes, captions, tooltips, etc. automatically.
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

    getElement().setInnerHTML("I am an initial impl of the portal layout");
  }
}
