/*
 * FileMapLoader.java
 *
 * Created on 7 ������� 2007 �., 7:32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package RPMap;

import app.MapForms;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Displayable;
import lang.Lang;
import lang.LangHolder;
//#debug
//# import misc.DebugLog;
import misc.ProgressResponse;
import misc.ProgressStoppable;

/**
 *
 * @author RFK
 */
public class FileMapLoader implements Runnable, ProgressStoppable {

  private String furl;
  private Displayable backDisp;
//  public int num,tot;
  /** Creates a new instance of FileMapLoader */
  public FileMapLoader(String url, Displayable backDisp) {
    furl=url;
    this.backDisp=backDisp;
    Thread t=new Thread(this);
    //t.setPriority(Thread.NORM_PRIORITY/2);
    t.start();
  }

  public void run() {
    try {
      load();

    } finally {
      //   MapCanvas.map.fileMapLoader=null;
      //  MapCanvas.map.repaint();
      progressResponse=null;
    }
  }//  private long timeP;
//  private void needPaint() {
//    if (timeP+2000<System.currentTimeMillis()) {
//      timeP=System.currentTimeMillis();
//      MapCanvas.map.repaint();
//    }
//  }
//  private boolean needUpd() {
//    if (timeP+2000<System.currentTimeMillis()) {
//      timeP=System.currentTimeMillis();
//      return true;
//    }
//    return false;
//  }
  private double lat,  lon;
  private int level;

