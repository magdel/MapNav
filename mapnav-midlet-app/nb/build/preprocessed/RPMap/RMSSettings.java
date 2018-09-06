/*
 * RMSSettings.java
 *
 * Created on 3 ������� 2006 �., 18:15
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package RPMap;

import app.MapForms;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotOpenException;
import kml.KMLMapRoute;
import lang.Lang;
import lang.LangHolder;
//#debug
import misc.DebugLog;
import misc.MVector;
import misc.ProgressForm;
import misc.ProgressResponse;
import misc.ProgressStoppable;
import misc.Util;

/**
 *
 * @author RFK
 */
public class RMSSettings implements Runnable, ProgressStoppable {
//        implements RecordFilter, RecordComparator {
    /*
   * The RecordStore used for storing the keys
   */
  private static String RSETTNAME="mapset10a";
  private static String RCMNAME="cminfo01";
  public static String RPICIMGNAME="picimg10";
  public static String RPICTILENAME="pictil10";
  private static String RMLNAME="maplab2";
  public static int picSaved;

  public RMSSettings() {
    try {
      RecordStore recordSetStore=RecordStore.openRecordStore(RSETTNAME, true);
      try {
        readSetting();
      } finally {
        recordSetStore.closeRecordStore();
      }
    } catch (RecordStoreException rse) {
    }
    MapForms.mM.ulp(24);
    try {
      recordPicImgStore=RecordStore.openRecordStore(RPICIMGNAME, true);
      if (mapInd>0){
        recordMyPicImgStore=RecordStore.openRecordStore(RMSOption.IMAPS_RMST[mapInd-1], true);
      }
      MapForms.mM.ulp(26);
      readPicsList();
      MapForms.mM.ulp(28);
    } catch (Throwable rse) {
//#mdebug
      DebugLog.add2Log("OP RMS:"+rse);
//#enddebug
    }
  }
  public Hashtable rmsTiles=new Hashtable(500);
  private MVector rmsRoutes=new MVector();
  public static RecordStore recordPicImgStore;
  public static RecordStore recordMyPicImgStore;

  public static void closeRecordStores() {
    try {
      recordPicImgStore.closeRecordStore();
    } catch (Throwable t) {
    }
    try {
      recordMyPicImgStore.closeRecordStore();
    } catch (Throwable t) {
    }
    try {
      recordPicImgStore.closeRecordStore();
    } catch (Throwable t) {
    }
    try {
      recordMyPicImgStore.closeRecordStore();
    } catch (Throwable t) {
    }
  }
  public int mapInd;

  public void switchIntMap(int mapIndex) {
    try {
      recordMyPicImgStore.closeRecordStore();
    } catch (Throwable t) {
    }
    try {
      recordMyPicImgStore.closeRecordStore();
    } catch (Throwable t) {
    }
    try {
      recordMyPicImgStore.closeRecordStore();
    } catch (Throwable t) {
    }
    recordMyPicImgStore=null;

    mapInd=mapIndex;
    if (mapIndex==0){
      return;
//--cleaning list of tiles-----
    }
    RMSTile rt;
    Hashtable rmsT=new Hashtable(1000);
    for (Enumeration e=rmsTiles.elements(); e.hasMoreElements();) {
      rt=(RMSTile) e.nextElement();
      if (!rt.isMyMap()){
        rmsT.put(rt, rt);
      }
    }
    rmsTiles=rmsT;
    

    try {
      recordMyPicImgStore=RecordStore.openRecordStore(RMSOption.IMAPS_RMSP[mapInd-1], true);
    } catch (Throwable ex) {
    }

    readRMSList(true);
    MapCanvas.map.clearAllTiles();


  }


   
  //private String filelookup = "File lookup";
  public boolean readRMSPic(MapTile mt) {
    if ((mt.tileServerType!=MapTile.SHOW_MP)&&(RMSOption.getBoolOpt(RMSOption.BL_EXTCACHEUSE))){
    //  mt.paintState(filelookup);
      if (OuterMapLoader.readRMSPic(mt)){
        mt.paintState(null);
        return true;
      }
    }
    RMSTile rt=getFromList(mt);
    if (rt==null){
      if ( //(MapCanvas.map.externalMapIndex==0)||
        (MapCanvas.map.oml==null)||((mt.tileType&MapTile.SHOW_MASKSERVER)!=MapTile.SHOW_MP)){
        mt.paintState(null);
        return false;
      } else {
        return MapCanvas.map.oml.loadTile(mt);
      }
    }
    rt.updateTS();
    /*
    try {
    int nrs=rt.recordId;
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream outputStream = new DataOutputStream(baos);
    try {
    writeRMSTile(rt, outputStream);
    } catch (IOException ioe) {}
    
    byte[] b = baos.toByteArray();
    if (rt.isMyMap())
    recordMyPicListStore.setRecord(nrs,b, 0, b.length);
    else recordPicListStore.setRecord(nrs,b, 0, b.length);
    } catch (RecordStoreException rse) {}
     */

    try {
      mt.img=null;
      byte[] bi;
      if (rt.isMyMap()){
        bi=recordMyPicImgStore.getRecord(rt.recordId);
      } else {
        bi=recordPicImgStore.getRecord(rt.recordId);
      }
      mt.img=Image.createImage(bi, 0, bi.length);
      //}
mt.paintState(null);
      return true;
    } catch (RecordStoreException e) {
//#mdebug
      if (RMSOption.debugEnabled){
        DebugLog.add2Log("Read RMSPic:"+e+"\n"+rt);
     }
//#enddebug
      
    }
    mt.paintState(null);
    return false;
  }

