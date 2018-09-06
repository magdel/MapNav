
package misc;

import RPMap.MapCanvas;
import RPMap.MapUtil;
import RPMap.RMSOption;
import gpspack.GPSReader;
import javax.microedition.io.Connector;
import javax.microedition.lcdui.*;
import javax.wireless.messaging.*;
import lang.Lang;
import lang.LangHolder;

/**
 * Prompts for text and sends it via an SMS MessageConnection
 */
public class SMSSender
  implements CommandListener, Runnable {

    /** user interface command for indicating Send request */
    Command sendCommand=new Command(LangHolder.getString(Lang.send), Command.OK, 1);
    /** user interface command for going back to the previous screen */
    Command backCommand=new Command(LangHolder.getString(Lang.back), Command.BACK, 2);
    /** The port on which we send SMS messages */
    String smsPort;
    /** The URL to send the message to */
    String destinationAddress;
    /** Area where the user enters a message to send */
    TextBox messageBox;
    /** Where to return if the user hits "Back" */
    Displayable backScreen;
    /** Displayed when a message is being sent */
    Displayable sendingScreen;
    /**
     * Initialize the MIDlet with the current display object and
     * graphical components.
     */
    int type;

    public SMSSender(Displayable backScreen, Displayable sendingScreen, String dest, int type) {
        //bm.getAppProperty("SMS-Port");
        this.destinationAddress="sms://"+dest;
        this.backScreen=backScreen;
        this.sendingScreen=sendingScreen;
        this.type=type;
        messageBox=new TextBox(LangHolder.getString(Lang.entershtext), null, 40, TextField.ANY);
        messageBox.setString(RMSOption.lastSMSText);
        messageBox.addCommand(backCommand);
        messageBox.addCommand(sendCommand);
        messageBox.setCommandListener(this);
    }

    /**
     * Prompt for message and send it
     */
    public void promptAndSend(String asmsPort) {
        //this.destinationAddress = destinationAddress;
        smsPort=asmsPort;
        MapCanvas.setCurrent(messageBox);
    }

    /**
     * Respond to commands, including exit
     * @param c user interface command requested
     * @param s screen object initiating the request
     */
    public void commandAction(Command c, Displayable s) {

        if (c==backCommand){

            MapCanvas.setCurrent(backScreen);
            MapCanvas.ss=null;
        } else if (c==sendCommand){
            MapCanvas.setCurrentMap();
            try {
                RMSOption.lastSMSText=messageBox.getString();
                MapCanvas.map.rmss.writeSetting();
            } catch (Exception e) {
            }
            new Thread(this).start();
        }

    }

    /**
     * Send the message. Called on a separate thread so we don't have
     * contention for the display
     */
    public void run() {
        try {
            String address=(type==0)?destinationAddress:destinationAddress+":"+smsPort;
            //String address = "sms://:"  + smsPort;

            MessageConnection smsconn=null;
            try {
                /** Open the message connection. */
                if (type==0) {
                    smsconn=(MessageConnection) Connector.open(address);
                } else {
                    smsconn=MapCanvas.map.smsW.smsconn; //(MessageConnection)Connector.open(address);
                }
                TextMessage txtmessage=(TextMessage) smsconn.newMessage(
                  MessageConnection.TEXT_MESSAGE, address);
                //txtmessage.setAddress(address);
                String txt;
                if (MapCanvas.gpsBinded&&(GPSReader.NUM_SATELITES>0)){
                    txt=String.valueOf(((int) (GPSReader.LATITUDE*1000000))/1000000.)
                      +","+String.valueOf(((int) (GPSReader.LONGITUDE*1000000))/1000000.)
                      +",14,"+MapUtil.dateTime2Str(GPSReader.SAT_TIME_MILLIS, true)
                      +",\n"+messageBox.getString();
                } else {
                    txt=String.valueOf(((int) (MapCanvas.reallat*1000000))/1000000.)
                      +","+String.valueOf(((int) (MapCanvas.reallon*1000000))/1000000.)
                      +","+String.valueOf(MapCanvas.map.level)+","+MapUtil.dateTime2Str(System.currentTimeMillis(), true)
                      +",\n"+messageBox.getString();
                }
                txtmessage.setPayloadText(txt);
                smsconn.send(txtmessage);

                //   if (smsconn != null) {
                //     try {
                //       smsconn.close();
                //     } catch (IOException ioe) {}
                //   }
                MapCanvas.showmsg("SMS",
                  LangHolder.getString(Lang.sent),
                  AlertType.INFO, MapCanvas.map);
//#mdebug
                if (RMSOption.debugEnabled) {
                    DebugLog.add2Log("SMS sent - OK");
                }
//#enddebug

            } catch (Throwable t) {
//#mdebug
                if (RMSOption.debugEnabled) {
                    DebugLog.add2Log("SMS send:"+t.toString()+"\naddr:"+address);
                }
//#enddebug

                Alert a=new Alert(LangHolder.getString(Lang.error),
                  t.toString()+"\n"+address,
                  null, AlertType.ERROR);
                a.setTimeout(5000);
                MapCanvas.setCurrent(a, backScreen);
            }
            try {
                if (type==0) {
                    smsconn.close();
                }
            } catch (Throwable t) {
            }
        } finally {
            MapCanvas.ss=null;
        }
    }
}
