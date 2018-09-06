/*
 * RMSRoute.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package RPMap;

import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author Raev
 */
public class RMSRoute {
  String routeName;
  int ptC;
  public int rId;
  
  public int mnTrackId;
  public int osmTrackId;
  public boolean saved;

  
  /** Creates a new instance of RMSRoute */
  public RMSRoute(DataInputStream in, int Id) throws IOException {
    routeName=in.readUTF();
    in.readInt();
    in.readByte();
    in.readByte();
    ptC=in.readInt();
    in.readByte();
    
    rId=Id;
    
    if (in.available()>0){
      int v = in.readInt();
      mnTrackId = in.readInt();
      osmTrackId = in.readInt();
      saved = in.readBoolean();
    }
    
  }
  
}
