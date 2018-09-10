/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import RPMap.FileMapLoader;
import RPMap.FileMapRouteLoader;
import RPMap.FileTrackSend;
import RPMap.HTTPMapRouteLoader;
import RPMap.MapCanvas;
import RPMap.MapPoint;
import RPMap.MapRoute;
import RPMap.RMSRoute;
import RPMap.MapRouteLoader;
import RPMap.ScreenSend;
import RPMap.RouteSend;
import RPMap.MapTile;
import RPMap.MapUtil;
import RPMap.RMSOption;
import RPMap.OSMTrackSend;
import RPMap.WebTrackSend;
import gpspack.LocStarter;

//#if Flickr
//# import camera.FlickrList;
//#endif
import cloudmade.CloudRouteResult;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.RecordStore;
import kml.KMLMapPoint;
import kml.KMLMapRoute;
import kml.KMLSearchResult;
import lang.Lang;
import lang.LangHolder;
import misc.BrowseList;
import misc.DataSenderThread;
//#debug
//# import misc.DebugLog;
import misc.GPSClubLoad;
//#if GeoCaching
//# import misc.GeoCacheLoad;
//#endif
import misc.LastVersion;
import misc.LocationCanvas;
import misc.GTCanvas;
//#debug
//# import misc.LogSend;
import misc.MNSInfo;
import misc.MapSound;
import misc.FileDialog;
import misc.SMSRecomend;
import netradar.NetRadar;
import gpspack.GPSReader;
import javax.microedition.lcdui.TextBox;
import misc.ProgressForm;
//#if Service
//# import misc.RMSExportImport;
//#endif
import misc.RouteEditList;
import misc.SMSSender;
import misc.GeneralFeedback;
import misc.ProgressStoppable;
import misc.SportCanvas;
import misc.StringSelectList;
import misc.TR102Sender;

/**
 * @author rfk
 */
public class MapForms implements GeneralFeedback, CommandListener, ItemCommandListener {

// HINT - Uncomment for accessing new MIDlet Started/Resumed logic.
//    private boolean midletPaused = false;
//<editor-fold defaultstate="collapsed" desc=" Generated Fields ">//GEN-BEGIN:|fields|0|
    private Form gotoForm;
    private TextField textLonFieldg;
    private TextField textLatFieldg;
    private StringItem stringCoordEx;
    private Form formOptGen;
    private ChoiceGroup choiceScale;
    private TextField textWorkPath;
    private List listOpt;
    private Form formOptAppear;
    private ChoiceGroup choiceFontSize;
    private ChoiceGroup choiceFontStyle;
    private ChoiceGroup choiceAppearCh;
    private ChoiceGroup choiceViewports2;
    private ChoiceGroup choiceShadowColor;
    private ChoiceGroup choiceForeColor;
    private ChoiceGroup choiceViewports3;
    private ChoiceGroup choiceViewports4;
    private Form formOptAlr;
    private ChoiceGroup choiceSounds;
    private Gauge gaugeVolume;
    private TextField textMaxSpd;
    private TextField textMaxClm;
    private TextField textMaxDsc;
    private Form formOptKeys;
    private ChoiceGroup choiceKey1;
    private ChoiceGroup choiceKeys;
    private ChoiceGroup choice5Add;
    private ChoiceGroup choiceKey6;
    private ChoiceGroup choiceKey6Hold;
    private ChoiceGroup choiceKey4Hold;
    private ChoiceGroup choiceKey5Hold;
    private ChoiceGroup choiceKey3Hold;
    private ChoiceGroup choiceKey4;
    private ChoiceGroup choiceKey1Hold;
    private ChoiceGroup choiceKey3;
    private ChoiceGroup choiceKeyCH;
    private ChoiceGroup choiceKey0;
    private ChoiceGroup choiceKey9Hold;
    private ChoiceGroup choiceKey9;
    private ChoiceGroup choiceKey7Hold;
    private ChoiceGroup choiceKey7;
    private ChoiceGroup choiceKey2;
    private ChoiceGroup choiceKey8;
    private ChoiceGroup choiceKey2Hold;
    private ChoiceGroup choiceKey8Hold;
    private Form formOptGeo;
    private ChoiceGroup choiceCoordType;
    private ChoiceGroup choiceDatum;
    private ChoiceGroup choiceProj;
    private ChoiceGroup choiceMeasure;
    private ChoiceGroup choiceRouteSearch;
    private Form formOptGPS;
    private ChoiceGroup choiceGpsOpt;
    private ChoiceGroup choiceGPSKind;
    private ChoiceGroup choiceOnlineUrlSUR;
    private ChoiceGroup choiceOnlineUrlMAP;
    private TextField textGPSCOM;
    private Form formOptTr;
    private ChoiceGroup choiceTrackPeriod;
    private ChoiceGroup choiceTrackDist;
    private ChoiceGroup choiceTrackPC;
    private ChoiceGroup choiceTrackUse;
    private ChoiceGroup choiceAutoWpt;
    private ChoiceGroup choiceRouteProx;
    private TextField textMaxTP;
    private ChoiceGroup choiceAutoWPTType;
    private Form formOptMaps;
    private ChoiceGroup choiceEMOpts;
    private ChoiceGroup choiceMaps;
    private TextField textOSMURL;
    private TextField textOnlineUrlSUR;
    private TextField textOnlineUrlMAP;
    private Form formOptLang;
    private ChoiceGroup choiceLang;
    private Form formOptRadar;
    private ChoiceGroup choiceNetRadar;
    private TextField textRadLogin;
    private TextField textRadPass;
    private ChoiceGroup choiceNRTrackPeriod;
    private StringItem stringRadarBytes;
    private TextField textOSMPass;
    private TextField textOSMLogin;
    private Form formAskReset;
    private StringItem stringAskReset;
    private Form formOptCache;
    private Gauge gaugeCache;
    private StringItem stringMapSize;
    private StringItem stringCacheSize;
    private ChoiceGroup choiceCacheOpt;
    private TextField textFileCachePath;
    private Form formOptDeb;
    private TextField textLogSavePath;
    private StringItem stringLog;
    private ChoiceGroup choiceDebug;
    private Form formRouteURL;
    private ChoiceGroup choiceCP;
    private ChoiceGroup choiceRouteURL;
    private TextField textRouteURL;
    private ChoiceGroup choiceImpFormat;
    private List listRoute;
    private List listSearch;
    private List listMore;
    private Form formSearch;
    private TextField textSearch;
    private Form formSendMMS;
    private ChoiceGroup choiceSerCount;
    private ChoiceGroup choiceImageType;
    private ChoiceGroup choiceSerInterval;
    private TextField textAddrMMS;
    private TextField textAddrFile;
    private ChoiceGroup choicePicSize;
    private ChoiceGroup choiceSendType;
    private Form formService;
    private StringItem stringServiceInfo;
    private TextField textBackupPath;
    private ChoiceGroup choiceExpType;
    private Form formRecFr;
    private TextField textYourName;
    private TextField textFrPhone;
    private Form formAbout;
    private StringItem stringURLVer;
    private StringItem stringFreeMem;
    private StringItem stringItem1;
    private StringItem stringItemLastDebug;
    private StringItem stringItemLastVersion;
    private Form formHelp;
    private StringItem stringItem2;
    private List listNav;
    private List listWP;
    private Form formTelInput;
    private ChoiceGroup choiceTels;
    private TextField textTelNum;
    private ChoiceGroup choiceSMSType;
    private List listTrack;
    private Form formDeleteAll;
    private StringItem stringDelAllQ;
    private Form formAddOMap;
    private StringItem stringMapFree;
    private TextField textCMapName;
    private TextField textOMapName;
    private List listBT;
    private Form formSaveTrack;
    private ChoiceGroup choiceOX_S;
    private ChoiceGroup choiceCP_S;
    private TextField textTrackSavePath;
    private ChoiceGroup choiceExpCount;
    private Form formSaveChanges;
    private StringItem stringSaveQ;
    private List listTrackMenu;
    private List listRouteMenu;
    private List listKML;
    private List listOMaps;
    private List listIMaps;
    private List listWPMenu;
    private List listSKMLMenu;
    private List listSKML;
    private Command itemGoto1;
    private Command cancel2Map;
    private Command back2Opt;
    private Command close2Map;
    private Command itemSelectOpt;
    private Command itemSaveOpt;
    private Command itemCommandListCOM;
    private Command screenReset;
    private Command cancelNo;
    private Command okYes;
    private Command helpClearCache;
    private Command itemBrowseMap;
    private Command itemClear;
    private Command itemSaveLog;
    private Command itemSaveLogWeb;
    private Command itemLoadMap;
    private Command itemRouteDelete;
    private Command itemCreateRoute;
    private Command itemCreate;
    private Command itemSelect1;
    private Command itemResults;
    private Command itemSearch;
    private Command item1Send;
    private Command itemWPDelete;
    private Command itemMerge;
    private Command itemClearMap;
    private Command itemBrowseFile;
    private Command itemLoadTrack;
    private Command itemExport;
    private Command itemTrackDelete;
    private Command itemComLoadMap;
    private Command itemSelBT;
    private Command itemNext1;
    private Command item1Save;
    private Command itemCommand;
    private Command itemTest;
    private Image imageMarks;
    private Image imageTracks;
    private Image imageGEGreen;
    private Image imageWPTs;
    private Image imageRoutes;
    private Image imageFold;
    private Image imageMapE;
    private Image imageMap;
    private Image imageGEGray;
    private Image imageGEYel;
    private Image imageTrW;
    private Image imageTrO;
    private Image imageTrS;

    private TextBox textBox;

    private ChoiceGroup choiceGPSReconnectDelay;


// HINT - Uncomment for accessing new MIDlet Started/Resumed logic.
// NOTE - Be aware of resolving conflicts of the constructor.
//    /**
//     * The ConvertedMapMidlet constructor.
//     */
//    public ConvertedMapMidlet() {
//    }
//<editor-fold defaultstate="collapsed" desc=" Generated Methods ">//GEN-BEGIN:|methods|0|
//</editor-fold>//GEN-END:|methods|0|
//<editor-fold defaultstate="collapsed" desc=" Generated Method: initialize ">//GEN-BEGIN:|0-initialize|0|0-preInitialize
    /**
     * Initilizes the application.
     * It is called only once when the MIDlet is started. The method is called before the <code>startMIDlet</code> method.
     */
    private void initialize() {//GEN-END:|0-initialize|0|0-preInitialize
        // write pre-initialize user code here
//GEN-LINE:|0-initialize|1|0-postInitialize
        // write post-initialize user code here
    }//GEN-BEGIN:|0-initialize|2|
//</editor-fold>//GEN-END:|0-initialize|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Method: startMIDlet ">//GEN-BEGIN:|3-startMIDlet|0|3-preAction
    /**
     * Performs an action assigned to the Mobile Device - MIDlet Started point.
     */
    public void startMIDlet() {//GEN-END:|3-startMIDlet|0|3-preAction
        // write pre-action user code here
//GEN-LINE:|3-startMIDlet|1|3-postAction
        // write post-action user code here
    }//GEN-BEGIN:|3-startMIDlet|2|
//</editor-fold>//GEN-END:|3-startMIDlet|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Method: resumeMIDlet ">//GEN-BEGIN:|4-resumeMIDlet|0|4-preAction
    /**
     * Performs an action assigned to the Mobile Device - MIDlet Resumed point.
     */
    public void resumeMIDlet() {//GEN-END:|4-resumeMIDlet|0|4-preAction
        // write pre-action user code here
//GEN-LINE:|4-resumeMIDlet|1|4-postAction
        // write post-action user code here
    }//GEN-BEGIN:|4-resumeMIDlet|2|
//</editor-fold>//GEN-END:|4-resumeMIDlet|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Method: switchDisplayable ">//GEN-BEGIN:|5-switchDisplayable|0|5-preSwitch
    /**
     * Switches a current displayable in a display. The <code>display</code> instance is taken from <code>getDisplay</code> method. This method is used by all actions in the design for switching displayable.
     * @param alert the Alert which is temporarily set to the display; if <code>null</code>, then <code>nextDisplayable</code> is set immediately
     * @param nextDisplayable the Displayable to be set
     */
    public void switchDisplayable(Alert alert, Displayable nextDisplayable) {//GEN-END:|5-switchDisplayable|0|5-preSwitch
        // write pre-switch user code here
        Display display=getDisplay();//GEN-BEGIN:|5-switchDisplayable|1|5-postSwitch
        if (alert==null){
            display.setCurrent(nextDisplayable);
        } else {
            display.setCurrent(alert, nextDisplayable);
        }//GEN-END:|5-switchDisplayable|1|5-postSwitch
        // write post-switch user code here
    }//GEN-BEGIN:|5-switchDisplayable|2|
//</editor-fold>//GEN-END:|5-switchDisplayable|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Method: commandAction for Displayables ">//GEN-BEGIN:|7-commandAction|0|7-preCommandAction
    /**
     * Called by a system to indicated that a command has been invoked on a particular displayable.
     * @param command the Command that was invoked
     * @param displayable the Displayable where the command was invoked
     */
    public void commandAction(Command command, Displayable displayable) {//GEN-END:|7-commandAction|0|7-preCommandAction
        // Insert global pre-action code here
        if (displayable==textBox){
            if (command==item1Save){
               String newName = textBox.getString();
               MapRoute mr=MapCanvas.map.rmss.loadRoute(listTrack.getSelectedIndex()-1, MapRoute.TRACKKIND);
               mr.name=newName;
               MapCanvas.map.rmss.saveRoute(mr);
               showSelectTrackForm(true);

            }else{
                MapCanvas.setCurrent(listTrackMenu);
            }
        } else if (displayable==formAbout){//GEN-BEGIN:|7-commandAction|1|428-preAction
            if (command==back2Opt){//GEN-END:|7-commandAction|1|428-preAction
                back2More();
//GEN-LINE:|7-commandAction|2|428-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|3|557-preAction
        } else if (displayable==formAddOMap){
            if (command==back2Opt){//GEN-END:|7-commandAction|3|557-preAction
                // Insert pre-action code here
                if (mtc==EXTMAP) {
                    getDisplay().setCurrent(getListOMaps());
                }
                if (mtc==INTMAP) {
                    getDisplay().setCurrent(getListIMaps());
                }

//GEN-LINE:|7-commandAction|4|557-postAction
                // Insert post-action code here
            } else if (command==itemBrowseMap){//GEN-LINE:|7-commandAction|5|558-preAction
                // Insert pre-action code here
                if (mtc==EXTMAP) {
                    getDisplay().setCurrent(new BrowseList(formAddOMap, textCMapName, BrowseList.FILEBROWSE, ".MNO", textOMapName));
                } else {
                    getDisplay().setCurrent(new BrowseList(formAddOMap, textCMapName, BrowseList.FILEBROWSE, ".MNM", textOMapName));
                }

//GEN-LINE:|7-commandAction|6|558-postAction
                // Insert post-action code here
            } else if (command==itemComLoadMap){//GEN-LINE:|7-commandAction|7|559-preAction
                // Insert pre-action code here

                String url=textCMapName.getString();
                String name=textOMapName.getString();

                if (mtc==EXTMAP){
                    RMSOption.addOMap(name, url);
                    showListOMaps();
                } else {
                    if (url!=null){
                        String[] ns=new String[RMSOption.IMAPS_NAMES.length+1];

                        System.arraycopy(RMSOption.IMAPS_NAMES, 0, ns, 0, RMSOption.IMAPS_NAMES.length);
                        ns[ns.length-1]=name;
                        RMSOption.IMAPS_NAMES=ns;

                        ns=new String[RMSOption.IMAPS_RMST.length+1];
                        System.arraycopy(RMSOption.IMAPS_RMST, 0, ns, 0, RMSOption.IMAPS_RMST.length);
                        ns[ns.length-1]="INTM"+String.valueOf(System.currentTimeMillis())+'T';
                        RMSOption.IMAPS_RMST=ns;

                        ns=new String[RMSOption.IMAPS_RMSP.length+1];
                        System.arraycopy(RMSOption.IMAPS_RMSP, 0, ns, 0, RMSOption.IMAPS_RMSP.length);
                        ns[ns.length-1]="INTM"+String.valueOf(System.currentTimeMillis())+'P';
                        RMSOption.IMAPS_RMSP=ns;

                        float[] nsf=new float[RMSOption.IMAPS_CENT.length+3];
                        System.arraycopy(RMSOption.IMAPS_CENT, 0, nsf, 0, RMSOption.IMAPS_CENT.length);
                        RMSOption.IMAPS_CENT=nsf;

                        MapCanvas.map.rmss.switchIntMap(RMSOption.IMAPS_RMSP.length);
                        //RMSOption.setByteOpt(RMSOption.BO_INTMAPINDEX, (byte)RMSOption.IMAPS_RMSP.length);
                        //MapCanvas.map.rmss.switchIntMap(RMSOption.IMAPS_RMSP.length);
                        showListIMaps();
                        MapCanvas.setCurrent(new ProgressForm(LangHolder.getString(Lang.loading), LangHolder.getString(Lang.intmaps), new FileMapLoader(url, listIMaps), getFormAddOMap()));
                        //MapCanvas.setCurrent(new ProgressForm(LangHolder.getString(Lang.loading),"GeoCaching.ru",new GeoCacheLoad(MapCanvas.map),listMore));

                        // fileMapLoader = new FileMapLoader(addrs1);
                        ///MapCanvas.map.loadMap(url);
                    }
                    // showListIMaps();
                }

//GEN-LINE:|7-commandAction|8|559-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|9|278-preAction
        } else if (displayable==formAskReset){
            if (command==cancelNo){//GEN-END:|7-commandAction|9|278-preAction
                // Insert pre-action code here
                switchDisplayable(null, getListOpt());//GEN-LINE:|7-commandAction|10|278-postAction
                // Insert post-action code here
            } else if (command==okYes){//GEN-LINE:|7-commandAction|11|276-preAction
                resetSettings();  // Insert pre-action code here
//GEN-LINE:|7-commandAction|12|276-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|13|548-preAction
        } else if (displayable==formDeleteAll){
            if (command==cancelNo){//GEN-END:|7-commandAction|13|548-preAction
                back2RoutesForm(true);
// Insert pre-action code here
//GEN-LINE:|7-commandAction|14|548-postAction
                // Insert post-action code here
            } else if (command==okYes){//GEN-LINE:|7-commandAction|15|547-preAction
                deleteAllOk();// Insert pre-action code here
//GEN-LINE:|7-commandAction|16|547-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|17|423-preAction
        } else if (displayable==formHelp){
            if (command==back2Opt){//GEN-END:|7-commandAction|17|423-preAction
                back2More();
//GEN-LINE:|7-commandAction|18|423-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|19|81-preAction
        } else if (displayable==formOptAlr){
            if (command==back2Opt){//GEN-END:|7-commandAction|19|81-preAction
                // Insert pre-action code here
                switchDisplayable(null, getListOpt());//GEN-LINE:|7-commandAction|20|81-postAction
                // Insert post-action code here
            } else if (command==itemSaveOpt){
                saveOptAlerts();
                back2Opt(false);

            } else if (command==itemTest){
                saveOptAlerts();
                MapSound.playSound(MapSound.FINISHSOUND);
            }
        } else if (displayable==formOptAppear){
            if (command==back2Opt){//GEN-END:|7-commandAction|23|37-preAction
                // Insert pre-action code here
                switchDisplayable(null, getListOpt());//GEN-LINE:|7-commandAction|24|37-postAction
                // Insert post-action code here
            } else if (command==itemSaveOpt){//GEN-LINE:|7-commandAction|25|35-preAction
                // Insert pre-action code here
                saveOptAppear();
                back2Opt(false);
//GEN-LINE:|7-commandAction|26|35-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|27|268-preAction
        } else if (displayable==formOptCache){
            if (command==back2Opt){//GEN-END:|7-commandAction|27|268-preAction
                // Insert pre-action code here
                switchDisplayable(null, getListOpt());//GEN-LINE:|7-commandAction|28|268-postAction
                // Insert post-action code here
            } else if (command==helpClearCache){//GEN-LINE:|7-commandAction|29|270-preAction
                clearInetPicCache(); // Insert pre-action code here
                showSettingForm();
            } else if (command==itemSaveOpt){
                saveOptCache();
                back2Opt(false);

            } else if (command==screenReset){
                switchDisplayable(null, getFormAskReset());
            }//GEN-BEGIN:|7-commandAction|35|289-preAction
        } else if (displayable==formOptDeb){
            if (command==back2Opt){//GEN-END:|7-commandAction|35|289-preAction
                // Insert pre-action code here
                switchDisplayable(null, getListOpt());//GEN-LINE:|7-commandAction|36|289-postAction
                // Insert post-action code here
            } else if (command==itemClear){//GEN-LINE:|7-commandAction|37|291-preAction
                stringLog.setText(MapUtil.emptyString);// Insert pre-action code here
//#mdebug
//#                 DebugLog.clear(); // Insert pre-action code here
//#enddebug

//GEN-LINE:|7-commandAction|38|291-postAction
                // Insert post-action code here
            } else if (command==itemSaveLog){//GEN-LINE:|7-commandAction|39|293-preAction

//#mdebug
//#                 new LogSend(getFormOptDeb(), textLogSavePath.getString(), LogSend.FILESEND); // Insert pre-action code here
//#enddebug
//GEN-LINE:|7-commandAction|40|293-postAction
                // Insert post-action code here
            } else if (command==itemSaveLogWeb){//GEN-LINE:|7-commandAction|41|295-preAction
//#mdebug
//#                 MapCanvas.setCurrent(new ProgressForm(LangHolder.getString(Lang.sending), LangHolder.getString(Lang.waitpls), new LogSend(getFormOptDeb(), textLogSavePath.getString(), LogSend.WEBSEND), getFormOptDeb()));
//# 
//#enddebug
                //new GeoCacheLoad(MapCanvas.map);

// Insert pre-action code here
//GEN-LINE:|7-commandAction|42|295-postAction
                // Insert post-action code here
            } else if (command==itemSaveOpt){//GEN-LINE:|7-commandAction|43|297-preAction
                saveOptDebug();
                back2Opt(false);
                // Insert pre-action code here
//GEN-LINE:|7-commandAction|44|297-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|45|142-preAction
        } else if (displayable==formOptGPS){
            if (command==back2Opt){//GEN-END:|7-commandAction|45|142-preAction
                // Insert pre-action code here
                switchDisplayable(null, getListOpt());//GEN-LINE:|7-commandAction|46|142-postAction
                // Insert post-action code here
            } else if (command==itemSaveOpt){//GEN-LINE:|7-commandAction|47|144-preAction
                saveOptGPS();
                back2Opt(true);
// Insert pre-action code here
//GEN-LINE:|7-commandAction|48|144-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|49|22-preAction
        } else if (displayable==formOptGen){
            if (command==back2Opt){//GEN-END:|7-commandAction|49|22-preAction
                // Insert pre-action code here
                switchDisplayable(null, getListOpt());//GEN-LINE:|7-commandAction|50|22-postAction
                // Insert post-action code here
            } else if (command==itemSaveOpt){//GEN-LINE:|7-commandAction|51|311-preAction
                saveOptGeneral();
                back2Opt(false);        // Insert pre-action code here
//GEN-LINE:|7-commandAction|52|311-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|53|121-preAction
        } else if (displayable==formOptGeo){
            if (command==back2Opt){//GEN-END:|7-commandAction|53|121-preAction
                // Insert pre-action code here
                switchDisplayable(null, getListOpt());//GEN-LINE:|7-commandAction|54|121-postAction
                // Insert post-action code here
            } else if (command==itemSaveOpt){//GEN-LINE:|7-commandAction|55|123-preAction
                saveOptGeoInfo();
                back2Opt(false);           // Insert pre-action code here
//GEN-LINE:|7-commandAction|56|123-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|57|96-preAction
        } else if (displayable==formOptKeys){
            if (command==back2Opt){//GEN-END:|7-commandAction|57|96-preAction
                // Insert pre-action code here
                switchDisplayable(null, getListOpt());//GEN-LINE:|7-commandAction|58|96-postAction
                // Insert post-action code here
            } else if (command==itemSaveOpt){//GEN-LINE:|7-commandAction|59|95-preAction
                saveOptKeys();
                back2Opt(false);
                // Insert pre-action code here
//GEN-LINE:|7-commandAction|60|95-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|61|226-preAction
        } else if (displayable==formOptLang){
            if (command==back2Opt){//GEN-END:|7-commandAction|61|226-preAction
                // Insert pre-action code here
                switchDisplayable(null, getListOpt());//GEN-LINE:|7-commandAction|62|226-postAction
                // Insert post-action code here
            } else if (command==itemSaveOpt){//GEN-LINE:|7-commandAction|63|228-preAction

                saveOptLang();
                back2Opt(true);

                // Insert pre-action code here
//GEN-LINE:|7-commandAction|64|228-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|65|202-preAction
        } else if (displayable==formOptMaps){
            if (command==back2Opt){//GEN-END:|7-commandAction|65|202-preAction
                // Insert pre-action code here
                switchDisplayable(null, getListOpt());//GEN-LINE:|7-commandAction|66|202-postAction
                // Insert post-action code here
            } else if (command==itemSaveOpt){//GEN-LINE:|7-commandAction|67|204-preAction
                saveOptMaps();
                back2Opt(false);   // Insert pre-action code here
//GEN-LINE:|7-commandAction|68|204-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|69|249-preAction
        } else if (displayable==formOptRadar){
            if (command==back2Opt){//GEN-END:|7-commandAction|69|249-preAction
                // Insert pre-action code here
                switchDisplayable(null, getListOpt());//GEN-LINE:|7-commandAction|70|249-postAction
                // Insert post-action code here
            } else if (command==itemSaveOpt){//GEN-LINE:|7-commandAction|71|251-preAction
                saveOptNetRadar();
                back2Opt(false);     // Insert pre-action code here
//GEN-LINE:|7-commandAction|72|251-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|73|158-preAction
        } else if (displayable==formOptTr){
            if (command==back2Opt){//GEN-END:|7-commandAction|73|158-preAction
                // clearForms();
// Insert pre-action code here
                switchDisplayable(null, getListOpt());//GEN-LINE:|7-commandAction|74|158-postAction
                // Insert post-action code here
            } else if (command==itemSaveOpt){//GEN-LINE:|7-commandAction|75|160-preAction
                saveOptTrack();
                back2Opt(false);   // Insert pre-action code here
//GEN-LINE:|7-commandAction|76|160-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|77|439-preAction
        } else if (displayable==formRecFr){
            if (command==back2Opt){//GEN-END:|7-commandAction|77|439-preAction
                // Insert pre-action code here
                switchDisplayable(null, getListMore());//GEN-LINE:|7-commandAction|78|439-postAction
                // Insert post-action code here
            } else if (command==item1Send){//GEN-LINE:|7-commandAction|79|438-preAction
                // Insert pre-action code here
                RMSOption.setStringOpt(RMSOption.SO_YOURNAME, textYourName.getString());
                MapCanvas.setCurrent(new ProgressForm("SMS", LangHolder.getString(Lang.sending), new SMSRecomend(textFrPhone.getString(), textYourName.getString()), formRecFr));

//GEN-LINE:|7-commandAction|80|438-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|81|344-preAction
        } else if (displayable==formRouteURL){
            if (command==back2Opt){//GEN-END:|7-commandAction|81|344-preAction
                back2RoutesForm(true);
//GEN-LINE:|7-commandAction|82|344-postAction
                // Insert post-action code here
            } else if (command==itemBrowseMap){//GEN-LINE:|7-commandAction|83|345-preAction
                String filter=null;
                if (choiceImpFormat.getSelectedIndex()==0){
                    if (selectKind==MapRoute.ROUTEKIND) {
                        filter=".RTE";
                    }
                    if (selectKind==MapRoute.TRACKKIND) {
                        filter=".PLT";
                    }
                    if (selectKind==MapRoute.WAYPOINTSKIND) {
                        filter=".WPT";
                    }
                } else if (choiceImpFormat.getSelectedIndex()==1){
                    filter=".GPX";
                } else {
                    filter=".LOC";
                }

                getDisplay().setCurrent(new BrowseList(formRouteURL, textRouteURL, BrowseList.FILEBROWSE, filter, null));
                // Insert pre-action code here
//GEN-LINE:|7-commandAction|84|345-postAction
                // Insert post-action code here
            } else if (command==itemLoadMap){//GEN-LINE:|7-commandAction|85|342-preAction
                loadRouteURL();
//GEN-LINE:|7-commandAction|86|342-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|87|543-preAction
        } else if (displayable==formSaveChanges){
            if (command==cancelNo){//GEN-END:|7-commandAction|87|543-preAction
                // saveSettingsCancel(); // Insert pre-action code here
//GEN-LINE:|7-commandAction|88|543-postAction
                // Insert post-action code here
            } else if (command==okYes){//GEN-LINE:|7-commandAction|89|542-preAction
                // saveSettingsOk();// Insert pre-action code here
//GEN-LINE:|7-commandAction|90|542-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|91|528-preAction
        } else if (displayable==formSaveTrack){
            if (command==back2Opt){//GEN-END:|7-commandAction|91|528-preAction
                if (saveKind==EXPORTKIND){
                    if (selectKind==MapRoute.TRACKKIND) {
                        showSelectTrackForm(true);
                    } else if (selectKind==MapRoute.ROUTEKIND) {
                        showSelectRouteForm(true);
                    } else if (selectKind==MapRoute.WAYPOINTSKIND) {
                        showSelectWPForm(true);
                    }
                } else if ((saveKind==SENDKIND_IR)||(saveKind==SENDKIND_BT)){
                    if (selectKind==MapRoute.TRACKKIND) {
                        MapCanvas.setCurrent(listTrackMenu);
                    } else if (selectKind==MapRoute.ROUTEKIND) {
                        MapCanvas.setCurrent(listRouteMenu);
                    } else if (selectKind==MapRoute.WAYPOINTSKIND) {
                        MapCanvas.setCurrent(listWPMenu);
                    }
                }
                formSaveTrack=null;
                textTrackSavePath=null;
                choiceCP_S=null;
                choiceOX_S=null;
                choiceExpCount=null;

//GEN-LINE:|7-commandAction|92|528-postAction
                // Insert post-action code here
            } else if (command==item1Save){//GEN-LINE:|7-commandAction|93|526-preAction
                if (selectKind==MapRoute.TRACKKIND){
                    if (choiceOX_S.getSelectedIndex()>0) {
                        choiceCP_S.setSelectedIndex(0, true);
                    }
                }
                if ((saveKind==SENDKIND_IR)||(saveKind==SENDKIND_BT)){

                    RouteSend.EXPORTCODEPAGE=(byte) choiceCP_S.getSelectedIndex();
                    RMSOption.routeCP=RouteSend.EXPORTCODEPAGE;

                    RouteSend.EXPORTFORMAT=(byte) choiceOX_S.getSelectedIndex();
                    RMSOption.routeFormat=RouteSend.EXPORTFORMAT;

//            MapRoute mr =MapCanvas.map.rmss.loadRoute(getListTrack().getSelectedIndex()-1,MapRoute.TRACKKIND);
//            
//            DataSenderThread dst = new DataSenderThread(listTrackMenu,mr,DataSenderThread.SENDTYPE_BLUE);
//            MapCanvas.setCurrent(new ProgressForm("IR","Sending",dst ,listTrackMenu));
//          
                    if (saveKind==SENDKIND_IR){
                        MapRoute mr;
                        DataSenderThread dst;
                        if (selectKind==MapRoute.TRACKKIND){
                            mr=MapCanvas.map.rmss.loadRoute(getListTrack().getSelectedIndex()-1, MapRoute.TRACKKIND);
                            dst=new DataSenderThread(listTrackMenu, mr, DataSenderThread.SENDTYPE_IR);
                            MapCanvas.setCurrent(new ProgressForm("IR", "Sending", dst, listTrackMenu));
                        } else if (selectKind==MapRoute.ROUTEKIND){
                            mr=MapCanvas.map.rmss.loadRoute(getListRoute().getSelectedIndex()-1, MapRoute.ROUTEKIND);
                            dst=new DataSenderThread(listRouteMenu, mr, DataSenderThread.SENDTYPE_IR);
                            MapCanvas.setCurrent(new ProgressForm("IR", "Sending", dst, listRouteMenu));
                        } else if (selectKind==MapRoute.WAYPOINTSKIND){
                            mr=MapCanvas.map.rmss.loadRoute(getListWP().getSelectedIndex()-1, MapRoute.WAYPOINTSKIND);
                            dst=new DataSenderThread(listWPMenu, mr, DataSenderThread.SENDTYPE_IR);
                            MapCanvas.setCurrent(new ProgressForm("IR", "Sending", dst, listWPMenu));
                        } else {
                            return;
                        }

                        dst.start();
                    } else {
                        showBTForm(null, null, false, false);
                    }
                    formSaveTrack=null;
                    textTrackSavePath=null;
                    choiceCP_S=null;
                    choiceOX_S=null;
                    choiceExpCount=null;

//            dst.start();

                } else {
                    if (textTrackSavePath.getString().length()>2) {
                        saveTrack();
                    } else {
                        MapCanvas.showmsg(LangHolder.getString(Lang.error), LangHolder.getString(Lang.browse), AlertType.ERROR, formSaveTrack);
                    }
                }
                // Insert pre-action code here
//GEN-LINE:|7-commandAction|94|526-postAction
                // Insert post-action code here
            } else if (command==itemBrowseMap){//GEN-LINE:|7-commandAction|95|529-preAction
                getDisplay().setCurrent(new BrowseList(formSaveTrack, textTrackSavePath, BrowseList.DIRBROWSE, null, null));
                // Insert pre-action code here
//GEN-LINE:|7-commandAction|96|529-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|97|367-preAction
        } else if (displayable==formSearch){
            if (command==back2Opt){//GEN-END:|7-commandAction|97|367-preAction
                // Insert pre-action code here
                switchDisplayable(null, getListMore());//GEN-LINE:|7-commandAction|98|367-postAction
                // Insert post-action code here
            } else if (command==itemResults){//GEN-LINE:|7-commandAction|99|371-preAction
                // Insert pre-action code here
                switchDisplayable(null, getListSearch());//GEN-LINE:|7-commandAction|100|371-postAction
                // Insert post-action code here
            } else if (command==itemSearch){//GEN-LINE:|7-commandAction|101|369-preAction
                // Insert pre-action code here
                //
                String searchString=textSearch.getString();
                if (searchString.equals(MapUtil.emptyString)) {
                    if (MapCanvas.map.gpsActive()&&MapCanvas.gpsBinded) {
                        searchString=""+GPSReader.LATITUDE+","+GPSReader.LONGITUDE;
                    } else {
                        searchString=""+MapCanvas.reallat+","+MapCanvas.reallon;
                    }
                }
                MapCanvas.setCurrent(new ProgressForm(LangHolder.getString(Lang.search), LangHolder.getString(Lang.waitpls), new KMLSearchResult(searchString, getListSearch()), formSearch));

//GEN-LINE:|7-commandAction|102|369-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|103|388-preAction
        } else if (displayable==formSendMMS){
            if (command==back2Opt){//GEN-END:|7-commandAction|103|388-preAction
                back2More();
//GEN-LINE:|7-commandAction|104|388-postAction
                // Insert post-action code here
            } else if (command==item1Send){//GEN-LINE:|7-commandAction|105|389-preAction
                RMSOption.imageType=(byte) choiceImageType.getSelectedIndex();
                RMSOption.lastSendMMSURL=textAddrMMS.getString();
                RMSOption.lastSendFileURL=textAddrFile.getString();
                RMSOption.choicePicSize=choicePicSize.getSelectedIndex();
                RMSOption.choiceSendType=choiceSendType.getSelectedIndex();
                RMSOption.choiceSerInterval=choiceSerInterval.getSelectedIndex();
                RMSOption.choiceSerCount=choiceSerCount.getSelectedIndex();
                MapCanvas.map.rmss.writeSettingNow();

                sendScreenMMS();
//GEN-LINE:|7-commandAction|106|389-postAction
                // Insert post-action code here
            } else if (command==itemBrowseMap){//GEN-LINE:|7-commandAction|107|391-preAction
                getDisplay().setCurrent(new BrowseList(formSendMMS, textAddrFile, BrowseList.DIRBROWSE, null, null));
                // Insert pre-action code here
//GEN-LINE:|7-commandAction|108|391-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|109|447-preAction
        } else if (displayable==formService){
            if (command==back2Opt){//GEN-END:|7-commandAction|109|447-preAction
                // Insert pre-action code here
                switchDisplayable(null, getListMore());//GEN-LINE:|7-commandAction|110|447-postAction
                // Insert post-action code here
            } else if (command==itemBrowseFile){//GEN-LINE:|7-commandAction|111|454-preAction

                getDisplay().setCurrent(new BrowseList(formService, textBackupPath, BrowseList.FILEBROWSE, ".MNB", null));

// Insert pre-action code here
//GEN-LINE:|7-commandAction|112|454-postAction
                // Insert post-action code here
            } else if (command==itemBrowseMap){//GEN-LINE:|7-commandAction|113|453-preAction

                getDisplay().setCurrent(new BrowseList(formService, textBackupPath, BrowseList.DIRBROWSE, null, null));

// Insert pre-action code here
//GEN-LINE:|7-commandAction|114|453-postAction
                // Insert post-action code here
            } else if (command==itemClearMap){//GEN-LINE:|7-commandAction|115|456-preAction
                // Insert pre-action code here
                MapCanvas.map.rmss.deleteAllTiles((byte) (MapCanvas.map.showMapSer+MapCanvas.map.showMapView), formService);
//GEN-LINE:|7-commandAction|116|456-postAction
                // Insert post-action code here
            } else if (command==itemExport){//GEN-LINE:|7-commandAction|117|449-preAction
//#if Service
//#                 // Insert pre-action code here
//#                 byte expt=0;
//#                 if (choiceExpType.isSelected(0)) {
//#                     expt+=1;
//#                 }
//#                 if (choiceExpType.isSelected(1)) {
//#                     expt+=2;
//#                 }
//# 
//#                 MapCanvas.setCurrent(new ProgressForm(LangHolder.getString(Lang.export),
//#                   LangHolder.getString(Lang.waitpls),
//#                   new RMSExportImport(true, textBackupPath.getString(), formService, expt),
//#                   formService));
//#endif


//GEN-LINE:|7-commandAction|118|449-postAction
                // Insert post-action code here
            } else if (command==itemLoadTrack){//GEN-LINE:|7-commandAction|119|451-preAction
//#if Service
//# 
//#                 byte expt=0;
//#                 if (choiceExpType.isSelected(0)) {
//#                     expt+=1;
//#                 }
//#                 if (choiceExpType.isSelected(1)) {
//#                     expt+=2;
//#                 }
//#                 MapCanvas.setCurrent(new ProgressForm(LangHolder.getString(Lang.loading),
//#                   LangHolder.getString(Lang.waitpls),
//#                   new RMSExportImport(false, textBackupPath.getString(), formService, expt),
//#                   formService));
//#endif

                // Insert pre-action code here
//GEN-LINE:|7-commandAction|120|451-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|121|510-preAction
        } else if (displayable==formTelInput){
            if (command==back2Opt){//GEN-END:|7-commandAction|121|510-preAction
                getDisplay().setCurrent(getListMore());
//GEN-LINE:|7-commandAction|122|510-postAction
                // Insert post-action code here
            } else if (command==itemNext1){//GEN-LINE:|7-commandAction|123|511-preAction
                if (choiceTels.getSelectedIndex()==0){
                    RMSOption.lastTel3=RMSOption.lastTel2;
                    RMSOption.lastTel2=RMSOption.lastTel1;
                    RMSOption.lastTel1=RMSOption.lastSMSNumber;
                    RMSOption.lastSMSNumber=textTelNum.getString();
                    RMSOption.changed=true;
                    MapCanvas.map.rmss.writeSetting();
                    sendCoordSMS(getTextTelNum().getString(), choiceSMSType.getSelectedIndex());
                } else {
                    sendCoordSMS(choiceTels.getString(choiceTels.getSelectedIndex()), choiceSMSType.getSelectedIndex());
                }
                // Insert pre-action code here
//GEN-LINE:|7-commandAction|124|511-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|125|16-preAction
        } else if (displayable==gotoForm){
            if (command==cancel2Map){//GEN-END:|7-commandAction|125|16-preAction
                // Insert pre-action code here
//GEN-LINE:|7-commandAction|126|16-postAction
                back2Map();
            } else if (command==itemGoto1){//GEN-LINE:|7-commandAction|127|14-preAction
                // Insert pre-action code here
//GEN-LINE:|7-commandAction|128|14-postAction
                setAddress();
            }//GEN-BEGIN:|7-commandAction|129|571-preAction
        } else if (displayable==listBT){
//            if (command==List.SELECT_COMMAND){//GEN-END:|7-commandAction|129|571-preAction
//                // write pre-action user code here
//                listBTAction();//GEN-LINE:|7-commandAction|130|571-postAction
//                // write post-action user code here
//            } else
                if (command==cancel2Map){//GEN-LINE:|7-commandAction|131|573-preAction
                MapCanvas.map.endGPSLookup(false);
                back2Map();
// Insert pre-action code here
//GEN-LINE:|7-commandAction|132|573-postAction
                // Insert post-action code here
            } else if (command==itemClear){//GEN-LINE:|7-commandAction|133|576-preAction
                if (btGPS){
                    RMSOption.BT_DEVICE_NAMES=new String[0];
                    RMSOption.BT_DEVICE_URLS=new String[0];
                } else {
                    RMSOption.BT_DEVICE_NAMES_SEND=new String[0];
                    RMSOption.BT_DEVICE_URLS_SEND=new String[0];
                }

                clearForms();
                MapForms.mm.showBTForm(null, null, false, btGPS);
                // Insert pre-action code here
//GEN-LINE:|7-commandAction|134|576-postAction
                // Insert post-action code here
            } else if (command==itemSelBT){//GEN-LINE:|7-commandAction|135|574-preAction
                // Insert pre-action code here
                //#if SE_K750_E_BASEDEV
//#                 RMSOption.urlGPS="";
//#                 RMSOption.lastBTDeviceName="b";
//#                 MapCanvas.map.startGPS();
//# 
                //#else
          if (listBT.getSelectedIndex()==listBT.size()-1){
            //adding new device
            LocStarter.showSearchBlueGPS(false,btGPS);
          } else {
            //activate selected device
            if (btGPS){
            RMSOption.urlGPS=RMSOption.BT_DEVICE_URLS[listBT.getSelectedIndex()];
            RMSOption.lastBTDeviceName=RMSOption.BT_DEVICE_NAMES[listBT.getSelectedIndex()];
            MapCanvas.map.startGPS();}
            else{
              onFeedback(RMSOption.BT_DEVICE_URLS_SEND[listBT.getSelectedIndex()]);
            }
          }
                //#endif


//GEN-LINE:|7-commandAction|136|574-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|137|597-preAction
        } else if (displayable==listIMaps){
            if (command==List.SELECT_COMMAND){//GEN-END:|7-commandAction|137|597-preAction
                // write pre-action user code here
               // listIMapsAction();//GEN-LINE:|7-commandAction|138|597-postAction
                // write post-action user code here
                itemSelectInIMaps();

            } else if (command==back2Opt){//GEN-LINE:|7-commandAction|139|599-preAction
                // Insert pre-action code here
                switchDisplayable(null, getListMore());//GEN-LINE:|7-commandAction|140|599-postAction
                // Insert post-action code here
            } else if (command==itemWPDelete){//GEN-LINE:|7-commandAction|141|602-preAction
                // Insert pre-action code here

                int id=listIMaps.getSelectedIndex();
                if ((id>0)&&(id<listIMaps.size()-1)){
                    String[] mn=new String[RMSOption.IMAPS_NAMES.length-1];
                    String[] mt=new String[mn.length];
                    String[] mp=new String[mn.length];
                    float[] mc=new float[mn.length*3];

                    id--;
                    for (int i=0; i<RMSOption.IMAPS_NAMES.length; i++) {
                        if (i!=id){
                            if (i<id){
                                mn[i]=RMSOption.IMAPS_NAMES[i];
                                mt[i]=RMSOption.IMAPS_RMST[i];
                                mp[i]=RMSOption.IMAPS_RMSP[i];
                                mc[i*3]=RMSOption.IMAPS_CENT[i*3];
                                mc[i*3+1]=RMSOption.IMAPS_CENT[i*3+1];
                                mc[i*3+2]=RMSOption.IMAPS_CENT[i*3+2];
                            } else {
                                mn[i-1]=RMSOption.IMAPS_NAMES[i];
                                mt[i-1]=RMSOption.IMAPS_RMST[i];
                                mp[i-1]=RMSOption.IMAPS_RMSP[i];
                                mc[(i-1)*3]=RMSOption.IMAPS_CENT[(i*3)];
                                mc[(i-1)*3+1]=RMSOption.IMAPS_CENT[(i*3)+1];
                                mc[(i-1)*3+2]=RMSOption.IMAPS_CENT[(i*3)+2];
                            }
                        } else {
                            try {
                                RecordStore.deleteRecordStore(RMSOption.IMAPS_RMST[i]);
                            } catch (Throwable tt) {
                            }
                            try {
                                RecordStore.deleteRecordStore(RMSOption.IMAPS_RMSP[i]);
                            } catch (Throwable tt) {
                            }
                        }
                    }
                    RMSOption.IMAPS_NAMES=mn;
                    RMSOption.IMAPS_RMST=mt;
                    RMSOption.IMAPS_RMSP=mp;
                    RMSOption.IMAPS_CENT=mc;
                    showListIMaps();
                    MapCanvas.showmsg(LangHolder.getString(Lang.intmaps), LangHolder.getString(Lang.deleted), AlertType.INFO, listIMaps);
                } else {
                    MapCanvas.showmsg(LangHolder.getString(Lang.attention), LangHolder.getString(Lang.nosel), AlertType.WARNING, listIMaps);
                }


//GEN-LINE:|7-commandAction|142|602-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|143|580-preAction
        } else if (displayable==listKML){
//            if (command==List.SELECT_COMMAND){//GEN-END:|7-commandAction|143|580-preAction
//                // write pre-action user code here
//                listKMLAction();//GEN-LINE:|7-commandAction|144|580-postAction
//                // write post-action user code here
//            } else
                if (command==back2Opt){//GEN-LINE:|7-commandAction|145|583-preAction
                // Insert pre-action code here
                switchDisplayable(null, getListMore());//GEN-LINE:|7-commandAction|146|583-postAction
                // Insert post-action code here
            } else if (command==itemSelect1){//GEN-LINE:|7-commandAction|147|582-preAction
                // Insert pre-action code here
                int i=listKML.getSelectedIndex();
                KMLMapRoute mr=(KMLMapRoute) MapCanvas.map.kmlRoutes.elementAt(i);
                if (mr.active){
                    mr.stopIt();
                    MapCanvas.showmsg(LangHolder.getString(Lang.info), String.valueOf(mr.name)+"\n "+LangHolder.getString(Lang.off), AlertType.INFO, listKML);
                } else {
                    mr.start();
                    MapCanvas.showmsg(LangHolder.getString(Lang.info), String.valueOf(mr.name)+"\n "+LangHolder.getString(Lang.on), AlertType.INFO, listKML);
                }
                Image im=null;
                if (!mr.active) {
                    im=getImageGEGray();
                } else if (mr.notReady) {
                    im=getImageGEYel();
                } else {
                    im=getImageGEGreen();
                }
                listKML.set(i, mr.name, im);


//GEN-LINE:|7-commandAction|148|582-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|149|360-preAction
        } else if (displayable==listMore){
            if (command==List.SELECT_COMMAND){//GEN-END:|7-commandAction|149|360-preAction
                // write pre-action user code here
                listMoreAction();//GEN-LINE:|7-commandAction|150|360-postAction
                // write post-action user code here
            } else if (command==cancel2Map){//GEN-LINE:|7-commandAction|151|362-preAction
                back2Map();

//GEN-LINE:|7-commandAction|152|362-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|153|476-preAction
        } else if (displayable==listNav){
            if (command==List.SELECT_COMMAND){//GEN-END:|7-commandAction|153|476-preAction
                // write pre-action user code here
                listNavAction();//GEN-LINE:|7-commandAction|154|476-postAction
                // write post-action user code here
            } else if (command==close2Map){//GEN-LINE:|7-commandAction|155|478-preAction
                // Insert pre-action code here
//GEN-LINE:|7-commandAction|156|478-postAction
                back2Map();
            }//GEN-BEGIN:|7-commandAction|157|589-preAction
        } else if (displayable==listOMaps){
            if (command==List.SELECT_COMMAND){//GEN-END:|7-commandAction|157|589-preAction
                // write pre-action user code here
//listOMapsAction ();//GEN-LINE:|7-commandAction|158|589-postAction
                // write post-action user code here
                itemSelectInOMaps();
            } else if (command==back2Opt){//GEN-LINE:|7-commandAction|159|591-preAction
                // Insert pre-action code here
                switchDisplayable(null, getListMore());//GEN-LINE:|7-commandAction|160|591-postAction
                // Insert post-action code here
            } else if (command==itemWPDelete){//GEN-LINE:|7-commandAction|161|593-preAction
                // Insert pre-action code here
                int id=listOMaps.getSelectedIndex();
                if ((id>0)&&(id<listOMaps.size()-1)){
                    String[] mn=new String[RMSOption.OMAPS_NAMES.length-1];
                    String[] mu=new String[mn.length];
                    id--;
                    for (int i=0; i<RMSOption.OMAPS_NAMES.length; i++) {
                        if (i!=id){
                            if (i<id){
                                mn[i]=RMSOption.OMAPS_NAMES[i];
                                mu[i]=RMSOption.OMAPS_URLS[i];
                            } else {
                                mn[i-1]=RMSOption.OMAPS_NAMES[i];
                                mu[i-1]=RMSOption.OMAPS_URLS[i];
                            }
                        }
                    }
                    RMSOption.OMAPS_NAMES=mn;
                    RMSOption.OMAPS_URLS=mu;
                    showListOMaps();
                    MapCanvas.showmsg(LangHolder.getString(Lang.extmap), LangHolder.getString(Lang.deleted), AlertType.INFO, listOMaps);
                } else {
                    MapCanvas.showmsg(LangHolder.getString(Lang.attention), LangHolder.getString(Lang.nosel), AlertType.WARNING, listOMaps);
                }

//GEN-LINE:|7-commandAction|162|593-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|163|26-preAction
        } else if (displayable==listOpt){
            if (command==List.SELECT_COMMAND){//GEN-END:|7-commandAction|163|26-preAction
                // write pre-action user code here
                listOptAction();//GEN-LINE:|7-commandAction|164|26-postAction
                // write post-action user code here
            } else if (command==close2Map){//GEN-LINE:|7-commandAction|165|28-preAction

                back2Map();// Insert pre-action code here
//GEN-LINE:|7-commandAction|166|28-postAction
                // Insert post-action code here
            } else if (command==item1Save){//GEN-LINE:|7-commandAction|167|664-preAction
                // write pre-action user code here
                FileDialog.showSaveForm(LangHolder.getString(Lang.options), new Item[0], new RMSOption(), listOpt, ".SET");

//GEN-LINE:|7-commandAction|168|664-postAction
                // write post-action user code here
            } else if (command==itemLoadMap){//GEN-LINE:|7-commandAction|169|663-preAction
                // write pre-action user code here
                FileDialog.showLoadForm(LangHolder.getString(Lang.options), new Item[0], new RMSOption(), listOpt, ".SET");

//GEN-LINE:|7-commandAction|170|663-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|171|333-preAction
        } else if (displayable==listRoute){
            if (command==List.SELECT_COMMAND){//GEN-END:|7-commandAction|171|333-preAction
                // write pre-action user code here
                listRouteAction();//GEN-LINE:|7-commandAction|172|333-postAction
                // write post-action user code here
            } else if (command==cancel2Map){//GEN-LINE:|7-commandAction|173|335-preAction
                // Insert pre-action code here
//GEN-LINE:|7-commandAction|174|335-postAction
                showNavigateForm();
            } else if (command==itemCreateRoute){//GEN-LINE:|7-commandAction|175|337-preAction
                // Insert pre-action code here
                createRoute(MapRoute.ROUTEKIND);
                MapCanvas.showmsg(LangHolder.getString(Lang.create), LangHolder.getString(Lang.ok), AlertType.INFO, listRoute);

//GEN-LINE:|7-commandAction|176|337-postAction
                // Insert post-action code here
            } else if (command==itemSelect1){//GEN-LINE:|7-commandAction|177|336-preAction
                if ((listRoute.getSelectedIndex()!=listRoute.size()-1)&&(listRoute.getSelectedIndex()!=0)){
                    getListRouteMenu().setTitle(listRoute.getString(listRoute.getSelectedIndex()));
                    MapCanvas.setCurrent(getListRouteMenu());
                } else {
                    selectRoute(MapRoute.ROUTEKIND);
                }
//GEN-LINE:|7-commandAction|178|336-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|179|632-preAction
        } else if (displayable==listRouteMenu){
            if (command==List.SELECT_COMMAND){//GEN-END:|7-commandAction|179|632-preAction
                // write pre-action user code here
                listRouteMenuAction();//GEN-LINE:|7-commandAction|180|632-postAction
                // write post-action user code here
            } else if (command==back2Opt){//GEN-LINE:|7-commandAction|181|634-preAction
                // Insert pre-action code here
                switchDisplayable(null, getListRoute());//GEN-LINE:|7-commandAction|182|634-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|183|646-preAction
        } else if (displayable==listSKML){
            if (command==List.SELECT_COMMAND){//GEN-END:|7-commandAction|183|646-preAction
                // write pre-action user code here
                listSKMLAction();//GEN-LINE:|7-commandAction|184|646-postAction
                // write post-action user code here
            } else if (command==close2Map){//GEN-LINE:|7-commandAction|185|648-preAction
                // Insert pre-action code here
                showNavigateForm();
//GEN-LINE:|7-commandAction|186|648-postAction
                // Insert post-action code here
            } else if (command==itemSelect1){//GEN-LINE:|7-commandAction|187|649-preAction
                // Insert pre-action code here
                if (listSKML.size()>0){
                    if ((listSKML.getSelectedIndex()!=listSKML.size()-1)&&!((MapCanvas.map.activeRoute!=null)&&(listSKML.getSelectedIndex()==0))){
                        getListSKMLMenu().setTitle(listSKML.getString(listSKML.getSelectedIndex()));
                        MapCanvas.setCurrent(getListSKMLMenu());
                    } else {
                        selectRoute(MapRoute.KMLDOCUMENT);
                    }
                }
//GEN-LINE:|7-commandAction|188|649-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|189|652-preAction
        } else if (displayable==listSKMLMenu){
            if (command==List.SELECT_COMMAND){//GEN-END:|7-commandAction|189|652-preAction
                // write pre-action user code here
                listSKMLMenuAction();//GEN-LINE:|7-commandAction|190|652-postAction
                // write post-action user code here
            } else if (command==back2Opt){//GEN-LINE:|7-commandAction|191|654-preAction
                // Insert pre-action code here
                switchDisplayable(null, getListSKML());//GEN-LINE:|7-commandAction|192|654-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|193|374-preAction
        } else if (displayable==listSearch){
//if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|193|374-preAction
            // write pre-action user code here
//listSearchAction ();//GEN-LINE:|7-commandAction|194|374-postAction
            // write post-action user code here
//} else
            if (command==back2Opt){//GEN-LINE:|7-commandAction|195|376-preAction
                // Insert pre-action code here
                switchDisplayable(null, getFormSearch());//GEN-LINE:|7-commandAction|196|376-postAction
                // Insert post-action code here
            } else if (command==itemGoto1){//GEN-LINE:|7-commandAction|197|378-preAction
                // Insert pre-action code here
                if (KMLSearchResult.kmlPoints!=null){

                    KMLMapPoint mp=(KMLMapPoint) KMLSearchResult.kmlPoints.elementAt(listSearch.getSelectedIndex());
                    if (mp.accuracy>8) {
                        mp.accuracy=(byte) 8;
                    }
                    MapCanvas.map.setLocation(mp.lat, mp.lon, mp.accuracy*2);
                    MapCanvas.setCurrentMap();
                }
//GEN-LINE:|7-commandAction|198|378-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|199|501-preAction
        } else if (displayable==listTrack){
//if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|199|501-preAction
            // write pre-action user code here
//listTrackAction ();//GEN-LINE:|7-commandAction|200|501-postAction
            // write post-action user code here
//} else
            if (command==cancel2Map){//GEN-LINE:|7-commandAction|201|503-preAction
                showNavigateForm();

            } else if (command==itemLoadTrack){

                formRouteURL=null;
                textRouteURL=null;
                choiceRouteURL=null;
                choiceCP=null;
                choiceImpFormat=null;

                getChoiceCP().setSelectedIndex(RMSOption.routeCP, true);
                getTextRouteURL().setString(RMSOption.lastURL);
                getFormRouteURL();
                choiceImpFormat.delete(2);
                choiceImpFormat.setSelectedIndex((RMSOption.getByteOpt(RMSOption.BO_IMPFORMATRT)==MapRouteLoader.FORMATOZI)?0:1, true);

                //formRouteURL.delete(3);
                MapCanvas.setCurrent(getFormRouteURL());

                //getChoiceImpFormat().setSelectedIndex(RMSOption.getByteOpt(RMSOption.BO_IMPFORMAT),true);


//GEN-LINE:|7-commandAction|204|505-postAction
                // Insert post-action code here
            } else if (command==itemSelect1){//GEN-LINE:|7-commandAction|205|504-preAction
                if ((listTrack.getSelectedIndex()!=listTrack.size()-1)&&(listTrack.getSelectedIndex()!=0)){
                    getListTrackMenu().setTitle(listTrack.getString(listTrack.getSelectedIndex()));
                    MapCanvas.setCurrent(getListTrackMenu());
                } else {
                    selectRoute(MapRoute.TRACKKIND);
                }
//GEN-LINE:|7-commandAction|206|504-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|207|619-preAction
        } else if (displayable==listTrackMenu){
            if (command==List.SELECT_COMMAND){//GEN-END:|7-commandAction|207|619-preAction
                // write pre-action user code here
                listTrackMenuAction();//GEN-LINE:|7-commandAction|208|619-postAction
                // write post-action user code here
            } else if (command==back2Opt){//GEN-LINE:|7-commandAction|209|621-preAction
                // Insert pre-action code here
                switchDisplayable(null, getListTrack());//GEN-LINE:|7-commandAction|210|621-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|211|466-preAction
        } else if (displayable==listWP){
//if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|211|466-preAction
            // write pre-action user code here
//listWPAction ();//GEN-LINE:|7-commandAction|212|466-postAction
            // write post-action user code here
//} else
            if (command==cancel2Map){//GEN-LINE:|7-commandAction|213|468-preAction
                showNavigateForm();
//GEN-LINE:|7-commandAction|214|468-postAction
                // Insert post-action code here
            } else if (command==itemCreateRoute){//GEN-LINE:|7-commandAction|215|470-preAction
                // Insert pre-action code here
                createRoute(MapRoute.WAYPOINTSKIND);
                MapCanvas.showmsg(LangHolder.getString(Lang.create), LangHolder.getString(Lang.ok), AlertType.INFO, listWP);

//GEN-LINE:|7-commandAction|216|470-postAction
                // Insert post-action code here
            } else if (command==itemMerge){//GEN-LINE:|7-commandAction|217|471-preAction
                // Insert pre-action code here
//GEN-LINE:|7-commandAction|218|471-postAction
                // Insert post-action code here
                mergeWPs();
                MapCanvas.showmsg(LangHolder.getString(Lang.merge), LangHolder.getString(Lang.ok), AlertType.INFO, listWP);

            } else if (command==itemSelect1){//GEN-LINE:|7-commandAction|219|469-preAction
                if ((listWP.getSelectedIndex()!=listWP.size()-1)&&(listWP.getSelectedIndex()!=0)){
                    getListWPMenu().setTitle(listWP.getString(listWP.getSelectedIndex()));
                    MapCanvas.setCurrent(getListWPMenu());
                } else {
                    selectRoute(MapRoute.WAYPOINTSKIND);
                }
//GEN-LINE:|7-commandAction|220|469-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|221|605-preAction
        } else if (displayable==listWPMenu){
            if (command==List.SELECT_COMMAND){//GEN-END:|7-commandAction|221|605-preAction
                // write pre-action user code here
                listWPMenuAction();//GEN-LINE:|7-commandAction|222|605-postAction
                // write post-action user code here
            } else if (command==back2Opt){//GEN-LINE:|7-commandAction|223|607-preAction
                // Insert pre-action code here
                switchDisplayable(null, getListWP());//GEN-LINE:|7-commandAction|224|607-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|7-commandAction|225|7-postCommandAction
        }//GEN-END:|7-commandAction|225|7-postCommandAction
        // Insert global post-action code here
    }//GEN-BEGIN:|7-commandAction|226|
//</editor-fold>//GEN-END:|7-commandAction|226|

//<editor-fold defaultstate="collapsed" desc=" Generated Method: commandAction for Items ">//GEN-BEGIN:|8-itemCommandAction|0|8-preItemCommandAction
    /**
     * Called by a system to indicated that a command has been invoked on a particular item.
     * @param command the Command that was invoked
     * @param item the Item where the command was invoked
     */
    public void commandAction(Command command, Item item) {//GEN-END:|8-itemCommandAction|0|8-preItemCommandAction
        // Insert global pre-action code here
        if (item==stringURLVer){//GEN-BEGIN:|8-itemCommandAction|1|433-preAction
            if (command==itemGoto1){//GEN-END:|8-itemCommandAction|1|433-preAction
                // Insert pre-action code here
                try {
                    if (mM.platformRequest("http://mapnav.spb.ru/wap.wml")){
                        MapCanvas.map.exitMidlet();
                    }
                } catch (Throwable t) {
                }

//GEN-LINE:|8-itemCommandAction|2|433-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|8-itemCommandAction|3|660-preAction
        } else if (item==textFileCachePath){
            if (command==itemBrowseMap){//GEN-END:|8-itemCommandAction|3|660-preAction
                // write pre-action user code here
                getDisplay().setCurrent(new BrowseList(formOptCache, textFileCachePath, BrowseList.DIRBROWSE, null, null));
//GEN-LINE:|8-itemCommandAction|4|660-postAction
                // write post-action user code here
            }//GEN-BEGIN:|8-itemCommandAction|5|150-preAction
        } else if (item==textGPSCOM){
            if (command==itemCommandListCOM){//GEN-END:|8-itemCommandAction|5|150-preAction
                // Insert pre-action code here
                getDisplay().setCurrent(new StringSelectList(textGPSCOM, getFormOptGPS(), MNSInfo.enumCommPorts(), "COM-"+LangHolder.getString(Lang.port)));

//GEN-LINE:|8-itemCommandAction|6|150-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|8-itemCommandAction|7|306-preAction
        } else if (item==textLogSavePath){
            if (command==itemBrowseMap){//GEN-END:|8-itemCommandAction|7|306-preAction
                getDisplay().setCurrent(new BrowseList(formOptDeb, textLogSavePath, BrowseList.DIRBROWSE, null, null));

                // Insert pre-action code here
//GEN-LINE:|8-itemCommandAction|8|306-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|8-itemCommandAction|9|329-preAction
        } else if (item==textWorkPath){
            if (command==itemBrowseMap){//GEN-END:|8-itemCommandAction|9|329-preAction
                MapCanvas.setCurrent(new BrowseList(formOptGen, textWorkPath, BrowseList.DIRBROWSE, null, null));
                // Insert pre-action code here
//GEN-LINE:|8-itemCommandAction|10|329-postAction
                // Insert post-action code here
            }//GEN-BEGIN:|8-itemCommandAction|11|8-postItemCommandAction
        }//GEN-END:|8-itemCommandAction|11|8-postItemCommandAction
        // Insert global post-action code here
    }//GEN-BEGIN:|8-itemCommandAction|12|
//</editor-fold>//GEN-END:|8-itemCommandAction|12|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: gotoForm ">//GEN-BEGIN:|13-getter|0|13-preInit
    /**
     * Returns an initiliazed instance of gotoForm component.
     * @return the initialized component instance
     */
    public Form getGotoForm() {
        if (gotoForm==null){//GEN-END:|13-getter|0|13-preInit
            // Insert pre-init code here
            gotoForm=new Form(LangHolder.getString(Lang.goto_), new Item[]{getTextLatFieldg(), getTextLonFieldg(), getStringCoordEx()});//GEN-BEGIN:|13-getter|1|13-postInit
            gotoForm.addCommand(getItemGoto1());
            gotoForm.addCommand(getCancel2Map());
            gotoForm.setCommandListener(this);//GEN-END:|13-getter|1|13-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|13-getter|2|
        return gotoForm;
    }
//</editor-fold>//GEN-END:|13-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: textLatFieldg ">//GEN-BEGIN:|18-getter|0|18-preInit
    /**
     * Returns an initiliazed instance of textLatFieldg component.
     * @return the initialized component instance
     */
    public TextField getTextLatFieldg() {
        if (textLatFieldg==null){//GEN-END:|18-getter|0|18-preInit
            // Insert pre-init code here
            textLatFieldg=new TextField(LangHolder.getString(Lang.latitude)+"\n", null, 32, TextField.ANY);//GEN-BEGIN:|18-getter|1|18-postInit
            textLatFieldg.setInitialInputMode("");//GEN-END:|18-getter|1|18-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|18-getter|2|
        return textLatFieldg;
    }
//</editor-fold>//GEN-END:|18-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: textLonFieldg ">//GEN-BEGIN:|19-getter|0|19-preInit
    /**
     * Returns an initiliazed instance of textLonFieldg component.
     * @return the initialized component instance
     */
    public TextField getTextLonFieldg() {
        if (textLonFieldg==null){//GEN-END:|19-getter|0|19-preInit
            // Insert pre-init code here
            textLonFieldg=new TextField(LangHolder.getString(Lang.longitude)+"\n", null, 32, TextField.ANY);//GEN-BEGIN:|19-getter|1|19-postInit
            textLonFieldg.setInitialInputMode("");//GEN-END:|19-getter|1|19-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|19-getter|2|
        return textLonFieldg;
    }
//</editor-fold>//GEN-END:|19-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringCoordEx ">//GEN-BEGIN:|20-getter|0|20-preInit
    /**
     * Returns an initiliazed instance of stringCoordEx component.
     * @return the initialized component instance
     */
    public StringItem getStringCoordEx() {
        if (stringCoordEx==null){//GEN-END:|20-getter|0|20-preInit
            // Insert pre-init code here
            stringCoordEx=new StringItem(LangHolder.getString(Lang.example)+"\n", "");//GEN-LINE:|20-getter|1|20-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|20-getter|2|
        return stringCoordEx;
    }
//</editor-fold>//GEN-END:|20-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: formOptGen ">//GEN-BEGIN:|21-getter|0|21-preInit
    /**
     * Returns an initiliazed instance of formOptGen component.
     * @return the initialized component instance
     */
    public Form getFormOptGen() {
        if (formOptGen==null){//GEN-END:|21-getter|0|21-preInit
            // Insert pre-init code here
            formOptGen=new Form(LangHolder.getString(Lang.options), new Item[]{getChoiceScale(), getTextWorkPath()});//GEN-BEGIN:|21-getter|1|21-postInit
            formOptGen.addCommand(getBack2Opt());
            formOptGen.addCommand(getItemSaveOpt());
            formOptGen.setCommandListener(this);//GEN-END:|21-getter|1|21-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|21-getter|2|
        return formOptGen;
    }
//</editor-fold>//GEN-END:|21-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceScale ">//GEN-BEGIN:|312-getter|0|312-preInit
    /**
     * Returns an initiliazed instance of choiceScale component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceScale() {
        if (choiceScale==null){//GEN-END:|312-getter|0|312-preInit
            // Insert pre-init code here
            choiceScale=new ChoiceGroup(LangHolder.getString(Lang.general)//GEN-BEGIN:|312-getter|1|312-postInit
              , Choice.MULTIPLE);
            choiceScale.append(LangHolder.getString(Lang.preview), null);
            choiceScale.append(LangHolder.getString(Lang.parallelload), null);
            choiceScale.append(LangHolder.getString(Lang.fullscreen), null);
            choiceScale.append(LangHolder.getString(Lang.safemode), null);
            choiceScale.append(LangHolder.getString(Lang.showcoord), null);
            choiceScale.append(LangHolder.getString(Lang.lighton), null);
            choiceScale.append(LangHolder.getString(Lang.blinklight), null);
            choiceScale.append(LangHolder.getString(Lang.light)+" 50%", null);
            choiceScale.append(LangHolder.getString(Lang.foxhunter), null);
            choiceScale.append(LangHolder.getString(Lang.limitrot), null);
            choiceScale.append(LangHolder.getString(Lang.addminimize), null);
            choiceScale.append(LangHolder.getString(Lang.cross), null);
            choiceScale.append(LangHolder.getString(Lang.transppointer), null);
            choiceScale.append(LangHolder.getString(Lang.lightlock), null);
            choiceScale.append(LangHolder.getString(Lang.clearwithreload), null);
            choiceScale.append(LangHolder.getString(Lang.showclock), null);
            choiceScale.setFitPolicy(Choice.TEXT_WRAP_ON);
//choiceScale.setSelectedFlags (new boolean[] { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false });
//choiceScale.setFont (0, null);
//choiceScale.setFont (1, null);
//choiceScale.setFont (2, null);
//choiceScale.setFont (3, null);
//choiceScale.setFont (4, null);
//choiceScale.setFont (5, null);
//choiceScale.setFont (6, null);
//choiceScale.setFont (7, null);
//choiceScale.setFont (8, null);
//choiceScale.setFont (9, null);
//choiceScale.setFont (10, null);
//choiceScale.setFont (11, null);
//choiceScale.setFont (12, null);
//choiceScale.setFont (13, null);
//choiceScale.setFont (14, null);
//choiceScale.setFont (15, null);//GEN-END:|312-getter|1|312-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|312-getter|2|
        return choiceScale;
    }
//</editor-fold>//GEN-END:|312-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: textWorkPath ">//GEN-BEGIN:|328-getter|0|328-preInit
    /**
     * Returns an initiliazed instance of textWorkPath component.
     * @return the initialized component instance
     */
    public TextField getTextWorkPath() {
        if (textWorkPath==null){//GEN-END:|328-getter|0|328-preInit
            // Insert pre-init code here
            textWorkPath=new TextField(LangHolder.getString(Lang.workpath), null, 128, TextField.ANY);//GEN-BEGIN:|328-getter|1|328-postInit
            textWorkPath.addCommand(getItemBrowseMap());
            textWorkPath.setItemCommandListener(this);//GEN-END:|328-getter|1|328-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|328-getter|2|
        return textWorkPath;
    }
//</editor-fold>//GEN-END:|328-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: listOpt ">//GEN-BEGIN:|24-getter|0|24-preInit
    /**
     * Returns an initiliazed instance of listOpt component.
     * @return the initialized component instance
     */
    public List getListOpt() {
        if (listOpt==null){//GEN-END:|24-getter|0|24-preInit
            // Insert pre-init code here
            listOpt=new List(LangHolder.getString(Lang.options), Choice.IMPLICIT);//GEN-BEGIN:|24-getter|1|24-postInit
            listOpt.append(LangHolder.getString(Lang.general), null);
            listOpt.append(LangHolder.getString(Lang.appearance), null);
            listOpt.append(LangHolder.getString(Lang.alerts), null);
            listOpt.append(LangHolder.getString(Lang.keys), null);
            listOpt.append(LangHolder.getString(Lang.geoinfo), null);
            listOpt.append("GPS", null);
            listOpt.append(LangHolder.getString(Lang.track), null);
            listOpt.append(LangHolder.getString(Lang.maps), null);
            listOpt.append(LangHolder.getString(Lang.language), null);
            listOpt.append("NetRadar", null);
            listOpt.append(LangHolder.getString(Lang.cache), null);
            listOpt.append(LangHolder.getString(Lang.debug), null);
            listOpt.addCommand(getClose2Map());
            listOpt.addCommand(getItemLoadMap());
            listOpt.addCommand(getItem1Save());
            listOpt.setCommandListener(this);
            listOpt.setFitPolicy(Choice.TEXT_WRAP_DEFAULT);
            listOpt.setSelectedFlags(new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false});//GEN-END:|24-getter|1|24-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|24-getter|2|
        return listOpt;
    }
//</editor-fold>//GEN-END:|24-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Method: listOptAction ">//GEN-BEGIN:|24-action|0|24-preAction
    /**
     * Performs an action assigned to the selected list element in the listOpt component.
     */
    public void listOptAction() {//GEN-END:|24-action|0|24-preAction
        // enter pre-action user code here
        switch (getListOpt().getSelectedIndex()) {//GEN-BEGIN:|24-action|1|31-preAction
            case 0://GEN-END:|24-action|1|31-preAction
                // write pre-action user code here
                getFormOptGen();
                showOptGen();
                switchDisplayable(null, getFormOptGen());//GEN-LINE:|24-action|2|31-postAction
                // write post-action user code here

                break;//GEN-BEGIN:|24-action|3|33-preAction
            case 1://GEN-END:|24-action|3|33-preAction
                // write pre-action user code here
                getFormOptAppear();
                showOptAppear();
                switchDisplayable(null, getFormOptAppear());//GEN-LINE:|24-action|4|33-postAction
                // write post-action user code here

                break;//GEN-BEGIN:|24-action|5|79-preAction
            case 2://GEN-END:|24-action|5|79-preAction
                // write pre-action user code here
                getFormOptAlr();
                showOptAlerts();
                switchDisplayable(null, getFormOptAlr());//GEN-LINE:|24-action|6|79-postAction
                // write post-action user code here

                break;//GEN-BEGIN:|24-action|7|93-preAction
            case 3://GEN-END:|24-action|7|93-preAction
                // write pre-action user code here
                getFormOptKeys();
                showOptKeys();
                switchDisplayable(null, getFormOptKeys());//GEN-LINE:|24-action|8|93-postAction
                // write post-action user code here

                break;//GEN-BEGIN:|24-action|9|119-preAction
            case 4://GEN-END:|24-action|9|119-preAction
                // write pre-action user code here
                getFormOptGeo();
                showOptGeoInfo();
                switchDisplayable(null, getFormOptGeo());//GEN-LINE:|24-action|10|119-postAction
                // write post-action user code here

                break;//GEN-BEGIN:|24-action|11|140-preAction
            case 5://GEN-END:|24-action|11|140-preAction
                // write pre-action user code here
                getFormOptGPS();
                showOptGPS();
                switchDisplayable(null, getFormOptGPS());//GEN-LINE:|24-action|12|140-postAction
                // write post-action user code here

                break;//GEN-BEGIN:|24-action|13|156-preAction
            case 6://GEN-END:|24-action|13|156-preAction
                // write pre-action user code here
                getFormOptTr();
                showOptTrack();
                switchDisplayable(null, getFormOptTr());//GEN-LINE:|24-action|14|156-postAction
                // write post-action user code here

                break;//GEN-BEGIN:|24-action|15|200-preAction
            case 7://GEN-END:|24-action|15|200-preAction
                // write pre-action user code here
                getFormOptMaps();
                showOptMaps();
                switchDisplayable(null, getFormOptMaps());//GEN-LINE:|24-action|16|200-postAction
                // write post-action user code here

                break;//GEN-BEGIN:|24-action|17|224-preAction
            case 8://GEN-END:|24-action|17|224-preAction
                // write pre-action user code here
                getFormOptLang();
                showOptLang();
                switchDisplayable(null, getFormOptLang());//GEN-LINE:|24-action|18|224-postAction
                // write post-action user code here

                break;//GEN-BEGIN:|24-action|19|247-preAction
            case 9://GEN-END:|24-action|19|247-preAction
                // write pre-action user code here
                getFormOptRadar();
                showOptNetRadar();
                switchDisplayable(null, getFormOptRadar());//GEN-LINE:|24-action|20|247-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|24-action|21|266-preAction
            case 10://GEN-END:|24-action|21|266-preAction
                // write pre-action user code here
                getFormOptCache();
                showOptCache();
                switchDisplayable(null, getFormOptCache());//GEN-LINE:|24-action|22|266-postAction
                // write post-action user code here

                break;//GEN-BEGIN:|24-action|23|287-preAction
            case 11://GEN-END:|24-action|23|287-preAction
                // write pre-action user code here
                getFormOptDeb();
                showOptDebug();
                switchDisplayable(null, getFormOptDeb());//GEN-LINE:|24-action|24|287-postAction
                // write post-action user code here

                break;//GEN-BEGIN:|24-action|25|24-postAction
        }//GEN-END:|24-action|25|24-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|24-action|26|
//</editor-fold>//GEN-END:|24-action|26|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: formOptAppear ">//GEN-BEGIN:|34-getter|0|34-preInit
    /**
     * Returns an initiliazed instance of formOptAppear component.
     * @return the initialized component instance
     */
    public Form getFormOptAppear() {
        if (formOptAppear==null){//GEN-END:|34-getter|0|34-preInit
            // Insert pre-init code here
            formOptAppear=new Form(LangHolder.getString(Lang.appearance), new Item[]{getChoiceFontSize(), getChoiceFontStyle(), getChoiceAppearCh(), getChoiceForeColor(), getChoiceShadowColor(), getChoiceViewports2(), getChoiceViewports3(), getChoiceViewports4()});//GEN-BEGIN:|34-getter|1|34-postInit
            formOptAppear.addCommand(getItemSaveOpt());
            formOptAppear.addCommand(getBack2Opt());
            formOptAppear.setCommandListener(this);//GEN-END:|34-getter|1|34-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|34-getter|2|
        return formOptAppear;
    }
//</editor-fold>//GEN-END:|34-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceFontSize ">//GEN-BEGIN:|39-getter|0|39-preInit
    /**
     * Returns an initiliazed instance of choiceFontSize component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceFontSize() {
        if (choiceFontSize==null){//GEN-END:|39-getter|0|39-preInit
            // Insert pre-init code here
            choiceFontSize=new ChoiceGroup(LangHolder.getString(Lang.fontsize), Choice.POPUP);//GEN-BEGIN:|39-getter|1|39-postInit
            choiceFontSize.append(LangHolder.getString(Lang.large), null);
            choiceFontSize.append(LangHolder.getString(Lang.normal), null);
            choiceFontSize.append(LangHolder.getString(Lang.small), null);
//choiceFontSize.setSelectedFlags (new boolean[] { false, false, false });
//choiceFontSize.setFont (0, null);
//choiceFontSize.setFont (1, null);
//choiceFontSize.setFont (2, null);//GEN-END:|39-getter|1|39-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|39-getter|2|
        return choiceFontSize;
    }
//</editor-fold>//GEN-END:|39-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceFontStyle ">//GEN-BEGIN:|43-getter|0|43-preInit
    /**
     * Returns an initiliazed instance of choiceFontStyle component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceFontStyle() {
        if (choiceFontStyle==null){//GEN-END:|43-getter|0|43-preInit
            // Insert pre-init code here
            choiceFontStyle=new ChoiceGroup(LangHolder.getString(Lang.fontstyle), Choice.POPUP);//GEN-BEGIN:|43-getter|1|43-postInit
            choiceFontStyle.append(LangHolder.getString(Lang.normal), null);
            choiceFontStyle.append(LangHolder.getString(Lang.bold), null);
//choiceFontStyle.setSelectedFlags (new boolean[] { false, false });
//choiceFontStyle.setFont (0, null);
//choiceFontStyle.setFont (1, null);//GEN-END:|43-getter|1|43-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|43-getter|2|
        return choiceFontStyle;
    }
//</editor-fold>//GEN-END:|43-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceAppearCh ">//GEN-BEGIN:|46-getter|0|46-preInit
    /**
     * Returns an initiliazed instance of choiceAppearCh component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceAppearCh() {
        if (choiceAppearCh==null){//GEN-END:|46-getter|0|46-preInit
            // Insert pre-init code here
            choiceAppearCh=new ChoiceGroup(LangHolder.getString(Lang.options), Choice.MULTIPLE);//GEN-BEGIN:|46-getter|1|46-postInit
            choiceAppearCh.append(LangHolder.getString(Lang.mattetext), null);
            choiceAppearCh.append("50% (30%)", null);
//choiceAppearCh.setSelectedFlags (new boolean[] { false, false });
//choiceAppearCh.setFont (0, null);
//choiceAppearCh.setFont (1, null);//GEN-END:|46-getter|1|46-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|46-getter|2|
        return choiceAppearCh;
    }
//</editor-fold>//GEN-END:|46-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceForeColor ">//GEN-BEGIN:|49-getter|0|49-preInit
    /**
     * Returns an initiliazed instance of choiceForeColor component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceForeColor() {
        if (choiceForeColor==null){//GEN-END:|49-getter|0|49-preInit
            // Insert pre-init code here
            choiceForeColor=new ChoiceGroup(LangHolder.getString(Lang.mapfont)+':'+' '+LangHolder.getString(Lang.forecolor), Choice.POPUP);//GEN-LINE:|49-getter|1|49-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|49-getter|2|
        return choiceForeColor;
    }
//</editor-fold>//GEN-END:|49-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceShadowColor ">//GEN-BEGIN:|50-getter|0|50-preInit
    /**
     * Returns an initiliazed instance of choiceShadowColor component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceShadowColor() {
        if (choiceShadowColor==null){//GEN-END:|50-getter|0|50-preInit
            // Insert pre-init code here
            choiceShadowColor=new ChoiceGroup(LangHolder.getString(Lang.mapfont)+':'+' '+LangHolder.getString(Lang.shadow), Choice.POPUP);//GEN-LINE:|50-getter|1|50-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|50-getter|2|
        return choiceShadowColor;
    }
//</editor-fold>//GEN-END:|50-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceViewports2 ">//GEN-BEGIN:|51-getter|0|51-preInit
    /**
     * Returns an initiliazed instance of choiceViewports2 component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceViewports2() {
        if (choiceViewports2==null){//GEN-END:|51-getter|0|51-preInit
            // Insert pre-init code here
            choiceViewports2=new ChoiceGroup(LangHolder.getString(Lang.view)+" 2", Choice.POPUP);//GEN-BEGIN:|51-getter|1|51-postInit
            choiceViewports2.append(LangHolder.getString(Lang.map), null);
            choiceViewports2.append(LangHolder.getString(Lang.compass), null);
            choiceViewports2.append(LangHolder.getString(Lang.trheight), null);
            choiceViewports2.append(LangHolder.getString(Lang.position), null);
            choiceViewports2.append(LangHolder.getString(Lang.satellites), null);
            choiceViewports2.append(LangHolder.getString(Lang.info), null);
            choiceViewports2.append(LangHolder.getString(Lang.speed), null);
            choiceViewports2.append(LangHolder.getString(Lang.variometer), null);
            choiceViewports2.append(LangHolder.getString(Lang.navigation), null);
//choiceViewports2.setSelectedFlags (new boolean[] { false, false, false, false, false, false, false, false });
//choiceViewports2.setFont (0, null);
//choiceViewports2.setFont (1, null);
//choiceViewports2.setFont (2, null);
//choiceViewports2.setFont (3, null);
//choiceViewports2.setFont (4, null);
//choiceViewports2.setFont (5, null);
//choiceViewports2.setFont (6, null);
//choiceViewports2.setFont (7, null);//GEN-END:|51-getter|1|51-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|51-getter|2|
        return choiceViewports2;
    }
//</editor-fold>//GEN-END:|51-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceViewports3 ">//GEN-BEGIN:|60-getter|0|60-preInit
    /**
     * Returns an initiliazed instance of choiceViewports3 component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceViewports3() {
        if (choiceViewports3==null){//GEN-END:|60-getter|0|60-preInit
            // Insert pre-init code here
            choiceViewports3=new ChoiceGroup(LangHolder.getString(Lang.view)+" 3", Choice.POPUP);//GEN-BEGIN:|60-getter|1|60-postInit
            choiceViewports3.append(LangHolder.getString(Lang.map), null);
            choiceViewports3.append(LangHolder.getString(Lang.compass), null);
            choiceViewports3.append(LangHolder.getString(Lang.trheight), null);
            choiceViewports3.append(LangHolder.getString(Lang.position), null);
            choiceViewports3.append(LangHolder.getString(Lang.satellites), null);
            choiceViewports3.append(LangHolder.getString(Lang.info), null);
            choiceViewports3.append(LangHolder.getString(Lang.speed), null);
            choiceViewports3.append(LangHolder.getString(Lang.variometer), null);
            choiceViewports3.append(LangHolder.getString(Lang.navigation), null);
//choiceViewports3.setSelectedFlags (new boolean[] { false, false, false, false, false, false, false, false });
//choiceViewports3.setFont (0, null);
//choiceViewports3.setFont (1, null);
//choiceViewports3.setFont (2, null);
//choiceViewports3.setFont (3, null);
//choiceViewports3.setFont (4, null);
//choiceViewports3.setFont (5, null);
//choiceViewports3.setFont (6, null);
//choiceViewports3.setFont (7, null);//GEN-END:|60-getter|1|60-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|60-getter|2|
        return choiceViewports3;
    }
//</editor-fold>//GEN-END:|60-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceViewports4 ">//GEN-BEGIN:|69-getter|0|69-preInit
    /**
     * Returns an initiliazed instance of choiceViewports4 component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceViewports4() {
        if (choiceViewports4==null){//GEN-END:|69-getter|0|69-preInit
            // Insert pre-init code here
            choiceViewports4=new ChoiceGroup(LangHolder.getString(Lang.view)+" 4", Choice.POPUP);//GEN-BEGIN:|69-getter|1|69-postInit
            choiceViewports4.append(LangHolder.getString(Lang.map), null);
            choiceViewports4.append(LangHolder.getString(Lang.compass), null);
            choiceViewports4.append(LangHolder.getString(Lang.trheight), null);
            choiceViewports4.append(LangHolder.getString(Lang.position), null);
            choiceViewports4.append(LangHolder.getString(Lang.satellites), null);
            choiceViewports4.append(LangHolder.getString(Lang.info), null);
            choiceViewports4.append(LangHolder.getString(Lang.speed), null);
            choiceViewports4.append(LangHolder.getString(Lang.variometer), null);
            choiceViewports4.append(LangHolder.getString(Lang.navigation), null);
//choiceViewports4.setSelectedFlags (new boolean[] { false, false, false, false, false, false, false, false });
//choiceViewports4.setFont (0, null);
//choiceViewports4.setFont (1, null);
//choiceViewports4.setFont (2, null);
//choiceViewports4.setFont (3, null);
//choiceViewports4.setFont (4, null);
//choiceViewports4.setFont (5, null);
//choiceViewports4.setFont (6, null);
//choiceViewports4.setFont (7, null);//GEN-END:|69-getter|1|69-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|69-getter|2|
        return choiceViewports4;
    }
//</editor-fold>//GEN-END:|69-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: formOptAlr ">//GEN-BEGIN:|80-getter|0|80-preInit
    /**
     * Returns an initiliazed instance of formOptAlr component.
     * @return the initialized component instance
     */
    public Form getFormOptAlr() {
        if (formOptAlr==null){//GEN-END:|80-getter|0|80-preInit
            // Insert pre-init code here
            formOptAlr=new Form(LangHolder.getString(Lang.alerts), new Item[]{getChoiceSounds(), getTextMaxSpd(), getTextMaxClm(), getTextMaxDsc(), getGaugeVolume()});//GEN-BEGIN:|80-getter|1|80-postInit
            formOptAlr.addCommand(getBack2Opt());
            formOptAlr.addCommand(getItemSaveOpt());
            formOptAlr.addCommand(getItemTest());
            formOptAlr.setCommandListener(this);//GEN-END:|80-getter|1|80-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|80-getter|2|
        return formOptAlr;
    }
//</editor-fold>//GEN-END:|80-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceSounds ">//GEN-BEGIN:|84-getter|0|84-preInit
    /**
     * Returns an initiliazed instance of choiceSounds component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceSounds() {
        if (choiceSounds==null){//GEN-END:|84-getter|0|84-preInit
            // Insert pre-init code here
            choiceSounds=new ChoiceGroup(LangHolder.getString(Lang.sounds), Choice.MULTIPLE);//GEN-BEGIN:|84-getter|1|84-postInit
            choiceSounds.append(LangHolder.getString(Lang.sndon), null);
            choiceSounds.append(LangHolder.getString(Lang.sndsat), null);
            choiceSounds.append(LangHolder.getString(Lang.vibration), null);
            choiceSounds.append(LangHolder.getString(Lang.soundontp), null);
            choiceSounds.append("Max "+LangHolder.getString(Lang.speed), null);
            choiceSounds.append("Waypoint approach", null);
            choiceSounds.append(LangHolder.getString(Lang.updownsound), null);
            // Insert post-init code here
        }//GEN-BEGIN:|84-getter|2|
        return choiceSounds;
    }
//</editor-fold>//GEN-END:|84-getter|2|

    /**
     * Returns an initiliazed instance of textMaxSpd component.
     * @return the initialized component instance
     */
    public TextField getTextMaxSpd() {
        if (textMaxSpd==null){
            // Insert pre-init code here
            //!NO DECIMAL
            textMaxSpd=new TextField("Max "+LangHolder.getString(Lang.speed)+' '+'('+LangHolder.getString(Lang.kmh)+')', null, 6, TextField.ANY);//GEN-LINE:|90-getter|1|90-postInit
            // Insert post-init code here
        }
        return textMaxSpd;
    }

    public TextField getTextMaxDsc() {
        if (textMaxDsc==null){
            // Insert pre-init code here
            //!NO DECIMAL
            textMaxDsc=new TextField(LangHolder.getString(Lang.downwarnspeed), null, 4, TextField.ANY);//GEN-LINE:|90-getter|1|90-postInit
            // Insert post-init code here
        }
        return textMaxDsc;
    }

    public TextField getTextMaxClm() {
        if (textMaxClm==null){
            // Insert pre-init code here
            //!NO DECIMAL
            textMaxClm=new TextField(LangHolder.getString(Lang.upwarnspeed), null, 4, TextField.ANY);//GEN-LINE:|90-getter|1|90-postInit
            // Insert post-init code here
        }
        return textMaxClm;
    }

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: gaugeVolume ">//GEN-BEGIN:|91-getter|0|91-preInit
    /**
     * Returns an initiliazed instance of gaugeVolume component.
     * @return the initialized component instance
     */
    public Gauge getGaugeVolume() {
        if (gaugeVolume==null){//GEN-END:|91-getter|0|91-preInit
            // Insert pre-init code here
            gaugeVolume=new Gauge(LangHolder.getString(Lang.volume), true, 10, 5);//GEN-LINE:|91-getter|1|91-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|91-getter|2|
        return gaugeVolume;
    }
//</editor-fold>//GEN-END:|91-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: formOptKeys ">//GEN-BEGIN:|94-getter|0|94-preInit
    /**
     * Returns an initiliazed instance of formOptKeys component.
     * @return the initialized component instance
     */
    public Form getFormOptKeys() {
        if (formOptKeys==null){//GEN-END:|94-getter|0|94-preInit
            // Insert pre-init code here
            formOptKeys=new Form(LangHolder.getString(Lang.keys), new Item[]{getChoice5Add(), getChoiceKeys(), getChoiceKey1(), getChoiceKey1Hold(), getChoiceKey2(), getChoiceKey2Hold(), getChoiceKey3(), getChoiceKey3Hold(), getChoiceKey4(), getChoiceKey4Hold(), getChoiceKey5Hold(), getChoiceKey6(), getChoiceKey6Hold(), getChoiceKey7(), getChoiceKey7Hold(), getChoiceKey8(), getChoiceKey8Hold(), getChoiceKey9(), getChoiceKey9Hold(), getChoiceKey0(), getChoiceKeyCH()});//GEN-BEGIN:|94-getter|1|94-postInit
            formOptKeys.addCommand(getItemSaveOpt());
            formOptKeys.addCommand(getBack2Opt());
            formOptKeys.setCommandListener(this);//GEN-END:|94-getter|1|94-postInit
            // Insert post-init code here
            setKeyChoice(getChoiceKey1());
            setKeyChoice(getChoiceKey1Hold());
            setKeyChoice(getChoiceKey2());
            setKeyChoice(getChoiceKey2Hold());
            setKeyChoice(getChoiceKey3());
            setKeyChoice(getChoiceKey3Hold());
            setKeyChoice(getChoiceKey4());
            setKeyChoice(getChoiceKey4Hold());
            setKeyChoice(getChoiceKey5Hold());
            setKeyChoice(getChoiceKey6());
            setKeyChoice(getChoiceKey6Hold());
            setKeyChoice(getChoiceKey7());
            setKeyChoice(getChoiceKey7Hold());
            setKeyChoice(getChoiceKey8());
            setKeyChoice(getChoiceKey8Hold());
            setKeyChoice(getChoiceKey9());
            setKeyChoice(getChoiceKey9Hold());
            setKeyChoice(getChoiceKey0());
            setKeyChoice(getChoiceKeyCH());

        }//GEN-BEGIN:|94-getter|2|
        return formOptKeys;
    }
//</editor-fold>//GEN-END:|94-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choice5Add ">//GEN-BEGIN:|98-getter|0|98-preInit
    /**
     * Returns an initiliazed instance of choice5Add component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoice5Add() {
        if (choice5Add==null){//GEN-END:|98-getter|0|98-preInit
            // Insert pre-init code here
            choice5Add=new ChoiceGroup(LangHolder.getString(Lang.hold5add), Choice.EXCLUSIVE);//GEN-BEGIN:|98-getter|1|98-postInit
            choice5Add.append(LangHolder.getString(Lang.waypoints), null);
            choice5Add.append(LangHolder.getString(Lang.marks), null);
            choice5Add.setFitPolicy(Choice.TEXT_WRAP_ON);
//choice5Add.setSelectedFlags (new boolean[] { false, false });
//choice5Add.setFont (0, null);
//choice5Add.setFont (1, null);//GEN-END:|98-getter|1|98-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|98-getter|2|
        return choice5Add;
    }
//</editor-fold>//GEN-END:|98-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceKeys ">//GEN-BEGIN:|101-getter|0|101-preInit
    /**
     * Returns an initiliazed instance of choiceKeys component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceKeys() {
        if (choiceKeys==null){//GEN-END:|101-getter|0|101-preInit
            // Insert pre-init code here
            choiceKeys=new ChoiceGroup(LangHolder.getString(Lang.keys), Choice.MULTIPLE);//GEN-BEGIN:|101-getter|1|101-postInit
            choiceKeys.append(LangHolder.getString(Lang.smoothscroll), null);
//choiceKeys.setSelectedFlags (new boolean[] { false });
//choiceKeys.setFont (0, null);//GEN-END:|101-getter|1|101-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|101-getter|2|
        return choiceKeys;
    }
//</editor-fold>//GEN-END:|101-getter|2|

    /**
     * Returns an initiliazed instance of choiceKey1 component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceKey1() {
        if (choiceKey1==null){
            // Insert pre-init code here
            choiceKey1=new ChoiceGroup(LangHolder.getString(Lang.key)+'1', Choice.POPUP);//GEN-LINE:|103-getter|1|103-postInit
            // Insert post-init code here
        }
        return choiceKey1;
    }

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceKey1Hold ">//GEN-BEGIN:|104-getter|0|104-preInit
    /**
     * Returns an initiliazed instance of choiceKey1Hold component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceKey1Hold() {
        if (choiceKey1Hold==null){//GEN-END:|104-getter|0|104-preInit
            // Insert pre-init code here
            choiceKey1Hold=new ChoiceGroup(LangHolder.getString(Lang.keyhold)+'1', Choice.POPUP);//GEN-LINE:|104-getter|1|104-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|104-getter|2|
        return choiceKey1Hold;
    }
//</editor-fold>//GEN-END:|104-getter|2|

    public ChoiceGroup getChoiceKey2() {
        if (choiceKey2==null){
            // Insert pre-init code here
            choiceKey2=new ChoiceGroup(LangHolder.getString(Lang.key)+'2', Choice.POPUP);//GEN-LINE:|103-getter|1|103-postInit
            // Insert post-init code here
        }
        return choiceKey2;
    }

    public ChoiceGroup getChoiceKey8() {
        if (choiceKey8==null){
            // Insert pre-init code here
            choiceKey8=new ChoiceGroup(LangHolder.getString(Lang.key)+'8', Choice.POPUP);//GEN-LINE:|103-getter|1|103-postInit
            // Insert post-init code here
        }
        return choiceKey8;
    }

    public ChoiceGroup getChoiceKey2Hold() {
        if (choiceKey2Hold==null){//GEN-END:|106-getter|0|106-preInit
            // Insert pre-init code here
            choiceKey2Hold=new ChoiceGroup(LangHolder.getString(Lang.keyhold)+'2', Choice.POPUP);//GEN-LINE:|106-getter|1|106-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|106-getter|2|
        return choiceKey2Hold;
    }

    public ChoiceGroup getChoiceKey8Hold() {
        if (choiceKey8Hold==null){
            // Insert pre-init code here
            choiceKey8Hold=new ChoiceGroup(LangHolder.getString(Lang.keyhold)+'8', Choice.POPUP);//GEN-LINE:|103-getter|1|103-postInit
            // Insert post-init code here
        }
        return choiceKey8Hold;
    }

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceKey3 ">//GEN-BEGIN:|105-getter|0|105-preInit
    /**
     * Returns an initiliazed instance of choiceKey3 component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceKey3() {
        if (choiceKey3==null){//GEN-END:|105-getter|0|105-preInit
            // Insert pre-init code here
            choiceKey3=new ChoiceGroup(LangHolder.getString(Lang.key)+'3', Choice.POPUP);//GEN-LINE:|105-getter|1|105-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|105-getter|2|
        return choiceKey3;
    }
//</editor-fold>//GEN-END:|105-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceKey3Hold ">//GEN-BEGIN:|106-getter|0|106-preInit
    /**
     * Returns an initiliazed instance of choiceKey3Hold component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceKey3Hold() {
        if (choiceKey3Hold==null){//GEN-END:|106-getter|0|106-preInit
            // Insert pre-init code here
            choiceKey3Hold=new ChoiceGroup(LangHolder.getString(Lang.keyhold)+'3', Choice.POPUP);//GEN-LINE:|106-getter|1|106-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|106-getter|2|
        return choiceKey3Hold;
    }
//</editor-fold>//GEN-END:|106-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceKey4 ">//GEN-BEGIN:|107-getter|0|107-preInit
    /**
     * Returns an initiliazed instance of choiceKey4 component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceKey4() {
        if (choiceKey4==null){//GEN-END:|107-getter|0|107-preInit
            // Insert pre-init code here
            choiceKey4=new ChoiceGroup(LangHolder.getString(Lang.key)+'4', Choice.POPUP);//GEN-LINE:|107-getter|1|107-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|107-getter|2|
        return choiceKey4;
    }
//</editor-fold>//GEN-END:|107-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceKey4Hold ">//GEN-BEGIN:|108-getter|0|108-preInit
    /**
     * Returns an initiliazed instance of choiceKey4Hold component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceKey4Hold() {
        if (choiceKey4Hold==null){//GEN-END:|108-getter|0|108-preInit
            // Insert pre-init code here
            choiceKey4Hold=new ChoiceGroup(LangHolder.getString(Lang.keyhold)+'4', Choice.POPUP);//GEN-LINE:|108-getter|1|108-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|108-getter|2|
        return choiceKey4Hold;
    }
//</editor-fold>//GEN-END:|108-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceKey5Hold ">//GEN-BEGIN:|109-getter|0|109-preInit
    /**
     * Returns an initiliazed instance of choiceKey5Hold component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceKey5Hold() {
        if (choiceKey5Hold==null){//GEN-END:|109-getter|0|109-preInit
            // Insert pre-init code here
            choiceKey5Hold=new ChoiceGroup(LangHolder.getString(Lang.keyhold)+'5', Choice.POPUP);//GEN-LINE:|109-getter|1|109-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|109-getter|2|
        return choiceKey5Hold;
    }
//</editor-fold>//GEN-END:|109-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceKey6 ">//GEN-BEGIN:|110-getter|0|110-preInit
    /**
     * Returns an initiliazed instance of choiceKey6 component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceKey6() {
        if (choiceKey6==null){//GEN-END:|110-getter|0|110-preInit
            // Insert pre-init code here
            choiceKey6=new ChoiceGroup(LangHolder.getString(Lang.key)+'6', Choice.POPUP);//GEN-LINE:|110-getter|1|110-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|110-getter|2|
        return choiceKey6;
    }
//</editor-fold>//GEN-END:|110-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceKey6Hold ">//GEN-BEGIN:|111-getter|0|111-preInit
    /**
     * Returns an initiliazed instance of choiceKey6Hold component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceKey6Hold() {
        if (choiceKey6Hold==null){//GEN-END:|111-getter|0|111-preInit
            // Insert pre-init code here
            choiceKey6Hold=new ChoiceGroup(LangHolder.getString(Lang.keyhold)+'6', Choice.POPUP);//GEN-LINE:|111-getter|1|111-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|111-getter|2|
        return choiceKey6Hold;
    }
//</editor-fold>//GEN-END:|111-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceKey7 ">//GEN-BEGIN:|112-getter|0|112-preInit
    /**
     * Returns an initiliazed instance of choiceKey7 component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceKey7() {
        if (choiceKey7==null){//GEN-END:|112-getter|0|112-preInit
            // Insert pre-init code here
            choiceKey7=new ChoiceGroup(LangHolder.getString(Lang.key)+'7', Choice.POPUP);//GEN-LINE:|112-getter|1|112-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|112-getter|2|
        return choiceKey7;
    }
//</editor-fold>//GEN-END:|112-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceKey7Hold ">//GEN-BEGIN:|113-getter|0|113-preInit
    /**
     * Returns an initiliazed instance of choiceKey7Hold component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceKey7Hold() {
        if (choiceKey7Hold==null){//GEN-END:|113-getter|0|113-preInit
            // Insert pre-init code here
            choiceKey7Hold=new ChoiceGroup(LangHolder.getString(Lang.keyhold)+'7', Choice.POPUP);//GEN-LINE:|113-getter|1|113-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|113-getter|2|
        return choiceKey7Hold;
    }
//</editor-fold>//GEN-END:|113-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceKey9 ">//GEN-BEGIN:|114-getter|0|114-preInit
    /**
     * Returns an initiliazed instance of choiceKey9 component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceKey9() {
        if (choiceKey9==null){//GEN-END:|114-getter|0|114-preInit
            // Insert pre-init code here
            choiceKey9=new ChoiceGroup(LangHolder.getString(Lang.key)+'9', Choice.POPUP);//GEN-LINE:|114-getter|1|114-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|114-getter|2|
        return choiceKey9;
    }
//</editor-fold>//GEN-END:|114-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceKey9Hold ">//GEN-BEGIN:|115-getter|0|115-preInit
    /**
     * Returns an initiliazed instance of choiceKey9Hold component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceKey9Hold() {
        if (choiceKey9Hold==null){//GEN-END:|115-getter|0|115-preInit
            // Insert pre-init code here
            choiceKey9Hold=new ChoiceGroup(LangHolder.getString(Lang.keyhold)+'9', Choice.POPUP);//GEN-LINE:|115-getter|1|115-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|115-getter|2|
        return choiceKey9Hold;
    }
//</editor-fold>//GEN-END:|115-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceKey0 ">//GEN-BEGIN:|116-getter|0|116-preInit
    /**
     * Returns an initiliazed instance of choiceKey0 component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceKey0() {
        if (choiceKey0==null){//GEN-END:|116-getter|0|116-preInit
            // Insert pre-init code here
            choiceKey0=new ChoiceGroup(LangHolder.getString(Lang.key)+'0', Choice.POPUP);//GEN-LINE:|116-getter|1|116-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|116-getter|2|
        return choiceKey0;
    }
//</editor-fold>//GEN-END:|116-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceKeyCH ">//GEN-BEGIN:|117-getter|0|117-preInit
    /**
     * Returns an initiliazed instance of choiceKeyCH component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceKeyCH() {
        if (choiceKeyCH==null){//GEN-END:|117-getter|0|117-preInit
            // Insert pre-init code here
            choiceKeyCH=new ChoiceGroup(LangHolder.getString(Lang.key)+'#', Choice.POPUP);//GEN-LINE:|117-getter|1|117-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|117-getter|2|
        return choiceKeyCH;
    }
//</editor-fold>//GEN-END:|117-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: formOptGeo ">//GEN-BEGIN:|120-getter|0|120-preInit
    /**
     * Returns an initiliazed instance of formOptGeo component.
     * @return the initialized component instance
     */
    public Form getFormOptGeo() {
        if (formOptGeo==null){//GEN-END:|120-getter|0|120-preInit
            // Insert pre-init code here
            formOptGeo=new Form(LangHolder.getString(Lang.geoinfo), new Item[]{getChoiceCoordType(), 
            getChoiceDatum(), getChoiceProj(), getChoiceMeasure(), getChoiceRouteSearch()});
            formOptGeo.addCommand(getBack2Opt());
            formOptGeo.addCommand(getItemSaveOpt());
            formOptGeo.setCommandListener(this);//GEN-END:|120-getter|1|120-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|120-getter|2|
        return formOptGeo;
    }
//</editor-fold>//GEN-END:|120-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceCoordType ">//GEN-BEGIN:|124-getter|0|124-preInit
    /**
     * Returns an initiliazed instance of choiceCoordType component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceCoordType() {
        if (choiceCoordType==null){//GEN-END:|124-getter|0|124-preInit
            // Insert pre-init code here
            choiceCoordType=new ChoiceGroup(LangHolder.getString(Lang.coordtype), Choice.POPUP);//GEN-BEGIN:|124-getter|1|124-postInit
            choiceCoordType.append("23\u00B040\'47\'\'", null);
            choiceCoordType.append("23\u00B040.783\'", null);
            choiceCoordType.append("23.67972\u00B0", null);
//choiceCoordType.setSelectedFlags (new boolean[] { false, false, false });
//choiceCoordType.setFont (0, null);
//choiceCoordType.setFont (1, null);
//choiceCoordType.setFont (2, null);//GEN-END:|124-getter|1|124-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|124-getter|2|
        return choiceCoordType;
    }
//</editor-fold>//GEN-END:|124-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceDatum ">//GEN-BEGIN:|128-getter|0|128-preInit
    /**
     * Returns an initiliazed instance of choiceDatum component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceDatum() {
        if (choiceDatum==null){//GEN-END:|128-getter|0|128-preInit
            // Insert pre-init code here
            choiceDatum=new ChoiceGroup(LangHolder.getString(Lang.dispdatum), Choice.POPUP);//GEN-BEGIN:|128-getter|1|128-postInit
            choiceDatum.append("WGS84", null);
            choiceDatum.append("\u041F\u0443\u043B\u043A\u043E\u0432\u043E 1942", null);


        }//GEN-BEGIN:|128-getter|2|
        return choiceDatum;
    }
//</editor-fold>//GEN-END:|128-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceProj ">//GEN-BEGIN:|131-getter|0|131-preInit
    /**
     * Returns an initiliazed instance of choiceProj component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceProj() {
        if (choiceProj==null){//GEN-END:|131-getter|0|131-preInit
            // Insert pre-init code here
            choiceProj=new ChoiceGroup(LangHolder.getString(Lang.dispproj), Choice.POPUP);//GEN-BEGIN:|131-getter|1|131-postInit
            choiceProj.append(LangHolder.getString(Lang.geodetic), null);
            choiceProj.append("Universal TM", null);
            choiceProj.append("Gauss-Kruger (USSR TM)", null);
//choiceProj.setSelectedFlags (new boolean[] { false, false, false });
//choiceProj.setFont (0, null);
//choiceProj.setFont (1, null);
//choiceProj.setFont (2, null);//GEN-END:|131-getter|1|131-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|131-getter|2|
        return choiceProj;
    }
//</editor-fold>//GEN-END:|131-getter|2|



    public ChoiceGroup getChoiceMeasure() {
        if (choiceMeasure==null){

            choiceMeasure=new ChoiceGroup(LangHolder.getString(Lang.measureunits), Choice.POPUP);//GEN-BEGIN:|135-getter|1|135-postInit
            choiceMeasure.append(LangHolder.getString(Lang.metric), null);
            choiceMeasure.append(LangHolder.getString(Lang.nautical), null);
            choiceMeasure.append(LangHolder.getString(Lang.imperial), null);

        }

        return choiceMeasure;
    }

     public ChoiceGroup getChoiceRouteSearch() {
        if (choiceRouteSearch==null){

            choiceRouteSearch=new ChoiceGroup(LangHolder.getString(Lang.routes), Choice.POPUP);//GEN-BEGIN:|135-getter|1|135-postInit
            choiceRouteSearch.append("CloudMade", null);
            choiceRouteSearch.append("Google", null);

        }
        return choiceRouteSearch;
    }


//<editor-fold defaultstate="collapsed" desc=" Generated Getter: formOptGPS ">//GEN-BEGIN:|141-getter|0|141-preInit
    /**
     * Returns an initiliazed instance of formOptGPS component.
     * @return the initialized component instance
     */
    public Form getFormOptGPS() {
        if (formOptGPS==null){//GEN-END:|141-getter|0|141-preInit
            // Insert pre-init code here
            formOptGPS=new Form("GPS", new Item[]{getChoiceGPSKind(), getTextGPSCOM(), getChoiceGpsOpt(), getChoiceGPSReconnectDelay()});
            formOptGPS.addCommand(getBack2Opt());
            formOptGPS.addCommand(getItemSaveOpt());
            formOptGPS.setCommandListener(this);//GEN-END:|141-getter|1|141-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|141-getter|2|
        return formOptGPS;
    }
//</editor-fold>//GEN-END:|141-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceGPSKind ">//GEN-BEGIN:|145-getter|0|145-preInit
    /**
     * Returns an initiliazed instance of choiceGPSKind component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceGPSKind() {
        if (choiceGPSKind==null){//GEN-END:|145-getter|0|145-preInit
            // Insert pre-init code here
            choiceGPSKind=new ChoiceGroup(LangHolder.getString(Lang.conntype), Choice.POPUP);//GEN-BEGIN:|145-getter|1|145-postInit
            choiceGPSKind.append("Bluetooth", null);
            choiceGPSKind.append("COM", null);
            choiceGPSKind.append(LangHolder.getString(Lang.builtin), null);
            choiceGPSKind.append("Socket", null);
//choiceGPSKind.setSelectedFlags (new boolean[] { false, false, false });
//choiceGPSKind.setFont (0, null);
//choiceGPSKind.setFont (1, null);
//choiceGPSKind.setFont (2, null);//GEN-END:|145-getter|1|145-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|145-getter|2|
        return choiceGPSKind;
    }
//</editor-fold>//GEN-END:|145-getter|2|

    public ChoiceGroup getChoiceOnlineUrlSUR() {
        if (choiceOnlineUrlSUR==null){
            choiceOnlineUrlSUR=new ChoiceGroup("Online 1 type", Choice.POPUP);
            choiceOnlineUrlSUR.append("google map", null);
            choiceOnlineUrlSUR.append("OSM", null);
        }
        return choiceOnlineUrlSUR;
    }

    public ChoiceGroup getChoiceOnlineUrlMAP() {
        if (choiceOnlineUrlMAP==null){
            choiceOnlineUrlMAP=new ChoiceGroup("Online 2 type", Choice.POPUP);
            choiceOnlineUrlMAP.append("google map", null);
            choiceOnlineUrlMAP.append("OSM", null);
        }
        return choiceOnlineUrlMAP;
    }

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: textGPSCOM ">//GEN-BEGIN:|149-getter|0|149-preInit
    /**
     * Returns an initiliazed instance of textGPSCOM component.
     * @return the initialized component instance
     */
    public TextField getTextGPSCOM() {
        if (textGPSCOM==null){//GEN-END:|149-getter|0|149-preInit
            // Insert pre-init code here
            textGPSCOM=new TextField("COM-"+LangHolder.getString(Lang.port)+" or host:port", null, 32, TextField.ANY);//GEN-BEGIN:|149-getter|1|149-postInit
            textGPSCOM.addCommand(getItemCommandListCOM());
            textGPSCOM.setItemCommandListener(this);//GEN-END:|149-getter|1|149-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|149-getter|2|
        return textGPSCOM;
    }
//</editor-fold>//GEN-END:|149-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceGpsOpt ">//GEN-BEGIN:|152-getter|0|152-preInit
    /**
     * Returns an initiliazed instance of choiceGpsOpt component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceGpsOpt() {
        if (choiceGpsOpt==null){//GEN-END:|152-getter|0|152-preInit
            // Insert pre-init code here
            choiceGpsOpt=new ChoiceGroup(LangHolder.getString(Lang.options), Choice.MULTIPLE);//GEN-BEGIN:|152-getter|1|152-postInit
            choiceGpsOpt.append(LangHolder.getString(Lang.autoreconn), null);
            choiceGpsOpt.append("HGE-100", null);
            choiceGpsOpt.append("Bluetooth auth", null);
            choiceGpsOpt.append("Bluetooth monitor", null);
        }//GEN-BEGIN:|152-getter|2|
        return choiceGpsOpt;
    }
//</editor-fold>//GEN-END:|152-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: formOptTr ">//GEN-BEGIN:|157-getter|0|157-preInit
    /**
     * Returns an initiliazed instance of formOptTr component.
     * @return the initialized component instance
     */
    public Form getFormOptTr() {
        if (formOptTr==null){//GEN-END:|157-getter|0|157-preInit
            // Insert pre-init code here
            formOptTr=new Form(LangHolder.getString(Lang.track), new Item[]{getChoiceTrackPC(), getChoiceTrackUse(), getChoiceTrackPeriod(), getChoiceTrackDist(), getChoiceRouteProx(), getChoiceAutoWpt(), getChoiceAutoWPTType(), getTextMaxTP()});//GEN-BEGIN:|157-getter|1|157-postInit
            formOptTr.addCommand(getBack2Opt());
            formOptTr.addCommand(getItemSaveOpt());
            formOptTr.setCommandListener(this);//GEN-END:|157-getter|1|157-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|157-getter|2|
        return formOptTr;
    }
//</editor-fold>//GEN-END:|157-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceTrackPC ">//GEN-BEGIN:|161-getter|0|161-preInit
    /**
     * Returns an initiliazed instance of choiceTrackPC component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceTrackPC() {
        if (choiceTrackPC==null){//GEN-END:|161-getter|0|161-preInit
            // Insert pre-init code here
            choiceTrackPC=new ChoiceGroup(LangHolder.getString(Lang.trackpc), Choice.POPUP);//GEN-BEGIN:|161-getter|1|161-postInit
            choiceTrackPC.append(LangHolder.getString(Lang.last100), null);
            choiceTrackPC.append(LangHolder.getString(Lang.all), null);
//choiceTrackPC.setSelectedFlags (new boolean[] { false, false });
//choiceTrackPC.setFont (0, null);
//choiceTrackPC.setFont (1, null);//GEN-END:|161-getter|1|161-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|161-getter|2|
        return choiceTrackPC;
    }
//</editor-fold>//GEN-END:|161-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceTrackUse ">//GEN-BEGIN:|164-getter|0|164-preInit
    /**
     * Returns an initiliazed instance of choiceTrackUse component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceTrackUse() {
        if (choiceTrackUse==null){//GEN-END:|164-getter|0|164-preInit
            // Insert pre-init code here
            choiceTrackUse=new ChoiceGroup(LangHolder.getString(Lang.trackopt), Choice.MULTIPLE);//GEN-BEGIN:|164-getter|1|164-postInit
            choiceTrackUse.append(LangHolder.getString(Lang.usetrtime), null);
            choiceTrackUse.append(LangHolder.getString(Lang.usetrdist), null);
            choiceTrackUse.append(LangHolder.getString(Lang.usetrturn), null);
//choiceTrackUse.setSelectedFlags (new boolean[] { false, false, false });
//choiceTrackUse.setFont (0, null);
//choiceTrackUse.setFont (1, null);
//choiceTrackUse.setFont (2, null);//GEN-END:|164-getter|1|164-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|164-getter|2|
        return choiceTrackUse;
    }
//</editor-fold>//GEN-END:|164-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceTrackPeriod ">//GEN-BEGIN:|168-getter|0|168-preInit
    /**
     * Returns an initiliazed instance of choiceTrackPeriod component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceTrackPeriod() {
        if (choiceTrackPeriod==null){//GEN-END:|168-getter|0|168-preInit
            // Insert pre-init code here
            choiceTrackPeriod=new ChoiceGroup(LangHolder.getString(Lang.trackperiod), Choice.POPUP);//GEN-BEGIN:|168-getter|1|168-postInit
            choiceTrackPeriod.append("1 "+LangHolder.getString(Lang.sec), null);
            choiceTrackPeriod.append("5 "+LangHolder.getString(Lang.sec), null);
            choiceTrackPeriod.append("10 "+LangHolder.getString(Lang.sec), null);
            choiceTrackPeriod.append("30 "+LangHolder.getString(Lang.sec), null);
            choiceTrackPeriod.append("1 "+LangHolder.getString(Lang.min), null);
            choiceTrackPeriod.append("2 "+LangHolder.getString(Lang.min), null);
            choiceTrackPeriod.append("5 "+LangHolder.getString(Lang.min), null);
//choiceTrackPeriod.setSelectedFlags (new boolean[] { false, false, false, false, false, false });
//choiceTrackPeriod.setFont (0, null);
//choiceTrackPeriod.setFont (1, null);
//choiceTrackPeriod.setFont (2, null);
//choiceTrackPeriod.setFont (3, null);
//choiceTrackPeriod.setFont (4, null);
//choiceTrackPeriod.setFont (5, null);//GEN-END:|168-getter|1|168-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|168-getter|2|
        return choiceTrackPeriod;
    }
//</editor-fold>//GEN-END:|168-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceTrackDist ">//GEN-BEGIN:|174-getter|0|174-preInit
    /**
     * Returns an initiliazed instance of choiceTrackDist component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceTrackDist() {
        if (choiceTrackDist==null){//GEN-END:|174-getter|0|174-preInit
            // Insert pre-init code here
            choiceTrackDist=new ChoiceGroup(LangHolder.getString(Lang.trackdist), Choice.POPUP);//GEN-BEGIN:|174-getter|1|174-postInit
            choiceTrackDist.append("10 "+LangHolder.getString(Lang.m), null);
            choiceTrackDist.append("30 "+LangHolder.getString(Lang.m), null);
            choiceTrackDist.append("60 "+LangHolder.getString(Lang.m), null);
            choiceTrackDist.append("100 "+LangHolder.getString(Lang.m), null);
            choiceTrackDist.append("250 "+LangHolder.getString(Lang.m), null);
            choiceTrackDist.append("500 "+LangHolder.getString(Lang.m), null);
            choiceTrackDist.append("1 "+LangHolder.getString(Lang.km), null);
            choiceTrackDist.append("2 "+LangHolder.getString(Lang.km), null);
            choiceTrackDist.append("5 "+LangHolder.getString(Lang.km), null);
//choiceTrackDist.setSelectedFlags (new boolean[] { false, false, false, false, false, false, false });
//choiceTrackDist.setFont (0, null);
//choiceTrackDist.setFont (1, null);
//choiceTrackDist.setFont (2, null);
//choiceTrackDist.setFont (3, null);
//choiceTrackDist.setFont (4, null);
//choiceTrackDist.setFont (5, null);
//choiceTrackDist.setFont (6, null);//GEN-END:|174-getter|1|174-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|174-getter|2|
        return choiceTrackDist;
    }
//</editor-fold>//GEN-END:|174-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceRouteProx ">//GEN-BEGIN:|182-getter|0|182-preInit
    /**
     * Returns an initiliazed instance of choiceRouteProx component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceRouteProx() {
        if (choiceRouteProx==null){//GEN-END:|182-getter|0|182-preInit
            // Insert pre-init code here
            choiceRouteProx=new ChoiceGroup(LangHolder.getString(Lang.wayptsprox), Choice.POPUP);//GEN-BEGIN:|182-getter|1|182-postInit
            choiceRouteProx.append("10 "+LangHolder.getString(Lang.m), null);
            choiceRouteProx.append("50 "+LangHolder.getString(Lang.m), null);
            choiceRouteProx.append("100 "+LangHolder.getString(Lang.m), null);
            choiceRouteProx.append("250 "+LangHolder.getString(Lang.m), null);
            choiceRouteProx.append("500 "+LangHolder.getString(Lang.m), null);
            choiceRouteProx.append("1000 "+LangHolder.getString(Lang.m), null);
//choiceRouteProx.setSelectedFlags (new boolean[] { false, false, false, false, false, false });
//choiceRouteProx.setFont (0, null);
//choiceRouteProx.setFont (1, null);
//choiceRouteProx.setFont (2, null);
//choiceRouteProx.setFont (3, null);
//choiceRouteProx.setFont (4, null);
//choiceRouteProx.setFont (5, null);//GEN-END:|182-getter|1|182-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|182-getter|2|
        return choiceRouteProx;
    }
//</editor-fold>//GEN-END:|182-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceAutoWpt ">//GEN-BEGIN:|189-getter|0|189-preInit
    /**
     * Returns an initiliazed instance of choiceAutoWpt component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceAutoWpt() {
        if (choiceAutoWpt==null){//GEN-END:|189-getter|0|189-preInit
            // Insert pre-init code here
            choiceAutoWpt=new ChoiceGroup(LangHolder.getString(Lang.advanceopt), Choice.MULTIPLE);//GEN-BEGIN:|189-getter|1|189-postInit
            choiceAutoWpt.append(LangHolder.getString(Lang.trackautostart), null);
            choiceAutoWpt.append(LangHolder.getString(Lang.cleandeftr), null);
            choiceAutoWpt.append(LangHolder.getString(Lang.autoselectwpt), null);
            choiceAutoWpt.append(LangHolder.getString(Lang.limitshowtrack), null);
            choiceAutoWpt.append(LangHolder.getString(Lang.coloredtrack), null);
            choiceAutoWpt.append(LangHolder.getString(Lang.trackbackup), null);
//choiceAutoWpt.setSelectedFlags (new boolean[] { false, false, false, false, false });
//choiceAutoWpt.setFont (0, null);
//choiceAutoWpt.setFont (1, null);
//choiceAutoWpt.setFont (2, null);
//choiceAutoWpt.setFont (3, null);
//choiceAutoWpt.setFont (4, null);//GEN-END:|189-getter|1|189-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|189-getter|2|
        return choiceAutoWpt;
    }
//</editor-fold>//GEN-END:|189-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceAutoWPTType ">//GEN-BEGIN:|195-getter|0|195-preInit
    /**
     * Returns an initiliazed instance of choiceAutoWPTType component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceAutoWPTType() {
        if (choiceAutoWPTType==null){
            // Insert pre-init code here
            choiceAutoWPTType=new ChoiceGroup(LangHolder.getString(Lang.autoselectwpt), Choice.POPUP);//GEN-BEGIN:|195-getter|1|195-postInit
            choiceAutoWPTType.append(LangHolder.getString(Lang.autowptnext), null);
            choiceAutoWPTType.append(LangHolder.getString(Lang.autowptnear), null);
//choiceAutoWPTType.setSelectedFlags (new boolean[] { false, false });
//choiceAutoWPTType.setFont (0, null);
//choiceAutoWPTType.setFont (1, null);
            // Insert post-init code here
        }//GEN-BEGIN:|195-getter|2|
        return choiceAutoWPTType;
    }
//</editor-fold>//GEN-END:|195-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: textMaxTP ">//GEN-BEGIN:|198-getter|0|198-preInit
    /**
     * Returns an initiliazed instance of textMaxTP component.
     * @return the initialized component instance
     */
    public TextField getTextMaxTP() {
        if (textMaxTP==null){//GEN-END:|198-getter|0|198-preInit
            // Insert pre-init code here
            //!NO-NUMERIC
            textMaxTP=new TextField(LangHolder.getString(Lang.points2startnew), null, 5, TextField.ANY);//GEN-LINE:|198-getter|1|198-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|198-getter|2|
        return textMaxTP;
    }
//</editor-fold>//GEN-END:|198-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: formOptMaps ">//GEN-BEGIN:|201-getter|0|201-preInit
    /**
     * Returns an initiliazed instance of formOptMaps component.
     * @return the initialized component instance
     */
    public Form getFormOptMaps() {
        if (formOptMaps==null){//GEN-END:|201-getter|0|201-preInit
            // Insert pre-init code here
            formOptMaps=new Form(LangHolder.getString(Lang.maps), new Item[]{getChoiceEMOpts(),
                  getChoiceMaps(), getTextOSMURL(), getOnlineUrlSUR(), getChoiceOnlineUrlSUR(), getOnlineUrlMAP(), getChoiceOnlineUrlMAP()});
            formOptMaps.addCommand(getBack2Opt());
            formOptMaps.addCommand(getItemSaveOpt());
            formOptMaps.setCommandListener(this);//GEN-END:|201-getter|1|201-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|201-getter|2|
        return formOptMaps;
    }
//</editor-fold>//GEN-END:|201-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceEMOpts ">//GEN-BEGIN:|205-getter|0|205-preInit
    /**
     * Returns an initiliazed instance of choiceEMOpts component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceEMOpts() {
        if (choiceEMOpts==null){//GEN-END:|205-getter|0|205-preInit
            // Insert pre-init code here
            choiceEMOpts=new ChoiceGroup(LangHolder.getString(Lang.maps), Choice.MULTIPLE);//GEN-BEGIN:|205-getter|1|205-postInit
            choiceEMOpts.append(LangHolder.getString(Lang.autocenter), null);
            choiceEMOpts.append(LangHolder.getString(Lang.cacheindex), null);
//choiceEMOpts.setSelectedFlags (new boolean[] { false, false });
//choiceEMOpts.setFont (0, null);
//choiceEMOpts.setFont (1, null);//GEN-END:|205-getter|1|205-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|205-getter|2|
        return choiceEMOpts;
    }
//</editor-fold>//GEN-END:|205-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceMaps ">//GEN-BEGIN:|208-getter|0|208-preInit
    /**
     * Returns an initiliazed instance of choiceMaps component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceMaps() {
        if (choiceMaps==null){//GEN-END:|208-getter|0|208-preInit
            // Insert pre-init code here
            choiceMaps=new ChoiceGroup(LangHolder.getString(Lang.mapstorotate), Choice.MULTIPLE);//GEN-BEGIN:|208-getter|1|208-postInit
            choiceMaps.append("User Map", null);
            choiceMaps.append("Google Surface", null);
            choiceMaps.append("Google Hybrid", null);
            choiceMaps.append("Google Map", null);
            choiceMaps.append("VirtEarth Surface", null);
            choiceMaps.append("VirtEarth Map", null);
            choiceMaps.append("Yahoo Surface", null);
            choiceMaps.append("Yahoo Map", null);
            choiceMaps.append("Ask Surface", null);
            choiceMaps.append("Ask Hybrid", null);
            choiceMaps.append("Ask Map", null);
            choiceMaps.append("OpenStreetMap (Mapnik)", null);
            choiceMaps.append("OpenStreetMap (OsmRender)", null);
            choiceMaps.append("Gurtam", null);
            choiceMaps.append("Custom Online 1", null);
            choiceMaps.append("Custom Online 2", null);
            // Insert post-init code here
        }//GEN-BEGIN:|208-getter|2|
        return choiceMaps;
    }
//</editor-fold>//GEN-END:|208-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: formOptLang ">//GEN-BEGIN:|225-getter|0|225-preInit
    /**
     * Returns an initiliazed instance of formOptLang component.
     * @return the initialized component instance
     */
    public Form getFormOptLang() {
        if (formOptLang==null){//GEN-END:|225-getter|0|225-preInit
            // Insert pre-init code here
            formOptLang=new Form(LangHolder.getString(Lang.language), new Item[]{getChoiceLang()});//GEN-BEGIN:|225-getter|1|225-postInit
            formOptLang.addCommand(getBack2Opt());
            formOptLang.addCommand(getItemSaveOpt());
            formOptLang.setCommandListener(this);//GEN-END:|225-getter|1|225-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|225-getter|2|
        return formOptLang;
    }
//</editor-fold>//GEN-END:|225-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceLang ">//GEN-BEGIN:|229-getter|0|229-preInit
    /**
     * Returns an initiliazed instance of choiceLang component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceLang() {
        if (choiceLang==null){//GEN-END:|229-getter|0|229-preInit
            // Insert pre-init code here
            choiceLang=new ChoiceGroup(LangHolder.getString(Lang.setlang), Choice.EXCLUSIVE);//GEN-BEGIN:|229-getter|1|229-postInit
            choiceLang.append("English", null);
            choiceLang.append("\u0420\u0443\u0441\u0441\u043A\u0438\u0439", null);
            choiceLang.append("Deutsch", null);
            choiceLang.append("Portuguese", null);
            choiceLang.append("Suomen kieli ", null);
            choiceLang.append("\u0423\u043A\u0440\u0430\u0457\u043D\u0441\u044C\u043A\u0430 \u043C\u043E\u0432\u0430", null);
            choiceLang.append("\u0411\u044A\u043B\u0433\u0430\u0440\u0441\u043A\u0438", null);
            choiceLang.append("Slovensky", null);
            choiceLang.append("Lietuviu", null);
            choiceLang.append("Espa\u00F1ol", null);
            choiceLang.append("Polski", null);
            choiceLang.append("Norsk", null);
            choiceLang.append("T\u00FCrk\u00E7e", null);
            choiceLang.append("\u0639\u0631\u0628\u064A", null);
            choiceLang.append("Magyar", null);
            choiceLang.append("Fran\u00E7ais", null);
            choiceLang.append("\u010Cesky", null);
            choiceLang.append("Italiano", null);

            // Insert post-init code here
        }//GEN-BEGIN:|229-getter|2|
        return choiceLang;
    }
//</editor-fold>//GEN-END:|229-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: formOptRadar ">//GEN-BEGIN:|248-getter|0|248-preInit
    /**
     * Returns an initiliazed instance of formOptRadar component.
     * @return the initialized component instance
     */
    public Form getFormOptRadar() {
        if (formOptRadar==null){//GEN-END:|248-getter|0|248-preInit
            // Insert pre-init code here
            formOptRadar=new Form("NetRadar", new Item[]{
                  new StringItem("NETRADAR.RU", "Register for free at netradar.ru to obtain Login and Password"),
                  getTextRadLogin(), getTextRadPass(), getChoiceNetRadar(), getChoiceNRTrackPeriod(), getStringRadarBytes(), getTextOSMLogin(), getTextOSMPass()});//GEN-BEGIN:|248-getter|1|248-postInit
            formOptRadar.addCommand(getBack2Opt());
            formOptRadar.addCommand(getItemSaveOpt());
            formOptRadar.setCommandListener(this);//GEN-END:|248-getter|1|248-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|248-getter|2|
        return formOptRadar;
    }
//</editor-fold>//GEN-END:|248-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: textRadLogin ">//GEN-BEGIN:|252-getter|0|252-preInit
    /**
     * Returns an initiliazed instance of textRadLogin component.
     * @return the initialized component instance
     */
    public TextField getTextRadLogin() {
        if (textRadLogin==null){//GEN-END:|252-getter|0|252-preInit
            // Insert pre-init code here
            textRadLogin=new TextField("Login"+"\n", null, 32, TextField.ANY);//GEN-LINE:|252-getter|1|252-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|252-getter|2|
        return textRadLogin;
    }
//</editor-fold>//GEN-END:|252-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: textRadPass ">//GEN-BEGIN:|253-getter|0|253-preInit
    /**
     * Returns an initiliazed instance of textRadPass component.
     * @return the initialized component instance
     */
    public TextField getTextRadPass() {
        if (textRadPass==null){//GEN-END:|253-getter|0|253-preInit
            // Insert pre-init code here
            textRadPass=new TextField("Password"+"\n", null, 32, TextField.ANY|TextField.PASSWORD|TextField.NON_PREDICTIVE);//GEN-LINE:|253-getter|1|253-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|253-getter|2|
        return textRadPass;
    }
//</editor-fold>//GEN-END:|253-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceNetRadar ">//GEN-BEGIN:|254-getter|0|254-preInit
    /**
     * Returns an initiliazed instance of choiceNetRadar component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceNetRadar() {
        if (choiceNetRadar==null){//GEN-END:|254-getter|0|254-preInit
            // Insert pre-init code here
            choiceNetRadar=new ChoiceGroup(LangHolder.getString(Lang.options), Choice.MULTIPLE);//GEN-BEGIN:|254-getter|1|254-postInit
            choiceNetRadar.append(LangHolder.getString(Lang.connhold), null);
            choiceNetRadar.append(LangHolder.getString(Lang.realtimenr), null);
            choiceNetRadar.append("UDP", null);
            choiceNetRadar.append(LangHolder.getString(Lang.showtrack), null);
            // Insert post-init code here
        }//GEN-BEGIN:|254-getter|2|
        return choiceNetRadar;
    }
//</editor-fold>//GEN-END:|254-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceNRTrackPeriod ">//GEN-BEGIN:|258-getter|0|258-preInit
    /**
     * Returns an initiliazed instance of choiceNRTrackPeriod component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceNRTrackPeriod() {
        if (choiceNRTrackPeriod==null){//GEN-END:|258-getter|0|258-preInit
            // Insert pre-init code here
            choiceNRTrackPeriod=new ChoiceGroup(LangHolder.getString(Lang.trackperiod), Choice.POPUP);//GEN-BEGIN:|258-getter|1|258-postInit
            choiceNRTrackPeriod.append("1 "+LangHolder.getString(Lang.min), null);
            choiceNRTrackPeriod.append("10 "+LangHolder.getString(Lang.min), null);
            // Insert post-init code here
        }//GEN-BEGIN:|258-getter|2|
        return choiceNRTrackPeriod;
    }
//</editor-fold>//GEN-END:|258-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringRadarBytes ">//GEN-BEGIN:|261-getter|0|261-preInit
    /**
     * Returns an initiliazed instance of stringRadarBytes component.
     * @return the initialized component instance
     */
    public StringItem getStringRadarBytes() {
        if (stringRadarBytes==null){//GEN-END:|261-getter|0|261-preInit
            // Insert pre-init code here
            stringRadarBytes=new StringItem(LangHolder.getString(Lang.radarbytes)+"\n", "");//GEN-LINE:|261-getter|1|261-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|261-getter|2|
        return stringRadarBytes;
    }
//</editor-fold>//GEN-END:|261-getter|2|

    /**
     * Returns an initiliazed instance of choiceRadarSite component.
     * @return the initialized component instance
     */
    /*public ChoiceGroup getChoiceRadarSite () {
    if (choiceRadarSite == null) {//GEN-END:|262-getter|0|262-preInit
    // Insert pre-init code here
    choiceRadarSite = new ChoiceGroup (LangHolder.getString(Lang.advanceopt), Choice.POPUP);//GEN-BEGIN:|262-getter|1|262-postInit
    choiceRadarSite.append ("mapnav.spb.ru", null);
    choiceRadarSite.append ("netradar.ru", null);
    // Insert post-init code here
    }//GEN-BEGIN:|262-getter|2|
    return choiceRadarSite;
    }*/
//<editor-fold defaultstate="collapsed" desc=" Generated Getter: formOptCache ">//GEN-BEGIN:|267-getter|0|267-preInit
    /**
     * Returns an initiliazed instance of formOptCache component.
     * @return the initialized component instance
     */
    public Form getFormOptCache() {
        if (formOptCache==null){//GEN-END:|267-getter|0|267-preInit
            // Insert pre-init code here
            formOptCache=new Form(LangHolder.getString(Lang.cache), new Item[]{getGaugeCache(), getStringCacheSize(), getStringMapSize(), getChoiceCacheOpt(), getTextFileCachePath()});//GEN-BEGIN:|267-getter|1|267-postInit
            formOptCache.addCommand(getBack2Opt());
            formOptCache.addCommand(getHelpClearCache());
            formOptCache.addCommand(getItemSaveOpt());
            formOptCache.addCommand(getScreenReset());
            formOptCache.setCommandListener(this);//GEN-END:|267-getter|1|267-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|267-getter|2|
        return formOptCache;
    }
//</editor-fold>//GEN-END:|267-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: gaugeCache ">//GEN-BEGIN:|283-getter|0|283-preInit
    /**
     * Returns an initiliazed instance of gaugeCache component.
     * @return the initialized component instance
     */
    public Gauge getGaugeCache() {
        if (gaugeCache==null){//GEN-END:|283-getter|0|283-preInit
            // Insert pre-init code here
            gaugeCache=new Gauge(LangHolder.getString(Lang.imagebuf), true, 14, 0);//GEN-LINE:|283-getter|1|283-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|283-getter|2|
        return gaugeCache;
    }
//</editor-fold>//GEN-END:|283-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringCacheSize ">//GEN-BEGIN:|284-getter|0|284-preInit
    /**
     * Returns an initiliazed instance of stringCacheSize component.
     * @return the initialized component instance
     */
    public StringItem getStringCacheSize() {
        if (stringCacheSize==null){//GEN-END:|284-getter|0|284-preInit
            // Insert pre-init code here
            stringCacheSize=new StringItem(LangHolder.getString(Lang.imagecachesize)+"\n"//GEN-BEGIN:|284-getter|1|284-postInit
              , "");//GEN-END:|284-getter|1|284-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|284-getter|2|
        return stringCacheSize;
    }
//</editor-fold>//GEN-END:|284-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringMapSize ">//GEN-BEGIN:|285-getter|0|285-preInit
    /**
     * Returns an initiliazed instance of stringMapSize component.
     * @return the initialized component instance
     */
    public StringItem getStringMapSize() {
        if (stringMapSize==null){//GEN-END:|285-getter|0|285-preInit
            // Insert pre-init code here
            stringMapSize=new StringItem(LangHolder.getString(Lang.mapimagesize)+"\n"//GEN-BEGIN:|285-getter|1|285-postInit
              , "");//GEN-END:|285-getter|1|285-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|285-getter|2|
        return stringMapSize;
    }
//</editor-fold>//GEN-END:|285-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: formAskReset ">//GEN-BEGIN:|275-getter|0|275-preInit
    /**
     * Returns an initiliazed instance of formAskReset component.
     * @return the initialized component instance
     */
    public Form getFormAskReset() {
        if (formAskReset==null){//GEN-END:|275-getter|0|275-preInit
            // Insert pre-init code here
            formAskReset=new Form(LangHolder.getString(Lang.reset), new Item[]{getStringAskReset()});//GEN-BEGIN:|275-getter|1|275-postInit
            formAskReset.addCommand(getOkYes());
            formAskReset.addCommand(getCancelNo());
            formAskReset.setCommandListener(this);//GEN-END:|275-getter|1|275-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|275-getter|2|
        return formAskReset;
    }
//</editor-fold>//GEN-END:|275-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringAskReset ">//GEN-BEGIN:|281-getter|0|281-preInit
    /**
     * Returns an initiliazed instance of stringAskReset component.
     * @return the initialized component instance
     */
    public StringItem getStringAskReset() {
        if (stringAskReset==null){//GEN-END:|281-getter|0|281-preInit
            // Insert pre-init code here
            stringAskReset=new StringItem(LangHolder.getString(Lang.reset)+" ?", "");//GEN-LINE:|281-getter|1|281-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|281-getter|2|
        return stringAskReset;
    }
//</editor-fold>//GEN-END:|281-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: formOptDeb ">//GEN-BEGIN:|288-getter|0|288-preInit
    /**
     * Returns an initiliazed instance of formOptDeb component.
     * @return the initialized component instance
     */
    public Form getFormOptDeb() {
        if (formOptDeb==null){//GEN-END:|288-getter|0|288-preInit
            // Insert pre-init code here
            formOptDeb=new Form(LangHolder.getString(Lang.debug), new Item[]{getChoiceDebug(), getTextLogSavePath(), getStringLog()});//GEN-BEGIN:|288-getter|1|288-postInit
            formOptDeb.addCommand(getBack2Opt());
            formOptDeb.addCommand(getItemClear());
            formOptDeb.addCommand(getItemSaveLog());
            formOptDeb.addCommand(getItemSaveLogWeb());
            formOptDeb.addCommand(getItemSaveOpt());
            formOptDeb.setCommandListener(this);//GEN-END:|288-getter|1|288-postInit
            // Insert post-init code here
            if (!MNSInfo.filesAvailable()) {
                formOptDeb.removeCommand(getItemSaveLog());
            }

        }//GEN-BEGIN:|288-getter|2|
        return formOptDeb;
    }
//</editor-fold>//GEN-END:|288-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceDebug ">//GEN-BEGIN:|298-getter|0|298-preInit
    /**
     * Returns an initiliazed instance of choiceDebug component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceDebug() {
        if (choiceDebug==null){//GEN-END:|298-getter|0|298-preInit
            // Insert pre-init code here
            choiceDebug=new ChoiceGroup(LangHolder.getString(Lang.debugopt), Choice.MULTIPLE);//GEN-BEGIN:|298-getter|1|298-postInit
            choiceDebug.append(LangHolder.getString(Lang.enable), null);
            choiceDebug.append(LangHolder.getString(Lang.mapcorrect), null);
            choiceDebug.append(LangHolder.getString(Lang.savemapcorrect), null);
            choiceDebug.append(LangHolder.getString(Lang.mapcorrectall), null);
            choiceDebug.append(LangHolder.getString(Lang.savenmea), null);
            choiceDebug.append(LangHolder.getString(Lang.tellnewdebugv), null);
            // Insert post-init code here
        }//GEN-BEGIN:|298-getter|2|
        return choiceDebug;
    }
//</editor-fold>//GEN-END:|298-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: textLogSavePath ">//GEN-BEGIN:|305-getter|0|305-preInit
    /**
     * Returns an initiliazed instance of textLogSavePath component.
     * @return the initialized component instance
     */
    public TextField getTextLogSavePath() {
        if (textLogSavePath==null){//GEN-END:|305-getter|0|305-preInit
            // Insert pre-init code here
            textLogSavePath=new TextField(LangHolder.getString(Lang.sendaddrfile)+"\n", null, 128, TextField.ANY);//GEN-BEGIN:|305-getter|1|305-postInit
            textLogSavePath.addCommand(getItemBrowseMap());
            textLogSavePath.setItemCommandListener(this);//GEN-END:|305-getter|1|305-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|305-getter|2|
        return textLogSavePath;
    }
//</editor-fold>//GEN-END:|305-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringLog ">//GEN-BEGIN:|308-getter|0|308-preInit
    /**
     * Returns an initiliazed instance of stringLog component.
     * @return the initialized component instance
     */
    public StringItem getStringLog() {
        if (stringLog==null){//GEN-END:|308-getter|0|308-preInit
            // Insert pre-init code here
            stringLog=new StringItem(LangHolder.getString(Lang.log)+"\n", "");//GEN-LINE:|308-getter|1|308-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|308-getter|2|
        return stringLog;
    }
//</editor-fold>//GEN-END:|308-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: listRoute ">//GEN-BEGIN:|332-getter|0|332-preInit
    /**
     * Returns an initiliazed instance of listRoute component.
     * @return the initialized component instance
     */
    public List getListRoute() {
        if (listRoute==null){//GEN-END:|332-getter|0|332-preInit
            // Insert pre-init code here
            listRoute=new List(LangHolder.getString(Lang.routes), Choice.IMPLICIT);//GEN-BEGIN:|332-getter|1|332-postInit
            listRoute.append("LE", null);
            listRoute.addCommand(getCancel2Map());
            listRoute.addCommand(getItemSelect1());
            listRoute.addCommand(getItemCreateRoute());
            listRoute.setCommandListener(this);
            listRoute.setSelectCommand(getItemSelect1());
            listRoute.setSelectedFlags(new boolean[]{false});//GEN-END:|332-getter|1|332-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|332-getter|2|
        return listRoute;
    }
//</editor-fold>//GEN-END:|332-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Method: listRouteAction ">//GEN-BEGIN:|332-action|0|332-preAction
    /**
     * Performs an action assigned to the selected list element in the listRoute component.
     */
    public void listRouteAction() {//GEN-END:|332-action|0|332-preAction
        // enter pre-action user code here
//      if ((listRoute.getSelectedIndex()!=listRoute.size()-1)&&(listRoute.getSelectedIndex()!=0))
//      {     getListRouteMenu().setTitle(listRoute.getString(listRoute.getSelectedIndex()));
//             MapCanvas.setCurrent(getListRouteMenu());   
//           
//      }
//          //else selectRoute(MapRoute.TRACKKIND);
//          else selectRoute(MapRoute.ROUTEKIND);
        switch (getListRoute().getSelectedIndex()) {//GEN-BEGIN:|332-action|1|339-preAction
            case 0://GEN-END:|332-action|1|339-preAction
                // write pre-action user code here
//GEN-LINE:|332-action|2|339-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|332-action|3|332-postAction
        }//GEN-END:|332-action|3|332-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|332-action|4|
//</editor-fold>//GEN-END:|332-action|4|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: formRouteURL ">//GEN-BEGIN:|341-getter|0|341-preInit
    /**
     * Returns an initiliazed instance of formRouteURL component.
     * @return the initialized component instance
     */
    public Form getFormRouteURL() {
        if (formRouteURL==null){//GEN-END:|341-getter|0|341-preInit
            // Insert pre-init code here
            formRouteURL=new Form(LangHolder.getString(Lang.load), new Item[]{getTextRouteURL(), getChoiceRouteURL(), getChoiceCP(), getChoiceImpFormat()});//GEN-BEGIN:|341-getter|1|341-postInit
            formRouteURL.addCommand(getItemLoadMap());
            formRouteURL.addCommand(getBack2Opt());
            formRouteURL.addCommand(getItemBrowseMap());
            formRouteURL.setCommandListener(this);//GEN-END:|341-getter|1|341-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|341-getter|2|
        return formRouteURL;
    }
//</editor-fold>//GEN-END:|341-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: textRouteURL ">//GEN-BEGIN:|346-getter|0|346-preInit
    /**
     * Returns an initiliazed instance of textRouteURL component.
     * @return the initialized component instance
     */
    public TextField getTextRouteURL() {
        if (textRouteURL==null){//GEN-END:|346-getter|0|346-preInit
            // Insert pre-init code here
            textRouteURL=new TextField(LangHolder.getString(Lang.urlfile), null, 128, TextField.ANY);//GEN-BEGIN:|346-getter|1|346-postInit
            textRouteURL.setInitialInputMode("");//GEN-END:|346-getter|1|346-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|346-getter|2|
        return textRouteURL;
    }
//</editor-fold>//GEN-END:|346-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceRouteURL ">//GEN-BEGIN:|347-getter|0|347-preInit
    /**
     * Returns an initiliazed instance of choiceRouteURL component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceRouteURL() {
        if (choiceRouteURL==null){//GEN-END:|347-getter|0|347-preInit
            // Insert pre-init code here
            choiceRouteURL=new ChoiceGroup(LangHolder.getString(Lang.loadfrom), Choice.POPUP);//GEN-BEGIN:|347-getter|1|347-postInit
            choiceRouteURL.append(LangHolder.getString(Lang.internet), null);
            choiceRouteURL.append(LangHolder.getString(Lang.file), null);
            choiceRouteURL.setSelectedIndex(1, true);
            // Insert post-init code here
        }//GEN-BEGIN:|347-getter|2|
        return choiceRouteURL;
    }
//</editor-fold>//GEN-END:|347-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceCP ">//GEN-BEGIN:|350-getter|0|350-preInit
    /**
     * Returns an initiliazed instance of choiceCP component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceCP() {
        if (choiceCP==null){//GEN-END:|350-getter|0|350-preInit
            // Insert pre-init code here
            choiceCP=new ChoiceGroup(LangHolder.getString(Lang.codepage), Choice.POPUP);//GEN-BEGIN:|350-getter|1|350-postInit
            choiceCP.append("UTF-8", null);
            choiceCP.append("Win1251", null);
            // Insert post-init code here
        }//GEN-BEGIN:|350-getter|2|
        return choiceCP;
    }
//</editor-fold>//GEN-END:|350-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceImpFormat ">//GEN-BEGIN:|353-getter|0|353-preInit
    /**
     * Returns an initiliazed instance of choiceImpFormat component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceImpFormat() {
        if (choiceImpFormat==null){//GEN-END:|353-getter|0|353-preInit
            // Insert pre-init code here
            choiceImpFormat=new ChoiceGroup("Import Format", Choice.POPUP);//GEN-BEGIN:|353-getter|1|353-postInit
            choiceImpFormat.append("Ozi", null);
            choiceImpFormat.append("GPX", null);
            choiceImpFormat.append("LOC", null);
            // Insert post-init code here
        }//GEN-BEGIN:|353-getter|2|
        return choiceImpFormat;
    }
//</editor-fold>//GEN-END:|353-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: listMore ">//GEN-BEGIN:|359-getter|0|359-preInit
    /**
     * Returns an initiliazed instance of listMore component.
     * @return the initialized component instance
     */
    public List getListMore() {
        if (listMore==null){//GEN-END:|359-getter|0|359-preInit
            // Insert pre-init code here
            listMore=new List(LangHolder.getString(Lang.info), Choice.IMPLICIT);//GEN-BEGIN:|359-getter|1|359-postInit
            listMore.append("KML (WikiMapia)", null);
            listMore.append(LangHolder.getString(Lang.mypoi), null);
            listMore.append(LangHolder.getString(Lang.getgeocache), null);
            listMore.append(LangHolder.getString(Lang.search), null);
            listMore.append(LangHolder.getString(Lang.search)+':'+LangHolder.getString(Lang.route), null);
            listMore.append(LangHolder.getString(Lang.intmaps), null);
            listMore.append(LangHolder.getString(Lang.extmaps), null);
            listMore.append(LangHolder.getString(Lang.takescreen), null);
            listMore.append(LangHolder.getString(Lang.sendcsms), null);
            listMore.append(LangHolder.getString(Lang.precpos), null);
            listMore.append("GT Mode", null);
            listMore.append("Flickr", null);
            listMore.append("Sport Mode", null);
            listMore.append("Service", null);
            listMore.append(LangHolder.getString(Lang.recfriend), null);
            listMore.append(LangHolder.getString(Lang.help), null);
            listMore.append(LangHolder.getString(Lang.about), null);
            listMore.append(LangHolder.getString(Lang.donate), null);
            listMore.append("TR-102 Mode", null);
            listMore.addCommand(getCancel2Map());
            listMore.setCommandListener(this);
//listMore.setSelectedFlags (new boolean[] { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false });//GEN-END:|359-getter|1|359-postInit
            // Insert post-init code here

        }//GEN-BEGIN:|359-getter|2|
        return listMore;
    }
//</editor-fold>//GEN-END:|359-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Method: listMoreAction ">//GEN-BEGIN:|359-action|0|359-preAction
    /**
     * Performs an action assigned to the selected list element in the listMore component.
     */
    public void listMoreAction() {//GEN-END:|359-action|0|359-preAction
        // enter pre-action user code here
        switch (getListMore().getSelectedIndex()) {//GEN-BEGIN:|359-action|1|363-preAction
            case 0://GEN-END:|359-action|1|363-preAction
                // write pre-action user code here
                showKMLList();
//GEN-LINE:|359-action|2|363-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|359-action|3|364-preAction
            case 1://GEN-END:|359-action|3|364-preAction
                // write pre-action user code here
                GPSClubLoad.getGPSClub();
//GEN-LINE:|359-action|4|364-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|359-action|5|419-preAction
            case 2://GEN-END:|359-action|5|419-preAction
                // write pre-action user code here
                getGeoCache();
//GEN-LINE:|359-action|6|419-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|359-action|7|365-preAction
            case 3://GEN-END:|359-action|7|365-preAction
                // write pre-action user code here
                switchDisplayable(null, getFormSearch());//GEN-LINE:|359-action|8|365-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|359-action|9|383-preAction
            case 4://GEN-END:|359-action|9|383-preAction
                // write pre-action user code here
                if ((MapCanvas.map.gpsActive()&&(MapCanvas.gpsBinded))&&((MapCanvas.map.activeRoute==null)||(MapCanvas.map.activeRoute.pt2Nav()==null))){
                    MapCanvas.showmsgmodal(LangHolder.getString(Lang.info), LangHolder.getString(Lang.route)+','+LangHolder.getString(Lang.waypoints)+" - "+LangHolder.getString(Lang.nosel)+'!', AlertType.WARNING, listMore);
                } else {
                    MapPoint mpFrom, mpTo;

                    if (MapCanvas.map.activeRoute!=null){
                        if (MapCanvas.map.gpsActive()&&MapCanvas.gpsBinded) {
                            mpFrom=new MapPoint(GPSReader.LATITUDE, GPSReader.LONGITUDE, 0, null);
                        } else {
                            mpFrom=new MapPoint(MapCanvas.reallat, MapCanvas.reallon, 0, null);
                        }
                        mpTo = MapCanvas.map.activeRoute.pt2Nav();
                       
                    } else {
                        mpFrom=new MapPoint(GPSReader.LATITUDE, GPSReader.LONGITUDE, 0, null);
                        mpTo=new MapPoint(MapCanvas.reallat, MapCanvas.reallon, 0, null);
                    }
                    ProgressStoppable ps;
                    if(RMSOption.routeSearchType==RMSOption.ROUTE_SEARCH_CLOUD)
                        ps = new CloudRouteResult(mpFrom, mpTo);
                    else ps= new KMLSearchResult(mpFrom, mpTo);
                    //ProgressStoppable ps = new KMLSearchResult(mpFrom, mpTo);
                     MapCanvas.setCurrent(new ProgressForm(LangHolder.getString(Lang.search),
                          LangHolder.getString(Lang.route), ps, listMore));
                }
//GEN-LINE:|359-action|10|383-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|359-action|11|384-preAction
            case 5://GEN-END:|359-action|11|384-preAction
                // write pre-action user code here
                showListIMaps();
//GEN-LINE:|359-action|12|384-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|359-action|13|385-preAction
            case 6://GEN-END:|359-action|13|385-preAction
                // write pre-action user code here
                showListOMaps();
//GEN-LINE:|359-action|14|385-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|359-action|15|386-preAction
            case 7://GEN-END:|359-action|15|386-preAction
                // write pre-action user code here
                getFormSendMMS();
                getTextAddrMMS().setString(RMSOption.lastSendMMSURL);
                getTextAddrFile().setString(RMSOption.lastSendFileURL);
                choicePicSize.setSelectedIndex(RMSOption.choicePicSize, true);
                choiceSendType.setSelectedIndex(RMSOption.choiceSendType, true);
                choiceSerInterval.setSelectedIndex(RMSOption.choiceSerInterval, true);
                choiceSerCount.setSelectedIndex(RMSOption.choiceSerCount, true);
                choiceImageType.setSelectedIndex(RMSOption.imageType, true);

                switchDisplayable(null, getFormSendMMS());//GEN-LINE:|359-action|16|386-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|359-action|17|418-preAction
            case 8://GEN-END:|359-action|17|418-preAction
                // write pre-action user code here
                showSMSSendForm();

//GEN-LINE:|359-action|18|418-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|359-action|19|420-preAction
            case 9://GEN-END:|359-action|19|420-preAction
                // write pre-action user code here
                MapCanvas.setCurrent(new LocationCanvas());
//GEN-LINE:|359-action|20|420-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|359-action|21|421-preAction
            case 10://GEN-END:|359-action|21|421-preAction
                // write pre-action user code here
                if (!MapCanvas.map.gpsActive()){
                    MapCanvas.showmsg(LangHolder.getString(Lang.info), "GPS is not connected", AlertType.WARNING, listMore);
                    return;
                }
                MapCanvas.setCurrent(new GTCanvas());


                break;//GEN-BEGIN:|359-action|23|426-preAction
            case 11://GEN-END:|359-action|23|426-preAction
                // write pre-action user code here
                showFlickr();
                break;//GEN-BEGIN:|359-action|25|436-preAction
            case 12://GEN-END:|359-action|21|421-preAction
                // write pre-action user code here
                if (!MapCanvas.map.gpsActive()){
                    MapCanvas.showmsg(LangHolder.getString(Lang.info), "GPS is not connected", AlertType.WARNING, listMore);
                    return;
                }
                if ((MapCanvas.map.activeRoute==null)||(MapCanvas.map.activeRoute.pts.size()!=2)){
                    MapCanvas.showmsgmodal(LangHolder.getString(Lang.info), "No correct start line.\n Create and open 2 waypoints to use them as start/finish line. Specify them to be at least 20 meters wider then actual line with respect to GPS data accuracy.", AlertType.WARNING, listMore);
                    return;
                }
                MapCanvas.setCurrent(new SportCanvas(MapCanvas.map.activeRoute));


                break;//GEN-BEGIN:|359-action|23|426-preAction
            case 13://GEN-END:|359-action|25|436-preAction
                // write pre-action user code here
                switchDisplayable(null, getFormService());//GEN-LINE:|359-action|30|445-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|359-action|27|444-preAction
            case 14://GEN-END:|359-action|27|444-preAction
                // write pre-action user code here
                getFormRecFr();
                textYourName.setString(RMSOption.getStringOpt(RMSOption.SO_YOURNAME));
                switchDisplayable(null, getFormRecFr());//GEN-LINE:|359-action|26|436-postAction
//GEN-LINE:|359-action|28|444-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|359-action|29|445-preAction
            case 15://GEN-END:|359-action|29|445-preAction
                // write pre-action user code here


                switchDisplayable(null, getFormHelp());//GEN-LINE:|359-action|22|421-postAction

                // write post-action user code here
                break;//GEN-BEGIN:|359-action|31|464-preAction
            case 16:
                // write pre-action user code here
                getFormAbout();
                Runtime.getRuntime().gc();
                stringFreeMem.setText(String.valueOf(Runtime.getRuntime().freeMemory()));

                switchDisplayable(null, getFormAbout());//GEN-LINE:|359-action|24|426-postAction
                // write post-action user code here
                new Thread(new LastVersion(stringItemLastVersion, stringItemLastDebug)).start();

                // write post-action user code here
                break;

            case 17:
                byte[] dm={89, 97, 110, 100, 101, 120, 32, 109, 111, 110, 101, 121, 58, 10, 32, 52, 49, 48, 48, 49, 49, 51, 50, 56, 50, 48, 55, 49, 56, 10, 10, 32, 87, 101, 98, 77, 111, 110, 101, 121, 58, 10, 32, 82, 53, 52, 50, 57, 56, 52, 49, 54, 48, 51, 50, 56, 10, 32, 90, 50, 55, 50, 54, 50, 48, 51, 48, 51, 54, 57, 49, 10};
                //int sum = ;
                if (MapUtil.checkSum(dm)==4438){
                    //String ss2=  "Yandex money:\n 41001132820718\n\n WebMoney:\n R542984160328\n Z272620303691\n";
                    String ss2=misc.Util.byteArrayToString(dm, true);
                    //byte[] d = ss2.getBytes();
                    MapCanvas.showmsgmodal(LangHolder.getString(Lang.donate), ss2, AlertType.INFO, listMore);
                    // d[0]=d[0];
                }

                break;
            case 18://GEN-END:|359-action|21|421-preAction
                // write pre-action user code here
                if (!MapCanvas.map.gpsActive()){
                    MapCanvas.showmsg(LangHolder.getString(Lang.info), "GPS is not connected", AlertType.WARNING, listMore);
                    return;
                }
                TR102Sender.showTR102Sender();

        }//GEN-END:|359-action|33|359-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|359-action|34|
//</editor-fold>//GEN-END:|359-action|34|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: formSearch ">//GEN-BEGIN:|366-getter|0|366-preInit
    /**
     * Returns an initiliazed instance of formSearch component.
     * @return the initialized component instance
     */
    public Form getFormSearch() {
        if (formSearch==null){//GEN-END:|366-getter|0|366-preInit
            // Insert pre-init code here
            formSearch=new Form(LangHolder.getString(Lang.search), new Item[]{getTextSearch()});//GEN-BEGIN:|366-getter|1|366-postInit
            formSearch.addCommand(getBack2Opt());
            formSearch.addCommand(getItemSearch());
            formSearch.addCommand(getItemResults());
            formSearch.setCommandListener(this);//GEN-END:|366-getter|1|366-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|366-getter|2|
        return formSearch;
    }
//</editor-fold>//GEN-END:|366-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: textSearch ">//GEN-BEGIN:|381-getter|0|381-preInit
    /**
     * Returns an initiliazed instance of textSearch component.
     * @return the initialized component instance
     */
    public TextField getTextSearch() {
        if (textSearch==null){//GEN-END:|381-getter|0|381-preInit
            // Insert pre-init code here
            textSearch=new TextField(LangHolder.getString(Lang.place), null, 128, TextField.ANY);//GEN-LINE:|381-getter|1|381-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|381-getter|2|
        return textSearch;
    }
//</editor-fold>//GEN-END:|381-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: listSearch ">//GEN-BEGIN:|373-getter|0|373-preInit
    /**
     * Returns an initiliazed instance of listSearch component.
     * @return the initialized component instance
     */
    public List getListSearch() {
        if (listSearch==null){//GEN-END:|373-getter|0|373-preInit
            // Insert pre-init code here
            listSearch=new List(LangHolder.getString(Lang.search), Choice.IMPLICIT);//GEN-BEGIN:|373-getter|1|373-postInit
            listSearch.append("No search perfomed yet", null);
            listSearch.addCommand(getBack2Opt());
            listSearch.addCommand(getItemGoto1());
            listSearch.setCommandListener(this);
            listSearch.setSelectCommand(getItemGoto1());
            listSearch.setSelectedFlags(new boolean[]{false});//GEN-END:|373-getter|1|373-postInit
            // Insert post-init code here
            if (KMLSearchResult.kmlPoints!=null){
                for (int i=0; i<KMLSearchResult.kmlPoints.size(); i++) {
                    KMLMapPoint mp=(KMLMapPoint) KMLSearchResult.kmlPoints.elementAt(i);
                    listSearch.append(mp.name, null);
                }
            }

        }//GEN-BEGIN:|373-getter|2|
        return listSearch;
    }
//</editor-fold>//GEN-END:|373-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Method: listSearchAction ">//GEN-BEGIN:|373-action|0|373-preAction
    /**
     * Performs an action assigned to the selected list element in the listSearch component.
     */
    public void listSearchAction() {//GEN-END:|373-action|0|373-preAction
        // enter pre-action user code here
        switch (getListSearch().getSelectedIndex()) {//GEN-BEGIN:|373-action|1|379-preAction
            case 0://GEN-END:|373-action|1|379-preAction
                // write pre-action user code here
//GEN-LINE:|373-action|2|379-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|373-action|3|373-postAction
        }//GEN-END:|373-action|3|373-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|373-action|4|
//</editor-fold>//GEN-END:|373-action|4|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: formSendMMS ">//GEN-BEGIN:|387-getter|0|387-preInit
    /**
     * Returns an initiliazed instance of formSendMMS component.
     * @return the initialized component instance
     */
    public Form getFormSendMMS() {
        if (formSendMMS==null){//GEN-END:|387-getter|0|387-preInit
            // Insert pre-init code here
            formSendMMS=new Form(LangHolder.getString(Lang.takescreen), new Item[]{getChoicePicSize(), getChoiceSendType(), getChoiceImageType(), getTextAddrMMS(), getTextAddrFile(), getChoiceSerInterval(), getChoiceSerCount()});//GEN-BEGIN:|387-getter|1|387-postInit
            formSendMMS.addCommand(getBack2Opt());
            formSendMMS.addCommand(getItem1Send());
            formSendMMS.addCommand(getItemBrowseMap());
            formSendMMS.setCommandListener(this);//GEN-END:|387-getter|1|387-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|387-getter|2|
        return formSendMMS;
    }
//</editor-fold>//GEN-END:|387-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choicePicSize ">//GEN-BEGIN:|392-getter|0|392-preInit
    /**
     * Returns an initiliazed instance of choicePicSize component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoicePicSize() {
        if (choicePicSize==null){//GEN-END:|392-getter|0|392-preInit
            // Insert pre-init code here
            choicePicSize=new ChoiceGroup(LangHolder.getString(Lang.picsize), Choice.POPUP);//GEN-BEGIN:|392-getter|1|392-postInit
            choicePicSize.append("120x120", null);
            choicePicSize.append("128x160", null);
            choicePicSize.append("176x220", null);
            choicePicSize.append(LangHolder.getString(Lang.realsize), null);
//choicePicSize.setSelectedFlags (new boolean[] { false, false, false, true });
//choicePicSize.setFont (0, null);
//choicePicSize.setFont (1, null);
//choicePicSize.setFont (2, null);
//choicePicSize.setFont (3, null);//GEN-END:|392-getter|1|392-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|392-getter|2|
        return choicePicSize;
    }
//</editor-fold>//GEN-END:|392-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceSendType ">//GEN-BEGIN:|397-getter|0|397-preInit
    /**
     * Returns an initiliazed instance of choiceSendType component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceSendType() {
        if (choiceSendType==null){//GEN-END:|397-getter|0|397-preInit
            // Insert pre-init code here
            choiceSendType=new ChoiceGroup(LangHolder.getString(Lang.action), Choice.POPUP);//GEN-BEGIN:|397-getter|1|397-postInit
            choiceSendType.append("MMS", null);
            choiceSendType.append(LangHolder.getString(Lang.file), null);
            choiceSendType.append(LangHolder.getString(Lang.file)+'+'+LangHolder.getString(Lang.interval), null);
//choiceSendType.setSelectedFlags (new boolean[] { false, true, false });
//choiceSendType.setFont (0, null);
//choiceSendType.setFont (1, null);
//choiceSendType.setFont (2, null);//GEN-END:|397-getter|1|397-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|397-getter|2|
        return choiceSendType;
    }
//</editor-fold>//GEN-END:|397-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceImageType ">//GEN-BEGIN:|401-getter|0|401-preInit
    /**
     * Returns an initiliazed instance of choiceImageType component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceImageType() {
        if (choiceImageType==null){//GEN-END:|401-getter|0|401-preInit
            // Insert pre-init code here
            choiceImageType=new ChoiceGroup(LangHolder.getString(Lang.format), Choice.POPUP);//GEN-BEGIN:|401-getter|1|401-postInit
            choiceImageType.append("GIF", null);
            choiceImageType.append("BMP", null);
//choiceImageType.setSelectedFlags (new boolean[] { false, false });
//choiceImageType.setFont (0, null);
//choiceImageType.setFont (1, null);//GEN-END:|401-getter|1|401-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|401-getter|2|
        return choiceImageType;
    }
//</editor-fold>//GEN-END:|401-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: textAddrMMS ">//GEN-BEGIN:|404-getter|0|404-preInit
    /**
     * Returns an initiliazed instance of textAddrMMS component.
     * @return the initialized component instance
     */
    public TextField getTextAddrMMS() {
        if (textAddrMMS==null){//GEN-END:|404-getter|0|404-preInit
            // Insert pre-init code here
            textAddrMMS=new TextField(LangHolder.getString(Lang.sendaddrmms), null, 128, TextField.ANY);//GEN-BEGIN:|404-getter|1|404-postInit
            textAddrMMS.setInitialInputMode("");//GEN-END:|404-getter|1|404-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|404-getter|2|
        return textAddrMMS;
    }
//</editor-fold>//GEN-END:|404-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: textAddrFile ">//GEN-BEGIN:|405-getter|0|405-preInit
    /**
     * Returns an initiliazed instance of textAddrFile component.
     * @return the initialized component instance
     */
    public TextField getTextAddrFile() {
        if (textAddrFile==null){//GEN-END:|405-getter|0|405-preInit
            // Insert pre-init code here
            textAddrFile=new TextField(LangHolder.getString(Lang.sendaddrfile), null, 128, TextField.ANY);//GEN-LINE:|405-getter|1|405-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|405-getter|2|
        return textAddrFile;
    }
//</editor-fold>//GEN-END:|405-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceSerInterval ">//GEN-BEGIN:|406-getter|0|406-preInit
    /**
     * Returns an initiliazed instance of choiceSerInterval component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceSerInterval() {
        if (choiceSerInterval==null){//GEN-END:|406-getter|0|406-preInit
            // Insert pre-init code here
            choiceSerInterval=new ChoiceGroup(LangHolder.getString(Lang.interval), Choice.POPUP);//GEN-BEGIN:|406-getter|1|406-postInit
            choiceSerInterval.append("2 "+LangHolder.getString(Lang.sec), null);
            choiceSerInterval.append("5 "+LangHolder.getString(Lang.sec), null);
            choiceSerInterval.append("10 "+LangHolder.getString(Lang.sec), null);
            choiceSerInterval.append("20 "+LangHolder.getString(Lang.sec), null);
            choiceSerInterval.append("30 "+LangHolder.getString(Lang.sec), null);
//choiceSerInterval.setSelectedFlags (new boolean[] { false, true, false, false, false });
//choiceSerInterval.setFont (0, null);
//choiceSerInterval.setFont (1, null);
//choiceSerInterval.setFont (2, null);
//choiceSerInterval.setFont (3, null);
//choiceSerInterval.setFont (4, null);//GEN-END:|406-getter|1|406-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|406-getter|2|
        return choiceSerInterval;
    }
//</editor-fold>//GEN-END:|406-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceSerCount ">//GEN-BEGIN:|412-getter|0|412-preInit
    /**
     * Returns an initiliazed instance of choiceSerCount component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceSerCount() {
        if (choiceSerCount==null){//GEN-END:|412-getter|0|412-preInit
            // Insert pre-init code here
            choiceSerCount=new ChoiceGroup(LangHolder.getString(Lang.count), Choice.POPUP);//GEN-BEGIN:|412-getter|1|412-postInit
            choiceSerCount.append("5", null);
            choiceSerCount.append("10", null);
            choiceSerCount.append("20", null);
            choiceSerCount.append("30", null);
//choiceSerCount.setSelectedFlags (new boolean[] { true, false, false, false });
//choiceSerCount.setFont (0, null);
//choiceSerCount.setFont (1, null);
//choiceSerCount.setFont (2, null);
//choiceSerCount.setFont (3, null);//GEN-END:|412-getter|1|412-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|412-getter|2|
        return choiceSerCount;
    }
//</editor-fold>//GEN-END:|412-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: formHelp ">//GEN-BEGIN:|422-getter|0|422-preInit
    /**
     * Returns an initiliazed instance of formHelp component.
     * @return the initialized component instance
     */
    public Form getFormHelp() {
        if (formHelp==null){//GEN-END:|422-getter|0|422-preInit
            // Insert pre-init code here
            formHelp=new Form(LangHolder.getString(Lang.help), new Item[]{getStringItem2()});//GEN-BEGIN:|422-getter|1|422-postInit
            formHelp.addCommand(getBack2Opt());
            formHelp.setCommandListener(this);//GEN-END:|422-getter|1|422-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|422-getter|2|
        return formHelp;
    }
//</editor-fold>//GEN-END:|422-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringItem2 ">//GEN-BEGIN:|424-getter|0|424-preInit
    /**
     * Returns an initiliazed instance of stringItem2 component.
     * @return the initialized component instance
     */
    public StringItem getStringItem2() {
        if (stringItem2==null){//GEN-END:|424-getter|0|424-preInit
            // Insert pre-init code here
            stringItem2=new StringItem(LangHolder.getString(Lang.helplabel), LangHolder.getString(Lang.help_info));//GEN-LINE:|424-getter|1|424-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|424-getter|2|
        return stringItem2;
    }
//</editor-fold>//GEN-END:|424-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: formAbout ">//GEN-BEGIN:|427-getter|0|427-preInit
    /**
     * Returns an initiliazed instance of formAbout component.
     * @return the initialized component instance
     */
    public Form getFormAbout() {
        if (formAbout==null){//GEN-END:|427-getter|0|427-preInit
            // Insert pre-init code here
            formAbout=new Form(LangHolder.getString(Lang.about), new Item[]{getStringItem1(), getStringItemLastVersion(), getStringItemLastDebug(), getStringURLVer(), getStringFreeMem()});//GEN-BEGIN:|427-getter|1|427-postInit
            formAbout.addCommand(getBack2Opt());
            formAbout.setCommandListener(this);//GEN-END:|427-getter|1|427-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|427-getter|2|
        return formAbout;
    }
//</editor-fold>//GEN-END:|427-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringItem1 ">//GEN-BEGIN:|429-getter|0|429-preInit
    /**
     * Returns an initiliazed instance of stringItem1 component.
     * @return the initialized component instance
     */
    public StringItem getStringItem1() {
        if (stringItem1==null){//GEN-END:|429-getter|0|429-preInit
            // Insert pre-init code here
            stringItem1=new StringItem("", LangHolder.getString(Lang.about_info)+//GEN-BEGIN:|429-getter|1|429-postInit
              "\nVendor: "+mM.getAppProperty("MIDlet-Vendor")+
              "\nSite: "+mM.getAppProperty("MIDlet-Info-URL")+
              "\nVersion: "+mM.getAppProperty("MIDlet-Version")+"\n");//GEN-END:|429-getter|1|429-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|429-getter|2|
        return stringItem1;
    }
//</editor-fold>//GEN-END:|429-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringItemLastVersion ">//GEN-BEGIN:|430-getter|0|430-preInit
    /**
     * Returns an initiliazed instance of stringItemLastVersion component.
     * @return the initialized component instance
     */
    public StringItem getStringItemLastVersion() {
        if (stringItemLastVersion==null){//GEN-END:|430-getter|0|430-preInit
            // Insert pre-init code here
            stringItemLastVersion=new StringItem(LangHolder.getString(Lang.lastver)+"\n", "loading...");//GEN-LINE:|430-getter|1|430-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|430-getter|2|
        return stringItemLastVersion;
    }
//</editor-fold>//GEN-END:|430-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringItemLastDebug ">//GEN-BEGIN:|431-getter|0|431-preInit
    /**
     * Returns an initiliazed instance of stringItemLastDebug component.
     * @return the initialized component instance
     */
    public StringItem getStringItemLastDebug() {
        if (stringItemLastDebug==null){//GEN-END:|431-getter|0|431-preInit
            // Insert pre-init code here
            stringItemLastDebug=new StringItem(LangHolder.getString(Lang.lastdeb)+"\n", "loading...");//GEN-LINE:|431-getter|1|431-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|431-getter|2|
        return stringItemLastDebug;
    }
//</editor-fold>//GEN-END:|431-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringURLVer ">//GEN-BEGIN:|432-getter|0|432-preInit
    /**
     * Returns an initiliazed instance of stringURLVer component.
     * @return the initialized component instance
     */
    public StringItem getStringURLVer() {
        if (stringURLVer==null){//GEN-END:|432-getter|0|432-preInit
            // Insert pre-init code here
            stringURLVer=new StringItem("", "http://mapnav.spb.ru/wap.wml", Item.HYPERLINK);//GEN-BEGIN:|432-getter|1|432-postInit
            stringURLVer.addCommand(getItemGoto1());
            stringURLVer.setItemCommandListener(this);//GEN-END:|432-getter|1|432-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|432-getter|2|
        return stringURLVer;
    }
//</editor-fold>//GEN-END:|432-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringFreeMem ">//GEN-BEGIN:|434-getter|0|434-preInit
    /**
     * Returns an initiliazed instance of stringFreeMem component.
     * @return the initialized component instance
     */
    public StringItem getStringFreeMem() {
        if (stringFreeMem==null){//GEN-END:|434-getter|0|434-preInit
            // Insert pre-init code here
            stringFreeMem=new StringItem(LangHolder.getString(Lang.freemem)+"\n", "");//GEN-LINE:|434-getter|1|434-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|434-getter|2|
        return stringFreeMem;
    }
//</editor-fold>//GEN-END:|434-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: formRecFr ">//GEN-BEGIN:|437-getter|0|437-preInit
    /**
     * Returns an initiliazed instance of formRecFr component.
     * @return the initialized component instance
     */
    public Form getFormRecFr() {
        if (formRecFr==null){//GEN-END:|437-getter|0|437-preInit
            // Insert pre-init code here
            formRecFr=new Form(LangHolder.getString(Lang.recfriend), new Item[]{getTextYourName(), getTextFrPhone()});//GEN-BEGIN:|437-getter|1|437-postInit
            formRecFr.addCommand(getItem1Send());
            formRecFr.addCommand(getBack2Opt());
            formRecFr.setCommandListener(this);//GEN-END:|437-getter|1|437-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|437-getter|2|
        return formRecFr;
    }
//</editor-fold>//GEN-END:|437-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: textYourName ">//GEN-BEGIN:|441-getter|0|441-preInit
    /**
     * Returns an initiliazed instance of textYourName component.
     * @return the initialized component instance
     */
    public TextField getTextYourName() {
        if (textYourName==null){//GEN-END:|441-getter|0|441-preInit
            // Insert pre-init code here
            textYourName=new TextField(LangHolder.getString(Lang.yourname), null, 32, TextField.ANY);//GEN-LINE:|441-getter|1|441-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|441-getter|2|
        return textYourName;
    }
//</editor-fold>//GEN-END:|441-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: textFrPhone ">//GEN-BEGIN:|442-getter|0|442-preInit
    /**
     * Returns an initiliazed instance of textFrPhone component.
     * @return the initialized component instance
     */
    public TextField getTextFrPhone() {
        if (textFrPhone==null){//GEN-END:|442-getter|0|442-preInit
            // Insert pre-init code here
            textFrPhone=new TextField(LangHolder.getString(Lang.entfrtel), null, 32, TextField.ANY);//GEN-LINE:|442-getter|1|442-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|442-getter|2|
        return textFrPhone;
    }
//</editor-fold>//GEN-END:|442-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: formService ">//GEN-BEGIN:|446-getter|0|446-preInit
    /**
     * Returns an initiliazed instance of formService component.
     * @return the initialized component instance
     */
    public Form getFormService() {
        if (formService==null){//GEN-END:|446-getter|0|446-preInit
            // Insert pre-init code here
            formService=new Form("Service", new Item[]{getChoiceExpType(), getTextBackupPath(), getStringServiceInfo()});//GEN-BEGIN:|446-getter|1|446-postInit
            formService.addCommand(getBack2Opt());
            formService.addCommand(getItemExport());
            formService.addCommand(getItemLoadTrack());
            formService.addCommand(getItemBrowseMap());
            formService.addCommand(getItemBrowseFile());
            formService.addCommand(getItemClearMap());
            formService.setCommandListener(this);//GEN-END:|446-getter|1|446-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|446-getter|2|
        return formService;
    }
//</editor-fold>//GEN-END:|446-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceExpType ">//GEN-BEGIN:|458-getter|0|458-preInit
    /**
     * Returns an initiliazed instance of choiceExpType component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceExpType() {
        if (choiceExpType==null){//GEN-END:|458-getter|0|458-preInit
            // Insert pre-init code here
            choiceExpType=new ChoiceGroup("Export options\n", Choice.MULTIPLE);//GEN-BEGIN:|458-getter|1|458-postInit
            choiceExpType.append("Application data", null);
            choiceExpType.append("Map cache", null);
//choiceExpType.setSelectedFlags (new boolean[] { false, false });
//choiceExpType.setFont (0, null);
//choiceExpType.setFont (1, null);//GEN-END:|458-getter|1|458-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|458-getter|2|
        return choiceExpType;
    }
//</editor-fold>//GEN-END:|458-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: textBackupPath ">//GEN-BEGIN:|461-getter|0|461-preInit
    /**
     * Returns an initiliazed instance of textBackupPath component.
     * @return the initialized component instance
     */
    public TextField getTextBackupPath() {
        if (textBackupPath==null){//GEN-END:|461-getter|0|461-preInit
            // Insert pre-init code here
            textBackupPath=new TextField("Path to backup\n", null, 128, TextField.ANY);//GEN-BEGIN:|461-getter|1|461-postInit
            textBackupPath.setInitialInputMode("c:/other/");//GEN-END:|461-getter|1|461-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|461-getter|2|
        return textBackupPath;
    }
//</editor-fold>//GEN-END:|461-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringServiceInfo ">//GEN-BEGIN:|462-getter|0|462-preInit
    /**
     * Returns an initiliazed instance of stringServiceInfo component.
     * @return the initialized component instance
     */
    public StringItem getStringServiceInfo() {
        if (stringServiceInfo==null){//GEN-END:|462-getter|0|462-preInit
            // Insert pre-init code here
            stringServiceInfo=new StringItem("Attention!\n", "Remember to have enough free space in phone when doing export/import. Because in case of import failure application would crash on startup most likely.\nAlso it is important to be sure there is no internet, no GPS and no other kind of activity in program at export/import time.");//GEN-LINE:|462-getter|1|462-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|462-getter|2|
        return stringServiceInfo;
    }
//</editor-fold>//GEN-END:|462-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: listWP ">//GEN-BEGIN:|465-getter|0|465-preInit
    /**
     * Returns an initiliazed instance of listWP component.
     * @return the initialized component instance
     */
    public List getListWP() {
        if (listWP==null){//GEN-END:|465-getter|0|465-preInit
            // Insert pre-init code here
            listWP=new List(LangHolder.getString(Lang.waypoints), Choice.IMPLICIT);//GEN-BEGIN:|465-getter|1|465-postInit
            listWP.append("L", null);
            listWP.addCommand(getCancel2Map());
            listWP.addCommand(getItemSelect1());
            listWP.addCommand(getItemCreateRoute());
            listWP.addCommand(getItemMerge());
            listWP.setCommandListener(this);
            listWP.setSelectCommand(getItemSelect1());
            listWP.setSelectedFlags(new boolean[]{false});//GEN-END:|465-getter|1|465-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|465-getter|2|
        return listWP;
    }
//</editor-fold>//GEN-END:|465-getter|2|

//    /**
//     * Performs an action assigned to the selected list element in the listWP component.
//     */
//    public void listWPAction() {//GEN-END:|465-action|0|465-preAction
//        // enter pre-action user code here
////      if ((listWP.getSelectedIndex()!=listWP.size()-1)&&(listWP.getSelectedIndex()!=0))
////      {
////        getListWPMenu().setTitle(listWP.getString(listWP.getSelectedIndex()));
////         MapCanvas.setCurrent(getListWPMenu());
////      }
////          else selectRoute(MapRoute.WAYPOINTSKIND);
//
//        switch (getListWP().getSelectedIndex()) {//GEN-BEGIN:|465-action|1|473-preAction
//            case 0://GEN-END:|465-action|1|473-preAction
//                // write pre-action user code here
////GEN-LINE:|465-action|2|473-postAction
//                // write post-action user code here
//                break;//GEN-BEGIN:|465-action|3|465-postAction
//        }//GEN-END:|465-action|3|465-postAction
//        // enter post-action user code here
//    }//GEN-BEGIN:|465-action|4|
////</editor-fold>//GEN-END:|465-action|4|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: listNav ">//GEN-BEGIN:|475-getter|0|475-preInit
    /**
     * Returns an initiliazed instance of listNav component.
     * @return the initialized component instance
     */
    public List getListNav() {
        if (listNav==null){//GEN-END:|475-getter|0|475-preInit
            // Insert pre-init code here
            listNav=new List(LangHolder.getString(Lang.navigation), Choice.IMPLICIT);//GEN-BEGIN:|475-getter|1|475-postInit
            listNav.append(LangHolder.getString(Lang.waypoints), getImageWPTs());
            listNav.append(LangHolder.getString(Lang.routes), getImageRoutes());
            listNav.append(LangHolder.getString(Lang.tracks), getImageTracks());
            listNav.append("  KML", getImageGEGreen());
            listNav.append(LangHolder.getString(Lang.nav2), getImageMarks());
            listNav.append(" TrackBack", getImageRoutes());
            listNav.addCommand(getClose2Map());
            listNav.setCommandListener(this);
            listNav.setSelectedFlags(new boolean[]{false, false, false, false, false, false});//GEN-END:|475-getter|1|475-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|475-getter|2|
        return listNav;
    }
//</editor-fold>//GEN-END:|475-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Method: listNavAction ">//GEN-BEGIN:|475-action|0|475-preAction
    /**
     * Performs an action assigned to the selected list element in the listNav component.
     */
    public void listNavAction() {//GEN-END:|475-action|0|475-preAction
        // enter pre-action user code here
        switch (getListNav().getSelectedIndex()) {//GEN-BEGIN:|475-action|1|479-preAction
            case 0://GEN-END:|475-action|1|479-preAction
                // write pre-action user code here
                showSelectWPForm(true);
//GEN-LINE:|475-action|2|479-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|475-action|3|481-preAction
            case 1://GEN-END:|475-action|3|481-preAction
                // write pre-action user code here
                showSelectRouteForm(true);
//GEN-LINE:|475-action|4|481-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|475-action|5|483-preAction
            case 2://GEN-END:|475-action|5|483-preAction
                // write pre-action user code here
                showSelectTrackForm(true);
//GEN-LINE:|475-action|6|483-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|475-action|7|485-preAction
            case 3://GEN-END:|475-action|7|485-preAction
                // write pre-action user code here
                showSelectSKMLForm(true);
//GEN-LINE:|475-action|8|485-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|475-action|9|487-preAction
            case 4://GEN-END:|475-action|9|487-preAction
                // write pre-action user code here
                MapCanvas.map.showSaveMarkForm(true);
//GEN-LINE:|475-action|10|487-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|475-action|11|489-preAction
            case 5://GEN-END:|475-action|11|489-preAction
                // write pre-action user code here
                MapCanvas.map.trackBack();// Insert pre-action code here
                back2Map();
//GEN-LINE:|475-action|12|489-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|475-action|13|475-postAction
        }//GEN-END:|475-action|13|475-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|475-action|14|
//</editor-fold>//GEN-END:|475-action|14|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: listTrack ">//GEN-BEGIN:|500-getter|0|500-preInit
    /**
     * Returns an initiliazed instance of listTrack component.
     * @return the initialized component instance
     */
    public List getListTrack() {
        if (listTrack==null){//GEN-END:|500-getter|0|500-preInit
            // Insert pre-init code here
            listTrack=new List(LangHolder.getString(Lang.tracks), Choice.IMPLICIT);//GEN-BEGIN:|500-getter|1|500-postInit
            listTrack.append("L", null);
            listTrack.addCommand(getCancel2Map());
            listTrack.addCommand(getItemSelect1());
            listTrack.addCommand(getItemLoadTrack());
            listTrack.setCommandListener(this);
            listTrack.setSelectCommand(getItemSelect1());
            listTrack.setSelectedFlags(new boolean[]{false});//GEN-END:|500-getter|1|500-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|500-getter|2|
        return listTrack;
    }
//</editor-fold>//GEN-END:|500-getter|2|
//
//    /**
//     * Performs an action assigned to the selected list element in the listTrack component.
//     */
//    public void listTrackAction() {//GEN-END:|500-action|0|500-preAction
////        // enter pre-action user code here
////      if ((listTrack.getSelectedIndex()!=listTrack.size()-1)&&(listTrack.getSelectedIndex()!=0))
////      {     getListTrackMenu().setTitle(listTrack.getString(listTrack.getSelectedIndex()));
////            MapCanvas.setCurrent(getListTrackMenu());
////      }
////          else selectRoute(MapRoute.TRACKKIND);
//
//        switch (getListTrack().getSelectedIndex()) {//GEN-BEGIN:|500-action|1|506-preAction
//            case 0://GEN-END:|500-action|1|506-preAction
//                // write pre-action user code here
////GEN-LINE:|500-action|2|506-postAction
//                // write post-action user code here
//                break;//GEN-BEGIN:|500-action|3|500-postAction
//        }//GEN-END:|500-action|3|500-postAction
//        // enter post-action user code here
//    }//GEN-BEGIN:|500-action|4|
////</editor-fold>//GEN-END:|500-action|4|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: formTelInput ">//GEN-BEGIN:|509-getter|0|509-preInit
    /**
     * Returns an initiliazed instance of formTelInput component.
     * @return the initialized component instance
     */
    public Form getFormTelInput() {
        if (formTelInput==null){//GEN-END:|509-getter|0|509-preInit
            // Insert pre-init code here
            formTelInput=new Form(LangHolder.getString(Lang.telnum), new Item[]{getTextTelNum(), getChoiceSMSType(), getChoiceTels()});//GEN-BEGIN:|509-getter|1|509-postInit
            formTelInput.addCommand(getBack2Opt());
            formTelInput.addCommand(getItemNext1());
            formTelInput.setCommandListener(this);//GEN-END:|509-getter|1|509-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|509-getter|2|
        return formTelInput;
    }
//</editor-fold>//GEN-END:|509-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: textTelNum ">//GEN-BEGIN:|513-getter|0|513-preInit
    /**
     * Returns an initiliazed instance of textTelNum component.
     * @return the initialized component instance
     */
    public TextField getTextTelNum() {
        if (textTelNum==null){//GEN-END:|513-getter|0|513-preInit
            // Insert pre-init code here
            textTelNum=new TextField(LangHolder.getString(Lang.plinptelnum), null, 30, TextField.ANY);//GEN-LINE:|513-getter|1|513-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|513-getter|2|
        return textTelNum;
    }
//</editor-fold>//GEN-END:|513-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceSMSType ">//GEN-BEGIN:|514-getter|0|514-preInit
    /**
     * Returns an initiliazed instance of choiceSMSType component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceSMSType() {
        if (choiceSMSType==null){//GEN-END:|514-getter|0|514-preInit
            // Insert pre-init code here
            choiceSMSType=new ChoiceGroup(LangHolder.getString(Lang.send), Choice.EXCLUSIVE);//GEN-BEGIN:|514-getter|1|514-postInit
            choiceSMSType.append("SMS", null);
            choiceSMSType.append("MapNav SMS", null);
//choiceSMSType.setSelectedFlags (new boolean[] { false, false });
//choiceSMSType.setFont (0, null);
//choiceSMSType.setFont (1, null);//GEN-END:|514-getter|1|514-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|514-getter|2|
        return choiceSMSType;
    }
//</editor-fold>//GEN-END:|514-getter|2|

     public ChoiceGroup getChoiceGPSReconnectDelay() {
        if (choiceGPSReconnectDelay==null){
            choiceGPSReconnectDelay=new ChoiceGroup(LangHolder.getString(Lang.reset), Choice.EXCLUSIVE);//GEN-BEGIN:|514-getter|1|514-postInit
            choiceGPSReconnectDelay.append("1 "+LangHolder.getString(Lang.sec), null);
            choiceGPSReconnectDelay.append("30 "+LangHolder.getString(Lang.sec), null);
        }
        return choiceGPSReconnectDelay;
    }

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceTels ">//GEN-BEGIN:|517-getter|0|517-preInit
    /**
     * Returns an initiliazed instance of choiceTels component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceTels() {
        if (choiceTels==null){//GEN-END:|517-getter|0|517-preInit
            // Insert pre-init code here
            choiceTels=new ChoiceGroup(LangHolder.getString(Lang.last), Choice.EXCLUSIVE);//GEN-BEGIN:|517-getter|1|517-postInit
            choiceTels.append(LangHolder.getString(Lang.new_), null);
            choiceTels.append(" ", null);
            choiceTels.append(" ", null);
            choiceTels.append(" ", null);
//choiceTels.setSelectedFlags (new boolean[] { false, false, false, false });
//choiceTels.setFont (0, null);
//choiceTels.setFont (1, null);
//choiceTels.setFont (2, null);
//choiceTels.setFont (3, null);//GEN-END:|517-getter|1|517-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|517-getter|2|
        return choiceTels;
    }
//</editor-fold>//GEN-END:|517-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: formSaveTrack ">//GEN-BEGIN:|525-getter|0|525-preInit
    /**
     * Returns an initiliazed instance of formSaveTrack component.
     * @return the initialized component instance
     */
    public Form getFormSaveTrack() {
        if (formSaveTrack==null){//GEN-END:|525-getter|0|525-preInit
            // Insert pre-init code here
            textTrackSavePath=null;
            choiceCP_S=null;
            choiceOX_S=null;
            choiceExpCount=null;

            formSaveTrack=new Form(LangHolder.getString(Lang.export), new Item[]{getTextTrackSavePath(), getChoiceCP_S(), getChoiceOX_S(), getChoiceExpCount()});//GEN-BEGIN:|525-getter|1|525-postInit
            formSaveTrack.addCommand(getItem1Save());
            formSaveTrack.addCommand(getBack2Opt());
            formSaveTrack.addCommand(getItemBrowseMap());
            formSaveTrack.setCommandListener(this);//GEN-END:|525-getter|1|525-postInit
            // Insert post-init code here
            // if (selectKind!=MapRoute.TRACKKIND)
            // {formSaveTrack.delete(2);
            // }
        }//GEN-BEGIN:|525-getter|2|
        return formSaveTrack;
    }
//</editor-fold>//GEN-END:|525-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: textTrackSavePath ">//GEN-BEGIN:|530-getter|0|530-preInit
    /**
     * Returns an initiliazed instance of textTrackSavePath component.
     * @return the initialized component instance
     */
    public TextField getTextTrackSavePath() {
        if (textTrackSavePath==null){//GEN-END:|530-getter|0|530-preInit
            // Insert pre-init code here
            textTrackSavePath=new TextField(LangHolder.getString(Lang.sendaddrfile)+"\n", null, 128, TextField.ANY);//GEN-LINE:|530-getter|1|530-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|530-getter|2|
        return textTrackSavePath;
    }
//</editor-fold>//GEN-END:|530-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceCP_S ">//GEN-BEGIN:|531-getter|0|531-preInit
    /**
     * Returns an initiliazed instance of choiceCP_S component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceCP_S() {
        if (choiceCP_S==null){//GEN-END:|531-getter|0|531-preInit
            // Insert pre-init code here
            choiceCP_S=new ChoiceGroup(LangHolder.getString(Lang.codepage), Choice.POPUP);//GEN-BEGIN:|531-getter|1|531-postInit
            choiceCP_S.append("UTF-8", null);
            choiceCP_S.append("Win1251", null);
//choiceCP_S.setSelectedFlags (new boolean[] { false, false });
//choiceCP_S.setFont (0, null);
//choiceCP_S.setFont (1, null);//GEN-END:|531-getter|1|531-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|531-getter|2|
        return choiceCP_S;
    }
//</editor-fold>//GEN-END:|531-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceOX_S ">//GEN-BEGIN:|534-getter|0|534-preInit
    /**
     * Returns an initiliazed instance of choiceOX_S component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceOX_S() {
        if (choiceOX_S==null){//GEN-END:|534-getter|0|534-preInit
            // Insert pre-init code here
            choiceOX_S=new ChoiceGroup(LangHolder.getString(Lang.format), Choice.POPUP);//GEN-BEGIN:|534-getter|1|534-postInit
            choiceOX_S.append("Ozi", null);
            choiceOX_S.append("KML", null);
            choiceOX_S.append("GPX", null);
//choiceOX_S.setSelectedFlags (new boolean[] { false, false, false });
//choiceOX_S.setFont (0, null);
//choiceOX_S.setFont (1, null);
//choiceOX_S.setFont (2, null);//GEN-END:|534-getter|1|534-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|534-getter|2|
        return choiceOX_S;
    }
//</editor-fold>//GEN-END:|534-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceExpCount ">//GEN-BEGIN:|538-getter|0|538-preInit
    /**
     * Returns an initiliazed instance of choiceExpCount component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceExpCount() {
        if (choiceExpCount==null){//GEN-END:|538-getter|0|538-preInit
            // Insert pre-init code here
            choiceExpCount=new ChoiceGroup(LangHolder.getString(Lang.export), Choice.POPUP);//GEN-BEGIN:|538-getter|1|538-postInit
            choiceExpCount.append(LangHolder.getString(Lang.selexport), null);
            choiceExpCount.append(LangHolder.getString(Lang.allexport), null);
//choiceExpCount.setSelectedFlags (new boolean[] { false, false });
//choiceExpCount.setFont (0, null);
//choiceExpCount.setFont (1, null);//GEN-END:|538-getter|1|538-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|538-getter|2|
        return choiceExpCount;
    }
//</editor-fold>//GEN-END:|538-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: formSaveChanges ">//GEN-BEGIN:|541-getter|0|541-preInit
    /**
     * Returns an initiliazed instance of formSaveChanges component.
     * @return the initialized component instance
     */
    public Form getFormSaveChanges() {
        if (formSaveChanges==null){//GEN-END:|541-getter|0|541-preInit
            // Insert pre-init code here
            formSaveChanges=new Form(LangHolder.getString(Lang.save), new Item[]{getStringSaveQ()});//GEN-BEGIN:|541-getter|1|541-postInit
            formSaveChanges.addCommand(getOkYes());
            formSaveChanges.addCommand(getCancelNo());
            formSaveChanges.setCommandListener(this);//GEN-END:|541-getter|1|541-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|541-getter|2|
        return formSaveChanges;
    }
//</editor-fold>//GEN-END:|541-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringSaveQ ">//GEN-BEGIN:|544-getter|0|544-preInit
    /**
     * Returns an initiliazed instance of stringSaveQ component.
     * @return the initialized component instance
     */
    public StringItem getStringSaveQ() {
        if (stringSaveQ==null){//GEN-END:|544-getter|0|544-preInit
            // Insert pre-init code here
            stringSaveQ=new StringItem(LangHolder.getString(Lang.changed)+"\n", LangHolder.getString(Lang.savech));//GEN-LINE:|544-getter|1|544-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|544-getter|2|
        return stringSaveQ;
    }
//</editor-fold>//GEN-END:|544-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: formDeleteAll ">//GEN-BEGIN:|546-getter|0|546-preInit
    /**
     * Returns an initiliazed instance of formDeleteAll component.
     * @return the initialized component instance
     */
    public Form getFormDeleteAll() {
        if (formDeleteAll==null){//GEN-END:|546-getter|0|546-preInit
            // Insert pre-init code here
            formDeleteAll=new Form(LangHolder.getString(Lang.deleteall), new Item[]{getStringDelAllQ()});//GEN-BEGIN:|546-getter|1|546-postInit
            formDeleteAll.addCommand(getOkYes());
            formDeleteAll.addCommand(getCancelNo());
            formDeleteAll.setCommandListener(this);//GEN-END:|546-getter|1|546-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|546-getter|2|
        return formDeleteAll;
    }
//</editor-fold>//GEN-END:|546-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringDelAllQ ">//GEN-BEGIN:|549-getter|0|549-preInit
    /**
     * Returns an initiliazed instance of stringDelAllQ component.
     * @return the initialized component instance
     */
    public StringItem getStringDelAllQ() {
        if (stringDelAllQ==null){//GEN-END:|549-getter|0|549-preInit
            // Insert pre-init code here
            stringDelAllQ=new StringItem("", "");//GEN-LINE:|549-getter|1|549-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|549-getter|2|
        return stringDelAllQ;
    }
//</editor-fold>//GEN-END:|549-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: formAddOMap ">//GEN-BEGIN:|556-getter|0|556-preInit
    /**
     * Returns an initiliazed instance of formAddOMap component.
     * @return the initialized component instance
     */
    public Form getFormAddOMap() {
        if (formAddOMap==null){//GEN-END:|556-getter|0|556-preInit
            // Insert pre-init code here
            formAddOMap=new Form(LangHolder.getString(Lang.addmap), new Item[]{getTextOMapName(), getTextCMapName(), getStringMapFree()});//GEN-BEGIN:|556-getter|1|556-postInit
            formAddOMap.addCommand(getBack2Opt());
            formAddOMap.addCommand(getItemBrowseMap());
            formAddOMap.addCommand(getItemComLoadMap());
            formAddOMap.setCommandListener(this);//GEN-END:|556-getter|1|556-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|556-getter|2|
        return formAddOMap;
    }
//</editor-fold>//GEN-END:|556-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: textOMapName ">//GEN-BEGIN:|561-getter|0|561-preInit
    /**
     * Returns an initiliazed instance of textOMapName component.
     * @return the initialized component instance
     */
    public TextField getTextOMapName() {
        if (textOMapName==null){//GEN-END:|561-getter|0|561-preInit
            // Insert pre-init code here
            textOMapName=new TextField(LangHolder.getString(Lang.label), null, 128, TextField.ANY);//GEN-LINE:|561-getter|1|561-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|561-getter|2|
        return textOMapName;
    }
//</editor-fold>//GEN-END:|561-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: textCMapName ">//GEN-BEGIN:|562-getter|0|562-preInit
    /**
     * Returns an initiliazed instance of textCMapName component.
     * @return the initialized component instance
     */
    public TextField getTextCMapName() {
        if (textCMapName==null){//GEN-END:|562-getter|0|562-preInit
            // Insert pre-init code here
            textCMapName=new TextField(LangHolder.getString(Lang.entermn), null, 128, TextField.ANY);//GEN-LINE:|562-getter|1|562-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|562-getter|2|
        return textCMapName;
    }
//</editor-fold>//GEN-END:|562-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringMapFree ">//GEN-BEGIN:|563-getter|0|563-preInit
    /**
     * Returns an initiliazed instance of stringMapFree component.
     * @return the initialized component instance
     */
    public StringItem getStringMapFree() {
        if (stringMapFree==null){//GEN-END:|563-getter|0|563-preInit
            // Insert pre-init code here
            stringMapFree=new StringItem(LangHolder.getString(Lang.imagecachefree)+"\n", "");//GEN-LINE:|563-getter|1|563-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|563-getter|2|
        return stringMapFree;
    }
//</editor-fold>//GEN-END:|563-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: listBT ">//GEN-BEGIN:|570-getter|0|570-preInit
    /**
     * Returns an initiliazed instance of listBT component.
     * @return the initialized component instance
     */
    public List getListBT() {
        if (listBT==null){//GEN-END:|570-getter|0|570-preInit
            // Insert pre-init code here
            listBT=new List(LangHolder.getString(Lang.btdevices), Choice.IMPLICIT);//GEN-BEGIN:|570-getter|1|570-postInit
            listBT.append("New device", null);
            listBT.addCommand(getCancel2Map());
            listBT.addCommand(getItemSelBT());
            listBT.addCommand(getItemClear());
            listBT.setCommandListener(this);
            listBT.setFitPolicy(Choice.TEXT_WRAP_OFF);
            listBT.setSelectCommand(getItemSelBT());
            listBT.setSelectedFlags(new boolean[]{false});//GEN-END:|570-getter|1|570-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|570-getter|2|
        return listBT;
    }
//</editor-fold>//GEN-END:|570-getter|2|


//<editor-fold defaultstate="collapsed" desc=" Generated Getter: listKML ">//GEN-BEGIN:|579-getter|0|579-preInit
    /**
     * Returns an initiliazed instance of listKML component.
     * @return the initialized component instance
     */
    public List getListKML() {
        if (listKML==null){//GEN-END:|579-getter|0|579-preInit
            // Insert pre-init code here
            listKML=new List("KML", Choice.IMPLICIT);//GEN-BEGIN:|579-getter|1|579-postInit
            listKML.append("LE", null);
            listKML.addCommand(getItemSelect1());
            listKML.addCommand(getBack2Opt());
            listKML.setCommandListener(this);
            listKML.setFitPolicy(Choice.TEXT_WRAP_ON);
            listKML.setSelectCommand(getItemSelect1());
            listKML.setSelectedFlags(new boolean[]{false});//GEN-END:|579-getter|1|579-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|579-getter|2|
        return listKML;
    }
//</editor-fold>//GEN-END:|579-getter|2|

//    /**
//     * Performs an action assigned to the selected list element in the listKML component.
//     */
//    public void listKMLAction() {//GEN-END:|579-action|0|579-preAction
//        // enter pre-action user code here
//        switch (getListKML().getSelectedIndex()) {//GEN-BEGIN:|579-action|1|585-preAction
//            case 0://GEN-END:|579-action|1|585-preAction
//                // write pre-action user code here
////GEN-LINE:|579-action|2|585-postAction
//                // write post-action user code here
//                break;//GEN-BEGIN:|579-action|3|579-postAction
//        }//GEN-END:|579-action|3|579-postAction
//        // enter post-action user code here
//    }//GEN-BEGIN:|579-action|4|
////</editor-fold>//GEN-END:|579-action|4|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: listOMaps ">//GEN-BEGIN:|588-getter|0|588-preInit
    /**
     * Returns an initiliazed instance of listOMaps component.
     * @return the initialized component instance
     */
    public List getListOMaps() {
        if (listOMaps==null){//GEN-END:|588-getter|0|588-preInit
            // Insert pre-init code here
            listOMaps=new List(LangHolder.getString(Lang.extmaps), Choice.IMPLICIT);//GEN-BEGIN:|588-getter|1|588-postInit
            listOMaps.append("Add Map", null);
            listOMaps.addCommand(getBack2Opt());
            listOMaps.addCommand(getItemWPDelete());
            listOMaps.setCommandListener(this);
            listOMaps.setSelectedFlags(new boolean[]{false});//GEN-END:|588-getter|1|588-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|588-getter|2|
        return listOMaps;
    }
//</editor-fold>//GEN-END:|588-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: listIMaps ">//GEN-BEGIN:|596-getter|0|596-preInit
    /**
     * Returns an initiliazed instance of listIMaps component.
     * @return the initialized component instance
     */
    public List getListIMaps() {
        if (listIMaps==null){//GEN-END:|596-getter|0|596-preInit
            // Insert pre-init code here
            listIMaps=new List(LangHolder.getString(Lang.intmaps), Choice.IMPLICIT);//GEN-BEGIN:|596-getter|1|596-postInit
            listIMaps.append("Add Map", null);
            listIMaps.addCommand(getBack2Opt());
            listIMaps.addCommand(getItemWPDelete());
            listIMaps.setCommandListener(this);
            listIMaps.setSelectedFlags(new boolean[]{false});//GEN-END:|596-getter|1|596-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|596-getter|2|
        return listIMaps;
    }
//</editor-fold>//GEN-END:|596-getter|2|


    public List getListWPMenu() {
        if (listWPMenu==null){//GEN-END:|604-getter|0|604-preInit
            // Insert pre-init code here
            listWPMenu=new List("", Choice.IMPLICIT);//GEN-BEGIN:|604-getter|1|604-postInit
            listWPMenu.append(LangHolder.getString(Lang.open), null);
            listWPMenu.append(LangHolder.getString(Lang.edit), null);
            listWPMenu.append(LangHolder.getString(Lang.delete), null);
            listWPMenu.append(LangHolder.getString(Lang.export), null);
            listWPMenu.append(LangHolder.getString(Lang.send)+" BT", null);
            listWPMenu.append(LangHolder.getString(Lang.send)+" IR", null);
            listWPMenu.append(LangHolder.getString(Lang.clone), null);
            listWPMenu.append(LangHolder.getString(Lang.route), null);
            listWPMenu.append(LangHolder.getString(Lang.preload), null);
            listWPMenu.addCommand(getBack2Opt());
            listWPMenu.setCommandListener(this);
//listWPMenu.setSelectedFlags (new boolean[] { false, false, false, false, false, false, false, false, false });//GEN-END:|604-getter|1|604-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|604-getter|2|
        return listWPMenu;
    }
//</editor-fold>//GEN-END:|604-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Method: listWPMenuAction ">//GEN-BEGIN:|604-action|0|604-preAction
    /**
     * Performs an action assigned to the selected list element in the listWPMenu component.
     */
    public void listWPMenuAction() {//GEN-END:|604-action|0|604-preAction
        // enter pre-action user code here
        switch (getListWPMenu().getSelectedIndex()) {//GEN-BEGIN:|604-action|1|609-preAction
            case 0://GEN-END:|604-action|1|609-preAction
                // write pre-action user code here
                selectRoute(MapRoute.WAYPOINTSKIND);
//GEN-LINE:|604-action|2|609-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|604-action|3|610-preAction
            case 1://GEN-END:|604-action|3|610-preAction
                // write pre-action user code here
                editRoute(MapRoute.WAYPOINTSKIND, listWP);
//GEN-LINE:|604-action|4|610-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|604-action|5|611-preAction
            case 2://GEN-END:|604-action|5|611-preAction
                // write pre-action user code here
                deleteWP(MapRoute.WAYPOINTSKIND);
//GEN-LINE:|604-action|6|611-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|604-action|7|612-preAction
            case 3://GEN-END:|604-action|7|612-preAction
                // write pre-action user code here
                formSaveTrack=null;
                getFormSaveTrack();
                textTrackSavePath.setString(RMSOption.lastTrackSavePath);
                choiceCP_S.setSelectedIndex(RMSOption.routeCP, true);
                choiceOX_S.setSelectedIndex(RMSOption.routeFormat, true);
                saveKind=EXPORTKIND;

                switchDisplayable(null, getFormSaveTrack());//GEN-LINE:|604-action|8|612-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|604-action|9|667-preAction
            case 4://GEN-END:|604-action|9|667-preAction
                // write pre-action user code here
                showSendRoute(SENDKIND_BT);
//GEN-LINE:|604-action|10|667-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|604-action|11|614-preAction
            case 5://GEN-END:|604-action|11|614-preAction
                // write pre-action user code here
                showSendRoute(SENDKIND_IR);
//          saveKind=SENDKIND;
//              // Insert pre-action code here
//              formSaveTrack=null;       
//              getFormSaveTrack();
//              choiceCP_S.setSelectedIndex(RMSOption.routeCP,true);
//              choiceOX_S.setSelectedIndex(RMSOption.routeFormat,true);
//              formSaveTrack.delete(3);
//              formSaveTrack.delete(0);
//              formSaveTrack.removeCommand(itemBrowseMap);
//              getDisplay().setCurrent(formSaveTrack);
//GEN-LINE:|604-action|12|614-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|604-action|13|615-preAction
            case 6://GEN-END:|604-action|13|615-preAction
                // write pre-action user code here
                if ((listWP.getSelectedIndex()==0)||(listWP.getSelectedIndex()==listWP.size()-1)){
                    MapCanvas.setCurrent(getSelectWarningAlert(), listWP);
                    return;
                }
                cloneRoute(MapRoute.WAYPOINTSKIND, listWP, false);
                MapCanvas.showmsg(LangHolder.getString(Lang.clone), LangHolder.getString(Lang.ok), AlertType.INFO, listWP);
//GEN-LINE:|604-action|14|615-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|604-action|15|616-preAction
            case 7://GEN-END:|604-action|15|616-preAction
                // write pre-action user code here
                cloneRoute(MapRoute.WAYPOINTSKIND, listWP, true);
                MapCanvas.showmsg(LangHolder.getString(Lang.clone), LangHolder.getString(Lang.ok), AlertType.INFO, getListRoute());

//GEN-LINE:|604-action|16|616-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|604-action|17|617-preAction
            case 8://GEN-END:|604-action|17|617-preAction
                // write pre-action user code here
                MapRoute mrs=MapCanvas.map.rmss.loadRoute(listWP.getSelectedIndex()-1, MapRoute.WAYPOINTSKIND);
                back2Map();
                MapCanvas.map.startTrack(mrs);
                mrs.startPlay(false);
//GEN-LINE:|604-action|18|617-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|604-action|19|604-postAction
        }//GEN-END:|604-action|19|604-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|604-action|20|
//</editor-fold>//GEN-END:|604-action|20|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: listTrackMenu ">//GEN-BEGIN:|618-getter|0|618-preInit
    /**
     * Returns an initiliazed instance of listTrackMenu component.
     * @return the initialized component instance
     */
    public List getListTrackMenu() {
        if (listTrackMenu==null){//GEN-END:|618-getter|0|618-preInit
            // Insert pre-init code here
            listTrackMenu=new List("", Choice.IMPLICIT);//GEN-BEGIN:|618-getter|1|618-postInit
            listTrackMenu.append(LangHolder.getString(Lang.open), null);
            listTrackMenu.append(LangHolder.getString(Lang.delete), null);
            listTrackMenu.append(LangHolder.getString(Lang.export), null);
            listTrackMenu.append(LangHolder.getString(Lang.send)+" WWW", null);
            listTrackMenu.append(LangHolder.getString(Lang.send)+" BT", null);
            listTrackMenu.append(LangHolder.getString(Lang.send)+" OSM", null);
            listTrackMenu.append(LangHolder.getString(Lang.send)+" IR", null);
            listTrackMenu.append(LangHolder.getString(Lang.route), null);
            listTrackMenu.append(LangHolder.getString(Lang.preload), null);
            listTrackMenu.append(LangHolder.getString(Lang.play), null);
            listTrackMenu.append(LangHolder.getString(Lang.rename), null);
            listTrackMenu.addCommand(getBack2Opt());
            listTrackMenu.setCommandListener(this);
            //listTrackMenu.setSelectedFlags(new boolean[]{false, false, false, false, false, false, false, false, false, false});//GEN-END:|618-getter|1|618-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|618-getter|2|
        return listTrackMenu;
    }
//</editor-fold>//GEN-END:|618-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Method: listTrackMenuAction ">//GEN-BEGIN:|618-action|0|618-preAction
    /**
     * Performs an action assigned to the selected list element in the listTrackMenu component.
     */
    public void listTrackMenuAction() {//GEN-END:|618-action|0|618-preAction
        // enter pre-action user code here
        switch (getListTrackMenu().getSelectedIndex()) {//GEN-BEGIN:|618-action|1|623-preAction
            case 0://GEN-END:|618-action|1|623-preAction
                // write pre-action user code here
                selectRoute(MapRoute.TRACKKIND);
//GEN-LINE:|618-action|2|623-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|618-action|3|624-preAction
            case 1://GEN-END:|618-action|3|624-preAction
                // write pre-action user code here
                deleteTrack(MapRoute.TRACKKIND);
//GEN-LINE:|618-action|4|624-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|618-action|5|625-preAction
            case 2://GEN-END:|618-action|5|625-preAction
                // write pre-action user code here
                formSaveTrack=null;
                getFormSaveTrack();
                textTrackSavePath.setString(RMSOption.lastTrackSavePath);
                choiceCP_S.setSelectedIndex(RMSOption.routeCP, true);
                choiceOX_S.setSelectedIndex(RMSOption.routeFormat, true);
                saveKind=EXPORTKIND;
                switchDisplayable(null, formSaveTrack);
//GEN-LINE:|618-action|6|625-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|618-action|7|626-preAction
            case 3://GEN-END:|618-action|7|626-preAction
                // write pre-action user code here
                if (RMSOption.netRadarLogin.equals(MapUtil.emptyString)){
                    getListOpt();
                    showOptNetRadar();
                    return;
                }
                MapCanvas.setCurrent(
                  new ProgressForm("WWW", "Track upload",
                  new WebTrackSend(listTrackMenu, MapCanvas.map.rmss.loadRoute(listTrack.getSelectedIndex()-1, MapRoute.TRACKKIND)), listTrackMenu));
//GEN-LINE:|618-action|8|626-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|618-action|9|665-preAction
            case 4://GEN-END:|618-action|9|665-preAction
                // write pre-action user code here
                showSendRoute(SENDKIND_BT);
//GEN-LINE:|618-action|10|665-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|618-action|11|670-preAction
            case 5://GEN-END:|618-action|11|670-preAction
                // write pre-action user code here
                MapCanvas.setCurrent(
                  new ProgressForm("OSM", "Track upload",
                  new OSMTrackSend(listTrackMenu, MapCanvas.map.rmss.loadRoute(listTrack.getSelectedIndex()-1, MapRoute.TRACKKIND)), listTrackMenu));
//GEN-LINE:|618-action|12|670-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|618-action|13|627-preAction
            case 6://GEN-END:|618-action|13|627-preAction
                // write pre-action user code here
                showSendRoute(SENDKIND_IR);
//          saveKind=SENDKIND_IR;
//              // Insert pre-action code here
//              formSaveTrack=null;       
//              getFormSaveTrack();
//              choiceCP_S.setSelectedIndex(RMSOption.routeCP,true);
//              choiceOX_S.setSelectedIndex(RMSOption.routeFormat,true);
//              formSaveTrack.delete(3);
//              formSaveTrack.delete(0);
//              formSaveTrack.removeCommand(itemBrowseMap);
//              getDisplay().setCurrent(formSaveTrack);

//GEN-LINE:|618-action|14|627-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|618-action|15|628-preAction
            case 7://GEN-END:|618-action|15|628-preAction
                // write pre-action user code here
                cloneRoute(MapRoute.TRACKKIND, listTrack, true);
                MapCanvas.showmsg(LangHolder.getString(Lang.clone), LangHolder.getString(Lang.ok), AlertType.INFO, listRoute);

//GEN-LINE:|618-action|16|628-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|618-action|17|629-preAction
            case 8://GEN-END:|618-action|17|629-preAction
                // write pre-action user code here
                MapRoute mrs=MapCanvas.map.rmss.loadRoute(listTrack.getSelectedIndex()-1, MapRoute.TRACKKIND);
                back2Map();
                MapCanvas.map.startTrack(mrs);
                MapCanvas.map.activeTrack.recalcMapLevelScreen(null);

                mrs.startPlay(false);

                break;
                
            case 9:
                MapRoute mrs1=MapCanvas.map.rmss.loadRoute(listTrack.getSelectedIndex()-1, MapRoute.TRACKKIND);
                back2Map();
                MapCanvas.map.startTrack(mrs1);
                MapCanvas.map.activeTrack.recalcMapLevelScreen(null);
                mrs1.startPlay(true);

                break;

            case 10://rename
renameTrack();
                break;


        }//GEN-END:|618-action|21|618-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|618-action|22|
//</editor-fold>//GEN-END:|618-action|22|

    public void renameTrack() {

    String tn = MapCanvas.map.rmss.getRouteClearName(listTrack.getSelectedIndex()-1);
    textBox = new TextBox(LangHolder.getString(Lang.rename), tn, 30, 0);
    textBox.addCommand(getBack2Opt());
    textBox.addCommand(getItem1Save());
    textBox.setCommandListener(this);
    MapCanvas.setCurrent(textBox);

    }


//<editor-fold defaultstate="collapsed" desc=" Generated Getter: listRouteMenu ">//GEN-BEGIN:|631-getter|0|631-preInit
    /**
     * Returns an initiliazed instance of listRouteMenu component.
     * @return the initialized component instance
     */
    public List getListRouteMenu() {
        if (listRouteMenu==null){//GEN-END:|631-getter|0|631-preInit
            // Insert pre-init code here
            listRouteMenu=new List("", Choice.IMPLICIT);//GEN-BEGIN:|631-getter|1|631-postInit
            listRouteMenu.append(LangHolder.getString(Lang.open), null);
            listRouteMenu.append(LangHolder.getString(Lang.edit), null);
            listRouteMenu.append(LangHolder.getString(Lang.delete), null);
            listRouteMenu.append(LangHolder.getString(Lang.export), null);
            listRouteMenu.append(LangHolder.getString(Lang.send)+" BT", null);
            listRouteMenu.append(LangHolder.getString(Lang.send)+" IR", null);
            listRouteMenu.append(LangHolder.getString(Lang.clone), null);
            listRouteMenu.append(LangHolder.getString(Lang.waypoints), null);
            listRouteMenu.append(LangHolder.getString(Lang.reverse), null);
            listRouteMenu.append(LangHolder.getString(Lang.preload), null);
            listRouteMenu.addCommand(getBack2Opt());
            listRouteMenu.setCommandListener(this);
            //listRouteMenu.setSelectedFlags(new boolean[]{false, false, false, false, false, false, false, false, false, false});//GEN-END:|631-getter|1|631-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|631-getter|2|
        return listRouteMenu;
    }
//</editor-fold>//GEN-END:|631-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Method: listRouteMenuAction ">//GEN-BEGIN:|631-action|0|631-preAction
    /**
     * Performs an action assigned to the selected list element in the listRouteMenu component.
     */
    public void listRouteMenuAction() {//GEN-END:|631-action|0|631-preAction
        // enter pre-action user code here
        switch (getListRouteMenu().getSelectedIndex()) {//GEN-BEGIN:|631-action|1|636-preAction
            case 0://GEN-END:|631-action|1|636-preAction
                // write pre-action user code here
                selectRoute(MapRoute.ROUTEKIND);
//GEN-LINE:|631-action|2|636-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|631-action|3|637-preAction
            case 1://GEN-END:|631-action|3|637-preAction
                // write pre-action user code here
                editRoute(MapRoute.ROUTEKIND, listRoute);

//GEN-LINE:|631-action|4|637-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|631-action|5|638-preAction
            case 2://GEN-END:|631-action|5|638-preAction
                // write pre-action user code here
                deleteRoute(MapRoute.ROUTEKIND);
//GEN-LINE:|631-action|6|638-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|631-action|7|639-preAction
            case 3://GEN-END:|631-action|7|639-preAction
                // write pre-action user code here
                formSaveTrack=null;
                getFormSaveTrack();
                textTrackSavePath.setString(RMSOption.lastTrackSavePath);
                choiceCP_S.setSelectedIndex(RMSOption.routeCP, true);
                choiceOX_S.setSelectedIndex(RMSOption.routeFormat, true);
                saveKind=EXPORTKIND;
                getDisplay().setCurrent(formSaveTrack);
//GEN-LINE:|631-action|8|639-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|631-action|9|666-preAction
            case 4://GEN-END:|631-action|9|666-preAction
                // write pre-action user code here
                showSendRoute(SENDKIND_BT);
//GEN-LINE:|631-action|10|666-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|631-action|11|640-preAction
            case 5://GEN-END:|631-action|11|640-preAction
                // write pre-action user code here
                showSendRoute(SENDKIND_IR);
//          saveKind=SENDKIND;
//              // Insert pre-action code here
//              formSaveTrack=null;       
//              getFormSaveTrack();
//              choiceCP_S.setSelectedIndex(RMSOption.routeCP,true);
//              choiceOX_S.setSelectedIndex(RMSOption.routeFormat,true);
//              formSaveTrack.delete(3);
//              formSaveTrack.delete(0);
//              formSaveTrack.removeCommand(itemBrowseMap);
//              getDisplay().setCurrent(formSaveTrack);
//GEN-LINE:|631-action|12|640-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|631-action|13|641-preAction
            case 6://GEN-END:|631-action|13|641-preAction
                // write pre-action user code here
                cloneRoute(MapRoute.ROUTEKIND, listRoute, false);
                MapCanvas.showmsg(LangHolder.getString(Lang.clone), LangHolder.getString(Lang.ok), AlertType.INFO, listRoute);
//GEN-LINE:|631-action|14|641-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|631-action|15|642-preAction
            case 7://GEN-END:|631-action|15|642-preAction
                // write pre-action user code here
                cloneRoute(MapRoute.ROUTEKIND, listRoute, true);
                MapCanvas.showmsg(LangHolder.getString(Lang.clone), LangHolder.getString(Lang.ok), AlertType.INFO, listWP);

//GEN-LINE:|631-action|16|642-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|631-action|17|643-preAction
            case 8://GEN-END:|631-action|17|643-preAction
                // write pre-action user code here
                reverseRoute(MapRoute.ROUTEKIND, listRoute);
                MapCanvas.showmsg(LangHolder.getString(Lang.reverse), LangHolder.getString(Lang.ok), AlertType.INFO, listRoute);
//GEN-LINE:|631-action|18|643-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|631-action|19|644-preAction
            case 9://GEN-END:|631-action|19|644-preAction
                // write pre-action user code here
                MapRoute mrs=MapCanvas.map.rmss.loadRoute(listRoute.getSelectedIndex()-1, MapRoute.ROUTEKIND);
                back2Map();
                MapCanvas.map.startTrack(mrs);
                mrs.startPlay(false);

//GEN-LINE:|631-action|20|644-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|631-action|21|631-postAction
        }//GEN-END:|631-action|21|631-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|631-action|22|
//</editor-fold>//GEN-END:|631-action|22|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: listSKML ">//GEN-BEGIN:|645-getter|0|645-preInit
    /**
     * Returns an initiliazed instance of listSKML component.
     * @return the initialized component instance
     */
    public List getListSKML() {
        if (listSKML==null){//GEN-END:|645-getter|0|645-preInit
            // Insert pre-init code here
            listSKML=new List("KML", Choice.IMPLICIT);//GEN-BEGIN:|645-getter|1|645-postInit
            listSKML.append("LE", null);
            listSKML.addCommand(getClose2Map());
            listSKML.addCommand(getItemSelect1());
            listSKML.setCommandListener(this);
            listSKML.setSelectCommand(getItemSelect1());
            listSKML.setSelectedFlags(new boolean[]{false});//GEN-END:|645-getter|1|645-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|645-getter|2|
        return listSKML;
    }
//</editor-fold>//GEN-END:|645-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Method: listSKMLAction ">//GEN-BEGIN:|645-action|0|645-preAction
    /**
     * Performs an action assigned to the selected list element in the listSKML component.
     */
    public void listSKMLAction() {//GEN-END:|645-action|0|645-preAction
        // enter pre-action user code here
//   if ((listSKML.getSelectedIndex()!=listWP.size()-1))
//      {  MapCanvas.setCurrent(getListSKMLMenu());
//         getListSKMLMenu().setTitle(listSKML.getString(listSKML.getSelectedIndex()));
//      }
//          //else selectRoute(MapRoute.TRACKKIND);
//          else selectRoute(MapRoute.WAYPOINTSKIND);
//   
        switch (getListSKML().getSelectedIndex()) {//GEN-BEGIN:|645-action|1|650-preAction
            case 0://GEN-END:|645-action|1|650-preAction
                // write pre-action user code here
//GEN-LINE:|645-action|2|650-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|645-action|3|645-postAction
        }//GEN-END:|645-action|3|645-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|645-action|4|
//</editor-fold>//GEN-END:|645-action|4|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: listSKMLMenu ">//GEN-BEGIN:|651-getter|0|651-preInit
    /**
     * Returns an initiliazed instance of listSKMLMenu component.
     * @return the initialized component instance
     */
    public List getListSKMLMenu() {
        if (listSKMLMenu==null){//GEN-END:|651-getter|0|651-preInit
            // Insert pre-init code here
            listSKMLMenu=new List("", Choice.IMPLICIT);//GEN-BEGIN:|651-getter|1|651-postInit
            listSKMLMenu.append(LangHolder.getString(Lang.open), null);
            listSKMLMenu.append(LangHolder.getString(Lang.route), null);
            listSKMLMenu.addCommand(getBack2Opt());
            listSKMLMenu.setCommandListener(this);

            // Insert post-init code here
        }//GEN-BEGIN:|651-getter|2|
        return listSKMLMenu;
    }
//</editor-fold>//GEN-END:|651-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Method: listSKMLMenuAction ">//GEN-BEGIN:|651-action|0|651-preAction
    /**
     * Performs an action assigned to the selected list element in the listSKMLMenu component.
     */
    public void listSKMLMenuAction() {//GEN-END:|651-action|0|651-preAction
        // enter pre-action user code here

        switch (getListSKMLMenu().getSelectedIndex()) {
            case 0:
                selectRoute(MapRoute.KMLDOCUMENT);

                break;
                  case 1:

                cloneRoute(MapRoute.KMLDOCUMENT, listSKML, true);
                MapCanvas.showmsg(LangHolder.getString(Lang.clone), LangHolder.getString(Lang.ok), AlertType.INFO, getListRoute());
                
                break;
        }

    }//GEN-BEGIN:|651-action|4|
//</editor-fold>//GEN-END:|651-action|4|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: itemGoto1 ">//GEN-BEGIN:|15-getter|0|15-preInit
    /**
     * Returns an initiliazed instance of itemGoto1 component.
     * @return the initialized component instance
     */
    public Command getItemGoto1() {
        if (itemGoto1==null){//GEN-END:|15-getter|0|15-preInit
            // Insert pre-init code here
            itemGoto1=new Command(LangHolder.getString(Lang.goto_), Command.ITEM, 1);//GEN-LINE:|15-getter|1|15-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|15-getter|2|
        return itemGoto1;
    }
//</editor-fold>//GEN-END:|15-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: cancel2Map ">//GEN-BEGIN:|17-getter|0|17-preInit
    /**
     * Returns an initiliazed instance of cancel2Map component.
     * @return the initialized component instance
     */
    public Command getCancel2Map() {
        if (cancel2Map==null){//GEN-END:|17-getter|0|17-preInit
            // Insert pre-init code here
            cancel2Map=new Command(LangHolder.getString(Lang.back), Command.BACK, 10);//GEN-LINE:|17-getter|1|17-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|17-getter|2|
        return cancel2Map;
    }
//</editor-fold>//GEN-END:|17-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: back2Opt ">//GEN-BEGIN:|23-getter|0|23-preInit
    /**
     * Returns an initiliazed instance of back2Opt component.
     * @return the initialized component instance
     */
    public Command getBack2Opt() {
        if (back2Opt==null){//GEN-END:|23-getter|0|23-preInit
            // Insert pre-init code here
            back2Opt=new Command(LangHolder.getString(Lang.back), Command.BACK, 10);//GEN-LINE:|23-getter|1|23-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|23-getter|2|
        return back2Opt;
    }
//</editor-fold>//GEN-END:|23-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: close2Map ">//GEN-BEGIN:|29-getter|0|29-preInit
    /**
     * Returns an initiliazed instance of close2Map component.
     * @return the initialized component instance
     */
    public Command getClose2Map() {
        if (close2Map==null){//GEN-END:|29-getter|0|29-preInit
            // Insert pre-init code here
            close2Map=new Command(LangHolder.getString(Lang.close), Command.BACK, 99);//GEN-LINE:|29-getter|1|29-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|29-getter|2|
        return close2Map;
    }
//</editor-fold>//GEN-END:|29-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: itemSelectOpt ">//GEN-BEGIN:|30-getter|0|30-preInit
    /**
     * Returns an initiliazed instance of itemSelectOpt component.
     * @return the initialized component instance
     */
    public Command getItemSelectOpt() {
        if (itemSelectOpt==null){//GEN-END:|30-getter|0|30-preInit
            // Insert pre-init code here
            itemSelectOpt=new Command(LangHolder.getString(Lang.select), Command.ITEM, 1);//GEN-LINE:|30-getter|1|30-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|30-getter|2|
        return itemSelectOpt;
    }
//</editor-fold>//GEN-END:|30-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: itemSaveOpt ">//GEN-BEGIN:|36-getter|0|36-preInit
    /**
     * Returns an initiliazed instance of itemSaveOpt component.
     * @return the initialized component instance
     */
    public Command getItemSaveOpt() {
        if (itemSaveOpt==null){//GEN-END:|36-getter|0|36-preInit
            // Insert pre-init code here
            itemSaveOpt=new Command(LangHolder.getString(Lang.save), Command.ITEM, 1);//GEN-LINE:|36-getter|1|36-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|36-getter|2|
        return itemSaveOpt;
    }
//</editor-fold>//GEN-END:|36-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: itemCommandListCOM ">//GEN-BEGIN:|151-getter|0|151-preInit
    /**
     * Returns an initiliazed instance of itemCommandListCOM component.
     * @return the initialized component instance
     */
    public Command getItemCommandListCOM() {
        if (itemCommandListCOM==null){//GEN-END:|151-getter|0|151-preInit
            // Insert pre-init code here
            itemCommandListCOM=new Command(LangHolder.getString(Lang.select), Command.ITEM, 2);//GEN-LINE:|151-getter|1|151-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|151-getter|2|
        return itemCommandListCOM;
    }
//</editor-fold>//GEN-END:|151-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: helpClearCache ">//GEN-BEGIN:|271-getter|0|271-preInit
    /**
     * Returns an initiliazed instance of helpClearCache component.
     * @return the initialized component instance
     */
    public Command getHelpClearCache() {
        if (helpClearCache==null){//GEN-END:|271-getter|0|271-preInit
            // Insert pre-init code here
            helpClearCache=new Command(LangHolder.getString(Lang.clearcache), Command.HELP, 2);//GEN-LINE:|271-getter|1|271-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|271-getter|2|
        return helpClearCache;
    }
//</editor-fold>//GEN-END:|271-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: screenReset ">//GEN-BEGIN:|274-getter|0|274-preInit
    /**
     * Returns an initiliazed instance of screenReset component.
     * @return the initialized component instance
     */
    public Command getScreenReset() {
        if (screenReset==null){//GEN-END:|274-getter|0|274-preInit
            // Insert pre-init code here
            screenReset=new Command(LangHolder.getString(Lang.reset), Command.HELP, 15);//GEN-LINE:|274-getter|1|274-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|274-getter|2|
        return screenReset;
    }
//</editor-fold>//GEN-END:|274-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: okYes ">//GEN-BEGIN:|277-getter|0|277-preInit
    /**
     * Returns an initiliazed instance of okYes component.
     * @return the initialized component instance
     */
    public Command getOkYes() {
        if (okYes==null){//GEN-END:|277-getter|0|277-preInit
            // Insert pre-init code here
            okYes=new Command(LangHolder.getString(Lang.yes), Command.OK, 1);//GEN-LINE:|277-getter|1|277-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|277-getter|2|
        return okYes;
    }
//</editor-fold>//GEN-END:|277-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: cancelNo ">//GEN-BEGIN:|279-getter|0|279-preInit
    /**
     * Returns an initiliazed instance of cancelNo component.
     * @return the initialized component instance
     */
    public Command getCancelNo() {
        if (cancelNo==null){//GEN-END:|279-getter|0|279-preInit
            // Insert pre-init code here
            cancelNo=new Command(LangHolder.getString(Lang.no), Command.CANCEL, 2);//GEN-LINE:|279-getter|1|279-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|279-getter|2|
        return cancelNo;
    }
//</editor-fold>//GEN-END:|279-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: itemClear ">//GEN-BEGIN:|292-getter|0|292-preInit
    /**
     * Returns an initiliazed instance of itemClear component.
     * @return the initialized component instance
     */
    public Command getItemClear() {
        if (itemClear==null){//GEN-END:|292-getter|0|292-preInit
            // Insert pre-init code here
            itemClear=new Command(LangHolder.getString(Lang.clear), Command.ITEM, 5);//GEN-LINE:|292-getter|1|292-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|292-getter|2|
        return itemClear;
    }
//</editor-fold>//GEN-END:|292-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: itemSaveLog ">//GEN-BEGIN:|294-getter|0|294-preInit
    /**
     * Returns an initiliazed instance of itemSaveLog component.
     * @return the initialized component instance
     */
    public Command getItemSaveLog() {
        if (itemSaveLog==null){//GEN-END:|294-getter|0|294-preInit
            // Insert pre-init code here
            itemSaveLog=new Command(LangHolder.getString(Lang.savefile), Command.ITEM, 3);//GEN-LINE:|294-getter|1|294-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|294-getter|2|
        return itemSaveLog;
    }
//</editor-fold>//GEN-END:|294-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: itemSaveLogWeb ">//GEN-BEGIN:|296-getter|0|296-preInit
    /**
     * Returns an initiliazed instance of itemSaveLogWeb component.
     * @return the initialized component instance
     */
    public Command getItemSaveLogWeb() {
        if (itemSaveLogWeb==null){//GEN-END:|296-getter|0|296-preInit
            // Insert pre-init code here
            itemSaveLogWeb=new Command(LangHolder.getString(Lang.savesite), Command.ITEM, 4);//GEN-LINE:|296-getter|1|296-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|296-getter|2|
        return itemSaveLogWeb;
    }
//</editor-fold>//GEN-END:|296-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: itemBrowseMap ">//GEN-BEGIN:|307-getter|0|307-preInit
    /**
     * Returns an initiliazed instance of itemBrowseMap component.
     * @return the initialized component instance
     */
    public Command getItemBrowseMap() {
        if (itemBrowseMap==null){//GEN-END:|307-getter|0|307-preInit
            // Insert pre-init code here
            itemBrowseMap=new Command(LangHolder.getString(Lang.browse), Command.ITEM, 2);//GEN-LINE:|307-getter|1|307-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|307-getter|2|
        return itemBrowseMap;
    }
//</editor-fold>//GEN-END:|307-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: itemCreate ">//GEN-BEGIN:|330-getter|0|330-preInit
    /**
     * Returns an initiliazed instance of itemCreate component.
     * @return the initialized component instance
     */
    public Command getItemCreate() {
        if (itemCreate==null){//GEN-END:|330-getter|0|330-preInit
            // Insert pre-init code here
            itemCreate=new Command(LangHolder.getString(Lang.create), Command.ITEM, 4);//GEN-LINE:|330-getter|1|330-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|330-getter|2|
        return itemCreate;
    }
//</editor-fold>//GEN-END:|330-getter|2|

    /**
     * Returns an initiliazed instance of itemSelect1 component.
     * @return the initialized component instance
     */
    public Command getItemSelect1() {
        if (itemSelect1==null){//GEN-END:|331-getter|0|331-preInit
            // Insert pre-init code here
            itemSelect1=new Command(LangHolder.getString(Lang.select), Command.ITEM, 1);//GEN-LINE:|331-getter|1|331-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|331-getter|2|
        return itemSelect1;
    }
//</editor-fold>//GEN-END:|331-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: itemCreateRoute ">//GEN-BEGIN:|338-getter|0|338-preInit
    /**
     * Returns an initiliazed instance of itemCreateRoute component.
     * @return the initialized component instance
     */
    public Command getItemCreateRoute() {
        if (itemCreateRoute==null){//GEN-END:|338-getter|0|338-preInit
            // Insert pre-init code here
            itemCreateRoute=new Command(LangHolder.getString(Lang.create), Command.ITEM, 4);//GEN-LINE:|338-getter|1|338-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|338-getter|2|
        return itemCreateRoute;
    }
//</editor-fold>//GEN-END:|338-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: itemRouteDelete ">//GEN-BEGIN:|340-getter|0|340-preInit
    /**
     * Returns an initiliazed instance of itemRouteDelete component.
     * @return the initialized component instance
     */
    public Command getItemRouteDelete() {
        if (itemRouteDelete==null){//GEN-END:|340-getter|0|340-preInit
            // Insert pre-init code here
            itemRouteDelete=new Command(LangHolder.getString(Lang.delete), Command.ITEM, 3);//GEN-LINE:|340-getter|1|340-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|340-getter|2|
        return itemRouteDelete;
    }
//</editor-fold>//GEN-END:|340-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: itemLoadMap ">//GEN-BEGIN:|343-getter|0|343-preInit
    /**
     * Returns an initiliazed instance of itemLoadMap component.
     * @return the initialized component instance
     */
    public Command getItemLoadMap() {
        if (itemLoadMap==null){//GEN-END:|343-getter|0|343-preInit
            // Insert pre-init code here
            itemLoadMap=new Command(LangHolder.getString(Lang.load), Command.ITEM, 1);//GEN-LINE:|343-getter|1|343-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|343-getter|2|
        return itemLoadMap;
    }
//</editor-fold>//GEN-END:|343-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: itemSearch ">//GEN-BEGIN:|370-getter|0|370-preInit
    /**
     * Returns an initiliazed instance of itemSearch component.
     * @return the initialized component instance
     */
    public Command getItemSearch() {
        if (itemSearch==null){//GEN-END:|370-getter|0|370-preInit
            // Insert pre-init code here
            itemSearch=new Command(LangHolder.getString(Lang.search), Command.ITEM, 1);//GEN-LINE:|370-getter|1|370-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|370-getter|2|
        return itemSearch;
    }
//</editor-fold>//GEN-END:|370-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: itemResults ">//GEN-BEGIN:|372-getter|0|372-preInit
    /**
     * Returns an initiliazed instance of itemResults component.
     * @return the initialized component instance
     */
    public Command getItemResults() {
        if (itemResults==null){//GEN-END:|372-getter|0|372-preInit
            // Insert pre-init code here
            itemResults=new Command(LangHolder.getString(Lang.results), Command.ITEM, 2);//GEN-LINE:|372-getter|1|372-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|372-getter|2|
        return itemResults;
    }
//</editor-fold>//GEN-END:|372-getter|2|

    public Command getItemTest() {
        if (itemTest==null){
            // Insert pre-init code here
            itemTest=new Command("Test", Command.ITEM, 3);
            // Insert post-init code here
        }
        return itemTest;
    }

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: item1Send ">//GEN-BEGIN:|390-getter|0|390-preInit
    /**
     * Returns an initiliazed instance of item1Send component.
     * @return the initialized component instance
     */
    public Command getItem1Send() {
        if (item1Send==null){//GEN-END:|390-getter|0|390-preInit
            // Insert pre-init code here
            item1Send=new Command(LangHolder.getString(Lang.send), Command.ITEM, 1);//GEN-LINE:|390-getter|1|390-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|390-getter|2|
        return item1Send;
    }
//</editor-fold>//GEN-END:|390-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: itemExport ">//GEN-BEGIN:|450-getter|0|450-preInit
    /**
     * Returns an initiliazed instance of itemExport component.
     * @return the initialized component instance
     */
    public Command getItemExport() {
        if (itemExport==null){//GEN-END:|450-getter|0|450-preInit
            // Insert pre-init code here
            itemExport=new Command(LangHolder.getString(Lang.export), Command.ITEM, 4);//GEN-LINE:|450-getter|1|450-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|450-getter|2|
        return itemExport;
    }
//</editor-fold>//GEN-END:|450-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: itemLoadTrack ">//GEN-BEGIN:|452-getter|0|452-preInit
    /**
     * Returns an initiliazed instance of itemLoadTrack component.
     * @return the initialized component instance
     */
    public Command getItemLoadTrack() {
        if (itemLoadTrack==null){//GEN-END:|452-getter|0|452-preInit
            // Insert pre-init code here
            itemLoadTrack=new Command(LangHolder.getString(Lang.load), Command.ITEM, 6);//GEN-LINE:|452-getter|1|452-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|452-getter|2|
        return itemLoadTrack;
    }
//</editor-fold>//GEN-END:|452-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: itemBrowseFile ">//GEN-BEGIN:|455-getter|0|455-preInit
    /**
     * Returns an initiliazed instance of itemBrowseFile component.
     * @return the initialized component instance
     */
    public Command getItemBrowseFile() {
        if (itemBrowseFile==null){//GEN-END:|455-getter|0|455-preInit
            // Insert pre-init code here
            itemBrowseFile=new Command(LangHolder.getString(Lang.browse)+' '+LangHolder.getString(Lang.file), Command.ITEM, 5);//GEN-LINE:|455-getter|1|455-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|455-getter|2|
        return itemBrowseFile;
    }
//</editor-fold>//GEN-END:|455-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: itemClearMap ">//GEN-BEGIN:|457-getter|0|457-preInit
    /**
     * Returns an initiliazed instance of itemClearMap component.
     * @return the initialized component instance
     */
    public Command getItemClearMap() {
        if (itemClearMap==null){//GEN-END:|457-getter|0|457-preInit
            // Insert pre-init code here
            itemClearMap=new Command("ClearMap", Command.ITEM, 7);//GEN-LINE:|457-getter|1|457-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|457-getter|2|
        return itemClearMap;
    }
//</editor-fold>//GEN-END:|457-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: itemMerge ">//GEN-BEGIN:|472-getter|0|472-preInit
    /**
     * Returns an initiliazed instance of itemMerge component.
     * @return the initialized component instance
     */
    public Command getItemMerge() {
        if (itemMerge==null){//GEN-END:|472-getter|0|472-preInit
            // Insert pre-init code here
            itemMerge=new Command(LangHolder.getString(Lang.merge), Command.ITEM, 5);//GEN-LINE:|472-getter|1|472-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|472-getter|2|
        return itemMerge;
    }
//</editor-fold>//GEN-END:|472-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: itemWPDelete ">//GEN-BEGIN:|474-getter|0|474-preInit
    /**
     * Returns an initiliazed instance of itemWPDelete component.
     * @return the initialized component instance
     */
    public Command getItemWPDelete() {
        if (itemWPDelete==null){//GEN-END:|474-getter|0|474-preInit
            // Insert pre-init code here
            itemWPDelete=new Command(LangHolder.getString(Lang.delete), Command.ITEM, 3);//GEN-LINE:|474-getter|1|474-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|474-getter|2|
        return itemWPDelete;
    }
//</editor-fold>//GEN-END:|474-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: itemTrackDelete ">//GEN-BEGIN:|507-getter|0|507-preInit
    /**
     * Returns an initiliazed instance of itemTrackDelete component.
     * @return the initialized component instance
     */
    public Command getItemTrackDelete() {
        if (itemTrackDelete==null){//GEN-END:|507-getter|0|507-preInit
            // Insert pre-init code here
            itemTrackDelete=new Command(LangHolder.getString(Lang.delete), Command.ITEM, 3);//GEN-LINE:|507-getter|1|507-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|507-getter|2|
        return itemTrackDelete;
    }
//</editor-fold>//GEN-END:|507-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: itemNext1 ">//GEN-BEGIN:|512-getter|0|512-preInit
    /**
     * Returns an initiliazed instance of itemNext1 component.
     * @return the initialized component instance
     */
    public Command getItemNext1() {
        if (itemNext1==null){//GEN-END:|512-getter|0|512-preInit
            // Insert pre-init code here
            itemNext1=new Command(LangHolder.getString(Lang.next), Command.ITEM, 1);//GEN-LINE:|512-getter|1|512-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|512-getter|2|
        return itemNext1;
    }
//</editor-fold>//GEN-END:|512-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: item1Save ">//GEN-BEGIN:|527-getter|0|527-preInit
    /**
     * Returns an initiliazed instance of item1Save component.
     * @return the initialized component instance
     */
    public Command getItem1Save() {
        if (item1Save==null){//GEN-END:|527-getter|0|527-preInit
            // Insert pre-init code here
            item1Save=new Command(LangHolder.getString(Lang.save), Command.ITEM, 1);//GEN-LINE:|527-getter|1|527-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|527-getter|2|
        return item1Save;
    }
//</editor-fold>//GEN-END:|527-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: itemComLoadMap ">//GEN-BEGIN:|560-getter|0|560-preInit
    /**
     * Returns an initiliazed instance of itemComLoadMap component.
     * @return the initialized component instance
     */
    public Command getItemComLoadMap() {
        if (itemComLoadMap==null){//GEN-END:|560-getter|0|560-preInit
            // Insert pre-init code here
            itemComLoadMap=new Command(LangHolder.getString(Lang.addmap), Command.ITEM, 3);//GEN-LINE:|560-getter|1|560-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|560-getter|2|
        return itemComLoadMap;
    }
//</editor-fold>//GEN-END:|560-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: itemSelBT ">//GEN-BEGIN:|575-getter|0|575-preInit
    /**
     * Returns an initiliazed instance of itemSelBT component.
     * @return the initialized component instance
     */
    public Command getItemSelBT() {
        if (itemSelBT==null){//GEN-END:|575-getter|0|575-preInit
            // Insert pre-init code here
            itemSelBT=new Command(LangHolder.getString(Lang.connect), Command.ITEM, 1);//GEN-LINE:|575-getter|1|575-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|575-getter|2|
        return itemSelBT;
    }
//</editor-fold>//GEN-END:|575-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: imageWPTs ">//GEN-BEGIN:|480-getter|0|480-preInit
    /**
     * Returns an initiliazed instance of imageWPTs component.
     * @return the initialized component instance
     */
    public Image getImageWPTs() {
        if (imageWPTs==null){//GEN-END:|480-getter|0|480-preInit
            // Insert pre-init code here
            try {//GEN-BEGIN:|480-getter|1|480-@java.io.IOException
                imageWPTs=Image.createImage("/img/wpts.png");
            } catch (java.io.IOException e) {//GEN-END:|480-getter|1|480-@java.io.IOException
                e.printStackTrace();
            }//GEN-LINE:|480-getter|2|480-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|480-getter|3|
        return imageWPTs;
    }
//</editor-fold>//GEN-END:|480-getter|3|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: imageRoutes ">//GEN-BEGIN:|482-getter|0|482-preInit
    /**
     * Returns an initiliazed instance of imageRoutes component.
     * @return the initialized component instance
     */
    public Image getImageRoutes() {
        if (imageRoutes==null){//GEN-END:|482-getter|0|482-preInit
            // Insert pre-init code here
            try {//GEN-BEGIN:|482-getter|1|482-@java.io.IOException
                imageRoutes=Image.createImage("/img/routes.png");
            } catch (java.io.IOException e) {//GEN-END:|482-getter|1|482-@java.io.IOException
                e.printStackTrace();
            }//GEN-LINE:|482-getter|2|482-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|482-getter|3|
        return imageRoutes;
    }
//</editor-fold>//GEN-END:|482-getter|3|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: imageTracks ">//GEN-BEGIN:|484-getter|0|484-preInit
    /**
     * Returns an initiliazed instance of imageTracks component.
     * @return the initialized component instance
     */
    public Image getImageTracks() {
        if (imageTracks==null){//GEN-END:|484-getter|0|484-preInit
            // Insert pre-init code here
            try {//GEN-BEGIN:|484-getter|1|484-@java.io.IOException
                imageTracks=Image.createImage("/img/tracks.png");
            } catch (java.io.IOException e) {//GEN-END:|484-getter|1|484-@java.io.IOException
                e.printStackTrace();
            }//GEN-LINE:|484-getter|2|484-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|484-getter|3|
        return imageTracks;
    }
//</editor-fold>//GEN-END:|484-getter|3|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: imageGEGreen ">//GEN-BEGIN:|486-getter|0|486-preInit
    /**
     * Returns an initiliazed instance of imageGEGreen component.
     * @return the initialized component instance
     */
    public Image getImageGEGreen() {
        if (imageGEGreen==null){//GEN-END:|486-getter|0|486-preInit
            // Insert pre-init code here
            try {//GEN-BEGIN:|486-getter|1|486-@java.io.IOException
                imageGEGreen=Image.createImage("/img/gegreen.png");
            } catch (java.io.IOException e) {//GEN-END:|486-getter|1|486-@java.io.IOException
                e.printStackTrace();
            }//GEN-LINE:|486-getter|2|486-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|486-getter|3|
        return imageGEGreen;
    }
//</editor-fold>//GEN-END:|486-getter|3|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: imageMarks ">//GEN-BEGIN:|488-getter|0|488-preInit
    /**
     * Returns an initiliazed instance of imageMarks component.
     * @return the initialized component instance
     */
    public Image getImageMarks() {
        if (imageMarks==null){//GEN-END:|488-getter|0|488-preInit
            // Insert pre-init code here
            try {//GEN-BEGIN:|488-getter|1|488-@java.io.IOException
                imageMarks=Image.createImage("/img/marks.png");
            } catch (java.io.IOException e) {//GEN-END:|488-getter|1|488-@java.io.IOException
                e.printStackTrace();
            }//GEN-LINE:|488-getter|2|488-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|488-getter|3|
        return imageMarks;
    }
//</editor-fold>//GEN-END:|488-getter|3|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: imageMap ">//GEN-BEGIN:|524-getter|0|524-preInit
    /**
     * Returns an initiliazed instance of imageMap component.
     * @return the initialized component instance
     */
    public Image getImageMap() {
        if (imageMap==null){//GEN-END:|524-getter|0|524-preInit
            // Insert pre-init code here
            try {//GEN-BEGIN:|524-getter|1|524-@java.io.IOException
                imageMap=Image.createImage("/img/map.png");
            } catch (java.io.IOException e) {//GEN-END:|524-getter|1|524-@java.io.IOException
                e.printStackTrace();
            }//GEN-LINE:|524-getter|2|524-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|524-getter|3|
        return imageMap;
    }
//</editor-fold>//GEN-END:|524-getter|3|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: imageMapE ">//GEN-BEGIN:|568-getter|0|568-preInit
    /**
     * Returns an initiliazed instance of imageMapE component.
     * @return the initialized component instance
     */
    public Image getImageMapE() {
        if (imageMapE==null){//GEN-END:|568-getter|0|568-preInit
            // Insert pre-init code here
            try {//GEN-BEGIN:|568-getter|1|568-@java.io.IOException
                imageMapE=Image.createImage("/img/mape.png");
            } catch (java.io.IOException e) {//GEN-END:|568-getter|1|568-@java.io.IOException
                e.printStackTrace();
            }//GEN-LINE:|568-getter|2|568-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|568-getter|3|
        return imageMapE;
    }
//</editor-fold>//GEN-END:|568-getter|3|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: imageFold ">//GEN-BEGIN:|569-getter|0|569-preInit
    /**
     * Returns an initiliazed instance of imageFold component.
     * @return the initialized component instance
     */
    public Image getImageFold() {
        if (imageFold==null){//GEN-END:|569-getter|0|569-preInit
            // Insert pre-init code here
            try {//GEN-BEGIN:|569-getter|1|569-@java.io.IOException
                imageFold=Image.createImage("/img/folders.png");
            } catch (java.io.IOException e) {//GEN-END:|569-getter|1|569-@java.io.IOException
                e.printStackTrace();
            }//GEN-LINE:|569-getter|2|569-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|569-getter|3|
        return imageFold;
    }
//</editor-fold>//GEN-END:|569-getter|3|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: imageGEGray ">//GEN-BEGIN:|586-getter|0|586-preInit
    /**
     * Returns an initiliazed instance of imageGEGray component.
     * @return the initialized component instance
     */
    public Image getImageGEGray() {
        if (imageGEGray==null){//GEN-END:|586-getter|0|586-preInit
            // Insert pre-init code here
            try {//GEN-BEGIN:|586-getter|1|586-@java.io.IOException
                imageGEGray=Image.createImage("/img/gegray.png");
            } catch (java.io.IOException e) {//GEN-END:|586-getter|1|586-@java.io.IOException
                e.printStackTrace();
            }//GEN-LINE:|586-getter|2|586-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|586-getter|3|
        return imageGEGray;
    }
//</editor-fold>//GEN-END:|586-getter|3|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: imageGEYel ">//GEN-BEGIN:|587-getter|0|587-preInit
    /**
     * Returns an initiliazed instance of imageGEYel component.
     * @return the initialized component instance
     */
    public Image getImageGEYel() {
        if (imageGEYel==null){//GEN-END:|587-getter|0|587-preInit
            // Insert pre-init code here
            try {//GEN-BEGIN:|587-getter|1|587-@java.io.IOException
                imageGEYel=Image.createImage("/img/geyel.png");
            } catch (java.io.IOException e) {//GEN-END:|587-getter|1|587-@java.io.IOException
                e.printStackTrace();
            }//GEN-LINE:|587-getter|2|587-postInit
            // Insert post-init code here
        }//GEN-BEGIN:|587-getter|3|
        return imageGEYel;
    }
//</editor-fold>//GEN-END:|587-getter|3|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceCacheOpt ">//GEN-BEGIN:|657-getter|0|657-preInit
    /**
     * Returns an initiliazed instance of choiceCacheOpt component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceCacheOpt() {
        if (choiceCacheOpt==null){//GEN-END:|657-getter|0|657-preInit
            // write pre-init user code here
            choiceCacheOpt=new ChoiceGroup(LangHolder.getString(Lang.options), Choice.MULTIPLE);//GEN-BEGIN:|657-getter|1|657-postInit
            choiceCacheOpt.append(LangHolder.getString(Lang.usefilecache), null);
            choiceCacheOpt.append("GMT", null);
            choiceCacheOpt.setFitPolicy(Choice.TEXT_WRAP_DEFAULT);
//choiceCacheOpt.setSelectedFlags (new boolean[] { false });
//choiceCacheOpt.setFont (0, null);//GEN-END:|657-getter|1|657-postInit
            // write post-init user code here
        }//GEN-BEGIN:|657-getter|2|
        return choiceCacheOpt;
    }
//</editor-fold>//GEN-END:|657-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: textFileCachePath ">//GEN-BEGIN:|659-getter|0|659-preInit
    /**
     * Returns an initiliazed instance of textFileCachePath component.
     * @return the initialized component instance
     */
    public TextField getTextFileCachePath() {
        if (textFileCachePath==null){//GEN-END:|659-getter|0|659-preInit
            // write pre-init user code here
            textFileCachePath=new TextField(LangHolder.getString(Lang.filecachepath), null, 128, TextField.ANY);//GEN-BEGIN:|659-getter|1|659-postInit
            textFileCachePath.addCommand(getItemBrowseMap());
            textFileCachePath.setItemCommandListener(this);//GEN-END:|659-getter|1|659-postInit
            // write post-init user code here
        }//GEN-BEGIN:|659-getter|2|
        return textFileCachePath;
    }
//</editor-fold>//GEN-END:|659-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: itemCommand ">//GEN-BEGIN:|661-getter|0|661-preInit
    /**
     * Returns an initiliazed instance of itemCommand component.
     * @return the initialized component instance
     */
    public Command getItemCommand() {
        if (itemCommand==null){//GEN-END:|661-getter|0|661-preInit
            // write pre-init user code here
            itemCommand=new Command("Item", Command.ITEM, 0);//GEN-LINE:|661-getter|1|661-postInit
            // write post-init user code here
        }//GEN-BEGIN:|661-getter|2|
        return itemCommand;
    }
//</editor-fold>//GEN-END:|661-getter|2|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: imageTrS ">//GEN-BEGIN:|671-getter|0|671-preInit
    /**
     * Returns an initiliazed instance of imageTrS component.
     * @return the initialized component instance
     */
    public Image getImageTrS() {
        if (imageTrS==null){//GEN-END:|671-getter|0|671-preInit
            // write pre-init user code here
            try {//GEN-BEGIN:|671-getter|1|671-@java.io.IOException
                imageTrS=Image.createImage("/img/trackss.png");
            } catch (java.io.IOException e) {//GEN-END:|671-getter|1|671-@java.io.IOException
                e.printStackTrace();
            }//GEN-LINE:|671-getter|2|671-postInit
            // write post-init user code here
        }//GEN-BEGIN:|671-getter|3|
        return imageTrS;
    }
//</editor-fold>//GEN-END:|671-getter|3|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: imageTrO ">//GEN-BEGIN:|672-getter|0|672-preInit
    /**
     * Returns an initiliazed instance of imageTrO component.
     * @return the initialized component instance
     */
    public Image getImageTrO() {
        if (imageTrO==null){//GEN-END:|672-getter|0|672-preInit
            // write pre-init user code here
            try {//GEN-BEGIN:|672-getter|1|672-@java.io.IOException
                imageTrO=Image.createImage("/img/trackso.png");
            } catch (java.io.IOException e) {//GEN-END:|672-getter|1|672-@java.io.IOException
                e.printStackTrace();
            }//GEN-LINE:|672-getter|2|672-postInit
            // write post-init user code here
        }//GEN-BEGIN:|672-getter|3|
        return imageTrO;
    }
//</editor-fold>//GEN-END:|672-getter|3|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: imageTrW ">//GEN-BEGIN:|673-getter|0|673-preInit
    /**
     * Returns an initiliazed instance of imageTrW component.
     * @return the initialized component instance
     */
    public Image getImageTrW() {
        if (imageTrW==null){//GEN-END:|673-getter|0|673-preInit
            // write pre-init user code here
            try {//GEN-BEGIN:|673-getter|1|673-@java.io.IOException
                imageTrW=Image.createImage("/img/tracksw.png");
            } catch (java.io.IOException e) {//GEN-END:|673-getter|1|673-@java.io.IOException
                e.printStackTrace();
            }//GEN-LINE:|673-getter|2|673-postInit
            // write post-init user code here
        }//GEN-BEGIN:|673-getter|3|
        return imageTrW;
    }
//</editor-fold>//GEN-END:|673-getter|3|

//<editor-fold defaultstate="collapsed" desc=" Generated Getter: textOSMLogin ">//GEN-BEGIN:|674-getter|0|674-preInit
    /**
     * Returns an initiliazed instance of textOSMLogin component.
     * @return the initialized component instance
     */
    public TextField getTextOSMLogin() {
        if (textOSMLogin==null){//GEN-END:|674-getter|0|674-preInit
            // write pre-init user code here
            textOSMLogin=new TextField("OSM Login", null, 32, TextField.EMAILADDR|TextField.NON_PREDICTIVE);//GEN-LINE:|674-getter|1|674-postInit
            // write post-init user code here
        }//GEN-BEGIN:|674-getter|2|
        return textOSMLogin;
    }
//</editor-fold>//GEN-END:|674-getter|2|

    public TextField getTextOSMPass() {
        if (textOSMPass==null){
            textOSMPass=new TextField("OSM Password", null, 32, TextField.ANY|TextField.PASSWORD|TextField.NON_PREDICTIVE);//GEN-LINE:|675-getter|1|675-postInit
        }
        return textOSMPass;
    }
    public TextField getTextOSMURL() {
        if (textOSMURL==null){
            textOSMURL=new TextField("OsmRender URL", null, 140, TextField.URL|TextField.NON_PREDICTIVE);//GEN-LINE:|676-getter|1|676-postInit
        }
        return textOSMURL;
    }

    public TextField getOnlineUrlSUR() {
        if (textOnlineUrlSUR==null){
            textOnlineUrlSUR=new TextField("Online URL 1", null, 140, TextField.URL|TextField.NON_PREDICTIVE);//GEN-LINE:|676-getter|1|676-postInit
        }
        return textOnlineUrlSUR;
    }

    public TextField getOnlineUrlMAP() {
        if (textOnlineUrlMAP==null){
            textOnlineUrlMAP=new TextField("Online URL 2", null, 140, TextField.URL|TextField.NON_PREDICTIVE);//GEN-LINE:|676-getter|1|676-postInit
        }
        return textOnlineUrlMAP;
    }

    public Display getDisplay() {
        return Display.getDisplay(mM);
    }

    public void exitMIDlet() {
        getDisplay().setCurrent(null);
        destroyApp(true);
        mM.notifyDestroyed();
    }
    public static MapForms mm;
    public static String smsPort="24789";

    /** Creates a new instance of HelloMidlet */
    public MapForms() {
        mm=this;
        try {
            smsPort=mM.getAppProperty("SMS-Port");
        } catch (Throwable t) {
        }
    }

    public void showFlickr() {
        //#if Flickr
//#         FlickrList.showFlickr();
//#         MapCanvas.map.clearAllTiles();
//#         clearForms();
        //#else
     showAbsent();
        //#endif
    }

    private void showSendRoute(byte saveKind) {
        System.gc();
        MapForms.saveKind=saveKind;
        // Insert pre-action code here
        formSaveTrack=null;
        getFormSaveTrack();
        choiceCP_S.setSelectedIndex(RMSOption.routeCP, true);
        choiceOX_S.setSelectedIndex(RMSOption.routeFormat, true);
        formSaveTrack.delete(3);
        formSaveTrack.delete(0);
        formSaveTrack.removeCommand(itemBrowseMap);
        getDisplay().setCurrent(formSaveTrack);
    }

    private void showKMLList() {
        getListKML();
        listKML.deleteAll();
        MapCanvas.map.rmss.loadKMLRoutes();
        for (int i=0; i<MapCanvas.map.kmlRoutes.size(); i++) {
            KMLMapRoute mr=(KMLMapRoute) MapCanvas.map.kmlRoutes.elementAt(i);
            if (!mr.active) {
                listKML.append(mr.name, getImageGEGray());
            } else if (mr.notReady) {
                listKML.append(mr.name, getImageGEYel());
            } else {
                listKML.append(mr.name, getImageGEGreen());
            }
        }
        MapCanvas.setCurrent(listKML);
    }

//  public static void playConnLostSound() {
//    if (!RMSOption.soundOn) return;
//    //if (!canPlay()) return; пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
//    try{new MapSound(MapSound.CONNLOSTSOUND);}catch(Throwable t){}
//  }
//  public static void playSatFoundSound() {
//    if (!RMSOption.soundOn) return;
//    if (!RMSOption.satSoundStatus) return;
//    if (!canPlay()) return;
//    try{new MapSound(MapSound.SATFOUNDSOUND);}catch(Throwable t){}
//  }
//  public static void playSatLostSound() {
//    if (!RMSOption.soundOn) return;
//    if (!RMSOption.satSoundStatus) return;
//    if (!canPlay()) return;
//    try{new MapSound(MapSound.SATLOSTSOUND);}catch(Throwable t){}
//  }
//  public static void playGPSFoundSound() {
//    if (!RMSOption.soundOn) return;
//    if (!canPlay()) return;
//    try{new MapSound(MapSound.GPSFOUNDSOUND);}catch(Throwable t){}
//  }
//  public static void playGPSAddTrackPoint() {
//    if (!RMSOption.soundOn) return;
//    if (!RMSOption.getBoolOpt(RMSOption.BL_ADDTRACKPOINTSOUND_ON)) return;
//    if (!canPlay()) return;
//    try{MapSound.playTone(MapSound.GPSADDTRACKPOINT);}catch(Throwable t){}
//  }
//  public static void playNCSound() {
//    if (!RMSOption.soundOn) return;
//    if (!canPlay()) return;
//    try{new MapSound(MapSound.NEWCONNECTSOUND);}catch(Throwable t){}
//  }
//  public static void playNewMesSound() {
//    if (!RMSOption.soundOn) return;
//    if (!canPlay()) return;
//    try{new MapSound(MapSound.NEWMESSAGESOUND);}catch(Throwable t){}
//  }
//  public static void playWarnSpeedSound() {
//    if (!RMSOption.soundOn) return;
//    if (!canPlay()) return;
//    try{MapSound.playTone(MapSound.GPSWARNMAXSPEED);}catch(Throwable t){}
//  }
    public void getGeoCache() {
        //#if GeoCaching
//#if SE_K750_E_BASEDEV
//#else
//# 
//#     if (MapCanvas.map.gpsActive()||MapCanvas.map.secretGeoCombination()){
//#       if (GPSReader.NUM_SATELITES>0||MapCanvas.map.secretGeoCombination()) {
//#endif
//#         clearForms();
//#         getListMore();
//#         MapCanvas.setCurrent(new ProgressForm(LangHolder.getString(Lang.loading), "GeoCaching.ru", new GeoCacheLoad(MapCanvas.map), listMore));
//# 
//#         //(new Thread(pointEditForm)).start();
//# //        MapCanvas.setCurrent(new Alert("GeoCaching.ru", "пїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ", null, AlertType.INFO),MapCanvas.map);
//#if SE_K750_E_BASEDEV
//#else
//#       } else MapCanvas.showmsg("GeoCaching.ru", "пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ", AlertType.WARNING,getListMore());
//#     } else MapCanvas.showmsg("GeoCaching.ru", "пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ GPS пїЅпїЅпїЅпїЅпїЅпїЅпїЅ", AlertType.WARNING,getListMore());
//#endif
//#else
    showAbsent();
//#endif
    }

    public static void showAbsent() {
        MapCanvas.showmsg("Absent in light version", "Information", AlertType.INFO, MapCanvas.display.getCurrent());
    }

    private String getMarkShowName() {
        if (RMSOption.showMarks) {
            return LangHolder.getString(Lang.hide);
        } else {
            return LangHolder.getString(Lang.show);
        }
    }

    private void sendCoordSMS(String tel, int type) {
        Alert sendingMessageAlert=new Alert("SMS", LangHolder.getString(Lang.sending), null, AlertType.INFO);
        MapCanvas.ss=new SMSSender(getFormTelInput(), sendingMessageAlert, tel, type);
        MapCanvas.ss.promptAndSend(smsPort);
    }

    public void loadRouteURL() {
        try {
            RMSOption.lastURL=getTextRouteURL().getString();
            RMSOption.routeCP=(byte) choiceCP.getSelectedIndex();
            int i=choiceImpFormat.getSelectedIndex();
            byte format=(i==0)?MapRouteLoader.FORMATOZI:(i==1)?MapRouteLoader.FORMATGPX:MapRouteLoader.FORMATLOC;
            if (selectKind==MapRoute.WAYPOINTSKIND) {
                RMSOption.setByteOpt(RMSOption.BO_IMPFORMATWP, format);
            } else if (selectKind==MapRoute.TRACKKIND) {
                RMSOption.setByteOpt(RMSOption.BO_IMPFORMATTR, format);
            } else if (selectKind==MapRoute.ROUTEKIND) {
                RMSOption.setByteOpt(RMSOption.BO_IMPFORMATRT, format);
            }

            if (getChoiceRouteURL().getSelectedIndex()==0){
                MapCanvas.setCurrent(new ProgressForm(LangHolder.getString(Lang.loading), LangHolder.getString(Lang.waitpls),
                  new HTTPMapRouteLoader(getTextRouteURL().getString(), selectKind, RMSOption.routeCP, format),
                  formRouteURL));
            } else if (getChoiceRouteURL().getSelectedIndex()==1){
                MapCanvas.setCurrent(new ProgressForm(LangHolder.getString(Lang.loading), LangHolder.getString(Lang.waitpls),
                  new FileMapRouteLoader(getTextRouteURL().getString(), selectKind, RMSOption.routeCP, format),
                  formRouteURL));
            }
        } catch (Throwable e) {
            MapCanvas.showmsg("ERROR", e.toString(), AlertType.ERROR, MapCanvas.map);
        }
    }

    public void deleteRoute(byte kind) {
        if ((getListRoute().getSelectedIndex()!=0)&&(getListRoute().getSelectedIndex()!=getListRoute().size()-1)){
            MapCanvas.map.rmss.deleteRoute(getListRoute().getSelectedIndex()-1, kind);
            MapCanvas.showmsg(LangHolder.getString(Lang.routes), LangHolder.getString(Lang.deleted), AlertType.INFO, getListRoute());
            showSelectRouteForm(false);
        }
    }

    public void deleteWP(byte kind) {
        if ((getListWP().getSelectedIndex()!=0)&&(getListWP().getSelectedIndex()!=getListWP().size()-1)){
            MapCanvas.map.rmss.deleteRoute(getListWP().getSelectedIndex()-1, kind);
            MapCanvas.showmsg(LangHolder.getString(Lang.waypoints), LangHolder.getString(Lang.deleted), AlertType.INFO, getListWP());
            showSelectWPForm(false);
        }
    }

    public void deleteTrack(byte kind) {
        if ((getListTrack().getSelectedIndex()!=0)&&(getListTrack().getSelectedIndex()!=getListTrack().size()-1)){
            MapCanvas.map.rmss.deleteRoute(getListTrack().getSelectedIndex()-1, kind);
            MapCanvas.showmsg(LangHolder.getString(Lang.tracks), LangHolder.getString(Lang.deleted), AlertType.INFO, getListTrack());
            showSelectTrackForm(false);
        }
    }

    private void editRoute(byte kind, List routeList) {
        System.gc();
        if ((routeList.getSelectedIndex()!=0)&&(routeList.getSelectedIndex()!=routeList.size()-1)){
            MapCanvas.map.activeRoute=null;
            MapCanvas.setCurrent(new RouteEditList(routeList, MapCanvas.map.rmss.loadRoute(routeList.getSelectedIndex()-1, kind)));
        }
    }

    private void createRoute(byte kind) {
        MapRoute mr=MapRoute.createRoute(kind);
        MapCanvas.map.rmss.saveRoute(mr);
        refreshRouteForm(kind);
    }

    private void cloneRoute(byte kind, List routeList, boolean invert) {
        MapRoute mrs=MapCanvas.map.rmss.loadRoute(routeList.getSelectedIndex()-1, kind);
        MapRoute mr=mrs.clone(false);
        mrs=null;
        if (invert){
            if (mr.kind==MapRoute.WAYPOINTSKIND) {
                mr.kind=MapRoute.ROUTEKIND;
            } else if (mr.kind==MapRoute.ROUTEKIND) {
                mr.kind=MapRoute.WAYPOINTSKIND;
            } else if (mr.kind==MapRoute.KMLDOCUMENT) {
                mr.kind=MapRoute.ROUTEKIND;
            } else if (mr.kind==MapRoute.TRACKKIND){
                mr.kind=MapRoute.ROUTEKIND;
                mr.autoName();
            }
        }

        if (kind==MapRoute.KMLDOCUMENT) {
            mr.name="CLONED "+mr.name;
        } else if (kind==MapRoute.TRACKKIND) {
            mr.name="CLONED TRACK";
        } else if (mr.kind==MapRoute.WAYPOINTSKIND) {
            mr.name="CLONED WAYPOINTS";
        } else {
            mr.name="CLONED ROUTE";
        }
        MapCanvas.map.rmss.saveRoute(mr);
        refreshRouteForm(mr.kind);
    }

    private void reverseRoute(byte kind, List routeList) {
        MapRoute mrs=MapCanvas.map.rmss.loadRoute(routeList.getSelectedIndex()-1, kind);
        MapRoute mr=mrs.clone(true);
        mrs=null;
        if (kind==MapRoute.TRACKKIND) {
            mr.name="REVERSED TRACK";
        } else if (mr.kind==MapRoute.WAYPOINTSKIND) {
            mr.name="REVERSED WAYPOINTS";
        } else {
            mr.name="REVERSED ROUTE";
        }
        MapCanvas.map.rmss.saveRoute(mr);
        refreshRouteForm(mr.kind);
    }

    private void saveTrack() {

        RouteSend.EXPORTCODEPAGE=(byte) choiceCP_S.getSelectedIndex();
        RMSOption.routeCP=RouteSend.EXPORTCODEPAGE;

        RouteSend.EXPORTFORMAT=(byte) choiceOX_S.getSelectedIndex();
        RMSOption.routeFormat=RouteSend.EXPORTFORMAT;

        RMSOption.lastTrackSavePath=textTrackSavePath.getString();
        List list=null;
        if (selectKind==MapRoute.TRACKKIND) {
            list=getListTrack();
        } else if (selectKind==MapRoute.WAYPOINTSKIND) {
            list=getListWP();
        } else if (selectKind==MapRoute.ROUTEKIND) {
            list=getListRoute();
        }

        try {
            if ((choiceExpCount.getSelectedIndex()==0)&&((list.getSelectedIndex()==0)||(list.getSelectedIndex()==list.size()-1))) //if ((listWP.getSelectedIndex()==0)|| (listWP.getSelectedIndex()==listWP.size()-1)) {
            {
                MapCanvas.setCurrent(getSelectWarningAlert(), formSaveTrack);
                return;
            }


            if (choiceExpCount.getSelectedIndex()==0) {
                MapCanvas.setCurrent(new ProgressForm(LangHolder.getString(Lang.saving), LangHolder.getString(Lang.waitpls),
                  new FileTrackSend(formSaveTrack, textTrackSavePath.getString(), MapCanvas.map.rmss.loadRoute(list.getSelectedIndex()-1, selectKind), false),
                  formSaveTrack));
            } else {
                MapCanvas.setCurrent(new ProgressForm(LangHolder.getString(Lang.saving), LangHolder.getString(Lang.waitpls),
                  new FileTrackSend(formSaveTrack, textTrackSavePath.getString(), selectKind),
                  formSaveTrack));
            }

//      MapCanvas.map.sendRoute(,);
        } catch (Exception e) {
//#mdebug
//#             if (RMSOption.debugEnabled) {
//#                 DebugLog.add2Log("STr:"+e);
//#             }
//#enddebug
        }
    }

    private void mergeWPs() {
        MapRoute bmr=MapCanvas.map.activeRoute;
        MapCanvas.map.rmss.reloadRMSRoutes(MapRoute.WAYPOINTSKIND);
        MapRoute mr=MapRoute.createRoute(MapRoute.WAYPOINTSKIND);
        int rc=MapCanvas.map.rmss.getRouteCount();
        MapRoute mra;
        for (int i=0; i<rc; i++) {
            MapCanvas.map.loadRoute(i, MapRoute.WAYPOINTSKIND);
            mra=MapCanvas.map.activeRoute;
            for (int j=0; j<mra.pts.size(); j++) {
                mr.addMapPoint((MapPoint) mra.pts.elementAt(j));
            }
        }

        MapCanvas.map.rmss.saveRoute(mr);
        MapCanvas.map.activeRoute=bmr;
        refreshRouteForm(MapRoute.WAYPOINTSKIND);
    }

    public void back2RoutesForm(boolean show) {
        if (selectKind==MapRoute.ROUTEKIND) {
            showSelectRouteForm(show);
        } else if (selectKind==MapRoute.WAYPOINTSKIND) {
            showSelectWPForm(show);
        } else if (selectKind==MapRoute.TRACKKIND) {
            showSelectTrackForm(show);
        } else if (selectKind==MapRoute.KMLDOCUMENT) {
            showSelectSKMLForm(show);
        }
    }

    private void deleteAllOk() {
        try {
            MapCanvas.map.rmss.deleteRoutes(selectKind);
        } catch (Throwable t) {
//#mdebug
//#             if (RMSOption.debugEnabled) {
//#                 DebugLog.add2Log("DelAll:"+t);
//#             }
//#enddebug
        }
        back2RoutesForm(true);
    }

    private void showDeleteAllConfirmation() {
        String s=null;
        if (selectKind==MapRoute.ROUTEKIND) {
            s=LangHolder.getString(Lang.routes);
        } else if (selectKind==MapRoute.WAYPOINTSKIND) {
            s=LangHolder.getString(Lang.waypoints);
        } else if (selectKind==MapRoute.TRACKKIND) {
            s=LangHolder.getString(Lang.tracks);
        } else if (selectKind==MapRoute.KMLDOCUMENT) {
            s="KML";
        }
        getFormDeleteAll().setTitle(s);
        stringDelAllQ.setText(LangHolder.getString(Lang.deleteall)+" "+s);
        MapCanvas.setCurrent(getFormDeleteAll());
    }
    private static byte selectKind;
    private static final byte EXPORTKIND=1;
    private static final byte SENDKIND_BT=2;
    private static final byte SENDKIND_IR=3;
    private static byte saveKind;

    public void selectRoute(byte kind) {
        formRouteURL=null;
        textRouteURL=null;
        choiceRouteURL=null;
        choiceCP=null;
        choiceImpFormat=null;

        selectKind=kind;
        int i;
        if (kind==MapRoute.TRACKKIND){
            if (getListTrack().getSelectedIndex()==0){
                getListTrack().deleteAll();
                if (MapCanvas.map.activeTrack==null){
                    MapCanvas.map.startTrack(new MapRoute(MapRoute.TRACKKIND));
                    MapCanvas.map.rmss.saveRoute(MapCanvas.map.activeTrack);
                    MapCanvas.setCurrent(MapCanvas.map);
                } else {
                    if (MapCanvas.map.activeTrack.defTrack){
                        MapCanvas.map.rmss.saveRoute(MapCanvas.map.activeTrack);
                        MapCanvas.map.activeTrack.defTrack=false;
                        MapCanvas.map.activeTrack.rId=-1;
                    }
                    boolean saved=false;
                    if (MapCanvas.map.rmss.saveRoute(MapCanvas.map.activeTrack)){
                        saved=true;
                        MapCanvas.map.endTrack();
                    }
                    showSelectTrackForm(true);
                    if (!saved){
                        MapCanvas.showmsgmodal("Error", "Storing error", AlertType.ERROR, MapCanvas.display.getCurrent());
                    }
                }
            } else if (getListTrack().getSelectedIndex()!=getListTrack().size()-1){
                if (MapCanvas.map.activeTrack!=null){
                    MapCanvas.map.rmss.saveRoute(MapCanvas.map.activeTrack);
                }
                MapCanvas.map.loadRoute(getListTrack().getSelectedIndex()-1, kind);
                getListTrack().deleteAll();
                if (MapCanvas.map.activeTrack!=null){
                    MapCanvas.map.activeTrack.recalcMapLevelScreen(null);
                }
                MapCanvas.showmsg(LangHolder.getString(Lang.opened), LangHolder.getString(Lang.pointscount)+"\n"+String.valueOf(MapCanvas.map.activeTrack.pts.size()), AlertType.INFO, MapCanvas.map);
                clearForms();
            } else {
                showDeleteAllConfirmation();
            }
        } else if (kind==MapRoute.ROUTEKIND){
            if (getListRoute().getSelectedIndex()==0){
                getListRoute().deleteAll();
                if (MapCanvas.map.getActiveRoute()==null){
                    getFormRouteURL();
                    getChoiceCP().setSelectedIndex(RMSOption.routeCP, true);
                    getTextRouteURL().setString(RMSOption.lastURL);
                    getChoiceImpFormat().delete(2);
                    getChoiceImpFormat().setSelectedIndex((RMSOption.getByteOpt(RMSOption.BO_IMPFORMATRT)==MapRouteLoader.FORMATOZI)?0:1, true);
                    MapCanvas.setCurrent(getFormRouteURL());
                } else {
                    MapCanvas.map.rmss.saveRoute(MapCanvas.map.activeRoute);
                    MapCanvas.map.setRoute(null);
                    showSelectRouteForm(true);
                }
            } else if (getListRoute().getSelectedIndex()!=getListRoute().size()-1){
                MapCanvas.map.loadRoute(getListRoute().getSelectedIndex()-1, kind);
                getListRoute().deleteAll();
                MapCanvas.showmsg(LangHolder.getString(Lang.opened), LangHolder.getString(Lang.pointscount)+"\n"+String.valueOf(MapCanvas.map.getActiveRoute().pts.size()), AlertType.INFO, MapCanvas.map);
                clearForms();
            } else {
                showDeleteAllConfirmation();
            }
        } else if (kind==MapRoute.WAYPOINTSKIND){
            if (getListWP().getSelectedIndex()==0){
                getListWP().deleteAll();
                if (MapCanvas.map.getActiveRoute()==null){
                    getFormRouteURL();
                    getChoiceCP().setSelectedIndex(RMSOption.routeCP, true);
                    getTextRouteURL().setString(RMSOption.lastURL);
                    i=RMSOption.getByteOpt(RMSOption.BO_IMPFORMATWP);
                    getChoiceImpFormat().setSelectedIndex((i==MapRouteLoader.FORMATOZI)?0:(i==MapRouteLoader.FORMATGPX)?1:2, true);
                    MapCanvas.setCurrent(getFormRouteURL());
                } else {
                    MapCanvas.map.rmss.saveRoute(MapCanvas.map.activeRoute);
                    MapCanvas.map.setRoute(null);
                    showSelectWPForm(true);
                }
            } else if (getListWP().getSelectedIndex()!=getListWP().size()-1){
                MapCanvas.setCurrent(getListWPMenu());
                MapCanvas.map.loadRoute(getListWP().getSelectedIndex()-1, kind);
                getListWP().deleteAll();
                MapCanvas.showmsg(LangHolder.getString(Lang.opened), LangHolder.getString(Lang.pointscount)+"\n"+String.valueOf(MapCanvas.map.getActiveRoute().pts.size()), AlertType.INFO, MapCanvas.map);
                clearForms();
            } else {
                showDeleteAllConfirmation();
            }
        } else if (kind==MapRoute.KMLDOCUMENT){
            if ((getListSKML().getSelectedIndex()==0)&&(MapCanvas.map.getActiveRoute()!=null)){
                getListSKML().deleteAll();
                if (MapCanvas.map.getActiveRoute()!=null){
                    if (MapCanvas.map.activeRoute.rId>0) {
                        MapCanvas.map.rmss.saveRoute(MapCanvas.map.activeRoute);
                    }
                    MapCanvas.map.setRoute(null);
                    showSelectSKMLForm(true);
                }
            } else if (getListSKML().getSelectedIndex()!=getListSKML().size()-1){
                MapCanvas.setCurrent(getListSKMLMenu());

                MapCanvas.map.loadRoute(getListSKML().getSelectedIndex()-((MapCanvas.map.getActiveRoute()!=null)?1:0), kind);
                getListSKML().deleteAll();
                MapCanvas.showmsg(LangHolder.getString(Lang.opened), LangHolder.getString(Lang.pointscount)+"\n"+String.valueOf(MapCanvas.map.getActiveRoute().pts.size()), AlertType.INFO, MapCanvas.map);
                clearForms();
            } else {
                showDeleteAllConfirmation();
            }
        }
    }

    public void refreshRouteForm(byte kind) {
        if (kind==MapRoute.ROUTEKIND) {
            showSelectRouteForm(false);
        } else if (kind==MapRoute.WAYPOINTSKIND) {
            showSelectWPForm(false);
        } else if (kind==MapRoute.TRACKKIND) {
            showSelectTrackForm(false);
        } else if (kind==MapRoute.KMLDOCUMENT) {
            showSelectSKMLForm(false);
        }
    }

    public void showSelectSKMLForm(boolean show) {
//#debug
//#         byte tP=0;
//#debug
//#         try {
            getListSKML().deleteAll();
            if (MapCanvas.map.getActiveRoute()!=null) //     getListRoute().append(LangHolder.getString(Lang.loadroute),null);
            //   else
            {
                getListSKML().append((MapCanvas.map.activeRoute.kind==MapRoute.WAYPOINTSKIND)?LangHolder.getString(Lang.closewp):LangHolder.getString(Lang.closeroute), null);
            }
//#debug
//#             tP=2;
            MapCanvas.map.rmss.reloadRMSRoutes(MapRoute.KMLDOCUMENT);
//#debug
//#             tP=3;
            int rc=MapCanvas.map.rmss.getRouteCount();
//#debug
//#             tP=4;
            for (int i=0; i<rc; i++) {
                //getListRoute().append(MapCanvas.map.routeNames(i),null);

                if (MapCanvas.map.activeRoute==null) {
                    getListSKML().append(MapCanvas.map.rmss.getRouteName(i), null);
                } else if ((MapCanvas.map.activeRoute.kind==MapRoute.KMLDOCUMENT)&&(MapCanvas.map.activeRoute.rId==MapCanvas.map.rmss.getRouteId(i))) {
                    getListSKML().append(MapCanvas.map.rmss.getRouteName(i), getImageRoutes());
                } else {
                    getListSKML().append(MapCanvas.map.rmss.getRouteName(i), null);
                }

            }
            if (getListSKML().size()>((MapCanvas.map.getActiveRoute()!=null)?1:0)) {
                getListSKML().append(LangHolder.getString(Lang.deleteall), null);
            }
            if (getListSKML().size()>0) {
                getListSKML().setSelectedIndex(0, true);
            }
//#debug
//#             tP=5;
            if (show){
                MapCanvas.setCurrent(getListSKML());
                selectKind=MapRoute.KMLDOCUMENT;
            }
//#mdebug
//#         } catch (Throwable t) {
//#             if (RMSOption.debugEnabled) {
//#                 DebugLog.add2Log("Sel KL:"+String.valueOf(tP)+":"+t);
//#             }
//#         }
//#enddebug
    }

    public void showSelectRouteForm(boolean show) {
//#debug
//#         byte tP=0;
//#debug
//#         try {
            getListRoute().deleteAll();
            if (MapCanvas.map.getActiveRoute()==null) {
                getListRoute().append(LangHolder.getString(Lang.loadroute), null);
            } else {
                getListRoute().append((MapCanvas.map.activeRoute.kind==MapRoute.WAYPOINTSKIND)?LangHolder.getString(Lang.closewp):LangHolder.getString(Lang.closeroute), null);
            }
//#debug
//#             tP=2;
            MapCanvas.map.rmss.reloadRMSRoutes(MapRoute.ROUTEKIND);
//#debug
//#             tP=3;
            int rc=MapCanvas.map.rmss.getRouteCount();
//#debug
//#             tP=4;
            for (int i=0; i<rc; i++) {
                //getListRoute().append(MapCanvas.map.routeNames(i),null);

                if (MapCanvas.map.activeRoute==null) {
                    getListRoute().append(MapCanvas.map.rmss.getRouteName(i), null);
                } else if ((MapCanvas.map.activeRoute.kind==MapRoute.ROUTEKIND)&&(MapCanvas.map.activeRoute.rId==MapCanvas.map.rmss.getRouteId(i))) {
                    getListRoute().append(MapCanvas.map.rmss.getRouteName(i), getImageRoutes());
                } else {
                    getListRoute().append(MapCanvas.map.rmss.getRouteName(i), null);
                }

            }
            if (getListRoute().size()>1) {
                getListRoute().append(LangHolder.getString(Lang.deleteall), null);
            }
            getListRoute().setSelectedIndex(0, true);
//#debug
//#             tP=5;
            if (show){
                MapCanvas.setCurrent(getListRoute());
                selectKind=MapRoute.ROUTEKIND;
            }
//#mdebug
//#         } catch (Throwable t) {
//#             if (RMSOption.debugEnabled) {
//#                 DebugLog.add2Log("Sel RT:"+String.valueOf(tP)+":"+t);
//#             }
//#         }
//#enddebug
    }

    public void showSelectWPForm(boolean show) {
//#debug
//#         byte tP=0;
//#debug
//#         try {
            getListWP().deleteAll();
            if (MapCanvas.map.getActiveRoute()==null) {
                getListWP().append(LangHolder.getString(Lang.loadwp), null);
            } else {
                getListWP().append((MapCanvas.map.activeRoute.kind==MapRoute.WAYPOINTSKIND)?LangHolder.getString(Lang.closewp):LangHolder.getString(Lang.closeroute), null);
            }
//#debug
//#             tP=2;
            MapCanvas.map.rmss.reloadRMSRoutes(MapRoute.WAYPOINTSKIND);
//#debug
//#             tP=3;
            int rc=MapCanvas.map.rmss.getRouteCount();
//#debug
//#             tP=4;
            for (int i=0; i<rc; i++) {
                if (MapCanvas.map.activeRoute==null) {
                    getListWP().append(MapCanvas.map.rmss.getRouteName(i), null);
                } else if ((MapCanvas.map.activeRoute.kind==MapRoute.WAYPOINTSKIND)&&(MapCanvas.map.activeRoute.rId==MapCanvas.map.rmss.getRouteId(i))) {
                    getListWP().append(MapCanvas.map.rmss.getRouteName(i), getImageWPTs());
                } else {
                    getListWP().append(MapCanvas.map.rmss.getRouteName(i), null);
                }

            }
            if (getListWP().size()>1) {
                getListWP().append(LangHolder.getString(Lang.deleteall), null);
            }
//#debug
//#             tP=5;
            getListWP().setSelectedIndex(0, true);
//#debug
//#             tP=6;
            if (show){
                MapCanvas.setCurrent(getListWP());
                selectKind=MapRoute.WAYPOINTSKIND;
            }
//#mdebug
//#         } catch (Throwable t) {
//#             if (RMSOption.debugEnabled) {
//#                 DebugLog.add2Log("Sel WPT:"+String.valueOf(tP)+":"+t);
//#             }
//#         }
//#enddebug
    }

    public void showSelectTrackForm(boolean show) {
        getListTrack().deleteAll();
        if (MapCanvas.map.activeTrack==null) {
            getListTrack().append(LangHolder.getString(Lang.starttr), null);
        } else {
            getListTrack().append(LangHolder.getString(Lang.closetr), null);
        }
        MapCanvas.map.rmss.reloadRMSRoutes(MapRoute.TRACKKIND);
        int rc=MapCanvas.map.rmss.getRouteCount();
        Image tI;
        for (int i=0; i<rc; i++) {
            RMSRoute rmsR=MapCanvas.map.rmss.getRMSRoute(i);
            tI=null;
            if ((MapCanvas.map.activeTrack!=null)&&(MapCanvas.map.activeTrack.rId==rmsR.rId)) {
                tI=getImageTracks();
            } else if (rmsR.osmTrackId>0) {
                tI=getImageTrO();
            } else if (rmsR.mnTrackId>0) {
                tI=getImageTrW();
            } else if (rmsR.saved) {
                tI=getImageTrS();
            }
            getListTrack().append(MapCanvas.map.rmss.getRouteName(i), tI);

//      if (MapCanvas.map.activeTrack==null)
//        getListTrack().append(MapCanvas.map.rmss.getRouteName(i),null);
//      else
//        if (MapCanvas.map.activeTrack.rId==rmsR.rId)
//          getListTrack().append(MapCanvas.map.rmss.getRouteName(i),getImageTracks());
//        else
//          getListTrack().append(MapCanvas.map.rmss.getRouteName(i),null);

        }
        if (getListTrack().size()>1) {
            getListTrack().append(LangHolder.getString(Lang.deleteall), null);
        }
        getListTrack().setSelectedIndex(0, true);
        if (show){
            MapCanvas.setCurrent(getListTrack());
            selectKind=MapRoute.TRACKKIND;
        }
    }

    public void onFeedback(Object feedObject) {
        MapRoute mr;
        DataSenderThread dst;
        if (selectKind==MapRoute.TRACKKIND){
            mr=MapCanvas.map.rmss.loadRoute(getListTrack().getSelectedIndex()-1, MapRoute.TRACKKIND);
            dst=new DataSenderThread(listTrackMenu, mr, DataSenderThread.SENDTYPE_BLUE);
            MapCanvas.setCurrent(new ProgressForm("Bluetooth", "Sending", dst, listTrackMenu));
        } else if (selectKind==MapRoute.ROUTEKIND){
            mr=MapCanvas.map.rmss.loadRoute(getListRoute().getSelectedIndex()-1, MapRoute.ROUTEKIND);
            dst=new DataSenderThread(listRouteMenu, mr, DataSenderThread.SENDTYPE_BLUE);
            MapCanvas.setCurrent(new ProgressForm("Bluetooth", "Sending", dst, listRouteMenu));
        } else if (selectKind==MapRoute.WAYPOINTSKIND){
            mr=MapCanvas.map.rmss.loadRoute(getListWP().getSelectedIndex()-1, MapRoute.WAYPOINTSKIND);
            dst=new DataSenderThread(listWPMenu, mr, DataSenderThread.SENDTYPE_BLUE);
            MapCanvas.setCurrent(new ProgressForm("Bluetooth", "Sending", dst, listWPMenu));
        } else {
            return;
        }

        dst.url=(String) feedObject;
        dst.start();
    }
    private GeneralFeedback genNotify;
    private boolean btGPS;

    public void showBTForm(String btUrl, String btName, boolean connected) {
        showBTForm(btUrl, btName, connected, btGPS);
    }

    public void showBTForm(String btUrl, String btName, boolean connected, boolean gps) {
        btGPS=gps;
        //clearForms();
        if (!connected){
            getListBT().deleteAll();
            if (gps) {
                genNotify=MapCanvas.map;
            } else {
                genNotify=this;
            }
        }

        String[] NAMES=(gps)?RMSOption.BT_DEVICE_NAMES:RMSOption.BT_DEVICE_NAMES_SEND;
        String[] URLS=(gps)?RMSOption.BT_DEVICE_URLS:RMSOption.BT_DEVICE_URLS_SEND;

        if (btUrl!=null){
            String[] ns=new String[NAMES.length+1];

            System.arraycopy(NAMES, 0, ns, 0, NAMES.length);
            ns[ns.length-1]=btName;
            if (gps) {
                RMSOption.BT_DEVICE_NAMES=ns;
            } else {
                RMSOption.BT_DEVICE_NAMES_SEND=ns;
            }

            ns=new String[URLS.length+1];
            System.arraycopy(URLS, 0, ns, 0, URLS.length);
            ns[ns.length-1]=btUrl;
            if (gps) {
                RMSOption.BT_DEVICE_URLS=ns;
            } else {
                RMSOption.BT_DEVICE_URLS_SEND=ns;
            }
            MapCanvas.map.rmss.writeSettingNow();
        }
        if (connected){
            genNotify.onFeedback(btUrl);
            return;
        }
        NAMES=(gps)?RMSOption.BT_DEVICE_NAMES:RMSOption.BT_DEVICE_NAMES_SEND;
        URLS=(gps)?RMSOption.BT_DEVICE_URLS:RMSOption.BT_DEVICE_URLS_SEND;

        for (int i=0; i<NAMES.length; i++) {
            listBT.append(NAMES[i], null);
        }
        listBT.append(LangHolder.getString(Lang.adddevice), null);
        listBT.setSelectedIndex(0, true);
        MapCanvas.autoShowMap=true;
        MapCanvas.setCurrent(listBT);
    }

    public void showNavigateForm() {
        clearForms();
        getListNav();
        if (MapCanvas.map.activeTrack==null){
            listNav.delete(5);
        }
        MapCanvas.setCurrent(listNav);
    }

    public void showMoreForm() {
        MapCanvas.setCurrent(getListMore());
    }

    private void clearInetPicCache() {
        MapCanvas.map.rmss.clearInetPicCache();
    }

    private void resetSettings() {
        MapCanvas.map.rmss.resetSettings();
        MapCanvas.showmsgmodal(LangHolder.getString(Lang.attention), LangHolder.getString(Lang.chrestart), AlertType.WARNING, MapCanvas.map);
        (new Thread() {

            public void run() {
                long t=System.currentTimeMillis();
                while (t>System.currentTimeMillis()-2000) {
                    yield();
                }
                exitMIDlet();
            }
        }).start();
        //t.start();
        //exitMIDlet ()
        //showSettingForm();
    }

    private void saveOptGeneral() {
        RMSOption.scaleMap=choiceScale.isSelected(0);
        RMSOption.parallelLoad=choiceScale.isSelected(1);
        RMSOption.fullScreen=choiceScale.isSelected(2);
        RMSOption.safeMode=choiceScale.isSelected(3);
        RMSOption.showCoords=choiceScale.isSelected(4);
        RMSOption.lightOn=choiceScale.isSelected(5);
        RMSOption.blinkLight=choiceScale.isSelected(6);
        RMSOption.light50=choiceScale.isSelected(7);
        RMSOption.foxHunter=choiceScale.isSelected(8);
        RMSOption.limitImgRot=choiceScale.isSelected(9);
        RMSOption.setBoolOpt(RMSOption.BL_ADDMINIZE, choiceScale.isSelected(10));
        RMSOption.setBoolOpt(RMSOption.BL_CROSS_OFF, !choiceScale.isSelected(11));
        RMSOption.setBoolOpt(RMSOption.BL_TRANS_POINTER, choiceScale.isSelected(12));
        RMSOption.setBoolOpt(RMSOption.BL_LIGHTONLOCK, choiceScale.isSelected(13));
        RMSOption.setBoolOpt(RMSOption.BL_RELOADWITHDELETE, choiceScale.isSelected(14));
        RMSOption.setBoolOpt(RMSOption.BL_SHOWCLOCKONMAP, choiceScale.isSelected(15));

        RMSOption.setStringOpt(RMSOption.SO_WORKPATH, textWorkPath.getString());

        //MapCanvas.map.maxTRunLoad=(RMSOption.parallelLoad)?2:1;
        MapTile.setPicScaling(RMSOption.scaleMap);
        MapCanvas.map.setFullScreenMode(RMSOption.fullScreen);
        MapCanvas.lightControlled=true;
        MapCanvas.setLight();
        MapCanvas.map.removeCommand(MapCanvas.map.minCommand);
        if (RMSOption.getBoolOpt(RMSOption.BL_ADDMINIZE)) {
            MapCanvas.map.addCommand(MapCanvas.map.minCommand);
        }

        MapCanvas.map.rmss.writeSettingNow();
    }

    private void saveOptAppear() {

        RMSOption.fontSize=(byte) choiceFontSize.getSelectedIndex();
        RMSOption.fontStyle=(byte) choiceFontStyle.getSelectedIndex();
        if (choiceForeColor.getSelectedIndex()<MapUtil.colors.length) {
            RMSOption.foreColor=MapUtil.colors[choiceForeColor.getSelectedIndex()];
        }
        if (choiceShadowColor.getSelectedIndex()<MapUtil.colors.length) {
            RMSOption.shadowColor=MapUtil.colors[choiceShadowColor.getSelectedIndex()];
        }
        RMSOption.setByteOpt(RMSOption.BO_VIEW2, (byte) choiceViewports2.getSelectedIndex());
        RMSOption.setByteOpt(RMSOption.BO_VIEW3, (byte) choiceViewports3.getSelectedIndex());
        RMSOption.setByteOpt(RMSOption.BO_VIEW4, (byte) choiceViewports4.getSelectedIndex());

        RMSOption.setBoolOpt(RMSOption.BL_DARKENBACK, choiceAppearCh.isSelected(0));
        RMSOption.setBoolOpt(RMSOption.BL_TRANS50, choiceAppearCh.isSelected(1));
        MapCanvas.clearDarkenImages();

        MapCanvas.map.rmss.writeSettingNow();
    }

    private void saveOptKeys() {
        RMSOption.setByteOpt(RMSOption.BO_HOLD5ADD, (byte) choice5Add.getSelectedIndex());
        RMSOption.setBoolOpt(RMSOption.BL_SMOOTH_SCROLL, choiceKeys.isSelected(0));
        RMSOption.keyFunction[RMSOption.KEY_0]=(byte) choiceKey0.getSelectedIndex();
        RMSOption.keyFunction[RMSOption.KEY_1]=(byte) choiceKey1.getSelectedIndex();
        RMSOption.keyFunction[RMSOption.KEY_1HOLD]=(byte) choiceKey1Hold.getSelectedIndex();
        RMSOption.keyFunction[RMSOption.KEY_2]=(byte) choiceKey2.getSelectedIndex();
        RMSOption.keyFunction[RMSOption.KEY_2HOLD]=(byte) choiceKey2Hold.getSelectedIndex();
        RMSOption.keyFunction[RMSOption.KEY_3]=(byte) choiceKey3.getSelectedIndex();
        RMSOption.keyFunction[RMSOption.KEY_3HOLD]=(byte) choiceKey3Hold.getSelectedIndex();
        RMSOption.keyFunction[RMSOption.KEY_4]=(byte) choiceKey4.getSelectedIndex();
        RMSOption.keyFunction[RMSOption.KEY_4HOLD]=(byte) choiceKey4Hold.getSelectedIndex();
        RMSOption.keyFunction[RMSOption.KEY_5HOLD]=(byte) choiceKey5Hold.getSelectedIndex();
        RMSOption.keyFunction[RMSOption.KEY_6]=(byte) choiceKey6.getSelectedIndex();
        RMSOption.keyFunction[RMSOption.KEY_6HOLD]=(byte) choiceKey6Hold.getSelectedIndex();
        RMSOption.keyFunction[RMSOption.KEY_7]=(byte) choiceKey7.getSelectedIndex();
        RMSOption.keyFunction[RMSOption.KEY_7HOLD]=(byte) choiceKey7Hold.getSelectedIndex();
        RMSOption.keyFunction[RMSOption.KEY_8]=(byte) choiceKey8.getSelectedIndex();
        RMSOption.keyFunction[RMSOption.KEY_8HOLD]=(byte) choiceKey8Hold.getSelectedIndex();
        RMSOption.keyFunction[RMSOption.KEY_9]=(byte) choiceKey9.getSelectedIndex();
        RMSOption.keyFunction[RMSOption.KEY_9HOLD]=(byte) choiceKey9Hold.getSelectedIndex();
        RMSOption.keyFunction[RMSOption.KEY_SHARP]=(byte) choiceKeyCH.getSelectedIndex();
        MapCanvas.map.rmss.writeSettingNow();
    }

    private void saveOptGeoInfo() {
        RMSOption.coordType=(byte) (choiceCoordType.getSelectedIndex()+1);
        RMSOption.showDatum=choiceDatum.getSelectedIndex()+1;
        RMSOption.showProj=choiceProj.getSelectedIndex()+1;
        RMSOption.unitFormat=(byte) choiceMeasure.getSelectedIndex();
        RMSOption.routeSearchType=(byte) choiceRouteSearch.getSelectedIndex();
        MapCanvas.map.rmss.writeSettingNow();
    }

    private void saveOptAlerts() {
        RMSOption.soundOn=choiceSounds.isSelected(0);
        RMSOption.satSoundStatus=choiceSounds.isSelected(1);
        RMSOption.setBoolOpt(RMSOption.BL_VIBRATE_ON, choiceSounds.isSelected(2));
        RMSOption.setBoolOpt(RMSOption.BL_ADDTRACKPOINTSOUND_ON, choiceSounds.isSelected(3));
        RMSOption.setBoolOpt(RMSOption.BL_WARNMAXSPEED, choiceSounds.isSelected(4));
        RMSOption.setBoolOpt(RMSOption.BL_WAYPOINTNOTIFY, choiceSounds.isSelected(5));
        RMSOption.setBoolOpt(RMSOption.BL_WARNDOWNSPEED, choiceSounds.isSelected(6));
        RMSOption.setByteOpt(RMSOption.BO_VOLUME, (byte) (gaugeVolume.getValue()*10));
        RMSOption.maxSpeed=(int) Double.parseDouble(textMaxSpd.getString());
        try {
            RMSOption.setDoubleOpt(RMSOption.DO_MAXCLIMBSPD, Double.parseDouble(textMaxClm.getString()));
        } catch (Throwable t) {
            RMSOption.setDoubleOpt(RMSOption.DO_MAXCLIMBSPD, 2);
        }
        try {
            RMSOption.setDoubleOpt(RMSOption.DO_MAXDESCENTSPD, Double.parseDouble(textMaxDsc.getString()));
        } catch (Throwable t) {
            RMSOption.setDoubleOpt(RMSOption.DO_MAXDESCENTSPD, 2);
        }
        MapCanvas.map.rmss.writeSettingNow();
    }

    private void saveOptGPS() {
        int optGPSCount=1;
        if (MNSInfo.bluetoothAvailable()) {
            optGPSCount++;
        }
        if (MNSInfo.comportsAvailable()) {
            optGPSCount++;
        }
        if (MNSInfo.locationAvailable()) {
            optGPSCount++;
        }
        if (optGPSCount>0){
            byte[] opts=new byte[optGPSCount];
            optGPSCount=0;
            if (MNSInfo.bluetoothAvailable()){
                opts[optGPSCount]=0;
                optGPSCount++;
            }
            if (MNSInfo.comportsAvailable()){
                opts[optGPSCount]=1;
                optGPSCount++;
            }
            if (MNSInfo.locationAvailable()){
                opts[optGPSCount]=2;
                optGPSCount++;
            }
            opts[optGPSCount]=3;
            optGPSCount++;
            byte gk=(byte) opts[choiceGPSKind.getSelectedIndex()];
            RMSOption.connGPSType=gk;
        }
        //   MapCanvas.map.rmss.setGPSConn((byte)choiceGPSKind.getSelectedIndex());
        RMSOption.comGPSPort=textGPSCOM.getString();
        RMSOption.setBoolOpt(RMSOption.BL_HGE100, false);
        if (choiceGpsOpt.isSelected(1)){
            RMSOption.setBoolOpt(RMSOption.BL_HGE100, true);
            RMSOption.comGPSPort="AT5;baudrate=9600";
            RMSOption.connGPSType=1;
        }

        RMSOption.setBoolOpt(RMSOption.BL_BLUETOOTH_AUTHENTICATE, choiceGpsOpt.isSelected(2));
        RMSOption.setBoolOpt(RMSOption.BL_BLUETOOTH_MONITOR, choiceGpsOpt.isSelected(3));
        RMSOption.gpsAutoReconnect=choiceGpsOpt.isSelected(0);
        RMSOption.gpsReconnectDelay =(choiceGPSReconnectDelay.getSelectedIndex()==0) ?1000:30000;

        MapCanvas.map.rmss.writeSettingNow();
    }

    private void saveOptTrack() {
        RMSOption.showTrPoints=(byte) (choiceTrackPC.getSelectedIndex()+1);

        RMSOption.setTrackPeriodIndex(choiceTrackPeriod.getSelectedIndex());
        RMSOption.setTrackDistIndex(choiceTrackDist.getSelectedIndex());
        RMSOption.setProxIndex(choiceRouteProx.getSelectedIndex());
        RMSOption.setBoolOpt(RMSOption.BL_AUTOWPTNEAREST, choiceAutoWPTType.getSelectedIndex()==1);


        int truse=0;
        if (choiceTrackUse.isSelected(0)) {
            truse=RMSOption.USETIME;
        }
        if (choiceTrackUse.isSelected(1)) {
            truse=truse+RMSOption.USEDIST;
        }
        RMSOption.trackRecordUse=(byte) truse;
        RMSOption.addTrackPointOnTurn=choiceTrackUse.isSelected(2);

        RMSOption.trackAutoStart=choiceAutoWpt.isSelected(0);
        RMSOption.cleanDefaultTrack=choiceAutoWpt.isSelected(1);
        RMSOption.autoSelectWpt=choiceAutoWpt.isSelected(2);
        RMSOption.setBoolOpt(RMSOption.BL_LIMITTRACKSHOWROTATE, choiceAutoWpt.isSelected(3));
        RMSOption.coloredTrack=choiceAutoWpt.isSelected(4);
        RMSOption.setBoolOpt(RMSOption.BL_TRACKBACKUP, choiceAutoWpt.isSelected(5));

        RMSOption.maxTrackPoints=Integer.parseInt(textMaxTP.getString());
        if (RMSOption.maxTrackPoints<50){
            RMSOption.maxTrackPoints=50;
        }
        MapCanvas.map.rmss.writeSettingNow();
    }

    private void saveOptLang() {
        byte cl=RMSOption.currLang;
        try {
            RMSOption.currLang=(byte) choiceLang.getSelectedIndex();
            try {
                LangHolder.setCurrUiLanguage(LangHolder.LANG_AVAILABLE()[choiceLang.getSelectedIndex()]);
            } catch (Throwable t) {
            }
            MapCanvas.map.rmss.writeSettingNow();
        } catch (Throwable t) {
            RMSOption.currLang=cl;
        }
    }

    private void saveOptMaps() {

        RMSOption.setStringOpt(RMSOption.SO_ONLINE_URL_SUR, textOnlineUrlSUR.getString());
        RMSOption.setStringOpt(RMSOption.SO_ONLINE_URL_MAP, textOnlineUrlMAP.getString());

        RMSOption.setByteOpt(RMSOption.BO_ONLINE_SUR_TYPE, (byte) choiceOnlineUrlSUR.getSelectedIndex());
        RMSOption.setByteOpt(RMSOption.BO_ONLINE_MAP_TYPE, (byte) choiceOnlineUrlMAP.getSelectedIndex());

        RMSOption.setBoolOpt(RMSOption.BL_AUTOCENTEROM, choiceEMOpts.isSelected(0));
        RMSOption.setBoolOpt(RMSOption.BL_CACHEINDEX, choiceEMOpts.isSelected(1));
        RMSOption.setStringOpt(RMSOption.SO_OSMURL, textOSMURL.getString());
        boolean[] b=new boolean[RMSOption.MAPVSCOUNT];
        choiceMaps.getSelectedFlags(b);
        RMSOption.setMapVS(b);
        MapCanvas.map.fillSerViewLists(RMSOption.mapVS);
        MapCanvas.map.rmss.writeSettingNow();
    }

    private void saveOptNetRadar() {
        RMSOption.holdInetConn=choiceNetRadar.isSelected(0);
        RMSOption.setBoolOpt(RMSOption.BL_REALTIMENR, choiceNetRadar.isSelected(1));
        RMSOption.setBoolOpt(RMSOption.BL_REALTIMENR_UDP, choiceNetRadar.isSelected(2));
        RMSOption.setBoolOpt(RMSOption.BL_SHOWTRACKNR, choiceNetRadar.isSelected(3));

        RMSOption.netRadarLogin=textRadLogin.getString();
        RMSOption.netRadarPass=textRadPass.getString();
        RMSOption.setByteOpt(RMSOption.BO_NRPERIOD, (byte) choiceNRTrackPeriod.getSelectedIndex());
        RMSOption.setStringOpt(RMSOption.SO_OSMLOGIN, textOSMLogin.getString());
        RMSOption.setStringOpt(RMSOption.SO_OSMPASS, textOSMPass.getString());
        MapCanvas.map.rmss.writeSettingNow();
        MapCanvas.map.endNetRadar();
    }

    private void saveOptCache() {
        RMSOption.cacheSize=gaugeCache.getValue();
        RMSOption.setBoolOpt(RMSOption.BL_EXTCACHEUSE, choiceCacheOpt.isSelected(0));
        RMSOption.setByteOpt(RMSOption.BO_CACHE_FORMAT_TYPE,choiceCacheOpt.isSelected(1)?RMSOption.CACHE_FORMAT_GMT:RMSOption.CACHE_FORMAT_MV);
        RMSOption.setStringOpt(RMSOption.SO_EXTCACHEPATH, textFileCachePath.getString());

        MapCanvas.map.rmss.writeSettingNow();
    }

    private void saveOptDebug() {
        RMSOption.debugEnabled=choiceDebug.isSelected(0);
        //MapCanvas.map.rmss.setBlueMaster(choiceDebug.isSelected(1));
        RMSOption.mapCorrectMode=choiceDebug.isSelected(1);
        RMSOption.saveMapCorrectMode=choiceDebug.isSelected(2);
        RMSOption.correctMapAll=choiceDebug.isSelected(3);
        RMSOption.writeNMEA=choiceDebug.isSelected(4);
        RMSOption.tellNewDebugVersion=choiceDebug.isSelected(5);
        RMSOption.logSavePath=textLogSavePath.getString();
        MapCanvas.map.rmss.writeSettingNow();
    }

    public void back2Map() {
        MapCanvas.setCurrent(MapCanvas.map);
        clearForms();
        MapCanvas.map.repaint();
    }

    public void back2More() {
        MapCanvas.setCurrent(getListMore());
    }

    private void back2Nav() {
        clearForms();
        MapCanvas.setCurrent(getListNav());
    }

    private void back2Opt(boolean clear) {
        //List sf = listOpt;
        //Command cm = close2Map;
        if (clear) {
            clearForms();
        }
        //listOpt=sf;
        //close2Map=cm;
        MapCanvas.setCurrent(getListOpt());
    }

    private void showOptGen() {
        choiceScale.setSelectedIndex(0, RMSOption.scaleMap);
        choiceScale.setSelectedIndex(1, RMSOption.parallelLoad);
        choiceScale.setSelectedIndex(2, RMSOption.fullScreen);
        choiceScale.setSelectedIndex(3, RMSOption.safeMode);
        choiceScale.setSelectedIndex(4, RMSOption.showCoords);
        choiceScale.setSelectedIndex(5, RMSOption.lightOn);
        choiceScale.setSelectedIndex(6, RMSOption.blinkLight);
        choiceScale.setSelectedIndex(7, RMSOption.light50);
        choiceScale.setSelectedIndex(8, RMSOption.foxHunter);
        choiceScale.setSelectedIndex(9, RMSOption.limitImgRot);
        choiceScale.setSelectedIndex(10, RMSOption.getBoolOpt(RMSOption.BL_ADDMINIZE));
        choiceScale.setSelectedIndex(11, !RMSOption.getBoolOpt(RMSOption.BL_CROSS_OFF));
        choiceScale.setSelectedIndex(12, RMSOption.getBoolOpt(RMSOption.BL_TRANS_POINTER));
        choiceScale.setSelectedIndex(13, RMSOption.getBoolOpt(RMSOption.BL_LIGHTONLOCK));
        choiceScale.setSelectedIndex(14, RMSOption.getBoolOpt(RMSOption.BL_RELOADWITHDELETE));
        choiceScale.setSelectedIndex(15, RMSOption.getBoolOpt(RMSOption.BL_SHOWCLOCKONMAP));

        textWorkPath.setString(RMSOption.getStringOpt(RMSOption.SO_WORKPATH));
    }

    private void showOptAppear() {
        MapUtil.fillColorChoice(choiceForeColor);
        MapUtil.fillColorChoice(choiceShadowColor);
        choiceFontSize.setSelectedIndex(RMSOption.fontSize, true);
        choiceFontStyle.setSelectedIndex(RMSOption.fontStyle, true);
        int ind=MapUtil.getColorIndex(RMSOption.foreColor);
        choiceForeColor.setSelectedIndex(ind, true);
        ind=MapUtil.getColorIndex(RMSOption.shadowColor);
        choiceShadowColor.setSelectedIndex(ind, true);

        choiceAppearCh.setSelectedIndex(0, RMSOption.getBoolOpt(RMSOption.BL_DARKENBACK));
        choiceAppearCh.setSelectedIndex(1, RMSOption.getBoolOpt(RMSOption.BL_TRANS50));

        choiceViewports2.setSelectedIndex(RMSOption.getByteOpt(RMSOption.BO_VIEW2), true);
        choiceViewports3.setSelectedIndex(RMSOption.getByteOpt(RMSOption.BO_VIEW3), true);
        choiceViewports4.setSelectedIndex(RMSOption.getByteOpt(RMSOption.BO_VIEW4), true);

    }

    private void showOptKeys() {
        choice5Add.setSelectedIndex(RMSOption.getByteOpt(RMSOption.BO_HOLD5ADD), true);
        choiceKeys.setSelectedIndex(0, RMSOption.getBoolOpt(RMSOption.BL_SMOOTH_SCROLL));
        choiceKey0.setSelectedIndex(RMSOption.keyFunction[RMSOption.KEY_0], true);
        choiceKey1.setSelectedIndex(RMSOption.keyFunction[RMSOption.KEY_1], true);
        choiceKey1Hold.setSelectedIndex(RMSOption.keyFunction[RMSOption.KEY_1HOLD], true);
        choiceKey2.setSelectedIndex(RMSOption.keyFunction[RMSOption.KEY_2], true);
        choiceKey2Hold.setSelectedIndex(RMSOption.keyFunction[RMSOption.KEY_2HOLD], true);
        choiceKey3.setSelectedIndex(RMSOption.keyFunction[RMSOption.KEY_3], true);
        choiceKey3Hold.setSelectedIndex(RMSOption.keyFunction[RMSOption.KEY_3HOLD], true);
        choiceKey4.setSelectedIndex(RMSOption.keyFunction[RMSOption.KEY_4], true);
        choiceKey4Hold.setSelectedIndex(RMSOption.keyFunction[RMSOption.KEY_4HOLD], true);
        choiceKey5Hold.setSelectedIndex(RMSOption.keyFunction[RMSOption.KEY_5HOLD], true);
        choiceKey6.setSelectedIndex(RMSOption.keyFunction[RMSOption.KEY_6], true);
        choiceKey6Hold.setSelectedIndex(RMSOption.keyFunction[RMSOption.KEY_6HOLD], true);
        choiceKey7.setSelectedIndex(RMSOption.keyFunction[RMSOption.KEY_7], true);
        choiceKey7Hold.setSelectedIndex(RMSOption.keyFunction[RMSOption.KEY_7HOLD], true);
        choiceKey8.setSelectedIndex(RMSOption.keyFunction[RMSOption.KEY_8], true);
        choiceKey8Hold.setSelectedIndex(RMSOption.keyFunction[RMSOption.KEY_8HOLD], true);
        choiceKey9.setSelectedIndex(RMSOption.keyFunction[RMSOption.KEY_9], true);
        choiceKey9Hold.setSelectedIndex(RMSOption.keyFunction[RMSOption.KEY_9HOLD], true);
        choiceKeyCH.setSelectedIndex(RMSOption.keyFunction[RMSOption.KEY_SHARP], true);
    }

    private void showOptGeoInfo() {
        choiceCoordType.setSelectedIndex(RMSOption.coordType-1, true);
        if (RMSOption.showDatum<1) {
            RMSOption.showDatum=1;
        }
        if (RMSOption.showDatum>2) {
            RMSOption.showDatum=2;
        }
        choiceDatum.setSelectedIndex(RMSOption.showDatum-1, true);
        if (RMSOption.showProj<1) {
            RMSOption.showProj=1;
        }
        if (RMSOption.showProj>3) {
            RMSOption.showProj=3;
        }
        choiceProj.setSelectedIndex(RMSOption.showProj-1, true);
        choiceMeasure.setSelectedIndex(RMSOption.unitFormat, true);
        choiceRouteSearch.setSelectedIndex(RMSOption.routeSearchType, true);

    }

    private void showOptAlerts() {
        choiceSounds.setSelectedIndex(0, RMSOption.soundOn);
        choiceSounds.setSelectedIndex(1, RMSOption.satSoundStatus);
        choiceSounds.setSelectedIndex(2, RMSOption.getBoolOpt(RMSOption.BL_VIBRATE_ON));
        choiceSounds.setSelectedIndex(3, RMSOption.getBoolOpt(RMSOption.BL_ADDTRACKPOINTSOUND_ON));
        choiceSounds.setSelectedIndex(4, RMSOption.getBoolOpt(RMSOption.BL_WARNMAXSPEED));
        choiceSounds.setSelectedIndex(5, RMSOption.getBoolOpt(RMSOption.BL_WAYPOINTNOTIFY));
        choiceSounds.setSelectedIndex(6, RMSOption.getBoolOpt(RMSOption.BL_WARNDOWNSPEED));
        gaugeVolume.setValue(RMSOption.getByteOpt(RMSOption.BO_VOLUME)/10);
        textMaxSpd.setString(String.valueOf(RMSOption.maxSpeed));
        textMaxClm.setString(String.valueOf(RMSOption.getDoubleOpt(RMSOption.DO_MAXCLIMBSPD)));
        textMaxDsc.setString(String.valueOf(RMSOption.getDoubleOpt(RMSOption.DO_MAXDESCENTSPD)));
    }

    private void showOptCache() {
        gaugeCache.setValue(RMSOption.cacheSize);
        stringCacheSize.setText(String.valueOf(MapCanvas.map.rmss.getInetCacheSizeBytes()));
        stringMapSize.setText(String.valueOf(MapCanvas.map.rmss.getMyCacheSizeBytes()));
        textFileCachePath.setString(RMSOption.getStringOpt(RMSOption.SO_EXTCACHEPATH));
        choiceCacheOpt.setSelectedIndex(0, RMSOption.getBoolOpt(RMSOption.BL_EXTCACHEUSE));
        choiceCacheOpt.setSelectedIndex(1, RMSOption.getByteOpt(RMSOption.BO_CACHE_FORMAT_TYPE)==RMSOption.CACHE_FORMAT_GMT);

        //  stringCacheFree.setText(String.valueOf(MapCanvas.map.rmss.getSizeAvailable()));
    }

    private void showOptGPS() {
        int optGPSCount;
        if (RMSOption.connGPSType<0) {
            RMSOption.connGPSType=0;
        }
        if (RMSOption.connGPSType>3) {
            RMSOption.connGPSType=3;
        }

        optGPSCount=1;
        if (MNSInfo.bluetoothAvailable()) {
            optGPSCount++;
        }
        if (MNSInfo.comportsAvailable()) {
            optGPSCount++;
        }
        if (MNSInfo.locationAvailable()) {
            optGPSCount++;
        }

        if (optGPSCount>0){
            byte[] opts=new byte[optGPSCount];
            optGPSCount=0;
            if (MNSInfo.bluetoothAvailable()){
                opts[optGPSCount]=0;
                optGPSCount++;
            }
            if (MNSInfo.comportsAvailable()){
                opts[optGPSCount]=1;
                optGPSCount++;
            }
            if (MNSInfo.locationAvailable()){
                opts[optGPSCount]=2;
                optGPSCount++;
            }
            opts[optGPSCount]=3;
            optGPSCount++;

            byte sel=RMSOption.connGPSType;

            if (choiceGPSKind.size()!=optGPSCount){
                if (!MNSInfo.locationAvailable()){
                    choiceGPSKind.delete(2);
                }
                if (!MNSInfo.comportsAvailable()){
                    choiceGPSKind.delete(1);
                }
                if (!MNSInfo.bluetoothAvailable()){
                    choiceGPSKind.delete(0);
                }
            }

            for (int i=0; i<opts.length; i++) {
                if (opts[i]==sel){
                    sel=(byte) i;
                    break;
                }
            }

            if (choiceGPSKind.size()>0) {
                try {
                    choiceGPSKind.setSelectedIndex(0, true);
                    choiceGPSKind.setSelectedIndex(sel, true);
                } catch (Throwable t) {
                }
            }
        }
        textGPSCOM.setString(RMSOption.comGPSPort);
        choiceGpsOpt.setSelectedIndex(0, RMSOption.gpsAutoReconnect);
        choiceGpsOpt.setSelectedIndex(1, RMSOption.getBoolOpt(RMSOption.BL_HGE100));
        choiceGpsOpt.setSelectedIndex(2, RMSOption.getBoolOpt(RMSOption.BL_BLUETOOTH_AUTHENTICATE));
        choiceGpsOpt.setSelectedIndex(3, RMSOption.getBoolOpt(RMSOption.BL_BLUETOOTH_MONITOR));

        choiceGPSReconnectDelay.setSelectedIndex((RMSOption.gpsReconnectDelay==1000)?0:1, true);
    }

    private void showOptLang() {
        choiceLang.setSelectedIndex(RMSOption.currLang, true);
    }

    private void showOptMaps() {
        boolean[] b=new boolean[RMSOption.MAPVSCOUNT];
        MapCanvas.map.fillChoiceLists(b);
        choiceMaps.setSelectedFlags(b);
        choiceEMOpts.setSelectedIndex(0, RMSOption.getBoolOpt(RMSOption.BL_AUTOCENTEROM));
        choiceEMOpts.setSelectedIndex(1, RMSOption.getBoolOpt(RMSOption.BL_CACHEINDEX));
        textOSMURL.setString(RMSOption.getStringOpt(RMSOption.SO_OSMURL));
        textOnlineUrlSUR.setString(RMSOption.getStringOpt(RMSOption.SO_ONLINE_URL_SUR));
        textOnlineUrlMAP.setString(RMSOption.getStringOpt(RMSOption.SO_ONLINE_URL_MAP));
        choiceOnlineUrlSUR.setSelectedIndex(RMSOption.getByteOpt(RMSOption.BO_ONLINE_SUR_TYPE), true);
        choiceOnlineUrlMAP.setSelectedIndex(RMSOption.getByteOpt(RMSOption.BO_ONLINE_MAP_TYPE), true);
    }

    private void showOptTrack() {
        choiceTrackPC.setSelectedIndex(RMSOption.showTrPoints-1, true);

        choiceTrackPeriod.setSelectedIndex(RMSOption.getTrackPeriodIndex(), true);
        choiceTrackDist.setSelectedIndex(RMSOption.getTrackDistIndex(), true);
        choiceRouteProx.setSelectedIndex(RMSOption.getProxIndex(), true);

        choiceTrackUse.setSelectedIndex(0, (RMSOption.trackRecordUse&RMSOption.USETIME)==RMSOption.USETIME);
        choiceTrackUse.setSelectedIndex(1, (RMSOption.trackRecordUse&RMSOption.USEDIST)==RMSOption.USEDIST);
        choiceTrackUse.setSelectedIndex(2, RMSOption.addTrackPointOnTurn);
//    choiceAutoWPTType.setSelectedFlags(
//      new boolean[]{ !RMSOption.getBoolOpt(RMSOption.BL_AUTOWPTNEAREST),
//      RMSOption.getBoolOpt(RMSOption.BL_AUTOWPTNEAREST)});//SelectedIndex(RMSOption.getBoolOpt(RMSOption.BL_AUTOWPTNEAREST)?1:0,true);
        choiceAutoWPTType.setSelectedIndex(RMSOption.getBoolOpt(RMSOption.BL_AUTOWPTNEAREST)?1:0, true);

        choiceAutoWpt.setSelectedIndex(0, RMSOption.trackAutoStart);
        choiceAutoWpt.setSelectedIndex(1, RMSOption.cleanDefaultTrack);
        choiceAutoWpt.setSelectedIndex(2, RMSOption.autoSelectWpt);
        choiceAutoWpt.setSelectedIndex(3, RMSOption.getBoolOpt(RMSOption.BL_LIMITTRACKSHOWROTATE));
        choiceAutoWpt.setSelectedIndex(4, RMSOption.coloredTrack);
        choiceAutoWpt.setSelectedIndex(5, RMSOption.getBoolOpt(RMSOption.BL_TRACKBACKUP));

        textMaxTP.setString(String.valueOf(RMSOption.maxTrackPoints));
    }

    public void showOptNetRadar() {
        textRadLogin.setString(RMSOption.netRadarLogin);
        textRadPass.setString(RMSOption.netRadarPass);
        stringRadarBytes.setText(String.valueOf(NetRadar.bytesDown/1024)+" kb");
        choiceNetRadar.setSelectedIndex(0, RMSOption.holdInetConn);
        choiceNetRadar.setSelectedIndex(1, RMSOption.getBoolOpt(RMSOption.BL_REALTIMENR));
        choiceNetRadar.setSelectedIndex(2, RMSOption.getBoolOpt(RMSOption.BL_REALTIMENR_UDP));
        choiceNetRadar.setSelectedIndex(3, RMSOption.getBoolOpt(RMSOption.BL_SHOWTRACKNR));
        choiceNRTrackPeriod.setSelectedIndex(RMSOption.getByteOpt(RMSOption.BO_NRPERIOD), true);
        textOSMLogin.setString(RMSOption.getStringOpt(RMSOption.SO_OSMLOGIN));
        textOSMPass.setString(RMSOption.getStringOpt(RMSOption.SO_OSMPASS));
    }

    private void showOptDebug() {
        choiceDebug.setSelectedIndex(0, RMSOption.debugEnabled);
        //choiceDebug.setSelectedIndex(1, RMSOption.blueMaster);
        choiceDebug.setSelectedIndex(1, RMSOption.mapCorrectMode);
        choiceDebug.setSelectedIndex(2, RMSOption.saveMapCorrectMode);
        choiceDebug.setSelectedIndex(3, RMSOption.correctMapAll);
        choiceDebug.setSelectedIndex(4, RMSOption.writeNMEA);
        choiceDebug.setSelectedIndex(5, RMSOption.tellNewDebugVersion);

        textLogSavePath.setString(RMSOption.logSavePath);
    }

    public void showSettingForm() {
        MapCanvas.setCurrent(getListOpt());
    }

    private void setAddress() {
        double lat=0;
        double lon=0;
        try {

            try {
                lat=MapUtil.parseCoord(textLatFieldg.getString(), RMSOption.coordType);
            } catch (Throwable t) {
                MapCanvas.showmsg(LangHolder.getString(Lang.attention), LangHolder.getString(Lang.followf)+"\n"+LangHolder.getString(Lang.latitude), AlertType.ERROR, getGotoForm());
                return;
            }
            try {
                lon=MapUtil.parseCoord(textLonFieldg.getString(), RMSOption.coordType);
            } catch (Throwable t) {
                MapCanvas.showmsg(LangHolder.getString(Lang.attention), LangHolder.getString(Lang.followf)+"\n"+LangHolder.getString(Lang.longitude), AlertType.ERROR, getGotoForm());
                return;
            }

            MapCanvas.map.setLocation(lat, lon);
            MapCanvas.setCurrent(MapCanvas.map);
        } catch (Exception e) {
            MapCanvas.showmsg(LangHolder.getString(Lang.error), LangHolder.getString(Lang.followf), AlertType.ERROR, getGotoForm());
        }
    }

    private byte mtc;
    private final static byte EXTMAP=1;
    private final static byte INTMAP=2;

    public void showConnectMapForm(byte mapTypeConnect) {
        mtc=mapTypeConnect;
        textOMapName=null;
        textCMapName=null;
        stringMapFree=null;
        formAddOMap=null;
        getFormAddOMap();
        //formAddOMap.append(new MNCheckGroup("title","capt1","capt2"));
        if (mtc==INTMAP) {
            stringMapFree.setText(String.valueOf(MapCanvas.map.rmss.getSizeAvailable()));
        } else {
            formAddOMap.delete(2);
        }
        textCMapName.setString(RMSOption.lastCMapName);
        MapCanvas.setCurrent(formAddOMap);
    }

    public void sendScreenMMS() {
        System.gc();
        if (RMSOption.choiceSendType==0){
            ScreenSend.sendMapMMS(RMSOption.lastSendMMSURL,
              (byte) (RMSOption.choicePicSize+1),
              (byte) (RMSOption.choiceSendType+1), 0, 0, (byte) RMSOption.imageType);
        } else {
            long[] interval={2000, 5000, 10000, 20000, 30000};
            int[] count={5, 10, 20, 30};
            ScreenSend.sendMapMMS(RMSOption.lastSendFileURL,
              (byte) (RMSOption.choicePicSize+1),
              (byte) (RMSOption.choiceSendType+1),
              interval[RMSOption.choiceSerInterval], count[RMSOption.choiceSerCount], RMSOption.imageType);
        }
    }

    public void showSMSSendForm() {
        getListMore();
        MapCanvas.setCurrent(getFormTelInput());
        textTelNum.setString(RMSOption.lastSMSNumber);
        choiceTels.set(1, RMSOption.lastTel1, null);
        choiceTels.set(2, RMSOption.lastTel2, null);
        choiceTels.set(3, RMSOption.lastTel3, null);
    }

    public Form getHelloForm() {
        getGotoForm();
        if (RMSOption.coordType==RMSOption.COORDMINSECTYPE) {
            stringCoordEx.setText("60 23 41\n(GG MM SS)");
        } else if (RMSOption.coordType==RMSOption.COORDMINMMMTYPE) {
            stringCoordEx.setText("60 23.683\n(GG MM.MMM)");
        } else if (RMSOption.coordType==RMSOption.COORDGGGGGGTYPE) {
            stringCoordEx.setText("60.39471\n(GG.GGGGG)");
        } else {
            stringCoordEx.setText("???");
        }
        return getGotoForm();
    }

    public void initializeMap() {
        try {
            if (RMSOption.currLang==-1){
                String s=System.getProperty("microedition.locale").toUpperCase();
                if (s.length()>2) {
                    s=s.substring(0, 2);
                }
                String[] LANG_AVAILABLE=LangHolder.LANG_AVAILABLE();
                for (byte i=0; i<LANG_AVAILABLE.length; i++) {
                    if (s.equals(LANG_AVAILABLE[i])){
                        RMSOption.currLang=i;
                        break;
                    }
                }

            }
        } catch (Throwable t) {
//#mdebug
//#             DebugLog.add2Log("Lang as:"+t);
//#enddebug
        }
        if (RMSOption.currLang==-1) {
            RMSOption.currLang=0;
        }
        mM.ulp(21);
        MapCanvas.map=new MapCanvas();
        mM.ulp(40);

        //try{Thread.sleep(100);}catch(Throwable t){}

        MapCanvas.map.setCommandListener(MapCanvas.map);
        mM.ulp(41);
        MapCanvas.setLight();
        mM.ulp(42);
    }

    public static Alert getSelectWarningAlert() {
        Alert a=new Alert(LangHolder.getString(Lang.attention), LangHolder.getString(Lang.nosel), null, AlertType.WARNING);
        a.setTimeout(3000);
        return a;
    }

    private void showListIMaps() {
//    showLoadMapForm();
        clearForms();
        getListIMaps().deleteAll();
        listIMaps.append(LangHolder.getString(Lang.addmap), null);
        for (int i=0; i<RMSOption.IMAPS_NAMES.length; i++) {
            listIMaps.append(RMSOption.IMAPS_NAMES[i], getImageMap());
        }
        if (RMSOption.IMAPS_NAMES.length>0) {
            listIMaps.append(LangHolder.getString(Lang.deleteall), null);
        }
        listIMaps.setSelectedIndex(0, true);
        MapCanvas.setCurrent(listIMaps);
    }

    private void itemSelectInIMaps() {
        int i=listIMaps.getSelectedIndex();
        if (i==0){
            showConnectMapForm(INTMAP);
        } else if (i==listIMaps.size()-1){
            //delete all
            RMSOption.IMAPS_NAMES=new String[0];

            for (int u=0; u<RMSOption.IMAPS_RMST.length; u++) {
                try {
                    RecordStore.deleteRecordStore(RMSOption.IMAPS_RMST[u]);
                } catch (Throwable tt) {
                }
            }
            RMSOption.IMAPS_RMST=new String[0];

            for (int u=0; u<RMSOption.IMAPS_RMSP.length; u++) {
                try {
                    RecordStore.deleteRecordStore(RMSOption.IMAPS_RMSP[u]);
                } catch (Throwable tt) {
                }
            }
            RMSOption.IMAPS_RMSP=new String[0];

            RMSOption.IMAPS_CENT=new float[0];
            showListIMaps();

        } else {
            MapCanvas.showmsg(LangHolder.getString(Lang.info), LangHolder.getString(Lang.usekeycm), AlertType.INFO, MapCanvas.map);
            MapCanvas.map.setMapSerView((byte) (MapTile.SHOW_MP+MapTile.SHOW_SUR));
            MapCanvas.map.setUserMapIndex((byte) (i+RMSOption.OMAPS_URLS.length), true);
            MapCanvas.map.userMapIndexLabel=MapCanvas.map.userMapIndexUsed;
            MapCanvas.map.setSerViewLabel();
            clearForms();
        }
    }

    private void showListOMaps() {
        clearForms();
        getListOMaps().deleteAll();
        listOMaps.append(LangHolder.getString(Lang.addmap), null);
        for (int i=0; i<RMSOption.OMAPS_NAMES.length; i++) {
            listOMaps.append(RMSOption.OMAPS_NAMES[i], getImageMapE());
        }
        if (RMSOption.OMAPS_NAMES.length>0) {
            listOMaps.append(LangHolder.getString(Lang.deleteall), null);
        }
        listOMaps.setSelectedIndex(0, true);
        MapCanvas.setCurrent(listOMaps);
    }

    private void itemSelectInOMaps() {
        int i=listOMaps.getSelectedIndex();
        if (i==0){
            showConnectMapForm(EXTMAP);
        } else if (i==listOMaps.size()-1){
            //delete all
            RMSOption.OMAPS_NAMES=new String[0];
            RMSOption.OMAPS_URLS=new String[0];
            showListOMaps();
        } else {
            //MapCanvas.showmsgmodal(LangHolder.getString(Lang.info),LangHolder.getString(Lang.usekeycm)+'\n'+RMSOption.OMAPS_URLS[i-1],AlertType.INFO, MapCanvas.map);
            MapCanvas.setCurrentMap();
            MapCanvas.map.setMapSerView((byte) (MapTile.SHOW_MP+MapTile.SHOW_SUR));
            MapCanvas.map.setUserMapIndex((byte) (i), true);
            MapCanvas.map.userMapIndexLabel=MapCanvas.map.userMapIndexUsed;
            MapCanvas.map.setSerViewLabel();
            clearForms();

        }
    }

    public void copyM2WPT() {
        MapCanvas.map.copyMarks2WP();
        getListNav();
        showSelectWPForm(false);
        MapCanvas.showmsg(LangHolder.getString(Lang.waypoints), LangHolder.getString(Lang.ok), AlertType.INFO, listWP);
    }

    private void setKeyChoice(ChoiceGroup cg) {
        for (int i=0; i<RMSOption.keyLangs.length; i++) {
            if (RMSOption.keyLangs[i]>=0) {
                cg.append(LangHolder.getString(RMSOption.keyLangs[i]), null);
            } else {
                switch (RMSOption.keyLangs[i]) {
                    case -1:
                        cg.append("Flickr", null);
                        break;
                    case -2:
                        cg.append(LangHolder.getString(Lang.zoomin)+" 3x", null);
                        break;
                    case -3:
                        cg.append(LangHolder.getString(Lang.zoomout)+" 3x", null);
                        break;
                    case -4:
                        cg.append(LangHolder.getString(Lang.mapsrc)+'/'+LangHolder.getString(Lang.map), null);
                        break;
                    case -5:
                        cg.append("NR:"+LangHolder.getString(Lang.sendmes), null);
                        break;
                    case -6:
                        cg.append("x2", null);
                        break;

                }
            }
        }

    }

    public void clearForms() {
        gotoForm=null;
        textLatFieldg=null;
        textLonFieldg=null;
        itemGoto1=null;
        formOptGen=null;
        choiceScale=null;
        gaugeCache=null;
        stringCacheSize=null;
//    stringCacheFree=null;
        helpClearCache=null;
        itemCreate=null;
        cancel2Map=null;
        itemSelect1=null;
        listRoute=null;
        itemRouteDelete=null;
        formRouteURL=null;
        textRouteURL=null;
        choiceRouteURL=null;
        choiceLang=null;
        listMore=null;

        screenReset=null;

        formAbout=null;
        stringItem1=null;
        close2Map=null;
        formHelp=null;
        stringItem2=null;
        listWP=null;
        itemWPDelete=null;
        listNav=null;
        formSendMMS=null;
        textAddrMMS=null;
        item1Send=null;
        choicePicSize=null;
        choiceSendType=null;
        textAddrFile=null;

        choiceFontSize=null;
        choiceFontStyle=null;

        listTrack=null;
        itemTrackDelete=null;
        itemExport=null;

        imageMarks=null;
        imageRoutes=null;
        imageTracks=null;
        imageWPTs=null;
        imageMap=null;

        formTelInput=null;
        textTelNum=null;
        itemNext1=null;
        listOpt=null;
        itemSelectOpt=null;
        formOptLang=null;
        choiceLang=null;
        back2Opt=null;
        formOptTr=null;
        choiceTrackPeriod=null;
        choiceTrackUse=null;
        formOptCache=null;
        gaugeCache=null;
        stringCacheSize=null;
        choiceTrackDist=null;

        itemLoadMap=null;

        choiceCoordType=null;
        choiceTrackPC=null;
        formOptDeb=null;
        choiceDebug=null;
        itemClear=null;
        stringLog=null;

        stringMapSize=null;
        itemBrowseMap=null;

        formSaveTrack=null;
        textTrackSavePath=null;
        item1Save=null;
        itemSaveLog=null;

        formOptAlr=null;
        choiceSounds=null;
        stringCoordEx=null;
        choiceTels=null;

        formSaveChanges=null;
        stringSaveQ=null;
        okYes=null;
        cancelNo=null;

        formOptRadar=null;
        textRadLogin=null;
        textRadPass=null;
        stringRadarBytes=null;
        textLogSavePath=null;
        itemSaveLogWeb=null;

        formOptMaps=null;
        choiceMaps=null;
        choiceNetRadar=null;
        choiceRouteProx=null;
        stringMapFree=null;

        formDeleteAll=null;
        stringDelAllQ=null;


        stringItemLastVersion=null;
        formOptGPS=null;
        choiceGPSKind=null;
        choiceOnlineUrlSUR=null;
        choiceOnlineUrlMAP=null;
        textGPSCOM=null;
        itemCommandListCOM=null;
        choiceGpsOpt=null;

        stringItemLastDebug=null;

        itemCreateRoute=null;

        stringFreeMem=null;
        stringURLVer=null;

        itemSaveOpt=null;
        formAskReset=null;
        stringAskReset=null;

        choiceSerInterval=null;
        choiceSerCount=null;

        choiceDatum=null;

        formAddOMap=null;
        textCMapName=null;
        itemComLoadMap=null;

        imageMapE=null;
        imageFold=null;

        listBT=null;
        itemSelBT=null;
        formOptGeo=null;
        choiceProj=null;

        choiceCP=null;
        choiceImageType=null;
        choiceCP_S=null;
        choiceOX_S=null;

        listKML=null;
        imageGEGray=null;
        imageGEYel=null;
        imageGEGreen=null;

        itemLoadTrack=null;

        formOptKeys=null;
        choice5Add=null;

        listOMaps=null;
        textOMapName=null;

        formService=null;
        textBackupPath=null;

        stringServiceInfo=null;
        itemBrowseFile=null;
        textWorkPath=null;
        choiceEMOpts=null;

        choiceExpType=null;

        listIMaps=null;
        stringMapFree=null;

        choiceKeys=null;

        choiceAutoWPTType=null;
        choiceAutoWpt=null;
        choiceExpCount=null;

        formOptAppear=null;
        choiceForeColor=null;
        choiceShadowColor=null;

        choiceImpFormat=null;

        choiceViewports2=null;
        choiceViewports3=null;
        choiceViewports4=null;
        listWPMenu=null;
        listTrackMenu=null;
        listRouteMenu=null;

        choiceAppearCh=null;

        formRecFr=null;
        textYourName=null;
        textFrPhone=null;

        textMaxSpd=null;
        textMaxClm=null;
        textMaxDsc=null;
        choiceMeasure=null;
        choiceRouteSearch=null;

        choiceKey1=null;
        choiceKey1Hold=null;
        choiceKey3=null;
        choiceKey3Hold=null;
        choiceKey4=null;
        choiceKey4Hold=null;
        choiceKey5Hold=null;
        choiceKey6=null;
        choiceKey6Hold=null;
        choiceKey7=null;
        choiceKey7Hold=null;
        choiceKey9=null;
        choiceKey9Hold=null;
        choiceKey0=null;
        choiceKeyCH=null;
        choiceKey2=null;
        choiceKey2Hold=null;
        choiceKey8=null;
        choiceKey8Hold=null;
        itemMerge=null;

        textMaxTP=null;
        choiceSMSType=null;
        gaugeVolume=null;

        formSearch=null;
        textSearch=null;
        itemSearch=null;
        listSearch=null;

        itemResults=null;

        listSKML=null;
        listSKMLMenu=null;
        itemClearMap=null;
        choiceNRTrackPeriod=null;

        choiceCacheOpt=null;
        textFileCachePath=null;
        textOSMLogin=null;
        textOSMPass=null;
        textOSMURL=null;
        textOnlineUrlSUR=null;
        textOnlineUrlMAP=null;
        //imageTrO=null;
        //imageTrS=null;
        //imageTrW=null;

        itemTest=null;
        textBox=null;
        choiceGPSReconnectDelay=null;

        System.gc();
    }
    public static MapMidlet mM;

    public MapForms(MapMidlet mM) {
        MapForms.mM=mM;
        mm=this;
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
        if (MapCanvas.timer!=null) {
            MapCanvas.timer.cancel();
        }
        MapCanvas.timer=null;
    }
// HINT - Uncomment for accessing new MIDlet Started/Resumed logic.
// NOTE - Be aware of resolving conflicts of following methods.
//    /**
//     * Called when MIDlet is started.
//     * Checks whether the MIDlet have been already started and initialize/starts or resumes the MIDlet.
//     */
//    public void startApp() {
//        if (midletPaused) {
//            resumeMIDlet ();
//        } else {
//            initialize ();
//            startMIDlet ();
//        }
//        midletPaused = false;
//    }
//
//    /**
//     * Called when MIDlet is paused.
//     */
//    public void pauseApp() {
//        midletPaused = true;
//    }
//
//    /**
//     * Called to signal the MIDlet to terminate.
//     * @param unconditional if true, then the MIDlet has to be unconditionally terminated and all resources has to be released.
//     */
//    public void destroyApp(boolean unconditional) {
//    }
}
