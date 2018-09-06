package gpspack;

import RPMap.FileTrackSend;
import RPMap.MapCanvas;
import RPMap.MapPoint;
import RPMap.MapRoute;
import RPMap.MapUtil;
import RPMap.RMSOption;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
//#debug
//# import misc.DebugLog;
import misc.GraphUtils;
import misc.MapSound;
import misc.Util;

public final class GPSReader implements Runnable {

    private static final int TRACK_BACKUP_PERIOD=600000;
    private String url=MapUtil.emptyString;
    /** Disables any mediation of values */
    public static boolean RAWDATA;
    public static boolean WRITETRACK=true;  // This is the data retrieved from the GPS
    public static float SPEED_KMH;
    //public static String SPEED_KMH_STR = "";
    public static int NUM_SATELITES;
    //public static String NUM_SATELITES_STR = "";
    public static double LATITUDE;
    public static double LONGITUDE;
    public static int ALTITUDE;  //* Prev values for track on course */
    private double pvLATITUDE;
    private double pvLONGITUDE;  //public static String ALTITUDE_M = "M";
    public static double ALTSPEED_MS;
    public static float COURSE;
    public static int COURSE_I;
    public static int POSFIX;
    private static double tLATITUDE;//61+41/60.+15/3600.;
    private static double tLONGITUDE;//30+40/60.+3/3600.;
    private static int tALTITUDE;//302;//30+40/60.+3/3600.;
    public static float PDOP;
    public static float HDOP;
    public static float VDOP;  //private static int valCount;
    public static String zeroUTC="00:00:00";
    //public static String UTC = zeroUTC;
    public byte minUTC;
    public byte hourUTC;
    public byte secUTC;

    public String satTimeUTC() {
        if ((minUTC==0)&&(hourUTC==0)&&(secUTC==0)){
            return zeroUTC;
        }
        return MapUtil.numStr(hourUTC, 2)+':'+MapUtil.numStr(minUTC, 2)+':'+MapUtil.numStr(secUTC, 2);
    }
    public byte min;
    public byte hour;
    public byte sec;
    private String timeStr;
    private byte tsMin, tsHour, tsSec;

    public String satTime() {
        if ((min==0)&&(hour==0)&&(sec==0)){
            return zeroUTC;
        }
        if ((min==tsMin)&&(hour==tsHour)&&(sec==tsSec)){
            return timeStr;
        }
        tsMin=min;
        tsHour=hour;
        tsSec=sec;
        timeStr=MapUtil.numStr(tsHour, 2)+':'+MapUtil.numStr(tsMin, 2)+':'+MapUtil.numStr(tsSec, 2);
        return timeStr;
    }
    public short year;
    public byte month;
    public byte day;
    private String dateStr;
    private byte tsMonth, tsDay;
    private short tsYear;

    public String satDate() {
        if ((year==0)&&(month==0)&&(day==0)){
            return MapUtil.emptyString;
        }
        if ((year==tsYear)&&(month==tsMonth)&&(day==tsDay)){
            return dateStr;
        }
        tsYear=year;
        tsMonth=month;
        tsDay=day;
        dateStr=MapUtil.numStr(tsYear, 4)+'-'+MapUtil.numStr(tsMonth, 2)+'-'+MapUtil.numStr(tsDay, 2);
        return dateStr;
    }
    private float[] speedVector=new float[3];//{0, 0, 0};
    private int spIndex;
    private boolean speedStarted;
    private byte speedCnt;
    private float[] latVector=new float[4];//{0, 0, 0, 0};
    private int latIndex;
    private boolean latStarted;
    private byte latCnt;
    private float[] lonVector=new float[4];//{0, 0, 0, 0};
    private int lonIndex;
    private boolean lonStarted;
    private byte lonCnt;
    private int[] altVector=new int[5];//{0, 0, 0, 0, 0};
    private int alIndex;
    private boolean altStarted;
    private byte altCnt;
    private float[] crsVector=new float[3];//{0, 0, 0};
    private int crsIndex;
    private boolean crsStarted;
    private byte crsCnt;//----------STATISTICS---------------------------------------------
    public static int minAlt=20000;
    public static int maxAlt=-1000;

    public static void resetAlt() {
        minAlt=20000;
        maxAlt=-1000;
        statReady=false;
    }
    public static double maxAS;
    public static double minAS;

    public static void resetAS() {
        minAS=0;
        maxAS=0;
        statReady=false;
    }
    public static float minA;
    public static float maxA;

    public static void resetA() {
        minA=0;
        maxA=0;
        statReady=false;
    }
    public static float CUR_A;
    public static float maxV;
    public static float minV=5000;

    public static void resetV() {
        minV=5000;
        maxV=0;
        statReady=false;
    }
    public static float avgV;  //public static long USED;
    private static long startUsed;
    public static boolean statReady;

    final public static void calcStat() {
        if (GPSReader.ALTITUDE<GPSReader.minAlt){
            GPSReader.minAlt=GPSReader.ALTITUDE;
        }
        if (GPSReader.ALTITUDE>GPSReader.maxAlt){
            GPSReader.maxAlt=GPSReader.ALTITUDE;
        }
        if (GPSReader.ALTSPEED_MS<GPSReader.minAS){
            GPSReader.minAS=GPSReader.ALTSPEED_MS;
        }
        if (GPSReader.ALTSPEED_MS>GPSReader.maxAS){
            GPSReader.maxAS=GPSReader.ALTSPEED_MS;
        }
        if (GPSReader.SPEED_KMH<GPSReader.minV){
            GPSReader.minV=GPSReader.SPEED_KMH;
        }
        if (GPSReader.SPEED_KMH>GPSReader.maxV){
            GPSReader.maxV=GPSReader.SPEED_KMH;
        }
        if (GPSReader.CUR_A<GPSReader.minA){
            GPSReader.minA=GPSReader.CUR_A;
        }
        if (GPSReader.CUR_A>GPSReader.maxA){
            GPSReader.maxA=GPSReader.CUR_A;
        }
        long tm=System.currentTimeMillis();
        RMSOption.USED+=tm-GPSReader.startUsed;
        GPSReader.startUsed=tm;

        if (RMSOption.USED>1000){
            GPSReader.avgV=(float) MapUtil.distRound2(RMSOption.DISTANCE*3600./RMSOption.USED);
        } else {
            GPSReader.avgV=0;
        }
        statReady=true;
    }
    private long lastStartTime;
    private float lastSpeed;

    /** Method to calculate the travelled distance from the current speed during the time */
    private static float calculateDistance(long totTime) {
        return (SPEED_KMH*0.0002777777777778f*((float) totTime));
//    return (float)(SPEED_KMH*0.2777777777778f * ((float)totTime/1000f));
    }

    /** Method to calculate the acceleration */
    private float calculateAcceleration(long totTime) {
        return ((GPSReader.SPEED_KMH-lastSpeed)*277.7777777778f/(float) totTime);
//    return (float)((GPSReader.SPEED_KMH-lastSpeed) *0.2777777777778f / (float)totTime*1000.f);
    }

    public void calculateOdometer() {
        long ls=SAT_TIME_MILLIS;
        long totTime=ls-lastStartTime;
        if ((GPSReader.NUM_SATELITES>0)&&(GPSReader.SPEED_KMH>3)&&(lastStartTime>0)){
            float tot=calculateDistance(totTime);
            RMSOption.ODOMETER+=tot;
            RMSOption.DISTANCE+=tot;
            tot=calculateAcceleration(totTime);
            // if (Math.abs(tot)<60)
            CUR_A=(float) MapUtil.distRound2(tot);
        }
        lastStartTime=ls;
        lastSpeed=SPEED_KMH;
        calcStat();
    }

    public void resetCalcs() {
        mvReadyLessZero=MVREADYSTARTVALUE;
        mvReady=false;
        lastStartTime=0;
        prevAltTime=0;
        speedStarted=false;
        speedCnt=0;
        latStarted=false;
        latCnt=0;
        crsStarted=false;
        crsCnt=0;
        lonStarted=false;
        lonCnt=0;
        altStarted=false;
        altCnt=0;
        altspdIndex=0;
        altspdStarted=false;
    }
    private static int prevALTITUDE;//302;//30+40/60.+3/3600.;
    private static long prevAltTime;
    private int altspdIndex;
    private boolean altspdStarted;
    private float[] altspdVector=new float[4];
    private byte altspdCnt;

