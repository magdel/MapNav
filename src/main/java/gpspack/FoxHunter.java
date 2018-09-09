/*
 * FoxHunter.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gpspack;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;

/**
 *
 * @author RFK
 */
public class FoxHunter extends Thread {
  public int distance=1000;
  public boolean stopped;
  /** Creates a new instance of FoxHunter */
  public FoxHunter() {
  }
  private int getDuration() {
    int d=300;
    if (distance<30)
      d=500;
    else
      if (distance<6)
        d=1000;
    return d;
  }
  private long getDelay() {
    long d;
    if (distance<6)
      d=0;
    else d= distance*4;
    if (d>1000) d=1000;
    return d;
  }
  public void run() {
    try{
      try{
        while (!stopped) {
          if (distance<1000)
            if (distance<6)
              Manager.playTone(81, getDuration(), 100);
            else
              if (distance<30)
                Manager.playTone(80, getDuration(), 100);
              else
                Manager.playTone(78, getDuration(), 100);
          Thread.sleep(getDuration());
          Thread.sleep(getDelay());
        }
      } catch(MediaException _ex) {
        return;}
    } catch(InterruptedException _ex) {
      return;
      
    }
  }
}
