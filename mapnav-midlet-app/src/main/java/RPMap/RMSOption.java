/*
 * RMSOption.java
 *
 * Created on 20 01 2007 ., 16:24
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package RPMap;

import java.io.*;
import javax.microedition.lcdui.Item;
import lang.Lang;
import misc.ProgressReadWritable;
import misc.ProgressResponse;

/**
 * Static class for options
 * @author RFK
 */
public final class RMSOption implements ProgressReadWritable {

    public static String surGL_URL="http://khm1.google.com/kh/v=88&x=";
    //public static String netGL_URLtr="http://mt0.google.com/mt?v=nqt.83&x=";
    public static String netGL_URLtr="http://mt0.google.com/vt/lyrs=h@157000000&hl=en&x=";
    //GET /mt?v=w2t.83&hl=ru&x=25&y=11&z=5&s=Galile HTTP/1.1
    //public static String netGL_URLtr="http://mt0.google.com/mt?n=404&v=apt.67&x=";
    public static boolean scaleMap;//=false;
    public static boolean parallelLoad;//=false;
    public static int cacheSize;//=0;
    public static boolean onlineMap=true;
    public static String lastURL="mapnav.spb.ru/lake.rte";
    public static byte currLang=-1;
    static boolean zooSent;//=false;
    static int xCenter=127;
    static int yCenter=127;
    static int level=1;
    static int mapServ=MapTile.SHOW_VE+MapTile.SHOW_SUR;
    public static boolean fullScreen;
    public static String lastSendMMSURL="";
    public static String lastSendFileURL="c:/other/";
    public static boolean safeMode=(Runtime.getRuntime().totalMemory()<500000);
    public static byte fontSize=1;
    public static byte fontStyle;//= 0;
    public static long trackPeriod=60000;
    static int activeTrackId=-1;
    public static int picFileNumber=1;
    public static double trackDist=0.100;
    final public static byte USETIME=1;
    final public static byte USEDIST=2;
    public static byte trackRecordUse=(byte) (USETIME+USEDIST);
    public static String lastSMSNumber="";
    public static String lastSMSText="I am here [your name]";
    public static int lastWPId=-1;
    public static int lastRTId=-1;
    public static int lastTRId=-1;
    public static String lastMapName="c:/other/map.mnm"; //----OBSOLETE--
    final public static byte COORDMINSECTYPE=1;
    final public static byte COORDMINMMMTYPE=2;
    final public static byte COORDGGGGGGTYPE=3;
    final public static byte COORDGGGMMMMMMTYPE=4;
    public static byte coordType=COORDMINMMMTYPE;
    final public static byte TRSHOWLAST100=1;
    final public static byte TRSHOWALL=2;
    public static byte showTrPoints=TRSHOWLAST100;
    public static boolean debugEnabled;//=false;
    public static boolean lightOn=true;
    //public static boolean blueMaster;//=false;   ----OBSOLETE--
    public static boolean showCoords=true;
    public static String lastTrackSavePath="c:/other/";
    public static boolean showMarks=true;
    public static boolean blinkLight;
    public static long blinkInterval=9000;
    public static boolean soundOn=true;
    public static boolean satSoundStatus=true;
    public static String lastTel1="";
    public static String lastTel2="";
    public static String lastTel3="";
    public static boolean gpsAutoReconnect=true;
    public static String netRadarLogin="";
    public static String netRadarPass="";
    public static boolean mapCorrectMode;//=false;
    public static String logSavePath="c:/other/";
    public static boolean foxHunter;//=false;
    //public static boolean showScale;//=false;
    public static boolean netRadarWasActive;//=false;
    /** in meters! */
    public static float ODOMETER;
    public final static byte MAPVSCOUNT=16;
    public static boolean[] mapVS={true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false};
    public static int defTRId=-1;
    public static boolean mapRotate;//=false;
    public static boolean limitImgRot=true;
    public static boolean trackAutoStart=true;
    public static boolean holdInetConn;//=false;
    public static int routeProximity=50;
    public static boolean autoSelectWpt=true;
    public static boolean compassOverMap;
    public static boolean addTrackPointOnTurn=true;
    public static boolean langSelected;//=false;
    public static byte connGPSType;
    public static String comGPSPort="COM0";
    public static String urlGPS="";
    public static String lastBTDeviceName="";
    public static boolean writeNMEA;//=false;
    public static int numberMapPoint=1;
    public static boolean saveMapCorrectMode;//=false;
    public static int mapDX;//=0;
    public static int mapDY;//=0;
    public static final int DATUMWGS84=1;
    public static final int DATUMPULKOVO1942=2;
    public static int showDatum=DATUMWGS84;
    public static boolean light50;//=false;
    public static int choicePicSize=3;
    public static int choiceSendType=1;
    public static int choiceSerInterval;
    public static int choiceSerCount;
    public static String lastCMapName="E:/others/map.mnm";
    //public static String netGL_URL="http://mt1.google.com/mt/v=nq.83&x=";
    public static String netGL_URL="http://mt1.google.com/vt/lyrs=m@157000000&hl=ru&x=";
    public static boolean cleanDefaultTrack=true;
    public static boolean correctMapAll;
    public static String[] BT_DEVICE_NAMES={};
    public static String[] BT_DEVICE_URLS={};
    public static final int PROJ_GEO=1;
    public static final int PROJ_UTM=2;
    public static final int PROJ_GAUSSKRUG=3;
    public static int showProj=PROJ_GEO;
    public static int currModeIndex;
    public static int currInfoModeIndex;
    public static int currNavModeIndex;
    public static int currPrfModeIndex;
    public static int currSpdModeIndex;
    public static byte routeCP;
    public static byte imageType;
    public static String infoAboutLastVersion="";
    public static byte routeFormat;
    public static boolean tellNewDebugVersion;
    private final static byte BOOLCOUNT=31;
    public static boolean[] boolOptions={false, true, true, false, false, false, false, true, false, true,
        true, false, true, false, false, false, false, false, false, true,
        false, false, true, true, false, false, false, false, false, true, false};
    public final static byte BL_ADDMINIZE=0;
    public final static byte BL_AUTOCENTEROM=1;
    public final static byte BL_VIBRATE_ON=2;
    public final static byte BL_ADDTRACKPOINTSOUND_ON=3;
    public final static byte BL_CROSS_OFF=4;
    public final static byte BL_MARKS_ADDED=5;
    public final static byte BL_TRANS_POINTER=6;
    public final static byte BL_SMOOTH_SCROLL=7;
    public final static byte BL_AUTOWPTNEAREST=8;
    public final static byte BL_DARKENBACK=9;
    public final static byte BL_LIGHTONLOCK=10;
    public final static byte BL_TRANS50=11;
    public final static byte BL_WRITETRACK=12;
    public final static byte BL_RAWDATA=13;
    public final static byte BL_HGE100=14;
    public final static byte BL_WARNMAXSPEED=15;
    public final static byte BL_CACHEINDEX=16;
    public final static byte BL_REALTIMENR=17;
    public final static byte BL_SHOWTRACKNR=18;
    public final static byte BL_LIMITTRACKSHOWROTATE=19;
    public final static byte BL_RELOADWITHDELETE=20;
    public final static byte BL_EXTCACHEUSE=21;
    public final static byte BL_SHOWCLOCKONMAP=22;
    public final static byte BL_WAYPOINTNOTIFY=23;
    public final static byte BL_GT_AUTOSEND=24;
    public final static byte BL_HIDE_ROUTE_DIRECTION=25;
    public final static byte BL_WARNDOWNSPEED=26;
    public final static byte BL_TRACKBACKUP=27;
    public final static byte BL_BLUETOOTH_AUTHENTICATE=28;
    public final static byte BL_BLUETOOTH_MONITOR=29;
    public final static byte BL_REALTIMENR_UDP=30;
    //  public final static byte BL_CHECKGPSALIVE=20;
    private final static byte BYTECOUNT=19;
    public static byte[] byteOptions={0, 0, 0, 0, 1, 3, 5, 0, 0, 0, 0, 100, 0, 4, 3, 0, 0, 0, 1};
    public final static byte BO_HOLD5ADD=0;
    public final static byte BO_EXTMAPINDEX=1;
    public final static byte BO_SCALETYPE=2;
    public final static byte BO_IMPFORMATWP=3;
    public final static byte BO_VIEW2=4;
    public final static byte BO_VIEW3=5;
    public final static byte BO_VIEW4=6;
    public final static byte BO_SPLITMODE=7;
    //public final static byte BO_RADARSITE=8;
    public final static byte BO_IMPFORMATRT=9;
    public final static byte BO_IMPFORMATTR=10;
    public final static byte BO_VOLUME=11;
    public final static byte BO_NRPERIOD=12;
    public final static byte BO_GT_SPD=13;
    public final static byte BO_GT_DIST=14;
    public final static byte BO_TR102_INTERVAL=15;
    public final static byte BO_ONLINE_SUR_TYPE=16;
    public final static byte BO_ONLINE_MAP_TYPE=17;
    public final static byte BO_CACHE_FORMAT_TYPE=18;

