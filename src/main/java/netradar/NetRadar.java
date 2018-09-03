/*
 * NetRadar.java
 *
 * Created on 1 ���� 2007 �., 13:09
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package netradar;

import RPMap.MapCanvas;
import RPMap.MapUtil;
import RPMap.RMSOption;
import camera.MD5;
import gpspack.GPSReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;
import misc.*;

/**
 *
 * @author Raev
 */
//public class NetRadar implements Runnable,GPSLocationListener {
public class NetRadar implements Runnable {

    public static NetRadar netRadar;
    public static NetRadarUserList netRadarUserList;
    Thread thread;
    private boolean stopped;
    Hashtable users=new Hashtable();
    MVector users_v=new MVector();
    //public Enumeration getUsersEa(){return users.elements();};

    public MVector getUsersV() {
        return users_v;
    }

    ;

    public NetRadarUser getUser(Object key) {
        return (NetRadarUser) users.get(key);
    }
    private Hashtable msgs=new Hashtable();

    public Enumeration getMsgs() {
        return msgs.elements();
    }
    //private Queue pts=new Queue(20);
    final public static int OKSTATUS=1;
    final public static int AUTHERRORSTATUS=2;
    final public static int STARTSTATUS=3;
    final public static int CODERRORSTATUS=4;
    public int serviceStatus=STARTSTATUS;
    public String serviceDesc="Connecting...";
    private String connectError="Connect error";
    public boolean bind;
    public static int bytesDown;
    private byte[] buffer;
    private int bufPos;
    public static boolean oneTimeCall;
    private ConnHolder cH;
    /** Creates a new instance of NetRadar */
    private static byte[] bb={104, 116, 116, 112, 58, 47, 47, 110, 101, 116, 114, 97, 100, 97, 114, 46, 114, 117, 47};
    //private static byte[] bb={104, 116, 116, 112, 58, 47, 47, 110, 101, 116, 114, 97, 100, 97, 114, 46, 114, 117, 47};
    //#if SE_K750_E_BASEDEV
//#     public static String netradarSiteURL="http://netradar.ru/";
//#     //public static String netradarSiteURL="http://localhost/";
    //#else
  public static String netradarSiteURL=Util.byteArrayToString(bb, true);
  //public static String netradarSiteURL="http://netradar.ru/";
    //#endif
    NetRadarIT nrIT;
    public NetRadarWT nrWT;
    //static int userId=-1;
    //static String userName="UNDEF";

    public NetRadar() {
        readMessagesList();

        buffer=new byte[500];
        thread=new Thread(this);
        thread.start();

        NetRadar.netRadar=this;

        try {
            nrWT=new NetRadarWT();
        } catch (Throwable t) {
        }

        if (RMSOption.getBoolOpt(RMSOption.BL_REALTIMENR)){
            nrIT=new NetRadarIT();
        } 
        if (RMSOption.holdInetConn || (RMSOption.getBoolOpt(RMSOption.BL_REALTIMENR) && RMSOption.getBoolOpt(RMSOption.BL_REALTIMENR_UDP))){
            cH=new ConnHolder();
            cH.start();
        }

        //  if (!MapCanvas.map.gpsLocListeners.contains(this))
        //    MapCanvas.map.gpsLocListeners.addElement(this);

        try {
            imgRead=Image.createImage("/img/read.png");
            imgUnread=Image.createImage("/img/unread.png");
        } catch (IOException _ex) {
        }
    }
    public Image imgUnread;
    public Image imgRead;
    public Image imgStatus;

    public void stop() {
        try {
            stopped=true;
//    NetRadar.userId=-1;
            NetRadarIT nrITloc = nrIT;
            if (nrITloc!=null){
                nrITloc.stop();
            }
            if (cH!=null){
                cH.stopped=true;
            }
            cH=null;
            thread=null;
            writeMessageList();
        } catch (Throwable t) {
        }
    }

