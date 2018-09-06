/*
 * KMLMapRoute.java
 *
 * Created on 21 ������ 2007 �., 12:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package kml;

import RPMap.MapCanvas;
import RPMap.MapRoute;
import RPMap.MapUtil;
import RPMap.RMSOption;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
//#debug
import misc.DebugLog;
import misc.HTTPUtils;
import misc.MVector;
import misc.ProgressResponse;
import misc.ProgressStoppable;
import misc.TextCanvas;
import org.xmlpull.v1.XmlPullParserException;

/**
 *
 * @author Raev
 */
public final class KMLMapRoute extends MapRoute implements ProgressStoppable {

    public String description;
    public String href;
    public long refreshInterval=60000;
    public String srcKML;
    public boolean notReady;
    public static final byte KMLKIND_POINTS=0;
    public static final byte KMLKIND_ROUTE=1;
    public byte kmlKind;//=KMLKIND_POINTS

    /** Saves route to stream */
    public void save2Stream(DataOutputStream out) throws Exception {
        super.save2Stream(out);
        if (description==null){
            description=MapUtil.emptyString;
        }
        out.writeUTF(description);
        if (href==null){
            href=MapUtil.emptyString;
        }
        out.writeUTF(href);
        out.writeLong(refreshInterval);
        if (srcKML==null){
            srcKML=MapUtil.emptyString;
        }
        out.writeUTF(srcKML);
        out.writeByte(kmlKind);
        out.writeByte(1);//version;
    }

    /** Create route from stream */
    public KMLMapRoute(DataInputStream in) throws Exception {
        super(in);
        description=in.readUTF();
        href=in.readUTF();
        refreshInterval=in.readLong();
        srcKML=in.readUTF();
        kmlKind=in.readByte();
        in.readByte(); //version
    }

    /** Creates a new instance of KMLMapRoute */
    public KMLMapRoute() {
        super(KMLDOCUMENT);
    }

    public KMLMapRoute(String sourceKML) {
        super(KMLDOCUMENT);
        srcKML=sourceKML;
    }

    public void readLocal() throws XmlPullParserException, IOException {
        KMLParser kp=new KMLParser(this, KMLParser.PARSENETWORKKML);
        kp.runWith(this.getClass().getResourceAsStream(srcKML));
    }

    public void readURL(String url) throws IOException, XmlPullParserException {
        HttpConnection httpConn=HTTPUtils.getHttpConn(url);
        try {
            InputStream is=httpConn.openInputStream();
            //InputStream is = this.getClass().getResourceAsStream("/maps_1.kml");
            try {
                KMLParser kp=new KMLParser(this, KMLParser.PARSEDOCUMENTKML);
                kp.runWith(is);
                geoInfo=GEOINFO_KML;
                notReady=false;
            } finally {
                try {
                    is.close();
                } catch (Throwable t) {
                }
                MapCanvas.map.repaint();
            }
        } finally {
            httpConn.close();
        }
    }

    public void start() {
        active=true;
        notReady=true;
        stopped=false;
        (new Thread(this)).start();
    }
    public boolean active;
    private boolean stopped;

    public boolean stopIt() {
        active=false;
        stopped=true;
                return true;

    }

