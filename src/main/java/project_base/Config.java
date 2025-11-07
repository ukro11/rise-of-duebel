package project_base;

/**
 * In dieser Klasse werden globale, statische Einstellungen verwaltet.
 * Die Werte können nach eigenen Wünschen angepasst werden.
 */
public class Config {

    // Titel des Programms (steht oben in der Fenstertitelzeile)
    public final static String WINDOW_TITLE = "Kago4 Project v1.0";

    // Konfiguration des Standardfensters: Anzeige und Breite des Programmfensters (Width) und Höhe des Programmfensters (Height)
    public final static boolean SHOW_DEFAULT_WINDOW = true;
    public final static int WINDOW_WIDTH = 1920;
    public final static int WINDOW_HEIGHT = 1080;   // Effektive Höhe ist etwa 29 Pixel geringer (Titelleiste wird mitgezählt)
    public final static boolean WINDOW_FULLSCREEN = true;

    public final static Environment RUN_ENV = Environment.PRODUCTION;

    // Weitere Optionen für das Projekt
    public final static boolean USE_SOUND = true;

    public enum Environment {
        DEVELOPMENT,
        PRODUCTION
    }
}