    void updateStatusImg() {
        Enumeration me=msgs.elements();
        NetRadarMessage nrm;
        boolean allRead=true;
        while (me.hasMoreElements()) {
            nrm=(NetRadarMessage) me.nextElement();
            if (nrm.rf==1){
                allRead=false;
                break;
            }
        }
        if (allRead){
            imgStatus=null;
        } else {
            imgStatus=imgUnread;
        }
    }
    private String NR="NR";
    private String NW="NW";
    private String NL="NL";
    private String NI="NI";
    private boolean secondread;
    String nrSocketServerURL;
    String nrDatagramServerURL;
    long nrUserId ;

    private void parseInfo(String ns) {
        // $NR,MagDel,59.7,29.2,45.3,43.7,113.5,64534346
        //    from, maxid,total,unread
        // $NL,2,1,1,1
        if (ns.length()>10){
            String s=null;
            s=ns;
            //if (ns.charAt(ns.length())=='\n') s=ns.substring(0,ns.length()-4);

            String DATA_STRING=s.toString().substring(3, s.length());
            String DATA_TYPE=s.toString().substring(1, 3);
            String[] data;
            // Check the gps data type and convert the information to a more readable format.
            if (DATA_TYPE.compareTo(NI)==0){
                try {
                    data=MapUtil.parseString(DATA_STRING, ',');
                    //int userId=Long.parseLong(data[7]);

                    nrSocketServerURL=data[0];//"socket://81.3.138.244:"+port
                    nrUserId=Long.parseLong( data[1]);
                    nrDatagramServerURL=data[2];//"datagram://87.242.113.123:5000"
                    serviceStatus=OKSTATUS;
                    serviceDesc=MapUtil.S_OK;
                } catch (Throwable t) {
                    //#mdebug
//#                     if (RMSOption.debugEnabled){
//#                         DebugLog.add2Log(NI+" p:"+t.toString());
//#                     }
                    //#enddebug
                }
            } else if (DATA_TYPE.compareTo(NR)==0){
                try {
                    data=MapUtil.parseString(DATA_STRING, ',');
                    //int userId=Long.parseLong(data[7]);

                    NetRadarUser nru=(NetRadarUser) users.get(data[7]+data[9]);
                    boolean nu=false;
                    boolean newdt=true;
                    long dt=Long.parseLong(data[6]);
                    if (nru!=null){
                        newdt=dt>nru.dt;
                    }
                    if (newdt){
                        if (nru==null){
                            nu=true;
                            nru=new NetRadarUser();
                        }
                        nru.userName=data[0];
                        nru.lat=Double.parseDouble(data[1]);
                        nru.lon=Double.parseDouble(data[2]);
                        nru.alt=(int) Double.parseDouble(data[3]);
                        nru.speed=(float) Double.parseDouble(data[4]);
                        nru.crs=(int) Double.parseDouble(data[5]);
                        nru.dt=dt;
                        nru.userId=Integer.parseInt(data[7]);
                        // if (data.length>8)
                        //   if (!data[8].equals(MapUtil.emptyString)){

                        //if ((nru.status==null)||(!data[8].equals(nru.status))) {
                        nru.status=data[8]+' '+MapUtil.dateTime2Str(dt, false);
                        //}
                        nru.status_v=null;
                        // }
                        //byte ut=
                        //  }
                        nru.ut=Byte.parseByte(data[9]);


                        if (nu){
                            users.put(data[7]+data[9], nru);
                            users_v.addElement(nru);
                        }
                        //track append
                        if (secondread){
                            nru.tracklat[nru.trackpos]=nru.lat;
                            nru.tracklon[nru.trackpos]=nru.lon;
                            nru.nextpos();
                        }
                        secondread=true;
                    }
                    serviceStatus=OKSTATUS;
                    serviceDesc=MapUtil.S_OK;
                } catch (Throwable t) {
                    //#mdebug
//#                     if (RMSOption.debugEnabled){
//#                         DebugLog.add2Log(NR+" p:"+t.toString());
//#                     }
                    //#enddebug
                }
            } else if (DATA_TYPE.compareTo(NL)==0){
                try {
                    data=MapUtil.parseString(DATA_STRING, ',');

                    NetRadarMessage nrm=(NetRadarMessage) msgs.get(data[1]);
                    if (nrm!=null){
                        return;
                    }
                    nrm=new NetRadarMessage();
                    nrm.senderId=Integer.parseInt(data[0]);
                    nrm.msgId=Long.parseLong(data[1]);
                    nrm.rf=1;
                    nrm.subject=(data[2]);
                    NetRadarUser nru1=(NetRadarUser) users.get(data[0]+'1');
                    if (nru1==null){
                        nrm.userName=MapUtil.emptyString;
                    } else {
                        nrm.userName=nru1.userName;
                    }
                    msgs.put(data[1], nrm);
                    hasNewMessages=(hasNewMessages||(nrm.rf==1));
                    serviceStatus=OKSTATUS;
                    serviceDesc=MapUtil.S_OK;

                } catch (Throwable t) {
                    //#mdebug
//#                     if (RMSOption.debugEnabled){
//#                         DebugLog.add2Log(NR+" p:"+t.toString());
//#                     }
                    //#enddebug
                }
            } else if (DATA_TYPE.compareTo(NW)==0){
                try {
                    data=MapUtil.parseString(DATA_STRING, ',');
                    serviceStatus=AUTHERRORSTATUS;
                    serviceDesc=data[0];
                } catch (Throwable t) {
                }
            }
        }
    }
    private boolean hasNewMessages;
    private String logiMD5;

