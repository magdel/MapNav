//******************************************************************************
// DirectGif89Frame.java
//******************************************************************************
package net.jmge.gif;

//import java.awt.Image;
//import java.awt.image.PixelGrabber;
import RPMap.MapUtil;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.Image;

//==============================================================================
/** Instances of this Gif89Frame subclass are constructed from RGB image info,
 *  either in the form of an Image object or a pixel array.
 *  <p>
 *  There is an important restriction to note.  It is only permissible to add
 *  DirectGif89Frame objects to a Gif89Encoder constructed without an explicit
 *  color map.  The GIF color table will be automatically generated from pixel
 *  information.
 *
 * @version 0.90 beta (15-Jul-2000)
 * @author J. M. G. Elliott (tep@jmge.net)
 * @see Gif89Encoder
 * @see Gif89Frame
 * @see IndexGif89Frame
 */
public class DirectGif89Frame extends Gif89Frame {
  
  private int[] argbPixels;
  
  //----------------------------------------------------------------------------
  /** Construct an DirectGif89Frame from a Java image.
   *
   * @param img
   *   A java.awt.Image object that supports pixel-grabbing.
   * @exception IOException
   *   If the image is unencodable due to failure of pixel-grabbing.
   */
  public DirectGif89Frame(Image img, boolean reducePal) throws IOException {
    //PixelGrabber pg = new PixelGrabber(img, 0, 0, -1, -1, true);
    theWidth = img.getWidth();
    theHeight = img.getHeight();
    argbPixels = new int[theWidth*theHeight];
    ciPixels = new byte[argbPixels.length];
    img.getRGB(argbPixels,0,theWidth,0,0,theWidth,theHeight);
    if (reducePal) filterSysColors(argbPixels);
  }
  
//  public void filter256Colors(int[] colors){
//    
//    int[] most256colors_c = new  int[150];
//    int[] most256colors_cc = new  int[150];
//    int c,cc,mc=0, mincc=1000000;
//    for (int start_ind=0;start_ind<colors.length;start_ind++) {
//      c=colors[start_ind];cc=1;
//      if((c&0xFF000000)!=0){
//        colors[start_ind]=colors[start_ind]&0x00FFFFFF;
//        for (int i=start_ind+1;i<colors.length;i++){
//          if (((colors[i]&0xFF000000)!=0)&&(c==colors[i])){
//            cc++;
//            colors[i]=colors[i]&0x00FFFFFF;
//          }
//        }
//        if (mc<most256colors_c.length){
//          most256colors_c[mc]=c;
//          most256colors_cc[mc]=cc;
//          mc++;
//          if (mincc>cc) mincc=cc;
//        } else
//          if (cc>mincc) {
//          int less=cc,less_i=-1;
//          for (int i=0;i<most256colors_c.length;i++){
//            if (less>most256colors_cc[i]){
//              less=most256colors_cc[i];
//              less_i=i;
//              mincc=less;
//            }
//          }
//          if (less_i>=0){
//            most256colors_c[less_i]=c;
//            most256colors_cc[less_i]=cc;
//          }
//          }
//      }
//    }
//    for (int i=0;i<colors.length;i++)
//      colors[i]=colors[i]|0xFF000000;
//    //make all close to this allowed range
//    int cd;
//    if (mc==most256colors_c.length) {
//      for (int i=0;i<colors.length;i++){
//        c=colors[i];
//        int less=distColors(c,most256colors_c[0]),less_i=0;
//        for (int mi=1;mi<most256colors_c.length;mi++){
//          cd=distColors(c,most256colors_c[mi]);
//          if (cd<less){
//            less=cd;less_i=mi;
//          }
//        }
//        colors[i]=most256colors_c[less_i];
//      }
//    }
//  }
  
  public void filterSysColors(int[] colors){
    InputStream is = this.getClass().getResourceAsStream("/mp.dat");
    
    int[] pal = new int[256];
    try{
      int r,g,b;
      for (int i=0;i<pal.length;i++){
        r=is.read();
        g=is.read();
        b=is.read();
        is.read();//skip flag
        pal[i]=0xFF000000|(r<<16)|(g<<8)|b;
      }
    } catch(Throwable t){}
    
    //make all close to this allowed range
    int cd,c,c2;//EFF1EE  16 14 17
    int less,less_i;
    for (int i=colors.length-1;i>=0;i--){
      c=colors[i];
      c2=pal[pal.length-1];
      less=Math.abs(((c&0xFF0000)>>16)-((c2&0xFF0000)>>16))+Math.abs(((c&0xFF00)>>8)-((c2&0xFF00)>>8))+Math.abs((c&0xFF)-(c2&0xFF));
      less_i=pal.length-1;
      for (int mi=pal.length-2;mi>=0;mi--){
        c2=pal[mi];
        cd=Math.abs(((c&0xFF0000)>>16)-((c2&0xFF0000)>>16))+Math.abs(((c&0xFF00)>>8)-((c2&0xFF00)>>8))+Math.abs((c&0xFF)-(c2&0xFF));
        if (cd<less){
          less=cd;less_i=mi;
        }
      }
      colors[i]=pal[less_i];
    }
  }
  
  public static int distColors(int c1, int c2){
    int r1=c1&0xFF0000;
    int r2=c2&0xFF0000;
    int b1=c1&0x0000FF;
    int b2=c2&0x0000FF;
    int g1=c1&0x00FF00;
    int g2=c2&0x00FF00;
    // return Math.abs(c1&0xFF0000-(c2&0xFF0000)+Math.abs(b1-b2)+Math.abs(g1-g2);
    return Math.abs(r1-r2)+Math.abs(b1-b2)+Math.abs(g1-g2);
  }
  //----------------------------------------------------------------------------
  /** Construct an DirectGif89Frame from ARGB pixel data.
   *
   * @param width
   *   Width of the bitmap.
   * @param height
   *   Height of the bitmap.
   * @param argb_pixels
   *   Array containing at least width*height pixels in the format returned by
   *   java.awt.Color.getRGB().
   */
  public DirectGif89Frame(int width, int height, int argb_pixels[]) {
    theWidth = width;
    theHeight = height;
    argbPixels = new int[theWidth * theHeight];
    System.arraycopy(argb_pixels, 0, argbPixels, 0, argbPixels.length);
    ciPixels = new byte[argbPixels.length];
  }
  
  //----------------------------------------------------------------------------
  Object getPixelSource() { return argbPixels; }
}