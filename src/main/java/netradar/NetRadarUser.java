/*
 * NetRadarUser.java
 *

 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package netradar;

import RPMap.MapPoint;
import RPMap.RMSOption;
import java.util.Vector;
import misc.MVector;

/**
 *
 * @author RFK
 */
public class NetRadarUser {
  public String userName;
  public double lat;
  public double lon;
  public int alt;
  public float speed;
  public int crs;
  public long dt;
  public MapPoint nav2;
  public int userId;
  public byte ut;
  public double[] tracklat;
  public double[] tracklon;
  public byte trackpos;
  public byte trackcnt;
  public long lastTrackAdd;
  public String status;
  public MVector status_v;
  // public long lastMessDT;
  // public boolean mesListUpdatePending;
  /** Creates a new instance of NetRadarUser */
  public void nextpos(){
    if (trackpos==tracklat.length-1) trackpos=(byte)0;
    else trackpos++;
    if (trackcnt<tracklat.length) trackcnt++;
  }
  
  public NetRadarUser() {
    if (RMSOption.getBoolOpt(RMSOption.BL_SHOWTRACKNR)){
      tracklat = new double[20];
      tracklon = new double[20];
    } else {
      tracklat = new double[2];
      tracklon = new double[2];
    }
  }
  void updateMapPoint() {
    if (nav2==null) return;
    nav2.lat=lat;
    nav2.lon=lon;
    nav2.alt=alt;
    nav2.visible=false;
  }
  
//  int messFrom;
//  int messFromUnread;
//  Vector msgList;
//  public NetRadarUser(int userId) {
//    this.userId=userId;
//    (new Thread(this)).start();
//  }
  
//  public void run() {
//
//  }
//
//  public void setProgressResponse(ProgressResponse progressResponse) {
//  }
//
//  public void stop() {
//  }
}
