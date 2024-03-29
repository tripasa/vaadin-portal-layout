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
package org.vaadin.sasha.portallayout.client.dnd.util;

import org.vaadin.sasha.portallayout.client.dnd.util.impl.DOMUtilImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Provides DOM utility methods.
 */
public class DOMUtil {

    /**
     * Whether or not debugging is enabled.
     */
    public static final boolean DEBUG = false;

    private static DOMUtilImpl impl;

    static {
        impl = GWT.create(DOMUtilImpl.class);
    }

    /**
     * Adjust line breaks within in the provided title for optimal readability
     * and display length for the current user agent.
     * 
     * @param title
     *            the desired raw text
     * @return formatted and escaped text
     */
    public static String adjustTitleForBrowser(String title) {
        return impl.adjustTitleForBrowser(title).replaceAll("</?code>", "`");
    }

    /**
     * Cancel all currently selected region(s) on the current page.
     */
    public static void cancelAllDocumentSelections() {
        impl.cancelAllDocumentSelections();
    }

    /**
     * Set a widget's border style for debugging purposes.
     * 
     * @param widget
     *            the widget to color
     * @param color
     *            the desired border color
     */
    public static void debugWidgetWithColor(Widget widget, String color) {
        if (DEBUG) {
            widget.getElement().getStyle()
                    .setProperty("border", "2px solid " + color);
        }
    }

    /**
     * Set an element's location as fast as possible, avoiding some of the
     * overhead in
     * {@link com.google.gwt.user.client.ui.AbsolutePanel#setWidgetPosition(Widget, int, int)}
     * .
     * 
     * @param elem
     *            the element's whose position is to be modified
     * @param left
     *            the left pixel offset
     * @param top
     *            the top pixel offset
     */
    public static void fastSetElementPosition(Element elem, int left, int top) {
        elem.getStyle().setPropertyPx("left", left);
        elem.getStyle().setPropertyPx("top", top);
    }

    /**
     * Find child widget intersection at the provided location using the
     * provided comparator strategy. TODO Handle LTR case for Bidi TODO Change
     * IndexedPanel -> InsertPanel
     * 
     * @param parent
     *            the parent widget which contains the children to be compared
     * @param location
     *            the location of the intersection
     * @param comparator
     *            the comparator strategy
     * @return the index of the matching child
     */
    public static int findIntersect(IndexedPanel parent, Location location,
            LocationWidgetComparator comparator) {
        int widgetCount = parent.getWidgetCount();

        // short circuit in case dropTarget has no children
        if (widgetCount == 0) {
            return 0;
        }

        // binary search over range of widgets to find intersection
        int low = 0;
        int high = widgetCount;

        while (true) {
            int mid = (low + high) / 2;
            assert mid >= low;
            assert mid < high;
            Widget widget = parent.getWidget(mid);
            WidgetArea midArea = new WidgetArea(widget, null);
            if (mid == low) {
                if (mid == 0) {
                    return (comparator.locationIndicatesIndexFollowingWidget(
                            midArea, location)) ? high : mid;
                } else {
                    return high;
                }
            }
            if (midArea.getBottom() < location.getTop()) {
                low = mid;
            } else if (midArea.getTop() > location.getTop()) {
                high = mid;
            } else if (midArea.getRight() < location.getLeft()) {
                low = mid;
            } else if (midArea.getLeft() > location.getLeft()) {
                high = mid;
            } else {
                return (comparator.locationIndicatesIndexFollowingWidget(
                        midArea, location)) ? mid + 1 : mid;
            }
        }
    }

    /**
     * Gets an element's CSS based 'border-left-width' in pixels or
     * <code>0</code> (zero) when the element is hidden.
     * 
     * @param elem
     *            the element to be measured
     * @return the width of the left CSS border in pixels
     */
    public static int getBorderLeft(Element elem) {
        return impl.getBorderLeft(elem);
    }

    /**
     * Gets an element's CSS based 'border-top-widget' in pixels or
     * <code>0</code> (zero) when the element is hidden.
     * 
     * @param elem
     *            the element to be measured
     * @return the width of the top CSS border in pixels
     */
    public static int getBorderTop(Element elem) {
        return impl.getBorderTop(elem);
    }

    /**
     * Gets an element's client height in pixels or <code>0</code> (zero) when
     * the element is hidden. This is equal to offset height minus the top and
     * bottom CSS borders.
     * 
     * @param elem
     *            the element to be measured
     * @return the element's client height in pixels
     */
    public static int getClientHeight(Element elem) {
        return impl.getClientHeight(elem);
    }

    public static int getClientWidth(Element elem) {
        return impl.getClientWidth(elem) - getHorizontalMargin(elem) - impl.getHorizontalBorders(elem);
    }

    public static String getEffectiveStyle(Element elem, String styleName) {
        return impl.getEffectiveStyle(elem, styleName);
    }

    public static int getHorizontalBorders(Widget widget) {
        return impl.getHorizontalBorders(widget);
    }

    public static int getHorizontalBorders(Element element) {
        return impl.getHorizontalBorders(element);
    }
    
    /**
     * Determine an element's node name via the <code>nodeName</code> property.
     * 
     * @param elem
     *            the element whose node name is to be determined
     * @return the element's node name
     */
    public static String getNodeName(Element elem) {
        return elem.getNodeName();
    }

    public static int getIntPropertyValue(Element element, String name) {
        return impl.getIntProperty(element, name);
    }
    
    public static int getVerticalBorders(Widget widget) {
        return impl.getVerticalBorders(widget);
    }
    
    public static int getVerticalBorders(Element element) {
        return impl.getVerticalBorders(element);
    }

    public static int getVerticalMargin(Element element) {
        return getIntPropertyValue(element, "paddingTop")
                + getIntPropertyValue(element, "paddingBottom");
    }

    public static int getHorizontalMargin(Element element) {
        return getIntPropertyValue(element, "paddingRight")
                + getIntPropertyValue(element, "paddingLeft");
    }

    /**
     * Report a fatal exception via <code>Window.alert()</code> than throw a
     * <code>RuntimeException</code>.
     * 
     * @param msg
     *            the message to report
     * @throws RuntimeException
     *             a new exception based on the provided message
     */
    public static void reportFatalAndThrowRuntimeException(String msg)
            throws RuntimeException {
        msg = "gwt-dnd warning: " + msg;
        Window.alert(msg);
        throw new RuntimeException(msg);
    }

    /**
     * Set the browser's status bar text, if supported and enabled in the client
     * browser.
     * 
     * @param text
     *            the message to use as the window status
     */
    public static void setStatus(String text) {
        Window.setStatus(text);
    }

    /**
     * TODO Change IndexedPanel -> InsertPanel
     */
    @SuppressWarnings("unused")
    private static void debugWidgetWithColor(IndexedPanel parent, int index,
            String color) {
        if (DEBUG) {
            if (index >= parent.getWidgetCount()) {
                debugWidgetWithColor(
                        parent.getWidget(parent.getWidgetCount() - 1), color);
            } else {
                debugWidgetWithColor(parent.getWidget(index), color);
            }
        }
    }

}
