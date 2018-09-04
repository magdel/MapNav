/*
 * FlickrList.java
 *
 * Created on 23 ������� 2007 �., 0:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package camera;

import RPMap.MapCanvas;
import RPMap.MapUtil;
import RPMap.RMSOption;
import app.MapForms;
import gpspack.GPSReader;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import lang.Lang;
import lang.LangHolder;
//#debug
//# import misc.DebugLog;
import misc.BrowseList;
import misc.HTTPUtils;
import misc.ProgressForm;
import misc.ProgressResponse;
import misc.ProgressStoppable;
import misc.Util;

/**
 *
 * @author RFK
 */
public class FlickrList extends List implements CommandListener, Runnable, ProgressStoppable, ProgressResponse, ItemCommandListener {

    public static FlickrList flickr;

    public static void showFlickr() {
        flickr=new FlickrList();
        MapCanvas.setCurrent(flickr);
    }

    /** Creates a new instance of FlickrList */
    public FlickrList() {
        super("Flickr", Choice.IMPLICIT, new String[]{"Registration", "Take a shot", "Send file", "PhotoLog"}, new Image[]{null, null, null, null});
        addCommand(backCommand);
        setSelectCommand(itemSelect);
        setCommandListener(this);
        initFlickr("97b075f8607aafa20ca7441f43e8834e", "40d20a2620e0753e");
    }
    Command itemSelect=new Command(LangHolder.getString(Lang.select), Command.ITEM, 1), backCommand=new Command(LangHolder.getString(Lang.back), Command.BACK, 10), gotoCommand=new Command(LangHolder.getString(Lang.goto_), Command.BACK, 2);
    Form regForm;
    Command saveRegCommand;

    private void showRegForm() {
        regForm=new Form("Registration", new Item[]{
              new StringItem("Visit\n", "Goto to obtain mini-token"),
              new StringItem("", "www.flickr.com/auth-72157602577356052", Item.HYPERLINK),
              new StringItem("to obtain mini-token", ""),
              //!NO-NUMERIC
              new TextField("mini-token", RMSOption.getStringOpt(RMSOption.SO_FLICKRTOKEN), 9, TextField.ANY)
          });
        saveRegCommand=new Command(LangHolder.getString(Lang.save), Command.ITEM, 1);
        regForm.addCommand(saveRegCommand);
        regForm.addCommand(backCommand);
        ((Item) (regForm.get(1))).setDefaultCommand(gotoCommand);
        ((Item) (regForm.get(1))).setItemCommandListener(this);
        regForm.setCommandListener(this);
        MapCanvas.setCurrent(regForm);
    }

    private void clearRegForm() {
        regForm=null;
        saveRegCommand=null;
    }

    public void commandAction(Command command, Item item) {
        if (command==gotoCommand){
            try {
                MapForms.mM.platformRequest("http://www.flickr.com/auth-72157602577356052");
            } catch (Throwable t) {
            }
        }
    }
    CameraTool camTool;

    public void showShot(boolean autoshot) {
        MapCanvas.map.clearAllTiles();
        try {
            camTool=new CameraTool();
            camTool.initCamera(autoshot);
            if (autoshot){
                (new Thread(camTool)).start();
            }
        } catch (Throwable t) {
            camTool=null;
            //#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("CT init:"+t);
//#             }
            //#enddebug
        }
    }

    private void showFileSend() {
        fileForm=new Form("Send file!");

        fileName=new TextField("Filename", "", 60, TextField.ANY);
        fileTitle=new TextField("Title", "", 50, TextField.ANY);
        fileTags=new TextField("Tags", "", 50, TextField.ANY);

        fileForm.append(fileName);
        fileForm.append(fileTitle);
        fileForm.append(fileTags);

        browseCommand=new Command(LangHolder.getString(Lang.browse), Command.ITEM, 2);
        fileForm.addCommand(browseCommand);
        sendCommand=new Command(LangHolder.getString(Lang.send), Command.ITEM, 1);
        fileForm.addCommand(sendCommand);
        previewCommand=new Command(LangHolder.getString(Lang.show), Command.ITEM, 5);
        fileForm.addCommand(previewCommand);
        fileForm.addCommand(backCommand);
        fileForm.setCommandListener(this);
        MapCanvas.setCurrent(fileForm);
    }

