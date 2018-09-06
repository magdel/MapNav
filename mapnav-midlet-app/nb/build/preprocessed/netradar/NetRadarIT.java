/*
 * NetRadarIT.java
 *
 * Created on 6 ������� 2008 �., 0:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package netradar;

import RPMap.MapCanvas;
import RPMap.MapUtil;
import RPMap.RMSOption;
import camera.MD5;
import common.MNProtocol;
import gpspack.GPSReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import javax.microedition.io.SocketConnection;
//#debug
import misc.DebugLog;

/**
 *
 * @author RFK
 */
public class NetRadarIT implements Runnable {

    /**
     * Time of last success read from socket
     */
    public long lastReadTime=System.currentTimeMillis();
    private Thread thrd;
    private final byte[] signBytes;

    /** Creates a new instance of NetRadarIT */
    public NetRadarIT() {
        this.signBytes=MD5.getHash(RMSOption.netRadarPass);
        this.thrd=new Thread(this);
        this.thrd.start();
    }
    private boolean stopped;

    void stop() {
        stopped=true;
        if (thrd!=null){
            thrd.interrupt();
        }
        thrd=null;
    }
    //private int errC=300;
    //private boolean addtp;
    public static long latency;
    public static long runTime;
    byte[] soundToSend;
    String soundFormat;

