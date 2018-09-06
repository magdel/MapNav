/*
 * GPSRouteLoader.java
 *
 * Created on 13 ������ 2007 �., 4:28
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package RPMap;

import app.MapForms;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Vector;
import misc.MVector;
import misc.ProgressResponse;
import misc.ProgressStoppable;
import misc.Util;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 *
 * @author RFK
 */
public abstract class MapRouteLoader implements Runnable, ProgressStoppable {

    public static final byte CODEPAGEUTF=0;
    public static final byte CODEPAGEWIN1251=1;
    public static final byte FORMATOZI=0;
    public static final byte FORMATKML=1;
    public static final byte FORMATGPX=2;
    public static final byte FORMATLOC=3;
    String furl;
    private String[] result=new String[25];
    private MVector routes=new MVector();
    byte loadKind;
    byte CP;
    byte format;
    protected boolean stopped;
    private Calendar calendar;

    public MapRouteLoader(String url, byte kind, byte CP, byte format, boolean autoStart) {
        furl=url;
        loadKind=kind;
        this.CP=CP;
        this.format=format;

        calendar=Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        if (autoStart){
            Thread t=new Thread(this);
            t.start();
        }
    }

    public MapRouteLoader(String url, byte kind, byte CP, byte format) {
        this(url, kind, CP, format, true);
    }

    public MapRoute getRoute() {
        if (routes.size()>0) {
            return (MapRoute) routes.elementAt(0);
        }
        return null;
    }

    void loadFromStream(InputStream is) throws IOException {
        if (format==FORMATLOC){
            //if (loadKind==MapRoute.ROUTEKIND) loadGPXWayPointsFromStream(is,true);
            //else
            if (loadKind==MapRoute.WAYPOINTSKIND){
                loadLOCWayPointsFromStream(is, false);
            }
            //else if (loadKind==MapRoute.TRACKKIND) loadTrackFromStream(is);
        } else if (format==FORMATGPX){
            //if (loadKind==MapRoute.ROUTEKIND) loadGPXWayPointsFromStream(is,true);
            //else if (loadKind==MapRoute.WAYPOINTSKIND) loadGPXWayPointsFromStream(is,false);
            //else if (loadKind==MapRoute.TRACKKIND)
            loadGPXWayPointsFromStream(is, loadKind);
        } else if (CP==CODEPAGEUTF){
            if (loadKind==MapRoute.ROUTEKIND){
                loadRouteFromStream(is);
            } else if (loadKind==MapRoute.WAYPOINTSKIND){
                loadWayPointsFromStream(is);
            } else if (loadKind==MapRoute.TRACKKIND){
                loadTrackFromOZIStream(is);
            }
        } else {
            ByteArrayOutputStream baos=new ByteArrayOutputStream(10000);
            byte[] tb=new byte[200];
            boolean allRead=false;
            int n1;
            int rb;
            do {
                n1=0;
                while (n1!=tb.length) {
                    rb=is.read(tb, n1, tb.length-n1);
                    if (rb<0){
                        allRead=true;
                        break;
                    }
                    n1=n1+rb;
                }
                baos.write(tb, 0, n1);
            } while (!allRead);
            String s=Util.byteArrayToString(baos.toByteArray(), false);
            baos=null;
            ByteArrayInputStream bais=new ByteArrayInputStream(Util.stringToByteArray(s, true));
            s=null;
            if (stopped){
                return;
            }
            if (loadKind==MapRoute.ROUTEKIND){
                loadRouteFromStream(bais);
            } else if (loadKind==MapRoute.WAYPOINTSKIND){
                loadWayPointsFromStream(bais);
            } else if (loadKind==MapRoute.TRACKKIND){
                loadTrackFromOZIStream(bais);
            }
        }
    }

