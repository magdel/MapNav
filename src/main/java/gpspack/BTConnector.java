package gpspack; 

import RPMap.MapUtil;
import RPMap.RMSOption;
import java.util.Vector;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice; 
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
//#debug
//# import misc.DebugLog;
import javax.microedition.io.StreamConnection;

public class BTConnector implements Runnable, DiscoveryListener {

    private DiscoveryAgent discoveryAgent;
    //private LocalDevice localDevice;
    private Vector devices=new Vector(); /* RemoteDevice */

    String[] deviceNames=new String[20];
    private int index;
    private int selIndex;

    public String getSelectedDeviceName() {
        return deviceNames[selIndex];
    }
    //private int []attrSet = {0x4321};
    //private UUID []uuidSet = {new UUID(0x1101)};//4353L
    //private UUID []uuidSet = {new UUID(4353),new UUID(4357)};//4353L ??? ???????? ?? OBEX
//  private UUID []uuidSet = {new UUID(4353)};//4353L
    UUID[] uuidSet;
    // obviously, 0x1105 is the UUID for
    // the Object Push Profile
    //UUID[] uuidSetOBEX = {new UUID(0x1105) };
    // 0x0100 is the attribute for the service name element
    // in the service record
    //    int[] attrSetOBEX = {0x0100};
    //UUID auuid[] = {
    //              new UUID(4353L)
    //          };
    private int transactionID;
    RemoteDevice remoteDevice;
//  public static String urlGPS = "";
    private boolean doneSearchingAll;
    //private boolean isSearching;
    //private boolean isServiceSearching;
    private boolean doneServiceSearching;
    Vector thrdNames=new Vector();
    BTCanvas canvas;
    boolean stopped;
   
    public BTConnector(StreamConnection conn) {
        RemoteDevice connRD=null;
        try {
            connRD=RemoteDevice.getRemoteDevice(conn);
        } catch (Throwable ex) {
//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("BTA: "+ex.getMessage()+" "+ex);
//#             }
//#enddebug
        }
        if (connRD==null)
            return;
        try {
//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("BTAtest: "+connRD.isAuthenticated());
//#             }
//#enddebug
            if (connRD!=null && !connRD.isAuthenticated()){
                connRD.authenticate();
            }
//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("BTAtest2: "+connRD.isAuthenticated());
//#             }
//#enddebug

        } catch (Throwable ex) {
//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("BTA: "+ex.getMessage()+" "+ex);
//#             }
//#enddebug
        }
    }

    public BTConnector(BTCanvas canv, boolean gps) {
        canvas=canv;
        //LocalDevice ld=getLocalDevice();
        gotService=false;
        if (gps){
            uuidSet=new UUID[]{new UUID(4353)};
        } else {
            uuidSet=new UUID[]{new UUID(0x1105)};
        }

        (new Thread(this)).start();
        //btT = new Thread(this);
        //btT.start();
    }
    //Thread btT;
    // reset the settings if a new device search is started.

    public void startSearch() {

//    if (RMSOption.debugEnabled)
//      DebugLog.add2Log("BT Conn startSearch 1");

        try {
            cancelInquiry(false);
        } catch (Throwable e) {
//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("BT Conn:"+e+" - OK!!!");
//#             }
//#enddebug
        }

//    if (RMSOption.debugEnabled)
//      DebugLog.add2Log("BT Conn startSearch 2");

        //try{if (btT!=null) btT.interrupt();}catch(Throwable t){}
        gotService=false;
        stopped=false;
        (new Thread(this)).start();
//    btT = new Thread(this);
        //  btT.start();

    }

    public Vector getDevices() {
        return devices;
    }

//  static boolean isBlueOn(){
//    try{
//      return LocalDevice.isPowerOn();
//    }catch(Throwable t){return true;}
//  }
    public String[] getDeviceNames() {
        return deviceNames;
    }

    public int getSize() {
        return index;
    }

    public boolean doneSearchingDevices() {
        return doneSearchingAll;
    }

    public boolean doneSearchingServices() {
        return doneServiceSearching;
    }