    private String getURLParams() {
        String s;
        long dt=0;
        double lat=0;
        double lon=0;
        int alt=0;
        int crs=0;
        double spd=0;
        if ((GPSReader.NUM_SATELITES>0)||oneTimeCall){
            if ((GPSReader.NUM_SATELITES>0)){
                dt=GPSReader.SAT_TIME_MILLIS;
                if (dt==0){
                    dt=System.currentTimeMillis();
                }
                lat=GPSReader.LATITUDE;
                lon=GPSReader.LONGITUDE;
                alt=GPSReader.ALTITUDE;
                crs=GPSReader.COURSE_I;
                spd=MapUtil.speedRound1(GPSReader.SPEED_KMH);

//        s=s+"&lat="+MapUtil.coordRound5(GPSReader.LATITUDE)+
//          "&lon="+MapUtil.coordRound5(GPSReader.LONGITUDE)+
//          "&alt="+GPSReader.ALTITUDE+
//          "&crs="+GPSReader.COURSE_I+
//          "&spd="+MapUtil.speedRound1(GPSReader.SPEED_KMH)+
//          "&dt="+String.valueOf(System.currentTimeMillis());
            } else {
                lat=MapUtil.coordRound5(MapCanvas.reallat);
                lon=MapUtil.coordRound5(MapCanvas.reallon);
                dt=System.currentTimeMillis();
//        s=s+"&lat="+MapUtil.coordRound5(MapCanvas.reallat)+
//          "&lon="+MapUtil.coordRound5(MapCanvas.reallon)+
//          "&alt=0&crs=0&spd=0&dt="+String.valueOf(System.currentTimeMillis());
            }
        }
//    else {
////      s=s+"&dt=0";
//    }
        String sSigned=RMSOption.netRadarLogin+dt+MD5.getHashString(RMSOption.netRadarPass);
        String sign=MD5.getHashString(sSigned);
        //?loginame=celari&logipass=pavel&lat=60&lon=30&alt=0&crs=180&spd=10&dt=500
        s="?lg="+HTTPUtils.urlEncodeString(RMSOption.netRadarLogin)
          +"&sign="+sign
          +"&lat="+lat
          +"&lon="+lon
          +"&alt="+alt
          +"&crs="+crs
          +"&spd="+spd
          +"&dt="+dt
          +"&v=1";
        return s;
    }