    public final static byte CACHE_FORMAT_MV=0;
    public final static byte CACHE_FORMAT_GMT=1;

    public final static byte SCALE_NOTHING=0;
    public final static byte SCALE_LINES=1;
    public final static byte SCALE_CIRCLES=2;
    public final static byte HOLD5_WPT=0;
    public final static byte HOLD5_MARK=1;
    public final static byte HOLD5_TRACK=2;
    public static String[] OMAPS_NAMES={};
    public static String[] OMAPS_URLS={};
    private final static byte STRINGCOUNT=13;
    public static String[] stringOptions={"", "", "", "", "", "", "", "http://a.tah.openstreetmap.org/Tiles/tile/", "MegaVAZ", "", "", "", ""};
    public final static byte SO_WORKPATH=0;
    public final static byte SO_FLICKRTOKEN=1;
    public final static byte SO_FLICKRFULL=2;
    public final static byte SO_YOURNAME=3;
    public final static byte SO_EXTCACHEPATH=4;
    public final static byte SO_OSMLOGIN=5;
    public final static byte SO_OSMPASS=6;
    public final static byte SO_OSMURL=7;
    public final static byte SO_GT_NAME=8;
    public final static byte SO_TR102_SERVER=9;
    public final static byte SO_TR102_IMEI=10;
    public final static byte SO_ONLINE_URL_SUR=11;
    public final static byte SO_ONLINE_URL_MAP=12;

