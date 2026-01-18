package rise_of_duebel.model.sound;


import javax.sound.sampled.Clip;

public class SoundManager {

    public static void playSound(SoundSource source) {
        SoundManager.playSound(source, 0);
    }

    public static void playSound(SoundSource source, boolean loop) {
        SoundManager.playSound(source, loop ? Clip.LOOP_CONTINUOUSLY : 0);
    }

    public static void playSound(SoundSource source, int loop) {
        if (source != null) {
            source.playSound(loop);
        }
    }

    public static void resumeSound(SoundSource source) {
        if (source != null) {
            source.resumeSound();
        }
    }

    public static void stopSound(SoundSource source) {
        SoundManager.stopSound(source, false);
    }

    public static void stopSound(SoundSource source, boolean destroy) {
        if (source != null) {
            source.stopSound(destroy);
        }
    }
}
