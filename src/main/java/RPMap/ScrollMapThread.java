/*
 * ScrollThread.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package RPMap;

/**
 *
 * @author RFK
 */
public class ScrollMapThread extends Thread {
//    public ScrollThread() {
//      (new Thread(this)).start();
//    }
  public boolean stopped;
  public void run() {
    boolean incs=false;
    try {
      while (true){
        if ((MapCanvas.moveX==0)&&(MapCanvas.moveY==0))
          synchronized(MapCanvas.map){
          MapCanvas.map.wait();
          }
        if (stopped)return;
        MapCanvas.yCenter+=MapCanvas.moveY;
        MapCanvas.xCenter+=MapCanvas.moveX;
        if (incs){
//        if (MapCanvas.map.moveX!=0)
//        if (Math.abs(MapCanvas.map.moveX)<25)
//          if (MapCanvas.map.moveX>0) MapCanvas.map.moveX+=2;
//          else MapCanvas.map.moveX-=2;

        if (MapCanvas.moveX!=0)
        if (Math.abs(MapCanvas.moveX)<25)
          if (MapCanvas.moveX>6) MapCanvas.moveX+=2;
          else if (MapCanvas.moveX>0) MapCanvas.moveX++;
        else if (MapCanvas.moveX<-6) MapCanvas.moveX-=2;
          else MapCanvas.moveX--;
        
        if (MapCanvas.moveY!=0)
        if (Math.abs(MapCanvas.moveY)<25)
          if (MapCanvas.moveY>6) MapCanvas.moveY+=2;
          else if (MapCanvas.moveY>0) MapCanvas.moveY++;
        else if (MapCanvas.moveY<-6) MapCanvas.moveY-=2;
          else MapCanvas.moveY--;
        }
        int mx = MapUtil.getBitmapSize(MapCanvas.map.level);
        
        if (MapCanvas.yCenter<=0){
          MapCanvas.moveY=0;
          MapCanvas.yCenter=0;
        }
        if (MapCanvas.yCenter>=mx){
          MapCanvas.moveY=0;
          MapCanvas.yCenter=mx;
        }
        if (MapCanvas.xCenter<=0){
          MapCanvas.moveX=0;
          MapCanvas.xCenter=0;
        }
        if (MapCanvas.xCenter>=mx){
          MapCanvas.moveX=0;
          MapCanvas.xCenter=mx;
        }
        
        MapCanvas.map.repaint();
        MapCanvas.map.serviceRepaints();
        Thread.sleep(30);
      incs=!incs;
      }
    } catch (InterruptedException ex) {
    }
    
  }
  
}
