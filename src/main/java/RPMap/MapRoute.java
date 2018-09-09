/*
 * GPSRoute.java
 *
 * Created on 12  2007 ., 2:22
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package RPMap;

import app.MapForms;
import gpspack.GPSReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import kml.KMLMapPoint;
import lang.Lang;
import lang.LangHolder;
//#debug
//# import misc.DebugLog;
import misc.GraphUtils;
import misc.MVector;
import misc.MapSound;
import misc.ProgressResponse;
import misc.TextCanvas;
import misc.Util;

/**
 *
 * @author julia
 */
public class MapRoute implements Runnable {

    public static final int DEF_PTS_COUNT=3000;
    final static public byte ROUTEKIND=1;
    final static public byte WAYPOINTSKIND=2;
    final static public byte TRACKKIND=3;
    final static public byte KMLDOCUMENT=4;
    final static public byte NRWAYPOINTSKIND=5;
    static String RTRNAMEDEF="trac5def";
    final static public byte ICONS_GARMIN=1;
    final static public byte ICONS_GPSCLUB=2;
    public MVector pts=new MVector(100, 300);
    private int actpts;
    public int color=0xFF00FF;
    public String name;
    public byte kind=MapRoute.WAYPOINTSKIND;
    public byte gpsSystem=MapRoute.ICONS_GARMIN;
    public int rId=-1;
    public boolean defTrack;
    public static final byte GEOINFO_GEOCACHE=1;
    public static final byte GEOINFO_GPSCLUB=2;
    public static final byte GEOINFO_NRMESS=3;
    public static final byte GEOINFO_KML=4;
    public static final byte GEOINFO_GEOCACHECOM=5;
    /** ������� �������� � ������� � ��� ����������� */
    public byte geoInfo;
    static public int selColor=0xFF00FF;
    public boolean showPoint;
    public boolean showLabels=true;
    public boolean showNextAlways=true;
    //final static public byte MAGELLANGPS=2;
    boolean fastDraw;
    /** level for coordinates are cached */
    protected int mapLevelScreen=-1;
    /** map center for coordinates are cached */
    protected int mapXCenter, mapYCenter;
    public int mnTrackId;
    public int osmTrackId;
    public boolean saved;

    public void setActPts(int pI) {
        actpts=pI;
        if (markShowState!=MARK_STATE_KEEP){
            markShowState=MARK_STATE_UNKNOWN;
        }
    }
    /* return length of track */

    public final double distance() {
        if (pts.size()<2){
            return 0;
        }
        MapPoint p=(MapPoint) pts.lastElement();
        MapPoint p1=(MapPoint) pts.firstElement();
        return p.dist-p1.dist;
    }

    public final double avgspeed() {
        if (pts.size()<2){
            return 0;
        }
        MapPoint p1=(MapPoint) pts.firstElement();
        MapPoint p2=(MapPoint) pts.lastElement();
        return (distance()/((p2.ts-p1.ts))*3600000.);
    }

    public final String getRMSName() {
        if (defTrack){
            return RTRNAMEDEF;
        } else {
            return getRMSName(kind);
        }
    }

    public final static String getRMSName(byte rkind) {
        //String RRTSNAME="route3";
        //String RWPNAME="wayp3";
        // String RTRNAME="trac3";
        // String RKLNAME="skml3b";

        if (rkind==ROUTEKIND){
            return "route5";
        } else if (rkind==WAYPOINTSKIND){
            return "wayp5";
        } else if (rkind==TRACKKIND){
            return "trac5";
        } else if (rkind==KMLDOCUMENT){
            return "skml5";
        } else {
            return "EE";
        }
    }

    /** Creates a new instance of GPSRoute */
    public MapRoute(byte routeKind) {
        kind=routeKind;
        if (kind==TRACKKIND){
            showLabels=false;
            name=MapUtil.trackNameAuto();
        }

        /*  pts.addElement(new GPSPoint(60,30,5,"Start"));
        pts.addElement(new GPSPoint(60.1,30.1,5,"pt #2"));
        pts.addElement(new GPSPoint(60,30.2,5,"pt 3"));
        pts.addElement(new GPSPoint(60.2,30.3,5,"next pt"));
        pts.addElement(new GPSPoint(60.0,30.35,5,"Finish"));
        actpts=2;*/
    }
    private long offsetUTC=MapUtil.diffLoc2UTCmillis;

    public void recalcMapLevelScreen(MapPoint pt) {
        if (pt==null){
            mapLevelScreen=MapCanvas.map.level;
            mapXCenter=MapCanvas.xCenter;
            mapYCenter=MapCanvas.yCenter;
            MapPoint mp;
            if (pts.size()<5){
                for (int i=pts.size()-1; i>=0; i--) {
                    mp=(MapPoint) pts.elementAt(i);
                    mp.scrX=MapUtil.getXMap(mp.lon, mapLevelScreen);
                    mp.scrY=MapUtil.getYMap(mp.lat, mapLevelScreen);
                    mp.visible=true;
                }
                return;
            }
            mp=(MapPoint) pts.elementAt(pts.size()-1);
            mp.scrX=MapUtil.getXMap(mp.lon, mapLevelScreen);
            mp.scrY=MapUtil.getYMap(mp.lat, mapLevelScreen);
            int px=mp.scrX, py=mp.scrY;
            int dcxm=MapUtil.getXMap(MapCanvas.reallon, mapLevelScreen), dcym=MapUtil.getYMap(MapCanvas.reallat, mapLevelScreen);
            if (kind==TRACKKIND){
                timeLastRecalc=System.currentTimeMillis();
                for (int i=pts.size()-2; i>=0; i--) {
                    mp=(MapPoint) pts.elementAt(i);
                    mp.scrX=MapUtil.getXMap(mp.lon, mapLevelScreen);
                    mp.scrY=MapUtil.getYMap(mp.lat, mapLevelScreen);
                    if ((Math.abs(px-mp.scrX)>4)||(Math.abs(py-mp.scrY)>4)){
                        mp.visible=true;
                        px=mp.scrX;
                        py=mp.scrY;
                    } else {
                        mp.visible=false;
                    }
                }
            } else if ((kind==WAYPOINTSKIND)){
                for (int i=pts.size()-2; i>=0; i--) {
                    mp=(MapPoint) pts.elementAt(i);
                    mp.scrX=MapUtil.getXMap(mp.lon, mapLevelScreen);
                    mp.scrY=MapUtil.getYMap(mp.lat, mapLevelScreen);

                    if ((mp.lon==0)&&(mp.lat==0)&&(i!=actpts)){
                        mp.visible=false;
                    } else if ((((Math.abs(px-mp.scrX)>10)||(Math.abs(py-mp.scrY)>12))&&MapCanvas.isOn2Screen(mp.scrX-dcxm, mp.scrY-dcym))||(i==actpts)){
                        mp.visible=true;
                        px=mp.scrX;
                        py=mp.scrY;
                    } else {
                        mp.visible=false;
                    }
                }
            } else if (kind==NRWAYPOINTSKIND){
                for (int i=pts.size()-1; i>=0; i--) {
                    mp=(MapPoint) pts.elementAt(i);
                    mp.scrX=MapUtil.getXMap(mp.lon, mapLevelScreen);
                    mp.scrY=MapUtil.getYMap(mp.lat, mapLevelScreen);
                    mp.visible=false;
                }
            } else {
                for (int i=pts.size()-1; i>=0; i--) {
                    mp=(MapPoint) pts.elementAt(i);
                    mp.scrX=MapUtil.getXMap(mp.lon, mapLevelScreen);
                    mp.scrY=MapUtil.getYMap(mp.lat, mapLevelScreen);
                    mp.visible=true;
                }
            }
        } else {
            pt.scrX=MapUtil.getXMap(pt.lon, mapLevelScreen);
            pt.scrY=MapUtil.getYMap(pt.lat, mapLevelScreen);
            pt.visible=true;
            if (kind==TRACKKIND){
                if (System.currentTimeMillis()-timeLastRecalc>30000){
                    recalcMapLevelScreen(null);
                }
            }
        }
    }
    private long timeLastRecalc;

    /** Deletes point from the route */
    public final void deleteMapPoint(MapPoint pt) {
        pts.removeElement(pt);
    }

    /** Adds point to the route */
    public final void addMapPoint(MapPoint pt) {
        if (pts.size()==0){
            pt.dist=0;
        } else {
            MapPoint p=(MapPoint) pts.lastElement();
            double dbp=distBetween(p, pt);
            pt.dist=(float) (p.dist+dbp);
            int da=pt.alt-p.alt;
            if (da>1){
                altGain+=da;
            } else if (da<-1){
                altLost-=da;
            }
        }

        recalcMapLevelScreen(pt);
        pts.addElement(pt);
        saved=false;
    }

    /** Adds point to the route */
    public final void addMapPointFromTrackLoad(MapPoint pt) {
        pt.kind=MapPoint.TYPE_TRACK;
        if (pts.size()==0){
            pt.dist=0;
            pt.speed=0;
        } else {
            MapPoint p=(MapPoint) pts.lastElement();
            double dbp=distBetween(p, pt);
            pt.dist=(float) (p.dist+dbp);
            if (pt.ts>p.ts){
                pt.speed=(float) (dbp/(pt.ts-p.ts)*3600000f);
            } else {
                pt.speed=p.speed;
            }
            if (pt.speed>1500){
                pt.speed=p.speed;
            }
            if (pt.ts-p.ts<2000){
                if (Math.abs(pt.speed-p.speed)>70){
                    pt.speed=p.speed;
                }
            }
        }
        recalcMapLevelScreen(pt);
        pts.addElement(pt);
    }

    /** Adds point to the track */
    public final void addTrackMapPoint(MapPoint pt) {
        pt.kind=MapPoint.TYPE_TRACK;
//        if (defTrack){
//            if (pts.size()==DEF_PTS_COUNT){
//                for (int i=0; i<DEF_PTS_COUNT-1; i++) {
//                    pts.setElementAt(pts.elementAt(i+1), i);
//                }
//                pts.setSize(DEF_PTS_COUNT-1);
//            }
//
//        }

        if (maxTrackSpeed<pt.speed){
            maxTrackSpeed=pt.speed;
            //  if (minTrackSpeed>pt.speed)minTrackSpeed=pt.speed;
//    recalcMapLevelScreen(pt);
//    pts.addElement(pt);
        }
        addMapPoint(pt);
    }
    private float maxTrackSpeed=1;
    /** ����� ������ */
    public int altGain;
    /** ������ ������ */
    public int altLost;
//  private float minTrackSpeed=0;

