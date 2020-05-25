/*
  Interface: Audible
  Author:    Nathan Roslavker
  Fixes:     Mikhail Kruk
  Purpose:   Signifies that the implementing class is an animation and
  provides basic support for sounds
*/

package scorch;

import java.applet.*;
import java.util.Vector;

import scorch.utility.*;

public abstract class Audible {
    protected static Vector<AudioClip> sounds;

    protected void startSound(int index) {
        if (sounds != null && sounds.size() > index && ScorchApplet.sounds)
            sounds.elementAt(index).play();
    }

    protected void loopSound(int index) {
        if (sounds != null && sounds.size() > index)
            sounds.elementAt(index).loop();
    }

    protected void stopSound(int index) {
        if (sounds != null && sounds.size() > index)
            sounds.elementAt(index).stop();
    }

    protected static int addSound(AudioClip sound) {
        if (sounds == null)
            sounds = new Vector<>();
        sounds.addElement(sound);

        return sounds.indexOf(sound);
    }

    protected static void loadSounds(ScorchApplet owner) {
        Debug.println("empty load sounds call");
        sounds = null;
    }
}
