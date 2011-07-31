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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * Portlet header. COntains the controls for the basic operations with portlet
 * like closing, collapsing and pinning.
 * 
 * @author p4elkin
 */
public class PortletHeader extends ComplexPanel {

    private static class CaptionContainer extends FocusWidget implements HasHTML {
        private String caption;
        
        public CaptionContainer() {
            super(DOM.createDiv());
        }
        
        @Override
        public String getText() {
            return caption;
        }

        @Override
        public void setText(String text) {
            this.caption = text;
            getElement().setInnerHTML(caption == null || caption.isEmpty() ? "&nbsp":caption);
        }

        @Override
        public String getHTML() {
            return getText();
        }

        @Override
        public void setHTML(String html) {
            setText(html);
        }
    }
    
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
    private final CaptionContainer captionHtml = new CaptionContainer();

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
            captionHtml.setFocus(true);
        }
    };

    /**
     * Constructor.
     * 
     * @param parent
     *            Portlet to which this header belongs.
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
        captionHtml.addMouseDownHandler(mouseDownHandler);

        add(captionHtml, (com.google.gwt.user.client.Element) captionWrapper);
        add(collapseButton, (com.google.gwt.user.client.Element) buttonBar);
        add(closeButton, (com.google.gwt.user.client.Element) buttonBar);

        closeButton.setStyleName(getClassName() + BUTTON_CLASSNAME_SUFFIX);
        closeButton.addStyleName(getClassName() + BUTTON_CLASSNAME_SUFFIX
                + CLOSEBUTTON_CLASSNAME_SUFFIX);
        collapseButton.setStyleName(getClassName() + BUTTON_CLASSNAME_SUFFIX);
        collapseButton.addStyleName(getClassName() + BUTTON_CLASSNAME_SUFFIX
                + COLLAPSEBUTTON_CLASSNAME_SUFFIX);

        buttonBar.setClassName(getClassName() + BUTTONBAR_CLASSNAME_SUFFIX);
        captionHtml.setStyleName(getClassName()
                + CAPTIONWRAPPER_CLASSNAME_SUFFIX);
    }

    public void setCaption(final String caption) {
        captionHtml.setText(caption);
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        getElement().getStyle().getWidth();
        setWidth(width);
    }

    public Widget getDraggableArea() {
        return captionHtml;
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

    public String getCaption() {
        return captionHtml.getText();
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
                button.setStyleName(getClassName() + BUTTON_CLASSNAME_SUFFIX);
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

}
