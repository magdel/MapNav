/*
 * KMLParser.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package kml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Vector;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
//#debug
//# import misc.DebugLog;
import RPMap.RMSOption;
import misc.MVector;

/**
 *
 * @author RFK
 */
public class KMLParser {
  private KMLMapRoute mapRoute;
  public final static byte PARSENETWORKKML=1;
  public final static byte PARSEDOCUMENTKML=2;
  private byte parseKind;
  private Hashtable iconHash = new Hashtable(20);
  private MVector kmlPoints = new MVector(30);
  /** Creates a new instance of KMLParser */
  public KMLParser(KMLMapRoute mapRoute,byte parseKind) {
    this.mapRoute=mapRoute;
    this.parseKind=parseKind;
  }
  public void setParseKind(byte parseKind){
    this.parseKind=parseKind;
  }
  private InputStream inputStream;
  public void runWith(InputStream inputStream) throws XmlPullParserException, IOException{
    this.inputStream=inputStream;
    runMe();
  }
  //public void run(){
  //    try{
  //    runMe();
  //    }catch(Throwable t){
  //    }
  //}
  public void runMe() throws XmlPullParserException, IOException {
  //  try {
      ///    HttpConnection httpConnection = (HttpConnection)Connector.open(kmlUrl);
      iconHash.clear();
      kmlPoints.removeAllElements();
      
      KXmlParser parser = new KXmlParser();
//      if (inputStream==null){
//        if (parseKind==PARSENETWORKKML)
//          //  parser.setInput(new InputStreamReader(this.getClass().getResourceAsStream("/ge.kml"),"UTF-8"));
//          parser.setInput(new InputStreamReader(this.getClass().getResourceAsStream("/nn.kml"),"UTF-8"));
//        else
//          parser.setInput(new InputStreamReader(this.getClass().getResourceAsStream("/ks.xml"),"UTF-8"));
//
//      } else
      
      //ByteArrayOutputStream baos = new ByteArrayOutputStream(5000);
      
      
      parser.setInput(inputStream,null);
      
      parser.nextTag();
      //#debug
//#       parser.require(XmlPullParser.START_TAG, null, "kml");
      try{
        while (parser.nextTag() != XmlPullParser.END_TAG) {
          //#debug
//#           parser.require(XmlPullParser.START_TAG, null, null);
          
          String name = parser.getName().toLowerCase();
          
          if (parseKind==PARSENETWORKKML){
            if (name.equals(s_networklink)) readNetworkLink(parser);
            else {
              int nt = parser.next();
              if (nt==XmlPullParser.START_TAG) {
                parser.skipTree();parser.next();} else
                  if (nt==XmlPullParser.TEXT) parser.next();}
          } else
            if (parseKind==PARSEDOCUMENTKML){
            if (name.equals(s_document)) readDocument(parser);
            else if (name.equals(s_folder)) readFolder(parser);
            else parser.skipTree();
            
//            else {
//              int nt = parser.next();
//              if (nt==XmlPullParser.START_TAG) {
//                parser.skipSubTree();parser.next();} else
//                  if (nt==XmlPullParser.TEXT) parser.next();
//            }
            //#debug
//#             parser.require(XmlPullParser.END_TAG, null, null);
            }
        }
      }finally{
        mapRoute.setActPts(0);
        mapRoute.pts=kmlPoints;
        mapRoute.recalcMapLevelScreen(null);
      }
      //#debug
//#       // parser.require(XmlPullParser.END_TAG, null, "kml");
      //parser.next();
      
      
      //#debug
//#       //parser.require(XmlPullParser.END_DOCUMENT, null, null);
  //  } catch (Exception e) {
  //    //#debug
  //    e.printStackTrace();
  //    //	descriptions.addElement(e.toString());
  //    //			newsList.append("Error", null);
  //  //#mdebug
  //    if (RMSOption.debugEnabled){
  //      DebugLog.add2Log("KML e:"+e);
  //    }
 // //#enddebug
   // }
    try{inputStream.close();}catch(Throwable t){}
    inputStream=null;
  }
  
  void readNetworkLink(KXmlParser parser) throws IOException, XmlPullParserException {
    //#debug
//#     parser.require(XmlPullParser.START_TAG, null, "NetworkLink");
    while (parser.nextTag() != XmlPullParser.END_TAG) {
      //#debug
//#       parser.require(XmlPullParser.START_TAG, null, null);
      String name = parser.getName();
      //#debug
//#       System.out.println("NetWorkLink<"+name+">");
      name=name.toLowerCase();
      if (name.equals(s_name))
        mapRoute.name=parser.nextText();
      else if (name.equals(s_description))
        mapRoute.description=parser.nextText();
      else if (name.equals(s_url)) readUrl(parser);
      else
        
      {
        int nt = parser.next();
        if (nt==XmlPullParser.START_TAG) {
          parser.skipTree();parser.next();} else
            if (nt==XmlPullParser.TEXT) parser.next();
      }
      //#debug
//#       parser.require(XmlPullParser.END_TAG, null, null);
    }
    //#debug
//#     parser.require(XmlPullParser.END_TAG, null, "NetworkLink");
  }
  
