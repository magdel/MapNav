/*
 * TextCanvas.java
 *
 * Created on 18 ���� 2007 �., 20:07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package misc;

import RPMap.MapCanvas;
import RPMap.MapRoute;
import RPMap.RMSOption;
import RPMap.RMSSettings;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Stack;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import lang.Lang;
import lang.LangHolder;

/**
 *
 * @author Raev
 */
public class TextCanvas extends Canvas implements Runnable, CommandListener, ProgressStoppable {

    int geoid;
    Command backCommand=new Command(LangHolder.getString(Lang.back), Command.BACK, 2);
    Command reloCommand=new Command(LangHolder.getString(Lang.refresh), Command.ITEM, 1);
    String cap;
    byte geoType;

    private void init() {
        addCommand(backCommand);
        setCommandListener(this);
        setFullScreenMode(true);
    }

    /** Creates a new instance of TextCanvas */
    public TextCanvas(String cap, int id, byte geoInfo) {
        super();
        geoid=id;
        this.cap=cap;
        this.geoType=geoInfo;
        init();
        addCommand(reloCommand);
        fh=font.getHeight();
        runLoad();
    }

    public TextCanvas(String text) {
        super();
        this.cap="";
        init();
        fh=font.getHeight();
        prepare(text);
    }
    Displayable backDisp;

    public TextCanvas(String text, Displayable backDisp) {
        super();
        this.backDisp=backDisp;
        this.cap="";
        init();
        fh=font.getHeight();
        prepare(text);
    }
    private String[] infoSite={"geocaching.ru", "gps-club.ru"};

    private void runLoad() {
        String info=RMSSettings.loadGeoInfo(geoid, geoType);
        if (info==null){
            MapCanvas.setCurrent(new ProgressForm(LangHolder.getString(Lang.loading),
              infoSite[geoType-1],
              this, MapCanvas.map));
            Thread t=new Thread(this);
            t.start();
        } else {
            prepare(info);
            MapCanvas.setCurrent(this);
        }
    }

    public boolean stopIt() {
        stopped=true;
        return true;

    }
    private boolean stopped;

    private void getGPSClub() {
        try {

            //http://poi.gps-club.ru/mapnav.php?desc=927&cp=utf
            String s=HTTPUtils.getHTTPContentAsString("http://poi.gps-club.ru/mapnav.php?desc="+geoid+"&cp=utf");

            //������ http://poi.gps-club.ru/mapnav.php?icon=35&cp=utf ��� icon ���� ID ��������� ������ ���������:
            //���
            //$POI,����,http://poi.gps-club.ru/mapicons/dps_post.png

            //� ������� ������� ���� �������� ���������, ����� ���� �� ������. ��� ������ � PNG ������� � ������� ������������.
            //������ ������ �������� ���������� � 20 �������� �� �������� ���������.

            if (stopped) {
                return;
            }
            prepare(s);
            RMSSettings.saveGeoInfo(geoid, s, geoType);
            MapCanvas.setCurrent(this);
        } catch (Throwable t) {
//#mdebug
            if (RMSOption.debugEnabled) {
                DebugLog.add2Log("GC2:"+t.toString());
            }
//#enddebug
            MapCanvas.showmsgmodal(LangHolder.getString(Lang.load), t.toString(), AlertType.ERROR, this);
        }
    }
    private String ENDWITH_GEO="�������� �������";
    private String ENDWITH_GEO2="���������� �������";

