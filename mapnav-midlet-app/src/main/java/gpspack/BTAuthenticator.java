/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gpspack;

import javax.microedition.io.StreamConnection;

/**
 *
 * @author rfk
 */
public class BTAuthenticator {

    public BTAuthenticator(StreamConnection conn) {
        BTConnector bt = new BTConnector(conn);
    }
}
