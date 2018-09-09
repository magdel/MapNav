/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package misc;

import RPMap.MapCanvas;
import RPMap.MapUtil;
import RPMap.RMSOption;
import app.MapForms;
import gpspack.GPSReader;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextField;
import lang.Lang;
import lang.LangHolder;

/**
 *
 * @author rfk
 */
public class TR102Sender extends Canvas implements CommandListener, Runnable {

    public String ES="";

    public static void showTR102Sender() {
        if (tr102==null){
            tr102=new TR102Sender();
        }
        MapCanvas.setCurrent(tr102);
    }
    private static TR102Sender tr102;
    private String zeroDate="000000";

    private TR102Sender() {
        //setFullScreenMode(RMSOption.fullScreen);
        addCommand(exitCommand);
        addCommand(setupCommand);
        addCommand(startCommand);
        setCommandListener(this);
    }
    Command setupCommand=new Command(LangHolder.getString(Lang.options), Command.ITEM, 3);
    Command stopCommand=new Command(LangHolder.getString(Lang.off), Command.ITEM, 1);
    Command startCommand=new Command(LangHolder.getString(Lang.on), Command.ITEM, 1);
    Command saveCommand=new Command(LangHolder.getString(Lang.save), Command.ITEM, 1);
    Command exitCommand=new Command(LangHolder.getString(Lang.back), Command.BACK, 10);
    private boolean active=false;
    private Form setupForm;
    private TextField textServerAddress;
    private TextField textIMEI;
    private ChoiceGroup choiceTiming;

    private void disconnect() {
        try {
            os.close();
        } catch (Throwable tt) {
        }
        try {
            is.close();
        } catch (Throwable tt) {
        }
        try {
            conn.close();
        } catch (Throwable tt) {
        }
        conn=null;
        is=null;
        os=null;
    }
    SocketConnection conn;
    InputStream is;
    OutputStream os;
    private boolean sended;

    private void sendCoordinates() {

//                    String data1="$355632000166323,1,1,040202,093633,E12129.2252,N2459.8891,00161,0.0100,147,07";
//            byte[] rawdata1 = data1.getBytes();
//            int cs1 = MapUtil.checkSum(rawdata1);
//            String checkSum1 = "*33";


        if (conn==null){
            try {
                conn=(SocketConnection) Connector.open("socket://"+RMSOption.getStringOpt(RMSOption.SO_TR102_SERVER), Connector.READ_WRITE);
                is=conn.openDataInputStream();
                os=conn.openDataOutputStream();
            } catch (Throwable t) {
                disconnect();
            }
        }


        try {
            while (is.available()>0) {
                is.read();
            }
            //$355632001072033,1,2,090707,163926,E03018.9235,N5955.3428,-17.8,2.06,265.83,03*37
            if (GPSReader.NUM_SATELITES==0){
                lastSendTime=System.currentTimeMillis()+sendDelay(RMSOption.getByteOpt(RMSOption.BO_TR102_INTERVAL));
                return;
            }
            String date=MapCanvas.map.getGPSDate();
            if (date.equals(ES)){
                date=zeroDate;
            } else {
                //2009-03-27
                date=date.substring(8, 10)+date.substring(5, 7)+date.substring(2, 4);
            }
            String time=MapCanvas.map.getGPSTime();
            if (time.equals(ES)){
                time=zeroDate;
            } else {
                //23:43:50
                time=time.substring(0, 2)+time.substring(3, 5)+time.substring(6, 8);
            }

            String lon=MapUtil.coordToString(GPSReader.LONGITUDE, MapUtil.CLONTYPE, RMSOption.COORDGGGMMMMMMTYPE);
            String lat=MapUtil.coordToString(GPSReader.LATITUDE, MapUtil.CLATTYPE, RMSOption.COORDGGGMMMMMMTYPE);
            String alt=MapUtil.numStr(GPSReader.ALTITUDE, 5);
            String spdmph=""+MapUtil.speedRound1(GPSReader.SPEED_KMH/1.852);
            String crs=MapUtil.numStr(GPSReader.COURSE_I, 3);
//$355632001072033,1,2,090707,163926,E03018.9235,N5955.3428,-17.8,2.06,265.83,03*37


            String data="$"+RMSOption.getStringOpt(RMSOption.SO_TR102_IMEI)+",1,"+
              GPSReader.POSFIX+","+date+","+time+","+lon+","+lat+","+alt+","+
              spdmph+","+crs+","+MapUtil.numStr(GPSReader.NUM_SATELITES, 2);
            //data="$355632000166323,1,1,040202,093633,E12129.2252,N2459.8891,00161,0.0100,147,07";
            //data="$355632004158201,1,3,170608,154628,E02807.6645,N5445.5751,193.6,52.5,231.77,06";
            byte[] rawdata=data.getBytes();

            String checkSum="*37!";

            os.write(rawdata);
            os.write(checkSum.getBytes());
            os.flush();
            sended=true;
        } catch (Throwable t) {
            sended=false;
            disconnect();
        }
    }

    private int checkSum(byte[] data) {
        byte sum=0;
        //int astpos=0;
        for (int i=1; i<data.length; i++) {
            sum=(byte) (sum^data[i]);

        }
        return sum;
    }

