/*
 * MarkList.java
 *
 * Created on 15 ������ 2008 �., 15:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package misc;

import RPMap.MapCanvas;
import RPMap.MapMark;
import RPMap.MapUtil;
import RPMap.RMSOption;
import RPMap.RMSSettings;
import app.MapForms;
import gpspack.GPSReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import lang.Lang;
import lang.LangHolder;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;

/**
 *
 * @author Raev
 */
public class MarkList extends List implements CommandListener, ProgressReadWritable {

    private static MarkList markList;
    MVector marks;
    boolean select2Nav;

    /** Creates a new instance of MarkList */
    public MarkList(MVector marks, boolean nav2) {
        super(LangHolder.getString(Lang.marks), List.IMPLICIT);

        this.marks=marks;
        select2Nav=nav2;
        markList=this;

        saveCommand=new Command(LangHolder.getString(Lang.save), Command.ITEM, 5);
        backCommand=new Command(LangHolder.getString(Lang.back), Command.BACK, 10);
        yesCommand=new Command(LangHolder.getString(Lang.yes), Command.OK, 1);
        noCommand=new Command(LangHolder.getString(Lang.no), Command.CANCEL, 2);
        wptCommand=new Command(LangHolder.getString(Lang.waypoints), Command.ITEM, 3);
        shCommand=new Command((RMSOption.showMarks)?LangHolder.getString(Lang.hide):LangHolder.getString(Lang.show), Command.ITEM, 2);

        loadCommand=new Command(LangHolder.getString(Lang.load), Command.ITEM, 4);

        addCommand(backCommand);
        addCommand(shCommand);
        addCommand(wptCommand);
        addCommand(saveCommand);
        addCommand(loadCommand);

        setCommandListener(this);
        fillMarkList();
    }
//  public MarkList(){
//    super(MapUtil.emptyString,List.IMPLICIT);
//  }
    Command saveCommand, backCommand, yesCommand, noCommand, wptCommand, shCommand;
    Command loadCommand;
    private List listCommands;

    private List get_listCommands() {
        if (listCommands==null){
            listCommands=new List(LangHolder.getString(Lang.action), List.IMPLICIT);
            listCommands.append(LangHolder.getString(Lang.goto_), null);
            listCommands.append(LangHolder.getString(Lang.nav2u), null);
            listCommands.append(LangHolder.getString(Lang.edit), null);
            listCommands.append(LangHolder.getString(Lang.delete), null);
            listCommands.append(LangHolder.getString(Lang.recordvoice), null);
            listCommands.append(LangHolder.getString(Lang.play), null);

            listCommands.addCommand(backCommand);
            listCommands.setCommandListener(this);
        }
        return listCommands;
    }
    private Form formEdit;
    private TextField textName;
    private TextField textLat;
    private TextField textLon;
    private Gauge gaugeLevel;
    private MapMark selectedMark;

    private Form get_formEdit() {
        formEdit=null;
        if (formEdit==null){
            formEdit=new Form(LangHolder.getString(Lang.addmark));
            textName=new TextField(LangHolder.getString(Lang.spmarkname), selectedMark.name, 100, TextField.ANY);
            formEdit.append(textName);
            gaugeLevel=new Gauge(LangHolder.getString(Lang.level), true, 18, selectedMark.level);
            formEdit.append(gaugeLevel);

            textLat=new TextField(LangHolder.getString(Lang.latitude), null, 12, TextField.NON_PREDICTIVE);
            formEdit.append(textLat);
            textLon=new TextField(LangHolder.getString(Lang.longitude), null, 12, TextField.NON_PREDICTIVE);
            formEdit.append(textLon);

            textLat.setString(MapUtil.coord2EditString(selectedMark.lat, MapUtil.CLATTYPE, RMSOption.coordType));
            textLon.setString(MapUtil.coord2EditString(selectedMark.lon, MapUtil.CLONTYPE, RMSOption.coordType));

            StringItem si=new StringItem(LangHolder.getString(Lang.example)+"\n", MapUtil.emptyString);
            if (RMSOption.coordType==RMSOption.COORDMINSECTYPE){
                si.setText("60 23 41.0\n(GG MM SS.S)");
            } else if (RMSOption.coordType==RMSOption.COORDMINMMMTYPE){
                si.setText("60 23.683\n(GG MM.MMM)");
            } else if (RMSOption.coordType==RMSOption.COORDGGGGGGTYPE){
                si.setText("60.39471\n(GG.GGGGG)");
            }
            formEdit.append(si);

            formEdit.addCommand(saveCommand);
            formEdit.addCommand(backCommand);
            formEdit.setCommandListener(this);
        }
        return formEdit;
    }