    public static int foreColor=0xFFFFFF;
    public static int shadowColor=0x0;
    public static final byte UNITS_METRIC=0;
    public static final byte UNITS_NAUTICAL=1;
    public static final byte UNITS_IMPERIAL=2;
    public static byte unitFormat;//=UNITS_METRIC
    public static int maxSpeed=90;
    public static final byte KF_NONE=0;
    public static final byte KF_LANDSCAPE=1;
    public static final byte KF_LIGHT=2;
    public static final byte KF_SENDSMS=3;
    public static final byte KF_SCALEPLUS=4;
    public static final byte KF_SCALEMINUS=5;
    public static final byte KF_ADDWPT=6;
    public static final byte KF_SETTINGS=7;
    public static final byte KF_SCREENSHOT=8;
    public static final byte KF_FLICKR=9;
    public static final byte KF_CHANGEMAPSERVER=10;
    public static final byte KF_CHANGEMAPTYPE=11;
    public static final byte KF_PRECISELOCATION=12;
    public static final byte KF_NEXTWPT=13;
    public static final byte KF_NEARESTWPT=14;
    public static final byte KF_SOUNDONOFF=15;
    public static final byte KF_TRACKWRITE=16;
    public static final byte KF_GEOCACHING=17;
    public static final byte KF_MYPOI=18;
    public static final byte KF_SENDNETRADARPOS=19;
    public static final byte KF_TRACKCROSS=20;
    public static final byte KF_SCALEPLUS3=21;
    public static final byte KF_SCALEMINUS3=22;
    public static final byte KF_CHANGEMAP=23;
    public static final byte KF_NETRADAR_WT=24;
    public static final byte KF_INVERSEMAP=25;
    public static final byte KF_VIEWPORT=26;
    public static final byte KF_SCALE=27;
    public static final byte KF_DOUBLESIZE=28;
    public static final byte KF_REFRESH=29;
    public static final byte KF_NEXTSCREEN=30;
    //DO NOT STORE IT
    public static short[] keyLangs={Lang.nosel, Lang.landscape, Lang.light, Lang.sendcsms,
        Lang.zoomin,
        Lang.zoomout, Lang.addmark, Lang.options, Lang.takescreen, -1,
        Lang.mapsrc, Lang.map, Lang.precpos, Lang.next1, Lang.nearestwpt,
        Lang.sounds, Lang.gpsqmtrrec, Lang.getgeocache,
        Lang.mypoi, Lang.sendnetradar,
        Lang.trackcross, -2, -3, -4, -5,
        Lang.inversemap, Lang.splitview2, Lang.scaleyes, -6, Lang.refresh,
        Lang.screen
    };
    public static final byte KEY_4=0;//KF_LANDSCAPE
    public static final byte KEY_6=1;//KF_LIGHT
    public static final byte KEY_1=2;//KF_SCALEMINUS
    public static final byte KEY_3=3;//KF_SCALEPLUS
    public static final byte KEY_5HOLD=4;//KF_ADDWPT
    public static final byte KEY_6HOLD=5;//KF_SETTINGS
    public static final byte KEY_4HOLD=6;//KF_SENDSMS
    public static final byte KEY_7=7;//KF_TRACKWRITE
    public static final byte KEY_9=8;//KF_SOUNDONOFF
    public static final byte KEY_0=9;//KF_CHANGEMAPSERVER
    public static final byte KEY_SHARP=10;//KF_CHANGEMAPTYPE
    public static final byte KEY_1HOLD=11;
    public static final byte KEY_3HOLD=12;
    public static final byte KEY_7HOLD=13;//KF_GEOCACHING
    public static final byte KEY_9HOLD=14;//KF_TRACKCROSS
    public static final byte KEY_2=15;//KF_SOUNDONOFF
    public static final byte KEY_8=16;//KF_VIEWPORT
    public static final byte KEY_2HOLD=17;//KF_REFRESH
    public static final byte KEY_8HOLD=18;//KF_DOUBLESIZE
    private static final byte KEYFUNCTIONCOUNT=19;
    public static byte[] keyFunction={KF_LANDSCAPE, KF_LIGHT, KF_SCALEPLUS, KF_SCALEMINUS, KF_ADDWPT,
        KF_SETTINGS, KF_SENDSMS, KF_TRACKWRITE, KF_INVERSEMAP, KF_CHANGEMAPSERVER,
        KF_CHANGEMAPTYPE, KF_NONE, KF_NONE, KF_GEOCACHING, KF_TRACKCROSS,
        KF_NEXTSCREEN, KF_VIEWPORT, KF_REFRESH, KF_DOUBLESIZE};
    public static int maxTrackPoints=3000;
    /** in meters! */
    public static float DISTANCE;
    public static boolean coloredTrack=true;
    /** ����� ������ GPS */
    public static long USED;
    public static short mapDL;
    public static long lastUpdateTime;
    public static String[] BT_DEVICE_NAMES_SEND={};
    public static String[] BT_DEVICE_URLS_SEND={};//public static boolean doubleScaleMap=true;
    public static int adsNumber;
    private final static byte DOUBLECOUNT=4;
    public static double[] doubleOptions={2, 2, 7, 5};
    public final static byte DO_MAXDESCENTSPD=0;
    public final static byte DO_MAXCLIMBSPD=1;
    public final static byte DO_MIN_TEMP=2;
    public final static byte DO_MAX_TEMP=3;