    private void contactServer() {
        if (stopped){
            return;
        }
        hasNewMessages=false;
        InputStream is=null;
        HttpConnection c;
        NetRadarUser nru_nav=getNav2User();
        boolean contacted=false;
        byte trc=0;
        do {
            try {
                String fn=netradarSiteURL+"nrs/ms_nr_uv.php"+getURLParams();//"cgi-bin/uv.pl"
                //http://localhost/nrs/ms_nr_uv.php?lg=magdel&sign=3023888b978e15bbd804f69b32504303&lat=0.0&lon=0.0&alt=0&crs=0&spd=0&dt=0&v=1
                //http://localhost/nrs/ms_nr_uv.php?lg=magdel&sign=e8440bf2252ba05ffd4f551f6633a0ce&lat=60.00811004638672&lon=30.339847564697266&alt=32&crs=233&spd=233&dt=1251745207000&v=1
                try {
                    c=HTTPUtils.getHttpConn(fn);
                } catch (IOException e) {
                    serviceStatus=CODERRORSTATUS;
                    serviceDesc=connectError;
                    trc++;
                    continue;
                }
                is=c.openInputStream();
                bytesDown+=917;
                MapCanvas.loadedBytes+=917;
                bufPos=0;
                try {
                    int rb;
                    do {
                        rb=is.read();
                        if (rb<0){
                            break;
                        }
                        bytesDown++;
                        MapCanvas.loadedBytes++;

                        if (rb==10){
                            if (bufPos>10) //              if(sb.charAt(0) == '$')
                            {
                                if (buffer[0]==36){
                                    parseInfo(Util.byteArrayToString(buffer, 0, bufPos, true));
                                    //parseInfo(MapUtil.fromBytesUTF8(buffer,bufPos));
                                    //parseInfo(new String(buffer,0,bufPos,"UTF-8"));
                                }
                            }
                            bufPos=0;
                        } else {
                            buffer[bufPos]=(byte) rb;
                            bufPos++;
                        }

                    } while ((rb>-1)&&(!stopped));
                    MapCanvas.inetAvailable=true;

                } finally {
                    try {
                        c.close();
                    } catch (Throwable t) {
//#mdebug
//#                         if (errCnt<7){
//#                             if (RMSOption.debugEnabled){
//#                                 DebugLog.add2Log("NR U:"+t.toString());
//#                                 errCnt++;
//#                             }
//# 
//#                         }
                        //#enddebug
                    }
                    try {
                        if (is!=null){
                            is.close();
                        }
                    } catch (Throwable t) {
                    }
                    //try{if (os != null) os.close();}catch(Throwable t){}
                }
                contacted=true;
            } catch (Throwable e) {
//#mdebug
//#                 if (RMSOption.debugEnabled){
//#                     DebugLog.add2Log("NR:"+e.toString());
//#                 }
                //#enddebug
                serviceStatus=CODERRORSTATUS;
                serviceDesc=e.toString();
            }

            trc++;
        } while ((!contacted)&&(trc<=3));

        if (nru_nav!=null){
            nru_nav.updateMapPoint();
        }
        updateStatusImg();
        if (hasNewMessages){
            MapSound.playSound(MapSound.NEWMESSAGESOUND);//MapMidlet.playNewMesSound();
//    {
//      NetRadarUser nru;
//      for (int i=users.size()-1;i>=0;i--) {
//        nru = (NetRadarUser) users.elementAt(i);
//        if (nru.userId==nru_nav.userId) {
//          nru.nav2=nru_nav.nav2;
//          nru.updateMapPoint();
//        }
//      }
//    }
        }
        MapCanvas.map.repaint();
//    userId = 2;
    }  //#debug
    int errCnt;
//  private void contactBanner() {
//    bannerDown=true;
//    InputStream is = null;
//    //OutputStream os = null;
//    HttpConnection c;
//    try {
//      //String fn="http://www.gps-club.ru/top/button.php?u=MagDel";
//      String fn="http://top.gps-club.ru/button.php?u=MagDel";
//
//      c = (HttpConnection)Connector.open(fn);
//      c.setRequestMethod(HttpConnection.GET);
//      //c.setRequestProperty("Content-Type", "text/plain");
//      //c.setRequestProperty("Accept", "text/plain");
//
//      //os = c.openOutputStream();
//      //os.flush();                // Optional, openInputStream will flush
//      is = c.openInputStream();
//      int bD=0;
//      try {
//        int rb;
//        do {
//          rb=is.read();
//          bD++;
//          if (bD>10000) break;
//        } while ((rb >-1)&&(!stopped));
//      } finally {
//        try{ c.close();}catch(Throwable t){}
//        try{ if (is != null) is.close();}catch(Throwable t){}
//        // try{if (os != null) os.close();}catch(Throwable t){}
//      }
//
//    } catch (Throwable e) {
////#debug
//      if (RMSOption.debugEnabled) DebugLog.add2Log("NRb:"+e.toString());
//    };
//  }
//  private boolean bannerDown=false;

