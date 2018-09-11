/*
 * MNProtocol.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package common;

/**
 *
 * @author RFK
 */
public class MNProtocol {
  
  public final static byte STATUS_WAITVERSION = 0;
  public final static byte STATUS_WAITAUTHORIZATION = 1;
  public final static byte STATUS_WAITUSERS = 2;
  public final static byte STATUS_WAITCOORDINATES = 3;
  public final static byte STATUS_TERMINATED = 4;
  
  
  public final static byte COMMAND_HEADERSIZE = 5;
  
  public final static byte COMMAND_VERSION = 1;
  public final static int COMMAND_VERSION_SIZE = 1;
  public final static int COMMAND_VERSION_VALUE = 2;
  
  public final static byte COMMAND_VERSIONANS = 2;
  public final static int COMMAND_VERSIONANS_SIZE = 1;
  public final static int COMMAND_VERSIONANS_OK = 1;
  public final static int COMMAND_VERSIONANS_BAD = 0;
  
  public final static byte COMMAND_AUTHORIZE = 3;
  public final static int  COMMAND_AUTHORIZE_SIZE = 0;
  
  public final static byte COMMAND_AUTHORIZEANS = 4;
  public final static int COMMAND_AUTHORIZEANS_SIZE = 1;
  public final static int COMMAND_AUTHORIZEANS_OK = 1;
  public final static int COMMAND_AUTHORIZEANS_BAD = 0;

  public final static byte COMMAND_USERLIST = 5;
  public final static int  COMMAND_USERLIST_SIZE = 0;
  
  public final static byte COMMAND_USERLISTANS = 6;
  public final static int  COMMAND_USERLISTANS_SIZE = 1;
  public final static byte COMMAND_USERLISTANS_OK = 1;
  public final static byte COMMAND_USERLISTANS_BAD = 2;

  public final static byte COMMAND_MYCOORDS = 7;

  public final static byte COMMAND_OTHERCOORDS = 8;
  
  public final static byte COMMAND_PING = 9;
  
  public final static byte COMMAND_SOUND = 10;
  

  public final static byte U_COMMAND_POSITION = 1;
  public final static int U_COMMAND_POSITION_SIZE = 1;
  public final static int U_COMMAND_POSITION_VALUE = 1;

}
