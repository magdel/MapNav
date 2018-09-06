/*
 * LocationCanvas.java
 *
 * Created on 25 ������ 2007 �., 23:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package misc;

import RPMap.MapCanvas;
import RPMap.MapPoint;
import RPMap.MapRoute;
import RPMap.MapUtil;
import RPMap.RMSOption;
import app.MapForms;
import gpspack.GPSLocationListener;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import lang.Lang;
import lang.LangHolder;

/**
 *
 * @author julia
 */
public class LocationCanvas extends Canvas implements CommandListener,Runnable, GPSLocationListener{
  
  /** Creates a new instance of LocationCanvas */
  public LocationCanvas() {
    if (!MapCanvas.map.gpsLocListenersRaw.contains(this))
      MapCanvas.map.gpsLocListenersRaw.addElement(this);
    setFullScreenMode(RMSOption.fullScreen);
    addCommand(resetCommand);
    addCommand(exitCommand);
    addCommand(saveCommand);
    setCommandListener(this);
  }
  
  Command saveCommand = new Command(LangHolder.getString(Lang.save), Command.ITEM, 1);
  Command resetCommand = new Command(LangHolder.getString(Lang.reset), Command.ITEM, 2);
  Command exitCommand = new Command(LangHolder.getString(Lang.back), Command.BACK, 10);
  
  private double[] lats= new double[100];
  private double[] lons= new double[100];
  private int[] alts= new int[100];
  private double[] dists= new double[100];
  private int ptscount;
  private void allocate() {
    if (ptscount<lats.length)return;
    int oldlen = lats.length;
    oldlen = oldlen+oldlen/2;
    double[] nlats = new double[oldlen];
    for (int i=lats.length-1;i>=0;i--) nlats[i]=lats[i];
    lats=nlats;
    double[] nlons = new double[oldlen];
    for (int i=lons.length-1;i>=0;i--) nlons[i]=lons[i];
    lons=nlons;
    int[] nalts = new int[oldlen];
    for (int i=alts.length-1;i>=0;i--) nalts[i]=alts[i];
    alts=nalts;
    dists = new double[oldlen];
  }
  
  private void reset(){ptscount=0;calcStat();}
  
  private double LAT;
  private double LON;
  private double ALT;
  private double PRECISE50;
  private double PRECISE90;
  private double MAXDEV;
  
  private double sum(double[] arr){
    double res=0;
    for (int i=ptscount-1;i>=0;i--) res+=arr[i];
    return res;
  }
  private int sum(int[] arr){
    int res=0;
    for (int i=ptscount-1;i>=0;i--) res+=arr[i];
    return res;
  }
  
