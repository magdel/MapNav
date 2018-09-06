/*
 * MapKMLPoint.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package kml;

import RPMap.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
//#debug
//# import misc.DebugLog;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 *
 * @author RFK
 */
public class KMLMapPoint extends MapPoint {

    public String styleUrl;
    public String description;
    public byte accuracy;
    public float northBB;
    public float southBB;
    public float eastBB;
    public float westBB;

    public boolean isRect() {
        return (northBB!=0);
    }
    public int scrNBB;
    public int scrSBB;
    public int scrEBB;
    public int scrWBB;
    public float[] lineCoords;
    KMLParser kml;

    public void save2Stream(DataOutputStream out) throws IOException {
        super.save2Stream(out);

        out.writeByte(1);//version
        if (styleUrl==null) {
            styleUrl=MapUtil.emptyString;
        }
        out.writeUTF(styleUrl);
        if (description==null) {
            description=MapUtil.emptyString;
        }
        out.writeUTF(description);
        out.writeByte(accuracy);
        out.writeFloat(northBB);
        out.writeFloat(southBB);
        out.writeFloat(eastBB);
        out.writeFloat(westBB);

        out.writeInt(scrNBB);
        out.writeInt(scrSBB);
        out.writeInt(scrEBB);
        out.writeInt(scrWBB);
        if (lineCoords==null) {
            out.writeInt(0);
        } else {
            out.writeInt(lineCoords.length);
            for (int i=0; i<lineCoords.length; i++) {
                out.writeFloat(lineCoords[i]);
            }
        }
    }

    public KMLMapPoint(DataInputStream in) throws IOException {
        super(in);
        kind=MapPoint.TYPE_KML;
        byte ver=in.readByte();

        styleUrl=in.readUTF();
        description=in.readUTF();
        if (description.equals(MapUtil.emptyString)) {
            description=null;
        }
        accuracy=in.readByte();

        northBB=in.readFloat();
        southBB=in.readFloat();
        eastBB=in.readFloat();
        westBB=in.readFloat();

        scrNBB=in.readInt();
        scrSBB=in.readInt();
        scrEBB=in.readInt();
        scrWBB=in.readInt();

        int len=in.readInt();
        if (len>0){
            lineCoords=new float[len];
            for (int i=0; i<lineCoords.length; i++) {
                lineCoords[i]=in.readFloat();
            }
        }
    }

    /** Creates a new instance of MapKMLPoint */
    public KMLMapPoint(KXmlParser parser, KMLParser kml) throws IOException, XmlPullParserException {
        super(0, 0, 0, 0);
        kind=MapPoint.TYPE_KML;
        this.kml=kml;
        //#debug
//#         parser.require(XmlPullParser.START_TAG, null, "Placemark");
        boolean hasName=false;
        try {
            while (parser.nextTag()!=XmlPullParser.END_TAG) {
                //#debug
//#                 parser.require(XmlPullParser.START_TAG, null, null);
                String name=parser.getName();
                //#debug
//#                 System.out.println("Placemark<"+name+">");
                name=name.toLowerCase();
                if (name.equals(kml.s_addressdetails)){
                    this.accuracy=Byte.parseByte(parser.getAttributeValue(null, kml.s_accuracy));
                    parser.skipTree();
                } else if (name.equals(kml.s_description)){
                    name=parser.nextText();
                    if (!hasName){
                        this.name=name;
                        if (this.name!=null){
                            this.name=this.name.trim();
                            int pos=this.name.indexOf("<");
                            if (pos>50) {
                                pos=50;
                            }
                            if (pos>1) {
                                this.name=this.name.substring(0, pos-1);
                            }
                        }
                    } 
                } else if (name.equals(kml.s_address)){
                    if (!hasName) {
                        this.name=parser.nextText();
                    } else {
                        parser.nextText();
                    }

                } else if (name.equals(kml.s_name)){
                    this.name=parser.nextText();
                    hasName=(this.name!=null);
                    if (hasName) {
                        hasName=!this.name.equals(MapUtil.emptyString);
                    }
                } else if (name.equals(kml.s_styleurl)){
                    this.styleUrl=parser.nextText();
//        } else
//          if (name.equals(KMLParser.s_description)) {
//        this.description=parser.nextText();
                } else if (name.equals(kml.s_region)) {
                    readRegion(parser);
                } else if (name.equals(kml.s_multigeometry)) {
                    readMultiGeometry(parser);
                } else if (name.equals(kml.s_geometrycollection)) {
                    readGeometryCollection(parser);
                } else if (name.equals(kml.s_point)){

                    parser.nextTag();
                    String coords=parser.nextText();
                    try {
                        String[] data=MapUtil.parseString(","+coords, ',');
                        lon=Double.parseDouble(data[0]);
                        lat=Double.parseDouble(data[1]);
                    } catch (Throwable t) {
                    }
                    parser.next();
                } else {
                    parser.skipTree();
                }

                //#debug
//#                 parser.require(XmlPullParser.END_TAG, null, null);
            }
        } finally {
            this.kml=null;
        }
        //#debug
//#         parser.require(XmlPullParser.END_TAG, null, null);
    }