    boolean readData(boolean authorizeOnly) {
        boolean authorized=false;
        DataInputStream is=null;
        DataOutputStream os=null;
        SocketConnection c;
        try {
            int[] uservisid=new int[NetRadar.netRadar.users.size()];
            for (int i=0; i<NetRadar.netRadar.users_v.size(); i++) {
                uservisid[i]=(int) ((NetRadarUser) NetRadar.netRadar.users_v.elementAt(i)).userId;
            }
            c=(SocketConnection) Connector.open(NetRadar.netRadar.nrSocketServerURL, Connector.READ_WRITE);
            //c = (SocketConnection)Connector.open("socket://localhost:5002",Connector.READ_WRITE);

            //int port = (RMSOption.getByteOpt(RMSOption.BO_RADARSITE)==0)?5001:5002;
            //c = (SocketConnection)Connector.open("socket://81.3.138.244:"+port,Connector.READ_WRITE);
            //c = (SocketConnection)Connector.open("socket://77.241.32.150:"+port,Connector.READ_WRITE);
            try {
                is=c.openDataInputStream();
                try {
                    os=c.openDataOutputStream();
                    try {
                        os.writeByte(MNProtocol.COMMAND_VERSION);
                        os.writeInt(MNProtocol.COMMAND_VERSION_SIZE);
                        os.writeByte(MNProtocol.COMMAND_VERSION_VALUE);
                        os.flush();
                        NetRadar.bytesDown+=6;
                        byte cmd=is.readByte();
                        int cmdsize=is.readInt();
                        byte bres=is.readByte();
                        NetRadar.bytesDown+=6;
                        if (bres!=MNProtocol.COMMAND_VERSIONANS_OK){
                            throw new Exception("WV");
                        }

                        //byte[] dataW = new byte[100];
                        ByteArrayOutputStream baos=new ByteArrayOutputStream(100);
                        DataOutputStream dos=new DataOutputStream(baos);
                        dos.writeUTF(RMSOption.netRadarLogin);
                        dos.writeUTF(RMSOption.netRadarPass);

                        byte[] ba=baos.toByteArray();
                        os.writeByte(MNProtocol.COMMAND_AUTHORIZE);
                        os.writeInt(ba.length);
                        os.write(ba);
                        NetRadar.bytesDown+=5+ba.length;
                        os.flush();
                        baos.reset();

                        cmd=is.readByte();
                        cmdsize=is.readInt();
                        bres=is.readByte();
                        NetRadar.bytesDown+=5;
                        if (bres!=MNProtocol.COMMAND_AUTHORIZEANS_OK){
                            throw new Exception("WA");
                        }
                        authorized=true;
                        if (authorizeOnly) {
                            return authorized;
                        }
                        dos.writeShort(uservisid.length);
                        for (int u=0; u<uservisid.length; u++) {
                            dos.writeInt(uservisid[u]);
                        }
                        ba=baos.toByteArray();
                        os.writeByte(MNProtocol.COMMAND_USERLIST);
                        os.writeInt(ba.length);
                        os.write(ba);
                        NetRadar.bytesDown+=5+ba.length;
                        os.flush();
                        baos.reset();

                        cmd=is.readByte();
                        cmdsize=is.readInt();
                        bres=is.readByte();
                        NetRadar.bytesDown+=5;
                        if (bres!=MNProtocol.COMMAND_USERLISTANS_OK){
                            throw new Exception("WU");
                        }

//            uservisid=null;
////            e=null;
                        byte[] inBuffer=new byte[200];
                        ByteArrayInputStream bais=new ByteArrayInputStream(inBuffer);
                        DataInputStream dis=new DataInputStream(bais);
                        long nextSendTime=0;
                        long delay;
                        //long lastTrAdd=0;                        //boolean addtp=false;
                        while (!stopped) {
                            delay=System.currentTimeMillis();
                            byte[] sts=soundToSend;
                            if (sts!=null){
                                baos.reset();
                                dos.writeInt(sts.length);
                                dos.write(sts);
                                soundToSend=null;
                                sts=null;
                                if (soundFormat==null){
                                    soundFormat=MapUtil.emptyString;
                                }
                                dos.writeUTF(soundFormat);
                                dos.writeLong(System.currentTimeMillis());

                                ba=baos.toByteArray();
                                os.writeByte(MNProtocol.COMMAND_SOUND);
                                os.writeInt(ba.length);
                                os.write(ba);
                                os.flush();
                                baos.reset();
                                NetRadar.bytesDown+=5+ba.length;
                                ba=null;
                                //   os.flush();
                                nextSendTime=System.currentTimeMillis()+500;
                            }
                            if (nextSendTime<System.currentTimeMillis()){
                                if (GPSReader.NUM_SATELITES>0){
                                    dos.writeInt((int) (GPSReader.LATITUDE*100000d));
                                    dos.writeInt((int) (GPSReader.LONGITUDE*100000d));
                                    dos.writeShort(GPSReader.ALTITUDE);
                                    dos.writeShort((int) (GPSReader.SPEED_KMH*10f));
                                    dos.writeShort(GPSReader.COURSE_I);
                                    if (GPSReader.SAT_TIME_MILLIS==0){
                                        dos.writeLong(System.currentTimeMillis());
                                    } else {
                                        dos.writeLong(GPSReader.SAT_TIME_MILLIS);
                                    }

                                    ba=baos.toByteArray();
                                    os.writeByte(MNProtocol.COMMAND_MYCOORDS);
                                    os.writeInt(ba.length);
                                    os.write(ba);
                                    os.flush();
                                    NetRadar.bytesDown+=5+ba.length;
                                    baos.reset();
                                    nextSendTime=System.currentTimeMillis()+500;
                                } else if (nextSendTime<System.currentTimeMillis()-10000){
                                    os.writeByte(MNProtocol.COMMAND_PING);
                                    os.writeInt(1);
                                    os.writeByte(0);
                                    os.flush();
                                    NetRadar.bytesDown+=6;
                                    nextSendTime=System.currentTimeMillis()+500;
                                }
                            }
                            if (is.available()>0){
                                cmd=is.readByte();
                                cmdsize=is.readInt();
                                if (cmdsize>inBuffer.length){
                                    inBuffer=new byte[cmdsize];
                                    bais=new ByteArrayInputStream(inBuffer);
                                    dis=new DataInputStream(bais);
                                }
                                is.readFully(inBuffer, 0, cmdsize);
                                NetRadar.bytesDown+=cmdsize+5;
                                lastReadTime=System.currentTimeMillis();
                                bais.reset();
                                if (cmd==MNProtocol.COMMAND_OTHERCOORDS){

                                    int uc=dis.readShort();
                                    // boolean b=(lastTrAdd<System.currentTimeMillis()),nu;
                                    boolean nu;
                                    int uid;
                                    byte ut;

                                    NetRadarUser nru, nru_nav=NetRadar.netRadar.getNav2User();
                                    String ss;
                                    for (int i=0; i<uc; i++) {
                                        if (!dis.readBoolean()){
                                            continue;
                                        }
                                        uid=dis.readInt();
                                        ut=dis.readByte();
                                        ss=String.valueOf(uid);
                                        nru=NetRadar.netRadar.getUser(ss+'.'+ut);
                                        nu=false;
                                        if (nru==null){
                                            nu=true;
                                            nru=new NetRadarUser();
                                            nru.userName=String.valueOf(uid);
                                        }
                                        //nru.userName=is.readUTF();
                                        nru.lat=((double) dis.readInt())/100000.0d;
                                        nru.lon=((double) dis.readInt())/100000.0d;
                                        nru.alt=dis.readShort();
                                        nru.speed=((float) dis.readShort())/10.0f;
                                        nru.crs=dis.readShort();
                                        nru.dt=dis.readLong();
                                        long dd=0;
                                        if (nru.ut==3){
                                            dd=System.currentTimeMillis()-nru.dt;
                                        }

                                        if (nru.lastTrackAdd<System.currentTimeMillis()){
                                            nru.tracklat[nru.trackpos]=nru.lat;
                                            nru.tracklon[nru.trackpos]=nru.lon;
                                            nru.nextpos();
                                            nru.lastTrackAdd=System.currentTimeMillis()+3000;
                                        }

                                        if (nu){
                                            NetRadar.netRadar.users.put(ss+'.'+ut, nru);
                                            NetRadar.netRadar.users_v.addElement(nru);
                                        }
                                    }

                                    if (nru_nav!=null){
                                        nru_nav.updateMapPoint();
                                    }
                                }

                                if (cmd==MNProtocol.COMMAND_SOUND){

                                    int uc=dis.readShort();
                                    // boolean b=(lastTrAdd<System.currentTimeMillis()),nu;
                                    boolean nu;
                                    int uid;
                                    byte ut;

                                    //String ss;
                                    for (int i=0; i<uc; i++) {
                                        if (!dis.readBoolean()){
                                            continue;
                                        }
                                        byte[] snd=new byte[dis.readInt()];
                                        dis.read(snd);
                                        String frmt=dis.readUTF();
                                        NetRadar.netRadar.nrWT.playStreamFormat(new ByteArrayInputStream(snd), frmt);
                                        //NetRadar.netRadar.nrWT.playStreamFormat(new ByteArrayInputStream(snd),null);
                                    }
                                }


                                NetRadarIT.latency=System.currentTimeMillis()-delay;
                                MapCanvas.map.repaintMap();
                            }
                            Thread.sleep(100);
                            runTime+=System.currentTimeMillis()-delay;
                        }

                    } finally {
                        os.close();
                    }
                } finally {
                    is.close();
                }
            } finally {
                c.close();
            }
        } catch (Throwable t) {
            //#mdebug
            if (RMSOption.debugEnabled){
                DebugLog.add2Log("NRIT:"+t.toString());
            }
            //#enddebug

        }
        return authorized;
    }

