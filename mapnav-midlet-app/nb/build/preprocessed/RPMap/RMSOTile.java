/*
 * RMSTile.java
 *
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package RPMap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author julia
 */
public final class RMSOTile {
    int numX;
    int numY;
    public int level;
    
    public int tileOffs;
    public int picSize;
    
    public final int hashCode(){
      return (((((numX<<6)+numY)<<13)+(numY<<7)^numX)<<2 )+ level;
    }

  public final boolean equals(Object obj) {
    RMSOTile rt = (RMSOTile)obj;
    return (rt.numX==numX)&&(rt.numY==numY)&&(rt.level==level);
  }
    

    
}
