/*
 * MapUtil.java
 *
 * Created on 4 Январь 2007 г., 22:02
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package RPMap;

import gpspack.GPSReader;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
//#debug
//# import misc.DebugLog;
import lang.Lang;
import lang.LangHolder;

/**
 *
 * @author julia
 */
public final class MapUtil {

    public static Font LARGEFONT=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_LARGE);
    public static Font MEDIUMFONT=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
    public static Font SMALLFONT=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
    public static Font LARGEFONTB=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE);
    public static Font MEDIUMFONTB=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
    public static Font SMALLFONTB=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL);
    final public static int blockSize=256;
    final static public double LNdiv2=-0.69314718055994530941723212145818;
    final static public double SQRT3=1.7320508075688772935274463415059;
    final static public double PI=3.1415926535897932384626433832795;
    final static public double PI2div3=2.0943951023931954923084289221863;
    final static public double PImul2=6.283185307179586476925286766559;
    final static public double PIdiv2=1.5707963267948966192313216916398;
    final static public double PIdiv3=1.0471975511965977461542144610932;
    final static public double PIdiv4=0.78539816339744830961566084581988;
    final static public double PIdiv6=0.52359877559829887307710723054658;
    final static public double PIdiv12=0.26179938779914943653855361527329;
    final static public double PImul2_rev=0.15915494309189533576888376337251;
    final static public double E=2.7182818284590452353602874713527;
    final static public double G2R=0.017453292519943295769236907684886;
    final static public double R2G=57.295779513082320876798154814105;
    static public byte[] CRLFb={13, 10};
    static public String CRLFs="\r\n";
    static public String DBSPs="--";
    static public String emptyString="";
    static public String spaceString=" ";
    //static public byte[] UVTM2 = {117, 118, 116, 109, 50, 46, 112, 108};
    static public byte[] CGI_BIN={99, 103, 105, 45, 98, 105, 110, 47};
    static public byte[] UV={117, 118, 46, 112, 108};
    static public byte[] UV_S={117, 118, 0x73, 46, 112, 108};
    static public byte[] UV_R={117, 118, 0x72, 46, 112, 108};
    static public String SH_0="0";
    static public String SH_90="90";
    static public String SH_180="180";
    static public String SH_270="270";
    static public String SH_45="45";
    static public String SH_135="135";
    static public String SH_225="225";
    static public String SH_315="315";
    static public String SH_NORTH="N";
    static public String SH_EAST="E";
    static public String SH_SOUTH="S";
    static public String SH_WEST="W";
    static public String SH_NORTHEAST="NE";
    static public String SH_SOUTHEAST="SE";
    static public String SH_SOUTHWEST="SW";
    static public String SH_NORTHWEST="NW";
    static public String homeSiteURL="http://mapnav.spb.ru/";
    static public String mapServ_Google="Google ";
    static public String mapServ_VE="VE ";
    static public String mapServ_Yahoo="Yahoo ";
    static public String mapServ_Ask="Ask ";
    static public String mapServ_OpenStreet="OpenStreet ";
    static public String mapServ_Gurtam="MapQuestOSM ";
    static public String mapServ_Online="Online ";
    static public String version="?";
    static public String SH_MAP="M";
    static public String SH_MENU="*";
    static public String S_PDOP="PDOP ";
    static public String S_HDOP="HDOP ";
    static public String S_VDOP="VDOP ";
    static public String S_UTC = " UTC";
    //static public String S_T="T";
    static public String S_OK="OK";
    static public String CGI_BINs=misc.Util.byteArrayToString(CGI_BIN, true);
    //static public String UVTM2s = misc.Util.byteArrayToString(UVTM2, true);
    static public String UVs=misc.Util.byteArrayToString(UV, true);
    static public String UV_Ss=misc.Util.byteArrayToString(UV_S, true);
    static public String UV_Rs=misc.Util.byteArrayToString(UV_R, true);
    public static String MAP_MNO="/map.mno";
    public static String BTSPP = "btspp";


//  final static public int getTileCount(int level) {
//    return (level < 2) ? 1 : 2 << (level - 2);
//  }
//  final static public int getTileCount(int level) {
//    int res;
//    if (level<2) res=1;else res=2<<(level-2);
//    return res;
//  }
    final static int getBitmapSize(int level) {
        //return blockSize * getTileCount(level);
        return blockSize*((level<2)?1:2<<(level-2));
    }

    final static int getBitmapOrig(int level) {
        return (blockSize*((level<2)?1:2<<(level-2)))>>1;
    }

    final static double getPixelsPerLonDegree(int level) {
        return ((double) (blockSize*((level<2)?1:2<<(level-2))))*0.0027777777777777777777777777777778d;
    }

    final static double getPixelsPerLonRadian(int level) {
        return ((double) (blockSize*((level<2)?1:2<<(level-2))))*(0.15915494309189533576888376337251d);
    }
    // final static double grad2rad(double g) {return g*G2R;}
//  final static double abs(double a) {
//    if (a>=0) return a;
//    return -a;
//  }
//  final static int abs(int a) {
//    if (a>=0) return a;
//    return -a;
//  }
//  final static public double sqr(double a) {
//    return a * a;
//  }

    final static public double getLon(int X, int level) {
        return (double) (X-getBitmapOrig(level))/getPixelsPerLonDegree(level);
    }

    final static public double getLat(int Y, int level) {
        return (atan(exp((double) (Y-getBitmapOrig(level))/(-getPixelsPerLonRadian(level))))-PIdiv4)*114.59155902616464175359630962821d;

        //double z = (double)(Y-getBitmapOrig(level))/(-getPixelsPerLonRadian(level));
        //return (atan(exp(z))-PIdiv4)*114.59155902616464175359630962821d;

//return (2*atan(exp(z))-PIdiv2)*57.295779513082320876798154814105;
    }

    final static public String getGLBlockName(int NumX, int NumY, int level) {
        int d;
        StringBuffer res=new StringBuffer(20);
        if (level<2){
            d=1;
        } else {
            d=2<<(level-2);
        }
        if ((NumX<0)||(d-1<NumX)){
            NumX=NumX-(NumX/d)*d;
            if (NumX<0){
                NumX=NumX+d;
            }
        }
        res.append('t');
        for (int i=2; i<=level; i++) {
            d=d>>1;
            if (NumY<d){
                if (NumX<d){
                    res.append('q');
                } else {
                    res.append('r');
                    NumX=NumX-d;
                }
            } else {
                if (NumX<d){
                    res.append('t');
                } else {
                    res.append('s');
                    NumX=NumX-d;
                }
                NumY=NumY-d;
            }
        }
        return res.toString();
    }

    final static public String getYHBlockName(int NumX, int NumY, int level) {
        level=18-level;
        String s="&x="+NumX+"&y="+(((1<<17-level)>>1)-1-NumY)+"&z="+(level+1);
        return s;
    }

    final static public String getYHBlockNameURL(int NumX, int NumY, int level, byte tileImageType) {
        String s=tileImageType==MapTile.SHOW_MAP?"http://png.maps.yimg.com/png?v=3.1.0":"http://aerial.maps.yimg.com/tile?v=1.4&t=a";
        s=s+getYHBlockName(NumX, NumY, level);
        return s;
    }

//    final static public String getGMFileCacheURL(int NumX, int NumY, int level, int osmtype) {
//        StringBuffer sb=new StringBuffer(40);
//        sb.append("http://tile.openstreetmap.org/");
//
//        sb.append(level).append('/').append(NumY).append('/').append(NumX).append(".png");
//        return sb.toString();
//    }


    public static final int OSMTYPE_OSM = 1;
    public static final int OSMTYPE_OSMRENDER = 2;
    public static final int OSMTYPE_MAPQUEST = 3;
    final static public String getOSBlockNameURL(int NumX, int NumY, int level, int osmtype) {
        StringBuffer sb=new StringBuffer(40);
        //http://tile.openstreetmap.org/Tiles/tile.php/13/4752/2344.png osmrender
        //http://a.tah.openstreetmap.org/Tiles/tile.php/13/4752/2345.png

        //http://tile.openstreetmap.org/13/4753/2344.png
        if (osmtype==OSMTYPE_MAPQUEST){
            sb.append("http://otile1.mqcdn.com/tiles/1.0.0/osm/");
        } else if (osmtype==OSMTYPE_OSMRENDER){
            sb.append(RMSOption.getStringOpt(RMSOption.SO_OSMURL));
        } else {
            sb.append("http://tile.openstreetmap.org/");
        }
        level=18-level;
        sb.append(17-level).append('/').append(NumX).append('/').append(NumY).append(".png");
        return sb.toString();
    }

    final static public String getAskBlockNameURL(int NumX, int NumY, int level, byte tileImageType) {
        StringBuffer sb=new StringBuffer(40);
        level=18-level;
        sb.append(level<=6?"http://mapcache":"http://mapstatic").append(NumX+level&4).append(tileImageType==MapTile.SHOW_SUR?".ask.com/sat":tileImageType==MapTile.SHOW_MAP?".ask.com/map":".ask.com/mapt").append('/').append(level+2).append('/').append(NumX-((1<<17-level)>>1)).append('/').append(NumY-((1<<17-level)>>1)).append("?partner=&tc=20");
        //sb.append(bf.m_daString[i]);

        return sb.toString();
    }
