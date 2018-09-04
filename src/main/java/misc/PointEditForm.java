/*
 * PointEditForm.java
 *
 * Created on 15 ������ 2007 �., 12:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package misc;

import RPMap.MapCanvas;
import RPMap.MapPoint;
import RPMap.MapRoute;
import RPMap.MapUtil;
import RPMap.RMSOption;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import lang.Lang;
import lang.LangHolder;

/**
 *
 * @author RFK
 */
public class PointEditForm extends Form implements CommandListener, Runnable,ProgressStoppable{
  
  MapPoint mapPoint;
  TextField textName;
  ChoiceGroup choiceIcon;
  RouteEditList backDisplay;
  TextField textLat;
  TextField textLon;
  TextField textAlt;
  ChoiceGroup choiceForeColor;
  ChoiceGroup choiceBackColor;
  
  
  /** Creates a new instance of PointEditForm */
  public PointEditForm(RouteEditList backDisplay) {
    super(LangHolder.getString(Lang.editpoint));
    this.backDisplay=backDisplay;
    
    backCommand=new Command(LangHolder.getString(Lang.back), Command.BACK, 10);
    saveCommand=new Command(LangHolder.getString(Lang.save), Command.ITEM, 1);
    addCommand(backCommand);
    addCommand(saveCommand);
    setCommandListener(this);
    
    textName=new TextField(LangHolder.getString(Lang.label),null,30,TextField.ANY);
    append(textName);
    
    
    StringItem si = new StringItem(LangHolder.getString(Lang.example)+"\n",MapUtil.emptyString);
    if (RMSOption.coordType==RMSOption.COORDMINSECTYPE) si.setText("60 23 41\n(GG MM SS.S)");
    else if (RMSOption.coordType==RMSOption.COORDMINMMMTYPE) si.setText("60 23.683\n(GG MM.MMM)");
    else if (RMSOption.coordType==RMSOption.COORDGGGGGGTYPE) si.setText("60.39471\n(GG.GGGGG)");
    append(si);
    textLat=new TextField(LangHolder.getString(Lang.latitude),null,12,TextField.NON_PREDICTIVE);
    append(textLat);
    textLon=new TextField(LangHolder.getString(Lang.longitude),null,12,TextField.NON_PREDICTIVE);
    append(textLon);
    //!NO-NUMERIC
    textAlt=new TextField(LangHolder.getString(Lang.altitude),null,6,TextField.ANY|TextField.NON_PREDICTIVE);
    append(textAlt);
    
  }
  
  void setPoint(MapPoint mapPoint) {
    this.mapPoint=mapPoint;
    textName.setString(mapPoint.name);
    byte i=(byte)mapPoint.pointSymbol;
    if ((i<0)||(i>70))i=0;
    choiceIcon.setSelectedIndex(i,true);
    textLat.setString(MapUtil.coord2EditString(mapPoint.lat,MapUtil.CLATTYPE,RMSOption.coordType));
    textLon.setString(MapUtil.coord2EditString(mapPoint.lon,MapUtil.CLONTYPE,RMSOption.coordType));
    textAlt.setString(String.valueOf((int)mapPoint.alt));
    
    if (choiceForeColor.size()>MapUtil.colors.length) choiceForeColor.delete(choiceForeColor.size()-1);
    int ind = MapUtil.getColorIndex(mapPoint.foreColor);
    if (ind>=0) choiceForeColor.setSelectedIndex(ind,true);
    else {
      Image ic=Image.createImage(14,14);
      Graphics g=ic.getGraphics();
      g.setColor(mapPoint.foreColor);
      g.fillRect(0,0,14,14);
      choiceForeColor.append(MapUtil.emptyString,Image.createImage(ic));
      choiceForeColor.setSelectedIndex(MapUtil.colors.length-1,true);
    }
    
    if (choiceBackColor.size()>MapUtil.colors.length) choiceBackColor.delete(choiceBackColor.size()-1);
    ind = MapUtil.getColorIndex(mapPoint.backColor);
    if (ind>=0) choiceBackColor.setSelectedIndex(ind,true);
    else {
      Image ic=Image.createImage(14,14);
      Graphics g=ic.getGraphics();
      g.setColor(mapPoint.backColor);
      g.fillRect(0,0,14,14);
      choiceBackColor.append(MapUtil.emptyString,Image.createImage(ic));
      choiceBackColor.setSelectedIndex(MapUtil.colors.length-1,true);
    }
    
  }
  private Command backCommand,saveCommand;
  