  void load() {
//#debug
//#     double errPos =1;
//#debug
//#     double errPos1 =1;
    boolean overSize=false;
    int minL=50, maxL=0;
    long fSize=0;
//    timeP=System.currentTimeMillis();
    StreamConnection conn=null;
    InputStream is=null;
    String err=MapUtil.emptyString; // used for debugging
    int tr=0;
    long rst=System.currentTimeMillis();
    try {
//#debug
//#       errPos =1.05;

      try {
        try {
//#debug
//#           errPos =1.1;
          conn=(StreamConnection) Connector.open("file:///"+furl, Connector.READ);
//#debug
//#           errPos =1.15;
          try {
            fSize=((FileConnection) conn).fileSize();
          } catch (Throwable t) {
            fSize=1;
          }
//#debug
//#           errPos =1.2;
          if (fSize>MapCanvas.map.rmss.getSizeAvailable()+200000){
            overSize=true;
            throw new Exception("Map oversize! "+fSize);
          }
//#debug
//#           errPos =1.25;

          is=conn.openInputStream();
//#debug
//#           errPos =1.3;

          MapCanvas.map.repaint();

//#debug
//#           errPos =1.35;

          byte b;
          byte[] bt=new byte[30000];
          int n, n1, n2, n3, n4;
//#debug
//#           errPos =1.4;

          n1=is.read();
          n2=is.read();
          lat=n1+n2/60.;
          n1=is.read();
          n2=is.read();
          lon=n1+n2/60.;
          n2=is.read();
          level=n2;
          RMSOption.IMAPS_CENT[RMSOption.IMAPS_CENT.length-1]=level;
          RMSOption.IMAPS_CENT[RMSOption.IMAPS_CENT.length-2]=(float) lon;
          RMSOption.IMAPS_CENT[RMSOption.IMAPS_CENT.length-3]=(float) lat;

//#debug
//#           errPos =1.45;

          n1=is.read();
          n2=is.read();
          n3=is.read();
          n4=is.read();
          n=(n1<<24)+(n2<<16)+(n3<<8)+n4;
          //n=n<<8 +is.read();
          //n=n<<8 +is.read();
          //n=n<<8 +is.read();
//#debug
//#           errPos =1.5;
          int tot=n;
          try {
            MapCanvas.map.repaint();
            MapCanvas.map.serviceRepaints();
          } catch (Exception e) {
          }
//#debug
//#           errPos =1.55;
          MapTile mt;
          int NumX, NumY, Level, picSize;
          byte MapType;
          //tot=n;
          try {
//#debug
//#             errPos =1.6;
            //prA.setIndicator(getGauge(tot,1));
            //MapMidlet.display.setCurrent(prA,mapCanvas);
            String s=LangHolder.getString(Lang.waitpls);
            mt=new SurfaceMapTile(5, 5, 5, MapTile.SHOW_MP, false);
            for (int i=0; i<n; i++) {
              if (stopped) {
                 //#debug
//#               errPos =1.65;
                break;
             
              // num=i+1;
              }
              if (progressResponse!=null) {
                if (System.currentTimeMillis()-rst>2000){
                  s=LangHolder.getString(Lang.maploading)+i+'/'+tot;
                  progressResponse.setProgress((byte) (i*100/tot), s);
                  rst=System.currentTimeMillis();
                }
              }
              n1=is.read();
              n2=is.read();
              n3=is.read();
              n4=is.read();
              NumX=(n1<<24)+(n2<<16)+(n3<<8)+n4;
//#debug
//#               errPos =1.7;
              n1=is.read();
              n2=is.read();
              n3=is.read();
              n4=is.read();
              NumY=(n1<<24)+(n2<<16)+(n3<<8)+n4;
//#debug
//#               errPos =1.75;
              n1=is.read();
              n2=is.read();
              n3=is.read();
              n4=is.read();
              Level=(n1<<24)+(n2<<16)+(n3<<8)+n4;
//#debug
//#               errPos =1.8;
              //read but not used yet!
              MapType=(byte) is.read();

//#debug
//#               errPos =1.85;

              //mt= new NetworkMapTile(NumX,NumY,Level,MapTile.SHOW_MP,false);
              //mt= new SurfaceMapTile(NumX,NumY,Level,MapTile.SHOW_MP,false);
              mt.numX=NumX;
              mt.numY=NumY;
              mt.level=Level;
              if (Level<minL) {
                minL=Level;
              }
              if (Level>maxL) {
                maxL=Level;
                //#debug
//#               errPos =1.9;
              }
              n1=is.read();
              n2=is.read();
              n3=is.read();
              n4=is.read();
              picSize=(n1<<24)+(n2<<16)+(n3<<8)+n4;

              //#mdebug
//# //              if (RMSOption.debugEnabled)
//# //                DebugLog.add2Log("ML5 P:"+i+":S:"+picSize);
//#enddebug

//#debug
//#               errPos =1.92;
              if (picSize>bt.length){
                bt=null;
//#mdebug
//#                 if (RMSOption.debugEnabled)
//#                   DebugLog.add2Log("ML5 NS:"+picSize);
//#enddebug
//#debug
//#                 errPos =1.93;
                bt=new byte[picSize];
//#mdebug
//#                 if (RMSOption.debugEnabled)
//#                   DebugLog.add2Log("ML5 NS:"+picSize+" - OK");
//#enddebug
//#debug
//#                 errPos =1.94;
              }
              //int p=0;
              n1=0;
              while (n1!=picSize) {
                n1=n1+is.read(bt, n1, picSize-n1);
//#debug
//#               errPos =1.95;
              //for (int p=0;p<picSize;p++) bt[p]=(byte)is.read();
              }
              MapCanvas.map.rmss.savePic(mt, bt, picSize);
//#debug
//#               errPos =1.96;
              tr++;
            /*if (needUpd()) {
            prA.setIndicator(getGauge(tot,i));
            prA.setString(LangHolder.getString(Lang.maploading")+String.valueOf(num)+"/"+String.valueOf(tot));
            }*/
            //needPaint();
//#debug
//#               errPos =1.97;
            }
          } finally {
//#mdebug
//#             if (RMSOption.debugEnabled)
//#               DebugLog.add2Log("ML5 MS:"+bt.length+" - OK");
//#enddebug

            bt=null;

//#debug
//#             errPos1 =2.1;
            MapCanvas.map.rmss.writeRMSList(true);
//#mdebug
//#             if (RMSOption.debugEnabled)
//#               DebugLog.add2Log("ML5 saved - OK");
//#enddebug
//#debug
//#             errPos1 =2.2;
            // MapCanvas.map.setLocation(lat,lon,level);
            MapCanvas.map.rmss.writeSettingNow();
            MapCanvas.map.clearAllTiles();
//#mdebug
//#             if (RMSOption.debugEnabled)
//#               DebugLog.add2Log("ML5 cl - OK");
//#enddebug
//#debug
//#             errPos1 =2.3;
            //MapCanvas.map.showMapSer=MapTile.SHOW_MP;
            //MapCanvas.map.showMapView=MapTile.SHOW_SUR;
            // MapCanvas.map.setMapSerView((byte)(MapTile.SHOW_MP|MapTile.SHOW_SUR));
//#mdebug
//#             if (RMSOption.debugEnabled)
//#               DebugLog.add2Log("ML5 setMSV - OK");
//#enddebug
//#debug
//#             errPos1 =2.4;
            if (!stopped){
//              Alert a=new Alert(LangHolder.getString(Lang.map),
//                  LangHolder.getString(Lang.tiles)+":"+String.valueOf(tr)+"/"+ String.valueOf(n),
//                  null,AlertType.INFO);
//              a.setTimeout(2000);
////#debug
//              errPos1 =2.5;
//              MapMidlet.display.setCurrent(a, backDisp);
              MapCanvas.showmsg(LangHolder.getString(Lang.map), LangHolder.getString(Lang.tiles)+":"+String.valueOf(tr)+"/"+String.valueOf(n)+" \nM: "+String.valueOf(minL)+'-'+String.valueOf(maxL), AlertType.INFO, backDisp);
            //MapMidlet.display.setCurrent(backDisp);
            }
          }
        } finally {
          is.close();
          is=null;
          conn.close();
          conn=null;
        }
      } catch (IOException i1oe) {
//#mdebug
//#         if (RMSOption.debugEnabled)
//#           DebugLog.add2Log("ML1 IO:"+i1oe);
//#enddebug
      }

    } catch (Throwable e) {
//#mdebug
//#       if (RMSOption.debugEnabled)
//#         DebugLog.add2Log("ML2:"+errPos+':'+errPos1+':'+e);
//#enddebug
      Alert a;
      String ss;
      if (overSize) {
        a=new Alert(LangHolder.getString(Lang.map),
          LangHolder.getString(Lang.required)+':'+String.valueOf(fSize+100000)+"\n"+
          LangHolder.getString(Lang.available)+':'+String.valueOf(MapCanvas.map.rmss.getSizeAvailable()), null, AlertType.ERROR);
      } else {
        ss=LangHolder.getString(Lang.filenotfnd)+' '+e;
        //#mdebug
//#      ss="E:"+errPos+" E1:"+errPos1+" "+LangHolder.getString(Lang.filenotfnd)+' '+e;
//#enddebug
        a=new Alert(LangHolder.getString(Lang.map), ss, null, AlertType.ERROR);
      }
      a.setTimeout(15000);
      MapCanvas.setCurrent(a, backDisp);
    }
  }
  private ProgressResponse progressResponse;

  public void setProgressResponse(ProgressResponse progressResponse) {
    this.progressResponse=progressResponse;
  }
  private boolean stopped;

  public boolean stopIt() {
    stopped=true;
    return true;
  }
}
