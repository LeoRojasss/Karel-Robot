import java.util.LinkedList;
import java.util.Queue;

public class magicTren {
    private Queue<Tren> trenes;

    public magicTren() {
        this.trenes = new LinkedList<>();
    }

    // Método para encolar un nuevo tren
    public void encolarTren(Tren nuevoTren) {
        trenes.offer(nuevoTren);
    }

    // Cambia el estado de cada tren para permitir su salida
    public void permitirSalida() {
        trenes.forEach(tren -> tren.cambiarEstadoSalida(false));
    }
}