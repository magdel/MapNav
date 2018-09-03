/*
 * RMSTile.java
 *
 * Created on 11 Январь 2007 г., 21:57
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
public final class RMSTile {
    int numX;
    int numY;
    public int level;
    public int recordId;
    long ts;
    /**SHOW_GL SHOW_VE SHOWMAP SHOWNET*/
    public byte tileType;
    
    //public int tileOffs;
    //public int picSize;
    
    public final int hashCode(){
      int mt=tileType;
      return ((((((numX<<6)+numY)<<13)+(numY<<7)^numX)<<2 )+ level)^mt;
    }

  public final boolean equals(Object obj) {
    RMSTile rt = (RMSTile)obj;
    return (rt.numX==numX)&&(rt.numY==numY)&&(rt.level==level)&&(rt.tileType==tileType);
  }
    
    
   // int dataSize;
    public RMSTile(DataInputStream dis) throws IOException {
      loadFromStream (dis);
    }
    RMSTile(int lev,int nX,int nY, int rId, byte mT){
        numX=nX;
        numY=nY;
        level=lev;
        recordId=rId;
        tileType=mT;
        updateTS();
    }
    
    void updateTS(){
        ts=System.currentTimeMillis();
    }
    /** Return true if it is tile of custom map */
    public final boolean isMyMap() {
      return ((tileType&MapTile.SHOW_MASKSERVER)==MapTile.SHOW_MP);
    }
    public void save2Stream (DataOutputStream dos) throws IOException {
      dos.writeInt(numX);
      dos.writeInt(numY);
      dos.writeInt(level);
      dos.writeInt(recordId);
      dos.writeLong(ts);
      dos.writeByte(tileType);
    }
    public void loadFromStream (DataInputStream dis) throws IOException {
      numX=dis.readInt() ;
      numY=dis.readInt() ;
      level=dis.readInt() ;
      recordId=dis.readInt() ;
      ts=dis.readLong() ;
      tileType=dis.readByte() ;
    }
}
