package project_base.animation;

/* Quelle: easings.net */
public class Easings {

    public static double linear(double x) {
        return x;
    }

    public static double easeInSine(double x) {
        return (double) (1.0 - Math.cos(x * Math.PI / 2.0));
    }

    public static double easeOutSine(double x) {
        return (double) Math.sin(x * Math.PI / 2.0);
    }

    public static double easeInOutSine(double x) {
        return (double) (-(Math.cos(Math.PI * x) - 1.0) / 2.0);
    }

    public static double easeInCubic(double x) {
        return x * x * x;
    }

    public static double easeOutCubic(double x) {
        return (double) (1.0 - Math.pow(1.0 - x, 3.0));
    }

    public static double easeInOutCubic(double x) {
        return (double) (x < 0.5 ? 4.0 * x * x * x : 1.0 - Math.pow(-2.0 * x + 2.0, 3.0) / 2.0);
    }

    public static double easeInQuint(double x) {
        return x * x * x * x * x;
    }

    public static double easeOutQuint(double x) {
        return (double) (1.0 - Math.pow(1.0 - x, 5.0));
    }

    public static double easeInOutQuint(double x) {
        return x < 0.5 ? 16.0f * x * x * x * x * x : 1.0f - (double) Math.pow(-2.0 * x + 2.0, 5.0) / 2.0f;
    }

    public static double easeInQuad(double x) {
        return x * x;
    }

    public static double easeOutQuad(double x) {
        return 1.0f - (1.0f - x) * (1.0f - x);
    }

    public static double easeInOutQuad(double x) {
        return x < 0.5 ? 2.0f * x * x : 1.0f - (double) Math.pow(-2.0 * x + 2.0, 2.0) / 2.0f;
    }

    public static double easeInQuart(double x) {
        return x * x * x * x;
    }

    public static double easeOutQuart(double x) {
        return 1.0f - (double) Math.pow(1.0 - x, 4.0);
    }

    public static double easeInOutQuart(double x) {
        return x < 0.5 ? 8.0f * x * x * x * x : 1.0f - (double) Math.pow(-2.0 * x + 2.0, 4.0) / 2.0f;
    }

    public static double easeInExpo(double x) {
        return x == 0.0f ? 0.0f : (double) Math.pow(2.0, 10.0 * (x - 1.0));
    }

    public static double easeOutExpo(double x) {
        return x == 1.0f ? 1.0f : (double) (-Math.pow(2.0, -10.0 * x) + 1.0);
    }

    public static double easeInOutExpo(double x) {
        if (x == 0.0f) return 0.0f;
        if (x == 1.0f) return 1.0f;
        return x < 0.5f ? (double) (Math.pow(2.0, 20.0 * x - 10.0) / 2.0) : (double) (2.0 - Math.pow(2.0, -20.0 * x + 10.0) / 2.0);
    }

    public static double easeInElastic(double x) {
        return x == 0.0f ? 0.0f : x == 1.0f ? 1.0f : (double) (-Math.pow(2.0, 10.0 * x - 10.0) * Math.sin((x * 10.0 - 10.75) * (2.0 * Math.PI) / 3.0));
    }

    public static double easeOutElastic(double x) {
        return x == 0.0f ? 0.0f : x == 1.0f ? 1.0f : (double) (Math.pow(2.0, -10.0 * x) * Math.sin((x * 10.0 - 0.75) * (2.0 * Math.PI) / 3.0) + 1.0);
    }

    public static double easeInOutElastic(double x) {
        if (x == 0.0f) return 0.0f;
        if (x == 1.0f) return 1.0f;
        return x < 0.5f ?
                (double) (-Math.pow(2.0, 20.0 * x - 10.0) * Math.sin((20.0 * x - 11.125) * (2.0 * Math.PI) / 4.5) / 2.0) :
                (double) (Math.pow(2.0, -20.0 * x + 10.0) * Math.sin((20.0 * x - 11.125) * (2.0 * Math.PI) / 4.5) / 2.0 + 1.0);
    }
}