  private RMSTile getOldestRMSTile() {
    RMSTile rt=null;
    RMSTile oldrt=null;

    //Hashtable rmsT = new Hashtable(1000);
    int maxcheck=100;
    for (Enumeration e=rmsTiles.elements(); e.hasMoreElements();) {
      rt=(RMSTile) e.nextElement();
      if (rt.isMyMap()){
        continue;
      }
      maxcheck--;
      if (maxcheck==0){
        break;
      }
      if (oldrt==null){
        oldrt=rt;
        continue;
      }
      if (oldrt.ts>rt.ts){
        oldrt=rt;
      }
    }

//    for (int i=rmsTiles.size()-1;i>=0;i--) {
//      rt = (RMSTile) rmsTiles.elementAt(i);
//      if (rt.isMyMap()) continue;
//      if (oldrt==null) {oldrt=rt;continue;};
//      if (oldrt.ts>rt.ts) oldrt=rt;
//    }
    return oldrt;
  }
  //������ ���������� ����� �������!
  private RMSTile getFromList(MapTile mt) {
    //RMSTile rt=null;

//    for (int i=rmsTiles.size()-1;i>=0;i--) {
//      rt = (RMSTile) rmsTiles.elementAt(i);
//      if ((rt.level==mt.level)&&(rt.numX==mt.numX)&&(rt.numY==mt.numY)&&(rt.mapType==mt.tileType))
//        return rt;
//    }
    gflRMSTile.level=mt.level;
    gflRMSTile.tileType=mt.tileType;
    gflRMSTile.numX=mt.numX;
    gflRMSTile.numY=mt.numY;
    return (RMSTile) rmsTiles.get(gflRMSTile);
  }
  private RMSTile gflRMSTile=new RMSTile(0, 0, 0, 0, (byte) 0);

  public void deleteAllTiles(byte tileType, Displayable backDisp) {
    tileTypeToDelete=tileType;
    this.backDisp=backDisp;
    MapCanvas.setCurrent(new ProgressForm("Clearing map", MapCanvas.map.serViewLabel, this, backDisp));
    (new Thread(this)).start();
  }
  private byte tileTypeToDelete;
  private Displayable backDisp;

  public void run() {
    stopped=false;
    try {
      MapCanvas.map.clearAllTiles();
      RMSTile rt;
      Hashtable rmsT=new Hashtable(500);
      int elemCount=rmsTiles.size();
      int i=0;
      for (Enumeration e=rmsTiles.elements(); e.hasMoreElements();) {
        if (stopped){
          break;
        }
        rt=(RMSTile) e.nextElement();
        if (rt.tileType!=tileTypeToDelete){
          rmsT.put(rt, rt);
        } else {
          try {
            if (rt.isMyMap()){
              recordMyPicImgStore.deleteRecord(rt.recordId);
            } else {
              recordPicImgStore.deleteRecord(rt.recordId);
            }
          } catch (Throwable t) {
          }
        }
        progressResponse.setProgress((byte) (100*i/elemCount), LangHolder.getString(Lang.deleted));
      }
      if (!stopped){
        rmsTiles=rmsT;
      }
    } finally {
      progressResponse=null;
      MapCanvas.setCurrent(backDisp);
    }
  }
  ProgressResponse progressResponse;

  public void setProgressResponse(ProgressResponse progressResponse) {
    this.progressResponse=progressResponse;
  }
  private boolean stopped;

  public boolean stopIt() {
    stopped=true;
            return true;

  }

  public void deletePic(MapTile mt) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
    if (RMSOption.getBoolOpt(RMSOption.BL_EXTCACHEUSE)){
      OuterMapLoader.deletePic(mt);
      return;
    }
    RMSTile rt=getFromList(mt);

