/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RPMap;

import misc.MapSound;

/**
 *
 * @author rfk
 */
public class RouteSound {

    public static final byte SOUND_INTERNAL=1;
    public static final byte SOUND_EXTERNAL=2;

    byte soundSource;

    byte soundKind;

    String soundRes;

    RouteSound(byte soundSource, byte soundKind) {
        this.soundSource=soundSource;
        this.soundKind=soundKind;
        if (soundSource==SOUND_INTERNAL) {
            switch (soundKind) {
                case SoundNotifier.SOUND_BACKWARD:
                    soundRes="/mel/r_back";
                case SoundNotifier.SOUND_FORWARD:
                    soundRes="/mel/r_forw";
                case SoundNotifier.SOUND_LEFT:
                    soundRes="/mel/r_left";
                case SoundNotifier.SOUND_RIGHT:
                    soundRes="/mel/r_right";
            }
        }
    }

    public void setSoundRes(String soundRes) {
        this.soundRes=soundRes;
    }

    public void playSound() {
        if (soundSource==SOUND_INTERNAL){
            MapSound.playCustomSound(MapSound.TYPE_INTERNAL, soundRes);
        }
        if (soundSource==SOUND_EXTERNAL){
            MapSound.playCustomSound(MapSound.TYPE_EXTERNAL, soundRes);
        }
    }
}
