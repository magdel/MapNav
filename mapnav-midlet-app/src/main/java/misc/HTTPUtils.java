package misc;

import RPMap.MapCanvas;
import RPMap.MapUtil;
import RPMap.RMSOption;
import java.io.*;
import java.util.Date;
import java.util.Random;
import javax.microedition.io.*;
import lang.Lang;
import lang.LangHolder;

public final class HTTPUtils {

    public int responseCode;
    public String error;
    public String type;
    public long length;
    public ByteArrayOutputStream baos;
    public boolean ioError;

    /** Creates a new instance of HTTPResult for unifyied content retrievement */
    public HTTPUtils(int rc, String tp, long lng, ByteArrayOutputStream bs, String err, boolean ioError) {
        this.responseCode=rc;
        this.error=err;
        this.type=tp;
        this.length=lng;
        this.baos=bs;
        this.ioError=ioError;
    }

    public String getAsString() {
        return Util.byteArrayToString(baos.toByteArray(), true);
    }

    public final static String getHTTPContentAsString(String url) {
        return Util.byteArrayToString(getHTTPContent(url).baos.toByteArray(), true);
    }

    public final static HTTPUtils getHTTPContent(String url) {
        return getHTTPContent(url, false, null);
    }

    public final static HTTPUtils getHTTPContent(String url, ProgressResponse progressResponse) {
        return getHTTPContent(url, false, progressResponse);
    }
    private static String task="download";