    if (rt.isMyMap()){
      recordMyPicImgStore.deleteRecord(rt.recordId);
    } else {
      recordPicImgStore.deleteRecord(rt.recordId);
    }
    rt.recordId=-1;
    rmsTiles.remove(rt);
  }

  public void savePic(MapTile mt, byte[] bi) {
    savePic(mt, bi, bi.length);
  }

  public synchronized void savePic(MapTile mt, byte[] bi, int bi_length) {
    if ((mt.tileServerType!=MapTile.SHOW_MP)&&(RMSOption.getBoolOpt(RMSOption.BL_EXTCACHEUSE))){
      OuterMapLoader.savePic(mt, bi, bi_length);
      return;
    }
    RMSTile rt=getFromList(mt);
    //if (rt!=null) return;
    boolean old=false;
    byte tileType=(byte) (mt.tileImageType+mt.tileServerType);
    try {
      int nrs=0;
      int sa=getSizeAvailable();
//      if (sa>200000) {
//      } else 
      if (sa<50000){
        return;
      }

//        else {
//        if ((tileType&MapTile.SHOW_MP)==MapTile.SHOW_MP) return;
//        rt=getOldestRMSTile();
//        if (rt==null) return;
//        nrs=rt.recordId;
//        old=true;
//      };

      if (rt==null){
        rt=new RMSTile(mt.level, mt.numX, mt.numY, 0, tileType);
      }
      rt.level=mt.level;
      rt.numX=mt.numX;
      rt.numY=mt.numY;
      rt.tileType=tileType;
      // rt.recordId =0;

      rt.updateTS();

      int nr;

      if (rt.isMyMap()){
        rt.recordId=recordMyPicImgStore.addRecord(bi, 0, bi_length);
      } else {
        if (old){
          recordPicImgStore.setRecord(nrs, bi, 0, bi_length);
        } else {
          rt.recordId=recordPicImgStore.addRecord(bi, 0, bi_length);
        }
        picSaved++;
      }

      if ((tileType&MapTile.SHOW_MP)==MapTile.SHOW_MP){
        rmsTiles.put(rt, rt);
      //     rmsTiles.addElement(rt);
      } else if (!rmsTiles.contains(rt)){
        rmsTiles.put(rt, rt);
      //     rmsTiles.addElement(rt);
      }
    } catch (RecordStoreFullException rsf) {
      //#mdebug
      if (RMSOption.debugEnabled)
        DebugLog.add2Log("SP RSFE:"+rsf);
//#enddebug
      
    } catch (RecordStoreException rse) {
      //#mdebug
      if (RMSOption.debugEnabled){
        DebugLog.add2Log("SP RSE:"+rse);
       }      
//#enddebug
      
    }
    if (picSaved>10){
      picSaved=0;
      writeRMSList(false);
    }
  }

  private void readPicsList() {
    try {
      readRMSList(false);
      readRMSList(true);

    } catch (Throwable e) {
//#mdebug
      if (RMSOption.debugEnabled){
        DebugLog.add2Log("Read pic list:"+e);
  }
//#enddebug
      
    }
  }

  public void clearInetPicCache() {
    try {
      RMSTile rt;

      Hashtable rmsT=new Hashtable(1000);
      for (Enumeration e=rmsTiles.elements(); e.hasMoreElements();) {
        rt=(RMSTile) e.nextElement();
        if (rt.isMyMap()){
          rmsT.put(rt, rt);
        }
      }
      rmsTiles=rmsT;

      try {
        recordPicImgStore.closeRecordStore();
      } catch (Throwable t) {
      }
      //recordPicListStore.closeRecordStore();

      RecordStore.deleteRecordStore(RPICTILENAME);
      RecordStore.deleteRecordStore(RPICIMGNAME);

      recordPicImgStore=RecordStore.openRecordStore(RPICIMGNAME, true);
      writeRMSList(false);
    //recordPicListStore = RecordStore.openRecordStore(RPICLISTNAME, true);
    } catch (Exception e) {
//#mdebug
      if (RMSOption.debugEnabled)
        DebugLog.add2Log("Clear inet pic list:"+e);
//#enddebug
      
    }
  }

  public int getSizeAvailable() {
    try {
      return recordPicImgStore.getSizeAvailable();
    } catch (RecordStoreNotOpenException ioe) {
      return 0;
    }
  }

  public int getInetCacheSizeBytes() {
    try {
      return recordPicImgStore.getSize();
    } catch (RecordStoreNotOpenException ioe) {
      return 0;
    }
  }

  public int getMyCacheSizeBytes() {
    try {
      return recordMyPicImgStore.getSize();
    } catch (Throwable ioe) {
      return 0;
    }
  }
