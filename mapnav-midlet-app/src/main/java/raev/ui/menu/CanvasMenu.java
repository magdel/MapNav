/*
 * CanvasMenu.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package raev.ui.menu;

import RPMap.MapCanvas;
import RPMap.MapTimerTask;
import RPMap.MapUtil;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author RFK
 */
public class CanvasMenu{
  CanvasMenuListener canvasMenuListener;
  final static byte ROOT = 0;
  /** Creates a new instance of CanvasMenu */
  public CanvasMenu(CanvasMenuListener canvasMenuListener) {
    this.canvasMenuListener=canvasMenuListener;
    rootItem = new CanvasMenuItem(MapUtil.emptyString,ROOT,ROOT);
    activeItem=rootItem;
  }
  
  CanvasMenuItem rootItem;
  CanvasMenuItem activeItem;
  
  public CanvasMenu addMenuItems(CanvasMenuItem menuItem){
    activeItem.addItems(menuItem);
    return this;
  }
  
  public CanvasMenuItem setActive(int keyCode ){
    CanvasMenuItem cmi = getItemByKeyCode(keyCode);
    if (cmi!=null){
      processed=true;
      if (!cmi.isFinal()) {
        activeItem=cmi;return null;
      } else
        return cmi;
    } else return null;
//    for (int i=0;i<activeItem.items.size();i++){
//      cmi=  (CanvasMenuItem)activeItem.items.elementAt(i);
//      if (cmi.key==keyCode) {
//        processed=true;
//        if (!cmi.isFinal()) {
//          activeItem=cmi;return null;
//        } else
//          return cmi;
//      }
//
//    }
//    return null;
  }
  
  public CanvasMenuItem getItemByKeyCode(int keyCode ){
    CanvasMenuItem cmi;
    for (int i=activeItem.items.size()-1;i>=0;i--){
      cmi=  (CanvasMenuItem)activeItem.items.elementAt(i);
      if (cmi.key==keyCode) {
        return cmi;
      }
    }
    return null;
  }
  
  int msw;
  int mxo = 4;
  int myo = 4;
  int fh;
  int ic;
  int my;
  public void drawMenu(Graphics g){
    Font bf = g.getFont();
    Font f = MapUtil.MEDIUMFONT;
    g.setFont(f);
    fh = f.getHeight();
    int mx = g.getClipWidth();
    my = g.getClipHeight();
    ic = activeItem.items.size();
    String s;
    msw=0;
    for (int i=0;i<ic;i++) {
      s=((CanvasMenuItem)activeItem.items.elementAt(i)).caption;
      msw= (f.stringWidth(s)>msw)?f.stringWidth(s):msw;
    }
    msw+=4;
    g.setColor(0);
    g.fillRect(mxo,my-mxo-ic*fh,msw,ic*fh);
    g.setColor(0xFFFFFF);
    g.drawRect(mxo,my-myo-ic*fh,msw,ic*fh);
    CanvasMenuItem cmi;
    g.setColor(0xE0E0E0);
    for (int i=0;i<ic;i++) {
      cmi=((CanvasMenuItem)activeItem.items.elementAt(i));
      if (cmi.higlighted) {
        g.setColor(0xFFFFFF);
        g.fillRoundRect(mxo+2,my-myo-(i+1)*fh+1,msw-3,fh-2,4,4);
        g.setColor(0x0);
      }
      g.drawString(cmi.caption,mxo+2,my-myo-i*fh,Graphics.BOTTOM|Graphics.LEFT);
      if (cmi.higlighted) g.setColor(0xE0E0E0);
    }
    g.setFont(bf);
  }
  public void clean(){canvasMenuListener=null;rootItem=null;activeItem=null;}
  private boolean processed;
  public boolean processKey(int keyCode){
    processed=false;
    CanvasMenuItem cmi = getItemByKeyCode(keyCode);
    if (cmi!=null) {
      cmi.higlighted=true;
      processed=true;
      MapCanvas.timer.schedule(new MapTimerTask(this,cmi.key),200);
    }
    MapCanvas.map.repaint();
    //  forcepaint=true;
    return processed;
  }
  // private boolean forcepaint=true;
  public boolean processPointer(int x, int y){
//    processed=false;
    int i;
    if (!MapCanvas.map.landscapeMap){
      if ((x<mxo)||(x>mxo+msw)) return false;
      if ((y<=my-myo-ic*fh)||(y>my-myo)) return false;
      i = (my-y-myo)/fh;
    } else{
      
      if ((y<mxo)||(y>mxo+msw)) return false;
      if ((x>=mxo+ic*fh)||(x<mxo)) return false;
      i = (x-mxo)/fh;
      
    }
    //   forcepaint=false;
    return processKey(((CanvasMenuItem)activeItem.items.elementAt(i)).key);
    
  }
  
  public void fireKey(int keyCode){
    //processed=false;
    CanvasMenuItem cmi = setActive(keyCode);
    if (cmi!=null) {
      cmi.higlighted=false;
      canvasMenuListener.commandCanvasAction(cmi,cmi.commandId);
    }
    MapCanvas.map.repaint();
    MapCanvas.map.serviceRepaints();
    //return processed;
  }
  
}
