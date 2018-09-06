/*
 * MMSMapSend.java
 *
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package RPMap;
import javax.microedition.io.Connector;
import javax.microedition.lcdui.AlertType;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessagePart;
import javax.wireless.messaging.MultipartMessage;
import lang.Lang;
import lang.LangHolder;
import misc.Util;

/**
 *
 * @author RFK
 */
public class MMSMapSend extends ScreenSend{
  
  /** Creates a new instance of MMSMapSend */
  public MMSMapSend(String addrs1, byte size, boolean series, long interval, int count,byte imageType) {
    super(addrs1, size,true,series,interval,count,imageType);
  }
  void send() {
    try {
      MessageConnection msgConn = (MessageConnection)Connector.open("mms://"+addr1);
      try {
        MultipartMessage mulMsg = (MultipartMessage)msgConn.newMessage(MessageConnection.MULTIPART_MESSAGE);
        
        mulMsg.setAddress("mms://"+addr1);
        //mulMsg.setAddress("mms://pavelrv@fromru.com");
        //mulMsg.setAddress(addr2);
        mulMsg.setSubject("Map picture from MapMobNavigator");
        
        
        String mimeType = "text/plain";
        String encoding = "utf-8";
        String text = "There is a map image from location: "+
            MapUtil.coordToString(MapUtil.getLat(MapCanvas.map.yCenter, MapCanvas.map.level), MapUtil.CLATTYPE,RMSOption.coordType)+
            " "+MapUtil.coordToString(MapUtil.getLon(MapCanvas.map.xCenter, MapCanvas.map.level), MapUtil.CLONTYPE,RMSOption.coordType);
        
        byte[] contents = Util.stringToByteArray(text, true);
        MessagePart msgPart = new MessagePart(contents, 0, contents.length, mimeType, "id1", "contentLocation", encoding);
        mulMsg.addMessagePart(msgPart);
        
        
        //InputStream is = getClass().getResourceAsStream("/img/t.jpg");
        if (imageType==IMAGEBMP)
          mimeType = "image/bmp";
        else
          mimeType = "image/gif";
        
        String fnn;
        if (imageType==IMAGEBMP)
          fnn = "map.bmp";
        else
          fnn = "map.gif";
        
        //contents = new byte[bmpsize*bmpsize*3+bmpheader.length];
        
        contents=getPic();
        sending=true;
        MapCanvas.map.repaint();
//is.read(contents);
        msgPart = new MessagePart(contents, 0, contents.length, mimeType, "id2", fnn, null);
        mulMsg.addMessagePart(msgPart);
        
        msgConn.send(mulMsg);
      }finally {
        sending=false;
        //msgConn.close();
      }
      MapCanvas.showmsg(LangHolder.getString(Lang.sent),LangHolder.getString(Lang.mmssent),AlertType.INFO, MapCanvas.map);
      //MapMidlet.display.setCurrent(new Alert(LangHolder.getString(Lang.sent),LangHolder.getString(Lang.mmssent),null,AlertType.INFO), MapCanvas.map);
    } catch (Exception e) {
      MapCanvas.showmsgmodal(LangHolder.getString(Lang.sent),e.toString(),AlertType.ERROR, MapCanvas.map);
//      MapMidlet.display.setCurrent(new Alert(LangHolder.getString(Lang.sent),e.toString(),null,AlertType.ERROR), MapCanvas.map);
    }
    
  }
}
