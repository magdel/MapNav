/*
 * WebTrackSend.java
 *
 * Created on 17 ������ 2008 �., 19:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package RPMap;

import camera.MD5;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Displayable;
import lang.Lang;
import lang.LangHolder;
//#debug
import misc.DebugLog;
import misc.HTTPUtils;
import misc.ProgressResponse;
import misc.ProgressStoppable;
import netradar.NetRadar;

/**
 *
 * @author Raev
 */
public class WebTrackSend extends RouteSend implements ProgressStoppable {

    /** Creates a new instance of WebTrackSend */
    public WebTrackSend(Displayable backDisp, MapRoute mr) {
        super(backDisp, null, mr);
    }

    private int sendPart(String tid, String pn, int starti, int endi) throws Throwable {
        StringBuffer ps=new StringBuffer(mR.pts.size()*35);
        String s;
        MapPoint mp;
        int crs, spd;

        for (int i=starti; (i<mR.pts.size())&&(i<endi); i++) {
            mp=(MapPoint) mR.pts.elementAt(i);
            spd=(int) mp.speed;
            if (i<mR.pts.size()-1){
                crs=(int) (MapRoute.courseToCoords(mp.lat, mp.lon, ((MapPoint) mR.pts.elementAt(i+1)).lat, ((MapPoint) mR.pts.elementAt(i+1)).lon)*MapUtil.R2G);
            } else {
                crs=0;
            }
            if (crs<0){
                crs+=360;
            }
            if (i==starti){
                s=MapUtil.emptyString+MapUtil.coordRound5(mp.lat)+','+MapUtil.coordRound5(mp.lon)+','+mp.alt+','+spd+','+crs+','+mp.ts;
            } else {
                s=":"+MapUtil.coordRound5(mp.lat)+','+MapUtil.coordRound5(mp.lon)+','+mp.alt+','+spd+','+crs+','+mp.ts;
            }
            ps.append(s);
        }

        String ss=ps.toString();
        s=null;

        String ts=HTTPUtils.getTS();
        StringBuffer sb=new StringBuffer();

        HTTPUtils.appendParam(sb, "tr", ss, ts);
        HTTPUtils.appendParamEnd(sb, ts);
        s=sb.toString();
        sb=null;

        try {
            String sign=MD5.getHashString(RMSOption.netRadarLogin+pn+tid+MD5.getHashString(RMSOption.netRadarPass));

            String s11=NetRadar.netradarSiteURL+"nrs/ms_tr_part.php?lg="+
              HTTPUtils.urlEncodeString(RMSOption.netRadarLogin)+"&sign="+sign+
              "&pn="+pn+"&ti="+tid;

            String httpRes=HTTPUtils.sendDataPostRequestS(s, null, null, s11, ts, null);
//#debug
   System.out.print(httpRes);
            return Integer.parseInt(httpRes);
        } catch (Throwable t) {
//#mdebug
            if (RMSOption.debugEnabled){
                DebugLog.add2Log("WWW SPE: "+t);
            }
//#enddebug
            return 0;
        }
    }

    void send() throws Throwable {
        if (RMSOption.netRadarLogin.equals("")){
            MapCanvas.showmsgmodal(LangHolder.getString(Lang.error), "Enter login and password in Settings - Netradar!", AlertType.WARNING, backDisp);
            return;
        }

        String prs=LangHolder.getString(Lang.loading)+"...";
        progressResponse.setProgress((byte) 0, prs);
        //StringBuffer ps = new StringBuffer();
        String s;
        MapPoint mp;

        String ts=HTTPUtils.getTS();
        StringBuffer sb=new StringBuffer();
        HTTPUtils.appendParam(sb, "trname", mR.name, ts);
        HTTPUtils.appendParamEnd(sb, ts);
//#debug
        double errPos=1;
        String sign=MD5.getHashString(RMSOption.netRadarLogin+mR.name+MD5.getHashString(RMSOption.netRadarPass));
        try {
            String s11=NetRadar.netradarSiteURL+"nrs/ms_tr_ini.php?lg="+
              HTTPUtils.urlEncodeString(RMSOption.netRadarLogin)+"&sign="+sign;
            String httpRes=HTTPUtils.sendDataPostRequestS(sb.toString(), null, null, s11, ts, null);
//#debug
            errPos=2;

            prs=LangHolder.getString(Lang.sending)+"...";
            progressResponse.setProgress((byte) 10, prs);

//#debug
            errPos=3;
            String tid=httpRes;
            int itid=Integer.parseInt(tid);
            if (itid==0){
                throw new Exception("Initialization error. Try again");
            }
//#debug
            errPos=4;
            int sendc=42;
            int starti=0;
            int endi=starti+sendc;
            int pn=1;
            int sendr=0;
            int sendcount=mR.pts.size()/sendc+1;
//#mdebug
            if (RMSOption.debugEnabled){
                DebugLog.add2Log("WWW SC:"+sendc+':'+sendcount+':'+tid);
            }
//#enddebug
            while (starti<mR.pts.size()) {
                System.gc();
//#mdebug
                if (RMSOption.debugEnabled){
                    DebugLog.add2Log("WWW SP:"+starti+':'+endi);
                }
//#enddebug
//#debug
                errPos=5;
                sendr=sendPart(tid, String.valueOf(pn), starti, endi);
//#debug
                errPos=5.5;
                if (sendr==0){
//#mdebug
                    if (RMSOption.debugEnabled){
                        DebugLog.add2Log("WWW SPE:"+starti+':'+endi);
                    }
//#enddebug
                    System.gc();
                    Thread.sleep(1500);
                    System.gc();
                    sendr=sendPart(tid, String.valueOf(pn), starti, endi);
                }
//#debug
                errPos=6;
                if (sendr==0){
                    sendr=sendPart(tid, String.valueOf(pn), starti, endi);
                }
//#debug
                errPos=7;
                if (sendr==0){
                    throw new Exception("Send error. Try again");
                }
//#debug
                errPos=8;
                starti+=sendc;
                endi+=sendc;
                pn++;
                progressResponse.setProgress((byte) (10+(pn-1)*90/sendcount), prs);
            }
            mR.mnTrackId=itid;
            MapCanvas.map.rmss.saveRoute(mR);

            MapCanvas.showmsg(LangHolder.getString(Lang.info), "Upload OK!", AlertType.INFO, backDisp);
//#mdebug
            if (RMSOption.debugEnabled){
                DebugLog.add2Log("WWW - OK");
            }
//#enddebug

        } catch (Throwable t) {
//#mdebug
            if (RMSOption.debugEnabled){
                DebugLog.add2Log("WWW:"+errPos+':'+t);
            }
//#enddebug
            MapCanvas.showmsgmodal(LangHolder.getString(Lang.error), "Upload error:\n"+t.getMessage(), AlertType.ERROR, backDisp);
        }
    }

    void sendAll() {
    }
    ProgressResponse progressResponse;

    public void setProgressResponse(ProgressResponse progressResponse) {
        this.progressResponse=progressResponse;
    }

    public void setProgress(byte percent, String task) {
        if (progressResponse!=null){
            progressResponse.setProgress((byte) (20+percent*80/100), LangHolder.getString(Lang.track)+"...");
        }
    }

    public boolean stopIt() {
                return true;

    }
}
