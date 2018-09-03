/*
 * MapTile.java
 *
 * Created on 5 ������ 2007 �., 15:54
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package RPMap;

import java.io.IOException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
//#debug
//# import misc.DebugLog;
import misc.HTTPUtils;
import misc.MVector;
import misc.ProgressResponse;

/**
 *
 * @author julia
 */
public abstract class MapTile implements ProgressResponse {

    public static String IMAGEGIF="image/gif";
    public static String IMAGEJPEG="image/jpeg";
    public static String IMAGEJPG="image/jpg";
    public static String IMAGEPNG="image/png";

    public static boolean isImageType(String type) {
        return type.equals(IMAGEJPEG)||type.equals(IMAGEJPG)||type.equals(IMAGEPNG)||type.equals(IMAGEGIF);
    }
    private static String QUEUED="Queued";

    public void setProgress(byte percent, String task) {
        imgState=task;
        percentLoaded=percent;
    }
    Image img;
    //Image imgN=null;
    int level;
    int numX;//����� �����
    int numY;
    String blockName;
    //int topY;//���������� ���� �����
    //int leftX;
    String imgState;
    byte percentLoaded;
    boolean mapLoaded;
    //boolean mapNLoaded=false;
    private static int[] rgb;//= new int[16384];
    private static int[] rgb256;//= new int[65536];

    public static void setPicScaling(boolean scale) {
        Runtime.getRuntime().gc();
        if (scale){
            try {
                rgb=new int[16384];
                rgb256=new int[65536];
            } catch (Throwable t) {
                rgb=null;
                rgb256=null;
                Runtime.getRuntime().gc();
//#debug
//#                 DebugLog.add2Log("MTSPS: "+t);
            }
        } else {
            rgb=null;
            rgb256=null;
        }
    }    //final static public byte MAPGL=1;
    //final static public byte NETGL=2;
    //final static public byte MAPNETGL=3;
    //final static public byte MAPVE=4+1;
    //final static public byte NETVE=4+2;
    //final static public byte MAPNETVE=4+3;
    final static public byte SHOW_GL=0;
    final static public byte SHOW_VE=1<<2;
    final static public byte SHOW_MP=2<<2;
    final static public byte SHOW_YH=3<<2;
    final static public byte SHOW_AS=4<<2;
    final static public byte SHOW_OS=5<<2;
    final static public byte SHOW_GU=6<<2;
    final static public byte SHOW_UM=7<<2;
    //final static public byte SHOW_CG = 7<<2;
    //final static public byte SHOW_MASKSERVER = 124;
    final static public byte SHOW_MASKSERVER=SHOW_GL|SHOW_VE|SHOW_MP|SHOW_YH|SHOW_AS|SHOW_OS|SHOW_GU|SHOW_UM;
    /** SHOW_GL SHOW_VE SHOW_MP*/
    byte tileServerType;
    final static public byte SHOW_SUR=1;
    final static public byte SHOW_MAP=2;
    final static public byte SHOW_SURMAP=3;
    /** SHOWMAP ��� SHOWNET */
    byte tileImageType;
    /** All info */
    byte tileType;

    void paintState(String s) {
        imgState=s;
    }

    /** Creates a new instance of MapTile */
    public MapTile(int nX, int nY, int lev, byte serverType) {
        tileServerType=serverType;
        paintState(QUEUED);
        numX=nX;
        numY=nY;
        level=lev;

        blockName=MapUtil.getGLBlockName(numX, numY, level);

        if (level==1){
            try {
                img=Image.createImage("/img/t.jpg");
                mapLoaded=true;
                paintState(null);
            } catch (IOException _ex) {
            }        //findScale();
        }
    }
    /*
    public MapTile(int nX,int nY, int lev, String fn, MapCanvas amc,byte mapType) {
    mc=amc;
    mapTileType=mapType;
    numX=nX;
    numY=nY;
    level=lev;
    topY=numY*MapUtil.blockSize;
    leftX=numX*MapUtil.blockSize;
    if ((mapType&MapTile.SHOW_VE)==MapTile.SHOW_VE) {
    blockName=MapUtil.getVEBlockName(numX,numY,level);
    } else blockName=MapUtil.getGLBlockName(numX,numY,level);
    try {
    img = Image.createImage(fn+".jpg");
    mapLoaded=true;
    } catch(IOException _ex) { }
    try {
    imgN = Image.createImage(fn+".png");
    mapNLoaded=true;
    } catch(IOException _ex) { }
    }
     */

//    final private String sub6String(String s) {
//        if (s.length()<6){
//            return s.substring(0, 1);
//        } else if (s.length()<7){
//            return s.substring(0, 2);
//        } else {
//            return s.substring(0, s.length()-5);
//        }
//    }
    final private boolean isInsideOf(MapTile mt) {
        if (mt.level>=level){
            return false;
        }
        String ss=blockName.substring(0, mt.blockName.length());
        return mt.blockName.equals(ss);
    }

