/*
 * KMLSearchResult.java
 *
 * Created on 1 ��� 2008 �., 11:53
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package kml;

import RPMap.MapCanvas;
import RPMap.MapPoint;
import RPMap.MapRoute;
import RPMap.MapUtil;
import RPMap.RMSOption;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.List;
import misc.HTTPUtils;
import misc.ProgressResponse;
import misc.ProgressStoppable;
//#debug
//# import misc.DebugLog;
import misc.Util;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 *
 * @author RFK
 */
public class KMLSearchResult implements Runnable, ProgressStoppable {
  String place;
  public static Vector kmlPoints;
  List resultList;
  
  /** Creates a new instance of KMLSearchResult */
  public KMLSearchResult(String place,List resultList) {
    this.place=place;
    this.resultList=resultList;
    (new Thread(this)).start();
  }
  
  private byte searchKind = KMLPLACESEARCH;
  public final static byte KMLPLACESEARCH = 0;
  public final static byte KMLDIRSEARCH = 1;
  
  MapPoint from;
  MapPoint to;
  public KMLSearchResult(MapPoint from, MapPoint to) {
    searchKind = KMLDIRSEARCH;
    this.from=from;
    this.to=to;
    (new Thread(this)).start();
  }
  void readResponse(KXmlParser parser) throws IOException, XmlPullParserException {
    //#debug
//#     parser.require(XmlPullParser.START_TAG, null, "Response");
    while (parser.nextTag() != XmlPullParser.END_TAG) {
      //#debug
//#       parser.require(XmlPullParser.START_TAG, null, null);
      String name = parser.getName();
      //#debug
//#       System.out.println("Response<"+name+">");
      name=name.toLowerCase();
      if (name.equals(kmlP.s_placemark)) {
        
        KMLMapPoint mp = new KMLMapPoint(parser,kmlP);
        kmlPoints.addElement(mp);
        
      }  else if (parser.isEmptyElementTag())
        parser.next();
      else parser.skipTree();
      
    }
    //#debug
//#     parser.require(XmlPullParser.END_TAG, null, "Response");
  }
  
  private void readResult(InputStream inputStream){
    try{
      
      KXmlParser parser = new KXmlParser();
      
      parser.setInput(inputStream,null);
      
      parser.nextTag();
      //#debug
//#       parser.require(XmlPullParser.START_TAG, null, "kml");
      
      while (parser.nextTag() != XmlPullParser.END_TAG) {
        //#debug
//#         parser.require(XmlPullParser.START_TAG, null, null);
        
        String name = parser.getName().toLowerCase();
        
        if (name.equals(kmlP.s_response)) readResponse(parser);
        else parser.skipTree();
        
        parser.require(XmlPullParser.END_TAG, null, null);
      }
      
      
      //#debug
//#       parser.require(XmlPullParser.END_TAG, null, "kml");
      
    } catch (Exception e) {
      //#debug
//#       e.printStackTrace();
    }
  }
  
  
  KMLParser kmlP;
  public void run() {
    String url=MapUtil.emptyString;
    try{
      if (searchKind==KMLPLACESEARCH) {
        //byte[] ba = Util.stringToByteArray(place,true);
        //StringBuffer sb = new StringBuffer(30);
        //int c;
        //for (int i=0;i<ba.length;i++){
        //  c=ba[i];
        //  sb.append((char)(c));
       // }
        //"http://www.mgmaps.com/unzipkml.php?url="+HTTPUtils.urlEncodeStringAll
        url=("http://maps.google.com/maps/geo?q="+HTTPUtils.urlEncodeUnicode(place)
        +"&output=xml");
      }else {
        url=("http://maps.google.com/maps?f=d&source=s_d&saddr="+from.lat+","+from.lon+"&daddr="+to.lat+","+to.lon
            +"&output=kml");
      }
////        ByteArrayOutputStream baos= HTTPUtils.getHTTPContent("http://8motions.com/tools/kmlProxy.php?url=" + HTTPUtils.urlEncodeString("http://wikimapia.org/d/?BBOX=29,59,30,60"));
//        String ss="http://www.mgmaps.com/unzipkml.php?url=" + HTTPUtils.urlEncodeString("http://wikimapia.org/d/?BBOX=29,59,30,60")+"&gzip=yes";
//        ByteArrayOutputStream baos= HTTPUtils.getHTTPContent(ss);
//
//        FileConnection fc = (FileConnection)Connector.open("file:///root1/res3.data");
//        fc.create();
//        fc.openOutputStream().write(baos.toByteArray());
//        fc.close();
      Thread.sleep(200);
      if (progressResponse!=null){
        progressResponse.setProgress((byte)(20),"Requesting...");
      }
      
      if (searchKind==KMLPLACESEARCH) {
        HttpConnection httpConn = HTTPUtils.getHttpConn(url);
        // HttpConnection httpConn = (HttpConnection)Connector.open(url);
        try{
//            s= s+s;
          InputStream is = httpConn.openInputStream();
          kmlPoints = new Vector(3);
          try{
            if (progressResponse!=null){
              progressResponse.setProgress((byte)(70),"Parsing...");
            }
            kmlP = new KMLParser(null,KMLParser.PARSEDOCUMENTKML);
            readResult(is);
            if (progressResponse!=null){
              progressResponse.setProgress((byte)(90),"Almost done...");
            }
          }finally{
            kmlP=null;
            is.close();
          }
          resultList.deleteAll();
          for(int i=0;i<kmlPoints.size();i++){
            KMLMapPoint mp = (KMLMapPoint) kmlPoints.elementAt(i);
            resultList.append(mp.name,null);
          }
          MapCanvas.setCurrent(resultList);
          
        }finally{
          httpConn.close();
        }
        
      }else{
        
        KMLMapRoute kmr=new KMLMapRoute();
        kmr.readURL(url);
        if (stopped) return;
        kmr.kind=MapRoute.KMLDOCUMENT;
        kmr.kmlKind=KMLMapRoute.KMLKIND_ROUTE;
        MapCanvas.map.activeRoute=kmr;
        MapCanvas.setCurrentMap();
      }
      
    }catch(Throwable t){
      //#mdebug
//#       if (RMSOption.debugEnabled)
//#         DebugLog.add2Log("KMLR:"+url+" e:"+t);
      //#enddebug

      MapCanvas.setCurrentMap();
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
