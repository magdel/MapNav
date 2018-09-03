/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package misc;

import RPMap.MapCanvas;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.*;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.*;
import lang.Lang;
import lang.LangHolder;

/**
 *
 * @author rfk
 */
public class FileDialog implements CommandListener, Runnable, ProgressStoppable {

  private FileDialog() {
  }
  private static FileDialog saveDialog;
  Form fileForm;
  ProgressReadWritable pRW;
  Displayable backDisp;
  Command mainCommand,
    backCommand=new Command(LangHolder.getString(Lang.back), Command.BACK, 5),
    browseCommand=new Command(LangHolder.getString(Lang.browse), Command.ITEM, 1);
  Item[] items;
  TextField textFileName;
  String fileExt, name;
  private static final byte DLG_SAVE=1;
  private static final byte DLG_LOAD=2;
  private byte dlgType;

  private static void createAndShow(byte dlgType) {
    saveDialog.dlgType=dlgType;
    if (dlgType==DLG_SAVE){
      saveDialog.fileForm=new Form(LangHolder.getString(Lang.save)+":"+saveDialog.name);
      saveDialog.mainCommand=new Command(LangHolder.getString(Lang.save), Command.ITEM, 2);
      saveDialog.textFileName=new TextField(LangHolder.getString(Lang.sendaddrfile)+"\n", null, 128, TextField.ANY);
    } else if (dlgType==DLG_LOAD){
      saveDialog.fileForm=new Form(LangHolder.getString(Lang.load)+":"+saveDialog.name);
      saveDialog.mainCommand=new Command(LangHolder.getString(Lang.load), Command.ITEM, 2);
      saveDialog.textFileName=new TextField(LangHolder.getString(Lang.urlfile)+"\n", null, 128, TextField.ANY);
    }

    saveDialog.fileForm.append(saveDialog.textFileName);
    for (int i=0; i<saveDialog.items.length; i++) {
      saveDialog.fileForm.append(saveDialog.items[i]);
    }

    saveDialog.fileForm.addCommand(saveDialog.mainCommand);
    saveDialog.fileForm.addCommand(saveDialog.backCommand);
    saveDialog.fileForm.addCommand(saveDialog.browseCommand);
    saveDialog.fileForm.setCommandListener(saveDialog);

    MapCanvas.setCurrent(saveDialog.fileForm);

  }

  public static void showSaveForm(String name, Item[] items, ProgressReadWritable pRW, Displayable backDisp, String fileExt) {
    saveDialog=new FileDialog();
    saveDialog.fileExt=fileExt;
    saveDialog.name=name;
    saveDialog.pRW=pRW;
    saveDialog.backDisp=backDisp;
    saveDialog.items=items;
    createAndShow(DLG_SAVE);
  }

  public static void showLoadForm(String name, Item[] items, ProgressReadWritable pRW, Displayable backDisp, String fileExt) {
    saveDialog=new FileDialog();
    saveDialog.fileExt=fileExt;
    saveDialog.name=name;
    saveDialog.pRW=pRW;
    saveDialog.backDisp=backDisp;
    saveDialog.items=items;
    createAndShow(DLG_LOAD);
  }

  public void commandAction(Command command, Displayable displayable) {
    if (displayable==fileForm){
      if (command==mainCommand){
        //saveDialog=null;
        //MapCanvas.setCurrent(backDisp);
        MapCanvas.setCurrent(new ProgressForm((dlgType==DLG_LOAD)?LangHolder.getString(Lang.loading):LangHolder.getString(Lang.saving), name, this, fileForm));
        (new Thread(this)).start();

      } else if (command==backCommand){
        saveDialog=null;
        MapCanvas.setCurrent(backDisp);
      } else if (command==browseCommand){
        if (dlgType==DLG_SAVE){
          MapCanvas.setCurrent(new BrowseList(fileForm, textFileName, BrowseList.DIRBROWSE, null, null));
        } else {
          MapCanvas.setCurrent(new BrowseList(fileForm, textFileName, BrowseList.FILEBROWSE, fileExt, null));
        }
      }
    }
  }

  public void run() {
    String fn=textFileName.getString();
    if (dlgType==DLG_SAVE) {
      if (fn.endsWith("_")) {
        fn+=fileExt;
      } else {
        fn+="_"+fileExt;
      }
    }
    try {
      StreamConnection fc;
      if (fn.startsWith("http://")){
        fc=(StreamConnection) Connector.open(fn);
      } else {
        fc=(StreamConnection) Connector.open("file:///"+fn);
      }
      try {
        if (dlgType==DLG_SAVE){
          if (((FileConnection) fc).exists()){
            throw new Exception("File already exists!");
          }
          ((FileConnection) fc).create();
          OutputStream os=fc.openOutputStream();
          try {
            pRW.writeData(os, items);
          } finally {
            os.close();
          }
        } else if (dlgType==DLG_LOAD){
          InputStream is=fc.openInputStream();
          try {
            pRW.readData(is, items);
          } finally {
            is.close();
          }
        }
      } finally {
        fc.close();
      }

      MapCanvas.showmsg("OK", (dlgType==DLG_SAVE)?LangHolder.getString(Lang.saved)+" "+fn:LangHolder.getString(Lang.loaded)+" "+fn,
        AlertType.INFO, backDisp);
    } catch (Throwable t) {
      MapCanvas.showmsgmodal("Error", t.toString()+" "+fn, AlertType.ERROR, fileForm);
    }

  }

  public void setProgressResponse(ProgressResponse progressResponse) {
    pRW.setProgressResponse(progressResponse);
  }

  public boolean stopIt() {
    pRW.stopIt();
            return true;

  }
}
