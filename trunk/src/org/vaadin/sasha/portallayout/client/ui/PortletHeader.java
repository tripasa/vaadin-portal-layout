package org.vaadin.sasha.portallayout.client.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VCaption;

/**
 * Portlet header. COntains the controls for the basic operations with portlet
 * like closing, collapsing and pinning.
 * 
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
    private static final String BUTTON_CLOSE_SUFFIX = "close";

    /**
     * Class name suffix for the collapse button
     */
    private static final String BUTTON_COLLAPSE_SUFFIX = "collapse";

    /**
     * Class name suffix for the collapse button
     */
    private static final String BUTTON_EXPAND_SUFFIX = "expand";
    
    /**
     * Class name suffix for the collapse button
     */
    private static final String BUTTON_SUFFIX = "-button";

    /**
     * Class name suffix for the button bar.
     */
    private static final String BUTTONBAR_SUFFIX = "-buttonbar";

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
    private VCaption vcaption;
    
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
    private Map<String, Button> actionIdToButton = new HashMap<String, Button>();
    
    /**
     * 
     */
    private Map<String, String> actionIdToIcon = new HashMap<String, String>();
    
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
            vcaption.getElement().focus();
            parentPortlet.blur();
        }
    };

    /**
     * Constructor.
     * 
     * @param parent
     *            Portlet to which this header belongs.
     */
    public PortletHeader(final Portlet parent, final ApplicationConnection client) {
        super();
        setElement(captionWrapper);
        vcaption = new VCaption(null, client);
        vcaption.addStyleName("caption");
        captionWrapper.setClassName(getClassName());
        captionWrapper.appendChild(buttonBar);
        parentPortlet = parent;

        closeButton.addClickHandler(closeButtonClickHandler);
        collapseButton.addClickHandler(collapseButtonClickHandler);
        vcaption.addMouseDownHandler(mouseDownHandler);

        add(vcaption, (com.google.gwt.user.client.Element) captionWrapper);
        add(collapseButton, (com.google.gwt.user.client.Element) buttonBar);
        add(closeButton, (com.google.gwt.user.client.Element) buttonBar);

        closeButton.setStyleName(getClassName() + BUTTON_SUFFIX);
        closeButton.addStyleDependentName(BUTTON_CLOSE_SUFFIX);
        
        collapseButton.setStyleName(getClassName() + BUTTON_SUFFIX);
        collapseButton.addStyleDependentName(BUTTON_COLLAPSE_SUFFIX);
        buttonBar.setClassName(getClassName() + BUTTONBAR_SUFFIX);
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        getElement().getStyle().getWidth();
        setWidth(width);
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
                    button.addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            parentPortlet.onActionTriggered(key);
                        }
                    });
                    insert(button, (com.google.gwt.user.client.Element) buttonBar, 0, true);
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

    public void updateCaption(UIDL uidl) {
        vcaption.updateCaption(uidl);
    }

    public void toggleCollapseStyles(boolean isCollapsed) {
        collapseButton.removeStyleDependentName(isCollapsed ? BUTTON_COLLAPSE_SUFFIX : BUTTON_EXPAND_SUFFIX);
        collapseButton.addStyleDependentName(isCollapsed ?  BUTTON_EXPAND_SUFFIX : BUTTON_COLLAPSE_SUFFIX);
    }

}
