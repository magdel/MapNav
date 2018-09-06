package misc;

//*******************************************************************************
//*******************************************************************************
// Universal backlight control class
// Copyright (C) 2006 by Roman Lut
// Free for any use. Please credit me in "About" box :)
//*******************************************************************************
//*******************************************************************************
/*
Singleton. Reference with TLightController.GetInstance()
Default state is ENABLE.

Query TLightController.GetInstance(midlet).CanControl() to see wheether class is able to control backlight.
Query TLightController.GetInstance(midlet).CanControlBrightness() to see wheether class is able to control brightness.
Use TLightController.GetInstance(midlet).SetBrightness(brightness) to control backlight brightness.
brightness is 0 (minimum) to 255 (maximum);

Controlling method is determined automatically.
 */
import RPMap.MapCanvas;
import javax.microedition.midlet.MIDlet;
//import com.siemens.mp.game.Light;
//import com.nokia.mid.ui.DeviceControl;
//import com.motorola.multimedia.Lighting;
//import com.motorola.funlight.*;
//import javax.microedition.lcdui.Display;
//import com.samsung.util.LCDLight;
import java.util.TimerTask;
//import com.samsung.util.LCDLight;
//import mmpp.media.BackLight;

//=============================================
// TLightController
//=============================================
//#define EXT_LIGHTa
public class TLightController extends TimerTask {
    //light control method

    private static final byte LIGHT_NONE=0;
    private static final byte LIGHT_NOKIA=2;
    private static final byte LIGHT_MIDP20=7;
   
    private static final byte LIGHT_SIEMENS=1;
    private static final byte LIGHT_MOTOROLA_LIGHT=3;
    private static final byte LIGHT_MOTOROLA_FUNLIGHT=4;
    private static final byte LIGHT_SAMSUNG=5;
    private static final byte LIGHT_LG=6;
   
    private byte method;
    private static TLightController inst=null;
    private com.motorola.funlight.Region r1, r2, r3;
    //Timer   funLightsTimer;
//---- for timertask -----
    private static MIDlet midletRef;
    private static int curBrightness;
    //------------------------

    //============================
    // void ApplyState()
    //============================
    private final void ApplyState() {
        switch (method) {
            case LIGHT_NOKIA: {
                com.nokia.mid.ui.DeviceControl.setLights(0, curBrightness);
            }
            break;
            case LIGHT_MIDP20:
                if (curBrightness>0){
                    javax.microedition.lcdui.Display.getDisplay(midletRef).flashBacklight(0x7fffffff);
                } else {
                    javax.microedition.lcdui.Display.getDisplay(midletRef).flashBacklight(0);
                }
                break;
            //#ifdef EXT_LIGHT
//#             case LIGHT_SIEMENS: {
//#                 if (curBrightness>0){
//#                     com.siemens.mp.game.Light.setLightOn();
//#                 } else {
//#                     com.siemens.mp.game.Light.setLightOff();
//#                 }
//#             }
//#             break;
//# 
//# 
//#             case LIGHT_MOTOROLA_LIGHT:
//#                 if (curBrightness>0){
//#                     com.motorola.multimedia.Lighting.backlightOn();
//#                 } else {
//#                     com.motorola.multimedia.Lighting.backlightOff();
//#                 }
//#                 break;
//# 
//#             case LIGHT_MOTOROLA_FUNLIGHT:
//#                 int cc=curBrightness*255/100;
//#                 int c=cc+(cc<<8)+(cc<<16);
//#                 com.motorola.funlight.FunLight.getRegion(1).setColor(c);
//#                 r1.setColor(0);
//#                 r2.setColor(0);
//#                 r3.setColor(0);
//#                 break;
//# 
//#             case LIGHT_SAMSUNG:
//#                 if (curBrightness>0){
//#                     com.samsung.util.LCDLight.on(0x0fffffff);  //max 60 seconds ?
//#                 } else {
//#                     com.samsung.util.LCDLight.off();
//#                 }
//#                 break;
//# 
//#             case LIGHT_LG:
//#                 if (curBrightness>0){
//#                     mmpp.media.BackLight.on(0x0fffffff);
//#                 } else {
//#                     mmpp.media.BackLight.off();
//#                 }
//#                 break;
            //#endif

        }

    }

