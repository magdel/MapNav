/*
 * NetworkMapTile.java
 *
 * Created on 17 ������ 2007 �., 18:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package RPMap;

import javax.microedition.lcdui.Image;
import lang.Lang;
//#debug
//# import misc.DebugLog;

import lang.LangHolder;
import misc.HTTPUtils;

/**
 *
 * @author Raev
 */
public class NetworkMapTile extends MapTile {

    /** Creates a new instance of NetworkMapTile */
    public NetworkMapTile(int nX, int nY, int lev, byte mapType, boolean load) {
        super(nX, nY, lev, mapType);
        tileImageType=SHOW_MAP;
        tileType=(byte) (tileImageType+tileServerType);
        if (load){
            goLoad();
        }
    }

    void load() {
        if (mapLoaded){
            return;
        }
        if (!RMSOption.onlineMap){
            return;
        }
        try {
            paintState(LangHolder.getString(Lang.loading));
            MapCanvas.map.repaint();

//      InputStream is = null;
//      OutputStream os = null;
//      HttpConnection c=null;
            // try {
            String url;
            //   if (tileServerType==SHOW_CG) {
            //     url="http://mapsquid.mapabc.com/googlechina/maptile?v=w2.61&x=" +String.valueOf(numX)+"&y="+String.valueOf(numY)+"&zoom="+String.valueOf(18-level) ;
            //  } else
            if (tileServerType==SHOW_UM){

                url=MapUtil.getOnlineURL(tileImageType, numX, numY, level);
                paintState(url);

            } else if (tileServerType==SHOW_GU){

                //url="http://maps.gurtam.by/map_gmaps?n=404&x=" +String.valueOf(numX)+"&y="+String.valueOf(numY)+"&zoom="+String.valueOf(18-level) ;
                //url="http://t3.maps.gurtam.by/map_gmaps?n=404&x="+String.valueOf(numX)+"&y="+String.valueOf(numY)+"&zoom="+String.valueOf(18-level);
                url=MapUtil.getOSBlockNameURL(numX, numY, level, MapUtil.OSMTYPE_MAPQUEST);
            } else if (tileServerType==SHOW_OS){
                url=MapUtil.getOSBlockNameURL(numX, numY, level, MapUtil.OSMTYPE_OSM);
            } else if (tileServerType==SHOW_AS){
                url=MapUtil.getAskBlockNameURL(numX, numY, level, tileImageType);
            } else if (tileServerType==SHOW_YH){
                url=MapUtil.getYHBlockNameURL(numX, numY, level, tileImageType);
            } else if (tileServerType==SHOW_VE){
                url=MapUtil.getNetVEURL(numX, numY, level);
            } else {
                url=RMSOption.netGL_URL+String.valueOf(numX)+"&y="+String.valueOf(numY)+"&zoom="+String.valueOf(18-level);
            }

            downloadImage(url);

            mapLoaded=true;
            MapCanvas.map.repaint();

        } catch (Throwable i1oe) {
            paintState(LangHolder.getString(Lang.errorcon));
            //#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("NMT L:"+i1oe);
//#             }
//#enddebug
        }

    }

    public String getFilePath() {
        return "MAP/";
    }
}