//  public int getSizeBytes(byte bytesKind){
//    //���� �������� ��� ������� � ����������
//    return 0;
//  }
//  private void saveDefautSetting(int nr) {
//    
//  }
  public void resetSettings() {
    try {
      RecordStore.deleteRecordStore(RSETTNAME);
    } catch (RecordStoreException ioe) {
    }
  }

  public void saveMapMark(MapMark mm) {
    ByteArrayOutputStream baos=new ByteArrayOutputStream();
    DataOutputStream outputStream=new DataOutputStream(baos);
    try {
      writeMapMark(outputStream, mm);
    } catch (IOException ioe) {
    }
    byte[] b=baos.toByteArray();
    try {
      RecordStore recordMLStore=RecordStore.openRecordStore(RMLNAME, true);
      try {
//int nr=recordMLStore.getNumRecords();
        if (mm.rId==0){
          mm.rId=recordMLStore.addRecord(b, 0, b.length);
        } else {
          recordMLStore.setRecord(mm.rId, b, 0, b.length);
        }
      } finally {
        recordMLStore.closeRecordStore();
      }
    } catch (RecordStoreException rse) {
    }
  }

  public void deleteMapMark(MapMark mm) {
    try {
      RecordStore recordMLStore=RecordStore.openRecordStore(RMLNAME, true);
      try {
        recordMLStore.deleteRecord(mm.rId);
      } finally {
        recordMLStore.closeRecordStore();
      }
    } catch (RecordStoreException rse) {
    }
  }

  public void deleteMapMarks() {
    try {
      RecordStore.deleteRecordStore(RMLNAME);
    } catch (RecordStoreException rse) {
    }
  }

  private void writeMapMark(DataOutputStream out, MapMark mm) throws IOException {
    out.writeInt(1);
    out.writeDouble(mm.lat);
    out.writeDouble(mm.lon);
    out.writeInt(mm.level);
    out.writeUTF(mm.name);
    out.writeBoolean(mm.hasSound);
  }

  private MapMark readMapMark(DataInputStream in, int i) throws IOException {
    int ver = in.readInt();
    double lat=in.readDouble();
    double lon=in.readDouble();
    int level=in.readInt();
    String nm=in.readUTF();
    boolean hS = in.readBoolean();
    return new MapMark(lat, lon, level, nm, i,hS);
  }

  public void loadMapMark(MVector marks) {

    marks.removeAllElements();
    try {
      RecordStore recordMLStore=RecordStore.openRecordStore(RMLNAME, true);
      try {
        if (!RMSOption.getBoolOpt(RMSOption.BL_MARKS_ADDED)){
          if (recordMLStore.getNumRecords()==0){
            saveMapMark(new MapMark(53.52555555556, 108.1575, 6, "Lake Baikal", 0));//53 31 32     108 9 27
            saveMapMark(new MapMark(58.58333333333, 49.63333333333, 11, "Kirov", 0));  //58 35    49 38
            saveMapMark(new MapMark(59.84527777778, 29.04166666667, 14, "Leningradskaya Nuclear Power Station", 0));  //59 50 43      29 02 30
            saveMapMark(new MapMark(61.388055555, 30.94944444444, 12, "Island Valaam", 0));//61 23 17   30 56 58
            saveMapMark(new MapMark(43.35305555556, 42.43694444444, 11, "Mount Elbrus", 0)); //[Image]43�21?11? �. �. 42�26?13? �. �. (G)
            saveMapMark(new MapMark(59.93888888889, 30.31555555556, 16, "Saint-Petersburg Palace Square", 0));//59 56 20    30 18 56
//          saveMapMark(new MapMark(60,30,10,"Samara",0));
            RMSOption.setBoolOpt(RMSOption.BL_MARKS_ADDED, true);
          }
        }
        DataInputStream in;
        MapMark mm;
        RecordEnumeration re=recordMLStore.enumerateRecords(null, null, false);
        while (re.hasNextElement()) {
          try {
            int i=re.nextRecordId();
            in=Util.getDataInputStream(recordMLStore.getRecord(i));
            try {
              mm=readMapMark(in, i);
              marks.addElement(mm);
              //in.close();
            } catch (IOException ioe) {
            }
          } catch (RecordStoreException e) {
          }
        }
        re.destroy();
      } finally {
        recordMLStore.closeRecordStore();
      }
    } catch (Throwable rse) {
    }
  }

  public synchronized boolean saveRoute(MapRoute route) {
    boolean res=false;
      if (route.kind==MapRoute.NRWAYPOINTSKIND){
      return res;
    }
    ByteArrayOutputStream baos=new ByteArrayOutputStream(8000);
    DataOutputStream outputStream=new DataOutputStream(baos);
    try {
      route.save2Stream(outputStream);
      byte[] b=baos.toByteArray();
      baos.reset();

      route.saveInfo2Stream(outputStream);
      byte[] nb=baos.toByteArray();

      outputStream=null;
      baos=null;

      RecordStore rS=RecordStore.openRecordStore(route.getRMSName(), true);
      try {
        RecordStore rSn=RecordStore.openRecordStore(route.getRMSName()+"N", true);
        try {

          if (route.rId==-1){
            route.rId=rS.addRecord(b, 0, b.length);
            rSn.addRecord(nb, 0, nb.length);
          } else {
            rS.setRecord(route.rId, b, 0, b.length);
            rSn.setRecord(route.rId, nb, 0, nb.length);
          }
          if (route.defTrack){
            RMSOption.defTRId=route.rId;
          }

        } finally {
          rSn.closeRecordStore();
        }
      } finally {
        rS.closeRecordStore();
      }
      res=true;
    } catch (Throwable rse) {
    }
    reloadRMSRoutes(route.kind);
    return res;
  }

  public int getRouteCount() {
    return rmsRoutes.size();
  }

  public int getRouteId(int i) {
    RMSRoute r=(RMSRoute) rmsRoutes.elementAt(i);
    return r.rId;

  }
  public RMSRoute getRMSRoute(int i){
    return (RMSRoute) rmsRoutes.elementAt(i);
  }
  public String getRouteName(int i) {
    RMSRoute r=(RMSRoute) rmsRoutes.elementAt(i);
    return String.valueOf(r.ptC)+": "+r.routeName;
  }
  public String getRouteClearName(int i) {
    RMSRoute r=(RMSRoute) rmsRoutes.elementAt(i);
    return r.routeName;
  }
