/*
 * BrowseList.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package misc;

import RPMap.MapCanvas;
import RPMap.MapUtil;
import RPMap.RMSOption;
import app.MapForms;
import java.util.Enumeration;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextField;
import lang.Lang;
import lang.LangHolder;

/**
 *
 * @author RFK
 */
public class BrowseList extends List implements CommandListener, Runnable{
    private static String FILE="file:///";
    private static String DIR_END=".";
    private static String DIR_SEP="/";
  private Displayable backDisp;
  private String[] path = new String[20];
  private int plevel=-1;
  private TextField fn;
  private TextField fn_clear;
  private byte kind;
  private String extFilter;
  public final static byte FILEBROWSE = 1;
  public final static byte DIRBROWSE = 2;
  /** Creates a new instance of BrowseList */
  private final static String listCap(byte bk) {
    if (bk==FILEBROWSE)
      return LangHolder.getString(Lang.browsefile);
    else //if (bk==DIRBROWSE)
      return LangHolder.getString(Lang.browsedir);
  }
  private BrowseList(byte brkind,String extFilter) {
    super((extFilter==null)?listCap(brkind):listCap(brkind)+extFilter,List.IMPLICIT);
    this.extFilter=extFilter;
    kind = brkind;
    backCommand=new Command(LangHolder.getString(Lang.back), Command.BACK, 3);
    cancelCommand=new Command(LangHolder.getString(Lang.cancel), Command.CANCEL, 99);
    openCommand=new Command(LangHolder.getString(Lang.open), Command.ITEM, 1);
    addCommand(backCommand);
    addCommand(cancelCommand);
    addCommand(openCommand);
    setCommandListener(this);
    setSelectCommand(openCommand);
    
  }
  public BrowseList(Displayable backForm,TextField fileName,byte brkind,String extFilter,TextField textFNClear) {
    super((extFilter==null)?listCap(brkind):listCap(brkind)+extFilter,List.IMPLICIT);
    this.extFilter=extFilter;
    
    backDisp=backForm;
    fn=fileName;
    fn_clear=textFNClear;
    kind = brkind;
    //String s=System.getProperty("microedition.io.file.FileConnection.version");
    //if (s==null) throw new Exception("No FileConnection exists");
    
//#if SE_K750_E_BASEDEV
//#else
    String s = RMSOption.getStringOpt(RMSOption.SO_WORKPATH);
    if (s.equals(MapUtil.emptyString)) {
      Enumeration e =  javax.microedition.io.file.FileSystemRegistry.listRoots();
      while (e.hasMoreElements()) {
        String r=(String)e.nextElement();
        append(r,MapForms.mm.getImageFold());
      }
      e=null;
      plevel=0;
    } else{
      try{
        
        plevel=0;
        MapUtil.parseString(DIR_SEP+s,'/',path);
        for (int i=0;i<path.length;i++)
          if (path[i]!=null)
            if (!path[i].equals(MapUtil.emptyString)){
          path[i]=path[i]+DIR_SEP;
          plevel++;
            }
        plevel--;
        currPath=s;
        
        Thread t = new Thread(this);
        t.start();
      }catch(Throwable t){
        Enumeration e =  javax.microedition.io.file.FileSystemRegistry.listRoots();
        while (e.hasMoreElements()) {
          String r=(String)e.nextElement();
          append(r,MapForms.mm.getImageFold());
        }
        e=null;
        plevel=0;
      }
    }
//#endif
    
    //append("dummy",null);
    
    backCommand=new Command(LangHolder.getString(Lang.back), Command.BACK, 3);
    cancelCommand=new Command(LangHolder.getString(Lang.cancel), Command.CANCEL, 99);
    openCommand=new Command(LangHolder.getString(Lang.open), Command.ITEM, 1);
    addCommand(backCommand);
    addCommand(cancelCommand);
    addCommand(openCommand);
    setCommandListener(this);
    setSelectCommand(openCommand);
    
//#if SE_K750_E_BASEDEV
//#    String s = RMSOption.getStringOpt(RMSOption.SO_WORKPATH);
//#    if (!s.equals(MapUtil.emptyString))
//#     try{
//#         plevel=0;
//#         MapUtil.parseString('/'+s,'/',path);
//#         for (int i=0;i<path.length;i++)
//#           if (path[i]!=null)
//#           if (!path[i].equals(MapUtil.emptyString)){
//#           path[i]=path[i]+'/';
//#           plevel++;
//#           }
//#         plevel--;
//#         currPath=s;
//#       }catch(Throwable t){
//#         plevel=0;
//#       }
//#
//#     Thread t = new Thread(this);
//#     t.start();
//#endif
    
  }
  
