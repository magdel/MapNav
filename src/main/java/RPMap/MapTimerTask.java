/*
 * MapTimerTask.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package RPMap;

import java.util.TimerTask;
//#debug
//# import misc.DebugLog;
import raev.ui.menu.CanvasMenu;

/**
 *
 * @author RFK
 */
public class MapTimerTask extends TimerTask {
  private int newLevel;
  private double lat;
  private double lon;
  byte showMapSerDisp;
  byte showMapViewDisp;
  byte userMapIndex;
  private byte kind;
  private static final byte LOCKIND = 0;
  private static final byte GPSKIND = 1;
  private static final byte GPSDELAYKIND = 2;
  private static final byte CANVASKEYKIND = 3;
  
  /** Creates a new instance of MapTimerTask */
  public MapTimerTask(int newLevel,double lat, double lon,byte showMapSerDisp, byte showMapViewDisp, byte userMapIndex) {
    super();
    this.newLevel=newLevel;
    this.lat=lat;
    this.lon=lon;
    this.showMapSerDisp=showMapSerDisp;
    this.showMapViewDisp=showMapViewDisp;
    this.userMapIndex=userMapIndex;
    kind=LOCKIND;
  }
  
  /** Creates a new instance of MapTimerTask for GPS connect*/
  public MapTimerTask() {
    super();
    kind=GPSKIND;
  }
  
  CanvasMenu cm;
  int key2Fire;
  public MapTimerTask(CanvasMenu cm, int keyCode) {
    super();
    kind=CANVASKEYKIND;
    this.cm=cm;
    key2Fire=keyCode;
  }
  private long delay;
  private long started;
  public long left2Reconnect;
  public MapTimerTask(long delay) {
    super();
    kind=GPSDELAYKIND;
    this.delay=delay;
    started=System.currentTimeMillis();
    left2Reconnect=delay;
  }
  
  public void run() {
    //MapCanvas.map.clearAllTiles(); or else no our scaling
    if (kind==LOCKIND){
      boolean ct=((MapCanvas.map.showMapView!=showMapViewDisp)||(MapCanvas.map.showMapSer!=showMapSerDisp)||(MapCanvas.map.userMapIndexUsed!=userMapIndex));
      //boolean ct=(!RMSOption.scaleMap)&&((MapCanvas.map.level==newLevel)||(MapCanvas.map.userMapIndex!=userMapIndex));
      MapCanvas.map.showMapSer=showMapSerDisp;
      MapCanvas.map.showMapView=showMapViewDisp;
      MapCanvas.map.setLocation(lat,lon,newLevel);
      MapCanvas.map.setUserMapIndex(userMapIndex,false);
      if (ct) MapCanvas.map.clearAllTiles();
      MapCanvas.map.repaint();
      cancel();
    } else if (kind==CANVASKEYKIND){
      cm.fireKey(key2Fire);
      //MapCanvas.map.endGPSLookup(true);
      cancel();
    } else if (kind==GPSKIND){
      MapCanvas.map.startAutoGPSLookup();
      //MapCanvas.map.endGPSLookup(true);
      cancel();
    }else if (kind==GPSDELAYKIND){
      left2Reconnect=started+delay-System.currentTimeMillis();
      if (left2Reconnect<=0) {
           //#mdebug
//#             if (RMSOption.debugEnabled)
//#               DebugLog.add2Log("GPSD restart");
//#enddebug
        MapCanvas.map.breakBTGPS();
        
        MapCanvas.map.startAutoGPSLookup();
      }
      MapCanvas.map.repaint();
    }
  }
  
}
