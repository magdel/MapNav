/*
 * FileMapSend.java
 *
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package RPMap;

import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.AlertType;
import lang.Lang;
import lang.LangHolder;

/**
 *
 * @author RFK
 */
public class FileScreenSend extends ScreenSend {
  
  /** Creates a new instance of FileMapSend */
  public FileScreenSend(String addrs1, byte size, boolean series, long interval, int count, byte imageType) {
    super(addrs1, size,false,series,interval,count,imageType);
  }
  public FileScreenSend(String addrs1, byte[] pic) {
    super(addrs1, (byte)100,false,false,0,0,ScreenSend.IMAGEJPG);
    this.pic=pic;
  }
  byte[] pic;
  void send() {
    //"file:///c:/other/"
    try {
      //String fn=addr1+"MAP"+MapUtil.make4(RMSOption.picFileNumber)+".BMP";
      String fn=MapUtil.getScreenFileName(addr1,imageType);
      try {
        MapCanvas.map.rmss.writeSetting();
      }catch (Exception e){}
      FileConnection fc = (FileConnection)Connector.open("file:///"+fn,Connector.WRITE);
      fc.create();
      OutputStream os =  fc.openOutputStream();
      try {
        if (pic==null)
        os.write(getPic());
        else 
        os.write(pic);
      } finally {
        os.close();}
     MapCanvas.showmsg(LangHolder.getString(Lang.sent),LangHolder.getString(Lang.filesaved)+" \n"+fn,AlertType.INFO, MapCanvas.map);
//      MapMidlet.display.setCurrent(new Alert(LangHolder.getString(Lang.sent),LangHolder.getString(Lang.filesaved)+" \n"+fn,null,AlertType.INFO), MapCanvas.map);
      
    } catch (Exception e) {
      MapCanvas.showmsgmodal(LangHolder.getString(Lang.sent),e.toString(),AlertType.ERROR, MapCanvas.map);
      //MapMidlet.display.setCurrent(new Alert(LangHolder.getString(Lang.sent),e.toString(),null,AlertType.ERROR), MapCanvas.map);
    };
  }
  
}
