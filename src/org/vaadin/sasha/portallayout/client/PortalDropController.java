package org.vaadin.sasha.portallayout.client;

import org.vaadin.sasha.portallayout.client.dnd.DragContext;
import org.vaadin.sasha.portallayout.client.dnd.VetoDragException;
import org.vaadin.sasha.portallayout.client.dnd.drop.AbstractPositioningDropController;
import org.vaadin.sasha.portallayout.client.dnd.util.CoordinateLocation;
import org.vaadin.sasha.portallayout.client.dnd.util.DOMUtil;
import org.vaadin.sasha.portallayout.client.dnd.util.LocationWidgetComparator;
import org.vaadin.sasha.portallayout.client.ui.PortalDropPositioner;
import org.vaadin.sasha.portallayout.client.ui.Portlet;
import org.vaadin.sasha.portallayout.client.ui.VPortalLayout;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
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
  private Widget dummy;

  /**
   * Target index for the dropped portlet.
   */
  private int targetDropIndex = -1;

  /**
   * Constructor.
   * 
   * @param dropTarget
   *          Drop Area.
   */
  public PortalDropController(Panel dropTarget) {
    super(dropTarget);
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
   * When the portlet is dropped, the actual parent portal might change. Anyway
   * the parent should update its data accordingly.
   * 
   * @param portlet
   *          Portlet that is dropped.
   */
  private void updatePortletLocationOnDrop(final Portlet portlet) {
    final VPortalLayout dropTargetPortal = getDropTargetAsPortalLayout();
    final VPortalLayout currentParent = portlet.getParentPortal();

    /**
     * Do the logic required for the former parent to clean up the trace of the
     * removed portlet
     */
    if (!currentParent.equals(dropTargetPortal))
      currentParent.onPortletMovedOut(portlet);

    /**
     * Do the the logic required by the new parent to add the new portlet
     */
    if (dropTargetPortal.getWidgetIndex(portlet) != targetDropIndex) {
      portlet.setParentPortal(dropTargetPortal);
      dropTargetPortal.onPortletPositionUpdated(portlet, targetDropIndex);
    }
  }

  /**
   * Updates the current drop panel and the index inside of it.
   * 
   * @param context
   * @return true if the target drop panel was changed.
   */
  private int updateDropPosition(final DragContext context) {

    int targetDropIndex = DOMUtil.findIntersect(getDropTargetAsPortalLayout(),
        new CoordinateLocation(context.mouseX, context.mouseY),
        getLocationWidgetComparator());
    return targetDropIndex;
  }

  /**
   * Get current index of the wire frame object.
   * 
   * @return Dummy object index.
   */
  private int getDummyIndex() {
    return (dummy == null) ? -1 : getDropTargetAsPortalLayout().getWidgetIndex(
        dummy);
  }

  /**
   * Get drop area casted to VPortalLayout.
   * 
   * @return Target PortalLayout.
   */
  private VPortalLayout getDropTargetAsPortalLayout() {
    return VPortalLayout.class.cast(getDropTarget());
  }

  /**
   * Create a wire frame object.
   * 
   * @param context
   *          Drop Context.
   * @return New wire frame object.
   */
  protected Widget newPositioner(DragContext context) {

    final Portlet portlet = (Portlet) context.selectedWidgets.get(0);
    if (portlet == null)
      return null;
    final Widget result = new PortalDropPositioner(portlet, this);
    RootPanel.get().add(result, -500, -500);
    return result;
  }

  @Override
  public void onDrop(DragContext context) {
    super.onDrop(context);
    assert targetDropIndex != -1 : "Should not happen after onPreviewDrop did not veto";
    final VPortalLayout portal = getDropTargetAsPortalLayout();
    final Widget widget = context.selectedWidgets.get(0);
    if (widget instanceof Portlet) {
      updatePortletLocationOnDrop((Portlet) widget);
      portal.addToRootElement(widget, targetDropIndex);
    }
  }

  @Override
  public void onLeave(DragContext context) {
    super.onLeave(context);
    dummy.removeFromParent();
    dummy = null;
    getDropTargetAsPortalLayout().recalculateLayoutAndPortletSizes();
  }

  @Override
  public void onMove(DragContext context) {
    super.onMove(context);

    final VPortalLayout portal = getDropTargetAsPortalLayout();

    int targetIndex = updateDropPosition(context);

    int dummyIndex = getDummyIndex();

    // int spacing = portal.getSpacingInfo().vSpacing;
    // System.out.println("Spacing" + spacing);
    // dummy.getElement().getStyle().setPropertyPx("marginTop", targetIndex == 0
    // ? 0 : spacing);

    if (dummyIndex != targetIndex
        && (dummyIndex != targetIndex - 1 || targetIndex == 0)) {
      if (dummyIndex == 0 && portal.getWidgetCount() == 1) {
        // Do nothing...
      } else if (targetIndex == -1) {
        dummy.removeFromParent();
      } else {
        portal.addToRootElement(dummy, targetIndex);
      }
    }
  }

  @Override
  public void onEnter(DragContext context) {
    super.onEnter(context);
    dummy = newPositioner(context);
    final VPortalLayout portal = getDropTargetAsPortalLayout();
    portal.addToRootElement(dummy, updateDropPosition(context));
    portal.recalculateLayoutAndPortletSizes();
  }

  @Override
  public void onPreviewDrop(DragContext context) throws VetoDragException {
    super.onPreviewDrop(context);
    targetDropIndex = getDummyIndex();
    if (targetDropIndex == -1)
      throw new VetoDragException();
  }
}