//                    stringbuffer.append("http://tile.openstreetmap.org/");
//                    stringbuffer.append(17 - super.m_fI);
//                    stringbuffer.append('/');
//                    stringbuffer.append(super.m_dI);
//                    stringbuffer.append('/');
//                    stringbuffer.append(super.m_eI);
//                    stringbuffer.append(".png");
//  public final static String a(int NumX, int NumY, int level, int j1) {
//    int f =0;
//    String s = null;
//    boolean flag = false;
//    if(j1 % 2 == 1)
//      flag = true;
//    switch(f) {
//      default:
//        break;
//
//      case 5: // '\005'
//        int k1 = 1 << 17 - level;
//        int l1 = NumX - k1 / 2;
//        int i2 = NumY - k1 / 2;
//        String s1 = "map/";
//        if(flag)
//          s1 = "sat/";
//        s = "http://tiles.ask.com/" + s1 + (level + 2) + "/" + l1 + "/" + i2;
//        break;
//
//    }
//    return s;
//  }
//  private String _aIZString(int i, boolean flag)
//    {
//        int ai1[] = new int[0];
//        String s = null;
//        if(flag && ba.m_mbq != null && (ai1 = ba.m_mbq._aIIaI(super.m_dI, super.m_eI, super.m_fI)).length > 0 && ai1[0] < 8 && ai1[0] != 1)
//            s = bq._aIIIString(super.m_dI, super.m_eI, super.m_fI, ai1[0]);
//        if(s == null)
//        {
//            StringBuffer stringbuffer = new StringBuffer();
//            if(i >= ba.m_aI)
//            {
//                String s1 = ba.m_haString[i - ba.m_aI];
//                stringbuffer.append(s1);
//                stringbuffer.append(s1.indexOf('?') >= 0 ? '&' : '?');
//                if(s1.indexOf("kh") >= 0)
//                {
//                    stringbuffer.append("t=t");
//                    for(int k = 17 - super.m_fI - 1; k >= 0; k--)
//                        stringbuffer.append(m_haC[((super.m_eI >> k & 1) << 1) + (super.m_dI >> k & 1)]);
//
//                } else
//                {
//                    stringbuffer.append("x=");
//                    stringbuffer.append(super.m_dI);
//                    stringbuffer.append("&y=");
//                    stringbuffer.append(super.m_eI);
//                    stringbuffer.append("&zoom=");
//                    stringbuffer.append(super.m_fI);
//                }
//            } else
//            {
//                switch(i)
//                {
//                default:
//                    break;
//
//                case 9: // '\t'
//                case 10: // '\n'
//                case 11: // '\013'
//                case 12: // '\f'
//                    stringbuffer.append("N/A/");
//                    stringbuffer.append(super.m_dI);
//                    stringbuffer.append('/');
//                    stringbuffer.append(super.m_eI);
//                    stringbuffer.append('/');
//                    stringbuffer.append(super.m_fI);
//                    break;
//
//                case 3: // '\003'
//                case 4: // '\004'
//                case 5: // '\005'
//                    stringbuffer.append("http://");
//                    char c = i != 3 ? ((char) (i != 4 ? 'h' : 'a')) : 'r';
//                    stringbuffer.append(c);
//                    stringbuffer.append(((super.m_eI & 1) << 1) + (super.m_dI & 1));
//                    stringbuffer.append(".ortho.tiles.virtualearth.net/tiles/");
//                    stringbuffer.append(c);
//                    for(int l = 17 - super.m_fI - 1; l >= 0; l--)
//                        stringbuffer.append(((super.m_eI >> l & 1) << 1) + (super.m_dI >> l & 1));
//
//                    stringbuffer.append(i != 3 ? ".jpeg" : ".png");
//                    stringbuffer.append("?g=");
//                    stringbuffer.append(ba.m_eaString[i]);
//                    break;
//
//                case 2: // '\002'
//                    stringbuffer.append(_aIZString(1, false));
//                    stringbuffer.append('|');
//                    // fall through
//
//                case 0: // '\0'
//                case 1: // '\001'
//                    stringbuffer.append(i != 0 ? i != 2 ? "http://aerial.maps.yimg.com/tile?t=a" : "http://aerial.maps.yimg.com/png?t=h" : "http://png.maps.yimg.com/png?t=m");
//                    stringbuffer.append("&v=");
//                    stringbuffer.append(ba.m_eaString[i]);
//                    stringbuffer.append("&x=");
//                    stringbuffer.append(super.m_dI);
//                    stringbuffer.append("&y=");
//                    stringbuffer.append(((1 << 17 - super.m_fI) >> 1) - 1 - super.m_eI);
//                    stringbuffer.append("&z=");
//                    stringbuffer.append(super.m_fI + 1);
//                    break;
//
//                case 8: // '\b'
//                    stringbuffer.append(_aIZString(7, false));
//                    stringbuffer.append('|');
//                    // fall through
//
//                case 6: // '\006'
//                case 7: // '\007'
//                    stringbuffer.append(super.m_fI <= 6 ? "http://mapcache" : "http://mapstatic");
//                    stringbuffer.append(super.m_dI + super.m_eI & 4);
//                    stringbuffer.append(i != 6 ? i != 8 ? ".ask.com/sat" : ".ask.com/mapt" : ".ask.com/map");
//                    stringbuffer.append('/');
//                    stringbuffer.append(super.m_fI + 2);
//                    stringbuffer.append('/');
//                    stringbuffer.append(super.m_dI - ((1 << 17 - super.m_fI) >> 1));
//                    stringbuffer.append('/');
//                    stringbuffer.append(super.m_eI - ((1 << 17 - super.m_fI) >> 1));
//                    stringbuffer.append("?partner=");
//                    stringbuffer.append("&tc=");
//                    stringbuffer.append(ba.m_eaString[i]);
//                    break;
//
//                case 13: // '\r'
//                    stringbuffer.append("http://tile.openstreetmap.org/");
//                    stringbuffer.append(17 - super.m_fI);
//                    stringbuffer.append('/');
//                    stringbuffer.append(super.m_dI);
//                    stringbuffer.append('/');
//                    stringbuffer.append(super.m_eI);
//                    stringbuffer.append(".png");
//                    break;
//                }
//            }
//            s = stringbuffer.toString();
//        }
//        for(int j = 0; j < ai1.length; j++)
//        {
//            String s2;
//            if((s2 = '|' + bq._aIIIString(super.m_dI, super.m_eI, super.m_fI, ai1[j])).length() > 1)
//                s = s + s2;
//        }
//
//        return s;
//    }

    final static public String getVEBlockName(int NumX, int NumY, int level) {
        int d;
        StringBuffer res=new StringBuffer(20);
        if (level<2){
            d=1;
        } else {
            d=2<<(level-2);
        }
        if ((NumX<0)||(d-1<NumX)){
            NumX=NumX-(NumX/d)*d;
            if (NumX<0){
                NumX=NumX+d;
            }
        }
        for (int i=2; i<=level; i++) {
            d=d>>1;
            if (NumY<d){
                if (NumX<d){
                    res=res.append('0');
                } else {
                    res.append('1');
                    NumX=NumX-d;
                }
            } else {
                if (NumX<d){
                    res.append('2');
                } else {
                    res.append('3');
                    NumX=NumX-d;
                }
                NumY=NumY-d;
            }
        }
        return res.toString();
    }

    public static String getNetVEURL(int x, int y, int level) {
        String bn=getVEBlockName(x, y, level);
        return "http://r"+bn.charAt(0)+".ortho.tiles.virtualearth.net/tiles/r"+bn+".png?g=1";
    }

    public static String getMapVEURL(int x, int y, int level) {
        String bn=getVEBlockName(x, y, level);
        return "http://h"+bn.charAt(0)+".ortho.tiles.virtualearth.net/tiles/h"+bn+".jpg?g=1";
    }
    public static final byte ONLINE_TYPE_GOOGLE=0;
    public static final byte ONLINE_TYPE_OSM=1;
//  public static final byte ONLINE_TYPE_YA = 2;
//  public static final byte ONLINE_TYPE_MAIL = 3;

    public static String getOnlineURL(byte tileImageType, int numX, int numY, int level) {
        String bn=(tileImageType==MapTile.SHOW_SUR)?RMSOption.getStringOpt(RMSOption.SO_ONLINE_URL_SUR):RMSOption.getStringOpt(RMSOption.SO_ONLINE_URL_MAP);
        byte boi=(tileImageType==MapTile.SHOW_SUR)?RMSOption.BO_ONLINE_SUR_TYPE:RMSOption.BO_ONLINE_MAP_TYPE;
        switch (RMSOption.getByteOpt(boi)) {
            case ONLINE_TYPE_GOOGLE:
                //url=RMSOption.netGL_URL +String.valueOf(numX)+"&y="+String.valueOf(numY)+"&zoom="+String.valueOf(18-level) ;
                bn=bn+String.valueOf(numX)+"&y="+String.valueOf(numY)+"&zoom="+String.valueOf(18-level);
                break;
            case ONLINE_TYPE_OSM:
                bn=bn+(level-1)+'/'+numX+'/'+numY+".png";
                //bn=bn+numX+"&y="+numY+"&z="+(level-1);
                break;
        }
        return bn;
    }

    /*
    static public double cos(double a) {
    //        return Math.cos(a);
    double a2=a*a;
    double a4=a2*a2;
    double a8=a4*a4;
    return 1.-a2/2.+a4/24.-a4*a2/720.+a8/40320.-a8*a2/3628800.+a8*a4/479001600.-a8*a4*a2/87178291200.;
    }
     */
    //more precise then builtin function
/*  final static public double cos(double a) {
    return Math.cos(a);
    double a2=a*a;
    double a4=a2*a2;
    double a8=a4*a4;
    double a16=a8*a8;
    return 1.-a2*0.5+a4*0.041666666666666666666666666666667
    -a4*a2*0.0013888888888888888888888888888889
    +a8*2.4801587301587301587301587301587e-5
    -a8*a2*2.7557319223985890652557319223986e-7
    +a8*a4*2.0876756987868098979210090321201e-9
    -a8*a4*a2*1.1470745597729724713851697978682e-11
    +a16*4.7794773323873852974382074911175e-14
    -a16*a2*1.5619206968586226462216364350057e-16
    +a16*a4*4.1103176233121648584779906184361e-19
    -a16*a4*a2*8.8967913924505732867488974425025e-22;

    } */
    /*
    static public double sin(double a) {
    //        return Math.sin(a);
    double a2=a*a;
    double a3=a2*a;
    double a5=a3*a2;
    return a-a3/6.+a3*a2/120.-a5*a2/5040.+a5*a3*a/362880.-a5*a5*a/39916800.+a5*a5*a3/6227020800.;
    }
     */
    /*
    final static public double sin(double a) {
    return Math.sin(a);*/
    /*    double a2=a*a;
    double a3=a2*a;
    double a5=a3*a2;
    double a7=a5*a2;
    double a10=a5*a5;
    return a-a3*0.16666666666666666666666666666667
    +a5*0.0083333333333333333333333333333333
    -a7*0.0001984126984126984126984126984127
    +a7*a2*2.7557319223985890652557319223986e-6
    -a10*a*2.5052108385441718775052108385442e-8
    +a10*a3*1.6059043836821614599392377170155e-10
    -a10*a5*7.6471637318198164759011319857881e-13
    +a10*a7*2.8114572543455207631989455830103e-15
    -a10*a7*a2*8.2206352466243297169559812368723e-18;
    }*/
    final static private double _ln(double x) {
        if (!(x>0.)){
            return Double.NaN;
        }
        double f=0.0;
        int appendix=0;
        while (x>0.0&&x<=1.0) {
            x*=2.0;
            appendix++;
        }
        //x/=2.0;
        x*=0.5;
        appendix--;
        //double y1=;
        double y2=x+1.;
        double k=(x-1.)/y2;
        //double k=y;
        y2=k*k;
        for (long i=1; i<45; i+=2) {
            f+=k/i;
            k*=y2;
        }
        f*=2.0;
        for (int i=0; i<appendix; i++) {
            f+=LNdiv2;
        }
        return f;
    }

    final static public double ln(double x) {
        if (!(x>0.)){
            return Double.NaN;
        }
        if (x==1.0){
            return 0.0;
        }
        if (x>1.){
            x=1/x;
            return -_ln(x);
        }
        return _ln(x);
    }

    final static public double exp(double x) {
        if (x==0.){
            return 1.;
        }
        double f=1;
        double k;
        boolean isless=(x<0.);
        if (isless){
            x=-x;
        }
        k=x;
        //
        for (long i=2; i<45; i++) {
            f=f+k;
            k=k*x/i;
        }
        //
        if (isless){
            return 1./f;
        } else {
            return f;
        }
    }

    final static public double atan2(double y, double x) {

        if (y==0.&&x==0.){
            return 0.;
        }
        if (x>0.){
            return atan(y/x);
        }
        if (x<0.){
            if (y<0.){
                return -(Math.PI-atan(y/x));
            } else {
                return Math.PI-atan(-y/x);
            }
        }

        if (y<0.){
            return -MapUtil.PIdiv2;
        } else {
            return MapUtil.PIdiv2;
        }
    }