    void readRegion(KXmlParser parser) throws IOException, XmlPullParserException {
        //#debug
//#         parser.require(XmlPullParser.START_TAG, null, "Region");
        while (parser.nextTag()!=XmlPullParser.END_TAG) {
            //#debug
//#             parser.require(XmlPullParser.START_TAG, null, null);
            String name=parser.getName();
            //#debug
//#             System.out.println("Region<"+name+">");
            name=name.toLowerCase();
            if (name.equals(kml.s_latlonaltbox)) {
                readLatLonAltBox(parser);
            } else {
                parser.skipTree();
            }

            //#debug
//#             parser.require(XmlPullParser.END_TAG, null, null);
        }
        //#debug
//#         parser.require(XmlPullParser.END_TAG, null, "Region");
    }

    void readLatLonAltBox(KXmlParser parser) throws IOException, XmlPullParserException {
        //#debug
//#         parser.require(XmlPullParser.START_TAG, null, "LatLonAltBox");
        while (parser.nextTag()!=XmlPullParser.END_TAG) {
            //#debug
//#             parser.require(XmlPullParser.START_TAG, null, null);
            String name=parser.getName();
            //#debug
//#             System.out.println("LatLonAltBox<"+name+">");
            name=name.toLowerCase();
            if (name.equals(kml.s_north)){
                northBB=Float.parseFloat(parser.nextText());
            } else if (name.equals(kml.s_south)){
                southBB=Float.parseFloat(parser.nextText());
            } else if (name.equals(kml.s_east)){
                eastBB=Float.parseFloat(parser.nextText());
            } else if (name.equals(kml.s_west)){
                westBB=Float.parseFloat(parser.nextText());
            } else {
                parser.skipTree();
            }

            //#debug
//#             parser.require(XmlPullParser.END_TAG, null, null);
        }
        //#debug
//#         parser.require(XmlPullParser.END_TAG, null, "LatLonAltBox");
    }

    void readMultiGeometry(KXmlParser parser) throws IOException, XmlPullParserException {
        //#debug
//#         parser.require(XmlPullParser.START_TAG, null, "MultiGeometry");
        while (parser.nextTag()!=XmlPullParser.END_TAG) {
            //#debug
//#             parser.require(XmlPullParser.START_TAG, null, null);
            String name=parser.getName().toLowerCase();
            //#debug
//#             System.out.println("MultiGeometry<"+name+">");

            if (name.equals(kml.s_linestring)) {
                readLineString(parser);
            } else {
                parser.skipTree();
            }

            //#debug
//#             parser.require(XmlPullParser.END_TAG, null, null);
        }
        //#debug
//#         parser.require(XmlPullParser.END_TAG, null, "MultiGeometry");
    }

    void readGeometryCollection(KXmlParser parser) throws IOException, XmlPullParserException {
        //#debug
//#         parser.require(XmlPullParser.START_TAG, null, "GeometryCollection");
        while (parser.nextTag()!=XmlPullParser.END_TAG) {
            //#debug
//#             parser.require(XmlPullParser.START_TAG, null, null);
            String name=parser.getName().toLowerCase();
            //#debug
//#             System.out.println("GeometryCollection<"+name+">");

            if (name.equals(kml.s_linestring)) {
                readLineString(parser);
            } else {
                parser.skipTree();
            }

            //#debug
//#             parser.require(XmlPullParser.END_TAG, null, null);
        }
        //#debug
//#         parser.require(XmlPullParser.END_TAG, null, "GeometryCollection");
    }

    void readLineString(KXmlParser parser) throws IOException, XmlPullParserException {
        //#debug
//#         parser.require(XmlPullParser.START_TAG, null, "LineString");
        while (parser.nextTag()!=XmlPullParser.END_TAG) {
            //#debug
//#             parser.require(XmlPullParser.START_TAG, null, null);
            String name=parser.getName().toLowerCase();
            //#debug
//#             System.out.println("LineString<"+name+">");

            if (name.equals(kml.s_coordinates)){
                String coords=parser.nextText();
                try {
                    String[] datac=MapUtil.parseString(' '+coords.trim(), '\n');
                    int length=datac.length;
                    for (int l=0; l<length; l++) {
                        if (datac[l]==null){
                            length=l;
                            break;
                        }
                    }

                    if (length==0){
                        datac=null;
                        // try{
                        datac=MapUtil.parseString(' '+coords.trim(), ' ');
                        //  }catch(Exception e){}

                        length=datac.length;
                        for (int l=0; l<length; l++) {
                            if (datac[l]==null){
                                length=l;
                                break;
                            }
                        }
                    }

                    if (length>0){
                        float[] lc=new float[length+length];
                        String[] data=new String[4];
                        for (int i=0; i<length; i++) {
                            MapUtil.parseString(','+datac[i], ',', data);
                            lc[i+i]=Float.parseFloat(data[0]);
                            lc[i+i+1]=Float.parseFloat(data[1]);
                        }
                        if (lineCoords==null) {
                            lineCoords=lc;
                        } else {
                            float[] old=lineCoords;
                            lineCoords=new float[old.length+lc.length];
                            for (int i=0; i<lineCoords.length; i++) {
                                if (i<old.length) {
                                    lineCoords[i]=old[i];
                                } else {
                                    lineCoords[i]=lc[i-old.length];
                                }
                            }
                        }
                    }
                } catch (Throwable t) {
                    //#mdebug
//#                     if (RMSOption.debugEnabled) {
//#                         DebugLog.add2Log("KMLMP2:"+t.toString());
//#                     }
                    //#enddebug
                }

            } else {
                parser.skipTree();
            }

            //#debug
//#             parser.require(XmlPullParser.END_TAG, null, null);
        }
        //#debug
//#         parser.require(XmlPullParser.END_TAG, null, "LineString");
    }
}
