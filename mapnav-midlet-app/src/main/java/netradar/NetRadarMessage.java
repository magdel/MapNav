/*
 * NetRadarMessage.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package netradar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Raev
 */
public class NetRadarMessage {
  long msgId;
  int senderId;
  byte rf;
  String subject;
  String userName;
  /** Creates a new instance of NetRadarMessage */
  public NetRadarMessage(){}
  
  public NetRadarMessage(DataInputStream dis) throws IOException {
    loadFromStream(dis);
  }
   public void save2Stream (DataOutputStream dos) throws IOException {
     dos.writeByte(1); //version
     dos.writeLong(msgId);
      dos.writeInt(senderId);
      dos.writeByte(rf); 
      dos.writeUTF(subject);
      dos.writeUTF(userName);
    }
    public void loadFromStream (DataInputStream dis) throws IOException {
      dis.readByte();
      msgId=dis.readLong();
      senderId=dis.readInt();
      rf=dis.readByte();
      subject=dis.readUTF();
      userName=dis.readUTF();
    }
  
}
