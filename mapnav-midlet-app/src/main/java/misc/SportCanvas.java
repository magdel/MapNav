/*
 * LocationCanvas.java
 *
 * Created on 25 ������ 2007 �., 23:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package misc;

import RPMap.MapCanvas;
import RPMap.MapPoint;
import RPMap.MapRoute;
import RPMap.MapUtil;
import RPMap.RMSOption;
import app.MapForms;
import gpspack.GPSLocationListener;
import gpspack.GPSReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import lang.Lang;
import lang.LangHolder;

/**
 *
 * @author Pavel Raev
 */
public class SportCanvas extends Canvas implements CommandListener, Runnable, GPSLocationListener, ProgressReadWritable {

    private String Current="Current";
    private String Current_time_="Current time: ";
    private String Dist="Dist";
    private String Lap_="Lap #";
    private String PAUSED="PAUSED";
    private String READY_TO_START="READY TO START";
    private String RUNNING="RUNNING";
    private String Sats_="Sats: ";
    private String Time="Time";
    private String minkm=" min/km";
    private boolean userRawMode;
    private String basis;

    /** Creates a new instance of SportCanvas */
    public SportCanvas(MapRoute startPoints) {

        if (startPoints.pts.size()!=2){
            throw new IllegalArgumentException("No points!");
        }
        startLat1=((MapPoint) startPoints.pts.elementAt(0)).lat;
        startLon1=((MapPoint) startPoints.pts.elementAt(0)).lon;
        startLat2=((MapPoint) startPoints.pts.elementAt(1)).lat;
        startLon2=((MapPoint) startPoints.pts.elementAt(1)).lon;
        basis="Start width: "+(MapUtil.distWithNameRound3(
          MapRoute.distBetweenCoords(startLat1, startLon1, startLat2, startLon2)));

        userRawMode=GPSReader.RAWDATA;
        GPSReader.RAWDATA=false;
        if (!MapCanvas.map.gpsLocListenersRaw.contains(this)){
            MapCanvas.map.gpsLocListenersRaw.addElement(this);
        }
        setFullScreenMode(RMSOption.fullScreen);
        addCommand(exitCommand);
        addCommand(setupCommand);
        addCommand(goCommand);
        addCommand(saveCommand);
        setCommandListener(this);
        minTemp=RMSOption.getDoubleOpt(RMSOption.DO_MIN_TEMP);
        maxTemp=RMSOption.getDoubleOpt(RMSOption.DO_MAX_TEMP);

        if (minTemp==0){
            minTemp=7;
        }
        if (maxTemp==0){
            maxTemp=5;
        }


        (new Thread(this)).start();

    }
    Command setupCommand=new Command(LangHolder.getString(Lang.options), Command.ITEM, 4);
    Command saveCommand=new Command(LangHolder.getString(Lang.save), Command.ITEM, 3);
    Command goCommand=new Command("Go!", Command.ITEM, 1);
    Command stopCommand=new Command("Stop", Command.ITEM, 2);
    Command exitCommand=new Command(LangHolder.getString(Lang.back), Command.BACK, 10);
    private final String[] aniStr={"\\", "|", "/", "-"};
    private int aniStrIndex;

    private void nextAniStrIndex() {
        aniStrIndex++;
        if (aniStrIndex>=aniStr.length){
            aniStrIndex=0;
        }
    }
    private int pageIndex;
    private String[] pages={" RUN ", " STAT "};
    private String gpsLost="No GPS data";

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
        g.drawString(SPORT_MODE+pages[pageIndex], dcx, 0, Graphics.HCENTER|Graphics.TOP);
        g.drawString(aniStr[aniStrIndex]+pointIndex, dmaxx-1, 0, Graphics.RIGHT|Graphics.TOP);
        g.fillTriangle(fh/4, fh/2, fh/2, fh*3/4, fh/2, fh/4);
        g.fillTriangle(fh/2+fh/2, fh/2, fh/2+fh/4, fh*3/4, fh/2+fh/4, fh/4);

