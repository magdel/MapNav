/*
 * FileGPSRouteLoader.java
 *
 * Created on 15 Январь 2007 г., 17:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package RPMap;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Displayable;

/**
 *
 * @author Raev
 */
public class FileMapRouteLoader extends MapRouteLoader {
  
  /** Creates a new instance of FileGPSRouteLoader */
  public FileMapRouteLoader(String url,byte kind, byte CP,byte format) {
    super(url,kind,CP,format);
  }
  
  void load() {
    
    FileConnection conn = null;
    InputStream is = null;
    String err = MapUtil.emptyString; // used for debugging
    
    try {
      try {
        conn = (FileConnection)Connector.open("file:///"+furl,Connector.READ);
        is = conn.openInputStream();
        loadFromStream(is); 
        
      }finally { 
        try{Thread.sleep(500);}catch(Throwable re){}
        is.close();is=null;
      conn.close();conn=null;
      }
    } catch (IOException i1oe) {
//            paintState("������ ����� �����");
    };
  
  }
  
}