final static public double asin(double x) {
    return  atan(x/Math.sqrt(1 - x*x));
}
final static public double acos(double x) {
    return  2*atan(Math.sqrt((1-x)/(1+x)));
}
    final static public double atan(double x) {
        boolean signChange=false;
        boolean Invert=false;
        int sp=0;
        double x2, a;

        if (x<0.){
            x=-x;
            signChange=true;
        }

        if (x>1.){
            x=1/x;
            Invert=true;
        }

        while (x>PI/12) {
            sp++;
            a=x+SQRT3;
            a=1/a;
            x=x*SQRT3;
            x=x-1;
            x=x*a;
        }

        x2=x*x;
        a=x2+1.4087812;
        a=0.55913709/a;
        a=a+0.60310579;
        a=a-(x2*0.05160454);
        a=a*x;

        while (sp>0) {
            a=a+PIdiv6;
            sp--;
        }

        if (Invert){
            a=PIdiv2-a;
        }
        if (signChange){
            a=-a;
        }
        return a;
    }

    final static public int getXMap(double lon, int level) {
        //double X = getBitmapOrig(level) + lon * getPixelsPerLonDegree(level);
        return (int) (0.5+getBitmapOrig(level)+lon*getPixelsPerLonDegree(level));// X;
    }

    final static public int getYMap(double lat, int level) {
        double z=Math.sin(lat*G2R);
        //double Y = (double)getBitmapOrig(level) - 0.5 * ln((1.+z)/(1.-z)) * getPixelsPerLonRadian(level);
        //return (int) Y;
        return (int) (0.5+(double) getBitmapOrig(level)-0.5*ln((1.+z)/(1.-z))*getPixelsPerLonRadian(level));
    }

    final static public int getNumX(int xMap) {
//    int NumX =  (int)((double)xMap/ blockSize); //floor
        // int NumX =  (int)(xMap/ blockSize); //floor
        return xMap/blockSize;
    }

    final static public int getNumY(int yMap) {
        //int NumY =  (int)(yMap/ blockSize);
        return yMap/blockSize;
    }

    final static public String getGLBlockNameD(double lat, double lon, int level) {
        return getGLBlockName(getNumX(getXMap(lon, level)), getNumY(getYMap(lat, level)), level);
    }
    public static final byte CLATTYPE=1;
    public static final byte CLONTYPE=2;

    final static public String coordToString(double coord, byte lontype, byte coordType) {
        int icoord=(int) (coord);
        int icoordg, icoordm, icoords;
        double dcoord, dcoordm, dcoords;
        char a;

        if (lontype==CLATTYPE){
            if (coord>0){
                dcoord=coord-icoord;
                a='N';
            } else {
                dcoord=icoord-coord;
                a='S';
            }
        } else //if (lontype==CLONTYPE)
        {
            if (coord>0){
                dcoord=coord-icoord;
                a='E';
            } else {
                dcoord=icoord-coord;
                a='W';
            }
        }
        String s=null;
        if (coordType==RMSOption.COORDMINSECTYPE){
            icoordm=(int) (dcoord*60.);
            dcoords=((dcoord-(icoordm/60.))*3600.);
            dcoords=MapUtil.coordRound1(dcoords);
            icoordg=Math.abs(icoord);
            s=a+MapUtil.numStr(icoordg, 2)+((char) 0xb0)+numStr(icoordm, 2)+'\''+(dcoords)+"''";
        } else if (coordType==RMSOption.COORDMINMMMTYPE){
            icoordg=Math.abs(icoord);
            icoordm=(int) (dcoord*60.);
            icoords=(int) ((dcoord-(icoordm/60.))*60000.);

            s=a+MapUtil.numStr(icoordg, 2)+((char) 0xb0)+MapUtil.numStr(icoordm, 2)+'.'+MapUtil.numStr(icoords, 3);
        } else if (coordType==RMSOption.COORDGGGMMMMMMTYPE){
            icoordg=Math.abs(icoord);
            icoordm=(int) (dcoord*60.);
            icoords=(int) ((dcoord-(icoordm/60.))*600000.);
            if (lontype==CLATTYPE) {
                s=a+MapUtil.numStr(icoordg, 2)+MapUtil.numStr(icoordm, 2)+'.'+MapUtil.numStr(icoords, 4);
            } else {
                s=a+MapUtil.numStr(icoordg, 3)+MapUtil.numStr(icoordm, 2)+'.'+MapUtil.numStr(icoords, 4);
            }
        } else if (coordType==RMSOption.COORDGGGGGGTYPE){
            icoordm=((int) (dcoord*100000.));
            icoordg=Math.abs(icoord);
            s=a+MapUtil.numStr(icoordg, 2)+'.'+MapUtil.numStr(icoordm, 5)+((char) 0xb0);
        }

        return s;
    }

    final static public String coord2EditString(double coord, byte lontype, byte coordType) {
        int icoord=(int) (coord);
        int icoordg, icoordm, icoords;
        double dcoord, dcoordm, dcoords;
        char a;

        if (lontype==CLATTYPE){
            if (coord>0){
                dcoord=coord-icoord;
                a=' ';
            } else {
                dcoord=icoord-coord;
                a='-';
            }
        } else //if (lontype==CLONTYPE)
        {
            if (coord>0){
                dcoord=coord-icoord;
                a=' ';
            } else {
                dcoord=icoord-coord;
                a='-';
            }
        }
        String s=null;
        if (coordType==RMSOption.COORDMINSECTYPE){
            icoordm=(int) (dcoord*60.);
            icoords=(int) ((dcoord-(icoordm/60.))*3600.);
            dcoords=MapUtil.coordRound1((dcoord-(icoordm/60.))*3600.);
            icoordg=Math.abs(icoord);
            s=a+MapUtil.numStr(icoordg, 2)+' '+numStr(icoordm, 2)+' '+String.valueOf(dcoords);
//      s=a+MapUtil.make2(icoordg)+' '+ make2(icoordm)+' '+ make2(icoords);
        } else if (coordType==RMSOption.COORDMINMMMTYPE){
            icoordg=Math.abs(icoord);
            icoordm=(int) (dcoord*60.);
            icoords=(int) ((dcoord-(icoordm/60.))*60000.);

            s=a+MapUtil.numStr(icoordg, 2)+' '+MapUtil.numStr(icoordm, 2)+'.'+MapUtil.numStr(icoords, 3);
        } else if (coordType==RMSOption.COORDGGGGGGTYPE){
            icoordm=((int) (dcoord*100000.));
            icoordg=Math.abs(icoord);
            s=a+MapUtil.numStr(icoordg, 2)+'.'+MapUtil.numStr(icoordm, 5);
        }

        return s;
    }

    /**
     * Разбор координаты в указанном формате
     * RMSOption.COORDMINSECTYPE
     * RMSOption.COORDMINMMMTYPE
     * RMSOption.COORDGGGGGGTYPE
     *
     */
    public final static double parseCoord(String coordS, byte coordType) throws Exception {
        String[] ps=MapUtil.parseString(' '+coordS.trim(), ' ');
        if (coordType==RMSOption.COORDMINSECTYPE){
            if ((Double.parseDouble(ps[0])>=0)&&(ps[0].charAt(0)!='-')){
                return Double.parseDouble(ps[0])+Double.parseDouble(ps[1])/60.+Double.parseDouble(ps[2])/3600.;
            } else {
                return Double.parseDouble(ps[0])-Double.parseDouble(ps[1])/60.-Double.parseDouble(ps[2])/3600.;
            }
        } else if (coordType==RMSOption.COORDMINMMMTYPE){
            if ((Double.parseDouble(ps[0])>=0)&&(ps[0].charAt(0)!='-')){
                return Double.parseDouble(ps[0])+Double.parseDouble(ps[1])/60.;
            } else {
                return Double.parseDouble(ps[0])-Double.parseDouble(ps[1])/60.;
            }
        } else if (coordType==RMSOption.COORDGGGGGGTYPE){
            return Double.parseDouble(ps[0]);
        }
        return 0;
    }

    public static String numStr(long number, int digits) {
        StringBuffer s=new StringBuffer(digits);
        boolean rev=(number<0);
        if (rev){
            number=-number;
            digits--;
        }
        s.append(number);
        for (int i=s.length(); s.length()<digits; s.insert(0, '0'));

        if (rev){
            s.insert(0, '-');
        }
        return s.toString();
    }
//    public static String numStrCrop(int number, int digits) {
//    String s;
//    for (s=MapUtil.emptyString+number; s.length()<digits; s="0"+s);
//    if (s.length()>digits) {
//      s=s.substring(s.length()-digits);
//    }
//    return s;
//  }

