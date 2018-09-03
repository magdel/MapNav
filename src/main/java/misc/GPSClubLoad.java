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
import RPMap.RMSSettings;
import gpspack.GPSReader;
import java.io.*;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import lang.Lang;
import lang.LangHolder;

/**
 *
 * @author RFK
 */
public class GPSClubLoad implements Runnable,ProgressStoppable {
  Displayable backScreen;
  GPSClubLoad gcl;
  MapRoute route;
  private byte[] buffer;
  private int bufPos;
  //http://www.geocaching.su/?pn=101&cid=2618
  /** Creates a new instance of GeoCacheLoad */
  public GPSClubLoad(Displayable bs) {
    backScreen=bs;
    gcl=this;
    buffer = new byte[500];
    if (iconsGpsClub==null)
      iconsGpsClub = new Hashtable();
    Thread t = new Thread(this);
    t.start();
  }
  public boolean stopIt(){
    stopped=true;
            return true;

  }
  private boolean stopped;
  
  public static void getGPSClub(){
    MapCanvas.setCurrent(new ProgressForm(LangHolder.getString(Lang.loading),"gps-club.ru",new GPSClubLoad(MapCanvas.map),MapCanvas.map));
  }
  
  public void run() {
    InputStream is = null;
    HttpConnection c;
    //#debug
//#     int tracePos=1;
    ic_lq = new Vector();
    try {
      route = new MapRoute(MapRoute.WAYPOINTSKIND);
      route.gpsSystem = MapRoute.ICONS_GPSCLUB;
      route.geoInfo=MapRoute.GEOINFO_GPSCLUB;
//      http://poi.gps-club.ru/mapnav.php?tl=54.00,35.00&br=56.00,37.00&sum=20&cp=tr
//MagDel, ����� �������� ������.
//http://poi.gps-club.ru/mapnav.php?tl=54.00...um=50&cp=tr
//��� tl - ����� ������� ����, br - ������������� ������ ������.
//���������� ����� ����������� ����� �������.
//sum - ���������� ���������� �����.
//�������� ������� � ��������� ���������� � ����.
//cp - ��������� ������. ���������� ��������
//tr - ��������� ���������� � �������� (�� �����),
///utf - �������������� � ���-8, ��� ��������� - cp ����� �������� � ��������� cp-1251.
      //double tl = MapUtil.
      int mx,my;
      
      if ((GPSReader.NUM_SATELITES>0)&&(MapCanvas.gpsBinded)) {
        mx=MapUtil.getXMap(GPSReader.LONGITUDE,MapCanvas.map.level);
        my=MapUtil.getYMap(GPSReader.LATITUDE,MapCanvas.map.level);
      } else {
        mx=MapUtil.getXMap(MapCanvas.reallon,MapCanvas.map.level);
        my=MapUtil.getYMap(MapCanvas.reallat,MapCanvas.map.level);
      }
      int mxt=mx-MapCanvas.dmaxx;
      int mxb=mx+MapCanvas.dmaxx;
      int myt=my+MapCanvas.dmaxy;
      int myb=my-MapCanvas.dmaxy;
      
      String fn="http://poi.gps-club.ru/mapnav.php?tl="+MapUtil.getLat(myt,MapCanvas.map.level)+","+
          MapUtil.getLon(mxt,MapCanvas.map.level)+"&&br="+MapUtil.getLat(myb,MapCanvas.map.level)+","+
          MapUtil.getLon(mxb,MapCanvas.map.level)+"&sum=40&cp=utf";
      
      //fn="http://poi.gps-club.ru/mapnav.php?tl=54.00,35.00&br=56.00,37.00&sum=50&cp=tr";
      //#debug
//#       tracePos=2;
      c = (HttpConnection)Connector.open(fn);
      c.setRequestMethod(HttpConnection.GET);
      is = c.openInputStream();
      //#debug
//#       tracePos=3;
      
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
      MapCanvas.inetAvailable=true;
      
      //#debug
//#       tracePos=4;
      MapCanvas.map.setRoute(route);
      MapCanvas.setCurrentMap();
      loadqueue();
    } catch (Throwable e) {
//#mdebug
//#       if (RMSOption.debugEnabled)
//#         DebugLog.add2Log("GC:"+String.valueOf(tracePos)+" "+e.toString());
//#enddebug
      MapCanvas.showmsgmodal(LangHolder.getString(Lang.load),e.toString(),AlertType.ERROR, backScreen);
    }
    ic_lq=null;
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
    //$POI,Lezhachie policzejskie,55.425846099853516,36.649787902832031,3169,51
    if(ns.length()>10){
      String s=ns;
      //if (ns.charAt(ns.length())=='\n') s=ns.substring(0,ns.length()-4);
      
      String DATA_STRING = s.toString().substring(4, s.length());
      String DATA_TYPE = s.toString().substring(1, 4);
      String[] data;
      // Check the gps data type and convert the information to a more readable format.
      if(DATA_TYPE.compareTo("POI") == 0){
        try{
          data = MapUtil.parseString(DATA_STRING,',');
          MapPoint mp = new MapPoint(Double.parseDouble(data[1]),Double.parseDouble(data[2]),0,data[0]);
          try{mp.pointSymbol = (short)(Integer.parseInt(data[4]));}catch(Throwable t){mp.pointSymbol=1;}
          //=(byte)Integer.parseInt(data[3]);
          try{mp.ts=Integer.parseInt(data[3]);}catch(Throwable t){mp.ts=2618;}
          route.addMapPoint(mp);
          
          Image im = (Image) iconsGpsClub.get(mp);
          if (im==null){
            // add to load queue
            ic_lq.addElement(mp);
          }
          
          serviceStatus=OKSTATUS;
          serviceDesc="OK";
        }catch(Throwable t) {
          //#debug
//#           if (RMSOption.debugEnabled) DebugLog.add2Log("GC parse:"+t.toString());
        }
      }
//      else
//        if(DATA_TYPE.compareTo("GW") == 0){
//        try{
//          data = MapUtil.parseString(DATA_STRING,',');
//          serviceStatus= ERRORSTATUS;
//          serviceDesc=data[0];
//        }catch(Throwable t) {}
//        }
    }
  }
  
