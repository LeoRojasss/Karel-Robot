import kareltherobot.*;
import java.awt.Color;
import java.util.concurrent.Semaphore;

public class MiPrimerRobot implements Directions {

    static Color colorMinero = new Color(0, 0, 0);
    static Color colorTren = new Color(0, 0, 255);
    static Color colorExtractor = new Color(255, 0, 0);

    private static int numMineros = 2; // Valor predeterminado
    private static int numTrenes = 0; // Valor predeterminado
    private static int numExtractores = 0; // Valor predeterminado

    // Semáforo para controlar el acceso a la entrada de la mina
    private static final Semaphore controlAccesoMina = new Semaphore(1);

    public static void main(String[] args) {
        // Analizar argumentos de la línea de comando
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-m":
                    numMineros = Integer.parseInt(args[++i]);
                    break;
                case "-t":
                    numTrenes = Integer.parseInt(args[++i]);
                    break;
                case "-e":
                    numExtractores = Integer.parseInt(args[++i]);
                    break;
                default:
                    System.out.println("Argumento no reconocido: " + args[i]);
            }
        }

        // Configuración inicial del mundo
        World.readWorld("Mundo.kwld");
        World.setVisible(true);
        World.setDelay(20);

        // Calle inicial para cada tipo de robot
        int calleInicialMinero = 10;
        int calleInicialTren = 12; // Incrementamos la calle para los trenes
        int calleInicialExtractor = 14; // Incrementamos aún más para los extractores

        // Crear e iniciar robots fuera de la mina
        iniciarRobots("Minero", numMineros, colorMinero, calleInicialMinero);
        iniciarRobots("Tren", numTrenes, colorTren, calleInicialTren);
        iniciarRobots("Extractor", numExtractores, colorExtractor, calleInicialExtractor);
    }

    private static void iniciarRobots(String tipo, int cantidad, Color color, int calleInicial) {
        for (int i = 0; i < cantidad; i++) {
            switch (tipo) {
                case "Minero":
                    // Mineros en la calle 10
                    new Thread(new Minero(calleInicial, 1 + i, East, 0, color)).start();
                    break;
                case "Tren":
                    // Trenes en la calle 12
                    new Thread(new Tren(calleInicial, 1 + i, East, 0, color)).start();
                    break;
                case "Extractor":
                    // Extractores en la calle 14
                    new Thread(new Extractor(calleInicial, 1 + i, East, 0, color)).start();
                    break;
                default:
                    throw new IllegalArgumentException("Tipo de robot no reconocido.");
            }
        }
    }
}