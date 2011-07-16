package org.vaadin.sasha.portallayout.client.ui;

/**
 * 
 * @author p4elkin
 *
 */
public interface RealtiveHeightCapable {
  
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
  public void setSizes(int width, int height);
}
