package rise_of_duebel.animation.tween;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rise_of_duebel.animation.Easings;
import rise_of_duebel.animation.tween.value.Tween2d;
import rise_of_duebel.animation.tween.value.Tween2i;
import rise_of_duebel.animation.tween.value.TweenValue;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

/***
 * @author Mark
 */
public class Tween<T extends Number, Q extends TweenValue> {

    private static CopyOnWriteArrayList<Tween<?, ?>> tweens = new CopyOnWriteArrayList<>();

    private final Logger logger = LoggerFactory.getLogger(Tween.class);

    protected Q value;
    protected T target;
    protected TweenDirection direction;
    protected double duration;
    protected double delay;
    protected boolean loop;
    protected boolean stayAlive;
    protected Function<Double, Double> easing;

    protected T start;
    protected double elapsed;
    protected double delayElapsed;
    protected boolean running;
    protected boolean paused;
    private boolean delayFinished;

    protected Consumer<Tween<T, Q>> onStart;
    protected Consumer<Tween<T, Q>> onStop;
    protected Consumer<Tween<T, Q>> onPause;
    protected Consumer<Tween<T, Q>> onResume;
    protected Consumer<Tween<T, Q>> onUpdate;
    protected Consumer<Tween<T, Q>> onFinish;

    protected Tween<?, ?> after;

    /**
     * Erstellt einen Tween zwischen value und target.
     * Unterstützt nur Double und Integer.
     *
     * @param value Startwert
     * @param target Zielwert
     * @param duration Dauer
     */
    private Tween(T value, T target, double duration) {
        this.start = value;
        this.target = target;
        this.duration = duration;
        this.easing = (x) -> Easings.linear(x);
        this.elapsed = 0;
        this.running = false;
        this.stayAlive = false;
        this.delayFinished = true;
        this.direction = TweenDirection.DEFAULT;

        if (value instanceof Double) {
            this.value = (Q) new Tween2d((double) value, (double) target, this.direction);

        } else if (value instanceof Integer) {
            this.value = (Q) new Tween2i((int) value, (int) target, this.direction);

        } else {
            this.logger.warn("Tween only supports doubles and integers");
        }
    }

    /**
     * Factory-Methode für einen Tween.
     *
     * @param value Startwert
     * @param target Zielwert
     * @param duration Dauer
     * @param <T> Number-Typ
     * @param <Q> TweenValue-Typ
     * @return neuer Tween
     */
    public static <T extends Number, Q extends TweenValue> Tween<T, Q> to(T value, T target, double duration) {
        return new Tween<T, Q>(value, target, duration);
    }

    /**
     * Aktualisiert den Tween (Delay, Interpolation, Callbacks, Loop/Finish).
     *
     * @param dt delta time
     */
    public void update(double dt) {
        if (this.value == null) return;

        if (this.isRunning() && !this.isPaused()) {
            if (!this.delayFinished) {
                this.delayElapsed += dt;
                this.delayElapsed = Math.min(this.delayElapsed, this.delay);
                if (this.delayElapsed >= this.delay) {
                    this.delayFinished = true;
                }
                return;
            }
            this.elapsed += dt;
            this.elapsed = Math.min(this.elapsed, this.duration);

            this.value.animateValue(this.easing, this.elapsed, this.duration);
            if (this.onUpdate != null) this.onUpdate.accept(this);

            if (this.isFinished()) {
                if (this.isLoop()) {
                    this.elapsed = 0;

                } else {
                    if (!this.stayAlive) {
                        this.running = false;
                        Tween.tweens.remove(this);
                        if (this.after != null) this.after.animate();

                    } else if (this.after != null) {
                        this.logger.warn("Tween \"after\" function will not be called because your tween should stay alive");
                    }
                }
                if (this.onFinish != null) this.onFinish.accept(this);
            }
        }
    }

    /** Startet den Tween (reset + in globale Liste eintragen). */
    public void animate() {
        this.reset();
        this.running = true;
        if (!Tween.tweens.contains(this)) Tween.tweens.add(this);
        if (this.onStart != null) this.onStart.accept(this);
    }

    /** Stoppt den Tween ohne Finish-Callback. */
    public void stop() {
        this.stop(false);
    }

    /**
     * Stoppt den Tween und setzt ihn zurück.
     *
     * @param callFinish true, wenn onFinish zusätzlich ausgeführt werden soll
     */
    public void stop(boolean callFinish) {
        this.running = false;
        this.reset();
        if (this.onStop != null) this.onStop.accept(this);
        if (this.onFinish != null && callFinish) this.onFinish.accept(this);
    }

    /**
     * Setzt die Easing-Funktion.
     *
     * @param easing easing
     * @return this
     */
    public Tween<T, Q> ease(Function<Double, Double> easing) {
        this.easing = easing;
        return this;
    }

    /**
     * Setzt die Dauer.
     *
     * @param duration Dauer
     * @return this
     */
    public Tween<T, Q> duration(double duration) {
        this.duration = duration;
        return this;
    }

    /**
     * Setzt die Richtung.
     *
     * @param direction Direction
     * @return this
     */
    public Tween<T, Q> direction(TweenDirection direction) {
        this.direction = direction;
        return this;
    }

    /**
     * Setzt eine Startverzögerung.
     *
     * @param delay Delay
     * @return this
     */
    public Tween<T, Q> delay(double delay) {
        this.delay = delay;
        if (delay > 0) {
            this.delayFinished = false;
        }
        return this;
    }

    /**
     * Aktiviert/deaktiviert Loop.
     *
     * @param loop Loop-Flag
     * @return this
     */
    public Tween<T, Q> loop(boolean loop) {
        this.loop = loop;
        return this;
    }

