/*
 * Stoppable.java
 *
 * Created on 15 ���� 2007 �., 20:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package misc;

import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author RFK
 */
public interface ProgressStoppable {
  
  /** Sets instance of ProgressResponse to update progress information */
  public void setProgressResponse(ProgressResponse progressResponse);
  /** Stops instance of Stoppable
   * return true, if dialog can proceed to backDisplay
   */
  public boolean stopIt();
  
  
}
