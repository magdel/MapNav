/*
 * CheckTimerTask.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gpspack;

import RPMap.MapCanvas;
import RPMap.RMSOption;
import app.MapForms;
import java.util.TimerTask;
//#debug
import misc.DebugLog;
import misc.MapSound;

/**
 *
 * @author RFK
 */
public class CheckTimerTask extends TimerTask{
  
  /** Creates a new instance of CheckTimerTask */
  public CheckTimerTask() {
  }
  private boolean firstRun=true;
  public void run() {
    if (firstRun) {
      if (GPSReader.lastReadTime>0){
        firstRun=false;
        return;
      }
    }
    if (System.currentTimeMillis()-GPSReader.lastReadTime>13000){
      cancel();

        //#mdebug
      if (RMSOption.debugEnabled)
        DebugLog.add2Log("CTT2 recon");
//#enddebug
      try{
        try{
        MapSound.playSound(MapSound.NEWCONNECTSOUND);//  MapMidlet.playNCSound();
        }finally{
            MapCanvas.map.breakBTGPS();
            //#mdebug
            if (RMSOption.debugEnabled)
              DebugLog.add2Log("CTT2 con br");
//#enddebug        
          }
          }finally{
            try{Thread.sleep(3000);}catch(Throwable t){}
            if (RMSOption.getBoolOpt(RMSOption.BL_VIBRATE_ON))
              MapCanvas.display.vibrate(1000);
            //#mdebug
            if (RMSOption.debugEnabled)
              DebugLog.add2Log("CTT2 con cl");
//#enddebug        
            MapCanvas.map.endGPSLookup(true);
          }
      }
    }
  }