    public void run() {
        long lastRun=0;
        int xC, yC;
        do {
            lastRun=System.currentTimeMillis();
            xC=MapCanvas.xCenter;
            yC=MapCanvas.yCenter;
            try {
                String url=null;
                if (href.indexOf('?')<0){
                    url=href+'?'+MapUtil.getBBOX();
                } else {
                    url=href+'&'+MapUtil.getBBOX();////        ByteArrayOutputStream baos= HTTPUtils.getHTTPContent("http://8motions.com/tools/kmlProxy.php?url=" + HTTPUtils.urlEncodeString("http://wikimapia.org/d/?BBOX=29,59,30,60"));
//        String ss="http://www.mgmaps.com/unzipkml.php?url=" + HTTPUtils.urlEncodeString("http://wikimapia.org/d/?BBOX=29,59,30,60")+"&gzip=yes";
//        ByteArrayOutputStream baos= HTTPUtils.getHTTPContent(ss);
//
//        FileConnection fc = (FileConnection)Connector.open("file:///root1/res3.data");
//        fc.create();
//        fc.openOutputStream().write(baos.toByteArray());
//        fc.close();
                }
                if ((url.endsWith("kmz"))||(url.startsWith("http://wiki"))) //   url = "http://8motions.com/tools/kmlProxy.php?url=" + HTTPUtils.urlEncodeString(url);
                //" + n._dStringString(s) + (ba.m_MZ ? "
                {
                    url="http://www.mgmaps.com/unzipkml.php?url="+HTTPUtils.urlEncodeString(url);//+"&gzip=yes";
                }
                readURL(url);
//        HttpConnection httpConn = HTTPUtils.getHttpConn(url);
//        try{
//
////          int i = httpConn.getResponseCode();
////            String s2 = httpConn.getType();
////            String s = httpConn.getHeaderField("Location");
////
////            s= s+s;
//          InputStream is = httpConn.openInputStream();
//          try{
//
//            KMLParser kp = new KMLParser(this,KMLParser.PARSEDOCUMENTKML);
//            kp.runWith(is);
//            MapCanvas.map.repaint();
//            notReady=false;
//
//          }finally{
//            is.close();
//          }
//        }finally{
//          httpConn.close();
//        }
            } catch (Throwable t) {
                notReady=true;
                //#mdebug
                if (RMSOption.debugEnabled) {
                    DebugLog.add2Log("KMLR:"+t);
                }
//#enddebug
            }
            try {
                while (lastRun+10000>System.currentTimeMillis()) {
                    Thread.sleep(2000);
                }

                while ((Math.abs(xC-MapCanvas.xCenter)<60)&&(Math.abs(yC-MapCanvas.yCenter)<60)) {
                    Thread.sleep(1000);
                }

            } catch (Throwable t) {
            }
        } while (!stopped);
        active=false;
    }
//  private void parseInputStream(InputStream is) throws IOException,XmlPullParserException {
//    KXmlParser parser = new KXmlParser();
//    parser.setInput(new InputStreamReader(is,"UTF-8"));
//
//  }

    protected void recalcMapLevelScreen(KMLMapPoint pt) {
        if (pt==null){
            mapLevelScreen=MapCanvas.map.level;
            KMLMapPoint mp;
            for (int i=pts.size()-1; i>=0; i--) {
                mp=(KMLMapPoint) pts.elementAt(i);
                mp.scrX=MapUtil.getXMap(mp.lon, mapLevelScreen);
                mp.scrY=MapUtil.getYMap(mp.lat, mapLevelScreen);
                if (mp.isRect()){
                    mp.scrWBB=MapUtil.getXMap(mp.westBB, mapLevelScreen);
                    mp.scrEBB=MapUtil.getXMap(mp.eastBB, mapLevelScreen);
                    mp.scrNBB=MapUtil.getYMap(mp.northBB, mapLevelScreen);
                    mp.scrSBB=MapUtil.getYMap(mp.southBB, mapLevelScreen);
                }
            }
        } else {
            pt.scrX=MapUtil.getXMap(pt.lon, mapLevelScreen);
            pt.scrY=MapUtil.getYMap(pt.lat, mapLevelScreen);

            if (pt.isRect()){
                pt.scrWBB=MapUtil.getXMap(pt.westBB, mapLevelScreen);
                pt.scrEBB=MapUtil.getXMap(pt.eastBB, mapLevelScreen);
                pt.scrNBB=MapUtil.getYMap(pt.northBB, mapLevelScreen);
                pt.scrSBB=MapUtil.getYMap(pt.southBB, mapLevelScreen);
            }
        }
    }

