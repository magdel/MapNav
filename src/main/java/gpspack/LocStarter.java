/*
 * LocStarter.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gpspack;

import javax.microedition.io.StreamConnection;
import RPMap.RMSOption;
import RPMap.MapCanvas;
//#debug
//# import misc.DebugLog;

/**
 *
 * @author RFK
 */
public class LocStarter {
  private static BTCanvas gpsCanvas;
  /** Creates a new instance of LocStarter */
  public LocStarter() {
  }
  public static void start(GPSReader gpsReader){
    LocIntMedium.startInternal(gpsReader);
  }

  public static void makeAuthenticationFeedback(StreamConnection conn){
    BTAuthenticator bta = new BTAuthenticator(conn);
  } 
  public static void showSearchBlueGPS(boolean autoSearch, boolean gps){
//#debug
//#     byte dp=0;
    try{
      stop();
      gpsCanvas = new BTCanvas(gps);
//#debug
//#       dp=1;
      if(!autoSearch) gpsCanvas.setCommandListener(gpsCanvas);
//#debug
//#       dp=2;
      if (autoSearch){
        gpsCanvas.autoSrch=true;
//#debug
//#         dp=3;
        gpsCanvas.startSrch();
      } else
        MapCanvas.setCurrent(gpsCanvas);
    } catch (Throwable e){
//#mdebug
//#       if (RMSOption.debugEnabled)
//#         DebugLog.add2Log("LS Blue GPS:"+e+"\n"+String.valueOf(dp));
//#enddebug
    }
  }
  
  public static void stop(){
    try{if (gpsCanvas!=null) gpsCanvas.stop();}catch(Throwable t){}
    gpsCanvas=null;
  }
}
