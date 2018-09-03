package gpspack;

import RPMap.MapCanvas;
import RPMap.MapUtil;
import RPMap.RMSOption;
import app.MapForms;
import javax.microedition.lcdui.*;
import java.util.*;
import javax.microedition.io.StreamConnection;
import lang.Lang;
import lang.LangHolder;
//#debug
//# import misc.DebugLog;

public class BTCanvas extends Canvas implements Runnable, CommandListener {

    public BTConnector btConnect; // class used to get the bluetooth devices.
    private boolean active=true; // active while thread is running.
    private boolean stopped;
    private String[] deviceNames; // An array with the device names
    int index=-1; // Keep track of the bluetooth device that is selected.
    private int midIndex;
    private final int WIDTH, HEIGHT; // Height and width of the canvas.
    // used for the connection animation
    private boolean connecting=false;
    private int step;
    private int maxstep=10;
    // Used for the device searching animation
    private int searchAnimDeg;
    //private float []sinTable = new float[640];
    public boolean autoSrch;

    public BTCanvas(boolean gps) {

        WIDTH=getWidth();
        HEIGHT=getHeight();
        //connectX = WIDTH/2 - 40;

        midIndex=((HEIGHT-20)/20)/2;
        //System.out.println("midIndex=" + midIndex);

        addCommand(exitCommand);
        addCommand(connCommand);
        setCommandListener(this);
        btConnect=new BTConnector(this, gps);

        tC=new Thread(this);
        //t.setPriority(Thread.MAX_PRIORITY);
        tC.start();

    }
    Thread tC;
    Command connCommand=new Command(LangHolder.getString(Lang.select), Command.ITEM, 1);
    Command exitCommand=new Command(LangHolder.getString(Lang.back), Command.BACK, 99);

    public void startSrch() {
        index=-1;
        connecting=false;
        btConnect.canvas=this;
        btConnect.startSearch();
    }

    public void stop() {
        stopped=true;
        if (btConnect!=null){
            btConnect.cancelInquiry(true);
        }
        btConnect=null;
        //  try{if (tC!=null)if (tC.isAlive())tC.interrupt();}catch(Throwable t){}
        tC=null;
    }

    public void commandAction(Command command, Displayable displayable) {
        if (displayable==this){
            if (command==exitCommand){
                stopped=true;
                MapCanvas.map.endGPSLookup(false);
                //MapMidlet.mm.back2Map();
                MapCanvas.setCurrent(MapForms.mm.getListBT());
            } else if (command==connCommand){
                if (//btConnect.doneSearching()&&
                  (!connecting)&&(index>=0)){
                    btConnect.connect(index);
                    connecting=true;
                } else {
                    // if (!btConnect.doneSearching())
                    //   MapCanvas.showmsg(LangHolder.getString(Lang.attention),LangHolder.getString(Lang.waitbtend),AlertType.WARNING,this);
                    // else
                    if (index<0){
                        MapCanvas.setCurrent(MapForms.getSelectWarningAlert(), this);
                    }
                }

//        else if(connecting){
//          startSrch();
//        }
            }

        }
    }

    public static boolean checkBluetoothOn() {
        return true;//BTConnector.isBlueOn(); ���� ��������
    }
    private static int xd, yd, br=255;
    private long lastT;
    private Random random=new Random();
//#debug
//#     float pp=0;

