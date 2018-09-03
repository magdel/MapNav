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
import lang.LangHolder;
//#debug
//# import misc.DebugLog;
import misc.HTTPUtils;

/**
 *
 * @author Raev
 */
public class SurNetwMapTile extends MapTile {

    /** Creates a new instance of NetworkMapTile */
    public SurNetwMapTile(int nX, int nY, int lev, byte mapType, boolean load) {
        super(nX, nY, lev, mapType);
        tileImageType=SHOW_SURMAP;
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

            String url;
            if (tileServerType==SHOW_AS){
                url=MapUtil.getAskBlockNameURL(numX, numY, level, tileImageType);
            } else {
                url=RMSOption.netGL_URLtr+String.valueOf(numX)+"&y="+String.valueOf(numY)+"&zoom="+String.valueOf(18-level);
            }

            downloadImage(url);


            mapLoaded=true;
            MapCanvas.map.repaint();
        } catch (Throwable i1oe) {
            paintState(LangHolder.getString(Lang.errorcon));
            //#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("SNM L:"+i1oe);
//#             }
//#enddebug

        }

    }

    public String getFilePath() {
        return "SRM/";
    }
}