    public final static byte ROUTE_SEARCH_GOOGLE=1;
    public final static byte ROUTE_SEARCH_CLOUD=0;
    public static byte routeSearchType=ROUTE_SEARCH_CLOUD;

    public static long gpsReconnectDelay=30000;
    //public static long lastUpdateDate;
//--- stored alone------
    public static String[] IMAPS_NAMES={};
    public static String[] IMAPS_RMST={};
    public static String[] IMAPS_RMSP={};
    public static float[] IMAPS_CENT={};

    /** Saves options content to stream */
    public static void save2Stream(DataOutputStream os) throws IOException {
        os.writeInt(123);
        os.writeUTF(surGL_URL);
        os.writeUTF(netGL_URLtr);
        os.writeBoolean(scaleMap);
        os.writeBoolean(parallelLoad);
        os.writeInt(cacheSize);
        os.writeBoolean(onlineMap);
        os.writeUTF(lastURL);
        os.writeByte(currLang);
        os.writeBoolean(zooSent);
        os.writeInt(xCenter);
        os.writeInt(yCenter);
        os.writeInt(level);
        os.writeInt(mapServ);
        os.writeBoolean(fullScreen);
        os.writeUTF(lastSendMMSURL);
        os.writeUTF(lastSendFileURL);
        os.writeBoolean(safeMode);
        os.writeByte(fontSize);
        os.writeByte(fontStyle);
        os.writeLong(trackPeriod);
        os.writeInt(activeTrackId);
        os.writeInt(picFileNumber);
        os.writeByte(trackRecordUse);
        os.writeDouble(trackDist);
        os.writeUTF(lastSMSNumber);
        os.writeUTF(lastSMSText);
        os.writeInt(lastWPId);
        os.writeInt(lastRTId);
        os.writeInt(lastTRId);
        os.writeUTF(lastMapName);
        os.writeByte(coordType);
        os.writeByte(showTrPoints);
        os.writeBoolean(debugEnabled);
        os.writeBoolean(lightOn);
        os.writeBoolean(showCoords);
        os.writeUTF(lastTrackSavePath);
        os.writeBoolean(showMarks);
        os.writeBoolean(blinkLight);
        os.writeLong(blinkInterval);
        os.writeBoolean(zooSent);
        os.writeBoolean(soundOn);
        os.writeBoolean(satSoundStatus);
        os.writeUTF(lastTel1);
        os.writeUTF(lastTel2);
        os.writeUTF(lastTel3);
        os.writeBoolean(gpsAutoReconnect);
        os.writeUTF(netRadarLogin);
        os.writeUTF(netRadarPass);
        os.writeBoolean(mapCorrectMode);
        os.writeUTF(logSavePath);
        os.writeBoolean(foxHunter);
        os.writeBoolean(netRadarWasActive);
        os.writeFloat(ODOMETER);

        os.writeInt(mapVS.length);
        for (int i=0; i<mapVS.length; i++) {
            os.writeBoolean(mapVS[i]);
        }
        os.writeInt(defTRId);
        os.writeBoolean(mapRotate);
        os.writeBoolean(limitImgRot);
        os.writeBoolean(trackAutoStart);
        os.writeBoolean(holdInetConn);
        os.writeInt(routeProximity);
        os.writeBoolean(autoSelectWpt);
        os.writeBoolean(compassOverMap);
        os.writeBoolean(addTrackPointOnTurn);
        os.writeBoolean(langSelected);
        os.writeByte(connGPSType);
        os.writeUTF(comGPSPort);
        os.writeUTF(urlGPS);
        os.writeUTF(lastBTDeviceName);
        os.writeBoolean(writeNMEA);
        os.writeInt(numberMapPoint);
        os.writeBoolean(saveMapCorrectMode);
        os.writeInt(mapDX);
        os.writeInt(mapDY);
        os.writeInt(showDatum);
        os.writeBoolean(light50);
        //usedTime += (System.currentTimeMillis()-started2use);
        //started2use = System.currentTimeMillis();
        //os.writeLong(usedTime);
        //os.writeLong(0);

        os.writeInt(choicePicSize);
        os.writeInt(choiceSendType);
        os.writeInt(choiceSerInterval);
        os.writeInt(choiceSerCount);

        os.writeUTF(lastCMapName);

//    os.writeUTF(activeCMapFilename); //----OBSOLETE--

        os.writeUTF(netGL_URL);
        os.writeBoolean(cleanDefaultTrack);
        os.writeBoolean(correctMapAll);

        os.writeByte(BT_DEVICE_NAMES.length);
        for (int i=0; i<BT_DEVICE_NAMES.length; i++) {
            os.writeUTF(BT_DEVICE_NAMES[i]);
        }
        os.writeByte(BT_DEVICE_URLS.length);
        for (int i=0; i<BT_DEVICE_URLS.length; i++) {
            os.writeUTF(BT_DEVICE_URLS[i]);
        }
        os.writeInt(showProj);

        os.writeInt(currModeIndex);
        os.writeInt(currInfoModeIndex);
        os.writeInt(currNavModeIndex);
        os.writeInt(currPrfModeIndex);
        os.writeInt(currSpdModeIndex);
        os.writeByte(routeCP);
        os.writeByte(imageType);
        os.writeUTF(infoAboutLastVersion);
        os.writeByte(routeFormat);
        os.writeBoolean(tellNewDebugVersion);

        os.writeInt(boolOptions.length);
        for (int i=0; i<boolOptions.length; i++) {
            os.writeBoolean(boolOptions[i]);
        }
        os.writeInt(byteOptions.length);
        for (int i=0; i<byteOptions.length; i++) {
            os.writeByte(byteOptions[i]);
        }
        os.writeByte(OMAPS_NAMES.length);
        for (int i=0; i<OMAPS_NAMES.length; i++) {
            os.writeUTF(OMAPS_NAMES[i]);
        }
        os.writeByte(OMAPS_URLS.length);
        for (int i=0; i<OMAPS_URLS.length; i++) {
            os.writeUTF(OMAPS_URLS[i]);
        }
        os.writeInt(stringOptions.length);
        for (int i=0; i<stringOptions.length; i++) {
            os.writeUTF(stringOptions[i]);
        }
        os.writeInt(foreColor);
        os.writeInt(shadowColor);
        os.writeByte(unitFormat);
        os.writeInt(maxSpeed);

        os.writeInt(keyFunction.length);
        for (int i=0; i<keyFunction.length; i++) {
            os.writeByte(keyFunction[i]);
        }
        os.writeInt(maxTrackPoints);
        os.writeFloat(DISTANCE);
        os.writeBoolean(coloredTrack);
        os.writeLong(USED);
        os.writeShort(mapDL);
        os.writeLong(lastUpdateTime);

        os.writeByte(BT_DEVICE_NAMES_SEND.length);
        for (int i=0; i<BT_DEVICE_NAMES_SEND.length; i++) {
            os.writeUTF(BT_DEVICE_NAMES_SEND[i]);
        }
        os.writeByte(BT_DEVICE_URLS_SEND.length);
        for (int i=0; i<BT_DEVICE_URLS_SEND.length; i++) {
            os.writeUTF(BT_DEVICE_URLS_SEND[i]);
        }
        os.writeInt(adsNumber);

        os.writeInt(doubleOptions.length);
        for (int i=0; i<doubleOptions.length; i++) {
            os.writeDouble(doubleOptions[i]);
        }
        os.writeByte(routeSearchType);
        os.writeLong(gpsReconnectDelay);

    }

