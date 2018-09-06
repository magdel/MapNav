/*
 * OuterMapLoader.java
 *
 * Created on 24 ��� 2007 �., 1:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package RPMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Image;
//#debug
import misc.DebugLog;

/**
 *
 * @author RFK
 */
public class OuterMapLoader implements Runnable {

    private String furl;
    private Connection conn;
    private boolean consumeMemory=RMSOption.getBoolOpt(RMSOption.BL_CACHEINDEX);
    private boolean noRelocate;
    public boolean ready;

    /** Creates a new instance of OuterMapLoader */
    public OuterMapLoader(String url, boolean noRelocate) {
        furl=url;
        this.noRelocate=noRelocate;
        if (consumeMemory||furl.equals(MapUtil.MAP_MNO)){
            rmsH=new Hashtable(2000);
        }
        Thread t=new Thread(this);
        //t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }
    private boolean stopped;

    public void closeConn() {
        ready=false;
        stopped=true;
        try {
            if (conn!=null){
                conn.close();
            }
        } catch (Throwable t) {
        }
    }

    public void run() {
        try {
            initialize();
        } finally {
            ready=true;
            secondrun=true;
        }
    }
    private static boolean secondrun;
    private double lat, lon;
    private int level;
    public int tot;
    private Hashtable rmsH;
    long skip;

    private void initialize() {
        boolean overSize=false;
        long fSize=0;
        InputStream is=null;
        // String err = MapUtil.emptyString; // used for debugging
        //#debug
        double tracePos=1.01;
        try {

            try {
                try {
                    if (furl.equals(MapUtil.MAP_MNO)){
                        //#debug
                        tracePos=1.01;
                        is=this.getClass().getResourceAsStream(MapUtil.MAP_MNO);
                    } else {
                        //#debug
                        tracePos=1.02;
                        conn=Connector.open("file:///"+furl, Connector.READ);

                        //#debug
                        tracePos=1.1;
                        is=((FileConnection) conn).openInputStream();
                    }

                    //#debug
                    tracePos=1.2;
                    int n, n1, n2, n3, n4;

                    n1=is.read();
                    // skip++;
                    n2=is.read();
                    //  skip++;
                    lat=n1+n2/60.;
                    n1=is.read();
                    //  skip++;
                    n2=is.read();
                    //   skip++;
                    lon=n1+n2/60.;
                    n2=is.read();
                    //  skip++;
                    level=n2;

                    //#debug
                    tracePos=1.3;

                    n1=is.read();
                    // skip++;
                    n2=is.read();
                    // skip++;
                    n3=is.read();
                    //  skip++;
                    n4=is.read();
                    //  skip++;
                    n=(n1<<24)+(n2<<16)+(n3<<8)+n4;
                    tot=n;
                    //#debug
                    tracePos=1.4;
                    if (consumeMemory){
                        RMSOTile rt;
                        int NumX, NumY, Level, picSize;
                        byte MapType;


                        for (int i=0; i<n; i++) {
                            n1=is.read();
                            //   skip++;
                            n2=is.read();
                            //   skip++;
                            n3=is.read();
                            //   skip++;
                            n4=is.read();
                            //   skip++;
                            NumX=(n1<<24)+(n2<<16)+(n3<<8)+n4;
                            n1=is.read();
                            //   skip++;
                            n2=is.read();
                            //    skip++;
                            n3=is.read();
                            //    skip++;
                            n4=is.read();
                            //   skip++;
                            NumY=(n1<<24)+(n2<<16)+(n3<<8)+n4;
                            n1=is.read();
                            //   skip++;
                            n2=is.read();
                            //   skip++;
                            n3=is.read();
                            //   skip++;
                            n4=is.read();
                            //   skip++;
                            Level=(n1<<24)+(n2<<16)+(n3<<8)+n4;
                            MapType=(byte) is.read();
                            //   skip++;

                            //mt= new NetworkMapTile(NumX,NumY,Level,MapType);
                            //rt = new RMSTile(Level,NumX,NumY,0,(byte)(MapTile.SHOW_SUR+MapTile.SHOW_MP));
                            rt=new RMSOTile();
                            rt.level=Level;
                            rt.numX=NumX;
                            rt.numY=NumY;

                            n1=is.read();
                            //  skip++;
                            n2=is.read();
                            //  skip++;
                            n3=is.read();
                            //  skip++;
                            n4=is.read();
                            //  skip++;
                            picSize=(n1<<24)+(n2<<16)+(n3<<8)+n4;
                            rt.picSize=picSize;
                            n1=is.read();
                            //  skip++;
                            n2=is.read();
                            //skip++;
                            n3=is.read();
                            // skip++;
                            n4=is.read();
                            //  skip++;
                            picSize=(n1<<24)+(n2<<16)+(n3<<8)+n4;
                            rt.tileOffs=picSize;
                            //  rmsT.addElement(rt);
                            rmsH.put(rt, rt);

                            /*
                            bt= new byte[picSize];
                            //int p=0;
                            n1=0;
                            while (n1!=bt.length)
                            n1=n1+is.read(bt,n1,bt.length-n1);
                            //for (int p=0;p<picSize;p++) bt[p]=(byte)is.read();
                            MapCanvas.map.rmss.savePic(mt,(byte)(MapTile.SHOW_MP+MapTile.SHOWMAP),bt);
                             */

                        }
                    }

                    skip=tot*21+9;
                    //#debug
                    tracePos=2.0;

                    if (!noRelocate&&secondrun){
                        MapCanvas.map.setLocation(lat, lon, level);
                    }
                    //#mdebug
                    if (RMSOption.debugEnabled){
                        DebugLog.add2Log("OM load IS OK");
                    }
//#enddebug

                } finally {
                    is.close();
                    is=null;
                    //#debug
                    tracePos=3.0;

                }
            } catch (IOException i1oe) {
//#mdebug
                if (RMSOption.debugEnabled){
                    DebugLog.add2Log("OM load IO:"+i1oe.toString());
                }
//#enddebug
            }

        } catch (Throwable e) {
//#mdebug
            if (RMSOption.debugEnabled){
                DebugLog.add2Log("M i2:"+tracePos+":"+furl+":"+e.toString());
            }
//#enddebug
        }
    }
    RMSOTile searchRMSOTile=new RMSOTile();

    private RMSOTile getFromList(MapTile mt) {
        //RMSTile rt=null;
        searchRMSOTile.numX=mt.numX;
        searchRMSOTile.numY=mt.numY;
        searchRMSOTile.level=mt.level;
        return (RMSOTile) rmsH.get(searchRMSOTile);
    }
    private byte[] bt=new byte[20000];
    private String s_reading="Reading";

    public boolean loadTile(MapTile mt) {
//#mdebug
        if (RMSOption.debugEnabled){
            DebugLog.add2Log("r:"+ready+" s:"+stopped);
        }
//#enddebug

        long stopLoad=System.currentTimeMillis()+10000;
        try {
            while ((!ready)&&(!stopped)&&(stopLoad<System.currentTimeMillis())) {
                Thread.sleep(30);
            }
            if ((!ready)&&(!stopped)){
                //#mdebug
                if (RMSOption.debugEnabled){
                    DebugLog.add2Log("!!!r:"+ready+" s:"+stopped);
                }
//#enddebug
                mt.paintState(null);
                return false;
            }

        } catch (Throwable t) {
        }
        if (stopped){
            mt.paintState(null);
            return false;        //#debug
        }

        byte dp=0;
        long nS=0;
        RMSOTile rt=null;
        if (consumeMemory){
            rt=getFromList(mt);
            if (rt==null){
                mt.paintState(null);
                return false;
            }

        }
        synchronized (this) {
            mt.paintState(s_reading);
            if (!consumeMemory){
                rt=getFromFile(mt);
                if (rt==null){
                    mt.paintState(null);
                    return false;
                }

            }
            int pS=0;
            try {
                //#debug
                dp=1;
                InputStream is;
                if (furl.equals(MapUtil.MAP_MNO)){
                    is=this.getClass().getResourceAsStream(MapUtil.MAP_MNO);
                } else {
                    is=((FileConnection) conn).openInputStream();
                }

                try {
                    //#debug
                    dp=2;
                    long needSkip=skip+rt.tileOffs;
                    nS=needSkip;
                    int tries=100;
                    long skipped;
                    while (needSkip>0) {
                        skipped=is.skip(needSkip);
                        if (skipped>0){
                            needSkip-=skipped;
                        }
                        tries--;
                        if (tries<=0){
                            throw new Exception("Underskip!");
                        }
                    }
                    //#debug
                    dp=3;
                    pS=rt.picSize;
                    System.gc();
                    if (rt.picSize>bt.length){
                        bt=null;
                        bt=new byte[rt.picSize];
                    }
                    int n1=0;
                    //#debug
                    dp=4;
                    while (n1!=pS) {
                        n1=n1+is.read(bt, n1, pS-n1);
                    }
                    //#debug
                    dp=5;
                    System.gc();
                    mt.img=Image.createImage(bt, 0, pS);
                    //#debug
                    dp=6;
                } finally {
                    is.close();
                }
            } catch (Throwable t) {
//#mdebug
                if (RMSOption.debugEnabled){
                    DebugLog.add2Log("Mout RI:"+dp+":"+nS+":"+pS+":"+t.toString());
                }
//#enddebug
            }
            mt.paintState(null);
        }
        return true;
    }
    private byte[] na=new byte[13];
    private int[] nai=new int[13];

    private RMSOTile getFromFile( MapTile mt) {
        InputStream is;
        RMSOTile rt=null;
        int NumX, NumY, Level, picSize=0;
        try {
            if (furl.equals(MapUtil.MAP_MNO)){
                is=this.getClass().getResourceAsStream(MapUtil.MAP_MNO);
            } else {
                is=((FileConnection) conn).openInputStream();
            }

            try {

                int n1, n2, n3, n4;
                is.skip(9);
                byte MapType;
                for (int i=0; i<tot; i++) {

                    n1=0;
                    while (n1!=na.length) {
                        n1=n1+is.read(na, n1, na.length-n1);
                    }

                    for (int ii=11; ii>=0; ii--) {
                        if (na[ii]<0){
                            nai[ii]=na[ii]+256;
                        } else {
                            nai[ii]=na[ii];
                        }
                    }
                    NumX=(int) (nai[0]<<24)+(int) (nai[1]<<16)+(int) (nai[2]<<8)+(int) nai[3];
                    NumY=(int) (nai[4]<<24)+(int) (nai[5]<<16)+(int) (nai[6]<<8)+(int) nai[7];
                    Level=(int) (nai[8]<<24)+(int) (nai[9]<<16)+(int) (nai[10]<<8)+(int) nai[11];
                    MapType=na[12];

                    //optimize to use one read buffer!!!!!!!1

                    //mt= new NetworkMapTile(NumX,NumY,Level,MapType);
                    if ((mt.numY==NumY)&&(mt.numX==NumX)&&(mt.level==Level)){ //rt = new RMSOTile(Level,NumX,NumY);
                        rt=new RMSOTile();
                        rt.level=Level;
                        rt.numX=NumX;
                        rt.numY=NumY;
                    }

                    if (rt!=null){
                        n1=is.read();
                        //     skip++;
                        n2=is.read();
                        //     skip++;
                        n3=is.read();
                        //     skip++;
                        n4=is.read();
                        //     skip++;
                        picSize=
                          (n1<<24)+(n2<<16)+(n3<<8)+n4;
                        // if (rt!=null)
                        rt.picSize=picSize;
                        n1=is.read();
                        //       skip++;
                        n2=is.read();
                        //      skip++;
                        n3=is.read();
                        //     skip++;
                        n4=is.read();
                        //    skip++;
                        picSize=(n1<<24)+(n2<<16)+(n3<<8)+n4;
                        //  if (rt!=null)
                        rt.tileOffs=picSize;
                        //  break;
                    } else {
                        is.skip(8);
                    }

                    if (rt!=null){
                        break;
                    }
                }

            } finally {
                is.close();
                is=null;
            }

        } catch (Throwable t) {
//#mdebug
            if (RMSOption.debugEnabled){
                DebugLog.add2Log("Mout FM:"+picSize+":"+t.toString());
            }
//#enddebug
        }
        return rt;
    }
    static String lastPath="";

    public static void tryMkDir(String path) {
        try {
            StreamConnection conn=(StreamConnection) Connector.open("file:///"+path, Connector.WRITE);
            try {
                ((FileConnection) conn).mkdir();
            } finally {
                conn.close();
            }

        } catch (Throwable t) {
        }
    }

    public static void savePic(MapTile mt, byte[] bi, int bi_length) {
        if (RMSOption.getByteOpt(RMSOption.BO_CACHE_FORMAT_TYPE)==RMSOption.CACHE_FORMAT_MV){
            savePicMV(mt, bi, bi_length);
        } else {
            savePicGMT(mt, bi, bi_length);
        }
    }

    public static void savePicMV(MapTile mt, byte[] bi, int bi_length) {
        String path=RMSOption.getStringOpt(RMSOption.SO_EXTCACHEPATH)
          +mt.getFileServer()+mt.getFilePath()+mt.getFileLevel();
        //String path = "root1/";
        StreamConnection conn;
//WORKPATH
        if (!lastPath.equals(path)){
            path=RMSOption.getStringOpt(RMSOption.SO_EXTCACHEPATH);
            tryMkDir(path);
            //VE
            path+=mt.getFileServer();
            tryMkDir(path);

            //SUR MAP
            path+=mt.getFilePath();
            tryMkDir(path);

            //ZOOM
            path+=mt.getFileLevel();
            tryMkDir(path);

            lastPath=path;
        }
        //filename
        try {
            path+=mt.getFileName();
            conn=(StreamConnection) Connector.open("file:///"+path, Connector.READ_WRITE);
            try {
                ((FileConnection) conn).create();
                //InputStream is = conn.openInputStream();
                OutputStream os=conn.openOutputStream();
                os.write(bi, 0, bi_length);
                os.close();
                //is.close();
            } finally {
                conn.close();
            }
        } catch (Throwable t) {
            //#mdebug
            if (RMSOption.debugEnabled){
                DebugLog.add2Log("MLC0 FS:"+path+":"+t.toString());
            }
//#enddebug    
        }

    }

    public static void savePicGMT(MapTile mt, byte[] bi, int bi_length) {
        String path=RMSOption.getStringOpt(RMSOption.SO_EXTCACHEPATH)
          +'T'+mt.getFileServer()+mt.getFilePath()+'Z'+(mt.level-1)
          +'/'+mt.numY+'/'+mt.numX+".pic";
        //String path = "root1/";
        StreamConnection conn;
//WORKPATH
        if (!lastPath.equals(path)){
            path=RMSOption.getStringOpt(RMSOption.SO_EXTCACHEPATH);
            tryMkDir(path);
            //T
            path+='T';
            //VE
            path+=mt.getFileServer();
            tryMkDir(path);

            //SUR MAP
            path+=mt.getFilePath();
            tryMkDir(path);

            //sb.append(level).append('/').
            path+='Z';
            path+=(mt.level-1);
            path+='/';
            tryMkDir(path);
            //append(NumY).append('/').
            path+=mt.numY;
            path+='/';
            tryMkDir(path);
            //append(NumX).append(".png");
            path+=mt.numX;
            path+=".pic";

            lastPath=path;
        }
        //filename
        try {
            conn=(StreamConnection) Connector.open("file:///"+path, Connector.READ_WRITE);
            try {
                ((FileConnection) conn).create();
                //InputStream is = conn.openInputStream();
                OutputStream os=conn.openOutputStream();
                os.write(bi, 0, bi_length);
                os.close();
                //is.close();
            } finally {
                conn.close();
            }
        } catch (Throwable t) {
            //#mdebug
            if (RMSOption.debugEnabled){
                DebugLog.add2Log("MLC1 FS:"+path+":"+t.toString());
            }
//#enddebug
        }

    }

    public static void deletePic(MapTile mt) {
        if (RMSOption.getByteOpt(RMSOption.BO_CACHE_FORMAT_TYPE)==RMSOption.CACHE_FORMAT_MV){
            deletePicMV(mt);
        } else {
            deletePicGMT(mt);
            deletePicMV(mt);
        }
    }

    public static void deletePicMV(MapTile mt) {
        String path=RMSOption.getStringOpt(RMSOption.SO_EXTCACHEPATH)
          +mt.getFileServer()+mt.getFilePath()+mt.getFileLevel()+mt.getFileName();
        try {
            StreamConnection conn=(StreamConnection) Connector.open("file:///"+path, Connector.READ_WRITE);
            try {
                ((FileConnection) conn).delete();
                //is.close();
            } finally {
                conn.close();
            }

        } catch (Throwable t) {
            //#mdebug
            if (RMSOption.debugEnabled){
                DebugLog.add2Log("MLC2 DL:"+path+":"+t.toString());
            }
//#enddebug    
        }
    }

    public static void deletePicGMT(MapTile mt) {
        String path=RMSOption.getStringOpt(RMSOption.SO_EXTCACHEPATH)
          +'T'+mt.getFileServer()+mt.getFilePath()+'Z'+(mt.level-1)
          +'/'+mt.numY+'/'+mt.numX+".pic";

        try {
            StreamConnection conn=(StreamConnection) Connector.open("file:///"+path, Connector.READ_WRITE);
            try {
                ((FileConnection) conn).delete();
                //is.close();
            } finally {
                conn.close();
            }

        } catch (Throwable t) {
            //#mdebug
            if (RMSOption.debugEnabled){
                DebugLog.add2Log("MLC3 DL:"+path+":"+t.toString());
            }
//#enddebug
        }
    }

    public static boolean readRMSPic(MapTile mt) {
        if (RMSOption.getByteOpt(RMSOption.BO_CACHE_FORMAT_TYPE)==RMSOption.CACHE_FORMAT_MV){
            return readRMSPicMV(mt);
        } else {
            if (!readRMSPicGMT(mt)){
                return readRMSPicMV(mt);
            } else {
                return true;
            }
        }
    }

    public static boolean readRMSPicMV(MapTile mt) {
        String path=RMSOption.getStringOpt(RMSOption.SO_EXTCACHEPATH)
          +mt.getFileServer()+mt.getFilePath()+mt.getFileLevel()+mt.getFileName();
        try {
            long tm=System.currentTimeMillis()+3000;
            StreamConnection conn=(StreamConnection) Connector.open("file:///"+path, Connector.READ);
            try {
                InputStream is=conn.openInputStream();

                //проверка чтоб не зациклилось при запросах на чтение
                if (System.currentTimeMillis()-tm>0){
                    MapCanvas.moveX=0;
                    MapCanvas.moveY=0;
                }
                try {
                    mt.img=null;
                    mt.img=Image.createImage(is);
                    //mt.img = Image.createImage(bi, 0, bi.length);
                    //}
                } finally {
                    is.close();
                }
//is.close();
            } finally {
                conn.close();
            }
            return true;
        } catch (Throwable t) {
            //#mdebug
            if (RMSOption.debugEnabled){
                DebugLog.add2Log("MLC4 FL:"+path+":"+t.toString());
            }
//#enddebug    
            return false;
        }
    }

    public static boolean readRMSPicGMT(MapTile mt) {
        String path=RMSOption.getStringOpt(RMSOption.SO_EXTCACHEPATH)
          +'T'+mt.getFileServer()+mt.getFilePath()+'Z'+(mt.level-1)
          +'/'+mt.numY+'/'+mt.numX+".pic";

        try {
            long tm=System.currentTimeMillis()+3000;
            StreamConnection conn=(StreamConnection) Connector.open("file:///"+path, Connector.READ);
            try {
                InputStream is=conn.openInputStream();

                //проверка чтоб не зациклилось при запросах на чтение
                if (System.currentTimeMillis()-tm>0){
                    MapCanvas.moveX=0;
                    MapCanvas.moveY=0;
                }
                try {
                    mt.img=null;
                    mt.img=Image.createImage(is);
                    //mt.img = Image.createImage(bi, 0, bi.length);
                    //}
                } finally {
                    is.close();
                }
//is.close();
            } finally {
                conn.close();
            }


            return true;
        } catch (Throwable t) {
            //#mdebug
            if (RMSOption.debugEnabled){
                DebugLog.add2Log("MLC5 FL:"+path+":"+t.toString());
            }
//#enddebug
            return false;
        }
    }
}