    //============================
    // run() (timer task)
    //============================
    public final void run() {
        try {
            ApplyState();
        } catch (Throwable t) {
        }
    }

    //=============================================
    // public TLightController()
    //=============================================
    private TLightController(MIDlet midlet) {
        curBrightness=(byte) 100;
        midletRef=midlet;

        method=LIGHT_NONE;

            try {
                Class.forName("com.nokia.mid.ui.DeviceControl");
                method=LIGHT_NOKIA;
            } catch (Exception e3) {
//#ifdef EXT_LIGHT
//#         try {
//#             Class.forName("com.siemens.mp.game.Light");
//#             method=LIGHT_SIEMENS;
//#         } catch (Exception e) {
//# 
//#                 try {
//#                     Class.forName("com.motorola.funlight.FunLight");
//#                     method=LIGHT_MOTOROLA_FUNLIGHT;
//#                 } catch (Exception e1) {
//# 
//#                     try {
//# 
//#                         Class.forName("com.motorola.multimedia.Lighting");
//#                         method=LIGHT_MOTOROLA_LIGHT;
//#                     } catch (Exception e2) {
//# 
//# 
//#                         try {
//#                             Class.forName("com.samsung.util.LCDLight");
//# 
//#                             if (com.samsung.util.LCDLight.isSupported()==false){
//# 
//#                                 throw new Exception();
//#                             }
//#                             method=LIGHT_SAMSUNG;
//#                         } catch (Exception e4) {
//# 
//#                             try {
//#                                 Class.forName("mmpp.media.BackLight");
//# 
//#                                 method=LIGHT_LG;
//#                             } catch (Exception e5) {
//#endif
                                if (System.getProperty("microedition.profiles").indexOf("2.0")>0){
                                    method=LIGHT_MIDP20;
                                }
//#ifdef EXT_LIGHT
//# 
//#                             }
//#                         }
//#                     }
//#                 }
//#             }
        //#endif
        }
//#ifdef EXT_LIGHT
//#         if (method==LIGHT_MOTOROLA_FUNLIGHT){
//#             com.motorola.funlight.FunLight.getControl();
//#             r1=com.motorola.funlight.FunLight.getRegion(2);
//#             r2=com.motorola.funlight.FunLight.getRegion(3);
//#             r3=com.motorola.funlight.FunLight.getRegion(4);
//# 
//#             //funLightsTimer = new Timer();
//#             MapCanvas.timer.scheduleAtFixedRate(this, 0, 100);
//#         } else {
              //#endif
            //funLightsTimer = new Timer();
            MapCanvas.timer.scheduleAtFixedRate(this, 0, 3000);
//#ifdef EXT_LIGHT
//#         }
          //#endif
        ApplyState();
    }

    //=============================================
    //GetInstance()
    //=============================================
    public static TLightController GetInstance(MIDlet midlet) {
        if (inst==null){
            inst=new TLightController(midlet);
        }
        return inst;
    }

    //=============================================
    //public boolean CanControl()
    //=============================================
    public boolean CanControl() {
        return method!=LIGHT_NONE;
    }

    //=============================================
    //public boolean CanControlBrightness()
    //=============================================
    public boolean CanControlBrightness() {
        return (method==LIGHT_NOKIA)||(method==LIGHT_MOTOROLA_FUNLIGHT);
    }

    //=============================================
    //public void SetBrightness()
    //=============================================
    public void SetBrightness(int brightness) {
        if (curBrightness==brightness){
            return;
        }
        curBrightness=brightness;
        ApplyState();
    }
}