    private void clearFileForm() {
    }
    private Form fileForm;
    private TextField fileName;
    private TextField fileTitle;
    private TextField fileTags;
    private Command browseCommand, sendCommand, previewCommand;

    private void showFilePreview() {
        System.gc();
        previewForm=new Form("Photo!");
        previewImage=new ImageItem(fileName.getString(),
          Util.readImageFile(fileName.getString()),
          Item.LAYOUT_CENTER,
          "Error reading file");
        previewForm.append(previewImage);

        previewForm.addCommand(backCommand);
        previewForm.setCommandListener(this);
        MapCanvas.setCurrent(previewForm);
    }
    private Form previewForm;
    private ImageItem previewImage;

    private boolean isRegistered() {
        if (RMSOption.getStringOpt(RMSOption.SO_FLICKRTOKEN).equals(MapUtil.emptyString)){
            MapCanvas.showmsg(LangHolder.getString(Lang.info), "First register the application", AlertType.WARNING, this);
            return false;
        } else {
            return true;
        }
    }

    public void commandAction(Command command, Displayable displayable) {
        if (displayable==this){
            if (command==backCommand){
                flickr=null;
                MapForms.mm.back2More();
            } else if (command==itemSelect){
                switch (getSelectedIndex()) {
                    case 0:
                        if (!RMSOption.getStringOpt(RMSOption.SO_FLICKRFULL).equals(MapUtil.emptyString)){
                            MapCanvas.showmsg(LangHolder.getString(Lang.info), "Already registered! Use Reset in Settings - Cache", AlertType.INFO, this);
                        } else {
                            showRegForm();
                        }
                        break;
                    case 1:
                        if (isRegistered()){
                            showShot(false);
                        }
                        break;
                    case 2:
                        if (isRegistered()){
                            showFileSend();
                        }
                        break;
                    case 3:
                        //photolog
                        if (isRegistered()){
                            showShot(true);
                            //   MapMidlet.showmsg(LangHolder.getString(Lang.info),"Not implemented yet. Look for next version, please!",AlertType.INFO,flickr);
                            // Insert pre-action code here
                            // Do nothing
                            // Insert post-action code here
                        }
                        break;

                }
            }
        } else if (displayable==regForm){
            if (command==saveRegCommand){
                if (!((TextField) (regForm.get(3))).getString().equals(RMSOption.getStringOpt(RMSOption.SO_FLICKRTOKEN))){
                    RMSOption.setStringOpt(RMSOption.SO_FLICKRTOKEN, ((TextField) (regForm.get(3))).getString());
                    RMSOption.setStringOpt(RMSOption.SO_FLICKRFULL, MapUtil.emptyString);
                    MapCanvas.map.rmss.writeSettingNow();
                    photo=null;
                    photofile=null;
                    MapCanvas.setCurrent(
                      new ProgressForm("Flickr", "Registration...", this, this));
                    (new Thread(this)).start();

                } else {
                    MapCanvas.setCurrent(this);
                }
                clearRegForm();
            } else if (command==backCommand){
                MapCanvas.setCurrent(this);
                clearRegForm();
            }
        } else if (displayable==fileForm){
            if (command==browseCommand){
                MapCanvas.setCurrent(new BrowseList(fileForm, fileName, BrowseList.FILEBROWSE, ".JPG", null));
            } else if (command==sendCommand){

                MapCanvas.setCurrent(
                  new ProgressForm(LangHolder.getString(Lang.sending), LangHolder.getString(Lang.waitpls), this, fileForm));

                photo=null;
                if (MapCanvas.map.gpsActive()&&MapCanvas.map.gpsBinded){
                    lat=GPSReader.LATITUDE;
                    lon=GPSReader.LONGITUDE;
                } else {
                    lat=MapCanvas.reallat;
                    lon=MapCanvas.reallon;
                }
                photofile=fileName.getString();
                tags=fileTags.getString();
                title=fileTitle.getString();

                (new Thread(this)).start();

            } else if (command==backCommand){
                MapCanvas.setCurrent(this);
                clearFileForm();
            } else if (command==previewCommand){

                showFilePreview();

            }
        } else if (displayable==previewForm){
            if (command==backCommand){
                MapCanvas.setCurrent(fileForm);
                previewForm=null;
                previewImage=null;
                System.gc();
            }
        }

    }
    boolean stopped;
    public byte[] photo;
    public String photofile;
    public double lat, lon;
    public String tags="", title="";

