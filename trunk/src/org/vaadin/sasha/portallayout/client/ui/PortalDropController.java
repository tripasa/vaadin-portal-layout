package org.vaadin.sasha.portallayout.client.ui;

import org.vaadin.sasha.portallayout.client.dnd.DragContext;
import org.vaadin.sasha.portallayout.client.dnd.VetoDragException;
import org.vaadin.sasha.portallayout.client.dnd.drop.AbstractPositioningDropController;
import org.vaadin.sasha.portallayout.client.dnd.util.CoordinateLocation;
import org.vaadin.sasha.portallayout.client.dnd.util.DOMUtil;
import org.vaadin.sasha.portallayout.client.dnd.util.LocationWidgetComparator;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Widget;

/**
 * Drop process controller for the portlets.
 * 
 * @author p4elkin
 */
public class PortalDropController extends AbstractPositioningDropController {

    /**
     * Wire frame object that is displayed when portlet is dragged.
     */
    private PortalDropPositioner dummy;

    /**
     * 
     * @return
     */
    public PortalDropPositioner getDummy() {
        return dummy;
    }

    /**
     * Portal.
     */
    private VPortalLayout portal;

    /**
     * Target index for the dropped portlet.
     */
    private int targetDropIndex = -1;

    /**
     * Constructor.
     * 
     * @param dropTarget
     *            Drop Area.
     */
    public PortalDropController(final VPortalLayout portal) {
        super(portal.getContentPanel());
        this.portal = portal;
    }

    /**
     * Get the location comparison policy object. Currently the portlets swap if
     * the target is below the line y = x.
     * 
     * @return Comparsion policy object.
     */
    protected LocationWidgetComparator getLocationWidgetComparator() {
        return LocationWidgetComparator.BOTTOM_RIGHT_COMPARATOR;
    }

    /**
     * When the portlet is dropped, the actual parent portal might change.
     * Anyway the parent should update its data accordingly.
     * 
     * @param portlet
     *            Portlet that is dropped.
     */
    private void updatePortletLocationOnDrop(final Portlet portlet) {
        final VPortalLayout currentParent = portlet.getParentPortal();

        /**
         * Do the the logic required by the new parent to add the new portlet
         */
        if (portal.getChildPosition(portlet) != targetDropIndex) {
            portlet.setParentPortal(portal);
            portal.onPortletPositionUpdated(portlet, targetDropIndex);
        }
        
        /**
         * Do the logic required for the former parent to clean up the trace of
         * the removed portlet
         */
        if (!currentParent.equals(portal))
            currentParent.onPortletMovedOut(portlet);
    }

    /**
     * Updates the current drop panel and the index inside of it.
     * 
     * @param context
     * @return true if the target drop panel was changed.
     */
    private int updateDropPosition(final DragContext context) {
        final CoordinateLocation curLocation = new CoordinateLocation(context.mouseX, context.mouseY);
        int targetDropIndex = DOMUtil.findIntersect(portal.getContentPanel(), curLocation, getLocationWidgetComparator());
        return targetDropIndex;
    }

    /**
     * Get current index of the wire frame object.
     * 
     * @return Dummy object index.
     */
    private int getDummyIndex() {
        return (dummy == null) ? -1 : portal.getChildPosition(dummy);
    }

    /**
     * Create a wire frame object.
     * 
     * @param context
     *            Drop Context.
     * @return New wire frame object.
     */
    protected PortalDropPositioner newPositioner(DragContext context) {
        final Portlet portlet = (Portlet) context.selectedWidgets.get(0);
        if (portlet != null)
            return new PortalDropPositioner(portlet);
        return null;
    }

    @Override
    public void onDrop(DragContext context) {
        super.onDrop(context);
        assert targetDropIndex != -1 : "Should not happen after onPreviewDrop did not veto";
        final Widget widget = context.selectedWidgets.get(0);
        updatePortletLocationOnDrop((Portlet) widget);
        portal.addToRootElement((Portlet) widget, targetDropIndex);
    }

    @Override
    public void onLeave(DragContext context) {
        dummy.removeFromParent();
        dummy = null;
    }

    @Override
    public void onMove(final DragContext context) {
        super.onMove(context);
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                if (dummy == null)
                    return;
                int targetIndex = updateDropPosition(context);
                int dummyIndex = getDummyIndex();
                if (dummyIndex != targetIndex
                        && (dummyIndex != targetIndex - 1 || targetIndex == 0)) {
                    if (dummyIndex == 0 && portal.getChildCount() == 1) {
                        // Do nothing...
                    } else if (targetIndex == -1) {
                        dummy.removeFromParent();
                    } else {
                        portal.addToRootElement(dummy, targetIndex);
                        if (dummyIndex == 0)
                            portal.getChildAt(0).setSpacingValue(0);
                        if (dummyIndex == 1 && portal.getPortletCount() > 1)
                            portal.getChildAt(1).setSpacingValue(portal.getVerticalSpacing());
                    }
                }
            }
        });
    }

    @Override
    public void onEnter(final DragContext context) {
        dummy = newPositioner(context);
        int dummyIndex = updateDropPosition(context);
        portal.onPortletEntered(dummy, dummyIndex);
    }

    @Override
    public void onPreviewDrop(DragContext context) throws VetoDragException {
        super.onPreviewDrop(context);
        targetDropIndex = getDummyIndex();
        if (targetDropIndex == -1)
            throw new VetoDragException();
    }
}