    public void run() {
        try {

            Thread.yield();

            try {
                long tm;
                while (!stopped) {
                    contactServer();

                    NetRadar.oneTimeCall=false;
                    //if (oneTimeCall) break;
//          if (!bannerDown)
//            try{contactBanner();}catch(Throwable t){}
                    tm=System.currentTimeMillis()+((RMSOption.getByteOpt(RMSOption.BO_NRPERIOD)==0)?56753:597986);
                    try {
                        while ((tm>System.currentTimeMillis())&&(!NetRadar.oneTimeCall)&&(!stopped)) {
                            Thread.sleep(500);
                        }
                    } catch (Throwable t) {
                    }
                    if (nrIT!=null){
                        if (nrSocketServerURL!=null){
                            try {
                                while (!stopped) {
                                    Thread.sleep(500);
                                    if (nrIT.lastReadTime+19000<System.currentTimeMillis()){
                                        nrIT.stop();
                                        Thread.sleep(1000);
                                        nrIT=new NetRadarIT();
                                    }
                                }
                            } catch (Throwable t) {
//#mdebug
//#                                 if (RMSOption.debugEnabled){
//#                                     DebugLog.add2Log("NR IT ch:"+t.toString());
//#                                 }
                                //#enddebug
                            }
                        }
                    }
                }
            } finally {
                netRadarUserList=null;
//////        netRadarTrack=null;
                // try {
                //MapCanvas.map.gpsLocListeners.removeElement(this);
                MapCanvas.map.repaint();
                // } finally {
                stop();
                //if (NetRadar.oneTimeCall)
                // NetRadar.netRadar = null;

                // }
            }
        } catch (Throwable t) {
        }
        nrIT=null;
    }

    public NetRadarUser getNav2User() {
        if (users==null){
            return null;
        }
        NetRadarUser nru;
        for (int i=0; i<users_v.size(); i++) {
            nru=(NetRadarUser) users_v.elementAt(i);
            if (nru.nav2!=null){
                return nru;
            }
        }
//    Enumeration ue = users.elements();
//    while (ue.hasMoreElements()){
//      nru = (NetRadarUser) ue.nextElement();
//      if (nru.nav2!=null) return nru;
//    }
        return null;
    }
//  private long lastPtsAdded;
////#debug
//  byte errC;
//  public void gpsLocationAction(double lat, double lon, int alt, long time,float spd, float crs) {
//    return;
//////#debug
////    try{
////      if (lastPtsAdded<System.currentTimeMillis()){
////        synchronized(pts){
////          if (pts.size()==0) {
////            MapPoint pt = new MapPoint(lat,lon,alt,time);
////            pt.speed=spd;
////            pt.dist=(int)crs;
////            pts.add(pt);
////          } else {
////            MapPoint p = (MapPoint)pts.lastElement();
////            if (MapRoute.distBetweenCoords(p.lat,p.lon,lat,lon)>0.05d) {
////              MapPoint pt = new MapPoint(lat,lon,alt,time);
////              pt.speed=spd;
////              pt.dist=(int)crs;
////              pts.add(pt);
////            }
////          }
////        }
////        lastPtsAdded=(nrt.size()<25)? System.currentTimeMillis()+11000:System.currentTimeMillis()+18000;
////      }
//////#mdebug
////    }catch(Throwable t){
////      if (RMSOption.debugEnabled)
////        if (errC<10){DebugLog.add2Log("NRgLA:"+t.toString());
////        errC++;
////        }
////    }
//////#enddebug
//  }
//  

