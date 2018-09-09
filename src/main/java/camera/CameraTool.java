/*
 * CameraTool.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package camera;

import RPMap.FileScreenSend;
import RPMap.MapCanvas;
import RPMap.RMSOption;
import app.MapForms;
import gpspack.GPSReader;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.TextField;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.control.VideoControl;
import lang.Lang;
import lang.LangHolder;
import misc.ProgressForm;

/**
 *
 * @author RFK
 */
public class CameraTool implements CommandListener, Runnable {
  
//  /** Creates a new instance of CameraTool */
//  public CameraTool() {
//  }
  
  private Form captureForm;
  private Command photoCommand,disCommand,sendCommand,saveCommand;
  private Player player;
  private VideoControl control;
  public byte[] photoBits;
  //private Image photo;
  private String uploadedPhotoId;
  
  
  private boolean autoshot;
  public void initCamera(boolean autoshot) throws Exception {
    // use capture://image on nokia series 40 and capture://video on WTK and elsewhere
    try {
      player = Manager.createPlayer("capture://image");
    } catch(Exception exception) {
      player = Manager.createPlayer("capture://video");
    }
    
    player.realize();
    control = (VideoControl) player.getControl("VideoControl");
    if (control != null) {
      captureForm = new Form("Take Photo");
      captureForm.append((Item) control.initDisplayMode(control.USE_GUI_PRIMITIVE, null));
      
//      javax.microedition.amms.control.FormatControl
//� javax.microedition.amms.control.ImageFormatControl
//� javax.microedition.amms.control.camera.CameraControl
//All methods supported, except getCameraRotation()
//� javax.microedition.amms.control.camera.ExposureControl
//Not supported: setExposureTime(), getExposureTime(), getExposureValue(), getFStop
//and setFStop()
      //javax.microedition.amms.control.camera.FocusControl.
      
      
      sendCommand = new Command("Take",Command.ITEM,1);
      disCommand = new Command(LangHolder.getString(Lang.back),Command.BACK,3);
      captureForm.addCommand(sendCommand);
      captureForm.addCommand(disCommand);
      captureForm.setCommandListener(this);
      MapCanvas.setCurrent(captureForm);
      
      player.start();
      this.autoshot=autoshot;
      
      
    }
  }
  
  public Form takeForm;
  private double lat,lon;
  private void doTakePhoto() {
    lat=0;
    if (player.getState() == Player.STARTED) {
      try {
        
//        try{
//          if (MNSInfo.classExists("javax.microedition.amms.control.camera.FocusControl")){
//            FocusControl fc = (FocusControl)player.getControl("FocusControl");
//            fc.setFocus(FocusControl.AUTO);
//            Thread.sleep(1000);
//          }
//        }catch(Throwable t){}
        photoBits = control.getSnapshot(null);
        player.stop();
//        InputStream is = MapCanvas.map.getClass().getResourceAsStream("/img/t.jpg");
//        DataInputStream dis = new DataInputStream(is);
//        photoBits = new byte[16841];
//        dis.readFully(photoBits);
        
        if (MapCanvas.map.gpsActive()&&MapCanvas.map.gpsBinded) {
          lat = GPSReader.LATITUDE;
          lon = GPSReader.LONGITUDE;
        }else{
          lat = MapCanvas.reallat;
          lon = MapCanvas.reallon;
        }
        //new FileMapSend(RMSOption.lastSendFileURL,photoBits);
        //FlickrList.lastShot = Image.createImage(photoBits, 0, photoBits.length);
        if (!autoshot){
          takeForm = new Form("Photo!");
          try{
            takeForm.append(Image.createImage(photoBits, 0, photoBits.length));
          }catch(Throwable t){}
          takeForm.append(String.valueOf(photoBits.length)+" bytes");
          
          textTitle = new TextField("Title","",50,TextField.ANY);
          textTags = new TextField("Tags","",50,TextField.ANY);
          
          takeForm.append(textTitle);
          takeForm.append(textTags);
          
          saveCommand = new Command(LangHolder.getString(Lang.save),Command.ITEM,2);
          takeForm.addCommand(saveCommand);
          sendCommand = new Command(LangHolder.getString(Lang.send),Command.ITEM,1);
          takeForm.addCommand(sendCommand);
          takeForm.setCommandListener(this);
          MapCanvas.setCurrent(takeForm);
        }
      } catch (Exception ex) {
        MapCanvas.showmsg("Error taking photo",ex.toString(), AlertType.ERROR,  FlickrList.flickr);
      } finally {
        player.close();
      }
    }
  }
  TextField textTitle;
  TextField textTags;
  
