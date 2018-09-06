/*
 * SurfaceMapTile.java
 *
 * Created on 17 ������ 2007 �., 18:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package RPMap;

import javax.microedition.lcdui.Image;
import lang.Lang;
import lang.LangHolder;
//#debug
import misc.DebugLog;

import misc.HTTPUtils;

/**
 *
 * @author Raev
 */
public class SurfaceMapTile extends MapTile {

    /** Creates a new instance of SurfaceMapTile */
    public SurfaceMapTile(int nX, int nY, int lev, byte mapType, boolean load) {
        super(nX, nY, lev, mapType);
        tileImageType=SHOW_SUR;
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

        // try {
        paintState(LangHolder.getString(Lang.loading));
        MapCanvas.map.repaint();

        // InputStream is = null;
        //  OutputStream os = null;
        //  HttpConnection c=null;
        try {
            String url;
            if (tileServerType==SHOW_UM){
                url=MapUtil.getOnlineURL(tileImageType, numX, numY, level);
                paintState(url);
            } else if (tileServerType==SHOW_GU){

                //url="http://maps.gurtam.by/map_gmaps?n=404&x=" +String.valueOf(numX)+"&y="+String.valueOf(numY)+"&zoom="+String.valueOf(18-level) ;
                url="http://vec02.maps.yandex.net/tiles?l=map&v=2.0.4&x="+String.valueOf(numX)+"&y="+String.valueOf(numY)+"&z="+String.valueOf(level);
            } else if (tileServerType==SHOW_OS){
                url=MapUtil.getOSBlockNameURL(numX, numY, level, MapUtil.OSMTYPE_OSMRENDER);
            } else if (tileServerType==SHOW_YH){
                url=MapUtil.getYHBlockNameURL(numX, numY, level, MapTile.SHOW_SUR);
//          c = (HttpConnection) Connector.open(MapUtil.getYHBlockNameURL(numX,numY,level,MapTile.SHOW_SUR));
            } else if (tileServerType==SHOW_VE){
                url=MapUtil.getMapVEURL(numX, numY, level);
//          c = (HttpConnection) Connector.open(MapUtil.getMapVEURL(numX,numY,level));
            } else if (tileServerType==SHOW_AS){
                url=MapUtil.getAskBlockNameURL(numX, numY, level, tileImageType);
//          c = (HttpConnection) Connector.open(MapUtil.getAskBlockNameURL(numX,numY,level,tileImageType));
            } else {
                url=RMSOption.surGL_URL+String.valueOf(numX)+"&y="+String.valueOf(numY)+"&z="+String.valueOf(level-1);
            }
            //url=RMSOption.surGL_URL  +"&x=" +String.valueOf(numX)+"&y="+String.valueOf(numY)+"&z="+String.valueOf(level) ;


            downloadImage(url);

            mapLoaded=true;
            MapCanvas.map.repaint();
        } catch (Throwable i1oe) {
            paintState(LangHolder.getString(Lang.errorcon));
            //#mdebug
            if (RMSOption.debugEnabled){
                DebugLog.add2Log("SMT L:"+i1oe);
            }
//#enddebug
        }
    }

    public String getFilePath() {
        return "SUR/";
    }
}