    /** Adds mark as point to the route */
    public final void addMark(MapMark mm) {
        addMapPoint(new MapPoint(mm.lat, mm.lon, 0, mm.name));
    }

    /** Retrieve active point in the route navigate to */
    final public MapPoint pt2Nav() {
        if ((pts.size()>0)&&(kind!=TRACKKIND)){
            return (MapPoint) pts.elementAt(actpts);
        } else {
            return null;
        }
    }

    final public MapPoint lastPoint() {
        if (pts.size()>0){
            return (MapPoint) pts.lastElement();
        } else {
            return null;
        }
    }

    /** Checks route for emptyness */
    final public boolean isEmpty() {
        return (pts.size()==0);
    }

    /** Inverts display mode for route  */
    public void swapShowPoint() {
        showPoint=!showPoint;
    }

    /** Inverts display mode for route  */
    public void swapShowLabels() {
        showLabels=!showLabels;
    }

    /** Place nearest point in active state */
    public void nearestNavPoint() {
        int minI=0;
        MapPoint mp;
        int pc=pts.size();
        double lat=(MapCanvas.gpsBinded&&MapCanvas.map.gpsActive())?GPSReader.LATITUDE:MapCanvas.reallat;
        double lon=(MapCanvas.gpsBinded&&MapCanvas.map.gpsActive())?GPSReader.LONGITUDE:MapCanvas.reallon;
        double d, md=100000000;
        for (int i=0; i<pc; i++) {
            mp=(MapPoint) pts.elementAt(i);
            d=distBetweenCoords(lat, lon, mp.lat, mp.lon);
            if (d<=md){
                md=d;
                minI=i;
            }
        }
        setActPts(minI);
    }

    /** Place next point in active state */
    public void nextNavPoint() {
        if (actpts<pts.size()-1){
            actpts++;
        } else {
            actpts=0;
        }
        setActPts(actpts);
    }

    /** Place previous point in active state */
    public void prevNavPoint() {
        if (actpts>0){
            actpts--;
        } else {
            actpts=pts.size()-1;
        }
        setActPts(actpts);
    }
    private double kLat, kLon;
    private int kDist;

    private void setNextWptAuto(int pI, int dist) {
        kLat=GPSReader.LATITUDE;
        kLon=GPSReader.LONGITUDE;
        kDist=dist;

        if (markShowState==MARK_STATE_NEXT){
            markShowState=MARK_STATE_KEEP;
        }

        setActPts(pI);
        if (RMSOption.getBoolOpt(RMSOption.BL_WAYPOINTNOTIFY)){
            MapSound.playSound(MapSound.NEXTWPTSOUND);
        }
    }

    private void checkAndResetCurrentMark() {
        if (markShowState==MARK_STATE_KEEP){
            if (distBetweenCoords(kLat, kLon, GPSReader.LATITUDE, GPSReader.LONGITUDE)*1000d>kDist+14){
                markShowState=MARK_STATE_UNKNOWN;
            }
        }
    }
    private byte checkCount;

    public final void checkProximity() {
        //#debug
//#         try {

            if (kind!=ROUTEKIND && kind != KMLDOCUMENT){
                return;
            }
            if (pts.size()<1){
                return;
            }
            if (actpts>=pts.size()-1){
                return;
            }
            checkAndResetCurrentMark();
            int dist=(int) (distFromPrecise(GPSReader.LATITUDE, GPSReader.LONGITUDE)*1000d);
            if (dist<=RMSOption.routeProximity){
                setNextWptAuto(actpts+1, dist);
                return;
            }
            checkCount++;
            if (checkCount>15){
                checkCount=0;
            }
            MapPoint mp;
            int pc=actpts+4;
            if (pc>pts.size()){
                pc=pts.size();
            }
            if ((RMSOption.getBoolOpt(RMSOption.BL_AUTOWPTNEAREST))){
                for (int i=actpts+1; i<pc; i++) {
                    mp=(MapPoint) pts.elementAt(i);
                    dist=(int) (distBetweenCoords(GPSReader.LATITUDE, GPSReader.LONGITUDE, mp.lat, mp.lon)*1000d);
                    if (dist<=RMSOption.routeProximity){
                        setNextWptAuto(i, dist);
                        return;
                    }
                }

                if ((checkCount==0)){
                    pc=pts.size();
                    for (int i=actpts+4; i<pc; i++) {
                        mp=(MapPoint) pts.elementAt(i);
                        dist=(int) (distBetweenCoords(GPSReader.LATITUDE, GPSReader.LONGITUDE, mp.lat, mp.lon)*1000d);
                        if (dist<=RMSOption.routeProximity){
                            setNextWptAuto(i, dist);
                            return;
                        }
                    }
                }
            }
            //#mdebug
//#         } catch (Throwable t) {
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("AutoMR:"+t);
//#             }
//#         }
        //#enddebug
    }

    public static final double courseDiff(double crs1, double crs2) {
        double dcourse=Math.abs(crs1-crs2);
        if (dcourse>MapUtil.PI){
            dcourse=MapUtil.PImul2-dcourse;
        }
        return dcourse;
    }

    /**
     * Absolute courses diffence
     * from to where
     */
    public static final double courseDiffAbs(double crs1, double crs2) {
        double diff=crs2-crs1;//normalizeCourse(crs2)-normalizeCourse(crs1);
        double dcourse=Math.abs(diff);
        boolean sign=diff<0;
        if (dcourse>MapUtil.PI){
            dcourse=MapUtil.PImul2-dcourse;
            sign=!sign;
        }
        //return dcourse;
        //return  normalizeCourseR(dcourse);
        return (sign)?-dcourse:dcourse;
    }

    /** Gets direction to active point from specified location */
    final public double courseFrom(double lat, double lon) {
        MapPoint gp=pt2Nav();
        if (gp==null){
            return 0;
        }
        return courseToCoords(lat, lon, gp.lat, gp.lon);
//    int x=MapUtil.getXMap(lon, mc.level);
//    int y=MapUtil.getYMap(lat, mc.level);
//    int x1=MapUtil.getXMap(gp.lon, mc.level);
//    int y1=MapUtil.getYMap(gp.lat, mc.level);
//    return -MapUtil.atan2(x1-x,y1-y)+MapUtil.PI;
    }

    /** Gets direction for current route step */
    final public double courseStep() {
        //if (actpts<1) return 0;
        MapPoint cp=(MapPoint) pts.elementAt(actpts);
        MapPoint pp=(MapPoint) pts.elementAt((actpts==0)?pts.size()-1:actpts-1);
        return courseToCoords(pp.lat, pp.lon, cp.lat, cp.lon);
//    int x=MapUtil.getXMap(lon, mc.level);
//    int y=MapUtil.getYMap(lat, mc.level);
//    int x1=MapUtil.getXMap(gp.lon, mc.level);
//    int y1=MapUtil.getYMap(gp.lat, mc.level);
//    return -MapUtil.atan2(x1-x,y1-y)+MapUtil.PI;
    }

    /** Gets direction for next route step */
    final public double courseNextStep() {
        //if (actpts<1) return 0;
        MapPoint cp=(MapPoint) pts.elementAt(actpts);
        MapPoint pp=(MapPoint) pts.elementAt((actpts==pts.size()-1)?0:actpts+1);
        return courseToCoords(cp.lat, cp.lon, pp.lat, pp.lon);
    }

    /** Gets distance to active point from specified location */
    final public double distFromRound(double lat, double lon) {
        return MapUtil.distRound3(distFromPrecise(lat, lon));
    }

    final public double distFromPrecise(double lat, double lon) {
        MapPoint gp=pt2Nav();
        if (gp==null){
            return 0;
        }
        return distBetweenCoords(lat, lon, gp.lat, gp.lon);
    }

    /** Gets distance in km between 2 points */
    final public static double distBetween(MapPoint p1, MapPoint p2) {
        return distBetweenCoords(p1.lat, p1.lon, p2.lat, p2.lon);
    }
    public final static double ER=6372.795;

    /** Gets distance in km between 2 points */
    final public static double distBetweenCoords(double lat1, double lon1, double lat2, double lon2) {
        double coslat1=Math.cos(lat1*MapUtil.G2R);
        double coslat2=Math.cos(lat2*MapUtil.G2R);
        double sinlat1=Math.sin(lat1*MapUtil.G2R);
        double sinlat2=Math.sin(lat2*MapUtil.G2R);
        double deltaLat=Math.abs(lat1-lat2)*MapUtil.G2R;
        double deltaLon=Math.abs(lon1-lon2)*MapUtil.G2R;
        double sinDeltaLon=Math.sin((lon2-lon1)*MapUtil.G2R);
        double cosDeltaLon=Math.cos(deltaLon);
        double sqr1=coslat2*sinDeltaLon;
        double sqr2=coslat1*sinlat2-sinlat1*coslat2*cosDeltaLon;
        double angleDelta=
          MapUtil.atan(Math.sqrt(sqr1*sqr1
          +sqr2*sqr2)
          /(sinlat1*sinlat2+coslat1*coslat2*cosDeltaLon));
        if (angleDelta<0){
            angleDelta+=MapUtil.PI;
            // double angleDiff =
            //     MapUtil.atan2(sinDeltaLon*coslat2, coslat1*sinlat2 - sinlat1*coslat2*cosDeltaLon);
        }
        return angleDelta*ER;

// ������ ����������
//     double kmlon = MapUtil.grad2rad(MapUtil.abs(lon1-lon2))*40000.*(MapUtil.PImul2_rev)*Math.cos(MapUtil.grad2rad(MapUtil.abs(lat1+lat2)/2.));
//    double kmlat = MapUtil.grad2rad(MapUtil.abs(lat1-lat2))*40000.*(MapUtil.PImul2_rev);
//    return Math.sqrt(kmlat*kmlat+kmlon*kmlon);
    }

    /** Gets angle distance between points
     * ������� ���������� ����� �������! */
    final public static double angleBetweenCoords(double lat1, double lon1, double lat2, double lon2) {
        double coslat1=Math.cos(lat1*MapUtil.G2R);
        double coslat2=Math.cos(lat2*MapUtil.G2R);
        double sinlat1=Math.sin(lat1*MapUtil.G2R);
        double sinlat2=Math.sin(lat2*MapUtil.G2R);
        double deltaLat=Math.abs(lat1-lat2)*MapUtil.G2R;
        double deltaLon=Math.abs(lon1-lon2)*MapUtil.G2R;
        double sinDeltaLon=Math.sin((lon2-lon1)*MapUtil.G2R);
        double cosDeltaLon=Math.cos(deltaLon);
        double sqr1=coslat2*sinDeltaLon;
        double sqr2=coslat1*sinlat2-sinlat1*coslat2*cosDeltaLon;
        double angleDelta=
          MapUtil.atan(Math.sqrt(sqr1*sqr1
          +sqr2*sqr2)
          /(sinlat1*sinlat2+coslat1*coslat2*cosDeltaLon));
//        if (angleDelta<0){
//            angleDelta+=MapUtil.PI;
//        }
        return angleDelta;
    }

