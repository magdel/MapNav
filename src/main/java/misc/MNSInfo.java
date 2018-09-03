package misc;

import java.util.Enumeration;
import java.util.Vector;

public class MNSInfo {
  
  public static Enumeration enumCommPorts() {
    Vector vector = new Vector();
    try{
      String s;
      if((s = System.getProperty("microedition.commports")) != null) {
        int i = 0;
        int j;
        do
        {
          String s1;
          if((j = s.indexOf(',', i)) != -1)
            s1 = s.substring(i, j);
          else
            s1 = s.substring(i);
          i = j + 1;
          s1=s1.toUpperCase();
          if (s1.startsWith("COM"))
            vector.addElement(s1);
        } while(j != -1);
      }
    }catch(Throwable t){}
    return vector.elements();
  }
  
  private static byte locAv;
  public static boolean locationAvailable() {
    //return false;
    if (locAv==0)
      locAv= (
          (classExists("javax.microedition.location.LocationProvider"))
          ||(propertyEquals("microedition.platform","SXG75"))
          ) ?(byte)1:(byte)2;
    return (locAv==1);
  }
  
  private static byte btAv;
  public static boolean bluetoothAvailable() {
    //return false;
    if (btAv==0)
      btAv=(classExists("javax.bluetooth.LocalDevice"))?(byte)1:(byte)2;
    return (btAv==1);
  }
//  private static byte comAv;
  public static boolean comportsAvailable() {
    return true;//override for always make possible
//    if (comAv==0)
//      comAv= (enumCommPorts().hasMoreElements())?(byte)1:(byte)2;
//    return (comAv==1);

  }
  public static boolean filesAvailable() {
    return classExists("javax.microedition.io.file.FileConnection");
  }
  
  public static boolean classExists(String s) {
    boolean flag=false;
    try {
      Class.forName(s);
      flag = true;
    } catch(Throwable t) {}
    return flag;
  }
  
  public static boolean propertyEquals(String prop, String val) {
    boolean flag;
    String s2;
    if((s2 = System.getProperty(prop)) != null)
      flag = s2.equals(val);
    else
      flag = false;
    return flag;
  }
  
//  public static String getIMEI(){
//    String s=null;
//    String[] ss = listIMEI();
//    int i=ss.length-1;
//    while ((s==null)&&(i>=0)) {
//      if (System.getProperty(ss[i])!=null) {
//        s=System.getProperty(ss[i]);
//        break;
//      }
//      i--;
//    }
//    return s;
//  }
//
//  private static String[] listIMEI() {
//    String as[] = new String[6];
//    as[0] = "com.siemens.mp.imei";
//    as[1] = "com.sonyericsson.imei";
//    as[2] = "com.sonyericsson.IMEI";
//    as[3] = "phone.imei";
//    as[4] = "IMEI";
//    as[5] = "com.nokia.IMEI";
//    return as;
//  }
}