    public void run() {
        try {
            while (!stopped&&(NetRadar.netRadar.nrSocketServerURL==null)) {
                Thread.sleep(100);
            }
        } catch (Throwable t) {
            stopped=true;
        }
        if (RMSOption.getBoolOpt(RMSOption.BL_REALTIMENR_UDP)){
            boolean authorized=false;
            while (!stopped) {
                if (!authorized) {
                    authorized=readData(true);
                }
                if (authorized) {
                    sendUSPData();
                }
                if (!stopped){
                    try {
                        Thread.sleep(3000);
                    } catch (Throwable t) {
                        stopped=true;
                    }
                }
            }

        } else {
            while (!stopped) {
                //errC--;
                readData(false);
                if (!stopped){
                    try {
                        Thread.sleep(3000);
                    } catch (Throwable t) {
                        stopped=true;
                    }
                }
            }
        }
    }

    private void sendUSPData() {

        DatagramConnection c;
        try {
          c=(DatagramConnection) Connector.open(NetRadar.netRadar.nrDatagramServerURL, Connector.WRITE);
            //c=(DatagramConnection) Connector.open("datagram://localhost:5000", Connector.READ_WRITE);
            Datagram dg=c.newDatagram(64);

            try {

                ByteArrayOutputStream baos=new ByteArrayOutputStream(100);
                DataOutputStream dos=new DataOutputStream(baos);


                byte[] inBuffer=new byte[200];
                ByteArrayInputStream bais=new ByteArrayInputStream(inBuffer);
                DataInputStream dis=new DataInputStream(bais);
                long nextSendTime=0;
                long delay;
                long lastSentTime=0;
                while (!stopped) {
                    delay=System.currentTimeMillis();

                    if (nextSendTime<delay){
                        long satTime=(GPSReader.SAT_TIME_MILLIS==0)?System.currentTimeMillis():GPSReader.SAT_TIME_MILLIS;
                        if (GPSReader.NUM_SATELITES>0 && lastSentTime!=satTime){
                            baos.reset();
                            dos.writeLong(NetRadar.netRadar.nrUserId);

                            dos.writeInt((int) (GPSReader.LATITUDE*100000d));
                            dos.writeInt((int) (GPSReader.LONGITUDE*100000d));
                            dos.writeShort(GPSReader.ALTITUDE);
                            dos.writeShort((int) (GPSReader.SPEED_KMH*10f));
                            dos.writeShort(GPSReader.COURSE_I);
                            
                            lastSentTime=satTime;
                            dos.writeLong(lastSentTime);
                            
                            byte[] signedData=baos.toByteArray();

                            dos.write(signBytes);

                            byte[] dataWithSign=baos.toByteArray();
                            byte[] signHash=MD5.getHash(dataWithSign);

                            dg.reset();
                            dg.writeByte(MNProtocol.U_COMMAND_POSITION);
                            dg.writeInt(signedData.length+signHash.length);
                            dg.write(signedData);
                            dg.write(signHash);

                            c.send(dg);

                            NetRadar.bytesDown+=signedData.length+signHash.length;
                            nextSendTime=delay+1000;
                            //System.out.println("sent "+nextSendTime);
                        }

                        NetRadarIT.latency=System.currentTimeMillis()-delay;
                        MapCanvas.map.repaintMap();
                    }
                    Thread.sleep(100);
                    runTime+=System.currentTimeMillis()-delay;
                }

            } finally {
                c.close();
                //System.out.println("disconnect");
            }
        } catch (Throwable t) {
            //#mdebug
            if (RMSOption.debugEnabled){
                DebugLog.add2Log("NRITU: "+t.getMessage()+" "+t);
            }
            //#enddebug
        }
    }
}