    /** Gets course from point to point */
    final public static double courseToCoords(double lat1, double lon1, double lat2, double lon2) {
        double coslat1=Math.cos(lat1*MapUtil.G2R);
        double coslat2=Math.cos(lat2*MapUtil.G2R);
        double sinlat1=Math.sin(lat1*MapUtil.G2R);
        double sinlat2=Math.sin(lat2*MapUtil.G2R);
        double deltaLat=Math.abs(lat1-lat2)*MapUtil.G2R;
        double deltaLon=Math.abs(lon1-lon2)*MapUtil.G2R;
        double sinDeltaLon=Math.sin((lon2-lon1)*MapUtil.G2R);
        double cosDeltaLon=Math.cos(deltaLon);

//    double angleDelta =
//        MapUtil.atan(Math.sqrt(MapUtil.sqr(coslat2*sinDeltaLon)+
//        MapUtil.sqr(coslat1*sinlat2-sinlat1*coslat2*cosDeltaLon))/
//        (sinlat1*sinlat2+coslat1*coslat2*cosDeltaLon));
        double angleDiff=
          MapUtil.atan2(coslat1*sinlat2-sinlat1*coslat2*cosDeltaLon, sinDeltaLon*coslat2);
        //if (angleDelta<0) angleDiff=-angleDiff;
        return -angleDiff+MapUtil.PIdiv2;

    }

    /** Gets dist between 2 points and current point */
    final private static double height2Mar(double latC, double lonC, double latA, double lonA, double latB, double lonB) {
        double sin_a=Math.sin(angleBetweenCoords(latB, lonB, latC, lonC));
        double crs_A=courseToCoords(latC, lonC, latA, lonA);
        double crs_B=courseToCoords(latC, lonC, latB, lonB);
        double C=crs_A>crs_B?crs_A-crs_B:crs_B-crs_A;
        if (C>MapUtil.PI){
            C=MapUtil.PImul2-C;
        }
        double sin_hb=sin_a*Math.sin(C);
        return ER*MapUtil.atan(Math.sqrt(1/(1-sin_hb*sin_hb)-1));
    }

    final public double height2CurrentStep(double latB, double lonB) {
        MapPoint mpf=(MapPoint) pts.elementAt(actpts);
        MapPoint mpl=(MapPoint) pts.elementAt((actpts==0)?pts.size()-1:actpts-1);
        return height2Mar(mpl.lat, mpl.lon, mpf.lat, mpf.lon, latB, lonB);
    }

    final public boolean left2CurrentStep(double latB, double lonB) {
        // if (actpts==0) return false;
        MapPoint mpf=(MapPoint) pts.elementAt(actpts);
        MapPoint mpl=(MapPoint) pts.elementAt((actpts==0)?pts.size()-1:actpts-1);
        double c2p=courseToCoords(mpl.lat, mpl.lon, mpf.lat, mpf.lon);
        double c2c=courseToCoords(mpl.lat, mpl.lon, latB, lonB);
        double dc=c2p-c2c;
        if (dc>=MapUtil.PI){
            return true;
        } else if (dc>=0){
            return false;
        } else if (dc>=-MapUtil.PI){
            return true;
        } else {
            return false;
//    if (dc>=180) return true;
//    else if (dc>=0) return false;
//    else if (dc>=-180) return true;
//    else return false;
        }
    }
    private long nextTimeNearestTrackPoint;
    private String trackPointInfo;
    private int trackPointInfoIndex;

    public void clearNearestTrackpointDisplay(){
        nextTimeNearestTrackPoint=System.currentTimeMillis()-1;
        trackPointInfo=null;
    }

    void drawNearestTrackPoint() {
        if (nextTimeNearestTrackPoint>System.currentTimeMillis()){
            return;
        }

       // if (trackPointInfo!=null){
       //     nextTimeNearestTrackPoint=System.currentTimeMillis()+4000;
       // } else {
            nextTimeNearestTrackPoint=System.currentTimeMillis()+4000;
       // }

        trackPointInfo=null;
        int x=MapCanvas.xCenter, y=MapCanvas.yCenter;

        if (MapCanvas.map.gpsActive()){
            if (MapCanvas.gpsBinded) {
                return;
            }
            int dcxg=MapUtil.getXMap(GPSReader.LONGITUDE, MapCanvas.map.level),
              dcyg=MapUtil.getYMap(GPSReader.LATITUDE, MapCanvas.map.level);
            if ((Math.abs(x-dcxg)<10)&&(Math.abs(y-dcyg)<10)){
                return;
            }
        }

        MapPoint p1;

        for (int i=pts.size()-1; i>=0; i--) {
            p1=(MapPoint) pts.elementAt(i);
            if ((Math.abs(x-p1.scrX)<5)&&(Math.abs(y-p1.scrY)<5)){
                trackPointInfo=MapUtil.dateTime2Str(p1.ts, false)+' '+MapUtil.speedWithNameRound1(p1.speed);
                trackPointInfoIndex=i;
                break;
            }
        }
    }

    /** Draws route */
    public void drawRoute(Graphics g, int dcx, int dcy, boolean rotate) {
        boolean recalced=false;
        if (pts.size()==0){
            return;
        }
        if (kind==TRACKKIND){
            if (MapCanvas.map.level!=mapLevelScreen){
                recalcMapLevelScreen(null);
                recalced=true;
            }
        } else {
            if ((MapCanvas.map.level!=mapLevelScreen)||(Math.abs(mapXCenter-MapCanvas.xCenter)>MapCanvas.dmaxx/2)||(Math.abs(mapYCenter-MapCanvas.yCenter)>MapCanvas.dmaxy/2)){
                recalcMapLevelScreen(null);
                recalced=true;
            }
        }
        if (!recalced){
            if (kind==TRACKKIND){
                drawNearestTrackPoint();
            }
        }

        int x1, y1, x2, y2, xt, yt;
        MapPoint p1, p2;
        MapPoint pn=pt2Nav();
        int indexStart;
        if (kind==TRACKKIND){
            indexStart=pts.size()-1;
        } else {
            indexStart=(rotate)?((actpts+25>pts.size()-1)?pts.size()-1:actpts+25):pts.size()-1;
        }
        int fh=g.getFont().getHeight();

        int pc=pts.size();
        if (kind==TRACKKIND){
            if (rotate&&RMSOption.getBoolOpt(RMSOption.BL_LIMITTRACKSHOWROTATE)){
                pc=50;
            } else if (RMSOption.showTrPoints==RMSOption.TRSHOWLAST100){
                pc=100;
            }
        } else if (rotate){
            pc=50;
        }

        int pc_o=pc;
        //MapCanvas mc = MapCanvas.map;
        //connect with line
        int dx=dcx-MapCanvas.xCenter, dy=dcy-MapCanvas.yCenter;

        p1=(MapPoint) pts.elementAt(indexStart);

        x1=p1.scrX+dx;
        y1=p1.scrY+dy;
        //x1=MapUtil.getXMap(p1.lon, mc.level)+dx;
        //y1=MapUtil.getYMap(p1.lat, mc.level)+dy;
        if (MapCanvas.rotateMap){
            xt=x1;
            yt=y1;
            x1=MapCanvas.rotateMX(xt, yt);
            y1=MapCanvas.rotateMY(xt, yt);
        }

        boolean drawAllPts=(kind==ROUTEKIND)&&(pts.size()<50);
        if ((kind==ROUTEKIND)||(kind==TRACKKIND)){
            boolean ct=(kind==TRACKKIND)&&(RMSOption.coloredTrack);
            if (!showPoint){
                for (int i=indexStart; i>0; i--) {
                    p2=(MapPoint) pts.elementAt(i-1);
                    //if ((!p2.visible) && (!drawAllPts)){
                    if (!(p2.visible||drawAllPts)){
                        continue;
                    }
                    if (pc==0){
                        break;
                    }
                    pc--;
                    x2=p2.scrX+dx;
                    y2=p2.scrY+dy;
                    //x2=MapUtil.getXMap(p2.lon, mc.level)+dx;
                    //y2=MapUtil.getYMap(p2.lat, mc.level)+dy;

                    if (MapCanvas.rotateMap){
                        xt=x2;
                        yt=y2;
                        x2=MapCanvas.rotateMX(xt, yt);
                        y2=MapCanvas.rotateMY(xt, yt);
                    }

                    //if ((!MapCanvas.isOn2Screen(x2, y2)) &&(!drawAllPts)){
                    if (!(drawAllPts||MapCanvas.isOn2Screen(x2, y2))){
                        p1=p2;
                        x1=x2;
                        y1=y2;
                        continue;
                    }

                    g.setColor(0x0);
                    g.drawLine(x1+1, y1+1, x2+1, y2+1);
                    g.drawLine(x1+1, y1, x2+1, y2);
                    if (defTrack){
                        g.setColor(0xFFC040);
                    } else if (ct){
                        g.setColor(makeTColor((int) (p2.speed/maxTrackSpeed*127)));
                    } else {
                        g.setColor(color);
                    }
                    g.drawLine(x1, y1, x2, y2);
                    g.drawLine(x1-1, y1, x2-1, y2);
                    g.drawLine(x1, y1-1, x2, y2-1);


                    p1=p2;

                    x1=x2;
                    y1=y2;

                }
            }
        }

        pc=pc_o;
        for (int i=indexStart; i>=0; i--) {
            if ((showPoint)&&(i!=actpts)){
                continue;
            }
            p2=(MapPoint) pts.elementAt(i);
            if (!p2.visible){
                continue;
            }
            if (pc==0){
                break;
            }
            pc--;

            x2=p2.scrX+dx;
            y2=p2.scrY+dy;
            //x2=MapUtil.getXMap(p2.lon, mc.level)+dx;
            //y2=MapUtil.getYMap(p2.lat, mc.level)+dy;
            if (MapCanvas.rotateMap){
                xt=x2;
                yt=y2;
                x2=MapCanvas.rotateMX(xt, yt);
                y2=MapCanvas.rotateMY(xt, yt);
            }

            if (!MapCanvas.isOn2Screen(x2, y2)){
                continue;
            }
            int sw=0;
            if (p2!=pn){
                if (showLabels){
                    if (p2.name!=null){
                        sw=g.getFont().stringWidth(p2.name)/2+1;
                        g.setColor(p2.backColor);
                        g.fillRect(x2-sw-1, y2-fh-1-10, sw*2+2, fh+2);
                        g.setColor(p2.foreColor);
                        g.drawLine(x2, y2, x2, y2-9);
                        g.drawString(p2.name, x2, y2-10, Graphics.BOTTOM|Graphics.HCENTER);
                        g.drawRect(x2-sw-1, y2-fh-1-10, sw*2+2, fh+2);
                        //   MapCanvas.drawShadowString(g,p2.name,x2,y2-10,p2.foreColor,Graphics.BOTTOM|Graphics.HCENTER,p2.backColor);
                    }
                }
                if ((kind!=TRACKKIND)&&(!fastDraw)){
                    g.drawImage(p2.getImageIcon(gpsSystem), x2, y2, Graphics.HCENTER|Graphics.VCENTER);
                } else //draw some point faster then picture...
                if (!fastDraw){ //g.setColor(0x0);
                    //g.drawRect(x2, y2,3,3);
//          g.setColor(0x70FF60);
//          g.drawRect(x2-1, y2-1, 3, 3);
//          g.setColor(0x3030A0);
//          g.drawRect(x2-2, y2-2, 5, 5);
                    g.setColor(0x70FF60);
                    g.drawRect(x2, y2, 1, 1);
                    g.setColor(0x3030A0);
                    g.drawRect(x2-1, y2-1, 3, 3);
                } else {
                    g.setColor(0x70FF60);
                    g.drawRect(x2-3, y2-3, 7, 7);
                    g.setColor(0x3030A0);
                    g.drawRect(x2-4, y2-4, 9, 9);
                }
            }
        }

        if ((kind!=TRACKKIND)&&(kind!=NRWAYPOINTSKIND)){
            if (pn!=null){
                p2=pn;
                x2=p2.scrX-MapCanvas.xCenter+dcx;
                y2=p2.scrY-MapCanvas.yCenter+dcy;
                //x2=MapUtil.getXMap(p2.lon, mc.level)-mc.xCenter+dcx;
                //y2=MapUtil.getYMap(p2.lat, mc.level)-mc.yCenter+dcy;
                if (MapCanvas.rotateMap){
                    xt=x2;
                    yt=y2;
                    x2=MapCanvas.rotateMX(xt, yt);
                    y2=MapCanvas.rotateMY(xt, yt);
                }
                g.setColor(0x0);
                int sw=0;

                if (showLabels||showNextAlways){
                    if (p2.name!=null){
                        sw=g.getFont().stringWidth(p2.name)/2+1;
                        g.setColor(p2.backColor);
                        g.fillRect(x2-sw-1, y2-fh-1-10, sw*2+2, fh+2);
                        g.setColor(p2.foreColor);
                        g.drawLine(x2, y2, x2, y2-9);
                        g.drawString(p2.name, x2, y2-10, Graphics.BOTTOM|Graphics.HCENTER);
                        g.drawRect(x2-sw-1, y2-fh-1-10, sw*2+2, fh+2);
//         MapCanvas.drawShadowString(g,p2.name,x2,y2-10,p2.foreColor,Graphics.BOTTOM|Graphics.HCENTER,p2.backColor);

                    }
                }
                g.drawImage(p2.getImageIcon(gpsSystem), x2, y2, Graphics.HCENTER|Graphics.VCENTER);

                if (showLabels||showNextAlways){
                    if (p2.name!=null){
                        g.setColor(0x0);
                        g.drawRect(x2-sw-3+1, y2-fh-3-10+1, sw*2+6, fh+6);
                        g.setColor(0xFFFFFF);
                        g.drawRect(x2-sw-2, y2-fh-2-10, sw*2+4, fh+4);
                        g.drawRect(x2-sw-3, y2-fh-3-10, sw*2+6, fh+6);
                    }
                } else {
                    g.setColor(0x0);
                    g.drawRect(x2-8, y2-8, 16, 16);
                    g.setColor(0xFFFFFF);
                    g.drawRect(x2-8, y2-8, 16, 16);
                    g.drawRect(x2-9, y2-9, 18, 18);
                }
            }
        }
        final String tpi=trackPointInfo;
        if (tpi!=null){
            p2=(MapPoint) pts.elementAt(trackPointInfoIndex);

            x2=p2.scrX+dx;
            y2=p2.scrY+dy;

            if (MapCanvas.rotateMap){
                xt=x2;
                yt=y2;
                x2=MapCanvas.rotateMX(xt, yt);
                y2=MapCanvas.rotateMY(xt, yt);
            }


            g.setColor(0x70FF60);
            g.drawRect(x2-2, y2-2, 5, 5);
            g.setColor(0x3030A0);
            g.drawRect(x2-3, y2-3, 7, 7);


            Font f=g.getFont();
            g.setFont(MapUtil.SMALLFONT);
            MapCanvas.drawMapString(g, tpi, 0, MapCanvas.dcy+5, Graphics.TOP|Graphics.LEFT);
            g.setFont(f);


        }
    }