    public void drawRoute(Graphics g, int dcx, int dcy, boolean rotate) {
        byte backKind=kind;
        try {
            kind=WAYPOINTSKIND;
            showLabels=false;
            super.drawRoute(g, dcx, dcx, rotate);
            KMLMapPoint p2;
            int dx=dcx-MapCanvas.xCenter, dy=dcy-MapCanvas.yCenter;
            for (int i=pts.size()-1; i>=0; i--) {

                p2=(KMLMapPoint) pts.elementAt(i);

                if (p2.lineCoords!=null){
                    int x1, y1, x2, y2, xt, yt;
                    g.setColor(0xF050F0);
                    float[] lc=p2.lineCoords;
                    int ml=MapCanvas.map.level;
                    for (int j=lc.length/2-2; j>=0; j--) {
//            g.drawLine(
//                MapUtil.getXMap(lc[j+j+2],ml)+dx,
//                MapUtil.getYMap(lc[j+j+3],ml)+dy,
//                MapUtil.getXMap(lc[j+j],ml)+dx,
//                MapUtil.getYMap(lc[j+j+1],ml)+dy
//                );

                        x1=MapUtil.getXMap(p2.lineCoords[j+j+2], ml)+dx;
                        y1=MapUtil.getYMap(p2.lineCoords[j+j+3], ml)+dy;
                        if (MapCanvas.rotateMap){
                            xt=x1;
                            yt=y1;
                            x1=MapCanvas.rotateMX(xt, yt);
                            y1=MapCanvas.rotateMY(xt, yt);
                        }

                        x2=MapUtil.getXMap(p2.lineCoords[j+j], ml)+dx;
                        y2=MapUtil.getYMap(p2.lineCoords[j+j+1], ml)+dy;
                        if (MapCanvas.rotateMap){
                            xt=x2;
                            yt=y2;
                            x2=MapCanvas.rotateMX(xt, yt);
                            y2=MapCanvas.rotateMY(xt, yt);
                        }

                        g.setColor(0x202080);
                        g.drawLine(x1+1, y1+1, x2+1, y2+1);
                        g.setColor(0x40FF40);
                        g.drawLine(x1, y1, x2, y2);

                    }
                }
            }

        } finally {
            kind=backKind;
        }
    }

