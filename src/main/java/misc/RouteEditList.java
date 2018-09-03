/*
 * RouteEditList.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package misc;

import RPMap.MapCanvas;
import RPMap.MapPoint;
import RPMap.MapRoute;
import app.MapForms;
import gpspack.GPSReader;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextBox;
import lang.Lang;
import lang.LangHolder;

/**
 *
 * @author RFK
 */
public class RouteEditList extends List implements CommandListener {
  
  Displayable backDisplay;
  MapRoute mapRoute;
  PointEditForm pointEditForm;
  public boolean changed;
  /** Creates a new instance of RouteEditList */
  public RouteEditList(Displayable backDisplay, MapRoute mapRoute) {
    super(mapRoute.name,List.IMPLICIT);
    this.backDisplay=backDisplay;
    this.mapRoute=mapRoute;
//    editCommand=new Command(LangHolder.getString(Lang.edit), Command.ITEM, 1);
    insCommand=new Command(LangHolder.getString(Lang.insert), Command.ITEM, 2);
//    delCommand=new Command(LangHolder.getString(Lang.delete), Command.ITEM, 3);
    renCommand=new Command(LangHolder.getString(Lang.rename), Command.ITEM, 4);
    ren1Command=new Command(LangHolder.getString(Lang.rename), Command.ITEM, 1);
    saveCommand=new Command(LangHolder.getString(Lang.save), Command.ITEM, 5);
//    upCommand=new Command(LangHolder.getString(Lang.up), Command.ITEM, 6);
//    gotoCommand=new Command(LangHolder.getString(Lang.goto_), Command.ITEM, 7);
    backCommand=new Command(LangHolder.getString(Lang.back), Command.BACK, 10);
    noCommand=new Command(LangHolder.getString(Lang.no), Command.CANCEL, 2);
    yesCommand=new Command(LangHolder.getString(Lang.yes), Command.OK, 1);
    addCommand(backCommand);
    addCommand(insCommand);
    addCommand(renCommand);
    addCommand(saveCommand);
    setCommandListener(this);
    //setSelectCommand(editCommand);
    MapPoint mp;
    if (mapRoute.kind!=MapRoute.TRACKKIND) {
      for (int i=0;i<mapRoute.pts.size();i++){
        mp = (MapPoint)mapRoute.pts.elementAt(i);
        append(mp.getName(),mp.getImageIcon((byte)1));
      }
    } else {
      for (int i=0;i<mapRoute.pts.size();i++){
        mp = (MapPoint)mapRoute.pts.elementAt(i);
        append(mp.getName(),null);
      }
    }
    
  }
  
  private Command backCommand,ren1Command,saveCommand,insCommand,noCommand,renCommand,yesCommand;
  private TextBox textBox;
  private Form formAsk;
  private List listCommands;
  private List get_listCommands(){
    if (listCommands==null){
      listCommands = new List(LangHolder.getString(Lang.action),List.IMPLICIT);
      listCommands.append(LangHolder.getString(Lang.edit),null);
      listCommands.append(LangHolder.getString(Lang.up),null);
      listCommands.append(LangHolder.getString(Lang.down),null);
      listCommands.append(LangHolder.getString(Lang.delete),null);
      listCommands.append(LangHolder.getString(Lang.goto_),null);
      listCommands.addCommand(backCommand);
      listCommands.setCommandListener(this);
    }
    return listCommands;
  }
  