    public void calculateAltSpeed() {
        if (SAT_TIME_MILLIS==prevAltTime){
            return;
        }
        float altSpd=0;
        if (prevAltTime>0){
            long totTime=SAT_TIME_MILLIS-prevAltTime;
            //ALTSPEED_MPM = (int)((float)(ALTITUDE-prevALTITUDE)/(totTime*0.00001666666666666667f));
            altSpd=((float) (ALTITUDE-prevALTITUDE)/(totTime*0.001f));

        }
        prevALTITUDE=ALTITUDE;
        prevAltTime=SAT_TIME_MILLIS;

        if (prevAltTime==0){
            return;
        }

        altspdIndex=altspdIndex==altspdVector.length-1?0:altspdIndex+1;
        altspdVector[altspdIndex]=altSpd;

        if (!altspdStarted){
            altspdStarted=true;
            for (int n=altspdVector.length-1; n>=0; n--) {
                altspdVector[n]=altSpd;
            }
        }
        if (altspdCnt<altspdVector.length){
            altspdCnt++;
        }
        byte vc, vc1;
        if (SPEED_KMH<30){
            vc=4;
            // } else if (SPEED_KMH<25){
            //      vc=2;
        } else {
            vc=3;
        }
        if (vc>altspdCnt){
            vc=altspdCnt;
        }
        //if (RAWDATA){
        //    vc=1;
        //}
        vc1=vc;
        int nI=altspdIndex;
        double tot=altspdVector[nI];
        vc1--;
        while (vc1>0) {
            nI=nI==0?altspdVector.length-1:nI-1;
            tot+=altspdVector[nI];
            vc1--;
        }

        GPSReader.ALTSPEED_MS=MapUtil.doubleRound1(tot/(double) (vc));

        if (RMSOption.getBoolOpt(RMSOption.BL_WARNDOWNSPEED)){
            if (GPSReader.ALTSPEED_MS>RMSOption.getDoubleOpt(RMSOption.DO_MAXCLIMBSPD)){
                MapSound.playSound(MapSound.UPSPEED);
            } else if (GPSReader.ALTSPEED_MS<-RMSOption.getDoubleOpt(RMSOption.DO_MAXDESCENTSPD)){
                MapSound.playSound(MapSound.DOWNSPEED);
            }
        }
    }
    public boolean readStopped;
    public static boolean notFinished;

    public void stop() {
        readStopped=true;
        if (cTT!=null){
            cTT.cancel();
        }
        if (locReader!=null){
            locReader.stop();
            notFinished=false;
        }
        locReader=null;
    }
    private static long timeP;

    final public void needPaint() {
        if (MapCanvas.mode==MapCanvas.MAPMODE){
            if (timeP<System.currentTimeMillis()){
                timeP=System.currentTimeMillis()+400;
                MapCanvas.map.repaint();
            }
        } else {
            if (timeP<System.currentTimeMillis()){
                timeP=System.currentTimeMillis()+200;
                MapCanvas.map.repaint();
            }
        }
    }
    private long timeTrackSave;//  public static long TRACKPERIOD = 60000;
    //12private long timeT;
    private double lastLat, lastLon;
    private float lastCrs, lastSpd;
    //private int lastAlt;
    public static long SAT_TIME_MILLIS;
    long lastTrackSatTimeMillis;

    private void addTrackPoint(boolean previous) {
        if (SAT_TIME_MILLIS==0){
            return;
        }
        MapPoint mp=null;
        //12timeT=System.currentTimeMillis();
        lastLat=LATITUDE;
        lastLon=LONGITUDE;
        // lastAlt=ALTITUDE;
        lastCrs=COURSE;
        lastSpd=SPEED_KMH;
        if (previous&&(SAT_TIME_MILLIS>0)&&latStarted&&lonStarted){
            mp=new MapPoint(pvLATITUDE, pvLONGITUDE, ALTITUDE, SAT_TIME_MILLIS);
        } else {
            mp=new MapPoint(LATITUDE, LONGITUDE, ALTITUDE, SAT_TIME_MILLIS);
        }
        mp.speed=SPEED_KMH;
        mp.kind=MapPoint.TYPE_TRACK;
        //mp.kind=MapPoint.TRACK;
        MapCanvas.map.activeTrack.addTrackMapPoint(mp);

        lastTrackSatTimeMillis=SAT_TIME_MILLIS;

        if (MapCanvas.map.activeTrack.pts.size()>=RMSOption.maxTrackPoints){
            (new Thread(MapCanvas.map.activeTrack)).start();
            try {
                Thread.sleep(20);
            } catch (Throwable t) {
            }
            if (RMSOption.getBoolOpt(RMSOption.BL_TRACKBACKUP)){
                new FileTrackSend(MapCanvas.display.getCurrent(), RMSOption.getStringOpt(RMSOption.SO_WORKPATH), MapCanvas.map.activeTrack, true);
            }
            MapCanvas.map.startTrack(new MapRoute(MapRoute.TRACKKIND));
            timeTrackSave=System.currentTimeMillis()-TRACK_BACKUP_PERIOD+60000;
        }

        //10 minutes
        left2autosave=timeTrackSave-System.currentTimeMillis()+TRACK_BACKUP_PERIOD;
        if (left2autosave<=0){
            timeTrackSave=System.currentTimeMillis();
            left2autosave=timeTrackSave-System.currentTimeMillis()+TRACK_BACKUP_PERIOD;
            (new Thread(MapCanvas.map.activeTrack)).start();
            try {
                Thread.sleep(20);
            } catch (Throwable t) {
            }
            if (RMSOption.getBoolOpt(RMSOption.BL_TRACKBACKUP)){
                new FileTrackSend(MapCanvas.display.getCurrent(), RMSOption.getStringOpt(RMSOption.SO_WORKPATH), MapCanvas.map.activeTrack, true);
            }
        }
        if (RMSOption.getBoolOpt(RMSOption.BL_ADDTRACKPOINTSOUND_ON))
          MapSound.playTone(MapSound.GPSADDTRACKPOINT);
    }
    public long left2autosave;

    public void addTrackCross() {
        if (MapCanvas.map.activeTrack==null){
            return;
        }
        MapPoint mp=null;
        long timeT=(SAT_TIME_MILLIS>0)?SAT_TIME_MILLIS:System.currentTimeMillis();
        lastLat=LATITUDE;
        lastLon=LONGITUDE;
        lastCrs=COURSE;
        lastSpd=SPEED_KMH;
        double lat=LATITUDE, lon=LONGITUDE, dlon=0.0001801801801802/Math.cos(lat*MapUtil.G2R);
        mp=new MapPoint(lat, lon, ALTITUDE, timeT);
        mp.speed=SPEED_KMH;
        mp.kind=MapPoint.TYPE_TRACK;
        MapCanvas.map.activeTrack.addTrackMapPoint(mp);
        timeT+=20;
        mp=new MapPoint(lat, lon+dlon, ALTITUDE, timeT);
        mp.speed=SPEED_KMH;
        mp.kind=MapPoint.TYPE_TRACK;
        MapCanvas.map.activeTrack.addTrackMapPoint(mp);
        timeT+=20;
        mp=new MapPoint(lat+0.0001801801801802, lon, ALTITUDE, timeT);
        mp.speed=SPEED_KMH;
        mp.kind=MapPoint.TYPE_TRACK;
        MapCanvas.map.activeTrack.addTrackMapPoint(mp);
        timeT+=20;
        mp=new MapPoint(lat, lon, ALTITUDE, timeT);
        mp.speed=SPEED_KMH;
        mp.kind=MapPoint.TYPE_TRACK;
        MapCanvas.map.activeTrack.addTrackMapPoint(mp);
        timeT+=20;
        mp=new MapPoint(lat, lon-dlon, ALTITUDE, timeT);
        mp.speed=SPEED_KMH;
        mp.kind=MapPoint.TYPE_TRACK;
        MapCanvas.map.activeTrack.addTrackMapPoint(mp);
        timeT+=20;
        mp=new MapPoint(lat-0.0001801801801802, lon, ALTITUDE, timeT);
        mp.speed=SPEED_KMH;
        mp.kind=MapPoint.TYPE_TRACK;
        MapCanvas.map.activeTrack.addTrackMapPoint(mp);
        timeT+=20;
        mp=new MapPoint(lat, lon, ALTITUDE, timeT);
        mp.speed=SPEED_KMH;
        mp.kind=MapPoint.TYPE_TRACK;
        MapCanvas.map.activeTrack.addTrackMapPoint(mp);

        if (RMSOption.getBoolOpt(RMSOption.BL_ADDTRACKPOINTSOUND_ON))
          MapSound.playTone(MapSound.GPSADDTRACKPOINT);
    }