    public void run() {
        stopped=false;
        String photoid=null;
        try {
            try {
                fli_userToken=RMSOption.getStringOpt(RMSOption.SO_FLICKRFULL);
//#if SE_K750_E_BASEDEV
                //#else
        //if (RMSOption.getStringOpt(RMSOption.SO_FLICKRTOKEN).equals("000000009"))
                //#endif
                //  fli_userToken = "72157602662678548-ca6e178635351d55";
                progressResponse.setProgress((byte) 10, "registration...");
                if (fli_userToken.equals(MapUtil.emptyString)){
                    if (!Registration(RMSOption.getStringOpt(RMSOption.SO_FLICKRTOKEN))){
                        throw new Exception("Registration failed");
                        //    if(CameraForm.a(a).m_lastLocation != null)
//                        s = s + " geotagged geo:lon=" + CameraForm.a(a).m_lastLocation.m_lon + " geo:lat=" + CameraForm.a(a).m_lastLocation.m_lat;
//                    else
//                        s = s + " geotagged geo:lon=" + CameraForm.a(a).getLon() + " geo:lat=" + CameraForm.a(a).getLat();
                    }
                }
                progressResponse.setProgress((byte) 20, "reading file...");
                if (lat!=0){
                    tags=tags+" geotagged geo:lon="+String.valueOf(MapUtil.coordRound5(lon))+" geo:lat="+String.valueOf(MapUtil.coordRound5(lat));
                }

                if (photofile!=null){
                    photo=Util.readFile(photofile);
                }
                if (photo!=null){
                    progressResponse.setProgress((byte) 30, "photo...");
                    if (!stopped){
                        photoid=SendPhoto(photo, title, "Send with Mapnav "+MapUtil.version+" - http://mapnav.spb.ru "+MapUtil.trackNameAuto(), tags);
                        photo=null;
                        //#debug
//#                         System.out.println("photoid:"+photoid);
                        if (photoid==null){
                            throw new Exception("phid=null");
                        }
                        progressResponse.setProgress((byte) 90, "geotagging...");
                        if ((!stopped)&&(photoid!=null)){
                            if (lat!=0){
                                sendLonLat(photoid, String.valueOf(MapUtil.coordRound5(lat)), String.valueOf(MapUtil.coordRound5(lon)));
                            }
                        }
                    }
                }
            } finally {
                photo=null;
                lat=0;
                camTool=null;
            }
            if (photoid!=null) {
                MapCanvas.showmsg(LangHolder.getString(Lang.sent), "Photo is sent. photoid="+photoid, AlertType.INFO, this);
            } else {
                MapCanvas.showmsg(LangHolder.getString(Lang.sent), "Registration complete", AlertType.INFO, this);
            }

        } catch (Throwable t) {
            if ((fli_flickrErrCode==98)|(fli_flickrErrCode==96)){
                RMSOption.setStringOpt(RMSOption.SO_FLICKRFULL, "");
                RMSOption.setStringOpt(RMSOption.SO_FLICKRTOKEN, "");
            }
            if (photofile==null){
                MapCanvas.showmsgmodal(LangHolder.getString(Lang.error), fli_flickrErr, AlertType.ERROR, camTool.takeForm);
            } else {
                MapCanvas.showmsgmodal(LangHolder.getString(Lang.error), fli_flickrErr, AlertType.ERROR, fileForm);
            }
        }
        progressResponse=null;
    }
    ProgressResponse progressResponse;

    public void setProgressResponse(ProgressResponse progressResponse) {
        this.progressResponse=progressResponse;
    }

    public boolean stopIt() {
        stopped=true;
                return true;

    }

    public void setProgress(byte percent, String task) {
        if (progressResponse!=null){
            progressResponse.setProgress((byte) (30+percent*60/100), "photo sending...");
        }
    }
    public static Image lastShot;
    private String fli_secretToken;
    private String fli_userToken;
    private String fli_sharedToken;
    private String fli_md5Token;
    // private String e;
    private String fli_miniToken;
    public String fli_Blogs[][];
    public String fli_Tags[];
    public String fli_Sets[][];
    public String fli_GroupsPools[][];
    private int fli_flickrErrCode;
    private String fli_flickrErr;
    //private Settings a_net_landspurg_util_Settings_fld;
    //private String photoid;
    //97b075f8607aafa20ca7441f43e8834e   //40d20a2620e0753e

