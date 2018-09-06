/*****************************************************************************
 *
 * Description: Using IR with the obex protocol.
 *
 * Created By: Johan Kateby
 *
 * @file        DataSenderThread.java
 *
 * COPYRIGHT All rights reserved Sony Ericsson Mobile Communications AB 2004.
 * The software is the copyrighted work of Sony Ericsson Mobile Communications AB.
 * The use of the software is subject to the terms of the end-user license
 * agreement which accompanies or is included with the software. The software is
 * provided "as is" and Sony Ericsson specifically disclaim any warranty or
 * condition whatsoever regarding merchantability or fitness for a specific
 * purpose, title or non-infringement. No warranty of any kind is made in
 * relation to the condition, suitability, availability, accuracy, reliability,
 * merchantability and/or non-infringement of the software provided herein.
 *
 *****************************************************************************/
package misc;

import RPMap.FileTrackSend;
import RPMap.MapCanvas;
import RPMap.MapRoute;
import RPMap.MapRouteLoader;
import RPMap.RMSOption;
import RPMap.RouteSend;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Displayable;
import javax.obex.*;
import javax.microedition.io.*;
import java.io.*;
import javax.microedition.lcdui.Alert;
//#debug
import misc.DebugLog;
import lang.Lang;
import lang.LangHolder;

public class DataSenderThread extends Thread implements ProgressStoppable {
  // Info string of the outcome of the send operation
  //private String status= "Message sent :-)";
  Displayable backDisp;
  MapRoute mR;
  public static final byte SENDTYPE_IR=1;
  public static final byte SENDTYPE_BLUE=2;
  private byte sendType;
  public String url="irdaobex://discover";
      

  public DataSenderThread(Displayable backDisp, MapRoute mR, byte sendType) {
    this.backDisp=backDisp;
    this.mR=mR;
    this.sendType=sendType;
  }

    private void closeIR(ClientSession session, OutputStream out, Operation op) {
        if (session!=null){
            try {
                try {
                    session.disconnect(null);
                } finally {
                    session.close();
                }
            } catch (Throwable ioe) {
            }
        }
        try {
            if (out!=null){
                out.close();
            }
            if (op!=null){
                op.close();
            }
        } catch (Throwable ioe) {
            //status= "Failure closing connections:"+ioe.toString();
        }
    }

  private void sendWithIR() {
    // Header for setting the size and filename of the message
    HeaderSet head=null;
    progressResponse.setProgress((byte) -1, LangHolder.getString(Lang.connecting));
    try {
      //private String bt_url = "btgoep://0050C000321B:12";
      //The ClientSession interface provides methods for OBEX requests which we will use with a PUT request
      ClientSession session=null;
      Operation op=null;
      OutputStream out=null;

      //open a client connection against any irserver using the obex protocoll

      //url = bt_url;
      //url = RMSOption.BT_DEVICE_URLS[0];
      //String nu="btgoep";
      //int ind=url.indexOf(':');
      //nu+=url.substring(ind);
//
//      session=(ClientSession) Connector.open(url);
//      // Creating the headerset
//      //head=session.connect(null);
//      head=session.createHeaderSet();
//      //HeaderSet hs=session.createHeaderSet();

      // now let's send the connect operation
//      session.connect(head);

      session=(ClientSession) Connector.open(url);
      try {
        // Creating the headerset
        head=session.connect(null);



        progressResponse.setProgress((byte) -1, LangHolder.getString(Lang.send));

        ByteArrayOutputStream baos=new ByteArrayOutputStream(20000);
        DataOutputStream dos=new DataOutputStream(baos);
        if (RouteSend.EXPORTFORMAT==MapRouteLoader.FORMATOZI){
          mR.save2OziStream(dos,progressResponse);
        } else if (RouteSend.EXPORTFORMAT==MapRouteLoader.FORMATKML){
          mR.save2KML(dos,progressResponse);
        } else if (RouteSend.EXPORTFORMAT==MapRouteLoader.FORMATGPX){
          mR.save2GPX(dos,progressResponse);
        }
        byte[] messageBytes=baos.toByteArray();
        dos=null;
        baos=null;

        // Include the length header, telling the server the length of the message
        head.setHeader(head.LENGTH, new Long(messageBytes.length));
        // Include the name header, telling the server what filename the message should be saved as.
        head.setHeader(HeaderSet.NAME, FileTrackSend.sendFilename(mR,false));
        // Check that everything is OK
        int responseCode=head.getResponseCode();

        if (responseCode!=ResponseCodes.OBEX_HTTP_OK){

          MapCanvas.setCurrent(new Alert("Error: "+responseCode), backDisp);// gui.showAlert("Failure: Bad response code");
         closeIR(session, out, op);

           /* if (session!=null){
              try {
                try {
                  session.disconnect(null);
                } finally {
                  session.close();
                }
              } catch (Throwable ioe) {
              }
              session=null;
            }
*/
          return;
        }

        // Initiate the PUT request
        op=session.put(head);
        // Open the output stream
        out=op.openOutputStream();
        progressResponse.setProgress((byte) -1, LangHolder.getString(Lang.sending));
        // Send the message to the receiver
        out.write(messageBytes);
        out.flush();
        // End the transaction
        MapCanvas.setCurrent(new Alert(LangHolder.getString(Lang.sent)), backDisp);
        Thread.sleep(500);
        op.getResponseCode();
      } finally {
         closeIR(session, out, op);
      }
    } catch (Throwable e) {
      //status= "Failure:"+e.toString();
      MapCanvas.showmsgmodal(LangHolder.getString(Lang.error), e.toString(), AlertType.ERROR, backDisp);
    }


  }

