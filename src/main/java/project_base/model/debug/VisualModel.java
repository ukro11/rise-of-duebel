package project_base.model.debug;

import KAGO_framework.control.ViewController;
import KAGO_framework.view.DrawTool;
import project_base.ProgramController;

public abstract class VisualModel implements Comparable<VisualModel> {

    protected ViewController viewController;
    protected ProgramController programController;
    protected String id;
    protected int zIndex;

    public VisualModel(String id) {
        this(id, 0);
    }

    public VisualModel(String id, int zIndex) {
        this.viewController = ViewController.getInstance();
        this.programController = this.viewController.getProgramController();
        this.id = id;
        this.zIndex = zIndex;
    }

    public String getId() {
        return this.id;
    }

    public int zIndex() {
        return zIndex;
    }

    public abstract void draw(DrawTool drawTool);
    public abstract void update(double dt);

    @Override
    public int compareTo(VisualModel v) {
        return Integer.compare(v.zIndex, this.zIndex);
    }
}
