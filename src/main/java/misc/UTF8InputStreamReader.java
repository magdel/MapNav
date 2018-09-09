/*
 * MNInputStreamReader.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package misc;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 *
 * @author RFK
 */
public class UTF8InputStreamReader extends Reader{
  InputStream is;
  /** Creates a new instance of MNInputStreamReader */
  public UTF8InputStreamReader(InputStream is) {
    this.is=is;
  }
  
  public int read(char[] cbuf, int off, int len) throws IOException {
    int b,b2,b3, r=0;
    while (len>0){
      b = is.read();
      if (b<0) break;
      if (b<128)
        cbuf[off]= (char)b;
      else if (b<224){
        b2 = is.read();
        cbuf[off]= (char)(((b& 0x1F) << 6) | (b2 & 0x3F));
      } else {
        b2 = is.read();
        b3 = is.read();
        cbuf[off]=  (char)(((b & 0x0F) << 12) | ((b2 & 0x3F) << 6) | (b3 & 0x3F));
      }
      r++;
      off++;
      len--;
    }
    return r;
  }
  
  public void close() throws IOException {
    is.close();
  }
  
}