  void readUrl(KXmlParser parser) throws IOException, XmlPullParserException {
    //#debug
//#     parser.require(XmlPullParser.START_TAG, null, "Url");
    while (parser.nextTag() != XmlPullParser.END_TAG) {
      //#debug
//#       parser.require(XmlPullParser.START_TAG, null, null);
      String name = parser.getName();
      //#debug
//#       System.out.println("Url<"+name+">");
      name=name.toLowerCase();
      if (name.equals(s_href)) mapRoute.href=parser.nextText();
      else {
        int nt = parser.next();
        if (nt==XmlPullParser.START_TAG) {
          parser.skipTree();parser.next();} else
            if (nt==XmlPullParser.TEXT) parser.next();
      }
      //#debug
//#       parser.require(XmlPullParser.END_TAG, null, null);
    }
    //#debug
//#     parser.require(XmlPullParser.END_TAG, null, "Url");
  }
  
  
  void readDocument(KXmlParser parser) throws IOException, XmlPullParserException {
    //#debug
//#     parser.require(XmlPullParser.START_TAG, null, "Document");
    while (parser.nextTag() != XmlPullParser.END_TAG) {
      //#debug
//#       parser.require(XmlPullParser.START_TAG, null, null);
      String name = parser.getName();
      //#debug
//#       System.out.println("Document<"+name+">");
      name=name.toLowerCase();
      if (name.equals(s_style)) {
        
        KMLStyle ks = new KMLStyle(parser,this);
        iconHash.put(ks.id,ks);
        
      } else if (name.equals(s_placemark)) {
        
        KMLMapPoint mp = new KMLMapPoint(parser,this);
        kmlPoints.addElement(mp);
        
      } else
        if (name.equals(s_folder)) readFolder(parser);
        else if (name.equals(s_name)) {
        mapRoute.name=parser.nextText();
        } else parser.skipTree();
      
      
      //  parser.require(XmlPullParser.END_TAG, null, name);
    }
    //#debug
//#     parser.require(XmlPullParser.END_TAG, null, "Document");
  }
  
  void readFolder(KXmlParser parser) throws IOException, XmlPullParserException {
    //#debug
//#     parser.require(XmlPullParser.START_TAG, null, "Folder");
    while (parser.nextTag() != XmlPullParser.END_TAG) {
      //#debug
//#       parser.require(XmlPullParser.START_TAG, null, null);
      String name = parser.getName();
      //#debug
//#       System.out.println("Folder<"+name+">");
      name=name.toLowerCase();
      if (name.equals(s_style)) {
        
        KMLStyle ks = new KMLStyle(parser,this);
        iconHash.put(ks.id,ks);
        
      } else if (name.equals(s_placemark)) {
        
        KMLMapPoint mp = new KMLMapPoint(parser,this);
        kmlPoints.addElement(mp);
        
      } else
        if (name.equals(s_folder)) readFolder(parser);
        else
          parser.skipTree();
      
      // parser.require(XmlPullParser.END_TAG, null, name);
    }
    //#debug
//#     parser.require(XmlPullParser.END_TAG, null, "Folder");
  }
  
  String s_folder = "folder";
  String s_placemark = "placemark";
  String s_style = "style";
  String s_document = "document";
  String s_url = "url";
  String s_networklink = "networklink";
  String s_description = "description";
  String s_kml = "kml";
  String s_href = "href";
  String s_name = "name";
  String s_region = "region";
  String s_point = "point";
  String s_styleurl = "styleurl";
  String s_latlonaltbox =  "latlonaltbox";
  
  String s_address =  "address";
  String s_addressdetails =  "addressdetails";
  String s_response =  "response";
  String s_accuracy =  "Accuracy";
  
  String s_id  = "id";
  String s_iconstyle  = "iconstyle";
  String s_icon  = "icon";
  
  String s_multigeometry  = "multigeometry";
  String s_coordinates  = "coordinates";
  String s_linestring  = "linestring";
  String s_geometrycollection  = "geometrycollection";
  
  String s_north  = "north";
  String s_south =  "south";
  String s_east =  "east";
  String s_west =  "west";
  
}
