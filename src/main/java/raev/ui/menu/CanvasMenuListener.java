/*
 * CanvasMenuListener.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package raev.ui.menu;

/**
 *
 * @author RFK
 */
public interface CanvasMenuListener {
  
  /** Creates a new instance of CanvasMenuListener */
  public boolean commandCanvasAction(CanvasMenuItem menuItem, byte commandId);
}
