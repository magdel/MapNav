/*
 * GeoCacheLoad.java
 *
 * Created on 19 ������� 2007 �., 4:06
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package misc;

import RPMap.MapCanvas;
import RPMap.MapPoint;
import RPMap.MapRoute;
import RPMap.MapUtil;
import RPMap.RMSOption;
import gpspack.GPSReader;
import java.io.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import lang.Lang;
import lang.LangHolder;

/**
 *
 * @author RFK
 */
public class GeoCacheLoad implements Runnable,ProgressStoppable {
  Displayable backScreen;
  GeoCacheLoad gcl;
  MapRoute route;
  private byte[] buffer;
  private int bufPos;
  //http://www.geocaching.su/?pn=101&cid=2618
  /** Creates a new instance of GeoCacheLoad */
  public GeoCacheLoad(Displayable bs) {
    backScreen=bs;
    gcl=this;
    buffer = new byte[500];
    Thread t = new Thread(this);
    t.start();
  }
  public boolean stopIt(){
    stopped=true;
            return true;

  }
  private boolean stopped;
  
  public void run() {
    InputStream is = null;
    HttpConnection c;
    //#debug
    int tracePos=1;
    
    try {
      route = new MapRoute(MapRoute.WAYPOINTSKIND);
      route.geoInfo=MapRoute.GEOINFO_GEOCACHE;
      route.name="GC"+MapUtil.trackNameAuto();
      double lat,lon;
      if ((GPSReader.NUM_SATELITES>0)&&(MapCanvas.gpsBinded)) {
        lat=GPSReader.LATITUDE;lon=GPSReader.LONGITUDE;
      } else {
        lat=MapCanvas.map.reallat;lon=MapCanvas.map.reallon;
      }
      String  fn=MapUtil.homeSiteURL+"cgi-bin/georu.pl?lat="+String.valueOf(lat)+"&lon="+String.valueOf(lon);
      //#debug
      tracePos=2;
      c = (HttpConnection)Connector.open(fn);
      c.setRequestMethod(HttpConnection.GET);
      is = c.openInputStream();
      //#debug
      tracePos=3;
      
      bufPos=0;
      try {
        int rb;
        do {
          rb=is.read();
          if (stopped) {
            try{ if (is != null) is.close();}catch(Throwable t){}
            try{ c.close();}catch(Throwable t){}
            backScreen=null;
            gcl=null;
            return;
          }
          if (rb<0) break;
          if(rb == 10) {
            if (bufPos>10)
              if(buffer[0] == 36)
                parseInfo(Util.byteArrayToString(buffer,0,bufPos,true));
                //parseInfo(MapUtil.fromBytesUTF8(buffer,bufPos));
            bufPos=0;
          } else {
            buffer[bufPos]=(byte)rb;
            bufPos++;
          }
        } while ((rb >-1));
      } finally {
        try{ if (is != null) is.close();}catch(Throwable t){}
        try{ c.close();}catch(Throwable t){}
      }
      if (stopped) {
        backScreen=null;
        gcl=null;
        return;
      }
      //#debug
      tracePos=4;
      MapCanvas.map.setRoute(route);
      MapCanvas.setCurrentMap();
    } catch (Throwable e) {
//#mdebug
      if (RMSOption.debugEnabled)
        DebugLog.add2Log("GC:"+String.valueOf(tracePos)+" "+e.toString());
//#enddebug
      MapCanvas.showmsgmodal(LangHolder.getString(Lang.load),e.toString(),AlertType.ERROR, backScreen);
    };
    backScreen=null;
    gcl=null;
  }
  
  final private static int OKSTATUS = 1;
  //final public static int AUTHERRORSTATUS = 2;
  final private static int STARTSTATUS = 3;
  final private static int ERRORSTATUS = 4;
  
  public int serviceStatus=STARTSTATUS;
  public String serviceDesc="Connecting...";
  
  private void parseInfo(String ns) {
    // $NR,MagDel,59.7,29.2,45.3,43.7,113.5,64534346
    if(ns.length()>10){
      String s=null;
      s=ns;
      //if (ns.charAt(ns.length())=='\n') s=ns.substring(0,ns.length()-4);
      
      String DATA_STRING = s.toString().substring(3, s.length());
      String DATA_TYPE = s.toString().substring(1, 3);
      String[] data;
      // Check the gps data type and convert the information to a more readable format.
      if(DATA_TYPE.compareTo("GC") == 0){
        try{
          data = MapUtil.parseString(DATA_STRING,',');
          MapPoint mp = new MapPoint(Double.parseDouble(data[1]),Double.parseDouble(data[2]),0,data[0]);
          mp.pointSymbol=(byte)Integer.parseInt(data[3]);
          try{mp.ts=Integer.parseInt(data[4]);}catch(Throwable t){mp.ts=2618;};
          route.addMapPoint(mp);
          serviceStatus=OKSTATUS;
          serviceDesc="OK";
        }catch(Throwable t) {
          //#debug
          if (RMSOption.debugEnabled) DebugLog.add2Log("GC parse:"+t.toString());
        }
      } else
        if(DATA_TYPE.compareTo("GW") == 0){
        try{
          data = MapUtil.parseString(DATA_STRING,',');
          serviceStatus= ERRORSTATUS;
          serviceDesc=data[0];
        }catch(Throwable t) {}
        }
    }
  }

  public void setProgressResponse(ProgressResponse progressResponse) {
  }
  
}