    public void initFlickr(String secret, String shared) {
        fli_flickrErrCode=-1;
        //  a_net_landspurg_util_Settings_fld = settings;
        fli_secretToken=secret;
        fli_sharedToken=shared;
        fli_userToken="";//, ""
    }

    public void clearToken() {
        //a_net_landspurg_util_Settings_fld.setStringProperty("token", "");
        fli_userToken="";
    }

    public boolean checkRegistration() {
        return !fli_userToken.equals("");
    }

    private void flickrError(String httpRes) {
        try {
            fli_flickrErr=flickResult(httpRes, "err", "msg");
            fli_flickrErrCode=Integer.parseInt(flickResult(httpRes, "err", "code"));
//      //#debug
//      System.out.println("errMsg:" + fli_flickrErr);
////#debug
//      System.out.println("code:" + fli_flickrErrCode);
        } catch (Throwable t) {
        }
    }

    public boolean Registration(String s) {
        try {
            fli_miniToken=s;
            String httpRes;
            String e=fli_sharedToken+"api_key"+fli_secretToken+"methodflickr.auth.getFullTokenmini_token"+fli_miniToken;
            fli_md5Token=MD5.getHashString(e);
            httpRes="";
            httpRes=HTTPUtils.getHTTPContentAsString("http://www.flickr.com/services/rest/?method=flickr.auth.getFullToken&api_key="+fli_secretToken+"&mini_token="+fli_miniToken+"&api_sig="+fli_md5Token);
            fli_userToken=flickrResult(httpRes, "token");

            //a_net_landspurg_util_Settings_fld.setStringProperty("token", b);
            if (!fli_userToken.equals(MapUtil.emptyString)){
                RMSOption.setStringOpt(RMSOption.SO_FLICKRFULL, fli_userToken);
                return true;
            }
            flickrError(httpRes);
//#debug
//#             System.out.println("errMsg:"+fli_flickrErr);
//#debug
//#             System.out.println("code:"+fli_flickrErrCode);
            return false;
        } catch (Exception exception) {
//#debug
//#             exception.printStackTrace();
            return false;
        }
    }

    public boolean sendLonLat(String photo_id, String lat, String lon) {
        String httpRes;
        try {
//#debug
//#             System.out.println("goint to send latlon");
            String e=fli_sharedToken+"api_key"+fli_secretToken+"auth_token"+fli_userToken+"lat"+lat+"lon"+lon+"methodflickr.photos.geo.setLocationphoto_id"+photo_id;
            //String e = fli_sharedToken + "api_key" + fli_secretToken + "auth_token" + fli_userToken;
            fli_md5Token=MD5.getHashString(e);

//      httpRes = HTTPUtils.sendPostRequestS("http://api.flickr.com/services/rest/",
//          new String[]{"api_key","auth_token","api_sig","lat","lon","method","photo_id"}
//      ,new String[]{fli_secretToken,fli_userToken,fli_md5Token,lat,lon,"flickr.photos.geo.setLocation",photo_id});

            httpRes=HTTPUtils.getHTTPContentAsString("http://api.flickr.com/services/rest/?method=flickr.photos.geo.setLocation&api_key="+fli_secretToken+"&auth_token="+fli_userToken+"&photo_id="+photo_id+"&lat="+lat+"&lon="+lon+"&api_sig="+fli_md5Token);
            if (httpRes!=null){
                if (httpRes.trim().equals(MapUtil.emptyString)){
                    fli_md5Token=MD5.getHashString(e);
                    httpRes=HTTPUtils.getHTTPContentAsString("http://api.flickr.com/services/rest/?method=flickr.photos.geo.setLocation&api_key="+fli_secretToken+"&auth_token="+fli_userToken+"&photo_id="+photo_id+"&lat="+lat+"&lon="+lon+"&api_sig="+fli_md5Token);
                }
            }
            //#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("sLL POST RES:\n"+httpRes);
//#             }
//#enddebug      
            httpRes=flickrResult(httpRes, "resp");

//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("sLL fR:\n"+httpRes);
//#             }
//#enddebug
            if (!httpRes.equals(MapUtil.emptyString)){
                return true;
            }
            flickrError(httpRes);
            return false;
        } catch (Exception exception) {
            //#debug
//#             exception.printStackTrace();
            return false;
        }
    }
//    public String SendPhoto(byte abyte0[], String s, String s1, String s2, boolean flag)

