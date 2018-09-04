/*
 * LastVersion.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package misc;

import RPMap.MapCanvas;
import RPMap.MapUtil;
import RPMap.RMSOption;
import RPMap.RMSSettings;
import javax.microedition.lcdui.StringItem;
import lang.LangHolder;

/**
 *
 * @author RFK
 */
public class LastVersion implements Runnable {

  StringItem strStable;
  StringItem strDebug;
  byte mode;

  /** Creates a new instance of LastVersion */
  public LastVersion(StringItem sS, StringItem sD) {
    strStable=sS;
    strDebug=sD;
    mode=0;
  }
  //LastVersion lv3;
  private int w,h;
  public LastVersion(int scrW, int scrH) {
    //lv3=this;
    w=scrW; h=scrH;
    mode=1;
    (new Thread(this)).start();
  }

  private void checkNewVer() {
    String s, s1;
    try {
      String[] data_our=new String[3];
      String[] data_site=new String[3];

      //if (RMSOption.tellNewDebugVersion) {
      //  s1=HTTPUtils.getHTTPContentAsString(MapUtil.homeSiteURL+"lastdeb.txt");
      //} else {
        //s1=HTTPUtils.getHTTPContentAsString(MapUtil.homeSiteURL+"lastver.txt");
        s1=HTTPUtils.getHTTPContentAsString("https://raw.githubusercontent.com/magdel/MapNav/master/appconfig.txt");
      //}
      if (!s1.equals(RMSOption.infoAboutLastVersion)) {
        if (!s1.equals(MapUtil.version)){

          MapUtil.parseString('.'+s1, '.', data_site);
          MapUtil.parseString('.'+MapUtil.version, '.', data_our);
          boolean newver=false;
          int sv=Integer.parseInt(data_site[0]);
          int ov=Integer.parseInt(data_our[0]);
          if (sv>ov) {
            newver=true;
          } else if (sv==ov){
            sv=Integer.parseInt(data_site[1]);
            ov=Integer.parseInt(data_our[1]);
            if (sv>ov) {
              newver=true;
            } else if (sv==ov){
              sv=Integer.parseInt(data_site[2]);
              ov=Integer.parseInt(data_our[2]);
              if (sv>ov) {
                newver=true;
              }
            }
          }
          if (newver){
            //other version is available at site to download
            if (RMSOption.tellNewDebugVersion) {
              s=MapUtil.homeSiteURL+"newdeb_"+LangHolder.getCurrUiLanguage().toLowerCase()+".txt";
            } else {
              s=MapUtil.homeSiteURL+"newver_"+LangHolder.getCurrUiLanguage().toLowerCase()+".txt";
            }
            s=HTTPUtils.getHTTPContentAsString(s);
            if (MapCanvas.map.isShown()){
              MapCanvas.setCurrent(new TextCanvas(s));
              RMSOption.infoAboutLastVersion=s1;
            }
          }
        }
      }
    } catch (Throwable t) {
    }

  }

  private void getNewAds() {
    if (RMSOption.adsNumber>100)return;
    HTTPUtils img_bytes = HTTPUtils.getHTTPContent(MapUtil.homeSiteURL+"ad/ai.php?v=1&w="+w+"&h="+h);
    if ((img_bytes.responseCode!=200)||(img_bytes.baos.size()==0)) return;
    RMSOption.adsNumber++;
    RMSSettings.saveGeoData(RMSOption.adsNumber, img_bytes.baos.toByteArray(), RMSSettings.GEODATA_ADIMAGE);
  }

  private void getNewSettings() {
    String s1, s2, s3;
    //try {
      //Thread.sleep(25000);

      s1=HTTPUtils.getHTTPContentAsString(MapUtil.homeSiteURL+"lastglsur.txt");
      s2=HTTPUtils.getHTTPContentAsString(MapUtil.homeSiteURL+"lastglhyb.txt");
      s3=HTTPUtils.getHTTPContentAsString(MapUtil.homeSiteURL+"lastglmap.txt");

      if ((s1.length()<5)||(s2.length()<5)||(s3.length()<5)) {
        return;
      }
      RMSOption.surGL_URL=s1;
      RMSOption.netGL_URLtr=s2;
      RMSOption.netGL_URL=s3;

//    } catch (Throwable t) {
//    }

  }

  public void run() {
    if (mode==1) {
      try {
        Thread.sleep(70000);
        checkNewVer();
        Thread.sleep(10000);
        getNewSettings();
        Thread.sleep(20000);
        getNewAds();
      } catch (Throwable t) {
      }
        return;
    }
    String s;
    try {
      s=HTTPUtils.getHTTPContentAsString(MapUtil.homeSiteURL+"lastver.txt");
      strStable.setText(s);
    } catch (Throwable t) {
      strStable.setText("No info");
    }
    try {
      s=HTTPUtils.getHTTPContentAsString(MapUtil.homeSiteURL+"lastdeb.txt");
      strDebug.setText(s);
    } catch (Throwable t) {
      strStable.setText("No info");
    }

  }
}