    public void paint(Graphics g) {

        int dminx=0;
        int dminy=0+g.getFont().getHeight();
        int dmaxx=getWidth();
        int dmaxy=getHeight();
        int dcx=(dminx+dmaxx)/2;
        int dcy=(dminy+dmaxy)/2;

        int sr=Math.min((dmaxx-dminx), (dmaxy-dminy))/3;

        g.setColor(0x0);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(0xFFFFFF);
        try {
//#debug
//#             pp=1;
            if (btConnect==null){
                return;
            }
            deviceNames=btConnect.getDeviceNames();
//#debug
//#             pp=2;

            // Display all the devices that are found by the BTConnector.
            if (!connecting){
                for (int i=0; i<btConnect.getSize(); i++) {
                    int offset=0;
                    if (btConnect.getSize()*20>HEIGHT-40){
                        if (index>midIndex){
                            offset=(index-midIndex)*20;
                        }
                    }
                    if (deviceNames[i]!=null){
                        if (i==index){
                            g.setColor(0x008000);
                            g.drawString(deviceNames[i], 0+1, 1+20+i*20-offset, 0);
                            g.setColor(0x00FF00);
                            g.drawString(deviceNames[i], 0, 20+i*20-offset, 0);
                            g.setColor(0xFFFFFF);
                        } else {
                            g.drawString(deviceNames[i], 0, 20+i*20-offset, 0);
                        }
                    }
                }
//#debug
//#                 pp=3;

                g.setColor(0x0);
                g.fillRect(0, 0, WIDTH, 20);
                g.setColor(0xFFFFFF);
                if (!btConnect.doneSearchingDevices()){
                    g.drawString(LangHolder.getString(Lang.searchgoes), 0, 0, Graphics.LEFT|Graphics.TOP);
                } else {
                    g.drawString(LangHolder.getString(Lang.searchdone), 0, 0, Graphics.LEFT|Graphics.TOP);
                }
//#debug
//#                 pp=4;

                // Paint a little animation while searching for devices.
                if (!btConnect.doneSearchingDevices()){

                    searchAnimDeg=searchAnimDeg>=360?searchAnimDeg-360:searchAnimDeg+3;
//#debug
//#                     pp=5;

                    if (br>=255){
                        br=0;
                        xd=random.nextInt(sr-7)+7;
                        yd=searchAnimDeg;
                    }
                    br=br+5;
//#debug
//#                     pp=6;

                    int x1=(int) (dcx+sr*Math.sin(Math.toRadians(searchAnimDeg)));
                    int y1=(int) (dcy+sr*Math.cos(Math.toRadians(searchAnimDeg)));
//#debug
//#                     pp=7;

//int x2 = (int)sinTable[searchAnimDeg + 180] + centerX;
                    //int y2 = (int)sinTable[searchAnimDeg + 180 +90] + centerY;
                    g.drawLine(dcx, dcy, x1, y1);
                    int ar=sr/4;
                    g.drawArc(dcx-ar, dcy-ar, ar+ar, ar+ar, searchAnimDeg-90, -20);
                    ar=2*sr/4;
                    g.drawArc(dcx-ar, dcy-ar, ar+ar, ar+ar, searchAnimDeg-90, -20);
                    ar=3*sr/4;
                    g.drawArc(dcx-ar, dcy-ar, ar+ar, ar+ar, searchAnimDeg-90, -20);
                    ar=sr;
                    g.drawArc(dcx-ar, dcy-ar, ar+ar, ar+ar, searchAnimDeg-90, -20);
//#debug
//#                     pp=8;
                    //0123456789ABCDEF
                    //    4  7  A  D

                    g.setColor(0xD0D0D0);
                    ar=sr/4;
                    g.drawArc(dcx-ar, dcy-ar, ar+ar, ar+ar, searchAnimDeg-90-20, -30);
                    ar=2*sr/4;
                    g.drawArc(dcx-ar, dcy-ar, ar+ar, ar+ar, searchAnimDeg-90-20, -30);
                    ar=3*sr/4;
                    g.drawArc(dcx-ar, dcy-ar, ar+ar, ar+ar, searchAnimDeg-90-20, -30);
                    ar=sr;
                    g.drawArc(dcx-ar, dcy-ar, ar+ar, ar+ar, searchAnimDeg-90-20, -30);

                    g.setColor(0xA0A0A0);
                    ar=sr/4;
                    g.drawArc(dcx-ar, dcy-ar, ar+ar, ar+ar, searchAnimDeg-90-50, -40);
                    ar=2*sr/4;
                    g.drawArc(dcx-ar, dcy-ar, ar+ar, ar+ar, searchAnimDeg-90-50, -40);
                    ar=3*sr/4;
                    g.drawArc(dcx-ar, dcy-ar, ar+ar, ar+ar, searchAnimDeg-90-50, -40);
                    ar=sr;
                    g.drawArc(dcx-ar, dcy-ar, ar+ar, ar+ar, searchAnimDeg-90-50, -40);

                    g.setColor(0x707070);
                    ar=sr/4;
                    g.drawArc(dcx-ar, dcy-ar, ar+ar, ar+ar, searchAnimDeg-90-90, -30);
                    ar=2*sr/4;
                    g.drawArc(dcx-ar, dcy-ar, ar+ar, ar+ar, searchAnimDeg-90-90, -30);
                    ar=3*sr/4;
                    g.drawArc(dcx-ar, dcy-ar, ar+ar, ar+ar, searchAnimDeg-90-90, -30);
                    ar=sr;
                    g.drawArc(dcx-ar, dcy-ar, ar+ar, ar+ar, searchAnimDeg-90-90, -30);

                    g.setColor(0x404040);
                    ar=sr/4;
                    g.drawArc(dcx-ar, dcy-ar, ar+ar, ar+ar, searchAnimDeg-90-120, -20);
                    ar=2*sr/4;
                    g.drawArc(dcx-ar, dcy-ar, ar+ar, ar+ar, searchAnimDeg-90-120, -20);
                    ar=3*sr/4;
                    g.drawArc(dcx-ar, dcy-ar, ar+ar, ar+ar, searchAnimDeg-90-120, -20);
                    ar=sr;
                    g.drawArc(dcx-ar, dcy-ar, ar+ar, ar+ar, searchAnimDeg-90-120, -20);

                    g.setColor(br+(br<<8)+(br<<16));
                    g.drawArc((int) (dcx+xd*Math.sin(Math.toRadians(yd))-2), (int) (dcy+xd*Math.cos(Math.toRadians(yd))-2), 4, 4, 0, 360);


                }
//#debug
//#                 pp=9;

                // Paint a connecting animation when searching for a bluetooth service.
            } else {
//#debug
//#                 pp=10;
                g.drawString(LangHolder.getString(Lang.gpsautoconn), WIDTH/2, HEIGHT/2-12, Graphics.HCENTER|Graphics.BOTTOM);
//#debug
//#                 pp=11;

                int dcy1=getHeight()/4;

                int dy=dcy/6;
                maxstep=dy;
                int dc=(dcx+dcx/2)/dy;
                int dl=dcx-(dc*dy)/2;
                g.clipRect(dl, dcy, dc*dy, dy);
                for (int i=-1; i<=dc; i++) {
                    g.setColor(0xFFFFFF);
                    g.drawLine(dl+step+i*dy, dcy, dl+step+i*dy-dy, dcy+dy);
                    g.setColor(0xC0C0C0);
                    g.drawLine(dl+step+i*dy-1, dcy, dl+step+i*dy-1-dy, dcy+dy);
                    g.drawLine(dl+step+i*dy+1, dcy, dl+step+i*dy+1-dy, dcy+dy);
                    g.setColor(0x808080);
                    g.drawLine(dl+step+i*dy-2, dcy, dl+step+i*dy-2-dy, dcy+dy);
                    g.drawLine(dl+step+i*dy+2, dcy, dl+step+i*dy+2-dy, dcy+dy);
                    g.setColor(0x404040);
                    g.drawLine(dl+step+i*dy-3, dcy, dl+step+i*dy-3-dy, dcy+dy);
                    g.drawLine(dl+step+i*dy+3, dcy, dl+step+i*dy+3-dy, dcy+dy);
                }
                g.setClip(0, 0, getWidth(), getHeight());
                g.setColor(0xFFFFFF);
                g.drawRect(dl, dcy, dc*dy, dy);
                g.drawRect(dl-1, dcy-1, dc*dy+2, dy+2);
                if (lastT+100<System.currentTimeMillis()){
                    step=step==maxstep-1?0:step+1;
                    lastT=System.currentTimeMillis();
                }

                /*        g.drawRect(WIDTH/2-40, HEIGHT/2-10, 80, 20);
                g.fillRect(connectX, HEIGHT/2-10, 5, 20);
                //g.fillArc(connectX, HEIGHT/2-10, 5, 20);
                connectX = connectX >=WIDTH/2+40?WIDTH/2-45:connectX+1;
                //#debug
//#                 pp = 12;

                g.setColor(0xFFFFFF);
                g.fillRect(WIDTH/2-45, HEIGHT/2-10, 5, 21);
                g.fillRect(WIDTH/2+41, HEIGHT/2-10, 5, 21);
                g.setColor(0x000000);
                //#debug
//#                 pp = 13;
                 */
                //g.drawString("Exit", 0, HEIGHT, Graphics.LEFT | Graphics.BOTTOM);
                //g.drawString("Back...", WIDTH, HEIGHT, Graphics.RIGHT | Graphics.BOTTOM);
            }
        } catch (Throwable t) {
//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("BTC paint:"+pp+" "+t);
//#             }
//#enddebug
        }
    }

