/*
 * MapCanvas.java
 *
 * Created on  5 Январь 2007 г., 15:54
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package RPMap;
import app.MapMidlet;
import gpspack.GPSReader;
import app.MapForms;
import java.io.InputStream;
import java.util.Timer;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import kml.KMLMapRoute;
import lang.LangHolder;
import gpspack.LocStarter;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import lang.Lang;
//#debug
//# import misc.DebugLog;
import misc.GPSClubLoad;
import misc.GeneralFeedback;
import misc.GraphUtils;
import misc.KeyLockTimer;
import misc.LastVersion;
import misc.LightTimer;
import misc.LocationCanvas;
import misc.MNSInfo;
import misc.MVector;
import misc.MarkList;
import netradar.NetRadar;
import netradar.NetRadarIT;
import netradar.NetRadarUser;
import misc.SMSSender;
import misc.SMSWait;
import misc.TextCanvas;
import raev.ui.menu.CanvasMenu;
import raev.ui.menu.CanvasMenuItem;
import raev.ui.menu.CanvasMenuListener;

public final class MapCanvas extends Canvas implements GeneralFeedback, CommandListener, Runnable,CanvasMenuListener{

  public static Display display;
  
  public static MapCanvas map;
  public static SMSSender ss;
  public static boolean autoShowMap = true;
  public RMSSettings rmss;
  public OuterMapLoader oml;
  public static int xCenter;
  public static int yCenter;
  public int level=1;
  public int levelDisp=1;
  
  //public static int loadingTiles;
  public static int loadedBytes;
  
  /** SHOW_VE SHOW_GL SHOW_MP SHOW_YH*/
  public byte showMapSer;
  public byte showMapSerDisp;
  
  /** SHOWMAP SHOWNET SHOWMAPNET */
  public byte showMapView;
  public byte showMapViewDisp;
  
  public byte[] showMapSers;// = {MapTile.SHOW_GL,MapTile.SHOW_VE,MapTile.SHOW_MP};
  
  public byte[][] showMapViews;
//  = {
//    {MapTile.SHOWMAP,MapTile.SHOWMAPNET},
//    {MapTile.SHOWMAP,MapTile.SHOWNET},
//    {MapTile.SHOWMAP}};
  
  int curMapSerIndex ;
  int curMapViewIndex ;
  
  public MapRoute activeRoute;
  public MapRoute activeTrack;
  
  //public FileMapLoader fileMapLoader;
  
  public void setRoute(MapRoute r) {
    activeRoute=null;
    activeRoute=r;
  }
  
  public MapRoute getActiveRoute() {
    return activeRoute;
  }
  
  /** Map tiles list */
  public final MVector tiles = new MVector();
  
  /** Listeners for raw coordinate values */
  public MVector gpsLocListenersRaw = new MVector();
  
  /** List of all KMLMapRoutes */
  public MVector kmlRoutes = new MVector();
  
  /** List of marks */
  private MVector marks = new MVector();

  public void  loadRoute(int i,byte kind) {if (kind==MapRoute.TRACKKIND) startTrack(rmss.loadRoute(i, kind)); else activeRoute=rmss.loadRoute(i, kind);};
  
  public int getMinCacheSize() {
    //minCacheSizeb=(showMapView==MapTile.SHOW_SURMAP)?10:6;
    if (RMSOption.safeMode) return 1;
    int minCacheSizeb=((dmaxx-1)/MapUtil.blockSize +2)*((dmaxy-1)/MapUtil.blockSize +2);
    if (minCacheSizeb>12)minCacheSizeb=12;
    if (showMapView==MapTile.SHOW_SURMAP)
        minCacheSizeb*=2;
    int dp=RMSOption.mapRotate?minCacheSizeb/2:0;

    return minCacheSizeb+dp;
  }
  
  private void clearOneTile() {
    synchronized(tiles) {
    if (level>1) {
      for (int i=0;i<tiles.size();i++) {
        MapTile mt= (MapTile) tiles.elementAt(i);
        if ((mt.level==1)||(mt.level>level)) {
          tiles.removeElementAt(i);
          break;
        }
      }
    }

     if (tiles.size()>RMSOption.cacheSize+getMinCacheSize()) {
       for (int i=0;i<tiles.size();i++) {
          MapTile mt= (MapTile) tiles.elementAt(i);
          if (!isTileCurrentlyVisible(mt)) {
            tiles.removeElementAt(i);
           break;
          }
       }
      }
     if (tiles.size()>RMSOption.cacheSize+getMinCacheSize()) {
       for (int i=0;i<tiles.size();i++) {
          MapTile mt= (MapTile) tiles.elementAt(i);
          if (!isTileCurrentlyVisible(mt)) {
            tiles.removeElementAt(i);
           break;
          }
       }
      }

      if (tiles.size()>RMSOption.cacheSize+getMinCacheSize()) {
        tiles.removeElementAt(0) ;
      }
    }
  }

    private int minXViewable;
	private int minYViewable;
	private int maxXViewable;
	private int maxYViewable;

	public boolean isTileCurrentlyVisible(MapTile mapTile) {
		
		return mapTile.level == level && mapTile.numX >= minXViewable
				&& mapTile.numX <= maxXViewable && mapTile.numY >= minYViewable
				&& mapTile.numY <= maxYViewable;
	}

//  private void clearTilesInLevel1(int level,int xe, int ye) {
//    synchronized(tiles) {
//       for (int i=tiles.size()-1;i>=0;i--) {
//        MapTile mt= (MapTile) tiles.elementAt(i);
//        if ((mt.level==level)&&((mt.numX!=xe)||(mt.numY!=ye))) {
//          tiles.removeElementAt(i) ;
//        }
//      }
//    }
//
//  }
  public void clearAllTiles() {
    MapTile.clearDownloadQueue();
    
     synchronized(tiles) {
        tiles.removeAllElements();
     }
    System.gc();
  }

    private void drawScreenCaption(int langIndex, Graphics g, int fh, byte rr, int posIndex, int posCount) {
        if (splitMode==0){
            g.setColor(0x008000);
            g.fillRoundRect(dminx, dminy, dmaxx, fh, rr, rr);
            g.setColor(0xFFFFFF);
            g.drawString(LangHolder.getString(langIndex), dcx, dminy, Graphics.TOP|Graphics.HCENTER);
            if (posCount==0)
            g.drawLine(fh/4, fh, fh/4+(dmaxx-fh/2), fh);
            else {
            g.fillTriangle(fh/4, fh/2, fh/2, fh*3/4, fh/2, fh/4);
            g.fillTriangle(fh/2+fh/2, fh/2, fh/2+fh/4, fh*3/4, fh/2+fh/4, fh/4);
                g.drawLine(fh/4+(dmaxx-fh/2)*posIndex/posCount, fh, fh/4+(dmaxx-fh/2)*(posIndex+1)/posCount, fh);
            }
       }
    }
  private void reloadTiles() {
    if (RMSOption.getBoolOpt(RMSOption.BL_RELOADWITHDELETE))try{
      synchronized(tiles){
       for (int i=tiles.size()-1;i>=0;i--) {
        rmss.deletePic((MapTile)tiles.elementAt(i));
      }
      }
    }catch(Throwable t){
      //#debug
//#       DebugLog.add2Log("MC rT:"+t);
    }
    clearAllTiles();
    //tRunLoad=0;
    repaint();
  }
  public MapTile getTile(int numX,int numY, byte tileType) {
    if ((userMapIndexUsed==0)&&(MapTile.SHOW_MP==showMapSer)) return null;
    if ((numX<0)||(numY<0)) return null;
    int tc = (level < 2) ? 1 : 2 << (level - 2);
    if (numX>=tc) return null;
    if (numY>=tc) return null;
    
    MapTile mt=null;
    if (level==1) {
        synchronized(tiles){
      for (int i=tiles.size()-1;i>=0;i--) {
        mt= (MapTile) tiles.elementAt(i);
        if ((mt.numX==numX)&&(mt.numY==numY)&&(mt.level==level))
          return mt;
      }
        }
    } else {

      for (int i=tiles.size()-1;i>=0;i--) {
      mt= (MapTile) tiles.elementAt(i);
      if ((mt.numX==numX)&&(mt.numY==numY)&&(mt.level==level)&&(mt.tileImageType==tileType)&&(mt.tileServerType==showMapSer))
        return mt;
      }
    }
      clearOneTile();
      if (level==1) {
        mt = new SurfaceMapTile(0,0,1, showMapSer,false);
      } else {
        if (tileType==MapTile.SHOW_SUR) mt = new SurfaceMapTile(numX, numY, level, showMapSer,true);
        if (tileType==MapTile.SHOW_MAP) mt = new NetworkMapTile(numX, numY, level, showMapSer,true);
        if (tileType==MapTile.SHOW_SURMAP) mt = new SurNetwMapTile(numX, numY, level, showMapSer,true);
      }
     synchronized(tiles){
     tiles.addElement(mt);
     }
      return mt;
    
  }

  private long nextBlinkStartTime;
  private long nextBlinkEndTime;
  private boolean isBlinking;
  private void setBlinking(){
     if (nextBlinkEndTime<System.currentTimeMillis()){
       nextBlinkStartTime=System.currentTimeMillis()+10000;
       nextBlinkEndTime=nextBlinkStartTime+1000;
       isBlinking=false;
     } else{
         isBlinking = (nextBlinkStartTime<System.currentTimeMillis());
     }
  }
  private void blink(){
       nextBlinkStartTime=System.currentTimeMillis();
       nextBlinkEndTime=nextBlinkStartTime+1000;

  }


  /** Does the phone has pointer? (on exit plays as settings save success flag)*/
  public static boolean hasPointer;
  public MapCanvas() {
    
    MapCanvas.map=this;
    
      //#debug
//#      DebugLog.add2Log("MC");
        MapMidlet.ulp(22);
    hasPointer=hasPointerEvents();
    //#debug
//#     int dp=0;
    try{
      try {
        rmss= new RMSSettings();
                MapMidlet.ulp(31);
        //#debug
//#         dp=1;
        //LangHolder.setCurrUiLanguage(LangHolder.LANG_AVAILABLE[0]);
        String[] LANG_AVAILABLE = LangHolder.LANG_AVAILABLE();
         try{
        LangHolder.setCurrUiLanguage(LANG_AVAILABLE[RMSOption.currLang]);
        //throw new Exception("sssss1");
        }catch(Throwable t){
           //#debug
//#            DebugLog.add2Log("L i:"+t);
           RMSOption.currLang = 0;
           LangHolder.setCurrUiLanguage(LANG_AVAILABLE[RMSOption.currLang]);
        }
                MapMidlet.ulp(32);
        //#debug
//#         dp=2;
        //    canDisplay = display;
        if (RMSOption.fullScreen)
        try{  setFullScreenMode(RMSOption.fullScreen);}catch(Throwable t){}
                MapMidlet.ulp(33);
      } finally {
        //    compasCommand = new Command(LangHolder.getString(Lang.compass"), Command.ITEM, 1);
        saveMCommand = new Command(LangHolder.getString(Lang.marks), Command.ITEM, 6);
        gpsCommand = new Command(LangHolder.getString(Lang.gps_on), Command.ITEM, 3);
//    marCommand = new Command(LangHolder.getString(Lang.routes"), Command.ITEM, 4);
//    waypCommand = new Command(LangHolder.getString(Lang.waypoints"), Command.ITEM, 5);
        navCommand = new Command(LangHolder.getString(Lang.navigation), Command.ITEM, 4);
        addrCommand = new Command(LangHolder.getString(Lang.goto_), Command.ITEM, 7);
        loadCommand = new Command(LangHolder.getString(Lang.refresh), Command.ITEM, 9);
        setCommand = new Command(LangHolder.getString(Lang.options), Command.ITEM, 10);
        helpCommand = new Command(LangHolder.getString(Lang.more), Command.ITEM, 8);
        minCommand = new Command(LangHolder.getString(Lang.minimize), Command.ITEM, 19);
        //     gpsStatCommand = new Command(LangHolder.getString(Lang.gpsstat"), Command.ITEM, 13);
//      smsCommand = new Command(LangHolder.getString(Lang.waitsms"), Command.ITEM, 12);
        netRadCommand = new Command("NetRadar "+LangHolder.getString(Lang.on), Command.ITEM, 12);
        exitCommand = new Command(LangHolder.getString(Lang.exit_button), Command.ITEM, 20);
        backCommand = new Command(LangHolder.getString(Lang.cancel), Command.BACK, 10);
        //#debug
//#         dp=3;
                MapMidlet.ulp(34);
        
        if (MNSInfo.propertyEquals("microedition.platform","SXG75")) addCommand(new Command(MapUtil.emptyString, Command.ITEM, 1));
        
        //addCommand(compasCommand);
        addCommand(addrCommand);
        addCommand(helpCommand);
        addCommand(loadCommand);
        addCommand(setCommand);
        if (RMSOption.getBoolOpt(RMSOption.BL_ADDMINIZE))
          addCommand(minCommand);
        if (MNSInfo.bluetoothAvailable()||MNSInfo.comportsAvailable()||MNSInfo.locationAvailable()) addCommand(gpsCommand);
        //#debug
//#         dp=4;
        
        addCommand(saveMCommand);
//    addCommand(marCommand);
        addCommand(navCommand);
        //addCommand(smsCommand);
        addCommand(netRadCommand);
        addCommand(exitCommand);
//    addCommand(waypCommand);
                MapMidlet.ulp(35);
        
      }
      //#debug
//#       dp=5;
      //maxTRunLoad =  (RMSOption.parallelLoad)?2:1;
      //workOnline = RMSOption.onlineMap;
      //#debug
//#      DebugLog.add2Log("PS");
      MapTile.setPicScaling(RMSOption.scaleMap);
            MapMidlet.ulp(36);
      
      //#debug
//#       dp=6;
      try{
      rmss.loadMapMark(marks);
      }catch(Throwable t){
           //#debug
//#            DebugLog.add2Log("Lmm e:"+t);
        }
            MapMidlet.ulp(37);
      
      //#debug
//#       dp=7;
      //zooSent= RMSOption.zooSent;
      //#if SE_K750_E_BASEDEV
      //#else
      startSMSWait();
      //#endif
      
      //#debug
//#       dp=8;
      
      GPSReader.WRITETRACK=RMSOption.getBoolOpt(RMSOption.BL_WRITETRACK);
      GPSReader.RAWDATA=RMSOption.getBoolOpt(RMSOption.BL_RAWDATA);
      
      RMSOption.mapCorrectMode=false;
      //showScale=RMSOption.showScale;
      if (RMSOption.saveMapCorrectMode){
        mapXCorrect=RMSOption.mapDX;
        mapYCorrect=RMSOption.mapDY;
        mapLCorrect=RMSOption.mapDL;
      }
      
      //#debug
//#       dp=9;
      fillSerViewLists(RMSOption.mapVS);
            MapMidlet.ulp(38);
      //#debug
//#       dp=10;
      //showMapSer = (byte)(rmss.getMapServ()&MapTile.SHOW_MASKSERVER);
      //showMapView = (byte)(rmss.getMapServ()&MapTile.SHOW_SURMAP);
      MapCanvas.map.setMapSerView((byte)RMSOption.mapServ);
      //MapCanvas.map.setExternalMapIndex((byte)0);
      MapCanvas.map.setUserMapIndex(RMSOption.getByteOpt(RMSOption.BO_EXTMAPINDEX),false);
      //MapCanvas.map.setInternalMapIndex(externalMapIndex);
      
//      try{
//        if ((externalMapIndex>0)&&(externalMapIndex<=RMSOption.OMAPS_URLS.length)){
//          oml=new OuterMapLoader(RMSOption.OMAPS_URLS[externalMapIndex-1],true);
//        }
//      }catch(Throwable tttt){externalMapIndex=0;}
      
      userMapIndexLabel=userMapIndexUsed;
      //zoom(1);
      //zoom(-1);
      level=RMSOption.level;
      levelDisp=level;
      xCenter=RMSOption.xCenter;
      yCenter=RMSOption.yCenter;
      
      if (RMSOption.OMAPS_NAMES.length==0)
        try{
          InputStream is= this.getClass().getResourceAsStream(MapUtil.MAP_MNO);
          if (is!=null){
            RMSOption.addOMap(LangHolder.getString(Lang.map),MapUtil.MAP_MNO);
            setMapSerView((byte)(MapTile.SHOW_MP+MapTile.SHOW_SUR));
            setUserMapIndex((byte)1,false);
            setUserMapIndex((byte)0,false);
            setUserMapIndex((byte)1,false);
            userMapIndexLabel=userMapIndexUsed;
          }
        }catch(Throwable t){}
      
      
      setSerViewLabel();
      //#debug
//#       dp=11;
      try {
        if (RMSOption.lastWPId>-1) {
          activeRoute=RMSSettings.loadRouteById(RMSOption.lastWPId, MapRoute.WAYPOINTSKIND);
        } else
          if (RMSOption.lastRTId>-1) {
          activeRoute=RMSSettings.loadRouteById(RMSOption.lastRTId, MapRoute.ROUTEKIND);
          }
        
      }catch(Throwable e) {}
      try {
        if (RMSOption.lastTRId>-1) {
          startTrack(RMSSettings.loadRouteById(RMSOption.lastTRId, MapRoute.TRACKKIND));
          activeTrack.recalcMapLevelScreen(null);
        }
      }catch(Throwable e) {}
      //#debug
//#       dp=11;
            MapMidlet.ulp(39);
      try {
        if (RMSOption.trackAutoStart)
          if (activeTrack==null) {
          if ((RMSOption.defTRId>-1)&&(!RMSOption.cleanDefaultTrack))
            startTrack(RMSSettings.loadDefRouteById(RMSOption.defTRId));
          else {
            activeTrack = new MapRoute(MapRoute.TRACKKIND);
            activeTrack.defTrack=true;
            rmss.saveRoute(activeTrack);
          }
          }
      }catch(Throwable e) {}
      
      splitMode=RMSOption.getByteOpt(RMSOption.BO_SPLITMODE);
      
            MapMidlet.ulp(41);
    }catch(Throwable t){
      //#debug
//#       DebugLog.add2Log("MC init:"+dp+" - "+t);
    }
    //  addCommand(compasCommand);
    MapTile.startQueue();
    
    //#if SE_K750_E_BASEDEV
//# //   startDebugTrack();
//#     //startDebugGPS();
//#endif
    
    currModeIndex=RMSOption.currModeIndex;
    currInfoModeIndex=RMSOption.currInfoModeIndex;
    currNavModeIndex=RMSOption.currNavModeIndex;
    currPrfModeIndex=RMSOption.currPrfModeIndex;
    currSpdModeIndex=RMSOption.currSpdModeIndex;
    nextMode();
    prevMode();
    changeInfoMode(true);
    changeInfoMode(false);
    changeNavMode(true);
    changeNavMode(false);
    changePrfMode(true);
    changePrfMode(false);
    changeSpdMode(true);
    changeSpdMode(false);
    
  }
  
  private  Command addrCommand,saveMCommand,gpsCommand,navCommand,netRadCommand,
      loadCommand ,setCommand,helpCommand,exitCommand,backCommand;
  public Command minCommand;
  
//  private BTCanvas gpsCanvas;
  private GPSReader gpsReader;
  public String getGPSTime(){
     if (gpsReader!=null) return gpsReader.satTime();
     else return GPSReader.zeroUTC;
  }
  public String getGPSDate(){
     if (gpsReader!=null) return gpsReader.satDate();
     else return MapUtil.emptyString;
  }
  
  /** Tells if active GPS-receiver present */
  public boolean gpsActive() {
    return (gpsReader!=null);
  }
  public void run(){
    try{
      tiles.removeAllElements();
      MapTile.terminateDownloadQueue();
      setCommandListener(null);
      exitProgress=1;
      try {
        RMSOption.netRadarWasActive=(NetRadar.netRadar!=null);
        endNetRadar();}catch(Throwable e){}
      exitProgress=2;
      repaint();
      serviceRepaints();
      try{
        if (gpsReader!=null){
          try {try{breakBTGPS();}finally{
            endGPSLookup(false);}
          }catch(Throwable e){}
          repaint();
          final long tm=System.currentTimeMillis()+3000;
          try{
            while (GPSReader.notFinished&&(tm>System.currentTimeMillis()))
              Thread.sleep(50);
          }catch(Throwable ttt){}
        }
      }catch(Throwable tt){}
      exitProgress=3;
      repaint();
      serviceRepaints();

      try {endSMSWait();}catch(Throwable e){}
      repaint();
      try{Thread.sleep(100);}catch(Throwable ttt){}
      exitProgress=4;
      try {rmss.writeRMSList(false);}catch(Throwable e){
        showmsgmodal("ERROR","CACHE BREAKDOWN!",AlertType.ERROR,this);
      }
      repaint();
      try{Thread.sleep(100);}catch(Throwable ttt){}
      exitProgress=5;
      RMSOption.level=level;
      RMSOption.xCenter=xCenter;
      RMSOption.yCenter=yCenter;
      RMSOption.mapServ=(byte)(showMapSer+showMapView);
      //RMSOption.onlineMap=workOnline;
      RMSOption.mapDX=mapXCorrect;
      RMSOption.mapDY=mapYCorrect;
      RMSOption.mapDL=mapLCorrect;
      
      RMSOption.currModeIndex=currModeIndex;
      RMSOption.currInfoModeIndex=currInfoModeIndex;
      RMSOption.currNavModeIndex=currNavModeIndex;
      RMSOption.currPrfModeIndex=currPrfModeIndex;
      RMSOption.currSpdModeIndex=currSpdModeIndex;
      
      
      RMSOption.setBoolOpt(RMSOption.BL_WRITETRACK,GPSReader.WRITETRACK);
      RMSOption.setBoolOpt(RMSOption.BL_RAWDATA,GPSReader.RAWDATA);
      
      RMSOption.setByteOpt(RMSOption.BO_SPLITMODE,splitMode);
      
      RMSOption.lastWPId=-1;
      RMSOption.lastRTId=-1;
      RMSOption.lastTRId=-1;
      try{
        if (activeRoute!=null) {
       
          if (activeRoute.kind==MapRoute.WAYPOINTSKIND) RMSOption.lastWPId=activeRoute.rId;
          else
            if (activeRoute.kind==MapRoute.ROUTEKIND) RMSOption.lastRTId=activeRoute.rId;
       
        
        try{ rmss.saveRoute(activeRoute);}catch(Throwable tt){}
        activeRoute=null;
      }
        if (activeTrack!=null)
        {
          boolean defTr =activeTrack.defTrack; 
          if (defTr)
            if (RMSOption.cleanDefaultTrack)
            {
              activeTrack.defTrack=false;
              activeTrack.rId=-1;
            }
          
          try{if ((!defTr)||(activeTrack.pts.size()>0)) rmss.saveRoute(activeTrack);}catch(Throwable tt){}
          
          activeTrack.defTrack=defTr;
          if (activeTrack.kind==MapRoute.TRACKKIND) {
          if (activeTrack.defTrack) RMSOption.defTRId=activeTrack.rId;
          else RMSOption.lastTRId=activeTrack.rId;
          }

        } 
      }catch(Throwable t){}
      endTrack();
      repaint();
      try{Thread.sleep(50);}catch(Throwable ttt){}
      hasPointer= rmss.writeSettingNow();
      if (!hasPointer)
        showmsgmodal("ERROR","REINSTALL!",AlertType.ERROR,this);
      exitProgress=6;
      repaint();
      try{Thread.sleep(50);}catch(Throwable ttt){}
      exitProgress=7;
      repaint();
      serviceRepaints();
      try{Thread.sleep(700);}catch(Throwable ttt){}
    }finally {
      setCurrent(null);
      map=null;
      MapForms.mM.destroyApp(true);
      MapForms.mM.notifyDestroyed();
      MapForms.mM=null;
    }
  }
  private byte exitProgress;
  private boolean dead;
  public void exitMidlet() {
    dead=true;
    repaint();
    (new Thread(this)).start();
  }
  
  private Form exitForm;
  private Command yesCommand,noCommand;
  public void commandAction(Command command, Displayable displayable) {
    if (displayable == exitForm) {
      if (command == yesCommand) {
        setCurrentMap();
        exitMidlet();
      } else
        if (command == noCommand) {
        setCurrentMap();
        exitForm=null;
        yesCommand = null;
        noCommand = null;
        }
    } else
      if (displayable == this) {
      if (command == exitCommand) {
        exitForm = new Form(LangHolder.getString(Lang.exit));
        exitForm.append(new StringItem(LangHolder.getString(Lang.exit)+" ?",""));
        yesCommand = new Command(LangHolder.getString(Lang.yes), Command.OK, 1);
        noCommand = new Command(LangHolder.getString(Lang.no), Command.CANCEL, 2);
        exitForm.addCommand(yesCommand);
        exitForm.addCommand(noCommand);
        exitForm.setCommandListener(this);
        setCurrent(exitForm);
        
      } else if (command == minCommand) {
        setCurrent(null);
        MapForms.mM.notifyPaused();
      } else if (command == addrCommand) {
        setCurrent(MapForms.mm.getHelloForm());
      } else if (command == loadCommand) {
        reloadTiles();
        //  new NetRadar();
      } else if (command == setCommand) {
        MapForms.mm.showSettingForm();
      } else if (command == saveMCommand) {
        //setCurrent(new MarkList(marks,false));
        showSaveMarkForm(false);
      } else if (command == navCommand) {
        MapForms.mm.showNavigateForm();
      } else if (command == helpCommand) {
        MapForms.mm.showMoreForm();
//      } else if (command == marCommand) {
//        bm.showSelectRouteForm();
//      } else if (command == waypCommand) {
//        bm.showSelectWPForm();
      } else if (command == gpsCommand) {
//        if (blueAutoSearch) {endGPSLookup(false);RMSOption.lastBTDeviceName=MapUtil.emptyString; return;}
        if (gpsReader==null) {
          startGPSLookup();
        } else {
          RMSOption.lastBTDeviceName=MapUtil.emptyString;
          RMSOption.changed=true;
          endGPSLookup(false);
        }
      } else if (command == netRadCommand) {
        if (NetRadar.netRadar==null) {
          startNetRadar(false);
        } else {
          endNetRadar();
        }
      }
      } else if (displayable == wptForm) {
      if (command == saveCommand) {
        finishAddPoint();
      } else if (command == backCommand) {
        cancelAddPoint();
      }
      }
  }
  
  public void startNetRadar(boolean oneCall) {
    if (RMSOption.netRadarLogin.equals(MapUtil.emptyString)) {
      MapForms.mm.getListOpt();
      MapForms.mm.getFormOptRadar();
      MapForms.mm.showOptNetRadar();
      setCurrent(MapForms.mm.getFormOptRadar());
      return;
    }
    endNetRadar();
    //if (!oneCall){
    removeCommand(netRadCommand);
    netRadCommand=new Command("NetRadar "+LangHolder.getString(Lang.off), Command.ITEM, 12);
    addCommand(netRadCommand);
    //}
    NetRadar.netRadar = new NetRadar();
    repaint();
  }
  
  public void endNetRadar() {
    if (NetRadar.netRadar!=null) NetRadar.netRadar.stop();
    NetRadar.netRadar = null;
    removeCommand(netRadCommand);
    netRadCommand=new Command("NetRadar "+LangHolder.getString(Lang.on), Command.ITEM, 12);
    addCommand(netRadCommand);
    repaint();
  }
  
  public void endTrack() {
    if (activeTrack!=null) activeTrack.stopPlay();
    activeTrack=null;
    if (mode==TRACKELEVATIONMODE) mode=MAPMODE;
  }
  public void startTrack(MapRoute track) {
    activeTrack=track;
  }
  
  //#if SE_K750_E_BASEDEV
//#   public void startDebugTrack() {
//#     activeTrack = new MapRoute(MapRoute.TRACKKIND);
//#     activeTrack.addMapPoint(new MapPoint(60.001,30,20,1000,30));
//#     activeTrack.addMapPoint(new MapPoint(60.002,30,30,2000,35));
//#     activeTrack.addMapPoint(new MapPoint(60.0025,30,50,2500,50));
//#     activeTrack.addMapPoint(new MapPoint(60.004,30,40,4000,30));
//#     activeTrack.addMapPoint(new MapPoint(60.005,30,45,5000,25));
//#     activeTrack.addMapPoint(new MapPoint(60.006,30,50,6000,30));
//#     activeTrack.addMapPoint(new MapPoint(60.007,30,55,6010,35));
//#     activeTrack.addMapPoint(new MapPoint(60.008,30,50,6050,40));
//#     activeTrack.addMapPoint(new MapPoint(60.009,30,45,7000,50));
//#     activeTrack.addMapPoint(new MapPoint(60.012,30,75,8000,60));
//#   }
//#endif
  
  //#if SE_K750_E_BASEDEV
//#   public void startDebugGPS() {
//#     gpsReader = new GPSReader("",false);
//#     GPSReader.NUM_SATELITES=7;
//#     GPSReader.SPEED_KMH=7;
//#     GPSReader.COURSE=360-25;
//# //    gpsReader.LATITUDE=59.92276;
//# //    gpsReader.LONGITUDE=30.3078;
//#     GPSReader.LATITUDE=60.27217;
//#     GPSReader.LONGITUDE=30.32226;
//#   }
//#endif
  public void breakBTGPS() {
    if (gpsReader!=null) {
      gpsReader.breakConnection();
    }
  }
  MapTimerTask gpsMTT;
  public void endGPSLookup(boolean autoRestart) {
    try{
      if (autoRestart&&(!RMSOption.builtinGPS())) {
        gpsMTT= new MapTimerTask(RMSOption.gpsReconnectDelay);
        timer.schedule(gpsMTT,500,1000);
           //#mdebug
//#       if (RMSOption.debugEnabled)
//#         DebugLog.add2Log("MTT sch");
//#enddebug
        //       startAutoGPSLookup();
      } else {
        if (gpsMTT!=null) {
          gpsMTT.cancel();
          gpsMTT=null;
           //#mdebug
//#           if (RMSOption.debugEnabled)
//#         DebugLog.add2Log("MTT unsch");
//#enddebug
        }
        removeCommand(gpsCommand);
        gpsCommand=new Command(LangHolder.getString(Lang.gps_on), Command.ITEM, 3);
        addCommand(gpsCommand);
      }
      
      try{LocStarter.stop();}catch(Throwable t){}
      try{if (gpsReader!=null) gpsReader.stop();}catch(Throwable t){}
      
      if (!autoRestart) gpsReader=null;
      GPSReader.NUM_SATELITES=0;
      //  removeCommand(gpsStatCommand);
//      blueAutoSearch=false;
      //repaint();
        //#mdebug
//#       if (RMSOption.debugEnabled)
//#         DebugLog.add2Log("end G l");
//#enddebug
    } catch (Throwable e){
//#mdebug
//#       if (RMSOption.debugEnabled)
//#         DebugLog.add2Log("End GPS:"+e);
//#enddebug
    }
  }
  
  private void startGPSLookup() {
//    blueAutoSearch=false;
    if (gpsMTT!=null) {
      gpsMTT.cancel();
      gpsMTT=null;
    }
    
//#debug
//#     int dp=0;
    if (RMSOption.builtinGPS()) {
      try{
        //   addCommand(gpsStatCommand);
//#debug
//#         dp++;
        RMSOption.urlGPS="builtin";
        startGPS();
        
      } catch (Throwable e){
//#mdebug
//#         if (RMSOption.debugEnabled)
//#           DebugLog.add2Log("Blt GPS:"+e+"\n"+String.valueOf(dp));
//#enddebug
//#debug
//#         showmsg(LangHolder.getString(Lang.gps_off),e.toString()+"\n"+String.valueOf(dp),AlertType.ERROR, this);
//    MapMidlet.display.setCurrent(new Alert(LangHolder.getString(Lang.gps_off),e.toString()+"\n"+String.valueOf(dp),null,AlertType.ERROR), this);
      }
    } else
      if (RMSOption.commGPS()) {
      try{
        //   addCommand(gpsStatCommand);
//#debug
//#         dp++;
        RMSOption.urlGPS=RMSOption.getGPSCommUrl();
        startGPS();
        
      } catch (Throwable e){
//#mdebug
//#         if (RMSOption.debugEnabled)
//#           DebugLog.add2Log("Comm GPS:"+e+"\n"+String.valueOf(dp));
//#enddebug
//#debug
//#         showmsg(LangHolder.getString(Lang.gps_off),e.toString()+"\n"+String.valueOf(dp),AlertType.ERROR, this);
//     MapMidlet.display.setCurrent(new Alert(LangHolder.getString(Lang.gps_off),e.toString()+"\n"+String.valueOf(dp),null,AlertType.ERROR), this);
      }
      } else if (RMSOption.bluetoothGPS())
        try{
          //mapFeedback=this;
          MapForms.mm.showBTForm(null,null,false,true);
        } catch (Throwable e){
//#mdebug
//#           if (RMSOption.debugEnabled)
//#             DebugLog.add2Log("Blue GPS:"+e+"\n"+String.valueOf(dp));
//#enddebug
//#debug
//#           showmsg(LangHolder.getString(Lang.gps_off),e.toString()+"\n"+String.valueOf(dp),AlertType.ERROR, this);
          //  MapMidlet.display.setCurrent(new Alert(LangHolder.getString(Lang.gps_off),e.toString()+"\n"+String.valueOf(dp),null,AlertType.ERROR), this);
        }
    
  }
  public void startAutoGPSLookupDelayed() {
    timer.schedule(new MapTimerTask(),2200);
  }
  
  public void startAutoGPSLookup() {
    if (gpsMTT!=null) {
      gpsMTT.cancel();
      gpsMTT=null;
      gpsReader=null;
    }
//#debug
//#     int dp=0;
    if (RMSOption.builtinGPS()) {
      try{
        
//#debug
//#         dp++;
        startGPS();
        
      } catch (Throwable e){
//#mdebug
//#         if (RMSOption.debugEnabled)
//#           DebugLog.add2Log("Blt GPS:"+e+"\n"+String.valueOf(dp));
//#enddebug
//#debug
//#         showmsg(LangHolder.getString(Lang.gps_off),e.toString()+"\n"+String.valueOf(dp),AlertType.ERROR, this);
//       MapMidlet.display.setCurrent(new Alert(LangHolder.getString(Lang.gps_off),e.toString()+"\n"+String.valueOf(dp),null,AlertType.ERROR), this);
      }
    } else
      if (RMSOption.commGPS()) {
      try{
        
//#debug
//#         dp++;
        RMSOption.urlGPS=RMSOption.getGPSCommUrl();
        startGPS();
      } catch (Throwable e){
//#mdebug
//#         if (RMSOption.debugEnabled)
//#           DebugLog.add2Log("Auto com url GPS:"+e+"\n"+String.valueOf(dp));
//#enddebug
//#debug
//#         showmsg(LangHolder.getString(Lang.gps_off)+"!",e.toString()+"\n"+String.valueOf(dp),AlertType.ERROR, this);
//        MapMidlet.display.setCurrent(new Alert(LangHolder.getString(Lang.gps_off)+"!",e.toString()+"\n"+String.valueOf(dp),null,AlertType.ERROR), this);
      }
      } else
        if (RMSOption.bluetoothGPS()) {
//      if (!RMSOption.urlGPS.equals(MapUtil.emptyString)) {
      try {
        //#if !SE_K750_E_BASEDEV
        if (RMSOption.urlGPS!=null)
        //#endif
        {
          
          //#if !SE_K750_E_BASEDEV
          if (!RMSOption.urlGPS.equals(MapUtil.emptyString))
          //#endif
          
          startGPS();
          
          //     //#debug
          //   else if (RMSOption.debugEnabled) DebugLog.add2Log("AB url empty");
          
        }
        
        //  //#debug
        //  else if (RMSOption.debugEnabled) DebugLog.add2Log("AB url null");
        
//#debug
//#         dp++;
        repaint();
      } catch (Throwable e){
//#mdebug
//#         if (RMSOption.debugEnabled)
//#           DebugLog.add2Log("Auto blue url GPS:"+e+"\n"+String.valueOf(dp));
//#enddebug
        //#debug
//#         showmsg(LangHolder.getString(Lang.gps_off)+"!",e.toString()+"\n"+String.valueOf(dp),AlertType.ERROR, this);

      }
//      } else
//        try{
//          LocStarter.showSearchBlueGPS(true);
//          blueAutoSearch=true;
//          dp++;
//          removeCommand(gpsCommand);
//          dp++;
//          gpsCommand=new Command(LangHolder.getString(Lang.gps_off"), Command.ITEM, 5);
//          dp++;
//          addCommand(gpsCommand);
//          dp++;
//          //     addCommand(gpsStatCommand);
//          dp++;
//          repaint();
//        } catch (Throwable e){
////#mdebug
//          if (RMSOption.debugEnabled)
//            DebugLog.add2Log("Auto blue GPS:"+e.toString()+"\n"+String.valueOf(dp));
////#enddebug
//          MapMidlet.display.setCurrent(new Alert(LangHolder.getString(Lang.gps_off")+"!",e.toString()+"\n"+String.valueOf(dp),null,AlertType.ERROR), this);
//        }
//
        }
  }
  
  public void startGPS(){
    gpsBinded=true;
//#debug
//#     int dp=0;
//#debug
//#     try{
//      blueAutoSearch=false;
//      if (RMSOption.bluetoothGPS())
//        if (gpsCanvas!=null) {
////#debug
//        dp=2;
//        RMSOption.lastBTDeviceName=gpsCanvas.btConnect.getSelectedDeviceName();}
//#debug
//#       dp=3;
      try{LocStarter.stop();}catch(Throwable t){}
//#debug
//#       dp=4;
      gpsReader = new GPSReader(RMSOption.urlGPS, RMSOption.bluetoothGPS()|RMSOption.commGPS());

       //#mdebug
//#       if (RMSOption.debugEnabled)
//#         DebugLog.add2Log("gpsR created");
//#enddebug
//#debug
//#       dp=5;
      try{
        if (RMSOption.builtinGPS()) {
//#debug
//#           dp=6;
          LocStarter.start(gpsReader);
          RMSOption.lastBTDeviceName="internal";
        }
      }catch(Throwable ttr){
//#mdebug
//#         if (RMSOption.debugEnabled)
//#           DebugLog.add2Log("sGPS1x:"+ttr+" Con T:"+RMSOption.connGPSType+" DP:"+dp);
//#enddebug
      }
//#debug
//#       dp=7;
      
      removeCommand(gpsCommand);
      //  dp++;
//#debug
//#       dp=8;
      gpsCommand=new Command(LangHolder.getString(Lang.gps_off), Command.ITEM, 5);
      //  dp++;
//#debug
//#       dp=9;
      addCommand(gpsCommand);
//#debug
//#       dp=10;
      if (autoShowMap)
      MapForms.mm.back2Map();
//#debug
//#       dp=11;
      
      repaint();
//#mdebug
//#     }catch(Throwable tt){
//#       if (RMSOption.debugEnabled)
//#         DebugLog.add2Log("sGPS2y:"+tt+" Con T:"+RMSOption.connGPSType+" DP:"+dp);
//#     }
//#enddebug
    
  }
  
//  public void addMapMark(String nm) {
//    MapMark mm=new MapMark(MapUtil.getLat(yCenter, level), MapUtil.getLon(xCenter, level), level, nm, 0);
//    rmss.saveMapMark(mm);
//    rmss.loadMapMark(marks);
//  }
//  
  public void addMapMark(String nm,double lat,double lon, int lvl) {
    MapMark mm=new MapMark(lat, lon, lvl, nm, 0);
    rmss.saveMapMark(mm);
    rmss.loadMapMark(marks);
  }
  