    void loadRouteFromStream(InputStream is) throws IOException {

        int i=0;
        //char c;
        //String s = MapUtil.emptyString;
        String[] data;
        String DATA_STRING;
        char TYPE;
        MapRoute route=null;
        MapPoint pt=null;
        int rId;
        boolean inRoute=false;
        ByteArrayOutputStream baos=new ByteArrayOutputStream(150);

        // Start reading the data from the file
        do {
            i=is.read(); // read one byte at the time.
            baos.write(i);
            if (stopped){
                return;
            }

            if (i==10){ // Every route starts with CRLF'
                if (baos.size()>5){
                    DATA_STRING=Util.byteArrayToString(baos.toByteArray(), true);
                    //DATA_STRING = String.valueOf(s);//s.substring(5, s.length());
                    TYPE=DATA_STRING.charAt(0);

                    // Check the gps data type and convert the information to a more readable format.
                    if (TYPE=='R'){
                        try {
                            data=MapUtil.parseString(DATA_STRING, ',');
                            rId=Integer.parseInt(data[0]);
                            String nam=data[1];
                            int acolor=Integer.parseInt(data[3]);
                            acolor=swapOziColor(acolor);
                            route=new MapRoute(MapRoute.ROUTEKIND);
                            routes.addElement(route);
                            route.name=nam;
                            route.color=acolor;
                            inRoute=true;
                        } catch (Exception e) {
                        }

                    } else if (TYPE=='W'){
                        if (inRoute){
                            try {
                                data=MapUtil.parseString(DATA_STRING, ',');
                                double la=Double.parseDouble(data[4]);
                                double lo=Double.parseDouble(data[5]);
                                String nam=data[3];

                                if (data[12]!=null){
                                    if (nam!=null){
                                        if (data[12].length()>nam.length()){
                                            nam=data[12];
                                        }
                                    }
                                }

                                int pS=Integer.parseInt(data[7]);
                                int fC=swapOziColor(Integer.parseInt(data[10]));
                                int bC=swapOziColor(Integer.parseInt(data[11]));

                                pt=new MapPoint(la, lo, 0, nam);
                                pt.pointSymbol=(byte) pS;
                                pt.foreColor=fC;
                                pt.backColor=bC;

                                route.addMapPoint(pt);

                            } catch (Exception e) {
                            }
                        }

                    } else {
                        inRoute=false;
                        route=null;
                    }
                }

                //TYPE +=  ":" + exc;
                //s = MapUtil.emptyString;
                baos.reset();
            }
        } while ((i!=-1));
    }

    void loadWayPointsFromStream(InputStream is) throws IOException {

        int i=0;
        //char c;
        //String s = MapUtil.emptyString;
        String[] data;
        String DATA_STRING;
        //char TYPE;
        MapRoute route=null;
        MapPoint pt=null;
        int rId;
        boolean inRoute=false;
        int nR=0;

        route=new MapRoute(MapRoute.WAYPOINTSKIND);
        routes.addElement(route);
        route.name=MapUtil.extractFilename(furl);

        inRoute=true;
        ByteArrayOutputStream baos=new ByteArrayOutputStream(150);
        // Start reading the data from the file
        do {
            i=is.read(); // read one byte at the time.
            baos.write(i);
            //c = (char)i;
            //s += c;
            if (stopped){
                return;
            }
            if (i==10){ // Every route starts with CRLF'
                if (baos.size()>3){
                    if (nR>3){
                        //DATA_STRING = String.valueOf(s);//s.substring(5, s.length());
                        //TYPE = s.charAt(0);
                        DATA_STRING=Util.byteArrayToString(baos.toByteArray(), true);
                        // Check the gps data type and convert the information to a more readable format.
                        //if(TYPE == 'W'){
                        if (inRoute){
                            try {
                                data=MapUtil.parseString(DATA_STRING, ',');
                                double la=Double.parseDouble(data[1]);
                                double lo=Double.parseDouble(data[2]);
                                String nam=data[0];
                                if (data[9]!=null){
                                    if (nam!=null){
                                        if (data[9].length()>nam.length()){
                                            nam=data[9];
                                        }
                                    }
                                }
                                int pS=Integer.parseInt(data[4]);
                                int fC=swapOziColor(Integer.parseInt(data[7]));
                                int bC=swapOziColor(Integer.parseInt(data[8]));

                                pt=new MapPoint(la, lo, 0, nam);
                                pt.pointSymbol=(byte) pS;
                                pt.foreColor=fC;
                                pt.backColor=bC;
                                //pt.kind=MapPoint.WAYPOINT;

                                route.addMapPoint(pt);

                            } catch (Exception e) {
                                //       }
                            }
                        }
                        //  else {
                        //    inRoute=false;
                        //    route=null;
                        //    }
                    }
                }
                nR++;
                //TYPE +=  ":" + exc;
                baos.reset();
                // s = MapUtil.emptyString;
            }
        } while ((i!=-1));
    }

