/*
 * KeyLockTimer.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package misc;

import RPMap.MapCanvas;
import RPMap.RMSOption;
import java.util.TimerTask;

/**
 *
 * @author RFK
 */
public class KeyLockTimer extends TimerTask {
  /** Creates a new instance of KeyLockTimer */
  public KeyLockTimer() {
    //super();
  }
  
  private byte pass;
  public final void run() {
    try {
      if (pass==0) {
        MapCanvas.drawKeyLockSign=true;
        MapCanvas.map.repaint();
        pass++;
      } else {
        MapCanvas.drawKeyLockSign=false;
        MapCanvas.map.repaint();
        cancel();
      }
//#mdebug
//#       if (RMSOption.debugEnabled)
//#         DebugLog.add2Log("LT:"+pass);
//#enddebug
      
    }catch(Throwable e){
//#mdebug
//#       if (RMSOption.debugEnabled)
//#         DebugLog.add2Log("ELT:"+e.toString());
//#enddebug
    }
  }
}
