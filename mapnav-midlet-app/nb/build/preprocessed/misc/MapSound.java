/*
 * MapSound.java
 *
 * Created on 14 ������� 2007 �., 23:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package misc;

import RPMap.MapUtil;
import RPMap.RMSOption;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VolumeControl;

/**
 *
 * @author RFK
 */
public class MapSound implements PlayerListener, Runnable {

    public static final byte CONNLOSTSOUND=1;
    public static final byte SATLOSTSOUND=2;
    public static final byte SATFOUNDSOUND=3;
    public static final byte GPSFOUNDSOUND=4;
    public static final byte NEXTWPTSOUND=5;
    public static final byte NEWCONNECTSOUND=6;
    public static final byte NEWMESSAGESOUND=7;
    public static final byte FINISHSOUND=8;
    public static final byte GOSOUND=9;
    public static final byte MAXSPEED=10;
    public static final byte UPSPEED=11;
    public static final byte DOWNSPEED=12;
    //private static MapSound ms;
    private byte soundKind;
    private static long lastSound;
    private static long lastMaxSpeedTime;
    private static long lastUpSpeedTime;

    public static void playSound(byte sound) {
        if (!RMSOption.soundOn){
            return;
        }

        if (((MapSound.SATLOSTSOUND==sound)||(sound==MapSound.SATFOUNDSOUND))&&(!RMSOption.satSoundStatus)){
            return;
        }
        //if ((sound==MapSound.GPSADDTRACKPOINT)&&(!RMSOption.getBoolOpt(RMSOption.BL_ADDTRACKPOINTSOUND_ON))){
        //    return;
        //}

        switch (sound) {
            case NEXTWPTSOUND:
            case SATFOUNDSOUND:
            case GPSFOUNDSOUND:
            case NEWCONNECTSOUND:
            case NEWMESSAGESOUND:
            case FINISHSOUND:
            case GOSOUND:
                if (lastSound+1100>System.currentTimeMillis()){
                    return;
                }
                if (sound!=MAXSPEED) {
                    break;
                }
            case MAXSPEED: {
                if (System.currentTimeMillis()-lastMaxSpeedTime<30000){
                    return;
                } else {
                    lastMaxSpeedTime=System.currentTimeMillis();
                }
            }
            case UPSPEED:
            case DOWNSPEED:{
                if (System.currentTimeMillis()-lastUpSpeedTime<3000){
                    return;
                } else {
                    lastUpSpeedTime=System.currentTimeMillis();
                }
            }

        }

        lastSound=System.currentTimeMillis();
        try {
            new MapSound(sound);
        } catch (Throwable t) {
        }
    }

    public static void playCustomSound(byte type, String soundRes) {
        if (!RMSOption.soundOn){
            return;
        }
        try {
            new MapSound(type, soundRes);
        } catch (Throwable t) {
        }
    }
      public static void playMIDISound(byte type) {
        if (!RMSOption.soundOn){
            return;
        }
        try {
            new MapSound(type, TYPE_MIDI);
        } catch (Throwable t) {
        }
    }
    static final byte TYPE_PREDEFINED=0;
    public static final byte TYPE_INTERNAL=1;
    public static final byte TYPE_EXTERNAL=2;
    public static final byte TYPE_MIDI=3;
    public static final byte TYPE_BYTES=4;
    private byte customType;
    private String soundRes;

    /** Creates a new instance of MapSound */
    public MapSound(byte sound) {
        soundKind=sound;
        Thread t=new Thread(this);
        t.start();
    }

        /** Creates a new instance of MapSound for playing MIDI in parallel*/
    public MapSound(byte sound, byte customType) {
        soundKind=sound;
        this.customType=customType;
        Thread t=new Thread(this);
        t.start();
    }

    /** Creates a new instance of MapSound for playing files*/
    public MapSound(byte type, String res) {
        customType=type;
        soundRes=res;
        Thread t=new Thread(this);
        t.start();
    }
    private byte[] sound;
    /** Creates a new instance of MapSound for bytes*/
    public MapSound(byte[] sound, String format) {
        customType=TYPE_BYTES;
        soundRes=format;
        this.sound=sound;
        Thread t=new Thread(this);
        t.start();
    }

    Player lo_player;
    InputStream is;

    private String getAudioType(String fn) {
        int lp=fn.lastIndexOf('.');
        String ext=fn.substring(lp+1);
        return "audio/"+ext.toLowerCase();
    }

