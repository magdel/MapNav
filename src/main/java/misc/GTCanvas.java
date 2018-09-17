/*
 * LocationCanvas.java
 *
 * Created on 25 ������ 2007 �., 23:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package misc;

import RPMap.FontUtil;
import RPMap.MapCanvas;
import RPMap.MapRoute;
import RPMap.MapUtil;
import RPMap.RMSOption;
import app.MapForms;
import camera.MD5;
import gpspack.GPSLocationListener;
import gpspack.GPSReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import lang.Lang;
import lang.LangHolder;
import netradar.NetRadar;

/**
 *
 * @author Pavel Raev
 */
public class GTCanvas extends Canvas implements CommandListener, Runnable, GPSLocationListener, ProgressReadWritable {

    private boolean userRawMode;
    private static boolean disclaimerShown;

    /** Creates a new instance of LocationCanvas */
    public GTCanvas() {
        userRawMode=GPSReader.RAWDATA;
        GPSReader.RAWDATA=true;
        if (!MapCanvas.map.gpsLocListenersRaw.contains(this)){
            MapCanvas.map.gpsLocListenersRaw.addElement(this);
        }
        setFullScreenMode(RMSOption.fullScreen);
        addCommand(exitCommand);
        addCommand(setupCommand);
        addCommand(saveCommand);
        addCommand(sendCommand);
        setCommandListener(this);

        testMaxSpeed=statSpdTimeFor[RMSOption.getByteOpt(RMSOption.BO_GT_SPD)];
        testMaxDistance=statDistTimeFor[RMSOption.getByteOpt(RMSOption.BO_GT_DIST)];

        V0_100=""+((int) testMaxSpeed)+"km/h";//LangHolder.getString(Lang.kmh);
        S0_400=""+((int) 1000*testMaxDistance)+"m";//LangHolder.getString(Lang.m);
        if (disclaimerShown){
            state=STATE_OTHER;
        } else {
            state=STATE_NEEDINFO;
        }
        disclaimerShown=true;
        (new Thread(this)).start();

    }
    private Command setupCommand=new Command(LangHolder.getString(Lang.options), Command.ITEM, 3);
    private Command saveCommand=new Command(LangHolder.getString(Lang.save), Command.ITEM, 2);
    private Command sendCommand=new Command(LangHolder.getString(Lang.send), Command.ITEM, 1);
    private Command exitCommand=new Command(LangHolder.getString(Lang.back), Command.BACK, 10);
    private boolean active=true;
    private boolean screenActive=false;
    private static final int MAX_POINTS=1000;
    private static double[] lats=new double[MAX_POINTS];
    private static double[] lons=new double[MAX_POINTS];
    private static float[] spds=new float[MAX_POINTS];
    private static long[] dts=new long[MAX_POINTS];
    private int pointIndex;
    private double testMaxSpeed=20;
    private double testMaxDistance=0.1;
    private final String[] aniStr={"\\", "|", "/", "-"};
    private int aniStrIndex;

    private String getInfoText() {
        return "Important note\nREAD CAREFULLY!\n\n"
          +"The GT Mode is fully automatical. Just stop, wait for sign\nREADY TO START\n and start your acceleration when you want to."
          +" The mode automatically recognise your acceleration so you DO NOT need to hurry with start.\n"
          +"NEVER LOOK AT SCREEN WHEN ACCELERATING!\n"
          +"Keep your eyes on the road. GT Mode records acceleration and speed results automatically for viewing after the run NOT during. Use designated areas for acceleration test not general purpose roads.\n"
          +"MapNav shall not be held liable for any kind of accident appearing during it using.\n"
          +"And remember about GPS data precise change in different places/times so only relative comparing can be accurate within same place/time of data capture.\n"
          +"Good luck on the track!";
    }

    private void nextAniStrIndex() {
        aniStrIndex++;
        if (aniStrIndex>=aniStr.length){
            aniStrIndex=0;
        }
    }

    public int nextIndex(int index) {
        index++;
        if (index==MAX_POINTS){
            index=0;
        }
        return index;
    }

    public int prevIndex(int index) {
        index--;
        if (index<0){
            index=MAX_POINTS-1;
        }
        return index;
    }
    private int pageIndex;
    private String[] pages={" RUN ", " STAT "};
    private String gpsLost="No GPS data";

