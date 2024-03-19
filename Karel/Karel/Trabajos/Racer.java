import kareltherobot.*;
import java.awt.Color;
import java.util.concurrent.Semaphore;

public class Racer extends Robot implements Runnable {
    protected Semaphore semaforoAcceso;
    public Racer(int street, int avenue, Direction direction, int beepers, Color colorRobot, Semaphore semaforo) {
        super(street, avenue, direction, beepers,colorRobot);     
        this.semaforoAcceso = semaforo;
    }

    @Override
    public void run() {
        // Método run para ser sobrescrito por subclases
    }
}

class Minero extends Racer {
    public Minero(int street, int avenue, Direction direction, int beepers, Color colorRobot, Semaphore semaforo) {
        super(street, avenue, direction, beepers, colorRobot, semaforo);
    }

    @Override
    public void run() {
        // Lógica específica del minero
    }
}

class Tren extends Racer {
    public Tren(int street, int avenue, Direction direction, int beepers, Color colorRobot, Semaphore semaforo){
        super(street, avenue, direction, beepers, colorRobot,semaforo);
    }

    
}

class Extractor extends Racer {
    public Extractor(int street, int avenue, Direction direction, int beepers, Color colorRobot, Semaphore semaforo){
        super(street, avenue, direction, beepers, colorRobot,semaforo);
    }
    @Override
    public void run() {
        // Lógica específica del extractor
    }
}