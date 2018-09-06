/*
 * CanvasMenuItem.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package raev.ui.menu;

import misc.MVector;

/**
 *
 * @author RFK
 */
public class CanvasMenuItem{
  public String caption;
  MVector items = new MVector();
  byte commandId;
  int key;
  boolean higlighted;
  /** Creates a new instance of CanvasMenuItem */
  public CanvasMenuItem(String caption, byte commandId, int key) {
    this.caption=caption;
    this.commandId=commandId;
    this.key=key;
  }
  public CanvasMenuItem addItems(CanvasMenuItem item){
    items.addElement(item);
    return this;
  }
  public boolean isFinal(){
    return (items.size()==0);
  }
}
