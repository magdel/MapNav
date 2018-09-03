/*
 * RMSExpImpThread.java
 *
 * Created on 2 �������� 2007 �., 19:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package misc;

import RPMap.MapCanvas;
import RPMap.MapUtil;
import RPMap.RMSOption;
import RPMap.RMSSettings;
import RPMap.RMSTile;
import app.MapForms;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Displayable;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import lang.Lang;
import lang.LangHolder;

/**
 *
 * @author RFK
 */
public class RMSExportImport implements Runnable, ProgressStoppable{
  public static final byte SERVEXPORT_PRIVATE = 1;
  public static final byte SERVEXPORT_MAPCACHE = 2;
  
  boolean export;
  String url;
  Displayable backDisp;
  byte expType;
  /** Creates a new instance of RMSExpImpThread */
  public RMSExportImport(boolean export,String url,Displayable backDisp,byte expType) {
    this.url=url;
    this.export=export;
    this.expType=expType;
    this.backDisp=backDisp;
    Thread t = new Thread(this);
//    t.setPriority(Thread.H);
    t.start();
  }
  private final static byte START_OF_RS = 1;
  private final static byte START_OF_RECORD = 2;
  private final static byte START_OF_TILES = 3;
//  private final static byte START_OF_RS = 1;
  
  private void doExport(){
//#debug
//#     int errpos=0;
    String fn=url+MapUtil.trackNameAuto();
    if ((expType&SERVEXPORT_PRIVATE)==SERVEXPORT_PRIVATE) fn=fn+"P";
    if ((expType&SERVEXPORT_MAPCACHE)==SERVEXPORT_MAPCACHE) fn=fn+"M";
    fn=fn+".MNB";
    
    try {
      try {
        MapCanvas.map.rmss.writeSettingNow();
      }catch (Exception e){}
//#debug
//#       errpos=1;
      FileConnection fc = (FileConnection)Connector.open("file:///"+fn);
      try{
//#debug
//#         errpos=2;
        if (fc.exists()) throw new Exception("File already exists!");
//#debug
//#         errpos=3;
        fc.create();
//#debug
//#         errpos=4;
        OutputStream os =  fc.openOutputStream();
        DataOutputStream dos= new DataOutputStream(os);
//#debug
//#         errpos=5;
        try {
          String[] s_rs = RecordStore.listRecordStores();
//#debug
//#           errpos=6;
          dos.writeInt(1);//write version
          //dos.writeInt(s_rs.length);
          RecordStore rs;RecordEnumeration re;
          byte[] data;
//#debug
//#           errpos=7;
          String intpref="INTM";
          if ((expType&SERVEXPORT_PRIVATE)==SERVEXPORT_PRIVATE) {
            for (int i=0;i<s_rs.length;i++){
              if (s_rs[i].equals(RMSSettings.RPICIMGNAME)||
                  s_rs[i].equals(RMSSettings.RPICTILENAME)
                  ||s_rs[i].startsWith(intpref)
                  ) continue;
              try{
//#debug
//#                 errpos=8;
                dos.writeByte(START_OF_RS);//write version
                dos.writeUTF(s_rs[i]);
//#debug
//#                 errpos=9;
                rs=RecordStore.openRecordStore(s_rs[i],false);
                try{
//#debug
//#                   errpos=10;
                  int rcc=rs.getNumRecords();
////#debug
//              errpos=11;
                  dos.writeInt(rcc);
//#debug
//#                   errpos=12;
                  int recnum=0;
                  re = rs.enumerateRecords(null,null,false);
//#debug
//#                   errpos=13;
                  while (re.hasNextElement()) {
                    data = re.nextRecord();
                    if (data!=null) {
                      dos.writeByte(START_OF_RECORD);//write version
                      //#debug
//#                       errpos=14;
                      
                      //#debug
//#                       errpos=15;
                      dos.writeInt(data.length);
//#debug
//#                       errpos=16;
                      dos.write(data);
                    }
//#debug
//#                     errpos=17;
                    data=null;
                    System.gc();
                    if (stopped) throw new Exception();
                    recnum++;
//#debug
//#                     errpos=18;
                    if (progressResponse!=null){
                      progressResponse.setProgress((byte)(recnum*100/rcc),s_rs[i]);
                    }
                  }
                }finally{
                  rs.closeRecordStore();
                }
                
              }catch(Throwable t){
                if (stopped) throw new Exception(LangHolder.getString(Lang.cancel));
                else throw t;
              }
            }
          }
          if ((expType&SERVEXPORT_MAPCACHE)==SERVEXPORT_MAPCACHE) {
            dos.writeByte(START_OF_TILES);//write version
            
            RMSTile rt;
//#debug
//#             errpos=19;
            //Vector rmsT=MapCanvas.map.rmss.rmsTiles;
            //if((rt=(RMSTile)e.nextElement()).isMyMap()==myMap)rt.save2Stream(outputStream);
            
            int tc =0;
            //for (int i=rmsTiles.size()-1;i>=0;i--)
            //  if (((RMSTile) rmsTiles.elementAt(i)).isMyMap()==myMap)tc++;
            for (Enumeration e= MapCanvas.map.rmss.rmsTiles.elements(); e.hasMoreElements();)
              if(!((RMSTile)e.nextElement()).isMyMap())tc++;
            
            dos.writeInt(tc);
//#debug
//#             errpos=20;
            for (Enumeration e= MapCanvas.map.rmss.rmsTiles.elements(); e.hasMoreElements();){
              //for(int i=0;i<rmsT.size();i++){
//#debug
//#               errpos=21;
              //rt=(RMSTile)rmsT.elementAt(i);
              rt=(RMSTile)e.nextElement();
              
//#debug
//#               errpos=22;
              if (rt.isMyMap())
                continue;
              //data=RMSSettings.recordMyPicImgStore.getRecord(rt.recordId);
              else
                data=RMSSettings.recordPicImgStore.getRecord(rt.recordId);
//#debug
//#               errpos=23;
              rt.save2Stream(dos);
              if (!rt.isMyMap()){
//#debug
//#                 errpos=24;
                dos.writeInt(data.length);
//#debug
//#                 errpos=25;
                dos.write(data);
              }
              data=null;
              System.gc();
            }
            
            //#debug
//#             errpos=26;
          }
          
        } finally {
          dos.close();
          os.close();
        }
        //#debug
//#         errpos=27;
      }finally{
        fc.close();
      }
      
      
      MapCanvas.showmsg(LangHolder.getString(Lang.export),LangHolder.getString(Lang.filesaved)+" \n"+fn,AlertType.INFO, backDisp);
      
    } catch (Throwable t) {
//#mdebug
//#       if (RMSOption.debugEnabled)
//#         DebugLog.add2Log("RMS exp:"+errpos+":"+t.toString()+" \n"+fn);
//#enddebug
      MapCanvas.showmsgmodal(LangHolder.getString(Lang.export),t.toString(),AlertType.ERROR, backDisp);
    }
    
  }
  private void doImport(){
//#debug
//#     int errpos=0;
    String fn=url;
    try {
      try{
        FileConnection fc = (FileConnection)Connector.open("file:///"+fn,Connector.READ);
        try{
          if (!fc.exists()) throw new Exception("File not found!");
          InputStream is =  fc.openInputStream();
          DataInputStream dis= new DataInputStream(is);
          try {
//#debug
//#             errpos=1;
            RMSSettings.closeRecordStores();
            //String[] s_rs = RecordStore.listRecordStores();
            Thread.sleep(2000);
            //for (int i=0;i<s_rs.length;i++)
            //RecordStore.deleteRecordStore(s_rs[i]);
//#debug
//#             errpos=2;
            
            int ver = dis.readInt();
            if (ver<1) return;
            
            byte tt=dis.readByte();
            do  {
              //int rc = dis.readInt()-4,rsc;
              String rn;
              RecordStore rs;RecordEnumeration re;
              byte[] data;
              if (tt==START_OF_RS) {
                rn=dis.readUTF();
                
//#debug
//#                 errpos=3;
                try{RecordStore.deleteRecordStore(rn);}catch(Throwable t){}
//#debug
//#                 errpos=4;
                rs=RecordStore.openRecordStore(rn,true);
                
                int rsc=dis.readInt();
                tt=dis.readByte();
                int r=0;
                do {
                  if (tt==START_OF_RECORD) {
                    try{
//#debug
//#                       errpos=5;
                      
                      //  for (int r=0;r<rsc;r++){
                      data= new byte[dis.readInt()];
                      dis.readFully(data);
                      rs.addRecord(data,0,data.length);
                      if (stopped) throw new Exception();
                      if (progressResponse!=null){
                        progressResponse.setProgress((byte)(r*100/rsc),rn);
                      }
                      r++;
                      //   }
                    }catch(Throwable t){
                      if (stopped) throw new Exception(LangHolder.getString(Lang.cancel));
                      else throw t;
                    }
                    tt=dis.readByte();
                    
                  }
                  
                  
                }while (tt==START_OF_RECORD);
                continue;
              }
              
              if (tt==START_OF_TILES) {
                
//#debug
//#                 errpos=6;
                try{RecordStore.deleteRecordStore(RMSSettings.RPICIMGNAME);}catch(Throwable t){}
                //RecordStore.deleteRecordStore(RMSSettings.RMYPICIMGNAME);
//#debug
//#                 errpos=7;
                
                RMSSettings.recordPicImgStore = RecordStore.openRecordStore(RMSSettings.RPICIMGNAME, true);
                //RMSSettings.recordMyPicImgStore = RecordStore.openRecordStore(RMSSettings.RMYPICIMGNAME, true);
//#debug
//#                 errpos=8;
                
                RMSTile rt;
                String task=LangHolder.getString(Lang.cache);
                //Vector rmsT=new Vector(500);
                Hashtable rmsT=new Hashtable(500);
                int tc=dis.readInt();
                int irs;
                data=new byte[20000];
                for(int i=0;i<tc;i++){
                  rt = new RMSTile(dis);
                  if (rt.isMyMap()) continue;
                  //rmsT.addElement(rt);
                  rmsT.put(rt,rt);
                  irs=dis.readInt();
                  if (irs>data.length) data= new byte[irs];
                  dis.readFully(data,0,irs);
                  
                  //if (rt.isMyMap()) {
                  // rt.recordId=RMSSettings.recordMyPicImgStore.addRecord(data, 0, irs);
                  //} else {
                  rt.recordId=RMSSettings.recordPicImgStore.addRecord(data, 0, irs);
                  //}
                  if (progressResponse!=null){
                    progressResponse.setProgress((byte)(i*100/tc),task);
                  }
                }
                
                MapCanvas.map.rmss.rmsTiles=rmsT;
//#debug
//#                 errpos=9;
                MapCanvas.map.rmss.writeRMSList(false);
                //MapCanvas.map.rmss.writeRMSList(true);
                tt=dis.readByte();
                continue;
              }
              
              
              tt=dis.readByte();
            } while (tt>=0);
            
            
          } finally {
            dis.close();is.close();}
        }finally{
          fc.close();
        }
      }catch(IOException ie){
        // fn=null;
      }
      MapCanvas.showmsg(LangHolder.getString(Lang.load),LangHolder.getString(Lang.loaded)+" \n"+fn,AlertType.INFO, backDisp);
      
      Thread.sleep(2000);
      MapCanvas.setCurrent(null);
      MapCanvas.map=null;
      MapForms.mM.destroyApp(true);
      MapForms.mM.notifyDestroyed();
      MapForms.mM=null;
      
      
    } catch (Throwable t) {
//#mdebug
//#       if (RMSOption.debugEnabled)
//#         DebugLog.add2Log("RMS exp:"+errpos+":"+t.toString()+" \n"+fn);
//#enddebug
      MapCanvas.showmsgmodal(LangHolder.getString(Lang.load),t.toString(),AlertType.ERROR, backDisp);
    };
  }
  public void run() {
    try{
      if (export) doExport();
      else doImport();
    }finally{
      progressResponse=null;
    }
  }
  private boolean stopped;
  public boolean stopIt() {
    stopped=true;
            return true;

  }
  ProgressResponse progressResponse;
  public void setProgressResponse(ProgressResponse progressResponse) {
    this.progressResponse=progressResponse;
  }
  
}