    public String SendPhoto(byte[] abyte0, String title, String description, String tags) {
        //flag = true;
        //#mdebug
//#         if (RMSOption.debugEnabled){
//#             if (abyte0!=null){
//#                 DebugLog.add2Log("FL SPF: L="+abyte0.length);
//#             }
//#         }
//#enddebug    
        String a_title=title;
        String a_desc=description;
        String a_tags="mapnav cameraphone "+tags;
        String a_ispub="1";
        String a_isfr="1";
        String a_isfam="1";
        String e=fli_sharedToken+"api_key"+fli_secretToken+"auth_token"+fli_userToken;
        if (!a_desc.equals(MapUtil.emptyString)){
            e+="description"+a_desc;
        }
        if (!a_isfam.equals(MapUtil.emptyString)){
            e+="is_family"+a_isfam;
        }
        if (!a_isfr.equals(MapUtil.emptyString)){
            e+="is_friend"+a_isfr;
        }
        if (!a_ispub.equals(MapUtil.emptyString)){
            e+="is_public"+a_ispub;
        }
        if (!a_tags.equals(MapUtil.emptyString)){
            e+="tags"+a_tags;
        }
        if (!a_title.equals(MapUtil.emptyString)){
            e+="title"+a_title;
        }
        fli_md5Token=MD5.getHashString(e);
        String ts=HTTPUtils.getTS();
        StringBuffer sb=new StringBuffer();
        // if(flag)
        //{

//    HTTPUtils.appendParam(sb,"api_key",fli_secretToken,ts);
//    HTTPUtils.appendParam(sb,"auth_token",fli_userToken,ts);
//    HTTPUtils.appendParam(sb,"api_sig",fli_md5Token,ts);
        HTTPUtils.packParams(sb, new String[]{"api_key", "auth_token", "api_sig"}, new String[]{fli_secretToken, fli_userToken, fli_md5Token}, ts);

        //sb.append("--" + ts + "\r\n" + "Content-Disposition: form-data; name=\"api_key\"\r\n" + "\r\n" + fli_secretToken + "\r\n" + "--" + ts + "\r\n" + "Content-Disposition: form-data; name=\"auth_token\"\r\n" + "\r\n" + fli_userToken + "\r\n" + "--" + ts + "\r\n" + "Content-Disposition: form-data; name=\"api_sig\"\r\n" + "\r\n" + fli_md5Token + "\r\n");
        if (!a_title.equals(MapUtil.emptyString)){
            HTTPUtils.appendParam(sb, "title", a_title, ts);
            //  sb.append("--" + ts + "\r\n" + "Content-Disposition: form-data; name=\"title\"\r\n" + "\r\n" + a_title + "\r\n");
        }
        if (!a_desc.equals(MapUtil.emptyString)){
            HTTPUtils.appendParam(sb, "description", a_desc, ts);
//      sb.append("--" + ts + "\r\n" + "Content-Disposition: form-data; name=\"description\"\r\n" + "\r\n" + a_desc + "\r\n");
        }
        if (!a_tags.equals(MapUtil.emptyString)){
            HTTPUtils.appendParam(sb, "tags", a_tags, ts);
//      sb.append("--" + ts + "\r\n" + "Content-Disposition: form-data; name=\"tags\"\r\n" + "\r\n" + a_tags + "\r\n");
        }
        if (!a_ispub.equals(MapUtil.emptyString)){
            HTTPUtils.appendParam(sb, "is_public", a_ispub, ts);
//      sb.append("--" + ts + "\r\n" + "Content-Disposition: form-data; name=\"is_public\"\r\n" + "\r\n" + a_ispub + "\r\n");
        }
        if (!a_isfr.equals(MapUtil.emptyString)){
            HTTPUtils.appendParam(sb, "is_friend", a_isfr, ts);
//      sb.append("--" + ts + "\r\n" + "Content-Disposition: form-data; name=\"is_friend\"\r\n" + "\r\n" + a_isfr + "\r\n");
        }
        if (!a_isfam.equals(MapUtil.emptyString)){
            HTTPUtils.appendParam(sb, "is_family", a_isfam, ts);
//      sb.append("--" + ts + "\r\n" + "Content-Disposition: form-data; name=\"is_family\"\r\n" + "\r\n" + a_isfam + "\r\n");
//        } else
//        {
//            stringbuffer.append("--" + s9 + "\r\n" + "Content-Disposition: form-data; name=\"email\"\r\n" + "\r\n" + "sushietsashimi" + "\r\n" + "--" + s9 + "\r\n" + "Content-Disposition: form-data; name=\"password\"\r\n" + "\r\n" + "digiwiz" + "\r\n");
//        }
        }
        sb.append("--"+ts+"\r\n"+"Content-Disposition: form-data; name=\"photo\"; filename=\"test.jpg\"\r\n"+"Content-Type: image/jpeg\r\n"+"\r\n");
        //String s10 = new String(abyte0);
        //stringbuffer.append(s10);
        //s10 = null;
        //stringbuffer.append("\r\n--" + s9 + "--\r\n");
        String s11="http://api.flickr.com/services/upload/";//http://www.flickr.com/services/upload/";
        //  else
        //       s11 = "http://www.flickr.com/tools/uploader_go.gne";
        String httpRes=HTTPUtils.sendDataPostRequestS(sb.toString(), abyte0, "\r\n\r\n--"+ts+"--\r\n", s11, ts, this);

//String s12 = HTTPUtils.sendDataPostRequest(stringbuffer.toString(), s11, s9);
        //String s12 = sendPostRequest(stringbuffer.toString(), s11, s9);
//#debug
//#         System.out.println("POST RES:\n"+httpRes);

        //#mdebug
//#         if (RMSOption.debugEnabled){
//#             DebugLog.add2Log("FL RES:"+httpRes);
//#         }
//#enddebug 
        String s13=flickrResult(httpRes, "photoid");
        if ((s13.equals(""))){
            flickrError(httpRes);
            s13=null;
        }
        return s13;
    }
//    public boolean BlogPhoto(String s, String s1, String s2, String s3)
//    {
//        e = c + "api_key" + a_java_lang_String_fld + "auth_token" + b + "blog_id" + s1 + "description" + s3 + "methodflickr.blogs.postPhoto" + "photo_id" + s + "title" + s2;
//        d = MD5.getHashString(e);
//        String s4 = a();
//        String s5 = "--" + s4 + "\r\n" + "Content-Disposition: form-data; name=\"api_key\"\r\n" + "\r\n" + a_java_lang_String_fld + "\r\n" + "--" + s4 + "\r\n" + "Content-Disposition: form-data; name=\"auth_token\"\r\n" + "\r\n" + b + "\r\n" + "--" + s4 + "\r\n" + "Content-Disposition: form-data; name=\"blog_id\"\r\n" + "\r\n" + s1 + "\r\n" + "--" + s4 + "\r\n" + "Content-Disposition: form-data; name=\"description\"\r\n" + "\r\n" + s3 + "\r\n" + "--" + s4 + "\r\n" + "Content-Disposition: form-data; name=\"photo_id\"\r\n" + "\r\n" + s + "\r\n" + "--" + s4 + "\r\n" + "Content-Disposition: form-data; name=\"title\"\r\n" + "\r\n" + s2 + "\r\n" + "--" + s4 + "\r\n" + "Content-Disposition: form-data; name=\"api_sig\"\r\n" + "\r\n" + d + "\r\n" + "--" + s4 + "--\r\n";
//        String s6 = HTTPUtils.sendPostRequestS(s5, "http://www.flickr.com/services/rest/?method=flickr.blogs.postPhoto", s4);
//        return s6.indexOf("stat=\"ok\"") > -1;
//    }
//    public boolean AddToPhotoSet(String s, String s1)
//    {
//        e = c + "api_key" + a_java_lang_String_fld + "auth_token" + b + "methodflickr.photosets.addPhoto" + "photo_id" + s + "photoset_id" + s1;
//        d = MD5.getHashString(e);
//        String s2 = HTTPUtils.getTS();
//        String s3 = "--" + s2 + "\r\n" + "Content-Disposition: form-data; name=\"api_key\"\r\n" + "\r\n" + a_java_lang_String_fld + "\r\n" + "--" + s2 + "\r\n" + "Content-Disposition: form-data; name=\"auth_token\"\r\n" + "\r\n" + b + "\r\n" + "--" + s2 + "\r\n" + "Content-Disposition: form-data; name=\"photo_id\"\r\n" + "\r\n" + s + "\r\n" + "--" + s2 + "\r\n" + "Content-Disposition: form-data; name=\"photoset_id\"\r\n" + "\r\n" + s1 + "\r\n" + "--" + s2 + "\r\n" + "Content-Disposition: form-data; name=\"api_sig\"\r\n" + "\r\n" + d + "\r\n" + "--" + s2 + "--\r\n";
//        String s4 = HTTPUtils.sendPostRequestS(s3, "http://www.flickr.com/services/rest/?method=flickr.photosets.addPhoto", s2);
//        return s4.indexOf("stat=\"ok\"") > -1;
//    }
//
//    public boolean AddToGroup(String s, String s1)
//    {
//        e = c + "api_key" + a_java_lang_String_fld + "auth_token" + b + "group_id" + s1 + "methodflickr.groups.pools.add" + "photo_id" + s;
//        d = MD5.getHashString(e);
//        String s2 = HTTPUtils.getTS();
//        String s3 = "--" + s2 + "\r\n" + "Content-Disposition: form-data; name=\"api_key\"\r\n" + "\r\n" + a_java_lang_String_fld + "\r\n" + "--" + s2 + "\r\n" + "Content-Disposition: form-data; name=\"auth_token\"\r\n" + "\r\n" + b + "\r\n" + "--" + s2 + "\r\n" + "Content-Disposition: form-data; name=\"photo_id\"\r\n" + "\r\n" + s + "\r\n" + "--" + s2 + "\r\n" + "Content-Disposition: form-data; name=\"group_id\"\r\n" + "\r\n" + s1 + "\r\n" + "--" + s2 + "\r\n" + "Content-Disposition: form-data; name=\"api_sig\"\r\n" + "\r\n" + d + "\r\n" + "--" + s2 + "--\r\n";
//        String s4 = HTTPUtils.sendPostRequestS(s3, "http://www.flickr.com/services/rest/?method=flickr.groups.pools.add", s2);
//        return s4.indexOf("stat=\"ok\"") > -1;
//    }
//    public void GetBlogList()
//    {
//        e = c + "api_key" + a_java_lang_String_fld + "auth_token" + b + "methodflickr.blogs.getList";
//        d = MD5.getHashString(e);
//        String s = HTTPUtils.getHTTPContentAsString("http://www.flickr.com/services/rest/?method=flickr.blogs.getList&api_key=" + a_java_lang_String_fld + "&auth_token=" + b + "&api_sig=" + d);
//        String s1 = s.toString();
//        int i;
//        for(i = 0; s1.indexOf("<blog id") > -1; i++)
//            s1 = s1.substring(s1.indexOf("<blog id") + 8);
//
//        String as[][] = new String[2][i];
//        for(int j = 0; s.indexOf("<blog id") > -1; j++)
//        {
//            int k = s.indexOf("<blog id");
//            int l = s.indexOf("id=\"", k);
//            int i1 = s.indexOf("name=\"", l);
//            String s2 = s.substring(l + 4, s.indexOf("\"", l + 4));
//            String s3 = s.substring(i1 + 6, s.indexOf("\"", i1 + 6));
//            as[0][j] = s2;
//            as[1][j] = s3;
//            s = s.substring(i1 + 8);
//        }
//
//        Blogs = as;
//    }
//    public void GetSetsList()
//    {
//        e = c + "api_key" + a_java_lang_String_fld + "auth_token" + b + "methodflickr.photosets.getList";
//        d = MD5.getHashString(e);
//        String s = HTTPUtils.getHTTPContentAsString("http://www.flickr.com/services/rest/?method=flickr.photosets.getList&api_key=" + a_java_lang_String_fld + "&auth_token=" + b + "&api_sig=" + d);
//        String s1 = s.toString();
//        int i;
//        for(i = 0; s1.indexOf("<photoset id") > -1; i++)
//            s1 = s1.substring(s1.indexOf("<photoset id") + 12);
//
//        String as[][] = new String[2][i];
//        for(int j = 0; s.indexOf("<photoset id") > -1; j++)
//        {
//            int k = s.indexOf("<photoset id");
//            int l = s.indexOf("id=\"", k);
//            int i1 = s.indexOf("<title>", l);
//            String s2 = s.substring(l + 4, s.indexOf("\"", l + 4));
//            String s3 = s.substring(i1 + 7, s.indexOf("</title>", i1 + 7));
//            as[0][j] = s2;
//            as[1][j] = s3;
//            s = s.substring(i1 + 12);
//        }
//
//        Sets = as;
//    }
//    public void GetTagsList()
//    {
//        e = c + "api_key" + a_java_lang_String_fld + "auth_token" + b + "count10methodflickr.tags.getListUserPopular";
//        d = MD5.getHashString(e);
//        String s = HTTPUtils.getHTTPContentAsString("http://www.flickr.com/services/rest/?method=flickr.tags.getListUserPopular&api_key=" + a_java_lang_String_fld + "&auth_token=" + b + "&api_sig=" + d + "&count=10");
//        String s1 = s.toString();
//        int i;
//        for(i = 0; s1.indexOf("<tag count") > -1; i++)
//            s1 = s1.substring(s1.indexOf("<tag count") + 10);
//
//        if(s.indexOf(">MapNav<") > -1)
//            i--;
//        if(s.indexOf(">cameraphone<") > -1)
//            i--;
//        String as[] = new String[i];
//        int j = 0;
//        do
//        {
//            if(s.indexOf("<tag count") <= -1)
//                break;
//            int k = s.indexOf("<tag count");
//            String s2 = s.substring(s.indexOf(">", k + 10) + 1, s.indexOf("<", k + 10));
//            s = s.substring(k + 10);
//            if(!s2.equals(new String("j2memap")) && !s2.equals(new String("cameraphone")))
//            {
//                as[j] = s2;
//                j++;
//            }
//        } while(true);
//        Tags = as;
//    }
//    public void GetGroupsPoolsList()
//    {
//        e = c + "api_key" + a_java_lang_String_fld + "auth_token" + b + "methodflickr.groups.pools.getGroups";
//        d = MD5.getHashString(e);
//        String s = HTTPUtils.getHTTPContentAsString("http://www.flickr.com/services/rest/?method=flickr.groups.pools.getGroups&api_key=" + a_java_lang_String_fld + "&auth_token=" + b + "&api_sig=" + d);
//        String s1 = s.toString();
//        int i;
//        for(i = 0; s1.indexOf("<group ") > -1; i++)
//            s1 = s1.substring(s1.indexOf("<group ") + 7);
//
//        String as[][] = new String[2][i];
//        for(int j = 0; s.indexOf("<group ") > -1; j++)
//        {
//            int k = s.indexOf("<group ");
//            int l = s.indexOf("nsid=\"", k);
//            int i1 = s.indexOf("name=\"", l);
//            String s2 = s.substring(l + 6, s.indexOf("\"", l + 6));
//            String s3 = s.substring(i1 + 6, s.indexOf("\"", i1 + 7));
//            as[0][j] = s2;
//            as[1][j] = s3;
//            s = s.substring(i1 + 1);
//        }
//
//        GroupsPools = as;
//    }