    public void cancelInquiry(boolean stopGlobal) {
        try {
            if (discoveryAgent!=null){
                discoveryAgent.cancelServiceSearch(transactionID);
            }
        } catch (Throwable t) {
        }
        try {
            if (discoveryAgent!=null){
                discoveryAgent.cancelInquiry(this);
            }
        } catch (Throwable t) {
        }
        if (stopGlobal){

            synchronized (this) { // resume the thread.
                stopped=true;
                this.notify();
            }
            canvas=null;
            //try{if (btT!=null)if (btT.isAlive()) btT.interrupt();}catch(Throwable t){}
//      btT=null;
        }
    }
    boolean connecting;
    // Connect to a specifyed index.

    public void connect(int i) {
        discoveryAgent.cancelServiceSearch(transactionID);
        discoveryAgent.cancelInquiry(this);
        remoteDevice=(RemoteDevice) devices.elementAt(i);
        selIndex=i;
        RMSOption.lastBTDeviceName=getSelectedDeviceName();
        connecting=true;
        synchronized (this) { // resume the thread.
            connecting=true;
            this.notify();
        }
    }

    public void run() {

//#mdebug
//#         if (RMSOption.debugEnabled){
//#             DebugLog.add2Log("BT Connector started - OK");
//#         }
//#enddebug


        devices=new Vector();

        deviceNames=new String[20];

        index=0;
        remoteDevice=null;
        doneServiceSearching=false;
        doneSearchingAll=false;
        canvas.urlGPS=MapUtil.emptyString;

        try {
            // create/get a local device and discovery agent
//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("BT Conn before LD - OK");
//#             }
//#enddebug

            LocalDevice localDevice=LocalDevice.getLocalDevice();
            discoveryAgent=localDevice.getDiscoveryAgent();

//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("BT Conn after DiscA - OK");
//#             }
//#enddebug

            // start searching for remote devices. If a device is found the deviceDiscovered method will be called.

            discoveryAgent.startInquiry(DiscoveryAgent.GIAC, this);

//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("BT Conn after StartInq - OK");
//#             }
//#enddebug
            canvas.repaint();
            connecting=false;
            if (stopped){
                throw new Exception("Stopped1");
            }

            // Pause the thread until the user selects another bt device to connect to.
            synchronized (this) {
                while ((!connecting)&&(!stopped)) {
                    wait();
                }
            }

            if (stopped){
                throw new Exception("Stopped2");
            }

//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("BT Conn after select - OK");
//#             }
//#enddebug


//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("BT RD: "+(remoteDevice!=null));
//#             }
//#enddebug

// Searsh for services on the remote bt device.
            //isServiceSearching=true;
            doneServiceSearching=false;
            transactionID=discoveryAgent.searchServices(null, uuidSet, remoteDevice, this);

//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("BT Conn searchService - OK");
//#             }
//#enddebug
            gotService=false;
            synchronized (this) {
                while ((!gotService)&&(!stopped)&&(!doneServiceSearching)) {
                    wait();
                }
            }

            try {
                discoveryAgent.cancelServiceSearch(transactionID);
            } catch (Throwable t4) {
            }

            if (stopped){
                throw new Exception("Stopped3");
            }

//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("BT Conn searchService done - OK");
//#             }
//#enddebug

//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("RD auth: "+remoteDevice.isAuthenticated());
//#             }
//#enddebug
            if (canvas.urlGPS.equals(MapUtil.emptyString)){
//#mdebug
//#                 if (RMSOption.debugEnabled){
//#                     DebugLog.add2Log("BT Conn no services found - ?");
//#                 }
//#enddebug
                try {
                    remoteDevice.authenticate();
                } catch (Throwable t) {
//#mdebug
//#                     if (RMSOption.debugEnabled){
//#                         DebugLog.add2Log("auth "+t.getMessage()+" "+t);
//#                     }
//#enddebug

                }
                canvas.urlGPS="btspp://"+remoteDevice.getBluetoothAddress()+":1";
            }

        } catch (Throwable t) {
//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("BT run:"+t);
//#             }
//#enddebug
        }
        final String url=canvas.urlGPS;
        discoveryAgent=null;
        canvas=null;
        devices=null;
        deviceNames=null;
//#mdebug
//#         if (RMSOption.debugEnabled){
//#             DebugLog.add2Log("BT Conn is over, url "+url);
//#         }
//#enddebug

    }