    public void run() {
        if (geoType==MapRoute.GEOINFO_GPSCLUB){
            getGPSClub();
            return;
        }
        byte[] buffer=new byte[40];
        int bufPos;
        boolean begFound=false;
        InputStream is=null;
        HttpConnection c;
        ByteArrayOutputStream baos=new ByteArrayOutputStream(3000);

        Stack stack=new Stack();
        //#debug
        int tracePos=1;

        try {
            String fn="http://www.geocaching.su/?pn=101&cid="+geoid;

            //#debug
            tracePos=2;
            c=(HttpConnection) Connector.open(fn);
            c.setRequestMethod(HttpConnection.GET);
            is=c.openInputStream();
            //#debug
            tracePos=3;

            bufPos=0;
            try {
                int rb;
                do {
                    rb=is.read();
                    if (rb<0) {
                        break;
                    }
                    if (rb==0x3C){
                        if (stopped){
                            try {
                                if (is!=null) {
                                    is.close();
                                }
                            } catch (Throwable t) {
                            }
                            try {
                                c.close();
                            } catch (Throwable t) {
                            }
                            return;
                        }
                        if (bufPos>38){
                            //String s1 = MapUtil.fromBytesUTF8(buffer,bufPos);
                            if (!begFound){

                                String s1=Util.byteArrayToString(buffer, 0, bufPos, false);
                                if (s1.endsWith(ENDWITH_GEO)){
                                    begFound=true;
                                    buffer[bufPos]=(byte) rb;
                                    bufPos++;
                                    stack.push(null);
                                    Thread.yield();
                                    Thread.yield();
                                }
                            } else {
                                //
                                stack.push(null);
                                String s1=Util.byteArrayToString(buffer, 0, bufPos, false);
                                if (s1.endsWith(ENDWITH_GEO2)){
                                    Thread.yield();
                                    Thread.yield();
                                    break;
                                }

                            }
                        }
                    } else {
                        buffer[bufPos]=(byte) rb;
                        bufPos++;
                        if (begFound){
                            if (rb==0x3E) {
                                stack.pop();
                            }
                            if (rb!=0x3E) {
                                if (stack.empty()) {
                                    baos.write(rb);
                                }
                            }
                        }
                    }
                    if (bufPos==buffer.length){
                        for (int i=0; i<buffer.length-1; i++) {
                            buffer[i]=buffer[i+1];
                        }
                        bufPos--;
                    }
                } while ((rb>-1));
                buffer=null;
                Thread.yield();
                Thread.yield();
                String html=Util.byteArrayToString(baos.toByteArray(), false);
                baos=null;
                if (html.length()>20) {
                    html=html.substring(1, html.length()-18);
                }
                prepare(html);
                RMSSettings.saveGeoInfo(geoid, html, geoType);
            } finally {
                try {
                    if (is!=null) {
                        is.close();
                    }
                } catch (Throwable t) {
                }
                try {
                    c.close();
                } catch (Throwable t) {
                }
            }

            if (stopped){
                return;
            }
            MapCanvas.setCurrent(this);
        } catch (Throwable e) {
//#mdebug
            if (RMSOption.debugEnabled) {
                DebugLog.add2Log("GCi:"+String.valueOf(tracePos)+" "+e.toString());
            }
//#enddebug
            MapCanvas.showmsgmodal(LangHolder.getString(Lang.load), e.toString(), AlertType.ERROR, this);
        }
        ;
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command==backCommand){
            if (backDisp==null) {
                MapCanvas.setCurrentMap();
            } else {
                MapCanvas.setCurrent(backDisp);
            }
        } else if (command==reloCommand){
            RMSSettings.clearGeoInfo(geoid, geoType);
            MapCanvas.map.getActiveRoute().loadGeoCacheInfo();
        }
    }

    protected void paint(Graphics g) {
        int dminx=0;
        int dminy=fh;
        int dmaxx=getWidth();
        int dmaxy=getHeight();
        int dcx=(dminx+dmaxx)/2;
        int dcy=(dminy+dmaxy)/2;

        g.setFont(font);
        g.setColor(0);
//    g.setColor(MapMidlet.display.getColor(Display.COLOR_BACKGROUND));
        g.fillRect(0, 0, dmaxx, dmaxy);
        g.setColor(0xCCCC00);
//    g.setColor(MapMidlet.display.getColor(Display.COLOR_BORDER));
        g.drawRect(0, 0, dmaxx-1, dmaxy-1);
        g.setColor(0xFFFFFF);
        //g.setColor(MapMidlet.display.getColor(Display.COLOR_FOREGROUND));

        if (sv.size()==0) {
            return;
        }

        int mh=getHeight();
        int mini;
        mini=scrollY/fh;
        int maxi=mh/fh+1;
        if (maxi+mini>sv.size()) {
            maxi=sv.size()-mini;
        }

        for (int i=mini; i<mini+maxi; i++) {
            g.drawString((String) sv.elementAt(i), 2, i*fh-scrollY, Graphics.TOP|Graphics.LEFT);
        }

        g.setColor(0xDDDD88);//g.setColor(MapMidlet.display.getColor(Display.COLOR_HIGHLIGHTED_BORDER));
        //g.setColor(MapMidlet.display.getColor(Display.COLOR_HIGHLIGHTED_BORDER));
        int sh=(int) (((float) mh/(float) (mh+maxScrollY))*mh);
        int sp=(int) (((float) scrollY/(float) (maxScrollY+1))*(mh-sh));
        g.drawLine(dmaxx-2, sp, dmaxx-2, sp+sh);
        g.drawLine(dmaxx-4, sp, dmaxx-2, sp);
        g.drawLine(dmaxx-4, sp, dmaxx-4, sp+sh);
        g.drawLine(dmaxx-4, sp+sh, dmaxx-2, sp+sh);
        g.setColor(0xFFFF88);
        //g.setColor(MapMidlet.display.getColor(Display.COLOR_HIGHLIGHTED_FOREGROUND));
        g.drawLine(dmaxx-3, sp+1, dmaxx-3, sp+sh-1);
    }
    int scrollY;
    int fh;
    private MVector sv=new MVector();
    int maxScrollY;
    int lines;
    Font font=Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);

    ;

    public static void split(String info, MVector v, int mw, Font f) {
        mw=mw-5;
        //String left = cap+'\n'+info;
        String left=info;
        v.removeAllElements();
        while (left.length()>0) {
            int cw=0, cc=0;
            for (int i=0; i<left.length(); i++) {
                cw+=f.charWidth(left.charAt(i));
                if (cw>=mw){
                    cc=i-1;
                    for (int j=i; j>0; j--) {
                        if (left.charAt(j)==' '){
                            cc=j;
                            break;
                        }
                    }
                    break;
                }
                if (left.charAt(i)=='\n'){
                    cc=i;
                    i++;
                    break;
                }
                if (left.charAt(i)=='\r'){
                    cc=i;
                    i++;
                    break;
                }
                cc=i+1;
            }


            String news=left.substring(0, cc);
            if ((cc<left.length())&&((left.charAt(cc)=='\n')||(left.charAt(cc)=='\r'))) {
                left=left.substring(cc+1);
            } else {
                left=left.substring(cc);
            }
            if (news.length()>0) {
                v.addElement(news);
            }
        }
    }

    private void prepare(String info) {
        TextCanvas.split(info, sv, getWidth(), font);
        sv.insertElementAt(cap, 0);
        lines=getHeight()/fh;
        maxScrollY=(sv.size()-lines)*fh;
        if (maxScrollY<0) {
            maxScrollY=0;
        }
    }
    static private byte moveStep=2;
    final private static byte KEY_PRESSED=1;
    final private static byte KEY_RELEASED=2;
    final private static byte KEY_REPEATED=3;

