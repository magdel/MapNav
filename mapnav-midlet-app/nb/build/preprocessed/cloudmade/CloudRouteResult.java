/*
 * KMLSearchResult.java
 *
 * Created on 20.08.2010
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cloudmade;

import RPMap.HTTPMapRouteLoader;
import kml.*;
import RPMap.MapCanvas;
import RPMap.MapPoint;
import RPMap.MapRoute;
import RPMap.MapRouteLoader;
import RPMap.MapUtil;
import RPMap.RMSOption;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.List;
import lang.LangHolder;
import misc.HTTPUtils;
import misc.ProgressResponse;
import misc.ProgressStoppable;
//#debug
import misc.DebugLog;
import misc.Util;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 *
 * @author RFK
 */
public class CloudRouteResult implements Runnable, ProgressStoppable {
  String place;
  public static Vector kmlPoints;
  List resultList;
  
  
 private MapPoint from;
 private MapPoint to;
  public CloudRouteResult(MapPoint from, MapPoint to) {
    this.from=from;
    this.to=to;
    (new Thread(this)).start();
  }
  
  private void readResult(InputStream inputStream){
    try{
      
      
    } catch (Exception e) {
      //#debug
      e.printStackTrace();
    }
  }
  
  
  KMLParser kmlP;
  public void run() {
    String url=MapUtil.emptyString;
    try{
        
       // url="http://maps.google.com/maps?f=d&source=s_d&saddr="+from.lat+","+from.lon+
       //   "&daddr="+to.lat+","+to.lon
       //     +"&output=kml";
        //47.25976,9.58423,47.26117,9.59882/car/shortest.gpx
        String lang = (LangHolder.getCurrUiLanguage().equals("RU"))?"ru":"en";

        url="routes.cloudmade.com/be168daeaa8d45c096f20cc963cbcfab/api/0.3/"+from.lat+","+from.lon+","+to.lat+","+to.lon
            +"/car/fastest.gpx?lang="+lang;


        Thread.sleep(200);
      if (progressResponse!=null){
        progressResponse.setProgress((byte)(20),"Requesting...");
      }

      HTTPMapRouteLoader mrl = new HTTPMapRouteLoader(url, MapRoute.ROUTEKIND,
        MapRouteLoader.CODEPAGEUTF,
        MapRouteLoader.FORMATGPX,
        false);
      mrl.load();
      if (stopped) return;
        MapCanvas.map.activeRoute=mrl.getRoute();
        MapCanvas.setCurrentMap();
      
    }catch(Throwable t){
      //#mdebug
      if (RMSOption.debugEnabled)
        DebugLog.add2Log("KMLR:"+url+" e:"+t);
      //#enddebug
      
      MapCanvas.showmsgmodal("Error","Cloud access error: "+t.getMessage(),AlertType.ERROR, MapCanvas.map);
    }
    progressResponse=null;
    if (kmlPoints!=null)
    if (kmlPoints.size()==0)kmlPoints=null;
  }
  
  ProgressResponse progressResponse;
  public void setProgressResponse(ProgressResponse progressResponse) {
    this.progressResponse=progressResponse;
  }
  private boolean stopped;
  public boolean stopIt() {
    stopped=true;
            return true;

  }
  
//  String s_placemark = "placemark";
//  String s_url = "url";
//  String s_response = "response";
//  String s_description = "description";
//  String s_kml = "kml";
  
}
