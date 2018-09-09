/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package RPMap;

import camera.MD5;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Displayable;
import lang.Lang;
import lang.LangHolder;
//#debug
//# import misc.DebugLog;
import misc.HTTPUtils;
import misc.ProgressResponse;
import misc.ProgressStoppable;
import netradar.NetRadar;

/**
 *
 * @author Raev
 */
public class OSMTrackSend extends RouteSend implements ProgressStoppable {
  
  /** Creates a new instance of WebTrackSend */
  public OSMTrackSend(Displayable backDisp,MapRoute mr) {
    super(backDisp, null,mr);
  }
  
  void send() throws Throwable {
   if (mR.mnTrackId==0){
     MapCanvas.showmsgmodal(LangHolder.getString(Lang.error),"Upload to WWW first!",AlertType.WARNING,backDisp);
     return;
   }
   if (RMSOption.getStringOpt(RMSOption.SO_OSMLOGIN).equals(MapUtil.emptyString)){
     MapCanvas.showmsgmodal(LangHolder.getString(Lang.error),"Enter OSM Login and Password first in Settings!",AlertType.WARNING,backDisp);
     return;  
   }
   
     String prs = LangHolder.getString(Lang.loading)+"...";
    progressResponse.setProgress((byte)0,prs);
//#debug
//#     double errPos =1;
    String httpRes="";
    try{
    String osm_auth = RMSOption.getStringOpt(RMSOption.SO_OSMLOGIN)+':'+RMSOption.getStringOpt(RMSOption.SO_OSMPASS);
    String sign = MD5.getHashString(RMSOption.netRadarLogin+mR.mnTrackId+osm_auth+MD5.getHashString(RMSOption.netRadarPass));
    String url = NetRadar.netradarSiteURL+"nrs/ms_tr_osm.php?lg="+RMSOption.netRadarLogin+
      "&sign="+sign+"&tr_id="+mR.mnTrackId+"&lgo="+osm_auth;

      prs = LangHolder.getString(Lang.sending)+"...";
      progressResponse.setProgress((byte)100,prs);
      
      httpRes = HTTPUtils.getHTTPContentAsString(url);
      String osmRes = httpRes.substring(httpRes.lastIndexOf('\n')+1);
      int osmId = Integer.parseInt(osmRes);
      if (osmId<1000) throw new Exception("Some error!");
      mR.osmTrackId = osmId;
      MapCanvas.map.rmss.saveRoute(mR);
    
//#debug
//#     errPos =1.4;
          
      prs = LangHolder.getString(Lang.sending)+"...";
      progressResponse.setProgress((byte)100,prs);
            
      MapCanvas.showmsg(LangHolder.getString(Lang.info),"OSM Upload OK! ID="+osmId,AlertType.INFO,backDisp);
//#mdebug
//#       if (RMSOption.debugEnabled)
//#         DebugLog.add2Log("OSM - OK:"+httpRes);
//#enddebug
    }catch(NumberFormatException nf){
//#mdebug
//#       if (RMSOption.debugEnabled)
//#         DebugLog.add2Log("OSM:"+errPos+':'+httpRes+':'+nf);
//#enddebug
      MapCanvas.showmsg(LangHolder.getString(Lang.error),"Response error:\n"+nf+"\n Response:"+httpRes,AlertType.ERROR,backDisp);
      
    }catch  (Throwable t){
//#mdebug
//#       if (RMSOption.debugEnabled)
//#         DebugLog.add2Log("OSM:"+errPos+':'+t);
//#enddebug
      MapCanvas.showmsg(LangHolder.getString(Lang.error),"Upload error:\n"+t.getMessage()+"\n Response:"+httpRes,AlertType.ERROR,backDisp);
    }
    progressResponse=null;
  }
  
//  void send() throws Throwable {
//    String prs = LangHolder.getString(Lang.loading)+"...";
//    progressResponse.setProgress((byte)0,prs);
////#debug
//    double errPos =1;
//    try{
//    StringBuffer ps = new StringBuffer();
//    //String s;
//    //MapPoint mp;
//    
//    String ts = HTTPUtils.getTS();
//    StringBuffer sb = new StringBuffer();
//    HTTPUtils.appendParam(sb,"description",mR.name,ts);
//    HTTPUtils.appendParam(sb,"public","1",ts);
//    HTTPUtils.appendParam(sb,"tags","MapNav",ts);
//    
////#debug
//    errPos =1.05;
//    progressResponse.setProgress((byte)10,LangHolder.getString(Lang.saving));
//    
//    ByteArrayOutputStream baos = new ByteArrayOutputStream(30000);
//    mR.save2GPX(baos);
////#debug
//    errPos =1.1;
//    byte[] bytesTrack = baos.toByteArray();
//    baos=null;
////#debug
//    errPos =1.2;
//    
//    sb.append("--" + ts + "\r\n" + "Content-Disposition: form-data; name=\"file\"; filename=\""+mR.name+".gpx\"\r\n" + "Content-Type: text/plain\r\n" + "\r\n");
//
//    String s11 = "http://api.openstreetmap.org/api/0.5/gpx/create";
//
////#debug
//    errPos =1.3;
//    
//    String httpRes = HTTPUtils.sendDataPostRequestS(sb.toString(), bytesTrack, "\r\n\r\n--" + ts + "--\r\n", s11, ts,this);
//
//    
////#debug
//    errPos =1.4;
//          
//      prs = LangHolder.getString(Lang.sending)+"...";
//      progressResponse.setProgress((byte)100,prs);
//            
//      MapCanvas.showmsg(LangHolder.getString(Lang.info),"Upload OK!",AlertType.INFO,backDisp);
////#mdebug
//      if (RMSOption.debugEnabled)
//        DebugLog.add2Log("OSM - OK:"+httpRes);
////#enddebug
//      
//    }catch (Throwable t){
////#mdebug
//      if (RMSOption.debugEnabled)
//        DebugLog.add2Log("OSM:"+errPos+':'+t.toString());
////#enddebug
//      MapCanvas.showmsg(LangHolder.getString(Lang.error),"Upload error:\n"+t.getMessage(),AlertType.ERROR,backDisp);
//    }
//  }
//  
//  
  void sendAll() {
  }
  
  ProgressResponse progressResponse;
  public void setProgressResponse(ProgressResponse progressResponse){
    this.progressResponse=progressResponse;
  }

    public boolean stopIt() {
                return true;

    }
//  
//  public void setProgress(byte percent, String task) {
//    if (progressResponse!=null){
//      progressResponse.setProgress((byte)(20+percent*80/100),LangHolder.getString(Lang.track)+"...");
//    }
//  }
  
}