//  final static public String make2(int i) {
//    if (i < 10) {
//      StringBuffer sb = new StringBuffer(3);
//      sb.append('0').append(i);
//      return sb.toString();
//    } else {
//      return String.valueOf(i);
//    }
//  }
//
//  final static public String make3(int i) {
//    if (i < 10) {
//      StringBuffer sb = new StringBuffer(4);
//      sb.append('0').append('0').append(i);
//      return sb.toString();
//    } else if (i < 100) {
//      StringBuffer sb = new StringBuffer(4);
//      sb.append('0').append(i);
//      return sb.toString();
//    } else {
//      return String.valueOf(i);
//    }
//  }
//
//  final static public String make4(int i) {
//    boolean rev = false;
//    if (i < 0) {
//      i = -i;
//      rev = true;
//    }
//    if (rev) {
//      if (i < 10) {
//        StringBuffer sb = new StringBuffer(4);
//        sb.append('-').append('0').append('0').append(i);
//        return sb.toString();
//      } else if (i < 100) {
//        StringBuffer sb = new StringBuffer(4);
//        sb.append('-').append(i);
//        return sb.toString();
//      } else //if (i<10) return '-'+'0'+'0'+String.valueOf(i);
//      //if (i<100) return '-'+'0'+String.valueOf(i);
//      {
//        return String.valueOf(i);
//      }
//    } else {
//      if (i < 10) {
//        StringBuffer sb = new StringBuffer(4);
//        sb.append('0').append('0').append('0').append(i);
//        return sb.toString();
//      } else //    if (i<10) return '0'+'0'+'0'+String.valueOf(i);
//      if (i < 100) {
//        StringBuffer sb = new StringBuffer(4);
//        sb.append('0').append('0').append(i);
//        return sb.toString();
//      } else //if (i<100) return '0'+'0'+String.valueOf(i);
//      if (i < 1000) {
//        StringBuffer sb = new StringBuffer(4);
//        sb.append('0').append(i);
//        return sb.toString();
//      } else //      if (i<1000) return '0'+String.valueOf(i);
//      {
//        return String.valueOf(i);
//      }
//    }
//  }
//
//  final static public String make5(int i) {
//    boolean rev = false;
//    if (i < 0) {
//      i = -i;
//      rev = true;
//    }
//    if (rev) {
//      if (i < 10) {
//        return "-000" + String.valueOf(i);
//      } else //if (i<100) return '-'+'0'+'0'+String.valueOf(i);
//      if (i < 100) {
//        StringBuffer sb = new StringBuffer(5);
//        sb.append('-').append('0').append('0');
//        sb.append(i);
//        return sb.toString();
//      } else if (i < 1000) {
//        StringBuffer sb = new StringBuffer(5);
//        sb.append('-').append('0').append(i);
//        return sb.toString();
//      } else //if (i<1000) return '-'+'0'+String.valueOf(i);
//      {
//        return String.valueOf(i);
//      }
//    } else {
//      if (i < 10) {
//        return "0000" + String.valueOf(i);
//      } else if (i < 100) {
//        StringBuffer sb = new StringBuffer(5);
//        sb.append('0').append('0').append('0').append(i);
//        return sb.toString();
//      } else //if (i<100) return '0'+'0'+'0'+String.valueOf(i);
//      if (i < 1000) {
//        StringBuffer sb = new StringBuffer(5);
//        sb.append('0').append('0').append(i);
//        return sb.toString();
//      } else //if (i<1000) return '0'+'0'+String.valueOf(i);
//      if (i < 10000) {
//        StringBuffer sb = new StringBuffer(5);
//        sb.append('0').append(i);
//        return sb.toString();
//      } else //if (i<10000) return '0'+String.valueOf(i);
//      {
//        return String.valueOf(i);
//      }
//    }
//  }
    final public static double distRound3(double res) {
        boolean sign=res<0;
        if (sign){
            res=-res;
        }
        if (res<0.001){
            return 0;
        }
        if (res<1){
            res=((int) (res*1000d))/1000.d;
        } else if (res<5){
            res=((int) (res*100d))/100.d;
        } else {
            res=((int) (res*10d))/10.d;
        }
        if (sign){
            return -res;
        } else {
            return res;
        }
    }

    final public static double distRound2(double res) {
        boolean sign=res<0;
        if (sign){
            res=-res;
        }
        if (res<0.001){
            res=0;
        }
        if (res<5){
            res=((int) (res*100d))/100.d;
        } else {
            res=((int) (res*10d))/10.d;
        }
        if (sign){
            return -res;
        } else {
            return res;
        }
    }

    final public static double coordRound1(double res) {
        if (res<0.1){
            return 0;
        }
        return ((int) (res*10d))/10.d;
        //else return ((int)res);
    }

    final public static double doubleRound1(double res) {
        boolean sign=res<0;
        if (sign){
            res=-res;
        }
        if (res<0.1){
            return 0;
        }
        if (sign) {
            return -(((int) (res*10d))/10.d);
        } else {
            return ((int) (res*10d))/10.d;
        }

        //else return ((int)res);
    }

    final public static double speedRound1(double res) {
        if (res<0.1){
            return 0;
        }
        if (res<50){
            return ((int) (res*10d))/10.d;
        } else {
            return ((int) res);
        }
    }

    final public static double coordRound5(double res) {
        return ((int) (res*100000.d))/100000.d;
    }

    final public static double coordRound3(double res) {
        if (res<0.001){
            return 0;
        }
        return ((int) (res*1000.d))/1000.d;
    }
//  final static public String makeOdometer1(float f) {
//    int km = (int)(f/1000f);
//    int m = (int)(f - km*1000f);
//    return String.valueOf(km)+'.'+make3(m);
//  }

    /** return height within units */
    final static public String heightUnits(int alt) {
        if (RMSOption.unitFormat==RMSOption.UNITS_METRIC){
            return String.valueOf(alt);
        } else if (RMSOption.unitFormat==RMSOption.UNITS_NAUTICAL){
            //return String.valueOf(distRound3(alt*0.3281f))+' '+LangHolder.nm;
            return String.valueOf((int) (alt*3.281f));
        } else if (RMSOption.unitFormat==RMSOption.UNITS_IMPERIAL){
            return String.valueOf((int) (alt*3.281f));
        }
        return MapUtil.emptyString;
    }

    /** return round height with measure units */
    final static public String heightWithName(int alt) {
        if (RMSOption.unitFormat==RMSOption.UNITS_METRIC){
            return heightUnits(alt)+' '+LangHolder.getString(Lang.m);
        } else if (RMSOption.unitFormat==RMSOption.UNITS_NAUTICAL){
            //return String.valueOf(distRound3(alt*0.3281f))+' '+LangHolder.nm;
            return heightUnits(alt)+' '+LangHolder.ft;
        } else if (RMSOption.unitFormat==RMSOption.UNITS_IMPERIAL){
            return heightUnits(alt)+' '+LangHolder.ft;
        }
        return MapUtil.emptyString;
    }

    /** return round distance with measure units */
    final static public String distWithNameRound3(double dist) {
        if (RMSOption.unitFormat==RMSOption.UNITS_METRIC){
            if (dist<1){
                return String.valueOf((int) (dist*1000f))+' '+LangHolder.getString(Lang.m);
            } else {
                return String.valueOf(distRound3(dist))+' '+LangHolder.getString(Lang.km);
            }
        } else if (RMSOption.unitFormat==RMSOption.UNITS_NAUTICAL){
            if (dist*0.5399568f<1){
                return String.valueOf((int) (dist*3281f))+' '+LangHolder.ft;
            } else {
                return String.valueOf(distRound3(dist*0.5399568f))+' '+LangHolder.nm;
            }
        } else if (RMSOption.unitFormat==RMSOption.UNITS_IMPERIAL){
            if (dist*0.6213882f<1){
                return String.valueOf((int) (dist*3281f))+' '+LangHolder.ft;
            } else {
                return String.valueOf(distRound3(dist*0.6213882f))+' '+LangHolder.mi;
            }
        }
        return MapUtil.emptyString;
    }

    /** return round distance with measure units */
    final static public String distWithNameRound2(double dist) {
        if (RMSOption.unitFormat==RMSOption.UNITS_METRIC){
            if (dist<1){
                return String.valueOf((int) (dist*1000f))+' '+LangHolder.getString(Lang.m);
            } else {
                return String.valueOf(distRound2(dist))+' '+LangHolder.getString(Lang.km);
            }
        } else if (RMSOption.unitFormat==RMSOption.UNITS_NAUTICAL){
            if (dist*0.5399568f<1){
                return String.valueOf((int) (dist*3281f))+' '+LangHolder.ft;
            } else {
                return String.valueOf(distRound2(dist*0.5399568f))+' '+LangHolder.nm;
            }
        } else if (RMSOption.unitFormat==RMSOption.UNITS_IMPERIAL){
            if (dist*0.6213882f<1){
                return String.valueOf((int) (dist*3281f))+' '+LangHolder.ft;
            } else {
                return String.valueOf(distRound2(dist*0.6213882f))+' '+LangHolder.mi;
            }
        }
        return MapUtil.emptyString;
    }

    /** return round distance with measure units */
    final static public String distUnitsRound3(double dist) {
        if (RMSOption.unitFormat==RMSOption.UNITS_METRIC){
            return String.valueOf(distRound3(dist));
        } else if (RMSOption.unitFormat==RMSOption.UNITS_NAUTICAL){
            return String.valueOf(distRound3(dist*0.5399568f));
        } else if (RMSOption.unitFormat==RMSOption.UNITS_IMPERIAL){
            return String.valueOf(distRound3(dist*0.6213882f));
        }
        return MapUtil.emptyString;
    }

    /** return round distance with measure units */
    final static public String speedUnitsRound3(double dist) {
        return distUnitsRound3(dist);
    }

    /** return round speed with measure units */
    final static public String speedWithNameRound1(double speed) {
        //boolean neg = (speed<0);
        //if (neg)speed = -speed;
        if (RMSOption.unitFormat==RMSOption.UNITS_METRIC){
            if (speed<0.11){
                StringBuffer sb=new StringBuffer(7);
                sb.append('0').append(' ').append(LangHolder.getString(Lang.kmh));
                return sb.toString();
            } else {
                return String.valueOf(speedRound1(speed))+' '+LangHolder.getString(Lang.kmh);
            }
        } else if (RMSOption.unitFormat==RMSOption.UNITS_NAUTICAL){
            if (speed*0.5399568f<0.11){
                StringBuffer sb=new StringBuffer(7);
                sb.append('0').append(' ').append(LangHolder.kn);
                return sb.toString();
            } else {
                return String.valueOf(speedRound1(speed*0.5399568f))+' '+LangHolder.kn;
            }
        } else if (RMSOption.unitFormat==RMSOption.UNITS_IMPERIAL){
            if (speed*0.6213882f<0.11){
                StringBuffer sb=new StringBuffer(7);
                sb.append('0').append(' ').append(LangHolder.mph);
                return sb.toString();
            } else {
                return String.valueOf(speedRound1(speed*0.6213882f))+' '+LangHolder.mph;
            }
        }
        return MapUtil.emptyString;
    }

    final public static String[] parseString(String s, char delim) throws Exception {
        int i=0;
        int pos=0;
        int nextPos=0;
        String[] result=new String[150];
        // check how big the array is.
        while (pos>-1) {
            pos=s.indexOf(delim, pos);
            if (pos<0){
                continue;
            }
            pos++;
            i++;
        }

        if (i>500){
            throw new Exception("to big:"+i);
        }

        i=0;
        pos=0;

        // Start splitting the string, search for each ','
        try {


            while (pos>-1) {
                pos=s.indexOf(delim, pos);
                if (pos<0){
                    continue;
                }

                nextPos=s.indexOf(delim, pos+1);
                if (nextPos<0){
                    nextPos=s.length();
                }

                result[i]=s.substring(pos+1, nextPos).trim();
                i++;
                if (i==result.length) {
                    return result;
                }

                if (i>result.length){
                    break;
                }
                if (pos>-1){
                    pos++;
                }
            }
        } catch (Throwable t) {
            //#mdebug
//#             if (RMSOption.debugEnabled){
//#                 DebugLog.add2Log("parS:"+i+":"+pos+":"+t.toString());
//#             }
            //#enddebug
        }
        return result;
    }

    final public static void parseString(String data, char delim, String[] result) throws Exception {
        int i=0;
        int pos=0;
        int nextPos=0;
        for (int j=result.length-1; j>=0; j--) {
            result[j]=null;    // check how big the array is.
        }
        while (pos>-1) {
            pos=data.indexOf(delim, pos);
            if (pos<0){
                continue;
            }
            pos++;
            i++;
        }

        if (i>500){
            throw new Exception("to big:"+i);
        }

        i=0;
        pos=0;

        // Start splitting the string, search for each ','
        while (pos>-1) {
            pos=data.indexOf(delim, pos);
            if (pos<0){
                continue;
            }

            nextPos=data.indexOf(delim, pos+1);
            if (nextPos<0){
                nextPos=data.length();
            }

            result[i]=data.substring(pos+1, nextPos).trim();
            i++;
            if (i==result.length) {
                return;
            }
            if (pos>-1){
                pos++;
            }
        }

    }

    /*
    final public static String[] parseSpaceString(String s) throws Exception{
    int i=0;
    int pos=0;
    int nextPos=0;
    String[] result = new String[20];
    // check how big the array is.
    while(pos>-1){
    pos = s.indexOf(" ", pos);
    if(pos<0){
    continue;
    }
    pos++;
    i++;
    }

    if(i>500){
    throw new Exception("to big:" + i);
    }

    i=0;
    pos=0;

    // Start splitting the string, search for each ','
    while(pos>-1){
    pos = s.indexOf(" ", pos);
    if(pos<0){
    continue;
    }

    nextPos = s.indexOf(" ", pos+1);
    if(nextPos<0){
    nextPos = s.length();
    }

    result[i] = s.substring(pos+1, nextPos);
    i++;
    if(pos>-1){
    pos++;
    }
    }

    return result;
    }
     */
    public final static void ARGB_Img_rot(int[] ARGB_Img0, int[] ARGB_Img1, double phi, int ImW, int ImH, int dx, int dy) {
        int x0, y0, x1, y1;
        int xc=ImW/2-dx, yc=ImH/2-dy;
//    int sn=(int)(Math.sin(phi)*1000d);
//    int cs=(int)(Math.cos(phi)*1000d);
        int sn=(int) (Math.sin(phi)*16384d);
        int cs=(int) (Math.cos(phi)*16384d);
        for (y1=ImH-1; y1>=0; y1--) {
            for (x1=ImW-1; x1>=0; x1--) {
//    for (y1=0;y1<ImH;y1++){
//      for (x1=0;x1<ImW;x1++) {
                //На всякий случай заполняем картинку цветом фона
                ARGB_Img1[y1*ImW+x1]=0;
                //x0=(int)((cs*(x1-xc)+sn*(y1-yc))/16384+xc);
                //y0=(int)(yc-(sn*(x1-xc)-cs*(y1-yc))/16384);
                x0=(((cs*(x1-xc)+sn*(y1-yc))>>14)+xc);
                y0=(yc-((sn*(x1-xc)-cs*(y1-yc))>>14));
                //Проверяем, не выходит ли точка за пределы области
                if ((x0>=0)&&(x0<ImW)&&(y0>=0)&&(y0<ImH)){               //Закрашиваем точку
                    ARGB_Img1[y1*ImW+x1]=ARGB_Img0[y0*ImW+x0];
                }
            }
        }
//    int cr,pn ;
//    for (y1=ImH-1;y1>0;y1--){
//      cr = y1;
//      pn= 0;
//      for (x1=ImW/2-1;x1>=0;x1--) {
//        y0=y1;
//        x0=x1-(ImW/2-x1)*(100*(ImH-y1)/y1)/100;
//        if ((x0>=0)&&(x0<ImW)&&(y0>=0)&&(y0<ImH)) {
//          ARGB_Img1[y1*ImW+x1]=ARGB_Img0[y0*ImW+x0];
//        } else ARGB_Img1[y1*ImW+x1]=0;
//      }
//      for (x1=ImW/2;x1<ImW;x1++) {
//        y0=y1;
//        x0=(ImW/2-x1)*(100*(ImH-y1)/y1)/100;
//        if ((x0>=0)&&(x0<ImW)&&(y0>=0)&&(y0<ImH)) {
//          ARGB_Img1[y1*ImW+x1]=ARGB_Img1[y0*ImW+x0];
//        } else ARGB_Img1[y1*ImW+x1]=0;
//      }
//    }
    }

    public final static void ARGB_Img_DblScale(int[] ARGB_Img0, int[] ARGB_Img1, int ImW, int ImH) {
        int x0, y0, x1, y1;
        int xc=ImW/4, yc=ImH/4;
        int pix, i;
        for (y1=ImH/2-1; y1>=0; y1--) {
            for (x1=ImW/2-1; x1>=0; x1--) {
                pix=ARGB_Img0[(y1+yc)*ImW+x1+xc];
                i=(y1+y1)*ImW+x1+x1;
                ARGB_Img1[i]=pix;
                ARGB_Img1[i+1]=pix;
                i=(y1+y1+1)*ImW+x1+x1;
                ARGB_Img1[i]=pix;
                ARGB_Img1[i+1]=pix;
            }
        }
    }

