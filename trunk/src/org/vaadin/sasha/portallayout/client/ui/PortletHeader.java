package org.vaadin.sasha.portallayout.client.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VCaption;

/**
 * Portlet header. Contains the controls for the basic operations with portlet
 * like closing, collapsing and pinning.
 * 
 * @author p4elkin
 */
public class PortletHeader extends ComplexPanel implements Container {    
    
    public static final String CLASSNAME_SUFFIX = "-header";

    private static final String BUTTON_CLOSE_SUFFIX = "close";

    private static final String BUTTON_COLLAPSE_SUFFIX = "collapse";

    private static final String BUTTON_EXPAND_SUFFIX = "expand";
    
    private static final String BUTTON_SUFFIX = "-button";

    private static final String BUTTONBAR_SUFFIX = "-buttonbar";

    private final Element container = DOM.createDiv();
    
    private final Element controlContainer = DOM.createDiv();
    
    private final Portlet parentPortlet;

    private Widget child;
    
    private Button closeButton = new Button();

    private Button collapseButton = new Button();

    private VPortletCaption vcaption;
    
    private ApplicationConnection client;
    
    private boolean closable = true;

    private boolean collapsible = true;

    private Map<String, Button> actionIdToButton = new HashMap<String, Button>();
    
    private Map<String, String> actionIdToIcon = new HashMap<String, String>();
    
