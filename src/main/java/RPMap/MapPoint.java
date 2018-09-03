/*
 * GPSPoint.java
 *
 * Created on 12 ������ 2007 �., 2:25
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package RPMap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.microedition.lcdui.Image;
import misc.GPSClubLoad;
import misc.MVector;
import misc.Util;

/**
 *
 * @author julia
 */
public class MapPoint {
  /** Latitude */
  public double lat;
  /** Longitude */
  public double lon;
  /** Altitude */
  public int alt;
  /** Time */
  public long ts;
  /** Name of point */
  public String getName() {if(name==null) return MapUtil.emptyString;else return name;}
  public String name;
  public short pointSymbol=70;
  public int foreColor=0xffffff;
  public int backColor=0x808000;
  public byte kind=TYPE_ROUTE;
  final public static byte TYPE_ROUTE=1;
  final public static byte TYPE_TRACK=2;
  final public static byte TYPE_KML=3;
//  final public static byte WAYPOINT=4;
//  final public static byte WAYPOINT_GC=8;
  /** Distance from start in km (or some private numeric data) */
  public float dist;
  
  /** Track speed in kmh */
  public float speed;
  public boolean visible = true;
  //int proximity; not saved in Ozi format :(
  
  public int scrX;
  public int scrY;
  //public MapPointAction action;
  public String tag;
  
  /** Creates a new instance of GPSPoint */
  public MapPoint(double la, double lo, int al, long t) {
    lat=la;
    lon=lo;
    alt=al;
    ts=t;
  }
  public MapPoint(double la, double lo, int al, long t, float spd) {
    lat=la;
    lon=lo;
    alt=al;
    ts=t;
    speed=spd;
  }
  public MapPoint(double la, double lo, int al, long t, String nam) {
    lat=la;
    lon=lo;
    alt=al;
    ts=t;
    name=nam;
  }
  public MapPoint(double la, double lo, int al, String nam) {
    lat=la;
    lon=lo;
    alt=al;
    name=nam;
    ts=System.currentTimeMillis();
  }
  public void save2Stream(DataOutputStream out)  throws IOException {
    out.writeDouble(lat);
    out.writeDouble(lon);
    out.writeDouble(alt);
    out.writeLong(ts);
    out.writeUTF(getName());

    out.writeShort(pointSymbol);
    out.writeInt(foreColor);
    out.writeInt(backColor);
    out.writeInt(1);//reserved
    out.writeUTF(tag==null?MapUtil.emptyString:tag);
    out.writeFloat(speed);
  }
  public static double javaTime2WinTime(long ts){
    return (double)((double)ts/86400000.0d+25569.0d);
  }
  public static long winTime2JavaTime(double dt){
    return (long)((dt-25569.0d)*86400000.0d);
  }
  public void save2OziTrackStream(OutputStream out)  throws IOException {
    double d=javaTime2WinTime(ts);
    //d=d/86400000.+25569.;
    out.write(Util.stringToByteArray(String.valueOf(MapUtil.coordRound5(lat))+','+MapUtil.coordRound5(lon)+",0,"+
        String.valueOf((int)(alt*3.280839895))+','+d+",d,t"
        ,true));
    out.write(MapUtil.CRLFb);
  }
  public void save2OziWaypointStream(OutputStream out)  throws IOException {
    double d=javaTime2WinTime(ts);
    String s=name+','+MapUtil.coordRound5(lat)+','+MapUtil.coordRound5(lon)+','+d+','+
        String.valueOf(pointSymbol)+",1,4,"+
        String.valueOf(MapRouteLoader.swapOziColor(foreColor))+','+
        String.valueOf(MapRouteLoader.swapOziColor(backColor))+",,0,0,0,"+
        String.valueOf((int)(alt*3.280839895))+",6,0,17";
    out.write(Util.stringToByteArray(s,RouteSend.EXPORTCODEPAGE==MapRouteLoader.CODEPAGEUTF));
    out.write(MapUtil.CRLFb);
  }
  