        g.drawLine(0, dminy, dmaxx, dminy);
//        g.drawString(LangHolder.getString(Lang.points)+':'+' '+String.valueOf(ptscount), 0, dminy, Graphics.LEFT|Graphics.TOP);
        int sy=dminy+1;
        int dx1=1;
        int dx2=dmaxx/3+1;
        int dx3=dx2+dx2;
        if (pageIndex==0){
            g.setStrokeStyle(Graphics.DOTTED);
            g.drawString(basis, 0, sy, Graphics.LEFT|Graphics.TOP);
            //sy+=fh;
            //if (state==STATE_ACCELERATING) {
            if (GPSReader.NUM_SATELITES==0){
                g.drawString(gpsLost, dmaxx, sy, Graphics.RIGHT|Graphics.TOP);
                //}
            } else {
                g.drawString(Sats_+String.valueOf(GPSReader.NUM_SATELITES), dmaxx, sy, Graphics.RIGHT|Graphics.TOP);
            }
            sy+=fh+1;
            g.drawString(Current_time_+MapCanvas.map.getGPSTime(), 0, sy, Graphics.LEFT|Graphics.TOP);
            sy+=fh+1;

            int backColor=0xFFFF00;
            int frontColor=0x00;
            if (state==STATE_RUNNING){
                //color from temp
                backColor=0x20FF20;

                final double lastFourTemp=lastFourTemp(lastFourSpeed());
                if (lastFourTemp<maxTemp){
                    backColor=0xFFFF20;
                    frontColor=0xFF0000;
                } else if (lastFourTemp>minTemp){
                    backColor=0xFF0000;
                    frontColor=0xFFFF00;
                }
            }

            g.setColor(backColor);
            g.fillRect(0, sy, dmaxx, MapUtil.LARGEFONTB.getHeight());
            g.setFont(MapUtil.LARGEFONTB);
            g.setColor(frontColor);
            String cap=PAUSED;
            if (state==STATE_RUNNING){
                if (firstStartPassed){
                    cap=RUNNING;
                } else {
                    cap=READY_TO_START;

                }
            }

            g.drawString(cap, dcx, sy, Graphics.HCENTER|Graphics.TOP);
            sy+=MapUtil.LARGEFONTB.getHeight()+1;
            g.setColor(0xFFFF40);
            g.setFont(MapUtil.SMALLFONTB);

            g.drawString(MapUtil.speedWithNameRound1(lastFourSpeed()), 0, sy, Graphics.LEFT|Graphics.TOP);
            g.drawString(String.valueOf(MapUtil.speedRound1(lastFourTemp(lastFourSpeed())))+minkm, dmaxx, sy, Graphics.RIGHT|Graphics.TOP);

            sy+=fh+1;
            g.drawString(Current, 0, sy, Graphics.LEFT|Graphics.TOP);
            if (lapIndex>0){
                g.drawString(MapUtil.distWithNameRound2(lapDistance), dcx, sy, Graphics.HCENTER|Graphics.TOP);
                g.drawString(MapUtil.time2String(lapTime), dmaxx, sy, Graphics.RIGHT|Graphics.TOP);
            }
            sy+=fh+1;
        }

        g.drawString(Lap_, 0, sy, Graphics.LEFT|Graphics.TOP);
        g.drawString(Dist, dcx, sy, Graphics.HCENTER|Graphics.TOP);
        g.drawString(Time, dmaxx, sy, Graphics.RIGHT|Graphics.TOP);
        int lapI=(pageIndex==0)?lapIndex-4:lapIndex-16;
        if (lapI<1){
            lapI=1;
        }

