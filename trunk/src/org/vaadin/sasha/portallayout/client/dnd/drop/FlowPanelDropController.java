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
import org.vaadin.sasha.portallayout.client.dnd.util.DOMUtil;
import org.vaadin.sasha.portallayout.client.dnd.util.DragClientBundle;
import org.vaadin.sasha.portallayout.client.dnd.util.LocationWidgetComparator;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Drop controller used for the Vaadin Portal Layout 
 * @author p4elkin
 */
public class FlowPanelDropController extends AbstractInsertPanelDropController {

  /**
   * @param dropTarget the flow panel drop target
   */
  public FlowPanelDropController(FlowPanel dropTarget) {
    super(dropTarget);
  }

  @Override
  protected LocationWidgetComparator getLocationWidgetComparator() {
    return LocationWidgetComparator.BOTTOM_RIGHT_COMPARATOR;
  }
  
  @Override
  protected Widget newPositioner(DragContext context) {
    // Use two widgets so that setPixelSize() consistently affects dimensions
    // excluding positioner border in quirks and strict modes
    SimplePanel outer = new SimplePanel();
    outer.addStyleName(DragClientBundle.INSTANCE.css().positioner());

    // place off screen for border calculation
    RootPanel.get().add(outer, -500, -500);

    // Ensure IE quirks mode returns valid outer.offsetHeight, and thus valid
    // DOMUtil.getVerticalBorders(outer)
    outer.setWidget(new Label("x"));

    int width = 0;
    int height = 0;
    for (Widget widget : context.selectedWidgets) {
      width = Math.max(width, widget.getOffsetWidth());
      height += widget.getOffsetHeight();
    }

    SimplePanel inner = new SimplePanel();
    inner.setPixelSize(width - DOMUtil.getHorizontalBorders(outer), height
        - DOMUtil.getVerticalBorders(outer));

    outer.setWidget(inner);
    outer.getElement().getStyle().setProperty("border", "2px dashed green");
    return outer;
  }
}