    void loadTrackFromOZIStream(InputStream is) throws IOException {

        int i=0;
        //char c;
        //String s = MapUtil.emptyString;
        String[] data;
        String DATA_STRING;
        //char TYPE;
        MapRoute route=null;
        MapPoint pt=null;
        int rId;
        boolean inRoute=false;
        int nR=0;

        route=new MapRoute(MapRoute.TRACKKIND);
        routes.addElement(route);
        //route.name=furl.substring(furl.length()-7);
        inRoute=true;
        ByteArrayOutputStream baos=new ByteArrayOutputStream(150);
        // Start reading the data from the file
        do {
            i=is.read(); // read one byte at the time.
            baos.write(i);
            //c = (char)i;
            //s += c;
            if (stopped){
                return;
            }
            if (i==10){ // Every route starts with CRLF'
                if (baos.size()>3){
                    if (nR==4){
                        try {
                            DATA_STRING=Util.byteArrayToString(baos.toByteArray(), true);
                            data=MapUtil.parseString(DATA_STRING, ',');
                            route.name=data[2];
                            int fC=swapOziColor(Integer.parseInt(data[1]));
                            route.color=fC;
                        } catch (Exception e) {
                            route.name="Noname track";
                        }

                    }
                }
                if (nR>5){
                    //DATA_STRING = String.valueOf(s);//s.substring(5, s.length());
                    //TYPE = s.charAt(0);
                    DATA_STRING=Util.byteArrayToString(baos.toByteArray(), true);
                    // Check the gps data type and convert the information to a more readable format.
                    //if(TYPE == 'W'){
                    if (inRoute){
                        try {
                            data=MapUtil.parseString(','+DATA_STRING, ',');
                            double la=Double.parseDouble(data[0]);
                            double lo=Double.parseDouble(data[1]);
                            int al=(int) (Double.parseDouble(data[3])/3.280839895);
                            long ts=MapPoint.winTime2JavaTime(Double.parseDouble(data[4]));
                            pt=new MapPoint(la, lo, al, ts, null);
                            route.addMapPointFromTrackLoad(pt);

                        } catch (Exception e) {
                        }
                    }
                }
                nR++;
                //TYPE +=  ":" + exc;
                baos.reset();
                // s = MapUtil.emptyString;
            }
        } while ((i!=-1));
    }

    public void setProgressResponse(ProgressResponse progressResponse) {
    }

    public boolean stopIt() {
        stopped=true;
        return true;
    }

    abstract void load();

    public void run() {

        try {
            try {
                load();

                if (stopped){
                    return;
                }

                for (int i=routes.size()-1; i>=0; i--) {
                    MapRoute gpe=(MapRoute) routes.elementAt(i);
                    if (!gpe.isEmpty()){
                        MapCanvas.map.rmss.saveRoute(gpe);
                    }
                }
            } finally {
                MapForms.mm.back2RoutesForm(true);
            }
        } catch (Exception e) {
        }



    }

    static int swapOziColor(int oc) {
        int r=oc&0xFF;
        int g=oc&0xFF00;
        int b=oc&0xFF0000;
        return (r<<16)+(g)+(b>>16);
    }