    final void findScale() {
        try {
            if (img!=null){
                return;
            }
            if (rgb256==null){
                return;
            }
            int mtl=-1;
            MapTile mt=null;
            MapTile mtb=null;
            synchronized (MapCanvas.map.tiles) {
                for (int i=MapCanvas.map.tiles.size()-1; i>=0; i--) {
                    mt=(MapTile) MapCanvas.map.tiles.elementAt(i);
                    if (isInsideOf(mt)){
                        if (mt.img!=null){
                            if (mtl<mt.level){
                                mtl=mt.level;
                                mtb=mt;
                            }
                        }
                    }
                }
            }
//      if (mtb==null) {
//      }

            if (mtb==null){
                //paintState("---");
                return;
            }

            // if (mtb==null) {
            //   mtb=mc.getSpTile(0,0,1);
            // }
            mt=mtb;
            String s=blockName.substring(mt.blockName.length());
            if (s.length()>7){
                return;
            }
            int lx=0, ly=0, rx=255, ry=255, dd=128, spw=1;

            for (int i=0; i<s.length(); i++) {
                char sc=s.charAt(i);
                if (sc=='q'){
                    rx-=dd;
                    ry-=dd;
                } else if (sc=='r'){
                    lx+=dd;
                    ry-=dd;
                } else if (sc=='s'){
                    lx+=dd;
                    ly+=dd;
                } else { //t
                    rx-=dd;
                    ly+=dd;
                }
                dd=dd>>1;
                spw=spw<<1;
            }

            int width=rx-lx+1;
            int height=ry-ly+1;

            if ((img==null)&&(mt.img!=null)){
                try {
                    //    rgb= new int[16384];
                    mt.img.getRGB(rgb, 0, width, lx, ly, width, height);
                    //rgb256= new int[65536];
                    for (int a=width-1; a>=0; a--) {
                        for (int b=height-1; b>=0; b--) {
                            for (int cr=spw-1; cr>=0; cr--) {
                                for (int cc=spw-1; cc>=0; cc--) {
                                    rgb256[a*spw+(b*spw+cc)*256+cr]=rgb[a+b*width];
                                }
                                //  rgb=null;
                            }
                        }
                    }
                    if (img==null){
                        img=Image.createRGBImage(rgb256, 256, 256, false);
                    }
                } finally {
                    //rgb=null;
                    //rgb256=null;
                }
            }
        } catch (Throwable t) {
//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("MTFS: "+t);
//#             }
//#enddebug      
        }
    }

