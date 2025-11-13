package rise_of_duebel.model.sound;

public class SoundConstants {

    public final SoundSource SOUND_BACKGROUND;
    public final SoundSource[] SOUND_FRYING = new SoundSource[2];
    public final SoundSource[] SOUND_CUTTING = new SoundSource[2];
    public final SoundSource SOUND_PICKUP;
    public final SoundSource SOUND_TRASH;
    public final SoundSource SOUND_REWARD;

    public SoundConstants() {
        this.SOUND_BACKGROUND = new SoundSource("background", "background.wav");

        this.SOUND_FRYING[0] = new SoundSource("frying", "frying.wav");
        this.SOUND_FRYING[0].setVolume(0.8);
        this.SOUND_FRYING[1] = new SoundSource("frying", "frying.wav");
        this.SOUND_FRYING[1].setVolume(0.8);

        this.SOUND_CUTTING[0] = new SoundSource("cutting", "cutting.wav");
        this.SOUND_CUTTING[0].setVolume(0.8);
        this.SOUND_CUTTING[1] = new SoundSource("cutting", "cutting.wav");
        this.SOUND_CUTTING[1].setVolume(0.8);

        this.SOUND_PICKUP = new SoundSource("pick-up", "plop.wav");
        this.SOUND_PICKUP.setVolume(0.8);

        this.SOUND_TRASH = new SoundSource("trash", "trash.wav");
        this.SOUND_TRASH.setVolume(0.8);

        this.SOUND_REWARD = new SoundSource("reward", "reward.wav");
        this.SOUND_REWARD.setVolume(0.8);
    }
}
