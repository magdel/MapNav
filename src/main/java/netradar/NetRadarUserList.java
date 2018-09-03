/*
 * NetRadarUserList.java
 *
 * Created on 3 ���� 2007 �., 23:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package netradar;

import RPMap.MapCanvas;
import RPMap.MapRoute;
import RPMap.MapUtil;
import RPMap.RMSOption;
import RPMap.RMSSettings;
import camera.MD5;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.game.Sprite;
import lang.Lang;
import lang.LangHolder;
import misc.*;

/**
 *
 * @author RFK
 */
public class NetRadarUserList  extends List implements CommandListener,Runnable,ProgressStoppable {
  MVector users;
  Image iconRed;
  Image iconYellow;
  Image iconGreen;
  /** Creates a new instance of NetRadarUserList */
  public NetRadarUserList() {
    super(LangHolder.getString(Lang.users),List.IMPLICIT);
    backCommand=new Command(LangHolder.getString(Lang.back), Command.BACK, 10);
      unbindCommand=new Command(LangHolder.getString(Lang.unbind), Command.ITEM, 4);
    addCommand(backCommand);
    
    setCommandListener(this);
    
    try {
      Image icons = Image.createImage("/img/userlist.png");
      iconGreen = Image.createImage(icons,0,0,20,18, Sprite.TRANS_NONE);
      iconYellow = Image.createImage(icons,0,18,20,18, Sprite.TRANS_NONE);
      iconRed = Image.createImage(icons,0,36,20,18, Sprite.TRANS_NONE);
    } catch (IOException exception) {
//#mdebug
//#       if (RMSOption.debugEnabled)
//#         DebugLog.add2Log("UL5:"+exception.toString());
//#enddebug
    }
    
  }
  public void fillUserList(MVector userlist){
    users=userlist;
    listCommands=null;
    deleteAll();
    append("Messages",null);
    NetRadarUser nru;
    for (int i=0;i<users.size();i++) {
      nru=(NetRadarUser)users.elementAt(i);
      append(nru.userName,getIcon(nru.dt));
    }
  }
  private Command backCommand,openCommand,navCommand,bindCommand;
  public Command unbindCommand;
  
  private List listCommands;
  private List get_listCommands(){
    
    if (listCommands==null){
      listCommands = new List(LangHolder.getString(Lang.action),List.IMPLICIT);
      listCommands.append(LangHolder.getString(Lang.goto_),null);
      listCommands.append(LangHolder.getString(Lang.nav2u),null);
      listCommands.append(LangHolder.getString(Lang.bind2u),null);
      listCommands.append(LangHolder.getString(Lang.sendmes),null);
      listCommands.addCommand(backCommand);
      listCommands.setCommandListener(this);
    }
    return listCommands;
  }
  
  private List listMesCommands;
  private List get_listMesCommands(){
    
    if (listMesCommands==null){
      listMesCommands = new List(LangHolder.getString(Lang.action),List.IMPLICIT);
      listMesCommands.append(LangHolder.getString(Lang.open),null);
      listMesCommands.append(LangHolder.getString(Lang.send),null);
      listMesCommands.addCommand(backCommand);
      listMesCommands.setCommandListener(this);
    }
    return listMesCommands;
  }
  
  
  private List listMessages;
  private List get_listMessages(){
    listMessages=null;
    if (listMessages==null){
      listMessages = new List(LangHolder.getString(Lang.messages),List.IMPLICIT);
      msgs.removeAllElements();
      Enumeration um = NetRadar.netRadar.getMsgs();
      NetRadarMessage nrm;
      while (um.hasMoreElements()){
        nrm = (NetRadarMessage)um.nextElement();
        msgs.addElement(nrm);
      }
      
      for (int i=0;i<msgs.size();i++)
        for (int j=i+1;j<msgs.size();j++)
          if (((NetRadarMessage)msgs.elementAt(i)).msgId < ((NetRadarMessage)msgs.elementAt(j)).msgId) {
        nrm=(NetRadarMessage)msgs.elementAt(i);
        msgs.setElementAt(msgs.elementAt(j),i);
        msgs.setElementAt(nrm,j);
          }
      
      for (int i=0;i<msgs.size();i++) {
        nrm=(NetRadarMessage)msgs.elementAt(i);
        listMessages.append(nrm.subject+' '+nrm.userName,(nrm.rf==0)?NetRadar.netRadar.imgRead:NetRadar.netRadar.imgUnread);
      }
      
      
      listMessages.addCommand(backCommand);
      listMessages.setCommandListener(this);
    }
    return listMessages;
  }
  