// Key events handling
    protected void keyPressed(int keyCode) {
        keyEvent(KEY_PRESSED, keyCode);
    }

    protected void keyReleased(int keyCode) {
        keyEvent(KEY_RELEASED, keyCode);
    }

    protected void keyRepeated(int keyCode) {
        keyEvent(KEY_REPEATED, keyCode);
    }

    protected void pointerReleased(int x, int y) {
        if (y<getHeight()/2){
            scrollY=scrollY-fh*lines;
            if (scrollY<0) {
                scrollY=0;
            }
        } else {
            scrollY=scrollY+fh*lines;
            if (scrollY>maxScrollY) {
                scrollY=maxScrollY;
            }
        }
        repaint();
    }
    private static long timePressed;
    private static boolean keyLC;

    public final void keyEvent(byte eventType, int key) {
        boolean eD=false;
        if (eventType==KEY_RELEASED){
            moveStep=2;
            if (keyLC) {
                return;
            }
        }

        if (eventType==KEY_PRESSED){
            keyLC=false;
            moveStep=2;
            timePressed=System.currentTimeMillis();
        }

        if (eventType==KEY_REPEATED){
            if (moveStep<10) {
                moveStep++;
            }
        }

        if ((eventType==KEY_RELEASED)) {
            return;
        }

        int gA;

        if (!eD){
            switch (gA=getGameAction(key)) {
                case LEFT: // '\004'
                    scrollY=scrollY-fh*lines;
                    if (scrollY<0) {
                        scrollY=0;
                    }
                    break;

                case UP: // '\002'
                    scrollY=scrollY-moveStep;
                    if (scrollY<0) {
                        scrollY=0;
                    }
                    break;

                case RIGHT: // '\006'
                    scrollY=scrollY+fh*lines;
                    if (scrollY>maxScrollY) {
                        scrollY=maxScrollY;
                    }
                    break;

                case DOWN: // '\b'
                    scrollY=scrollY+moveStep;
                    if (scrollY>maxScrollY) {
                        scrollY=maxScrollY;
                    }
                    break;
            }
            ;
        }

        repaint();
    }

    public void setProgressResponse(ProgressResponse progressResponse) {
    }
}
