/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RPMap;

/**
 *
 * @author rfk
 */
public class SoundNotifier {

    public static final byte SOUND_FORWARD=1;
    public static final byte SOUND_BACKWARD=2;
    public static final byte SOUND_LEFT=3;
    public static final byte SOUND_RIGHT=4;
    public static final byte SOUND_TAKELEFT=5;
    public static final byte SOUND_TAKERIGHT=6;
    public static final byte SOUND_ARRIVE=7;
    private RouteSound forwSound=new RouteSound(RouteSound.SOUND_INTERNAL, SOUND_FORWARD);
    private RouteSound backSound=new RouteSound(RouteSound.SOUND_INTERNAL, SOUND_BACKWARD);
    private RouteSound leftSound=new RouteSound(RouteSound.SOUND_INTERNAL, SOUND_LEFT);
    private RouteSound rightSound=new RouteSound(RouteSound.SOUND_INTERNAL, SOUND_RIGHT);
    private RouteSound takeLeftSound=new RouteSound(RouteSound.SOUND_INTERNAL, SOUND_TAKELEFT);
    private RouteSound takeRightSound=new RouteSound(RouteSound.SOUND_INTERNAL, SOUND_TAKERIGHT);
    private RouteSound arriveSound=new RouteSound(RouteSound.SOUND_INTERNAL, SOUND_ARRIVE);


}