  public void run() {

    if (sendType==SENDTYPE_IR){
      sendWithIR();
    } else if (sendType==SENDTYPE_BLUE){
      sendWithBlue();
    }
    progressResponse=null;

  }

  private void sendWithBlue() {
    progressResponse.setProgress((byte) -1, LangHolder.getString(Lang.connecting));
    try {
      //private String bt_url = "btgoep://0050C000321B:12";
      //The ClientSession interface provides methods for OBEX requests which we will use with a PUT request
      ClientSession session=null;
      Operation op=null;
      OutputStream out=null;

      //url = bt_url;
      //url = RMSOption.BT_DEVICE_URLS[0];
      //String nu="btgoep";
      //int ind=url.indexOf(':');
      //nu+=url.substring(ind);

      //url= RMSOption.BT_DEVICE_URLS_SEND[0];
      session=(ClientSession) Connector.open(url);
      // connection obtained
      try {
        // now, let's create a session and a headerset objects
        //ClientSession cs = (ClientSession)connection;
        HeaderSet hs=session.createHeaderSet();

        // now let's send the connect operation
        session.connect(hs);

        ByteArrayOutputStream baos=new ByteArrayOutputStream(20000);
        DataOutputStream dos=new DataOutputStream(baos);
        if (RouteSend.EXPORTFORMAT==MapRouteLoader.FORMATOZI){
          mR.save2OziStream(dos,progressResponse);
        } else if (RouteSend.EXPORTFORMAT==MapRouteLoader.FORMATKML){
          mR.save2KML(dos,progressResponse);
        } else if (RouteSend.EXPORTFORMAT==MapRouteLoader.FORMATGPX){
          mR.save2GPX(dos,progressResponse);
        }
        byte[] messageBytes=baos.toByteArray();
        dos=null;
        baos=null;

        hs.setHeader(HeaderSet.NAME, FileTrackSend.sendFilename(mR,false));
        hs.setHeader(HeaderSet.TYPE, "text/plain");
        hs.setHeader(HeaderSet.LENGTH, new Long(messageBytes.length));

        op=session.put(hs);

        out=op.openOutputStream();
        progressResponse.setProgress((byte) -1, LangHolder.getString(Lang.sending));
        out.write(messageBytes);
        // file push complete

        out.flush();
        MapCanvas.setCurrent(new Alert(LangHolder.getString(Lang.sent)), backDisp);
        Thread.sleep(500);
        op.getResponseCode();
      } finally {
           closeIR(session, out, op);
        
      }
    } catch (Throwable e) {
      //status= "Failure:"+e.toString();
      MapCanvas.showmsgmodal(LangHolder.getString(Lang.error), e.toString(), AlertType.ERROR, backDisp);
    }


  }
  ProgressResponse progressResponse;

  public void setProgressResponse(ProgressResponse progressResponse) {
    this.progressResponse=progressResponse;
  }

  public boolean stopIt() {
              return true;

  }
}


