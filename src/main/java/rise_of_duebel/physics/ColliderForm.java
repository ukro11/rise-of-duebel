package rise_of_duebel.physics;

public enum ColliderForm {
    RECTANGLE("rectangle"),
    CIRCLE("circle"),
    POLYGON("polygon");

    private String shape;

    ColliderForm(String shape) {
        this.shape = shape;
    }

    public String getShape() {
        return shape;
    }
}