  public void save2OziRouteStream(OutputStream out)  throws IOException {
    double d=javaTime2WinTime(ts);
    String s=name+','+MapUtil.coordRound5(lat)+','+MapUtil.coordRound5(lon)+','+d+','+
        String.valueOf(pointSymbol)+",1,4,"+
        String.valueOf(MapRouteLoader.swapOziColor(foreColor))+','+
        String.valueOf(MapRouteLoader.swapOziColor(backColor))+",,0,0";
    out.write(Util.stringToByteArray(s,RouteSend.EXPORTCODEPAGE==MapRouteLoader.CODEPAGEUTF));
    out.write(MapUtil.CRLFb);
  }
  
  public MapPoint(DataInputStream in) throws IOException  {
    lat=in.readDouble();
    lon=in.readDouble();
    alt=(int)in.readDouble();
    ts=in.readLong();
    name=in.readUTF();
    if (name.equals(MapUtil.emptyString)) name=null;

    pointSymbol=in.readShort();
    foreColor=in.readInt();
    backColor=in.readInt();
    int version = in.readInt();//reserved
    String tTag=in.readUTF();
    if (tTag.equals(MapUtil.emptyString))
        tag=null;
    else
        tag=tTag;
    speed=in.readFloat();
  }
  /** Return icon for waypoint typed route*/
  public Image getImageIcon(byte gpsS) {
//    if (gpsS==MapRoute.MAGELLANGPS)
//      return retrieveMagPointIcon(pointSymbol);
//    else
    if (gpsS==MapRoute.ICONS_GPSCLUB)
      return GPSClubLoad.retrieveGPSClubIcon(this);
    else //if (gpsS==MapRoute.ICONS_GARMIN)
      return retrieveGarPointIcon(pointSymbol);
  }

  private static MVector iconsMag;

  /** Return icon for waypoint */
  public static Image retrieveGarPointIcon(int iconIndex) {
    if (iconIndex==112) iconIndex=20;
    if (iconsMag==null) {
      try {
        iconsMag= new MVector();
        iconsMag.setSize(71);
        Image ic = Image.createImage("/img/gwp.png");
        iconsMag.setElementAt(Image.createImage(ic,0,0,13,13,0),0);
        
        //    for (int x=0;x<3;x++)
        //      for (int y=0;y<7;y++) {
        //      iconsMag.addElement(Image.createImage(ic,x*15,y*15,15,15,0));
        //      }
      } catch(IOException _ex) { return null;}
    }
    
    if ((iconIndex>70)||(iconIndex<0)) return (Image)iconsMag.elementAt(0);
    Image im = (Image)iconsMag.elementAt(iconIndex);
    if (im==null) {
      try {
        int y=iconIndex/10;
        int x=iconIndex - y*10;
        Image ic = Image.createImage("/img/gwp.png");
        im=Image.createImage(ic,x*13,y*13,13,13,0);
        iconsMag.setElementAt(im,iconIndex);
      } catch(IOException _ex) { return (Image)iconsMag.elementAt(0);}
    }
    return im;
  }
// public boolean isGeoCache(){
//   return ((ts!=0) && (ts<30000));
// }
  public void move2Azimut(double dist,double azimut){
    azimut=-azimut;
    azimut+=90;
    //dLon=dist;
    double dLon=MapUtil.R2G*dist*Math.cos(azimut*MapUtil.G2R)/(40000000.*(MapUtil.PImul2_rev)*Math.cos(Math.abs(lat)*MapUtil.G2R));
    
    double dLat=MapUtil.R2G*dist*Math.sin(azimut*MapUtil.G2R)/(40000000.*(MapUtil.PImul2_rev));
    
    //double dLat=dist*Math.sin(azimut*MapUtil.G2R)/40000.;
    lat+=dLat;
    lon+=dLon;//*Math.cos(lat);
  }
    
  public final int hashCode(){
    return pointSymbol;
  }
  
  public final boolean equals(Object obj) {
    return (((MapPoint)obj).pointSymbol==pointSymbol);
  }
}