    private boolean checkTurn(double dist, double dcourse, double mindist, double mindcourse, double minspd) {
        if ((dist>mindist)&&(SPEED_KMH>minspd)){
            if (dcourse>mindcourse){
                addTrackPoint(true);
            }
            return false;
        }
        return true;
    }
    private final static long PROXIMITY_CHECK_PERIOD=3000;
    private long lastProximityCheck;

    final public void needAddTrackPoint(long satTime) {
        if (satTime!=0){
            SAT_TIME_MILLIS=satTime;
        }
        if (MapCanvas.map.gpsLocListenersRaw.size()!=0){
            for (int i=MapCanvas.map.gpsLocListenersRaw.size()-1; i>=0; i--) {
                GPSLocationListener locList=(GPSLocationListener) MapCanvas.map.gpsLocListenersRaw.elementAt(i);
                locList.gpsLocationAction(LATITUDE, LONGITUDE, ALTITUDE, SAT_TIME_MILLIS, SPEED_KMH, COURSE);
            }
        }
        MapRoute aR=MapCanvas.map.activeRoute;
        if (aR!=null){
            if (RMSOption.autoSelectWpt){
                if (System.currentTimeMillis()-lastProximityCheck>PROXIMITY_CHECK_PERIOD){
                    aR.checkProximity();
                    lastProximityCheck=System.currentTimeMillis();
                }
            }
            //#ifdef Fox
//#             if (fox!=null){
//#                 fox.distance=(int) (aR.distFromPrecise(LATITUDE, LONGITUDE)*1000.);
//#             }
            //#endif
        }
        if (!WRITETRACK){
            return;
        }
        if (MapCanvas.map.activeTrack==null){
            return;
        }
        if ((RMSOption.trackRecordUse&RMSOption.USETIME)==RMSOption.USETIME){
            if (SAT_TIME_MILLIS-RMSOption.trackPeriod>=lastTrackSatTimeMillis){
                addTrackPoint(false);
                return;
            }
        }

        double dist=MapRoute.distBetweenCoords(lastLat, lastLon, LATITUDE, LONGITUDE);

        if ((RMSOption.trackRecordUse&RMSOption.USEDIST)==RMSOption.USEDIST){
            if (dist>=RMSOption.trackDist){
                addTrackPoint(false);
                return;
            }
        }

        double dcourse=Math.abs(COURSE-lastCrs);
        if (dcourse>180){
            dcourse=360-dcourse;
        }
        if (RMSOption.addTrackPointOnTurn){
            if (startStop(dist)){
                if (endStop()){
                    if (checkTurn(dist, dcourse, 0.3, 12, 80)){
                        if (checkTurn(dist, dcourse, 0.1, 15, 50)){
                            if (checkTurn(dist, dcourse, 0.04, 20, 20)){
                                if (checkTurn(dist, dcourse, 0.03, 30, 12)){
                                    if (checkTurn(dist, dcourse, 0.02, 50, 5));
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    private boolean startStop(double dist) {
        if (dist>0.02){
            if (SPEED_KMH<1){
                if (lastSpd>5){
                    addTrackPoint(false);
                    return false;
                }
            }
        }
        return true;
    }

    private boolean endStop() {
        if (SPEED_KMH>5){
            if (lastSpd<1){
                addTrackPoint(false);
                return false;
            }
        }
        return true;
    }
    private Image satImage;
    private Image satImage2;
    private Image satImage0;

    final public Image satImage() {
        /*
        if (satImage==null) {
        try {
        satImage = Image.createImage("/img/sat.png");
        } catch(IOException _ex) { }
        return satImage;
        } else return satImage;
         */
        if (GPSReader.NUM_SATELITES>0){
            if ((GPSReader.POSFIX<3)&&gsaAvailable){
                if (satImage2==null){
                    try {
                        satImage2=Image.createImage("/img/sat2.png");
                    } catch (IOException _ex) {
                    }
                    return satImage2;
                } else {
                    return satImage2;
                }
            } else {
                if (satImage==null){
                    try {
                        satImage=Image.createImage("/img/sat.png");
                    } catch (IOException _ex) {
                    }
                    return satImage;
                } else {
                    return satImage;
                }
            }
        } else {
            if (satImage0==null){
                try {
                    satImage0=Image.createImage("/img/sat0.png");
                } catch (IOException _ex) {
                }
                return satImage0;
            } else {
                return satImage0;
            }
        }

    }
    private static boolean SATVALID=false;

    static void satLost() {
        if (!SATVALID){
            return;
        }
        SATVALID=false;
//    valCount=0;
        MapSound.playSound(MapSound.SATLOSTSOUND);//MapMidlet.playSatLostSound();
    }

    static boolean satFound() {
        if (SATVALID){
            return false;
        }
        SATVALID=true;
        //  valCount=0;
        MapSound.playSound(MapSound.SATFOUNDSOUND);//MapMidlet.playSatFoundSound();
        return true;
    }
    //#ifdef Fox
//#     public FoxHunter fox;
    //#endif
    public LocReaderInt locReader;

    /*
     * The url to the bluetooth device is the String parameter.
     */
    public GPSReader(String connurl, boolean startThread) {
        startUsed=System.currentTimeMillis();
        url=connurl; // Connection string to the bluetooth device.

        //#if SE_K750_E_BASEDEV
//#         url="file:///root1/nmea_mp.txt"; // need to be put in emulator to debug active GPS 
        //#endif

        if (startThread){
            //SPEED_KMH=0;
            //SPEED_KMH_STR="";
            //NUM_SATELITES=0;
            //Thread t = new Thread(this);
            String name="GPS"+System.currentTimeMillis()%1000;
            gpsThread=new Thread(this, name);
            //a_java_lang_Thread_fld.setPriority(Thread.MAX_PRIORITY);
            gpsThread.start();

            //#if SE_K750_E_BASEDEV
            //#else
            if (RMSOption.bluetoothGPS()){
                if (cTT!=null){
                    cTT.cancel();
                }
                if (RMSOption.getBoolOpt(RMSOption.BL_BLUETOOTH_MONITOR)){
                    //#mdebug
//#                             if (RMSOption.debugEnabled){
//#                                 DebugLog.add2Log("GPSC1 BT mon");
//#                             }
                    //#enddebug
                    cTT=new CheckTimerTask();
                    MapCanvas.timer.scheduleAtFixedRate(cTT, 15000, 7000);
                }
            }
            //#endif
            //#ifdef Fox
//#             if (RMSOption.foxHunter){
//#                 if (RMSOption.soundOn){
//#                     fox=new FoxHunter();
//#                     fox.setPriority(Thread.NORM_PRIORITY/2);
//#                     fox.start();
//#                 }
//#             }
            //#endif
        }
    }
    private CheckTimerTask cTT;

    private int readDataInt2(byte[] data, byte pos) {
        int res=data[pos]-'0';
        res*=10;
        res+=data[pos+1]-'0';
        return res;
    }

    private int readDataInt3(byte[] data, byte pos) {
        int res=data[pos]-'0';
        res*=10;
        pos++;
        res+=data[pos]-'0';
        res*=10;
        pos++;
        res+=data[pos]-'0';
        return res;
    }

    private int readDataInt(byte[] data, byte pos) {
        if ((data[pos]==DEL)||(data[pos]==AST)){
            return -1;
        }
        int res=0;
        while ((data[pos]!=DEL)&&(data[pos]!=PNT)&&(data[pos]!=AST)) {
            res*=10;
            res+=data[pos]-'0';
            pos++;
        }
        return res;
    }

    private int readChar(byte[] data, byte pos) {
        return data[pos];
    }

    private float readFloat(byte[] data, byte pos) {
        boolean sign=false;
        if (data[pos]=='-'){
            sign=true;
            pos++;
        }
        float res=data[pos]-'0';
        pos++;
        while ((data[pos]!=DEL)&&(data[pos]!=PNT)) {
            res*=10;
            res+=data[pos]-'0';
            pos++;
        }
        if ((data[pos]==PNT)&&(data[pos]!=DEL)&&(data[pos]!=AST)){
            pos++;
            res+=(data[pos]-'0')*0.1f;
        }
        if (sign){
            return -res;
        }
        return res;
    }

    private float readLat(byte[] data, byte pos) {
        if (data[pos]==DEL){
            return 0;
        }
        float res=data[pos]-'0';
        pos++;
        res*=10f;
        res+=data[pos]-'0';
        pos++;
        //grads are done
        float mn=data[pos]-'0';
        //now int part of minutes
        mn*=10f;
        pos++;
        mn+=data[pos]-'0';
        pos++;
        pos++;//skip point
        mn+=(data[pos]-'0')*0.1f;
        pos++;
        mn+=(data[pos]-'0')*0.01f;
        pos++;
        mn+=(data[pos]-'0')*0.001f;
        pos++;
        mn+=(data[pos]-'0')*0.0001f;
        res+=mn*0.01666666666667f;
        return res;
    }

    private float readLon(byte[] data, byte pos) {
        if (data[pos]==DEL){
            return 0;
        }
        float res=data[pos]-'0';
        pos++;
        res*=10.f;
        res+=data[pos]-'0';
        pos++;
        res*=10.f;
        res+=data[pos]-'0';
        pos++;
        //grads are done
        float mn=data[pos]-'0';
        //now int part of minutes
        mn*=10.f;
        pos++;
        mn+=data[pos]-'0';
        pos++;
        pos++;//skip point
        mn+=(data[pos]-'0')*0.1f;
        pos++;
        mn+=(data[pos]-'0')*0.01f;
        pos++;
        mn+=(data[pos]-'0')*0.001f;
        pos++;
        mn+=(data[pos]-'0')*0.0001f;
        res+=mn*0.01666666666667f;
        return res;
    }
    private final static byte P_GGA_SATUSED=7;
    private final static byte P_LATVALUE=2;
    private final static byte P_LATNS=3;
    private final static byte P_LONVALUE=4;
    private final static byte P_LONEW=5;
    private final static byte P_ALT=9;
    private final static byte P_RMC_VALID=2;
    private final static byte P_RMC_SPEED=7;
    private final static byte P_RMC_COURSE=8;
    private final static byte P_RMC_TIME=1;
    private final static byte P_RMC_DATE=9;
    private final static byte P_GSA_SATMODE2=2;
    private final static byte P_GSA_SATUSEDSTART=3;
    private final static byte P_GSA_PDOP=15;
    private final static byte P_GSA_HDOP=16;
    private final static byte P_GSA_VDOP=17;
    private final static byte P_GSV_MESCOUNT=1;
    private final static byte P_GSV_MESNUMBER=2;
    private final static byte P_GSV_SATINVIEW=3;
    private final static byte P_GSV_SATINFOSTART=4;
    private final static byte AST='*';
    private final static byte DEL=',';
    private final static byte PNT='.';
    private final static byte SPC=' ';

    private byte dataPos(byte[] data, byte p_pos) {
        byte res=0;
        for (int i=0; i<data.length; i++) {
            if (data[i]==DEL){
                res++;
                if (res==p_pos){
                    return (byte) (i+1);

                }
            } else if (data[i]=='\n'){
                break;
            }
        }
        return -1;
    }

    private boolean checkSum(byte[] data) {
        byte sum=0;
        int astpos=0;
        for (int i=1; i<data.length; i++) {
            if (data[i]!=AST){
                sum=(byte) (sum^data[i]);
            } else {
                astpos=i+1;
                break;
            }
        }
        int rs=Util.hex2int((char) data[astpos]);
        rs=(rs<<4)+Util.hex2int((char) data[astpos+1]);
        return ((rs^sum)==0);
    }
    boolean mayAddTrackPoint;

    private void parseGPGGA(byte[] data) {
        lastReadTime=System.currentTimeMillis();
        mayAddTrackPoint=false;
        try {
            //  data = MapUtil.parseString(DATA_STRING,',');
            byte pos=dataPos(data, P_GGA_SATUSED);

            int numSat=readDataInt(data, pos);


            if ((numSat==0)||((POSFIX<2)&&(gsaAvailable))){
                GPSReader.NUM_SATELITES=0;
                resetCalcs();
                satLost();
                return;
            } else if (satFound()){
                return;
            }
            pos=dataPos(data, P_LATVALUE);
            float flat=readLat(data, pos);
            pos=dataPos(data, P_LONVALUE);
            float flon=readLon(data, pos);

            //flat = (float)(Float.parseFloat(data[1].substring(0, 2))+Float.parseFloat(data[1].substring(2))/60.);
            //flon = (float)(Float.parseFloat(data[3].substring(0,3))+Float.parseFloat(data[3].substring(3))/60.);
            if ((flon>180)||(flat>90)){
                return;
            }
            if (!((flat==0)||(flon==0))){

                //double f;
                pos=dataPos(data, P_LATNS);
                if (readChar(data, pos)=='S'){
                    flat=-flat;
                }
                latIndex=latIndex==latVector.length-1?0:latIndex+1;
                latVector[latIndex]=flat;

                if (!latStarted){
                    latStarted=true;
                    for (int n=latVector.length-1; n>=0; n--) {
                        latVector[n]=flat;
//          latVector[0]=flat;
//          latVector[1]=flat;
//          latVector[2]=flat;
                    }
                }
                if (latCnt<latVector.length){
                    latCnt++;
                    //f=latVector[0];
                    //for(int n=latVector.length-1; n>0; n--){
                    //  f += latVector[n];
                    //}
                }
                byte vc, vc1;
                if (SPEED_KMH<6){
                    vc=4;
                } else if (SPEED_KMH<10){
                    vc=3;
                } else if (SPEED_KMH<25){
                    vc=2;
                } else {
                    vc=1;
                }
                if (vc>latCnt){
                    vc=latCnt;
                }
                if (RAWDATA){
                    vc=1;
                }
                vc1=vc;
                int nI=latIndex;
                double tot=latVector[nI];
                vc1--;
                while (vc1>0) {
                    nI=nI==0?latVector.length-1:nI-1;
                    tot+=latVector[nI];
                    vc1--;
                }
                GPSReader.tLATITUDE=tot/(double) (vc);

                //GPSReader.tLATITUDE =(float)( f/(float)(latVector.length));
//        if (GPSReader.SPEED_KMH<40)
//          GPSReader.tLATITUDE =(latVector[0]+latVector[1]+latVector[2])/3.0;
//        else GPSReader.tLATITUDE = flat;

                //-------------LONGITUDE------------------------------------------------------------
                //if (data[4].equals(MapUtil.SH_WEST)) flon = -flon;
                pos=dataPos(data, P_LONEW);
                if (readChar(data, pos)=='W'){
                    flon=-flon;
                }
                lonIndex=lonIndex==lonVector.length-1?0:lonIndex+1;
                lonVector[lonIndex]=flon;

                if (!lonStarted){
                    lonStarted=true;
                    for (int n=lonVector.length-1; n>=0; n--) {
                        lonVector[n]=flon;
//          lonVector[0]=flon;
//          lonVector[1]=flon;
//          lonVector[2]=flon;
                    }
                }
                if (lonCnt<lonVector.length){
                    lonCnt++;
//          flon=lonVector[0];
//          for(int n=lonVector.length-1; n>0; n--){
//            flon += lonVector[n];
//          }
                }
                if (SPEED_KMH<6){
                    vc=4;
                } else if (SPEED_KMH<10){
                    vc=3;
                } else if (SPEED_KMH<25){
                    vc=2;
                } else {
                    vc=1;
                }
                if (vc>lonCnt){
                    vc=lonCnt;
                }
                if (RAWDATA){
                    vc=1;
                }
                vc1=vc;
                nI=lonIndex;
                tot=lonVector[nI];
                vc1--;
                while (vc1>0) {
                    nI=nI==0?lonVector.length-1:nI-1;
                    tot+=lonVector[nI];
                    vc1--;
                }
                GPSReader.tLONGITUDE=tot/(double) (vc);

//        //GPSReader.tLONGITUDE =(float)( flon/(float)(lonVector.length));
//        if (GPSReader.SPEED_KMH<40)
//          GPSReader.tLONGITUDE =(lonVector[0]+lonVector[1]+lonVector[2])/3.0;// float)( flon/(float)(lonVector.length));
//        else GPSReader.tLONGITUDE = flon;
                //-------------ALTITUDE------------------------------------------------------------

                alIndex=alIndex==altVector.length-1?0:alIndex+1;
                //int ialt=(int)Float.parseFloat(data[8]);

                pos=dataPos(data, P_ALT);
                int ialt=(int) readFloat(data, pos);

                altVector[alIndex]=ialt;

                if (!altStarted){
                    altStarted=true;
                    for (int n=altVector.length-1; n>=0; n--) {
                        altVector[n]=ialt;
//          altVector[0]=ialt;
//          altVector[1]=ialt;
//          altVector[2]=ialt;
                    }
                }
                if (altCnt<altVector.length){
                    altCnt++;//          flon=altVector[0];
//          for(int n=altVector.length-1; n>0; n--){
//            flon += altVector[n];
//          }
                }
                if (SPEED_KMH<6){
                    vc=5;
                } else if (SPEED_KMH<10){
                    vc=3;
                } else if (SPEED_KMH<25){
                    vc=2;
                } else {
                    vc=1;
                }
                if (vc>altCnt){
                    vc=altCnt;
                }
                if (RAWDATA){
                    vc=1;
                }
                vc1=vc;
                nI=alIndex;

                int toti=altVector[nI];
                vc1--;
                while (vc1>0) {
                    nI=nI==0?altVector.length-1:nI-1;
                    toti+=altVector[nI];
                    vc1--;
                }
                GPSReader.tALTITUDE=toti/vc;

                //GPSReader.tALTITUDE =(int)(flon/(float)altVector.length);
                //GPSReader.tALTITUDE =(int)((altVector[0]+altVector[1]+altVector[2])/3.0);//int)(flon/(float)altVector.length);

                // if (valCount>altVector.length) {
                pvLATITUDE=GPSReader.LATITUDE;
                pvLONGITUDE=GPSReader.LONGITUDE;

                GPSReader.ALTITUDE=tALTITUDE;
                GPSReader.LATITUDE=tLATITUDE;
                GPSReader.LONGITUDE=tLONGITUDE;
                //   }else {
                //   GPSReader.ALTITUDE=altVector[alIndex];
//          GPSReader.LATITUDE=latVector[latIndex];
//          GPSReader.LONGITUDE=lonVector[lonIndex];
//        }
                //  valCount++;

                //TODO Fix using units in message, not only predefined meters
                //GPSReader.ALTITUDE_M = data[9];

                calculateAltSpeed();

                GPSReader.NUM_SATELITES=numSat;
                mayAddTrackPoint=true;

                add2MoveVector(flat, flon);
                needPaint();
            }
            //exc = "";
        } catch (Exception e) {
            //#mdebug
//#             if (RMSOption.debugEnabled){
//#                 if (errCount<30){
//#                     DebugLog.add2Log("GPGGA:"+e.getMessage()+" "+e);
//#                     errCount++;
//#                 }
//#             }
//#enddebug
            GPSReader.NUM_SATELITES=0;
        }
    }

    private void parseGPRMC(byte[] data) {
        lastReadTime=System.currentTimeMillis();
        try {
            byte pos=dataPos(data, P_RMC_VALID);

            if (readChar(data, pos)=='A'){
                pos=dataPos(data, P_RMC_SPEED);
                float dmph=readFloat(data, pos)*1.852f;
                //float dmph = Float.parseFloat(data[6]);
                //dmph *= 1.852f;
                //SPEED_KMH_STR = String.valueOf(dmph);
                spIndex=spIndex==speedVector.length-1?0:spIndex+1;
                speedVector[spIndex]=dmph;

                if (!speedStarted){
                    for (int n=speedVector.length-1; n>=0; n--) {
                        speedVector[n]=dmph;
                    }
                    speedStarted=true;
                }
                if (speedCnt<speedVector.length){
                    speedCnt++;        //  }catch(Exception e){}
                }
                byte vc, vc1;
                if (SPEED_KMH<10){
                    vc=3;
                } else if (SPEED_KMH<25){
                    vc=2;
                } else {
                    vc=1;
                }
                if (vc>speedCnt){
                    vc=speedCnt;
                }
                if (RAWDATA){
                    vc=1;
                }
                vc1=vc;
                int nI=spIndex;
                float tot=speedVector[nI];
                vc1--;
                while (vc1>0) {
                    nI=nI==0?speedVector.length-1:nI-1;
                    tot+=speedVector[nI];
                    vc1--;
                }
//        float tot =speedVector[0];
//        for(int n=speedVector.length-1; n>0; n--){
//          tot += speedVector[n];
//        }
                //no more loose precise
                GPSReader.SPEED_KMH=tot/(float) (vc);
                //GPSReader.SPEED_KMH =tot/(float)(speedVector.length);
                //GPSReader.SPEED_KMH =(float)( (int)(tot/(float)(speedVector.length)*10f)/10f);

                if (GPSReader.SPEED_KMH>MVREADYMAXSPEED){
                    mvReadyLessZero=MVREADYSTARTVALUE;
                }
                if (dmph>2){  //?? ????????? ?????? ????????? ????????
                    if (GPSReader.SPEED_KMH>3){
                        pos=dataPos(data, P_RMC_COURSE);
                        dmph=readFloat(data, pos);

                        //dmph = Float.parseFloat(data[7]);//course
                        crsIndex=crsIndex==crsVector.length-1?0:crsIndex+1;
                        crsVector[crsIndex]=dmph;

                        if (!crsStarted){
                            for (int n=crsVector.length-1; n>=0; n--) {
                                crsVector[n]=dmph;
                            }
                            crsStarted=true;
                        }
                        if (crsCnt<crsVector.length){
                            crsCnt++;
                        }
                        if (SPEED_KMH<10){
                            vc=3;
                            //else if (SPEED_KMH<25) vc=2;
                        } else {
                            vc=2;
                        }
                        if (vc>crsCnt){
                            vc=crsCnt;
                        }
                        if (RAWDATA){
                            vc=1;
                        }
                        vc1=vc;
                        nI=crsIndex;
                        tot=(float) Math.cos(crsVector[nI]*MapUtil.G2R);
                        dmph=(float) Math.sin(crsVector[nI]*MapUtil.G2R);
                        vc1--;
                        while (vc1>0) {
                            nI=nI==0?crsVector.length-1:nI-1;
                            tot+=(float) Math.cos(crsVector[nI]*MapUtil.G2R);
                            dmph+=(float) Math.sin(crsVector[nI]*MapUtil.G2R);
                            vc1--;
                        }


//            tot = (float)Math.cos(crsVector[0]*MapUtil.G2R);
//            dmph = (float)Math.sin(crsVector[0]*MapUtil.G2R);
//            for(int n=crsVector.length-1; n>0; n--){
//              tot += (float)Math.cos(crsVector[n]*MapUtil.G2R);
//              dmph += (float)Math.sin(crsVector[n]*MapUtil.G2R);
//            }


                        try {
                            GPSReader.COURSE=(float) (MapUtil.atan2(dmph, tot)*MapUtil.R2G);
                            if (GPSReader.COURSE<0){
                                GPSReader.COURSE=360+GPSReader.COURSE;
                            }
                        } catch (Throwable t) {
                            GPSReader.COURSE=crsVector[crsIndex];
                        }
                        GPSReader.COURSE_I=(int) (GPSReader.COURSE);
                    }// speed >3
                    //COURSE = Float.parseFloat(data[7]);now relaxing values
                } //dmph>2
            }// =='A''

            //String sutc = data[0];
            //   UTC = sutc.substring(0,2)+':'+sutc.substring(2,4)+':'+sutc.substring(4,6);
            try {
                pos=dataPos(data, P_RMC_TIME);
                hourUTC=(byte) readDataInt2(data, pos);
                calendarUTC.set(Calendar.HOUR_OF_DAY, hourUTC);
                pos+=2;
                minUTC=(byte) readDataInt2(data, pos);
                calendarUTC.set(Calendar.MINUTE, minUTC);
                pos+=2;
                secUTC=(byte) readDataInt2(data, pos);
                calendarUTC.set(Calendar.SECOND, secUTC);

                pos+=3;
                calendarUTC.set(Calendar.MILLISECOND, readDataInt3(data, pos));

                pos=dataPos(data, P_RMC_DATE);
                calendarUTC.set(Calendar.DAY_OF_MONTH, readDataInt2(data, pos));
                pos+=2;
                calendarUTC.set(Calendar.MONTH, readDataInt2(data, pos)-1);
                pos+=2;
                calendarUTC.set(Calendar.YEAR, 2000+readDataInt2(data, pos));


                Date date=calendarUTC.getTime();
                SAT_TIME_MILLIS=date.getTime();
                calendarLoc.setTime(date);

                hour=(byte) calendarLoc.get(Calendar.HOUR_OF_DAY);
                min=(byte) calendarLoc.get(Calendar.MINUTE);
                sec=(byte) calendarLoc.get(Calendar.SECOND);

                year=(short) calendarLoc.get(Calendar.YEAR);
                month=(byte) (calendarLoc.get(Calendar.MONTH)+1);
                day=(byte) calendarLoc.get(Calendar.DAY_OF_MONTH);
                //GPSReader.UTC = MapUtil.make2(calendarLoc.get(Calendar.HOUR_OF_DAY))+':'+MapUtil.make2(calendarLoc.get(Calendar.MINUTE))+':'+MapUtil.make2(calendarLoc.get(Calendar.SECOND));

            } catch (Throwable t) {
//#mdebug
//#                 if (RMSOption.debugEnabled){
//#                     if (errCount<10){
//#                         DebugLog.add2Log("G UTC:"+t);
//#                         errCount++;
//#                     }
//#                     //GPSReader.UTC = zeroUTC;
//#                 }
//#enddebug
            }
            if (mayAddTrackPoint){
                needAddTrackPoint(0);
            }
            mayAddTrackPoint=false;

            calculateOdometer();
            if (RMSOption.getBoolOpt(RMSOption.BL_WARNMAXSPEED)){
                if (SPEED_KMH>RMSOption.maxSpeed){
                    MapSound.playSound(MapSound.MAXSPEED);// MapMidlet.playWarnSpeedSound();
                }
            }
            needPaint();

        } catch (Exception e) {
            GPSReader.SPEED_KMH=0;
            GPSReader.COURSE=0;
            GPSReader.COURSE_I=0;
//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 if (errCount<30){
//#                     DebugLog.add2Log("GPRMC:"+e+"\n"+String.valueOf(data));
//#                     errCount++;
//#                 }
//#             }
//#enddebug
        }
    }//$GPGSV,3,1,11,21,72,181,38,24,55,079,39,16,54,268,41,06,44,121,36*72
//$GPGSV,3,2,11,18,18,166,31,25,12,345,31,03,11,287,36,29,09,079,34*7F
//$GPGSV,3,3,11,27,04,003,36,26,03,092,25,31,00,229,*4E
    private boolean gsaAvailable;

    private void parseGPGSA(byte[] data) {
        try {
            byte pos=dataPos(data, P_GSA_SATMODE2);
            GPSReader.POSFIX=readDataInt(data, pos);

            for (int i=0; i<satUsed.length; i++) {
                satUsed[i]=-1;
            }
            byte si=P_GSA_SATUSEDSTART;
            pos=dataPos(data, si);
            int satNum, sI=0, sc=0;
            while ((sc<12)&&((satNum=readDataInt(data, pos))>=0)) {
                satUsed[sI]=satNum;
                si++;
                pos=dataPos(data, si);
                sI++;
                sc++;
            }

            pos=dataPos(data, P_GSA_PDOP);
            GPSReader.PDOP=readFloat(data, pos);
            pos=dataPos(data, P_GSA_HDOP);
            GPSReader.HDOP=readFloat(data, pos);
            pos=dataPos(data, P_GSA_VDOP);
            GPSReader.VDOP=readFloat(data, pos);
            gsaAvailable=true;
        } catch (Exception e) {
            GPSReader.POSFIX=0;
            //#mdebug
//#             if (RMSOption.debugEnabled){
//#                 if (errCount<30){
//#                     DebugLog.add2Log("GPGSA:"+e+"\n"+String.valueOf(data));
//#                     errCount++;
//#                 }
//#             }
//#enddebug
        }
    }
    int satCount;
    private static final int CHANELS=70;
    int[] satUsed=new int[CHANELS];
    int[] satNumbers=new int[CHANELS];
    int[] satAzimuth=new int[CHANELS];
    int[] satElevation=new int[CHANELS];
    int[] satSNR=new int[CHANELS];
    private int satIndex;

    private void parseGPGSV(byte[] data) {
        try {
            byte pos=dataPos(data, P_GSV_MESNUMBER);
            int mn=readDataInt(data, pos);
            if (mn==1){
                satIndex=0;
                //for (int i=satNumbers.length-1;i>=0;i--)satNumbers[i]=-1;
            }
            byte si=0;
            while ((pos=dataPos(data, (byte) (P_GSV_SATINFOSTART+si)))>0) {
                if (satIndex>=satNumbers.length){
                    break;
                }
                satNumbers[satIndex]=readDataInt(data, pos);
                si++;
                pos=dataPos(data, (byte) (P_GSV_SATINFOSTART+si));
                satElevation[satIndex]=readDataInt(data, pos);
                si++;
                pos=dataPos(data, (byte) (P_GSV_SATINFOSTART+si));
                satAzimuth[satIndex]=readDataInt(data, pos);
                si++;
                pos=dataPos(data, (byte) (P_GSV_SATINFOSTART+si));
                satSNR[satIndex]=readDataInt(data, pos);
                if (satSNR[satIndex]<0){
                    satSNR[satIndex]=0;
                }
                si++;
                satIndex++;
            }
            if (satIndex>satCount){
                satCount=satIndex;
            }
            needPaint();
        } catch (Exception e) {
            GPSReader.POSFIX=0;
            //#mdebug
//#             if (RMSOption.debugEnabled){
//#                 if (errCount<30){
//#                     DebugLog.add2Log("GPGSA:"+e.getMessage()+" "+e);
//#                     errCount++;
//#                 }
//#             }
//#enddebug
        }
    }

    final void parseGPS(byte[] data) {
        if (checkSum(data)){
            if ((data[3]=='G')&&(data[4]=='G')&&(data[5]=='A')){
                parseGPGGA(data);
            } else if ((data[3]=='R')&&(data[4]=='M')&&(data[5]=='C')){
                parseGPRMC(data);
            } else if ((data[3]=='G')&&(data[4]=='S')&&(data[5]=='A')){
                parseGPGSA(data);
            } else if ((data[3]=='G')&&(data[4]=='S')&&(data[5]=='V')){
                parseGPGSV(data);
            }
        } else {
            //#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("Check sum failed "+(char) data[3]+(char) data[4]+(char) data[5]);
//#             }
//#enddebug
        }
        //#if SE_K750_E_BASEDEV
//#         try {
//#             Thread.sleep(200);
//#         } catch (Throwable t) {
//#         }
        //#endif
    }

    private Calendar calendarUTC;//"UTC"
    private Calendar calendarLoc;//"UTC"
    private int errCount=0;
    private StreamConnection streamConn;

    public void breakConnection() {
        try {
            stop();
        } catch (Throwable t) {
        }
        try {
            if (streamConn!=null){
                streamConn.close();
            }
        } catch (Throwable t) {
        }
    }
    private StreamConnection fileConn;
    private OutputStream fileOut;
    boolean logCr;
    private byte inBuffer[]=new byte[200];
    private byte dataBuffer[]=new byte[1000];
    private int dataBufferEnd;
    public static long lastReadTime=System.currentTimeMillis();
    private int skipped;

    private boolean readGPSPiece() {
        // lastPieceReadTime=System.currentTimeMillis();
        int intRead=0;
        boolean flag=false;
        if (RMSOption.writeNMEA){
            if (!logCr){
                if (fileConn==null){
                    logCr=true;
                    int picN=RMSOption.picFileNumber;
                    RMSOption.picFileNumber++;
                    String fn=RMSOption.logSavePath+"NMEA"+MapUtil.numStr(picN, 4)+".TXT";
                    try {
                        fileConn=(StreamConnection) Connector.open("file:///"+fn, Connector.WRITE);
                        ((FileConnection) fileConn).create();
                        fileOut=fileConn.openOutputStream();
                    } catch (Throwable ex) {
//#mdebug
//#                         if (RMSOption.debugEnabled){
//#                             DebugLog.add2Log("GPS fw:"+ex.getMessage()+" "+ex);
//#                         }
//#enddebug
                    }
                }
            }
        }

        if (streamConn==null){
            try {
                streamConn=(StreamConnection) Connector.open(url, 1, true);
                connStatus=2;
                tryCount=0;
                //valCount=0;
//#mdebug
//#                 if (RMSOption.debugEnabled){
//#                     DebugLog.add2Log("GPS rc - OK");
//#                 }
//#enddebug
            } catch (IOException _ex) {
                tryCount++;
                if (RMSOption.getBoolOpt(RMSOption.BL_VIBRATE_ON)){
                    MapCanvas.display.vibrate(500);
                }
                MapSound.playTone(MapSound.GPSLOSTTONE);
                try {
                    Thread.sleep(900);
                } catch (InterruptedException _ex2) {
                }
                if (tryCount>5){
//#mdebug
//#                     if (RMSOption.debugEnabled){
//#                         DebugLog.add2Log("GPS rc f!");
//#                         //streamConn = null;
//#                     }
//#enddebug
                    return false;
                } else {
                    return true;
                }
            }
        }
        try {
            if (inStream==null){
                inStream=streamConn.openInputStream();
//        inStream = streamConn.openDataInputStream();
                if (inStream==null){
                    return false;
                }
//#mdebug
//#                 if (RMSOption.debugEnabled){
//#                     DebugLog.add2Log("R iS - OK5");
//#                 }
//#enddebug
            } 
            boolean streamHasData=true;

            //#if SE_K750_E_BASEDEV
//#else
            streamHasData=(inStream.available()!=0);
            //#endif

            while (streamHasData) {
                streamHasData=false;
               // int readed=0;
               // int br;

//                while ((readed<inBuffer.length) && ((br=inStream.read())>=0)){
//                    inBuffer[readed]=(byte)((br<128)?br:127-br);
//                    readed++;
//                }
//                intRead=readed;
//
//                if (intRead>0){
                if ((intRead=inStream.read(inBuffer))>0){
//#mdebug
//#                             if (RMSOption.debugEnabled){
//#                                 DebugLog.add2Log("Read "+intRead+" s:"+skipped);
//#                             }
//#enddebug
                    skipped=0;
                    if (fileOut!=null){
                        try {
                            fileOut.write(inBuffer, 0, intRead);
                        } catch (Throwable t) {
//#mdebug
//#                             if (RMSOption.debugEnabled){
//#                                 DebugLog.add2Log("SW5:"+t);
//#                             }
//#enddebug
                        }
                    }
                    tryCount=0;

                    System.arraycopy(inBuffer, 0, dataBuffer, dataBufferEnd, intRead);

                    /*for (int i=0; i<intRead; i++) {
                    dataBuffer[dataBufferEnd+i]=inBuffer[i];
                    }*/
                    dataBufferEnd+=intRead;

                    boolean again;
                    do {
                        again=false;
                        for (int i=0; i<dataBufferEnd; i++) {
                            if (dataBuffer[i]=='\n'){

                                try {
                                    parseGPS(dataBuffer);
                                } catch (Throwable t) {
                                }
                                System.arraycopy(dataBuffer, i+1, dataBuffer, i+1-i-1, dataBufferEnd-(i+1));

                                /*for (int j=i+1; j<dataBufferEnd; j++) {
                                dataBuffer[j-i-1]=dataBuffer[j];
                                }*/

                                dataBufferEnd-=i+1;
                                again=true;
                                break;
                            }
                        }
                    } while (again);
                    //#if SE_K750_E_BASEDEV
//#                     ////
                    //#else
                 streamHasData=(inStream.available()!=0);
                    //#endif
                } else
                    skipped++;
            }
            flag=true;

        } catch (IOException _ex) {
            //#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("GPSR E: "+_ex);
//#             }
            //#enddebug
            tryCount++;
            MapSound.playTone(MapSound.GPSLOSTTONE);
            closeConn();

            flag=true;
        }
        return flag;
    }

    private void closeConn() {
        try {
            if (inStream!=null){
                inStream.close();
            }
        } catch (Throwable _ex) {
        }

        try {
            if (streamConn!=null){
                streamConn.close();
            }
        } catch (Throwable _ex) {
        }
        inStream=null;
        streamConn=null;

        try {
            if (fileOut!=null){
                fileOut.close();
            }
        } catch (Throwable _ex) {
        }

        try {
            if (fileConn!=null){
                fileConn.close();
            }
        } catch (Throwable _ex) {
        }
        fileOut=null;
        fileConn=null;

    }
    private Thread gpsThread;
    private byte connStatus;
    private int tryCount;
    //public DataInputStream inStream;
    public InputStream inStream;
    //private String inString;

    public final void run() {
//#mdebug
//#         if (RMSOption.debugEnabled){
//#             DebugLog.add2Log("GPSR url:"+url);
//#         }
//#enddebug
        notFinished=true;
        calendarUTC=Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendarUTC.setTime(new Date());
        calendarLoc=Calendar.getInstance(TimeZone.getDefault());
        System.gc();
        //inString=MapUtil.emptyString;
        timeP=System.currentTimeMillis();
        //12timeT=0;
        timeTrackSave=System.currentTimeMillis();
        OutputStream os=null;
        try {
            streamConn=(StreamConnection) Connector.open(url,
              RMSOption.getBoolOpt(RMSOption.BL_HGE100)?Connector.READ_WRITE:Connector.READ);
            connStatus=2;

            if (RMSOption.getBoolOpt(RMSOption.BL_BLUETOOTH_AUTHENTICATE)&&url.startsWith(MapUtil.BTSPP)){
                LocStarter.makeAuthenticationFeedback(streamConn);
            }
            if (RMSOption.getBoolOpt(RMSOption.BL_HGE100)){
                try {
                    os=streamConn.openOutputStream();
                    os.write("$STA\r\n".getBytes());
//#mdebug
//#                     if (RMSOption.debugEnabled){
//#                         DebugLog.add2Log("G 100 - OK");
//#                     }
//#enddebug
                } catch (Throwable t) {
//#mdebug
//#                     if (RMSOption.debugEnabled){
//#                         DebugLog.add2Log("G 100: "+t.getMessage()+" "+t);
//#                     }
//#enddebug
                }
            }
//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("G conn opened - OK");
//#             }
//#enddebug
        } catch (Exception e) {
//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("G first conn:"+e.getMessage()+" "+e);
//#             }
//#enddebug
            if (!RMSOption.gpsAutoReconnect){
                RMSOption.urlGPS=MapUtil.emptyString;
                //stopped=true;
            }
            connStatus=0;
        }
        try {
            // int dddfdf=343434;
            try {
                for (; (connStatus==2)&&readGPSPiece()&&(!readStopped); sleepGPS());
            } finally {
                try {
                    if (cTT!=null){
                        cTT.cancel();
                        cTT=null;
                    }
                    //#ifdef Fox
//#                     if (fox!=null){
//#                         fox.stopped=true;
//#                         fox=null;
//#                     }
                    //#endif
                } catch (Throwable tty) {
                }
                closeConn();

            }
//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("G conn closed");
//#             }
//#enddebug
            MapCanvas.map.repaint();
            synchronized (gpsThread) {
                gpsThread.notify();
            }
        } finally {
            // try{
            System.gc();
            if (!readStopped){
                finishGPS(RMSOption.gpsAutoReconnect);
            }
            gpsThread=null;
            notFinished=false;
        }
//#mdebug
//#         if (RMSOption.debugEnabled){
//#             DebugLog.add2Log("G ends");
//#         }
//#enddebug
    }

    public void finishGPS(boolean autoRestart) {
        MapSound.playSound(MapSound.CONNLOSTSOUND);// MapMidlet.playConnLostSound();
        MapCanvas.map.endGPSLookup(autoRestart);
    }
//#debug
//#     //private byte wrc = 10;
    //private long lastGarbage=System.currentTimeMillis();

    public void sleepGPS() {
        try {
            //Thread.sleep(50);
            if ((RMSOption.connGPSType!=1)&&(RMSOption.connGPSType!=3)){
                Thread.sleep(20);
            } else {
                Thread.sleep(2);
            }
        } catch (InterruptedException _ex) {
            readStopped=true;
        }
    }
    private double[] mvLats=new double[8];
    private double[] mvLons=new double[8];
    private int mvIndex;
    private double prevLat, prevLon, prevLat2, prevLon2;
    private final static byte MVREADYMAXSPEED=20;
    private final static byte MVREADYSTARTVALUE=12;
    private int mvReadyLessZero=MVREADYSTARTVALUE;
    public static boolean mvReady;
    // public double mvCenterLat;
    // public double mvCenterLon;
    // public double mvCenterCoeff=1000;
    public static double COURSE_MOVE_RAD;//=MapRoute.courseToCoords(60,30,54,27);

    private final void add2MoveVector(double lat, double lon) {
        if (MapRoute.distBetweenCoords(prevLat, prevLon, lat, lon)>0.003d){
            if (MapRoute.distBetweenCoords(prevLat2, prevLon2, lat, lon)>0.006d){
                prevLat2=prevLat;
                prevLon2=prevLon;
                prevLat=lat;
                prevLon=lon;
                mvReadyLessZero--;
                mvIndex=(mvIndex>=mvLats.length-1)?0:mvIndex+1;
                mvLats[mvIndex]=lat;
                mvLons[mvIndex]=lon;
                //now calc course with leveling
                if (mvReadyLessZero<0){
                    double mlat=0, mlon=0;
                    for (int n=mvLats.length-1; n>=0; n--) {
                        mlat+=mvLats[n];
                        mlon+=mvLons[n];
                    }
                    mlat=mlat/(double) mvLats.length;
                    mlon=mlon/(double) mvLats.length;
                    mvReady=(MapRoute.distBetweenCoords(mlat, mlon, LATITUDE, LONGITUDE)>0.002);
                    if (mvReady){
                        COURSE_MOVE_RAD=MapRoute.courseToCoords(mlat, mlon, LATITUDE, LONGITUDE);
                        // mvCenterLat=lat;
                        // mvCenterLon=lon;
                    }
                } else {
                    mvReady=false;
                }
            }
        }
    }

    public void drawSats(Graphics g, int fh, int to) {
        g.setColor(0xFFFF00);
        g.setFont(MapUtil.SMALLFONT);
        int cd, fhl=g.getFont().getHeight();
        if ((MapCanvas.dmaxx+fhl+to)<(MapCanvas.dmaxy)){
            cd=(int) ((MapCanvas.dmaxx)/1.05);
        } else {
            cd=(int) ((MapCanvas.dmaxy-fhl-to)/1.05);
            //cd = 96
        }
        int sr=cd/2;// speedmeter radius   //48
        int ps=cd/12;  //ps=8
        int gpsx=MapCanvas.dcx;
        int gpsy=MapCanvas.dcy;
        gpsy+=to/2;

        if (RMSOption.mapRotate){
//        g.setColor(0x20F020);
            g.setColor(0x00C000);
            g.fillTriangle(gpsx-ps, gpsy+sr, gpsx+ps, gpsy+sr, gpsx, gpsy-sr+ps+ps+ps/2);

        }
        g.setColor(0xFFFFFF);
        //base
        g.drawArc(gpsx-sr, gpsy-sr, cd+1, cd+1, 0, 360);
        //smaller
        g.drawArc(gpsx-sr+1, gpsy-sr+1, cd-1, cd-1, 0, 360);
        //
        g.drawArc(gpsx-sr, gpsy-sr, cd, cd, 0, 360);
        //ld
        g.drawArc(gpsx-sr+1, gpsy-sr+1, cd, cd, 0, 360);
        //d
        g.drawArc(gpsx-sr, gpsy-sr+1, cd, cd, 0, 360);
        //l
        g.drawArc(gpsx-sr+1, gpsy-sr, cd, cd, 0, 360);

        g.setStrokeStyle(Graphics.DOTTED);
        //g.drawLine(gpsx-sr,gpsy,gpsx+sr,gpsy);
        //g.drawLine(gpsx,gpsy-sr,gpsx,gpsy+sr);
        g.drawArc(gpsx-sr+ps+ps, gpsy-sr+ps+ps, cd+1-ps-ps-ps-ps, cd+1-ps-ps-ps-ps, 0, 360);
        g.drawArc(gpsx-ps-ps, gpsy-ps-ps, ps+ps+ps+ps+1, ps+ps+ps+ps+1, 0, 360);
        int x, y, x1, y1;
        double crsr;
        for (float f=0; f<6; f+=1) {
            crsr=f*30*MapUtil.G2R-MapUtil.PIdiv2;
            if (RMSOption.mapRotate){
                crsr=crsr-COURSE*MapUtil.G2R;
            }
            x=(int) (gpsx+sr*Math.cos(crsr)+0.5);
            y=(int) (gpsy+sr*Math.sin(crsr)+0.5);
            x1=(int) (gpsx+sr*Math.cos(crsr+MapUtil.PI)+0.5);
            y1=(int) (gpsy+sr*Math.sin(crsr+MapUtil.PI)+0.5);
            g.drawLine(x, y, x1, y1);
        }
        g.setStrokeStyle(Graphics.SOLID);
        if (!RMSOption.mapRotate){

            g.drawString(MapUtil.SH_WEST, gpsx-sr+3, gpsy-fhl, Graphics.TOP|Graphics.LEFT);
            g.drawString(MapUtil.SH_EAST, gpsx+sr-3, gpsy-fhl, Graphics.TOP|Graphics.RIGHT);
            g.drawString(MapUtil.SH_SOUTH, gpsx+2, gpsy+sr-1, Graphics.BOTTOM|Graphics.LEFT);
            g.drawString(MapUtil.SH_NORTH, gpsx+2, gpsy-sr+1, Graphics.TOP|Graphics.LEFT);
        }
        int r, w=g.getFont().stringWidth(MapUtil.SH_SOUTHWEST), satNum;
        boolean green;
        // if (gpsReader!=null)
        for (int i=0; i<satCount; i++) {
            r=sr-satElevation[i]*sr/90;
            if (r<0){
                continue;
            }
            if (r>sr){
                continue;
            }
            crsr=satAzimuth[i]*MapUtil.G2R-MapUtil.PIdiv2;
            if (RMSOption.mapRotate){
                crsr=crsr-COURSE*MapUtil.G2R;
            }
            x=(int) (gpsx+r*Math.cos(crsr)+0.5);
            y=(int) (gpsy+r*Math.sin(crsr)+0.5);
            satNum=satNumbers[i];
            green=false;
            for (int j=0; j<satUsed.length; j++) {
                if (satUsed[j]==satNum){
                    green=true;
                    break;
                }
            }
//      if (green)
//        g.setColor(0x20A020);
//      else
//        if (satSNR[i]>0)
//          g.setColor(0xA0A020);
//        else g.setColor(0xC02020);

            if (green){
                r=(0x20B020);
            } else if (satSNR[i]>0){
                r=(0xB0B020);
            } else {
                r=(0xC02020);
            }
            x1=satSNR[i]+10;
            if (x1>40){
                x1=40;
            }
            x1=25*x1/10;
            if (satSNR[i]==0){
                x1=100;
            }
            g.setColor(GraphUtils.fadeColor(r, x1));

            g.fillArc(x-w/2-1, y-w/2, w+2, w+1, 0, 360);
            g.setColor(0xFFFFFF);
            g.drawArc(x-w/2-1, y-w/2, w+2, w+1, 0, 360);
            String s;
            if (MapCanvas.satMode==MapCanvas.SATSIGNALMODE){
                if (satSNR[i]>0){
                    s=String.valueOf(satSNR[i]);
                    g.drawString(s, x+1, y+fhl/2+1, Graphics.HCENTER|Graphics.BOTTOM);
                }
            } else //if (satMode==SATNUMBERMODE)
            {
                s=String.valueOf(satNumbers[i]);
                g.drawString(s, x+1, y+fhl/2+1, Graphics.HCENTER|Graphics.BOTTOM);
            }
        }

    }
}