    public final static HTTPUtils getHTTPContent(String url, boolean specifyUserAgent, ProgressResponse progressResponse) {
        //OutputStream os = null;
        ByteArrayOutputStream baos=new ByteArrayOutputStream(4096);
        //byte[] buffer = new byte[100];
        //int bufPos=0;
        HttpConnection c;
        long length=-0xFF;
        int rc=-1;
        String error=null;
        String type=MapUtil.emptyString;
        boolean ioError=false;
        try {
            if (!MapCanvas.inetAvailable){
                MapCanvas.moveX=0;
                MapCanvas.moveY=0;
            }

            c=getHttpConn(url, specifyUserAgent);
            try {
                InputStream is=null;
                try {
                    //c.setRequestMethod(HttpConnection.GET);
                    is=c.openInputStream();
                    type=c.getType();
                    length=c.getLength();
                    rc=c.getResponseCode();

                    MapCanvas.loadedBytes+=512;
                    byte[] pb=new byte[2048];
                    int nr=0;
                    while (true) {
                        nr=is.read(pb);
                        if (nr<0){
                            break;
                        }
                        MapCanvas.loadedBytes+=nr;
                        baos.write(pb, 0, nr);
                        if ((length>0)&&(baos.size()>=length)){
                            break;
                        }
                        if ((progressResponse!=null)&&(length>0)){
                            progressResponse.setProgress((byte) (100*baos.size()/length), task);
                        }
                        MapCanvas.map.repaintMap();
                    }
                    pb=null;
                    MapCanvas.inetAvailable=true;
                } finally {
                    try {
                        if (is!=null) {
                            is.close();
                        }
                    } catch (Throwable t) {
                    }
                }
            } finally {
                try {
                    if (c!=null) {
                        c.close();
                    }
                } catch (Throwable t) {
                }
            }
        } catch (IOException ioe) {
            error=ioe.getMessage();
            ioError=true;
//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("HTPC IO:"+ioe.toString());
//#             }
            //#enddebug
        } catch (Throwable e) {
            error=e.getMessage();
            
//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("HTPC:"+e.toString());
//#             }
            //#enddebug
        }
        return new HTTPUtils(rc, type, length, baos, error, ioError);
    }

//  public final static String sendPostRequestS(String data_str, String url, String timeStmp) {
//    return Util.byteArrayToString(sendPostRequest(data_str, url, timeStmp),true);
////    String res =null;
////    try {
////      HttpConnection httpconn;
////      DataInputStream din;
////      ByteArrayOutputStream baos = new ByteArrayOutputStream();
////      //DataOutputStream dos= new DataOutputStream(baos);
////      OutputStream os;
////
////      //StringBuffer stringbuffer;
////      httpconn = null;
////      din = null;
////      //dos = null;
////      // System.out.println("Sending:" + url.toString());
////      (httpconn = (HttpConnection)Connector.open(url, 3)).setRequestMethod("POST");
////      try{
////        if(timeStmp != null)
////          httpconn.setRequestProperty("Content-Type", "multipart/form-data; boundary=--" + timeStmp);
////        byte data[] = Util.stringToByteArray(data_str,true);
////        httpconn.setRequestProperty("Content-Length", Integer.toString(data.length));
////        os = httpconn.openOutputStream();
////        os.write(data);
//////        for(int i = 0; i < data.length; i++) {
//////          dos.writeByte(data[i]);
//////          //       if(i % 100 == 0)
//////          //         System.out.println("writing..." + i);
//////        }
//////        dos = httpconn.openDataOutputStream();
//////        for(int i = 0; i < data.length; i++) {
//////          dos.writeByte(data[i]);
//////          //       if(i % 100 == 0)
//////          //         System.out.println("writing..." + i);
//////        }
////
//////#debug
////        System.out.println("Writing done");
//////        stringbuffer = new StringBuffer();
////
////        int iRC=httpconn.getResponseCode();
////
////        long l= httpconn.getLength();
////        din = new DataInputStream(httpconn.openInputStream());
////        int k;
////        if(l  != -1L) {
////          for(int i1 = 0; (long)i1 < l; i1++) {
////            int j;
////            if((j = din.read()) != -1)
////              baos.write(j);
////          }
////
////        } else {
////          while((k = din.read()) != -1)
////            baos.write(k);
////        }
////
////        res = httpconn.getResponseMessage() + "\r\n"+Util.byteArrayToString(baos.toByteArray(),true);
////        baos=null;
////
////
////      } finally {
////        try {
////          if(httpconn != null)
////            httpconn.close();
////        } catch(Throwable _ex) { }
////        try {
////          din.close();
////        } catch(Throwable _ex) { }
//////        try {
//////          if(dos != null)
//////            dos.close();
//////        } catch(Throwable _ex) { }
////      }
//////#debug
////      System.out.println("Writing ok");
////      return res;
////    }catch(Throwable ttt){;
//////#mdebug
////    if (RMSOption.debugEnabled) DebugLog.add2Log("HTTPsP:"+ttt.toString());
//////#enddebug
//////#debug
////    System.out.println("Writing BAD:"+ttt.toString());
////    return null;}
//
//  }
//  public final static byte[] sendPostRequest(String data_str, String url, String timeStmp) {
//    try {
//      HttpConnection httpconn;
//      DataInputStream din;
//      ByteArrayOutputStream baos = new ByteArrayOutputStream();
//      //DataOutputStream dos= new DataOutputStream(baos);
//      OutputStream os;
//
//      //StringBuffer stringbuffer;
//      httpconn = null;
//      din = null;
//      //dos = null;
//      // System.out.println("Sending:" + url.toString());
//      (httpconn = (HttpConnection)Connector.open(url, 3)).setRequestMethod("POST");
//      try{
//        if(timeStmp != null)
//          httpconn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + timeStmp);
//        byte data[] = Util.stringToByteArray(data_str,true);
//        httpconn.setRequestProperty("Content-Length", Integer.toString(data.length));
//        os = httpconn.openOutputStream();
//        os.write(data);
////        for(int i = 0; i < data.length; i++) {
////          os.write(data[i]);
////          //       if(i % 100 == 0)
////          //         System.out.println("writing..." + i);
////        }
////        dos = httpconn.openDataOutputStream();
////        for(int i = 0; i < data.length; i++) {
////          dos.writeByte(data[i]);
////          //       if(i % 100 == 0)
////          //         System.out.println("writing..." + i);
////        }
//
////#debug
//        System.out.println("Writing done");
////        stringbuffer = new StringBuffer();
//
//        int iRC=httpconn.getResponseCode();
//        //      stringbuffer.append(httpconn.getResponseCode()+" "+httpconn.getResponseMessage() + " \n");
//
//        long l= httpconn.getLength();
//        din = new DataInputStream(httpconn.openInputStream());
//        int k;
//        if(l  != -1L) {
//          for(int i1 = 0; (long)i1 < l; i1++) {
//            int j;
//            if((j = din.read()) != -1)
//              baos.write(j);
//          }
//
//        } else {
//          while((k = din.read()) != -1)
//            baos.write(k);
//        }
//      } finally {
//        try {
//          if(httpconn != null)
//            httpconn.close();
//        } catch(Throwable _ex) { }
//        try {
//          din.close();
//        } catch(Throwable _ex) { }
////        try {
////          if(dos != null)
////            dos.close();
////        } catch(Throwable _ex) { }
//      }
////#debug
//      System.out.println("Writing ok");
//      return baos.toByteArray();
//    }catch(Throwable ttt){;
////#mdebug
//    if (RMSOption.debugEnabled) DebugLog.add2Log("HTTPsP:"+ttt.toString());
////#enddebug
////#debug
//    System.out.println("Writing BAD:"+ttt.toString());
//    return null;}
//
//  }
    public final static String sendDataPostRequestS(String data_str, byte[] postdata, String end_data_str, String url, String timeStmp, ProgressResponse prResp) {
        return Util.byteArrayToString(sendDataPostRequest(data_str, postdata, end_data_str, url, timeStmp, prResp), true);
    }