    /**
     *                         10  14  182022
     *               0123456789 11131517192123
     *                           12  16
     * Converts data 2007-08-26T19:28:54Z in long
     * Converts data 2007-08-26T19:28:54.123Z in long
     *
     */
    //TODO Исправить чтение миллисекунд
    private long isoTime(String time) {
        if ((time.length()==20 && time.charAt(19)!='Z')||(time.charAt(10)!='T')){
            throw new IllegalArgumentException("Date has wrong format 20: "+time);
        }
        if ((time.length()==24 && time.charAt(23)!='Z')){
            throw new IllegalArgumentException("Date has wrong format 24: "+time);
        }
        calendar.set(Calendar.YEAR, Integer.parseInt(time.substring(0, 4)));
        calendar.set(Calendar.MONTH, Integer.parseInt(time.substring(5, 7))-1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(time.substring(8, 10)));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(11, 13)));
        calendar.set(Calendar.MINUTE, Integer.parseInt(time.substring(14, 16)));
        calendar.set(Calendar.SECOND, Integer.parseInt(time.substring(17, 19)));
        if (time.length()==20)
          calendar.set(Calendar.MILLISECOND, 0);
        else
          calendar.set(Calendar.MILLISECOND, Integer.parseInt(time.substring(20, 23)));

        return calendar.getTime().getTime();
    }

    private void readLabel(KXmlParser parser, MapPoint mp) throws IOException, XmlPullParserException {
        //#debug
        parser.require(XmlPullParser.START_TAG, null, label);

        while (parser.nextTag()!=XmlPullParser.END_TAG) {
            //#debug
            parser.require(XmlPullParser.START_TAG, null, null);
            String tag=parser.getName();
            //#debug
            System.out.println("label<"+tag+">");
            if (tag.equals(label_text)||tag.toLowerCase().equals(label_text)){
                mp.name=parser.nextText();
            } else {
                parser.skipTree();
            }

            //#debug
            parser.require(XmlPullParser.END_TAG, null, null);
        }
        //#debug
        parser.require(XmlPullParser.END_TAG, null, label);
        //#debug
        System.out.println("leaved extensions");
    }

    private void readExtension(KXmlParser parser, MapPoint mp) throws IOException, XmlPullParserException {
        //#debug
        parser.require(XmlPullParser.START_TAG, null, extensions);

        while (parser.nextTag()!=XmlPullParser.END_TAG) {
            //#debug
            parser.require(XmlPullParser.START_TAG, null, null);
            String tag=parser.getName();
            //#debug
            System.out.println("extensions<"+tag+">");
            if (tag.equals(label)||tag.toLowerCase().equals(label)){
                readLabel(parser, mp);
            } else {
                parser.skipTree();
            }

            //#debug
            parser.require(XmlPullParser.END_TAG, null, null);
        }
        //#debug
        parser.require(XmlPullParser.END_TAG, null, extensions);
        //#debug
        System.out.println("leaved extensions");

    }

    private void readWpt(KXmlParser parser, MapRoute mr) throws IOException, XmlPullParserException {
        //#debug
        parser.require(XmlPullParser.START_TAG, null, null);

        MapPoint mp=new MapPoint(
          Double.parseDouble(parser.getAttributeValue(null, lat)),
          Double.parseDouble(parser.getAttributeValue(null, lon)), 0, 0);

        while (parser.nextTag()!=XmlPullParser.END_TAG) {
            //#debug
            parser.require(XmlPullParser.START_TAG, null, null);
            String tag=parser.getName();
            //#debug
            System.out.println("wpt<"+tag+">");
            if (tag.equals(name)){
                mp.name=parser.nextText();
            } else if (tag.equals(desc)){
                mp.name=parser.nextText();
            } else if (tag.equals(time)){
                mp.ts=isoTime(parser.nextText());
            } else if (tag.equals(ele)){
                mp.alt=(int) Double.parseDouble(parser.nextText());
            } else if (tag.equals(extensions)){
                readExtension(parser, mp);
            } else {
                parser.skipTree();
            }

            //#debug
            parser.require(XmlPullParser.END_TAG, null, null);
        }
        if (mr.kind==MapRoute.TRACKKIND){
            mr.addMapPointFromTrackLoad(mp);
        } else {
            mr.addMapPoint(mp);
        }

        //#debug
        parser.require(XmlPullParser.END_TAG, null, null);

    }

    private void readTrkSeg(KXmlParser parser, MapRoute mr) throws IOException, XmlPullParserException {
        //#debug
        parser.require(XmlPullParser.START_TAG, null, null);

        while (parser.nextTag()!=XmlPullParser.END_TAG) {
            //#debug
            parser.require(XmlPullParser.START_TAG, null, null);
            String tag=parser.getName();
            //#debug
            System.out.println("trkseg<"+tag+">");
            if (tag.equals(trkpt)){
                readWpt(parser, mr);
            } else {
                parser.skipTree();
            }
            //#debug
            parser.require(XmlPullParser.END_TAG, null, null);
        }
        //#debug
        parser.require(XmlPullParser.END_TAG, null, null);

    }

    /*
    private void readRtept(KXmlParser parser, MapRoute mr)throws IOException, XmlPullParserException{
    //#debug
    parser.require(XmlPullParser.START_TAG, null, rtept);
    
    MapPoint mp = new MapPoint(
    Double.parseDouble(parser.getAttributeValue(null,lat)),
    Double.parseDouble(parser.getAttributeValue(null,lon)),0,0);
    mr.addMapPoint(mp);

    while (parser.nextTag() != XmlPullParser.END_TAG) {
    //#debug
    parser.require(XmlPullParser.START_TAG, null, null);
    String tag = parser.getName();
    //#debug
    System.out.println("rtept<"+tag+">");
    if (tag.equals(name)||tag.toLowerCase().equals(name))
    mp.name=parser.nextText();
    else if (parser.isEmptyElementTag())
    parser.next();
    else parser.skipSubTree();
    //#debug
    parser.require(XmlPullParser.END_TAG, null, null);
    }
    //#debug
    parser.require(XmlPullParser.END_TAG, null, rtept);
    }*/
