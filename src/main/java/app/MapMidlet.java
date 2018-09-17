/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package app;

import RPMap.FontUtil;
import RPMap.MapCanvas;
import RPMap.MapUtil;
import RPMap.RMSOption;
import RPMap.RMSSettings;
import lang.Lang;
import lang.LangHolder;
import misc.GraphUtils;
import misc.MapSound;
import misc.Util;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;

//#debug
//# import misc.DebugLog;

/**
 * @author rfk
 */
public class MapMidlet extends MIDlet {
    MapForms nM;

    private boolean started;

    public void startApp() {
        if (!started) {
            started = true;
            (new Thread(splashCanvas = new SplashCanvas(this))).start();
        }
    }

    private static SplashCanvas splashCanvas;
    private static int percent;

    public static void ulp(int progress) {
        percent = progress;
        if (splashCanvas == null) {
            return;
        }
        splashCanvas.repaint();
    }

    class SplashCanvas extends Canvas implements Runnable {
        boolean waitForAds;

        public void run() {
            MapUtil.version = mM.getAppProperty("MIDlet-Version");
            Display.getDisplay(mM).setCurrent(this);
            ulp(2);
            try {
                // Thread.sleep(50);
                try {
                    logo = Image.createImage("/img/nb.png");
                    rx = 45 + (int) (System.currentTimeMillis() % (dmaxx - 90));
                    ry = 20 + (int) (System.currentTimeMillis() % (dmaxy - 40));
                } catch (Throwable t) {
                }
                ulp(4);
                nM = new MapForms(mM);
                ulp(6);

                MapCanvas.display = Display.getDisplay(mM);
                // Thread.sleep(20);
                ulp(10);

                ulp(19);
                //   Thread.sleep(20);
                nM.initializeMap();
                ulp(20);
                if (RMSOption.adsNumber > 0) {
                    try {
                        logo = Image.createImage(
                                Util.getDataInputStream(
                                        RMSSettings.loadGeoData(RMSOption.adsNumber, RMSSettings.GEODATA_ADIMAGE)));
                        rx = dmaxx - logo.getWidth() / 2;
                        ry = dmaxy - logo.getHeight() / 2 - 1;
                        waitForAds = true;
                    } catch (Throwable t) {
                    }
                }

//          try{
//            if (!RMSOption.activeCMapFilename.equals(MapUtil.emptyString))
//              MapCanvas.map.oml=new OuterMapLoader(RMSOption.activeCMapFilename,true);
//          }catch(Throwable t) {}
//          ulp(55);
//          //waiting for map loading finish
//          try{
//            if (MapCanvas.map.oml!=null) {
//              while (!MapCanvas.map.oml.ready) Thread.sleep(50);
//            }
//          }catch(Throwable t) {}
                // Thread.sleep(20);
                ulp(60);
                //   Thread.sleep(20);

                //if (percent==50) throw new Exception("df");

                boolean om = RMSOption.onlineMap;
                try {
                    Image im = Image.createImage(splashCanvas.dmaxx - splashCanvas.dminx + 1, splashCanvas.dmaxy - splashCanvas.dminy + 1);

                    ulp(70);
                    // Thread.sleep(20);
                    RMSOption.onlineMap = false;
                    try {
                        MapCanvas.map.paint(im.getGraphics());
                        ulp(75);
                        Thread.sleep(20);
                    } catch (Throwable e) {
//#debug
//#                 DebugLog.add2Log("FR M:" + e);
                    }

                } catch (Throwable tt) {
                }
                RMSOption.onlineMap = om;

                ulp(80);
                //Thread.sleep(20);
                //ulp(90);
                //Thread.sleep(20);
                //while(System.currentTimeMillis()<endTime ) Thread.sleep(50);
                ulp(95);
                // splashCanvas.percent=90;
                try {
                    MapSound.playMIDISound(MapSound.APPSTARTTONE);
                } catch (Throwable t) {
                }
                //Thread.sleep(40);
                //Thread.sleep(500);
                //if (RMSOption.langSelected) {
                //  splashCanvas.percent=95;
                ulp(98);
                try {
                    if (RMSOption.netRadarWasActive) {
                        MapCanvas.map.startNetRadar(false);
                    }
                } catch (Throwable t) {
                    //#debug
//#             DebugLog.add2Log("NR mmst:" + t);
                }
                ulp(99);
                serviceRepaints();
                try {
                    if (RMSOption.gpsAutoReconnect) {
                        if (!RMSOption.lastBTDeviceName.equals(MapUtil.emptyString)) {
                            MapCanvas.map.startAutoGPSLookupDelayed();
                        }
                    }
                } catch (Throwable t) {
                    //#debug
//#             DebugLog.add2Log("GP sald:" + t);
                }

                // } else {
                //   getFormOptLang().removeCommand(getBack2Opt());
                //   MapCanvas.setCurrent(getFormOptLang());
                // }
                if (splashCanvas != null) {
                    splashCanvas.ready2close = true;
                }
                if (MapCanvas.autoShowMap) {
                    if (waitForAds) {
                        Thread.sleep(3000);
                    }
                    MapCanvas.setCurrentMap();
                    MapCanvas.map.repaint();
                }
            } catch (Throwable t) {
                splashCanvas.errMsg = t.toString();

                Alert a = new Alert(LangHolder.getString(Lang.error), t.toString() + "\n Report to developers, please", null, AlertType.ERROR);
                a.setTimeout(Alert.FOREVER);
                MapCanvas.setCurrent(a, splashCanvas);
                //MapCanvas.setCurrent(MapCanvas.map);
            }
            splashCanvas = null;
        }