//  private RecordStore loadRecords;
//  private RecordEnumeration recEnum; 
//  public MapRoute beginLoadRoutes(byte kind) {
//    MapRoute route=new MapRoute(kind);
//    try {
//      loadRecords=RecordStore.openRecordStore(MapRoute.getRMSName(kind), true);
//      recEnum = loadRecords.enumerateRecords(null, null, false);
//      if (recEnum.hasNextElement()){
//        DataInputStream in=Util.getDataInputStream(recEnum.nextRecord());
//        try {
//          if (kind==MapRoute.KMLDOCUMENT){
//            route=new KMLMapRoute(in);
//          } else {
//            route=new MapRoute(in);
//          }
//          //route.rId=recEnum.;
//          //in.close();
//        } catch (Exception ioe) {
//        }
//      } else {
//        route=null;
//        recEnum.destroy();
//        loadRecords.closeRecordStore();
//      }
//    } catch (RecordStoreException rse) {
//    }
//    return route;
//  }  
//  public MapRoute nextLoadRoutes(byte kind) {
//    MapRoute route=new MapRoute(kind);
//    try {
//      if (recEnum.hasNextElement()){
//        DataInputStream in=Util.getDataInputStream(recEnum.nextRecord());
//        try {
//          if (kind==MapRoute.KMLDOCUMENT){
//            route=new KMLMapRoute(in);
//          } else {
//            route=new MapRoute(in);
//          }
//          //route.rId=recEnum.;
//          //in.close();
//        } catch (Exception ioe) {
//        }
//      } else {
//        route=null;
//        recEnum.destroy();
//        loadRecords.closeRecordStore();
//      }
//    } catch (RecordStoreException rse) {
//    }
//    return route;
//  }  
//  
  
  public MapRoute loadRoute(int i, byte kind) {
    MapRoute route=null;
    try {
      RecordStore rS=RecordStore.openRecordStore(MapRoute.getRMSName(kind), true);
      try {
        RMSRoute r=(RMSRoute) rmsRoutes.elementAt(i);
        DataInputStream in=Util.getDataInputStream(rS.getRecord(r.rId));
        try {
          if (kind==MapRoute.KMLDOCUMENT){
            route=new KMLMapRoute(in);
          } else {
            route=new MapRoute(in);
          }
          route.rId=r.rId;
          //in.close();
        } catch (Exception ioe) {
        }
      } finally {
        rS.closeRecordStore();
      }
    } catch (RecordStoreException rse) {
    }
    return route;
  }

  public static MapRoute loadRouteById(int rId, byte kind) {
    MapRoute route=null;
    try {
      RecordStore rS=RecordStore.openRecordStore(MapRoute.getRMSName(kind), true);
      try {
        DataInputStream in;
        in=Util.getDataInputStream(rS.getRecord(rId));
        try {
          if (kind==MapRoute.KMLDOCUMENT){
            route=new KMLMapRoute(in);
          } else {
            route=new MapRoute(in);
          }
          route.rId=rId;
          in.close();
        } catch (Exception ioe) {
        }
      } finally {
        rS.closeRecordStore();
      }
    } catch (RecordStoreException rse) {
    }
    return route;
  }

  public static MapRoute loadDefRouteById(int rId) {
    MapRoute route=null;
    try {
      RecordStore rS=RecordStore.openRecordStore(MapRoute.RTRNAMEDEF, true);
      try {
        DataInputStream in;
        in=Util.getDataInputStream(rS.getRecord(rId));
        try {
          route=new MapRoute(in);
          route.rId=rId;
          route.defTrack=true;
          in.close();
        } catch (Exception ioe) {
        }
      } finally {
        rS.closeRecordStore();
      }
    } catch (RecordStoreException rse) {
    }
    return route;
  }

  public void deleteRoutes(byte kind) {
    try {
      RecordStore.deleteRecordStore(MapRoute.getRMSName(kind)+"N");
    } catch (RecordStoreException rse) {
    }
    try {
      RecordStore.deleteRecordStore(MapRoute.getRMSName(kind));
    } catch (RecordStoreException rse) {
    }
    reloadRMSRoutes(kind);
  }

  public void deleteRoute(int i, byte kind) {
    try {
      RecordStore rS=RecordStore.openRecordStore(MapRoute.getRMSName(kind), true);
      try {
        RecordStore rSn=RecordStore.openRecordStore(MapRoute.getRMSName(kind)+"N", true);
        try {
          RMSRoute r=(RMSRoute) rmsRoutes.elementAt(i);
          try {
            rS.deleteRecord(r.rId);
          } finally {
            rSn.deleteRecord(r.rId);
          }
        } finally {
          rSn.closeRecordStore();
        }
      } finally {
        rS.closeRecordStore();
      }
    } catch (RecordStoreException rse) {
    }
    reloadRMSRoutes(kind);
  }

  public synchronized void reloadRMSRoutes(byte kind) {
//#debug
    byte tP=0;
//#debug
    try {
      rmsRoutes.removeAllElements();
//#debug
      tP=1;
      try {
        RecordStore rS=RecordStore.openRecordStore(MapRoute.getRMSName(kind)+"N", true);
//#debug
        tP=2;
        try {
          DataInputStream in;
          RMSRoute r;
          RecordEnumeration re=rS.enumerateRecords(null, null, false);
          try{
//#debug
          tP=3;
          while (re.hasNextElement()) {
            try {
//#debug
              tP=4;
              int i=re.nextRecordId();
//#debug
              tP=5;
              byte[] b=rS.getRecord(i);
//#debug
              tP=6;
              if (b==null){
                continue;
              }
              in=Util.getDataInputStream(b);
//#debug
              tP=7;
              try {
                r=new RMSRoute(in, i);
//#debug
                tP=8;

                rmsRoutes.addElement(r);
//#debug
                tP=9;
                in.close();
              } catch (IOException ioe) {
              }

            } catch (RecordStoreException e) {
            }
          }
//#debug
          tP=10;
          }finally{  re.destroy();}
        } finally {
          rS.closeRecordStore();
        }
//#debug
        tP=11;
      } catch (RecordStoreException rse) {
      }
//#mdebug
    } catch (Throwable t) {
      if (RMSOption.debugEnabled){
        DebugLog.add2Log("Rel RMSR:"+String.valueOf(tP)+":"+t);
      }
    }
//#enddebug
  }

  private void readSetting() {
    try {
      RecordStore recordSetStore=RecordStore.openRecordStore(RSETTNAME, true);
      try {
        int rc=recordSetStore.getNumRecords();
        if (rc>0){
          DataInputStream is;
          is=Util.getDataInputStream(recordSetStore.getRecord(1));
          try {
            RMSOption.loadFromStream(is);
            is.close();
          } catch (IOException ioe) {
          //#mdebug
      if (RMSOption.debugEnabled){
        DebugLog.add2Log("RS: "+ioe);
      }
//#enddebug
          }
        }
      } finally {
        recordSetStore.closeRecordStore();
      }
    } catch (Exception e) {
            //#mdebug
      if (RMSOption.debugEnabled){
        DebugLog.add2Log("RS1: "+e);
      }
//#enddebug
    }

    try {
      RecordStore recordSetStore=RecordStore.openRecordStore(RCMNAME, true);
      try {
        int rc=recordSetStore.getNumRecords();
        if (rc>0){
          DataInputStream is;
          is=Util.getDataInputStream(recordSetStore.getRecord(1));
          try {
            RMSOption.loadCMFromStream(is);
            is.close();
          } catch (IOException ioe) {
                 //#mdebug
      if (RMSOption.debugEnabled){
        DebugLog.add2Log("RS2: "+ioe);
      }
//#enddebug
          }
        }
      } finally {
        recordSetStore.closeRecordStore();
      }
    } catch (Exception e) {
           //#mdebug
      if (RMSOption.debugEnabled){
        DebugLog.add2Log("RS3: "+e);
      }
//#enddebug
    }
  }

  public boolean writeSettingNow() {
    RMSOption.changed=true;
    return writeSetting();
  }

  public synchronized boolean writeSetting() {
    if (RMSOption.changed){
      try {
        RecordStore recordSetStore=RecordStore.openRecordStore(RSETTNAME, true);
        try {
          int rc=recordSetStore.getNumRecords();
          ByteArrayOutputStream baos=new ByteArrayOutputStream();
          DataOutputStream outputStream=new DataOutputStream(baos);
          try {
            RMSOption.save2Stream(outputStream);
          } catch (IOException ioe) {
          }
          byte[] b=baos.toByteArray();

          if (rc==1){
            recordSetStore.setRecord(1, b, 0, b.length);
          } else {
            recordSetStore.addRecord(b, 0, b.length);
          }
          RMSOption.changed=false;
        } finally {
          recordSetStore.closeRecordStore();
        }
      } catch (Throwable e) {
        return false;
      }
    }
    try {
      RecordStore recordSetStore=RecordStore.openRecordStore(RCMNAME, true);
      try {
        int rc=recordSetStore.getNumRecords();
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        DataOutputStream outputStream=new DataOutputStream(baos);
        try {
          RMSOption.saveCM2Stream(outputStream);
        } catch (IOException ioe) {
        }
        byte[] b=baos.toByteArray();

        if (rc==1){
          recordSetStore.setRecord(1, b, 0, b.length);
        } else {
          recordSetStore.addRecord(b, 0, b.length);
        }
      } finally {
        recordSetStore.closeRecordStore();
      }
    } catch (Throwable e) {
      return false;
    }
    return true;
  }

  public void writeRMSList(boolean myMap) {
    try {
      RecordStore recordSetStore;

      if (myMap){
        if (mapInd==0){
          return;
        }
        recordSetStore=RecordStore.openRecordStore(RMSOption.IMAPS_RMST[mapInd-1], true);
      } else {
        recordSetStore=RecordStore.openRecordStore(RPICTILENAME, true);
      }
      try {
        ByteArrayOutputStream baos=new ByteArrayOutputStream(10000);
        DataOutputStream outputStream=new DataOutputStream(baos);
        try {
          int tc=0;
          //for (int i=rmsTiles.size()-1;i>=0;i--)
          //  if (((RMSTile) rmsTiles.elementAt(i)).isMyMap()==myMap)tc++;
          for (Enumeration e=rmsTiles.elements(); e.hasMoreElements();) {
            if (((RMSTile) e.nextElement()).isMyMap()==myMap){
              tc++;//
//          Hashtable rmsT = new Hashtable(1000);
//          for (Enumeration e= rmsTiles.elements(); e.hasMoreElements();) {
//            rt = (RMSTile) e.nextElement();
//            if (rt.isMyMap()) rmsT.put(rt,rt);
//          }
//          rmsTiles=rmsT;
            }
          }
          outputStream.writeInt(tc);
          RMSTile rt;
          for (Enumeration e=rmsTiles.elements(); e.hasMoreElements();) {
            if ((rt=(RMSTile) e.nextElement()).isMyMap()==myMap){
              rt.save2Stream(outputStream);//          for (int i=rmsTiles.size()-1;i>=0;i--) {
//            RMSTile rt = (RMSTile) rmsTiles.elementAt(i);
////            if ((rt.isMyMap()==myMap)&&( (rt.picSize==0)||(!myMap) ))
//            if ((rt.isMyMap()==myMap))
//              rt.save2Stream(outputStream);
//          }
            }
          }
        } catch (IOException ioe) {
        }
        byte[] b=baos.toByteArray();
        baos=null;
        if (recordSetStore.getNumRecords()==1){
          recordSetStore.setRecord(1, b, 0, b.length);
        } else {
          recordSetStore.addRecord(b, 0, b.length);
        }
        RMSOption.changed=true;
      } finally {
        recordSetStore.closeRecordStore();
      }
    } catch (Exception e) {
    }
  }

  public void readRMSList(boolean myMap) {
    try {
      RecordStore recordSetStore;
      if (myMap){
        if (mapInd==0){
          return;
        }
        recordSetStore=RecordStore.openRecordStore(RMSOption.IMAPS_RMST[mapInd-1], true);
      } else {
        recordSetStore=RecordStore.openRecordStore(RPICTILENAME, true);
      }
      try {

        try {
          int rc=recordSetStore.getNumRecords();

          if (rc>0){
            DataInputStream is;
            is=Util.getDataInputStream(recordSetStore.getRecord(1));
            try {
              int n=is.readInt();
              RMSTile rt;
              for (int i=n; i>0; i--) {
                rt=new RMSTile(is);
                //if (!rmsTiles.contains(rt))
                rmsTiles.put(rt, rt);
              //rmsTiles.addElement(rt);
              }
            } finally {
              is.close();
            }
          }
        } catch (IOException ioe) {
//#mdebug
          if (RMSOption.debugEnabled)
            DebugLog.add2Log("RMSL io:"+ioe);
//#enddebug
          
        }

      } finally {
        recordSetStore.closeRecordStore();
      }
    } catch (Exception e) {
    }
  }
