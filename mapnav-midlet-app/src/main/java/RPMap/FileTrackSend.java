/*
 * FileMapSend.java
 *
 * Created on 21 ������ 2007 �., 2:58
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package RPMap;

import java.io.DataOutputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Displayable;
import lang.Lang;
import lang.LangHolder;
//#debug
//# import misc.DebugLog;
import misc.ProgressResponse;

/**
 *
 * @author RFK
 */
public class FileTrackSend extends RouteSend {

    /** Creates a new instance of FileMapSend for autosave*/
    public FileTrackSend(Displayable backDisp, String addrs1, MapRoute mr, boolean autoSend) {
        super(backDisp, addrs1, mr);
        this.autoSend=autoSend;
    }
    private boolean autoSend;
    private byte kind;
//  private int count;

    /** Creates a new instance of FileMapSend to save ALL*/
    public FileTrackSend(Displayable backDisp, String addrs1, byte kind) {
        super(addrs1);
        this.kind=kind;
        //   this.count=count;
        this.backDisp=backDisp;
    }

    void sendAll() {
        try {
            try {
                makeFolder();
            } catch (Throwable t) {
//#mdebug
//#                 if (RMSOption.debugEnabled){
//#                     DebugLog.add2Log("RM send:"+t);
//#                 }
//#enddebug
                MapCanvas.showmsgmodal(LangHolder.getString(Lang.export), t.toString(), AlertType.ERROR, backDisp);
                mR=null;
                return;
            }
            int count=MapCanvas.map.rmss.getRouteCount();
            for (int i=0; i<count; i++) {
                mR=MapCanvas.map.rmss.loadRoute(i, kind);
                send();
            }
//      mR=MapCanvas.map.rmss.beginLoadRoutes(kind);
//      if (mR!=null) send();
//      while ((mR=MapCanvas.map.rmss.nextLoadRoutes(kind))!=null) send();
            MapCanvas.showmsg(LangHolder.getString(Lang.export), LangHolder.getString(Lang.saved), AlertType.INFO, backDisp);
        } catch (Throwable t) {
//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("R send:"+t+" \n"+mR.name);
//#             }
//#enddebug
            MapCanvas.showmsgmodal(LangHolder.getString(Lang.export), mR.name+':'+t, AlertType.ERROR, backDisp);
            mR=null;
        }

    }

    void makeFolder() throws Throwable {
        if (savePath.charAt(savePath.length()-1)!='/'){
            savePath=savePath+'/';
        }

        String fn=savePath+MapUtil.trackNameAuto()+'/';
        FileConnection fc=(FileConnection) Connector.open("file:///"+fn);
        try {
            fc.mkdir();
            savePath=fn;
        } finally {
            fc.close();
        }
    }

    public final static String sendFilename(MapRoute mR, boolean backup) {
        String extS=".ERR";
        String hdrS="ER";
        if (mR.kind==MapRoute.TRACKKIND){
            if (!backup){
                extS=RouteSend.EXPORTFORMAT==MapRouteLoader.FORMATOZI?".PLT":RouteSend.EXPORTFORMAT==MapRouteLoader.FORMATKML?".KML":".GPX";
            } else {
                extS=".BAK";
            }
            hdrS="TR";
            return hdrS+MapUtil.checkFilename(mR.name)+extS;
        } else {
            if (mR.kind==MapRoute.ROUTEKIND){
                extS=RouteSend.EXPORTFORMAT==MapRouteLoader.FORMATOZI?".RTE":RouteSend.EXPORTFORMAT==MapRouteLoader.FORMATKML?".KML":".GPX";
                hdrS="RT";
            } else if (mR.kind==MapRoute.WAYPOINTSKIND){
                extS=RouteSend.EXPORTFORMAT==MapRouteLoader.FORMATOZI?".WPT":RouteSend.EXPORTFORMAT==MapRouteLoader.FORMATKML?".KML":".GPX";
                //extS=".WPT";
                hdrS="WP";
            }
            return hdrS+MapUtil.numStr(RMSOption.picFileNumber, 4)+extS;
        }
    }
    private boolean sendOne=true;

