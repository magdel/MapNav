package misc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.lcdui.Image;

public final class Util {

    /** 
     * Writes string to Output Stream in utf format
     */
    public static final void writeStr2OS(OutputStream os, String s) throws IOException {
        os.write(Util.stringToByteArray(s, true));
    }

    /**
     * Writes string to Output Stream in ASCII format
     */
    public static final void writeStr2OSAscii(OutputStream os, String s) throws IOException {
        for (int i=0; i<s.length(); i++) {
            os.write((0xff)&s.charAt(i));
        }
    }

    public final static int hex2int(char c) {
        if (c>='0'&&c<='9'){
            return c-48;
        }
        if (c>='A'&&c<='F'){
            return (c-65)+10;
        }
        if (c>='a'&&c<='f'){
            return (c-97)+10;
        } else {
            return 0;
        }
    }

    public static final Image readImageFile(String filename) {
        Image res=null;
        try {
            StreamConnection sc=(StreamConnection) Connector.open("file:///"+filename, Connector.READ);
            InputStream is;
            try {
                is=sc.openInputStream();
                try {
                    res=Image.createImage(is);
                } finally {
                    is.close();
                }
            } finally {
                sc.close();
            }
        } catch (Throwable t) {
        }
        return res;
    }

    public static final byte[] readFile(String filename) throws IOException {
        StreamConnection sc=(StreamConnection) Connector.open("file:///"+filename, Connector.READ);
        byte[] b=null;
        InputStream is;
        try {
            is=sc.openInputStream();
            try {
                b=readStream(is);
            } finally {
                is.close();
            }
        } finally {
            sc.close();
        }
        return b;
    }

    public static final byte[] readStream(InputStream is) throws IOException {
        ByteArrayOutputStream baos=new ByteArrayOutputStream(30000);
        int b;
        do {
            b=is.read();
            if (b<0){
                break;
            }
            baos.write(b);
        } while (true);
        return baos.toByteArray();
    }

    static public DataInputStream getDataInputStream(byte[] array, int offset) {
        return new DataInputStream(new ByteArrayInputStream(array, offset, array.length-offset));
    }

    static public DataInputStream getDataInputStream(byte[] array) {
        return getDataInputStream(array, 0);
    }

    static public InputStream getInputStream(byte[] array) {
        return new ByteArrayInputStream(array);
    }

    // Puts the specified word (val) into the buffer (buf) at position off using the specified byte ordering (bigEndian)

    public static void putWord(byte[] buf, int off, int val, boolean bigEndian) {
        if (bigEndian){
            buf[off]=(byte) ((val>>8)&0x000000FF);
            buf[++off]=(byte) ((val)&0x000000FF);
        } else // Little endian
        {
            buf[off]=(byte) ((val)&0x000000FF);
            buf[++off]=(byte) ((val>>8)&0x000000FF);
        }
    }
    // Puts the specified word (val) into the buffer (buf) at position off using big endian byte ordering

    public static void putWord(byte[] buf, int off, int val) {
        Util.putWord(buf, off, val, true);
    }

    // Extracts a string from the buffer (buf) starting at position off, ending at position off+len

    public static String byteArrayToString(byte[] buf, int off, int len, boolean utf8) {

        // Length check
        if (buf.length<off+len){
            return (null);
        }

        // Remove \0's at the end
        while ((len>0)&&(buf[off+len-1]==0x00)) {
            len--;
        }

        // Read string in UTF-8 format
        if (utf8){
            try {
                byte[] buf2=new byte[len+2];
                Util.putWord(buf2, 0, len);
                System.arraycopy(buf, off, buf2, 2, len);
                ByteArrayInputStream bais=new ByteArrayInputStream(buf2);
                DataInputStream dis=new DataInputStream(bais);
                return (dis.readUTF());
            } catch (Exception e) {
                // do nothing
            }
        }

        // CP1251 or default character encoding?
        //if (Options.getBoolean(Options.OPTION_CP1251_HACK))
        if (true){
            return (byteArray1251ToString(buf, off, len));
        } else {
            return (new String(buf, off, len));
        }

    }
//	// Extracts a string from the buffer (buf) starting at position off, ending at position off+len
//	public static String byteArrayToString1251(byte[] buf, int off, int len)
//	{
//		return (Util.byteArrayToString(buf, off, len, false));
//	}
    // Converts the specified buffer (buf) to a string