  public void commandAction(Command command, Displayable displayable) {
    if (displayable==captureForm){
      if (command==sendCommand) {
        doTakePhoto();
      } else if (command==disCommand) {
        try {player.stop();}catch(Throwable  rr){}
        player=null;
        control=null;
        MapCanvas.setCurrent(FlickrList.flickr);
        captureForm=null;
      }
    } else
      if (displayable==takeForm){
      if (command==disCommand) {
        player=null;
        control=null;
        photoBits=null;
        captureForm=null;
        MapCanvas.setCurrent(FlickrList.flickr);
      } else
        if (command==saveCommand) {
        //MapMidlet.mm.back2More();
        new FileScreenSend(RMSOption.lastSendFileURL,photoBits);
        photoBits=null;
        } else if (command==sendCommand){
        MapCanvas.setCurrent(
            new ProgressForm(LangHolder.getString(Lang.sending),LangHolder.getString(Lang.waitpls),FlickrList.flickr,takeForm)
            );
        FlickrList.flickr.photo = photoBits;
        FlickrList.flickr.lat = lat;
        FlickrList.flickr.lon = lon;
        FlickrList.flickr.tags = textTags.getString();
        FlickrList.flickr.title = textTitle.getString();
        FlickrList.flickr.photofile=null;
        (new Thread(FlickrList.flickr)).start();
        }
      }
  }
  
