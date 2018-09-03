/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package misc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.lcdui.Item;

/**
 *
 * @author rfk
 */
public interface ProgressReadWritable extends ProgressStoppable {
 
  public void writeData(OutputStream os, Item[] items) throws IOException;
  public void readData(InputStream is, Item[] items) throws IOException;
  
}