//    public final static void ARGB_Img_Scale(int[] srcRGB, int[] dstRGB, int srcW, int srcH, int dstW, int dstH) {
//    int x0, y0, x1, y1;
//    int pix;
//    double xRate = srcW/dstW;
//    double yRate = srcH/dstH;
//    
//    for (y1=dstH-1; y1>=0; y1--) {
//      for (x1=dstW-1; x1>=0; x1--) {
//        pix=0;
//        
//        dstRGB[y1*dstW+x1]=pix;
//      }
//    }
//          ARGB_Img1[y1*2*ImW+x1*2]=ARGB_Img0[(y1+yc)*ImW+x1+xc];
//  }
    public final static void ARGB_Img_Invert(int[] ARGB_Img0) {
       
        for (int i=ARGB_Img0.length-1; i>=0; i--) {
         ARGB_Img0[i]=(~ARGB_Img0[i])&0x00FFFFFF;
        }
//        for (int y1=ImH-1; y1>=0; y1--) {
//            for (int x1=ImW-1; x1>=0; x1--) {
//                ARGB_Img0[(y1)*ImW+x1]=~(ARGB_Img0[(y1)*ImW+x1]);
//            }
//        }
    }

    public final static int invertColor(int color) {
        return (~color)&0x00FFFFFF;
    }
    private final static double ro=206264.8062471;// ' Число угловых секунд в радиане
//' Эллипсоид Красовского
    public final static double aP=6378245;// ' Большая полуось
    private final static double alP=1/298.3;// ' Сжатие
    private final static double e2P=2*alP-alP*alP;// ' Квадрат эксцентриситета
//' Эллипсоид WGS84 (GRS80, эти два эллипсоида сходны по большинству параметров)
    public final static double aW=6378137;//' Большая полуось
    private final static double alW=1/298.257223563;// ' Сжатие
    private final static double e2W=2*alW-alW*alW;//' Квадрат эксцентриситета
//' Вспомогательные значения для преобразования эллипсоидов
    private final static double _a=(aP+aW)/2.;
    private final static double e2=(e2P+e2W)/2.;
    private final static double da=aW-aP;
    private final static double da1=aW-aP;
    private final static double de2=e2W-e2P;//' Линейные элементы трансформирования, в метрах
    private final static double dx=24;//25
    private final static double dy=-141;//132
    private final static double dz=-81;//83
//' Угловые элементы трансформирования, в секундах
    private final static double wx=0;
    private final static double wy=-0.35;
    private final static double wz=-0.82;
