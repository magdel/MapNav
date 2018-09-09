/*
 * RouteSend.java
 *
 * Created on 5 ������� 2007 �., 1:57
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package RPMap;

import javax.microedition.lcdui.Displayable;
import misc.ProgressStoppable;

/**
 *
 * @author RFK
 */
public abstract class RouteSend implements Runnable, ProgressStoppable {

    String savePath;
    MapRoute mR;
    boolean sending;
    Displayable backDisp;
    public static byte EXPORTCODEPAGE;
    public static byte EXPORTFORMAT;

    /** Creates a new instance of MapSend */
    public RouteSend(Displayable backDisp, String path, MapRoute mr) {
        savePath=path;
        mR=mr;
        this.backDisp=backDisp;
        Thread t=new Thread(this);
        t.start();
    }

    public RouteSend(String path) {
        savePath=path;
        Thread t=new Thread(this);
        t.start();
    }

    abstract void send() throws Throwable;

    abstract void sendAll();

    //public void stop(){}
    //public void setProgressResponse(ProgressResponse progressResponse){}
    public void run() {
        try {
            try {
                try {
                    //needed to setup fields in inherited constructors
                    Thread.sleep(200);
                } catch (Throwable t) {
                }
                if (mR==null) {
                    sendAll();
                } else {
                    send();
                }
            } finally {
                mR=null;
                backDisp=null;
            }
        } catch (Throwable t) {
        }
    }
}