    /** Saves options content to stream */
    public static void saveCM2Stream(DataOutputStream os) throws IOException {
        os.writeInt(2);

        os.writeByte(IMAPS_NAMES.length);
        for (int i=0; i<IMAPS_NAMES.length; i++) {
            os.writeUTF(IMAPS_NAMES[i]);
        }
        os.writeByte(IMAPS_RMST.length);
        for (int i=0; i<IMAPS_RMST.length; i++) {
            os.writeUTF(IMAPS_RMST[i]);
        }
        os.writeByte(IMAPS_RMSP.length);
        for (int i=0; i<IMAPS_RMSP.length; i++) {
            os.writeUTF(IMAPS_RMSP[i]);
        }
        os.writeByte(IMAPS_CENT.length);
        for (int i=0; i<IMAPS_CENT.length; i++) {
            os.writeFloat(IMAPS_CENT[i]);
        }
    }

    /** load options content from stream */
    public static void loadCMFromStream(DataInputStream is) throws IOException {
//    started2use = System.currentTimeMillis();
        int nr=is.readInt();
        String s;

        if (nr>1){
            IMAPS_NAMES=new String[is.readByte()];
            for (int i=0; i<IMAPS_NAMES.length; i++) {
                IMAPS_NAMES[i]=is.readUTF();
            }
            IMAPS_RMST=new String[is.readByte()];
            for (int i=0; i<IMAPS_RMST.length; i++) {
                IMAPS_RMST[i]=is.readUTF();
            }
            IMAPS_RMSP=new String[is.readByte()];
            for (int i=0; i<IMAPS_RMSP.length; i++) {
                IMAPS_RMSP[i]=is.readUTF();
            }
            IMAPS_CENT=new float[is.readByte()];
            for (int i=0; i<IMAPS_CENT.length; i++) {
                IMAPS_CENT[i]=is.readFloat();
            }
        }

    }

