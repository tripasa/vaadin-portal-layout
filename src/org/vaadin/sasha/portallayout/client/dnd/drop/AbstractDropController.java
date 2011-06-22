/*
 * Copyright 2009 Fred Sauer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.vaadin.sasha.portallayout.client.dnd.drop;

import org.vaadin.sasha.portallayout.client.dnd.DragContext;
import org.vaadin.sasha.portallayout.client.dnd.VetoDragException;
import org.vaadin.sasha.portallayout.client.dnd.util.DragClientBundle;

import com.google.gwt.user.client.ui.Widget;


/**
 * Base class for typical drop controllers. Contains some basic functionality like adjust widget
 * styles.
 */
public abstract class AbstractDropController implements DropController {
  // CHECKSTYLE_JAVADOC_OFF

  /**
   * The drop target.
   */
  private Widget dropTarget;

  public AbstractDropController(Widget dropTarget) {
    this.dropTarget = dropTarget;
    dropTarget.addStyleName(DragClientBundle.INSTANCE.css().dropTarget());
  }

  public Widget getDropTarget() {
    return dropTarget;
  }

  public void onDrop(DragContext context) {
  }

  public void onEnter(DragContext context) {
    dropTarget.addStyleName(DragClientBundle.INSTANCE.css().dropTargetEngage());
  }

  public void onLeave(DragContext context) {
    dropTarget.removeStyleName(DragClientBundle.INSTANCE.css().dropTargetEngage());
  }

  public void onMove(DragContext context) {
  }

  public void onPreviewDrop(DragContext context) throws VetoDragException {
  }
}
