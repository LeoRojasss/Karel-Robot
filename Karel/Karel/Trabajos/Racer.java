import kareltherobot.*;
import java.awt.Color;

public class Racer extends Robot implements Runnable {
    public Racer(int street, int avenue, Direction direction, int beepers, Color color) {
        super(street, avenue, direction, beepers,color);     
    }

    @Override
    public void run() {
        // Método run para ser sobrescrito por subclases
    }
}

class Minero extends Racer {
    public Minero(int street, int avenue, Direction direction, int beepers, Color color) {
        super(street, avenue, direction, beepers, color);
    }

    @Override
    public void run() {
        // Lógica específica del minero
    }
}

class Tren extends Racer {
    public Tren(int street, int avenue, Direction direction, int beepers, Color color) {
        super(street, avenue, direction, beepers, color);
    }

    @Override
    public void run() {
        // Lógica específica del tren
    }
}

class Extractor extends Racer {
    public Extractor(int street, int avenue, Direction direction, int beepers, Color color) {
        super(street, avenue, direction, beepers, color);
    }

    @Override
    public void run() {
        // Lógica específica del extractor
    }
}