    public final static byte[] sendPostRequest(String data_str, String url, String timeStmp) {
        return sendDataPostRequest(data_str, null, null, url, timeStmp, null);
    }

    public final static byte[] sendDataPostRequest(String data_str, byte[] postdata, String end_data_str, String url, String timeStmp, ProgressResponse prResp) {
        try {
            DataInputStream din=null;
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            //DataOutputStream dos= new DataOutputStream(baos);
            DataOutputStream os=null;

            //StringBuffer stringbuffer;

            //httpconn = null;

            byte[] datas=(data_str==null)?new byte[0]:Util.stringToByteArray(data_str, true);
            byte[] datae=(end_data_str==null)?new byte[0]:Util.stringToByteArray(end_data_str, true);

            int conlen=datas.length;
            if (postdata!=null){
                conlen+=postdata.length;
            }
            //if (datae!=null)
            conlen+=datae.length;

            byte data[]=new byte[conlen];
            System.arraycopy(datas, 0, data, 0, datas.length);
            int ai=datas.length;
            datas=null;

            if (postdata!=null){
                System.arraycopy(postdata, 0, data, ai, postdata.length);
                ai+=postdata.length;
            }
            postdata=null;

            System.arraycopy(datae, 0, data, ai, datae.length);
            //ai+=datae.length;
            datae=null;
//dos = null;
            // System.out.println("Sending:" + url.toString());
            HttpConnection httpconn;
            (httpconn=(HttpConnection) Connector.open(url, Connector.READ_WRITE)).setRequestMethod(HttpConnection.POST);
            try {

                httpconn.setRequestProperty("Content-Type", "multipart/form-data; boundary="+timeStmp);
                //httpconn.setRequestProperty("Authorization", "Basic bWFwbmF2QGZyb250LnJ1Om15c3RyZWV0");
                httpconn.setRequestProperty("Content-Length", String.valueOf(conlen));
//        httpconn.setRequestProperty("Content-Length", Integer.toString(conlen));
                //httpconn.setRequestProperty("Content-Length", Integer.toString(conlen));
                //os = httpconn.openOutputStream()
                os=httpconn.openDataOutputStream();

////#debug
//        System.out.println("Writing started");
                if (prResp!=null){
                    prResp.setProgress((byte) 5, "");
                }
                //os.flush();

                for (int i=0; i<data.length; i++) {
                    if (i%1000==0){
                        if (prResp!=null){
                            prResp.setProgress((byte) (5+90*i/(data.length)), "");
                        }
                    }
//     
                    os.writeByte(data[i]);
                }
//        for (int i=0;i<datas.length;i++)
//          os.writeByte(datas[i]);
//        os.write(datas);
                //os.flush();


//        if (prResp!=null) prResp.setProgress((byte)10,"");
//        if (postdata!=null){
//          if (postdata.length<10000000){
//            for (int i=0;i<postdata.length;i++)
//              os.writeByte(postdata[i]);
////            os.write(postdata);
//         //os.flush();
//          }
//          else {
//            int wrpos = 0,bs;
//            while (wrpos<postdata.length){
//              bs=postdata.length-wrpos;
//              if (bs>10000) bs=10000;
//              os.write(postdata,wrpos,bs);
//              os.flush();
//              wrpos+=bs;
//              if (prResp!=null) prResp.setProgress((byte)(100*wrpos/(postdata.length+1)),"");
//            }
//          }
//          if (prResp!=null) prResp.setProgress((byte)95,"");
//           for (int i=0;i<datae.length;i++)
//            os.writeByte(datae[i]);
////          os.write(datae);
//          os.flush();
//          
//        }

                if (prResp!=null){
                    prResp.setProgress((byte) 95, "");
                }
                //os.flush();
                MapCanvas.loadedBytes+=conlen;

////#debug
//        System.out.println("Writing done");
//        stringbuffer = new StringBuffer();

                int iRC=httpconn.getResponseCode();
                //      stringbuffer.append(httpconn.getResponseCode()+" "+httpconn.getResponseMessage() + " \n");

                long l=httpconn.getLength();
                din=new DataInputStream(httpconn.openInputStream());
                int k;
                if (l!=-1L){
                    for (int i1=0; (long) i1<l; i1++) {
                        int j;
                        if ((j=din.read())!=-1){
                            baos.write(j);
                            MapCanvas.loadedBytes++;
                        }
                    }

                } else {
                    while ((k=din.read())!=-1) {
                        baos.write(k);
                        MapCanvas.loadedBytes++;
                    }
                }


            } finally {
                try {
                    if (din!=null){
                        din.close();
                    }
                } catch (Throwable _ex) {
                }
                try {
                    if (os!=null){
                        os.close();
                    }
                } catch (Throwable _ex) {
                }
                try {
                    if (httpconn!=null){
                        httpconn.close();
                    }
                } catch (Throwable _ex) {
                }
            }
            MapCanvas.inetAvailable=true;

//#debug
//#             System.out.println("Result is read: "+baos.size());
            return baos.toByteArray();
        } catch (Throwable ttt) {
//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("HTTPsP:"+ttt.toString());
//#             }
//#enddebug
//#debug
//#             System.out.println("Writing BAD:"+ttt.toString());
            return null;
        }

    }

