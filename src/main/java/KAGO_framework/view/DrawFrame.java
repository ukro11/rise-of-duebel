package KAGO_framework.view;

import javax.swing.*;
import java.awt.*;

/** Entspricht einem Fenster, das DrawingPanels beinhalten kann.
 *  Vorgegebene Klasse des Frameworks. Modifikation auf eigene Gefahr.
 */
public class DrawFrame extends JFrame {

    // Attribute

    // Referenzen
    private JPanel activePanel;           // Das im Moment sichtbare DrawingPanel
    private Canvas canvas;

    /**
     * Konstruktor
     * @param name Der Titel des Fensters
     * @param x Die obere linke x-Koordinate des Fensters bzgl. des Bildschirms
     * @param y Die obere linke y-Koordinaite des Fensters bzgl. des Bildschirms
     * @param width Die Breite des Fensters
     * @param height Die Höhe des Fensters
     */
    public DrawFrame(String name, int x, int y, int width, int height, JPanel startingPanel) {
        activePanel = startingPanel;
        setLocation(x,y);
        setSize(width,height);
        setTitle(name);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setVisible(true);
    }

    public DrawFrame(String name, int x, int y, int width, int height, Canvas canvas) {
        this.canvas = canvas;
        setLocation(x,y);
        setSize(width,height);
        setTitle(name);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(canvas);
        //setVisible(true);
    }

    /**
     * Ändert das aktuell vom DrawFrame gezeigte DrawingPanel.
     * @param panel Das anzuzeigende Panel.
     */
    public void setActiveDrawingPanel(JPanel panel){
        remove(panel);
        add(panel);
        revalidate();
        activePanel = panel;
    }

    @Override
    /**
     * Gibt die Breite des im Fenster liegenden DrawingPanels zurück
     */
    public int getWidth(){
        return activePanel.getWidth();
    }

    @Override
    /**
     * Gibt die Höhe des im Fenster liegenden DrawingPanels zurück
     */
    public int getHeight(){
        return activePanel.getHeight();
    }

    public Canvas getCanvas() { return canvas; }
}