    void send() throws Throwable {
        String fn="UNKNOWN", ss="";
        int picN=RMSOption.picFileNumber;
        try {
            String extS=".ERR";
            String hdrS="ER";
            if (autoSend){
                RouteSend.EXPORTFORMAT=MapRouteLoader.FORMATGPX;
            }

            fn=savePath+sendFilename(mR, false);
            if (mR.kind!=MapRoute.TRACKKIND){
                RMSOption.picFileNumber++;
                RMSOption.changed=true;
                try {
                    MapCanvas.map.rmss.writeSetting();
                } catch (Exception e) {
                }
            }
            String backName=savePath+sendFilename(mR, true);
            if (autoSend){
                try {
                    //надо подождать, пока другие потоки отработают
                    Thread.sleep(1000);
                    FileConnection fc=(FileConnection) Connector.open("file:///"+fn);
                    try {
                        if (fc.exists()){
                            try {
                                FileConnection fcBak=(FileConnection) Connector.open("file:///"+backName);
                                try {
                                    fcBak.delete();
                                } catch (Throwable t) {
//#mdebug
//#                                     if (RMSOption.debugEnabled){
//#                                         DebugLog.add2Log("R bk del:"+t+" \n"+backName);
//#                                     }
//#enddebug
                                }
                                fcBak.close();
                            } catch (Throwable t) {
//#mdebug
//#                                 if (RMSOption.debugEnabled){
//#                                     DebugLog.add2Log("R bk cl:"+t+" \n"+backName);
//#                                 }
//#enddebug
                            }
                            fc.rename(sendFilename(mR, true));

                        }
                    } finally {
                        fc.close();
                    }
                } catch (Throwable t) {
//#mdebug
//#                     if (RMSOption.debugEnabled){
//#                         DebugLog.add2Log("R bk ren:"+t+" \n"+fn);
//#                     }
//#enddebug
                }
            }
            FileConnection fc=(FileConnection) Connector.open("file:///"+fn);
            try {
                if (fc.exists()){
                    throw new Exception("File already exists!");
                }
                fc.create();
                OutputStream os=fc.openOutputStream();
                DataOutputStream dos=new DataOutputStream(os);
                try {
                    if (RouteSend.EXPORTFORMAT==MapRouteLoader.FORMATOZI){
                        mR.save2OziStream(dos, progressResponse);
                    } else if (RouteSend.EXPORTFORMAT==MapRouteLoader.FORMATKML){
                        mR.save2KML(dos, progressResponse);
                    } else if (RouteSend.EXPORTFORMAT==MapRouteLoader.FORMATGPX){
                        mR.save2GPX(dos, progressResponse);
                    }
                } finally {
                    dos.close();
                    os.close();
                }

                ss=fn;
                //��������� ���.����� ��� �����, ��� ��� ������� �� �������
                if ((mR.kind==MapRoute.ROUTEKIND)&&(RouteSend.EXPORTFORMAT==MapRouteLoader.FORMATOZI)){
                    mR.kind=MapRoute.WAYPOINTSKIND;
                    extS=".WPT";
                    hdrS="WP";
                    fn=savePath+hdrS+MapUtil.numStr(picN, 4)+extS;
                    fc=(FileConnection) Connector.open("file:///"+fn);
                    if (fc.exists()){
                        throw new Exception(" file already exists!");
                    }
                    fc.create();
                    os=fc.openOutputStream();
                    dos=new DataOutputStream(os);
                    try {
                        mR.save2OziStream(dos, progressResponse);
                    } finally {
                        dos.close();
                        os.close();
                    }
                    ss=ss+" \n"+fn;
                }
            } finally {
                fc.close();
            }
            mR.saved=true;
            MapCanvas.map.rmss.saveRoute(mR);

            if (autoSend){
                mR.backupFailed=false;
            } else if ((sendOne)&&(backDisp!=null)){
                MapCanvas.showmsg(LangHolder.getString(Lang.sent), LangHolder.getString(Lang.filesaved)+" \n"+ss, AlertType.INFO, backDisp);
            }
            progressResponse=null;
        } catch (Throwable t) {
            progressResponse=null;
//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("R send:"+t+" \n"+fn);
//#             }
//#enddebug
            if (autoSend&&(!RMSOption.debugEnabled)){
                mR.backupFailed=true;
            } else if ((sendOne)&&(backDisp!=null)){
                MapCanvas.showmsgmodal(LangHolder.getString(Lang.sent), t.toString(), AlertType.ERROR, backDisp);
            } else {
                if (!autoSend){
                    throw t;
                }
            }
        }
    }
    ProgressResponse progressResponse;

    public void setProgressResponse(ProgressResponse progressResponse) {
        this.progressResponse=progressResponse;
    }

    public boolean stopIt() {
        return true;

    }
}