    private ClickHandler closeButtonClickHandler = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            parentPortlet.close();
        }
    };

    
    private ClickHandler collapseButtonClickHandler = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            parentPortlet.toggleCollapseState();
        }
    };

    private MouseDownHandler blockingDownHandler = new MouseDownHandler() {

        @Override
        public void onMouseDown(MouseDownEvent event) {
            parentPortlet.blur();
            final NativeEvent nativeEvent = event.getNativeEvent();
            final Element target = nativeEvent.getEventTarget().cast();
            final Widget w = Util.findWidget(target, null);
            if (!(w instanceof HasWidgets)) {
                event.stopPropagation();
            }
        }
    };

    public PortletHeader(final Portlet parent, final ApplicationConnection client) {
        super();
        this.client = client;
        setElement(container);
        vcaption = new VPortletCaption(null, client);
        container.setClassName(getClassName());
        parentPortlet = parent;

        closeButton.addClickHandler(closeButtonClickHandler);
        collapseButton.addClickHandler(collapseButtonClickHandler);
        vcaption.addMouseDownHandler(blockingDownHandler);
        closeButton.addMouseDownHandler(blockingDownHandler);
        collapseButton.addMouseDownHandler(blockingDownHandler);
        
        closeButton.getElement().getStyle().setFloat(Float.RIGHT);
        collapseButton.getElement().getStyle().setFloat(Float.RIGHT);
        add(vcaption, container);
        add(closeButton, controlContainer);
        add(collapseButton, controlContainer);

        
        vcaption.getElement().appendChild(controlContainer);
        controlContainer.getStyle().setVerticalAlign(VerticalAlign.TOP);
        closeButton.setStyleName(getClassName() + BUTTON_SUFFIX);
        closeButton.addStyleDependentName(BUTTON_CLOSE_SUFFIX);
        
        collapseButton.setStyleName(getClassName() + BUTTON_SUFFIX);
        collapseButton.addStyleDependentName(BUTTON_COLLAPSE_SUFFIX);
        controlContainer.setClassName(getClassName() + BUTTONBAR_SUFFIX);
    }
    
    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        vcaption.updateComponentWidth();
    }
    
    public Widget getDraggableArea() {
        return vcaption;
    }

    public static String getClassName() {
        return Portlet.getClassName() + CLASSNAME_SUFFIX;
    }

    public void setClosable(boolean closable) {
        this.closable = closable;
        closeButton.setVisible(closable);
    }

    public void setCollapsible(boolean isCollapsible) {
        this.collapsible = isCollapsible;
        collapseButton.setVisible(isCollapsible);
    }

    public boolean isClosable() {
        return closable;
    }

    public boolean isCollapsible() {
        return collapsible;
    }

    public void updateActions(final Map<String, String> actions) {
        final Set<String> keys = actions.keySet();
        final Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
            final String key = it.next();
            final String icon = actions.get(key);
            final String currentIcon = actionIdToIcon.get(key);
            if (currentIcon == null ||
                !currentIcon.equals(icon)) {
                Button button = actionIdToButton.get(key);
                if (button == null) {
                    button = new Button();
                    button.getElement().getStyle().setFloat(Float.RIGHT);
                    button.addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            parentPortlet.onActionTriggered(key);
                        }
                    });
                    button.addMouseDownHandler(new MouseDownHandler() {
                        
                        @Override
                        public void onMouseDown(MouseDownEvent event) {
                            event.stopPropagation();
                            
                        }
                    });
                    add(button, controlContainer);
                }
                button.getElement().getStyle().setBackgroundImage("url("+ icon +")");
                button.setStyleName(getClassName() + BUTTON_SUFFIX);
                actionIdToButton.put(key, button);
                actionIdToIcon.put(key, icon);
            }
        }
        for (final String id : actionIdToIcon.keySet()) {
            if (!actions.containsKey(id)) {
                final Button b = actionIdToButton.get(id);
                if (b != null) {
                    b.removeFromParent();
                    orphan(b);
                }
                actionIdToButton.remove(id);
                actionIdToIcon.remove(id);
            }
        }
    }

    @Override
    protected void onAttach() {
        super.onAttach();
    }
    
    public void setHeaderWidget(Widget widget) {
        replaceChildComponent(child, widget);
    }
    
    public void updateCaption(UIDL uidl) {
        vcaption.updateCaption(uidl);
        vcaption.updateComponentWidth();
    }

    public void toggleCollapseStyles(boolean isCollapsed) {
        collapseButton.removeStyleDependentName(isCollapsed ? BUTTON_COLLAPSE_SUFFIX : BUTTON_EXPAND_SUFFIX);
        collapseButton.addStyleDependentName(isCollapsed ?  BUTTON_EXPAND_SUFFIX : BUTTON_COLLAPSE_SUFFIX);
    }
    
    private class VPortletCaption extends VCaption {
        
        public VPortletCaption(Paintable component, ApplicationConnection client) {
            super(component, client);
            getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        }

        public void updateComponentWidth() {
            controlContainer.getStyle().setWidth(PortletHeader.this.getOffsetWidth() - vcaption.getRequiredWidth() - 4, Unit.PX);
            if (child != null) {
                client.handleComponentRelativeSize(child);
            }
        }
        
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {/*No server side correspondence*/}

    @Override
    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
        if (oldComponent == child) {
            if (oldComponent != null && child != newComponent) {
                remove(child);
            }
            child = newComponent;
            child.getElement().getStyle().setFloat(Float.RIGHT);
            child.getElement().getStyle().setMarginRight(10, Unit.PX);
            add(newComponent, controlContainer);
            newComponent.addDomHandler(blockingDownHandler, MouseDownEvent.getType());
            vcaption.updateComponentWidth();
        }
    }

    @Override
    public boolean hasChildComponent(Widget component) {
        return child == component;
    }

    @Override
    public void updateCaption(Paintable component, UIDL uidl) {/*NOP*/}

    @Override
    public boolean requestLayout(Set<Paintable> children) {
        vcaption.updateComponentWidth();
        return false;
    }

    @Override
    public RenderSpace getAllocatedSpace(Widget child) {
        if (child == this.child) {
            int buttonSumWidth = 0;
            for (final Button b : actionIdToButton.values()) {
                buttonSumWidth += b.getOffsetWidth();
            }
            buttonSumWidth += closeButton.getOffsetWidth();
            buttonSumWidth += collapseButton.getOffsetWidth();
            return new RenderSpace(controlContainer.getOffsetWidth() - buttonSumWidth, getOffsetHeight());
        }
        return null;
    }

}