    private Form get_formConfirm() {
        formEdit=null;
        if (formEdit==null){
            formEdit=new Form(LangHolder.getString(Lang.delete));

            StringItem si=new StringItem(LangHolder.getString(Lang.deleteall)+"?\n", MapUtil.emptyString);
            formEdit.append(si);

            formEdit.addCommand(yesCommand);
            formEdit.addCommand(noCommand);
            formEdit.setCommandListener(this);
        }
        return formEdit;
    }

    private void saveMark() {
        double lat, lon;
        try {
            lat=MapUtil.parseCoord(textLat.getString(), RMSOption.coordType);
        } catch (Throwable t) {
            MapCanvas.showmsg(LangHolder.getString(Lang.attention), LangHolder.getString(Lang.followf)+"\n"+LangHolder.getString(Lang.latitude), AlertType.ERROR, this);
            return;
        }
        try {
            lon=MapUtil.parseCoord(textLon.getString(), RMSOption.coordType);
        } catch (Throwable t) {
            MapCanvas.showmsg(LangHolder.getString(Lang.attention), LangHolder.getString(Lang.followf)+"\n"+LangHolder.getString(Lang.longitude), AlertType.ERROR, this);
            return;
        }

        selectedMark.lat=lat;
        selectedMark.lon=lon;
        selectedMark.name=textName.getString();
        selectedMark.level=gaugeLevel.getValue();
    }

    public void commandAction(Command command, Displayable displayable) {
        if (displayable==formEdit){
            if (command==saveCommand){
                saveMark();
                if (marks.contains(selectedMark)){
                    marks.setElementAt(selectedMark, marks.indexOf(selectedMark));
                } else {
                    marks.addElement(selectedMark);
                }
                MapCanvas.map.rmss.saveMapMark(selectedMark);
                if (addDialog){
                    MapCanvas.setCurrentMap();
                    markList=null;
                } else {
                    fillMarkList();
                    MapCanvas.setCurrent(markList);
                }
            }
            if (command==backCommand){
                if (addDialog){
                    MapCanvas.setCurrentMap();
                    markList=null;
                } else {
                    MapCanvas.setCurrent(markList);
                }
            } else if (command==yesCommand){
                MapCanvas.map.deleteAllMarks();
                MapCanvas.map.showSaveMarkForm(select2Nav);
            } else if (command==noCommand){
                MapCanvas.setCurrent(markList);
            }
        } else if (displayable==listCommands){
            if (command==backCommand){
                MapCanvas.setCurrent(markList);
            } else if (command==List.SELECT_COMMAND){
                int i=listCommands.getSelectedIndex();
                //������� �������� ��� ������
                switch (i) {
                    case 0://�������
                        MapCanvas.map.setLocation(selectedMark.lat, selectedMark.lon, selectedMark.level);
                        MapCanvas.setCurrentMap();
                        markList=null;
                        break;
                    case 1://���������
                        MapCanvas.map.navigate2mark(selectedMark);
                        MapCanvas.setCurrentMap();
                        markList=null;
                        break;
                    case 2://������
                        MapCanvas.setCurrent(get_formEdit());
                        break;
                    case 3://�������
                        MapCanvas.map.rmss.deleteMapMark(selectedMark);
                        marks.removeElement(selectedMark);
                        fillMarkList();
                        MapCanvas.setCurrent(markList);
                        break;
                    case 4://record
                        new SoundRecorder(selectedMark, displayable, this);
                        break;
                    case 5://play
                        if (selectedMark.hasSound){
                            byte[] sound=RMSSettings.loadGeoData(selectedMark.rId, RMSSettings.GEODATA_MAPMARK);
                            String format=RMSSettings.loadGeoInfo(selectedMark.rId, RMSSettings.GEODATA_MAPMARK);
                            new MapSound(sound, format);
                        } else {
                            MapCanvas.showmsg("Empty", "First record then play", AlertType.INFO, displayable);
                        }
                        break;
                }
            }
        } else if (displayable==this){
            if (command==saveCommand){
                FileDialog.showSaveForm(LangHolder.getString(Lang.marks), new Item[0], this, this, ".MARKS");
            } else if (command==loadCommand){
                //Item[] its=new Item[0];
                FileDialog.showLoadForm(LangHolder.getString(Lang.marks), new Item[0], this, this, ".MARKS");
            } else if (command==wptCommand){
                MapForms.mm.copyM2WPT();
                markList=null;
            } else if (command==shCommand){
                RMSOption.showMarks=!RMSOption.showMarks;
                MapCanvas.setCurrentMap();
                markList=null;
            } else if (command==backCommand){
                MapCanvas.setCurrentMap();
                markList=null;
            } else if (command==List.SELECT_COMMAND){
                //������� ����� � ������
                int i=getSelectedIndex();
                if (select2Nav){
                    MapMark mm=(MapMark) marks.elementAt(i);
                    MapCanvas.map.navigate2mark(mm);
                    MapCanvas.setCurrentMap();
                    markList=null;
                } else {
                    if (i==0){
                        selectedMark=new MapMark((MapCanvas.gpsBinded)?GPSReader.LATITUDE:MapCanvas.reallat,
                          (MapCanvas.gpsBinded)?GPSReader.LONGITUDE:MapCanvas.reallon,
                          MapCanvas.map.level, MapUtil.trackNameAuto(), 0);
                        MapCanvas.setCurrent(get_formEdit());
                    } else if (i==size()-1){
                        MapCanvas.setCurrent(get_formConfirm());
                    } else {
                        //open list commands
                        selectedMark=(MapMark) marks.elementAt(i-1);
                        MapCanvas.setCurrent(get_listCommands());
                    }
                }
            }
        }
    }

