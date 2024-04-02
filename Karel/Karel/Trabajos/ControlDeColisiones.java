import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class ControlDeColisiones {
    public static final Map<String, Semaphore> semaforosPorPosicion = new HashMap<>();

    public static synchronized void inicializarPosicion(int x, int y) {
        String clave = posicionClave(x, y);
        semaforosPorPosicion.putIfAbsent(clave, new Semaphore(1));
    }

    public static synchronized Semaphore obtenerSemaforo(int x, int y) {
        String clave = posicionClave(x, y);
        // Asegura que siempre se devuelva un semáforo no nulo
        semaforosPorPosicion.putIfAbsent(clave, new Semaphore(1));
        return semaforosPorPosicion.get(clave);
    }

    public static String posicionClave(int x, int y) {
        return x + "," + y;
    }
}