    /** load options content from stream */
    public static void loadFromStream(DataInputStream is) throws IOException {
//    started2use = System.currentTimeMillis();
        int nr=is.readInt();
        String s;
        surGL_URL=is.readUTF();
        netGL_URLtr=is.readUTF();
        //mapURL=is.readUTF();
        //netURL=is.readUTF();
        scaleMap=is.readBoolean();
        parallelLoad=is.readBoolean();
        cacheSize=is.readInt();
        onlineMap=is.readBoolean();
        lastURL=is.readUTF();
        currLang=is.readByte();
        //currLang=0;
        boolean zooSentOld=is.readBoolean();
        xCenter=is.readInt();
        yCenter=is.readInt();
        level=is.readInt();
        mapServ=is.readInt();
        fullScreen=is.readBoolean();
        lastSendMMSURL=is.readUTF();
        lastSendFileURL=is.readUTF();
        safeMode=is.readBoolean();
        fontSize=is.readByte();
        fontStyle=is.readByte();
        trackPeriod=is.readLong();
        activeTrackId=is.readInt();
        picFileNumber=is.readInt();
        trackRecordUse=is.readByte();
        trackDist=is.readDouble();
        lastSMSNumber=is.readUTF();
        lastSMSText=is.readUTF();
        lastWPId=is.readInt();
        lastRTId=is.readInt();
        lastTRId=is.readInt();
        lastMapName=is.readUTF();
        coordType=is.readByte();
        showTrPoints=is.readByte();
        debugEnabled=is.readBoolean();
        lightOn=is.readBoolean();
        showCoords=is.readBoolean();
        lastTrackSavePath=is.readUTF();
        showMarks=is.readBoolean();
        blinkLight=is.readBoolean();
        blinkInterval=is.readLong();
        zooSent=is.readBoolean();
        soundOn=is.readBoolean();
        satSoundStatus=is.readBoolean();
        lastTel1=is.readUTF();
        lastTel2=is.readUTF();
        lastTel3=is.readUTF();
        gpsAutoReconnect=is.readBoolean();
        netRadarLogin=is.readUTF();
        netRadarPass=is.readUTF();
        mapCorrectMode=is.readBoolean();
        logSavePath=is.readUTF();
        foxHunter=is.readBoolean();
        netRadarWasActive=is.readBoolean();
        ODOMETER=is.readFloat();
        int bs=is.readInt();
        boolean[] b=new boolean[MAPVSCOUNT];
        for (int i=0; i<b.length; i++) {
            b[i]=true;
        }
        b[14]=false;
        b[15]=false;
        for (int i=0; i<bs; i++) {
            b[i]=is.readBoolean();
        }
        mapVS=b;
        defTRId=is.readInt();
        mapRotate=is.readBoolean();
        limitImgRot=is.readBoolean();
        trackAutoStart=is.readBoolean();
        holdInetConn=is.readBoolean();
        routeProximity=is.readInt();
        autoSelectWpt=is.readBoolean();
        compassOverMap=is.readBoolean();
        addTrackPointOnTurn=is.readBoolean();
        langSelected=is.readBoolean();

        connGPSType=is.readByte();
        comGPSPort=is.readUTF();
        urlGPS=is.readUTF();
        lastBTDeviceName=is.readUTF();
        writeNMEA=is.readBoolean();
        numberMapPoint=is.readInt();
        saveMapCorrectMode=is.readBoolean();
        mapDX=is.readInt();
        mapDY=is.readInt();
        showDatum=is.readInt();
        light50=is.readBoolean();
        //is.readLong();
        //usedTime=is.readLong();
        choicePicSize=is.readInt();
        choiceSendType=is.readInt();
        choiceSerInterval=is.readInt();
        choiceSerCount=is.readInt();
        lastCMapName=is.readUTF();
        netGL_URL=is.readUTF();
        cleanDefaultTrack=is.readBoolean();

        correctMapAll=is.readBoolean();

        BT_DEVICE_NAMES=new String[is.readByte()];
        for (int i=0; i<BT_DEVICE_NAMES.length; i++) {
            BT_DEVICE_NAMES[i]=is.readUTF();
        }
        BT_DEVICE_URLS=new String[is.readByte()];
        for (int i=0; i<BT_DEVICE_URLS.length; i++) {
            BT_DEVICE_URLS[i]=is.readUTF();
        }
        showProj=is.readInt();

        currModeIndex=is.readInt();
        currInfoModeIndex=is.readInt();
        currNavModeIndex=is.readInt();
        currPrfModeIndex=is.readInt();
        currSpdModeIndex=is.readInt();
        routeCP=is.readByte();
        imageType=is.readByte();
        infoAboutLastVersion=is.readUTF();
        routeFormat=is.readByte();
        tellNewDebugVersion=is.readBoolean();

        bs=is.readInt();
        b=new boolean[BOOLCOUNT];
        //for (int i=0;i<b.length;i++) b[i]=false;
        b[BL_SMOOTH_SCROLL]=true;
        b[BL_WRITETRACK]=true;
        b[BL_LIMITTRACKSHOWROTATE]=true;
        b[BL_SHOWCLOCKONMAP]=true;
        b[BL_WAYPOINTNOTIFY]=true;
        b[BL_BLUETOOTH_MONITOR]=true;
        for (int i=0; i<bs; i++) {
            b[i]=is.readBoolean();
        }
        boolOptions=b;

        bs=is.readInt();
        byte[] bt=new byte[BYTECOUNT];
        //for (int i=0;i<b.length;i++) b[i]=0;
        bt[BO_VIEW2]=1;
        bt[BO_VIEW3]=3;
        bt[BO_VIEW4]=5;
        bt[BO_VOLUME]=100;
        bt[BO_GT_SPD]=4;
        bt[BO_GT_DIST]=3;

        for (int i=0; i<bs; i++) {
            bt[i]=is.readByte();
        }
        byteOptions=bt;

        OMAPS_NAMES=new String[is.readByte()];
        for (int i=0; i<OMAPS_NAMES.length; i++) {
            OMAPS_NAMES[i]=is.readUTF();
        }
        OMAPS_URLS=new String[is.readByte()];
        for (int i=0; i<OMAPS_URLS.length; i++) {
            OMAPS_URLS[i]=is.readUTF();
        }
        bs=is.readInt();
        String[] sb=new String[STRINGCOUNT];
        for (int i=0; i<sb.length; i++) {
            sb[i]=MapUtil.emptyString;
        }
        sb[SO_OSMURL]="http://a.tah.openstreetmap.org/Tiles/tile/";
        sb[SO_GT_NAME]="MegaVAZ";
        for (int i=0; i<bs; i++) {
            sb[i]=is.readUTF();
        }
        stringOptions=sb;

        foreColor=is.readInt();
        shadowColor=is.readInt();
        unitFormat=is.readByte();
        maxSpeed=is.readInt();
        bs=is.readInt();
        bt=new byte[KEYFUNCTIONCOUNT];
        bt[KEY_2]=KF_NEXTSCREEN;
        bt[KEY_8]=KF_VIEWPORT;
        for (int i=0; i<bs; i++) {
            bt[i]=is.readByte();
        }
        keyFunction=bt;
        maxTrackPoints=is.readInt();
        DISTANCE=is.readFloat();
        coloredTrack=is.readBoolean();
        USED=is.readLong();
        mapDL=is.readShort();
        lastUpdateTime=is.readLong();
        if (nr>118){
            BT_DEVICE_NAMES_SEND=new String[is.readByte()];
            for (int i=0; i<BT_DEVICE_NAMES_SEND.length; i++) {
                BT_DEVICE_NAMES_SEND[i]=is.readUTF();
            }
            BT_DEVICE_URLS_SEND=new String[is.readByte()];
            for (int i=0; i<BT_DEVICE_URLS_SEND.length; i++) {
                BT_DEVICE_URLS_SEND[i]=is.readUTF();
            }

        }
        if (nr>119){
            adsNumber=is.readInt();
        }
        if (nr>120){
            bs=is.readInt();
            double[] db=new double[DOUBLECOUNT];
            db[DO_MAXCLIMBSPD]=2;
            db[DO_MAXDESCENTSPD]=2;

            for (int i=0; i<bs; i++) {
                db[i]=is.readDouble();
            }
            doubleOptions=db;
        }
        if (nr>121){
           routeSearchType = is.readByte();
        }
        if (nr>122){
           gpsReconnectDelay = is.readLong();
        }
        //  surGL_URL="http://khm1.google.com/kh/v=36&hl=en&x=";
    }