//  public void addOuterRMSTiles(Vector av){
//    RMSTile rt;
//    for(int i=rmsTiles.size()-1;i>=0;i--){
//      rt = (RMSTile) rmsTiles.elementAt(i);
//      if ((rt.isMyMap())&&( rt.picSize!=0 ))
//        rmsTiles.removeElementAt(i);
//    }
//
//    for(int i=av.size()-1;i>=0;i--){
//      rt = (RMSTile) av.elementAt(i);
//      rmsTiles.addElement(rt);
//    }
//  }
  private static String getGeoInfoName(long id, byte type) {
    return ("gin_"+id+"_"+type);
  }

  private static String getGeoDataName(long id, byte type) {
    return ("gdn_"+id+"_"+type);
  }

  public static void saveGeoInfo(long id, String info, byte type) {
    ByteArrayOutputStream baos=new ByteArrayOutputStream();
    DataOutputStream outputStream=new DataOutputStream(baos);
    try {
      outputStream.writeUTF(info);
    } catch (IOException ioe) {
    }
    byte[] b=baos.toByteArray();
    baos=null;
    try {
      RecordStore recordGEOStore=RecordStore.openRecordStore(getGeoInfoName(id, type), true);
      int nr=recordGEOStore.getNumRecords();
      if (nr==0){
        recordGEOStore.addRecord(b, 0, b.length);
      } else {
        recordGEOStore.setRecord(1, b, 0, b.length);
      }
      recordGEOStore.closeRecordStore();
    } catch (RecordStoreException rse) {
    }
  }

  public static String loadGeoInfo(long id, byte type) {
    String res=null;
    try {
      RecordStore recordGEOStore=RecordStore.openRecordStore(getGeoInfoName(id, type), true);
      try {
        int nr=recordGEOStore.getNumRecords();
        if (nr>0){
          DataInputStream is;
          is=Util.getDataInputStream(recordGEOStore.getRecord(1));
          try {
            res=is.readUTF();
            is.close();
          } catch (IOException ioe) {
          }
        }
      } finally {
        recordGEOStore.closeRecordStore();
      }
    } catch (RecordStoreException rse) {
    }
    return res;
  }
  
  public static final byte GEODATA_GPSCLUB=1;
  public static final byte GEODATA_ADIMAGE=2;
  public static final byte GEODATA_MAPMARK=3;

  public static void saveGeoData(int id, byte[] data, byte type) {
    try {
      RecordStore recordGEOStore=RecordStore.openRecordStore(getGeoDataName(id, type), true);
      int nr=recordGEOStore.getNumRecords();
      if (nr==0){
        recordGEOStore.addRecord(data, 0, data.length);
      } else {
        recordGEOStore.setRecord(1, data, 0, data.length);
      }
      recordGEOStore.closeRecordStore();
    } catch (RecordStoreException rse) {
    }
  }

  public static byte[] loadGeoData(int id, byte type) {
    byte[] res=null;
    try {
      RecordStore recordGEOStore=RecordStore.openRecordStore(getGeoDataName(id, type), true);
      try {
        int nr=recordGEOStore.getNumRecords();
        if (nr>0){
          res=recordGEOStore.getRecord(1);
        }
      } finally {
        recordGEOStore.closeRecordStore();
      }
    } catch (RecordStoreException rse) {
    }
    return res;
  }

  public static void clearGeoInfo(int id, byte type) {
    try {
      RecordStore.deleteRecordStore(getGeoInfoName(id, type));
    } catch (Throwable t) {
    }
  }

  public RMSTile maxLevelInCache(double lat, double lon) {
    RMSTile res=null;
    RMSTile rt;
    int nX,  nY;
//    for (int i=rmsTiles.size()-1;i>=0;i--){
//      rt=(RMSTile)rmsTiles.elementAt(i);
//      nX=MapUtil.getNumX(MapUtil.getXMap(lon, rt.level));
//      nY=MapUtil.getNumY(MapUtil.getYMap(lat, rt.level));
//      if ((rt.numX==nX)&&(rt.numY==nY)) {
//        if (res==null) res=rt;
//        else if(rt.level>res.level) res=rt;
//      }
//    }

    for (Enumeration e=rmsTiles.elements(); e.hasMoreElements();) {
      rt=(RMSTile) e.nextElement();
      nX=MapUtil.getNumX(MapUtil.getXMap(lon, rt.level));
      nY=MapUtil.getNumY(MapUtil.getYMap(lat, rt.level));
      if ((rt.numX==nX)&&(rt.numY==nY)){
        if (res==null){
          res=rt;
        } else if (rt.level>res.level){
          res=rt;
        }
      }
    }

    return res;
  }
  private static String KMLSTORE="kml_b4ab";

  public void loadKMLRoutes() {
    if (MapCanvas.map.kmlRoutes.size()!=0){
      return;
    }
    boolean exists=true;
    try {
      RecordStore recordGEOStore=RecordStore.openRecordStore(KMLSTORE, false);
    } catch (Throwable e) {
      exists=false;
    }
    if (!exists){
      try {
        RecordStore recordGEOStore=RecordStore.openRecordStore(KMLSTORE, true);
        try {
          KMLMapRoute mp;
          ByteArrayOutputStream baos;
          DataOutputStream dos;

          mp=new KMLMapRoute("/ge.kml");
          mp.readLocal();
          baos=new ByteArrayOutputStream();
          dos=new DataOutputStream(baos);
          mp.save2Stream(dos);
          byte[] data=baos.toByteArray();
          int cs=MapUtil.checkSum(data);
          if (cs==11366){
            recordGEOStore.addRecord(data, 0, data.length);
//      
//          mp = new KMLMapRoute("/we.kml");
//         // mp.href="http://www.weatherbonk.com/servlet/GoogleEarth?showAllWeather=true&display=C&showBarbs=on";
//       // mp.name="Weather";
//          mp.readLocal();
//        baos = new ByteArrayOutputStream();
//        dos=new DataOutputStream(baos);
//        mp.save2Stream(dos);
//        data=baos.toByteArray();
//        //if (MapUtil.checkSum(data)==23208)
//          recordGEOStore.addRecord(data, 0, data.length);
          }
        } finally {
          recordGEOStore.closeRecordStore();
        }


      } catch (Throwable e) {
//#debug
        e.printStackTrace();
      }
    }
    try {
      RecordStore recordGEOStore=RecordStore.openRecordStore(KMLSTORE, false);
      try {
        RecordEnumeration re=recordGEOStore.enumerateRecords(null, null, false);
        while (re.hasNextElement()) {
          DataInputStream is=Util.getDataInputStream(re.nextRecord());
          try {
            KMLMapRoute mp=new KMLMapRoute(is);
            MapCanvas.map.kmlRoutes.addElement(mp);
            is.close();
          } catch (Throwable ioe) {
          }
        }
        re.destroy();
      } finally {
        recordGEOStore.closeRecordStore();
      }
    } catch (RecordStoreException rse) {
    }

  }
}