    final private static int makeTColor(int spd) {
        if (spd>127){
            spd=127;
        }
        return GraphUtils.makeColor(spd+spd, (255-spd-spd), 0x40);
//    int c=MapUtil.makeColor(spd+spd,(127-spd)*192/256,0xFF);
//    return c;
//    0xFFC040
    }
    float[] ha;
    boolean[] hab;
    //float[] har;
    int halength;

    /** Draws route */
    public void drawHY(Graphics g, byte prfMode, boolean vertLines, int to) {
        int fh=g.getFont().getHeight();
        if (pts.size()==0){
            return;
        }
        int x1, y1, x2, y2;
        if (halength!=(MapCanvas.dmaxx-MapCanvas.dminx)){
            halength=(MapCanvas.dmaxx-MapCanvas.dminx);
            ha=new float[halength];
            hab=new boolean[halength];
        }
        MapPoint p1, p2, p3;

        p1=(MapPoint) pts.firstElement();
        p2=(MapPoint) pts.lastElement();
        float totd;
        boolean drawByDist=((prfMode==MapCanvas.PRFSPDMODE)||(prfMode==MapCanvas.PRFALTMODE));
        boolean drawAlt=((prfMode==MapCanvas.PRFALTTIMEMODE)||(prfMode==MapCanvas.PRFALTMODE));
        if (drawByDist){
            totd=p2.dist-p1.dist;
        } else {
            totd=p2.ts-p1.ts;
        }
        float incd=totd/(float) halength;
        float curd=0;
        float nextd=0;
        int apc=pts.size();
        int pn=0;
        int pc;
        float midh;
        int si, ei;
        for (int x=0; x<halength; x++) {
            hab[x]=false;
            nextd+=incd;
            pc=0;
            midh=0;
            si=pn;
            for (int i=si; i<apc; i++) {
                p2=(MapPoint) pts.elementAt(i);


                if (x!=halength-1){
                    if (drawByDist){
                        if (p2.dist>(nextd)){
                            if (i>si){
                                hab[x]=true;
                            }
                            break;
                        }
                    } else {
                        if (p2.ts-p1.ts>(nextd)){
                            if (i>si){
                                hab[x]=true;
                            }
                            break;
                        }
                    }
                } else {
                    hab[x]=true;
                    // break;
                }
                if (drawAlt){
                    midh+=p2.alt;
                } else {
                    midh+=p2.speed;
                }
                pn++;
                pc++;

//        if (drawAlt)
//          midh+= p2.alt;
//        else
//          midh+= p2.speed;
//        if (drawAlt)
//          midh= (float)(midh/pc+ p2.alt/pc);
//        else
//          midh= (float)(midh/pc+ p2.speed/pc);
            }
            if (pc>0){
                midh/=pc;
            }
            ha[x]=midh;
            curd+=incd;
        }

        float mina=1000000, maxa=-1000000;
        for (int x=ha.length-1; x>=0; x--) {
            if (!hab[x]){
                continue;
            }
            if (ha[x]>maxa){
                maxa=ha[x];
            }
            if (ha[x]<mina){
                mina=ha[x];
            }
        }
        //System.arraycopy(ha,0,har,0,ha.length);
        float ph=(float) ((MapCanvas.dmaxy-MapCanvas.dminy)-fh*3-7-to);
        for (int x=ha.length-1; x>=0; x--) {
            if (!hab[x]){
                continue;
            }
            ha[x]=(float) (ph*(ha[x]-mina)/(double) (maxa-mina));
        }
        g.setColor(0xFFFFFF);
        int lastx=0;
        float lasth=ha[0];
        for (int x=0; x<ha.length; x++) {
            //if (ha[x]==0) continue;
            //if (har[x]<0) g.setColor(0x0000FF);
            //else if (har[x]<3500) g.setColor(0x00FF00);
            //else g.setColor(0xFFFFFF);
            if (hab[x]){
                g.drawLine(lastx, MapCanvas.dmaxy-fh-fh-(int) lasth, x, MapCanvas.dmaxy-fh-fh-(int) ha[x]);
                lastx=x;
                lasth=ha[x];
                if (vertLines){
                    g.drawLine(x, MapCanvas.dmaxy-fh-fh, x, MapCanvas.dmaxy-fh-fh-(int) ha[x]);
                }
            }

        }
        if ((!drawAlt)&&(!drawByDist)){
            g.setStrokeStyle(Graphics.DOTTED);
            curd=(float) avgspeed();
            y1=(int) (ph*(curd-mina)/(maxa-mina));
            if ((curd<=maxa)&&(curd>=mina)){
                String s=MapUtil.speedUnitsRound3(avgspeed());
                g.drawLine(0, MapCanvas.dmaxy-fh-fh-y1, MapCanvas.dmaxx, MapCanvas.dmaxy-fh-fh-y1);
                g.setColor(0x0);
                g.fillRect(MapCanvas.dmaxx/8-2, MapCanvas.dmaxy-fh-fh-y1-fh-1, g.getFont().stringWidth(s)+2, fh);
                //g.drawString(s, MapCanvas.dmaxx/8-2, MapCanvas.dmaxy-fh-fh-y1-2, Graphics.LEFT|Graphics.BOTTOM);
                //g.drawString(s, MapCanvas.dmaxx/8, MapCanvas.dmaxy-fh-fh-y1, Graphics.LEFT|Graphics.BOTTOM);
                g.drawLine(0, MapCanvas.dmaxy-fh-fh-y1+1, MapCanvas.dmaxx, MapCanvas.dmaxy-fh-fh-y1+1);
                g.setColor(0xFFFFFF);
                g.drawString(s, MapCanvas.dmaxx/8-1, MapCanvas.dmaxy-fh-fh-y1, Graphics.LEFT|Graphics.BOTTOM);
            }
            g.setStrokeStyle(Graphics.SOLID);
        }
        //g.setColor(0xFFFFFF);
        g.drawLine(MapCanvas.dminx, MapCanvas.dmaxy-fh-fh, MapCanvas.dmaxx, MapCanvas.dmaxy-fh-fh);

        if (drawAlt){
            g.drawString(LangHolder.getString(Lang.maxal)+": "+MapUtil.heightUnits((int) maxa), MapCanvas.dminx, MapCanvas.dmaxy, Graphics.LEFT|Graphics.BOTTOM);
            g.drawString(LangHolder.getString(Lang.minal)+": "+MapUtil.heightUnits((int) mina), MapCanvas.dminx, MapCanvas.dmaxy-fh, Graphics.LEFT|Graphics.BOTTOM);
        } else {
            g.drawString(LangHolder.getString(Lang.maxmsp)+": "+MapUtil.speedUnitsRound3((int) maxa), MapCanvas.dminx, MapCanvas.dmaxy, Graphics.LEFT|Graphics.BOTTOM);
            g.drawString(LangHolder.getString(Lang.minmsp)+": "+MapUtil.speedUnitsRound3((int) mina), MapCanvas.dminx, MapCanvas.dmaxy-fh, Graphics.LEFT|Graphics.BOTTOM);
        }
        p1=(MapPoint) pts.elementAt(pts.size()-1);
        if (drawAlt){
            g.drawString(MapUtil.heightUnits((int) p1.alt), MapCanvas.dmaxx, MapCanvas.dmaxy, Graphics.RIGHT|Graphics.BOTTOM);
        } else {
            g.drawString(MapUtil.speedUnitsRound3((int) p1.speed), MapCanvas.dmaxx, MapCanvas.dmaxy, Graphics.RIGHT|Graphics.BOTTOM);
        }
        if (drawByDist){
            g.drawString(MapUtil.distWithNameRound3(totd), MapCanvas.dcx, MapCanvas.dminy+to, Graphics.HCENTER|Graphics.TOP);
//      totd= (float)(((int)(totd*10.))/10.);
//      g.drawString(String.valueOf(totd)+" "+LangHolder.getString(Lang.km),MapCanvas.dcx,MapCanvas.dminy+fh,Graphics.HCENTER|Graphics.TOP);
        } else {
            //totd= (float)(((int)(totd*10.))/10.);
            g.drawString(MapUtil.time2String((long) totd), MapCanvas.dcx, MapCanvas.dminy+to, Graphics.HCENTER|Graphics.TOP);

        }
        g.drawLine(MapCanvas.dminx, MapCanvas.dminy+fh+to, MapCanvas.dmaxx, MapCanvas.dminy+fh+to);
        g.drawLine(MapCanvas.dminx, MapCanvas.dminy+fh+to, MapCanvas.dminx+5, MapCanvas.dminy+fh+to-3);
        g.drawLine(MapCanvas.dminx, MapCanvas.dminy+fh+to, MapCanvas.dminx+5, MapCanvas.dminy+fh+to+3);
        g.drawLine(MapCanvas.dmaxx, MapCanvas.dminy+fh+to, MapCanvas.dmaxx-5, MapCanvas.dminy+fh+to-3);
        g.drawLine(MapCanvas.dmaxx, MapCanvas.dminy+fh+to, MapCanvas.dmaxx-5, MapCanvas.dminy+fh+to+3);
    }
    //private String sTime="00:00";
    //private String sDist="0000.";
    private String sTA="TA";
    private String sDIST="DIST";
    public int maprouteOffs;

