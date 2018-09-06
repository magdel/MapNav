/*
 * StringSelectList.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package misc;

import RPMap.MapCanvas;
import java.util.Enumeration;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextField;
import lang.Lang;
import lang.LangHolder;

/**
 *
 * @author RFK
 */
public class StringSelectList extends List implements CommandListener{
  private Displayable backDisp;
  private TextField tf;
  private Enumeration e;
  /** Creates a new instance of StringSelectList */
  public StringSelectList(TextField textField,Displayable backDisplay,Enumeration src,String title) {
    super(title,List.IMPLICIT);
    tf=textField;backDisp=backDisplay;e=src;
    
    backCommand=new Command(LangHolder.getString(Lang.back), Command.BACK, 3);
    openCommand=new Command(LangHolder.getString(Lang.select), Command.ITEM, 1);
    addCommand(backCommand);
    addCommand(openCommand);
    setCommandListener(this);
    setSelectCommand(openCommand);
    while (e.hasMoreElements()) {
      append((String)e.nextElement(),null);
    }
  }
  Command backCommand,openCommand;
  
  public void commandAction(Command command, Displayable displayable) {
    if (displayable == this) {
      if (command == backCommand) {
        MapCanvas.setCurrent(backDisp);
      } else
        if (command == openCommand) {
        try{tf.setString(getString(getSelectedIndex()));}catch(Throwable t){}
        MapCanvas.setCurrent(backDisp);
        }
    }
    
  }
}