    public static byte getTrackDistIndex() {
        if (trackDist==0.01){
            return 0;
        } else if (trackDist==0.03){
            return 1;
        } else if (trackDist==0.06){
            return 2;
        } else if (trackDist==0.1){
            return 3;
        } else if (trackDist==0.25){
            return 4;
        } else if (trackDist==0.5){
            return 5;
        } else if (trackDist==1.){
            return 6;
        } else if (trackDist==2.){
            return 7;
        } else //if (trackPeriod==300000)
        {
            return 8;
        }
    }

    public static void setTrackDistIndex(int ind) {
        double wtd=trackDist;
        if (ind==0){
            trackDist=0.01;
        } else if (ind==1){
            trackDist=0.03;
        } else if (ind==2){
            trackDist=0.06;
        } else if (ind==3){
            trackDist=0.1;
        } else if (ind==4){
            trackDist=0.25;
        } else if (ind==5){
            trackDist=0.5;
        } else if (ind==6){
            trackDist=1.;
        } else if (ind==7){
            trackDist=2.;
        } else //if (ind==1)
        {
            trackDist=5.;
        }
        if (wtd!=trackDist){
            changed=true;
        }
    }

    public static byte getTrackPeriodIndex() {
        if (trackPeriod==1000){
            return 0;
        } else if (trackPeriod==5000){
            return 1;
        } else if (trackPeriod==10000){
            return 2;
        } else if (trackPeriod==30000){
            return 3;
        } else if (trackPeriod==60000){
            return 4;
        } else if (trackPeriod==120000){
            return 5;
        } else //if (trackPeriod==300000)
        {
            return 6;
        }
    }