    void fillMarkList() {
        deleteAll();
        if (!select2Nav){
            append(LangHolder.getString(Lang.addmark), null);
        }
        for (int i=0; i<marks.size(); i++) {
            append(((MapMark) marks.elementAt(i)).name, null);
        }
        if (!select2Nav){
            append(LangHolder.getString(Lang.deleteall), null);
        }
    }
    private boolean addDialog;

    public void showAddMarkForm() {
        addDialog=true;
        selectedMark=new MapMark((MapCanvas.gpsBinded)?GPSReader.LATITUDE:MapCanvas.reallat,
          (MapCanvas.gpsBinded)?GPSReader.LONGITUDE:MapCanvas.reallon,
          MapCanvas.map.level, MapUtil.trackNameAuto(), 0);
        MapCanvas.setCurrent(get_formEdit());
    }
    ProgressResponse progressResponse;

    public void writeData(OutputStream os, Item[] items) throws IOException {
        try {
            Util.writeStr2OS(os, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");//<kml xmlns=\"http://earth.google.com/kml/2.0\">\r\n<Folder>\r\n<name>");
            Util.writeStr2OS(os, "<marks>");
            MapMark mm;
            for (int i=0; i<marks.size(); i++) {
                Util.writeStr2OS(os, "<mark>");
                mm=(MapMark) marks.elementAt(i);
                progressResponse.setProgress((byte) (100*i/marks.size()), "saving "+mm.name);
                Util.writeStr2OS(os, "<name>"+mm.name+"</name>");
                Util.writeStr2OS(os, "<lat>"+MapUtil.coordRound5(mm.lat)+"</lat>");
                Util.writeStr2OS(os, "<lon>"+MapUtil.coordRound5(mm.lon)+"</lon>");
                Util.writeStr2OS(os, "<zoom>"+mm.level+"</zoom>");
                if (mm.hasSound){
                    Util.writeStr2OS(os, "<soundFormat>"+RMSSettings.loadGeoInfo(mm.rId, RMSSettings.GEODATA_MAPMARK)+"</soundFormat>");
                    Util.writeStr2OS(os, "<soundData>");
                    os.write(Util.encodeBase64(RMSSettings.loadGeoData(mm.rId, RMSSettings.GEODATA_MAPMARK)).getBytes());
                    Util.writeStr2OS(os, "</soundData>");
                }
                Util.writeStr2OS(os, "</mark>");
            }
            Util.writeStr2OS(os, "</marks>");
        } catch (Throwable t) {
        }
        progressResponse=null;
    }

    public void readData(InputStream is, Item[] items) throws IOException {
        try {
            KXmlParser parser=new KXmlParser();
            parser.setInput(is, null);

            parser.nextTag();
            //#debug
            parser.require(XmlPullParser.START_TAG, null, "marks");
            try {
                String mark="mark";
                while (parser.nextTag()!=XmlPullParser.END_TAG) {
                    //#debug
                    parser.require(XmlPullParser.START_TAG, null, null);

                    String name=parser.getName().toLowerCase();

                    if (name.equals(mark)){
                        MapMark mm=new MapMark(parser);
                        MapCanvas.map.rmss.saveMapMark(mm);
                        if (mm.hasSound){
                            RMSSettings.saveGeoData(mm.rId, mm.soundData, RMSSettings.GEODATA_MAPMARK);
                            RMSSettings.saveGeoInfo(mm.rId, mm.soundFormat, RMSSettings.GEODATA_MAPMARK);
                        }
                        marks.addElement(mm);
                    } else {
                        parser.skipTree();          //#debug
                    }
                    parser.require(XmlPullParser.END_TAG, null, null);
                }

            } finally {
                fillMarkList();
            }
        } catch (Throwable t) {
        }
        progressResponse=null;
    }

    public void setProgressResponse(ProgressResponse progressResponse) {
        this.progressResponse=progressResponse;
    }

    public boolean stopIt() {
        progressResponse=null;
        return true;

    }
}