    /** Draws route */
    public void drawPointInfo(Graphics g, Font stdFont, Font smallFont, int offsY, int offsP, boolean drawHeader) {
        int fh=stdFont.getHeight();
        if (pts.size()==0){
            return;
        }
        MapPoint mp;
        double inid, rd;
        if (MapCanvas.gpsBinded&&MapCanvas.map.gpsActive()){
            inid=distFromPrecise(GPSReader.LATITUDE, GPSReader.LONGITUDE);
        } else {
            inid=distFromPrecise(MapCanvas.reallat, MapCanvas.reallon);
        }
        int to=fh+offsY;
        if (drawHeader){
            g.setColor(0xFFFFFF);
            g.drawString(sDIST, 0, to, Graphics.TOP|Graphics.LEFT);
            g.drawString(sTA, MapCanvas.dmaxx, to, Graphics.TOP|Graphics.RIGHT);
            g.drawString(eta, MapCanvas.dcx, to, Graphics.TOP|Graphics.HCENTER);
        } else {
            to-=fh;
            //g.drawLine(0,fh,MapCanvas.dmaxx,fh);
            //if (!MapCanvas.map.gpsActive()) return;
        }
        if (drawHeader){
            to+=fh;
            g.drawLine(0, to, MapCanvas.dmaxx, to);
//      g.setColor(0x505050);
            g.setColor(0x606020);
            g.fillRect(0, to, MapCanvas.dmaxx, fh);
            g.setColor(0xFFFFFF);
            g.drawString(pt2Nav().getName(), MapCanvas.dcx, to, Graphics.TOP|Graphics.HCENTER);
            to+=fh;
            g.drawString(String.valueOf(MapUtil.distUnitsRound3(inid)), 0, to, Graphics.TOP|Graphics.LEFT);
            g.drawString(activeETA(inid, false), MapCanvas.dcx, to, Graphics.TOP|Graphics.HCENTER);
            g.drawString(TA(inid), MapCanvas.dmaxx, to, Graphics.TOP|Graphics.RIGHT);
        }
        double prevDist=pt2Nav().dist;
        //if (offsP==0)offsP++;
        if (actpts+offsP<0){
            maprouteOffs=-actpts;
            offsP=maprouteOffs;
        }
        if (actpts+offsP>=pts.size()){
            maprouteOffs=pts.size()-actpts-1;
            offsP=maprouteOffs;
        }
        for (int i=actpts+offsP; i<pts.size(); i++) {
            mp=(MapPoint) pts.elementAt(i);
            inid+=(mp.dist-prevDist);
            prevDist=mp.dist;

            to+=fh;
            if (i==actpts){
                g.setColor(0x606020);
            } else {
                g.setColor(0x303030);
            }
            g.fillRect(0, to, MapCanvas.dmaxx, fh);
            g.setColor(0xFFFFFF);
            g.drawLine(0, to, MapCanvas.dmaxx, to);
            g.drawString(mp.getName(), MapCanvas.dcx, to, Graphics.TOP|Graphics.HCENTER);
            to+=fh;
            g.drawString(String.valueOf(MapUtil.distUnitsRound3(inid)), 0, to, Graphics.TOP|Graphics.LEFT);
            rd=(inid<0)?-inid:inid;
            g.drawString(activeETA(rd, false), MapCanvas.dcx, to, Graphics.TOP|Graphics.HCENTER);
            g.drawString(TA(rd), MapCanvas.dmaxx, to, Graphics.TOP|Graphics.RIGHT);
            if (to>MapCanvas.dmaxy){
                break;
            }
        }

    }

    //  /** Saves route to stream for persistent storage*/
    //public int getMapPointType(DataOutputStream out) throws Exception {
    /** Saves route to stream for persistent storage*/
    public void save2Stream(DataOutputStream out) throws Exception {
        if (name==null){
            name=");";
        }
        out.writeUTF(name);
        //out.writeBoolean(defTrack);
        out.writeInt(color);
        out.writeByte(kind);
        out.writeByte(gpsSystem);
        //out.writeBoolean(false);//res
        //out.writeByte(0);//res

        out.writeInt(pts.size());
        MapPoint pt;
        for (int i=0; i<pts.size(); i++) {
            pt=(MapPoint) pts.elementAt(i);
            out.writeByte(pt.kind);
            pt.save2Stream(out);
        }
        out.writeInt(5);
        if (actpts>=pts.size()){
            setActPts(0);
        }
        out.writeInt(actpts);
        out.writeInt(maprouteOffs);
        out.writeByte(geoInfo);
        out.writeBoolean(showLabels);

        out.writeInt(mnTrackId);
        out.writeInt(osmTrackId);
        out.writeBoolean(saved);
    }

    /** Saves route info for list to stream */
    public void saveInfo2Stream(DataOutputStream out) throws Exception {
        if (name==null){
            name=");";
        }
        out.writeUTF(name);
        out.writeInt(color);
        out.writeByte(kind);
        out.writeByte(gpsSystem);
        out.writeInt(pts.size());
        out.writeByte(geoInfo);
        out.writeInt(1);
        out.writeInt(mnTrackId);
        out.writeInt(osmTrackId);
        out.writeBoolean(saved);

    }

    /** Create route from stream */
    public MapRoute(DataInputStream in) throws Exception {
        name=in.readUTF();
        //defTrack = in.readBoolean();
        color=in.readInt();
        kind=in.readByte();
        gpsSystem=in.readByte();
        //in.readBoolean();
        //in.readByte();
        int pc=in.readInt();
        pts.ensureCapacity(pc);

        mapLevelScreen=MapCanvas.map.level;
        MapPoint mp=null;
        for (int i=0; i<pc; i++) {
            byte kk=in.readByte();
            if (kk==MapPoint.TYPE_ROUTE){
                mp=new MapPoint(in);
                addMapPoint(mp);
            } else if (kk==MapPoint.TYPE_TRACK){
                mp=new MapPoint(in);
                addTrackMapPoint(mp);
            } else if (kk==MapPoint.TYPE_KML){
                mp=new KMLMapPoint(in);
                addMapPoint(mp);
            }
            mp.kind=kk;
        }

        // if (kind==KMLDOCUMENT){
        //   for (int i=0; i<pc; i++) {
        //     addMapPoint(new KMLMapPoint(in));
        //   }
        // } else if (kind!=TRACKKIND){
        //   for (int i=0; i<pc; i++) {
        //     addMapPoint(new MapPoint(in));
        //   }
        // } else {
        //   for (int i=0; i<pc; i++) {
        //     addTrackMapPoint(new MapPoint(in));
        //   }
        // }
        try {
            int nr=in.readInt();// ����� ������
            actpts=in.readInt();

            maprouteOffs=in.readInt();
            geoInfo=in.readByte();
            showLabels=in.readBoolean();

            mnTrackId=in.readInt();
            osmTrackId=in.readInt();
            saved=in.readBoolean();

        } catch (Throwable t) {
        }
        if (kind==TRACKKIND){
            showLabels=false;
        }
        setActPts(actpts);
    }