  private void loadqueue(){
    MapPoint mp;String s;
    String[] data;
    HTTPUtils hr;
    for (int i=ic_lq.size()-1;i>=0;i--){
      mp = (MapPoint) ic_lq.elementAt(i);
      try{
        Image img;
        byte [] pb = RMSSettings.loadGeoData(mp.pointSymbol, RMSSettings.GEODATA_GPSCLUB);
        if (pb!=null)
          try{
            img=Image.createImage(pb,0, pb.length);//Image.createImage(is);
            iconsGpsClub.put(mp,img);
            MapCanvas.map.repaint(); 
            continue;
          }catch(Throwable t){}
        
        //http://poi.gps-club.ru/mapnav.php?icon=35&cp=utf
        s=HTTPUtils.getHTTPContentAsString("http://poi.gps-club.ru/mapnav.php?icon="+mp.pointSymbol+"&cp=utf");
        //$POI,����,http://poi.gps-club.ru/mapicons/dps_post.png
        // Check the gps data type and convert the information to a more readable format.
        data = MapUtil.parseString(s,',');
        hr=HTTPUtils.getHTTPContent(data[1]);
        if (hr.responseCode==200){
          pb = hr.baos.toByteArray();
          hr.baos=null;
          try{
            RMSSettings.saveGeoData(mp.pointSymbol, pb,RMSSettings.GEODATA_GPSCLUB);
          }finally{
            img=Image.createImage(pb,0, pb.length);//Image.createImage(is);
            iconsGpsClub.put(mp,img);
          }
        }
       MapCanvas.map.repaint(); 
      }catch(Throwable t){}
    }
   MapCanvas.map.repaint(); 
  }
  
  private Vector ic_lq;
  private static Hashtable iconsGpsClub;
  
  public static Image retrieveGPSClubIcon(MapPoint mp) {
    Image im = (Image) iconsGpsClub.get(mp);
    if (im==null) {
      im = MapPoint.retrieveGarPointIcon(mp.pointSymbol);
    }
    return im;
  }
  
  public void setProgressResponse(ProgressResponse progressResponse) {
  }
  
}