  private void calcStat(){
    if (ptscount==0) {
      LAT=0;LON=0;ALT=0;return;
    }
    double avg=sum(lats);
    LAT=avg/(double)ptscount;
    avg=sum(lons);
    LON=avg/(double)ptscount;
    avg=sum(alts);
    ALT=((int)(((double)avg/(double)ptscount)*10.))/10.;
    for (int i=ptscount-1;i>=0;i--){
      dists[i]=MapRoute.distBetweenCoords(LAT,LON,lats[i],lons[i]);
    }
    sort(dists);
    PRECISE50=((int)(dists[ptscount/2]*10000.))/10.;
    PRECISE90=((int)(dists[ptscount*9/10]*10000.))/10.;
    MAXDEV=((int)(dists[ptscount-1]*10000.))/10.;
    
  }
  private void sort(double[] arr){
    double t;
    for (int i=ptscount-1;i>=0;i--)
      for (int j=i-1;j>=0;j--){
      if (arr[i]<arr[j]){
        t=arr[j];
        arr[j]=arr[i];
        arr[i]=t;
      }
      }
    
  }
  protected void paint(Graphics g) {
    
    g.setFont(MapUtil.SMALLFONTB);
    int fh=g.getFont().getHeight();
    int dminx = 0;
    int dminy = fh;
    int dmaxx = getWidth();
    int dmaxy = getHeight();
    int dcx = (dminx+dmaxx)/2;
    int dcy = (dminy+dmaxy)/2;
    
    int sr = Math.min((dmaxx-dminx),(dmaxy-dminy))/3;
    
    g.setColor(0x0);
    g.fillRect(0, 0, dmaxx, dmaxy);
    g.setColor(0xFFFF40);
//    g.setColor(0xFFFFFF);
    g.drawString(LangHolder.getString(Lang.precise),dcx,0,Graphics.HCENTER|Graphics.TOP);
    g.drawLine(0,dminy,dmaxx,dminy);
    g.drawString(LangHolder.getString(Lang.points)+':'+' '+String.valueOf(ptscount),0,dminy,Graphics.LEFT|Graphics.TOP);
    g.drawString(LangHolder.getString(Lang.latitude)+':'+' '+MapUtil.coord2DatumLatString(LAT,LON,ALT),0,dminy+fh,Graphics.LEFT|Graphics.TOP);
    g.drawString(LangHolder.getString(Lang.longitude)+':'+' '+MapUtil.coord2DatumLonString(LAT,LON,ALT),0,dminy+fh+fh,Graphics.LEFT|Graphics.TOP);
    g.drawString(CEP50+':'+' '+String.valueOf(PRECISE50)+' '+LangHolder.getString(Lang.m),0,dminy+fh+fh+fh,Graphics.LEFT|Graphics.TOP);
    g.drawString(CEP90+':'+' '+String.valueOf(PRECISE90)+' '+LangHolder.getString(Lang.m),0,dminy+fh+fh+fh+fh,Graphics.LEFT|Graphics.TOP);
    g.drawString(LangHolder.getString(Lang.maxdev)+':'+' '+String.valueOf(MAXDEV)+' '+LangHolder.getString(Lang.m),0,dminy+fh+fh+fh+fh+fh,Graphics.LEFT|Graphics.TOP);
    g.drawString(LangHolder.getString(Lang.altitude)+':'+' '+String.valueOf(ALT)+' '+LangHolder.getString(Lang.m),0,dminy+fh+fh+fh+fh+fh+fh,Graphics.LEFT|Graphics.TOP);
    
  }
  
  private String CEP50 = "CEP50";
  private String CEP90 = "CEP90";
  
  public void commandAction(Command command, Displayable displayable) {
    if (displayable == this) {
      if (command == exitCommand) {
        try {
          MapCanvas.map.gpsLocListenersRaw.removeElement(this);
        }finally {
          MapForms.mm.back2Map();}
      } else if (command == resetCommand) {
        reset();
        repaint();
      }else if (command == saveCommand) {
        if (MapCanvas.map.activeRoute==null)
          MapCanvas.map.activeRoute=new MapRoute(MapRoute.WAYPOINTSKIND);
        MapPoint mp = new MapPoint(LAT,LON,(int)ALT,"WP PP #"+String.valueOf(RMSOption.numberMapPoint));
        RMSOption.numberMapPoint++;
        MapCanvas.map.activeRoute.addMapPoint(mp);
        MapCanvas.showmsg(LangHolder.getString(Lang.precpos),LangHolder.getString(Lang.saved)+'\n'+mp.name, AlertType.INFO,this);
        MapCanvas.map.rmss.saveRoute(MapCanvas.map.activeRoute);
      }
    }
  }
  
  public void run() {
  }
  long lastT;
  public void gpsLocationAction(double lat, double lon, int alt, long time,float spd, float crs) {
    if (lastT+3000>System.currentTimeMillis())return;
    lastT=System.currentTimeMillis();
    allocate();
    lats[ptscount]=lat;
    lons[ptscount]=lon;
    alts[ptscount]=alt;
    ptscount++;
    calcStat();
    repaint();
  }
  
}