    public void keyPressed(int key) {
        switch (getGameAction(key)) {
            case UP: // '\002'
                index=index>0?index-1:index;
                break;
            case DOWN: // '\b'
                index=index<btConnect.getSize()-1?index+1:index;
                break;

            case FIRE:
                commandAction(connCommand, this);
                //    if (mode==MAPMODE)
                //      gpsFixed = !gpsFixed;
                break;
            default:
                break;

        }
    }

    public void run() {
        try {
            Thread.sleep(300);
//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("BTC started");
//#             }
//#enddebug
            // boolean found=false;
            while (active&!stopped) {
                repaint();

                // If a connection do a device is completed and a bt service is found
                if (btConnect==null){
                    break;
                }

                if (btConnect.doneSearchingServices()&&(!urlGPS.equals(MapUtil.emptyString))){
                    active=false;
                }

                Thread.sleep(10);

            }

            // start the gps reading.
            //GPS gps = (GPS)midlet;
//#mdebug
//#             if (!stopped){
//#                 if (RMSOption.debugEnabled){
//#                     DebugLog.add2Log("BTC startGPS");
//#                 }
//#             }
//#enddebug

            //if (stopped) btConnect=null;

            if (!stopped){
                MapForms.mm.showBTForm(urlGPS, RMSOption.lastBTDeviceName, true);
            }

//        gps.startGPS();
        } catch (Throwable t1) {
//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("BTC run:"+t1);
//#             }
//#enddebug
        }
    }
    String urlGPS=MapUtil.emptyString;
}
