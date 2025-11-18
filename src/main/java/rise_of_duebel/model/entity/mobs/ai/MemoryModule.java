package rise_of_duebel.model.entity.mobs.ai;

import rise_of_duebel.Wrapper;

public class MemoryModule<T> {

    private double timestamp;
    private final T value;
    private final double expiry;
    private final boolean permanent;

    private MemoryModule(T value) {
        this(value, -1);
    }

    private MemoryModule(T value, double expiry) {
        this.timestamp = Wrapper.getTimer().getRunningTime();
        this.value = value;
        this.expiry = expiry;
        this.permanent = expiry < 0;
    }

    public static <T> MemoryModule<T> permanent(T value) {
        return new MemoryModule<>(value, Double.MAX_VALUE);
    }

    public static <T> MemoryModule<T> timed(T value, double expiry) {
        return new MemoryModule<>(value, expiry);
    }

    public boolean isPermanent() {
        return this.expiry == Double.MAX_VALUE;
    }

    public boolean isExpired() {
        if (this.permanent) return false;
        return this.timestamp + this.expiry <= Wrapper.getTimer().getRunningTime();
    }

    public T getValue() {
        return this.value;
    }

    public double getExpiry() {
        return this.expiry;
    }
}