//  public void delMapMark(MapMark mm) {
//    rmss.deleteMapMark(mm);
//    rmss.loadMapMark(marks);
//  }
  public void deleteAllMarks() {
    rmss.deleteMapMarks();
    rmss.loadMapMark(marks);
  }
//  
  public static boolean gpsBinded;
//private boolean modeCompass=false;
//private boolean modeHeightWay=false;
  public final static byte MAPMODE = 1;
  public final static byte COMPASSMODE = 2;
  public final static byte TRACKELEVATIONMODE = 3;
  public final static byte POSITIONMODE = 4;
  public final static byte STATMODE = 5;
  public final static byte SPEEDMODE = 6;
  //public final static byte TRAVELMODE = 7;
  public final static byte NAVMODE = 8;
  public final static byte SATMODE = 9;
  public final static byte VARMODE = 10;
  public static byte mode = MAPMODE;
  private static final byte[] modes = {MAPMODE,COMPASSMODE,TRACKELEVATIONMODE,POSITIONMODE,SATMODE,STATMODE,SPEEDMODE,VARMODE,NAVMODE};
  private static int currModeIndex;
  private void nextMode() {
//    extMenu=false;
 //   routeMenu=false;
    currModeIndex=currModeIndex>modes.length-2?0:currModeIndex+1;
    mode=modes[currModeIndex];
  }
  private void prevMode() {
  //  extMenu=false;
  //  routeMenu=false;
    currModeIndex=currModeIndex>0?currModeIndex-1:modes.length-1;
    mode=modes[currModeIndex];
  }
  
  private void changeView(boolean next){
    if (mode==TRACKELEVATIONMODE) changePrfMode(next);
    else if (mode==POSITIONMODE) changePosMode(next);
    else if (mode==SATMODE) changeSatMode(next);
    else if (mode==STATMODE) changeInfoMode(next);
    else if (mode==SPEEDMODE) changeSpdMode(next);
    else if (mode==NAVMODE) changeNavMode(next);
  }
  public final static byte TRACKINFOMODE = 0;
  public final static byte TRAVELINFOMODE = 1;
  public final static byte SPEEDINFOMODE = 2;
  public final static byte ACCELINFOMODE = 3;
  public final static byte ALTINFOMODE = 4;
  public final static byte ALTGAINMODE = 5;
  public final static byte ALTSPEEDINFOMODE = 6;
  public final static byte SYSTEMINFOMODE = 7;
  public final static byte TRACKWRITEINFOMODE = 8;
  public final static byte NETRADARINFOMODE = 9;
  public final static byte SUNINFOMODE = 10;
  public static byte infoMode = TRACKINFOMODE;
  private static final byte[] infoModes = {TRACKINFOMODE,TRAVELINFOMODE,SPEEDINFOMODE,ACCELINFOMODE,ALTINFOMODE,ALTGAINMODE,ALTSPEEDINFOMODE,SYSTEMINFOMODE,TRACKWRITEINFOMODE,NETRADARINFOMODE, SUNINFOMODE};
  private static final short[] infoModeNames = {Lang.track,Lang.travel,Lang.speed,Lang.curA,Lang.altitude,Lang.altgain,Lang.curAS,Lang.system,Lang.trackopt,-1,Lang.sun};
  private static int currInfoModeIndex;
  
  private void changeInfoMode(boolean next ) {
    if (next)
      currInfoModeIndex=currInfoModeIndex>infoModes.length-2?0:currInfoModeIndex+1;
    else
      currInfoModeIndex=currInfoModeIndex>0?currInfoModeIndex-1:infoModes.length-1;
    infoMode=infoModes[currInfoModeIndex];
  }
  
  private final static byte NAVALTMODE = 0;
  private final static byte NAVFULLMODE = 1;
  private final static byte NAVWPTMODE = 2;
  private final static byte NAVHSIMODE = 3;
  public static byte navMode = NAVALTMODE;
  private static final byte[] navModes = {NAVALTMODE,NAVFULLMODE,NAVHSIMODE,NAVWPTMODE};
  private static int currNavModeIndex;
  
  private void changeNavMode(boolean next ) {
    if (next)
      currNavModeIndex=currNavModeIndex>navModes.length-2?0:currNavModeIndex+1;
    else
      currNavModeIndex=currNavModeIndex>0?currNavModeIndex-1:navModes.length-1;
    navMode=navModes[currNavModeIndex];
  }
  
  public final static byte PRFALTMODE = 1;
  public final static byte PRFSPDMODE = 2;
  public final static byte PRFALTTIMEMODE = 3;
  public final static byte PRFSPDTIMEMODE = 4;
  public static byte prfMode = PRFALTMODE;
  private static final byte[] prfModes = {PRFALTMODE,PRFALTTIMEMODE,PRFSPDMODE,PRFSPDTIMEMODE};
  private static int currPrfModeIndex;
  private boolean prfVertLines=true;
  
  private void changePrfMode(boolean next ) {
    if (next)
      currPrfModeIndex=currPrfModeIndex>prfModes.length-2?0:currPrfModeIndex+1;
    else
      currPrfModeIndex=currPrfModeIndex>0?currPrfModeIndex-1:prfModes.length-1;
    prfMode=prfModes[currPrfModeIndex];
  }
  
  private final static byte SPDANALOGMODE = 1;
  private final static byte SPDDIGITMODE = 2;
  private final static byte SPDDIGFLMODE = 3;
  public static byte spdMode = SPDANALOGMODE;
  private static final byte[] spdModes = {SPDANALOGMODE,SPDDIGITMODE,SPDDIGFLMODE,};
  private static int currSpdModeIndex;
  
  private void changeSpdMode(boolean next ) {
    if (next)
      currSpdModeIndex=currSpdModeIndex>spdModes.length-2?0:currSpdModeIndex+1;
    else
      currSpdModeIndex=currSpdModeIndex>0?currSpdModeIndex-1:spdModes.length-1;
    spdMode=spdModes[currSpdModeIndex];
  }
  
  
  private final static byte POSCOORDMODE = 0;
  private final static byte POSPDOPMODE = 1;
  private final static byte POSTIMEMODE = 2;
  private final static byte POSUTCMODE = 3;
  public static byte posMode = POSCOORDMODE;
  private static final byte[] posModes = {POSCOORDMODE,POSPDOPMODE,POSTIMEMODE,POSUTCMODE};
  private static int currPosModeIndex;
  
  private void changePosMode(boolean next ) {
    if (next)
      currPosModeIndex=currPosModeIndex>posModes.length-2?0:currPosModeIndex+1;
    else
      currPosModeIndex=currPosModeIndex>0?currPosModeIndex-1:posModes.length-1;
    posMode=posModes[currPosModeIndex];
  }
  
  
  public final static byte SATSIGNALMODE = 0;
  public final static byte SATNUMBERMODE = 1;
  public static byte satMode = SATSIGNALMODE;
  private static final byte[] satModes = {SATSIGNALMODE,SATNUMBERMODE};
  private static int currSatModeIndex;
  
  private void changeSatMode(boolean next ) {
    if (next)
      currSatModeIndex=currSatModeIndex>satModes.length-2?0:currSatModeIndex+1;
    else
      currSatModeIndex=currSatModeIndex>0?currSatModeIndex-1:satModes.length-1;
    satMode=satModes[currSatModeIndex];
  }
  
 
  private void drawHeightWay(Graphics g,int to) {
    if (activeTrack!=null) activeTrack.drawHY(g, prfMode,prfVertLines,to);
  }
  
  public boolean isOnScreen(int x,int y) {
    if ((x>dmaxx)||(x<dminx)) return false;
    if ((y>dmaxy)||(y<dminy)) return false;
    return true;
  }
  
  public static boolean isOn2Screen(int x,int y) {
    if ((x-(dmaxx-dminx)>dmaxx)||(x+(dmaxx-dminx)<dminx)) return false;
    if ((y-(dmaxy-dminy)>dmaxy)||(y+(dmaxy-dminy)<dminy)) return false;
    return true;
  }
  
  public static int dminx;
  public static int dminy;
  public static int dmaxx;
  public static int dmaxy;
  public static int dcx ;
  public static int dcy ;
  public static double reallat,reallon;
  
  public static boolean drawKeyLockSign;
  
  private byte speedCoeff=1;
//#debug
//#   private static double tracePos=0;
  static int mapXCorrect;
  static int mapYCorrect;
  static short mapLCorrect;
  public boolean updateSize=true;
  
  static double coursPaint,coursradPaint;
  
  int[] ARGB_Img0;
  int[] ARGB_Img1;
  Image imgR;
  
  public static boolean rotateMap;

  boolean inverseMap;
  boolean doubleZoomMap;

  private void checkImgArrays(int minElementsCount){
   
    if (ARGB_Img0==null) {
      try{
                   ARGB_Img0 = new int[minElementsCount];
                    ARGB_Img1= new int[minElementsCount];
      }catch(Throwable t){
        ARGB_Img0=null;
        ARGB_Img1=null;
        throw new OutOfMemoryError("cIA: "+minElementsCount+" : " +t);
      }
                  } else
                    if (ARGB_Img0.length<(minElementsCount)) {
      try{
                    ARGB_Img0=null;ARGB_Img1=null;
                   ARGB_Img0 = new int[minElementsCount];
                    ARGB_Img1= new int[minElementsCount];
      }catch(Throwable t){
        ARGB_Img0=null;
        ARGB_Img1=null;
        throw new OutOfMemoryError("cIA: "+minElementsCount+" : " +t);
      }
                    }
  }
  private void freeImgArrays(){
    if ((!rotateMap)&&(!inverseMap)&&(!doubleZoomMap)){
        ARGB_Img0=null;
        ARGB_Img1=null; 
    }
  }
  //public boolean dblScaleMap;
  private boolean compassOverMap;
  
  Image imgLand;
  public boolean landscapeMap;
  
  //#if SE_K750_E_BASEDEV
