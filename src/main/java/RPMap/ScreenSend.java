/*
 * MapSend.java
 *
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package RPMap;
import app.MapForms;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import lang.Lang;
import lang.LangHolder;
//#if GIF
//# import net.jmge.gif.Gif89Encoder;
//#endif
/**
 *
 * @author RFK
 */
public abstract class ScreenSend implements Runnable{
  
  public final static byte SENDMMS =1;
  public final static byte SENDFILE =2;
  public final static byte SENDFILEINTERVAL =3;
  
  public final static byte IMAGEGIF =0;
  public final static byte IMAGEBMP =1;
  public final static byte IMAGEJPG =2;
  
  String addr1;
  int bmpw=120;
  int bmph=120;
  
  boolean sending;
  byte[] bmpheader = {
    0x42 ,0x4D ,0x38 ,0x2C ,0x01 ,0x00 ,0x00 ,0x00
        ,0x00 ,0x00 ,0x36 ,0x00 ,0x00 ,0x00 ,0x28 ,0x00
        ,0x00 ,0x00 ,-0x60 ,0x00 ,0x00 ,0x00 ,-0x60 ,0x00
        ,0x00 ,0x00 ,0x01 ,0x00 ,0x18 ,0x00 ,0x00 ,0x00
        ,0x00 ,0x00 ,0x02 ,0x2C ,0x01 ,0x00 ,0x20 ,0x2E
        ,0x00 ,0x00 ,0x20 ,0x2E ,0x00 ,0x00 ,0x00 ,0x00
        ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00
  };
  boolean series;
  long interval;
  int count;
  byte imageType;
  /** Creates a new instance of MapSend */
  public ScreenSend(String addrs1, byte size, boolean restrict, boolean series, long interval, int count, byte imageType) {
    addr1=addrs1;
    this.series=series;
    this.interval=interval;
    this.count=count;
    this.imageType=imageType;
    if (size==1) {bmpw=120;bmph=120;} else if (size==2) {bmpw=128;bmph=160;} else
      if (restrict) {
      bmpw=170;bmph=210;} else if (size==3) {bmpw=176;bmph=220;} else if (size==4) {bmpw=MapCanvas.map.dmaxx;bmph=MapCanvas.map.dmaxy;}
    
    //bmpw=240;
    //bmph=320;
    Thread t = new Thread(this);
    t.start();
    
  }
  
//  void writeNormalGIF(Image img,
// *                      String annotation,
// *                      int transparent_index,  // pass -1 for none
// *                      boolean interlaced,
// *                      OutputStream out) throws IOException
// *  {
// *    Gif89Encoder gifenc = new Gif89Encoder(img);
// *    gifenc.setComments(annotation);
// *    gifenc.setTransparentIndex(transparent_index);
// *    gifenc.getFrameAt(0).setInterlaced(interlaced);
// *    gifenc.encode(out);
// *  }
  
  public byte[] getPic() throws IOException{
    if (imageType==IMAGEGIF)
      return getGIFPic();
    else
      return getBMPPic();
  }
  
  private byte[] getGIFPic() throws IOException {
    //#if GIF
//#     MapCanvas.setCurrent(new SSCanvas());
//#     try{
//#       Gif89Encoder gifenc= new Gif89Encoder(getScreenImage(),MapCanvas.mode==MapCanvas.MAPMODE);
//#       //gifenc.setComments("");
//#       //gifenc.setTransparentIndex(transparent_index);
//#       //gifenc.getFrameAt(0).setInterlaced(interlaced);
//#       ByteArrayOutputStream baos = new ByteArrayOutputStream(22000);
//#       gifenc.encode(baos);
//#       return baos.toByteArray();
//#     }finally{
//#       MapCanvas.setCurrentMap();
//#     }
    //#else
    return getBMPPic();
    //#endif
  }
  private byte[] getBMPPic() throws IOException{
    int[] bi;
    byte[] bmpi;
    MapCanvas.setCurrent(new SSCanvas());
    try{
      bi = new int[bmpw*bmph];
      getScreenImage().getRGB(bi, 0, bmpw, 0,0, bmpw, bmph);
      int bmphl=bmpheader.length;
      bmpi = new byte[bmpw*bmph*3+bmphl];
      int biai=bmpw*bmph-1;
      for (int i=0;i<bmphl;i++)
        bmpi[i]=bmpheader[i];
      byte hbw = (byte)(bmpw>>8);
      byte hbh = (byte)(bmph>>8);
      bmpi[18]=(byte)bmpw;
      bmpi[19]=(byte)hbw;
      bmpi[22]=(byte)bmph;
      bmpi[23]=(byte)hbh;
      
      for (int y=0;y<bmph;y++)
        for (int x=0;x<bmpw;x++) {
        bmpi[(bmpw-x-1)*3+y*bmpw*3+bmphl]=(byte)((bi[biai]&0xFF));
        bmpi[(bmpw-x-1)*3+y*bmpw*3+1+bmphl]=(byte)((bi[biai]&0xFF00)>>8);
        bmpi[(bmpw-x-1)*3+y*bmpw*3+2+bmphl]=(byte)((bi[biai]&0xFF0000)>>16);
        biai--;
        }
      bi=null;
    }finally{
      MapCanvas.setCurrentMap();
    }
    return bmpi;
//    os.write(bmpheader);
    //os.write(bmpi);
    
  }
  private Image getScreenImage(){
    Image mi;
    if (MapCanvas.map.landscapeMap)
      mi = Image.createImage(bmph,bmpw);
    else
      mi = Image.createImage(bmpw,bmph);
    try {
      Graphics g = mi.getGraphics();
      MapCanvas.map.updateSize=false;
      MapCanvas.map.dmaxx=bmpw;
      MapCanvas.map.dmaxy=bmph;
      MapCanvas.map.dcx = bmpw/2;
      MapCanvas.map.dcy = bmph/2;
      MapCanvas.map.paint(g);MapCanvas.map.paint(g);

      if (MapCanvas.map.landscapeMap)
      {
        int a=bmph;bmph=bmpw;bmpw=a;
      }
    }finally{
      MapCanvas.map.updateSize=true;
    }
    return mi;
  }
  abstract void send();
  
  public void run() {
    try {
      send();
      if (series) {
        count--;
        try{ while(count>0){
          Thread.sleep(interval);
          send();
          count--;
        }
        }catch(Throwable t){}
      }
    } finally {
      MapCanvas.map.mapSend=null;
    }
  }
  
  public static void sendMapMMS(String addrs1, byte size, byte sendtype, long interval, int count, byte imageType) {
    try{
      if (sendtype==SENDMMS) {
        MapCanvas.map.mapSend = new MMSMapSend(addrs1, size, false,interval,count,imageType);
      } else if ((sendtype==SENDFILE)||(sendtype==SENDFILEINTERVAL)) {
        MapCanvas.map.mapSend = new FileScreenSend(addrs1, size, (sendtype==SENDFILEINTERVAL),interval, count,imageType);
      }
      
      MapCanvas.setCurrentMap();
      MapForms.mm.clearForms();
    } catch (Throwable e){
      MapCanvas.showmsgmodal(LangHolder.getString(Lang.send),e.toString(),AlertType.ERROR, MapCanvas.map);
     // MapMidlet.display.setCurrent(new Alert(LangHolder.getString(Lang.send),e.toString(),null,AlertType.ERROR), MapCanvas.map);
    }
  }
  
  
}