        private String buildType = "";
        private String url = "";
        private String navig = "Navigator";
        private String yourn = "Your Java2ME + GPS";
        private String navsys = "Navigation System";
        private MapMidlet mM;

        SplashCanvas(MapMidlet mm) {
            mM = mm;
            url = "www." + mM.getAppProperty("MIDlet-Info-URL").substring(7);


            //#if CustomDevice || CustomDeviceSign
//#         buildType=" (release)";
//#else
            //#if SE_K750_E_BASE
//#         buildType=" (debug)";
//#else
            //#if SE_K750_E_BASEDEV
//#         buildType="(!t!)";
//#else
//#endif
//#endif
//#endif

            setFullScreenMode(true);
            dmaxx = getWidth();// g.getClipWidth();
            dmaxy = getHeight();//g.getClipHeight();


        }

        String errMsg;
        int dminx;
        int dminy;
        int dmaxx;
        int dmaxy;
        Image logo;
        double coursradPaint = -0.4;

        protected void keyPressed(int keyCode) {
            if (ready2close) {
                MapCanvas.setCurrent(MapCanvas.map);
            }
        }

        protected void pointerPressed(int x, int y) {
            if (ready2close) {
                MapCanvas.setCurrent(MapCanvas.map);
            }
        }

        int rx, ry;
        private boolean ready2close;