    /** Saves track to KML stream */
    public void save2KML(OutputStream out, ProgressResponse progressResponse) throws Exception {
        // StringBuffer trackString = new StringBuffer(3000);
        Util.writeStr2OS(out, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<kml xmlns=\"http://earth.google.com/kml/2.0\">\r\n<Folder>\r\n<name>");
        Util.writeStr2OS(out, name);
        Util.writeStr2OS(out, "</name>\r\n<Style id=\"style\">\r\n<LineStyle>\r\n<color>ff0000ff</color>\r\n<width>1.5</width>\r\n</LineStyle>\r\n<PolyStyle>\r\n<fill>0</fill>\r\n</PolyStyle>\r\n");
        Util.writeStr2OS(out, "</Style>\r\n<open>1</open>\r\n<Placemark>\r\n<styleUrl>#style</styleUrl>\r\n<LineString>\r\n<extrude>0</extrude>\r\n");
        Util.writeStr2OS(out, "<altitudeMode>absolute</altitudeMode>\r\n<coordinates>\r\n");
        for (int i=0; i<pts.size(); i++) {
            if (i%10==0){
                if (progressResponse!=null){
                    progressResponse.setProgress((byte) (100*i/pts.size()), LangHolder.getString(Lang.saving));
                }
            }
            //Enumeration trackEnum=pts.elements();
            //while (trackEnum.hasMoreElements()) {
            MapPoint pos=(MapPoint) pts.elementAt(i);
            //MapPoint pos=(MapPoint) trackEnum.nextElement();
            Util.writeStr2OS(out, String.valueOf(pos.lon)+','
              +String.valueOf(pos.lat)+','+String.valueOf((int) pos.alt)+"\r\n");
        }
        Util.writeStr2OS(out, "</coordinates>\r\n</LineString>\r\n</Placemark>\r\n");

        //trackString += generateWaypointData( waypoints );

        Util.writeStr2OS(out, "</Folder>\r\n</kml>\r\n");

        //out.write(Util.stringToByteArray(trackString.toString(),true));
    }

    /** Saves track to OziFormat stream */
    public void save2OziStream(OutputStream out, ProgressResponse progressResponse) throws Exception {
        if (kind==TRACKKIND){
            String s=
              "OziExplorer Track Point File Version 2.1";
            out.write(Util.restoreCrLfToByteArray(s, true));
            out.write(MapUtil.CRLFb);
            s="WGS 84";
            out.write(Util.restoreCrLfToByteArray(s, true));
            out.write(MapUtil.CRLFb);
            s="Altitude is in Feet";
            out.write(Util.restoreCrLfToByteArray(s, true));
            out.write(MapUtil.CRLFb);
            s="Created by MapNav:  http://mapnav.spb.ru";
            out.write(Util.restoreCrLfToByteArray(s, true));
            out.write(MapUtil.CRLFb);
            s=name;
            s.replace(',', 'C');
            s="0,2,255,"+s+",0,0,2,8421376";
            out.write(Util.restoreCrLfToByteArray(s, RouteSend.EXPORTCODEPAGE==MapRouteLoader.CODEPAGEUTF));
            out.write(MapUtil.CRLFb);
            s=String.valueOf(pts.size());
            out.write(Util.restoreCrLfToByteArray(s, true));
            out.write(MapUtil.CRLFb);
            //MapPoint pt;
//      Enumeration mp=pts.elements();
            for (int i=0; i<pts.size(); i++) {
                if (i%10==0){
                    if (progressResponse!=null){
                        progressResponse.setProgress((byte) (100*i/pts.size()), LangHolder.getString(Lang.saving));
                    }
                }
                //Enumeration trackEnum=pts.elements();
                //while (trackEnum.hasMoreElements()) {
                ((MapPoint) pts.elementAt(i)).save2OziTrackStream(out);
//      pt.save2Stream(out);
            }
        } else if (kind==ROUTEKIND){
            String s="OziExplorer Route File Version 1.0";
            out.write(Util.restoreCrLfToByteArray(s, true));
            out.write(MapUtil.CRLFb);
            s="WGS 84";
            out.write(Util.restoreCrLfToByteArray(s, true));
            out.write(MapUtil.CRLFb);
            s="Reserved 1";
            out.write(Util.restoreCrLfToByteArray(s, true));
            out.write(MapUtil.CRLFb);
            s="Created by MapNav:  http://mapnav.spb.ru";
            out.write(Util.restoreCrLfToByteArray(s, true));
            out.write(MapUtil.CRLFb);
            s="R,  0,"+name+",,255";
            out.write(Util.restoreCrLfToByteArray(s, RouteSend.EXPORTCODEPAGE==MapRouteLoader.CODEPAGEUTF));
            out.write(MapUtil.CRLFb);
            for (int i=0; i<pts.size(); i++) {
                if (i%10==0){
                    if (progressResponse!=null){
                        progressResponse.setProgress((byte) (100*i/pts.size()), LangHolder.getString(Lang.saving));
                    }
                }

                out.write(Util.restoreCrLfToByteArray("W,0,"+String.valueOf(i+1)+','+String.valueOf(i+1)+',', true));
                ((MapPoint) pts.elementAt(i)).save2OziRouteStream(out);
            }

            for (int i=1; i<=100; i++) {
                s="R,"+i+",R"+i+",,16711680";
                out.write(Util.restoreCrLfToByteArray(s, true));
                out.write(MapUtil.CRLFb);
            }

        } else if (kind==WAYPOINTSKIND){
            String s="OziExplorer Waypoint File Version 1.1";
            out.write(Util.restoreCrLfToByteArray(s, true));
            out.write(MapUtil.CRLFb);
            s="WGS 84";
            out.write(Util.restoreCrLfToByteArray(s, true));
            out.write(MapUtil.CRLFb);
            s="Created by MapNav:  http://mapnav.spb.ru";
            out.write(Util.restoreCrLfToByteArray(s, true));
            out.write(MapUtil.CRLFb);
            s="garmin";
            out.write(Util.restoreCrLfToByteArray(s, true));
            out.write(MapUtil.CRLFb);
            for (int i=0; i<pts.size(); i++) {
                if (i%10==0){
                    if (progressResponse!=null){
                        progressResponse.setProgress((byte) (100*i/pts.size()), LangHolder.getString(Lang.saving));
                    }
                }

                out.write(Util.restoreCrLfToByteArray(String.valueOf(i+1)+',', true));
                ((MapPoint) pts.elementAt(i)).save2OziWaypointStream(out);
            }
        }
    }

    public MapRoute createBackRoute() {
        MapRoute mr=new MapRoute(ROUTEKIND);
        for (int i=pts.size()-1; i>=0; i--) {
            mr.addMapPoint((MapPoint) pts.elementAt(i));
        }
        mr.showLabels=false;
        mr.fastDraw=true;
        mr.color=0xFFA000;
        return mr;
    }

    public void autoName() {
        MapPoint mp;
        for (int i=0; i<pts.size(); i++) {
            mp=(MapPoint) pts.elementAt(i);
            mp.name=String.valueOf(i+1);
        }
    }

    private void makePointsRoute() {
        for (int i=pts.size()-1; i>=0; i--) {
            ((MapPoint) pts.elementAt(i)).kind=MapPoint.TYPE_ROUTE;
        }
    }

    public MapRoute clone(boolean reverse) {
        MapRoute mr=new MapRoute(kind);
        if (kind==KMLDOCUMENT){
            for (int i=0; i<pts.size(); i++) {
                MapPoint p= (MapPoint) pts.elementAt(i);
                MapPoint mp = new MapPoint(p.lat, p.lon, p.alt, p.name);
                mp.ts=p.ts;
                mp.pointSymbol=p.pointSymbol;
                mp.visible=true;
                mp.dist=p.dist;
                mp.speed=p.speed;
                mr.addMapPoint(mp);
            }

        }else
        if (reverse){
            for (int i=pts.size()-1; i>=0; i--) {
                mr.addMapPoint((MapPoint) pts.elementAt(i));
            }
        } else {
            for (int i=0; i<pts.size(); i++) {
                mr.addMapPoint((MapPoint) pts.elementAt(i));
            }
        }
        makePointsRoute();
        mr.showLabels=showLabels;
        mr.fastDraw=fastDraw;
        mr.color=color;
        mr.gpsSystem=gpsSystem;
        mr.name=name;
        //MapRoute.selColor=selColor;
        return mr;
    }
    private String eta="ETA ";
    private String noeta="ETA --:--";
    private String nota="--:--";
    private String ta="TA ";

    public String activeETA() {
        return activeETA(distFromPrecise(GPSReader.LATITUDE, GPSReader.LONGITUDE), true);
    }

    public String activeETAAndTA() {
        double dist=distFromPrecise(GPSReader.LATITUDE, GPSReader.LONGITUDE);
        double totdist=distLeft2Nav(dist);
        return activeETA(dist, true)+" ("+activeETA(totdist, false)+")";
    }

    /** ���������� �� ������� ���. ����� �� ��������� ���� ��������� ����������
     */
    public double distLeft2Nav(double dist2NavPt) {
        return lastPoint().dist-pt2Nav().dist+dist2NavPt;
    }

    private static double normalizeCourseG(double relCourse) {
        if (relCourse>180){
            relCourse=relCourse-360;
        }
        if (relCourse<-180){
            relCourse=relCourse+360;
        }
        return relCourse;
    }

    private static double normalizeCourseR(double relCourse) {
        if (relCourse>MapUtil.PI){
            return relCourse-MapUtil.PImul2;
        }
        if (relCourse<-180){
            return relCourse+MapUtil.PImul2;
            //relCourse=relCourse+360;
        }
        return relCourse;
    }

    private String timeA(float time, boolean ETA, boolean showName) {
        int days=(int) (time/24.); //1
        int h=(int) (time-days*24); //4.76
        int m=(int) ((time-days*24-h)*60.);

        String tName=(ETA)?eta:ta;
        if (days>9){
            if (showName){
                return noeta;
            } else {
                return nota;
            }
        } else if (days>0){
            if (showName){
                return tName+String.valueOf(days)+' '+MapUtil.numStr(h, 2)+':'+MapUtil.numStr(m, 2);
            } else {
                return String.valueOf(days)+' '+MapUtil.numStr(h, 2)+':'+MapUtil.numStr(m, 2);
            }
        } else {
            if (showName){
                return tName+MapUtil.numStr(h, 2)+':'+MapUtil.numStr(m, 2);
            } else {
                return MapUtil.numStr(h, 2)+':'+MapUtil.numStr(m, 2);
            }
        }

    }
    private long avsCoordSavedTime;
    private double avsLat, avsLon;
    private float avsSpeed;
    private final static long AVERAGE_SPD_DURATION=30000;

