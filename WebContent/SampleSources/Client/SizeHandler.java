package org.vaadin.sasha.portallayout.client.ui;

/**
 * 
 * @author p4elkin
 *
 */
public interface SizeHandler {
  
  /**
   * 
   * @return
   */
  public boolean isHeightRelative();
  
  /**
   * 
   * @return
   */
  public float getRealtiveHeight();
  
  /**
   * 
   * @return
   */
  public int getRequiredHeight();
  
  /**
   * 
   * @param heightValue
   */
  public void setRealtiveHeightValue(float heightValue);
  
  /**
   * 
   * @param width
   * @param height
   */
  public void setPortletSizes(int width, int height);
  
  /**
   * 
   * @param spacing
   */
  public void updateSpacing(int spacing);
}
