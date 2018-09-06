/*
 * Queue.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package misc;

import java.util.Vector;

/**
 *
 * @author Raev
 */
public class Queue extends Vector{
  private int maxElements;
  /** Creates a new instance of Queue */
  public Queue(int maxElements) {
    super();
    this.maxElements=maxElements;
  }

 public void addToQueue(Object elem){
  addElement(elem);
  if (size()>maxElements) {
    removeElementAt(0);
  }
 }
}
