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

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Object that controls the process of dropping the objects on portals.
 * @author p4elkin
 */
public class PortalDropController extends AbstractPositioningDropController {

  private VPortalLayout portal;
    
  private Widget dummy;
  
  private int targetDropIndex = -1;
  
  public PortalDropController(VPortalLayout dropTarget) {
    super(dropTarget);
    portal = dropTarget;
  }

  protected LocationWidgetComparator getLocationWidgetComparator() {
    return LocationWidgetComparator.BOTTOM_RIGHT_COMPARATOR;
  }

  @Override
  public void onDrop(DragContext context) {
    super.onDrop(context);
    assert targetDropIndex != -1 : "Should not happen after onPreviewDrop did not veto";
    int dropIdx = targetDropIndex;
    for (Widget widget : context.selectedWidgets) {
      if (widget instanceof Portlet)
        updatePortletLocation((Portlet)widget, dropIdx);
      portal.addToRootElement(widget, dropIdx);
      dropIdx = portal.getWidgetIndex(widget) + 1;
    }
  }
  
  private void updatePortletLocation(Portlet portlet, int dropIdx) {
    final VPortalLayout currentParent = portlet.getParentPortal();
    
    /**
     * Do the logic required for the former parent to clean up the 
     * trace of the removed portlet
     */
    if (!currentParent.equals(portal))
      currentParent.onPortletMovedOut(portlet);
    
    /**
     * Do the the logic required by the new parent to add
     * the new portlet
     */
    if (portal.getWidgetIndex(portlet) != dropIdx)
    {
      portlet.setParentPortal(portal);
      portal.handlePortletPositionUpdated(portlet, dropIdx);
    }
  }

  @Override
  public void onEnter(DragContext context) {
    super.onEnter(context);
    dummy = newPositioner(context);
    portal.addToRootElement(dummy, updateDropPosition(context));
    portal.recalculateLayoutAndPortletSizes();
  }
  
  /**
   * Updates the current drop panel and the index inside of it.
   * @param context
   * @return true if the target drop panel was changed.
   */
  private int updateDropPosition(final DragContext context) {    
    
    int targetDropIndex = DOMUtil.findIntersect(portal, new CoordinateLocation(context.mouseX,
        context.mouseY), getLocationWidgetComparator());
    return targetDropIndex;
  }

  @Override
  public void onLeave(DragContext context) {
    super.onLeave(context);
    dummy.removeFromParent();
    dummy = null;
    portal.recalculateLayoutAndPortletSizes();
  }
  
  @Override
  public void onMove(DragContext context) {
    super.onMove(context);
    
    int targetIndex = updateDropPosition(context);
    
    int dummyIndex = getDummyIndex(); 
    
    if (dummyIndex != targetIndex && 
            (dummyIndex != targetIndex - 1 || 
                targetIndex == 0)) {
      if (dummyIndex == 0 && 
          portal.getWidgetCount() == 1) {
        // Do nothing...
      } else if (targetIndex == -1) {
        dummy.removeFromParent();
      } else {
        portal.addToRootElement(dummy, targetIndex);
      }
    }
  }
  
  private int getDummyIndex() {
    return (dummy == null) ? 
        -1 : portal.getWidgetIndex(dummy);
  }

  @Override
  public void onPreviewDrop(DragContext context) throws VetoDragException {
    super.onPreviewDrop(context);
    targetDropIndex = getDummyIndex();
    if (targetDropIndex == -1)
      throw new VetoDragException();
  }
  
  protected Widget newPositioner(DragContext context) {
    
    final Portlet portlet = (Portlet)context.selectedWidgets.get(0);
    if (portlet == null)
      return null;
    final Widget result = new PortalDropPositioner(portlet); 
    RootPanel.get().add(result, -500, -500);
    return result;
 }

}
