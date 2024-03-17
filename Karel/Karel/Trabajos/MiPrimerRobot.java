import kareltherobot.*;
import java.awt.Color;

public class MiPrimerRobot implements Directions {

	static Color colorMinero = new Color(0,0,0);
	static Color colorTren = new Color(0,0,255);
	static Color colorExtractor = new Color(255,0,0);

    // Valores predeterminados para el número de robots de cada tipo
    private static int numMineros = 2;
    private static int numTrenes = 2;
    private static int numExtractores = 2;

    public static void main(String[] args) {
        // Analiza los argumentos de la línea de comando
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

        // Crear e iniciar los robots según los números especificados
        for (int i = 0; i < numMineros; i++) {
            Racer minero = new Minero(7, 1, East, 0,colorMinero); // Ajusta los parámetros según sea necesario
            new Thread(minero).start();
        }

        for (int i = 0; i < numTrenes; i++) {
            Racer tren = new Tren(11, 13, East, 0,colorTren); // Ajusta los parámetros según sea necesario
            new Thread(tren).start();
        }

        for (int i = 0; i < numExtractores; i++) {
            Racer extractor = new Extractor(1, 2, East, 0, colorExtractor); // Ajusta los parámetros según sea necesario
            new Thread(extractor).start();
        }
    }
}