  private Vector msgs = new Vector(31);
  
  private TextBox textBox;
  private TextBox getTextBox(){
    if (textBox==null){
      textBox= new TextBox(selectedUser.userName,"",1000,TextField.ANY);
      sendCommand = new Command(LangHolder.getString(Lang.send),Command.ITEM,1);
      textBox.addCommand(sendCommand);
      textBox.addCommand(backCommand);
      textBox.setCommandListener(this);
    }
    return textBox;
  }
  Command sendCommand;
  
  NetRadarUser selectedUser;
  Displayable backSendDisp;
  public void commandAction(Command command, Displayable displayable) {
    if (displayable==listMesCommands){
      if (command == backCommand) {
        MapCanvas.setCurrent(listMessages);
      } else
        if (command == List.SELECT_COMMAND) {
        int i=listMesCommands.getSelectedIndex();
        switch (i) {
          case 0:
            //������� ������ � ������. ����� �� ���� ��� ���������
            backSendDisp=listMesCommands;
            String mes = RMSSettings.loadGeoInfo(nrMes.msgId,MapRoute.GEOINFO_NRMESS);
            if (mes==null){
              MapCanvas.setCurrent(new ProgressForm(LangHolder.getString(Lang.messages),LangHolder.getString(Lang.connecting),this,listMessages));
              runKind=GETMSGBODY;
              runObject=new Thread(this);
              ((Thread)runObject).start();
            } else {
              nrMes.rf=1;
              //listMessages=get_listMessages();
              MapCanvas.setCurrent(new TextCanvas("   "+mes,listMesCommands));
            }
            break;
          case 1:
            backSendDisp=listMesCommands;
            NetRadarUser nru = NetRadar.netRadar.getUser(String.valueOf(nrMes.senderId)+'.'+'1');
            if (nru==null){
              nru = new NetRadarUser();
              nru.userId = nrMes.senderId;
              nru.userName= String.valueOf(nrMes.senderId);
            }
            selectedUser=nru;
            MapCanvas.setCurrent(getTextBox());
            
            break;
        }
        
        }
    } else if (displayable==textBox){
      if (command == backCommand) {
        MapCanvas.setCurrent(backSendDisp);
      } else
        if (command == sendCommand) {
        //������� ���������� ���������� textbox
        //http://mapnav.spb.ru/cgi-bin/uvs.pl?loginame=magdel&logipass=mymap&rid=2&text=Hellomyfriend
        mesBody=textBox.getString();
        MapCanvas.setCurrent(new ProgressForm(LangHolder.getString(Lang.messages),LangHolder.getString(Lang.sending),this,textBox));
        runKind=SENDMSGBODY;
        runObject=new Thread(this);
        ((Thread)runObject).start();
        }
    } else
      if (displayable==listMessages){
      if (command == backCommand) {
        MapCanvas.setCurrent(this);
      } else
        if (command == List.SELECT_COMMAND) {
        //������� ������ � ������. ����� �� ���� ��� ���������
        NetRadarMessage nrm = (NetRadarMessage)msgs.elementAt(listMessages.getSelectedIndex());
        nrMes=nrm;
        MapCanvas.setCurrent(get_listMesCommands());
        }
      } else if (displayable==listCommands){
      if (command == backCommand) {
        MapCanvas.setCurrent(this);
      } else
        if (command==List.SELECT_COMMAND) {
        int i=listCommands.getSelectedIndex();
        switch (i) {
          case 0:
            MapCanvas.gpsBinded=false;
            NetRadar.netRadar.bind =false;
            MapCanvas.map.setLocation(selectedUser.lat,selectedUser.lon);
            MapCanvas.setCurrentMap();
            break;
          case 1:
            MapCanvas.gpsBinded=true;
            selectedUser.nav2 = MapCanvas.map.navigate2location(selectedUser.lat,selectedUser.lon,selectedUser.userName);
            MapCanvas.map.activeRoute.kind=MapRoute.NRWAYPOINTSKIND;
            selectedUser.nav2.visible=false;
            NetRadar.netRadar.bind =false;
            MapCanvas.setCurrentMap();
            break;
          case 2:
            MapCanvas.gpsBinded=false;
            selectedUser.nav2 = MapCanvas.map.navigate2location(selectedUser.lat,selectedUser.lon,selectedUser.userName);
            MapCanvas.map.activeRoute.kind=MapRoute.NRWAYPOINTSKIND;
            selectedUser.nav2.visible=false;
            NetRadar.netRadar.bind =true;
            MapCanvas.setCurrentMap();
            break;
          case 3:
            backSendDisp=listCommands;
            MapCanvas.setCurrent(getTextBox());
            break;
          case 4:
            break;
        }
        
        }
      } else
        if (displayable == this) {
      if (command == unbindCommand) {
        NetRadar.netRadar.bind=false;
        MapCanvas.gpsBinded=true;
        MapCanvas.setCurrentMap();
      } else if (command == backCommand) {
        MapCanvas.setCurrentMap();
      } else if (command==List.SELECT_COMMAND) {
        int i=getSelectedIndex();
        if (i==0) {
          MapCanvas.setCurrent(get_listMessages());
        } else {
          selectedUser =(NetRadarUser)users.elementAt(i-1);
          get_listCommands().setTitle(selectedUser.userName);
          MapCanvas.setCurrent(get_listCommands());
        }
      }
        }
  }
  