    // Called if a remote btDevice is found
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
        try {
            try {
                deviceNames[index]=btDevice.getFriendlyName(false); // Store the name of the device
                if (deviceNames[index].equals(MapUtil.emptyString)){
                    throw new Exception();
                }
            } catch (Throwable t) {
                try {
                    deviceNames[index]=btDevice.getBluetoothAddress();
                } catch (Throwable ttt) {
                }
                new Thread(new getFriendlyNameThread(btDevice, index)).start();
//#mdebug
//#                 try {
//#                     if (RMSOption.debugEnabled){
//#                         DebugLog.add2Log("BT can't get name at once:"+index+" - OK");
//#                     }
//#                 } catch (Throwable t2) {
//#                 }
//#enddebug
            }
            index++; // keep track on how many devices are found.
            devices.addElement(btDevice); // store the device
            if (canvas.index<0){
                canvas.index=0;
            }
            canvas.repaint();

//#mdebug
//#             try {
//#                 if (RMSOption.debugEnabled){
//#                     DebugLog.add2Log("BT Found:"+index+":"+deviceNames[index-1]+" - OK");
//#                 }
//#             } catch (Throwable t) {
//#             }
//#enddebug
        } catch (Throwable t) {
//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("BT DevDisc:"+t);
//#             }
//#enddebug
        }
    }
    private boolean gotService;
    // When a connection is made and a service is found, get the connection string to that service.

    public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
//#mdebug
//#         try {
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("BT SSD - got some - "+gotService);
//#             }
//#         } catch (Throwable t) {
//#         }
//#enddebug
        if (gotService){
            return;
        }
        try {
//#mdebug
//#             try {
//#                 if (RMSOption.debugEnabled){
//#                     DebugLog.add2Log("BT SSD - OK");
//#                 }
//#             } catch (Throwable t) {
//#             }
//#enddebug


            int i=0;
            while ((i<servRecord.length)&&(canvas.urlGPS.equals(MapUtil.emptyString))) {
                canvas.urlGPS=servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
//#mdebug
//#                 try {
//#                     if (RMSOption.debugEnabled){
//#                         DebugLog.add2Log("BT url ("+i+"):"+canvas.urlGPS+" - OK");
//#                     }
//#                 } catch (Throwable t) {
//#                 }
//#enddebug
                i++;
            }
            gotService=true;
            synchronized (this) { // resume the thread.
                gotService=true;
                this.notify();
            }

        } catch (Throwable t) {
//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("BT servDisc:"+t);
//#             }
//#enddebug

        }
    }

    public void serviceSearchCompleted(int transID, int respCode) {
//#mdebug
//#         try {
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("BT Serv end - OK");
//#             }
//#         } catch (Throwable t) {
//#         }
//#enddebug
        //isServiceSearching=false;
        doneServiceSearching=true;
        synchronized (this) { // resume the thread.
            doneServiceSearching=true;
            this.notify();
        }
    }

    public void inquiryCompleted(int discType) {
//#mdebug
//#         try {
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("BT inq completed - OK");
//#             }
//#         } catch (Throwable t) {
//#         }
//#enddebug
        //isSearching=false;
        doneSearchingAll=true;
    }

    /********************************************************************
     * a separate thread is created for retrieving the friendlyName of a
     * remote device since this is a time consuming task
     ********************************************************************/
    class getFriendlyNameThread implements Runnable {

        RemoteDevice remDev;
        int devIndex;

        getFriendlyNameThread(RemoteDevice rDevice, int devI) {
            thrdNames.addElement(this);
            remDev=rDevice;
            devIndex=devI;
        }

        public void run() {
            try {
                while (!doneSearchingAll) {
                    Thread.sleep(10);
                }
                try {
                    doneSearchingAll=false;

                    deviceNames[devIndex]=remDev.getFriendlyName(true);
//#mdebug
//#                     try {
//#                         if (RMSOption.debugEnabled){
//#                             DebugLog.add2Log("BT got fn:"+deviceNames[devIndex]);
//#                         }
//#                     } catch (Throwable t) {
//#                     }
//#enddebug
                } finally {
                    doneSearchingAll=true;
                }
            } catch (Throwable ioe) {
//#mdebug
//#                 try {
//#                     if (RMSOption.debugEnabled){
//#                         DebugLog.add2Log("BT error fn:"+ioe);
//#                     }
//#                 } catch (Throwable t) {
//#                 }
//#enddebug
            }
            try {
                thrdNames.removeElement(this);
            } finally {
                remDev=null;
            }
        }
    }
}
