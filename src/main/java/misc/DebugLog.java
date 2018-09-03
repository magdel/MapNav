/*
 * DebugLog.java
 *
 * Created on 12 ������� 2007 �., 17:09
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package misc;

import RPMap.MapUtil;

/*
import RPMap.MapUtil;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.file.FileConnection;
 */
/**
 *
 * @author Raev
 */
public final class DebugLog {

    private static final StringBuffer log=new StringBuffer(2500);
    private static long nextMemPrint;
    //make integrated check about log writing

    public static void add2Log(String info) {
        //add2File(s);
        long t=System.currentTimeMillis();
        synchronized (log) {
            log.append(MapUtil.time2mmsszzzString(t));
            log.append(':');
            log.append(Thread.currentThread().getName());
            log.append(':');
            log.append(' ');
            log.append(info);
            log.append('\n');
            if (nextMemPrint<t){
                log.append("FM:TM "+Runtime.getRuntime().freeMemory()+':'+Runtime.getRuntime().totalMemory()+"\n");
                nextMemPrint=t+5000;
            }
        }
    }

    public static void clear() {
        log.setLength(0);
    }

    public static String logString() {
        return log.toString();
    }

    /*private static String started = MapUtil.trackNameAuto();
    private static void add2File(String s) {

    String path="E:/MapNav/LOG5800a.txt";
    //String path="E:/MapNav/LOG5800a.txt";
    //String path = "root1/deb_log.txt";
    StreamConnection conn;

    //filename
    try {
    conn=
    (StreamConnection) Connector.open("file:///"+
    path, Connector.READ_WRITE);
    try {
    if (!((FileConnection) conn).exists())
    try{
    ((FileConnection) conn).create();
    }catch(Throwable t){}
    InputStream is = conn.openInputStream();
    byte[] prevb = new byte[(int)((FileConnection) conn).fileSize()];
    try{
    is.read(prevb);
    }finally{
    is.close();
    }
    OutputStream os=conn.openOutputStream();
    try{
    os.write(prevb, 0, prevb.length);
    if (started!=null){
    byte[] bi2 = started.getBytes();
    os.write(bi2, 0, bi2.length);
    os.write(13);
    os.write(10);
    bi2 = "-----------------".getBytes();
    os.write(bi2, 0, bi2.length);
    os.write(13);
    os.write(10);
    started=null;
    }
    byte[] bi = s.getBytes();
    os.write(bi, 0, bi.length);
    os.write(13);
    os.write(10);
    }finally{
    os.close();
    }
    //is.close();
    } finally {
    conn.close();
    }

    } catch (Throwable t) {
    }

    }*/
}
