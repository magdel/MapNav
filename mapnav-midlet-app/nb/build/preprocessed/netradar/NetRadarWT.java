
package netradar;

import RPMap.RMSOption;
import java.io.*;
import javax.microedition.lcdui.Canvas;
import javax.microedition.media.*;
import javax.microedition.media.control.RecordControl;
import javax.microedition.media.control.VideoControl;
//#debug
import misc.DebugLog;
import misc.MapSound;
import misc.Util;

public final class NetRadarWT implements PlayerListener,Runnable {
  
  public final static int m_aI = 3000;
  private Player m_bPlayer = null;
  private RecordControl m_cRecordControl = null;
  //private VideoControl m_dVideoControl;
  
  public NetRadarWT() {
  }
  
//    private static String _bStringString(String s)
//    {
//        String s1 = "";
//        if((s = s.trim().toLowerCase()).endsWith(".mp3"))
//            s1 = "audio/mpeg";
//        else
//        if(s.endsWith(".amr"))
//            s1 = "audio/amr";
//        else
//        if(s.endsWith(".wav"))
//            s1 = "audio/x-wav";
//        else
//        if(s.endsWith(".mid"))
//            s1 = "audio/midi";
//        return s1;
//    }
  
//    public static byte[] _avaB()
//    {
//        byte abyte0[];
//        if(m_dVideoControl != null)
//            try
//            {
//                abyte0 = m_dVideoControl.getSnapshot(null);
//            }
//            catch(MediaException _ex)
//            {
//                abyte0 = null;
//            }
//        else
//            abyte0 = null;
//        return abyte0;
//    }
  
  public void playerUpdate(Player player, String s, Object obj) {
    if(s.equals("endOfMedia") || s.equals("error") || s.equals("recordError"))
      clearPlayer();
  }
  
  public void clearPlayer() {
//    if(m_dVideoControl != null) {
//      m_dVideoControl.setVisible(false);
//      m_dVideoControl = null;
//    }
    if(m_cRecordControl != null) {
      try {
        m_cRecordControl.commit();
      } catch(IOException _ex) { }
      m_cRecordControl = null;
    }
    if(m_bPlayer != null) {
      try {
        m_bPlayer.close();
      } catch(Exception _ex) { }
      m_bPlayer = null;
    }
  }
  
//    public final synchronized void _aStringV(String s)
//    {
//        try
//        {
//            InputStream inputstream = Class.forName("javax.microedition.media.Player").getResourceAsStream("/res/" + s);
//            _aInputStreamV(inputstream, _bStringString(s));
//            return;
//        }
//        catch(ClassNotFoundException _ex)
//        {
//            return;
//        }
//    }
  
  public synchronized void playStreamFormat(InputStream inputstream, String s) {
    clearPlayer();
    try {
      m_bPlayer = Manager.createPlayer(inputstream, s);
      m_bPlayer.addPlayerListener(this);
      m_bPlayer.realize();
      m_bPlayer.prefetch();
      m_bPlayer.start();
      return;
    } catch(IOException ioexception) {
      //#debug
      if (RMSOption.debugEnabled) DebugLog.add2Log("NR WT io:"+ioexception.toString());
      
      clearPlayer();
      //BTRadio._aStringV("IOException:", "" + ioexception.getMessage());
      return;
    } catch(MediaException mediaexception) {
      //#debug
      if (RMSOption.debugEnabled) DebugLog.add2Log("NR WT me:"+mediaexception.toString());
      clearPlayer();
      //BTRadio._aStringV("MediaException:", "" + mediaexception.getMessage());
      return;
    }
  }
  
//    public final synchronized void _cvV()
//    {
//        _bvV();
//        try
//        {
//            m_bPlayer = Manager.createPlayer("capture://audio");
//            m_bPlayer.addPlayerListener(this);
//            m_bPlayer.realize();
//            m_cRecordControl = (RecordControl)m_bPlayer.getControl("RecordControl");
//            m_cRecordControl.setRecordLocation("file://c:/other/1.wav" );
//            m_cRecordControl.startRecord();
//            m_bPlayer.start();
//            return;
//        }
//        catch(IOException _ex)
//        {
//            _bvV();
//            return;
//        }
//        catch(MediaException _ex)
//        {
//            _bvV();
//        }
//    }
  private String format;
  public synchronized byte[] recordSoundArray() {
    clearPlayer();
    byte soundBytes[] = null;
    try {
      boolean b=RMSOption.soundOn;
      try{
        RMSOption.soundOn=true;
        MapSound.playTone(MapSound.GPSADDTRACKPOINT);
        try {Thread.sleep(400);} catch (InterruptedException ex) {}
        m_bPlayer = Manager.createPlayer("capture://audio");
        m_bPlayer.addPlayerListener(this);
        m_bPlayer.realize();
        m_cRecordControl = (RecordControl)m_bPlayer.getControl("RecordControl");
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        m_cRecordControl.setRecordStream(bytearrayoutputstream);
        format=m_cRecordControl.getContentType();
        m_cRecordControl.startRecord();
        m_bPlayer.start();
        try {
          //Thread.currentThread();
          Thread.sleep(m_aI);
        } catch(InterruptedException _ex) { }
        clearPlayer();
        soundBytes = bytearrayoutputstream.toByteArray();
        try {
          Thread.sleep(100);
        } catch(InterruptedException _ex) { }
        MapSound.playTone(MapSound.GPSADDTRACKPOINT);
      } finally{
        RMSOption.soundOn=b;
      }
    } catch(IOException ioexception) {
      //#debug
      if (RMSOption.debugEnabled) DebugLog.add2Log("NR WT:"+ioexception.toString());
      
      clearPlayer();
      //BTRadio._aStringV("IOException:", "" + ioexception.getMessage());
    } catch(MediaException mediaexception) {
      //#debug
      if (RMSOption.debugEnabled) DebugLog.add2Log("NR WT:"+mediaexception.toString());
      clearPlayer();
      //BTRadio._aStringV("MediaException:", "" + mediaexception.getMessage());
    }
    return soundBytes;
  }
  
//    public final synchronized void _aCanvasV(Canvas canvas)
//    {
//        _bvV();
//        try
//        {
//            m_bPlayer = Manager.createPlayer("capture://video");
//            m_bPlayer.realize();
//            m_dVideoControl = (VideoControl)m_bPlayer.getControl("VideoControl");
//            m_dVideoControl.initDisplayMode(1, canvas);
//            m_dVideoControl.setVisible(true);
//            m_bPlayer.start();
//            return;
//        }
//        catch(IOException _ex)
//        {
//            _bvV();
//            return;
//        }
//        catch(MediaException _ex)
//        {
//            _bvV();
//        }
//    }
  private static boolean works;
  public void run() {
    if (works)return;
    //#debug
    if (RMSOption.debugEnabled) DebugLog.add2Log("Call NR WT");
    try{
      works=true;
      byte[] sound = recordSoundArray();
      //#if SE_K750_E_BASEDEV
//#       try{
//#         sound = Util.readStream(this.getClass().getResourceAsStream("/mel/fanfare_2.amr"));
//#       }catch(Throwable t){}
//#endif
      if (sound!=null){
        //#debug
        if (RMSOption.debugEnabled) DebugLog.add2Log("NR WT:"+format+":"+sound.length);
        NetRadar.netRadar.nrIT.soundFormat=format;
        NetRadar.netRadar.nrIT.soundToSend=sound;
      }
    } finally{
      works=false;
    }
  }
}