    private void showSetupForm() {
        setupForm=new Form(LangHolder.getString(Lang.options));
        textServerAddress=new TextField("Server address (host:port)", RMSOption.getStringOpt(RMSOption.SO_TR102_SERVER), 30, TextField.ANY);
        setupForm.append(textServerAddress);
        textIMEI=new TextField("IMEI", RMSOption.getStringOpt(RMSOption.SO_TR102_IMEI), 30, TextField.ANY);
        setupForm.append(textIMEI);
        Image[] ia=new Image[8];
        String[] sa={"10 sec", "30 sec", "1 min", "2 min", "5 min", "10 min", " 20 min", "30 min"};
        choiceTiming=new ChoiceGroup("Send interval", ChoiceGroup.EXCLUSIVE, sa, ia);
        choiceTiming.setSelectedIndex(RMSOption.getByteOpt(RMSOption.BO_TR102_INTERVAL), true);
        setupForm.append(choiceTiming);

        setupForm.setCommandListener(this);
        setupForm.addCommand(saveCommand);
        setupForm.addCommand(exitCommand);
        MapCanvas.setCurrent(setupForm);

    }

    public void commandAction(Command command, Displayable displayable) {
        if (displayable==setupForm){
            if (command==exitCommand){
                MapCanvas.setCurrent(this);
            } else {
                MapCanvas.setCurrent(this);
                RMSOption.setStringOpt(RMSOption.SO_TR102_SERVER, textServerAddress.getString());
                RMSOption.setStringOpt(RMSOption.SO_TR102_IMEI, textIMEI.getString());
                RMSOption.setByteOpt(RMSOption.BO_TR102_INTERVAL, (byte) choiceTiming.getSelectedIndex());
                MapCanvas.map.rmss.writeSettingNow();
            }
        } else if (displayable==this){
            if (command==exitCommand){
                if (!active){
                    tr102=null;
                }
                MapForms.mm.back2Map();
            } else if (command==setupCommand){
                showSetupForm();
            } else if (command==startCommand){
                active=true;
                (new Thread(this)).start();
                removeCommand(startCommand);
                addCommand(stopCommand);
            } else if (command==stopCommand){
                active=false;
                removeCommand(stopCommand);
                addCommand(startCommand);
            }
        }
    }

    private void nextAniStrIndex() {
        aniStrIndex++;
        if (aniStrIndex>=aniStr.length){
            aniStrIndex=0;
        }
    }

    private long sendDelay(byte interval) {
        if (interval==0){
            return 10000;
        } else if (interval==1){
            return 30000;
        } else if (interval==2){
            return 60000;
        } else if (interval==3){
            return 60000;
        } else if (interval==4){
            return 120000;
        } else if (interval==5){
            return 300000;
        } else if (interval==6){
            return 600000;
        } else if (interval==7){
            return 1200000;
        } else if (interval==8){
            return 1800000;
        } else {
            return 3000000;
        }
    }
    private long lastSendTime;

    public void run() {
        try {
            while (active) {
                if (System.currentTimeMillis()-lastSendTime>sendDelay(RMSOption.getByteOpt(RMSOption.BO_TR102_INTERVAL))){
                    sendCoordinates();
                    lastSendTime=System.currentTimeMillis();
                }
                nextAniStrIndex();
                repaint();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    active=false;
                }
            }
        } catch (Throwable t) {
        }
        disconnect();
    }

    protected void paint(Graphics g) {

        g.setFont(MapUtil.SMALLFONTB);
        int fh=g.getFont().getHeight();
        int dminx=0;
        int dminy=fh;
        int dmaxx=getWidth();
        int dmaxy=getHeight();
        int dcx=(dminx+dmaxx)/2;
        //int dcy=(dminy+dmaxy)/2;

        //int sr=Math.min((dmaxx-dminx), (dmaxy-dminy))/3;

        g.setColor(0x0);
        g.fillRect(0, 0, dmaxx, dmaxy);
        g.setColor(0xFFFF40);
//    g.setColor(0xFFFFFF);
        g.drawString(TR_MODE, dcx, 0, Graphics.HCENTER|Graphics.TOP);
        if (sended){
            g.setColor(0x20FF20);
        } else {
            g.setColor(0xFF2040);
        }
        g.drawString(aniStr[aniStrIndex], dmaxx-1, 0, Graphics.RIGHT|Graphics.TOP);
        g.setColor(0xFFFF40);
        g.fillTriangle(fh/4, fh/2, fh/2, fh*3/4, fh/2, fh/4);
        g.fillTriangle(fh/2+fh/2, fh/2, fh/2+fh/4, fh*3/4, fh/2+fh/4, fh/4);

        g.drawLine(0, dminy, dmaxx, dminy);
//        g.drawString(LangHolder.getString(Lang.points)+':'+' '+String.valueOf(ptscount), 0, dminy, Graphics.LEFT|Graphics.TOP);
        int sy=dminy+1;
        int dx1=1;
        int dx2=dmaxx/3+1;
        int dx3=dx2+dx2;

        //g.setStrokeStyle(Graphics.DOTTED);
        g.drawString("Server: "+RMSOption.getStringOpt(RMSOption.SO_TR102_SERVER), 0, sy, Graphics.LEFT|Graphics.TOP);
        sy+=fh;

        g.drawString("IMEI: "+RMSOption.getStringOpt(RMSOption.SO_TR102_IMEI), 0, sy, Graphics.LEFT|Graphics.TOP);
        sy+=fh;
//if (state==STATE_ACCELERATING) {
        if (active){
            g.drawString("Left to send: "+(sendDelay(RMSOption.getByteOpt(RMSOption.BO_TR102_INTERVAL))-(System.currentTimeMillis()-lastSendTime))/1000+" sec", 0, sy, Graphics.LEFT|Graphics.TOP);
        }
    }
    private final String[] aniStr={"\\", "|", "/", "-"};
    private int aniStrIndex;
    private String TR_MODE="TR-102 Mode";
}
