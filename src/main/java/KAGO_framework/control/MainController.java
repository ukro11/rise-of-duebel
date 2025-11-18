package KAGO_framework.control;

/**
 * Diese Klasse enthält die main-Methode. Von ihr wird als erstes ein Objekt innerhalb der main-Methode erzeugt,
 * die zu Programmstart aufgerufen wird und kein Objekt benötigt, da sie statisch ist.
 * Die erste Methode, die also nach der main-Methode aufgerufen wird, ist der Konstruktor dieser Klasse. Aus ihm
 * wird alles weitere erzeugt.
 * Vorgegebene Klasse des Frameworks. Modifikation auf eigene Gefahr.
 */
public class MainController {

    /**
     * Diese Methode startet das gesamte Framework und erzeugt am Ende dieses Prozesses ein Objekt der Klasse
     * ProgramController aus dem Paket "my_project > control"
     */
    public static void startFramework(){
        new ViewController();
    }
}
