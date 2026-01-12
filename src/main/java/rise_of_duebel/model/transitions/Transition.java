package rise_of_duebel.model.transitions;

import KAGO_framework.view.DrawTool;

/**
 * @author Mark
 * @version 1.0
 */
public interface Transition<T> {
    /***
     * Wird gecallt, wenn Transition beginnen soll (one time call)
     * @param before Szene, die jetzt geschlossen werden soll
     */
    void in(T before);

    /***
     * @return Ob die in-Animation schon fertig ist und die Szene schon gewechselt werden soll
     */
    boolean swap();

    /***
     *
     * @return
     */
    /***
     * @return Ob die out-Animation bzw. die Transition insgesamt fertig ist
     */
    boolean finished();

    /***
     * Wird gecallt, wenn Transition enden soll (one time call)
     * @param before Szene, die jetzt geschlossen werden soll
     * @param after Szene, die jetzt geöffnet wird
     */
    void out(T before, T after);

    /***
     * Normale Draw Function (Transition Draw)
     * @param drawTool
     */
    void draw(DrawTool drawTool);
}
