import kareltherobot.*;
import java.awt.Color;

public class Racer extends Robot implements Runnable {
    
    public Racer(int street, int avenue, Direction direction, int beepers, Color colorRobot) {
        super(street, avenue, direction, beepers, colorRobot);     
    }

    @Override
    public void run() {
        // Método run para ser sobrescrito por subclases
    }
    
    // Gira el robot a la derecha
    protected void turnRight() {
        turnLeft();
        turnLeft();
        turnLeft();
    }
    
    // Intenta moverse hacia adelante de manera segura, esperando si hay algo enfrente
    protected void moveSafely() {
        if (frontIsClear()) {
            move();
        } else {
            // Espera un momento para dar tiempo a otro robot o situación para cambiar
            waitForClearPath();
        }
    }

    // Espera hasta que el camino esté despejado
    private void waitForClearPath() {
        while (!frontIsClear()) {
            // Espera un momento para dar tiempo a otro robot para que se mueva
            try {
                Thread.sleep(500); // Medio segundo de espera
            } catch (InterruptedException e) {
                // Manejo de la interrupción
                Thread.currentThread().interrupt();
                return; // Si el hilo es interrumpido, sal del método
            }
        }
    }
}

class Minero extends Racer {
    private boolean secondRobotFoundBeeper = false; // Variable para controlar si el segundo robot encontró un beeper

    public Minero(int street, int avenue, Direction direction, int beepers, Color colorRobot) {
        super(street, avenue, direction, beepers, colorRobot);
    }

    // Método para orientar al robot hacia el Sur
    private void orientSouth() {
        while (!facingSouth()) {
            turnLeft();
        }
    }

    // Método para hacer que el robot busque la parte inferior del mapa
    private void goToBottom() {
        orientSouth();
        while (frontIsClear()) {
            moveSafely(); // Usa moveSafely para evitar colisiones
        }
    }

    // Comprueba si el espacio a la derecha del robot está libre
    private boolean rightIsClear() {
        turnRight();
        if (frontIsClear()) {
            turnLeft();
            return true;
        } else {
            turnLeft();
            return false;
        }
    }

    // Sigue la pared a la derecha
    private void followRightWall() {
        while (true) {
            if (!secondRobotFoundBeeper && nextToABeeper()) { // Si el segundo robot aún no ha encontrado un beeper y hay un beeper al frente
                secondRobotFoundBeeper = true; // Marcar que el segundo robot encontró un beeper
                stopRobot(); // Detener el robot
            }

            if (rightIsClear()) {
                turnRight();
                moveSafely();
            } else if (frontIsClear()) {
                moveSafely();
            } else {
                turnLeft();
            }
            // Aquí puede ir lógica adicional, por ejemplo, para recoger beepers o realizar otras tareas
        }
    }

    // Detiene el robot
    private void stopRobot() {
        while (true) {
            // Permanece en un bucle infinito para mantener el robot detenido
        }
    }

    @Override
    public void run() {
        goToBottom();
        followRightWall(); // Iniciar el seguimiento de la pared derecha
    }
}

class Tren extends Racer {
    public Tren(int street, int avenue, Direction direction, int beepers, Color colorRobot){
        super(street, avenue, direction, beepers, colorRobot);
    }
}

class Extractor extends Racer {
    public Extractor(int street, int avenue, Direction direction, int beepers, Color colorRobot){
        super(street, avenue, direction, beepers, colorRobot);
    }
    @Override
    public void run() {
        // Lógica específica del extractor
    }
}