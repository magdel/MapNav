/*
 * SMSRecomend.java
 *
 * Created on 25 ������ 2007 �., 17:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package misc;

import RPMap.MapCanvas;
import RPMap.RMSOption;
import app.MapForms;
import javax.microedition.lcdui.AlertType;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;
import lang.Lang;
import lang.LangHolder;

/**
 *
 * @author RFK
 */
public class SMSRecomend implements ProgressStoppable, Runnable {

    String destinationAddress;
    String yourname;

    /** Creates a new instance of SMSRecomend */
    public SMSRecomend(String destinationAddress, String yourname) {
        this.destinationAddress=destinationAddress;
        this.yourname=yourname;
        new Thread(this).start();
    }

    public void run() {
        String address="sms://"+destinationAddress;
        //String address = "sms://:"  + smsPort;

        MessageConnection smsconn=null;
        try {
            /** Open the message connection. */
            smsconn=MapCanvas.map.smsW.smsconn; //(MessageConnection)Connector.open(address);

            TextMessage txtmessage=(TextMessage) smsconn.newMessage(
              MessageConnection.TEXT_MESSAGE, address);
            //txtmessage.setAddress(address);

            txtmessage.setPayloadText(LangHolder.getString(Lang.rectext)+"\n http://mapnav.spb.ru/wap.wml \n"+yourname);
            smsconn.send(txtmessage);

            MapCanvas.showmsg("SMS", LangHolder.getString(Lang.sent), AlertType.INFO, MapForms.mm.getListMore());
//#mdebug
            if (RMSOption.debugEnabled) {
                DebugLog.add2Log("SMS rec sent - OK");
            }
//#enddebug

        } catch (Throwable t) {
//#mdebug
            if (RMSOption.debugEnabled) {
                DebugLog.add2Log("SMS rec send:"+t.toString()+"\naddr:"+address);
            }
//#enddebug
            MapCanvas.showmsg(LangHolder.getString(Lang.error), t.toString()+"\n"+address, AlertType.ERROR, MapForms.mm.getListMore());

        }
    }

    public void setProgressResponse(ProgressResponse progressResponse) {
    }

    public boolean stopIt() {
        return true;
    }
}