    /**
     * Steuert, ob der Tween nach Finish in der Liste bleibt.
     *
     * @param stayAlive Flag
     * @return this
     */
    public Tween<T, Q> stayAlive(boolean stayAlive) {
        this.stayAlive = stayAlive;
        return this;
    }

    /**
     * Setzt Pause zurück.
     *
     * @return this
     */
    public Tween<T, Q> resume() {
        this.paused = false;
        if (this.onResume != null) this.onResume.accept(this);
        return this;
    }

    /**
     * Pausiert den Tween.
     *
     * @return this
     */
    public Tween<T, Q> pause() {
        this.paused = true;
        if (this.onPause != null) this.onPause.accept(this);
        return this;
    }

    /**
     * Setzt Wert intern zurück auf start/target und setzt elapsed auf 0.
     *
     * @return this
     */
    public Tween<T, Q> reset() {
        if (this.start instanceof Double) {
            this.value = (Q) new Tween2d((double) this.start, (double) this.target, this.direction);

        } else if (this.start instanceof Integer) {
            this.value = (Q) new Tween2i((int) this.start, (int) this.target, this.direction);
        }
        this.elapsed = 0;
        return this;
    }

    /**
     * Setzt den aktuellen Wert direkt.
     *
     * @param value neuer Wert
     * @return this
     */
    public Tween<T, Q> set(T value) {
        this.value.setValue(value);
        return this;
    }

    /**
     * Konfiguriert den Tween neu (Start/Ziel/Dauer) und erstellt den Value-Adapter neu.
     *
     * @param value neuer Startwert
     * @param target neues Ziel
     * @param duration neue Dauer
     * @return this
     */
    public Tween<T, Q> redo(T value, T target, double duration) {
        this.start = value;
        this.target = target;
        this.duration = duration;
        this.elapsed = 0;
        this.running = false;

        if (value instanceof Double) {
            this.value = (Q) new Tween2d((double) value, (double) target, this.direction);

        } else if (value instanceof Integer) {
            this.value = (Q) new Tween2i((int) value, (int) target, this.direction);

        } else {
            this.logger.warn("Tween only supports doubles and integers");
        }
        return this;
    }

    /**
     * Setzt Start-Callback.
     *
     * @param onStart Callback
     * @return this
     */
    public Tween<T, Q> onStart(Consumer<Tween<T, Q>> onStart) {
        this.onStart = onStart;
        return this;
    }

    /**
     * Setzt Stop-Callback.
     *
     * @param onStop Callback
     * @return this
     */
    public Tween<T, Q> onStop(Consumer<Tween<T, Q>> onStop) {
        this.onStop = onStop;
        return this;
    }

    /**
     * Setzt Resume-Callback.
     *
     * @param onResume Callback
     * @return this
     */
    public Tween<T, Q> onResume(Consumer<Tween<T, Q>> onResume) {
        this.onResume = onResume;
        return this;
    }

    /**
     * Setzt Pause-Callback.
     *
     * @param onPause Callback
     * @return this
     */
    public Tween<T, Q> onPause(Consumer<Tween<T, Q>> onPause) {
        this.onPause = onPause;
        return this;
    }

    /**
     * Setzt Update-Callback.
     *
     * @param onUpdate Callback
     * @return this
     */
    public Tween<T, Q> onUpdate(Consumer<Tween<T, Q>> onUpdate) {
        this.onUpdate = onUpdate;
        return this;
    }

    /**
     * Setzt Finish-Callback.
     *
     * @param onFinish Callback
     * @return this
     */
    public Tween<T, Q> onFinish(Consumer<Tween<T, Q>> onFinish) {
        this.onFinish = onFinish;
        return this;
    }

    /**
     * @return true, wenn Tween läuft
     */
    public boolean isRunning() {
        return this.running;
    }

    /**
     * @return true, wenn Loop aktiv ist
     */
    public boolean isLoop() {
        return this.loop;
    }

    /**
     * @return Dauer
     */
    public double getDuration() {
        return this.duration;
    }

    /**
     * @return Delay
     */
    public double getDelay() {
        return this.delay;
    }

    /**
     * @return aktueller TweenValue-Adapter
     */
    public TweenValue getTweenValue() {
        return this.value;
    }

    /**
     * @return aktueller Wert (Number)
     */
    public T getValue() {
        return (T) this.value.getValue();
    }

    /**
     * @return aktueller Wert als double
     */
    public double getValueDouble() {
        return (double) this.value.getValue();
    }

    /**
     * @return aktueller Wert als int
     */
    public int getValueInt() {
        return (int) this.value.getValue();
    }

    /**
     * @return true, wenn elapsed >= duration
     */
    public boolean isFinished() {
        return this.elapsed >= this.duration;
    }

    /**
     * @return true, wenn pausiert
     */
    public boolean isPaused() {
        return this.paused;
    }

    /**
     * Aktualisiert alle registrierten Tweens.
     *
     * @param dt delta time
     */
    public static void updateAll(double dt) {
        for (Tween<?, ?> tween : Tween.tweens) {
            tween.update(dt);
            if (tween.isFinished() && !tween.isLoop()) {
                Tween.tweens.remove(tween);
            }
        }
    }

    /**
     * @return Debug-String des Tweens
     */
    @Override
    public String toString() {
        return "Tween{" +
                "value=" + value +
                ", start=" + start +
                ", target=" + target +
                ", duration=" + duration +
                ", delay=" + delay +
                ", loop=" + loop +
                ", stayAlive=" + stayAlive +
                ", easing=" + easing +
                ", elapsed=" + elapsed +
                ", running=" + running +
                '}';
    }

    public enum TweenDirection {
        DEFAULT,
        //REVERSE,
        //ALTERNATE,
        //ALTERNATE_REVERSE
    }
}
