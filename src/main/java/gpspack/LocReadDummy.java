/*
 * LocReadDummy.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gpspack;

/**
 *
 * @author RFK
 */
public class LocReadDummy extends LocReaderInt {
  int a;
  /** Creates a new instance of LocReadDummy */
  public LocReadDummy() {
    a=5;
  }

  public void stop() {
    a=3;
  }
  
}
