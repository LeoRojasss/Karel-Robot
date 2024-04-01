import kareltherobot.*;
import java.awt.Color;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.*;
import java.util.concurrent.*;

public class Racer extends Robot implements Runnable {
    
    public Racer(int street, int avenue, Direction direction, int beepers, Color colorRobot) {
        super(street, avenue, direction, beepers, colorRobot);     
    }

    @Override
    public void run() {
        // Método run para ser sobrescrito por subclases
    }

    protected void orientSouth() {
        while (!facingSouth()) {
            turnLeft();
        }
    }

    protected void orientNorth() {
        while (!facingNorth()) {
            turnLeft();
        }
    }

    protected void orientWest() {
        while (!facingWest()) {
            turnLeft();
        }
    }

    protected void orientEast() {
        while (!facingEast()) {
            turnLeft();
        }
    }

    protected void moveF() {
        while (frontIsClear()) {
            move();
        }
    }

    protected void moveX(int steps) {
        for (int i = 0; i < steps; i++) {
            move();
        }
    }

    protected void turnRight() {
        turnLeft();
        turnLeft();
        turnLeft();
    }


    }



    class Minero extends Racer {
        private static Lock primerLock = new ReentrantLock();
        private static Lock segundoLock = new ReentrantLock();
        private static boolean primerMinero = true;
        private static boolean trenListo = false; // Variable para indicar si el tren puede moverse
        private static CountDownLatch trenListoLatch = new CountDownLatch(1); // Latch para señalizar al tren
        private static int minaActual = 0;
        private static int menasAcabadas = 0;
        private static int minaDejada = 0;
        private Semaphore semaforoPosicionCritica;
    
        public Minero(int street, int avenue, Direction direction, int beepers, Color colorRobot) {
            super(street, avenue, direction, beepers, colorRobot);
            this.semaforoPosicionCritica = ControlDeColisiones.obtenerSemaforo(14, 10);
            World.setupThread(this);
        }
    
        protected void  soltar(){
            moveX(menasAcabadas + 1);
            if (anyBeepersInBeeperBag()) {            
                for (int i=0; i<50;i++){
                    putBeeper();
                    minaDejada = minaDejada + 1;
                }
                orientSouth();
                moveX(1);
            }
        }
    
        @Override
        public void run() {
            primerLock.lock();
            try {
                moveF();
                turnLeft();
                moveX(1);
                orientSouth();
                moveF();
            } finally {
                primerLock.unlock();
                // Indicar al tren que puede moverse
                trenListo = true;
                // Señalizar al tren que puede moverse
                trenListoLatch.countDown();
            }
            orientWest();
            moveF();
            orientSouth();
            moveF();
            orientEast();
            moveF();
            orientNorth();
            moveF();
            orientEast();
    
            if (primerMinero) {
                moveX(6);
                primerMinero = false;
            }else{
                moveX(5);
                orientSouth();
                moveX(1);
                orientEast();
                moveF();
                orientNorth();
            }
            
            while ((minaActual <= 200) && (menasAcabadas <3 )) {
                System.out.println("entro al while");
                segundoLock.lock();
                try {
                    if (minaActual < 200) {
                        if (primerMinero == false) {
                            for (int i=0; i<50;i++){
                                pickBeeper();
                                minaActual = minaActual + 1;
                            }
                            primerMinero = true;
                            orientWest();
                            soltar();
                            orientEast();
                            try {
                                semaforoPosicionCritica.acquire();
                                System.out.println("Minero adquiere la posición crítica (14, 10)");
                                orientEast(); 
                            
                                
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt(); // Restablece el estado de interrupción
                                System.out.println("El hilo del Minero fue interrumpido.");
                            } finally {
                                semaforoPosicionCritica.release();
                                System.out.println("Minero libera la posición crítica (14, 10)");
                            }
                            moveF();
                            orientNorth();
                        } else{
                            primerLock.lock();
                            try{
                            moveF();
                            orientEast();
                            moveX(menasAcabadas);
                            for (int i=0; i<50;i++){
                                pickBeeper();
                                minaActual = minaActual +1;
                            }
                            orientWest();
                            soltar();
                            
                            try {
                                semaforoPosicionCritica.acquire();
                                System.out.println("Minero adquiere la posición crítica (14, 10)");
                                
                            orientEast();
                                
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt(); // Restablece el estado de interrupción
                                System.out.println("El hilo del Minero fue interrumpido.");
                            } finally {
                                semaforoPosicionCritica.release();
                                System.out.println("Minero libera la posición crítica (14, 10)");
                            }
                            moveF();
                            orientNorth();
                            }finally{
                                primerLock.unlock();
                            }
                        }
                    } else{
                        System.out.println("Cambio de mena");
                        menasAcabadas = menasAcabadas + 1;
                        minaActual = 0;
                        if (menasAcabadas == 3) {
                            System.out.println("Todas las menas han sido minadas.");
                            
                            break;
                        }
                    }
                } finally {
                    System.out.println("CAMBIO");
                    segundoLock.unlock();
                    
                }
            }
        }

    

    public void liberarPosicion() {
        primerLock.unlock();
        System.out.println("Bloqueo liberado");
        // Señalizar al tren que puede moverse
        trenListoLatch.countDown();
    }
    
    public static CountDownLatch getTrenListoLatch() {
        return trenListoLatch;
    }
}