        protected void paint(Graphics g) {
            try {
                int dcx = (dminx + dmaxx) / 2;
                int dcy = (dminy + dmaxy) / 2;

                g.setColor(0x0);
                g.fillRect(dminx, dminy, dmaxx, dmaxy);

                g.setFont(FontUtil.LARGEFONTB);
                int fh = FontUtil.LARGEFONTB.getHeight();

                try {
                    coursradPaint += -0.03127;
                    if (true) {
                        int cd;
                        if ((dmaxx - dminx + 15) < (dmaxy - dminy)) {
                            cd = (int) ((dmaxx - dminx) / 1.49);
                        } else {
                            cd = (int) ((dmaxy - dminy - 30) / 1.49);
                        }

                        //if (compassOverMap) cd = (int) ((float)cd/1.2);

                        int sr = cd / 2;
                        int ps = cd / 10;
                        if (true) {
                            //g.setColor(0x00A000);
                            g.setColor(0x005F00);
                            g.fillTriangle(dcx - ps, dcy + sr + 2, dcx + ps, dcy + sr + 2, dcx, dcy - sr + ps + ps);
                        }

                        double crsr = -coursradPaint - MapUtil.PIdiv2;
                        int gpsx = dcx, gpsy = dcy;

                        //g.setColor(0xFFFFFF);
                        g.setColor(0x5F5F5F);
                        //base
                        g.drawArc(gpsx - sr, gpsy - sr, cd + 1, cd + 1, 0, 360);
                        //smaller
                        g.drawArc(gpsx - sr + 1, gpsy - sr + 1, cd - 1, cd - 1, 0, 360);
                        //
                        g.drawArc(gpsx - sr, gpsy - sr, cd, cd, 0, 360);
                        //ld
                        g.drawArc(gpsx - sr + 1, gpsy - sr + 1, cd, cd, 0, 360);
                        //d
                        g.drawArc(gpsx - sr, gpsy - sr + 1, cd, cd, 0, 360);
                        //l
                        g.drawArc(gpsx - sr + 1, gpsy - sr, cd, cd, 0, 360);


                        for (int r = 0; r < 24; r++) {
                            if ((r == 3) || (r == 9) || (r == 15) || (r == 21)) {
                                g.drawLine((int) (gpsx + sr * Math.cos(crsr + r * MapUtil.PIdiv12) + 1), (int) (1 + gpsy + sr * Math.sin(crsr + r * MapUtil.PIdiv12)),
                                        (int) (gpsx + (sr - ps * 1.5) * Math.cos(crsr + r * MapUtil.PIdiv12) + 1), (int) (1 + gpsy + (sr - ps * 1.5) * Math.sin(crsr + r * MapUtil.PIdiv12))
                                );
                            } else if ((r != 0) && (r != 6) && (r != 12) && (r != 18)) {
                                g.drawLine((int) (gpsx + sr * Math.cos(crsr + r * MapUtil.PIdiv12) + 1), (int) (1 + gpsy + sr * Math.sin(crsr + r * MapUtil.PIdiv12)),
                                        (int) (gpsx + (sr - ps) * Math.cos(crsr + r * MapUtil.PIdiv12) + 1), (int) (1 + gpsy + (sr - ps) * Math.sin(crsr + r * MapUtil.PIdiv12))
                                );
                            }
                        }

                        crsr = -coursradPaint - MapUtil.PIdiv2;

                        //if (true)
                        //  g.setColor(0xFFFFFF);
                        //else
                        //  g.setColor(0x66FFCC);

                        double ca = crsr + MapUtil.PIdiv2;
                        double cb = crsr - MapUtil.PIdiv2;
                        sr++;
                        String s = MapUtil.emptyString;
                        for (int cc = 4; cc > 0; cc--) {
                            ca = crsr + MapUtil.PIdiv2;
                            cb = crsr - MapUtil.PIdiv2;
                            g.fillTriangle((int) (dcx + (sr - ps) * Math.cos(crsr) + 1.5), (int) (dcy + (sr - ps) * Math.sin(crsr) + 1.5),
                                    (int) (dcx + sr * Math.cos(crsr) + 5 * Math.cos(ca) + 1.5), (int) (dcy + sr * Math.sin(crsr) + 5 * Math.sin(ca) + 1.5),
                                    (int) (dcx + sr * Math.cos(crsr) + 5 * Math.cos(cb) + 1.5), (int) (dcy + sr * Math.sin(crsr) + 5 * Math.sin(cb) + 1.5));
                            if (cc == 4) {
                                s = MapUtil.SH_NORTH;
                            }
                            if (cc == 3) {
                                s = MapUtil.SH_EAST;
                            }
                            if (cc == 2) {
                                s = MapUtil.SH_SOUTH;
                            }
                            if (cc == 1) {
                                s = MapUtil.SH_WEST;
                            }

                            g.drawString(s, (int) (dcx + (sr + ps) * Math.cos(crsr) + 1.5), (int) (dcy + (sr + ps) * Math.sin(crsr) - fh / 2 + 1.5), Graphics.TOP | Graphics.HCENTER);

                            crsr = crsr + MapUtil.PIdiv4;
                            ca = crsr + MapUtil.PIdiv2;
                            cb = crsr - MapUtil.PIdiv2;

                            if (cc == 4) {
                                s = MapUtil.SH_NORTHEAST;
                            }
                            if (cc == 3) {
                                s = MapUtil.SH_SOUTHEAST;
                            }
                            if (cc == 2) {
                                s = MapUtil.SH_SOUTHWEST;
                            }
                            if (cc == 1) {
                                s = MapUtil.SH_NORTHWEST;
                            }

                            g.drawString(s, (int) (dcx + (sr + ps) * Math.cos(crsr) + 1.5), (int) (dcy + (sr + ps) * Math.sin(crsr) - fh / 2 + 1.5), Graphics.TOP | Graphics.HCENTER);

                            crsr = crsr + MapUtil.PIdiv4;

                        }

                        sr--;
                        sr--;

                        if (true) {
                            //draw blue/red arrows
                            sr = sr - ps - 3 - ps / 3;
                            crsr = -coursradPaint - MapUtil.PIdiv2;
                            double acrsr = crsr + MapUtil.PI;
                            ca = crsr + MapUtil.PIdiv2;
                            cb = crsr - MapUtil.PIdiv2;

                            //g.setColor(0x0000FF);
                            g.setColor(0x00005F);
                            g.fillTriangle((int) (gpsx + sr * Math.cos(crsr) + 0.5), (int) (gpsy + sr * Math.sin(crsr) + 0.5),
                                    (int) (gpsx + ps * Math.cos(ca) + 0.5), (int) (gpsy + ps * Math.sin(ca) + 0.5),
                                    (int) (gpsx + ps * Math.cos(cb) + 0.5), (int) (gpsy + ps * Math.sin(cb) + 0.5));

                            //g.setColor(0xFF0000);
                            g.setColor(0x5F0000);
                            g.fillTriangle((int) (gpsx + sr * Math.cos(acrsr) + 0.5), (int) (gpsy + sr * Math.sin(acrsr) + 0.5),
                                    (int) (gpsx + ps * Math.cos(ca) + 0.5), (int) (gpsy + ps * Math.sin(ca) + 0.5),
                                    (int) (gpsx + ps * Math.cos(cb) + 0.5), (int) (gpsy + ps * Math.sin(cb) + 0.5));
                        }
                    }

                    if ((logo != null) && (!waitForAds)) {
                        g.drawImage(logo, rx, ry, Graphics.HCENTER | Graphics.VCENTER);
                    }

                } catch (Throwable t) {
                }
                int pc = percent * 2;
                if (pc > 100) {
                    pc = 100;
                }
                g.setColor(GraphUtils.fadeColor(0xD0D0FF, pc));
                g.drawString("Map Mobile", dcx, dcy, Graphics.BOTTOM | Graphics.HCENTER);
                g.setColor(GraphUtils.fadeColor(0xFFD0D0, pc));
                //g.setColor(0xFFD0D0);

                g.drawString(navig, dcx, dcy, Graphics.TOP | Graphics.HCENTER);
                g.setColor(GraphUtils.fadeColor(0xFFFFFF, percent));
                //g.setColor(0xFFFFFF);
                g.setFont(Font.getDefaultFont());
                int fhd = g.getFont().getHeight();
                g.drawString(yourn, dcx, dmaxy - fhd - fhd - fhd, Graphics.TOP | Graphics.HCENTER);
                g.drawString(navsys, dcx, dmaxy - fhd - fhd, Graphics.TOP | Graphics.HCENTER);

                g.setColor(0xFFFFFF);
                Font f = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL);
                g.setFont(f);
                try {

                    g.drawString("v" + MapUtil.version + buildType, dmaxx, dmaxy, Graphics.BOTTOM | Graphics.RIGHT);
                    //g.drawString("www."+getAppProperty("MIDlet-Info-URL").substring(7), dminx, dminy, Graphics.TOP| Graphics.LEFT);
                    if (errMsg == null) {
                        g.drawString(url, dmaxx / 2, dminy, Graphics.TOP | Graphics.HCENTER);
                    } else {
                        g.drawString("Err:" + errMsg, dminx, dminy, Graphics.TOP | Graphics.LEFT);
                    }
                } catch (Throwable ttt) {
                }
//g.drawString("image here!", 15, 75, Graphics.BOTTOM| Graphics.LEFT );
                g.drawLine(dminx, dmaxy - 1, dmaxx * percent / 100, dmaxy - 1);
                g.drawString(String.valueOf(percent), dminx, dmaxy - 1, Graphics.BOTTOM | Graphics.LEFT);
                if ((logo != null) && (waitForAds)) {
                    g.drawImage(logo, rx, ry, Graphics.HCENTER | Graphics.VCENTER);
                }

            } catch (Throwable t) {
            }
        }

    }


    public void pauseApp() {
        nM.pauseApp();
    }

    public void destroyApp(boolean unconditional) {
        nM.destroyApp(unconditional);
    }

}
