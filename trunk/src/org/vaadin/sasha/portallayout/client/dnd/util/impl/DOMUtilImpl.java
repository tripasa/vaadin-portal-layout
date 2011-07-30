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
package org.vaadin.sasha.portallayout.client.dnd.util.impl;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

/*
 * {@link com.allen_sauer.gwt.dnd.client.util.DOMUtil} default cross-browser implementation.
 */
public abstract class DOMUtilImpl {

  // CHECKSTYLE_JAVADOC_OFF

  public abstract String adjustTitleForBrowser(String title);

  public abstract void cancelAllDocumentSelections();

  public abstract int getBorderLeft(Element elem);

  public abstract int getBorderTop(Element elem);

  public abstract int getClientHeight(Element elem);

  public abstract int getClientWidth(Element elem);

  /**
   * From http://code.google.com/p/doctype/wiki/ArticleComputedStyle
   */
  public native String getEffectiveStyle(Element elem, String style) /*-{
    return this.@org.vaadin.sasha.portallayout.client.dnd.util.impl.DOMUtilImpl::getComputedStyle(Lcom/google/gwt/dom/client/Element;Ljava/lang/String;)(elem,style)
        || (elem.currentStyle ? elem.currentStyle[style] : null)
        || elem.style[style];
  }-*/;

  public final int getHorizontalBorders(Widget widget) {
    return widget.getOffsetWidth() - getClientWidth(widget.getElement());
  }

  public final int getVerticalBorders(Widget widget) {
    return widget.getOffsetHeight() - getClientHeight(widget.getElement());
  }

  private native String getComputedStyle(Element elem, String style) /*-{
    if ($doc.defaultView && $doc.defaultView.getComputedStyle) {
      var styles = $doc.defaultView.getComputedStyle(elem, "");
      if (styles) {
        return styles[style];
      }
    }

    return null;
  }-*/;

  public native int getPaddingLeft(Element elem)
  /*-{
    try {
      var computedStyle = $doc.defaultView.getComputedStyle(elem, null);
      var paddingLeftWidth = computedStyle.getPropertyValue("padding-left");
      return paddingLeftWidth.indexOf("px") == -1 ? 0 : parseInt(paddingLeftWidth.substr(0, paddingLeftWidth.length - 2));
    } catch(e) { throw new Error("getPaddingLeft exception:\n" + e); }
  }-*/;

  public native int getPaddingRight(Element elem)
  /*-{
    try {
      var computedStyle = $doc.defaultView.getComputedStyle(elem, null);
      var padding = computedStyle.getPropertyValue("padding-right");
      return padding.indexOf("px") == -1 ? 0 : parseInt(padding.substr(0, padding.length - 2));
    } catch(e) { throw new Error("getPaddingRight exception:\n" + e); }
  }-*/;

  public native int getPaddingBottom(Element elem)
  /*-{
    try {
      var computedStyle = $doc.defaultView.getComputedStyle(elem, null);
      var padding = computedStyle.getPropertyValue("padding-bottom");
      return padding.indexOf("px") == -1 ? 0 : parseInt(padding.substr(0, padding.length - 2));
    } catch(e) { throw new Error("getPaddingBottom exception:\n" + e); }
  }-*/;

  public native int getPaddingTop(Element elem)
  /*-{
    try {
      var computedStyle = $doc.defaultView.getComputedStyle(elem, null);
      var padding = computedStyle.getPropertyValue("padding-top");
      return padding.indexOf("px") == -1 ? 0 : parseInt(padding.substr(0, padding.length - 2));
    } catch(e) { throw new Error("getPaddingTop exception:\n" + e); }
  }-*/;

}