    public final static String getTS() {
        String s;
        Random random=new Random();
        Date date=new Date();
        random.setSeed(date.getTime());
        s=Integer.toHexString(random.nextInt());
        s=s+Integer.toHexString(random.nextInt());
        return "---------------------------"+s.substring(4);
    }

    public final static String SendLog(String log) {
        String username=RMSOption.netRadarLogin;
        if (username==null){
            username="unknown";
        } else if (username.equals(MapUtil.emptyString)){
            username="empty";
        }
        String timeStmp=getTS();
        StringBuffer stringbuffer=new StringBuffer();
        //stringbuffer.append("--" + timeStmp + "\r\n" + "Content-Disposition: form-data; name=\"username\"\r\n" + "\r\n" + a_java_lang_String_fld + "\r\n" + "--" + timeStmp + "\r\n" + "Content-Disposition: form-data; name=\"auth_token\"\r\n" + "\r\n" + b + "\r\n" + "--" + timeStmp + "\r\n" + "Content-Disposition: form-data; name=\"api_sig\"\r\n" + "\r\n" + d + "\r\n");

        if (!username.equals(MapUtil.emptyString)){
            appendParam(stringbuffer, "username", username, timeStmp);
        }
//      stringbuffer.append("--" + timeStmp + "\r\n"+"Content-Disposition: form-data; name=\"username\"\r\n\r\n" + username + "\r\n");
//    stringbuffer.append("--" + timeStmp + "\r\n" + "Content-Disposition: form-data; name=\"logtext\"\r\n" + "Content-Type: text/plain\r\n" + "\r\n");
        if (!log.equals(MapUtil.emptyString)){
            appendParam(stringbuffer, "logtext", log, timeStmp);
        }
//      stringbuffer.append("--" + timeStmp + "\r\n"+"Content-Disposition: form-data; name=\"logtext\"\r\n\r\n" + log + "\r\n");

        //stringbuffer.append("--" + timeStmp + "\r\n" + "Content-Disposition: form-data; name=\"logtext\"\r\n" + "Content-Type: text/plain\r\n" + "\r\n");
        //stringbuffer.append(log);
//    stringbuffer.append(MapUtil.CRLFs);
//    stringbuffer.append(MapUtil.DBSPs);
//    stringbuffer.append(timeStmp);
//    stringbuffer.append(MapUtil.DBSPs);
//    stringbuffer.append(MapUtil.CRLFs);
        //stringbuffer.append("\r\n--" + timeStmp + "--\r\n");
        appendParamEnd(stringbuffer, timeStmp);
        String url=MapUtil.homeSiteURL+"cgi-bin/log.pl";
        String resp=Util.byteArrayUTFToString(
          HTTPUtils.sendPostRequest(stringbuffer.toString(), url, timeStmp));
//?LangHolder.getString(Lang.sent):LangHolder.getString(Lang.error);
        //System.out.println(resp);
        return resp;
    }