    final public void drawTile(Graphics g, int x, int y, int dminx, int dmaxx, int dminy, int dmaxy) {
        if ((img==null)){
            if (RMSOption.onlineMap){
                //g.setColor(0x0);
                //g.fillRect(x+0,y+0,MapUtil.blockSize-1, MapUtil.blockSize-1);
                g.setColor(0x0F10E0);
                g.drawLine(x+0, y+0, x+MapUtil.blockSize-1, y+MapUtil.blockSize-1);
                g.drawLine(x+0, y+MapUtil.blockSize-1, x+MapUtil.blockSize-1, y+0);
                g.drawRect(x+0, y+0, MapUtil.blockSize-1, MapUtil.blockSize-1);
                g.drawRect(x+1, y+1, MapUtil.blockSize-2, MapUtil.blockSize-2);
            }
        } else {

            if (img!=null){
                int x_src=0, y_src=0, xw=0, yw=0;
                int sw=0, sh=0;
                try {
                    if (dmaxx-dminx>MapUtil.blockSize){
                        sw=MapUtil.blockSize;
                    } else {
                        sw=dmaxx-dminx;
                    }
                    if (dmaxy-dminy>MapUtil.blockSize){
                        sh=MapUtil.blockSize;
                    } else {
                        sh=dmaxy-dminy;
                    }
                    if (x<dminx){
                        x_src=-x;
                    } else {
                        x_src=dminx;
                    }
                    if (y<dminy){
                        y_src=-y;
                    } else {
                        y_src=dminy;
                    }
                    if (x<dminx){
                        xw=MapUtil.blockSize+x;
                    } else {
                        xw=dmaxx-dminx-x;
                    }
                    if (y<dminy){
                        yw=MapUtil.blockSize+y;
                    } else {
                        yw=dmaxy-dminy-y;
                    }
                    if (xw>sw){
                        xw=sw;
                    }
                    if (yw>sh){
                        yw=sh;
                    }
                    if (x<dminx){
                        sw=0;
                    } else {
                        sw=x;
                    }
                    if (y<dminy){
                        sh=0;
                    } else {
                        sh=y;
                    }
                    if (dmaxy>50){
                        g.drawRegion(img, x_src, y_src, xw, yw, 0, sw, sh, Graphics.TOP|Graphics.LEFT);
                        //  g.drawImage(img,x, y, g.TOP|g.LEFT);
                    }
                } catch (Throwable e) {
//#mdebug
//#                     DebugLog.add2Log("MTDT:x "+String.valueOf(x)+":y "+String.valueOf(y)
//#                       +":dmaxx "+String.valueOf(dmaxx)+":dmaxy "+String.valueOf(dmaxy)
//#                       +":x_src "+String.valueOf(x_src)+":y_src "+String.valueOf(y_src)
//#                       +":xw "+String.valueOf(xw)+":yw "+String.valueOf(yw)
//#                       +":sw "+String.valueOf(sw)+":sh "+String.valueOf(sh)
//#                       +e.toString());
//#enddebug
                }
            }
        }
        if (imgState!=null){
            g.setColor(0xFF10C0);
            //g.setFont(Font.getDefaultFont());
            if (percentLoaded==0){
                g.drawString(imgState, x+100, y+127, Graphics.TOP|Graphics.LEFT);
            } else {
                g.drawString(String.valueOf(percentLoaded)+"%", x+100, y+127, Graphics.TOP|Graphics.LEFT);
            }
        }
    }

    abstract void load();

    final void loadCache() {
        if (!mapLoaded){
            mapLoaded=MapCanvas.map.rmss.readRMSPic(this);
        }
    }

    final public void goLoad() {
        addTileForQueue(this);
    }
    private static String JPG=".jpg";
    private static String PNG=".png";

    public final String getFileName() {
        if ((tileImageType==SHOW_SUR)&&(tileServerType!=SHOW_OS)&&(tileServerType!=SHOW_GU)&&(tileServerType!=SHOW_UM)){
            return MapUtil.getGLBlockName(numX, numY, level)+JPG;
        } else {
            return MapUtil.getGLBlockName(numX, numY, level)+PNG;
        }
    }

    public abstract String getFilePath();

    public final String getFileLevel() {
        return MapUtil.numStr(level, 2)+'/';
    }
    private static String GL="GL/";
    private static String VE="VE/";
    private static String OS="OS/";
    private static String GU="GU/";

    protected final String getFileServer() {
        switch (tileServerType) {
            case SHOW_GL:
                return GL;
            case SHOW_VE:
                return VE;
            case SHOW_YH:
                return "YH/";
            case SHOW_AS:
                return "AS/";
            case SHOW_OS:
                return OS;
            case SHOW_GU:
                return GU;
            case SHOW_UM:
                return "MP/";
            default:
                return "UNK/";

        }
    }

    void downloadImage(String url) throws InterruptedException {
        HTTPUtils hr=HTTPUtils.getHTTPContent(url, tileServerType==MapTile.SHOW_GL, this);
        if (hr.ioError){
            Thread.sleep(2000);
            hr=HTTPUtils.getHTTPContent(url, tileServerType==MapTile.SHOW_GL, this);
        }
        if (isImageType(hr.type)){
            byte[] pb=hr.baos.toByteArray();
            hr.baos=null;
            try {
                MapCanvas.map.rmss.savePic(this, pb);
            } finally {
                img=Image.createImage(pb, 0, pb.length); //Image.createImage(is);
            }
        } else {
            //Thread.sleep(2000);
            paintState(hr.type+":"+String.valueOf(hr.responseCode));
        }
    }

