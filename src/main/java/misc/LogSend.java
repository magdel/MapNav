/*
 * LogSend.java
 *
 * Created on 18 ������� 2007 �., 3:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package misc;

import RPMap.MapCanvas;
import RPMap.MapUtil;
import RPMap.RMSOption;
import app.MapForms;
import java.io.OutputStream;
import java.util.Date;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Displayable;
import lang.Lang;
import lang.LangHolder;

/**
 *
 * @author RFK
 */
public class LogSend implements Runnable,ProgressStoppable {
  Displayable backScreen;
  LogSend ls;
  String savepath;
  public static final int FILESEND = 1;
  public static final int WEBSEND = 2;
  private int typeSend;
  /** Creates a new instance of LogSend */
  public LogSend(Displayable bs, String path,int fs) {
    typeSend=fs;
    backScreen=bs;
    ls=this;
    savepath=path;
    Thread t = new Thread(this);
    t.start();
  }
  private StringBuffer propbuf= new StringBuffer();
  
  private String showProp(String prop) {
    String value = System.getProperty(prop);
    propbuf.setLength(0);
    propbuf.append(prop).append(" = ");
    if (value == null) {
      propbuf.append("<undefined>");
    } else {
      propbuf.append("\"").append(value).append("\"");
    }
    propbuf.append("\n");
    return propbuf.toString();
  }
  
  private String getConfig() {
    StringBuffer buf= new StringBuffer();
    Runtime runtime = Runtime.getRuntime();
    runtime.gc();
    
    Date dt = new Date();
    try{buf.append("MapNav " + MapForms.mM.getAppProperty("MIDlet-Version") +" "+dt.toString()+" "+RMSOption.netRadarLogin+"\n\n");}catch(Throwable t){}
    buf.append("Free Memory = " + String.valueOf(runtime.freeMemory()) + "\n")
    .append("Total Memory = " + String.valueOf(runtime.totalMemory()) + "\n");
    
    try{
      buf.append("Screen WxH = " + String.valueOf(MapCanvas.map.getWidth()) + 'x'+ String.valueOf(MapCanvas.map.getHeight()) + "\n");
    }catch(Throwable t){}
    
    buf.append("\n");
    
    buf.append(showProp("microedition.configuration"))
    .append(showProp("microedition.profiles"));
    
    buf.append(showProp("microedition.platform"))
    .append(showProp("microedition.locale"))
    .append(showProp("microedition.encoding"))
    .append(showProp("microedition.io.file.FileConnection.version"))
    .append(showProp("bluetooth.api.version"))
    .append(showProp("microedition.commports"))
    .append(showProp("microedition.location.version"));
    
//    buf.append("TimeZones: ");
//    for (int i=0;i<TimeZone.getAvailableIDs().length;i++) {
//      buf.append(i);
//      buf.append('-');
//      buf.append(TimeZone.getAvailableIDs()[i]+"  ");
//    }
    return buf.toString();
  }
  
  private static String transWeb(String s) {
    StringBuffer sb = new StringBuffer(1000);
    for (int i=0;i<s.length();i++) {
      if (s.charAt(i)=='\n') sb.append("<br>");
      else if (s.charAt(i)=='<') sb.append("(");
      else if (s.charAt(i)=='>') sb.append(")");
      else if (s.charAt(i)=='"') sb.append("'");
      else if (s.charAt(i)=='&') sb.append("e");
      else sb.append(s.charAt(i));
    }
    return sb.toString();
  }
  public boolean stopIt(){
    stopped=true;
            return true;

  }
  private boolean stopped;
  public void run() {
    if (typeSend==FILESEND)
      try {
        String fn=savepath+"LOG"+MapUtil.numStr(RMSOption.picFileNumber,4)+".TXT";
        RMSOption.picFileNumber++;
        RMSOption.changed=true;
        FileConnection fc = (FileConnection)Connector.open("file:///"+fn,Connector.WRITE);
        fc.create();
        OutputStream os =  fc.openOutputStream();
        try {
          os.write(Util.stringToByteArray(getConfig(),true));
          os.write("\n\n".getBytes());
          //os.write("\n".getBytes());
          os.write(Util.stringToByteArray(DebugLog.logString(),true));
        } finally {
          os.close();}
        
        MapCanvas.showmsg(LangHolder.getString(Lang.sent),LangHolder.getString(Lang.filesaved)+" \n"+fn,AlertType.INFO, backScreen);
        backScreen=null;
      } catch (Throwable e) {
        MapCanvas.showmsgmodal(LangHolder.getString(Lang.sent),e.toString(),AlertType.ERROR, backScreen);
        backScreen=null;
      } else try {
        
        String res=HTTPUtils.SendLog(transWeb(getConfig()+"\n\n"+DebugLog.logString()));
        if (stopped){
          ls=null;
          backScreen=null;
          return;
        }
        //if (rc==200)
        MapCanvas.showmsg(LangHolder.getString(Lang.sent),res,AlertType.INFO, backScreen);
        //else MapMidlet.display.setCurrent(new Alert(LangHolder.getString(Lang.sent"),"Some error:"+"\n"+rc,null,AlertType.INFO), backScreen);
        
      } catch (Throwable e) {
        MapCanvas.showmsgmodal(LangHolder.getString(Lang.sent),e.toString(),AlertType.ERROR, backScreen);
      };
      ls=null;
      backScreen=null;
  }

  public void setProgressResponse(ProgressResponse progressResponse) {
  }
}
