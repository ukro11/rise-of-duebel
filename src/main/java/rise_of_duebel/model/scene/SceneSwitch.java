package rise_of_duebel.model.scene;

public record SceneSwitch(Scene last, Scene next, SceneTransition transition) {
    @Override
    public Scene last() {
        return this.last;
    }

    @Override
    public Scene next() {
        return this.next;
    }

    @Override
    public SceneTransition transition() {
        return this.transition;
    }
}