    /**
     * Returns average by 30 sec speed in km/h
     *
     */
    public float averageGPSSpeed() {
        if (avsCoordSavedTime==0){
            if (GPSReader.NUM_SATELITES>0){
                avsSpeed=GPSReader.SPEED_KMH;
                avsLat=GPSReader.LATITUDE;
                avsLon=GPSReader.LONGITUDE;
                avsCoordSavedTime=System.currentTimeMillis();
            }
            if (avsSpeed<0.0001f){
                avsSpeed=0.001f;
            }
            return avsSpeed;
        } else if ((System.currentTimeMillis()-avsCoordSavedTime>AVERAGE_SPD_DURATION)&&(GPSReader.NUM_SATELITES>0)){
            double cLat=GPSReader.LATITUDE, cLon=GPSReader.LONGITUDE;
            long tm=System.currentTimeMillis();
            avsSpeed=(float) (distBetweenCoords(avsLat, avsLon, cLat, cLon)*3600000/(tm-avsCoordSavedTime));
            avsLat=cLat;
            avsLon=cLon;
            avsCoordSavedTime=tm;
            if (avsSpeed<0.0001f){
                avsSpeed=0.001f;
            }
            return avsSpeed;
        } else {
            return avsSpeed;
        }
    }

    /**
     * ���������� ����� � ����� ��� ����������� ���������� ���������� � ��� ���������
     *
     *
     */
    private float activeETAH(double dist) {

        return (float) (dist/averageGPSSpeed());///time in hours  28.76

    }

    public String activeETA(double dist, boolean showName) {
        return timeA(activeETAH(dist), true, showName);//+"::"+averageGPSSpeed();
    }

    public String TAfull(double dist) {
        return ta+TA(dist);
    }

    public String TA(double dist) {

        float crsr=(float) (dist/averageGPSSpeed());///time in hours  28.76
        //crsr=crsr;
        //float ct = MapPoint.javaTime2WinTime(System.currentTimeMillis());
        //long t =System.currentTimeMillis()+offsetUTC;// dt.getTime();
        double ct=MapPoint.javaTime2WinTime(System.currentTimeMillis()+offsetUTC);
        double tct=(float) Math.floor(ct);
        crsr+=24.f*(ct-tct);

        return timeA(crsr, false, false);
//    int days = (int)(crsr/24.); //1
//    int h = (int)(crsr-days*24); //4.76
//    int m = (int)((crsr-days*24-h)*60.);
//    String s;
//    if (days>9) {
//      return nota;
//    } else
//      if (days>0){
//      s=String.valueOf(days)+' '+MapUtil.make2(h)+':'+MapUtil.make2(m);
//      } else {
//      s=MapUtil.make2(h)+':'+MapUtil.make2(m);
//      }
//    return s;
    }

    public void loadGeoCacheInfo() {
        TextCanvas tc=new TextCanvas(pt2Nav().getName(), (int) pt2Nav().ts, geoInfo);
    }

    public void loadGeoCachePage() {
        if (geoInfo==GEOINFO_GEOCACHE){
            try {
                MapForms.mM.platformRequest("http://www.geocaching.su/?pn=101&cid="+pt2Nav().ts);
            } catch (Throwable ex) {
                //ex.printStackTrace();
            }
        }
        if (geoInfo==GEOINFO_GEOCACHECOM){
            try {
                MapForms.mM.platformRequest(pt2Nav().tag);
            } catch (Throwable ex) {
                //ex.printStackTrace();
            }
        }
    }

    public static MapRoute createRoute(byte kind) {
        MapRoute mr=new MapRoute(kind);
        if (kind==MapRoute.WAYPOINTSKIND){
            mr.name="WP"+MapUtil.trackNameAuto();
        } else {
            mr.name="RT"+MapUtil.trackNameAuto();
        }
        return mr;
    }
    public static String ELE_END="</ele>\n";
    public static String TAG_ELE="<ele>";
    public static String TAG_END="\">\n";
    public static String TIME="<time>";
    public static String TIMETRKPT_END="</time>\n</trkpt>\n";
    public static String _LON="\" lon=\"";
    private static String TRKPT_LAT="<trkpt lat=\"";

