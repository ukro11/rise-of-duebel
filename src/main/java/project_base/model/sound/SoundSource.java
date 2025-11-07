package project_base.model.sound;

import project_base.utils.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundSource {

    private Logger logger = LoggerFactory.getLogger(SoundSource.class);
    private String id;
    private File file;
    private Clip audioClip;
    private boolean isPaused;
    private long clipTimePosition;
    private FloatControl volumeControl;
    private boolean loop;

    public SoundSource(String id, String filename) {
        try {
            this.id = id;
            this.file = new File(SoundSource.class.getResource("/sound/" + filename).getFile());
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(this.file);
            this.audioClip = AudioSystem.getClip();
            this.audioClip.open(audioStream);
            this.isPaused = false;
            this.clipTimePosition = 0;
            this.volumeControl = (FloatControl) this.audioClip.getControl(FloatControl.Type.MASTER_GAIN);
            this.loop = false;

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void playSound(int loop) {
        if (this.audioClip != null) {
            this.audioClip.loop(loop);
            if (this.isPaused) {
                this.resumeSound();

            } else {
                this.audioClip.setFramePosition(0);
                this.audioClip.start();
            }
        }
    }

    public void pauseSound() {
        if (this.audioClip != null && this.audioClip.isRunning()) {
            this.clipTimePosition = this.audioClip.getMicrosecondPosition();
            this.audioClip.stop();
            this.isPaused = true;
        }
    }

    public void resumeSound() {
        if (this.isPaused && this.audioClip != null) {
            this.audioClip.setMicrosecondPosition(this.clipTimePosition);
            this.audioClip.start();
            this.isPaused = false;
        }
    }

    public void stopSound() {
        this.stopSound(false);
    }

    public void stopSound(boolean destroy) {
        if (this.audioClip != null) {
            this.audioClip.stop();
            this.isPaused = false;
            this.clipTimePosition = 0;

            if (destroy) this.destroySound();
        }
    }

    public void destroySound() {
        if (this.audioClip.isOpen()) this.audioClip.close();
    }

    public void setVolume(double volume) {
        if (volume < 0.0 || volume > 1.0) this.logger.warn("Min volume is 0.0 and max volume is 1.0");

        float v = (float) MathUtils.clamp(volume, 0.0f, 1.0f);
        float range = this.volumeControl.getMaximum() - this.volumeControl.getMinimum();
        float gain = (range * v) + this.volumeControl.getMinimum();
        this.volumeControl.setValue(gain);
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public boolean isLoop() {
        return this.loop;
    }

    public boolean isPaused() {
        return this.isPaused;
    }

    public boolean isPlaying() {
        return this.audioClip != null && this.audioClip.isRunning();
    }

    public String getId() {
        return this.id;
    }
}

