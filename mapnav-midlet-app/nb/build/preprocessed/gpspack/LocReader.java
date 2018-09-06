/*
 * LocReader.java
 *
 * Created on 18 ?????? 2007 ?., 22:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package gpspack;

import RPMap.MapCanvas;
import RPMap.MapUtil;
import RPMap.RMSOption;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;
//#debug
import misc.DebugLog;

/**
 *
 * @author RFK
 */
public class LocReader extends LocReaderInt implements LocationListener, Runnable {

    private Object prov=null;
    private GPSReader gpsReader;

    /** Creates a new instance of LocReader */
    public LocReader(GPSReader gpsReader) {
        GPSReader.NUM_SATELITES=0;
        this.gpsReader=gpsReader;
        stopServ=false;
        locThread=new Thread(this);
        locThread.start();
    }
    Thread locThread;
    private boolean stopServ;

    public void stop() {
        GPSReader.NUM_SATELITES=0;
        gpsReader=null;
        stopServ=true;
        locThread=new Thread(this);
        locThread.start();
    }

    private void requestProvider() {
        Criteria crit1=new Criteria();
        crit1.setHorizontalAccuracy(25); // 25m
        crit1.setVerticalAccuracy(100); // 25m
        crit1.setPreferredResponseTime(Criteria.NO_REQUIREMENT);
        crit1.setPreferredPowerConsumption(Criteria.NO_REQUIREMENT);
        crit1.setCostAllowed(true);
        crit1.setSpeedAndCourseRequired(true);
        crit1.setAltitudeRequired(true);
        //crit1.setAddressInfoRequired(true);

        Criteria crit2=new Criteria();
        crit2.setPreferredResponseTime(Criteria.NO_REQUIREMENT);
        crit2.setPreferredPowerConsumption(Criteria.NO_REQUIREMENT);
        crit2.setCostAllowed(false);
        crit2.setSpeedAndCourseRequired(true);
        crit2.setAltitudeRequired(true);
        try {
            prov=LocationProvider.getInstance(crit1);
            if (prov!=null){
                go();
                return;
            }

            prov=LocationProvider.getInstance(crit2);
            if (prov!=null){
                go();
                return;
            }

            prov=LocationProvider.getInstance(null);
            if (prov!=null){
                go();
                return;
            }

        } catch (LocationException le) {
//#mdebug
            if (RMSOption.debugEnabled){
                DebugLog.add2Log("LR:"+le);
            }
//#enddebug
            locThread=null;
            MapCanvas.map.endGPSLookup(false);
        }
    }
    private Location lastLocation;

    private void go() {
        LocationProvider provider=(LocationProvider) prov;
        provider.setLocationListener(this, 1, -1, -1);
        gpsReader.resetCalcs();
        GPSReader.NUM_SATELITES=0;
        GPSReader.POSFIX=1;
        try {
            lastLocation=provider.getLocation(360);
        } catch (Throwable ex) {
            // ex.printStackTrace();
        }
        providerStateChanged(provider, provider.getState());

        //a_javax_microedition_location_LocationProvider_fld.setLocationListener(new m(this), -1, -1, -1);
        //lmProviderStateChanged(a_javax_microedition_location_LocationProvider_fld, a_javax_microedition_location_LocationProvider_fld.getState());
        //a_javax_microedition_location_Location_fld = a_javax_microedition_location_LocationProvider_fld.getLocation(360);

    }
    /*
    public void autoSearch(ProviderStatusListener listener) {
    try {
    for (int i = 0; i < freeCriterias.length; i++) {
    criteria = freeCriterias[i];

    provider = LocationProvider.getInstance(criteria);
    if (provider != null) {
    // Location provider found, send a selection event.
    listener.providerSelectedEvent();
    return;
    }
    }

    if (queryUI.confirmCostProvider()) {
    for (int i = 0; i < costCriterias.length; i++) {
    criteria = costCriterias[i];

    provider = LocationProvider.getInstance(criteria);
    if (provider != null) {
    // Location provider found, send a selection event.
    listener.providerSelectedEvent();
    return;
    }
    }
    } else {
    queryUI.showNoFreeServiceFound();
    }
    } catch (LocationException le) {
    queryUI.showOutOfService();
    }
    }
     */
    private final String[] dataGSVs=new String[20];
    private static String mimeExtra="application/X-jsr179-location-nmea";
    private static String GPGGA="GPGGA";
    private static String GPRMC="GPRMC";

