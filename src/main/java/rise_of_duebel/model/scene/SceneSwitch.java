package rise_of_duebel.model.scene;

import rise_of_duebel.model.transitions.Transition;

public record SceneSwitch(Scene last, Scene next, Transition<Scene> transition) {
    @Override
    public Scene last() {
        return this.last;
    }

    @Override
    public Scene next() {
        return this.next;
    }

    @Override
    public Transition<Scene> transition() {
        return this.transition;
    }
}
