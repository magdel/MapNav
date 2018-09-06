/*
 * GPSLocationListener.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gpspack;

/**
 *
 * @author julia
 */
public interface GPSLocationListener {
  
public void gpsLocationAction(double lat, double lon, int alt, long time, float spd, float crs);
    
}