  private Command backCommand,cancelCommand,openCommand;
  private String currPath;
  public void commandAction(Command command, Displayable displayable) {
    if (displayable == this) {
      if (command == cancelCommand) {
        //fileName=null;
        MapCanvas.setCurrent(backDisp);
        System.gc();
      } else
        if (command == backCommand) {
        if (plevel==0) {MapCanvas.setCurrent(backDisp);return;}
        plevel--;
        plevel--;
        currPath=MapUtil.emptyString;
        for (int i=0;i<=plevel;i++) currPath=currPath+path[i];
        Thread t = new Thread(this);
        t.start();
        } else
          if (command == openCommand) {
        path[plevel]=getString(getSelectedIndex());
        currPath=MapUtil.emptyString;
        for (int i=0;i<=plevel;i++) currPath=currPath+path[i];
        Thread t = new Thread(this);
        t.start();
          }
    }
    
  }
  public void run() {
    BrowseList bl = new BrowseList(kind,extFilter);
    bl.backDisp=backDisp;
    bl.path=path;
    bl.plevel=plevel;
    bl.fn = fn;
    bl.fn_clear=fn_clear;
    bl.currPath = currPath;
    
    try {
      //deleteAll();
      
      Enumeration e;
      bl.plevel++;
      if ( bl.plevel==0) {
        e =  FileSystemRegistry.listRoots();
        while (e.hasMoreElements()) {
          String r=(String)e.nextElement();
          bl.append(r,MapForms.mm.getImageFold());
        }
        e=null;
      } else {
        if (kind==DIRBROWSE) bl.append(DIR_END,null);
        if (currPath.endsWith(DIR_SEP)) {
          FileConnection fc=null;
          try{ fc = (FileConnection)Connector.open(FILE+ bl.currPath,Connector.READ);
          
          //if (fc.isDirectory()) {
          //fc.setWritable(false);
          e =  fc.list();
          while (e.hasMoreElements()) {
            String r=(String)e.nextElement();
            
            if (kind==FILEBROWSE) {
              
              if (extFilter!=null){
                if (!r.endsWith(    DIR_SEP))
                  if (!r.toUpperCase().endsWith(extFilter)) continue;
              }
              
              if (r.toUpperCase().endsWith(".WPT")) bl.append(r,MapForms.mm.getImageWPTs());
              else if (r.toUpperCase().endsWith(".RTE")) bl.append(r,MapForms.mm.getImageRoutes());
              else if (r.toUpperCase().endsWith(".PLT")) bl.append(r,MapForms.mm.getImageTracks());
              else if (r.toUpperCase().endsWith(".MNM")) bl.append(r,MapForms.mm.getImageMap());
              else if (r.toUpperCase().endsWith(".MNO")) bl.append(r,MapForms.mm.getImageMapE());
              else if (r.toUpperCase().endsWith(DIR_SEP)) bl.append(r,MapForms.mm.getImageFold());
              else bl.append(r,null);
              
            } else if (kind==DIRBROWSE)
              if (r.endsWith(DIR_SEP)) bl.append(r,MapForms.mm.getImageFold());
            
          }
          }finally{
            e=null;
            fc.close();
          }
        } else {
          if (kind==DIRBROWSE) fn.setString(currPath.substring(0,currPath.length()-1));
          else fn.setString(currPath);
          
          if (fn_clear!=null)
            try{
              String ss=currPath.substring(currPath.lastIndexOf('/')+1);
              fn_clear.setString(ss.substring(0,ss.lastIndexOf('.')));
            }catch(Throwable t){}
          
          MapCanvas.setCurrent(backDisp);
          System.gc();
          return;
        }
        
      }
    }catch (Throwable e) {
//#mdebug
      if (RMSOption.debugEnabled)
        DebugLog.add2Log("Browse:"+e.toString()+"\nPath:"+currPath);
//#enddebug
    }
    //   if (size()>0)
    //     setSelectedIndex(0,true);
    
    MapCanvas.setCurrent(bl);
  }
}
