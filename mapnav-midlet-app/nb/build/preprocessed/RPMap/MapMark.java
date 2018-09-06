/*
 * MapLabel.java
 *
 * Created on 12 ������ 2007 �., 21:36
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package RPMap;

import java.io.IOException;
import misc.Util;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 *
 * @author RFK
 */
public class MapMark {

  public double lat;
  public double lon;
  public int level;
  public String name;
  public int rId;
  public boolean hasSound;

  /** Creates a new instance of MapLabel */
  public MapMark(double la, double lo, int lvl, String nm, int id) {
    lat=la;
    lon=lo;
    level=lvl;
    name=nm;
    rId=id;
  }

  /** Creates a new instance of MapLabel */
  public MapMark(double la, double lo, int lvl, String nm, int id,boolean hasSound) {
    lat=la;
    lon=lo;
    level=lvl;
    name=nm;
    rId=id;
    this.hasSound=hasSound;
  }
 /**
  * Используется только для чтения из xml
  */
  public byte[] soundData;
  public String soundFormat;
  /** Creates a new instance of MapKMLPoint */
  public MapMark(KXmlParser parser) throws IOException, XmlPullParserException {
    //super(0, 0, 0, MapUtil.emptyString,-1);
    //this.kml=kml;
    this.name=MapUtil.emptyString;
    //#debug
    parser.require(XmlPullParser.START_TAG, null, "mark");
    while (parser.nextTag()!=XmlPullParser.END_TAG) {
      //#debug
      parser.require(XmlPullParser.START_TAG, null, null);
      String tag=parser.getName();
      //#debug
      System.out.println("mark<"+tag+">");
      //name=name.toLowerCase();

      if (tag.equals("name")){
        this.name=parser.nextText();
      } else if (tag.equals("lat")){
        this.lat=Double.parseDouble(parser.nextText());
      } else if (tag.equals("lon")){
        this.lon=Double.parseDouble(parser.nextText());
      } else if (tag.equals("zoom")){
        this.level=Integer.parseInt(parser.nextText());
      }  else if (tag.equals("soundFormat")){
        this.soundFormat=parser.nextText();
        this.hasSound=true;
      }  else if (tag.equals("soundData")){
        this.soundData=Util.decodeBase64(parser.nextText());
      } else {
        parser.skipTree();      //#debug
      }
      parser.require(XmlPullParser.END_TAG, null, null);
    }

    //#debug
    parser.require(XmlPullParser.END_TAG, null, null);
  }
}
