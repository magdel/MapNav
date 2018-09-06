/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package RPMap;

/**
 *
 * @author rfk
 */
public class MapPointAction {
  public byte actionType = ACTION_WP_APPROACH;
  public static final byte ACTION_WP_APPROACH = 1;

  public float approachSignalDistance = 0.5f;
  public boolean repeat=true;

  public byte signalType = SIGNAL_VIBRATE|SIGNAL_TONE;
  public static final byte SIGNAL_VIBRATE = 1;
  public static final byte SIGNAL_TONE = 2;
  public static final byte SIGNAL_PLAYFILE = 4;

  public MapPointAction(){

  }
  public MapPointAction(byte actionType){
    this.actionType = actionType;
  }

}
