/*
 * ProgressForm.java
 *
 * Created on 16 ������ 2007 �., 18:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package misc;

import RPMap.FontUtil;
import RPMap.MapCanvas;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import lang.Lang;
import lang.LangHolder;

/**
 *
 * @author Raev
 */
public class ProgressForm extends Canvas implements Runnable, CommandListener, ProgressResponse {

    private String percentSign=" %";

    /** Creates a new instance of ProgressForm */
    public ProgressForm(String msgTop, String msgBottom, ProgressStoppable stopInt, Displayable cancelDisp) {
        this.msgTop=msgTop;
        this.msgBottom=msgBottom;
        this.stopInt=stopInt;
        this.cancelDisp=cancelDisp;
        setTitle(msgTop);
        if (stopInt!=null){
            try {
                stopInt.setProgressResponse(this);
            } catch (Throwable t) {
            }
            if (cancelDisp!=null){
                stopCommand=new Command(LangHolder.getString(Lang.cancel), Command.CANCEL, 1);
                addCommand(stopCommand);
                setCommandListener(this);
            }
        }
        //if (RMSOption.fullScreen) setFullScreenMode(true);
        prgThread=new Thread(this);
        // prgThread.setPriority(Thread.MAX_PRIORITY);
        prgThread.start();
    }
    ProgressStoppable stopInt;
    Displayable cancelDisp;
    String msgTop;
    String msgBottom;
    private Thread prgThread;
    private Command stopCommand;

    protected void paint(Graphics g) {
        try {
            g.setFont(FontUtil.MEDIUMFONT);
            g.setColor(0xFFFFFF);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(0x808080);
            g.fillRect(0+20, 0+20, getWidth()-12-12, getHeight()-12-12);
            g.setColor(0x0);
            g.fillRect(0+12, 0+12, getWidth()-12-12, getHeight()-12-12);
            g.setColor(0xFFFFFF);
            g.drawRect(0+13, 0+13, getWidth()-14-13, getHeight()-14-13);
            int dcx=getWidth()/2;
            int dcy1=getHeight()/4;

            int dcy=getHeight()/2;
            if (msgTop!=null){
                g.drawString(msgTop, dcx, dcy1, Graphics.HCENTER|Graphics.BOTTOM);
            }
            if (msgBottom!=null){
                g.drawString(msgBottom, dcx, dcy1, Graphics.HCENTER|Graphics.TOP);
            }
            int dy=dcy/5;
            maxstep=dy;
            int dc=(dcx+dcx/2)/dy;
            int dl=dcx-(dc*dy)/2;
            if (percent<0){
                g.clipRect(dl, dcy, dc*dy, dy);
            } else {
                g.clipRect(dl, dcy, dc*dy*percent/100, dy);
                int Pr=percent;
                int r=(256*(100-Pr)/101)<<16;
                int b=((200*(50-Math.abs(50-Pr)))/101);
                int gr=(256*(Pr)/101)<<8;
                int color=r+gr+b;
//        int color=(256*(100-Pr))/101+((190*(Pr))/ 101)*256;//+((200*(50-Math.abs(50-Pr)))/101);
                g.setColor(color);
                //g.setColor(0x20D020);
                g.fillRect(dl, dcy, dc*dy, dy);
            }
            g.setColor(0xFFFFFF);
            for (int i=-1; i<=dc; i++) {
                int xl=dl+step+i*dy;
                int xr=dl+step+i*dy-dy;
                g.setColor(0xFFFFFF);
                g.drawLine(xl, dcy, xr, dcy+dy);
                g.setColor(0xC0C0C0);
                g.drawLine(xl-1, dcy, xr-1, dcy+dy);
                g.drawLine(xl+1, dcy, xr+1, dcy+dy);
                g.setColor(0x808080);
                g.drawLine(xl-2, dcy, xr-2, dcy+dy);
                g.drawLine(xl+2, dcy, xr+2, dcy+dy);
                g.setColor(0x505050);
                g.drawLine(xl-3, dcy, xr-3, dcy+dy);
                g.drawLine(xl+3, dcy, xr+3, dcy+dy);
                g.setColor(0x303030);
                g.drawLine(xl-4, dcy, xr-4, dcy+dy);
                g.drawLine(xl+4, dcy, xr+4, dcy+dy);
            }
            g.setClip(0, 0, getWidth(), getHeight());
            if (percent>=0){
                String s=String.valueOf(percent)+percentSign;
                g.setColor(0x0);
                g.setFont(FontUtil.SMALLFONTB);
                int pw= FontUtil.SMALLFONTB.stringWidth(s)/2;
                int fto=(dy- FontUtil.SMALLFONTB.getHeight())/2;
                g.fillRoundRect(dcx-pw-4, dcy+fto+2, pw+pw+8, FontUtil.SMALLFONTB.getHeight(), 3, 3);
                g.setColor(0xFFFFFF);
                g.setFont(FontUtil.MEDIUMFONT);
                g.drawString(s, dcx, dcy+fto+2, Graphics.HCENTER|Graphics.TOP);
            }
            g.setColor(0xFFFFFF);
            g.drawRect(dl, dcy, dc*dy, dy);
            g.drawRect(dl-1, dcy-1, dc*dy+2, dy+2);

            if (task!=null){
                g.drawString(task, dcx, getHeight()-20, Graphics.HCENTER|Graphics.BOTTOM);
            }

        } catch (Throwable t) {
        }
        if (percent<0){
            step=step==maxstep-1?0:step+1;
        }
    }
    int step;
    int maxstep;

    public void run() {
//#mdebug
//#         long sttm=System.currentTimeMillis();
//#         int cycl=0;
//#enddebug
        try {
            repaint();
            Thread.yield();
            try {
                Thread.sleep(150);
            } catch (Throwable t) {
            }
            Thread.yield();
            while (MapCanvas.display.getCurrent()==this) {
                repaint();
                try {
                    Thread.sleep(20);
                } catch (Throwable t) {
                }
                Thread.yield();
//#debug
//#                 cycl++;
            }
        } finally {
            prgThread=null;
            stopInt=null;
        }
//#mdebug
//#         sttm=System.currentTimeMillis()-sttm;
//#         if (RMSOption.debugEnabled){
//#             DebugLog.add2Log("PF TM: "+sttm+':'+cycl);
//#         }
//#enddebug

    }

    public void commandAction(Command command, Displayable displayable) {
        if (displayable==this){
            if (command==stopCommand){
                boolean showCancel=true;
                try {
                    if (stopInt!=null){
                        stopInt.stopIt();
                        showCancel=true;
                    }
                } finally {
                    stopInt=null;
                    removeCommand(stopCommand);
                    if (showCancel){
                        MapCanvas.setCurrent(cancelDisp);
                    }
                }
            }
        }
    }
    private byte percent=-1;
    private String task;

    public void setProgress(byte percent, String task) {
        this.percent=percent;
        this.task=task;
        repaint();
    }
}