class Tren extends Racer {
    private static final int RETRASO_TREN = 1500; // Retraso en milisegundos (ajusta según sea necesario)
    private static Lock primerLock = new ReentrantLock();
    private static boolean extractorListo = false;
    private static CountDownLatch extractorListoLatch = new CountDownLatch(1); // Latch para señalizar al tren
    private static boolean primerTren = true;

    public Tren(int street, int avenue, Direction direction, int beepers, Color colorRobot){
        super(street, avenue, direction, beepers, colorRobot);
    }

    @Override
    public void run() {
        // Esperar hasta que el minero libere la posición
        try {
            Minero.getTrenListoLatch().await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Interrumpido");
        }

        // Introducir un retraso antes de que el tren comience a moverse
        try {
            Thread.sleep(RETRASO_TREN);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Interrumpido");
        }

        // Una vez que se señaliza el latch y ha pasado el retraso, el tren puede avanzar
        primerLock.lock();
        try {
            moveF();
            turnLeft();
            moveX(1);
            orientSouth();
            moveF();
        } finally {
            primerLock.unlock();
            // Indicar al tren que puede moverse
            extractorListo = true;
            // Señalizar al tren que puede moverse
            extractorListoLatch.countDown();
        }
        orientWest();
        moveF();
        orientSouth();
        moveF();
        orientEast();
        primerLock.lock();
        if (primerTren) {
            moveF();
            orientNorth();
            moveF();
            orientEast();
            moveX(4);
            primerTren = false;
            primerLock.unlock();
        }else{
            moveF();
            orientNorth();
            moveF();
            orientEast();
            moveX(3);
        }
}
public static CountDownLatch getExtractorListoLatch() {
    return extractorListoLatch;
}
}


class Extractor extends Racer {
    private static final int RETRASO_EXTRACTOR = 5000; // Retraso en milisegundos (ajusta según sea necesario)
    private static Lock primerLock = new ReentrantLock();
    private static boolean primerExtractor = true;
    private static int beepersRecogidos = 0;
    private int numeroExtractor;

    
    public Extractor(int street, int avenue, Direction direction, int beepers, Color colorRobot, int numeroExtractor){
        super(street, avenue, direction, beepers, colorRobot);
        this.numeroExtractor=numeroExtractor;
    }



    @Override
    public void run() {
        try {
            Tren.getExtractorListoLatch().await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Interrumpido");
        }
    
        // Introducir un retraso antes de que el extractor comience a moverse
        try {
            Thread.sleep(RETRASO_EXTRACTOR);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Interrumpido");
        }
        
        // Lógica específica del extractor
        primerLock.lock();
        try {
            moveF();
            turnLeft();
            moveX(1);
            orientSouth();
            moveF();
        } finally {
            primerLock.unlock();
            // Indicar al tren que puede moverse
        }
        orientWest();
        moveF();
        orientSouth();

        if (primerExtractor) {
            moveF();
            orientEast();
            moveX(2);
            primerExtractor = false;
        }else{
            moveX(2);
        }
        for (int i = 0; i < 50 && beepersRecogidos < 12000; i++) {
            if (nextToABeeper()) {
                pickBeeper();
                beepersRecogidos++;
            } else {
                break;
            }
        }
        orientWest();
        moveF();
        turnRight();
        moveF();
        turnRight();
        moveF();
        turnLeft();
        move(); 
        while(anyBeepersInBeeperBag() == true ) {
            putBeeper();
            
        }
    }
}
