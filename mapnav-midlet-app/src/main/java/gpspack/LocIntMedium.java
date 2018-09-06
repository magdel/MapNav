/*
 * LocIntMedium.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gpspack;

/**
 *
 * @author RFK
 */
public class LocIntMedium {
  
  /** Creates a new instance of LocIntMedium */
  public LocIntMedium() {
  }
  public static final void startInternal(GPSReader gpsReader){
 //#if SE_K750_E_BASEDEV
 //#else   
 //#if NetRadarDevice
 //#else   
    gpsReader.locReader=new LocReader(gpsReader);
    if (gpsReader==null) new LocReadDummy();
 //#endif   
 //#endif   
  }
}