    public final static void appendParamEnd(StringBuffer stringbuffer, String ts) {
        //stringbuffer.append(MapUtil.CRLFs);
        stringbuffer.append(MapUtil.DBSPs);
        stringbuffer.append(ts);
        stringbuffer.append(MapUtil.DBSPs);
        stringbuffer.append(MapUtil.CRLFs);
    }

    public final static void appendParam(StringBuffer sb, String param, String val, String ts) {
        sb.append(MapUtil.DBSPs);
        sb.append(ts);
        sb.append(MapUtil.CRLFs);
        sb.append("Content-Disposition: form-data; name=\"");
        sb.append(param);
        sb.append('"');
        sb.append(MapUtil.CRLFs);
        sb.append(MapUtil.CRLFs);
        sb.append(val);
        sb.append(MapUtil.CRLFs);
    }

    public final static void packParams(StringBuffer sb, String[] params, String[] vals, String ts) {
        for (int i=0; i<params.length; i++) {
            appendParam(sb, params[i], vals[i], ts);
        }
    }

    public final static byte[] sendPostRequest(String url, String[] params, String[] vals) {
        String ts=HTTPUtils.getTS();
        StringBuffer sb=new StringBuffer();
        HTTPUtils.packParams(sb, params, vals, ts);
        HTTPUtils.appendParamEnd(sb, ts);
        return HTTPUtils.sendDataPostRequest(sb.toString(), null, null, url, ts, null);
    }

    public final static String sendPostRequestS(String url, String[] params, String[] vals) {
        return Util.byteArrayToString(sendPostRequest(url, params, vals), true);
    }

