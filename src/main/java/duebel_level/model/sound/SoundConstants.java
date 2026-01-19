package duebel_level.model.sound;

public class SoundConstants {

    public final SoundSource SOUND_BACKGROUND;
    public final SoundSource SOUND_JUMP;
    public final SoundSource SOUND_DEATH;
    public final SoundSource SOUND_WIN;

    public SoundConstants() {
        this.SOUND_BACKGROUND = new SoundSource("background", "background.wav");

        this.SOUND_JUMP = new SoundSource("jump", "jump.wav");
        this.SOUND_DEATH = new SoundSource("death", "death.wav");
        this.SOUND_WIN = new SoundSource("win", "win.wav");
    }
}