//' Дифференциальное различие масштабов
    private final static double ms=(0-0.12d)*0.000001d;

    final static double power(double base, double pow) {
        //x^y = e^(y*log(x))
        return exp(pow*ln(base));
    }

    static final double dB(double Bd, double Ld, double H) {
        double B=Bd*G2R;//0.017453292519943295769236907684886;
        double L=Ld*G2R;//0.017453292519943295769236907684886;
        double M=_a*(1-e2)/power((1-e2*Math.sin(B)*Math.sin(B)), 1.5);
        double N=_a*power((1-e2*Math.sin(B)*Math.sin(B)), -0.5);
        double dBr=ro/(M+H)*(N/_a*e2*Math.sin(B)*Math.cos(B)*da+(N*N/(_a*_a)+1)*N*Math.sin(B)*Math.cos(B)*de2*0.5-(dx*Math.cos(L)+dy*Math.sin(L))*Math.sin(B)+dz*Math.cos(B))-wx*Math.sin(L)*(1+e2*Math.cos(2*B))+wy*Math.cos(L)*(1+e2*Math.cos(2*B))-ro*ms*e2*Math.sin(B)*Math.cos(B);
        return dBr;
    }

    static final double WGS84_SK42_Lat(double Bd, double Ld, double H) {
        return Bd-dB(Bd, Ld, H)*2.7777777777777777777777777777778e-4;// / 3600.0;
    }

    static final double dL(double Bd, double Ld, double H) {
        double B=Bd*G2R;//0.017453292519943295769236907684886;
        double L=Ld*G2R;//0.017453292519943295769236907684886;
        double N=_a*power((1-e2*Math.sin(B)*Math.sin(B)), -0.5);
        double dLr=ro/((N+H)*Math.cos(B))*(-dx*Math.sin(L)+dy*Math.cos(L))+Math.tan(B)*(1-e2)*(wx*Math.cos(L)+wy*Math.sin(L))-wz;
        return dLr;
    }

    static final double WGS84_SK42_Long(double Bd, double Ld, double H) {
        return Ld-dL(Bd, Ld, H)*2.7777777777777777777777777777778e-4;// / 3600.0;
    }

    static final String coordTM2String(double coord, boolean lat, boolean sign) {
        if (lat){
            if (sign){
                return String.valueOf((int) coord)+'E';
            } else {
                return String.valueOf((int) coord)+'W';
            }
        } else {
            if (sign){
                return String.valueOf((int) coord)+'N';
            } else {
                return String.valueOf((int) coord)+'S';
            }
        }
    }
    private static double[] resSK=new double[3];
    private static char[] resZone=new char[1];

    final public static String coord2DatumLatString(double lat, double lon, double alt) {
        if (RMSOption.showProj==RMSOption.PROJ_GEO){
            if (RMSOption.showDatum==RMSOption.DATUMWGS84){
                return MapUtil.coordToString(lat, MapUtil.CLATTYPE, RMSOption.coordType);
            } else // if (RMSOption.showDatum==RMSOption.DATUMPULKOVO1942)
            {
                return MapUtil.coordToString(WGS84_SK42_Lat(lat, lon, alt), MapUtil.CLATTYPE, RMSOption.coordType);
            }
        } else if (RMSOption.showProj==RMSOption.PROJ_UTM){
            if (RMSOption.showDatum==RMSOption.DATUMWGS84){
                convert(lat, lon, resSK, resZone, true);
                return String.valueOf((int) resSK[2])+resZone[0]+' '+String.valueOf(coordTM2String(resSK[0], true, lon>=0));
            } else {
                convert(WGS84_SK42_Lat(lat, lon, alt), WGS84_SK42_Long(lat, lon, alt), resSK, resZone, true);
                return String.valueOf((int) resSK[2])+resZone[0]+' '+String.valueOf(coordTM2String(resSK[0], true, lon>=0));
            } // if (RMSOption.showDatum==RMSOption.DATUMPULKOVO1942)
        } else //      if (RMSOption.showProj==RMSOption.PROJ_GAUSSKRUG) {
        if (RMSOption.showDatum==RMSOption.DATUMWGS84){
            convert(lat, lon, resSK, resZone, false);
            return String.valueOf(coordTM2String(resSK[0], true, lon>=0));
        } else {
            convert(WGS84_SK42_Lat(lat, lon, alt), WGS84_SK42_Long(lat, lon, alt), resSK, resZone, false);
            return String.valueOf(coordTM2String(resSK[0], true, lon>=0));
        } // if (RMSOption.showDatum==RMSOption.DATUMPULKOVO1942)
        //}

//    {
//      double[] res = new double[2];
//      UTMCalc.convert(lat,lon,res);
//      return String.valueOf(res[0]);
//    }
    }

    final public static String coord2DatumLonString(double lat, double lon, double alt) {

        if (RMSOption.showProj==RMSOption.PROJ_GEO){
            if (RMSOption.showDatum==RMSOption.DATUMWGS84){
                return MapUtil.coordToString(lon, MapUtil.CLONTYPE, RMSOption.coordType);
            } else //        if (RMSOption.showDatum==RMSOption.DATUMPULKOVO1942)
            {
                return MapUtil.coordToString(WGS84_SK42_Long(lat, lon, alt), MapUtil.CLONTYPE, RMSOption.coordType);
            }
        } else if (RMSOption.showProj==RMSOption.PROJ_UTM){
            if (RMSOption.showDatum==RMSOption.DATUMWGS84){
                convert(lat, lon, resSK, resZone, true);
                return String.valueOf(coordTM2String(resSK[1], false, lat>=0));
            } else {
                convert(WGS84_SK42_Lat(lat, lon, alt), WGS84_SK42_Long(lat, lon, alt), resSK, resZone, true);
                return String.valueOf(coordTM2String(resSK[1], false, lat>=0));
            }
        } else //    if (RMSOption.showProj==RMSOption.PROJ_GAUSSKRUG) {
        if (RMSOption.showDatum==RMSOption.DATUMWGS84){
            convert(lat, lon, resSK, resZone, false);
            return String.valueOf(coordTM2String(resSK[1], false, lat>=0));
        } else {
            convert(WGS84_SK42_Lat(lat, lon, alt), WGS84_SK42_Long(lat, lon, alt), resSK, resZone, false);
            return String.valueOf(coordTM2String(resSK[1], false, lat>=0));
        }

//  }

//    {
//      double[] res = new double[2];
//      UTMCalc.convert(lat,lon,res);
//      return String.valueOf(res[1]);
//    }
    }
    public static String GMT = "GMT";
    private static Calendar calendarLoc=Calendar.getInstance(TimeZone.getDefault());
    private static Calendar calendarUTC=Calendar.getInstance(TimeZone.getTimeZone(GMT));
    /**
     * Показывает на сколько миллисекунд локальное время больше UTC
     */
    public static long diffLoc2UTCmillis;

    static {
        Date dt=new Date();
        calendarLoc.setTime(dt);
        calendarUTC.setTime(dt);
        calendarLoc.set(Calendar.HOUR_OF_DAY, 0);
        calendarLoc.set(Calendar.MINUTE, 0);
        calendarLoc.set(Calendar.SECOND, 0);
        calendarUTC.set(Calendar.HOUR_OF_DAY, 0);
        calendarUTC.set(Calendar.MINUTE, 0);
        calendarUTC.set(Calendar.SECOND, 0);
        long timeLoc=calendarLoc.getTime().getTime();
        long timeUTC=calendarUTC.getTime().getTime();
        diffLoc2UTCmillis=timeUTC-timeLoc;
    }

//  {
//    calendar.setTime(new Date());
//  }
    final public static String UTC2Local(long time) {
        String res;
        try {
            calendarLoc.setTime(new Date(time));
            res=
              MapUtil.numStr(calendarLoc.get(Calendar.HOUR_OF_DAY), 2)+':'+
              MapUtil.numStr(calendarLoc.get(Calendar.MINUTE), 2)+':'+
              MapUtil.numStr(calendarLoc.get(Calendar.SECOND), 2);

        } catch (Throwable t) {
            res=String.valueOf(time);
        }
        return res;
    }

    final public static String trackNameAuto() {
        return trackName(System.currentTimeMillis());
    }

    final public static String trackName(long time) {
        String res;
        try {
            Calendar cal;
            (cal=Calendar.getInstance()).setTime(new Date(time));
            res=MapUtil.numStr(cal.get(Calendar.YEAR), 4)+
              MapUtil.numStr(1+cal.get(Calendar.MONTH), 2)+
              MapUtil.numStr(cal.get(Calendar.DAY_OF_MONTH), 2)+
              '_'+
              MapUtil.numStr(cal.get(Calendar.HOUR_OF_DAY), 2)+
              MapUtil.numStr(cal.get(Calendar.MINUTE), 2);
        } catch (Throwable t) {
            res=String.valueOf(time);
        }
        return res;
    }

    final public static String trackNameSec() {
        String res;
        try {
            Calendar cal;
            (cal=Calendar.getInstance()).setTime(new Date());
            res=MapUtil.numStr(cal.get(Calendar.YEAR), 4)+
              MapUtil.numStr(1+cal.get(Calendar.MONTH), 2)+
              MapUtil.numStr(cal.get(Calendar.DAY_OF_MONTH), 2)+
              '_'+
              MapUtil.numStr(cal.get(Calendar.HOUR_OF_DAY), 2)+
              MapUtil.numStr(cal.get(Calendar.MINUTE), 2)+MapUtil.numStr(cal.get(Calendar.SECOND), 2);
        } catch (Throwable t) {
            res=String.valueOf(System.currentTimeMillis());
        }
        return res;
    }