    public void save2GPX(OutputStream outputstream, ProgressResponse progressResponse) throws Exception {
        Util.writeStr2OSAscii(outputstream, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        Util.writeStr2OSAscii(outputstream, "<gpx version=\"1.1\" creator=\"MapNav "+MapUtil.version+" http://mapnav.spb.ru\"\n"
          +" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n xmlns=\"http://www.topografix.com/GPX/1/1\"\n "
          +" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/gpx/1/1/gpx.xsd\">\n");

        Util.writeStr2OS(outputstream, "<metadata>\n<name>"+name+"</name>\n<author><name>"+RMSOption.netRadarLogin+"</name></author>\n</metadata>\n");
        if (kind==ROUTEKIND){
            Util.writeStr2OS(outputstream, "<rte>\n<name>"+name+"</name>\n");
            MapPoint mp;
            for (int i=0; i<pts.size(); i++) {
                if (i%10==0){
                    if (progressResponse!=null){
                        progressResponse.setProgress((byte) (100*i/pts.size()), LangHolder.getString(Lang.saving));
                    }
                }

                mp=(MapPoint) pts.elementAt(i);
                double f=MapUtil.coordRound5(mp.lat);
                double f1=MapUtil.coordRound5(mp.lon);

                Util.writeStr2OSAscii(outputstream, "<rtept lat=\""+f+_LON+f1+TAG_END);

                Util.writeStr2OSAscii(outputstream, TAG_ELE+mp.alt+ELE_END);

                Util.writeStr2OS(outputstream, "<name>"+mp.getName()+"</name>\n");
                Util.writeStr2OSAscii(outputstream, "</rtept>\n");
            }
            Util.writeStr2OSAscii(outputstream, "</rte>\n");
        } else if (kind==WAYPOINTSKIND){
            MapPoint mp;
            for (int i=0; i<pts.size(); i++) {
                if (i%10==0){
                    if (progressResponse!=null){
                        progressResponse.setProgress((byte) (100*i/pts.size()), LangHolder.getString(Lang.saving));
                    }
                }

                mp=(MapPoint) pts.elementAt(i);
                double f=MapUtil.coordRound5(mp.lat);
                double f1=MapUtil.coordRound5(mp.lon);

                Util.writeStr2OSAscii(outputstream, "<wpt lat=\""+f+_LON+f1+TAG_END);
                Util.writeStr2OSAscii(outputstream, TAG_ELE+((int) mp.alt)+ELE_END);
                Util.writeStr2OS(outputstream, "<name>"+mp.getName()+"</name>\n");
                Util.writeStr2OSAscii(outputstream, "</wpt>\n");
            }
        } else if (kind==TRACKKIND){
            Util.writeStr2OS(outputstream, "<trk>\n<name>"+name+"</name><trkseg>\n");
            MapPoint mp;
            Calendar calendar=Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
            for (int i=0; i<pts.size(); i++) {
                if (i%20==0){
                    if (progressResponse!=null){
                        progressResponse.setProgress((byte) (100*i/pts.size()), LangHolder.getString(Lang.saving));
                    }
                }

                mp=(MapPoint) pts.elementAt(i);
                double f=MapUtil.coordRound5(mp.lat);
                double f1=MapUtil.coordRound5(mp.lon);

                Util.writeStr2OSAscii(outputstream, TRKPT_LAT);
                Util.writeStr2OSAscii(outputstream, String.valueOf(f));
                Util.writeStr2OSAscii(outputstream, _LON);
                Util.writeStr2OSAscii(outputstream, String.valueOf(f1));
                Util.writeStr2OSAscii(outputstream, TAG_END);

                Util.writeStr2OSAscii(outputstream, TAG_ELE);
                Util.writeStr2OSAscii(outputstream, String.valueOf((mp.alt)));
                Util.writeStr2OSAscii(outputstream, ELE_END);

                calendar.setTime(new Date(mp.ts));
                String s=String.valueOf(calendar.get(Calendar.YEAR))+'-'
                  +MapUtil.numStr(calendar.get(Calendar.MONTH)+1, 2)+'-'
                  +MapUtil.numStr(calendar.get(Calendar.DAY_OF_MONTH), 2)+'T'
                  +MapUtil.numStr(calendar.get(Calendar.HOUR_OF_DAY), 2)+':'
                  +MapUtil.numStr(calendar.get(Calendar.MINUTE), 2)+':'
                  +MapUtil.numStr(calendar.get(Calendar.SECOND), 2)+'Z';
                Util.writeStr2OSAscii(outputstream, TIME);
                Util.writeStr2OSAscii(outputstream, s);
                Util.writeStr2OSAscii(outputstream, TIMETRKPT_END);
            }

            Util.writeStr2OSAscii(outputstream, "</trkseg>\n</trk>\n");
        }
        Util.writeStr2OSAscii(outputstream, "</gpx>");

    }
    private byte playType;
    private static final byte PLAYSAVE=0;
    private static final byte PLAYTRACK=1;
    private Thread playThread;
    private boolean slowPlay;

    public void stopPlay() {
        playThread=null;
    }

    public void startPlay(boolean slowPlay) {
        this.slowPlay=slowPlay;
        playType=PLAYTRACK;
        playThread=new Thread(this);
        playThread.start();
    }

    public void run() {
        if (playType==PLAYSAVE){
//#mdebug
//#             try {
//#enddebug
                MapRoute mr=this;
                MapCanvas.map.rmss.saveRoute(mr);
                mr=null;
                MapCanvas.map.rmss.writeSettingNow();
//#mdebug
//#             } catch (Throwable t) {
//#                 if (RMSOption.debugEnabled){
//#                     DebugLog.add2Log("SR:"+t);
//#                 }
//#             }
//#enddebug
        } else if (playType==PLAYTRACK){

            long startPlayTime=System.currentTimeMillis();
            MapPoint mp;
            try {
                Thread.sleep(2000);
                for (int i=0; i<pts.size(); i++) {
                    while (!MapCanvas.map.isShown()) {
                        Thread.sleep(200);
                    }
                    mp=(MapPoint) pts.elementAt(i);
                    MapCanvas.map.setLocation(mp.lat, mp.lon);
                    MapCanvas.map.repaint();
                    MapCanvas.map.serviceRepaints();
                    if (!slowPlay){
                        while ((MapTile.queueSize()!=0)) {
                            Thread.sleep(200);
                        }
                    } else {
                        Thread.sleep(300);
                    }
                    if (playThread==null){
                        break;
                    }
                }
            } catch (Throwable t) {
            }
            playThread=null;
            MapCanvas.map.startTrack(null);
            MapCanvas.map.repaint();

        }
    }

    public int getActpts() {
        return actpts;
    }

    /** Draws route */
    public boolean drawDirection(Graphics g, int x, int y) {
        //  if (GPSReader.NUM_SATELITES>0){
        if (!((kind==ROUTEKIND)||(kind==KMLDOCUMENT))){
            return false;
        }
        if ((pts.size()>1)&&(actpts<pts.size()-1)){
            if (needShowMark()){
                if ((actpts==0)||(markShowState==MARK_STATE_UNKNOWN)
                  ||((markShowState==MARK_STATE_NEXT)&&(markCoursePointIndex!=actpts))){
                    double crsC=(actpts==0)?normalizeCourseR(courseToCoords(GPSReader.LATITUDE, GPSReader.LONGITUDE, pt2Nav().lat, pt2Nav().lon)):normalizeCourseR(courseStep());
                    //double crsC=normalizeCourseR(courseToCoords(MapCanvas.reallat, MapCanvas.reallon, pt2Nav().lat, pt2Nav().lon));
                    double crsN=normalizeCourseR(courseNextStep());
                    crsC=courseDiffAbs(crsC, crsN)*MapUtil.R2G;
                    setMarkCourse(actpts, crsC, MARK_STATE_NEXT);
                }
                //crsC=-140;
                if (markShowState!=MARK_STATE_KEEP){
                    g.setColor(GraphUtils.fadeColor(RMSOption.shadowColor, 90));
                } else {
                    g.setColor(RMSOption.shadowColor);
                }
                drawDirectionMark(g, x-g.getFont().getHeight()/4, y-g.getFont().getHeight()/4, 9*g.getFont().getHeight()/2);
                //drawDirectionMark(g, x+g.getFont().getHeight()/4, y+g.getFont().getHeight()/4, 7*g.getFont().getHeight()/2, crs);
                if (markShowState!=MARK_STATE_KEEP){
                    g.setColor(GraphUtils.fadeColor(RMSOption.foreColor, 90));
                } else {
                    g.setColor(RMSOption.foreColor);
                }
                drawDirectionMark(g, x, y, g.getFont().getHeight()*4);
                return true;
            }
        }
        return false;
        //   }
        //   return false;
    }
    private static final byte MARK_STATE_UNKNOWN=0;
    private static final byte MARK_STATE_NEXT=1;
    private static final byte MARK_STATE_KEEP=2;
    private byte markShowState=MARK_STATE_NEXT;
    private double markCourse;
    private int markCoursePointIndex=-1;

    public void setMarkCourse(int ptIndex, double crs, byte state) {
        markCourse=crs;
        markCoursePointIndex=ptIndex;
        markShowState=state;
    }
    //private double savedMarkCourse;
    //private int savedMarkPointIndex;
    //public void saveCurrentMark(){
    //    savedMarkCourse=markCourse;
    //    markCoursePointIndex=savedMarkPointIndex;
    // }
    private long lastTimeMarkShowed;
    private long markShowInterval=1000;
    private long markShowDuration=2000;
    private boolean markVisible;

    private boolean needShowMark() {
        //if (actpts==0) return false;
        if (markVisible){
            if (System.currentTimeMillis()-lastTimeMarkShowed>markShowDuration){
                markVisible=false;
                lastTimeMarkShowed=System.currentTimeMillis();
            }
        } else {
            if (System.currentTimeMillis()-lastTimeMarkShowed>markShowInterval){
                markVisible=true;
                lastTimeMarkShowed=System.currentTimeMillis();
            }
        }
        if (markShowState==MARK_STATE_NEXT){
            float h2nav=activeETAH(distFromPrecise(GPSReader.LATITUDE, GPSReader.LONGITUDE)-0.040);
            //30s
            if (h2nav<1d/120d){
                markShowInterval=2000;
                markShowDuration=2000;
            } else if (h2nav<1d/60d){
                markShowInterval=10000;
                markShowDuration=2000;
            } else if (h2nav<5d/60d){
                markShowInterval=30000;
                markShowDuration=2000;
            } else {
                markShowInterval=120000;
                markShowDuration=2000;
            }
        } else if (markShowState==MARK_STATE_KEEP){
            markShowInterval=0;//1000;
            markShowDuration=5000;
        }
//            markShowInterval=1000;
        return markVisible;
    }

    private void drawDirectionMark(Graphics g, int x, int y, int size) {
        if (markShowState==MARK_STATE_UNKNOWN){
            return;
        }
        int ux=size/8;
        int uy=ux;
        int ox=x+(size-ux*8)/2;
        int oy=y+(size-uy*8)/2;
        double relCourse=normalizeCourseG(markCourse);
        if ((relCourse>-30)&&(relCourse<30)){
            g.fillTriangle(ox+ux*4, oy, ox+ux*2, oy+uy*2, ox+ux*6, oy+uy*2);
            g.fillRect(ox+ux*3, oy+uy*2, ux*2, uy*6);
        } else if ((relCourse>=30)&&(relCourse<60)){
            ox=ox+ux*8;
            g.fillTriangle(ox, oy, ox-ux*4, oy, ox, oy+uy*4);
            g.fillTriangle(ox, oy+uy*2, ox-ux*2, oy, ox-ux*2, oy+uy*4);
            g.fillTriangle(ox-ux*2, oy, ox-ux*2, oy+uy*4, ox-ux*6, oy+uy*4);
            g.fillTriangle(ox-ux*4, oy+uy*6, ox-ux*2, oy+uy*4, ox-ux*6, oy+uy*4);
            g.fillRect(ox-ux*6, oy+uy*4, ux*2, uy*4);
        } else if ((relCourse<=-30)&&(relCourse>-60)){
            g.fillTriangle(ox, oy, ox+ux*4, oy, ox, oy+uy*4);
            g.fillTriangle(ox, oy+uy*2, ox+ux*2, oy, ox+ux*2, oy+uy*4);
            g.fillTriangle(ox+ux*2, oy, ox+ux*2, oy+uy*4, ox+ux*6, oy+uy*4);
            g.fillTriangle(ox+ux*4, oy+uy*6, ox+ux*2, oy+uy*4, ox+ux*6, oy+uy*4);
            g.fillRect(ox+ux*4, oy+uy*4, ux*2, uy*4);
        } else if ((relCourse>=60)&&(relCourse<120)){
            ox=ox+ux*8;
            g.fillTriangle(ox, oy+uy*3, ox-ux*3, oy, ox-ux*3, oy+uy*6);
            g.fillRect(ox-ux*8, oy+uy*2, ux*6, uy*2);
            g.fillRect(ox-ux*8, oy+uy*4, ux*2, uy*4);
        } else if ((relCourse<=-60)&&(relCourse>-120)){
            g.fillTriangle(ox, oy+uy*3, ox+ux*3, oy, ox+ux*3, oy+uy*6);
            g.fillRect(ox+ux*2, oy+uy*2, ux*6, uy*2);
            g.fillRect(ox+ux*6, oy+uy*4, ux*2, uy*4);
        } else if ((relCourse>=120)&&(relCourse<150)){
            ox=ox+ux*8;
            g.fillTriangle(ox, oy+uy*6, ox, oy+uy*2, ox-ux*4, oy+uy*6);
            g.fillTriangle(ox, oy+uy*4, ox-ux*2, oy+uy*2, ox-ux*2, oy+uy*6);
            g.fillTriangle(ox-ux*2, oy+uy*2, ox-ux*2, oy+uy*6, ox-ux*6, oy+uy*2);
            g.fillTriangle(ox-ux*2, oy+uy*2, ox-ux*4, oy, ox-ux*6, oy+uy*2);
            g.fillRect(ox-ux*8, oy, ux*4, uy*2);
            g.fillRect(ox-ux*8, oy+uy*2, ux*2, uy*6);
        } else if ((relCourse<=-120)&&(relCourse>-150)){
            g.fillTriangle(ox, oy+uy*6, ox, oy+uy*2, ox+ux*4, oy+uy*6);
            g.fillTriangle(ox, oy+uy*4, ox+ux*2, oy+uy*2, ox+ux*2, oy+uy*6);
            g.fillTriangle(ox+ux*2, oy+uy*2, ox+ux*2, oy+uy*6, ox+ux*6, oy+uy*2);
            g.fillTriangle(ox+ux*2, oy+uy*2, ox+ux*4, oy, ox+ux*6, oy+uy*2);
            g.fillRect(ox+ux*4, oy, ux*4, uy*2);
            g.fillRect(ox+ux*6, oy+uy*2, ux*2, uy*6);
        } else if (((relCourse<=-150)&&(relCourse>=-180))||((relCourse>=150)&&(relCourse<=180))){
            g.fillTriangle(ox+ux*4, oy+uy*8, ox+ux*2, oy+uy*6, ox+ux*6, oy+uy*6);
            g.fillArc(ox+ux*2, oy+uy*1, ux*4, uy*4, 0, 360);
            g.fillRect(ox+ux*3, oy, ux*2, uy*6);
        } else {
            g.fillRect(ox, oy, ux*2, uy*2);
            g.fillRect(ox, oy+uy*6, ux*2, uy*2);
            g.fillRect(ox+ux*6, oy, ux*2, uy*2);
            g.fillRect(ox+ux*6, oy+uy*6, ux*2, uy*2);
        }


    }
    public boolean backupFailed;

    public static int getStatusColor(MapRoute mr) {
        int statusColor;
        if (mr==null){
            statusColor=(GPSReader.WRITETRACK)?(0x00FF00):0xFFFF00;
        } else if (mr.backupFailed){
            statusColor=0xFF2020;
        } else {
            statusColor=(GPSReader.WRITETRACK)?((mr.defTrack)?((mr.pts.size()==MapRoute.DEF_PTS_COUNT)?0xFF8080:0xD020D0):0x00FF00):0xFFFF00;
        }
        return statusColor;
    }
    //  private Image trGreenImage;
//  private Image trYellImage;
//
//  public Image getStatImage(){
//    if (kind==TRACKKIND){
//      if (GPSReader.WRITETRACK){
//        if (trGreenImage==null) {
//          try {
//            trGreenImage = Image.createImage("/img/trgr.png");
//          } catch(IOException _ex) { }
//          return trGreenImage;
//        } else return trGreenImage;
//      } else {
//        if (trYellImage==null) {
//          try {
//            trYellImage = Image.createImage("/img/tryl.png");
//          } catch(IOException _ex) { }
//          return trYellImage;
//        } else return trYellImage;
//      }
//
//    } else return null;
//  }
}