  private void savePoint(){
    double lat,lon,alt;
    try{
      lat = MapUtil.parseCoord(textLat.getString(),RMSOption.coordType);
    }catch(Throwable t){
      MapCanvas.showmsgmodal(LangHolder.getString(Lang.attention),LangHolder.getString(Lang.followf)+"\n"+LangHolder.getString(Lang.latitude),AlertType.ERROR,this);
      return;
    }
    try{
      lon = MapUtil.parseCoord(textLon.getString(),RMSOption.coordType);
    }catch(Throwable t){
      MapCanvas.showmsgmodal(LangHolder.getString(Lang.attention),LangHolder.getString(Lang.followf)+"\n"+LangHolder.getString(Lang.longitude),AlertType.ERROR,this);
      return;
    }
    try{
      alt = Double.parseDouble(textAlt.getString());
    }catch(Throwable t){
      MapCanvas.showmsgmodal(LangHolder.getString(Lang.attention),LangHolder.getString(Lang.followf)+"\n"+LangHolder.getString(Lang.altitude),AlertType.ERROR,this);
      return;
    }
    mapPoint.lat=lat;
    mapPoint.lon=lon;
    mapPoint.alt=(int)alt;
    mapPoint.name=textName.getString();
    mapPoint.pointSymbol=(byte)choiceIcon.getSelectedIndex();
    if (choiceForeColor.getSelectedIndex()<MapUtil.colors.length) mapPoint.foreColor=MapUtil.colors[choiceForeColor.getSelectedIndex()];
    if (choiceBackColor.getSelectedIndex()<MapUtil.colors.length) mapPoint.backColor=MapUtil.colors[choiceBackColor.getSelectedIndex()];
    backDisplay.set(backDisplay.indexPointSelected,mapPoint.name,mapPoint.getImageIcon(MapRoute.ICONS_GARMIN));
    MapCanvas.setCurrent(backDisplay);
  }
  
  public void commandAction(Command command, Displayable displayable) {
    if (displayable == this) {
      if (command == backCommand) {
        MapCanvas.setCurrent(backDisplay);
        backDisplay=null;
      } else if (command == saveCommand) {
        savePoint();
        ((RouteEditList)backDisplay).changed=true;
        backDisplay=null;
      }
    }
  }
  boolean initialized;
  public void run(){
    try {
      if (!initialized)
        try{
          String s=MapUtil.spaceString;
          Image ic;
          Graphics g;
          Image[] ia = new Image[MapUtil.colors.length];
          String[] ss= new String[MapUtil.colors.length];
//          for (int i=0;i<MapUtil.colors.length;i++) {
//            ic=Image.createImage(14,14);
//            Thread.yield();
//            g=ic.getGraphics();
//            g.setColor(MapUtil.colors[i]);
//            g.fillRect(0,0,14,14);
//            Thread.yield();
//            ia[i]=ic;
//            ss[i]=s;
//            Thread.yield();
//          }
//          Thread.yield();
//          choiceForeColor= new ChoiceGroup(LangHolder.getString(Lang.forecolor),ChoiceGroup.POPUP,ss,ia);
            choiceForeColor= new ChoiceGroup(LangHolder.getString(Lang.forecolor),ChoiceGroup.POPUP);
          MapUtil.fillColorChoice(choiceForeColor);
          
          Thread.yield();
   //       choiceBackColor= new ChoiceGroup(LangHolder.getString(Lang.backcolor),ChoiceGroup.POPUP,ss,ia);
          choiceBackColor= new ChoiceGroup(LangHolder.getString(Lang.backcolor),ChoiceGroup.POPUP);
          MapUtil.fillColorChoice(choiceBackColor);
          Thread.yield();
          insert(1,choiceForeColor);
          Thread.yield();
          insert(2,choiceBackColor);
          
          Thread.yield();
          ia = new Image[71];
          ss= new String[71];
          Thread.yield();
          for (int i=0;i<=70;i++) {
            ia[i]=MapPoint.retrieveGarPointIcon(i);
            Thread.yield();
            ss[i]=s;
            Thread.yield();
            progressResponse.setProgress((byte)((i*100)/70),LangHolder.getString(Lang.icon));
          }
          
          choiceIcon= new ChoiceGroup(LangHolder.getString(Lang.icon),ChoiceGroup.POPUP,ss,ia);
          Thread.yield();
          insert(3,choiceIcon);
          Thread.yield();
          
          initialized=true;
        }catch(Throwable t) {
          //#mdebug
//#           if (RMSOption.debugEnabled)
//#             DebugLog.add2Log("RunPEF:"+t.toString());
//#enddebug
        }
      // try{Thread.sleep(7000);}catch(Throwable t){}
      setPoint(mapPoint);
    }finally{
      progressResponse=null;
      MapCanvas.setCurrent(this);}
  }
  
  ProgressResponse progressResponse;
      
  public void setProgressResponse(ProgressResponse progressResponse) {
    this.progressResponse=progressResponse;
  }

  public boolean stopIt() {
              return true;

  }
}