//final public static byte INHTTP=1;
    //final public static byte INLOCFILE=2;
    private String wpt="wpt";
    private String rte="rte";
    private String trk="trk";
    private String time="time";
    private String trkpt="trkpt";
    private String rtept="rtept";
    private String trkseg="trkseg";
    private String ele="ele";
    private String name="name";
    private String desc="desc";
    private String extensions="extensions";
    private String label_text="label_text";
    private String label="label";
    private String lat="lat";
    private String lon="lon";

    void loadGPXWayPointsFromStream(InputStream is, byte loadKind) throws IOException {
        //boolean readRoute
        KXmlParser parser=new KXmlParser();
        MapRoute mr=null;
        if (loadKind==MapRoute.WAYPOINTSKIND){
            mr=new MapRoute(MapRoute.WAYPOINTSKIND);
            routes.addElement(mr);
            mr.name=MapUtil.extractFilename(furl);
        }
        try {
            parser.setInput(is, null);
            parser.nextTag();
            //#debug
            parser.require(XmlPullParser.START_TAG, null, "gpx");
            while (parser.nextTag()!=XmlPullParser.END_TAG) {
                //#debug
                parser.require(XmlPullParser.START_TAG, null, null);

                String tag=parser.getName();
                //#debug
                System.out.println("tag<"+tag+">");
                if (loadKind==MapRoute.ROUTEKIND){
                    if (tag.equals(rte)){

                        mr=new MapRoute(MapRoute.ROUTEKIND);
                        routes.addElement(mr);

                        while (parser.nextTag()!=XmlPullParser.END_TAG) {
                            //#debug
                            parser.require(XmlPullParser.START_TAG, null, null);
                            String gname=parser.getName();
                            //#debug
                            System.out.println("rte<"+gname+">");

                            if (gname.equals(name)){
                                mr.name=parser.nextText();
                            } else if (gname.equals(rtept)){
                                readWpt(parser, mr);
                            } else {
                                parser.skipTree();
                            }

                            //#debug
                            parser.require(XmlPullParser.END_TAG, null, null);
                        }
                    } else {
                        parser.skipTree();
                    }
                } else if (loadKind==MapRoute.WAYPOINTSKIND){
                    if (tag.equals(wpt)){
                        readWpt(parser, mr);
                    } else {
                        parser.skipTree();
                    }
                } else if (loadKind==MapRoute.TRACKKIND){
                    if (tag.equals(trk)){

                        mr=new MapRoute(MapRoute.TRACKKIND);
                        routes.addElement(mr);

                        while (parser.nextTag()!=XmlPullParser.END_TAG) {
                            //#debug
                            parser.require(XmlPullParser.START_TAG, null, null);
                            String gname=parser.getName();
                            //#debug
                            System.out.println("trk<"+gname+">");

                            if (gname.equals(name)){
                                mr.name=parser.nextText();
                            } else if (gname.equals(trkseg)){
                                readTrkSeg(parser, mr);
                            } else {
                                parser.skipTree();
                            }

                            //#debug
                            parser.require(XmlPullParser.END_TAG, null, null);
                        }
                    } else {
                        parser.skipTree();
                    }
                } else {
                    parser.skipTree();
                }

                //#debug
                parser.require(XmlPullParser.END_TAG, null, null);
            }
            //#debug
            parser.require(XmlPullParser.END_TAG, null, "gpx");

            //#debug
            //parser.require(XmlPullParser.END_DOCUMENT, null, null);
        } catch (Exception e) {
            //#debug
            e.printStackTrace();
        }

    }

    private String waypoint="waypoint";

    void loadLOCWayPointsFromStream(InputStream is, boolean readRoute) throws IOException {
        KXmlParser parser=new KXmlParser();
        MapRoute mr=null;
        if (!readRoute){
            mr=new MapRoute(MapRoute.WAYPOINTSKIND);
            routes.addElement(mr);
            mr.name=MapUtil.extractFilename(furl);
            mr.geoInfo=MapRoute.GEOINFO_GEOCACHECOM;
        }
        try {
            parser.setInput(is, null);
            parser.nextTag();
            //#debug
            parser.require(XmlPullParser.START_TAG, null, "loc");
            while (parser.nextTag()!=XmlPullParser.END_TAG) {
                //#debug
                parser.require(XmlPullParser.START_TAG, null, null);

                String tag=parser.getName().toLowerCase();

                if (tag.equals(waypoint)){
                    readWaypoint(parser, mr);
                } else {
                    parser.skipTree();
                }

                //#debug
                parser.require(XmlPullParser.END_TAG, null, null);
            }
            //#debug
            parser.require(XmlPullParser.END_TAG, null, "loc");

            //#debug
            //parser.require(XmlPullParser.END_DOCUMENT, null, null);
        } catch (Exception e) {
            //#debug
            e.printStackTrace();
        }

    }
    private String coord="coord";

    private void readWaypoint(KXmlParser parser, MapRoute mr) throws IOException, XmlPullParserException {
        //#debug
        parser.require(XmlPullParser.START_TAG, null, waypoint);

        MapPoint mp=new MapPoint(0, 0, 0, 0);
        mr.addMapPoint(mp);
        String s;
        String[] data;

        while (parser.nextTag()!=XmlPullParser.END_TAG) {
                //#debug
                parser.require(XmlPullParser.START_TAG, null, null);
                String gname=parser.getName();
                //#debug
                System.out.println("Waypoint<"+gname+">");
                if (gname.equals(name)){
                    mp.name=parser.nextText();
                } else if (gname.equals(coord)){
                    String slon=parser.getAttributeValue(null, lon);
                    String slat=parser.getAttributeValue(null, lat);
                    mp.lon=Double.parseDouble(slon);
                    mp.lat=Double.parseDouble(slat);
                    mp.alt=0;
                    parser.skipTree();
                } else if (gname.equals("link")){
                    mp.tag=parser.nextText();
                } else {
                    parser.skipTree();
                }

                //#debug
                parser.require(XmlPullParser.END_TAG, null, null);
            }
        
        //#debug
        parser.require(XmlPullParser.END_TAG, null, waypoint);
        //#debug
        System.out.println("leaved Location");

    }
}