    /*
    public static String sendGetRequest(String s)
    {
    try{
    HttpConnection httpconnection;
    DataInputStream datainputstream;
    StringBuffer stringbuffer;
    httpconnection = null;
    datainputstream = null;
    stringbuffer = new StringBuffer();
    System.out.println(s);
    (httpconnection = (HttpConnection)Connector.open(s, 3)).setRequestMethod("GET");
    stringbuffer.append(httpconnection.getResponseMessage() + "\r\n");
    datainputstream = new DataInputStream(httpconnection.openInputStream());
    int j;
    long l;
    if((l = httpconnection.getLength()) != -1L)
    {
    for(int k = 0; (long)k < l; k++)
    {
    int i;
    if((i = datainputstream.read()) != -1)
    stringbuffer.append((char)i);
    }

    } else
    {
    while((j = datainputstream.read()) != -1)
    stringbuffer.append((char)j);
    }
    datainputstream.close();
    try
    {
    if(httpconnection != null)
    httpconnection.close();
    }
    catch(IOException _ex) { }
    try
    {
    datainputstream.close();
    }
    catch(IOException _ex) { }

    stringbuffer = new StringBuffer("ERROR!");
    try
    {
    if(httpconnection != null)
    httpconnection.close();
    }
    catch(IOException _ex) { }
    try
    {
    if(datainputstream != null)
    datainputstream.close();
    }
    catch(IOException _ex) { }

    try
    {
    if(httpconnection != null)
    httpconnection.close();
    }
    catch(IOException _ex) { }
    try
    {
    if(datainputstream != null)
    datainputstream.close();
    }
    catch(IOException _ex) { }
    //throw exception;
    //System.out.println("Result of send:" + stringbuffer.toString());
    return stringbuffer.toString();
    }catch(Throwable ttt) {}
    return "errf";
    } */
    public final static String urlEncodeString(String url) {
        StringBuffer stringbuffer=new StringBuffer();
        for (int i=0; i<url.length(); i++) {
            char c=url.charAt(i);
            switch (c) {
                case 32: // ' '
                case 38: // '&'
                case 40: // '('
                case 41: // ')'
                case 58: // ':'
                case 63: // '?'
                case 64: // '@'
                    stringbuffer.append('%'); // Add % character
                    stringbuffer.append(toHexChar((c&0xF0)>>4));
                    stringbuffer.append(toHexChar(c&0x0F));
                    break;

                default:
                    stringbuffer.append(c);
                    break;
            }
        }

        return stringbuffer.toString();
    }

