/*
 * LightTimer.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package misc;

import RPMap.MapCanvas;
import RPMap.RMSOption;
//#if CustomDevice || CustomDeviceSign
//# import app.MapForms;
//# //import com.nokia.mid.ui.DeviceControl;
//# //import com.siemens.mp.game.Light;
//# //import com.motorola.funlight.*;
//# //import mmpp.media.BackLight;
//# 
//#else
//#endif
import java.util.TimerTask;

/**
 *
 * @author RFK
 */

public final class LightTimer extends TimerTask {
  
  public LightTimer() {
    super();
    lastPressed=System.currentTimeMillis();
  }
  
  static Object lt;
  public static void scheduleMe(){

    if (lt!=null) {
      LightTimer t =(LightTimer)lt;
      t.cancel();
    }
    lt=new LightTimer();
    //((LightTimer)lt).run();
    //lt=null;
    MapCanvas.timer.schedule( (LightTimer)lt, 200,RMSOption.blinkInterval);
//        MapMidlet.mm.timer.schedule( new LightTimer(), 500,RMSOption.blinkInterval);
    
  }
  public static long lastPressed;
  private boolean firstRun=true;
  //private boolean noNokiaSupport;
  //private boolean noBSSupport;
  //private boolean noMTSupport;
  //private boolean noLGSupport;
  //private boolean detected;
  public final void run() {
    try {
  /*
      if (noNokiaSupport&&noBSSupport&&noMTSupport&&noLGSupport) return;
 //   try {
      
      if (!detected) {
        try{
          Class.forName("com.nokia.mid.ui.DeviceControl");
          detected=true;
          noBSSupport=true;
          noMTSupport=true;
          noLGSupport=true;
        }catch(Throwable tt){noNokiaSupport=true;}
        if (!detected)
        try{
          Class.forName("com.siemens.mp.game.Light");
          detected=true;
          noMTSupport=true;
          noLGSupport=true;
        }catch(Throwable tt){noBSSupport=true;}
        if (!detected)
        try{
          Class.forName("com.motorola.funlight.FunLight");
          detected=true;
          noLGSupport=true;
        }catch(Throwable tt){noMTSupport=true;}
        if (!detected)
        try{
          Class.forName("mmpp.media.BackLight");
          detected=true;
        }catch(Throwable tt){noLGSupport=true;}
      }
      detected=true;
      if (noNokiaSupport&&noBSSupport) return;
      
    */
      if (!firstRun) {
        if (lastPressed>System.currentTimeMillis()-5000) return;
        //   if (!MapCanvas.map.isShown()) return;
      }
      firstRun=false;
      
      if ((!MapCanvas.lightControlled)&&(!RMSOption.lightOn)) return;

//#if CustomDevice || CustomDeviceSign
//#        if (TLightController.GetInstance(MapForms.mM).CanControl()){
//#          int lvl=(RMSOption.light50)?30:100;
//#          if (!RMSOption.lightOn) lvl=0;
//# 
//#          if (RMSOption.blinkLight)
//#            if ( RMSOption.lightOn)
//#              TLightController.GetInstance(MapForms.mM).SetBrightness(0);
//# 
//#         TLightController.GetInstance(MapForms.mM).SetBrightness(lvl);
//#       }
//# 
//# 
//#      /* if (!noNokiaSupport){
//#         if (RMSOption.blinkLight)
//#           if ( RMSOption.lightOn) DeviceControl.setLights(0, 0);
//#           else DeviceControl.setLights(0, lvl);
//#         
//#         if ( RMSOption.lightOn) DeviceControl.setLights(0, lvl);
//#         else DeviceControl.setLights(0, 0);
//#         return;
//#       }
//#       
//#       if (!noBSSupport){
//#         if (RMSOption.blinkLight)
//#           if ( RMSOption.lightOn) Light.setLightOff();
//#           else Light.setLightOn();
//#         
//#         if ( RMSOption.lightOn) Light.setLightOn();
//#         else Light.setLightOff();
//#         return;
//#       }
//#       */
//#else
//#endif
      
    }catch(Throwable e){
//#mdebug
      if (RMSOption.debugEnabled)
        DebugLog.add2Log("setLight:"+e.toString());
//#enddebug
    }
  }
  
  
}