    public void run() {
            if (customType==TYPE_MIDI){
                playTone(soundKind);
                return;
            }
        String fn=MapUtil.emptyString;
        try {
            if (customType==TYPE_PREDEFINED){
                if (soundKind==CONNLOSTSOUND){
                    fn="/mel/fanfare_2.amr";
                } else if (soundKind==SATLOSTSOUND){
                    fn="/mel/satl.amr";
                } else if (soundKind==SATFOUNDSOUND){
                    fn="/mel/satf.amr";
                } else if (soundKind==MAXSPEED){
                    fn="/mel/ps_8.wav";
                } else if (soundKind==UPSPEED){
                    fn="/mel/up.wav";
                } else if (soundKind==DOWNSPEED){
                    fn="/mel/down.wav";
                } else if (soundKind==GPSFOUNDSOUND){
                    fn="/mel/gpscon.amr";
                } else if (soundKind==NEXTWPTSOUND){
                    fn="/mel/wpt8.wav";
                } else if (soundKind==FINISHSOUND){
                    fn="/mel/finish.wav";
                } else if (soundKind==GOSOUND){
                    fn="/mel/go.wav";
                } else if (soundKind==NEWMESSAGESOUND){
                    fn="/mel/mes.amr";
                } else if (soundKind==NEWCONNECTSOUND){
                    playTone(GPSRECONNECTTONE);
                    return;
                }
                is=getClass().getResourceAsStream(fn);

                lo_player=Manager.createPlayer(is, getAudioType(fn));
            } else if (customType==TYPE_INTERNAL){

                fn=soundRes;
                is=getClass().getResourceAsStream(fn);
                lo_player=Manager.createPlayer(is, getAudioType(fn));

            } else if (customType==TYPE_EXTERNAL){
                fn=soundRes;
                is=Connector.openInputStream(fn);
                lo_player=Manager.createPlayer(is, getAudioType(fn));
            } else if (customType==TYPE_BYTES){

                is=new ByteArrayInputStream(sound);
                lo_player=Manager.createPlayer(is, soundRes);
            }

            lo_player.addPlayerListener(this);
            lo_player.prefetch();
            lo_player.start();
            try {
                VolumeControl vc=(VolumeControl) lo_player.getControl("VolumeControl");
                vc.setLevel(RMSOption.getByteOpt(RMSOption.BO_VOLUME));
            } catch (Throwable t) {
            }
        } catch (Throwable e) {
//#mdebug
            if (RMSOption.debugEnabled){
                DebugLog.add2Log("Play snd:"+customType+":"+fn+":"+e.getMessage()+"\n"+fn);
            }
//#enddebug
        }


    }
    public final static byte GPSLOSTTONE=100+1;
    public final static byte APPSTARTTONE=100+2;
    public final static byte GPSRECONNECTTONE=100+3;
    public final static byte GPSADDTRACKPOINT=100+4;
    // public final static byte GPSWARNMAXSPEED=100+5;

    public static void playTone(byte i) {
        if (!RMSOption.soundOn){
            return;
        }
        try {
            try {
                switch (i) {
                    default:
                        break;

                    // case GPSWARNMAXSPEED: // '\002'
                    //    Manager.playTone(79, 150, RMSOption.getByteOpt(RMSOption.BO_VOLUME));
                    //    Thread.sleep(150L);
                    //    Manager.playTone(91, 300, RMSOption.getByteOpt(RMSOption.BO_VOLUME));
                    //    break;

                    case APPSTARTTONE: // '\002'
                        Manager.playTone(89, 200, RMSOption.getByteOpt(RMSOption.BO_VOLUME));
                        Thread.sleep(300L);
                        Manager.playTone(80, 200, RMSOption.getByteOpt(RMSOption.BO_VOLUME));
                        Thread.sleep(300L);
                        Manager.playTone(80, 200, RMSOption.getByteOpt(RMSOption.BO_VOLUME));
                        Thread.sleep(200L);
                        Manager.playTone(80, 10, 0);
                        break;
                    /*
                    case 1: // '\001'
                    Manager.playTone(80, 500, 80);
                    Thread.sleep(500L);
                    Manager.playTone(74, 500, 80);
                    break;

                    case 3: // '\003'
                    Manager.playTone(68, 500, 80);
                    break;
                     */
                    case GPSRECONNECTTONE: // '\004'
                        Manager.playTone(97, 2000, RMSOption.getByteOpt(RMSOption.BO_VOLUME));
                        break;

                    case GPSLOSTTONE: // '\004'
                        Manager.playTone(88, 1000, RMSOption.getByteOpt(RMSOption.BO_VOLUME));
                        break;

                    case GPSADDTRACKPOINT: // '\004'
                        Manager.playTone(78, 300, RMSOption.getByteOpt(RMSOption.BO_VOLUME));
                        break;
                    /*
                    case 5: // '\005'
                    Manager.playTone(80, 500, 100);
                    break;

                    case 6: // '\006'
                    Manager.playTone(80, 500, 100);
                    break;

                    case 7: // '\007'
                    Manager.playTone(74, 200, 80);
                    break;
                     */
                }
                return;
            } catch (MediaException _ex) {
                return;
            } catch (InterruptedException _ex) {
                return;
            }
        } catch (Throwable t) {
            //#mdebug
            if (RMSOption.debugEnabled){
                DebugLog.add2Log("MS:"+t.toString());
            }
        //#enddebug

        }
    }
    private static String endOfMedia="endOfMedia";
    private static String error="error";

    public void playerUpdate(Player player, String s, Object object) {
        if (s.equals(endOfMedia)||s.equals(error)){
       try {
            try {
                lo_player.close();
            } finally {
                is.close();
            }
        } catch (Throwable ex) {
        }
        System.gc();
        }
    }

    
}