    public static int queueSize() {
        if (downloadQueue!=null){
            return downloadQueue.size()+downloading;
        }
        return 0;
    }
    private static MVector scaleQueue=new MVector();
    private static Thread scaleThread;
    private static final Object syncScale=new Object();
    private static MVector downloadQueue=new MVector();
    private static Thread downloadThread;
    private static Thread downloadThread2;
    private static final Object syncDownload=new Object();
    private static int downloading;

    public static void startQueue() {
        Runnable daemon=new Runnable() {

            public void run() {

                try {
                    while (true) {
                        MVector queue=scaleQueue;
                        if (queue==null){
                            break;
                        }

                        synchronized (syncScale) {
                            if (queue.size()==0){
                                syncScale.wait();
                            }
                        }
                        queue=scaleQueue;
                        if (queue==null){
                            break;
                        }
                        MapTile mt;
                        synchronized (syncScale) {
                            if (queue.size()==0){
                                continue;
                            }
                            mt=(MapTile) queue.firstElement();
                            queue.removeElementAt(0);
                        }

                        try {
                            if ((!mt.mapLoaded)&&(RMSOption.scaleMap)){
                                mt.findScale();
                            }
                        } catch (Error enp) {
                        }

                        if (mt.img!=null){
                            MapCanvas.map.repaint();
                        }
                        try {
                            mt.loadCache();
                            mt.paintState(null);
                        } catch (Throwable rr) {
                            //#debug
//#                             DebugLog.add2Log("SQLC: "+rr.getMessage());
                        }
                        if (mt.mapLoaded){
                            MapCanvas.map.repaint();
                            continue;
                        }
                        if (mt.tileServerType!=MapTile.SHOW_MP){
                            addTileForDownload(mt);
                        }
                    }
                } catch (Throwable ex) {
                    //#debug
//#                     DebugLog.add2Log("SQ: "+ex.getMessage());
                }
                scaleThread=null;
            }
        };
        scaleThread=new Thread(daemon);
        scaleThread.start();

        Runnable daemonDownload=new Runnable() {

            public void run() {
                synchronized (syncDownload) {
                    downloading++;
                }

                try {
                    while (true) {
                        MVector queue=downloadQueue;
                        if (queue==null){
                            break;
                        }
                        MapTile mt;

                        synchronized (syncDownload) {
                            downloading--;
                            if (queue.size()==0){
                                syncDownload.wait();
                            }
                            downloading++;
                            queue=downloadQueue;
                            if (queue==null){
                                break;
                            }
                            if (queue.size()==0){
                                continue;
                            }
                            mt=(MapTile) queue.firstElement();
                            queue.removeElementAt(0);
                        }
                        if (mt.level!=MapCanvas.map.levelDisp){
                            continue;
                        }

                        try {
                            mt.load();
                            if (mt.img!=null){
                                mt.paintState(null);
                            }
                        } catch (Throwable tr) {
                            //
                        }
                    }
                } catch (Throwable ex) {
                    //#debug
//#                     DebugLog.add2Log("DQ: "+ex.getMessage());
                }
                downloadThread=null;
                downloadThread2=null;
            }
        };
        downloadThread=new Thread(daemonDownload);
        downloadThread.start();
        if (RMSOption.parallelLoad){
            downloadThread2=new Thread(daemonDownload);
            downloadThread2.start();
        }
    }

    public static void terminateDownloadQueue() {
        if (scaleQueue!=null){
            synchronized (syncScale) {
                scaleQueue=null;
                syncScale.notify();
            }
        }
        if (downloadQueue!=null){
            synchronized (syncDownload) {
                downloadQueue=null;
                syncDownload.notifyAll();
            }
        }
    }

    public static void addTileForQueue(MapTile mt) {
        if (scaleQueue!=null){
            synchronized (syncScale) {
                scaleQueue.addElement(mt);
                syncScale.notify();
            }
        }
    }

    private static void addTileForDownload(MapTile mt) {
        if (downloadQueue!=null){
            synchronized (syncDownload) {
                downloadQueue.addElement(mt);
                syncDownload.notify();
            }
        }
    }

    public static void clearDownloadQueue() {
        if (downloadQueue!=null){
            synchronized (syncDownload) {
                downloadQueue.removeAllElements();
            }
        }
    }
}