        for (int i=lapI; i<lapIndex; i++) {
            sy+=fh+1;
            g.drawString(String.valueOf(i)+'('+MapUtil.speedRound1((double) (times[i]-times[i-1])/distances[i]/60000)+')', 0, sy, Graphics.LEFT|Graphics.TOP);
            g.drawString(MapUtil.distWithNameRound2(distances[i]), dcx, sy, Graphics.HCENTER|Graphics.TOP);
            g.drawString(MapUtil.time2String(times[i]-times[i-1]), dmaxx, sy, Graphics.RIGHT|Graphics.TOP);
        }

    }
    private String SPORT_MODE="Sport Mode";
    private Form setupForm;
    private TextField textMinName;
    private TextField textMaxName;
    /**
     * Min temp, in min/km
     */
    private double minTemp;
    private double maxTemp;

    private void showSetupForm() {
        setupForm=new Form(LangHolder.getString(Lang.options));
        textMinName=new TextField("Min temp, min/km", String.valueOf(MapUtil.doubleRound1(minTemp)), 4, TextField.DECIMAL);
        setupForm.append(textMinName);
        textMaxName=new TextField("Max temp, min/km", String.valueOf(MapUtil.doubleRound1(maxTemp)), 4, TextField.DECIMAL);
        setupForm.append(textMaxName);

        setupForm.setCommandListener(this);
        setupForm.addCommand(saveCommand);
        setupForm.addCommand(exitCommand);
        MapCanvas.setCurrent(setupForm);

    }
    private Form exitForm;
    private Command yesCommand;
    private Command noCommand;

    public void commandAction(Command command, Displayable displayable) {
        if (displayable==exitForm){
            if (command==yesCommand){
                active=false;
                try {
                    MapCanvas.map.gpsLocListenersRaw.removeElement(this);
                } finally {
                    GPSReader.RAWDATA=userRawMode;
                    MapForms.mm.back2Map();
                    System.gc();
                }
            } else {
                exitForm=null;
                yesCommand=null;
                noCommand=null;
                MapCanvas.setCurrent(this);
            }
        } else if (displayable==setupForm){
            if (command==exitCommand){
                MapCanvas.setCurrent(this);
            } else {

                resultsGathered=false;
                try {
                    double minT=Double.parseDouble(textMinName.getString());
                    double maxT=Double.parseDouble(textMaxName.getString());
                    if (minT<maxT){
                        double t=minT;
                        minT=maxT;
                        maxT=t;
                    }
                    RMSOption.setDoubleOpt(RMSOption.DO_MIN_TEMP, minT);
                    RMSOption.setDoubleOpt(RMSOption.DO_MAX_TEMP, maxT);
                    minTemp=RMSOption.getDoubleOpt(RMSOption.DO_MIN_TEMP);
                    maxTemp=RMSOption.getDoubleOpt(RMSOption.DO_MAX_TEMP);

                    MapCanvas.map.rmss.writeSettingNow();
                    MapCanvas.setCurrent(this);
                } catch (Throwable t) {
                    MapCanvas.showmsgmodal("Error", "Wrong number format: "+t.getMessage(), AlertType.ERROR, displayable);
                }
            }
        } else if (displayable==this){
            if (command==exitCommand){
                exitForm=new Form(LangHolder.getString(Lang.exit));
                exitForm.append(new StringItem(LangHolder.getString(Lang.exit)+" (Sport Mode)?", ""));
                yesCommand=new Command(LangHolder.getString(Lang.yes), Command.OK, 1);
                noCommand=new Command(LangHolder.getString(Lang.no), Command.CANCEL, 2);
                exitForm.addCommand(yesCommand);
                exitForm.addCommand(noCommand);
                exitForm.setCommandListener(this);
                MapCanvas.setCurrent(exitForm);

            } else if (command==setupCommand){
                showSetupForm();
            } else if (command==saveCommand){
                if (!resultsGathered){
                    MapCanvas.showmsg("Info", "Produce result first", AlertType.INFO, this);
                    return;
                }
                FileDialog.showSaveForm(LangHolder.getString(Lang.save),
                  new Item[0], this,
                  this, ".TXT");

            } else if (command==goCommand){
                removeCommand(goCommand);
                addCommand(stopCommand);
                clearData();
                state=STATE_RUNNING;
            } else if (command==stopCommand){
                state=STATE_PAUSED;
                removeCommand(stopCommand);
                addCommand(goCommand);
            }
        }
    }
    private boolean running=true;
    final private static byte KEY_PRESSED=1;
    final private static byte KEY_RELEASED=2;
    final private static byte KEY_REPEATED=3;

    protected void keyPressed(int keyCode) {
        keyEvent(KEY_PRESSED, keyCode);
    }

    public final void keyEvent(byte eventType, int key) {
        int gA;

        if (key==KEY_NUM5){
            //A
        }
        switch (gA=getGameAction(key)) {
            case FIRE: // '\004'
                //A
                break;
            case LEFT: // '\004'
                if (pageIndex>0){
                    pageIndex--;
                } else {
                    pageIndex=pages.length-1;
                }
                break;
            case RIGHT: // '\004'
                if (pageIndex==pages.length-1){
                    pageIndex=0;
                } else {
                    pageIndex++;
                }
                break;
        }
    }
    private long lastWarningTime;

    private void checkTempLimits() {
        long time=System.currentTimeMillis();
        if (time-lastWarningTime<10000){
            return;
        }
        final double lastFourTemp=lastFourTemp(lastFourSpeed());
        if (lastFourTemp>minTemp){
            lastWarningTime=time;
            MapSound.playSound(MapSound.DOWNSPEED);
        } else if (lastFourTemp<maxTemp){
            lastWarningTime=time;
            MapSound.playSound(MapSound.UPSPEED);
        }
    }

    private void clearData() {
        firstStartPassed=false;
        resultsGathered=false;
        lapIndex=0;
        lapDistance=0;
        lapTime=0;
        statTaken=MapUtil.dateTime2Str(System.currentTimeMillis(), false);
    }
    private boolean resultsGathered;
    private boolean active=true;
    private int state;
    private static final int STATE_PAUSED=0;
    private static final int STATE_RUNNING=1;
    private boolean firstStartPassed;

    public void run() {
        while (active) {
            if (running){

                switch (state) {
                    case STATE_PAUSED:

                        break;
                    case STATE_RUNNING:
                        checkTempLimits();
                        if (firstStartPassed){
                            safePointIndex=pointIndex;
                            if ((getDtsSafe(-1)-startTime>15000)&&findStartPass()){
                                if (lastIndex!=safePointIndex){
                                    double dist=MapRoute.distBetweenCoords(startLat, startLon, lastLat, lastLon);
                                    lapDistance+=dist;

                                    times[lapIndex]=startTime;
                                    distances[lapIndex]=lapDistance;
                                    lapIndex++;
                                    lastLat=startLat;
                                    lastLon=startLon;
                                    lastIndex=safePointIndex;
                                    lapDistance=0;
                                    lapTime=0;
                                    resultsGathered=true;
                                }
                            } else {
                                safePointIndex=pointIndex;
                                if (lastIndex!=safePointIndex){
                                    double clat=getLatSafe(-1);
                                    double clon=getLonSafe(-1);
                                    long time=getDtsSafe(-1);
                                    double dist=MapRoute.distBetweenCoords(clat, clon, lastLat, lastLon);
                                    lapDistance+=dist;
                                    lapTime=time-times[lapIndex-1];
                                    lastLat=clat;
                                    lastLon=clon;
                                    lastIndex=safePointIndex;
                                }
                            }

                        } else {
                            if (findStartPass()){
                                times[lapIndex]=startTime;
                                distances[lapIndex]=lapDistance;
                                lapIndex++;
                                lastLat=startLat;
                                lastLon=startLon;
                                lastIndex=pointIndex;
                                lapDistance=0;
                                lapTime=0;
                                firstStartPassed=true;
                                MapSound.playSound(MapSound.NEXTWPTSOUND);
                            }
                        }
                        break;
                }
            }

            nextAniStrIndex();
            repaint();
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                active=false;
            }
        }
    }

    private boolean findStartPass() {
        safePointIndex=pointIndex;

        int searchDepth=1;
        boolean keepLooking=true;
        long prevTime=0;
        while (keepLooking&&(searchDepth<5)) {
            searchDepth++;
            long time=getDtsSafe(-searchDepth);
            if (time==prevTime){
                break;
            }
            prevTime=time;
            double clat=getLatSafe(-searchDepth);
            double clon=getLonSafe(-searchDepth);
            double prevlat=getLatSafe(-searchDepth-3);
            double prevlon=getLonSafe(-searchDepth-3);
            if (MapRoute.distBetweenCoords(clat, clon, prevlat, prevlon)>0.003){
                keepLooking=!isConvex4(clon, clat, startLat1, startLon1, prevlat, prevlon, startLat2, startLon2);
                if (!keepLooking){
                    keepLooking=isDumbTr(startLat1, startLon1, clat, clon, startLat2, startLon2);
                    if (!keepLooking){
                        keepLooking=isDumbTr(startLat1, startLon1, prevlat, prevlon, startLat2, startLon2);
                        if (!keepLooking){
                            keepLooking=!isMediumCloser((startLat1+startLat2)/2.0d, (startLon1+startLon2)/2.0d,
                              clat, clon,
                              getLatSafe(-searchDepth-1), getLonSafe(-searchDepth-1),
                              getLatSafe(-searchDepth-2), getLonSafe(-searchDepth-2),
                              prevlat, prevlon);
                        }
                    }
                }
            }
        }
        if (!keepLooking){

            startLat=getLatSafe(-searchDepth-1);
            startLon=getLonSafe(-searchDepth-1);
            startTime=getDtsSafe(-searchDepth-1);
            return true;
        }
        return false;
    }
    private double startLat, startLon;
    private long startTime;
    //last data used
    private int lastIndex;
    private double lastLat, lastLon;
    private double lapDistance;
    private long lapTime;