/**
 *
 * @param time datetime to format
 * @return datetime as yyyy.mm.dd hh:nn:ss
 */
    final public static String dateTime2Str(long time, boolean utc) {
        String res;
        try {
            Calendar cal;
            (cal=Calendar.getInstance()).setTime(new Date(time));
            if (utc){
                cal.setTimeZone(TimeZone.getTimeZone(GMT));
            }
            res=MapUtil.numStr(cal.get(Calendar.YEAR), 4)+'.'+
              MapUtil.numStr(1+cal.get(Calendar.MONTH), 2)+'.'+
              MapUtil.numStr(cal.get(Calendar.DAY_OF_MONTH), 2)+
              ' '+
              MapUtil.numStr(cal.get(Calendar.HOUR_OF_DAY), 2)+':'+
              MapUtil.numStr(cal.get(Calendar.MINUTE), 2)+':'+
              MapUtil.numStr(cal.get(Calendar.SECOND), 2);
        } catch (Throwable t) {
            res=String.valueOf(time);
        }
        return res;
    }

    final public static String checkFilename(String fn) {
        StringBuffer sb=new StringBuffer(fn);
        for (int i=0; i<sb.length(); i++) {
            if (sb.charAt(i)==':'){
                sb.setCharAt(i, '_');
            } else if (sb.charAt(i)=='.'){
                sb.setCharAt(i, '_');
            } else if (sb.charAt(i)=='-'){
                sb.setCharAt(i, '_');
            } else if (sb.charAt(i)=='/'){
                sb.setCharAt(i, '_');
            } else if (sb.charAt(i)=='\\'){
                sb.setCharAt(i, '_');
            } else if (sb.charAt(i)==' '){
                sb.setCharAt(i, '_');
            }
        }
        return sb.toString();
    }

    public final static String time2mmsszzzString(long time) {
        time=time % 86400000;
        long hour=time/3600000;
        long min=(time-hour*3600000)/60000;
        long sec=(time-hour*3600000-min*60000)/1000;
        long msec=(time-hour*3600000-min*60000-sec*1000);
        String s=numStr(hour, 2)+':'+numStr(min, 2)+':'+numStr(sec, 2)+'.'+numStr(msec, 3);
        return s;
    }

    public final static String time2String(long time) {
        long hour=time/3600000;
        long min=(time-hour*3600000)/60000;
        long sec=(time-hour*3600000-min*60000)/1000;
        String s=numStr(hour, 2)+':'+numStr( min, 2)+':'+numStr(sec, 2);
        return s;
    }

    public final static String time2hhmmString(long time) {
        long hour=time/3600000;
        long min=(time-hour*3600000)/60000;
        String s=numStr((int) hour, 2)+':'+numStr((int) min, 2);
        return s;
    }

    public final static String getScreenFileName(String addr1, byte imageType) {
        RMSOption.picFileNumber++;
        RMSOption.changed=true;
        String trn=addr1+"scr"+MapUtil.trackNameSec();
        if (imageType==ScreenSend.IMAGEJPG){
            return trn+".jpg";
        } else if (imageType==ScreenSend.IMAGEGIF){
            return trn+".gif";
        } else {
            return trn+".bmp";
        }
    }

    public final static String getBBOX() {
        int mx, my;

        if ((GPSReader.NUM_SATELITES>0)&&(MapCanvas.gpsBinded)){
            mx=MapUtil.getXMap(GPSReader.LONGITUDE, MapCanvas.map.level);
            my=MapUtil.getYMap(GPSReader.LATITUDE, MapCanvas.map.level);
        } else {
            mx=MapUtil.getXMap(MapCanvas.reallon, MapCanvas.map.level);
            my=MapUtil.getYMap(MapCanvas.reallat, MapCanvas.map.level);
        }
        int mxt=mx-MapCanvas.dmaxx/3;
        int mxb=mx+MapCanvas.dmaxx/3;
        int myt=my+MapCanvas.dmaxy/3;
        int myb=my-MapCanvas.dmaxy/3;

        return "BBOX="+MapUtil.getLon(mxt, MapCanvas.map.level)+","+
          MapUtil.getLat(myt, MapCanvas.map.level)+","+MapUtil.getLon(mxb, MapCanvas.map.level)+","+
          MapUtil.getLat(myb, MapCanvas.map.level);
    }

    public static void convert(double Lat, double Long, double[] res, char[] resZ, boolean UTM) {

        //TM2LL=false;
        // boolean OK=true;
        //double Deg2Rad=Math.PI/180.0;
        //  String Area="UTM2";//options[selectedIndex].value;
        //  String Alpha100km="VQLFAVQLFAWRMGBWRMGBXSNHCXSNHCYTOJDYTOJDZUPKEZUPKEVQLFAVQLFAWRMGBWRMGBXSNHCXSNHCYTOJDYTOJDZUPKEZUPKE";
//  if (Area.equals("UK"))
//    { // British Isles (Airy Ellipsoid)
//    F0=0.9996012717; // Local scale factor on Central Meridian
//    A1=6377563.396*F0; // Major Semi-axis, Airy Ellipsoid
//    B1=6356256.910*F0; // Minor Semi-axis, Airy Ellipsoid
//    K0=49*Deg2Rad; // Lat of True Origin
//    Merid=-2;
//    Form.Meridian.value='';
//    L0=Merid*Deg2Rad; // Long of True Origin (2W)
//    N0=-100000; // Grid Northing of True Origin (m)
//    E0=400000;  // Grid Easting of True Origin (m)
//    }
//   else if (Area.substring(0,2)=='FI')
//    { // Finnish KKJ International Ellipsoid
//    F0=1.0;
//    A1=6378388.000*F0;
//    B1=6356911.946*F0;
//    K0=0;
//    Merid=21+3*(eval(Area.substring(2,3))-1);
//    Form.Meridian.value='';
//    L0=Merid*Deg2Rad;
//    N0=0;
//    E0=500000+1000000*eval(Area.substring(2,3));
//    }
// else if (Area=='IE')
//    { // Ireland (modified Airy Ellipsoid)
//    F0=1.000035;
//    A1=6377340.189*F0;
//    B1=6356034.5*F0;
//    K0=53.5*Deg2Rad;
//    Merid=-8;
//    Form.Meridian.value='';
//    L0=Merid*Deg2Rad;
//    N0=250000;
//    E0=200000;
//    }
// else if (Area=='SE')
//    { // Sweden (Bessel Ellipsoid)
//    F0=1.0;
//    A1=6377397.155*F0;
//    B1=6356078.963*F0;
//    K0=0.0;
//    Merid=15.8082777778;
//    Form.Meridian.value='';
//    L0=Merid*Deg2Rad;
//    N0=0;
//    E0=1500000;
//    }
// else if (Area=='SE2')
//    { // Sweden (GRS80)
//    F0=1.00000564631;
//    A1=6378137.0*F0;
//    B1=6356752.3*F0;
//    K0=0.0;
//    Merid=15.8062819;
//    Form.Meridian.value='';
//    L0=Merid*Deg2Rad;
//    N0=-667.968;
//    E0=1500064.08;
//    }
//   else if (Area.substring(0,2)=='IT')
//    { // Italy International Ellipsoid
//    F0=0.9996;
//    A1=6378388.000*F0;
//    B1=6356911.946*F0;
//    K0=0;
//    Merid=9+6*(eval(Area.substring(2,3))-1);
//    Form.Meridian.value='';
//    L0=Merid*Deg2Rad;
//    N0=0;
//    if (Area.substring(2,3)=='1') E0=1500000; else E0=2520000;
//    }
//  else if (Area=='UTM1')
//    { // UTM International Ellipsoid
//    F0=0.9996;
//    A1=6378388.000*F0;
//    B1=6356911.946*F0;
//    K0=0;
//    N0=0;
//    E0=500000;
//    }
//  else
//    if (Area.'UTM2')
//    { // UTM WGS84 Ellipsoid
        double F0=(UTM)?0.9996:1.0;
        double A1=(RMSOption.showDatum==RMSOption.DATUMWGS84)?MapUtil.aW*F0:MapUtil.aP*F0;
        double B1=(RMSOption.showDatum==RMSOption.DATUMWGS84)?6356752.3142*F0:6356863.0188*F0;
        double K0=0;
        double N0=0;
        double E0=500000;
//    }

        double N1=(A1-B1)/(A1+B1); // n
        double N2=N1*N1;
        double N3=N2*N1;
        double E2=((A1*A1)-(B1*B1))/(A1*A1); // e^2
//  if (TM2LL)
//    {
//    /* yet to be implemented */
//    }
//  else
//    {
        // double Deg=LatDegrees; double Min=LatMinutes; double Sec=LatSeconds;
//    boolean AllBlank=false;
        //double Lat=1.0*Math.abs(Deg)+Math.abs(Min)/60.0+Math.abs(Sec)/3600.0;
        //if (Lat>90) {alert("Invalid Latitude"); OK=false; }
        boolean South=(Lat<0);///((Form.NS.value.toUpperCase()=='S') && (Lat>0));
        if (South){
            Lat=-Lat;
            //Deg=LongDegrees; Min=LongMinutes;
            //Sec=LongSeconds;
        }
        boolean West=(Long<0);//(Form.EW.value.toUpperCase()=='W');
        // double Long=1.0*Math.abs(Deg)+Math.abs(Min)/60.0+Math.abs(Sec)/3600.0;
        //if (Long>180) {alert("Invalid Longitude"); OK=false; }
        //if (West) Long=-Long;
        double K=Lat*MapUtil.G2R;
        double L=Long*MapUtil.G2R;
        double SINK=Math.sin(K);
        double COSK=Math.cos(K);
        double TANK=SINK/COSK;
        double TANK2=TANK*TANK;
        double COSK2=COSK*COSK;
        double COSK3=COSK2*COSK;
        double K3=K-K0;
        double K4=K+K0;
        double Merid=0;
        double L0=0;
//    if (Area.equals("UTM1" )|| (Area.equals("UTM2"))) {
//      if (Form.AutoCM[0].checked)
//        {
        Merid=Math.floor((Long)/6)*6+3;
        if ((Lat>=72)&&(Long>=0)){
            if (Long<9){
                Merid=3;
            } else if (Long<21){
                Merid=15;
            } else if (Long<33){
                Merid=27;
            } else if (Long<42){
                Merid=39;
            }
        }
        if ((Lat>=56)&&(Lat<64)){
            if ((Long>=3)&&(Long<12)){
                Merid=9;
            }
        }
        boolean MeridWest=Merid<0;
        //if (MeridWest) {Form.MeridianEW.value='W';} else {Form.MeridianEW.value='E';}
        //Form.Meridian.value=Math.abs(Merid);
        L0=Merid*MapUtil.G2R;
        // Long of True Origin (3,9,15 etc)
//        }
//      else
//        {
//        Merid=Form.Meridian.value;
//        if (Merid=='') {Merid=0; alert("You must enter a value for Central Meridian"); OK=false;}
//        MeridWest=(Form.MeridianEW.value.toUpperCase()=='W');
//        if (MeridWest) Merid=-Merid;
//        L0=Merid*Deg2Rad; // Long of True Origin (3,9,15 etc)
//        }

//    }

        // ArcofMeridian
        double J3=K3*(1+N1+1.25*(N2+N3));
        double J4=Math.sin(K3)*Math.cos(K4)*(3*(N1+N2+0.875*N3));
        double J5=Math.sin(2*K3)*Math.cos(2*K4)*(1.875*(N2+N3));
        double J6=Math.sin(3*K3)*Math.cos(3*K4)*35/24*N3;
        double M=(J3-J4+J5-J6)*B1;

        // VRH2
        double Temp=1-E2*SINK*SINK;
        double V=A1/Math.sqrt(Temp);
        double R=V*(1-E2)/Temp;
        double H2=V/R-1.0;

        double P=L-L0;
        double P2=P*P;
        double P4=P2*P2;
        J3=M+N0;
        J4=V/2*SINK*COSK;
        J5=V/24*SINK*(COSK3)*(5-(TANK2)+9*H2);
        J6=V/720*SINK*COSK3*COSK2*(61-58*(TANK2)+TANK2*TANK2);
        double North=J3+P2*J4+P4*J5+P4*P2*J6;
        //if ((Area.equals("UTM1") || (Area.equals("UTM2"))) && South) North=North+10000000.0; // UTM S hemisphere
        if (South){
            North=10000000.0-North; // UTM S hemisphere
        }
        double J7=V*COSK;
        double J8=V/6*COSK3*(V/R-TANK2);
        double J9=V/120*COSK3*COSK2;
        J9=J9*(5-18*TANK2+TANK2*TANK2+14*H2-58*TANK2*H2);
        double East=E0+P*J7+P2*P*J8+P4*P*J9;
        res[0]=Math.ceil(East);
        res[1]=Math.ceil(North); // should strictly be trunc
        if (!UTM){
            return;
            //Form.Easting.value=IEast;
            //Form.Northing.value=INorth;
//  }
            //String EastStr=""+Math.abs(IEast); String NorthStr=""+abs(INorth);
            //while (EastStr.length<7) EastStr='0'+EastStr;
            //while (NorthStr.length<7) NorthStr='0'+NorthStr;
            //GR100km=eval(EastStr.substring(1,2)+NorthStr.substring(1,2));
            //GRremainder=EastStr.substring(2,7)+' '+NorthStr.substring(2,7);
//  if (Area=='UK')
//    { // British Isles
//    if (IEast<0 || INorth<0 || IEast>999999 || INorth>1499999)
//      GR='outside British grid area';
//    else
//      {
//      GR=Alpha100km.substring(GR100km,GR100km+1)+' '+GRremainder;
//      HJ=(INorth>=1000000); TOJ=(IEast>=500000); ST=(INorth<500000);
//      if (HJ)
//        { if (TOJ) P='J'; else P='H'; }
//      else
//        {
//        if (ST)
//          { if (TOJ) P='T'; else P='S'; }
//        else
//          { if (TOJ) P='O'; else P='N'; }
//        }
//      GR=P+GR;
//      }
//    Form.GridRef.value=GR;
//    }
//  else if (Area.substring(0,2)=='FI')
//    { // Finland
//    ELow=1000000*eval(Area.substring(2,3));
//    if (IEast<ELow || INorth<6600000 || IEast>(ELow+999999) || INorth>7799999)
//      GR='outside Finnish zone area';
//    else
//      {
//      GR=NorthStr+' : '+EastStr;
//      }
//    Form.GridRef.value=GR;
//    }
//  else if (Area=='IE')
//    { // Ireland
//    if (IEast<0 || INorth<0 || IEast>499999 || INorth>499999)
//      GR='outside Irish grid area';
//    else
//      {
//      GR=Alpha100km.substring(GR100km,GR100km+1)+' '+GRremainder;
//      }
//    Form.GridRef.value=GR;
//    }
//  else if (Area=='SE')
//    { // Sweden (Bessel Ellipsoid)
//    if (IEast<0 || INorth<0)
//      GR='outside Swedish grid area';
//    else
//      GR=NorthStr+' '+EastStr;
//    Form.GridRef.value=GR;
//    }
//  else
//    if ((Area.equals("UTM1")) || (Area.equals("UTM2")))
//    { // UTM
        }
        double LongZone=(Merid-3)/6+31;
        res[2]=LongZone;
//    String GR;
//    if (LongZone % 1 != 0)
//      GR="non-UTM central meridian";
//    else {
//      if (IEast<100000 || Lat<-80 || IEast>899999 || Lat>=84)
//        GR="outside UTM grid area";
//      else {
//        String Letters="ABCDEFGHJKLMNPQRSTUVWXYZ";
        //Pos=Math.round(Lat/8-0.5)+10+2;
        int Pos;
        if (!South) {
            Pos=(int) (Math.floor(Lat/8)+10+2);
        } else {
            Pos=(int) (Math.floor(-Lat/8)+10+2);
        }

        if ((Pos>=0)&&(Pos<Letters.length())){
            resZ[0]=Letters.charAt(Pos);// substring(Pos,Pos+1);
//        if (LatZone>'X') LatZone='X';
//        Pos=Math.round(abs(INorth)/100000-0.5);
//        while (Pos>19) Pos=Pos-20;
//        if (LongZone % 2 == 0) {
//          Pos=Pos+5; if (Pos>19) Pos=Pos-20; }
//        N100km=Letters.substring(Pos,Pos+1);
//        Pos=GR100km/10-1;
//        P=LongZone; while (P>3) P=P-3;
//        Pos=Pos+((P-1)*8);
//        E100km=Letters.substring(Pos,Pos+1);
//        GR=LongZone+LatZone+E100km+N100km+' '+GRremainder;
//      }
//    }
//    Form.GridRef.value=GR;
//    }
            //  if (!OK) ClearGridRef(Form);
        }
    }
    private static String Letters="ABCDEFGHJKLMNPQRSTUVWXYZ";
    public static int[] colors={0x000000, 0x000080, 0x008000, 0x008080, 0x800000, 0x800080, 0x808000,
        0x808080, 0xC0C0C0, 0x0000FF, 0x00FF00, 0x00FFFF, 0xFF0000, 0xFF00FF, 0xFFFF00,
        0xC0C0C0, 0x808080, 0xFFFFFF
    };
    public static String[] colorNames={"Black", "Navy", "Green", "Teal", "Maroon", "Purple", "Olive",
        "Gray", "LtGray", "Blue", "Lime", "Aqua", "Red", "Fuchsia", "Yellow",
        "LtGray", "Gray", "White"
    };


    public final static int getColorIndex(int color) {
        int ind=-1;
        for (int i=MapUtil.colors.length-1; i>=0; i--) {
            if (color==MapUtil.colors[i]){
                ind=i;
                break;
            }
        }
        return ind;
    }

    public static void fillColorChoice(ChoiceGroup cg) {
        Image ic;
        Graphics g;
        cg.deleteAll();
        for (int i=0; i<colors.length; i++) {
            ic=Image.createImage(14, 14);
            g=ic.getGraphics();
            g.setColor(colors[i]);
            g.fillRect(0, 0, 14, 14);
            cg.append(colorNames[i], Image.createImage(ic));
        }
    }

    public static Image Img_fadeDarken(int ImW, int ImH) {
        int[] ARGB_Img=new int[ImW*ImH];
        //int intense = ;
        int c=RMSOption.shadowColor+((RMSOption.getBoolOpt(RMSOption.BL_TRANS50))?0x80000000:0x50000000);
        for (int y1=ImH-1; y1>=0; y1--) {
            for (int x1=ImW-1; x1>=0; x1--) {
                //заполняем картинку черным цветом 40% прозрачности
                ARGB_Img[y1*ImW+x1]=c;
            }
        }
        c=RMSOption.shadowColor+0x30000000;
        int y1=ImH-1;
        for (int x1=ImW-1; x1>=0; x1--) {
            //заполняем картинку черным цветом 25% прозрачности
            ARGB_Img[y1*ImW+x1]=c;
        }
        //y1=0;
        for (int x1=ImW-1; x1>=0; x1--) {
            //заполняем картинку черным цветом 25% прозрачности
            ARGB_Img[x1]=c;
        }
        return Image.createRGBImage(ARGB_Img, ImW, ImH, true);
    }

    public static String extractFilename(String furl) {
        try {
            return furl.substring(furl.lastIndexOf('/')+1);
        } catch (Throwable ttt) {
            return furl.substring(furl.length()-6);
        }
    }

    public static int checkSumAsInt(byte[] data) {
        int sum=0;
        int bi;
        for (int i=data.length-1; i>=0; i--) {
            bi=data[i];
            if (bi<0) {
                bi+=256;
            }
            sum+=bi;
            //sum+=data[i];
        }
        return sum;
    }

    public static int checkSum(byte[] data) {
        int sum=0;
        //int bi;
        for (int i=data.length-1; i>=0; i--) {
            //bi = data[i];
            //if (bi<0)bi+=256;
            //sum+=bi;
            sum+=data[i];
        }
        return sum;
    }

    private static final double md(double Y, double X) {
//' Equivalent of spreadsheet Mod(y,x)
//' Returns the remainder on dividing y by x in the range
//' 0<=Md <x
        if (Y<0){
            return Y-X*((long) (Y/X))+X;
        } else {
            return Y-X*((long) (Y/X));
        }
//End Function
    }

    /**
     * Return sun time in hours
     *
     * @param datetime время
     * @param lat широта
     * @param lon долгота
     * @param zenith зенит солнца
     * @param rise вернуть восход или закат
     * @return time in hours
     */
    public static double getSun(long datetime, double lat, double lon, double zenith, boolean rise) {
        calendarLoc.setTime(new Date(datetime));
// N1 = Int(275 * Month(dateinput) / 9)
        int N1=(275*(calendarLoc.get(Calendar.MONTH)+1)/9);

//n2 = Int((Month(dateinput) + 9) / 12)
        int n2=(((calendarLoc.get(Calendar.MONTH)+1)+9)/12);

        int Year=calendarLoc.get(Calendar.YEAR);
        int n3=(1+((Year-4*(Year/4)+2)/3));

//n3 = (1 + Int((Year(dateinput) - 4 * Int(Year(dateinput) / 4) + 2) / 3))
//nd = N1 - (n2 * n3) + Day(dateinput) - 30
        int nd=N1-(n2*n3)+calendarLoc.get(Calendar.DAY_OF_MONTH)-30;

//lnghour = Lon / 15#
        double lnghour=lon/15.0;

//If (rise) Then
//  t = nd + ((6 - lnghour) / 24)
//Else
//  t = nd + ((18 - lnghour) / 24)
//End If
        double t;
        if (rise){
            t=nd+((6-lnghour)/24);
        } else {
            t=nd+((18-lnghour)/24);
        }

//m = (0.9856 * t) - 3.289
        double m=(0.9856*t)-3.289;
//  l = md(m + 1.916 * sIn(degtorad(m)) + 0.02 * sIn(degtorad(2 * m)) + 282.634, 360)

        double l=md(m+1.916*Math.sin(m*G2R)+0.02*Math.sin(2*m*G2R)+282.634, 360);
//ra = radtodeg(md(Atn(0.91764 * Tan(degtorad(l))), 2 * Pi))
        double ra=R2G*(md(atan(0.91764*Math.tan(l*G2R)), 2*PI));

//lquad = Int(l / 90) * 90
        double lquad=(int) (l/90)*90;
//raquad = Int(ra / 90) * 90
        double raquad=(int) (ra/90)*90;
        ra=ra+lquad-raquad;
        ra=ra/15;
        double sindec=0.39782*Math.sin(l*G2R);
//cosdec = Cos(Asn(sindec));
       // boolean negCosdec =sindec<0;
        double cosdec=Math.cos(asin(sindec));
      //  if (negCosdec)
       //     cosdec = -cosdec;
//coshr = (Cos(degtorad(zenith)) - sindec * sIn(degtorad(lat))) / (cosdec * (Cos(degtorad(lat)) + 1E-20))
        double coshr=(Math.cos(zenith*G2R)-sindec*Math.sin(lat*G2R))/(cosdec*(Math.cos(lat*G2R)+(1E-20)));
        double sun;
        double h;
//If (coshr > 1) Then
//   sun = -1
        if (coshr>1){
            sun=-1;
        } //ElseIf (coshr < -1) Then
        //   sun = -2
        else if (coshr<-1){
            sun=-2;
        } //Else
        //   If (rise) Then
        //     h = 360 - radtodeg(Acs(coshr))
        //   Else
        //    h = radtodeg(Acs(coshr))
        //   End If
        else {
            if (rise){
                h=360-R2G*acos(coshr);
            } else {
                h=R2G*acos(coshr);
            }


            h=h/15;
            t=h+ra-0.06571*t-6.622;
            sun=md(t-lnghour, 24);
        }
        return sun;
    }
    private static String notime="--:--";

    public static String getSunString(long datetime, double lat, double lon, boolean rise, boolean local) {
     try{
//         Calendar cc = Calendar.getInstance();
//         cc.setTime(new Date());
//         cc.set(Calendar.DAY_OF_MONTH, 25);
//         cc.set(Calendar.MONTH, 5);
//         cc.set(Calendar.YEAR, 1990);
//        double sun=getSun(cc.getTime().getTime(), 40.9, -74.3, 90.833333333333, rise);

        double sun=getSun(datetime, lat, lon, 90.833333333333, rise);
        if (sun==-2){
            return notime;
        }
        if (sun==-1){
            return notime;
        }
        long millis=(long) (sun*60*60000)+((local)?diffLoc2UTCmillis:0);
        //long millis=(long) (sun*60*60000)+60000*60*3;
        if (millis<0)millis+=60000*60*24;else if (millis>60000*60*24) millis-=60000*60*24;
        return time2hhmmString(millis);
     }catch(Throwable tt){
         return notime;
     }
    }
}