    public void showUserList() {

        if (netRadarUserList==null){
            netRadarUserList=new NetRadarUserList();
        }
        MVector userlist=users_v;
        //Enumeration ue = users.elements();
        //while(ue.hasMoreElements())userlist.addElement(ue.nextElement());

        NetRadarUser nru;
        for (int i=0; i<userlist.size(); i++) {
            for (int j=i+1; j<userlist.size(); j++) {
                if (((NetRadarUser) userlist.elementAt(i)).userName.toLowerCase().compareTo(((NetRadarUser) userlist.elementAt(j)).userName.toLowerCase())>0){
                    nru=(NetRadarUser) userlist.elementAt(i);
                    userlist.setElementAt(userlist.elementAt(j), i);
                    userlist.setElementAt(nru, j);
                }
            }
        }
        netRadarUserList.fillUserList(userlist);
        if (NetRadar.netRadar.bind){
            netRadarUserList.addCommand(netRadarUserList.unbindCommand);
        }

        MapCanvas.setCurrent(netRadarUserList);
    }

    public void writeMessageList() {
        try {
            RecordStore recordSetStore=RecordStore.openRecordStore("nrm05", true);

            try {
                ByteArrayOutputStream baos=new ByteArrayOutputStream(2000);
                DataOutputStream outputStream=new DataOutputStream(baos);
                try {
                    int tc=msgs.size();
                    outputStream.writeInt(tc);
                    Vector vmsgs=new Vector(30);
                    Enumeration um=msgs.elements();
                    NetRadarMessage nrm;
                    while (um.hasMoreElements()) {
                        nrm=(NetRadarMessage) um.nextElement();
                        vmsgs.addElement(nrm);
                    }

                    for (int i=0; i<vmsgs.size(); i++) {
                        for (int j=i+1; j<vmsgs.size(); j++) {
                            if (((NetRadarMessage) vmsgs.elementAt(i)).msgId<((NetRadarMessage) vmsgs.elementAt(j)).msgId){
                                nrm=(NetRadarMessage) vmsgs.elementAt(i);
                                vmsgs.setElementAt(vmsgs.elementAt(j), i);
                                vmsgs.setElementAt(nrm, j);
                            }
                        }
                    }
                    for (int i=0; (i<vmsgs.size())&&(i<30); i++) {
                        nrm=(NetRadarMessage) vmsgs.elementAt(i);
                        nrm.save2Stream(outputStream);
                    }

                } catch (IOException ioe) {
                }
                byte[] b=baos.toByteArray();
                baos=null;
                if (recordSetStore.getNumRecords()==1){
                    recordSetStore.setRecord(1, b, 0, b.length);
                } else {
                    recordSetStore.addRecord(b, 0, b.length);
                }
                RMSOption.changed=true;
            } finally {
                recordSetStore.closeRecordStore();
            }
        } catch (Exception e) {
        }
    }

    public void readMessagesList() {
        try {
            RecordStore recordSetStore=RecordStore.openRecordStore("nrm05", true);
            try {

                try {
                    int rc=recordSetStore.getNumRecords();

                    if (rc>0){
                        DataInputStream is=Util.getDataInputStream(recordSetStore.getRecord(1));
                        try {
                            int n=is.readInt();
                            NetRadarMessage rt;
                            for (int i=n; i>0; i--) {
                                rt=new NetRadarMessage(is);
                                //if (!rmsTiles.contains(rt))
                                msgs.put(String.valueOf(rt.msgId), rt);
                            }
                        } finally {
                            is.close();
                        }
                    }
                } catch (IOException ioe) {
//#mdebug
//#                     if (RMSOption.debugEnabled){
//#                         DebugLog.add2Log("RMSNM io:"+ioe.toString());
//# 
//#                     }
                    //#enddebug
                }

            } finally {
                recordSetStore.closeRecordStore();
            }
        } catch (Exception e) {
        }
    }
}
