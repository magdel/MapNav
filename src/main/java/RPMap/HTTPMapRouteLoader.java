/*
 * HTTPGPSRouteLoader.java
 *
 * Created on 15 ������ 2007 �., 17:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package RPMap;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Raev
 */
public class HTTPMapRouteLoader extends MapRouteLoader {

    public HTTPMapRouteLoader(String url, byte kind, byte CP, byte format) {
        super(url, kind, CP, format);
    }

    public HTTPMapRouteLoader(String url, byte kind, byte CP, byte format, boolean autoStart) {
        super(url, kind, CP, format, autoStart);
    }

    public void load() {
        HttpConnection conn = null;
        InputStream is = null;
        //String err = MapUtil.emptyString; // used for debugging

        try {
            try {
                conn = (HttpConnection) Connector.open("http://" + furl);
                is = conn.openInputStream();
                loadFromStream(is);
                if (getRoute() != null) {
                    getRoute().recalcMapLevelScreen(null);
                }

            } finally {
                if (is != null) {
                    is.close();
                }
                is = null;
                if (conn == null) {
                    conn.close();
                }
                conn = null;
            }
        } catch (IOException i1oe) {
//            paintState("������ ����� �����");
        }

    }
}