    private String flickrResult(String s, String s1) {
        String s2="<"+s1+">";
        String s3="</"+s1+">";
        if (s.indexOf(s2)>-1&&s.indexOf(s3)>-1){
            if (s.indexOf(s2)+s2.length()<s.indexOf(s3)){
                return s.substring(s.indexOf(s2)+s2.length(), s.indexOf(s3));
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    private String flickResult(String s, String s1, String s2) {
        String s3="<"+s1;
        String s4="/>";
        if (s.indexOf(s3)>-1&&s.indexOf(s4)>-1&&s.indexOf(s3)+s3.length()<s.indexOf(s4)){
            String s5=s.substring(s.indexOf(s3)+s3.length(), s.indexOf(s4));
            //System.out.println(s5);
            s3=s2+"=\"";
            s4="\"";
            int i=s5.indexOf(s3);
            if (i>-1&&s5.indexOf(s4, i)>-1&&s5.indexOf(s3)+s3.length()<s5.indexOf(s4, i+s3.length())){
                //  System.out.println((s5.indexOf(s3)+s3.length())+"\n"+s5.indexOf(s4, i+s3.length()));
                return s5.substring(s5.indexOf(s3)+s3.length(), s5.indexOf(s4, i+s3.length()));
            }
        }
        return "";
    }
}