    public static String byteArrayUTFToString(byte[] buf) {
        return Util.byteArrayToString(buf, 0, buf.length, true);
    }

    public static String byteArrayToString(byte[] buf, boolean utf8) {
        return (Util.byteArrayToString(buf, 0, buf.length, utf8));
    }


    public static byte[] stringLatinToByteArray(String val) {
        try {
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            DataOutputStream dos=new DataOutputStream(baos);
            for (int i=0; i<val.length(); i++) {
                dos.writeByte((byte) val.charAt(i));
            }
            return baos.toByteArray();
        } catch (Exception e) {
            // Do nothing
        }
        return null;
    }

    /**
     *  Converts the specified string (val) to a byte array
     */
    public static byte[] stringToByteArray(String val, boolean utf8) {
        // Write string in UTF-8 format
        if (utf8){
            try {
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                DataOutputStream dos=new DataOutputStream(baos);
                dos.writeUTF(val);
                byte[] raw=baos.toByteArray();
                dos=null;
                baos=null;
                byte[] result=new byte[raw.length-2];
                System.arraycopy(raw, 2, result, 0, raw.length-2);
                return result;
            } catch (Exception e) {
                // Do nothing
            }
        }

//		// CP1251 or default character encoding?
//		//if (Options.getBoolean(Options.OPTION_CP1251_HACK))
//		if (true)
//		{
        return (stringToByteArray1251(val));
//		}
//		else
//		{
//			return (val.getBytes());
//		}

    }

    // Restores CRLF sequense from LF
    public static byte[] restoreCrLfToByteArray(String val, boolean utf) {
        return stringToByteArray(restoreCrLf(val), utf);
    }
    // Restores CRLF sequense from LF

    public static String restoreCrLf(String val) {
        StringBuffer result=new StringBuffer();
        int size=val.length();
        for (int i=0; i<size; i++) {
            char chr=val.charAt(i);
            if (chr=='\r'){
                continue;
            }
            if (chr=='\n'){
                result.append("\r\n");
            } else {
                result.append(chr);
            }
        }
        return result.toString();
    }

    // Converts an Unicode string into CP1251 byte array

    public static byte[] stringToByteArray1251(String s) {
        byte byte_s[]=new byte[s.length()];//s.getBytes();
        for (int i=0; i<s.length(); i++) {
            char c=s.charAt(i);
            switch (c) {
                case 1025:
                    byte_s[i]=-88;
                    break;
                case 1105:
                    byte_s[i]=-72;
                    break;

                /* Ukrainian CP1251 chars section */
                case 1168:
                    byte_s[i]=-91;
                    break;
                case 1028:
                    byte_s[i]=-86;
                    break;
                case 1031:
                    byte_s[i]=-81;
                    break;
                case 1030:
                    byte_s[i]=-78;
                    break;
                case 1110:
                    byte_s[i]=-77;
                    break;
                case 1169:
                    byte_s[i]=-76;
                    break;
                case 1108:
                    byte_s[i]=-70;
                    break;
                case 1111:
                    byte_s[i]=-65;
                    break;
                /* end of section */

                default:
                    char c1=c;
                    if (c1>='\u0410'&&c1<='\u044F'){
                        byte_s[i]=(byte) ((c1-1040)+192);
                    } else if (c1<127){
                        byte_s[i]=(byte) c1;
                    }
                    break;
            }
        }
        return byte_s;
    }
    // Converts an CP1251 byte array into an Unicode string