    public final static String urlEncodeStringAll(String url) {
        StringBuffer stringbuffer=new StringBuffer();
        for (int i=0; i<url.length(); i++) {
            char c=url.charAt(i);
            switch (c) {
                case 32: // ' '
                case 38: // '&'
                case 40: // '('
                case 41: // ')'
                case 58: // ':'
                case 63: // '?'
                case 64: // '@'
                    stringbuffer.append('%'); // Add % character
                    stringbuffer.append(toHexChar((c&0xF0)>>4));
                    stringbuffer.append(toHexChar(c&0x0F));
                    break;
                default:
                    if ((c<0x7b)&&(c>32)){
                        stringbuffer.append(c);
                    } else {
                        stringbuffer.append('%'); // Add % character
                        stringbuffer.append(toHexChar((c&0xF0)>>4));
                        stringbuffer.append(toHexChar(c&0x0F));
                    }
//          else {
//          stringbuffer.append('%'); // Add % character
//          stringbuffer.append(toHexChar((c & 0xF000) >> 12));
//          stringbuffer.append(toHexChar((c & 0xF00) >> 8));
//          stringbuffer.append(toHexChar((c & 0xF0) >> 4));
//          stringbuffer.append(toHexChar(c & 0x0F));
//          }
                    break;
            }
        }

        return stringbuffer.toString();
    }

//   // Unreserved punctuation mark/symbols
//    private static String mark = "-_.!~*'()\"";
//
//    /**
//     * Converts Hex digit to a UTF-8 "Hex" character
//     * @param digitValue digit to convert to Hex
//     * @return the converted Hex digit
//     */
    static private char toHexChar(int digitValue) {
        if (digitValue<10) // Convert value 0-9 to char 0-9 hex char
        {
            return (char) ('0'+digitValue);
        } else // Convert value 10-15 to A-F hex char
        {
            return (char) ('A'+(digitValue-10));
        }
    }
//
//    /**
//     * Encodes a URL - This method assumes UTF-8
//     * @param url URL to encode
//     * @return the encoded URL
//     */
//    static public String encodeURL(String url) {
//        StringBuffer encodedUrl = new StringBuffer(); // Encoded URL
//        int len = url.length();
//        // Encode each URL character
//        for(int i = 0; i < len; i++) {
//            char c = url.charAt(i); // Get next character
//            if ((c >= '0' && c <= '9') ||
//                (c >= 'a' && c <= 'z') ||
//                (c >= 'A' && c <= 'Z'))
//                // Alphanumeric characters require no encoding, append as is
//                encodedUrl.append(c);
//            else {
//                int imark = mark.indexOf(c);
//                if (imark >=0) {
//                    // Unreserved punctuation marks and symbols require
//                    //  no encoding, append as is
//                    encodedUrl.append(c);
//                } else {
//                    // Encode all other characters to Hex, using the format "%XX",
//                    //  where XX are the hex digits
//                    encodedUrl.append('%'); // Add % character
//                    // Encode the character's high-order nibble to Hex
//                    encodedUrl.append(toHexChar((c & 0xF0) >> 4));
//                    // Encode the character's low-order nibble to Hex
//                    encodedUrl.append(toHexChar (c & 0x0F));
//                }
//            }
//        }
//        return encodedUrl.toString(); // Return encoded URL
//    }
    private static String UserAgentValue="Mozilla/5.0 (Windows; U; Windows NT 6.0; ru; rv:1.9) Gecko/2008052906 Firefox/3.0";
    private static String UserAgent="User-Agent";
    private static String GET="GET";
    private static String location="Location";

    //private static String defUA="";
    //private static boolean gotdefUA;
    /**
     * Return connection even if redirection happens
     */
    public final static HttpConnection getHttpConn(String url, boolean specifyUserAgent) throws IOException {
        try {
            HttpConnection httpConn=null;
            int numTry=0;
            int respCode=-1;
            while (numTry++<5) {
                httpConn=(HttpConnection) Connector.open(url);
                httpConn.setRequestMethod(GET);
                //if (!gotdefUA){
                //   defUA=httpConn.getRequestProperty(UserAgent);
                //   gotdefUA=true;
                // }
                if (specifyUserAgent){
                    httpConn.setRequestProperty(UserAgent, UserAgentValue);
                }
                //else
                //httpConn.setRequestProperty(UserAgent, defUA);

                //httpConn.setRequestProperty("Referer", "maps.google.com");
                respCode=httpConn.getResponseCode();

                if (respCode!=HttpsConnection.HTTP_TEMP_REDIRECT){
                    if (respCode!=HttpsConnection.HTTP_MOVED_TEMP){
                        if (respCode!=HttpsConnection.HTTP_MOVED_PERM){
                            break;
                        }
                    }
                }
                url=httpConn.getHeaderField(location);
                httpConn.close();
                numTry--;
            }
            // if(respCode == 200) {
            return httpConn;
            //  }
        } catch (Throwable t) {
//#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("Http cr:"+t.toString()+" \n"+url);
//#             }
//#enddebug
            if (t instanceof IOException)
                throw (IOException)t;
        }
        throw new IOException("No internal connection");
    }

    /**
     * Return connection even if redirection happens
     */
    public final static HttpConnection getHttpConn(String url) throws IOException {
        return getHttpConn(url, false);
    }

    public final static String urlEncodeUnicode(String paramValue) {
        byte[] ba=Util.stringToByteArray(paramValue, true);
        StringBuffer sb=new StringBuffer(30);
        int c;
        for (int i=0; i<ba.length; i++) {
            c=ba[i];
            sb.append((char) (c));
        }
        return HTTPUtils.urlEncodeStringAll(sb.toString());
    }
}
