/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package misc;

import RPMap.MapUtil;
import javax.microedition.lcdui.Graphics;

/**
 *Utils for graphic
 *
 * @author rfk
 */
public class GraphUtils {

    public final static int makeColor(int r, int g, int b) {
        return (r<<16)+(g<<8)+b;
    }

    /**
     * Leaves only specified percent
     * @param color
     * @param pc
     * @return
     */
    public final static int fadeColor(int color, int percent) {
        return makeColor(percent*((color&0xFF0000)>>16)/100, percent*((color&0xFF00)>>8)/100, percent*(color&0xFF)/100);
    }

    public static void paintSpeed(Graphics g, int sp, int offsY, boolean full, int color, int dmaxx, int dmaxy, int dcx) {
        int cw=dmaxx/3-4, ch;
        if (full){
            ch=dmaxy-4;
        } else {
            ch=dmaxy/2-3;
        }
        g.setColor(color);
        paintCypher(g, (sp%10), cw-3, ch-2, (dcx+cw/2), offsY+2);
        color=fadeColor(color, 20);
        if (sp<10){
            g.setColor(color);
        }
        paintCypher(g, ((sp/10)%10), cw-3, ch-2, (dcx-cw/2), offsY+2);
        if (sp<100){
            g.setColor(color);
        }
        paintCypher(g, ((sp/100)%10), cw-3, ch-2, (dcx-3*cw/2), offsY+2);
    }

    public static void paintAltSpeed(Graphics g, int asp, int offsY, int ch, int color, int dmaxx, int dmaxy, int dcx) {
        int cw=dmaxx/6-4;
        
        boolean sign=asp<0;
        if (sign){
            asp=-asp;
        }

        g.setColor(color);

        paintCypher(g, (asp%10), cw-3, ch-2, (dcx+cw), offsY+2);

        paintCypher(g, 10, cw-3, ch-2, (dcx), offsY+2);
        paintCypher(g, ((asp/10)%10), cw-3, ch-2, (dcx-cw), offsY+2);
        if (asp<100){
            if (sign){
                paintCypher(g, 11, cw-3, ch-2, (dcx-2*cw), offsY+2);
                return;
            } else {
                if (asp!=0) {
                    paintCypher(g, 12, cw-3, ch-2, (dcx-2*cw), offsY+2);
                }
                return;
            }
        }

        paintCypher(g, ((asp/100)%10), cw-3, ch-2, (dcx-2*cw), offsY+2);
        if (sign){
            paintCypher(g, 11, cw-3, ch-2, (dcx-3*cw), offsY+2);
        } else {
            if (asp!=0) {
                paintCypher(g, 12, cw-3, ch-2, (dcx-3*cw), offsY+2);
            }
        }

    }

    public static void paintAltitude(Graphics g, int alt, int offsY, int ch, int color, int dmaxx, int dmaxy, int dcx) {
        int cw=dmaxx/6-4;
        boolean sign=alt<0;
        if (sign){
            alt=-alt;
        }

        g.setColor(color);

        paintCypher(g, (alt%10), cw-3, ch-2, (dcx+cw*2), offsY+2);
        if (alt>10) {
            paintCypher(g, ((alt/10)%10), cw-3, ch-2, (dcx+cw), offsY+2);
        }
        if (alt>100) {
            paintCypher(g, ((alt/100)%10), cw-3, ch-2, (dcx), offsY+2);
        }
        if (alt>1000) {
            paintCypher(g, ((alt/1000)%10), cw-3, ch-2, (dcx-cw), offsY+2);
        }
        if (alt>10000) {
            paintCypher(g, ((alt/10000)%10), cw-3, ch-2, (dcx-cw*2), offsY+2);
        }

        if (sign){
            paintCypher(g, 11, cw-3, ch-2, (dcx-3*cw), offsY+2);
        }
    }

    public static void paintCypher(Graphics g, int c, int w, int h, int x, int y) {
        int wc=w/3;
        int wh=h/5;
        if (c==12){
            //g.fillRect(x, y, wc*3, wh);
            //g.fillRect(x, y, wc, wh*5);
            g.fillRect(x, y+wh*2, wc*3, wh);
            g.fillRect(x+wc, y+wh, wc, wh*3);
            //g.fillRect(x+wc+wc, y, wc, wh*5);
            //g.fillRect(x, y+wh*4, wc*3, wh);
        } else if (c==11){
            //g.fillRect(x, y, wc*3, wh);
            //g.fillRect(x, y, wc, wh*5);
            g.fillRect(x, y+wh*2, wc*3, wh);
            //g.fillRect(x+wc+wc, y, wc, wh*5);
            //g.fillRect(x, y+wh*4, wc*3, wh);
        } else if (c==10){
            g.fillRect(x+wc, y+wh*4, wc, wh);
        } else if (c==0){
            g.fillRect(x, y, wc*3, wh);
            g.fillRect(x, y, wc, wh*5);
            g.fillRect(x+wc+wc, y, wc, wh*5);
            g.fillRect(x, y+wh*4, wc*3, wh);
        } else if (c==1){
            g.fillRect(x+wc+wc, y, wc, wh*5);
            //g.fillRect(x+wc,y+wh,wc,wh);
        } else if (c==2){
            g.fillRect(x, y, wc*3, wh);
            g.fillRect(x+wc+wc, y, wc, wh*3);
            g.fillRect(x, y+wh*2, wc*3, wh);
            g.fillRect(x, y+wh*2, wc, wh*3);
            g.fillRect(x, y+wh*4, wc*3, wh);
        } else if (c==3){
            g.fillRect(x, y, wc*3, wh);
            g.fillRect(x+wc+wc, y, wc, wh*5);
            g.fillRect(x, y+wh*2, wc*3, wh);
            g.fillRect(x, y+wh*4, wc*3, wh);
        } else if (c==4){
            g.fillRect(x+wc+wc, y, wc, wh*5);
            g.fillRect(x, y, wc, wh*3);
            g.fillRect(x, y+wh*2, wc*3, wh);
        } else if (c==5){
            g.fillRect(x, y, wc*3, wh);
            g.fillRect(x, y, wc, wh*3);
            g.fillRect(x, y+wh*2, wc*3, wh);
            g.fillRect(x+wc+wc, y+wh*2, wc, wh*3);
            g.fillRect(x, y+wh*4, wc*3, wh);
        } else if (c==6){
            g.fillRect(x, y, wc*3, wh);
            g.fillRect(x, y, wc, wh*5);
            g.fillRect(x, y+wh*2, wc*3, wh);
            g.fillRect(x+wc+wc, y+wh*2, wc, wh*3);
            g.fillRect(x, y+wh*4, wc*3, wh);
        } else if (c==7){
            g.fillRect(x, y, wc*3, wh);
            g.fillRect(x+wc+wc, y, wc, wh*5);
        } else if (c==8){
            g.fillRect(x, y, wc*3, wh);
            g.fillRect(x, y, wc, wh*5);
            g.fillRect(x, y+wh*2, wc*3, wh);
            g.fillRect(x+wc+wc, y, wc, wh*5);
            g.fillRect(x, y+wh*4, wc*3, wh);
        } else if (c==9){
            g.fillRect(x, y, wc*3, wh);
            g.fillRect(x, y, wc, wh*3);
            g.fillRect(x, y+wh*2, wc*3, wh);
            g.fillRect(x+wc+wc, y, wc, wh*5);
            g.fillRect(x, y+wh*4, wc*3, wh);
        }
    }
}
