/*
 * SSCanvas.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package RPMap;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import lang.Lang;
import lang.LangHolder;

/**
 *
 * @author RFK
 */
public class SSCanvas extends Canvas{
  
  
  protected void paint(Graphics g) {
    g.setColor(0);
    g.fillRect(0,0,getWidth(),getHeight());
    g.setColor(0xFFFFFF);
    g.drawString(LangHolder.getString(Lang.takescreen)+"...",0,getHeight()/2,Graphics.BOTTOM|Graphics.LEFT);
  }
  
}
