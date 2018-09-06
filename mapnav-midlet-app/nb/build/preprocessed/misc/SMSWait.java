/*
 * @(#)SMSReceive.java	1.11 04/03/22
 *
 * Copyright (c) 2004 Sun Microsystems, Inc.  All rights reserved.
 * Use is subject to license terms
 */

package misc;

import RPMap.MapCanvas;
import RPMap.MapTile;
import RPMap.MapUtil;
import RPMap.RMSOption;
import RPMap.RMSTile;
import app.MapForms;
import java.io.IOException;
import java.util.Date;
import javax.microedition.io.Connector;
import javax.microedition.io.PushRegistry;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessageListener;
import javax.wireless.messaging.TextMessage;
import lang.Lang;
import lang.LangHolder;

/**
 * An example MIDlet displays text from an SMS MessageConnection
 */
public class SMSWait
    implements CommandListener, Runnable, MessageListener {
  
  /** user interface command for indicating Exit request. */
  Command exitCommand  = new Command(LangHolder.getString(Lang.back), Command.BACK, 10);
  /** user interface command for indicating Reply request */
  Command gotoCommand = new Command(LangHolder.getString(Lang.goto_), Command.ITEM, 1);
  Command gotoCMCommand = new Command(LangHolder.getString(Lang.gotoc), Command.ITEM, 2);
  Command saveCommand = new Command(LangHolder.getString(Lang.save), Command.ITEM, 3);
  /** user interface text box for the contents of the fetched URL. */
  public Alert content;
  /** instance of a thread for asynchronous networking and user interface. */
  Thread thread;
  /** Connections detected at start up. */
  String[] connections;
  /** Flag to signal end of processing. */
  boolean done;
  /** The port on which we listen for SMS messages */
  String smsPort;
  /** SMS message connection for inbound text messages. */
  public MessageConnection smsconn;
  /** Current message read from the network. */
  Message msg;
  /** Address of the message's sender */
  String senderAddress;
  /** Alert that is displayed when replying */
  Alert sendingMessageAlert;
  /** Prompts for and sends the text reply */
  SMSSender sender;
  /** The screen to display when we return from being paused */
  //Displayable resumeScreen;
  
  /**
   * Initialize the MIDlet with the current display object and
   * graphical components.
   */
  public SMSWait() {
    smsPort = MapForms.smsPort;
    
    //display = bm.display;
    
    content = new Alert("SMS",LangHolder.getString(Lang.smscoords),null,AlertType.INFO);
    content.setTimeout(Alert.FOREVER);
    content.addCommand(gotoCommand);
    content.addCommand(gotoCMCommand);
    content.addCommand(saveCommand);
    content.addCommand(exitCommand);
    content.setCommandListener(this);
    content.setString("Rec...ysnstt");
    
  }
  
  /**
   * Start creates the thread to do the MessageConnection receive
   * text.
   * It should return immediately to keep the dispatcher
   * from hanging.
   */
  public void start() {
    /** SMS connection to be read. */
    String smsConnection = "sms://:" + smsPort;
    try {
      /** Open the message connection. */
      if (smsconn == null) {
        try {
          smsconn = (MessageConnection) Connector.open(smsConnection);
          smsconn.setMessageListener(this);
        } catch (IOException ioe) {
//#mdebug
          if (RMSOption.debugEnabled)
            DebugLog.add2Log("Open SMS Conn:"+ioe.toString());
//#enddebug
        }
      }
      
      try {
        /** Initialize the text if we were started manually. */
        connections = PushRegistry.listConnections(true);
        if (connections == null || connections.length == 0) {
          content.setString("Waiting for SMS on port " + smsPort + "...");
        }
      } catch (Throwable e) {
//#mdebug
        if (RMSOption.debugEnabled)
          DebugLog.add2Log("list SMS conn:"+e.toString());
//#enddebug
      }
      done = false;
      thread = new Thread(this);
      thread.start();
    } catch (Throwable e) {
//#mdebug
      if (RMSOption.debugEnabled)
        DebugLog.add2Log("Common SMS:"+e.toString());
//#enddebug
    }
    //display.setCurrent(resumeScreen);
  }
  
  /**
   * Notification that a message arrived.
   * @param conn the connection with messages available
   */
  public void notifyIncomingMessage(MessageConnection conn) {
//#mdebug
    if (RMSOption.debugEnabled)
      DebugLog.add2Log("Incoming SMS - OK");
//#enddebug
    if (thread == null) {
      done = false;
      thread = new Thread(this);
      thread.start();
//#mdebug
      if (RMSOption.debugEnabled)
        DebugLog.add2Log("SMS thread started - OK");
//#enddebug
    }
  }
  
  /** Message reading thread. */
  public void run() {
    try {
      /** Check for sms connection. */
      try {
        msg = smsconn.receive();
        if (msg != null) {
          senderAddress = msg.getAddress();
          content.setTitle("From: " + senderAddress);
          if (msg instanceof TextMessage) {
            content.setString(((TextMessage)msg).getPayloadText());
          } else {
                 /*   StringBuffer buf = new StringBuffer();
                    byte[] data = ((BinaryMessage)msg).getPayloadData();
                    for (int i = 0; i < data.length; i++) {
                        int intData = (int)data[i] & 0xFF;
                        if (intData < 0x10) {
                            buf.append("0");
                        }
                        buf.append(Integer.toHexString(intData));
                        buf.append(' ');
                    }
                    content.setString(buf.toString());*/
          }
          MapCanvas.autoShowMap=false;
          MapCanvas.setCurrent(content);
        }
//#mdebug
        else if (RMSOption.debugEnabled)
          DebugLog.add2Log("Empty SMS recieved - BAD");
//#enddebug
        
      } catch (Throwable e) {
//#mdebug
        if (RMSOption.debugEnabled)
          DebugLog.add2Log("SMS recieve:"+e.toString());
//#enddebug
      }
    } finally {
      thread = null;
    }
  }
  
  /**
   * Destroy must cleanup everything.  The thread is signaled
   * to stop and no result is produced.
   * @param unconditional true if a forced shutdown was requested
   */
  public void stop() {
    done = true;
    thread = null;
    if (smsconn != null) {
      try {
        smsconn.close();
      } catch (IOException e) {
        // Ignore any errors on shutdown
      }
    }
  }
  
  /**
   * Respond to commands, including exit
   * @param c user interface command requested
   * @param s screen object initiating the request
   */
  public void commandAction(Command c, Displayable s) {
    try {
      if (c == saveCommand ) {
        // bm.display.setCurrent(bm.map);
        translateCoords(false);
        Date dt = new Date();
        
        MapCanvas.map.addMapMark("SMS "+MapUtil.trackNameAuto(),lat,lon,lvl);
        content.setString(content.getString()+"\n"+LangHolder.getString(Lang.saved)+"!");
        //Alert a=new Alert(LangHolder.getString(Lang.info"),
        //    LangHolder.getString(Lang.saved"),
        //    null,AlertType.INFO);
        //a.setTimeout(2000);
        //MapMidlet.display.setCurrent(a, content);
        
      } else if (c == exitCommand ) {
        MapCanvas.setCurrentMap();
      } else if (c == gotoCommand) {
        if (translateCoords(false)) gotoLoc();
      } else if (c == gotoCMCommand) {
        if (translateCoords(true)) gotoLoc();
      }
    } catch (Exception ex) {
      //   ex.printStackTrace();
    }
  }
  
  private double lat;
  private double lon;
  private int lvl=0;
  
  private void gotoLoc() {
    MapCanvas.gpsBinded=false;
    if (lvl>0) {
      MapCanvas.map.setLocation(lat,lon,lvl);
    } else
      MapCanvas.map.setLocation(lat,lon,12);
    
    MapCanvas.setCurrentMap();
    MapCanvas.map.repaint();
  }
  private boolean translateCoords(boolean maxLevel) {
    String s=MapUtil.emptyString;
    String[] data=null;
    try {
      s = content.getString()+"t";
      data = MapUtil.parseString("0,"+s,',');
      lat = Double.parseDouble(data[0]);
      lon = Double.parseDouble(data[1]);
      try {
        lvl = Integer.parseInt(data[2]);
        if (maxLevel) {
          RMSTile rt=MapCanvas.map.rmss.maxLevelInCache(lat,lon);
          if (rt!=null) {
            lvl=rt.level;
            MapCanvas.map.setMapSerView(rt.tileType);
            //MapCanvas.map.showMapSer = (byte)(rt.mapType&MapTile.SHOW_MASKSERVER);
            //MapCanvas.map.showMapView = (byte)(rt.mapType&MapTile.SHOW_SURMAP);
          }
        }
      }catch(Exception e) {
        lvl=10;
      }
      return true;
    }catch (Exception e){
//#mdebug
      if (RMSOption.debugEnabled)
        DebugLog.add2Log("SMS translate:"+e.toString());
//#enddebug
      
      Alert a=new Alert(LangHolder.getString(Lang.error),
          e.toString()+'\n'+s+'\n'+data[0],null,AlertType.ERROR);
      a.setTimeout(5000);
      MapCanvas.setCurrent(a, content);
      return false;
    }
  }
}