    public static String byteArray1251ToString(byte abyte0[], int i, int j) {
        String s=new String(abyte0, i, j);
        StringBuffer stringbuffer=new StringBuffer(j);
        for (int k=0; k<j; k++) {
            int l=abyte0[k+i]&0xff;
            switch (l) {
                case 168:
                    stringbuffer.append('\u0401');
                    break;
                case 184:
                    stringbuffer.append('\u0451');
                    break;

                /* Ukrainian CP1251 chars section */
                case 165:
                    stringbuffer.append('\u0490');
                    break;
                case 170:
                    stringbuffer.append('\u0404');
                    break;
                case 175:
                    stringbuffer.append('\u0407');
                    break;
                case 178:
                    stringbuffer.append('\u0406');
                    break;
                case 179:
                    stringbuffer.append('\u0456');
                    break;
                case 180:
                    stringbuffer.append('\u0491');
                    break;
                case 186:
                    stringbuffer.append('\u0454');
                    break;
                case 191:
                    stringbuffer.append('\u0457');
                    break;
                /* end of section */

                default:
                    if (l>=192&&l<=255){
                        stringbuffer.append((char) ((1040+l)-192));
                    } else {
                        stringbuffer.append(s.charAt(k));
                    }
                    break;
            }
        }
        return stringbuffer.toString();
    }


    static final char[] charTab=
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

    public static String encodeBase64(String data) {
        return encodeBase64(Util.stringToByteArray(data, true));
    }

    public static String encodeBase64(byte[] data) {
        return encodeBase64(data, 0, data.length, null).toString();
    }

    /** Encodes the part of the given byte array denoted by start and
    len to the Base64 format.  The encoded data is appended to the
    given StringBuffer. If no StringBuffer is given, a new one is
    created automatically. The StringBuffer is the return value of
    this method. */
    public static StringBuffer encodeBase64(
      byte[] data,
      int start,
      int len,
      StringBuffer buf) {

        if (buf==null){
            buf=new StringBuffer(data.length*3/2);
        }
        int end=len-3;
        int i=start;
        int n=0;

        while (i<=end) {
            int d=
              ((((int) data[i])&0x0ff)<<16)|((((int) data[i+1])&0x0ff)<<8)|(((int) data[i+2])&0x0ff);

            buf.append(charTab[(d>>18)&63]);
            buf.append(charTab[(d>>12)&63]);
            buf.append(charTab[(d>>6)&63]);
            buf.append(charTab[d&63]);

            i+=3;

            if (n++>=14){
                n=0;
                buf.append("\r\n");
            }
        }

        if (i==start+len-2){
            int d=
              ((((int) data[i])&0x0ff)<<16)|((((int) data[i+1])&255)<<8);

            buf.append(charTab[(d>>18)&63]);
            buf.append(charTab[(d>>12)&63]);
            buf.append(charTab[(d>>6)&63]);
            buf.append("=");
        } else if (i==start+len-1){
            int d=(((int) data[i])&0x0ff)<<16;

            buf.append(charTab[(d>>18)&63]);
            buf.append(charTab[(d>>12)&63]);
            buf.append("==");
        }

        return buf;
    }

    private static int decode(char c) {
        if (c>='A'&&c<='Z'){
            return ((int) c)-65;
        } else if (c>='a'&&c<='z'){
            return ((int) c)-97+26;
        } else if (c>='0'&&c<='9'){
            return ((int) c)-48+26+26;
        } else {
            switch (c) {
                case '+':
                    return 62;
                case '/':
                    return 63;
                case '=':
                    return 0;
                default:
                    throw new RuntimeException(
                      "unexpected code: "+c);
            }
        }
    }

    /** Decodes the given Base64 encoded String to a new byte array.
    The byte array holding the decoded data is returned. */
    public static byte[] decodeBase64(String s) {

        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        try {
            decodeBase64(s, bos);
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return bos.toByteArray();
    }

    public static void decodeBase64(String s, OutputStream os)
      throws IOException {
        int i=0;

        int len=s.length();

        while (true) {
            while (i<len&&s.charAt(i)<=' ') {
                i++;
            }
            if (i==len){
                break;
            }
            int tri=
              (decode(s.charAt(i))<<18)+(decode(s.charAt(i+1))<<12)+(decode(s.charAt(i+2))<<6)+(decode(s.charAt(i+3)));

            os.write((tri>>16)&255);
            if (s.charAt(i+2)=='='){
                break;
            }
            os.write((tri>>8)&255);
            if (s.charAt(i+3)=='='){
                break;
            }
            os.write(tri&255);

            i+=4;
        }
    }
}