    public void locationUpdated(LocationProvider locationProvider, Location location) {
//#debug
        float errpos=0;
        lastLocation=location;
        try {
            try {
                Calendar calendar;//"UTC"
                (calendar=Calendar.getInstance(TimeZone.getDefault())).setTime(new Date());
//#debug
                errpos=1;

                gpsReader.hour=(byte) calendar.get(Calendar.HOUR_OF_DAY);
                gpsReader.min=(byte) calendar.get(Calendar.MINUTE);
                gpsReader.sec=(byte) calendar.get(Calendar.SECOND);

//#debug
                errpos=1.1f;
                gpsReader.year=(short) calendar.get(Calendar.YEAR);
                gpsReader.month=(byte) (calendar.get(Calendar.MONTH)+1);
                gpsReader.day=(byte) calendar.get(Calendar.DAY_OF_MONTH);
//#debug
                errpos=1.2f;

                //GPSReader.UTC = MapUtil.make2(calendar.get(Calendar.HOUR_OF_DAY))+':'+MapUtil.make2(calendar.get(Calendar.MINUTE))+':'+MapUtil.make2(calendar.get(Calendar.SECOND));
            } catch (Throwable t) {
            }
//#debug
            errpos=2;
            if (lastProviderState!=LocationProvider.AVAILABLE){
                GPSReader.NUM_SATELITES=0;
                GPSReader.POSFIX=1;
                gpsReader.resetCalcs();
                return;
            }
//#debug
            errpos=2.0001f;
            try {
                String extraData=lastLocation.getExtraInfo(mimeExtra);
                if ((extraData!=null)&&(extraData.length()>10)){
                    //#mdebug
                         if (RMSOption.debugEnabled){
                             DebugLog.add2Log("LR MIME:"+extraData);
                         }
//#enddebug
                    MapUtil.parseString('$'+extraData, '$', dataGSVs);
                    for (int i=0; i<dataGSVs.length; i++) {
                        if ((dataGSVs[i]!=null)&&(dataGSVs[i].length()>5)){
                            //#mdebug
                            //   if (RMSOption.debugEnabled){
                            //       DebugLog.add2Log("LR MI:"+dataGSVs[i]);
                            //   }
//#enddebug
                            if (dataGSVs[i].startsWith(GPGGA)) {
                                continue;
                            }
                            if (dataGSVs[i].startsWith(GPRMC)) {
                                continue;
                            }
                            gpsReader.parseGPS(('$'+dataGSVs[i]).getBytes());
                        }
                    }
                }else{
                     //#mdebug
                         if (RMSOption.debugEnabled){
                             DebugLog.add2Log("LR MIME absent: "+extraData);
                         }
//#enddebug

                }
            } catch (Throwable ttee) {
//#mdebug
                if (RMSOption.debugEnabled){
                    DebugLog.add2Log("LR ED LU:"+errpos+":"+ttee);
                }
//#enddebug
            }
            if (!lastLocation.isValid()){
                GPSReader.NUM_SATELITES=0;
                GPSReader.POSFIX=1;
                gpsReader.resetCalcs();
                GPSReader.satLost();
                return;
            } else if (GPSReader.satFound()){
                return;
            }

//#debug
            errpos=2.05f;
            QualifiedCoordinates qc=lastLocation.getQualifiedCoordinates();
//#debug
            errpos=2.055f;
            if (qc==null){
                return;
            }

//#debug
            errpos=2.1f;
            double lat=qc.getLatitude(), lon=qc.getLongitude();
            if ((Math.abs(lat)<0.0002)&&(Math.abs(lon)<0.0002)){
                GPSReader.NUM_SATELITES=0;
                GPSReader.POSFIX=1;
                gpsReader.resetCalcs();
                return;
            }

            GPSReader.NUM_SATELITES=5;
            GPSReader.POSFIX=3;
            GPSReader.SAT_TIME_MILLIS=System.currentTimeMillis();
//#debug
            errpos=2.2f;
            GPSReader.LATITUDE=lat;
            GPSReader.LONGITUDE=lon;
            GPSReader.ALTITUDE=(int) qc.getAltitude();
//#debug
            errpos=2.3f;
            GPSReader.SPEED_KMH=(float) ((int) (lastLocation.getSpeed()*36f)/10f);
//#debug
            errpos=3;
            if (GPSReader.SPEED_KMH>3){
                GPSReader.COURSE=lastLocation.getCourse();
                GPSReader.COURSE_I=(int) GPSReader.COURSE;
            }

//#debug
            errpos=4;
            //if (MapCanvas.map.gpsLocListenersRaw.size()!=0)
            for (int i=MapCanvas.map.gpsLocListenersRaw.size()-1; i>=0; i--) {
                GPSLocationListener locList=(GPSLocationListener) MapCanvas.map.gpsLocListenersRaw.elementAt(i);
                locList.gpsLocationAction(GPSReader.LATITUDE, GPSReader.LONGITUDE, GPSReader.ALTITUDE, System.currentTimeMillis(), GPSReader.SPEED_KMH, GPSReader.COURSE);
            }
//#debug
            errpos=5;

//GPSReader.SPEED_KMH=lastLocation.getSpeed()*3.6f;

            gpsReader.calculateOdometer();
//#debug
            errpos=6;
            gpsReader.calcStat();
//#debug
            errpos=6.1f;
            gpsReader.needAddTrackPoint(System.currentTimeMillis());
//#debug
            errpos=6.2f;
            gpsReader.calculateAltSpeed();

//#debug
            errpos=6.3f;
            gpsReader.needPaint();
        } catch (Throwable tt) {
//#mdebug
            if (RMSOption.debugEnabled){
                DebugLog.add2Log("LR LU:"+errpos+":"+tt);
            }
//#enddebug
        }
    }
    private int lastProviderState=-632576;

    public void providerStateChanged(LocationProvider locationProvider, int i) {
        // locationProvider.
        lastProviderState=locationProvider.getState();
    }

    public void run() {
        if (stopServ){
            LocationProvider provider=(LocationProvider) prov;
            if (provider!=null){
                provider.setLocationListener(null, -1, -1, -1);
            }
            locThread=null;
        } else {
            requestProvider();
        }
    }
}