    protected void paint(Graphics g) {

        g.setFont(FontUtil.SMALLFONTB);
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
        g.drawString(GT_MODE+pages[pageIndex], dcx, 0, Graphics.HCENTER|Graphics.TOP);
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
            g.drawString("Current time: "+MapCanvas.map.getGPSTime(), 0, sy, Graphics.LEFT|Graphics.TOP);
            sy+=fh;
            //if (state==STATE_ACCELERATING) {
            if (GPSReader.NUM_SATELITES==0){
                g.drawString(gpsLost, 0, sy, Graphics.LEFT|Graphics.TOP);
                //}
            } else {
                g.drawString(String.valueOf(MapUtil.speedWithNameRound1(GPSReader.SPEED_KMH)), 0, sy, Graphics.LEFT|Graphics.TOP);
            }
            //sy+=fh;
            if (state==STATE_ACCELERATING){
                g.drawString(MapUtil.distWithNameRound3(getTravelDistance(lastCheckPointIndex)), dmaxx-1, sy, Graphics.RIGHT|Graphics.TOP);
            }
            //sy+=fh+1;
        }
        if (pageIndex==1){
            //g.drawLine(0, sy, dmaxx, sy);
            g.drawString(PARAM, dx1, sy, Graphics.LEFT|Graphics.TOP);
            g.drawString(CURRENT, (dx2+dx3)/2, sy, Graphics.HCENTER|Graphics.TOP);
            g.drawString(BEST, (dx3+dmaxx)/2, sy, Graphics.HCENTER|Graphics.TOP);
            sy+=fh;
            g.setStrokeStyle(Graphics.SOLID);
            g.drawLine(0, sy, dmaxx, sy);
            g.setStrokeStyle(Graphics.DOTTED);
            g.drawString(TRYN, dx1, sy, Graphics.LEFT|Graphics.TOP);
            g.drawString(String.valueOf(tryNumber), (dx2+dx3)/2, sy, Graphics.HCENTER|Graphics.TOP);
            if (hasBestResult){
                g.drawString(String.valueOf(bestTryNumber), (dx3+dmaxx)/2, sy, Graphics.HCENTER|Graphics.TOP);
            }
            sy+=fh;
            g.drawLine(0, sy, dmaxx, sy);
            g.drawString(V0_100, dx1, sy, Graphics.LEFT|Graphics.TOP);
            if (resultsGathered){
                g.drawString(String.valueOf(currStatSpeedTime), (dx2+dx3)/2, sy, Graphics.HCENTER|Graphics.TOP);
            }
            if (hasBestResult){
                g.drawString(String.valueOf(bestStatSpeedTime), (dx3+dmaxx)/2, sy, Graphics.HCENTER|Graphics.TOP);
            }
            sy+=fh;
            g.drawLine(0, sy, dmaxx, sy);
            g.drawString(S0_400, dx1, sy, Graphics.LEFT|Graphics.TOP);
            if (resultsGathered){
                g.drawString(String.valueOf(currStatDistTime), (dx2+dx3)/2, sy, Graphics.HCENTER|Graphics.TOP);
            }
            if (hasBestResult){
                g.drawString(String.valueOf(bestStatDistTime), (dx3+dmaxx)/2, sy, Graphics.HCENTER|Graphics.TOP);
            }
            sy+=fh;
            g.drawLine(0, sy, dmaxx, sy);
            g.drawString(END_SPEED, dx1, sy, Graphics.LEFT|Graphics.TOP);
            if (resultsGathered){
                g.drawString(String.valueOf(statMaxSpeed), (dx2+dx3)/2, sy, Graphics.HCENTER|Graphics.TOP);
            }
            if (hasBestResult){
                g.drawString(String.valueOf(bestMaxSpeed), (dx3+dmaxx)/2, sy, Graphics.HCENTER|Graphics.TOP);
            }
            sy+=fh;
            g.drawLine(0, sy, dmaxx, sy);
            g.drawString(MIN_DIST, dx1, sy, Graphics.LEFT|Graphics.TOP);
            if (resultsGathered){
                g.drawString(String.valueOf(statMinDist), (dx2+dx3)/2, sy, Graphics.HCENTER|Graphics.TOP);
            }
            if (hasBestResult){
                g.drawString(String.valueOf(bestMinDist), (dx3+dmaxx)/2, sy, Graphics.HCENTER|Graphics.TOP);
            }
        }
        sy+=fh;
        g.setStrokeStyle(Graphics.SOLID);
        g.drawLine(0, sy, dmaxx, sy);
        int backColor=0xFFFF40, frontColor=0x0;
        if (screenActive){
            String cap=STOP, det=STOPdet;
            if (state==STATE_FINISHED){
                cap=FINISHED;
                det=FINISHEDdet;
                backColor=0xFFFFFF;
                frontColor=0x0;

            } else if (state==STATE_ACCELERATING){
                cap=ACCELERATING;
                det=ACCELERATINGdet;
                backColor=0xFF00FF;
                frontColor=0x004000;
            } else if (state==STATE_FAILED){
                cap=FAILURE;
                det=FAILUREdet;
                backColor=0xFFFF40;
                frontColor=0xFF0000;
            } else if (state==STATE_OTHER){
                cap=STOP;
                det=STOPdet;
                backColor=0xFF0000;
                frontColor=0xFFFF40;
            } else if (state==STATE_READY_TO_START){
                cap=READY2START;
                det=READY2STARTdet;
                backColor=0x20FF20;
                frontColor=0x0;
            } else if (state==STATE_SUSPENDED){
                cap=SUSPENDED;
                det=FAILUREdet;
                backColor=0xB0B0FF;
                frontColor=0xFF0000;
            }

            g.setColor(backColor);
            g.fillRect(0, sy, dmaxx, FontUtil.LARGEFONTB.getHeight());
            g.setFont(FontUtil.LARGEFONTB);
            g.setColor(frontColor);
            g.drawString(cap, dcx, sy, Graphics.HCENTER|Graphics.TOP);
            sy+= FontUtil.LARGEFONTB.getHeight();
            g.setColor(0xFFFF40);
            g.setFont(FontUtil.SMALLFONT);
            g.drawString(det, 0, sy, Graphics.LEFT|Graphics.TOP);

        }
    }
    private String GT_MODE="GT Mode";
    private String PARAM="Param";
    private String CURRENT="Current";
    private String BEST="Best";
    private String V0_100;//="0-100"+LangHolder.getString(Lang.kmh);
    private String S0_400;//="0-400"+LangHolder.getString(Lang.m);
    private String STOP="STOP";
    private String STOPdet="Stop to prepare for start";
    private String READY2START="READY TO START";
    private String READY2STARTdet="You may start acceleration";
    private String ACCELERATING="ACCELERATING";
    private String ACCELERATINGdet="Keep going!";
    private String FINISHED="FINISHED";
    private String FINISHEDdet="Press 5 to confirm";
    private String FAILURE="FAILURE";
    private String FAILUREdet="Stop to start again";
    private String TRYN="Try #";
    private String END_SPEED="End speed";
    private String MIN_DIST="Min dist";
    private String SUSPENDED="Suspended";
    private Form setupForm;
    private TextField textName;
    private ChoiceGroup choiceSpdType;
    private ChoiceGroup choiceDistType;
    private ChoiceGroup choiceOpt;

    private void showSetupForm() {
        setupForm=new Form(LangHolder.getString(Lang.options));
        textName=new TextField("Car model", RMSOption.getStringOpt(RMSOption.SO_GT_NAME), 20, TextField.ANY);
        setupForm.append(textName);
        Image[] ia=new Image[statSpdTimeFor.length];
        String[] sa=new String[statSpdTimeFor.length];
        for (int i=0; i<statSpdTimeFor.length; i++) {
            sa[i]=""+(int) statSpdTimeFor[i]+" km/h";
        }
        //sa[1]="40 km/h";
        //sa[2]="60 km/h";
        //sa[3]="80 km/h";
        //sa[4]="100 km/h";
        choiceSpdType=new ChoiceGroup("Test speed", ChoiceGroup.EXCLUSIVE, sa, ia);
        choiceSpdType.setSelectedIndex(RMSOption.getByteOpt(RMSOption.BO_GT_SPD), true);
        setupForm.append(choiceSpdType);
        ia=new Image[statDistTimeFor.length];
        sa=new String[statDistTimeFor.length];
        for (int i=0; i<statDistTimeFor.length; i++) {
            sa[i]=""+(int) (statDistTimeFor[i]*1000)+" m";
        }
        //sa[1]="200 m";
        //sa[2]="300 m";
        //sa[3]="400 m";
        choiceDistType=new ChoiceGroup("Test distance", ChoiceGroup.EXCLUSIVE, sa, ia);
        choiceDistType.setSelectedIndex(RMSOption.getByteOpt(RMSOption.BO_GT_DIST), true);
        setupForm.append(choiceDistType);

        ia=new Image[1];
        sa=new String[1];
        sa[0]="Autosend to site";
        choiceOpt=new ChoiceGroup("Options", ChoiceGroup.MULTIPLE, sa, ia);
        choiceOpt.setSelectedIndex(0, RMSOption.getBoolOpt(RMSOption.BL_GT_AUTOSEND));
        setupForm.append(choiceOpt);

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
                state=STATE_OTHER;
                MapCanvas.setCurrent(this);
            } else {
                hasBestResult=false;
                resultsGathered=false;
                MapCanvas.setCurrent(this);
                RMSOption.setStringOpt(RMSOption.SO_GT_NAME, textName.getString());
                RMSOption.setByteOpt(RMSOption.BO_GT_SPD, (byte) choiceSpdType.getSelectedIndex());
                RMSOption.setByteOpt(RMSOption.BO_GT_DIST, (byte) choiceDistType.getSelectedIndex());
                RMSOption.setBoolOpt(RMSOption.BL_GT_AUTOSEND, choiceOpt.isSelected(0));
                MapCanvas.map.rmss.writeSettingNow();
                testMaxSpeed=statSpdTimeFor[RMSOption.getByteOpt(RMSOption.BO_GT_SPD)];
                testMaxDistance=statDistTimeFor[RMSOption.getByteOpt(RMSOption.BO_GT_DIST)];
                V0_100="0-"+((int) testMaxSpeed)+" km/h";//LangHolder.getString(Lang.kmh);
                S0_400="0-"+((int) 1000*testMaxDistance)+" m";//LangHolder.getString(Lang.m);
                state=STATE_OTHER;
            }
        } else if (displayable==this){
            if (command==exitCommand){
                exitForm=new Form(LangHolder.getString(Lang.exit));
                exitForm.append(new StringItem(LangHolder.getString(Lang.exit)+" (GT Mode)?", ""));
                yesCommand=new Command(LangHolder.getString(Lang.yes), Command.OK, 1);
                noCommand=new Command(LangHolder.getString(Lang.no), Command.CANCEL, 2);
                exitForm.addCommand(yesCommand);
                exitForm.addCommand(noCommand);
                exitForm.setCommandListener(this);
                MapCanvas.setCurrent(exitForm);
            } else if (command==setupCommand){
                state=STATE_SUSPENDED;
                showSetupForm();
            } else if (command==saveCommand){
                if (!resultsGathered){
                    MapCanvas.showmsg("Info", "Produce result first", AlertType.INFO, this);
                    return;
                }
                FileDialog.showSaveForm(LangHolder.getString(Lang.save),
                  new Item[0], this,
                  this, ".TXT");

            } else if (command==sendCommand){
                if (!resultsGathered){
                    MapCanvas.showmsg("Info", "Produce result first", AlertType.INFO, this);
                    return;
                }
                sendResults();
            }
        }
    }
    final private static byte KEY_PRESSED=1;
    final private static byte KEY_RELEASED=2;
    final private static byte KEY_REPEATED=3;

    protected void keyPressed(int keyCode) {
        keyEvent(KEY_PRESSED, keyCode);
    }

    public final void keyEvent(byte eventType, int key) {
        int gA;

        if (key==KEY_NUM5){
            if (state==STATE_FINISHED){
                state=STATE_OTHER;
                return;
            }
        }
        switch (gA=getGameAction(key)) {
            case FIRE: // '\004'
                if (state==STATE_FINISHED){
                    state=STATE_OTHER;
                }
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
    private static final byte STATE_OTHER=0;
    private static final byte STATE_READY_TO_START=2;
    private static final byte STATE_ACCELERATING=1;
    private static final byte STATE_FINISHED=3;
    private static final byte STATE_FAILED=4;
    private static final byte STATE_SUSPENDED=6;
    private static final byte STATE_NEEDINFO=7;
    private byte state;
    private boolean resultsGathered;
    private int tryNumber;

    public void run() {
        while (active) {
            if (screenActive){
                switch (state) {
                    case STATE_NEEDINFO:
                        if (MapCanvas.display.getCurrent()==this){
                            state=STATE_OTHER;
                            MapCanvas.setCurrent(new TextCanvas(getInfoText(), this));
                        }
                        break;
                    case STATE_READY_TO_START:
                        if (lastSpd>9){
                            resultsGathered=false;
                            state=STATE_ACCELERATING;
                            findStartMoment(pointIndex-1);
                            tryNumber++;
                            repaint();
                        }
                        break;
                    case STATE_ACCELERATING:
                        checkFinishConditions();
                        break;
                    case STATE_OTHER:
                    case STATE_FAILED:
                        //if (resultsGathered){
                        if (lastTwoSpeed()<2){
                            state=STATE_READY_TO_START;
                            MapSound.playSound(MapSound.GOSOUND);
                        }
                        //}
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
        if (pointIndex>2){
            screenActive=true;
        }
        lastSpd=spd;
        lastT=System.currentTimeMillis();
        repaint();
    }
    int startSlowPointIndex=-1;
    int startFastPointIndex=-1;
    int lastCheckPointIndex=-1;
    int pointsUsed;
    double maxSpeedFound;
    double minDistFound;
    boolean distExceeded;
    boolean speedExceeded;

    private void findStartMoment(int aPointIndex) {
        safePointIndex=aPointIndex;
        maxSpeedFound=0;
        minDistFound=0;
        speedExceeded=false;
        distExceeded=false;

        int pointsToLook=MAX_POINTS;
        int tempI=0;
        while (pointsToLook>0) {
            if (getSpeedSafe(tempI)<6){
                startSlowPointIndex=tempI;
                break;
            }
            tempI--;
            pointsToLook--;
        }

        //tempI=startSlowPointIndex;
        tempI++;
        while (pointsToLook>0) {
            if (getSpeedSafe(tempI)>7){
                startFastPointIndex=tempI;
                break;
            }
            tempI++;
            pointsToLook--;
        }
        lastCheckPointIndex=startSlowPointIndex;
        pointsUsed=2;
        maxSpeedFound=0;
    }

    private void checkFinishConditions() {
        int currentCheckPointIndex=lastCheckPointIndex;
        int absPointIndex;
        int realPointIndex=pointIndex;
        while (pointsUsed<MAX_POINTS) {
            absPointIndex=safePointIndex+currentCheckPointIndex;
            if (absPointIndex<0){
                absPointIndex=+MAX_POINTS;
            }
            if (absPointIndex>=MAX_POINTS){
                absPointIndex-=MAX_POINTS;
            }

            if (!speedExceeded){
                if (getSpeedSafe(currentCheckPointIndex)>testMaxSpeed){
                    minDistFound=getTravelDistance(currentCheckPointIndex);
                    speedExceeded=true;
                }
            }

            if (!distExceeded) {
                if (getTravelDistance(currentCheckPointIndex)>testMaxDistance){
                    distExceeded=true;
                }
            }

            if (speedExceeded&&distExceeded){
                lastCheckPointIndex=currentCheckPointIndex;
                repaint();
                storeResults();
                state=STATE_FINISHED;
                resultsGathered=true;
                MapSound.playSound(MapSound.FINISHSOUND);
                if (RMSOption.getBoolOpt(RMSOption.BL_GT_AUTOSEND)){
                    sendResults();
                }
                return;
            } else if (nextIndex(absPointIndex)==realPointIndex){
                break;
            } else {
                currentCheckPointIndex++;
                pointsUsed++;
            }

            if (maxSpeedFound<getSpeedSafe(currentCheckPointIndex)){
                maxSpeedFound=getSpeedSafe(currentCheckPointIndex);
            }

            if (maxSpeedFound-getSpeedSafe(currentCheckPointIndex)>8){
                state=STATE_FAILED;
                MapSound.playSound(MapSound.CONNLOSTSOUND);
                break;
            }
        }
        lastCheckPointIndex=currentCheckPointIndex;
        if (pointsUsed>=MAX_POINTS){
            state=STATE_FAILED;
        }
    }

    private long findPreciseSpdTime(double spd) {
        int nearestMoreSpdIndex=startSlowPointIndex;
        int watched=0;
        while (watched<MAX_POINTS) {
            if (getSpeedSafe(nearestMoreSpdIndex)>spd){
                break;
            }
            nearestMoreSpdIndex++;
            watched++;
        }
        if (spd-getSpeedSafe(nearestMoreSpdIndex-1)<0.02){
            return getDtsSafe(nearestMoreSpdIndex-1);
        }
        double dV=getSpeedSafe(nearestMoreSpdIndex)-getSpeedSafe(nearestMoreSpdIndex-1);
        long dT=getDtsSafe(nearestMoreSpdIndex)-getDtsSafe(nearestMoreSpdIndex-1);
        double a=dV/dT;
        double dVd=spd-getSpeedSafe(nearestMoreSpdIndex-1);
        return (long) (getDtsSafe(nearestMoreSpdIndex-1)+dVd/a);
    }

    private double getDistBetweenPoints(int i1, int i2) {
        return MapRoute.distBetweenCoords(getLatSafe(i1), getLonSafe(i1),
          getLatSafe(i2), getLonSafe(i2));

    }
    private double startLat, startLon;

//    private void findPreciseStartPos() {
//        startLat=getLatSafe(startSlowPointIndex);
//        startLon=getLonSafe(startSlowPointIndex);
//    }
    private long findPreciseDistTime(double dist) {
        int nearestMoreDistIndex=startSlowPointIndex;
        int watched=0;
        while (watched<MAX_POINTS) {
            if (getDistBetweenPoints(startSlowPointIndex, nearestMoreDistIndex)>dist){
                break;
            }
            nearestMoreDistIndex++;
            watched++;
        }
        if (dist-getDistBetweenPoints(startSlowPointIndex, nearestMoreDistIndex-1)<0.001){
            return getDtsSafe(nearestMoreDistIndex-1);
        }
        double dD=getDistBetweenPoints(nearestMoreDistIndex, nearestMoreDistIndex-1);
        long dT=getDtsSafe(nearestMoreDistIndex)-getDtsSafe(nearestMoreDistIndex-1);
        double a=dD/dT;
        double dVd=dist-getDistBetweenPoints(startSlowPointIndex, nearestMoreDistIndex-1);
        return (long) (getDtsSafe(nearestMoreDistIndex-1)+dVd/a);

    }

    private double findDistForSpeed(double spd) {
        int nearestMoreSpdIndex=startSlowPointIndex;
        int watched=0;
        while (watched<MAX_POINTS) {
            if (getSpeedSafe(nearestMoreSpdIndex)>spd){
                break;
            }
            nearestMoreSpdIndex++;
            watched++;
        }
        if (spd-getSpeedSafe(nearestMoreSpdIndex-1)<0.1){
            return getDistBetweenPoints(startSlowPointIndex, nearestMoreSpdIndex-1);
        }

        double dV=getSpeedSafe(nearestMoreSpdIndex)-getSpeedSafe(nearestMoreSpdIndex-1);
        double dS=getDistBetweenPoints(nearestMoreSpdIndex, nearestMoreSpdIndex-1);
        double a=dV/dS;
        double dVd=spd-getSpeedSafe(nearestMoreSpdIndex-1);
        return (getDistBetweenPoints(startSlowPointIndex, nearestMoreSpdIndex-1)+dVd/a);

    }

    private double findSpeedForDist(double dist) {
        int nearestMoreDistIndex=startSlowPointIndex;
        int watched=0;
        while (watched<MAX_POINTS) {
            if (getDistBetweenPoints(startSlowPointIndex, nearestMoreDistIndex)>dist){
                break;
            }
            nearestMoreDistIndex++;
            watched++;
        }

        if (dist-getDistBetweenPoints(startSlowPointIndex, nearestMoreDistIndex-1)<0.003){
            return getSpeedSafe(nearestMoreDistIndex-1);
        }

        double dD=getDistBetweenPoints(nearestMoreDistIndex, nearestMoreDistIndex-1);
        double dV=getSpeedSafe(nearestMoreDistIndex)-getSpeedSafe(nearestMoreDistIndex-1);
        if (dV<0.1){
            return getSpeedSafe(nearestMoreDistIndex-1);
        }
        double a=dD/dV;
        double dDd=dist-getDistBetweenPoints(nearestMoreDistIndex-1, startSlowPointIndex);
        return (getSpeedSafe(nearestMoreDistIndex-1)+dDd/a);
    }

    private long findPreciseStartTime() {
        long dT=getDtsSafe(startFastPointIndex)-getDtsSafe(startSlowPointIndex);
        double dV=getSpeedSafe(startFastPointIndex)-getSpeedSafe(startSlowPointIndex);
        double a=dV/dT;
        return (long) (getDtsSafe(startSlowPointIndex)-(getSpeedSafe(startSlowPointIndex)/a));
    }
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

    private double getTravelDistance(int currentCheckPointIndex) {
        return getDistBetweenPoints(startSlowPointIndex, currentCheckPointIndex);
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
    //����� �� ��������: 20��/�, 40, 60, 80, 100
    private double[] statSpdTime=new double[5];
    private double[] statSpdTimeFor={20, 40, 60, 80, 100, 200, 300};
    //����� �� ��������: 100�, 200�, 300�, 400�
    private double[] statDistTime=new double[4];
    private double[] statDistTimeFor={0.1, 0.2, 0.3, 0.4, 0.5, 1.0};
    //������������ �������� �� �������� ����������
    private double statMaxSpeed;
    //����������� ��������� �� �������� ��������
    private int statMinDist;
    //����� �������
    private double currStatSpeedTime;
    //����� �������
    private double currStatDistTime;
    private static double bestStatSpeedTime;
    private static double bestStatDistTime;
    private static double bestMaxSpeed;
    private static int bestMinDist;
    private static int bestTryNumber;
    private static boolean hasBestResult;
    private String statTaken;
    private long preciseStartTime;

    private void storeResults() {
        //statMaxSpeed=maxSpeedFound;
        //statMinDist=minDistFound;
        statTaken=MapCanvas.map.getGPSDate()+" "+MapCanvas.map.getGPSTime();
        long startTime=findPreciseStartTime();
        preciseStartTime=startTime;
        int lastSpdPos=0, lastDistPos=0;
        for (int i=0; i<statSpdTimeFor.length; i++) {
            if (testMaxSpeed>=statSpdTimeFor[i]){
                statSpdTime[i]=rr(findPreciseSpdTime(statSpdTimeFor[i])-startTime);
                currStatSpeedTime=statSpdTime[i];
                lastSpdPos=i;
            }
        }
        statMinDist=(int) (1000*findDistForSpeed(statSpdTimeFor[lastSpdPos]));
        for (int i=0; i<statDistTimeFor.length; i++) {
            if (testMaxDistance>=statDistTimeFor[i]){
                statDistTime[i]=rr(findPreciseDistTime(statDistTimeFor[i])-startTime);
                currStatDistTime=statDistTime[i];
                lastDistPos=i;
            }
        }
        statMaxSpeed=MapUtil.speedRound1(findSpeedForDist(statDistTimeFor[lastDistPos]));

        if ((!hasBestResult)||(statSpdTime[lastSpdPos]<bestStatSpeedTime)||(bestStatDistTime>statDistTime[lastDistPos])){
            hasBestResult=true;
            bestTryNumber=tryNumber;
            bestStatSpeedTime=statSpdTime[lastSpdPos];
            bestStatDistTime=statDistTime[lastDistPos];
            bestMaxSpeed=statMaxSpeed;
            bestMinDist=statMinDist;
        }
    }

    private double rr(double timems) {
        return (double) ((int) (timems/10.0))/100;
    }

    private void sendResults() {
        if (RMSOption.netRadarLogin.equals(MapUtil.emptyString)){
            MapCanvas.showmsg("Login not entered", "Enter MapNav site login in Setting-Netradar", AlertType.WARNING, this);
            return;
        }
        MapCanvas.setCurrent(new ProgressForm(LangHolder.getString(Lang.sending), "Statistic", new SiteSender(this), this));
    }

    private class SiteSender implements Runnable, ProgressStoppable {

        GTCanvas gtCanvas;

        SiteSender(GTCanvas gtc) {
            gtCanvas=gtc;
            (new Thread(this)).start();
        }

        public void run() {
            byte prev_state=state;
            state=STATE_SUSPENDED;
            try {
                try {
                    setProgress("Connecting", 10);
                    StringBuffer url=new StringBuffer(NetRadar.netradarSiteURL);
                    url.append("nrs/ms_gt_ini.php?");
                    url.append("lg=");
                    url.append(HTTPUtils.urlEncodeString(RMSOption.netRadarLogin));
                    url.append("&sign=");
                    url.append(MD5.getHashString(RMSOption.netRadarLogin+statTaken+MD5.getHashString(RMSOption.netRadarPass)));
                    url.append("&spd=");
                    url.append(testMaxSpeed);
                    url.append("&spdtime=");
                    url.append(MapUtil.coordRound3(currStatSpeedTime));
                    url.append("&dst=");
                    url.append((int) (testMaxDistance*1000));
                    url.append("&dsttime=");
                    url.append(MapUtil.coordRound3(currStatDistTime));
                    url.append("&td=");
                    url.append(HTTPUtils.urlEncodeString(statTaken));
                    url.append("&cn=");
                    url.append(HTTPUtils.urlEncodeUnicode(RMSOption.getStringOpt(RMSOption.SO_GT_NAME)));
                    url.append("&ts=");
                    url.append(statMaxSpeed);
                    url.append("&md=");
                    url.append(statMinDist);
                    url.append("&v=");
                    url.append(HTTPUtils.urlEncodeString(MapUtil.version));
                    String surl=url.toString();

                    String res=HTTPUtils.getHTTPContentAsString(surl);

                    //String ts=HTTPUtils.getTS();
                    //StringBuffer sb=new StringBuffer();

                    //HTTPUtils.appendParam(sb, "cn", RMSOption.getStringOpt(RMSOption.SO_GT_NAME), ts);
                    //HTTPUtils.appendParamEnd(sb, ts);
                    //String s=sb.toString();
                    //String res=HTTPUtils.sendDataPostRequestS(s, null, null, surl, ts, null);


                    setProgress("Initiated", 30);
                    String[] ra=new String[3];
                    MapUtil.parseString(res, ',', ra);
                    String id=ra[0].trim();
                    if (id.length()==0){
                        throw new Exception("Error store on server");
                    }
                    if (Integer.parseInt(id)<=0){
                        throw new Exception("Send initiation error: wrong login/password?");
                    }
                    String hash=ra[1].trim();
                    // now only left to send GPS data
                    url.setLength(0);
//20,10.3,1:40,24.7,1:100,13.5,2
                    if (lastCheckPointIndex-startSlowPointIndex>200){
                        throw new Exception("Too long acceleration for sending.");
                    }

                    for (int pi=startSlowPointIndex; pi<=lastCheckPointIndex; pi++) {
                        if (pi==startSlowPointIndex){
                            url.append(MapUtil.speedRound1(getSpeedSafe(pi))).append(',').append(rr(getDtsSafe(pi)-preciseStartTime)).append(',').append('1').append(',').append(pi-startSlowPointIndex);
                        } else {
                            url.append(':').append(MapUtil.speedRound1(getSpeedSafe(pi))).append(',').append(rr(getDtsSafe(pi)-preciseStartTime)).append(',').append('1').append(',').append(pi-startSlowPointIndex);
                        }
                    }
                    String spds=url.toString();

                    url.setLength(0);
                    url.append(NetRadar.netradarSiteURL);
                    url.append("nrs/ms_gt_up.php?");
                    url.append("lg=");
                    url.append(HTTPUtils.urlEncodeString(RMSOption.netRadarLogin));
                    url.append("&sign=");
                    url.append(MD5.getHashString(RMSOption.netRadarLogin+MD5.getHashString(RMSOption.netRadarPass)));
                    url.append("&id=");
                    url.append(id);
                    url.append("&sk=");
                    url.append(MD5.getHashString(hash+spds).substring(0, 4));
                    surl=url.toString();


                    String ts=HTTPUtils.getTS();
                    StringBuffer sb=new StringBuffer();
                    HTTPUtils.appendParam(sb, "spds", spds, ts);
                    HTTPUtils.appendParamEnd(sb, ts);

                    setProgress("Sending", 50);
                    res=HTTPUtils.sendDataPostRequestS(sb.toString(), null, null, surl, ts, null);
                    MapUtil.parseString(res, ',', ra);
                    if (Integer.parseInt(ra[0].trim())<=0){
                        throw new Exception("Send details error");
                    }

                    url.setLength(0);
//20,10.3,1:40,24.7,1:100,13.5,2

                    for (int pi=startSlowPointIndex; pi<=lastCheckPointIndex; pi++) {
                        if (pi==startSlowPointIndex){
                            url.append((int) (getTravelDistance(pi)*1000)).append(',').append(rr(getDtsSafe(pi)-preciseStartTime)).append(',').append('4').append(',').append(pi-startSlowPointIndex);
                        } else {
                            url.append(':').append((int) (getTravelDistance(pi)*1000)).append(',').append(rr(getDtsSafe(pi)-preciseStartTime)).append(',').append('4').append(',').append(pi-startSlowPointIndex);
                        }
                    }

                    spds=url.toString();

                    url.setLength(0);
                    url.append(NetRadar.netradarSiteURL);
                    url.append("nrs/ms_gt_up.php?");
                    url.append("lg=");
                    url.append(HTTPUtils.urlEncodeString(RMSOption.netRadarLogin));
                    url.append("&sign=");
                    url.append(MD5.getHashString(RMSOption.netRadarLogin+MD5.getHashString(RMSOption.netRadarPass)));
                    url.append("&id=");
                    url.append(id);
                    url.append("&sk=");
                    url.append(MD5.getHashString(hash+spds).substring(0, 4));
                    surl=url.toString();


                    ts=HTTPUtils.getTS();
                    sb.setLength(0);

                    HTTPUtils.appendParam(sb, "spds", spds, ts);
                    HTTPUtils.appendParamEnd(sb, ts);

                    setProgress("Sending", 60);
                    res=HTTPUtils.sendDataPostRequestS(sb.toString(), null, null, surl, ts, null);
                    MapUtil.parseString(res, ',', ra);
                    if (Integer.parseInt(ra[0].trim())<=0){
                        throw new Exception("Send details error");
                    }

                    url.setLength(0);
//20,10.3,1:40,24.7,1:100,13.5,2

                    for (int i=0; i<statSpdTimeFor.length; i++) {
                        if (testMaxSpeed>=statSpdTimeFor[i]){
                            // Util.writeStr2OS(os, "0-"+statSpdTimeFor[i]+" : "+statSpdTime[i]+"\r\n");
                            if (i==0){
                                url.append(statSpdTimeFor[i]).append(',').append(statSpdTime[i]).append(',').append('2').append(',').append(i);
                            } else {
                                url.append(':').append(statSpdTimeFor[i]).append(',').append(statSpdTime[i]).append(',').append('2').append(',').append(i);
//                                url.append(':').append(MapUtil.speedRound1(getSpeedSafe(pi))).append(',').append(rr(getDtsSafe(pi)-preciseStartTime)).append(',').append('1').append(',').append(pi-startSlowPointIndex);
                            }

                        }
                    }

                    spds=url.toString();


                    url.setLength(0);
                    url.append(NetRadar.netradarSiteURL);
                    url.append("nrs/ms_gt_up.php?");
                    url.append("lg=");
                    url.append(HTTPUtils.urlEncodeString(RMSOption.netRadarLogin));
                    url.append("&sign=");
                    url.append(MD5.getHashString(RMSOption.netRadarLogin+MD5.getHashString(RMSOption.netRadarPass)));
                    url.append("&id=");
                    url.append(id);
                    url.append("&sk=");
                    url.append(MD5.getHashString(hash+spds).substring(0, 4));
                    surl=url.toString();


                    ts=HTTPUtils.getTS();
                    sb.setLength(0);
                    HTTPUtils.appendParam(sb, "spds", spds, ts);
                    HTTPUtils.appendParamEnd(sb, ts);

                    setProgress("Sending", 70);
                    res=HTTPUtils.sendDataPostRequestS(sb.toString(), null, null, surl, ts, null);
                    MapUtil.parseString(res, ',', ra);

                    if (Integer.parseInt(ra[0].trim())<=0){
                        throw new Exception("Send details error");
                    }

                    url.setLength(0);
//20,10.3,1:40,24.7,1:100,13.5,2

                    for (int i=0; i<statDistTimeFor.length; i++) {
                        if (testMaxDistance>=statDistTimeFor[i]){
                            // Util.writeStr2OS(os, "0-"+statSpdTimeFor[i]+" : "+statSpdTime[i]+"\r\n");
                            if (i==0){
                                url.append(statDistTimeFor[i]*1000).append(',').append(statDistTime[i]).append(',').append('3').append(',').append(i);
                            } else {
                                url.append(':').append(statDistTimeFor[i]*1000).append(',').append(statDistTime[i]).append(',').append('3').append(',').append(i);
//                                url.append(':').append(MapUtil.speedRound1(getSpeedSafe(pi))).append(',').append(rr(getDtsSafe(pi)-preciseStartTime)).append(',').append('1').append(',').append(pi-startSlowPointIndex);
                            }

                        }
                    }

                    spds=url.toString();


                    url.setLength(0);
                    url.append(NetRadar.netradarSiteURL);
                    url.append("nrs/ms_gt_up.php?");
                    url.append("lg=");
                    url.append(HTTPUtils.urlEncodeString(RMSOption.netRadarLogin));
                    url.append("&sign=");
                    url.append(MD5.getHashString(RMSOption.netRadarLogin+MD5.getHashString(RMSOption.netRadarPass)));
                    url.append("&id=");
                    url.append(id);
                    url.append("&sk=");
                    url.append(MD5.getHashString(hash+spds).substring(0, 4));
                    surl=url.toString();


                    ts=HTTPUtils.getTS();
                    sb.setLength(0);
                    HTTPUtils.appendParam(sb, "spds", spds, ts);
                    HTTPUtils.appendParamEnd(sb, ts);


                    setProgress("Sending", 70);
                    res=HTTPUtils.sendDataPostRequestS(sb.toString(), null, null, surl, ts, null);
                    MapUtil.parseString(res, ',', ra);
                    if (Integer.parseInt(ra[0].trim())<=0){
                        throw new Exception("Send details error");
                    }


                } finally {
                    this.progressResponse=null;
                }
                //MapCanvas.setCurrent(gtCanvas);
                MapCanvas.showmsg("Info", "Stats are sent", AlertType.INFO, gtCanvas);
            } catch (Throwable t) {
                MapCanvas.showmsgmodal("Error", t.getMessage()+": "+t, AlertType.ERROR, gtCanvas);
            }
            state=prev_state;
            gtCanvas=null;
        }
        ProgressResponse progressResponse;

        private void setProgress(String info, int percent) {
            try {
                if (progressResponse!=null){
                    progressResponse.setProgress((byte) percent, info);
                }
            } catch (Throwable t) {
            }
        }

        public void setProgressResponse(ProgressResponse progressResponse) {
            this.progressResponse=progressResponse;
        }

        public boolean stopIt() {
            this.progressResponse=null;
            return true;

        }
    }

    private void writeReport(OutputStream os) throws IOException {
        Util.writeStr2OS(os, "MapNav ("+MapUtil.version+") GT Mode results\r\n");
        Util.writeStr2OS(os, "Statistics are taken "+statTaken+" for "+RMSOption.getStringOpt(RMSOption.SO_GT_NAME)+"\r\n");
        Util.writeStr2OS(os, "--------\r\n");
        Util.writeStr2OS(os, "Speed acceleration (km/h), sec:\r\n");
        for (int i=0; i<statSpdTimeFor.length; i++) {
            if (testMaxSpeed>=statSpdTimeFor[i]){
                Util.writeStr2OS(os, "0-"+statSpdTimeFor[i]+" : "+statSpdTime[i]+"\r\n");
            }
        }
        Util.writeStr2OS(os, "--------\r\n");
        Util.writeStr2OS(os, "Distance acceleration (m), sec:\r\n");
        for (int i=0; i<statDistTimeFor.length; i++) {
            if (testMaxDistance>=statDistTimeFor[i]){
                Util.writeStr2OS(os, "0-"+(statDistTimeFor[i]*1000)+" : "+statDistTime[i]+"\r\n");
            }
        }
        Util.writeStr2OS(os, "--------\r\n\r\n");
        Util.writeStr2OS(os, "Important note about result precision.\r\n");
        Util.writeStr2OS(os, "GPS data accuracy vary in time and place so results taken near in time and place may be compared correctly.\r\n");

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
    //private String importantNote="   Important note!\nGT Mode allows you to measure acceleration automatically. Just stop and wait for message "+
    // "READY TO START ";
}