    public static void setTrackPeriodIndex(int ind) {
        long wtp=trackPeriod;
        if (ind==0){
            trackPeriod=1000;
        } else if (ind==1){
            trackPeriod=5000;
        } else if (ind==2){
            trackPeriod=10000;
        } else if (ind==3){
            trackPeriod=30000;
        } else if (ind==4){
            trackPeriod=60000;
        } else if (ind==5){
            trackPeriod=120000;
        } else //if (ind==1)
        {
            trackPeriod=300000;
        }
        if (wtp!=trackPeriod){
            changed=true;
        }
    }

    public static byte getProxIndex() {
        if (routeProximity==10){
            return 0;
        } else if (routeProximity==50){
            return 1;
        } else if (routeProximity==100){
            return 2;
        } else if (routeProximity==250){
            return 3;
        } else if (routeProximity==500){
            return 4;
        } else //if (routeProximity==1000)
        {
            return 5;
        }
    }

    public static void setProxIndex(int ind) {
        int wtp=routeProximity;
        if (ind==0){
            routeProximity=10;
        } else if (ind==1){
            routeProximity=50;
        } else if (ind==2){
            routeProximity=100;
        } else if (ind==3){
            routeProximity=250;
        } else if (ind==4){
            routeProximity=500;
        } else //if (ind==1)
        {
            routeProximity=1000;
        }
        if (wtp!=routeProximity){
            changed=true;
        }
    }
    /** Signals about save needs */
    public static boolean changed=false;
    private static byte[] bt22;

    public static void push() {
        try {
            ByteArrayOutputStream ba=new ByteArrayOutputStream();
            DataOutputStream dos=new DataOutputStream(ba);
            save2Stream(dos);
            bt22=ba.toByteArray();
        } catch (Exception e) {
        }
    }

    public static void pop() {
        if (bt22==null){
            return;
        }
        try {
            ByteArrayInputStream bi=new ByteArrayInputStream(bt22);
            DataInputStream dis=new DataInputStream(bi);
            loadFromStream(dis);
        } catch (Exception e) {
        }
    }

    public static final boolean bluetoothGPS() {
        return (connGPSType==0);
    }

    public static final boolean commGPS() {
        return (connGPSType==1)||((connGPSType==3));
    }

    public static final boolean builtinGPS() {
        return (connGPSType==2);
    }

    public static final String getGPSCommUrl() {
        if (connGPSType==1){
            return "comm:"+comGPSPort;
        } else {
            return "socket://"+comGPSPort;
        }
    }

    public static boolean getBoolOpt(byte opt) {
        return boolOptions[opt];
    }

    public static void setBoolOpt(byte opt, boolean value) {
        boolOptions[opt]=value;
    }

    public static byte getByteOpt(byte opt) {
        return byteOptions[opt];
    }

    public static void setByteOpt(byte opt, byte value) {
        byteOptions[opt]=value;
    }

    public static String getStringOpt(byte opt) {
        return stringOptions[opt];
    }

    public static void setStringOpt(byte opt, String value) {
        stringOptions[opt]=value;
    }

    public static double getDoubleOpt(byte opt) {
        return doubleOptions[opt];
    }

    public static void setDoubleOpt(byte opt, double value) {
        doubleOptions[opt]=value;
    }

    public static void addOMap(String name, String url) {
        String[] ns=new String[RMSOption.OMAPS_NAMES.length+1];

        System.arraycopy(RMSOption.OMAPS_NAMES, 0, ns, 0, RMSOption.OMAPS_NAMES.length);
        ns[ns.length-1]=name;
        RMSOption.OMAPS_NAMES=ns;

        ns=new String[RMSOption.OMAPS_URLS.length+1];
        System.arraycopy(RMSOption.OMAPS_URLS, 0, ns, 0, RMSOption.OMAPS_URLS.length);
        ns[ns.length-1]=url;
        RMSOption.OMAPS_URLS=ns;
    }

    public void writeData(OutputStream os, Item[] items) throws IOException {
        DataOutputStream dos=new DataOutputStream(os);
        try {
            save2Stream(dos);
            MapCanvas.map.rmss.writeSettingNow();
        } finally {
            //progressResponse=null;
            dos.close();
        }
    }

    public void readData(InputStream is, Item[] items) throws IOException {
        DataInputStream dis=new DataInputStream(is);
        try {
            loadFromStream(dis);
        } finally {
            //progressResponse=null;
            dis.close();
        }
    }
    //ProgressResponse progressResponse;

    public void setProgressResponse(ProgressResponse progressResponse) {
        //this.progressResponse=progressResponse;
    }

    public boolean stopIt() {
        //this.progressResponse=null;
                return true;

    }

    public static void setMapVS(boolean[] a) {
        RMSOption.mapVS=a;
        RMSOption.changed=true;
    }
}
