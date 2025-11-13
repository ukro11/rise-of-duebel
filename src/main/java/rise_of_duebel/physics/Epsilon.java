package rise_of_duebel.physics;

public final class Epsilon {

    public static final double E = Epsilon.compute();

    private Epsilon() {}

    private static double compute() {
        double e = 0.5;
        while (1.0 + e > 1.0) {
            e *= 0.5;
        }
        return e;
    }
}