  private Image getIcon(long dt) {
    if (dt+180000>System.currentTimeMillis())
      return iconGreen;
    else if (dt+1800000>System.currentTimeMillis())
      return iconYellow;
    else return iconRed;
  }
  private byte runKind;
  private Object runObject;
  private String mesBody;
  private NetRadarMessage nrMes;
  private static final byte GETMSGLIST = 1;
  private static final byte GETMSGBODY = 2;
  private static final byte SENDMSGBODY = 3;
  public void run() {
    if (runKind==GETMSGLIST){
      
    } else if (runKind==GETMSGBODY){
      
      String url=NetRadar.netradarSiteURL+"nrs/ms_nr_uvr.php?lg="+HTTPUtils.urlEncodeString(RMSOption.netRadarLogin)+"&sign="+
        MD5.getHashString(RMSOption.netRadarLogin+nrMes.msgId+MD5.getHashString(RMSOption.netRadarPass))+
          "&letid="+nrMes.msgId+"&v=1";
      String res=MapUtil.emptyString;
      try{
        res=HTTPUtils.getHTTPContentAsString(url);// sendPostRequest(url,new String[] {"loginame","logipass","rid","text"},new String[]{RMSOption.netRadarLogin,RMSOption.netRadarPass,String.valueOf(selectedUser.userId), sb.toString()}),true);
        if (Thread.currentThread()!=runObject) return;
        
        nrMes.rf=0;
        listMessages=get_listMessages();
        MapCanvas.setCurrent(new TextCanvas("   "+res,backSendDisp));
        RMSSettings.saveGeoInfo(nrMes.msgId,res,MapRoute.GEOINFO_NRMESS);
        NetRadar.netRadar.updateStatusImg();
        
      }catch (Throwable t){
        MapCanvas.showmsg("Get",t.toString(),AlertType.ERROR,listMessages);
      }
      //nrMes=null;
      
    } else if (runKind==SENDMSGBODY){
      StringBuffer sb = new StringBuffer(400);
      for (int i=0;i<mesBody.length();i++)
        if (mesBody.charAt(i)=='"')
          sb.append('\'');
        else sb.append(mesBody.charAt(i));
      String url=NetRadar.netradarSiteURL;
      url=url+"nrs/ms_nr_uvs.php?v=1";
      String res=MapUtil.emptyString;
      try{
        res=Util.byteArrayToString(HTTPUtils.sendPostRequest(url,
          new String[] {"lg","sign","rid","text"},
          new String[]{RMSOption.netRadarLogin,MD5.getHashString(RMSOption.netRadarLogin+String.valueOf(selectedUser.userId)+MD5.getHashString(RMSOption.netRadarPass)),String.valueOf(selectedUser.userId), sb.toString()}),
          true);
      }catch (Throwable t){}
      if (Thread.currentThread()!=runObject) return;
      if (res.startsWith("$NS,OK")){
        MapCanvas.showmsg(LangHolder.getString(Lang.info),LangHolder.getString(Lang.sent),AlertType.INFO,backSendDisp);
        textBox.setString(MapUtil.emptyString);
      }
      else
        MapCanvas.showmsg(LangHolder.getString(Lang.error),"Some error. Try again,please",AlertType.ERROR,backSendDisp);
      
    }
  }
  
  public void setProgressResponse(ProgressResponse progressResponse) {
  }
  
  public boolean stopIt() {
    runObject=null;
            return true;

  }
  
  
}