    public void drawRoute(Graphics g, int dcx, int dcy) {
        MapCanvas mc=MapCanvas.map;
        if (MapCanvas.rotateMap){
            return;
        }
        if (mc.level!=mapLevelScreen){
            recalcMapLevelScreen(null);
        }
        int x1, y1, x2, y2, xt, yt;
        KMLMapPoint p1, p2;
        int innerRect=-1, innerSize=10000000;
        int fh=g.getFont().getHeight();

        int pc=0;
        //connect with line
        int dx=dcx-MapCanvas.xCenter, dy=dcy-MapCanvas.yCenter;
        int sw=0;

        for (int i=pts.size()-1; i>=pc; i--) {

            p2=(KMLMapPoint) pts.elementAt(i);


            //!!!!!!!!!!!!!!!! if (!mc.isOn2Screen(x2,y2)) continue;
            if (!p2.isRect()){
                x2=p2.scrX+dx;
                y2=p2.scrY+dy;
                if (showLabels){
                    if (p2.name!=null){
                        sw=g.getFont().stringWidth(p2.name)/2+1;
                        g.setColor(p2.backColor);
                        g.fillRect(x2-sw-1, y2-fh-1-10, sw*2+2, fh+2);
                        g.setColor(p2.foreColor);
                        g.drawLine(x2, y2, x2, y2-9);
                        g.drawString(p2.name, x2, y2-10, Graphics.BOTTOM|Graphics.HCENTER);
                        g.drawRect(x2-sw-1, y2-fh-1-10, sw*2+2, fh+2);
                    }
                }
                g.setColor(0x70FF60);
                g.drawRect(x2-3, y2-3, 7, 7);
                g.setColor(0x3030A0);
                g.drawRect(x2-4, y2-4, 9, 9);



            } else {
                if ((dcx>=p2.scrWBB+dx)&&(dcx<=dx+p2.scrEBB)&&(dcy>=p2.scrNBB+dy)&&(dcy<=dy+p2.scrSBB)){
                    if (innerSize>p2.scrEBB-p2.scrWBB){
                        innerSize=p2.scrEBB-p2.scrWBB;
                        innerRect=i;
                    }
                }
                //draw here rect of wiki
                g.setColor(0x0);
                g.drawRect(p2.scrWBB+dx+1, p2.scrNBB+dy+1, p2.scrEBB-p2.scrWBB, p2.scrSBB-p2.scrNBB);
                g.setColor(0xE0E0FF);
                g.drawRect(p2.scrWBB+dx, p2.scrNBB+dy, p2.scrEBB-p2.scrWBB, p2.scrSBB-p2.scrNBB);

            }
        }
        if (innerRect>=0){
            p2=(KMLMapPoint) pts.elementAt(innerRect);

            //draw here rect of wiki
            if (p2.lineCoords!=null){

                int fi=0, ni=2;
                for (int j=p2.lineCoords.length/2; j>=0; j--) {

                    x1=MapUtil.getXMap(p2.lineCoords[fi], mapLevelScreen)+dx;
                    y1=MapUtil.getYMap(p2.lineCoords[fi+1], mapLevelScreen)+dy;
                    x2=MapUtil.getXMap(p2.lineCoords[ni], mapLevelScreen)+dx;
                    y2=MapUtil.getYMap(p2.lineCoords[ni+1], mapLevelScreen)+dy;

                    g.setColor(0x0);
                    g.drawLine(x1+1, y1+1, x2+1, y2+1);
                    g.setColor(0xA0FFA0);
                    g.drawLine(x1, y1, x2, y2);

                    fi=fi<p2.lineCoords.length-3?fi+2:0;
                    ni=ni<p2.lineCoords.length-3?ni+2:0;
                }
            }

            x2=p2.scrWBB+(p2.scrEBB-p2.scrWBB)/2+dx;
            y2=p2.scrNBB+dy-5;

            if (p2.name!=null){


                if (!splitedName.equals(p2.name)){
                    splitedV.removeAllElements();
                    TextCanvas.split(p2.name, splitedV, g.getClipWidth()*3/4, MapUtil.SMALLFONT);
                    splitedName=p2.name;
                }
                if (splitedV.size()>0){
                    Font f=g.getFont();
                    g.setFont(MapUtil.SMALLFONT);
                    g.setColor(RMSOption.foreColor);
                    for (int i=0; i<splitedV.size(); i++) {
                        //s=(String)closest.status_v.elementAt(i);
                        MapCanvas.drawMapString(g, (String) splitedV.elementAt(i),
                          MapCanvas.dmaxx, MapCanvas.dminy+5+(i+1)*MapUtil.SMALLFONT.getHeight(), Graphics.RIGHT|Graphics.TOP);
//              g.drawString(s,dminx,dminy+(i+1)*MapUtil.SMALLFONT.getHeight(),Graphics.LEFT|Graphics.TOP);
                    }
                    g.setFont(f);
                }


//        sw = g.getFont().stringWidth(p2.name) / 2 + 1;
//        g.setColor(p2.backColor);
//        g.fillRect(x2 - sw - 1, y2 - fh - 1 - 10, sw * 2 + 2, fh + 2);
//        g.setColor(p2.foreColor);
//        g.drawLine(x2, y2, x2, y2 - 9);
//        g.drawString(p2.name, x2, y2 - 10, Graphics.BOTTOM | Graphics.HCENTER);
//        g.drawRect(x2 - sw - 1, y2 - fh - 1 - 10, sw * 2 + 2, fh + 2);
            }
        }
    }
    String splitedName="";
    MVector splitedV=new MVector();

    public void setProgressResponse(ProgressResponse progressResponse) {
    }
}
