/*
 * KMLStyle.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package kml;

import java.io.IOException;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 *
 * @author Raev
 */
public class KMLStyle {
  public String id;
  public String iconhref;
  KMLParser kml;
  /** Creates a new instance of KMLStyle */
  public KMLStyle(KXmlParser parser,KMLParser kml) throws IOException, XmlPullParserException {
    this.kml=kml;
      //#debug
    parser.require(XmlPullParser.START_TAG, null, "Style");
    id = parser.getAttributeValue(null,kml.s_id);
    while (parser.nextTag() != XmlPullParser.END_TAG) {
      //#debug
      parser.require(XmlPullParser.START_TAG, null, null);
      String name = parser.getName().toLowerCase();
      //#debug
      System.out.println("IconStyle<"+name+">");
      if (name.equals(kml.s_iconstyle))
        readIconStyle(parser);
      else
        parser.skipTree();
      
      //#debug
      parser.require(XmlPullParser.END_TAG, null,null);
    }
    //#debug
    parser.require(XmlPullParser.END_TAG, null, "Style");
  }
  
  void readIconStyle(KXmlParser parser) throws IOException, XmlPullParserException {
    //#debug
    parser.require(XmlPullParser.START_TAG, null, "IconStyle");
    while (parser.nextTag() != XmlPullParser.END_TAG) {
      //#debug
      parser.require(XmlPullParser.START_TAG, null, null);
      String name = parser.getName().toLowerCase();
      //#debug
      System.out.println("IconStyle<"+name+">");
      
      if (name.equals(kml.s_icon)) {
        parser.nextTag();
        iconhref=parser.nextText();
        parser.next();
        parser.next();
      } else
        parser.skipTree();
      
      //#debug
      parser.require(XmlPullParser.END_TAG, null, null);
    }
    //#debug
    parser.require(XmlPullParser.END_TAG, null, "IconStyle");
  }
  
}