  public void clearAll(){
    backDisplay=null;
    mapRoute=null;
    if (pointEditForm!=null){
        pointEditForm.backDisplay=null;
        pointEditForm.choiceBackColor=null;
        pointEditForm.choiceForeColor=null;
        pointEditForm.choiceIcon=null;
        pointEditForm.mapPoint=null;
        pointEditForm.progressResponse=null;
        pointEditForm.textAlt=null;
        pointEditForm.textLat=null;
        pointEditForm.textLon=null;
        pointEditForm.textName=null;
    }
    pointEditForm=null;
    formAsk=null;
    listCommands=null;
  }
  int indexPointSelected=-1; 
  public void commandAction(Command command, Displayable displayable) {
    if (displayable == formAsk) {
      if (command == yesCommand) {
        MapCanvas.map.rmss.saveRoute(mapRoute);
        MapForms.mm.refreshRouteForm(mapRoute.kind);
        MapCanvas.setCurrent(backDisplay);
        clearAll();
      } else if (command == noCommand) {
        MapForms.mm.refreshRouteForm(mapRoute.kind);
        MapCanvas.setCurrent(backDisplay);
        clearAll();
      }
    } else
      if (displayable == textBox) {
      if (command == backCommand) {
        MapCanvas.setCurrent(this);
        textBox=null;
      } else if (command == ren1Command) {
        mapRoute.name=textBox.getString();
        setTitle(mapRoute.name);
        MapCanvas.setCurrent(this);
        textBox=null;
        changed=true;
      }
      } else
        if (displayable==listCommands){
      if (command == backCommand) {
        MapCanvas.setCurrent(this);
      } else if (command==List.SELECT_COMMAND){
        //int i=0;
        indexPointSelected = getSelectedIndex();
        if (indexPointSelected<0) return;
            
        switch (listCommands.getSelectedIndex()) {
          //EDIT
          case 0:
            if (pointEditForm==null) pointEditForm= new PointEditForm(this);
            else pointEditForm.backDisplay=this;
            if (pointEditForm.initialized) {
              pointEditForm.setPoint((MapPoint)mapRoute.pts.elementAt(indexPointSelected));
              MapCanvas.setCurrent(pointEditForm);
            }else {
              pointEditForm.mapPoint=(MapPoint)mapRoute.pts.elementAt(indexPointSelected);
              if (pointEditForm.initialized) {
                pointEditForm.setPoint(pointEditForm.mapPoint);
                MapCanvas.setCurrent(pointEditForm);
              } else {
                MapCanvas.setCurrent(new ProgressForm(LangHolder.getString(Lang.loading),LangHolder.getString(Lang.icon),pointEditForm,null));
                (new Thread(pointEditForm)).start();
              }
            }
            
            break;
            //DELETE
          case 3:
            mapRoute.pts.removeElementAt(indexPointSelected);
            delete(indexPointSelected);
            changed=true;
            MapCanvas.setCurrent(this);
            break;
            //UP
          case 1:
            if (indexPointSelected<1) return;
            MapPoint mp = (MapPoint)mapRoute.pts.elementAt(indexPointSelected-1);
            MapPoint mpu = (MapPoint)mapRoute.pts.elementAt(indexPointSelected);
            mapRoute.pts.setElementAt(mpu,indexPointSelected-1);
            mapRoute.pts.setElementAt(mp,indexPointSelected);
            set(indexPointSelected,mp.name,mp.getImageIcon(MapRoute.ICONS_GARMIN));
            set(indexPointSelected-1,mpu.name,mpu.getImageIcon(MapRoute.ICONS_GARMIN));
            setSelectedIndex(indexPointSelected-1,true);
            changed=true;
            MapCanvas.setCurrent(this);
            break;
            //DOWN
          case 2:
            if (indexPointSelected>size()-2) return;
            mp = (MapPoint)mapRoute.pts.elementAt(indexPointSelected+1);
            mpu = (MapPoint)mapRoute.pts.elementAt(indexPointSelected);
            mapRoute.pts.setElementAt(mpu,indexPointSelected+1);
            mapRoute.pts.setElementAt(mp,indexPointSelected);
            set(indexPointSelected,mp.name,mp.getImageIcon(MapRoute.ICONS_GARMIN));
            set(indexPointSelected+1,mpu.name,mpu.getImageIcon(MapRoute.ICONS_GARMIN));
            setSelectedIndex(indexPointSelected+1,true);
            changed=true;
            MapCanvas.setCurrent(this);
            break;
            //GOTO
          case 4:
            mp=(MapPoint)mapRoute.pts.elementAt(indexPointSelected);
            MapCanvas.map.setLocation(mp.lat,mp.lon);
            backDisplay=null;
            MapCanvas.map.setRoute(mapRoute);
            mapRoute.setActPts(indexPointSelected);
            MapForms.mm.back2Map();
            break;
        }
      }
        } else if (displayable == this) {
      //  if (command == listMore.SELECT_COMMAND) {
      //       switch (get_listMore().getSelectedIndex()) {
      //         case 8:
      if (command == backCommand) {
        if (changed) {
          formAsk = new Form(LangHolder.getString(Lang.save));
          formAsk.append(new StringItem(mapRoute.name+" \n",LangHolder.getString(Lang.savechng),StringItem.PLAIN));
          formAsk.addCommand(yesCommand);
          formAsk.addCommand(noCommand);
          formAsk.setCommandListener(this);
          MapCanvas.setCurrent(formAsk);} else {
          MapForms.mm.refreshRouteForm(mapRoute.kind);
          MapCanvas.setCurrent(backDisplay);
          clearAll();
          }
      } else if (command == List.SELECT_COMMAND) {
        get_listCommands().setTitle(getString(getSelectedIndex()));
        MapCanvas.setCurrent(listCommands);
      } else if (command == insCommand) {
        int i = getSelectedIndex();
        MapPoint mp = new MapPoint((MapCanvas.gpsBinded)?GPSReader.LATITUDE:MapCanvas.reallat,
                (MapCanvas.gpsBinded)?GPSReader.LONGITUDE:MapCanvas.reallon,0,System.currentTimeMillis(),"NEW WPT");
        if (i<0)i=0;
        mapRoute.pts.insertElementAt(mp,i);
        insert(i,mp.name,mp.getImageIcon((byte)1));
        changed=true;
      } else if (command == saveCommand) {
        MapCanvas.map.rmss.saveRoute(mapRoute);
        MapCanvas.showmsg(LangHolder.getString(Lang.ok),LangHolder.getString(Lang.saved)+"\n"+mapRoute.name,AlertType.INFO, this);
        changed=false;
      } else if (command == renCommand) {
        textBox = new TextBox(LangHolder.getString(Lang.rename),mapRoute.name,30,0);
        textBox.addCommand(ren1Command);
        textBox.addCommand(backCommand);
        textBox.setCommandListener(this);
        MapCanvas.setCurrent(textBox);
//MapCanvas.map.rmss.saveRoute(mapRoute);
        //MapMidlet.display.setCurrent(new Alert(LangHolder.getString(Lang.ok"),LangHolder.getString(Lang.saved")+"\n"+mapRoute.name,null,AlertType.INFO), this);
      }
        }
  }
  
}
