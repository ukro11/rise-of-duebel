package KAGO_framework.view;

import KAGO_framework.control.Drawable;
import KAGO_framework.control.Interactable;
import KAGO_framework.control.ViewController;
import project_base.model.debug.VisualModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Die innere Klasse kapselt jeweils eine Szene.
 * Diese besteht aus einem Panel auf das gezeichnet wird und das Tastatur- und Mauseingaben empfängt.
 * Außerdem gibt es jeweils eine Liste von Objekte, die gezeichnet und aktualisiert werden sollen
 * und eine Liste von Objekten, die über Eingaben informiert werden sollen
 */
public class KagoScene {

    //private DrawingPanel drawingPanel;
    private List<Drawable> drawables;
    private List<VisualModel> visuals;
    private List<Interactable> interactables;

    public KagoScene(ViewController viewController){
        //this.drawingPanel = new DrawingPanel(viewController);
        //viewController.setBackground(new Color(255,255,255));
        this.drawables = new ArrayList<>();
        this.visuals = new ArrayList<>();
        this.interactables = new ArrayList<>();
    }

    /*public DrawingPanel getDrawingPanel() {
        return this.drawingPanel;
    }*/

    public List<Drawable> getDrawables() {
        return this.drawables;
    }

    public List<VisualModel> getVisuals() {
        return visuals;
    }

    public List<Interactable> getInteractables() {
        return this.interactables;
    }
}