//#   int paintCount;
  //#endif
  
  protected void sizeChanged(int w, int h){
    updateSize=true;
  }
  
  private void drawExit(Graphics g) {
    g.setColor(0x000000);
    g.fillRect(dminx, dminy, dmaxx, dmaxy);
    g.setColor(0xFFFFFF);
    
    g.setFont(FontUtil.SMALLFONTB);
    int fh = g.getFont().getHeight();
    g.drawString("Exiting...",0,0,Graphics.TOP|Graphics.LEFT);
    if (exitProgress>1)
      g.drawString("NetRadar closed.",0,fh,Graphics.TOP|Graphics.LEFT);
    if (exitProgress>2)
      g.drawString("GPS closing...",0,fh+fh,Graphics.TOP|Graphics.LEFT);
    if (exitProgress>3)
      g.drawString("GPS closed.",0,fh+fh+fh,Graphics.TOP|Graphics.LEFT);
    if (exitProgress>4)
      g.drawString("Cache flushed.",0,fh*4,Graphics.TOP|Graphics.LEFT);
    if (exitProgress>5)
      g.drawString("Track saved.",0,fh*5,Graphics.TOP|Graphics.LEFT);
    if (exitProgress>6){
      String s = (hasPointer)?"Success":"Fail";
      g.drawString("Options saved: "+s,0,fh*6,Graphics.TOP|Graphics.LEFT);
      g.drawString("Bye Bye...",0,fh*7,Graphics.TOP|Graphics.LEFT);
    }
  }
  
  Image winImage;
  private void setDrawSizes(boolean landMode){
    dminx = 0;//g.getClipX();
    dminy = 0;//g.getClipY();
    if (landMode){
      dmaxy = getWidth();//g.getClipWidth();
      dmaxx = getHeight();//g.getClipHeight();
    }else {
      dmaxx = getWidth();//g.getClipWidth();
      dmaxy = getHeight();//g.getClipHeight();
    }
    dcx = (dminx+dmaxx)/2;
    dcy = (dminy+dmaxy)/2;
  }
  public final void paint(Graphics g) {
      setBlinking();
    //#if SE_K750_E_BASEDEV
//#     paintCount++;
    //#endif
    if (dead) {
      drawExit(g);
      return;
    }
    freeImgArrays();
    Graphics savedG=null;
    boolean landMode=landscapeMap;
    
    if (landMode){
      if (imgLand==null){
        imgLand=Image.createImage(getHeight(),getWidth());
      }
      savedG=g;
      g=imgLand.getGraphics();
    } else imgLand=null;
    
    
    if (updateSize) {
      setDrawSizes(landMode);
      updateSize=false;
    }

    pointerActive = (System.currentTimeMillis()- pointerActivatedTime<5000);

    Graphics wg=null;
    if (splitMode>0){
      try{
        if (winImage!=null)
          if (splitMode<=2){
          if (winImage.getWidth()!=dmaxx) winImage=null;
          } else {
          if (winImage.getWidth()!=dmaxx/2) winImage=null;
          }
        if (winImage==null)
          winImage =(splitMode<=2)?Image.createImage(dmaxx,dmaxy/2):Image.createImage(dmaxx/2,dmaxy/2);
        wg = winImage.getGraphics();
      }catch(Throwable t){
        winImage=null;wg=null;splitMode=0;
      }
    } else if (inverseMap||doubleZoomMap) {
      try{
        if (winImage!=null)
          if (winImage.getWidth()!=dmaxx) winImage=null;
        
        if ((winImage==null))
          winImage =Image.createImage(dmaxx,dmaxy);
        wg = winImage.getGraphics();
      }catch (Throwable t){
        winImage =null;
        wg=null;
      }
    }
    int ImW=0,ImH=0;
    if ((inverseMap||doubleZoomMap)&&(winImage!=null)){
      //Определяем размер исходной картинки
      ImW=winImage.getWidth();     //Ширина
      ImH=winImage.getHeight();     //Высота
      //Создаем два ARGB массива
      try{
      checkImgArrays(ImW*ImH);
      }catch(Throwable t){
        winImage=null;
        inverseMap=false;
        doubleZoomMap=false;
        //#mdebug
//#         if (RMSOption.debugEnabled)
//#           DebugLog.add2Log("InvDblM :"+t);
        //#enddebug
      }
    }
    
    if (splitMode==0){
      if(!(inverseMap||doubleZoomMap)||(mode!=MAPMODE)||(winImage==null)) {
        if (!(inverseMap||doubleZoomMap)) winImage=null;
        paintWindow(g);
      } else {
        paintWindow(wg);
        winImage.getRGB(ARGB_Img0,0,ImW,0,0,ImW,ImH);
        if (inverseMap)
          MapUtil.ARGB_Img_Invert(ARGB_Img0);
        if (doubleZoomMap){
          MapUtil.ARGB_Img_DblScale(ARGB_Img0, ARGB_Img1, ImW, ImH);
        }
        
        if (doubleZoomMap)
        wg.drawRGB(ARGB_Img1,0,ImW,0,0,ImW,ImH,false);
         else
        wg.drawRGB(ARGB_Img0,0,ImW,0,0,ImW,ImH,false);
          
        g.drawImage(winImage,0,0,Graphics.TOP|Graphics.LEFT);
      }
    } else if (splitMode==2) {
      byte sMode = mode;
      int sdmaxy=dmaxy;
      //mode = MAPMODE;
      dmaxy=dmaxy/2;
      dcy = (dminy+dmaxy)/2;
      dcx = (dminx+dmaxx)/2;
      paintWindow(wg);
      g.drawImage(winImage,0,0,Graphics.TOP|Graphics.LEFT);
      mode = modes[RMSOption.getByteOpt(RMSOption.BO_VIEW2)];
      setDrawSizes(landMode);
      dmaxy=dmaxy/2;
      dmaxx=dmaxx/2;
      dcy = (dminy+dmaxy)/2;
      dcx = (dminx+dmaxx)/2;
      paintWindow(wg);
      g.drawImage(winImage,0,dmaxy,Graphics.TOP|Graphics.LEFT);
      
      mode = modes[RMSOption.getByteOpt(RMSOption.BO_VIEW3)];
      setDrawSizes(landMode);
      dmaxy=dmaxy/2;
      dmaxx=dmaxx/2;
      dcy = (dminy+dmaxy)/2;
      dcx = (dminx+dmaxx)/2;
      paintWindow(wg);
      g.drawImage(winImage,dmaxx,dmaxy,Graphics.TOP|Graphics.LEFT);
      
      mode=sMode;
      setDrawSizes(landMode);
    } else if (splitMode==3) {
      byte sMode = mode;
      int sdmaxy=dmaxy;
      //mode = MAPMODE;
      dmaxy=dmaxy/2;
      dmaxx=dmaxx/2;
      dcy = (dminy+dmaxy)/2;
      dcx = (dminx+dmaxx)/2;
      paintWindow(wg);
      g.drawImage(winImage,0,0,Graphics.TOP|Graphics.LEFT);
      mode = modes[RMSOption.getByteOpt(RMSOption.BO_VIEW2)];
      setDrawSizes(landMode);
      dmaxy=dmaxy/2;
      dmaxx=dmaxx/2;
      dcy = (dminy+dmaxy)/2;
      dcx = (dminx+dmaxx)/2;
      paintWindow(wg);
      g.drawImage(winImage,dmaxx,0,Graphics.TOP|Graphics.LEFT);
      
      mode = modes[RMSOption.getByteOpt(RMSOption.BO_VIEW3)];
      setDrawSizes(landMode);
      dmaxy=dmaxy/2;
      dmaxx=dmaxx/2;
      dcy = (dminy+dmaxy)/2;
      dcx = (dminx+dmaxx)/2;
      paintWindow(wg);
      g.drawImage(winImage,0,dmaxy,Graphics.TOP|Graphics.LEFT);
      
      mode = modes[RMSOption.getByteOpt(RMSOption.BO_VIEW4)];
      setDrawSizes(landMode);
      dmaxy=dmaxy/2;
      dmaxx=dmaxx/2;
      dcy = (dminy+dmaxy)/2;
      dcx = (dminx+dmaxx)/2;
      paintWindow(wg);
      g.drawImage(winImage,dmaxx,dmaxy,Graphics.TOP|Graphics.LEFT);
      
      mode=sMode;
      setDrawSizes(landMode);
    } else if (splitMode==1) {
      byte sMode = mode;
      int sdmaxy=dmaxy;
      dmaxy=dmaxy/2;
      dcy = (dminy+dmaxy)/2;
      paintWindow(wg);
      g.drawImage(winImage,0,0,Graphics.TOP|Graphics.LEFT);
      mode = modes[RMSOption.getByteOpt(RMSOption.BO_VIEW2)];
      setDrawSizes(landMode);
      dmaxy=dmaxy/2;
      dcx = (dminx+dmaxx)/2;
      dcy = (dminy+dmaxy)/2;
      
      paintWindow(wg);
      g.drawImage(winImage,0,dmaxy,Graphics.TOP|Graphics.LEFT);
      mode=sMode;
      setDrawSizes(landMode);
    }
    
    int fh = FontUtil.MEDIUMFONT.getHeight();
    if ((mode==MAPMODE)&&(hasPointer)&&(pointerActive)) {
      int bs = fh*5/3;
      scaleBoxYP=bs*2;
      scaleBoxYM=bs*2;
      scaleBoxH=bs;
      mapBoxYM=bs*3;
      mapBoxYS=bs*3;
      minBoxY=bs*4;
      bindBoxY=bs*4;
      menuBoxY=bs*5;
      rotBoxY=bs*5;
      
      scaleBoxXP=dmaxx-bs-2;
      scaleBoxXM=dmaxx-bs-2-bs-2;
      
      mapBoxXM=dmaxx-bs-2;
      mapBoxXS=dmaxx-bs-2-bs-2;
      
      minBoxX=dmaxx-bs-2;
      bindBoxX=dmaxx-bs-2-bs-2;
      menuBoxX=minBoxX;
      rotBoxX=bindBoxX;
      
      g.setColor(RMSOption.shadowColor);
      paintScaleBox(g,scaleBoxXP+1,scaleBoxYP+1,bs,DRAW_PLUS);
      paintScaleBox(g,scaleBoxXM+1,scaleBoxYM+1,bs,DRAW_MINUS);
      paintScaleBox(g,mapBoxXM+1,mapBoxYM+1,bs,DRAW_SERVMAP);
      paintScaleBox(g,mapBoxXS+1,mapBoxYS+1,bs,DRAW_SERV);
      paintScaleBox(g,minBoxX+1,minBoxY+1,bs,DRAW_MINM);
      paintScaleBox(g,bindBoxX+1,bindBoxY+1,bs,DRAW_BIND);
      paintScaleBox(g,menuBoxX+1,menuBoxY+1,bs,DRAW_MENU);
      paintScaleBox(g,rotBoxX+1,rotBoxY+1,bs,DRAW_ROT);
      g.setColor(RMSOption.foreColor);
      paintScaleBox(g,scaleBoxXP,scaleBoxYP,bs,DRAW_PLUS);
      paintScaleBox(g,scaleBoxXM,scaleBoxYM,bs,DRAW_MINUS);
      paintScaleBox(g,mapBoxXM,mapBoxYM,bs,DRAW_SERVMAP);
      paintScaleBox(g,mapBoxXS,mapBoxYS,bs,DRAW_SERV);
      paintScaleBox(g,minBoxX,minBoxY,bs,DRAW_MINM);
      paintScaleBox(g,bindBoxX,bindBoxY,bs,DRAW_BIND);
      paintScaleBox(g,menuBoxX,menuBoxY,bs,DRAW_MENU);
      paintScaleBox(g,rotBoxX,rotBoxY,bs,DRAW_ROT);
    }
    
//#debug
//#     tracePos=6;
    
    
    if (mapSend!=null)
      if (mapSend.sending) {
      g.setColor(0x0);
      g.drawString(LangHolder.getString(Lang.mapsending), dcx+1, dcy+1, Graphics.TOP|Graphics.HCENTER );
      g.setColor(0xA0FF20);
      g.drawString(LangHolder.getString(Lang.mapsending), dcx, dcy, Graphics.TOP|Graphics.HCENTER );
      }
    
    if (gpsMTT!=null) {
      g.setFont(FontUtil.MEDIUMFONT);
      fh= FontUtil.MEDIUMFONT.getHeight();
      g.setColor(0);
      g.fillRect(0,dcy-fh-fh-fh-fh,dmaxx,fh+fh);
      g.setColor(0xFF3020);
      g.drawString(LangHolder.getString(Lang.gpsautoconn), dcx, dcy-fh-fh-fh, Graphics.BOTTOM|Graphics.HCENTER);
      g.drawString(MapUtil.time2String(gpsMTT.left2Reconnect), dcx, dcy-fh-fh, Graphics.BOTTOM|Graphics.HCENTER);
    }
    
    if (inetAvailable) {
      runInetUpdates();
    }
    
    
    if (canvasMenu!=null)
      canvasMenu.drawMenu(g);
    
    if (drawKeyLockSign) {
      int xl=dmaxx/4,xr=3*dmaxx/4,yt=2*dmaxy/3;
      int xs=(xr-xl)/9;
      int ys=xs;
      g.setColor(0xFFFF30);
      g.fillRect(xl,yt+ys,xs*6,ys);
      g.fillRect(xl,yt+ys,xs,ys+ys+ys);
      g.fillRect(xl+xs+xs,yt+ys,xs,ys+ys);
      g.fillRect(xl+xs*6,yt-ys,xs*3,ys);
      g.fillRect(xl+xs*6,yt+xs*3,xs*3,ys);
      g.fillRect(xl+xs*6,yt-ys,xs,ys*4);
      g.fillRect(xl+xs*8,yt-ys,xs,ys*4);
      if (!keyLocked) {
        g.setColor(0xFF0000);
        g.drawLine(xl-xs,yt+xs*4,xl+xs*10,yt-ys);
        g.drawLine(xl-xs,yt+xs*4-1,xl+xs*10,yt-ys-1);
        g.drawLine(xl-xs,yt+xs*4-2,xl+xs*10,yt-ys-2);
      }
    }
    
    if (landMode){
      g=savedG;
      try{
        g.drawRegion(imgLand,0,0,getHeight(),getWidth(),Sprite.TRANS_ROT90,0,0,Graphics.LEFT|Graphics.TOP);
      }catch(Throwable t){
        int mind=dmaxx<dmaxy?dmaxx:dmaxy;
        g.drawRegion(imgLand,0,0,mind,mind,Sprite.TRANS_ROT90,0,0,Graphics.LEFT|Graphics.TOP);
      }
    }
    
  }

  private final static int COLOR_HSI_ROUTE_DIRECTION=0x8050FF;

  //int altdebug;
  private byte splitMode;
  public final void paintWindow(Graphics g) {
    //dblScaleMap= RMSOption.doubleScaleMap;
    rotateMap = RMSOption.mapRotate&&(gpsReader!=null)&&(gpsBinded);
    //boolean rotateReal = rotateMap;
    
    compassOverMap = RMSOption.compassOverMap&&(gpsReader!=null);
    try {
      
      g.setColor(0x000000);
      g.fillRect(dminx, dminy, dmaxx, dmaxy);
      
      Font stdFont=null,smallFont= FontUtil.SMALLFONT;
      if (splitMode>0) stdFont = FontUtil.SMALLFONT;
      else
        if ((mode==SPEEDMODE)||(mode==POSITIONMODE)) {
        stdFont = FontUtil.LARGEFONTB;
        } else
          if (mode==NAVMODE||(mode==STATMODE)){

        stdFont = FontUtil.MEDIUMFONT;
          } else
            if (RMSOption.fontSize==0) {
        if (RMSOption.fontStyle==1) {
          stdFont = FontUtil.LARGEFONTB;
        } else {
          stdFont = FontUtil.LARGEFONT;
        }
            } else if (RMSOption.fontSize==2) {
        if (RMSOption.fontStyle==1) {
          stdFont = FontUtil.SMALLFONTB;
        } else {
          stdFont = FontUtil.SMALLFONT;
        }
            } else  {
        if (RMSOption.fontStyle==1) {
          stdFont = FontUtil.MEDIUMFONTB;
        } else {
          stdFont = FontUtil.MEDIUMFONT;
        }
            }
      
      if (stdFont!=null) g.setFont(stdFont);
      
      //fnt=null;
//#debug
//#       tracePos=1;
      
      //g.setColor(0xC0C0C0);
      //g.drawString("Рисуем карту", dcx-40, dcy-5, Graphics.TOP|Graphics.LEFT);
      
      int gpsx=0,gpsy=0;
      boolean gpsA=false;
      gpsx=dcx;
      gpsy=dcy;
      
      if (rotateMap) gpsBinded=true;
      
      if (gpsReader!=null)
        if (GPSReader.NUM_SATELITES>1) {
        coursPaint=GPSReader.COURSE;
        coursradPaint=coursPaint*MapUtil.G2R;
        
        if (mode==MAPMODE) {
          if (gpsBinded) {
            int dx=0,dy=0;
            if (!rotateMap){
             
             //float sc = (GPSReader.SPEED_KMH<40)?((GPSReader.SPEED_KMH>10)?(GPSReader.SPEED_KMH-10)/15:0):2; 
             //float pp=((dmaxx<dmaxy)?dmaxx:dmaxy)*sc/7; 
             float pp=((dmaxx<dmaxy)?dmaxx:dmaxy)*((GPSReader.SPEED_KMH<40)?((GPSReader.SPEED_KMH>10)?(GPSReader.SPEED_KMH-10)/15:0):2)/7; 
             dx = (int)(-pp*Math.sin(GPSReader.COURSE*MapUtil.G2R));
             dy = (int)(-pp*Math.cos(GPSReader.COURSE*MapUtil.G2R));
            //dx=(int)cx;
            //dy=(int)cy;
            }
            xCenter=MapUtil.getXMap(GPSReader.LONGITUDE, level)-dx;
            yCenter=MapUtil.getYMap(GPSReader.LATITUDE, level)+dy;
            gpsx=dcx+dx;
            gpsy=dcy-dy;
          } else {
            gpsx=MapUtil.getXMap(GPSReader.LONGITUDE, level)-xCenter+dcx;
            gpsy=MapUtil.getYMap(GPSReader.LATITUDE, level)-yCenter+dcy;
          }
          if (rotateMap||(!compassOverMap)) {
            coursradPaint=coursPaint*MapUtil.G2R;
          } else {
            coursradPaint=0;
          }
          
        }
        gpsA=true;
        }
      
//#debug
//#       tracePos=1.93;
      
      if (NetRadar.netRadar!=null)
        if (NetRadar.netRadar.bind) {
        gpsBinded=true;
        NetRadarUser nru = NetRadar.netRadar.getNav2User();
        if (nru!=null){
          xCenter=MapUtil.getXMap(nru.lon, level);
          yCenter=MapUtil.getYMap(nru.lat, level);
        }
        }
//#debug
//#       tracePos=2;
      
      double crsr,acrsr,ca,cb,dist;
      int sr,sr1;
      int fh=g.getFont().getHeight();
      String s=MapUtil.emptyString,s1=null,s2=null;
      
//#debug
//#       tracePos=2.1;
      //if (gpsReader==null)
      reallat=MapUtil.getLat(yCenter, level);
      // else
      //   reallat=GPSReader.LATITUDE;
//#debug
//#       tracePos=2.2;
      reallon=MapUtil.getLon(xCenter, level);
      //else
      //  reallon=GPSReader.LONGITUDE;
      byte rr =10;
//#debug
//#       tracePos=2.3;
      
      int to = (splitMode==0)?fh:1;
      if (mode==NAVMODE) {
        drawScreenCaption(Lang.navigation,g, fh, rr,currNavModeIndex,navModes.length);

        g.setColor(0xFFFF00);
        
//#debug
//#         tracePos=2.301;
        boolean ready2Nav = activeRoute!=null;
        if (ready2Nav) ready2Nav=(activeRoute.kind==MapRoute.ROUTEKIND);
        if (ready2Nav) ready2Nav=(activeRoute.pts.size()>1);
        if (!ready2Nav){
          g.drawString(LangHolder.getString(Lang.route)+'-'+LangHolder.getString(Lang.nosel)+'!',dcx,fh+fh+fh,Graphics.TOP|Graphics.HCENTER);
        } else
          if ((navMode==NAVALTMODE)||(navMode==NAVFULLMODE)||(navMode==NAVHSIMODE)){
          int cd=0;
          if (navMode==NAVALTMODE){
            if (dmaxx+10<dmaxy)
              cd = (dmaxx-1)/3;
            else cd = (dmaxy-1-to)/3;
          } else if ((navMode==NAVFULLMODE)||(navMode==NAVHSIMODE)) {
            if (dmaxx+10<dmaxy)
              cd = (dmaxx-to)/2;
            else
              cd = (dmaxy-to-to)/2;
          }
          sr=cd/2;// speedmeter radius
          int ps=cd/10;
          gpsx=cd+cd+sr;
          gpsy=(int)(cd+to+1);
          int alteh=(int)(fh*1.5f);
          int alth=(alteh*4);
          int altint=(RMSOption.unitFormat==RMSOption.UNITS_METRIC)?100:1000;
          if (navMode==NAVALTMODE){
            //---------------draw altimeter----------------------------
            g.fillTriangle(cd+cd+ps,gpsy+alth/2,
                cd+cd,gpsy+alth/2-ps,
                cd+cd,gpsy+alth/2+ps);
            
            if (GPSReader.ALTSPEED_MS>1.5){
              g.fillTriangle(cd+cd+ps/2,gpsy+alth/2-sr,
                  cd+cd,gpsy+alth/2-sr+ps,
                  cd+cd+ps,gpsy+alth/2-sr+ps);
              g.fillRect(cd+cd+ps/4,gpsy+alth/2-sr+ps,
                  ps/2,sr-ps-ps-ps);
            } else if (GPSReader.ALTSPEED_MS<-1.5){
              g.fillTriangle(cd+cd+ps/2,gpsy+alth/2+sr,
                  cd+cd,gpsy+alth/2+sr-ps,
                  cd+cd+ps,gpsy+alth/2+sr-ps);
              g.fillRect(cd+cd+ps/4,gpsy+alth/2+ps+ps,
                  ps/2,sr-ps-ps-ps);
            }
            
            //-------
            //altdebug+=50;
            //GPSReader.ALTITUDE=altdebug;
            
//#debug
//#             tracePos=2.302;
            int fAlt=(int)((RMSOption.unitFormat==RMSOption.UNITS_METRIC)?GPSReader.ALTITUDE:((RMSOption.unitFormat==RMSOption.UNITS_NAUTICAL)||(RMSOption.unitFormat==RMSOption.UNITS_IMPERIAL))?GPSReader.ALTITUDE*3.281f:0);
            int fMaxAlt=fAlt-(fAlt % altint)+altint*3;
            int fMinAlt=fAlt-(fAlt % altint)-altint*2;
            int altey = (int)(((float)(fAlt % altint))/(float)altint*(float)alteh);
            //int altey=(int)(((float)alteh/(fAlt-(int)(fAlt*.01)))*100);
            g.clipRect(cd+cd+ps,(int)(gpsy+1+ps*1.2),cd,(int)(alth-ps*1.2));
            
            for(int aa=fMinAlt;aa<fMaxAlt;aa+=altint){
              int ly=altey+(int)(gpsy+alth-alteh*(aa-fMinAlt)/(float)altint);
              g.setColor(0xFFFFFF);
              g.drawString(String.valueOf(aa),
                  cd+cd+ps+ps,ly,Graphics.BOTTOM|Graphics.LEFT);
              g.setColor(0x66CCFF);
              g.drawLine(cd+cd+ps, ly, cd+cd+cd,ly);
              if (aa==0) {
                g.drawLine(cd+cd+ps, 2+ly,cd+cd+cd,2+ly);
                g.drawLine(cd+cd+ps, 4+ly,cd+cd+cd,4+ly);
              }
            }
            
            g.setClip(0,0,dmaxx,dmaxy);
            g.setColor(0x10FF10);
            if (RMSOption.unitFormat==RMSOption.UNITS_METRIC)
              s=MapUtil.numStr(fAlt,4)+' '+LangHolder.getString(Lang.m);
            else
              s=MapUtil.numStr(fAlt,4)+' '+LangHolder.ft;
            
            g.setColor(0x0);
            g.fillRect(cd+cd+ps, gpsy+alth/2-fh/2-1, cd-ps,fh+2);
            g.setColor(0x10FF10);
            g.drawRect(cd+cd+ps, gpsy+alth/2-fh/2-1, cd-ps,fh+2);
            g.drawString(s, cd+cd+ps+ps, gpsy+alth/2+fh/2, Graphics.BOTTOM|Graphics.LEFT);
            
            //s=MapUtil.make4(fAlt)+' '+LangHolder.getString(Lang.m");
            //g.drawString(s, cd+cd+ps, (int)(gpsy-fh+ps*1.2+1), Graphics.TOP|Graphics.LEFT);
            if (RMSOption.unitFormat==RMSOption.UNITS_METRIC)
              s=String.valueOf(GPSReader.ALTSPEED_MS)+' '+LangHolder.getString(Lang.mps);
            else
              s=String.valueOf((int)(GPSReader.ALTSPEED_MS*196.86f))+' '+LangHolder.fpm;
            g.setColor(0x10FF10);
            g.drawString(s, cd+cd+ps, gpsy+alth, Graphics.TOP|Graphics.LEFT);
          }
          
          //GPSReader.ALTITUDE+=5;
          //GPSReader.SPEED_KMH+=30;
//----------draw flight plan-------------------------------
//#debug
//#           tracePos=2.303;
          if (navMode==NAVALTMODE) {
            gpsx=cd;
            gpsy=cd+to+2;
            sr1=fh+to+3;
          } else {
            gpsx=dcx;
            gpsy=cd+to+2;
            sr1=fh+fh+to+3;}
          
          ps=cd/14;
          int fpr = cd-ps;
          
          sr=fpr;
          
          if(gpsReader!=null){
            if (navMode==NAVALTMODE)
              g.drawImage(gpsReader.satImage(),0,sr1,Graphics.BOTTOM|Graphics.LEFT);
            else
              g.drawImage(gpsReader.satImage(),dmaxx,sr1,Graphics.BOTTOM|Graphics.RIGHT);
          }
          acrsr=0;
//          if (activeRoute!=null)
//            acrsr=activeRoute.courseFrom(reallat, reallon)-MapUtil.PIdiv2;
          if (navMode!=NAVHSIMODE){
            g.setColor(0x008000);
            g.fillTriangle(gpsx-ps-ps,gpsy+sr,gpsx+ps+ps,gpsy+sr,gpsx,gpsy-sr+ps+ps+ps/2);
          }
          boolean l2p=false;
          double h2r = 0,c2r=0,crs2wp=0;
          
          if (activeRoute!=null)
            if ((activeRoute.kind==MapRoute.ROUTEKIND)||(activeRoute.kind==MapRoute.KMLDOCUMENT)) {
            if (gpsA) {         //-coursradPaint
//#debug
//#               tracePos=2.304;
              h2r = activeRoute.height2CurrentStep(GPSReader.LATITUDE,GPSReader.LONGITUDE);
              l2p=activeRoute.left2CurrentStep(GPSReader.LATITUDE,GPSReader.LONGITUDE);
              //acrsr = activeRoute.courseFrom(reallat, reallon)-MapUtil.PIdiv2;
              c2r=activeRoute.courseStep();
              if(navMode==NAVHSIMODE){
                acrsr = -coursradPaint+c2r-MapUtil.PIdiv2; // курс по этапу!
                dist=-coursradPaint+activeRoute.courseFrom(GPSReader.LATITUDE, GPSReader.LONGITUDE)-MapUtil.PIdiv2; // курс на прямо!
                crs2wp=dist;
              }else{
                acrsr = -coursradPaint+activeRoute.courseFrom(GPSReader.LATITUDE, GPSReader.LONGITUDE)-MapUtil.PIdiv2; // курс на прямо!
                dist=acrsr;
                crs2wp=dist;
              }
              if (h2r>0.05){
                crsr = (l2p)?-crs2wp:-crs2wp-MapUtil.PIdiv2;
                g.setColor(0x104010);
                g.fillArc((int)(gpsx-fpr+1),(int)(gpsy-fpr+1),fpr+fpr,fpr+fpr,(int)(crsr*MapUtil.R2G),90);
              }
              dist =Math.abs(MapRoute.courseDiff(activeRoute.courseFrom(GPSReader.LATITUDE, GPSReader.LONGITUDE),c2r))*MapUtil.R2G;

              if (navMode!=NAVHSIMODE){
                g.setColor(0x00C000);
                g.fillTriangle(gpsx-ps-ps,gpsy+sr,gpsx+ps+ps,gpsy+sr,gpsx,gpsy-sr+ps+ps+ps/2);
              }
              
              crsr = acrsr;
              if (navMode==NAVHSIMODE){
              //  g.setColor(0xC0C0C0);
               // g.drawLine(gpsx, gpsy-(ps*4), gpsx, gpsy-(fpr));
              // if (dist>90)
               //     crsr=crsr-MapUtil.PI;
              }

              if (navMode==NAVHSIMODE){
                ca=crs2wp+MapUtil.PIdiv2+MapUtil.PI;
                cb=crs2wp-MapUtil.PIdiv2+MapUtil.PI;
                int adcx = (int)(gpsx+sr*Math.cos(crs2wp)+1);
                int adcy = (int)(gpsy+sr*Math.sin(crs2wp)+2);
              
                g.setColor(0x20FF10);
               // ps++;
                g.fillTriangle((int)(adcx-4*ps*Math.cos(crs2wp)+1), (int)(adcy-4*ps*Math.sin(crs2wp)+1),
                  (int)(adcx+2*ps*Math.cos(ca)+1), (int)(adcy+2*ps*Math.sin(ca)+1),
                  (int)(adcx+2*ps*Math.cos(cb)+1), (int)(adcy+2*ps*Math.sin(cb)+1));
                //ps--;
              }
              g.setColor(COLOR_HSI_ROUTE_DIRECTION);
              sr-=2;
              int x1,x2,y1,y2,xc,yc;
              x1=(int)(gpsx+(sr)*Math.sin(crsr+MapUtil.PIdiv2));
              if (navMode==NAVHSIMODE)
                x2=(int)(gpsx+(ps*6)*Math.sin(crsr+MapUtil.PIdiv2));
              else
                x2=(int)(gpsx-(sr)*Math.sin(crsr+MapUtil.PIdiv2));
              
              y1=(int)(gpsy-(sr)*Math.cos(crsr+MapUtil.PIdiv2));
              if (navMode==NAVHSIMODE)
                y2=(int)(gpsy-(ps*6)*Math.cos(crsr+MapUtil.PIdiv2));
              else
                y2=(int)(gpsy+(sr)*Math.cos(crsr+MapUtil.PIdiv2));

              g.drawLine(x1,y1,x2,y2);
              g.drawLine(x1+1,y1+1 ,x2+1,y2+1);
              g.drawLine(x1,y1+1 ,x2,y2+1);
              g.drawLine(x1+1,y1-1 ,x2+1,y2-1);
              g.drawLine(x1+1,y1 ,x2+1,y2);
              g.drawLine(x1-1,y1 ,x2-1,y2);
              g.drawLine(x1-1,y1+1 ,x2-1,y2+1);
              g.drawLine(x1,y1-1 ,x2,y2-1);
              g.drawLine(x1-1,y1-1 ,x2-1,y2-1);
              
              
              if (navMode==NAVHSIMODE){
//second part plus arrows
                x1=(int)(gpsx-(sr)*Math.sin(crsr+MapUtil.PIdiv2));
                x2=(int)(gpsx-(ps*6)*Math.sin(crsr+MapUtil.PIdiv2));
                y1=(int)(gpsy+(sr)*Math.cos(crsr+MapUtil.PIdiv2));
                y2=(int)(gpsy+(ps*6)*Math.cos(crsr+MapUtil.PIdiv2));
                
                g.drawLine(x1,y1,x2,y2);
                g.drawLine(x1+1,y1+1 ,x2+1,y2+1);
                g.drawLine(x1,y1+1 ,x2,y2+1);
                g.drawLine(x1+1,y1-1 ,x2+1,y2-1);
                g.drawLine(x1+1,y1 ,x2+1,y2);
                g.drawLine(x1-1,y1 ,x2-1,y2);
                g.drawLine(x1-1,y1+1 ,x2-1,y2+1);
                g.drawLine(x1,y1-1 ,x2,y2-1);
                g.drawLine(x1-1,y1-1 ,x2-1,y2-1);
                
                xc=(int)(gpsx+(sr)*Math.sin(crsr+MapUtil.PIdiv2));
                yc=(int)(gpsy-(sr)*Math.cos(crsr+MapUtil.PIdiv2));
                x1=(int)(xc);
                x2=(int)(xc+(4*ps)*Math.sin(crsr-MapUtil.PIdiv3));
                y1=(int)(yc);
                y2=(int)(yc-(4*ps)*Math.cos(crsr-MapUtil.PIdiv3));
                
                g.drawLine(x1,y1,x2,y2);
                g.drawLine(x1+1,y1+1 ,x2+1,y2+1);
                g.drawLine(x1,y1+1 ,x2,y2+1);
                g.drawLine(x1+1,y1-1 ,x2+1,y2-1);
                g.drawLine(x1+1,y1 ,x2+1,y2);
                g.drawLine(x1-1,y1 ,x2-1,y2);
                g.drawLine(x1-1,y1+1 ,x2-1,y2+1);
                g.drawLine(x1,y1-1 ,x2,y2-1);
                g.drawLine(x1-1,y1-1 ,x2-1,y2-1);
                
                x2=(int)(xc+(4*ps)*Math.sin(crsr-MapUtil.PI2div3));
                y2=(int)(yc-(4*ps)*Math.cos(crsr-MapUtil.PI2div3));
                
                g.drawLine(x1,y1,x2,y2);
                g.drawLine(x1+1,y1+1 ,x2+1,y2+1);
                g.drawLine(x1,y1+1 ,x2,y2+1);
                g.drawLine(x1+1,y1-1 ,x2+1,y2-1);
                g.drawLine(x1+1,y1 ,x2+1,y2);
                g.drawLine(x1-1,y1 ,x2-1,y2);
                g.drawLine(x1-1,y1+1 ,x2-1,y2+1);
                g.drawLine(x1,y1-1 ,x2,y2-1);
                g.drawLine(x1-1,y1-1 ,x2-1,y2-1);
                              
              }
              if (navMode==NAVHSIMODE){
              // if (dist>90)
                 //   crsr=crsr+MapUtil.PI;
              }
              sr+=2;
              
              if ((navMode==NAVFULLMODE)||(navMode==NAVHSIMODE)) {
                g.setColor(COLOR_HSI_ROUTE_DIRECTION);
                if (dmaxx<150)
                  g.setFont(FontUtil.SMALLFONT);
                else g.setFont(FontUtil.MEDIUMFONTB);
                sr1=(int)(c2r*MapUtil.R2G);
                if (sr1>360) sr1-=360;
                if (sr1<0) sr1+=360;
                
                g.drawString(MapUtil.numStr(sr1,3),0,to+fh,Graphics.LEFT|Graphics.TOP);
                
                g.setColor(0xE0E020);
                g.drawString(MapUtil.numStr(GPSReader.COURSE_I,3),0,to+fh+fh,Graphics.LEFT|Graphics.TOP);
                if (navMode==NAVHSIMODE){
                  g.setColor(0xE02020);
             //     c2r =Math.abs(MapRoute.courseDiff(activeRoute.courseFrom(GPSReader.LATITUDE, GPSReader.LONGITUDE),c2r))*MapUtil.R2G;
               //   g.drawString(String.valueOf(MapUtil.coordRound1(c2r))+((char)0xb0),0, gpsy+cd-fh, Graphics.BOTTOM|Graphics.LEFT);
                   c2r=dist;
                  g.drawString(String.valueOf(MapUtil.coordRound1(dist))+((char)0xb0),0, gpsy+cd-fh, Graphics.BOTTOM|Graphics.LEFT);
                }
              }
            }
            }
          
          Font bf = g.getFont();
          g.setFont(FontUtil.SMALLFONT);
          fh= FontUtil.SMALLFONT.getHeight();
          
          try{
            
            g.setColor(0xFFFFFF);
            int x1=gpsx-fpr,y1=gpsy-fpr,dd=fpr+fpr;
            g.drawArc( (x1+1), (y1+1),dd,dd,0,360);
            g.drawArc( (x1+2), (y1+2),dd-2,dd-2,0,360);
            g.drawArc( (x1+1), (y1+1),dd-1,dd-1,0,360);
            g.drawArc( (x1+2), (y1+1),dd-1,dd-1,0,360);
            g.drawArc( (x1+1), (y1+2),dd-1,dd-1,0,360);
            g.drawArc( (x1+2), (y1+2),dd-1,dd-1,0,360);
            
            
//#debug
//#             tracePos=2.305;
            if (navMode==NAVHSIMODE){
              g.setColor(0xFFFF20);
              g.fillTriangle(gpsx-ps-ps,gpsy-sr-ps,gpsx+ps+ps,gpsy-sr-ps,gpsx,gpsy-sr+2);
              g.setColor(0xFFFFFF);
            }
            crsr = -coursradPaint-MapUtil.PIdiv2;
            //crsr = -MapUtil.PIdiv2;
            
            sr=cd-ps-1;
            
            for (int r=0;r<24;r++) {
              if ((r==3)||(r==9)||(r==15)||(r==21))
                g.drawLine((int)(gpsx+sr*Math.cos(crsr+r*MapUtil.PIdiv12)+1),(int)(1+gpsy+sr*Math.sin(crsr+r*MapUtil.PIdiv12)),
                    (int)(gpsx+(sr-ps*1.5)*Math.cos(crsr+r*MapUtil.PIdiv12)+1),(int)(1+gpsy+(sr-ps*1.5)*Math.sin(crsr+r*MapUtil.PIdiv12))
                    );
              else
                if ((r!=0)&&(r!=6)&&(r!=12)&&(r!=18))
                  g.drawLine((int)(gpsx+sr*Math.cos(crsr+r*MapUtil.PIdiv12)+1),(int)(1+gpsy+sr*Math.sin(crsr+r*MapUtil.PIdiv12)),
                      (int)(gpsx+(sr-ps)*Math.cos(crsr+r*MapUtil.PIdiv12)+1),(int)(1+gpsy+(sr-ps)*Math.sin(crsr+r*MapUtil.PIdiv12))
                      );
                else
//              if ((r!=0)&&(r!=6)&&(r!=12)&&(r!=18))
                  g.drawLine((int)(gpsx+sr*Math.cos(crsr+r*MapUtil.PIdiv12)+1),(int)(1+gpsy+sr*Math.sin(crsr+r*MapUtil.PIdiv12)),
                      (int)(gpsx+(sr-ps-ps)*Math.cos(crsr+r*MapUtil.PIdiv12)+1),(int)(1+gpsy+(sr-ps-ps)*Math.sin(crsr+r*MapUtil.PIdiv12))
                      );
            }
            g.setColor(0xFFFF00);
            crsr = -coursradPaint+MapUtil.PIdiv2;
            int tr = sr-ps*2;
            s=MapUtil.SH_0;
            Font fff =g.getFont();
            int fs=tr-fff.stringWidth(s)/2;
            g.drawString(s, (int)(gpsx-fs*Math.cos(crsr)), (int)(gpsy-fs*Math.sin(crsr)-fh/2), Graphics.TOP|Graphics.HCENTER );
            s=MapUtil.SH_90;
            fs=tr-fff.stringWidth(s)/2;
            g.drawString(s, (int)(gpsx-fs*Math.cos(crsr+MapUtil.PIdiv2)), (int)(gpsy-fs*Math.sin(crsr+MapUtil.PIdiv2)-fh/2), Graphics.TOP|Graphics.HCENTER );
            s=MapUtil.SH_180;
            fs=tr-fff.stringWidth(s)/2;
            g.drawString(s, (int)(gpsx-fs*Math.cos(crsr+MapUtil.PI)), (int)(gpsy-fs*Math.sin(crsr+MapUtil.PI)-fh/2), Graphics.TOP|Graphics.HCENTER );
            s=MapUtil.SH_270;
            fs=tr-fff.stringWidth(s)/2;
            g.drawString(s, (int)(gpsx-fs*Math.cos(crsr-MapUtil.PIdiv2)), (int)(gpsy-fs*Math.sin(crsr-MapUtil.PIdiv2)-fh/2), Graphics.TOP|Graphics.HCENTER );
            if (dmaxx>130){
              s=MapUtil.SH_45;
              fs=tr-fff.stringWidth(s)/2;
              g.drawString(s, (int)(gpsx-fs*Math.cos(crsr+MapUtil.PIdiv4)), (int)(gpsy-fs*Math.sin(crsr+MapUtil.PIdiv4)-fh/2), Graphics.TOP|Graphics.HCENTER );
              s=MapUtil.SH_135;
              fs=tr-fff.stringWidth(s)/2;
              g.drawString(s, (int)(gpsx-fs*Math.cos(crsr+MapUtil.PIdiv2+MapUtil.PIdiv4)), (int)(gpsy-fs*Math.sin(crsr+MapUtil.PIdiv2+MapUtil.PIdiv4)-fh/2), Graphics.TOP|Graphics.HCENTER );
              s=MapUtil.SH_225;
              fs=tr-fff.stringWidth(s)/2;
              g.drawString(s, (int)(gpsx-fs*Math.cos(crsr+MapUtil.PI+MapUtil.PIdiv4)), (int)(gpsy-fs*Math.sin(crsr+MapUtil.PI+MapUtil.PIdiv4)-fh/2), Graphics.TOP|Graphics.HCENTER );
              s=MapUtil.SH_315;
              fs=tr-fff.stringWidth(s)/2;
              g.drawString(s, (int)(gpsx-fs*Math.cos(crsr-MapUtil.PIdiv2+MapUtil.PIdiv4)), (int)(gpsy-fs*Math.sin(crsr-MapUtil.PIdiv2+MapUtil.PIdiv4)-fh/2), Graphics.TOP|Graphics.HCENTER );
            }
            
            int x2,y2,x3,y3;
            fpr=ps/2;
            if (navMode==NAVHSIMODE) crsr = -MapUtil.PIdiv2;
            else crsr = acrsr;
            if (navMode==NAVHSIMODE){
              g.setColor(0xD0D0D0);
              g.fillTriangle(
                  (int)(gpsx+(ps*2)*Math.sin(crsr+MapUtil.PIdiv2)),
                  (int)(gpsy-(ps*2)*Math.cos(crsr+MapUtil.PIdiv2)),
                  (int)(gpsx+(ps*4.5)*Math.sin(crsr+MapUtil.PI+MapUtil.PIdiv2+MapUtil.PIdiv3)),
                  (int)(gpsy-(ps*4.5)*Math.cos(crsr+MapUtil.PI+MapUtil.PIdiv2+MapUtil.PIdiv3)),
                  (int)(gpsx+(ps*4.5)*Math.sin(crsr+MapUtil.PI+MapUtil.PIdiv2-MapUtil.PIdiv3)),
                  (int)(gpsy-(ps*4.5)*Math.cos(crsr+MapUtil.PI+MapUtil.PIdiv2-MapUtil.PIdiv3))
                  );
              
              x1=(int)(gpsx-(ps*5)*Math.sin(crsr+MapUtil.PIdiv2));
              y1=(int)(gpsy+(ps*5)*Math.cos(crsr+MapUtil.PIdiv2));
              
              g.fillTriangle(
                  (int)(x1+(ps*1.5)*Math.sin(crsr+MapUtil.PIdiv2)),
                  (int)(y1-(ps*1.5)*Math.cos(crsr+MapUtil.PIdiv2)),
                  (int)(x1+(ps*2.3)*Math.sin(crsr+MapUtil.PI+MapUtil.PIdiv2+MapUtil.PIdiv3)),
                  (int)(y1-(ps*1.3)*Math.cos(crsr+MapUtil.PI+MapUtil.PIdiv2+MapUtil.PIdiv3)),
                  (int)(x1+(ps*2.3)*Math.sin(crsr+MapUtil.PI+MapUtil.PIdiv2-MapUtil.PIdiv3)),
                  (int)(y1-(ps*1.3)*Math.cos(crsr+MapUtil.PI+MapUtil.PIdiv2-MapUtil.PIdiv3))
                  );
              
//              x1=(int)(gpsx+(ps*5)*Math.sin(crsr+MapUtil.PIdiv2));
//              x2=(int)(gpsx);
//              y1=(int)(gpsy-(ps*5)*Math.cos(crsr+MapUtil.PIdiv2));
//              y2=(int)(gpsy);
              
              x1=gpsx;
              x2=gpsx;
              y1=gpsy-(ps*5);
              y2=gpsy+ps*5;
              
              g.drawLine(x1,y1,x2,y2);
              if (dmaxx>130){
                
                g.drawLine(x1+1,y1+1 ,x2+1,y2+1);
                g.drawLine(x1,y1+1 ,x2,y2+1);
                g.drawLine(x1+1,y1-1 ,x2+1,y2-1);
                g.drawLine(x1+1,y1 ,x2+1,y2);
                g.drawLine(x1-1,y1 ,x2-1,y2);
                g.drawLine(x1-1,y1+1 ,x2-1,y2+1);
                g.drawLine(x1,y1-1 ,x2,y2-1);
                g.drawLine(x1-1,y1-1 ,x2-1,y2-1);
              }
//              x1=(int)(gpsx-(ps*5)*Math.sin(crsr+MapUtil.PIdiv2));
//              x2=(int)(gpsx);
//              y1=(int)(gpsy+(ps*5)*Math.cos(crsr+MapUtil.PIdiv2));
//              y2=(int)(gpsy);
//
//              g.drawLine(x1,y1,x2,y2);
//              if (dmaxx>130){
//
//                g.drawLine(x1+1,y1+1 ,x2+1,y2+1);
//
//                g.drawLine(x1,y1+1 ,x2,y2+1);
//                g.drawLine(x1+1,y1-1 ,x2+1,y2-1);
//                g.drawLine(x1+1,y1 ,x2+1,y2);
//
//                g.drawLine(x1-1,y1 ,x2-1,y2);
//                g.drawLine(x1-1,y1+1 ,x2-1,y2+1);
//                g.drawLine(x1,y1-1 ,x2,y2-1);
//                g.drawLine(x1-1,y1-1 ,x2-1,y2-1);
//              }
              
            } else {
              g.setColor(0xFFFF20);
              g.fillTriangle(
                  (int)(gpsx+(ps*1.5)*Math.sin(crsr+MapUtil.PIdiv2)),
                  (int)(gpsy-(ps*1.5)*Math.cos(crsr+MapUtil.PIdiv2)),
                  (int)(gpsx+(ps*2.5)*Math.sin(crsr+MapUtil.PI+MapUtil.PIdiv2+MapUtil.PIdiv3)),
                  (int)(gpsy-(ps*2.5)*Math.cos(crsr+MapUtil.PI+MapUtil.PIdiv2+MapUtil.PIdiv3)),
                  (int)(gpsx+(ps*2.5)*Math.sin(crsr+MapUtil.PI+MapUtil.PIdiv2-MapUtil.PIdiv3)),
                  (int)(gpsy-(ps*2.5)*Math.cos(crsr+MapUtil.PI+MapUtil.PIdiv2-MapUtil.PIdiv3))
                  );
            }
            g.setColor(0xFFFFFF);
            for (int i=1;i<4;i++){
              alth=(int)(0.1667f*cd*i);
              g.drawArc((int)(gpsx-fpr/2-alth*Math.sin(crsr)),(int)(gpsy-fpr/2+alth*Math.cos(crsr)),fpr,fpr,0,360);
              g.drawArc((int)(gpsx-fpr/2-alth*Math.sin(crsr+MapUtil.PI)),(int)(gpsy-fpr/2+alth*Math.cos(crsr+MapUtil.PI)),fpr,fpr,0,360);
            }
            crsr = acrsr;
            
          }finally{
            g.setFont(bf);
            fh=bf.getHeight();
          }
          
          g.setColor(0x20FF10);
          if (activeRoute!=null)
            if (activeRoute.kind==MapRoute.ROUTEKIND){
            
            float ssNV=scaleNavFactor;
            if (navMode==NAVHSIMODE) scaleNavFactor=1;
            double maxOffs=3/scaleNavFactor;
            double scaleDist=1.0/scaleNavFactor;
            if (navMode==NAVHSIMODE) //h2r = c2r;
              alth=(int)((c2r<maxOffs?c2r:maxOffs)*0.1667*cd*scaleNavFactor);
            else alth=(int)((h2r<maxOffs?h2r:maxOffs)*0.1667*cd*scaleNavFactor);
            if(!l2p)alth=-alth;
            if (navMode==NAVHSIMODE)alth=-alth;
            scaleNavFactor=ssNV;
            
            double offsx= (gpsx-(double)alth*Math.sin(crsr));
            double offsy = (gpsy+(double)alth*Math.cos(crsr));
            
            int x1,y1,x2,y2,x3,y3;
            g.setColor(0xD0D0D0);
            if (navMode!=NAVHSIMODE){
              
              x1=(int)(offsx+(ps*1.5)*Math.sin(crsr+MapUtil.PIdiv2));
              y1=(int)(offsy-(ps*1.5)*Math.cos(crsr+MapUtil.PIdiv2));
              x2=(int)(offsx+(ps*2.5)*Math.sin(crsr+MapUtil.PI+MapUtil.PIdiv2+MapUtil.PIdiv3));
              y2=(int)(offsy-(ps*2.5)*Math.cos(crsr+MapUtil.PI+MapUtil.PIdiv2+MapUtil.PIdiv3));
              x3=(int)(offsx+(ps*2.5)*Math.sin(crsr+MapUtil.PI+MapUtil.PIdiv2-MapUtil.PIdiv3));
              y3=(int)(offsy-(ps*2.5)*Math.cos(crsr+MapUtil.PI+MapUtil.PIdiv2-MapUtil.PIdiv3));
              MapCanvas.drawTriangle(g,x1,y1,x2,y2,x3,y3);
              if (dmaxx>130){
                MapCanvas.drawTriangle(g,x1,y1,x2,y2,x3,y3,-1,-1);
                MapCanvas.drawTriangle(g,x1,y1,x2,y2,x3,y3,0,-1);
                MapCanvas.drawTriangle(g,x1,y1,x2,y2,x3,y3,1,-1);
                MapCanvas.drawTriangle(g,x1,y1,x2,y2,x3,y3,1,0);
                MapCanvas.drawTriangle(g,x1,y1,x2,y2,x3,y3,1,1);
                MapCanvas.drawTriangle(g,x1,y1,x2,y2,x3,y3,0,1);
                MapCanvas.drawTriangle(g,x1,y1,x2,y2,x3,y3,-1,1);
                MapCanvas.drawTriangle(g,x1,y1,x2,y2,x3,y3,-1,0);
              }
            }
            if (navMode==NAVHSIMODE)
              x1=(int)(offsx+(ps*6)*Math.sin(crsr+MapUtil.PIdiv2));
            else x1=(int)(offsx+(ps*1.5)*Math.sin(crsr+MapUtil.PIdiv2));
            
            if (navMode==NAVHSIMODE)
              x2=(int)(offsx-(ps*6)*Math.sin(crsr+MapUtil.PIdiv2));
            else x2=(int)(offsx+(ps*6)*Math.sin(crsr+MapUtil.PIdiv2));
            
            if (navMode==NAVHSIMODE)
              y1=(int)(offsy-(ps*6)*Math.cos(crsr+MapUtil.PIdiv2));
            else y1=(int)(offsy-(ps*1.5)*Math.cos(crsr+MapUtil.PIdiv2) );
            
            if (navMode==NAVHSIMODE)
              y2=(int)(offsy+(ps*6)*Math.cos(crsr+MapUtil.PIdiv2));
            else y2=(int)(offsy-(ps*6)*Math.cos(crsr+MapUtil.PIdiv2));
            if (navMode==NAVHSIMODE) g.setColor(COLOR_HSI_ROUTE_DIRECTION);
            
            g.drawLine(x1,y1,x2,y2);
            if ((dmaxx>130)||(navMode==NAVHSIMODE)){
              
              g.drawLine(x1+1,y1+1 ,x2+1,y2+1);
              
              g.drawLine(x1,y1+1 ,x2,y2+1);
              g.drawLine(x1+1,y1-1 ,x2+1,y2-1);
              g.drawLine(x1+1,y1 ,x2+1,y2);
              
              g.drawLine(x1-1,y1 ,x2-1,y2);
              g.drawLine(x1-1,y1+1 ,x2-1,y2+1);
              g.drawLine(x1,y1-1 ,x2,y2-1);
              g.drawLine(x1-1,y1-1 ,x2-1,y2-1);
            }
            
            if (navMode!=NAVHSIMODE){
              
              x1=(int)(offsx-(ps*1.5)*Math.sin(crsr+MapUtil.PIdiv2));
              x2=(int)(offsx-(ps*6)*Math.sin(crsr+MapUtil.PIdiv2));
              y1=(int)(offsy+(ps*1.5)*Math.cos(crsr+MapUtil.PIdiv2) );
              y2=(int)(offsy+(ps*6)*Math.cos(crsr+MapUtil.PIdiv2));
              g.drawLine(x1,y1,x2,y2);
              if (dmaxx>130){
                g.drawLine(x1+1,y1+1 ,x2+1,y2+1);
                
                g.drawLine(x1,y1+1 ,x2,y2+1);
                g.drawLine(x1+1,y1-1 ,x2+1,y2-1);
                g.drawLine(x1+1,y1 ,x2+1,y2);
                
                g.drawLine(x1-1,y1 ,x2-1,y2);
                g.drawLine(x1-1,y1+1 ,x2-1,y2+1);
                g.drawLine(x1,y1-1 ,x2,y2-1);
                g.drawLine(x1-1,y1-1 ,x2-1,y2-1);
                
              }
            }
            
            g.setColor(0x20FF20);
            
            if (l2p)
              s="<< "+MapUtil.distWithNameRound2(h2r)+" <<";
            else
              s=">> "+MapUtil.distWithNameRound2(h2r)+" >>";
            
            if (navMode==NAVALTMODE) {
              g.drawString(s, cd, gpsy+cd, Graphics.TOP|Graphics.HCENTER);
//            } else
//              if (navMode==NAVFULLMODE) {
//              g.drawString(s, gpsx, gpsy+sr+2+fh, Graphics.BOTTOM|Graphics.HCENTER);
            }else {
              g.drawString(s, dmaxx, gpsy+cd+fh, Graphics.BOTTOM|Graphics.RIGHT);
            }
            dist = activeRoute.distFromPrecise(GPSReader.LATITUDE, GPSReader.LONGITUDE);
            s2=MapUtil.distWithNameRound3(dist);
            s1=activeRoute.pt2Nav().getName();
            s1=MapCanvas.cutStringToWidth(s1,dmaxx/3,g);
            //if (s1.length()>6) s1=s1.substring(0,6);
            if (navMode==NAVALTMODE)
              s=String.valueOf(GPSReader.COURSE_I)+((char)0xb0);
            else s= MapUtil.speedWithNameRound1(GPSReader.SPEED_KMH);
            
            if (navMode==NAVALTMODE)
              g.drawString(s1, 1, gpsy+cd+fh, Graphics.TOP|Graphics.LEFT);
            else
              g.drawString(s1, 1, to, Graphics.TOP|Graphics.LEFT);
            if (navMode==NAVALTMODE)
              g.drawString(s2, cd+cd-1, gpsy+cd+fh, Graphics.TOP|Graphics.RIGHT);
            else g.drawString(s2, dmaxx-1, to, Graphics.TOP|Graphics.RIGHT);
            
            if (navMode==NAVALTMODE)
              g.drawString(activeRoute.activeETA(), 1, gpsy+cd+fh+fh, Graphics.TOP|Graphics.LEFT);
            else {
              if (navMode!=NAVHSIMODE){
                g.drawArc(fpr,gpsy+cd-fh-fh/2-fpr/2,fpr,fpr,0,360);
                g.drawString(MapUtil.distWithNameRound3(scaleDist), fpr+fpr+fpr, gpsy+cd-fh, Graphics.BOTTOM|Graphics.LEFT);
              }
              g.drawString(activeRoute.activeETA(), 1, gpsy+cd, Graphics.BOTTOM|Graphics.LEFT);
              g.drawString(activeRoute.TAfull(dist),1,gpsy+cd+fh,Graphics.BOTTOM|Graphics.LEFT);
            }
            if (navMode==NAVALTMODE){
              int c= g.getColor();
              g.setColor(0xE0E020);
              g.drawString(s, cd+cd-1, gpsy+cd+fh+fh, Graphics.TOP|Graphics.RIGHT);
              g.setColor(c);
            } else g.drawString(s, dmaxx-1, gpsy+cd, Graphics.BOTTOM|Graphics.RIGHT);
            
            if (activeRoute!=null)
              if ((navMode==NAVFULLMODE)||(navMode==NAVHSIMODE))
                activeRoute.drawPointInfo(g,stdFont,smallFont,gpsy+cd,1,false);
              else
                activeRoute.drawPointInfo(g,stdFont,smallFont,gpsy+cd+fh+fh,1,false);
            
            }
          
//#debug
//#           tracePos=2.306;
          
          if (navMode==NAVALTMODE) {
//---------------draw speedometer--------------------------
            
            sr=cd/2;// speedmeter radius
            ps=cd/10;
            gpsx=cd+cd+sr;
            gpsy=(int)(cd+1);
            
//#debug
//#             tracePos=2.307;
            g.setColor(0xFFFF00);
            
            g.drawArc(gpsx-sr,gpsy-sr,sr+sr,sr+sr,0,180);
            //g.drawArc(gpsx-sr-1,gpsy-sr,sr+sr,sr+sr,0,180);
            //g.drawArc(gpsx-sr+1,gpsy-sr,sr+sr,sr+sr,0,180);
            //g.drawArc(gpsx-sr-1,gpsy-sr-1,sr+sr,sr+sr,0,180);
            //g.drawArc(gpsx-sr+1,gpsy-sr-1,sr+sr,sr+sr,0,180);
            //g.drawArc(gpsx-sr,gpsy-sr-1,sr+sr,sr+sr,0,180);
            //g.drawArc(gpsx-sr-1,gpsy-sr+1,sr+sr,sr+sr,0,180);
            //g.drawArc(gpsx-sr+1,gpsy-sr+1,sr+sr,sr+sr,0,180);
            //g.drawArc(gpsx-sr,gpsy-sr+1,sr+sr,sr+sr,0,180);
            
            for (int r=0;r<7;r++) {
              if ((r==3))
                g.fillTriangle((int)(gpsx+sr*Math.cos(r*MapUtil.PIdiv6)+ps*Math.cos(r*MapUtil.PIdiv6+MapUtil.PIdiv2)/1.6),(int)(gpsy-sr*Math.sin(r*MapUtil.PIdiv6)-ps*Math.sin(r*MapUtil.PIdiv6+MapUtil.PIdiv2)/1.6),
                    (int)(gpsx+(sr-ps*2)*Math.cos(r*MapUtil.PIdiv6)),(int)(gpsy-(sr-ps*2)*Math.sin(r*MapUtil.PIdiv6)),
                    (int)(gpsx+sr*Math.cos(r*MapUtil.PIdiv6)+ps*Math.cos(r*MapUtil.PIdiv6-MapUtil.PIdiv2)/1.6),(int)(gpsy-sr*Math.sin(r*MapUtil.PIdiv6)-ps*Math.sin(r*MapUtil.PIdiv6-MapUtil.PIdiv2)/1.6)
                    );
              else
                if ((r==0))
                  g.fillTriangle((int)(gpsx+sr*Math.cos(r*MapUtil.PIdiv6)+ps*Math.cos(r*MapUtil.PIdiv6+MapUtil.PIdiv2)),(int)(gpsy-sr*Math.sin(r*MapUtil.PIdiv6)-ps*Math.sin(r*MapUtil.PIdiv6+MapUtil.PIdiv2)),
                      (int)(gpsx+(sr-ps*2)*Math.cos(r*MapUtil.PIdiv6)),(int)(gpsy-(sr-ps*2)*Math.sin(r*MapUtil.PIdiv6)),
                      (int)(gpsx+sr*Math.cos(r*MapUtil.PIdiv6)),(int)(gpsy-sr*Math.sin(r*MapUtil.PIdiv6))
                      );
                else
                  if ((r==6))
                    g.fillTriangle((int)(gpsx+sr*Math.cos(r*MapUtil.PIdiv6)),(int)(gpsy-sr*Math.sin(r*MapUtil.PIdiv6)),
                        (int)(gpsx+(sr-ps*2)*Math.cos(r*MapUtil.PIdiv6)),(int)(gpsy-(sr-ps*2)*Math.sin(r*MapUtil.PIdiv6)),
                        (int)(gpsx+sr*Math.cos(r*MapUtil.PIdiv6)+ps*Math.cos(r*MapUtil.PIdiv6-MapUtil.PIdiv2)),(int)(gpsy-sr*Math.sin(r*MapUtil.PIdiv6)-ps*Math.sin(r*MapUtil.PIdiv6-MapUtil.PIdiv2))
                        );
                  else
                    g.fillTriangle((int)(gpsx+sr*Math.cos(r*MapUtil.PIdiv6)+ps*Math.cos(r*MapUtil.PIdiv6+MapUtil.PIdiv2)/2),(int)(gpsy-sr*Math.sin(r*MapUtil.PIdiv6)-ps*Math.sin(r*MapUtil.PIdiv6+MapUtil.PIdiv2)/2),
                        (int)(gpsx+(sr-ps)*Math.cos(r*MapUtil.PIdiv6)),(int)(gpsy-(sr-ps)*Math.sin(r*MapUtil.PIdiv6)),
                        (int)(gpsx+sr*Math.cos(r*MapUtil.PIdiv6)+ps*Math.cos(r*MapUtil.PIdiv6-MapUtil.PIdiv2)/2),(int)(gpsy-sr*Math.sin(r*MapUtil.PIdiv6)-ps*Math.sin(r*MapUtil.PIdiv6-MapUtil.PIdiv2)/2)
                        );
            }
            
//#debug
//#             tracePos=2.308;
            g.setColor(0x0);
            g.drawLine(gpsx-sr-1,gpsy,gpsx+sr+1,gpsy);
            g.drawLine(gpsx-sr-1,gpsy+1,gpsx+sr+1,gpsy+1);
            //GPSReader.SPEED_KMH = (float)21.8;
            //GPSReader.SPEED_KMH=GPSReader.SPEED_KMH+40;
            ca = GPSReader.SPEED_KMH;
            if (RMSOption.unitFormat==RMSOption.UNITS_NAUTICAL)
              ca*=0.5399568f;
            else if (RMSOption.unitFormat==RMSOption.UNITS_IMPERIAL)
              ca*=0.6213882f;
            
            if ((ca<250)) speedCoeff=5;
            //if ((ca<330)&&(speedCoeff>6)) speedCoeff=6;
            else if ((ca>300)&&(speedCoeff<=5)) speedCoeff=15;
            //else if ((ca<330)&&(speedCoeff==9)) speedCoeff=3;
            //else if ((ca>300)&&(speedCoeff==3)) speedCoeff=9;
            
//#debug
//#             tracePos=2.309;
            if (speedCoeff==5) g.setColor(0xFFFFFF);
            else if (speedCoeff==15) g.setColor(0xFF8F00);
            //else if (speedCoeff==9) g.setColor(0xFF0000);
            //else if (speedCoeff>=3) g.setColor(0xFF00FF);
            try{
              g.setFont(smallFont);
              
              int r=0;
              g.drawString(String.valueOf(r*30*speedCoeff), (int)(gpsx-(sr-ps-ps/3)*Math.cos(r*MapUtil.PIdiv2)), (int)(gpsy-(sr-ps-ps)*Math.sin(r*MapUtil.PIdiv2)), Graphics.LEFT|Graphics.BOTTOM );
              if (dmaxx>160){
                r=1;
                g.drawString(String.valueOf(r*30*speedCoeff), (int)(gpsx-(sr-ps-ps)*Math.cos(r*MapUtil.PIdiv2)), (int)(gpsy-(sr-ps-ps/2)*Math.sin(r*MapUtil.PIdiv2)), Graphics.HCENTER|Graphics.TOP );
              }
              r=2;
              g.drawString(String.valueOf(r*30*speedCoeff), (int)(gpsx-(sr-ps-ps/3)*Math.cos(r*MapUtil.PIdiv2)), (int)(gpsy-(sr-ps-ps)*Math.sin(r*MapUtil.PIdiv2)), Graphics.RIGHT|Graphics.BOTTOM );
              
            } finally{
              g.setFont(stdFont);
            }
            ca = (ca/10.d/(double)speedCoeff);
            //g.setColor(0x00FFFF);
            g.setColor(0x66CCFF);
            g.fillTriangle((int)(gpsx-(ps*1.2)*Math.cos(ca*MapUtil.PIdiv6+MapUtil.PIdiv2)),(int)(gpsy-(ps*1.2)*Math.sin(ca*MapUtil.PIdiv6+MapUtil.PIdiv2)),
                (int)(gpsx-(sr-ps*2)*Math.cos(ca*MapUtil.PIdiv6)),(int)(gpsy-(sr-ps*2)*Math.sin(ca*MapUtil.PIdiv6)),
                (int)(gpsx-(ps*1.2)*Math.cos(ca*MapUtil.PIdiv6-MapUtil.PIdiv2)),(int)(gpsy-(ps*1.2)*Math.sin(ca*MapUtil.PIdiv6-MapUtil.PIdiv2))
                );
            
            g.fillArc((int)(gpsx-(ps*1.2)-1),(int)(gpsy-(ps*1.2)-1),
                (int)((ps*2.4)+2),(int)((ps*2.4)+2),0,360);
            
            
            s=MapUtil.speedWithNameRound1(GPSReader.SPEED_KMH);
            //s=String.valueOf(GPSReader.SPEED_KMH)+' '+LangHolder.getString(Lang.kmh);
            g.setColor(0x10FF10);
            //g.drawString(s, dmaxx, 2, Graphics.TOP|Graphics.RIGHT );
            //g.drawString(s, dmaxx, cd+1, Graphics.TOP|Graphics.RIGHT );
            g.drawString(s, cd+cd+ps, (int)(gpsy+ps*1.2+1), Graphics.TOP|Graphics.LEFT);
          }
          } else if (navMode==NAVWPTMODE){
          // Displays list of next waypoints
          
          if (activeRoute!=null)
            activeRoute.drawPointInfo(g,stdFont,smallFont,(splitMode==0)?0:-fh,activeRoute.maprouteOffs,true);
          }
        
        
        //-------------END FLIGHT----------------------------------------------
      } else
        
        
        if (mode==SATMODE) {
                          drawScreenCaption(Lang.satellites,g, fh, rr,currSatModeIndex,satModes.length);

        g.setColor(0xFFFFFF);
        if (satMode==SATSIGNALMODE)
          s="%";
        else //if (satMode==SATNUMBERMODE)
          s="#";
        g.drawString(s,dmaxx-fh/2,dminy,Graphics.TOP|Graphics.RIGHT);
        
        if (gpsReader!=null) gpsReader.drawSats(g,fh,to);
        
        } else

          if (mode==VARMODE) {
                     drawScreenCaption(Lang.variometer,g, fh, rr,0,0);

        g.setColor(0xFFFF00);
        if (gpsA){
            GraphUtils.paintAltSpeed(g,(int)(GPSReader.ALTSPEED_MS*10),fh,dmaxy/2,0xFFFF80,dmaxx,dmaxy, dcx);
            GraphUtils.paintAltitude(g,GPSReader.ALTITUDE,fh+dmaxy/2+2,(dmaxy/2-fh)-4,0xFFFF80,dmaxx,dmaxy, dcx);

            //gpsReader.ALTSPEED_MS
                  //GraphUtils.paintSpeed(g,(int)ca,fh,false,sr,dmaxx,dmaxy, dcx);

        }else{
          g.drawString(LangHolder.getString(Lang.mps), dmaxx/2, fh+(dmaxy-fh)/3, Graphics.HCENTER|Graphics.BOTTOM );
          g.drawString(LangHolder.getString(Lang.m), dmaxx/2, fh+2*(dmaxy-fh)/3, Graphics.HCENTER|Graphics.BOTTOM );
        }
          } else
          
          
          if (mode==SPEEDMODE) {
                drawScreenCaption(Lang.speed,g, fh, rr,currSpdModeIndex,spdModes.length);
        g.setColor(0xFFFF00);
        
        int cd;
        if ((dmaxx-dminx+15)<(dmaxy-dminy))
          cd = (int)((dmaxx-dminx)/1.1);
        else
          cd = (int)((dmaxy-dminy-to)/1.1);
        
        sr=cd/2;// speedmeter radius
        int ps=cd/12;
        
        if (spdMode==SPDANALOGMODE){
          gpsx=dcx;
          gpsy=(int)(dcy+ps*1.5);
          
//#debug
//#           tracePos=2.31;
          g.setColor(0x404040);
          g.fillArc(gpsx-sr,gpsy-sr,sr+sr,sr+sr,0,180);
          g.setColor(0xFFFF00);
          g.drawArc(gpsx-sr,gpsy-sr,sr+sr,sr+sr,0,180);
          g.drawArc(gpsx-sr-1,gpsy-sr,sr+sr,sr+sr,0,180);
          g.drawArc(gpsx-sr+1,gpsy-sr,sr+sr,sr+sr,0,180);
          g.drawArc(gpsx-sr-1,gpsy-sr-1,sr+sr,sr+sr,0,180);
          g.drawArc(gpsx-sr+1,gpsy-sr-1,sr+sr,sr+sr,0,180);
          g.drawArc(gpsx-sr,gpsy-sr-1,sr+sr,sr+sr,0,180);
          g.drawArc(gpsx-sr-1,gpsy-sr+1,sr+sr,sr+sr,0,180);
          g.drawArc(gpsx-sr+1,gpsy-sr+1,sr+sr,sr+sr,0,180);
          g.drawArc(gpsx-sr,gpsy-sr+1,sr+sr,sr+sr,0,180);
          
          for (int r=0;r<7;r++) {
            if ((r==3))
              g.fillTriangle((int)(gpsx+sr*Math.cos(r*MapUtil.PIdiv6)+ps*Math.cos(r*MapUtil.PIdiv6+MapUtil.PIdiv2)/1.6),(int)(gpsy-sr*Math.sin(r*MapUtil.PIdiv6)-ps*Math.sin(r*MapUtil.PIdiv6+MapUtil.PIdiv2)/1.6),
                  (int)(gpsx+(sr-ps*2)*Math.cos(r*MapUtil.PIdiv6)),(int)(gpsy-(sr-ps*2)*Math.sin(r*MapUtil.PIdiv6)),
                  (int)(gpsx+sr*Math.cos(r*MapUtil.PIdiv6)+ps*Math.cos(r*MapUtil.PIdiv6-MapUtil.PIdiv2)/1.6),(int)(gpsy-sr*Math.sin(r*MapUtil.PIdiv6)-ps*Math.sin(r*MapUtil.PIdiv6-MapUtil.PIdiv2)/1.6)
                  );
            else
              if ((r==0))
                g.fillTriangle((int)(gpsx+sr*Math.cos(r*MapUtil.PIdiv6)+ps*Math.cos(r*MapUtil.PIdiv6+MapUtil.PIdiv2)),(int)(gpsy-sr*Math.sin(r*MapUtil.PIdiv6)-ps*Math.sin(r*MapUtil.PIdiv6+MapUtil.PIdiv2)),
                    (int)(gpsx+(sr-ps*2)*Math.cos(r*MapUtil.PIdiv6)),(int)(gpsy-(sr-ps*2)*Math.sin(r*MapUtil.PIdiv6)),
                    (int)(gpsx+sr*Math.cos(r*MapUtil.PIdiv6)),(int)(gpsy-sr*Math.sin(r*MapUtil.PIdiv6))
                    );
              else
                if ((r==6))
                  g.fillTriangle((int)(gpsx+sr*Math.cos(r*MapUtil.PIdiv6)),(int)(gpsy-sr*Math.sin(r*MapUtil.PIdiv6)),
                      (int)(gpsx+(sr-ps*2)*Math.cos(r*MapUtil.PIdiv6)),(int)(gpsy-(sr-ps*2)*Math.sin(r*MapUtil.PIdiv6)),
                      (int)(gpsx+sr*Math.cos(r*MapUtil.PIdiv6)+ps*Math.cos(r*MapUtil.PIdiv6-MapUtil.PIdiv2)),(int)(gpsy-sr*Math.sin(r*MapUtil.PIdiv6)-ps*Math.sin(r*MapUtil.PIdiv6-MapUtil.PIdiv2))
                      );
                else
                  g.fillTriangle((int)(gpsx+sr*Math.cos(r*MapUtil.PIdiv6)+ps*Math.cos(r*MapUtil.PIdiv6+MapUtil.PIdiv2)/2),(int)(gpsy-sr*Math.sin(r*MapUtil.PIdiv6)-ps*Math.sin(r*MapUtil.PIdiv6+MapUtil.PIdiv2)/2),
                      (int)(gpsx+(sr-ps)*Math.cos(r*MapUtil.PIdiv6)),(int)(gpsy-(sr-ps)*Math.sin(r*MapUtil.PIdiv6)),
                      (int)(gpsx+sr*Math.cos(r*MapUtil.PIdiv6)+ps*Math.cos(r*MapUtil.PIdiv6-MapUtil.PIdiv2)/2),(int)(gpsy-sr*Math.sin(r*MapUtil.PIdiv6)-ps*Math.sin(r*MapUtil.PIdiv6-MapUtil.PIdiv2)/2)
                      );
          }
          
//#debug
//#           tracePos=2.32;
          g.setColor(0x0);
          g.drawLine(gpsx-sr-1,gpsy,gpsx+sr+1,gpsy);
          g.drawLine(gpsx-sr-1,gpsy+1,gpsx+sr+1,gpsy+1);
          //GPSReader.SPEED_KMH = (float)21.8;
          //GPSReader.SPEED_KMH=GPSReader.SPEED_KMH+40;
          ca = GPSReader.SPEED_KMH;
          if (RMSOption.unitFormat==RMSOption.UNITS_NAUTICAL)
            ca*=0.5399568f;
          else if (RMSOption.unitFormat==RMSOption.UNITS_IMPERIAL)
            ca*=0.6213882f;
          //if ((ca<50)&&(speedCoeff==2)) speedCoeff=1;
          if ((ca<50)) speedCoeff=1;
          else if ((ca>60)&&(speedCoeff<=1)) speedCoeff=2;
          else if ((ca<100)&&(speedCoeff>=3)) speedCoeff=2;
          else if ((ca>120)&&(speedCoeff<=2)) speedCoeff=3;
          else if ((ca<170)&&(speedCoeff>=6)) speedCoeff=3;
          else if ((ca>180)&&(speedCoeff<=3)) speedCoeff=6;
          else if ((ca<350)&&(speedCoeff>=20)) speedCoeff=6;
          else if ((ca>360)&&(speedCoeff<=6)) speedCoeff=20;
          
//#debug
//#           tracePos=2.33;
          if (speedCoeff==1) g.setColor(0xFFFFFF);
          else if (speedCoeff==2) g.setColor(0xFF8F00);
          else if (speedCoeff==3) g.setColor(0xFF0000);
          else if (speedCoeff>=3) g.setColor(0xFF00FF);
          int r=0;
          g.drawString(String.valueOf(r*30*speedCoeff), (int)(gpsx-(sr-ps-ps)*Math.cos(r*MapUtil.PIdiv2)), (int)(gpsy-(sr-ps-ps)*Math.sin(r*MapUtil.PIdiv2)), Graphics.LEFT|Graphics.BOTTOM );
          r=1;
          g.drawString(String.valueOf(r*30*speedCoeff), (int)(gpsx-(sr-ps-ps)*Math.cos(r*MapUtil.PIdiv2)), (int)(gpsy-(sr-ps-ps)*Math.sin(r*MapUtil.PIdiv2)), Graphics.HCENTER|Graphics.TOP );
          r=2;
          g.drawString(String.valueOf(r*30*speedCoeff), (int)(gpsx-(sr-ps-ps/2)*Math.cos(r*MapUtil.PIdiv2)), (int)(gpsy-(sr-ps-ps)*Math.sin(r*MapUtil.PIdiv2)), Graphics.RIGHT|Graphics.BOTTOM );
          
          ca = (ca/10./(double)speedCoeff);
          //g.setColor(0x00FFFF);
          g.setColor(0x66CCFF);
          g.fillTriangle((int)(gpsx-(ps*1.2)*Math.cos(ca*MapUtil.PIdiv6+MapUtil.PIdiv2)),(int)(gpsy-(ps*1.2)*Math.sin(ca*MapUtil.PIdiv6+MapUtil.PIdiv2)),
              (int)(gpsx-(sr-ps)*Math.cos(ca*MapUtil.PIdiv6)),(int)(gpsy-(sr-ps)*Math.sin(ca*MapUtil.PIdiv6)),
              (int)(gpsx-(ps*1.2)*Math.cos(ca*MapUtil.PIdiv6-MapUtil.PIdiv2)),(int)(gpsy-(ps*1.2)*Math.sin(ca*MapUtil.PIdiv6-MapUtil.PIdiv2))
              );
          
          g.fillArc((int)(gpsx-(ps*1.2)-1),(int)(gpsy-(ps*1.2)-1),(int)((ps*2.4)+2),(int)((ps*2.4)+2),0,360);
        } else
          if (spdMode==SPDDIGITMODE) {
          ca=GPSReader.SPEED_KMH;
          sr = 0xFFFF80;
          if (RMSOption.getBoolOpt(RMSOption.BL_WARNMAXSPEED))
          if (ca>=RMSOption.maxSpeed)
            sr = 0xFF3020;
          else if (ca>RMSOption.maxSpeed-5)
            sr = 0xFFF000;
          
          if (RMSOption.unitFormat==RMSOption.UNITS_NAUTICAL)
            ca*=0.5399568f;
          else if (RMSOption.unitFormat==RMSOption.UNITS_IMPERIAL)
            ca*=0.6213882f;
          GraphUtils.paintSpeed(g,(int)ca,fh,false,sr,dmaxx,dmaxy, dcx);
          // paintSpeed(g,914,fh);
          } else
            if (spdMode==SPDDIGFLMODE) {
          ca=GPSReader.SPEED_KMH;
          sr=0xFFFF80;
          if (RMSOption.getBoolOpt(RMSOption.BL_WARNMAXSPEED))
          if (ca>=RMSOption.maxSpeed)
            sr = 0xFF3020;
          else if (ca>RMSOption.maxSpeed-5)
            sr = 0xFFF000;

          if (RMSOption.unitFormat==RMSOption.UNITS_NAUTICAL)
            ca*=0.5399568f;
          else if (RMSOption.unitFormat==RMSOption.UNITS_IMPERIAL)
            ca*=0.6213882f;
          GraphUtils.paintSpeed(g,(int)ca,fh,true,sr, dmaxx,dmaxy, dcx);
          // paintSpeed(g,914,fh);
            }
        if (spdMode!=SPDDIGFLMODE) {
          //s=MapUtil.speedWithNameRound1(GPSReader.SPEED_KMH);
          //s=String.valueOf(GPSReader.SPEED_KMH)+' '+LangHolder.getString(Lang.kmh);
          //g.setColor(0x10FF10);
          //g.drawString(s, dmaxx, dmaxy-fh-fh, Graphics.BOTTOM|Graphics.RIGHT );
//#debug
//#           tracePos=2.34;
          
          if (activeRoute!=null)
            if (gpsA){
//#debug
//#             tracePos=2.351;
            
            if (activeRoute.pt2Nav()!=null){
              dist = activeRoute.distFromPrecise(GPSReader.LATITUDE, GPSReader.LONGITUDE);
              //        else dist = activeRoute.distFrom(reallat, reallon);
              s=MapUtil.distWithNameRound3(dist);
              
//                String.valueOf(dist)+' '+LangHolder.getString(Lang.km);
//#debug
//#               tracePos=2.352;
//            if (gpsReader!=null)
//              if (gpsReader.fox!=null) gpsReader.fox.distance= (int)(dist*1000.);
              
              s1=activeRoute.pt2Nav().getName();
              //if (s1.length()>6) s1=s1.substring(0,6);
              s1=MapCanvas.cutStringToWidth(s1,dmaxx/2,g);
              
//#debug
//#               tracePos=2.3531;
              if ((activeRoute.kind==MapRoute.ROUTEKIND)&&(activeRoute.pt2Nav()!=activeRoute.lastPoint())
                &&(dmaxx>110)){
              s2=MapUtil.distWithNameRound3(activeRoute.distLeft2Nav(dist));
              String s3 = activeRoute.lastPoint().getName();
              s3=MapCanvas.cutStringToWidth(s3,dmaxx/2,g);
              String s4=activeRoute.activeETA(activeRoute.distLeft2Nav(dist),true);
            g.setColor(0x20FFA0);
            Font baseF = g.getFont();
            g.setFont(FontUtil.SMALLFONT);
  g.drawString(s4, dmaxx, dmaxy, Graphics.BOTTOM|Graphics.RIGHT );
              g.drawString(s3, dmaxx, dmaxy-fh-fh, Graphics.BOTTOM|Graphics.RIGHT );
              g.drawString(s2, dmaxx, dmaxy-fh, Graphics.BOTTOM|Graphics.RIGHT );
            g.setFont(baseF);
              }

//#debug
//#               tracePos=2.353;
              g.setColor(0x20FF20);

              g.drawString(s1, dminx, dmaxy-fh-fh, Graphics.BOTTOM|Graphics.LEFT );
              g.drawString(s, dminx, dmaxy-fh, Graphics.BOTTOM|Graphics.LEFT );
              s=activeRoute.activeETA();
              g.drawString(s, dminx, dmaxy, Graphics.BOTTOM|Graphics.LEFT );
              
              s=null;
              s1=null;
            }
            if (spdMode==SPDANALOGMODE) {
              
//#debug
//#               tracePos=2.354;
              
              crsr = activeRoute.courseFrom(GPSReader.LATITUDE, GPSReader.LONGITUDE)-coursradPaint;
              
              if (Math.sin(crsr-MapUtil.PIdiv2)>0) {
                if (Math.cos(crsr-MapUtil.PIdiv2)>0) crsr=MapUtil.PIdiv2;
                else crsr=MapUtil.PI+MapUtil.PIdiv2;
              }
              crsr = crsr -MapUtil.PIdiv2;
              
//#debug
//#               tracePos=2.355;
              ca=crsr+MapUtil.PIdiv2;
              cb=crsr-MapUtil.PIdiv2;
              //sr=(int)gpsReader.SPEED_KMH-2;
              
              g.setColor(0x00FF00);
              g.fillTriangle((int)(1+gpsx+(sr+ps+ps)*Math.cos(crsr)), (int)(1+gpsy+(sr+ps+ps)*Math.sin(crsr)),
                  (int)(1+gpsx+(sr+3)*Math.cos(crsr)+6*Math.cos(ca)), (int)(1+gpsy+(sr+3)*Math.sin(crsr)+6*Math.sin(ca)),
                  (int)(1+gpsx+(sr+3)*Math.cos(crsr)+6*Math.cos(cb)), (int)(1+gpsy+(sr+3)*Math.sin(crsr)+6*Math.sin(cb)));
            }
//        g.drawString(s, 0, gpsy-fh, Graphics.TOP|Graphics.LEFT );
            
            //#debug
//#             tracePos=2.356;
            
            }
        }
          } else
            //--------------STAT MODE---------------------------------------------------
            if (mode==STATMODE) {
                                drawScreenCaption(infoModeNames[currInfoModeIndex],g, fh, rr,currInfoModeIndex,infoModeNames.length);

        int addh = stdFont.getHeight()+smallFont.getHeight();
        int addhc=addh-((splitMode==0)?0:stdFont.getHeight()-1);
        g.setColor(0xFFFFFF);
        //----------------FIRST ROW-----------------------------
        s1=null;
        if (infoMode==TRACKINFOMODE){
          s1=LangHolder.getString(Lang.label);
        }else if (infoMode==ACCELINFOMODE){
          s1=LangHolder.getString(Lang.curA);
        }else if (infoMode==ALTSPEEDINFOMODE){
          s1=LangHolder.getString(Lang.curAS);
        }else if ((infoMode==ALTINFOMODE)||(infoMode==ALTGAINMODE)){
          s1=LangHolder.getString(Lang.altitude);
        }else if (infoMode==SPEEDINFOMODE){
          s1=LangHolder.getString(Lang.speed);
        }else if (infoMode==TRAVELINFOMODE){
          s1=LangHolder.getString(Lang.distance);
        }else if (infoMode==TRACKWRITEINFOMODE){
          s1=LangHolder.getString(Lang.usetrtime);
        }else if (infoMode==SYSTEMINFOMODE){
          s1=LangHolder.getString(Lang.memtotal);
        }else if (infoMode==NETRADARINFOMODE){
          s1=LangHolder.getString(Lang.radarbytes);
        }else if (infoMode==SUNINFOMODE){
          s1=LangHolder.getString(Lang.sunrise);
        }
        if (s1!=null){
          g.setFont(smallFont);
          g.drawString(s1,1,addhc,Graphics.BOTTOM|Graphics.LEFT);
          s1=null;}
        
        if (infoMode==SYSTEMINFOMODE){
          s1=String.valueOf(Runtime.getRuntime().totalMemory()/1024)+' '+'k'+'b';
       }else if (infoMode==SUNINFOMODE){
          s1=MapUtil.getSunString(System.currentTimeMillis(), reallat, reallon, true,true);
        }else if (infoMode==NETRADARINFOMODE){
          s1=String.valueOf(NetRadar.bytesDown)+"  bytes";
          //s1=String.valueOf(MapUtil.distRound2(GPSReader.DISTANCE))+LangHolder.getString(Lang.m);
        }else if (infoMode==TRACKINFOMODE){
          if (activeTrack!=null) {
            s1=activeTrack.name;
          }
        }else if (infoMode==TRACKWRITEINFOMODE ){
          if ((RMSOption.trackRecordUse&RMSOption.USETIME)==RMSOption.USETIME)
            s1=String.valueOf(RMSOption.trackPeriod/1000)+' '+LangHolder.getString(Lang.sec);
          else
            s1=LangHolder.getString(Lang.off);
        } else if (!GPSReader.statReady) {s1=MapUtil.emptyString;} else if (infoMode==ACCELINFOMODE){
          s1=String.valueOf(MapUtil.distRound2(GPSReader.CUR_A))+' '+LangHolder.getString(Lang.ms2);
        }else if (infoMode==ALTSPEEDINFOMODE){
          if (RMSOption.unitFormat==RMSOption.UNITS_METRIC)
            s1=String.valueOf(MapUtil.distRound2(GPSReader.ALTSPEED_MS))+' '+LangHolder.getString(Lang.mps);
          else
            s1=String.valueOf((int)(GPSReader.ALTSPEED_MS*196.86f))+' '+LangHolder.fpm;
        }else if ((infoMode==ALTINFOMODE)||(infoMode==ALTGAINMODE)){
          s1=MapUtil.heightWithName(GPSReader.ALTITUDE);
        }else if (infoMode==SPEEDINFOMODE){
          s1=MapUtil.speedWithNameRound1(GPSReader.SPEED_KMH);
        }else if (infoMode==TRAVELINFOMODE){
          s1=MapUtil.distWithNameRound3(RMSOption.DISTANCE*0.001);
        }
        
        if (s1!=null){
          g.setColor(0x505050);
          g.fillRect(dminx,dminy+addhc,dmaxx,fh);
          g.setColor(0xFFFFFF);
          g.setFont(stdFont);
          g.drawString(s1,dcx,addhc+fh,Graphics.BOTTOM|Graphics.HCENTER);
          g.drawLine(dminx,addhc+fh,dmaxx,addhc+fh);
        }
        //------------------SECOND ROW-----------------------------
        s1=null;
        if (infoMode==TRACKINFOMODE){
          if (activeTrack!=null) {
            s1=LangHolder.getString(Lang.totdist);
          }
        } else if (infoMode==ACCELINFOMODE){
          s1=LangHolder.getString(Lang.minA);
        }else if (infoMode==ALTSPEEDINFOMODE){
          s1=LangHolder.getString(Lang.minAS);
        }else if (infoMode==ALTINFOMODE){
          s1=LangHolder.getString(Lang.minal);
        }else if (infoMode==ALTGAINMODE){
          s1=LangHolder.getString(Lang.altgain);
        }else if (infoMode==SPEEDINFOMODE){
          s1=LangHolder.getString(Lang.minmsp);
        }else if (infoMode==TRAVELINFOMODE){
          s1=LangHolder.getString(Lang.odometer);
        }else if (infoMode==TRACKWRITEINFOMODE){
          s1=LangHolder.getString(Lang.usetrdist);
        }else if (infoMode==SYSTEMINFOMODE){
          s1=LangHolder.getString(Lang.memused);
        }else if (infoMode==NETRADARINFOMODE){
          s1=LangHolder.getString(Lang.delay);
        }else if (infoMode==SUNINFOMODE){
          s1=LangHolder.getString(Lang.sunset);
        }
        addhc+=addh;
        if (s1!=null){
          g.setFont(smallFont);
          g.setColor(0xFFFFFF);
          g.drawString(s1,1,addhc,Graphics.BOTTOM|Graphics.LEFT);
          s1=null;}
        
        if (infoMode==NETRADARINFOMODE){
          s1=String.valueOf(NetRadarIT.latency)+' '+'m'+'s';
        }else if (infoMode==SUNINFOMODE){
         s1=MapUtil.getSunString(System.currentTimeMillis(), reallat, reallon, false,true);
      }else if (infoMode==ALTGAINMODE){
          if (activeTrack!=null)
            s1=MapUtil.heightWithName(activeTrack.altGain);
        }else if (infoMode==SYSTEMINFOMODE){
          s1=String.valueOf((Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024)+' '+'k'+'b';
          
        }else if (infoMode==TRACKWRITEINFOMODE ){
          if ((RMSOption.trackRecordUse&RMSOption.USEDIST)==RMSOption.USEDIST)
            s1=String.valueOf((int)(RMSOption.trackDist*1000))+' '+LangHolder.getString(Lang.m);
          else
            s1=LangHolder.getString(Lang.off);
        } else if (infoMode==TRACKINFOMODE){
          if (activeTrack!=null) {
            //s1=MapUtil.distRound2(activeTrack.distance())+LangHolder.getString(Lang.km);
            s1=MapUtil.distWithNameRound3(activeTrack.distance());
          }
        }else if (!GPSReader.statReady) {s1=MapUtil.emptyString;} else if (infoMode==ACCELINFOMODE){
          s1=String.valueOf(MapUtil.distRound2(GPSReader.minA))+' '+LangHolder.getString(Lang.ms2);
        }else if (infoMode==ALTSPEEDINFOMODE){
          //s1=String.valueOf(MapUtil.distRound2(GPSReader.minAS))+' '+LangHolder.getString(Lang.mps);
          if (RMSOption.unitFormat==RMSOption.UNITS_METRIC)
            s1=String.valueOf(MapUtil.distRound2(GPSReader.minAS))+' '+LangHolder.getString(Lang.mps);
          //   s=String.valueOf(GPSReader.ALTSPEED_MS)+' '+LangHolder.getString(Lang.mps);
          else
            //s=String.valueOf(MapUtil.distRound2(GPSReader.ALTSPEED_MS))+' '+LangHolder.getString(Lang.mps);
            s1=String.valueOf((int)(GPSReader.minAS*196.86f))+' '+LangHolder.fpm;
        }else if (infoMode==ALTINFOMODE){
          s1=MapUtil.heightWithName(GPSReader.minAlt);
        }else if (infoMode==SPEEDINFOMODE){
          //s1=String.valueOf(MapUtil.distRound2(GPSReader.minV))+' '+LangHolder.getString(Lang.kmh);
          s1=MapUtil.speedWithNameRound1(GPSReader.minV);
        }else if (infoMode==TRAVELINFOMODE){
          s1=MapUtil.distWithNameRound3(RMSOption.ODOMETER*0.001);
          //s1=String.valueOf(MapUtil.distRound2(RMSOption.ODOMETER))+LangHolder.getString(Lang.m);
        }
        if (s1!=null){
          g.setColor(0x505050);
          g.fillRect(dminx,dminy+addhc,dmaxx,fh);
          g.setColor(0xFFFFFF);
          g.setFont(stdFont);
          g.drawString(s1,dcx,addhc+fh,Graphics.BOTTOM|Graphics.HCENTER);
          g.drawLine(dminx,addhc+fh,dmaxx,addhc+fh);
        }
        //------------------THIRD ROW-----------------------------
        s1=null;
        if (infoMode==TRACKINFOMODE){
          if (activeTrack!=null) {
            s1=LangHolder.getString(Lang.avgspeed);
          }
        } else if (infoMode==ACCELINFOMODE){
          s1=LangHolder.getString(Lang.maxA);
        }else if (infoMode==ALTSPEEDINFOMODE){
          s1=LangHolder.getString(Lang.maxAS);
        }else if (infoMode==ALTINFOMODE){
          s1=LangHolder.getString(Lang.maxal);
        }else if (infoMode==ALTGAINMODE){
          s1=LangHolder.getString(Lang.altlost);
        }else if (infoMode==SPEEDINFOMODE){
          s1=LangHolder.getString(Lang.maxmsp);
        }else if (infoMode==TRAVELINFOMODE){
          s1=LangHolder.getString(Lang.time);
        }else if (infoMode==TRACKWRITEINFOMODE){
          s1=LangHolder.getString(Lang.usetrturn);
        }else if (infoMode==SYSTEMINFOMODE){
          s1=LangHolder.getString(Lang.freemem);
        }else if (infoMode==NETRADARINFOMODE){
          s1=LangHolder.getString(Lang.time);
        }else if (infoMode==SUNINFOMODE){
          s1=LangHolder.getString(Lang.sunrise)+MapUtil.S_UTC;
        }
        addhc+=addh;
        if (s1!=null){
          g.setFont(smallFont);
          g.setColor(0xFFFFFF);
          g.drawString(s1,1,addhc,Graphics.BOTTOM|Graphics.LEFT);
          s1=null;}
        
        if (infoMode==NETRADARINFOMODE){
          s1=String.valueOf(MapUtil.time2String(NetRadarIT.runTime));
        }else if (infoMode==SUNINFOMODE){
          s1=MapUtil.getSunString(System.currentTimeMillis(), reallat, reallon, true,false);
        }else if (infoMode==SYSTEMINFOMODE){
          s1=String.valueOf(Runtime.getRuntime().freeMemory()/1024)+' '+'k'+'b';
          
        }else if (infoMode==TRACKWRITEINFOMODE ){
          if (RMSOption.addTrackPointOnTurn)
            s1=LangHolder.getString(Lang.on);
          else
            s1=LangHolder.getString(Lang.off);
        }else if (infoMode==ALTGAINMODE){
          if (activeTrack!=null)
            s1=MapUtil.heightWithName(activeTrack.altLost);
        } else if (infoMode==TRACKINFOMODE){
          if (activeTrack!=null) {
            s1=MapUtil.speedWithNameRound1(activeTrack.avgspeed());
            //s1=MapUtil.distRound2(activeTrack.avgspeed())+LangHolder.getString(Lang.kmh);
          }
        }else if (!GPSReader.statReady) {s1=MapUtil.emptyString;} else if (infoMode==ACCELINFOMODE){
          s1=String.valueOf(MapUtil.distRound2(GPSReader.maxA))+' '+LangHolder.getString(Lang.ms2);
        }else if (infoMode==ALTSPEEDINFOMODE){
          //s1=String.valueOf(MapUtil.distRound2(GPSReader.maxAS))+' '+LangHolder.getString(Lang.mps);
          if (RMSOption.unitFormat==RMSOption.UNITS_METRIC)
            s1=String.valueOf(MapUtil.distRound2(GPSReader.maxAS))+' '+LangHolder.getString(Lang.mps);
          //   s=String.valueOf(GPSReader.ALTSPEED_MS)+' '+LangHolder.getString(Lang.mps);
          else
            //s=String.valueOf(MapUtil.distRound2(GPSReader.ALTSPEED_MS))+' '+LangHolder.getString(Lang.mps);
            s1=String.valueOf((int)(GPSReader.maxAS*196.86f))+' '+LangHolder.fpm;
          
        }else if (infoMode==ALTINFOMODE){
//          s1=String.valueOf(GPSReader.maxAlt)+' '+LangHolder.getString(Lang.m);
          s1=MapUtil.heightWithName(GPSReader.maxAlt);
        }else if (infoMode==SPEEDINFOMODE){
          s1=MapUtil.speedWithNameRound1(GPSReader.maxV);
          //s1=String.valueOf(MapUtil.distRound2(GPSReader.maxV))+LangHolder.getString(Lang.kmh);
        }else if (infoMode==TRAVELINFOMODE){
          s1=MapUtil.time2String(RMSOption.USED);
        }
        if (s1!=null){
          g.setColor(0x505050);
          g.fillRect(dminx,dminy+addhc,dmaxx,fh);
          g.setColor(0xFFFFFF);
          g.setFont(stdFont);
          g.drawString(s1,dcx,addhc+fh,Graphics.BOTTOM|Graphics.HCENTER);
          g.drawLine(dminx,addhc+fh,dmaxx,addhc+fh);
        }
        //------------------FOURTH ROW-----------------------------
        s1=null;
        if (infoMode==NETRADARINFOMODE){
          s1=LangHolder.getString(Lang.avgspeed);
        }else if (infoMode==TRACKINFOMODE){
          if (activeTrack!=null) {
            s1=LangHolder.getString(Lang.points);
          }
        }else if (infoMode==SPEEDINFOMODE){
          s1=LangHolder.getString(Lang.avgspeed);
        }else if (infoMode==SYSTEMINFOMODE){
          s1=LangHolder.getString(Lang.threads);
        } else if (infoMode==TRACKWRITEINFOMODE){
          if (activeTrack!=null) {
            s1=LangHolder.getString(Lang.trautosave);
          }
        }else if (infoMode==SUNINFOMODE){
          s1=LangHolder.getString(Lang.sunset)+MapUtil.S_UTC;
        }
        addhc+=addh;
        if (s1!=null){
          g.setFont(smallFont);
          g.setColor(0xFFFFFF);
          g.drawString(s1,1,addhc,Graphics.BOTTOM|Graphics.LEFT);
          s1=null;}
        
        if (infoMode==NETRADARINFOMODE){
          if (NetRadarIT.runTime>5000)
            s1=String.valueOf(MapUtil.distRound3(NetRadar.bytesDown*3.6/NetRadarIT.runTime))+" Mb/hour";
        } else if (infoMode==SUNINFOMODE){
          s1=MapUtil.getSunString(System.currentTimeMillis(), reallat, reallon, false,false);
        }else if (infoMode==SYSTEMINFOMODE){
          s1=String.valueOf(Thread.activeCount());
        } else
          if (infoMode==TRACKINFOMODE){
          if (activeTrack!=null) {
            s1=String.valueOf(activeTrack.pts.size());
          }
          } else
            if (infoMode==TRACKWRITEINFOMODE){
          if (activeTrack!=null)
            if (gpsReader!=null)
              s1=MapUtil.time2String(gpsReader.left2autosave);
            } else if (!GPSReader.statReady) {s1=MapUtil.emptyString;} else if (infoMode==SPEEDINFOMODE){
          s1=MapUtil.speedWithNameRound1(GPSReader.avgV);
          //s1=String.valueOf(MapUtil.distRound2(GPSReader.avgV))+LangHolder.getString(Lang.kmh);
            }
        if (s1!=null){
          g.setColor(0x505050);
          g.fillRect(dminx,dminy+addhc,dmaxx,fh);
          g.setColor(0xFFFFFF);
          g.setFont(stdFont);
          g.drawString(s1,dcx,addhc+fh,Graphics.BOTTOM|Graphics.HCENTER);
          g.drawLine(dminx,addhc+fh,dmaxx,addhc+fh);
        }
        
            } else if (mode==POSITIONMODE) {
  drawScreenCaption(Lang.position,g, fh, rr,currPosModeIndex,posModes.length);

        int addh = (int)((((dmaxy-dminy)- fh*5)/2+fh/2));
        
        // if (posMode!=POSTIMEMODE)
        s1=MapUtil.coord2DatumLatString(GPSReader.LATITUDE, GPSReader.LONGITUDE,GPSReader.ALTITUDE);
        s2=MapUtil.coord2DatumLonString(GPSReader.LATITUDE, GPSReader.LONGITUDE, GPSReader.ALTITUDE);
        
//        else{
//         if (gpsReader!=null)
//            s1 = gpsReader.satDate();
//          else
//            s1 = MapUtil.emptyString;
//
//          if (gpsReader!=null)
//            s2 = gpsReader.satTime();
//          else
//            s2 = GPSReader.zeroUTC;
//        }
        
        g.setColor(0x505050);
        g.fillRect(dminx,dminy+addh,dmaxx,fh);
        g.setColor(0xFFFFFF);
        g.drawString(s1,dcx,dminy+addh,Graphics.TOP|Graphics.HCENTER);
        g.drawString(s2,dcx,dminy+fh+addh,Graphics.TOP|Graphics.HCENTER);
        if (posMode==POSCOORDMODE)
///          s1=String.valueOf(GPSReader.ALTITUDE)+' '+LangHolder.getString(Lang.m);// GPSReader.ALTITUDE_M;
          s1=MapUtil.heightWithName(GPSReader.ALTITUDE);
        else if (posMode==POSPDOPMODE)
          s1=MapUtil.S_PDOP+String.valueOf(MapUtil.coordRound3(GPSReader.PDOP+0.0001f));// GPSReader.ALTITUDE_M;
        else if (posMode==POSTIMEMODE)
          s1=MapUtil.UTC2Local(System.currentTimeMillis());
        else if (posMode==POSUTCMODE)
          s1=MapUtil.emptyString;
        g.setColor(0x000050);
        g.fillRect(dminx,dminy+fh+fh+addh,dmaxx,fh);
        g.setColor(0xFFFFFF);
        g.drawString(s1,dcx,dminy+fh+fh+addh,Graphics.TOP|Graphics.HCENTER);
        
        if (posMode==POSCOORDMODE) {
          s1=MapUtil.speedWithNameRound1(GPSReader.SPEED_KMH);
          if (gpsReader!=null)
            s2 = gpsReader.satTime();
          else
            s2 = GPSReader.zeroUTC;
        } else if (posMode==POSPDOPMODE) {
          s1=MapUtil.S_HDOP+String.valueOf(MapUtil.coordRound3(GPSReader.HDOP+0.0001f));// GPSReader.ALTITUDE_M;
          s2=MapUtil.S_VDOP+String.valueOf(MapUtil.coordRound3(GPSReader.VDOP+0.0001f));// GPSReader.ALTITUDE_M;
        } else if (posMode==POSTIMEMODE) {
          if (gpsReader!=null)
            s1 = gpsReader.satDate();
          else
            s1 = MapUtil.emptyString;
          
          if (gpsReader!=null)
            s2 = gpsReader.satTime();
          else
            s2 = GPSReader.zeroUTC;
        }else if (posMode==POSUTCMODE) {
          if (gpsReader!=null)
            s1 = MapUtil.S_UTC;
          else
            s1 = MapUtil.emptyString;

          if (gpsReader!=null)
            s2 = gpsReader.satTimeUTC();
          else
            s2 = GPSReader.zeroUTC;
        }
        
        g.drawString(s1,dcx,dminy+fh+fh+fh+addh,Graphics.TOP|Graphics.HCENTER);
        g.setColor(0x500000);
        g.fillRect(dminx,dminy+fh+fh+fh+fh+addh,dmaxx,fh);
        g.setColor(0xFFFFFF);
        g.drawString(s2,dcx,dminy+fh+fh+fh+fh+addh,Graphics.TOP|Graphics.HCENTER);
        
            } else
//------------------TRACK PROFILE----------------------------------------------------------
              if (mode==TRACKELEVATIONMODE) {
              drawScreenCaption(((prfMode==PRFALTMODE)||(prfMode==PRFALTTIMEMODE))?
                Lang.trheight  :Lang.trspeed
                ,g, fh, rr,currPrfModeIndex,prfModes.length);

//        if (splitMode==0){
//          g.setColor(0x008000);
//          g.fillRoundRect(MapCanvas.dminx,MapCanvas.dminy,MapCanvas.dmaxx,fh,10,10);
//          g.setColor(0xFFFFFF);
//          if ((prfMode==PRFALTMODE)||(prfMode==PRFALTTIMEMODE))
//            g.drawString(LangHolder.getString(Lang.trheight),MapCanvas.dcx,MapCanvas.dminy,Graphics.HCENTER|Graphics.TOP);
//          else
//            g.drawString(LangHolder.getString(Lang.trspeed),MapCanvas.dcx,MapCanvas.dminy,Graphics.HCENTER|Graphics.TOP);
//
//          g.fillTriangle(fh/4,fh/2,fh/2,fh*3/4,fh/2,fh/4);
//          g.fillTriangle(fh/2+fh/2,fh/2,fh/2+fh/4,fh*3/4,fh/2+fh/4,fh/4);
//          g.drawLine(fh/4+(dmaxx-fh/2)*currPrfModeIndex/prfModes.length,
//              fh,
//              fh/4+(dmaxx-fh/2)*(currPrfModeIndex+1)/prfModes.length,
//              fh
//              );
//        }
        drawHeightWay(g,to);
              } else {
        
        int xC=xCenter;
        int yC=yCenter;
        int xN=MapUtil.getNumX(xC);
        int yN=MapUtil.getNumY(yC);
        int xtCb = xC-(xC/MapUtil.blockSize)*MapUtil.blockSize;
        int ytCb = yC-(yC/MapUtil.blockSize)*MapUtil.blockSize;
        int xtC;
        int ytC;
        minXViewable=xN-1;
	minYViewable=yN-1;
	maxXViewable=xN+1;
	maxYViewable=yN+1;

//#debug
//#         tracePos=2.41;
        
        
        int mapXC=0; //= (showMapSer==MapTile.SHOW_MP)?mapXCorrect:0;
        int mapYC=0; //= showMapSer==MapTile.SHOW_MP)?mapYCorrect:0;
        if ((showMapSer==MapTile.SHOW_MP)||(RMSOption.correctMapAll)){
          mapXC=mapXCorrect;
          mapYC=mapYCorrect;
          if ((mapLCorrect>0)&&(mapLCorrect!=level))
            if (mapLCorrect<level){
             mapXC = mapXC<<(level-mapLCorrect);
             mapYC = mapYC<<(level-mapLCorrect);
            } else {
             mapXC = mapXC>>(mapLCorrect-level);
             mapYC = mapYC>>(mapLCorrect-level);
            }
        }
        
        if (mode==MAPMODE) {
          
          Graphics gt=g;
          MapTile mt;
          int maxd;
          int xCs=xCenter,yCs=yCenter,dcxs=dcx,dcys=dcy,dmaxxs=dmaxx,dmaxys=dmaxy;
          boolean rotError=false;
          
          if (!((showMapSer==MapTile.SHOW_MP)&&(userMapIndexUsed==0)))
            try {
              if (rotateMap) {
                maxd = (int)(Math.sqrt(dmaxx*dmaxx+dmaxy*dmaxy));
                if (RMSOption.limitImgRot) if (maxd>256) maxd=256;
                
//#debug
//#                 tracePos=2.4105;
                if (imgR==null) {
                  imgR = Image.createImage(maxd,maxd);
                } else
                  if (imgR.getWidth()!=maxd)
                    imgR = Image.createImage(maxd,maxd);
                
//#debug
//#                 tracePos=2.4107;
                //rI = Image.createImage(dmaxx,dmaxy);
                g = imgR.getGraphics();
                g.setColor(0);
                g.fillRect(0,0,maxd-1,maxd-1);
//#debug
//#                 tracePos=2.411;
                
                //xCenter = xCenter+(maxd-dmaxx)/2;
                //yCenter = yCenter+(maxd-dmaxy)/2;
                dmaxx=maxd;
                dmaxy=maxd;
                dcx=dmaxx/2;
                dcy=dcx;
                
                xC=xCenter;
                yC=yCenter;
                xN=MapUtil.getNumX(xC);
                yN=MapUtil.getNumY(yC);
                xtCb = xC-(xC/MapUtil.blockSize)*MapUtil.blockSize;
                ytCb = yC-(yC/MapUtil.blockSize)*MapUtil.blockSize;
              } else imgR=null;
              
//#debug
//#               tracePos=2.4141;
              //---------------SUR---------------------------------
              if ((showMapView&MapTile.SHOW_SUR)==MapTile.SHOW_SUR) {
                mt = getTile(xN, yN,MapTile.SHOW_SUR);
//#debug
//#                 tracePos=2.415;
                if (mt!=null) mt.drawTile(g, dcx-xtCb+mapXC, dcy-ytCb+mapYC,dminx,dmaxx,dminy,dmaxy); //!
//#debug
//#                 tracePos=2.42;
                
                if (!RMSOption.safeMode) {
                  
                  if ((dcx-xtCb)>0) { //слева
                    xC=xCenter-MapUtil.blockSize;
                    yC=yCenter;
                    xN=MapUtil.getNumX(xC);
                    yN=MapUtil.getNumY(yC);
                    xtC = xC-(xC/MapUtil.blockSize)*MapUtil.blockSize+MapUtil.blockSize;
                    ytC = yC-(yC/MapUtil.blockSize)*MapUtil.blockSize;
                    mt = getTile(xN, yN,MapTile.SHOW_SUR);
                    if (mt!=null) mt.drawTile(g, dcx-xtC+mapXC, dcy-ytC+mapYC,dminx,dmaxx,dminy,dmaxy);//!

                  }
                  
                  if ((dcx-xtCb+MapUtil.blockSize)<dmaxx) {
                    xC=xCenter+MapUtil.blockSize;
                    yC=yCenter;
                    xN=MapUtil.getNumX(xC);
                    yN=MapUtil.getNumY(yC);
                    xtC = xC-(xC/MapUtil.blockSize)*MapUtil.blockSize-MapUtil.blockSize;
                    ytC = yC-(yC/MapUtil.blockSize)*MapUtil.blockSize;
                    mt = getTile(xN, yN,MapTile.SHOW_SUR);
                    if (mt!=null) mt.drawTile(g, dcx-xtC+mapXC, dcy-ytC+mapYC,dminx,dmaxx,dminy,dmaxy);//!
                  }
                  
                  if ((dcy-ytCb)>0) {
                    yC=yCenter-MapUtil.blockSize;//выше
                    yN=MapUtil.getNumY(yC);
                    ytC = yC-(yC/MapUtil.blockSize)*MapUtil.blockSize+MapUtil.blockSize;
                    
                    if ((dcx-xtCb)>0) {
                      xC=xCenter-MapUtil.blockSize;//левее
                      xN=MapUtil.getNumX(xC);
                      xtC = xC-(xC/MapUtil.blockSize)*MapUtil.blockSize+MapUtil.blockSize;
                      mt = getTile(xN, yN,MapTile.SHOW_SUR);
                      if (mt!=null) mt.drawTile(g, dcx-xtC+mapXC, dcy-ytC+mapYC,dminx,dmaxx,dminy,dmaxy);
                    }
                    
                    if ((dcx-xtCb+MapUtil.blockSize)<dmaxx) {
                      xC=xCenter+MapUtil.blockSize;
                      xN=MapUtil.getNumX(xC);
                      xtC = xC-(xC/MapUtil.blockSize)*MapUtil.blockSize-MapUtil.blockSize;
                      mt = getTile(xN, yN,MapTile.SHOW_SUR);
                      if (mt!=null) mt.drawTile(g, dcx-xtC+mapXC, dcy-ytC+mapYC,dminx,dmaxx,dminy,dmaxy);//!
                    }
                    
                    xC=xCenter;
                    xN=MapUtil.getNumX(xC);
                    xtC = xC-(xC/MapUtil.blockSize)*MapUtil.blockSize;
                    mt = getTile(xN, yN,MapTile.SHOW_SUR);
                    if (mt!=null) mt.drawTile(g, dcx-xtC+mapXC, dcy-ytC+mapYC,dminx,dmaxx,dminy,dmaxy);//!
                  }
                  
                  if ((dcy-ytCb+MapUtil.blockSize)<dmaxy) {
                    yC=yCenter+MapUtil.blockSize;
                    yN=MapUtil.getNumY(yC);
                    ytC = yC-(yC/MapUtil.blockSize)*MapUtil.blockSize-MapUtil.blockSize;
                    
                    if ((dcx-xtCb)>0) {
                      xC=xCenter-MapUtil.blockSize;
                      xN=MapUtil.getNumX(xC);
                      xtC = xC-(xC/MapUtil.blockSize)*MapUtil.blockSize+MapUtil.blockSize;
                      mt = getTile(xN, yN,MapTile.SHOW_SUR);
                      if (mt!=null) mt.drawTile(g, dcx-xtC+mapXC, dcy-ytC+mapYC,dminx,dmaxx,dminy,dmaxy);
                    }
                    
                    if ((dcx-xtCb+MapUtil.blockSize)<dmaxx) {
                      xC=xCenter+MapUtil.blockSize;
                      xN=MapUtil.getNumX(xC);
                      xtC = xC-(xC/MapUtil.blockSize)*MapUtil.blockSize-MapUtil.blockSize;
                      mt = getTile(xN, yN,MapTile.SHOW_SUR);
                      if (mt!=null) mt.drawTile(g, dcx-xtC+mapXC, dcy-ytC+mapYC,dminx,dmaxx,dminy,dmaxy);
                    }
                    
                    xC=xCenter;
                    xN=MapUtil.getNumX(xC);
                    xtC = xC-(xC/MapUtil.blockSize)*MapUtil.blockSize;
                    mt = getTile(xN, yN,MapTile.SHOW_SUR);
                    if (mt!=null) mt.drawTile(g, dcx-xtC+mapXC, dcy-ytC+mapYC,dminx,dmaxx,dminy,dmaxy);
                  }
                }
                
                if (RMSOption.mapCorrectMode)
                  if (showMapSer!=MapTile.SHOW_MP) {
                  xC=xCenter;
                  yC=yCenter;
                  xN=MapUtil.getNumX(xC);
                  yN=MapUtil.getNumY(yC);
                  xtC = xC-(xC/MapUtil.blockSize)*MapUtil.blockSize;
                  ytC = yC-(yC/MapUtil.blockSize)*MapUtil.blockSize;
                  byte ssms = showMapSer;
                  showMapSer=MapTile.SHOW_MP;
                  try{
                    mt = getTile(xN, yN,MapTile.SHOW_SUR);
                  }finally{showMapSer=ssms;}
                  if (mt!=null) mt.drawTile(g, dcx-xtC+mapXCorrect, dcy-ytC+mapYCorrect,dminx,dmaxx,dminy,dmaxy);
                  }
                  
              }
//#debug
//#               tracePos=2.43;
              
              //---------------MAP---------------------------------
              if ((showMapView&MapTile.SHOW_MAP)==MapTile.SHOW_MAP) {
                xC=xCenter;
                yC=yCenter;
                xN=MapUtil.getNumX(xC);
                yN=MapUtil.getNumY(yC);
                xtCb = xC-(xC/MapUtil.blockSize)*MapUtil.blockSize;
                ytCb = yC-(yC/MapUtil.blockSize)*MapUtil.blockSize;
                
                
                mt = getTile(xN, yN,showMapView);
                if (mt!=null) mt.drawTile(g, dcx-xtCb+mapXC, dcy-ytCb+mapYC,dminx,dmaxx,dminy,dmaxy);
                
                if (!RMSOption.safeMode) {
                  
                  if ((dcx-xtCb)>0) {
                    xC=xCenter-MapUtil.blockSize;
                    yC=yCenter;
                    xN=MapUtil.getNumX(xC);
                    yN=MapUtil.getNumY(yC);
                    xtC = xC-(xC/MapUtil.blockSize)*MapUtil.blockSize+MapUtil.blockSize;
                    ytC = yC-(yC/MapUtil.blockSize)*MapUtil.blockSize;
                    mt = getTile(xN, yN,showMapView);
                    if (mt!=null) mt.drawTile(g, dcx-xtC+mapXC, dcy-ytC+mapYC,dminx,dmaxx,dminy,dmaxy);
                  }
                  
                  if ((dcx-xtCb+MapUtil.blockSize)<dmaxx) {
                    xC=xCenter+MapUtil.blockSize;
                    yC=yCenter;
                    xN=MapUtil.getNumX(xC);
                    yN=MapUtil.getNumY(yC);
                    xtC = xC-(xC/MapUtil.blockSize)*MapUtil.blockSize-MapUtil.blockSize;
                    ytC = yC-(yC/MapUtil.blockSize)*MapUtil.blockSize;
                    mt = getTile(xN, yN,showMapView);
                    if (mt!=null) mt.drawTile(g, dcx-xtC+mapXC, dcy-ytC+mapYC,dminx,dmaxx,dminy,dmaxy);
                  }
                  
                  if ((dcy-ytCb)>0) {
                    yC=yCenter-MapUtil.blockSize;
                    yN=MapUtil.getNumY(yC);
                    ytC = yC-(yC/MapUtil.blockSize)*MapUtil.blockSize+MapUtil.blockSize;
                    
                    if ((dcx-xtCb)>0) {
                      xC=xCenter-MapUtil.blockSize;
                      xN=MapUtil.getNumX(xC);
                      xtC = xC-(xC/MapUtil.blockSize)*MapUtil.blockSize+MapUtil.blockSize;
                      mt = getTile(xN, yN,showMapView);
                      if (mt!=null) mt.drawTile(g, dcx-xtC+mapXC, dcy-ytC+mapYC,dminx,dmaxx,dminy,dmaxy);
                    }
                    
                    if ((dcx-xtCb+MapUtil.blockSize)<dmaxx) {
                      xC=xCenter+MapUtil.blockSize;
                      xN=MapUtil.getNumX(xC);
                      xtC = xC-(xC/MapUtil.blockSize)*MapUtil.blockSize-MapUtil.blockSize;
                      mt = getTile(xN, yN,showMapView);
                      if (mt!=null) mt.drawTile(g, dcx-xtC+mapXC, dcy-ytC+mapYC,dminx,dmaxx,dminy,dmaxy);
                    }
                    
                    xC=xCenter;
                    xN=MapUtil.getNumX(xC);
                    xtC = xC-(xC/MapUtil.blockSize)*MapUtil.blockSize;
                    mt = getTile(xN, yN,showMapView);
                    if (mt!=null) mt.drawTile(g, dcx-xtC+mapXC, dcy-ytC+mapYC,dminx,dmaxx,dminy,dmaxy);
                  }
                  
                  if ((dcy-ytCb+MapUtil.blockSize)<dmaxy) {
                    yC=yCenter+MapUtil.blockSize;
                    yN=MapUtil.getNumY(yC);
                    ytC = yC-(yC/MapUtil.blockSize)*MapUtil.blockSize-MapUtil.blockSize;
                    
                    if ((dcx-xtCb)>0) {
                      xC=xCenter-MapUtil.blockSize;
                      xN=MapUtil.getNumX(xC);
                      xtC = xC-(xC/MapUtil.blockSize)*MapUtil.blockSize+MapUtil.blockSize;
                      mt = getTile(xN, yN,showMapView);
                      if (mt!=null) mt.drawTile(g, dcx-xtC+mapXC, dcy-ytC+mapYC,dminx,dmaxx,dminy,dmaxy);
                    }
                    
                    if ((dcx-xtCb+MapUtil.blockSize)<dmaxx) {
                      xC=xCenter+MapUtil.blockSize;
                      xN=MapUtil.getNumX(xC);
                      xtC = xC-(xC/MapUtil.blockSize)*MapUtil.blockSize-MapUtil.blockSize;
                      mt = getTile(xN, yN,showMapView);
                      if (mt!=null) mt.drawTile(g, dcx-xtC+mapXC, dcy-ytC+mapYC,dminx,dmaxx,dminy,dmaxy);
                    }
                    
                    xC=xCenter;
                    xN=MapUtil.getNumX(xC);
                    xtC = xC-(xC/MapUtil.blockSize)*MapUtil.blockSize;
                    mt = getTile(xN, yN,showMapView);
                    if (mt!=null) mt.drawTile(g, dcx-xtC+mapXC, dcy-ytC+mapYC,dminx,dmaxx,dminy,dmaxy);
                  }
                }
              }
              
//#debug
//#               tracePos=2.431;
              try {
                if (rotateMap) {
//#debug
//#                   tracePos=2.432;
                  
                  //Определяем размер исходной картинки
                  int ImW=imgR.getWidth();     //Ширина
                  int ImH=ImW;     //Высота
                  //Создаем два ARGB массива
                  
                  checkImgArrays(ImW*ImH);
     
//#debug
//#                   tracePos=2.433;
//Загружаем в первый массив нашу картинку.
                  imgR.getRGB(ARGB_Img0,0,ImW,0,0,ImW,ImH);
                  //MapUtil.ARGB_Img_rot(ARGB_Img0,ARGB_Img1,testR,ImW,ImH,xCenter-xCs,yCenter-yCs);
                  //if (rotateMap)
                  MapUtil.ARGB_Img_rot(ARGB_Img0,ARGB_Img1,-coursradPaint,ImW,ImH,xCenter-xCs,yCenter-yCs);
                  //MapUtil.ARGB_Img_rot(ARGB_Img0,ARGB_Img1,0,ImW,ImH,xCenter-xCs,yCenter-yCs);
//                  if (rotateMap&&RMSOption.doubleScaleMap){
//                  int[] ia = ARGB_Img0;
//                  ARGB_Img0=ARGB_Img1;
//                  ARGB_Img1=ia;
//                  MapUtil.ARGB_Img_DblScale(ARGB_Img0,ARGB_Img1,ImW,ImH);
//                  }
//                  if (RMSOption.doubleScaleMap){
//                  MapUtil.ARGB_Img_DblScale(ARGB_Img0,ARGB_Img1,ImW,ImH);
//                  }
//#debug
//#                   tracePos=2.434;
                  gt.drawRGB(ARGB_Img1,0,ImW,dcxs-ImW/2,dcys-ImH/2,ImW,ImH,false);
//#debug
//#                   tracePos=2.435;
                }                 
              }catch(Throwable t) {
               
//#mdebug
//#                 if (RMSOption.debugEnabled)
//#                   DebugLog.add2Log("MC RM:"+String.valueOf(tracePos)+" m:"+String.valueOf(mode)+" - " + t);
//#enddebug
                try{gt.drawImage(imgR,0,0,Graphics.TOP|Graphics.LEFT);}catch(Throwable t1){}
                rotateMap=false;
                compassOverMap=false;
                rotError=true;
                RMSOption.mapRotate=false;
                gt.setColor(0xFF0000);
                gt.drawString("Out of memory!", dmaxx, dcy, Graphics.TOP|Graphics.RIGHT);
              }
              
            } finally {
              if (rotateMap||rotError) {
                g=gt;
                dmaxx = dmaxxs;
                dmaxy = dmaxys;
                dcx = dcxs;
                dcy = dcys;
                xCenter=xCs;
                yCenter=yCs;
              }
              
            }
//#debug
//#             tracePos=2.44;
            if (RMSOption.getByteOpt(RMSOption.BO_SCALETYPE)!=0) {
              Font wasF = g.getFont();
              g.setFont(FontUtil.SMALLFONT);
              int fhs = FontUtil.SMALLFONT.getHeight()/2;
//#debug
//#               tracePos=2.441;
              if ((dmaxx-dminx<160)||(RMSOption.getByteOpt(RMSOption.BO_SCALETYPE)==RMSOption.SCALE_LINES))
              setScale(reallat, level, (dmaxx-dminx)/3);
              else
              setScale(reallat, level, (dmaxx-dminx)/4);
              int sWdiv2=scaleWidth/2;
              
              g.setColor(0x303030);
              if (RMSOption.getByteOpt(RMSOption.BO_SCALETYPE)==RMSOption.SCALE_LINES)
                for (int i=0;i<8;i++) {
                g.drawLine(dcx-sWdiv2-i*scaleWidth+1, dminy+1, dcx-sWdiv2-i*scaleWidth+1, dmaxy+1);
                g.drawLine(dcx+sWdiv2+i*scaleWidth+1, dminy+1, dcx+sWdiv2+i*scaleWidth+1, dmaxy+1);
                g.drawLine(dminx+1, dcy-sWdiv2-i*scaleWidth+1, dmaxx+1, dcy-sWdiv2-i*scaleWidth+1);
                g.drawLine(dminx+1, dcy+sWdiv2+i*scaleWidth+1, dmaxx+1, dcy+sWdiv2+i*scaleWidth+1);
                } else
                  for (int i=1;i<10;i++) {
                g.drawArc(gpsx-i*scaleWidth-1,gpsy-i*scaleWidth-1,scaleWidth*i*2+2,scaleWidth*i*2+2,0,360);

                // g.drawArc(dcx-i*scaleWidth-1,dcy-i*scaleWidth-1,scaleWidth*i*2+2,scaleWidth*i*2+2,0,360);
                //    g.drawArc(dcx-i*scaleWidth-1,dcy-i*scaleWidth+1,scaleWidth*i*2,scaleWidth*i*2,0,360);
                  }
              
              g.setColor(0xD0D0D0);
              if (RMSOption.getByteOpt(RMSOption.BO_SCALETYPE)==RMSOption.SCALE_LINES)
                for (int i=0;i<8;i++) {
                g.drawLine(dcx-sWdiv2-i*scaleWidth, dminy, dcx-sWdiv2-i*scaleWidth, dmaxy);
                g.drawLine(dcx+sWdiv2+i*scaleWidth, dminy, dcx+sWdiv2+i*scaleWidth, dmaxy);
                g.drawLine(dminx, dcy-sWdiv2-i*scaleWidth, dmaxx, dcy-sWdiv2-i*scaleWidth);
                g.drawLine(dminx, dcy+sWdiv2+i*scaleWidth, dmaxx, dcy+sWdiv2+i*scaleWidth);
                } else
                  for (int i=1;i<10;i++) {
                g.drawArc(gpsx-i*scaleWidth,gpsy-i*scaleWidth,scaleWidth*i*2,scaleWidth*i*2,0,360);
                if ((i%2) == 0){
                  s1=String.valueOf(scaleWeight*i);
                  drawMapStringC(g, s1, gpsx, gpsy-scaleWidth*i-1,Graphics.BOTTOM|Graphics.HCENTER);
                  drawMapStringC(g, s1, gpsx, gpsy+scaleWidth*i,Graphics.TOP|Graphics.HCENTER);
                  drawMapStringC(g, s1, gpsx+scaleWidth*i+1, gpsy - fhs,Graphics.TOP|Graphics.LEFT);
                  drawMapStringC(g, s1, gpsx-scaleWidth*i-1, gpsy - fhs,Graphics.TOP|Graphics.RIGHT);
                }
                  }
              
              
//#debug
//#               tracePos=2.442;
              s=String.valueOf(scaleWeight)+scaleMeas;
              int dsy=
                  (RMSOption.getByteOpt(RMSOption.BO_SCALETYPE)==RMSOption.SCALE_LINES)
                  ? dcy+sWdiv2:gpsy+sWdiv2+sWdiv2;
              int dsx=
                  (RMSOption.getByteOpt(RMSOption.BO_SCALETYPE)==RMSOption.SCALE_LINES)
                  ? dcx:gpsx;
              drawMapString(g,s,dsx, dsy,Graphics.TOP|Graphics.HCENTER);
//              g.setColor(0x0);
//              g.drawString(s, dcx+1, 1+dsy, Graphics.TOP|Graphics.HCENTER );
//              g.drawString(s, dcx, dsy, Graphics.TOP|Graphics.HCENTER );
//              //g.drawString(s, dcx-1, -1+dcy+sWdiv2, Graphics.TOP|Graphics.HCENTER );
//              g.setColor(0xFFFFFF);
//              g.drawString(s, dcx, dsy, Graphics.TOP|Graphics.HCENTER );
              
              
              s=null;
              g.setFont(wasF);
              //g.drawLine(dcx-sWdiv2, dsy, dcx-sWdiv2, dsy-3);
              //g.drawLine(dcx+sWdiv2, dsy, dcx+sWdiv2, dsy-3);
              //g.drawLine(dcx-sWdiv2, dsy, dcx+sWdiv2, dsy);
            }
            
//#debug
//#             tracePos=2.443;
            if (RMSOption.showMarks)
              if (marks.size()!=0) {
              Font f = g.getFont();
              g.setFont(smallFont);
//#debug
//#               tracePos=2.444;
              try {
                MapMark m,cm=null;
                boolean dA;
                int mind=50,md;
                for (int i=marks.size()-1;i>=0;i--) {
                  m = (MapMark) marks.elementAt(i);
                  xtC=MapUtil.getXMap(m.lon,level)-xCenter+dcx;
                  ytC=MapUtil.getYMap(m.lat,level)-yCenter+dcy;
                  if (rotateMap) {
                    int x22=xtC,y22=ytC;
                    xtC=rotateMX(x22,y22);
                    ytC=rotateMY(x22,y22);
                  }
                  if (isOn2Screen(xtC,ytC)) {
                    md=(Math.abs(dcx-xtC))+(Math.abs(dcy-ytC));
                    if ((md<14)&&(md<mind)) {mind=md;cm=m;}
                    drawMark(g,xtC,ytC,m.name,false);
                  }
                }
                if (cm!=null){
                  xtC=MapUtil.getXMap(cm.lon,level)-xCenter+dcx;
                  ytC=MapUtil.getYMap(cm.lat,level)-yCenter+dcy;
                  if (rotateMap) {
                    int x22=xtC,y22=ytC;
                    xtC=rotateMX(x22,y22);
                    ytC=rotateMY(x22,y22);
                  }
                  drawMark(g,xtC,ytC,cm.name,true);
                }
                
//#debug
//#                 tracePos=2.445;
              }finally {g.setFont(f);}
              }
//#debug
//#             tracePos=2.45;
            if(!hideTrack)
            if (activeTrack!=null) activeTrack.drawRoute(g, dcx, dcy, rotateMap);
//#debug
//#             tracePos=2.51;
            g.setFont(smallFont);
            for (int i=0;i<kmlRoutes.size();i++){
              KMLMapRoute mr = (KMLMapRoute)kmlRoutes.elementAt(i);
              if (mr.active) mr.drawRoute(g, dcx, dcy);
            }
            
//#debug
//#             tracePos=2.515;
            if (activeRoute!=null) activeRoute.drawRoute(g, dcx, dcy, rotateMap);
            
            g.setFont(stdFont);
//#debug
//#             tracePos=2.52;
            
            if (NetRadar.netRadar!=null) {
              drawNetUsers(g);
            }
            if (!RMSOption.getBoolOpt(RMSOption.BL_CROSS_OFF)) {
//#debug
//#               tracePos=2.535;
              if (gpsBinded) {
                g.setColor(0xD0D0D0);
                int cs = dmaxx/24, cs2 = dmaxx/12;
                g.drawLine(dcx-cs, dcy-cs, dcx-cs2, dcy-cs2);
                g.drawLine(dcx+cs, dcy+cs, dcx+cs2, dcy+cs2);
                g.drawLine(dcx-cs, dcy+cs, dcx-cs2, dcy+cs2);
                g.drawLine(dcx+cs, dcy-cs, dcx+cs2, dcy-cs2);
                g.setColor(0x202020);
                g.drawLine(dcx-cs2, dcy-cs2, dcx-cs-cs2, dcy-cs-cs2);
                g.drawLine(dcx+cs2, dcy+cs2, dcx+cs+cs2, dcy+cs+cs2);
                g.drawLine(dcx-cs2, dcy+cs2, dcx-cs-cs2, dcy+cs+cs2);
                g.drawLine(dcx+cs2, dcy-cs2, dcx+cs2+cs, dcy-cs-cs2);
              } else {
                g.setColor(0xFFFF50);
                int cs = dmaxx/24, cs2 = dmaxx/12;
                g.drawLine(dcx, dcy-cs, dcx, dcy-cs2);
                g.drawLine(dcx, dcy+cs, dcx, dcy+cs2);
                g.drawLine(dcx-cs, dcy, dcx-cs2, dcy);
                g.drawLine(dcx+cs, dcy, dcx+cs2, dcy);
                g.setColor(0x202020);
                g.drawLine(dcx, dcy-cs2, dcx, dcy-cs-cs2);
                g.drawLine(dcx, dcy+cs2, dcx, dcy+cs+cs2);
                g.drawLine(dcx-cs2, dcy, dcx-cs-cs2, dcy);
                g.drawLine(dcx+cs2, dcy, dcx+cs+cs2, dcy);
              }
            }
            if (RMSOption.showCoords) {
//#debug
//#               tracePos=2.536;
              if (gpsA&&gpsBinded){
                s1=MapUtil.coord2DatumLatString(GPSReader.LATITUDE, GPSReader.LONGITUDE,GPSReader.ALTITUDE);
                s2=MapUtil.coord2DatumLonString(GPSReader.LATITUDE, GPSReader.LONGITUDE,GPSReader.ALTITUDE);
              } else{
                s1=MapUtil.coord2DatumLatString(reallat, reallon,0);
                s2=MapUtil.coord2DatumLonString(reallat, reallon, 0);
              }
              
//#debug
//#               tracePos=2.537;
              
              drawMapString(g,s1,dmaxx, dmaxy-fh, Graphics.BOTTOM|Graphics.RIGHT);
              drawMapString(g,s2,dmaxx, dmaxy, Graphics.BOTTOM|Graphics.RIGHT);
            }
//#debug
//#             tracePos=2.538;
            if (System.currentTimeMillis()<lastViewLabelDisplayTime){
            s=serViewLabel;
            stdFont = g.getFont();
            g.setFont(smallFont);
            
            drawMapString(g,s,dminx+g.getFont().stringWidth(MapUtil.SH_NORTHWEST), dminy,Graphics.TOP|Graphics.LEFT);
            
            g.setFont(stdFont);
            }
            //stdFont=null;
            s=null;
            s1=null;
            s2=null;
//#debug
//#             tracePos=2.54;
            
            if (MapTile.queueSize()>0) {
              g.setFont(smallFont);
              s1="("+MapTile.queueSize()+')'+' '+Integer.toString(loadedBytes/1024)+"kb";
              
              drawShadowString(g,s1,dmaxx-1, dminy+fh+1,0xFF0000, Graphics.TOP|Graphics.RIGHT,0);
              
              g.setFont(stdFont);
              s1=null;
              //s=null;
            }
            
            if (RMSOption.mapCorrectMode) {
              s="dX = "+String.valueOf(mapXCorrect);
              s1="dY = "+String.valueOf(mapYCorrect);
              
              drawShadowString(g,s,dmaxx-1,dcy,0xFF0000, Graphics.BOTTOM|Graphics.RIGHT,0xFFFFFF);
              drawShadowString(g,s1,dmaxx-1,dcy,0xFF0000, Graphics.TOP|Graphics.RIGHT,0xFFFFFF);
              
            }
            
        }
        
//#debug
//#         tracePos=2.541;
        if (mode==COMPASSMODE||compassOverMap) {
          int cd;
          if ((dmaxx-dminx+15)<(dmaxy-dminy))
            cd = (int)((dmaxx-dminx)/1.29);
          else
            cd = (int)((dmaxy-dminy-20)/1.29);
          
          //if (compassOverMap) cd = (int) ((float)cd/1.2);
          
          sr=cd/2;
          int ps=cd/12;
          if (mode==COMPASSMODE) {
            //#if SE_K750_E_BASEDEV
//#else
            g.setColor(0x505050);
            g.fillArc(dcx-sr,dcy-sr,cd+1,cd+1,0,360);
            g.setColor(0x404040);
            g.fillArc(dcx-sr+ps,dcy-sr+ps,cd+1-ps-ps,cd+1-ps-ps,0,360);
            g.setColor(0x303030);
            g.fillArc(dcx-sr+ps+ps/2,dcy-sr+ps+ps/2,cd+1-ps-ps-ps,cd+1-ps-ps-ps,0,360);
            //#endif
            
            g.setColor(0x00A000);
            g.fillTriangle(dcx-ps,dcy+sr+2,dcx+ps,dcy+sr+2,dcx,dcy-sr+ps+ps);
          }
          //draws WAYPOINT POINTER ON COMPASS
          if (activeRoute!=null) {
            if (gpsA) {
              //if (gpsReader==null)
              //   acrsr = -coursradPaint+activeRoute.courseFrom(reallat, reallon)-MapUtil.PIdiv2;
              // else //fixed compass waypointer
              if (mode==MAPMODE&!(gpsBinded))
                acrsr = -coursradPaint+activeRoute.courseFrom(reallat, reallon)-MapUtil.PIdiv2;
              else acrsr = -coursradPaint+activeRoute.courseFrom(GPSReader.LATITUDE, GPSReader.LONGITUDE)-MapUtil.PIdiv2;
              
              crsr = acrsr+MapUtil.PI;
              ca=crsr+MapUtil.PIdiv2;
              cb=crsr-MapUtil.PIdiv2;
              int adcx = (int)(dcx+sr*Math.cos(acrsr)+1);
              int adcy = (int)(dcy+sr*Math.sin(acrsr)+2);
              
              g.setColor(0x20FF10);
              ps++;
              g.fillTriangle((int)(adcx+2*ps*Math.cos(crsr)+1), (int)(adcy+2*ps*Math.sin(crsr)+1),
                  (int)(adcx+ps*Math.cos(ca)+1), (int)(adcy+ps*Math.sin(ca)+1),
                  (int)(adcx+ps*Math.cos(cb)+1), (int)(adcy+ps*Math.sin(cb)+1));
              ps--;
              
              if (GPSReader.mvReady &&(mode!=MAPMODE)){
                acrsr = -GPSReader.COURSE_MOVE_RAD+activeRoute.courseFrom(GPSReader.LATITUDE, GPSReader.LONGITUDE)-MapUtil.PIdiv2;
                
                crsr = acrsr+MapUtil.PI;
                ca=crsr+MapUtil.PIdiv2;
                cb=crsr-MapUtil.PIdiv2;
                adcx = (int)(dcx+sr*Math.cos(acrsr)+1);
                adcy = (int)(dcy+sr*Math.sin(acrsr)+2);
                
                g.setColor(0xC0C020);
                g.fillTriangle((int)(adcx+2*ps*Math.cos(crsr)+1), (int)(adcy+2*ps*Math.sin(crsr)+1),
                    (int)(adcx+ps*Math.cos(ca)+1), (int)(adcy+ps*Math.sin(ca)+1),
                    (int)(adcx+ps*Math.cos(cb)+1), (int)(adcy+ps*Math.sin(cb)+1));
                
              }
            }
          }
          crsr = -coursradPaint-MapUtil.PIdiv2;
          
//#debug
//#           tracePos=2.5411;
          if ((compassOverMap)&&(mode==MAPMODE)) {
            g.setColor(RMSOption.shadowColor);
            //base
            g.drawArc(dcx-sr+1,dcy-sr+1,cd+1,cd+1,0,360);
            //smaller
            g.drawArc(dcx-sr+1+1,dcy-sr+1+1,cd-1,cd-1,0,360);
            //
            g.drawArc(dcx-sr+1,dcy-sr+1,cd,cd,0,360);
            //ld
            g.drawArc(dcx-sr+1+1,dcy-sr+1+1,cd,cd,0,360);
            //d
            g.drawArc(dcx-sr+1,dcy-sr+1+1,cd,cd,0,360);
            //l
            g.drawArc(dcx-sr+1+1,dcy-sr+1,cd,cd,0,360);
            
            if (cd>150){
              //greater
              g.drawArc(dcx-sr,dcy-sr,cd+2,cd+2,0,360);
              //
              g.drawArc(dcx-sr-1,dcy-sr-1,cd+2,cd+2,0,360);
              //ld
              g.drawArc(dcx-sr,dcy-sr,cd+2,cd+2,0,360);
              //d
              g.drawArc(dcx-sr-1,dcy-sr,cd+2,cd+2,0,360);
              //l
              g.drawArc(dcx-sr,dcy-sr-1,cd+2,cd+2,0,360);
            }
            
            for (int r=0;r<24;r++) {
              if ((r==3)||(r==9)||(r==15)||(r==21))
                g.drawLine((int)(dcx+sr*Math.cos(crsr+r*MapUtil.PIdiv12)+1+1),(int)(1+dcy+sr*Math.sin(crsr+r*MapUtil.PIdiv12)+1),
                    (int)(dcx+(sr-ps*1.5)*Math.cos(crsr+r*MapUtil.PIdiv12)+1+1),(int)(1+dcy+(sr-ps*1.5)*Math.sin(crsr+r*MapUtil.PIdiv12)+1)
                    );
              else
                if ((r!=0)&&(r!=6)&&(r!=12)&&(r!=18))
                  g.drawLine((int)(dcx+sr*Math.cos(crsr+r*MapUtil.PIdiv12)+1+1),(int)(1+dcy+sr*Math.sin(crsr+r*MapUtil.PIdiv12)+1),
                      (int)(dcx+(sr-ps)*Math.cos(crsr+r*MapUtil.PIdiv12)+1+1),(int)(1+dcy+(sr-ps)*Math.sin(crsr+r*MapUtil.PIdiv12)+1)
                      );
            }
          }
          
          
          if (mode==COMPASSMODE)
            g.setColor(0xFFFFFF);
          else
            g.setColor(RMSOption.foreColor);
          //0x66FFCC);
          //base
          g.drawArc(dcx-sr,dcy-sr,cd+1,cd+1,0,360);
          //smaller
          g.drawArc(dcx-sr+1,dcy-sr+1,cd-1,cd-1,0,360);
          //
          g.drawArc(dcx-sr,dcy-sr,cd,cd,0,360);
          //ld
          g.drawArc(dcx-sr+1,dcy-sr+1,cd,cd,0,360);
          //d
          g.drawArc(dcx-sr,dcy-sr+1,cd,cd,0,360);
          //l
          g.drawArc(dcx-sr+1,dcy-sr,cd,cd,0,360);
          
          if (cd>150){
            //greater
            g.drawArc(dcx-sr,dcy-sr,cd+2,cd+2,0,360);
            //
            g.drawArc(dcx-sr-1,dcy-sr-1,cd+2,cd+2,0,360);
            //ld
            g.drawArc(dcx-sr,dcy-sr,cd+2,cd+2,0,360);
            //d
            g.drawArc(dcx-sr-1,dcy-sr,cd+2,cd+2,0,360);
            //l
            g.drawArc(dcx-sr,dcy-sr-1,cd+2,cd+2,0,360);
          }
          for (int r=0;r<24;r++) {
            if ((r==3)||(r==9)||(r==15)||(r==21))
              g.drawLine((int)(dcx+sr*Math.cos(crsr+r*MapUtil.PIdiv12)+1),(int)(1+dcy+sr*Math.sin(crsr+r*MapUtil.PIdiv12)),
                  (int)(dcx+(sr-ps*1.5)*Math.cos(crsr+r*MapUtil.PIdiv12)+1),(int)(1+dcy+(sr-ps*1.5)*Math.sin(crsr+r*MapUtil.PIdiv12))
                  );
            else
              if ((r!=0)&&(r!=6)&&(r!=12)&&(r!=18))
                g.drawLine((int)(dcx+sr*Math.cos(crsr+r*MapUtil.PIdiv12)+1),(int)(1+dcy+sr*Math.sin(crsr+r*MapUtil.PIdiv12)),
                    (int)(dcx+(sr-ps)*Math.cos(crsr+r*MapUtil.PIdiv12)+1),(int)(1+dcy+(sr-ps)*Math.sin(crsr+r*MapUtil.PIdiv12))
                    );
          }
          
          
          if ((compassOverMap)&&(mode==MAPMODE)) {
            g.setColor(RMSOption.shadowColor);
            
            
            sr++;
            
            for (int cc=4;cc>0;cc--) {
              ca=crsr+MapUtil.PIdiv2;
              cb=crsr-MapUtil.PIdiv2;
              g.fillTriangle((int)(dcx+(sr-ps)*Math.cos(crsr)+1.5), (int)(dcy+(sr-ps)*Math.sin(crsr)+1.5),
                  (int)(dcx+sr*Math.cos(crsr)+5*Math.cos(ca)+1.5), (int)(dcy+sr*Math.sin(crsr)+5*Math.sin(ca)+1.5),
                  (int)(dcx+sr*Math.cos(crsr)+5*Math.cos(cb)+1.5), (int)(dcy+sr*Math.sin(crsr)+5*Math.sin(cb)+1.5));
              if (cc==4) s=MapUtil.SH_NORTH;
              if (cc==3) s=MapUtil.SH_EAST;
              if (cc==2) s=MapUtil.SH_SOUTH;
              if (cc==1) s=MapUtil.SH_WEST;
              
              //drawMapString(g,s,(int)(dcx+(sr+ps)*Math.cos(crsr)+1.5), (int)(dcy+(sr+ps)*Math.sin(crsr)-fh/2+1.5), Graphics.TOP|Graphics.HCENTER);
              g.drawString(s, (int)(dcx+(sr+ps)*Math.cos(crsr)+1.5), (int)(dcy+(sr+ps)*Math.sin(crsr)-fh/2+1.5), Graphics.TOP|Graphics.HCENTER );
              
              crsr = crsr+MapUtil.PIdiv4;
              ca=crsr+MapUtil.PIdiv2;
              cb=crsr-MapUtil.PIdiv2;
              
              if (cc==4) s=MapUtil.SH_NORTHEAST;
              if (cc==3) s=MapUtil.SH_SOUTHEAST;
              if (cc==2) s=MapUtil.SH_SOUTHWEST;
              if (cc==1) s=MapUtil.SH_NORTHWEST;
              
              //drawMapString(g,s,(int)(dcx+(sr+ps)*Math.cos(crsr)+1.5), (int)(dcy+(sr+ps)*Math.sin(crsr)-fh/2+1.5), Graphics.TOP|Graphics.HCENTER);
              g.drawString(s, (int)(dcx+(sr+ps)*Math.cos(crsr)+1.5), (int)(dcy+(sr+ps)*Math.sin(crsr)-fh/2+1.5), Graphics.TOP|Graphics.HCENTER );
              
              crsr = crsr+MapUtil.PIdiv4;
              
            }
            
          }
          
          crsr = -coursradPaint-MapUtil.PIdiv2;
          
          if (mode==COMPASSMODE)
            g.setColor(0xFFFFFF);
          else
            g.setColor(RMSOption.foreColor);
          
          //  0x66FFCC);
          
          sr++;
          for (int cc=4;cc>0;cc--) {
            ca=crsr+MapUtil.PIdiv2;
            cb=crsr-MapUtil.PIdiv2;
            g.fillTriangle((int)(dcx+(sr-ps)*Math.cos(crsr)+1.5), (int)(dcy+(sr-ps)*Math.sin(crsr)+1.5),
                (int)(dcx+sr*Math.cos(crsr)+5*Math.cos(ca)+1.5), (int)(dcy+sr*Math.sin(crsr)+5*Math.sin(ca)+1.5),
                (int)(dcx+sr*Math.cos(crsr)+5*Math.cos(cb)+1.5), (int)(dcy+sr*Math.sin(crsr)+5*Math.sin(cb)+1.5));
            if (cc==4) s=MapUtil.SH_NORTH;
            if (cc==3) s=MapUtil.SH_EAST;
            if (cc==2) s=MapUtil.SH_SOUTH;
            if (cc==1) s=MapUtil.SH_WEST;
            // drawMapString(g,s,(int)(dcx+(sr+ps)*Math.cos(crsr)+1.5), (int)(dcy+(sr+ps)*Math.sin(crsr)-fh/2+1.5), Graphics.TOP|Graphics.HCENTER);
            g.drawString(s, (int)(dcx+(sr+ps)*Math.cos(crsr)+1.5), (int)(dcy+(sr+ps)*Math.sin(crsr)-fh/2+1.5), Graphics.TOP|Graphics.HCENTER );
            
            crsr = crsr+MapUtil.PIdiv4;
            ca=crsr+MapUtil.PIdiv2;
            cb=crsr-MapUtil.PIdiv2;
            if (cc==4) s=MapUtil.SH_NORTHEAST;
            if (cc==3) s=MapUtil.SH_SOUTHEAST;
            if (cc==2) s=MapUtil.SH_SOUTHWEST;
            if (cc==1) s=MapUtil.SH_NORTHWEST;
            g.drawString(s, (int)(dcx+(sr+ps)*Math.cos(crsr)+1.5), (int)(dcy+(sr+ps)*Math.sin(crsr)-fh/2+1.5), Graphics.TOP|Graphics.HCENTER );
            
            crsr = crsr+MapUtil.PIdiv4;
          }
          
//#debug
//#           tracePos=2.5412;
          
          sr--;
          sr--;
          
          if (mode==COMPASSMODE) {
            //draw blue/red arrows
            sr=sr-ps-3-ps/3;
            crsr = -coursradPaint-MapUtil.PIdiv2;
            acrsr = crsr+MapUtil.PI;
            ca=crsr+MapUtil.PIdiv2;
            cb=crsr-MapUtil.PIdiv2;
            
             if (GPSReader.SPEED_KMH>3) {
             if (RMSOption.unitFormat==RMSOption.UNITS_IMPERIAL) {
              g.setColor(0xFF0000);
            } else {
              g.setColor(0x0000FF);
            } 
            } else {
              g.setColor(0x464646);
            }
            g.fillTriangle((int)(dcx+sr*Math.cos(crsr)+0.5), (int)(dcy+sr*Math.sin(crsr)+0.5),
                (int)(dcx+ps*Math.cos(ca)+0.5), (int)(dcy+ps*Math.sin(ca)+0.5),
                (int)(dcx+ps*Math.cos(cb)+0.5), (int)(dcy+ps*Math.sin(cb)+0.5));
            
            if (GPSReader.SPEED_KMH>3) {
              if (RMSOption.unitFormat==RMSOption.UNITS_IMPERIAL) {
                g.setColor(0x0000FF);
              } else {
                g.setColor(0xFF0000);
              }
            } else {
              g.setColor(0x818181);
            }
            g.fillTriangle((int)(dcx+sr*Math.cos(acrsr)+0.5), (int)(dcy+sr*Math.sin(acrsr)+0.5),
                (int)(dcx+ps*Math.cos(ca)+0.5), (int)(dcy+ps*Math.sin(ca)+0.5),
                (int)(dcx+ps*Math.cos(cb)+0.5), (int)(dcy+ps*Math.sin(cb)+0.5));
            
            if (GPSReader.mvReady){
              crsr = -GPSReader.COURSE_MOVE_RAD-MapUtil.PIdiv2;
              acrsr = crsr+MapUtil.PI;
              ca=crsr+MapUtil.PIdiv2;
              cb=crsr-MapUtil.PIdiv2;
              ps=ps/2;
              sr=(sr*2)/3;
              g.setColor(0x3040FF);
              g.fillTriangle((int)(dcx+sr*Math.cos(crsr)+0.5), (int)(dcy+sr*Math.sin(crsr)+0.5),
                  (int)(dcx+ps*Math.cos(ca)+0.5), (int)(dcy+ps*Math.sin(ca)+0.5),
                  (int)(dcx+ps*Math.cos(cb)+0.5), (int)(dcy+ps*Math.sin(cb)+0.5));
              g.setColor(0xFF4030);
              g.fillTriangle((int)(dcx+sr*Math.cos(acrsr)+0.5), (int)(dcy+sr*Math.sin(acrsr)+0.5),
                  (int)(dcx+ps*Math.cos(ca)+0.5), (int)(dcy+ps*Math.sin(ca)+0.5),
                  (int)(dcx+ps*Math.cos(cb)+0.5), (int)(dcy+ps*Math.sin(cb)+0.5));
              
            }
            
          }
        }
//#debug
//#         tracePos=3;
        //Additional drawing to some modes
        
        //-----------------------draws our position-------------------------------------
        if (gpsA) {
          if (mode==MAPMODE) {
            if (compassOverMap&&(!rotateMap))
              crsr = MapUtil.G2R*coursPaint-MapUtil.PIdiv2;
            else
              if (rotateMap) crsr = -MapUtil.PIdiv2;
              else
                crsr = coursradPaint-MapUtil.PIdiv2;
            ca=crsr+MapUtil.PI2div3;
            cb=crsr-MapUtil.PI2div3;
            sr=(int)GPSReader.SPEED_KMH-2;
            sr=adaptSpeed(sr);
            sr=sr+sr;
            sr1=6+(sr-6)/6;
            
            boolean tp=RMSOption.getBoolOpt(RMSOption.BL_TRANS_POINTER);
            if (GPSReader.SPEED_KMH>2){
              
              g.setColor(0x0);
              if (tp)
                drawTriangle(g,(int)(1+gpsx+sr*Math.cos(crsr)), (int)(1+gpsy+sr*Math.sin(crsr)),
                    (int)(1+gpsx+sr1*Math.cos(ca)), (int)(1+gpsy+sr1*Math.sin(ca)),
                    (int)(1+gpsx+sr1*Math.cos(cb)), (int)(1+gpsy+sr1*Math.sin(cb)));
              else
                g.fillTriangle((int)(1+gpsx+sr*Math.cos(crsr)), (int)(1+gpsy+sr*Math.sin(crsr)),
                    (int)(1+gpsx+sr1*Math.cos(ca)), (int)(1+gpsy+sr1*Math.sin(ca)),
                    (int)(1+gpsx+sr1*Math.cos(cb)), (int)(1+gpsy+sr1*Math.sin(cb)));
              
              if (gpsBinded)
                g.setColor(0x20FF10);
              else g.setColor(0xFFFF10);
              if (tp)
                drawTriangle(g,(int)(gpsx+sr*Math.cos(crsr)), (int)(gpsy+sr*Math.sin(crsr)),
                    (int)(gpsx+sr1*Math.cos(ca)), (int)(gpsy+sr1*Math.sin(ca)),
                    (int)(gpsx+sr1*Math.cos(cb)), (int)(gpsy+sr1*Math.sin(cb)));
              else  g.fillTriangle((int)(gpsx+sr*Math.cos(crsr)), (int)(gpsy+sr*Math.sin(crsr)),
                  (int)(gpsx+sr1*Math.cos(ca)), (int)(gpsy+sr1*Math.sin(ca)),
                  (int)(gpsx+sr1*Math.cos(cb)), (int)(gpsy+sr1*Math.sin(cb)));
              if (!tp){
              g.setColor(0);
              drawTriangle(g,(int)(gpsx+sr*Math.cos(crsr)), (int)(gpsy+sr*Math.sin(crsr)),
                  (int)(gpsx+sr1*Math.cos(ca)), (int)(gpsy+sr1*Math.sin(ca)),
                  (int)(gpsx+sr1*Math.cos(cb)), (int)(gpsy+sr1*Math.sin(cb)));
              }
              
            } else {
              
              g.setColor(0x0);
              if (tp)
                g.drawArc(gpsx-sr1/2+1-2, gpsy-sr1/2+1-2,sr1+4,sr1+4,0, 360);
              else g.fillArc(gpsx-sr1/2+1-2, gpsy-sr1/2+1-2,sr1+4,sr1+4,0, 360);
              
              if (gpsBinded)
                g.setColor(0x20FF10);
              else g.setColor(0xFFFF10);
              
              if (tp)
                g.drawArc(gpsx-sr1/2-2, gpsy-sr1/2-2,sr1+4,sr1+4,0, 360);
              else g.fillArc(gpsx-sr1/2-2, gpsy-sr1/2-2,sr1+4,sr1+4,0, 360);
              
            }
            
            g.setColor(0xFF0000);
            g.fillArc(gpsx-sr1/2, gpsy-sr1/2,sr1,sr1,0, 360);
          }
          
//#debug
//#           tracePos=3.01;
          if ((mode==MAPMODE)||(mode==COMPASSMODE)) {
            
            s1=MapUtil.speedWithNameRound1(GPSReader.SPEED_KMH);
            s2=String.valueOf(GPSReader.COURSE_I)+((char)0xb0)+' '+' '+MapUtil.heightWithName(GPSReader.ALTITUDE);//+GPSReader.ALTITUDE_M;
                        
            if ((mode==MAPMODE)) {
              if ((splitMode==0)){
                drawMapString(g,s1, 1, dmaxy-1, Graphics.BOTTOM|Graphics.LEFT);
                drawMapString(g,s2, 2, dmaxy-fh-1, Graphics.BOTTOM|Graphics.LEFT);
              }
            } else {
              g.setColor(0xFFFFFF);
              g.drawString(s1, 1, dmaxy-1, Graphics.BOTTOM|Graphics.LEFT );
              g.drawString(s2, dmaxx-1, dmaxy-1, Graphics.BOTTOM|Graphics.RIGHT);
            }
            //g.drawString(s1, 1, dmaxy-1, Graphics.BOTTOM|Graphics.LEFT );
            //  if (mode==MAPMODE)
            //   drawMapString(g,s2, 1, dmaxy--1fh, Graphics.BOTTOM|Graphics.LEFT);
            //   g.drawString(s2, 1, dmaxy-1-fh, Graphics.BOTTOM|Graphics.LEFT );
            //  else g.drawString(s2, dmaxx-1, dmaxy-1, Graphics.BOTTOM|Graphics.RIGHT);
            s1=null;
            s2=null;
          }
        }
        
//#debug
//#         tracePos=5;
        
        if (activeRoute!=null) if (activeRoute.pt2Nav()!=null) if ((NetRadar.netRadar==null)||(!NetRadar.netRadar.bind)) {
          if ((mode==MAPMODE)||(mode==COMPASSMODE)) {
//#debug
//#             tracePos=5.05;
            if (gpsA&&((mode==COMPASSMODE)||gpsBinded))
              dist = activeRoute.distFromPrecise(GPSReader.LATITUDE, GPSReader.LONGITUDE);
            else dist = activeRoute.distFromPrecise(reallat, reallon);
//#debug
//#             tracePos=5.1;
            //        if (gpsReader!=null)
            //          if (gpsReader.fox!=null) gpsReader.fox.distance= (int)(dist*1000.);
            if ((activeRoute.kind==MapRoute.ROUTEKIND)&&(mode==MAPMODE)&&(activeRoute.pt2Nav()!=activeRoute.lastPoint())&&(dmaxx>140))
            s=MapUtil.distWithNameRound3(dist)+" ("+MapUtil.distWithNameRound3(activeRoute.distLeft2Nav(dist))+")";
            else s=MapUtil.distWithNameRound3(dist);
            //s=String.valueOf(dist)+' '+LangHolder.getString(Lang.km);
            
//#debug
//#             tracePos=5.15;
            
            s1=activeRoute.pt2Nav().getName();
//#debug
//#             tracePos=5.2;
            
            s1=MapCanvas.cutStringToWidth(s1,(mode==MAPMODE)?dmaxx:3*dmaxx/4,g);
//            if (dmaxx<=160) {
//              if (s1.length()>6) s1=s1.substring(0,6);} else if (s1.length()>9) s1=s1.substring(0,9);
//#debug
//#             tracePos=5.21;
            
//            g.setColor(0x0);
//            if (mode==MAPMODE) {
//              g.drawString(s1, dminx+1, dminy+fh+1, Graphics.TOP|Graphics.LEFT );
//              g.drawString(s, dminx+1, dminy+fh+fh+1, Graphics.TOP|Graphics.LEFT );
//              //g.drawString(s1, dminx-1, dminy+fh-1, Graphics.TOP|Graphics.LEFT );
//              //g.drawString(s, dminx-1, dminy+fh+fh-1, Graphics.TOP|Graphics.LEFT );
//            } else {
//              g.drawString(s1, dmaxx+1, dminy+1, Graphics.TOP|Graphics.RIGHT );
//              g.drawString(s, dminx+g.getFont().stringWidth(MapUtil.SH_NORTHWEST)+1, dminy+1, Graphics.TOP|Graphics.LEFT );
//              //g.drawString(s1, dmaxx-1, dminy-1, Graphics.TOP|Graphics.RIGHT );
//              //g.drawString(s, dminx-1, dminy-1, Graphics.TOP|Graphics.LEFT );
//            }
//#debug
//#             tracePos=5.22;

           s2=MapUtil.emptyString;
            if (gpsA) {
//#debug
//#               tracePos=5.41;
              if ((activeRoute.kind==MapRoute.ROUTEKIND)&&(mode==MAPMODE)&&(dmaxx>140)&&(activeRoute.pt2Nav()!=activeRoute.lastPoint()))
              s2=activeRoute.activeETAAndTA();
              else
                s2=activeRoute.activeETA();
//#debug
//#               tracePos=5.42;

//#debug
//#               tracePos=5.43;

            }

            if ((mode==MAPMODE)) {
              if ((splitMode==0)){
                  boolean dirDrawn = !RMSOption.getBoolOpt(RMSOption.BL_HIDE_ROUTE_DIRECTION);
                  if (dirDrawn)
                      dirDrawn = activeRoute.drawDirection(g, dminx, dminy+fh);
                  if (!dirDrawn){
                  if (gpsA)
                    drawMapString(g,s2, dminx, dminy+fh+fh+fh, Graphics.TOP|Graphics.LEFT );
                  drawMapString(g,s1, dminx, dminy+fh, Graphics.TOP|Graphics.LEFT);
                    if (activeRoute.pt2Nav().getName().length()>s1.length()){
                      s1=activeRoute.pt2Nav().getName().substring(s1.length());
                      s1=MapCanvas.cutStringToWidth(s1,3*dmaxx/4,g);
                      drawMapString(g,s1, dmaxx, dminy+fh+fh, Graphics.TOP|Graphics.RIGHT);

                    }
                  drawMapString(g,s, dminx, dminy+fh+fh, Graphics.TOP|Graphics.LEFT);
                }
              }
              //g.drawString(s1, dminx, dminy+fh, Graphics.TOP|Graphics.LEFT );
              //g.drawString(s, dminx, dminy+fh+fh, Graphics.TOP|Graphics.LEFT );
            } else {
              g.setColor(0xFFFFFF);
              g.drawString(s1, dmaxx, dminy, Graphics.TOP|Graphics.RIGHT );
              g.drawString(s, dminx+((splitMode==0)?g.getFont().stringWidth(MapUtil.SH_NORTHWEST):0), dminy, Graphics.TOP|Graphics.LEFT );
                g.drawString(s2, dminx, dminy+fh, Graphics.TOP|Graphics.LEFT );
            }
//#debug
//#             tracePos=5.23;
            
            s=null;
            s1=null;
            
            //---------------DIRECTION TO WAYPOINT----------------------------------
            
            if (mode==MAPMODE) {
              
              if (gpsA&&(gpsBinded)) {
                crsr = activeRoute.courseFrom(GPSReader.LATITUDE, GPSReader.LONGITUDE)-MapUtil.PIdiv2;
                xN=gpsx;yN=gpsy;
              }
              else {
                crsr = activeRoute.courseFrom(reallat, reallon)-MapUtil.PIdiv2;
                xN=dcx;yN=dcy;
              }
              
              if (rotateMap) crsr =crsr- coursradPaint;
//#debug
//#               tracePos=5.3;
              
              ca=crsr+MapUtil.PI2div3;
              cb=crsr-MapUtil.PI2div3;
              sr=(int)GPSReader.SPEED_KMH-2;
              sr=adaptSpeed(sr);
              sr=sr+sr+sr;
              sr1=8+(sr-6)/6;
              
//#debug
//#               tracePos=5.31;
              g.setStrokeStyle(Graphics.SOLID);
              g.setColor(0x0);
              int xa1=(int)(1+xN+sr*Math.cos(crsr)),ya1=(int)(1+yN+sr*Math.sin(crsr)),
                  xa2=(int)(1+xN+sr1*Math.cos(ca)),ya2=(int)(1+yN+sr1*Math.sin(ca)),
                  xa3=(int)(1+xN+sr1*Math.cos(cb)),ya3=(int)(1+yN+sr1*Math.sin(cb));
              drawTriangle(g,xa1, ya1,
                  xa2, ya2,
                  xa3, ya3);
            g.setStrokeStyle(Graphics.DOTTED);
                
              g.drawArc(xN-sr1/2, yN-sr1/2,sr1,sr1,0, 360);
              g.setStrokeStyle(Graphics.SOLID);
                  
//#debug
//#               tracePos=5.32;
              
              if (gpsBinded)
                g.setColor(0x20DF10);
              else g.setColor(0xFFFF10);
              xa1--;xa2--;xa3--;ya1--;ya2--;ya3--;
              drawTriangle(g,xa1, ya1,
                  xa2, ya2,
                  xa3, ya3);
              drawTriangle(g,xa1+1, ya1,
                  xa2+1, ya2,
                  xa3+1, ya3);
              drawTriangle(g,xa1, ya1+1,
                  xa2, ya2+1,
                  xa3, ya3+1);
              drawTriangle(g,xa1-1, ya1,
                  xa2-1, ya2,
                  xa3-1, ya3);
              drawTriangle(g,xa1, ya1-1,
                  xa2, ya2-1,
                  xa3, ya3-1);
              
//              drawTriangle(g,xa1+1, ya1+1,
//                  xa2+1, ya2+1,
//                  xa3+1, ya3+1);
//              drawTriangle(g,xa1-1, ya1+1,
//                  xa2-1, ya2+1,
//                  xa3-1, ya3+1);
//              drawTriangle(g,xa1-1, ya1+1,
//                  xa2-1, ya2+1,
//                  xa3-1, ya3+1);
//              drawTriangle(g,xa1-1, ya1-1,
//                  xa2-1, ya2-1,
//                  xa3-1, ya3-1);
//              drawTriangle(g,(int)(xN+sr*Math.cos(crsr)), (int)(yN+sr*Math.sin(crsr)),
//                  (int)(xN+sr1*Math.cos(ca)), (int)(yN+sr1*Math.sin(ca)),
//                  (int)(xN+sr1*Math.cos(cb)), (int)(yN+sr1*Math.sin(cb)));
              g.setColor(0xFF00FF);
              g.setStrokeStyle(Graphics.DOTTED);
              g.drawArc(xN-sr1/2, yN-sr1/2,sr1,sr1,0, 360);
              g.setStrokeStyle(Graphics.SOLID);
              xa1++;xa2++;xa3++;ya1++;ya2++;ya3++;
              g.setColor(0x0);
              drawTriangle(g,xa1, ya1,
                  xa2, ya2,
                  xa3, ya3);
//#debug
//#               tracePos=5.33;
            }
            
//        g.drawString(s, 0, dcy-fh, Graphics.TOP|Graphics.LEFT );
            
          }
        }
        
        if ((activeRoute==null)&&(gpsA)&&(!gpsBinded)&&(mode==MAPMODE)) {
          //#debug
//#           tracePos=5.441;
          //dist = MapRoute.distBetweenCoords(reallat, reallon,GPSReader.LATITUDE, GPSReader.LONGITUDE);
//#debug
//#           tracePos=5.442;
//          s=MapUtil.distWithNameRound3(dist);
//#debug
//#           tracePos=5.445;
//            drawMapString(g,s1,dmaxx, dmaxy-fh, Graphics.BOTTOM|Graphics.RIGHT);
          drawMapString(g,MapUtil.distWithNameRound3(MapRoute.distBetweenCoords(reallat, reallon,GPSReader.LATITUDE, GPSReader.LONGITUDE)), dminx, dminy+fh+fh, Graphics.TOP|Graphics.LEFT);
        }
        
        
        // end of MAP and COMPASS
              }
      
      
      //#debug
//#       tracePos=5.45;
      int ic_offs=0;
      if (gpsReader!=null) {
          if (mode==MAPMODE)
            if (RMSOption.getBoolOpt(RMSOption.BL_SHOWCLOCKONMAP))
          {
          s2=gpsReader.satTime();
          drawMapString(g, s2, 2, dmaxy-fh-fh-fh,Graphics.BOTTOM|Graphics.LEFT);  
          }
        if ((mode==MAPMODE)||(mode==COMPASSMODE)||(mode==SATMODE)) {
          s2=String.valueOf(GPSReader.NUM_SATELITES);
          if (((mode==MAPMODE)&&(splitMode==0))||(mode==SATMODE)) {
            if (mode==SATMODE)
              g.drawImage(gpsReader.satImage(),dminx,dmaxy-2,Graphics.BOTTOM|Graphics.LEFT);
            else  {g.drawImage(gpsReader.satImage(),dminx,dmaxy-fh-fh,Graphics.BOTTOM|Graphics.LEFT);ic_offs+=gpsReader.satImage().getWidth()+1;}
            if (gpsReader.locReader==null){
              g.setColor(0x0);
//              if (mode==SATMODE)
//                g.drawString(s2, dminx+1+gpsReader.satImage().getWidth(), dmaxy+1-2, Graphics.BOTTOM|Graphics.LEFT );
//              else g.drawString(s2, dminx+1+gpsReader.satImage().getWidth(), dmaxy+1-fh-fh, Graphics.BOTTOM|Graphics.LEFT );
              if (mode==SATMODE) {
                g.setColor(0xFFFFFF);
                g.drawString(s2, dminx+gpsReader.satImage().getWidth(), dmaxy-2, Graphics.BOTTOM|Graphics.LEFT );
              } else {
                drawMapString(g,s2, dminx+gpsReader.satImage().getWidth(), dmaxy-fh-fh, Graphics.BOTTOM|Graphics.LEFT );
                ic_offs+=g.getFont().stringWidth(s2)+1;
              }
//                g.drawString(s2, dminx+gpsReader.satImage().getWidth(), dmaxy-fh-fh, Graphics.BOTTOM|Graphics.LEFT );
              
            }
          } else if (mode!=MAPMODE){
            g.drawImage(gpsReader.satImage(),dmaxx-g.getFont().stringWidth(s2), dmaxy-fh, Graphics.BOTTOM|Graphics.RIGHT);
            if (gpsReader.locReader==null){
              //g.setColor(0x0);
              //g.drawString(s2, dmaxx+1, dmaxy-fh+1, Graphics.BOTTOM|Graphics.RIGHT);
              g.setColor(0xFFFFFF);
              g.drawString(s2, dmaxx, dmaxy-fh, Graphics.BOTTOM|Graphics.RIGHT);
            }
          }
        }
      }
      if ((activeTrack!=null)||isBlinking)

      if (!RMSOption.safeMode)
        if (((mode==MAPMODE)&&(splitMode==0) )||(mode==COMPASSMODE)||(mode==SATMODE)){
            
        if (activeTrack!=null){
            s2 = String.valueOf(activeTrack.pts.size());
        }else {
            s2=MapUtil.emptyString;
        }
            sr = g.getFont().stringWidth(s2)+4;
            if (sr<fh){
              sr=fh;
            }
          
          g.setColor(MapRoute.getStatusColor(activeTrack));
          g.fillRect(dminx+ic_offs,dmaxy-fh*3,sr,fh);
          g.setColor(0);
          g.drawRect(dminx+ic_offs,dmaxy-fh*3,sr,fh);
          g.drawString(s2,dminx+ic_offs+sr/2,dmaxy-fh-fh,Graphics.HCENTER|Graphics.BOTTOM);
          ic_offs+=sr+3;
          //g.setColor(0)
        
        }

      if (NetRadar.netRadar!=null)
        if (NetRadar.netRadar.imgStatus!=null)
          if (((mode==MAPMODE)&&(splitMode==0) )||(mode==COMPASSMODE)||(mode==SATMODE)){
        g.drawImage(NetRadar.netRadar.imgStatus,dminx+ic_offs+fh/2,dmaxy-fh-fh,Graphics.HCENTER|Graphics.BOTTOM);
        ic_offs+=fh;
          }
//#debug
//#       tracePos=5.5;
      if (!RMSOption.soundOn)
        if (((mode==MAPMODE)&&(splitMode==0) )||(mode==POSITIONMODE)||(mode==SATMODE)){
        sr1 = (dmaxx<165)?1:2;
        sr=dminx+ic_offs;
        to=fh/4;
        g.setColor(0xFFFF20);
        g.drawLine(sr+to,dmaxy-fh*3+sr1,sr+to,dmaxy-fh*3+fh-sr1);
        g.drawLine(sr+to+1,dmaxy-fh*3+sr1,sr+to+1,dmaxy-fh*3+fh-sr1);
        g.fillTriangle(sr+to,dmaxy-fh*3+to+to, sr+fh*3/4,dmaxy-fh*3+sr1,sr+fh*3/4,dmaxy-fh*2-sr1);
        g.setColor(0xFF2020);
        g.drawLine(sr+sr1,dmaxy-fh*3+sr1+to,sr+fh-sr1,dmaxy-fh*3+fh-sr1-to);
        g.drawLine(sr+sr1,dmaxy-fh*3+sr1+to+1,sr+fh-sr1,dmaxy-fh*3+fh-sr1-to+1);
        
        ic_offs+=sr+3;
        g.setColor(RMSOption.foreColor);
        }
    
      
//      if (blueAutoSearch) {
//        g.setColor(0x0);
//        g.drawString(LangHolder.getString(Lang.gpsautoconn"),dmaxx+1,dcy+1-fh,Graphics.RIGHT|Graphics.TOP);
//        g.drawString(RMSOption.lastBTDeviceName,dmaxx+1,dcy+1-fh,Graphics.RIGHT|Graphics.BOTTOM);
//        g.setColor(0x7070FF);
//        g.drawString(LangHolder.getString(Lang.gpsautoconn"),dmaxx,dcy-fh,Graphics.RIGHT|Graphics.TOP);
//        g.drawString(RMSOption.lastBTDeviceName,dmaxx,dcy-fh,Graphics.RIGHT|Graphics.BOTTOM);
//      }
      
      
      
//#debug
//#       tracePos=12;
      
    } catch(Throwable e) {
//#mdebug
//#       if (RMSOption.debugEnabled)
//#         DebugLog.add2Log("Paint:"+String.valueOf(tracePos)+":mode "+String.valueOf(mode)+":" + e);
//#       g.setColor(0xFF0000);
//#       g.drawString("Paint error:", dmaxx/2, dcy, Graphics.TOP|Graphics.LEFT);
//#       g.drawString(String.valueOf(tracePos), dmaxx, dcy+MapUtil.MEDIUMFONT.getHeight(), Graphics.TOP|Graphics.RIGHT);
//#enddebug
    }
    
//#if SE_K750_E_BASEDEV
//# //    g.setColor(0xFF0000);
//# //    g.drawString("Th C:"+String.valueOf(Thread.activeCount()),0,dcy,Graphics.LEFT|Graphics.BOTTOM);
//# //    g.drawString("P C:"+String.valueOf(paintCount),0,dcy,Graphics.LEFT|Graphics.TOP);
//#endif
    lastPainted=System.currentTimeMillis();
  }
  
  private void drawMark(Graphics g,int x, int y,String label,boolean drawAll) {
    int ms = (dmaxx>200)?24:15;
    int rs = (dmaxx>200)?24:16;
    int ws = (dmaxx>200)?6:3;
    g.setColor(0x0);
    g.fillTriangle(x+1,y+1,x+ms-ws+1,y-ms-ws+1,x+ms+ws+1,y-ms+ws+1);
    //  if (!drawAll)
    g.fillArc(x+ms+1-rs/2,y-ms+1-rs/2,rs,rs,0,360);
    
    g.setColor(0xFFDFCF);
    g.fillTriangle(x,y,x+ms-ws,y-ms-ws,x+ms+ws,y-ms+ws);
    // if (!drawAll)
    g.fillArc(x+ms-rs/2,y-ms-rs/2,rs,rs,0,360);
    
    if(drawAll){
//      g.setColor(0xFF0000);
//      g.drawRect(x-1,y-1,3,3);
//      g.setColor(0xFF5000);
//      g.drawRect(x-2,y-2,5,5);
//      g.setColor(0xFF8000);
//      g.drawRect(x-3,y-3,7,7);
      int ys = (dmaxx>200)?16:8;
      g.setColor(0xFFDFCF);
      int xN=g.getFont().stringWidth(label)+1;
      int yN=g.getFont().getHeight();
      g.fillRect(x-xN/2-1+4,y-ys-yN,xN+2,yN);
      g.setColor(0x204070);
      g.drawString(label,x-xN/2+4,y-ys,Graphics.BOTTOM|Graphics.LEFT);
    }
  }
  
  private void drawNetUsers(Graphics g) {
    NetRadarUser nru;
    double crsr,ca,cb;
    int sr,usx,usy,sr1,usx1,usy1;
    Font sfont = g.getFont();
    int fh=sfont.getHeight();
    String s="NetRadar:";
    if ((NetRadar.netRadar.serviceStatus!=NetRadar.OKSTATUS)) {
      g.setColor(0x0);
      g.drawString(s,dcx+1,dcy+1+fh,Graphics.HCENTER|Graphics.BOTTOM);
      g.drawString(NetRadar.netRadar.serviceDesc,dcx+1,dcy+1+fh,Graphics.HCENTER|Graphics.TOP);
      g.setColor(0xFFFFFF);
      g.drawString(s,dcx,dcy+fh,Graphics.HCENTER|Graphics.BOTTOM);
      g.drawString(NetRadar.netRadar.serviceDesc,dcx,dcy+fh,Graphics.HCENTER|Graphics.TOP);
    } else {
      NetRadarUser closest=null;
      int mind=10;
      //Enumeration users = NetRadar.netRadar.getUsers();
      MVector nv = NetRadar.netRadar.getUsersV();
      if (nv.size()==0) return;
      boolean usersPresent=false;
      //while (users.hasMoreElements())
      for (int i=0;i<nv.size();i++){
        usersPresent=true;
        //nru = (NetRadarUser)users.nextElement();
        nru = (NetRadarUser)nv.elementAt(i);
        Font fn= FontUtil.SMALLFONT;
        g.setFont(fn);
        try {
          if (RMSOption.getBoolOpt(RMSOption.BL_SHOWTRACKNR)){
            byte tpc = nru.trackcnt;
            tpc--;
            byte tpi=nru.trackpos,tpin;
            if (tpi==0) tpi=(byte)(nru.tracklon.length-1);
            else tpi--;
            tpin=tpi;
            if (tpi==0) tpi=(byte)(nru.tracklon.length-1);
            else tpi--;
            while (tpc>0){
              usx=MapUtil.getXMap(nru.tracklon[tpi], level)-xCenter+dcx;
              usy=MapUtil.getYMap(nru.tracklat[tpi], level)-yCenter+dcy;
              if (rotateMap) {
                int xt=usx,yt=usy;
                usx=rotateMX(xt,yt);
                usy=rotateMY(xt,yt);
              }
              usx1=MapUtil.getXMap(nru.tracklon[tpin], level)-xCenter+dcx;
              usy1=MapUtil.getYMap(nru.tracklat[tpin], level)-yCenter+dcy;
              if (rotateMap) {
                int xt=usx1,yt=usy1;
                usx1=rotateMX(xt,yt);
                usy1=rotateMY(xt,yt);
              }
              g.setColor(0x002000);
              g.drawLine(usx+1,usy+1,usx1+1,usy1+1);
              g.setColor(0xFF20CC);
              g.drawLine(usx,usy,usx1,usy1);
              tpin=tpi;
              if (tpi==0) tpi=(byte)(nru.tracklon.length-1);
              else tpi--;
              tpc--;
            }
          }
          usx=MapUtil.getXMap(nru.lon, level)-xCenter+dcx;
          usy=MapUtil.getYMap(nru.lat, level)-yCenter+dcy;
          if (rotateMap) {
            int xt=usx,yt=usy;
            usx=rotateMX(xt,yt);
            usy=rotateMY(xt,yt);
          }
          if (isOn2Screen(usx,usy)) {
            int md=(int)Math.sqrt((usx-dcx)*(usx-dcx)+(usy-dcy)*(usy-dcy));
            if (md<mind){
              mind=md;
              closest=nru;
            }
            crsr = MapUtil.G2R*nru.crs-MapUtil.PIdiv2;
            if (rotateMap)  crsr = crsr-coursradPaint;
            
            ca=crsr+MapUtil.PI2div3;
            cb=crsr-MapUtil.PI2div3;
            sr=(int)nru.speed-2;
            sr=adaptSpeed(sr);
            sr+=sr;
            sr1=6+(sr-6)/6;
            if (nru.speed>4.2){
              g.setColor(0x0);
              g.fillTriangle((int)(1+usx+sr*Math.cos(crsr)), (int)(1+usy+sr*Math.sin(crsr)),
                  (int)(1+usx+sr1*Math.cos(ca)), (int)(1+usy+sr1*Math.sin(ca)),
                  (int)(1+usx+sr1*Math.cos(cb)), (int)(1+usy+sr1*Math.sin(cb)));
              g.setColor(0xfeaa47);
              g.fillTriangle((int)(usx+sr*Math.cos(crsr)), (int)(usy+sr*Math.sin(crsr)),
                  (int)(usx+sr1*Math.cos(ca)), (int)(usy+sr1*Math.sin(ca)),
                  (int)(usx+sr1*Math.cos(cb)), (int)(usy+sr1*Math.sin(cb)));
              g.setColor(0x1350ff);
              g.fillTriangle((int)(usx+sr*Math.cos(crsr)/2), (int)(usy+sr*Math.sin(crsr)/2),
                  (int)(usx+sr1*Math.cos(ca)/2), (int)(usy+sr1*Math.sin(ca)/2),
                  (int)(usx+sr1*Math.cos(cb)/2), (int)(usy+sr1*Math.sin(cb)/2));
              g.setColor(0x2020FF);
              drawTriangle(g,(int)(usx+sr*Math.cos(crsr)), (int)(usy+sr*Math.sin(crsr)),
                  (int)(usx+sr1*Math.cos(ca)), (int)(usy+sr1*Math.sin(ca)),
                  (int)(usx+sr1*Math.cos(cb)), (int)(usy+sr1*Math.sin(cb)));
              g.setColor(0x0);
              g.drawArc(usx-2, usy-2,4,4,0, 360);
              
            }else{
              g.setColor(0x0);
//              if (tp)
//                g.drawArc(usx-sr1/2+1-2, usy-sr1/2+1-2,sr1+4,sr1+4,0, 360);
//              else
              g.fillArc(usx-sr1/2+1-2, usy-sr1/2+1-2,sr1+4,sr1+4,0, 360);
              
//              if (usBinded)
//                g.setColor(0x20FF10);
//              else
              g.setColor(0xFFE010);
              
//              if (tp)
//                g.drawArc(usx-sr1/2-2, usy-sr1/2-2,sr1+4,sr1+4,0, 360);
//              else
              g.fillArc(usx-sr1/2-2, usy-sr1/2-2,sr1+4,sr1+4,0, 360);
              
              g.setColor(0xFF0000);
              g.fillArc(usx-sr1/2, usy-sr1/2,sr1,sr1,0, 360);
              
            }
            g.setColor(0x0);
            int fw = fn.stringWidth(nru.userName)/2+1;
            if (fw<fn.stringWidth(String.valueOf((int)nru.speed)+' '+LangHolder.getString(Lang.kmh))/2+1) fw=fn.stringWidth(String.valueOf((int)nru.speed)+' '+LangHolder.getString(Lang.kmh))/2+1;
            String ups,dps;
            if (nru.alt>GPSReader.ALTITUDE) {
              dps=nru.userName;
              ups=String.valueOf((int)nru.speed)+' '+LangHolder.getString(Lang.kmh);
            } else {
              ups=nru.userName;
              dps=String.valueOf((int)nru.speed)+' '+LangHolder.getString(Lang.kmh);
            }
            
            if (crsr<MapUtil.PI) {
              g.setStrokeStyle(Graphics.DOTTED);
              g.drawRect(usx-fw,usy-6-fh-fh,fw+fw,fh+fh);
              g.drawLine(usx-fw,usy-6,usx,usy);
              g.drawLine(usx-fw+fw+fw,usy-6,usx,usy);
              g.setStrokeStyle(Graphics.SOLID);
              //g.fillRect(usx-fw,usy-6-fh-fh,fw+fw,fh+fh);
              drawMapStringColor(g, ups,usx,usy-6,Graphics.HCENTER|Graphics.BOTTOM,0xFFFFFF);
              //drawShadowString(g,ups,usx,usy-6,0xFFFFFF,Graphics.HCENTER|Graphics.BOTTOM,0);
              //g.setColor(0xFFFFFF);
              //g.drawString(ups,,);
              int ccc=0xFF0000;
              if (nru.dt+180000>System.currentTimeMillis())
                ccc=0x00FF00;
              else if (nru.dt+1800000>System.currentTimeMillis())
                ccc=0xFFFF00;
              drawMapStringColor(g,dps,usx,usy-6-fh,Graphics.HCENTER|Graphics.BOTTOM,ccc);
              //drawShadowString(g,dps,usx,usy-6-fh,ccc,Graphics.HCENTER|Graphics.BOTTOM,0);
              
              //g.drawString(dps,usx,usy-6-fh,Graphics.HCENTER|Graphics.BOTTOM);
            } else {
              g.setStrokeStyle(Graphics.DOTTED);
              g.drawRect(usx-fw,usy+6,fw+fw,fh+fh);
              g.drawLine(usx-fw,usy+6,usx,usy);
              g.drawLine(usx-fw+fw+fw,usy+6,usx,usy);
              g.setStrokeStyle(Graphics.SOLID);
              //g.fillRect(usx-fw,usy+6,fw+fw,fh+fh);
              // drawShadowString(g,dps,usx,usy+6,0xFFFFFF,Graphics.HCENTER|Graphics.TOP,0);
              drawMapStringColor(g,dps,usx,usy+6,Graphics.HCENTER|Graphics.TOP,0xFFFFFF);
              //g.setColor(0xFFFFFF);
              //g.drawString(dps,usx,usy+6,Graphics.HCENTER|Graphics.TOP);
              
              int ccc=0xFF0000;
              if (nru.dt+180000>System.currentTimeMillis())
                ccc=0x00FF00;
              else if (nru.dt+1800000>System.currentTimeMillis())
                ccc=0xFFFF00;
              drawMapStringColor(g,ups,usx,usy+6+fh,Graphics.HCENTER|Graphics.TOP,ccc);
              //drawShadowString(g,ups,usx,usy+6+fh,ccc,Graphics.HCENTER|Graphics.TOP,0);
              
//              if (nru.dt+180000>System.currentTimeMillis())
//                g.setColor(0x00FF00);
//              else if (nru.dt+1800000>System.currentTimeMillis())
//                g.setColor(0xFFFF00);
//              else g.setColor(0xFF0000);
//              g.drawString(ups,usx,usy+6+fh,Graphics.HCENTER|Graphics.TOP);
            }
          }
        }catch(Exception e){}
        g.setFont(sfont);
      }
      
      if (!usersPresent) {
        drawMapString(g,s,dcx+1,dcy+1+fh,Graphics.HCENTER|Graphics.BOTTOM);
        drawMapString(g,LangHolder.getString(Lang.noradaruser),dcx+1,dcy+1+fh,Graphics.HCENTER|Graphics.TOP);
//        g.setColor(0x0);
//        g.drawString(s,dcx+1,dcy+1+fh,Graphics.HCENTER|Graphics.BOTTOM);
//        g.drawString(LangHolder.getString(Lang.noradaruser),dcx+1,dcy+1+fh,Graphics.HCENTER|Graphics.TOP);
//        g.setColor(0xFFFFFF);
//        g.drawString(s,dcx,dcy+fh,Graphics.HCENTER|Graphics.BOTTOM);
//        g.drawString(LangHolder.getString(Lang.noradaruser),dcx,dcy+fh,Graphics.HCENTER|Graphics.TOP);
      }else{
        if (closest!=null){
          if (closest.status_v==null)
            if (closest.status!=null) {
            closest.status_v=new MVector();
            TextCanvas.split(closest.status,closest.status_v,g.getClipWidth(), FontUtil.SMALLFONT);
            }
          if (closest.status_v!=null){
            Font f=g.getFont();
            g.setFont(FontUtil.SMALLFONT);
            g.setColor(RMSOption.foreColor);
            for (int i=0;i<closest.status_v.size();i++) {
              //s=(String)closest.status_v.elementAt(i);
              drawMapString(g,(String)closest.status_v.elementAt(i),dminx,dminy+(i+1)* FontUtil.SMALLFONT.getHeight(),Graphics.LEFT|Graphics.TOP);
//              g.drawString(s,dminx,dminy+(i+1)*MapUtil.SMALLFONT.getHeight(),Graphics.LEFT|Graphics.TOP);
            }
            g.setFont(f);
          }
        }
      }
      
      
    }
  }
  public static void drawMapStringColor(Graphics g, String s, int x,int y, int anchor,int color) {
    int fc = RMSOption.foreColor;
    RMSOption.foreColor=color;
    drawMapString(g, s, x,y, anchor);
    RMSOption.foreColor=fc;
  }
  
  public static void clearDarkenImages(){
    imageSMDarken=null;
    imageMMDarken=null;
    imageLMDarken=null;
  }
  private static Image imageSMDarken;
  private static Image imageMMDarken;
  private static Image imageLMDarken;
  public static boolean drawDarkenBack(Graphics g, String s, int x,int y, int anchor){
    boolean drawed=false;
    if (imageLMDarken==null){
      imageLMDarken=MapUtil.Img_fadeDarken(FontUtil.LARGEFONTB.stringWidth(MapUtil.SH_WEST)*2, FontUtil.LARGEFONTB.getHeight());
      imageMMDarken=MapUtil.Img_fadeDarken(FontUtil.MEDIUMFONTB.stringWidth(MapUtil.SH_WEST)*2, FontUtil.MEDIUMFONTB.getHeight());
      imageSMDarken=MapUtil.Img_fadeDarken(FontUtil.SMALLFONTB.stringWidth(MapUtil.SH_WEST)*2, FontUtil.SMALLFONTB.getHeight());
    }
    Image di=imageLMDarken;
    Font f= g.getFont();
    if ((f== FontUtil.LARGEFONT)||(f== FontUtil.LARGEFONTB)){
      di=imageLMDarken;
    } else if ((f== FontUtil.MEDIUMFONT)||(f== FontUtil.MEDIUMFONTB)){
      di=imageMMDarken;
    } else if ((f== FontUtil.SMALLFONT)||(f== FontUtil.SMALLFONTB)){
      di=imageSMDarken;
    }
    int sw=f.stringWidth(s);
    int diw = di.getWidth();
    int tw=sw/diw+1;
    if ((anchor&Graphics.HCENTER)==Graphics.HCENTER){
      if ((anchor&Graphics.TOP)==Graphics.TOP)
        anchor=Graphics.TOP|Graphics.LEFT;
      else
        anchor=Graphics.BOTTOM|Graphics.LEFT;
      x = x-sw/2;
    }
    if ((anchor&Graphics.LEFT)==Graphics.LEFT) {
      if ((anchor&Graphics.TOP)==Graphics.TOP)
        for (int i=0;i<tw;i++)
          if ((i+1)*diw<=sw)
            g.drawRegion(di,0,0,diw,di.getHeight(),Sprite.TRANS_NONE, x+i*diw,y,Graphics.TOP|Graphics.LEFT);
          else g.drawRegion(di,0,0,sw%((i==0)?diw:i*diw),di.getHeight(),Sprite.TRANS_NONE, x+i*diw,y,Graphics.TOP|Graphics.LEFT);
//            g.drawImage(di,x+i*di.getWidth(),y,Graphics.TOP|Graphics.LEFT);
      else
        if ((anchor&Graphics.BOTTOM)==Graphics.BOTTOM)
          for (int i=0;i<tw;i++)
            if ((i+1)*diw<=sw)
              g.drawRegion(di,0,0,diw,di.getHeight(),Sprite.TRANS_NONE, x+i*diw,y,Graphics.BOTTOM|Graphics.LEFT);
            else g.drawRegion(di,0,0,sw%((i==0)?diw:i*diw),di.getHeight(),Sprite.TRANS_NONE, x+i*diw,y,Graphics.BOTTOM|Graphics.LEFT);
      
      drawed=true;
    } else
      if ((anchor&Graphics.RIGHT)==Graphics.RIGHT) {
      if ((anchor&Graphics.TOP)==Graphics.TOP)
        for (int i=0;i<tw;i++)
          if ((i+1)*diw<=sw)
            g.drawRegion(di,0,0,diw,di.getHeight(),Sprite.TRANS_NONE, x-i*diw,y,Graphics.TOP|Graphics.RIGHT);
          else g.drawRegion(di,0,0,sw%((i==0)?diw:i*diw),di.getHeight(),Sprite.TRANS_NONE, x-i*diw,y,Graphics.TOP|Graphics.RIGHT);
      else
        if ((anchor&Graphics.BOTTOM)==Graphics.BOTTOM)
          for (int i=0;i<tw;i++)
            if ((i+1)*diw<=sw)
              g.drawRegion(di,0,0,diw,di.getHeight(),Sprite.TRANS_NONE, x-i*diw,y,Graphics.BOTTOM|Graphics.RIGHT);
            else g.drawRegion(di,0,0,sw%((i==0)?diw:i*diw),di.getHeight(),Sprite.TRANS_NONE, x-i*diw,y,Graphics.BOTTOM|Graphics.RIGHT);
      
      drawed=true;
      }
    return drawed;
  }
  public static void drawMapString(Graphics g, String s, int x,int y, int anchor) {
    boolean drawed=false;
    if (RMSOption.getBoolOpt(RMSOption.BL_DARKENBACK)){
      drawed=drawDarkenBack(g, s, x,y, anchor);
      if (drawed){
        g.setColor(RMSOption.foreColor);
        g.drawString(s,x,y,anchor);
      }
    }
    if (drawed) return;
    imageSMDarken=null;
    imageMMDarken=null;
    imageLMDarken=null;
    g.setColor(RMSOption.shadowColor);
    g.drawString(s,x+1,y+1,anchor);
    g.setColor(RMSOption.foreColor);
    g.drawString(s,x,y,anchor);
  }
  public static void drawMapStringC(Graphics g, String s, int x,int y, int anchor) {
    g.setColor(RMSOption.shadowColor);
    g.drawString(s,x+1,y+1,anchor);
    g.setColor(RMSOption.foreColor);
    g.drawString(s,x,y,anchor);
  }
  
  public static void drawShadowString(Graphics g, String s, int x,int y, int color,int anchor,int shadow) {
    g.setColor(shadow);
    g.drawString(s,x+1,y+1,anchor);
    g.setColor(color);
    g.drawString(s,x,y,anchor);
  }
  
  private static void drawTriangle(Graphics g, int x1,int y1,int x2, int y2, int x3, int y3) {
    drawTriangle(g,x1, y1, x2,y2, x3,y3,0,0);
  }
  private static void drawTriangle(Graphics g, int x1,int y1,int x2, int y2, int x3, int y3,int ox,int oy) {
    g.drawLine(x1+ox, y1+oy, x2+ox,y2+oy);
    g.drawLine(x1+ox, y1+oy, x3+ox,y3+oy);
    g.drawLine(x3+ox, y3+oy, x2+ox,y2+oy);
  }
  
  public static int rotateMX(int x,int y) {
    int dx=x-dcx,dy=dcy-y;
//    return (int)(dcx-Math.sqrt(dx*dx+dy*dy)*Math.cos(MapUtil.atan2(dy,dx)+coursradPaint));
    return (int)(Math.sqrt(dx*dx+dy*dy)*Math.cos(MapUtil.atan2(dy,dx)+coursradPaint)+dcx);
  }
  
  public static int rotateMY(int x,int y) {
    int dx=x-dcx,dy=dcy-y;
    return (int)(dcy-Math.sqrt(dx*dx+dy*dy)*Math.sin(MapUtil.atan2(dy,dx)+coursradPaint));
    //return (int)(Math.sqrt(dx*dx+dy*dy)*Math.sin(-MapUtil.atan2(dy,dx)-coursradPaint)+dcy);
  }
  
  private int adaptSpeed(int sr) {
    //if (sr<0) sr=0;
    if (sr<1) sr=2;
    if (sr<2) sr=3;
    else if (sr<3) sr=5;
    else if (sr<4) sr=6;
    else if (sr<5) sr=7;
    else if (sr<8) sr=9;
    else if (sr<12) sr=11;
    else if (sr<20) sr=12;
//    else if (sr<16) sr=11;
//    else if (sr<20) sr=12;
//    else if (sr<25) sr=13;
    else if (sr<50) sr=13;
//    else if (sr<100) sr=15;
    else sr=14;
    return (int)((sr*dmaxx)/120);
  }
  
  //public boolean workOnline;
  
  private int scaleWeight;
  private int scaleWidth;
  //final static String scaleKM="km";
  //final static String scaleM="m";
  private String scaleMeas;
  private double[] scW = {50000,20000,10000,5000,2000,1000,500,200,100,50,20,10,5,2,1,0.5,0.2,0.1,0.05,0.02,0.01,0.005,0.002,0.001,0.0005,0.0002,0.0001,0.00005,0.00002,0.00001};
  
  private void setScale(double lat, int level, int scrw) {
    double alat=Math.abs(lat);
    
    double dlon = (double) scrw/MapUtil.getPixelsPerLonDegree(level);
    double dscrw = (dlon*MapUtil.G2R)*40000./(MapUtil.PImul2)*Math.cos((alat*MapUtil.G2R));
    //!1double dscrw = MapUtil.grad2rad(dlon)*40000./(2.*MapUtil.PI)*Math.cos(MapUtil.grad2rad(alat));
    
    int scWi=-1;
    for (int i=scW.length-1;i>=0;i--) {
      if (dscrw<scW[i]) {
        scWi=i+1;
        break;
      }
    }
    
    scaleWeight = (int) scW[scWi];
    if (scaleWeight>0) scaleMeas=LangHolder.getString(Lang.km);
    else {
      scaleWeight = (int) (scW[scWi]*1000);
      scaleMeas=LangHolder.getString(Lang.m);
    }
    double raddlon = scW[scWi]/(40000./(MapUtil.PImul2)*Math.cos((alat*MapUtil.G2R))) ;
    //!1double raddlon = scW[scWi]/(40000./(2.*MapUtil.PI)*Math.cos(MapUtil.grad2rad(alat))) ;
    scaleWidth = (int) ( raddlon*MapUtil.getPixelsPerLonRadian(level));
    
  }
  final private byte MAXLEVEL=20;
  //private String smaxLevel="20";
  MapTimerTask mapTT;
  final static private int MAPSCROLLPAUSE = 1500;
  private void zoom(int incL) {
    if (levelDisp+incL<1) return;
    if (levelDisp+incL>MAXLEVEL) return;
    
    //double lat,lon;
    //lat=MapUtil.getLat(yCenter, level);
    //lon=MapUtil.getLon(xCenter, level);
    //int ol=level;
    levelDisp=levelDisp+incL;
    //setLocation(lat, lon);
    if (mapTT!=null) mapTT.cancel();
    mapTT = new MapTimerTask(levelDisp,reallat,reallon,showMapSerDisp,showMapViewDisp,userMapIndexLabel);
    timer.schedule(mapTT,MAPSCROLLPAUSE);
    //setLocation(reallat, reallon);
    
    setSerViewLabel();
    //int xN=MapUtil.getNumX(xCenter);
    //int yN=MapUtil.getNumY(yCenter);
    //clearTilesInLevel(ol,xN,yN);
  }
  
  public void setLocation(double lat,double lon) {
    xCenter=MapUtil.getXMap(lon, level);
    yCenter=MapUtil.getYMap(lat, level);
  }
  public void setLocation(double lat,double lon, int lvl) {
    level=lvl;
    levelDisp=lvl;
    xCenter=MapUtil.getXMap(lon, level);
    yCenter=MapUtil.getYMap(lat, level);
    setSerViewLabel();
  }
  
  public void navigate2mark(MapMark mm) {
    activeRoute = new MapRoute(MapRoute.WAYPOINTSKIND);
    activeRoute.addMark(mm);
  }
  
  public MapPoint navigate2location(double lat,double lon, String name) {
    activeRoute = new MapRoute(MapRoute.WAYPOINTSKIND);
    MapPoint mp = new MapPoint(lat,lon,0,name);
    activeRoute.addMapPoint(mp);
    return mp;
  }
  
  public String serViewLabel=MapUtil.emptyString;
  private long lastViewLabelDisplayTime;

  public void setSerViewLabel() {
    lastViewLabelDisplayTime=System.currentTimeMillis()+5000;
    StringBuffer sb = new StringBuffer();
    if (showMapSerDisp==MapTile.SHOW_GL)
      sb.append(MapUtil.mapServ_Google);
    else if (showMapSerDisp==MapTile.SHOW_VE)
      sb.append(MapUtil.mapServ_VE);
    else if (showMapSerDisp==MapTile.SHOW_MP)
      try{
        if (userMapIndexLabel==0)
          sb.append(LangHolder.getString(Lang.no));
        else {
          if (userMapIndexLabel<=RMSOption.OMAPS_NAMES.length){
            sb.append('E').append(' ').append(RMSOption.OMAPS_NAMES[userMapIndexLabel-1]);
          } else {
            sb.append('I').append(' ').append(RMSOption.IMAPS_NAMES[userMapIndexLabel-RMSOption.OMAPS_NAMES.length-1]);
          }
        }
      }catch(Throwable t){} else if (showMapSerDisp==MapTile.SHOW_YH)
        sb.append(MapUtil.mapServ_Yahoo);
      else if (showMapSerDisp==MapTile.SHOW_AS)
        sb.append(MapUtil.mapServ_Ask);
      else if (showMapSerDisp==MapTile.SHOW_OS)
        sb.append(MapUtil.mapServ_OpenStreet);
      else if (showMapSerDisp==MapTile.SHOW_GU)
        sb.append(MapUtil.mapServ_Gurtam);
      else if (showMapSerDisp==MapTile.SHOW_UM)
        sb.append(MapUtil.mapServ_Online);

    if (showMapSerDisp!=MapTile.SHOW_MP) {
      if (showMapViewDisp==MapTile.SHOW_SUR)
        sb.append(LangHolder.getString(Lang.surface));
      else if (showMapViewDisp==MapTile.SHOW_MAP)
        sb.append(LangHolder.getString(Lang.map));
      else if (showMapViewDisp==MapTile.SHOW_SURMAP)
        sb.append(LangHolder.getString(Lang.hybrid));
    }
    sb.append(' ').append('M').append(':').append(levelDisp);
    serViewLabel=sb.toString();
  }
  public void setMapSerView(byte mapType) {
    byte mapTileType = (byte)(mapType&MapTile.SHOW_MASKSERVER);
    byte tileType = (byte)(mapType&MapTile.SHOW_SURMAP);
    
    for (int i=0;i<showMapSers.length;i++){
      if (mapTileType==showMapSers[i])
        for (int j=0;j<showMapViews[i].length;j++){
        if (tileType==showMapViews[i][j]) {
          curMapSerIndex=i;
          curMapViewIndex=j;
          showMapSer= showMapSers[curMapSerIndex];
          showMapView= showMapViews[curMapSerIndex][curMapViewIndex];
          showMapSerDisp= showMapSer;
          showMapViewDisp= showMapView;
          break;
        }
        }
    }
    setSerViewLabel();
  }
  
  public void setUserMapIndex(byte mapIndex,boolean forceCentrum){
    //if ((mapIndex==userMapIndex)&&(oml!=null)) return;
    if ((mapIndex==userMapIndexUsed)) return;
    if (oml!=null){
      oml.closeConn();
      oml=null;
    }
    if ((mapIndex>0)&&(mapIndex<=RMSOption.OMAPS_URLS.length)){
      oml=new OuterMapLoader(RMSOption.OMAPS_URLS[mapIndex-1],!(forceCentrum||RMSOption.getBoolOpt(RMSOption.BL_AUTOCENTEROM)));
    }else
      if ((mapIndex>0)&&(RMSOption.IMAPS_RMST.length>0)){
      int mi=mapIndex-RMSOption.OMAPS_URLS.length;
      rmss.switchIntMap(mi);
      if (forceCentrum||RMSOption.getBoolOpt(RMSOption.BL_AUTOCENTEROM)) {
        mi--;
        mi*=3;
        setLocation(RMSOption.IMAPS_CENT[mi],RMSOption.IMAPS_CENT[mi+1],(int)RMSOption.IMAPS_CENT[mi+2]);
      }
      }
    userMapIndexUsed=mapIndex;
    RMSOption.setByteOpt(RMSOption.BO_EXTMAPINDEX,mapIndex);
  }
  
  public byte userMapIndexUsed;
  public byte userMapIndexLabel;
//  public byte internalMapIndex;
//  private byte internalMapDisp;
  
  private void changeMapView() {
    if (showMapSerDisp==MapTile.SHOW_MP) {
      userMapIndexLabel=userMapIndexLabel>=(RMSOption.OMAPS_URLS.length+RMSOption.IMAPS_RMST.length)?0:(byte)(userMapIndexLabel+1);
    } else {
      curMapViewIndex = curMapViewIndex>=showMapViews[curMapSerIndex].length-1?0:curMapViewIndex+1;
      showMapViewDisp= showMapViews[curMapSerIndex][curMapViewIndex];
    }
    //curMapViewIndex_o=-1;
//    minCacheSizeb=(showMapView==MapTile.SHOW_SURMAP)?9:4;
    if (mapTT!=null) mapTT.cancel();
    mapTT = new MapTimerTask(levelDisp,reallat,reallon,showMapSerDisp,showMapViewDisp,userMapIndexLabel);
    timer.schedule(mapTT,MAPSCROLLPAUSE);
    
    //clearAllTiles();
    setSerViewLabel();
  }
  //private byte wasMV=MapTile.SHOW_SUR;
  //int curMapView_o=-1;
  //private boolean curMapViewIndex_oset;
  private void changeMapSer(){
    
    curMapSerIndex = curMapSerIndex>=showMapSers.length-1?0:curMapSerIndex+1;
    showMapSerDisp= showMapSers[curMapSerIndex];
    
    if (curMapViewIndex>showMapViews[curMapSerIndex].length-1){
      curMapViewIndex=0;
    }
    
    showMapViewDisp= showMapViews[curMapSerIndex][curMapViewIndex];
    
    if (mapTT!=null) mapTT.cancel();
    mapTT = new MapTimerTask(levelDisp,reallat,reallon,showMapSerDisp,showMapViewDisp,userMapIndexLabel);
    timer.schedule(mapTT,MAPSCROLLPAUSE);
    
    //clearAllTiles();
    setSerViewLabel();
  }
  
//  int curMapSerIndex = 0;
//  int curMapViewIndex = 0;
  private void changeMap(){
    if (showMapSerDisp==MapTile.SHOW_MP) {
      if (userMapIndexLabel>=(RMSOption.OMAPS_URLS.length+RMSOption.IMAPS_RMST.length)) changeMapSer();
      else changeMapView();
    } else {
      if (curMapViewIndex>=showMapViews[curMapSerIndex].length-1) changeMapSer();
      else changeMapView();
    }
  }
  
  private static boolean keyLocked;
  private void changeKeyLock() {
    lightControlled=lightControlled||(RMSOption.getBoolOpt(RMSOption.BL_LIGHTONLOCK));
    keyLocked = !keyLocked;
    if (keyLocked) {
      setCommandListener(null);
      removeCommand(addrCommand);
      removeCommand(saveMCommand);
      removeCommand(gpsCommand);
      removeCommand(navCommand);
      removeCommand(loadCommand);
      removeCommand(setCommand);
      removeCommand(helpCommand);
      removeCommand(exitCommand);
      removeCommand(minCommand);
      
      //   removeCommand(gpsStatCommand);
      removeCommand(netRadCommand);
      setKeyLockTimer();
      if (RMSOption.getBoolOpt(RMSOption.BL_LIGHTONLOCK)){
        RMSOption.lightOn=false;
        RMSOption.light50=true;
        setLight();
      }
      // AlertType.CONFIRMATION.playSound(bm.display);
    } else {
      setCommandListener(this);
      
      addCommand(addrCommand);
      addCommand(saveMCommand);
      if (MNSInfo.bluetoothAvailable()||MNSInfo.comportsAvailable()||MNSInfo.locationAvailable()) addCommand(gpsCommand);
      addCommand(netRadCommand);
      addCommand(navCommand);
      addCommand(loadCommand);
      addCommand(setCommand);
      addCommand(helpCommand);
      addCommand(exitCommand);
      
      if (RMSOption.getBoolOpt(RMSOption.BL_ADDMINIZE))
        addCommand(minCommand);
      
      //   if (gpsReader!=null)
      //    addCommand(gpsStatCommand);
      setKeyLockTimer();
      //  AlertType.INFO.playSound(bm.display);
      if (RMSOption.getBoolOpt(RMSOption.BL_LIGHTONLOCK)){
        RMSOption.lightOn=true;
        RMSOption.light50=true;
        setLight();
      }
    }
//#mdebug
//#     if (RMSOption.debugEnabled)
//#       DebugLog.add2Log("KLC:"+String.valueOf(keyLocked));
//#enddebug
    
  }
  
  
  // Stylus events handling (for future)
  int pX,pY,wX,wY;
  int scaleBoxXP,scaleBoxXM,scaleBoxYP,scaleBoxYM,scaleBoxH;
  int mapBoxXS,mapBoxXM,mapBoxYS,mapBoxYM;
  int minBoxX,minBoxY;
  int bindBoxX,bindBoxY;
  int menuBoxX,menuBoxY;
  int rotBoxX,rotBoxY;

  private boolean pointerActive;
  private long pointerActivatedTime;

  boolean notDragging;
  protected void pointerDragged(int x, int y) {
    if (notDragging) return;
    if ((pX==x)&&(pY==y)) return;
    pressedUsed=true;
    int mX,mY;
    if (landscapeMap){
      mX=-pX+x;
      mY=pY-y;
      //mX=-mX;
      //mY=-mY;
    }else{
      mX=pX-x;
      mY=pY-y;
    }
    if (mX>0) {
      if (wX+mX<MapUtil.getBitmapSize(level)-5)
        if(landscapeMap)
          xCenter=wX+mY;
        else
          xCenter=wX+mX;
    } else
      if (wX+mX>5)
        if(landscapeMap)
          xCenter=wX+mY;else
            xCenter=wX+mX;
    if (mY>0) {
      if (wY+mY<MapUtil.getBitmapSize(level)-5)
        if (landscapeMap)
          yCenter=wY+mX; else
            yCenter=wY+mY;
    }else
      if (wY+mY>5)
        if (landscapeMap)
          yCenter=wY+mX;
        else yCenter=wY+mY;
    repaint();
  }
  private boolean pressedUsed;
  protected void pointerPressed(int x, int y) {

    pointerActivatedTime=System.currentTimeMillis();

    if (!pointerActive){
      pointerActive=true;
      return;
    }

//    activeRoute=new KMLMapRoute(MapRoute.WAYPOINTSKIND);
//    ((KMLMapRoute)activeRoute).start();
    pressedUsed=true;
    notDragging=true;
    if (mode==MAPMODE) {
      if ((!landscapeMap&&(x>scaleBoxXP)&&(x<scaleBoxXP+scaleBoxH)&&(y>scaleBoxYP)&&(y<scaleBoxYP+scaleBoxH))
      ||(landscapeMap&&(y>scaleBoxXP)&&(y<scaleBoxXP+scaleBoxH)&&(dmaxy-x>scaleBoxYP)&&(dmaxy-x<scaleBoxYP+scaleBoxH))) {
        zoom(1);
        repaint();
        return;
      }
      if ((!landscapeMap&&(x>scaleBoxXM)&&(x<scaleBoxXM+scaleBoxH)&&(y>scaleBoxYM)&&(y<scaleBoxYM+scaleBoxH))
      ||(landscapeMap&&(y>scaleBoxXM)&&(y<scaleBoxXM+scaleBoxH)&&(dmaxy-x>scaleBoxYM)&&(dmaxy-x<scaleBoxYM+scaleBoxH))) {
        zoom(-1);
        repaint();
        return;
      }
      if ((!landscapeMap&&(x>mapBoxXS)&&(x<mapBoxXS+scaleBoxH)&&(y>mapBoxYS)&&(y<mapBoxYS+scaleBoxH))
      ||(landscapeMap&&(y>mapBoxXS)&&(y<mapBoxXS+scaleBoxH)&&(dmaxy-x>mapBoxYS)&&(dmaxy-x<mapBoxYS+scaleBoxH))) {
        changeMapSer();
        repaint();
        return;
      }
      if ((!landscapeMap&&(x>mapBoxXM)&&(x<mapBoxXM+scaleBoxH)&&(y>mapBoxYM)&&(y<mapBoxYM+scaleBoxH))
      ||(landscapeMap&&(y>mapBoxXM)&&(y<mapBoxXM+scaleBoxH)&&(dmaxy-x>mapBoxYM)&&(dmaxy-x<mapBoxYM+scaleBoxH))) {
        changeMapView();
        repaint();
        return;
      }
      if ((!landscapeMap&&(x>minBoxX)&&(x<minBoxX+scaleBoxH)&&(y>minBoxY)&&(y<minBoxY+scaleBoxH))
      ||(landscapeMap&&(y>minBoxX)&&(y<minBoxX+scaleBoxH)&&(dmaxy-x>minBoxY)&&(dmaxy-x<minBoxY+scaleBoxH))) {
        RMSOption.fullScreen=!RMSOption.fullScreen;
        setFullScreenMode(RMSOption.fullScreen);
        repaint();
        return;
      }
      if ((!landscapeMap&&(x>bindBoxX)&&(x<bindBoxX+scaleBoxH)&&(y>bindBoxY)&&(y<bindBoxY+scaleBoxH))
      ||(landscapeMap&&(y>bindBoxX)&&(y<bindBoxX+scaleBoxH)&&(dmaxy-x>bindBoxY)&&(dmaxy-x<bindBoxY+scaleBoxH))) {
        if (mode==MAPMODE) gpsBinded = !gpsBinded;
        repaint();
        return;
      }
      if ((!landscapeMap&&(x>menuBoxX)&&(x<menuBoxX+scaleBoxH)&&(y>menuBoxY)&&(y<menuBoxY+scaleBoxH))
      ||(landscapeMap&&(y>menuBoxX)&&(y<menuBoxX+scaleBoxH)&&(dmaxy-x>menuBoxY)&&(dmaxy-x<menuBoxY+scaleBoxH))) {
        showHideMenu();
        repaint();
        return;
      }
      if ((!landscapeMap&&(x>rotBoxX)&&(x<rotBoxX+scaleBoxH)&&(y>rotBoxY)&&(y<rotBoxY+scaleBoxH))
      ||(landscapeMap&&(y>rotBoxX)&&(y<rotBoxX+scaleBoxH)&&(dmaxy-x>rotBoxY)&&(dmaxy-x<rotBoxY+scaleBoxH))) {
        landscapeMap=!landscapeMap;
        updateSize=true;
        repaint();
        return;
      }
    }
    if (canvasMenu!=null)
      if (canvasMenu.processPointer(x,y))return;
    
    if (gpsBinded||(mode!=MAPMODE)){
      int x1=dmaxx/3;
      int x2=x1+x1;
      int y1=dmaxy/3;
      int y2=y1+y1;
//      if(landscapeMap){
//        int a=x1;
//        x1=y1;
//        y1=x1;
//        a=x2;
//        x2=y2;
//        y2=a;
//      }
      if ((y1>y)&&(x>x1)&&(x<x2)) {
        if (landscapeMap)
          changeView(false);
        else nextMode();
        repaint();
        return;
      }
      if ((y2<y)&&(x>x1)&&(x<x2)) {
        if (landscapeMap) changeView(true);
        else prevMode();
        repaint();
        return;
      }
      if ((x1>x)&&(y>y1)&&(y<y2)) {
        if (landscapeMap) prevMode();
        else changeView(false);
        repaint();
        return;
      }
      if ((x2<x)&&(y>y1)&&(y<y2)) {
        if (landscapeMap) nextMode();
        else changeView(true);
        repaint();
        return;
      }
      pointerPressedTime=System.currentTimeMillis();
      pressedUsed=false;
      return;
    }
    pressedUsed=false;
    notDragging=false;
    if(landscapeMap){
      pX=x;pY=y;wX=xCenter;wY=yCenter;
    }else{
      pX=x;pY=y;wX=xCenter;wY=yCenter;
    }
    pointerPressedTime=System.currentTimeMillis();
  }
  
  private long pointerPressedTime;
  
  protected void pointerReleased(int x, int y) {
    if (activeTrack!=null) {
          activeTrack.clearNearestTrackpointDisplay();
          activeTrack.drawNearestTrackPoint();
      }
    
    if ((System.currentTimeMillis()-pointerPressedTime>1500)&&(
       (Math.abs(x-pX)<5)&& (Math.abs(y-pY)<5)
        )){
      addMapPoint2Route(MapUtil.getLat(yCenter+y-getHeight()/2, level),
        MapUtil.getLon(xCenter+x-getWidth()/2, level));
      repaint();
      return;
    }
    
    if (pressedUsed) return;
    
    int mX,mY;
    if (landscapeMap){
      mX=dcx-x;
      mY=y-dcy;
      //mX=-mX;
      //mY=-mY;
    }else{
      mX=x-dcx;
      mY=y-dcy;
    }
    if (mX>0) {
      if (wX+mX<MapUtil.getBitmapSize(level)-5)
        if(landscapeMap)
          xCenter=wX+mY;
        else
          xCenter=wX+mX;
    } else
      if (wX+mX>5)
        if(landscapeMap)
          xCenter=wX+mY;else
            xCenter=wX+mX;
    if (mY>0) {
      if (wY+mY<MapUtil.getBitmapSize(level)-5)
        if (landscapeMap)
          yCenter=wY+mX; else
            yCenter=wY+mY;
    }else
      if (wY+mY>5)
        if (landscapeMap)
          yCenter=wY+mX;
        else yCenter=wY+mY;
    
       repaint();
  }
  
  static private byte moveStep=1;
  
  final private static byte KEY_PRESSED = 1;
  final private static byte KEY_RELEASED = 2;
  final private static byte KEY_REPEATED = 3;
  
// Key events handling
  protected void keyPressed(int keyCode) { keyEvent(KEY_PRESSED, keyCode); }
  protected void keyReleased(int keyCode) { keyEvent(KEY_RELEASED, keyCode); }
  protected void keyRepeated(int keyCode) { keyEvent(KEY_REPEATED, keyCode); }
  
  private int[] keys={0,0,0,0};
  public boolean secretGeoCombination(){
    return ((keys[3]==KEY_STAR)&&(keys[2]==KEY_STAR)&&(keys[1]==KEY_NUM2)&&(keys[0]==KEY_NUM8));
  }
  private float scaleNavFactor=1.0f;
  
  /** Time of press moment */
  private long timePressed;
  private boolean keyLC;
  public final void keyEvent(byte eventType,int key) {
    if (dead) {
      repaint();
      return;
    }
    long currTime = System.currentTimeMillis();
    boolean smoothScroll =RMSOption.getBoolOpt(RMSOption.BL_SMOOTH_SCROLL);
//coursradPaint+=0.1;
    if (eventType==KEY_PRESSED)
      if ((key==KEY_STAR)||(key==KEY_NUM0)||(key==KEY_NUM1)||(key==KEY_NUM2)||(key==KEY_NUM3)
      ||(key==KEY_NUM4)||(key==KEY_NUM5)||(key==KEY_NUM6)||(key==KEY_NUM7)
      ||(key==KEY_NUM8)||(key==KEY_NUM9)) {
      for (int i=keys.length-1;i>0;i--)
        keys[i]=keys[i-1];
      keys[0]=key;
      }
    if (keyLocked)
      if (key!=KEY_STAR ) return;
    
    boolean eD=false;
    boolean notify=false;
//    if (fileMapLoader!=null) return;
    if (eventType==KEY_RELEASED) {
      moveX=0;moveY=0;
////#mdebug
////#       if (RMSOption.debugEnabled)
////#         DebugLog.add2Log("KE R:"+String.valueOf(keyLocked)+'|'+String.valueOf(keyLC)+'|'+String.valueOf(key));
////#enddebug
      
      moveStep=1;
      if (key!=KEY_STAR && key!=KEY_NUM4 && key!=KEY_NUM6 && key!=KEY_NUM5&& key!=KEY_NUM1 && key!=KEY_NUM3 && key!=KEY_NUM7&& key!=KEY_NUM9&& key!=KEY_NUM2&& key!=KEY_NUM8   ) return;
      //if (timePressed+1000<System.currentTimeMillis()) return;
      
      if (keyLC) return;
      if (keyLocked) return;
////#mdebug
////#       if (RMSOption.debugEnabled)
////#         DebugLog.add2Log("KE R ends");
////#enddebug
      if (activeTrack!=null) {
          activeTrack.clearNearestTrackpointDisplay();
          activeTrack.drawNearestTrackPoint();
      }
    }
    
    if (eventType==KEY_PRESSED)  {
        if (activeTrack!=null) {
          activeTrack.clearNearestTrackpointDisplay();
          //activeTrack.drawNearestTrackPoint();
      }

      if (!smoothScroll) {
        if (scrollMapThread!=null){
          scrollMapThread.stopped=true;
          synchronized (this){
            notifyAll();
          }
        }
        scrollMapThread=null;
      } else
        if (scrollMapThread==null){
        scrollMapThread = new ScrollMapThread();
        scrollMapThread.start();
        }
      
      
////#mdebug
//      if (RMSOption.debugEnabled)
//        DebugLog.add2Log("KE P:"+String.valueOf(keyLocked)+'|'+String.valueOf(keyLC)+'|'+String.valueOf(key));
////#enddebug
      keyLC=false;
      moveStep=1;
      timePressed=currTime;
      if ((key==KEY_STAR)||(key==KEY_NUM4)||(key==KEY_NUM6)||(key==KEY_NUM5)||(key==KEY_NUM1)||(key==KEY_NUM3)||(key==KEY_NUM7)||(key==KEY_NUM9)||(key==KEY_NUM2)||(key==KEY_NUM8)) return;
////#mdebug
//      if (RMSOption.debugEnabled)
//        DebugLog.add2Log("KE P ends");
////#enddebug
    }
    
    if (eventType==KEY_REPEATED) {
////#mdebug
//      if (RMSOption.debugEnabled)
//        DebugLog.add2Log("KE C:"+String.valueOf(keyLocked)+'|'+String.valueOf(keyLC)+'|'+String.valueOf(key));
////#enddebug
      if ((key==KEY_NUM5)||(key==KEY_NUM6)||(key==KEY_NUM4)||(key==KEY_NUM1)||
      (key==KEY_NUM3)||(key==KEY_NUM7)||(key==KEY_NUM9)||(key==KEY_NUM2)||(key==KEY_NUM8))  {
        if (timePressed+1300>currTime) return;
        timePressed=currTime+1000000;
        keyLC=true;
        
        if (key==KEY_NUM5)
        mapKeyFunction(RMSOption.KEY_5HOLD);
        else
        if (key==KEY_NUM6)
        mapKeyFunction(RMSOption.KEY_6HOLD);
        else
        if (key==KEY_NUM4)
        mapKeyFunction(RMSOption.KEY_4HOLD);
        else
        if (key==KEY_NUM1)
        mapKeyFunction(RMSOption.KEY_1HOLD);
        else
        if (key==KEY_NUM3)
        mapKeyFunction(RMSOption.KEY_3HOLD);
        else
        if (key==KEY_NUM7)
        mapKeyFunction(RMSOption.KEY_7HOLD);
        else
        if (key==KEY_NUM9)
        mapKeyFunction(RMSOption.KEY_9HOLD);
        else
        if (key==KEY_NUM2)
        mapKeyFunction(RMSOption.KEY_2HOLD);
        else
        if (key==KEY_NUM8)
        mapKeyFunction(RMSOption.KEY_8HOLD);
          
        repaint();
        return;
      }
      
      
      if (key==KEY_STAR) {
////#mdebug
//        if (RMSOption.debugEnabled)
//          DebugLog.add2Log("KE C S in");
////#enddebug
        if (timePressed+50>currTime) return;
        timePressed=currTime+1000000;
        changeKeyLock();
        keyLC=true;
////#mdebug
//        if (RMSOption.debugEnabled)
//          DebugLog.add2Log("KE C S out");
////#enddebug
        return;
      }
////#mdebug
//      if (RMSOption.debugEnabled)
//        DebugLog.add2Log("KE C ends");
////#enddebug
      if (dmaxx<140) {
        if (moveStep<10) moveStep++;} else
          if (moveStep<20) moveStep++;
      
    }
    
    LightTimer.lastPressed=currTime;
    if ((eventType==KEY_RELEASED)&&(key!=KEY_STAR)&&(key!=KEY_NUM4)&&(key!=KEY_NUM6)&&(key!=KEY_NUM5)&&(key!=KEY_NUM1)&&(key!=KEY_NUM3)&&(key!=KEY_NUM7)&&(key!=KEY_NUM9)&&(key!=KEY_NUM2)&&(key!=KEY_NUM8)) return;
    int gA;
    boolean tb=canvasMenu!=null;
    if (tb)tb=canvasMenu.processKey(key);
    if (!tb)
    //  if ((!extMenu)&&(!routeMenu)&&(!gpsMenu)) 
      {
      
      switch(key) {
        case KEY_STAR:
          showHideMenu();
          //if (mode==MAPMODE)
          //  extMenu=!extMenu;
          
          eD=true;
          break;
        case KEY_POUND:
          //gpsReader.COURSE+=5;
          if (mode==MAPMODE)
            mapKeyFunction(RMSOption.KEY_SHARP);
          
          eD=true;
          break;
        case KEY_NUM1:
          if (mode==MAPMODE)
            mapKeyFunction(RMSOption.KEY_1);
          else
            if (mode==NAVMODE) {
            if (navMode==NAVWPTMODE) {
              if (activeRoute!=null) activeRoute.maprouteOffs+=1;} else if (scaleNavFactor<128)
                scaleNavFactor*=2;
            }
          eD=true;
          break;
        case KEY_NUM3:
          if (mode==MAPMODE)
            mapKeyFunction(RMSOption.KEY_3);
          else
            if (mode==NAVMODE) {
            if (navMode==NAVWPTMODE) {
              if (activeRoute!=null) activeRoute.maprouteOffs-=1;} else if (scaleNavFactor>0.01)
                scaleNavFactor*=0.5;
            }
          eD=true;
          break;
        case KEY_NUM7:
          //if (mode==MAPMODE)
          mapKeyFunction(RMSOption.KEY_7);
          eD=true;
          break;
        case KEY_NUM9:
          //  if (mode==MAPMODE)
          mapKeyFunction(RMSOption.KEY_9);
          eD=true;
          break;
        case KEY_NUM0:
          if (mode==MAPMODE)
            mapKeyFunction(RMSOption.KEY_0);
          
          else if (mode==STATMODE){
            if (infoMode==SYSTEMINFOMODE)
              System.gc();
            if (infoMode==ALTINFOMODE)
              GPSReader.resetAlt();
            else if (infoMode==ACCELINFOMODE)
              GPSReader.resetA();
            else if (infoMode==ALTSPEEDINFOMODE)
              GPSReader.resetAS();
            else if (infoMode==SPEEDINFOMODE)
              GPSReader.resetV();
            else if (infoMode==TRAVELINFOMODE){
              RMSOption.DISTANCE=0;
              RMSOption.USED=0;
            }
          }
          eD=true;
          break;
        case KEY_NUM2:
          //addMapPoint2Route();
          mapKeyFunction(RMSOption.KEY_2);
          //prevMode();
          eD=true;
          break;
        case KEY_NUM8:
          mapKeyFunction(RMSOption.KEY_8);
          //nextMode();
          eD=true;
          break;
        case KEY_NUM4:
          //nextMode();
          mapKeyFunction(RMSOption.KEY_4);
          eD=true;
          break;
        case KEY_NUM6:
          mapKeyFunction(RMSOption.KEY_6);
          eD=true;
          break;
        case KEY_NUM5:
          //#if SE_K750_E_BASEDEV
//#           if (mode==MAPMODE)
//#             gpsBinded = !gpsBinded;
//#           else if (mode==TRACKELEVATIONMODE) prfVertLines=!prfVertLines;
//#           else {
//#             currModeIndex=0;mode=MAPMODE;
//#           }
          //#else
          if (mode==MAPMODE)
            gpsBinded = !gpsBinded;
          else if (mode==TRACKELEVATIONMODE) prfVertLines=!prfVertLines;
          else {
            currModeIndex=0;mode=MAPMODE;
          }
          //#endif
          eD=true;
          break;
        default:
          break;
      }
      if (!eD) {
        switch(gA = getGameAction(key)) {
          
          case LEFT: // '\004'
            if (RMSOption.mapCorrectMode) {mapXCorrect++;mapLCorrect=(short)level; break;}
            if (mode==MAPMODE) {
              if (gpsBinded){
                //  zoom(1);
              } else
                if (smoothScroll) {
                
                if (landscapeMap) {if (moveY==0)moveY=1;} else if (moveX==0) moveX=-1;
                notify=true;
                
                } else
                  if (landscapeMap){
                if (yCenter+moveStep<MapUtil.getBitmapSize(level))
                  yCenter=yCenter+moveStep;}else{
                if (xCenter-moveStep>0)
                  xCenter=xCenter-moveStep;
                  }
              
            }else
              changeView(false);
            break;
          case FIRE:
            //#if SE_K750_E_BASEDEV
//#             return;
            //#else
            if (mode==MAPMODE)
              gpsBinded = !gpsBinded;
            else if (mode==TRACKELEVATIONMODE) prfVertLines=!prfVertLines;
            else {
              currModeIndex=0;mode=MAPMODE;
            }
            break;
            //#endif
          default:
            break;
            
          case UP: // '\002'
            if (RMSOption.mapCorrectMode) {mapYCorrect++;mapLCorrect=(short)level;break;}
            if ((mode==MAPMODE)&&(!gpsBinded)) {
              if (smoothScroll) {
                if (landscapeMap) {
                  if (moveX==0) moveX=-1;} else if (moveY==0) moveY=-1;
                notify=true;
                
              } else
                if (landscapeMap){
                if (xCenter-moveStep>0)
                  xCenter=xCenter-moveStep;
                }else{
                if (yCenter-moveStep>0)
                  yCenter=yCenter-moveStep;
                }
              
            } else
              prevMode();
            break;
            
            
          case RIGHT: // '\006'
            if (RMSOption.mapCorrectMode) {mapXCorrect--;mapLCorrect=(short)level;break;}
            if (mode==MAPMODE) {
              if (gpsBinded){
                //zoom(-1);
              } else
                if (smoothScroll){
                if (landscapeMap) {
                  if (moveY==0)
                    moveY=-1; }else if (moveX==0) moveX=1;
                notify=true;
                
                } else
                  if (landscapeMap){
                if (yCenter-moveStep>0)
                  yCenter=yCenter-moveStep;}else{
                if (xCenter+moveStep<MapUtil.getBitmapSize(level))
                  xCenter=xCenter+moveStep;
                  }
              
            }else
              changeView(true);
            break;
            
          case DOWN: // '\b'
            if (RMSOption.mapCorrectMode) {mapYCorrect--;mapLCorrect=(short)level;break;}
            if ((mode==MAPMODE)&&(!gpsBinded)) {
              if (smoothScroll){
                if (landscapeMap) {
                  if (moveX==0)moveX=1;}else if (moveY==0)moveY=1;
                notify=true;
                
              } else
                if (landscapeMap){
                if (xCenter+moveStep<MapUtil.getBitmapSize(level))
                  xCenter=xCenter+moveStep;}else{
                if (yCenter+moveStep<MapUtil.getBitmapSize(level))
                  yCenter=yCenter+moveStep;
                  }
              
            } else
              nextMode();
            break;
        }
      }
      }
    
    if (notify){
      synchronized (this){
        notifyAll();
      }
    } else
      repaint();
  }
  
  public static boolean lightControlled;
  private void changeLight() {
    //#mdebug
//#     showmsg(LangHolder.getString(Lang.info),"No light support in DEBUG versions!",AlertType.INFO,this);
//# 
    //#enddebug
    //#if CustomDevice || CustomDeviceSign
//#     lightControlled=true;
//#     if (!RMSOption.lightOn){
//#       RMSOption.lightOn=true;
//#       RMSOption.light50=true;
//#     } else
//#       if (RMSOption.light50) {
//#       RMSOption.light50=false;
//#       RMSOption.lightOn=true;
//#       } else
//#         if (!RMSOption.light50) {
//#       RMSOption.light50=true;
//#       RMSOption.lightOn=false;
//#         }
//#     setLight();
    //#endif
  }
  
  //private boolean zooSent;
  public static boolean inetAvailable;
  
  private void runInetUpdates() {
    //   if (rmss.getZooSent()) return;
    if (!RMSOption.zooSent) {
      String s=System.getProperty("microedition.platform");
      if (s.equals("j2me")) RMSOption.zooSent=true;
      if (s.endsWith("JAVASDK")) RMSOption.zooSent=true;
    }
    if (!RMSOption.zooSent) {
      try {
        RMSOption.zooSent=true;
        rmss.writeSettingNow();
        //no run zoo anymore
      }catch (Throwable a) {}
    }
     if (!versionStarted) {
      if (System.currentTimeMillis()-RMSOption.lastUpdateTime>86400000)
      try {
        versionStarted=true;
        RMSOption.lastUpdateTime = System.currentTimeMillis();
        rmss.writeSettingNow();
        new LastVersion(getWidth(), getHeight());
      }catch(Throwable t){}
    }
  }
  
  private boolean versionStarted;
//  private void runLastVersion() {
//   
//  }
  
  ScreenSend mapSend;
  
  public void fillChoiceLists(boolean[] b) {
    for (int mi=0;mi<showMapSers.length;mi++){
      
      if (showMapSers[mi]==MapTile.SHOW_GL) {
        for (int vi=0;vi<showMapViews[mi].length;vi++) {
          if (showMapViews[mi][vi]==MapTile.SHOW_SUR) b[1]=true;
          if (showMapViews[mi][vi]==MapTile.SHOW_SURMAP) b[2]=true;
          if (showMapViews[mi][vi]==MapTile.SHOW_MAP) b[3]=true;
        }
      }
      
      if (showMapSers[mi]==MapTile.SHOW_VE) {
        for (int vi=0;vi<showMapViews[mi].length;vi++) {
          if (showMapViews[mi][vi]==MapTile.SHOW_SUR) b[4]=true;
          if (showMapViews[mi][vi]==MapTile.SHOW_MAP) b[5]=true;
        }
      }
      
      if (showMapSers[mi]==MapTile.SHOW_MP) {
        for (int vi=0;vi<showMapViews[mi].length;vi++) {
          if (showMapViews[mi][vi]==MapTile.SHOW_SUR) b[0]=true;
        }
      }
      
      if (showMapSers[mi]==MapTile.SHOW_YH) {
        for (int vi=0;vi<showMapViews[mi].length;vi++) {
          if (showMapViews[mi][vi]==MapTile.SHOW_SUR) b[6]=true;
          if (showMapViews[mi][vi]==MapTile.SHOW_MAP) b[7]=true;
        }
      }
      
      if (showMapSers[mi]==MapTile.SHOW_AS) {
        for (int vi=0;vi<showMapViews[mi].length;vi++) {
          if (showMapViews[mi][vi]==MapTile.SHOW_SUR) b[8]=true;
          if (showMapViews[mi][vi]==MapTile.SHOW_SURMAP) b[9]=true;
          if (showMapViews[mi][vi]==MapTile.SHOW_MAP) b[10]=true;
        }
      }
      
      if (showMapSers[mi]==MapTile.SHOW_OS) {
        for (int vi=0;vi<showMapViews[mi].length;vi++) {
          if (showMapViews[mi][vi]==MapTile.SHOW_MAP) b[11]=true;
          if (showMapViews[mi][vi]==MapTile.SHOW_SUR) b[12]=true;
        }
      }
      
      if (showMapSers[mi]==MapTile.SHOW_GU) {
        for (int vi=0;vi<showMapViews[mi].length;vi++) {
          if (showMapViews[mi][vi]==MapTile.SHOW_MAP) b[13]=true;
        //  if (showMapViews[mi][vi]==MapTile.SHOW_SUR) b[14]=true;
        }
      }

       if (showMapSers[mi]==MapTile.SHOW_UM) {
        for (int vi=0;vi<showMapViews[mi].length;vi++) {
          if (showMapViews[mi][vi]==MapTile.SHOW_SUR) b[14]=true;
          if (showMapViews[mi][vi]==MapTile.SHOW_MAP) b[15]=true;
        }
      }
      
    }
  }
  
  public void fillSerViewLists(boolean[] b) {
    showMapSers=null;
    showMapViews=null;
    int mscount=0,mi=0;
    //определяем какие сервера
    if (b[1]|b[2]|b[3]) mscount++;
    if (b[4]|b[5]) mscount++;
    if (b[0]) mscount++;
    if (b[6]|b[7]) mscount++;
    //ASK
    if (b[8]|b[9]|b[10]) mscount++;
    //OS
    if (b[11]||b[12]) mscount++;
    //GU
    if (b[13]) mscount++;
    //UM
    if (b[14]||b[15]) mscount++;
    
    showMapSers = new byte[mscount];
    showMapViews = new byte[mscount][];
    //добавляем сервера в массив
    if (b[1]|b[2]|b[3]) {
      showMapSers[mi]=MapTile.SHOW_GL;
      mi++;
    }
    if (b[4]|b[5]) {
      showMapSers[mi]=MapTile.SHOW_VE;
      mi++;
    }
    if (b[0]) {
      showMapSers[mi]=MapTile.SHOW_MP;
      mi++;
    }
    if (b[6]|b[7]) {
      showMapSers[mi]=MapTile.SHOW_YH;
      mi++;
    }
    if (b[8]|b[9]|b[10]) {
      showMapSers[mi]=MapTile.SHOW_AS;
      mi++;
    }
    if (b[11]||b[12]) {
      showMapSers[mi]=MapTile.SHOW_OS;
      mi++;
    }
    if (b[13]) {
      showMapSers[mi]=MapTile.SHOW_GU;
      mi++;
    }
    if (b[14]||b[15]) {
      showMapSers[mi]=MapTile.SHOW_UM;
      mi++;
    }
    
    mi=0;mscount=0;
    //добавляем режимы по серверам в массив
    if (b[1]|b[2]|b[3]) {
      if (b[1]) mscount++;
      if (b[2]) mscount++;
      if (b[3]) mscount++;
      
      showMapViews[mi]=new byte[mscount];
      mscount=0;
      if (b[1]) {showMapViews[mi][mscount]=MapTile.SHOW_SUR ;mscount++;}
      if (b[2]) {showMapViews[mi][mscount]=MapTile.SHOW_SURMAP ;mscount++;}
      if (b[3]) {showMapViews[mi][mscount]=MapTile.SHOW_MAP ;mscount++;}
      mi++;
    }
    
    mscount=0;
    //добавляем режимы по серверам в массив
    if (b[4]|b[5]) {
      if (b[4]) mscount++;
      if (b[5]) mscount++;
      
      showMapViews[mi]=new byte[mscount];
      mscount=0;
      if (b[4]) {showMapViews[mi][mscount]=MapTile.SHOW_SUR ;mscount++;}
      if (b[5]) {showMapViews[mi][mscount]=MapTile.SHOW_MAP ;mscount++;}
      mi++;
    }
    
    mscount=0;
    //добавляем режимы по серверам в массив
    if (b[0]) {
      if (b[0]) mscount++;
      
      showMapViews[mi]=new byte[mscount];
      mscount=0;
      if (b[0]) {showMapViews[mi][mscount]=MapTile.SHOW_SUR ;mscount++;}
      mi++;
    }
    
    mscount=0;
    //добавляем режимы по серверам в массив
    if (b[6]|b[7]) {
      if (b[6]) mscount++;
      if (b[7]) mscount++;
      
      showMapViews[mi]=new byte[mscount];
      mscount=0;
      if (b[6]) {showMapViews[mi][mscount]=MapTile.SHOW_SUR ;mscount++;}
      if (b[7]) {showMapViews[mi][mscount]=MapTile.SHOW_MAP ;mscount++;}
      mi++;
    }
    mscount=0;
    if (b[8]|b[9]|b[10]) {
      if (b[8]) mscount++;
      if (b[9]) mscount++;
      if (b[10]) mscount++;
      
      showMapViews[mi]=new byte[mscount];
      mscount=0;
      if (b[8]) {showMapViews[mi][mscount]=MapTile.SHOW_SUR ;mscount++;}
      if (b[9]) {showMapViews[mi][mscount]=MapTile.SHOW_SURMAP ;mscount++;}
      if (b[10]) {showMapViews[mi][mscount]=MapTile.SHOW_MAP ;mscount++;}
      mi++;
    }
    
    mscount=0;
    
    if (b[11]||b[12]) {
      if (b[11]) mscount++;
      if (b[12]) mscount++;
      
      showMapViews[mi]=new byte[mscount];
      mscount=0;
      if (b[11]) {showMapViews[mi][mscount]=MapTile.SHOW_MAP ;mscount++;}
      if (b[12]) {showMapViews[mi][mscount]=MapTile.SHOW_SUR ;mscount++;}
      mi++;
    }
    
    mscount=0;
    //Гуртам
    if (b[13]) {
      if (b[13]) mscount++;
    //  if (b[14]) mscount++;
      
      showMapViews[mi]=new byte[mscount];
      mscount=0;
      if (b[13]) {showMapViews[mi][mscount]=MapTile.SHOW_MAP ;mscount++;}
    //  if (b[14]) {showMapViews[mi][mscount]=MapTile.SHOW_SUR ;mscount++;}
      mi++;
    }

    mscount=0;

    if (b[14]||b[15]) {
      if (b[14]) mscount++;
      if (b[15]) mscount++;

      showMapViews[mi]=new byte[mscount];
      mscount=0;
      if (b[14]) {showMapViews[mi][mscount]=MapTile.SHOW_SUR ;mscount++;}
      if (b[15]) {showMapViews[mi][mscount]=MapTile.SHOW_MAP ;mscount++;}
      mi++;
    }
    
  }
  private Form wptForm;
  private Command saveCommand;
  private TextField textName;
  private ChoiceGroup choiceAddType;
  private TextField textDist;
  private TextField textAzim;
  
  private void addMapPoint2Route(double reallat,double reallon){
    if (RMSOption.getByteOpt(RMSOption.BO_HOLD5ADD)==RMSOption.HOLD5_MARK){
      //setCurrent(MapMidlet.mm.get_formSaveMark());
      (new MarkList(marks,false)).showAddMarkForm();
      return;
    }
    
    if (activeRoute==null) {
      activeRoute=MapRoute.createRoute(MapRoute.WAYPOINTSKIND);
    } else
        if (activeRoute instanceof KMLMapRoute)
            return;
    MapPoint mp;
    if ((gpsReader!=null)&&(gpsBinded))
      mp = new MapPoint(GPSReader.LATITUDE,GPSReader.LONGITUDE,GPSReader.ALTITUDE,MapUtil.emptyString);
    else mp = new MapPoint(reallat,reallon,0,"WP #"+String.valueOf(RMSOption.numberMapPoint));
    RMSOption.numberMapPoint++;
    activeRoute.addMapPoint(mp);
    wptForm = new Form(LangHolder.getString(Lang.editpoint));
    textName=new TextField(LangHolder.getString(Lang.label),mp.name,30,TextField.ANY);
    wptForm.append(textName);
    textDist=new TextField(LangHolder.getString(Lang.distance)+", "+LangHolder.getString(Lang.m)+"\n","100",4,TextField.ANY);
    //!NO NUMERIC
    textAzim=new TextField(LangHolder.getString(Lang.azimut)+", "+((char)0xb0)+"\n","90",3,TextField.ANY);
    Image[] ia = new Image[2];
    String[] sa= new String[2];
    sa[0]=LangHolder.getString(Lang.currpos);
    sa[1]=LangHolder.getString(Lang.useazimut);
    choiceAddType=new ChoiceGroup(LangHolder.getString(Lang.whatpos),ChoiceGroup.EXCLUSIVE,sa,ia);
    wptForm.append(choiceAddType);
    wptForm.append(textDist);
    wptForm.append(textAzim);
    
    wptForm.setCommandListener(this);
    saveCommand=new Command(LangHolder.getString(Lang.save), Command.ITEM, 1);
    wptForm.addCommand(saveCommand);
    wptForm.addCommand(backCommand);
    setCurrent(wptForm);
  }
  
  private void finishAddPoint() {
    try {
      activeRoute.lastPoint().name=textName.getString();
      if (choiceAddType.getSelectedIndex()>0) {
        activeRoute.lastPoint().move2Azimut(Double.parseDouble(textDist.getString()),Double.parseDouble(textAzim.getString()));
      }
      rmss.saveRoute(activeRoute);
    }finally{
      wptForm=null;
      saveCommand=null;
      textName=null;
      textDist=null;
      textAzim=null;
      choiceAddType=null;
      setCurrent(this);
    }
  }
  private void cancelAddPoint() {
    try {
      activeRoute.deleteMapPoint(activeRoute.lastPoint());
    }finally{
      wptForm=null;
      saveCommand=null;
      textName=null;
      textDist=null;
      textAzim=null;
      choiceAddType=null;
      setCurrent(this);
    }
  }
  public void trackBack() {
    if (activeTrack==null) return;
    activeRoute = activeTrack.createBackRoute();
  }
  
  public SMSWait smsW;
  public void endSMSWait() {
    if (smsW!=null) {
      smsW.stop();
      smsW=null;
    }
  }
  public void startSMSWait() {
    try{
      smsW = new SMSWait();
      smsW.start();
    } catch (Throwable e){
      AlertType.ERROR.playSound(display);
//#mdebug
//#       if (RMSOption.debugEnabled)
//#         DebugLog.add2Log("Start SMS Wait:"+e);
//#enddebug
    }
  }
  
  private long lastPainted;
  public void repaintMap() {
    if (lastPainted+400<System.currentTimeMillis()) {
      repaint();
      lastPainted=System.currentTimeMillis();
    }
  }
  
  public void copyMarks2WP(){
    MapRoute wp=MapRoute.createRoute(MapRoute.WAYPOINTSKIND);
    
    MapPoint mp;
    for (int i=0;i<marks.size();i++) {
      wp.addMark((MapMark)marks.elementAt(i));
    }
    
    wp.name=LangHolder.getString(Lang.marks);
    rmss.saveRoute(wp);
  }

 
  private static final byte DRAW_MINUS=1;
  private static final byte DRAW_PLUS=2;
  private static final byte DRAW_MINM=3;
  private static final byte DRAW_SERV=4;
  private static final byte DRAW_SERVMAP=5;
  private static final byte DRAW_BIND=6;
  private static final byte DRAW_MENU=7;
  private static final byte DRAW_ROT=8;
  
  private void paintScaleBox(Graphics g, int x,int y,int fh, byte draw){
    int bw = fh-4;
    int lw = bw/4;
    int sw = bw/2;
    g.drawRect(x,y,bw,bw);
    if ((draw==DRAW_MINUS)||(draw==DRAW_PLUS))
      g.fillRect(x,y+sw-lw/2,bw,lw);
    if (draw==DRAW_PLUS)
      g.fillRect(x+sw-lw/2,y,lw,bw);
    if (draw==DRAW_SERV)
      g.drawString(MapUtil.SH_SOUTH,x+sw,y,Graphics.TOP|Graphics.HCENTER);
    if (draw==DRAW_SERVMAP)
      g.drawString(MapUtil.SH_MAP,x+sw,y,Graphics.TOP|Graphics.HCENTER);
    if (draw==DRAW_MINM)
      if (RMSOption.fullScreen) {g.fillRect(x+lw,y+lw+lw,sw,sw);
      g.fillRect(x+lw+lw,y+lw,sw,sw);} else {g.fillRect(x+lw,y+lw,bw-sw,lw);
      g.drawRect(x+lw,y+lw,bw-sw,sw);
      }
    if (draw==DRAW_BIND)
      if (gpsBinded){
      g.drawLine(x,y,x+bw,y+bw);
      g.drawLine(x+bw,y,x,y+bw);
      } else {
      g.drawLine(x,y+sw,x+bw,y+sw);
      g.drawLine(x+sw,y,x+sw,y+bw);
      }
    if (draw==DRAW_MENU)
      g.drawString(MapUtil.SH_MENU,x+sw,y,Graphics.TOP|Graphics.HCENTER);
    if (draw==DRAW_ROT)
      g.drawString(MapUtil.SH_90,x+sw,y,Graphics.TOP|Graphics.HCENTER);
  }
  
  //public static MNFeedback mapFeedback;
  public void onFeedback(Object feedObject) {
    RMSOption.urlGPS = (String) feedObject;
    MapCanvas.map.startGPS();
  }
  private boolean hideTrack;
  private CanvasMenu canvasMenu;
  
  private void showHideMenu(){
    
    if (canvasMenu!=null){
      canvasMenu.clean();
      canvasMenu=null;
      return;
    }
    String s1,s2,s3;
    canvasMenu = new CanvasMenu(this);
    
    CanvasMenuItem cmi = new CanvasMenuItem("1 - "+LangHolder.getString(Lang.view),MENU_VIEW,KEY_NUM1);
    s1=(RMSOption.getByteOpt(RMSOption.BO_SCALETYPE)!=0)?LangHolder.getString(Lang.scaleyes):LangHolder.getString(Lang.scaleno);
    cmi.addItems(new CanvasMenuItem(s1,MENU_SCALE,KEY_NUM1));
    s1=(splitMode==0)?LangHolder.getString(Lang.splitview2)+"(1)":(splitMode==1)?LangHolder.getString(Lang.splitview2)+"(2)":(splitMode==2)?LangHolder.getString(Lang.splitview2)+"(3)":LangHolder.getString(Lang.splitview2)+"(4)";
    cmi.addItems(new CanvasMenuItem(s1,MENU_SPLIT,KEY_NUM2));
    cmi.addItems(new CanvasMenuItem("3 - "+LangHolder.getString(Lang.inversemap),MENU_INVERSEMAP,KEY_NUM3));
    cmi.addItems(new CanvasMenuItem("4 - 2x",MENU_DOUBLESIZE,KEY_NUM4));
    if ((activeRoute!=null)&&(activeRoute.kind==MapRoute.ROUTEKIND)){
    s1=(!RMSOption.getBoolOpt(RMSOption.BL_HIDE_ROUTE_DIRECTION))?"5 - "+LangHolder.getString(Lang.directions)+" ¤":"5 - "+LangHolder.getString(Lang.directions);
        cmi.addItems(new CanvasMenuItem(s1,MENU_DIRECTIONS,KEY_NUM5));
    }
    if ((activeTrack!=null)){
    s1=(hideTrack)?"6 - "+LangHolder.getString(Lang.showtrack):"6 - "+LangHolder.getString(Lang.hide)+' '+LangHolder.getString(Lang.track).toLowerCase();
        cmi.addItems(new CanvasMenuItem(s1,MENU_HIDETRACK,KEY_NUM6));
    }

    s2=(RMSOption.onlineMap)?LangHolder.getString(Lang.online):LangHolder.getString(Lang.offline);
    canvasMenu.addMenuItems(cmi)
    .addMenuItems(new CanvasMenuItem(s2,MENU_ONLINE,KEY_NUM2));
    if (activeRoute!=null)
      if (activeRoute.pt2Nav()!=null) {
      cmi = new CanvasMenuItem(LangHolder.getString(Lang.routem),MENU_NAVIGATION,KEY_NUM3);
      cmi.addItems(new CanvasMenuItem(LangHolder.getString(Lang.next1),MENU_NAV_NEXT,KEY_NUM1))
      .addItems(new CanvasMenuItem(LangHolder.getString(Lang.prev2),MENU_NAV_PREV,KEY_NUM2))
      .addItems(new CanvasMenuItem(LangHolder.getString(Lang.ptrt),MENU_NAV_POINT,KEY_NUM3))
      .addItems(new CanvasMenuItem(LangHolder.getString(Lang.withnonames),MENU_NAV_LABELS,KEY_NUM4))
      .addItems(new CanvasMenuItem("5 - "+LangHolder.getString(Lang.goto_),MENU_GOTOWPT,KEY_NUM5));
      if (activeRoute.geoInfo>0) {
        if (activeRoute.geoInfo!=MapRoute.GEOINFO_GEOCACHECOM)
          cmi.addItems(new CanvasMenuItem("6 - "+LangHolder.getString(Lang.info),MENU_NAV_GEO,KEY_NUM6));
        if (activeRoute.geoInfo==MapRoute.GEOINFO_GEOCACHE || activeRoute.geoInfo==MapRoute.GEOINFO_GEOCACHECOM)
          cmi.addItems(new CanvasMenuItem("7 - "+LangHolder.getString(Lang.internet),MENU_NAV_PAGE,KEY_NUM7));
        if (activeRoute.rId<1)
          cmi.addItems(new CanvasMenuItem("8 - "+LangHolder.getString(Lang.save),MENU_SAVEGEOROUTE,KEY_NUM8));
      }
      canvasMenu.addMenuItems(cmi);
      }
    
    if (NetRadar.netRadar!=null) {
      canvasMenu.addMenuItems(new CanvasMenuItem(LangHolder.getString(Lang.usersm),MENU_NETRADAR,KEY_NUM4));
    }
    if (gpsReader!=null) {
      s1=RMSOption.mapRotate?LangHolder.getString(Lang.rotating):LangHolder.getString(Lang.norotating);
      s2=RMSOption.compassOverMap?LangHolder.getString(Lang.compassmap):LangHolder.getString(Lang.nocompassmap);
      canvasMenu.addMenuItems(new CanvasMenuItem(s1,MENU_ROTATE,KEY_NUM5)).addMenuItems(new CanvasMenuItem(s2,MENU_ROTCOMPASS,KEY_NUM6));
    }
    if ((activeTrack!=null)||(gpsReader!=null)) {
      cmi = new CanvasMenuItem(LangHolder.getString(Lang.gpsqm),MENU_GPS,KEY_NUM7);
      
      s1=GPSReader.WRITETRACK?LangHolder.getString(Lang.gpsqmtrrec):LangHolder.getString(Lang.gpsqmtrstop);
      s2=GPSReader.RAWDATA?LangHolder.getString(Lang.gpsqmrawdata):LangHolder.getString(Lang.gpsqmrelaxed);
      
      cmi.addItems(new CanvasMenuItem( s1,MENU_GPS_TRACK,KEY_NUM1));
      if (gpsReader!=null) cmi.addItems(new CanvasMenuItem(s2,MENU_GPS_RAW,KEY_NUM2));
      canvasMenu.addMenuItems(cmi);
    }
    
  }
  private final static byte MENU_SCALE = 1;
  private final static byte MENU_ONLINE = 2;
  private final static byte MENU_NAVIGATION= 3;
  private final static byte MENU_NAV_NEXT= 4;
  private final static byte MENU_NAV_PREV= 5;
  private final static byte MENU_NAV_POINT= 6;
  private final static byte MENU_NAV_LABELS= 7;
  private final static byte MENU_NAV_GEO= 8;
  private final static byte MENU_GPS= 9;
  private final static byte MENU_GPS_TRACK= 10;
  private final static byte MENU_GPS_RAW= 11;
  private final static byte MENU_ROTATE= 12;
  private final static byte MENU_ROTCOMPASS= 13;
  private final static byte MENU_NETRADAR= 14;
  private final static byte MENU_VIEW= 15;
  private final static byte MENU_SPLIT= 16;
  private final static byte MENU_SAVEGEOROUTE= 17;
  private final static byte MENU_GOTOWPT= 18;
  private final static byte MENU_NAV_PAGE= 19;
  private final static byte MENU_INVERSEMAP= 20;
  private final static byte MENU_DOUBLESIZE= 21;
  private final static byte MENU_DIRECTIONS= 22;
  private final static byte MENU_HIDETRACK= 23;
  private void setNextScale(){
    byte st = RMSOption.getByteOpt(RMSOption.BO_SCALETYPE);
    if (st<2)st++;
    else st=0;
    RMSOption.setByteOpt(RMSOption.BO_SCALETYPE,st);
    
  } 
  public boolean commandCanvasAction(CanvasMenuItem menuItem,byte commandId) {
    switch (commandId) {
      case MENU_SCALE:
        setNextScale();
        menuItem.caption=(RMSOption.getByteOpt(RMSOption.BO_SCALETYPE)!=0)?LangHolder.getString(Lang.scaleyes):LangHolder.getString(Lang.scaleno);
        break;
      case MENU_ONLINE:
        RMSOption.onlineMap=!RMSOption.onlineMap;
        menuItem.caption=(RMSOption.onlineMap)?LangHolder.getString(Lang.online):LangHolder.getString(Lang.offline);
        break;
        
      case MENU_NAV_NEXT:
        activeRoute.nextNavPoint();
        break;
      case MENU_NAV_PREV:
        activeRoute.prevNavPoint();
        break;
      case MENU_NAV_POINT:
        activeRoute.swapShowPoint();
        break;
      case MENU_NAV_LABELS:
        activeRoute.swapShowLabels();
        break;
      case MENU_NAV_GEO:
        //if (activeRoute.geoInfo>0)
        activeRoute.loadGeoCacheInfo();
        break;
      case MENU_NAV_PAGE:
        //if (activeRoute.geoInfo==0)
        activeRoute.loadGeoCachePage();
        break;
      case MENU_SAVEGEOROUTE:
        if (activeRoute.rId<1)
          rmss.saveRoute(activeRoute);
        showHideMenu();
        break;
        
      case MENU_GPS_TRACK:
        GPSReader.WRITETRACK=!GPSReader.WRITETRACK;
        menuItem.caption=GPSReader.WRITETRACK?LangHolder.getString(Lang.gpsqmtrrec):LangHolder.getString(Lang.gpsqmtrstop);
        break;
      case MENU_GPS_RAW:
        GPSReader.RAWDATA=!GPSReader.RAWDATA;
        menuItem.caption=GPSReader.RAWDATA?LangHolder.getString(Lang.gpsqmrawdata):LangHolder.getString(Lang.gpsqmrelaxed);
        break;
        
      case MENU_ROTATE:
        RMSOption.mapRotate=!RMSOption.mapRotate;
        menuItem.caption=RMSOption.mapRotate?LangHolder.getString(Lang.rotating):LangHolder.getString(Lang.norotating);
        break;
      case MENU_ROTCOMPASS:
        RMSOption.compassOverMap=!RMSOption.compassOverMap;
        menuItem.caption=RMSOption.compassOverMap?LangHolder.getString(Lang.compassmap):LangHolder.getString(Lang.nocompassmap);
        break;
      case MENU_NETRADAR:
        try{NetRadar.netRadar.showUserList();}catch(Throwable t) {}
        showHideMenu();
        break;
      case MENU_SPLIT:
        splitMode=(splitMode<3)?(byte)(splitMode+1):0;
        menuItem.caption=(splitMode==0)?LangHolder.getString(Lang.splitview2)+"(1)":(splitMode==1)?LangHolder.getString(Lang.splitview2)+"(2)":(splitMode==2)?LangHolder.getString(Lang.splitview2)+"(3)":LangHolder.getString(Lang.splitview2)+"(4)";
        //menuItem.caption=(splitMode==0)?LangHolder.getString(Lang.splitviewone):(splitMode==1)?LangHolder.getString(Lang.splitviewtwo):(splitMode==2)?LangHolder.getString(Lang.splitviewthree):LangHolder.getString(Lang.splitviewfour);
        ///    menuItem.caption=(splitMode==0)?LangHolder.getString(Lang.splitviewone):(splitMode==1)?LangHolder.getString(Lang.splitviewtwo):LangHolder.getString(Lang.splitviewfour);
        break;
      case MENU_GOTOWPT:
        setLocation(activeRoute.pt2Nav().lat,activeRoute.pt2Nav().lon);
        break;
      case MENU_INVERSEMAP:
        inverseMap=!inverseMap;
        break;
      case MENU_DOUBLESIZE:
        doubleZoomMap=!doubleZoomMap;
        break;
      case MENU_DIRECTIONS:
        RMSOption.setBoolOpt(RMSOption.BL_HIDE_ROUTE_DIRECTION, !RMSOption.getBoolOpt(RMSOption.BL_HIDE_ROUTE_DIRECTION));
     String  s1=(!RMSOption.getBoolOpt(RMSOption.BL_HIDE_ROUTE_DIRECTION))?"5 - "+LangHolder.getString(Lang.directions)+" ¤":"5 - "+LangHolder.getString(Lang.directions);
    menuItem.caption=s1;
        break;
      case MENU_HIDETRACK:
        hideTrack=!hideTrack;
        menuItem.caption=(hideTrack)?"6 - "+LangHolder.getString(Lang.showtrack):"6 - "+LangHolder.getString(Lang.hide)+' '+LangHolder.getString(Lang.track).toLowerCase();
        break;
    }
    RMSOption.changed=true;
    return true;
  }
  
  public static byte moveX,moveY;
  private ScrollMapThread scrollMapThread;
  
  public static void setCurrentMap(){
    setCurrent(map);
  }
  public static void setCurrent(Displayable nextDisplayable){
    display.setCurrent(nextDisplayable);
  }
  public static void setCurrent(Alert alert, Displayable nextDisplayable){
    display.setCurrent(alert,nextDisplayable);
  }
  public void showSaveMarkForm(boolean nav2){
    setCurrent(new MarkList(marks,nav2));
  }
  public static void showmsgmodal(String caption,String text,AlertType at,Displayable nextDisp){
    Alert a = new Alert(caption,text,null,at);
    a.setTimeout(Alert.FOREVER);
    display.setCurrent(a, nextDisp);
  }
  public static void showmsg(String caption,String text,AlertType at,Displayable nextDisp){
    Alert a = new Alert(caption,text,null,at);
    a.setTimeout(3200);
    display.setCurrent(a, nextDisp);
  }
  
  private void mapKeyFunction(byte key){
    // gpsReader.COURSE-=5;
    byte action = RMSOption.keyFunction[key];
    switch (action){
      case RMSOption.KF_LANDSCAPE:
        landscapeMap=!landscapeMap;
        updateSize=true;
        break;
      case RMSOption.KF_LIGHT:
        changeLight();
        break;
      case RMSOption.KF_SENDSMS:
        MapForms.mm.showSMSSendForm();
        break;
      case RMSOption.KF_SCALEPLUS:
        zoom(1);
        break;
      case RMSOption.KF_SCALEMINUS:
        zoom(-1);
        break;
      case RMSOption.KF_ADDWPT:
        addMapPoint2Route(reallat, reallon);
        break;
      case RMSOption.KF_SETTINGS:
        MapForms.mm.showSettingForm();
        break;
      case RMSOption.KF_SCREENSHOT:
        MapForms.mm.sendScreenMMS();
        break;
      case RMSOption.KF_FLICKR :
        MapForms.mm.showFlickr();
        break;
      case RMSOption.KF_CHANGEMAPSERVER:
        changeMapSer();
        break;
      case RMSOption.KF_CHANGEMAPTYPE:
        changeMapView();
        break;
      case RMSOption.KF_PRECISELOCATION :
        setCurrent(new LocationCanvas());
        break;
      case RMSOption.KF_NEXTWPT:
        if (activeRoute!=null)
          activeRoute.nextNavPoint();
        break;
      case RMSOption.KF_NEARESTWPT:
        if (activeRoute!=null)
          activeRoute.nearestNavPoint();
        break;
      case RMSOption.KF_SOUNDONOFF:
        RMSOption.soundOn=!RMSOption.soundOn;
        break;
      case RMSOption.KF_TRACKWRITE :
        GPSReader.WRITETRACK=!GPSReader.WRITETRACK;
        blink();
        break;
      case RMSOption.KF_GEOCACHING :
        //if (secretGeoCombination())
        MapForms.mm.getGeoCache();
        break;
      case RMSOption.KF_MYPOI :
        GPSClubLoad.getGPSClub();
        break;
      case RMSOption.KF_SENDNETRADARPOS :
        NetRadar.oneTimeCall=true;
        if (NetRadar.netRadar==null)
          startNetRadar(true);
        break;
      case RMSOption.KF_TRACKCROSS :
        if (gpsReader!=null)
          gpsReader.addTrackCross();
        break;
      case RMSOption.KF_SCALEPLUS3:
        zoom(3);
        break;
      case RMSOption.KF_SCALEMINUS3:
        zoom(-3);
        break;
      case RMSOption.KF_CHANGEMAP:
        changeMap();
        break;
      case RMSOption.KF_NETRADAR_WT:
        sayNetRadar_WT();
        break;
      case RMSOption.KF_INVERSEMAP:
        inverseMap=!inverseMap;
        break;
      case RMSOption.KF_VIEWPORT:
        splitMode=(splitMode<3)?(byte)(splitMode+1):0;
        break;
      case RMSOption.KF_SCALE:
        setNextScale();
        break;
      case RMSOption.KF_DOUBLESIZE:
        doubleZoomMap= !doubleZoomMap;
        break;
        case RMSOption.KF_REFRESH:
        reloadTiles();
        break;
        case RMSOption.KF_NEXTSCREEN:
        nextMode();
        break;
    }
  }
  private void sayNetRadar_WT(){
    if ((NetRadar.netRadar!=null)&&(NetRadar.netRadar.nrWT!=null)){
      (new Thread(NetRadar.netRadar.nrWT)).start();
    }
  }
  public static Timer timer = new Timer();
  public static void setLight() {
    try {
      LightTimer.scheduleMe();
    }catch (Throwable t){}
  }
  
  public static void setKeyLockTimer() {
    try {
      //if (timer==null) timer= new Timer();
      timer.schedule( new KeyLockTimer(), 0,1500);
    }catch (Throwable t){
//#mdebug
//#       if (RMSOption.debugEnabled)
//#         DebugLog.add2Log("st kT:"+t);
//#enddebug
    }
  }
   
  private static String cutStringToWidth(String stringToCut, int width, Graphics g){
    if (stringToCut.equals(MapUtil.emptyString))return MapUtil.emptyString;
    
    int sr = g.getFont().stringWidth(stringToCut);
    sr = (width*stringToCut.length())/sr;
    if (sr<=stringToCut.length()) {
      sr=31*sr/32;
      stringToCut=stringToCut.substring(0,sr);
    }
    return stringToCut;
  }
}