//--stats
    private int lapIndex;
    /**
     * Дата/Время пересечения линии старта
     */
    private long[] times=new long[100];
    /**
     * Расстояние на каждом круге, без накопления
     */
    private double[] distances=new double[100];
//-- start line
    private double startLat1, startLon1, startLat2, startLon2;
    //gps stats
    private static final int MAX_POINTS=1000;
    private static double[] lats=new double[MAX_POINTS];
    private static double[] lons=new double[MAX_POINTS];
    private static float[] spds=new float[MAX_POINTS];
    private static long[] dts=new long[MAX_POINTS];
    private int pointIndex;
    long lastT;
    float lastSpd;

    public void gpsLocationAction(double lat, double lon, int alt, long time, float spd, float crs) {
        if (lastT+100>System.currentTimeMillis()){
            return;
        }
        //allocate();
        lats[pointIndex]=lat;
        lons[pointIndex]=lon;
        spds[pointIndex]=spd;
        dts[pointIndex]=time;
        pointIndex++;
        if (pointIndex==MAX_POINTS){
            pointIndex=0;
        }

        lastSpd=spd;
        lastT=System.currentTimeMillis();
        repaint();
    }
//    private double getDistBetweenPoints(int i1, int i2) {
//        return MapRoute.distBetweenCoords(getLatSafe(i1), getLonSafe(i1),
//          getLatSafe(i2), getLonSafe(i2));
//
//    }
    int safePointIndex;

    private double getSpeedSafe(int relPos) {
        int index=safePointIndex+relPos;
        if (index<0){
            index=index+MAX_POINTS;
        }
        if (index>=MAX_POINTS){
            index=index-MAX_POINTS;
        }
        return spds[index];

    }

    private double getLatSafe(int relPos) {
        int index=safePointIndex+relPos;
        if (index<0){
            index=index+MAX_POINTS;
        }
        if (index>=MAX_POINTS){
            index=index-MAX_POINTS;
        }
        return lats[index];
    }

    private double getLonSafe(int relPos) {
        int index=safePointIndex+relPos;
        if (index<0){
            index=index+MAX_POINTS;
        }
        if (index>=MAX_POINTS){
            index=index-MAX_POINTS;
        }
        return lons[index];
    }

    private long getDtsSafe(int relPos) {
        int index=safePointIndex+relPos;
        if (index<0){
            index=index+MAX_POINTS;
        }
        if (index>=MAX_POINTS){
            index=index-MAX_POINTS;
        }
        return dts[index];
    }

    private double lastTwoSpeed() {
        int pI=pointIndex-1;
        if (pI<0){
            pI=MAX_POINTS-1;
        }
        int pI2=pI-1;
        if (pI2<0){
            pI2=MAX_POINTS-1;
        }
        return (spds[pI]+spds[pI2])/2.0;
    }

    private int getPrevPoint(int pI) {
        int pI2=pI-1;
        if (pI2<0){
            pI2=MAX_POINTS-1;
        }
        return pI2;
    }

    private double avgSpeed() {
        int cI=getPrevPoint(pointIndex);
        int cIend=cI;
        long time=dts[cI];
        int itC=22;
        while ((time-dts[cI]<3500)&&(itC>0)) {
            cI=getPrevPoint(cI);
            itC--;
        }
        if (cI==cIend) {
            return 0;
        }
        double dist=MapRoute.distBetweenCoords(lats[cI], lons[cI], lats[cIend], lons[cIend]);
        double dt=dts[cIend]-dts[cI];
        double spd=dist/dt*1000*60*60;
        return spd;
    }

    private double lastFourSpeed() {
        return avgSpeed();
//        int pI=pointIndex-1;
//        if (pI<0){
//            pI=MAX_POINTS-1;
//        }
//        int pI2=pI-1;
//        if (pI2<0){
//            pI2=MAX_POINTS-1;
//        }
//        int pI3=pI2-1;
//        if (pI3<0){
//            pI3=MAX_POINTS-1;
//        }
//        int pI4=pI3-1;
//        if (pI4<0){
//            pI4=MAX_POINTS-1;
//        }
//        return (spds[pI]+spds[pI2]+spds[pI3]+spds[pI4])/4.0d;
    }

    private double lastFourTemp(double speed) {
        if (speed<1){
            return 60.0d;
        }
        return 60.0d/speed;
    }
    private String statTaken;

    private void writeReport(OutputStream os) throws IOException {
        Util.writeStr2OS(os, "MapNav ("+MapUtil.version+") Sport Mode results\r\n");
        Util.writeStr2OS(os, "Statistics are taken "+statTaken+" for "+RMSOption.netRadarLogin+"\r\n");
        Util.writeStr2OS(os, "--------\r\n");
        Util.writeStr2OS(os, "Lap #\tDist\tTime\t\tTemp\r\n");
        long totalTime=0;
        double totalDist=0;
        for (int i=1; i<lapIndex; i++) {
            Util.writeStr2OS(os, ""+i+"\t"+MapUtil.distWithNameRound2(distances[i])+"\t"
              +MapUtil.time2String(times[i]-times[i-1])
              +"\t"+MapUtil.speedRound1((double) (times[i]-times[i-1])/distances[i]/60000)
              +"\r\n");
            totalTime+=times[i]-times[i-1];
            totalDist+=distances[i];
        }
        Util.writeStr2OS(os, "--------\r\n");
        Util.writeStr2OS(os, "Total\t"+MapUtil.distWithNameRound2(totalDist)+"\t"
          +MapUtil.time2String(totalTime)
          +"\t"+MapUtil.speedRound1((double) (totalTime)/totalDist/60000)
          +"\r\n");
    }

    public void writeData(OutputStream os, Item[] items) throws IOException {
        writeReport(os);
    }

    public void readData(InputStream is, Item[] items) throws IOException {
    }

    public void setProgressResponse(ProgressResponse progressResponse) {
    }

    public boolean stopIt() {
        return true;

    }

    /**
     * check for dumb triangle
     * @param xa x base 1
     * @param ya y base 1
     * @param xb x vert
     * @param yb y vert
     * @param xc x base 2
     * @param yc y base 2
     * @return is dumb
     */
    private boolean isDumbTr(double xa, double ya, double xb, double yb, double xc, double yc) {
        double ab=MapRoute.distBetweenCoords(xa, ya, xb, yb);
        double bc=MapRoute.distBetweenCoords(xc, yc, xb, yb);
        double ac=MapRoute.distBetweenCoords(xc, yc, xa, ya);
        if (ab<0.0005){
            return true;
        }
        if (bc<0.0005){
            return true;
        }
        if (ac<0.0005){
            return true;
        }
        //2*2/1*1+6*6= 49/37=
        double rel=ab*ab/(ac*ac+bc*bc);
        if (rel>1.2){
            return true;
        }
        rel=bc*bc/(ac*ac+ab*ab);
        if (rel>1.2){
            return true;
        }
        return false;
    }

    private boolean isConvex4(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        if (isClockWise(x1, y1, x2, y2, x3, y3)){
            return isClockWise(x2, y2, x3, y3, x4, y4)
              &&isClockWise(x3, y3, x4, y4, x1, y1)
              &&isClockWise(x4, y4, x1, y1, x2, y2);
        } else {
            return !isClockWise(x2, y2, x3, y3, x4, y4)
              &&!isClockWise(x3, y3, x4, y4, x1, y1)
              &&!isClockWise(x4, y4, x1, y1, x2, y2);
        }
    }

    private boolean isClockWise(double x1, double y1, double x2, double y2, double x3, double y3) {
        return ((x2-x1)*(y3-y1)-(x3-x1)*(y2-y1))<0;
    }

    private boolean isMediumCloser(double centerLat, double centerLon,
      double clat, double clon,
      double latSafe, double lonSafe,
      double latSafe2, double lonSafe2,
      double prevlat, double prevlon) {
        double dc=MapRoute.distBetweenCoords(centerLat, centerLon, clat, clon);
        double ds=MapRoute.distBetweenCoords(centerLat, centerLon, latSafe, lonSafe);
        double ds2=MapRoute.distBetweenCoords(centerLat, centerLon, latSafe2, lonSafe2);
        double dp=MapRoute.distBetweenCoords(centerLat, centerLon, prevlat, prevlon);

        return ((ds<=dc)||(ds<=dp))&&((ds2<=dp)||(ds2<=dc));

    }
    //private String importantNote="   Important note!\nGT Mode allows you to measure acceleration automatically. Just stop and wait for message "+
    // "READY TO START ";
}