  public void run() {
    try{
      while (true){
        Thread.sleep(1000);
        doTakePhoto();
        captureForm =null;
        
        MapCanvas.setCurrent(
            new ProgressForm(LangHolder.getString(Lang.sending),LangHolder.getString(Lang.waitpls),FlickrList.flickr,FlickrList.flickr)
            );
        FlickrList.flickr.photo = photoBits;
        FlickrList.flickr.lat = lat;
        FlickrList.flickr.lon = lon;
        FlickrList.flickr.tags = "photolog";
        FlickrList.flickr.title = "MapNav auto photo log";
        FlickrList.flickr.photofile=null;
        Thread t = new Thread(FlickrList.flickr);
        t.start();
        t.join();
        if (FlickrList.flickr.stopped) break;
        Thread.sleep(1000);
        initCamera(true);
        System.gc();
      }
    }catch(Throwable t){}
  }
  
  
  
  
  //#if MD5 && CameraPhone && MMAPI
//#     private boolean isPostError(final Response response) {
//#         boolean isError = false;
//#         final Exception ex = response.getException();
//#         if (ex != null) {
//#             isError = true;
//#             errorMessage = ex.getMessage();
//#         } else {
//#             try {
//#                 final Result result = response.getResult();
//#                 final String stat = result.getAsString("rsp.stat");
//#                 if ("fail".equals(stat)) {
//#                     isError = true;
//#                     errorCode = result.getAsString("rsp.err.code");
//#                     errorMessage = result.getAsString("rsp.err.msg");
//#                 }
//#             } catch (ResultException rex) {
//#                 isError = true;
//#             }
//#         }
//#         return isError;
//#     }
//#endif
  
//#if MD5 && CameraPhone && MMAPI
//#     private void doAuthenticate() {
//#
//#         final String authCode1 = get_authCode1().getString();
//#         final String authCode2 = get_authCode2().getString();
//#         final String authCode3 = get_authCode3().getString();
//#         if (authCode1 == null || authCode2 == null || authCode3 == null) {
//#             alert(AlertType.ERROR, Alert.FOREVER,
//#                 "Please enter the 9-digit auth code obtained after successful login", null, null);
//#             return;
//#         }
//#
//#         final String miniToken = authCode1 + "-" + authCode2 + "-" + authCode3;
//#         if (miniToken.length() != 11) {
//#             alert(AlertType.ERROR, Alert.FOREVER, "The auth code must be 9 digits", null, null);
//#             return;
//#         }
//#
//#         // args must be sorted by key for signing to work
//#         final Arg[] args = {
//#             new Arg("api_key", API_KEY),
//#             new Arg("api_sig", null), // placeholder for the signature
//#             new Arg("format", "json"),
//#             new Arg("method", "flickr.auth.getFullToken"),
//#             new Arg("mini_token", miniToken),
//#             new Arg("nojsoncallback", "1")
//#         };
//#
//#         try {
//#             sign(args);
//#         } catch (Exception ex) {
//#             alert(AlertType.ERROR, Alert.FOREVER, "Error signing full token request", ex, get_start());
//#             return;
//#         }
//#
//#         status("Getting full token...");
//#         Request.get(WEB_API_BASE, args, null, this, get_authenticate());
//#     }
//#
//#     private void handleAuthenticateResponse(final Response response) {
//#
//#         if (isError(response)) {
//#             alert(AlertType.ERROR, Alert.FOREVER, errorCode + ": " + errorMessage, null, get_start());
//#             return;
//#         }
//#
//#         final Result result = response.getResult();
//#         System.out.println(result);
//#         try {
//#             token = result.getAsString("auth.token._content");
//#             perms = result.getAsString("auth.perms._content");
//#             userid = result.getAsString("auth.user.nsid");
//#             username = result.getAsString("auth.user.username");
//#         } catch (Exception ex) {
//#             alert(AlertType.ERROR, Alert.FOREVER, "Error extracting full token from result", ex, get_start());
//#             return;
//#         }
//#
//#         saveAuthToken();
//#
//#         status("Login successful");
//#         getDisplay().setCurrent(get_takePhoto());
//#     }
//#
//#     private void deleteAuthToken() {
//#         token = null;
//#         perms = null;
//#         userid = null;
//#         username = null;
//#         try { RecordStore.deleteRecordStore(STORE_NAME); } catch (Exception ignore) {}
//#     }
//#
//#     private boolean saveAuthToken() {
//#         RecordStore store = null;
//#         try {
//#             try { RecordStore.deleteRecordStore(STORE_NAME); } catch (Exception ignore) {}
//#             store = RecordStore.openRecordStore(STORE_NAME, true);
//#             final ByteArrayOutputStream bos = new ByteArrayOutputStream();
//#             final DataOutputStream dos = new DataOutputStream(bos);
//#             dos.writeUTF(token);
//#             dos.writeUTF(perms);
//#             dos.writeUTF(userid);
//#             dos.writeUTF(username);
//#             final byte[] bits = bos.toByteArray();
//#             store.addRecord(bits, 0, bits.length);
//#             return true;
//#         } catch (Exception ex) {
//#             return false;
//#         } finally {
//#             if (store != null) { try { store.closeRecordStore(); } catch (Exception ignore) {}}
//#         }
//#     }
//#
//#     private boolean readAuthToken() {
//#         // try to read the persisted auth token
//#         RecordStore store = null;
//#         try {
//#             store = RecordStore.openRecordStore(STORE_NAME, false);
//#             final RecordEnumeration en = store.enumerateRecords(null, null, false);
//#             if (!en.hasNextElement()) {
//#                 return false;
//#             }
//#             final ByteArrayInputStream bis = new ByteArrayInputStream(en.nextRecord());
//#             final DataInputStream dis = new DataInputStream(bis);
//#             token = dis.readUTF();
//#             perms = dis.readUTF();
//#             userid = dis.readUTF();
//#             username = dis.readUTF();
//#             return true;
//#         } catch (Exception ex) {
//#             return false;
//#         } finally {
//#             if (store != null) { try { store.closeRecordStore(); } catch (Exception ignore) {}}
//#         }
//#     }
//#endif
  
//#if MD5 && CameraPhone && MMAPI
//#     private void doPostPhoto() {
//#
//#         final Arg apiKeyArg = new Arg("api_key", API_KEY);
//#         final Arg apiSigArg = new Arg("api_sig", null); // placeholder for the signature
//#         final Arg authTokenArg = new Arg("auth_token", token);
//#         final Arg formatArg = new Arg("format", "json");
//#         final Arg isPublicArg = new Arg("is_public",
//#             "Yes".equals(get_isPublicGroup().getString(0)) ? "1" : "0");
//#         final Arg noJsonCallbackArg = new Arg("nojsoncallback", "1");
//#         final Arg photoArg = new Arg("photo", "img" + Long.toString(System.currentTimeMillis(), 16) + ".jpg");
//#         final Arg tagsArg = new Arg("tags", get_tags().getString());
//#         final Arg titleArg = new Arg("title", get_title().getString());
//#
//#         // args must be sorted by key for signing to work
//#         final Arg[] postArgs = {
//#             apiKeyArg,
//#             apiSigArg,
//#             authTokenArg,
//#             formatArg,
//#             isPublicArg,
//#             noJsonCallbackArg,
//#             photoArg,
//#             tagsArg,
//#             titleArg
//#         };
//#
//#         final Arg[] apiKeyArgs = {
//#             new Arg(Arg.CONTENT_DISPOSITION, "form-data; name=" + apiKeyArg.getKey())
//#         };
//#         final Arg[] authTokenArgs = {
//#             new Arg(Arg.CONTENT_DISPOSITION, "form-data; name=" + authTokenArg.getKey())
//#         };
//#         final Arg[] formatArgs = {
//#             new Arg(Arg.CONTENT_DISPOSITION, "form-data; name=" + formatArg.getKey())
//#         };
//#         final Arg[] isPublicArgs = {
//#             new Arg(Arg.CONTENT_DISPOSITION, "form-data; name=" + isPublicArg.getKey())
//#         };
//#         final Arg[] noJsonCallbackArgs = {
//#             new Arg(Arg.CONTENT_DISPOSITION, "form-data; name=" + noJsonCallbackArg.getKey())
//#         };
//#         final Arg[] photoArgs = {
//#             new Arg(Arg.CONTENT_TYPE, "image/jpeg"),
//#             new Arg(Arg.CONTENT_DISPOSITION, "form-data; name=" + photoArg.getKey() +
//#                 "; filename=" + photoArg.getValue())
//#         };
//#         final Arg[] tagsArgs = {
//#             new Arg(Arg.CONTENT_DISPOSITION, "form-data; name=" + tagsArg.getKey())
//#         };
//#         final Arg[] titleArgs = {
//#             new Arg(Arg.CONTENT_DISPOSITION, "form-data; name=" + titleArg.getKey())
//#         };
//#
//#         final String multipartBoundary =
//#             Long.toString(System.currentTimeMillis(), 16) +
//#             "--" + getClass().getName();
//#         final Arg[] httpArgs = {
//#             new Arg("MIME-Version", "1.0"),
//#             new Arg("Content-Type", "multipart/form-data; boundary=" + multipartBoundary)
//#         };
//#
//#         final Arg signedArg;
//#         try {
//#             signedArg = sign(postArgs);
//#         } catch (Exception ex) {
//#             alert(AlertType.ERROR, Alert.FOREVER, "Error signing upload request", ex, get_takePhoto());
//#             return;
//#         }
//#
//#         final Arg[] apiSigArgs = {
//#             new Arg(Arg.CONTENT_DISPOSITION, "form-data; name=" + signedArg.getKey())
//#         };
//#         final Part[] postData = {
//#             new Part(apiKeyArg.getValue().getBytes(), apiKeyArgs),
//#             new Part(authTokenArg.getValue().getBytes(), authTokenArgs),
//#             new Part(signedArg.getValue().getBytes(), apiSigArgs),
//#             new Part(formatArg.getValue().getBytes(), formatArgs),
//#             new Part(isPublicArg.getValue().getBytes(), isPublicArgs),
//#             new Part(noJsonCallbackArg.getValue().getBytes(), noJsonCallbackArgs),
//#             new Part(tagsArg.getValue().getBytes(), tagsArgs),
//#             new Part(titleArg.getValue().getBytes(), titleArgs),
//#             new Part(photoBits, photoArgs)
//#         };
//#         final PostData mp = new PostData(postData, multipartBoundary);
//#
//#         status("Starting upload...");
//#         Request.post(UPLOAD_BASE, null, httpArgs, this, mp, get_takePhoto());
//#     }
//#
//#     private void handlePostPhotoResponse(final Response response) {
//#
//#         if (isPostError(response)) {
//#             status(errorCode + ": " + errorMessage);
//#             return;
//#         }
//#         status("Upload successful");
//#
//#         photo = null;
//#         photoBits = null;
//#         get_title().setString(null);
//#         get_tags().setString(null);
//#         get_takePhoto().delete(get_takePhoto().size() - 1);
//#
//#         final Result result = response.getResult();
//#         uploadedPhotoId = null;
//#         try {
//#             uploadedPhotoId = result.getAsString("rsp.photoid");
//#         } catch (ResultException rex) {
//#             status("Geocoding error: " + rex.getMessage());
//#             return;
//#         }
//#
//#         doGeocoding();
//#     }
//#endif
  
//#if MD5 && CameraPhone && MMAPI
//#     private void doGeocoding() {
//#
//#         // args must be sorted by key for signing to work
//#         final Arg[] args = {
//#             new Arg("output", "xml"),
//#             new Arg("appid", GEOCODE_APPID),
//#             new Arg("location", locationGroup.getString(locationGroup.getSelectedIndex()))
//#         };
//#
//#         status("Getting coordinates...");
//#         Request.get(GEOCODE_BASE, args, null, this, get_locationGroup());
//#     }
//#
//#     private void handleGeocodeResponse(final Response response) {
//#
//#         if (isPostError(response)) {
//#             status(errorCode + ": " + errorMessage);
//#             return;
//#         }
//#         status("Got coordinates");
//#
//#         final Result result = response.getResult();
//#
//#         String latitude = null;
//#         String longitude = null;
//#         String precision = null;
//#         try {
//#             latitude = result.getAsString("ResultSet.Result.Latitude");
//#             longitude = result.getAsString("ResultSet.Result.Longitude");
//#             precision = result.getAsString("ResultSet.Result.precision");
//#         } catch (ResultException rex) {
//#             status("Error extracting coordinates: " + rex.getMessage());
//#             return;
//#         }
//#
//#         // args must be sorted by key for signing to work
//#         final Arg[] args = {
//#             new Arg("api_key", API_KEY),
//#             new Arg("api_sig", null), // placeholder for the signature
//#             new Arg("auth_token", token),
//#             new Arg("format", "json"),
//#             new Arg("lat", latitude),
//#             new Arg("lon", longitude),
//#             new Arg("method", "flickr.photos.geo.setLocation"),
//#             new Arg("nojsoncallback", "1"),
//#             new Arg("photo_id", uploadedPhotoId)
//#         };
//#
//#         try {
//#             sign(args);
//#         } catch (Exception ex) {
//#             status("Error signing setLocation request" + ex.getMessage());
//#             return;
//#         }
//#
//#         status("Adding location...");
//#         Request.get(WEB_API_BASE, args, null, this, GEOCODE_BASE);
//#     }
//#
//#     private void handleApplyGeocodeResponse(final Response response) {
//#
//#         if (isPostError(response)) {
//#             status(errorCode + ": " + errorMessage);
//#             return;
//#         }
//#         status("Placed photo");
//#
//#         try {
//#             Thread.currentThread().sleep(2000);
//#         } catch (InterruptedException ignore) {}
//#
//#         getDisplay().setCurrent(get_takePhoto());
//#     }
//#endif
  
  
  
}
