// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi
package misc;

import RPMap.MapCanvas;
import RPMap.MapMark;
import RPMap.MapUtil;
import RPMap.RMSOption;
import RPMap.RMSSettings;
import java.io.*;
import javax.microedition.lcdui.Displayable;
import javax.microedition.media.*;
import javax.microedition.media.control.RecordControl;

public final class SoundRecorder implements PlayerListener, Runnable, ProgressStoppable {

    private Player m_bPlayer;
    private RecordControl m_cRecordControl;
    private long recStarted;
    private Thread thread;
    private MapMark mark;
    private MarkList mList;
    private Displayable backForm;

    public SoundRecorder(MapMark selectedMark, Displayable backForm, MarkList markList) {
        this.mark=selectedMark;
        this.mList=markList;
        this.backForm=backForm;

        MapCanvas.setCurrent(new ProgressForm(selectedMark.name, "Recording...", this, backForm));
        this.thread=new Thread(this);
        this.thread.start();
    }

    public void playerUpdate(Player player, String s, Object obj) {
        if (s.equals("endOfMedia")||s.equals("error")||s.equals("recordError")){
            clearPlayer();
        }
    }

    public void clearPlayer() {

        if (m_cRecordControl!=null){
            try {
                m_cRecordControl.commit();
            } catch (IOException _ex) {
            }
            m_cRecordControl=null;
        }
        if (m_bPlayer!=null){
            try {
                m_bPlayer.close();
            } catch (Exception _ex) {
            }
            m_bPlayer=null;
        }
    }

    public synchronized void playStreamFormat(InputStream inputstream, String s) {
        clearPlayer();
        try {
            m_bPlayer=Manager.createPlayer(inputstream, s);
            m_bPlayer.addPlayerListener(this);
            m_bPlayer.realize();
            m_bPlayer.prefetch();
            m_bPlayer.start();
            return;
        } catch (IOException ioexception) {
            //#mdebug
            if (RMSOption.debugEnabled){
                DebugLog.add2Log("SR io:"+ioexception.toString());
            }
            //#enddebug

            clearPlayer();
            return;
        } catch (MediaException mediaexception) {
            //#mdebug
            if (RMSOption.debugEnabled){
                DebugLog.add2Log("SR me:"+mediaexception.toString());
            }
            //#enddebug
            clearPlayer();
            return;
        }
    }
    private String format;

    public byte[] recordSoundArray() {
        clearPlayer();
        byte soundBytes[]=null;
        try {
            boolean b=RMSOption.soundOn;
            try {
                RMSOption.soundOn=true;
                MapSound.playTone(MapSound.GPSADDTRACKPOINT);
                try {
                    Thread.sleep(400);
                } catch (InterruptedException ex) {
                }
                m_bPlayer=Manager.createPlayer("capture://audio");
                m_bPlayer.addPlayerListener(this);
                m_bPlayer.realize();
                m_cRecordControl=(RecordControl) m_bPlayer.getControl("RecordControl");
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                m_cRecordControl.setRecordStream(baos);
                format=m_cRecordControl.getContentType();
                m_cRecordControl.startRecord();
                m_bPlayer.start();
                recStarted=System.currentTimeMillis();
                try {
                    while (System.currentTimeMillis()-recStarted<MAX_RECORD_MILLIS) {
                        Thread.sleep(300);
                        if (stopped){
                            //#mdebug
                            if (RMSOption.debugEnabled){
                                DebugLog.add2Log("SR st br");
                            }
                            //#enddebug
                            break;
                        }
                        if (progressResponse!=null){
                            try {
                                progressResponse.setProgress((byte) (100*(System.currentTimeMillis()-recStarted)/MAX_RECORD_MILLIS),
                                  MapUtil.time2String(System.currentTimeMillis()-recStarted)+" "+baos.size());
                            } catch (Throwable r) {
                            }
                        }
                    }
                } catch (InterruptedException _ex) {
                    stopped=true;
//#mdebug
                    if (RMSOption.debugEnabled){
                        DebugLog.add2Log("SR intr");
                    }
                    //#enddebug
                }
                clearPlayer();
                soundBytes=baos.toByteArray();
                MapSound.playTone(MapSound.GPSADDTRACKPOINT);
            } finally {
                RMSOption.soundOn=b;
            }
        } catch (IOException ioexception) {
            //#mdebug
            if (RMSOption.debugEnabled){
                DebugLog.add2Log("SR ioe:"+ioexception.toString());
            }
            //#enddebug

            clearPlayer();
            //BTRadio._aStringV("IOException:", "" + ioexception.getMessage());
        } catch (MediaException mediaexception) {
            //#mdebug
            if (RMSOption.debugEnabled){
                DebugLog.add2Log("SR mde:"+mediaexception.toString());
            }
            //#enddebug
            clearPlayer();
            //BTRadio._aStringV("MediaException:", "" + mediaexception.getMessage());
        }
        return soundBytes;
    }
    private boolean stopped;
    private final static int MAX_RECORD_MILLIS=10000;

    public void run() {
        //#mdebug
        if (RMSOption.debugEnabled){
            DebugLog.add2Log("Call SR");
        }
        //#enddebug
        try {
            byte[] sound=recordSoundArray();
            //#if SE_K750_E_BASEDEV
//#             try {
//#                 sound=Util.readStream(this.getClass().getResourceAsStream("/mel/fanfare_2.amr"));
//#                 format="audio/amr";
//#             } catch (Throwable t) {
//#             }
//#endif

            if (sound!=null){
                //#mdebug
                if (RMSOption.debugEnabled){
                    DebugLog.add2Log("SR:"+format+":"+sound.length);
                }
                //#enddebug
                if (progressResponse!=null){
                    progressResponse.setProgress((byte) -1, "Storing..");
                    try {
                        Thread.sleep(70);
                    } catch (InterruptedException ex) {
                    }
                }
                RMSSettings.saveGeoData(mark.rId, sound, RMSSettings.GEODATA_MAPMARK);
                RMSSettings.saveGeoInfo(mark.rId, format, RMSSettings.GEODATA_MAPMARK);
                mark.hasSound=true;
                MapCanvas.map.rmss.saveMapMark(mark);
                mList.fillMarkList();
            }
        } finally {
            mark=null;
            progressResponse=null;
            mList=null;
            MapCanvas.setCurrent(backForm);
            backForm=null;
            thread=null;
            System.gc();
        }
    }
    private ProgressResponse progressResponse;

    public void setProgressResponse(ProgressResponse progressResponse) {
        this.progressResponse=progressResponse;
    }

    public boolean stopIt() {
        stopped=true;
        if (thread!=null){
            thread.interrupt();
        }
        thread=null;
        return false;
    }
}
