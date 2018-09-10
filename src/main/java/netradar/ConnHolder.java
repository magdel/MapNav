/*
 * ConnHolder.java
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package netradar;

import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import java.io.InputStream;

/**
 * @author RFK
 */
public class ConnHolder extends Thread {

    /**
     * Creates a new instance of ConnHolder
     */
    public ConnHolder() {
    }

    // private boolean tryConn=true;
    boolean stopped;

    public void run() {
        try {
            while (!stopped) {
                //     if (tryConn) {
                try {
                    SocketConnection sc = (SocketConnection) Connector.open("socket://login.icq.com:5190");
                    InputStream is = null;
                    try {
                        is = sc.openInputStream();
                        sleep(30000);
                        is.close();
                    } finally {

                        is = null;

                        sc.close();
                        sc = null;

                    }
                } catch (Throwable t) {
                }
                //      }

                sleep(200);
            }

        } catch (Throwable t) {
        }
    }

}
