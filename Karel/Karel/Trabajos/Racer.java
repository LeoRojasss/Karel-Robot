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
    }

class semaforos {
    public static Semaphore semaforoTrenIda = new Semaphore(1);
    public static Semaphore semaforoTrenVenida = new Semaphore(0); 
    public static Semaphore semaforoMinero = new Semaphore(1);
    public static Semaphore semaforoTrenMinero = new Semaphore(0);
    public static Semaphore semaforoTrenExtractor = new Semaphore(1);
    public static Semaphore semaforoExtractorTren = new Semaphore(0);
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

        private int identificador;
    
        public Minero(int street, int avenue, Direction direction, int beepers, Color colorRobot, int identificador) {
            super(street, avenue, direction, beepers, colorRobot);
            this.identificador = identificador;
            World.setupThread(this);
        }
    
        public void soltar(){
            moveX(menasAcabadas + 1);
            if (anyBeepersInBeeperBag()) {            
                for (int i=0; i<50;i++){
                    putBeeper();
                    minaDejada = minaDejada + 1;
                }
            }
            orientSouth();
            moveX(1);
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
                trenListo = true;
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
            
            while ((minaActual <= 200) && (menasAcabadas <7 )) {
                System.out.println("entro al while");
                if (minaActual < 200) {
                    if (identificador == 1){
                        segundoLock.lock();
                        try{
                            if (primerMinero == false) {
                                for (int i=0; i<50;i++){
                                    pickBeeper();
                                    minaActual = minaActual + 1;
                                }
                                orientWest();
                                moveX(menasAcabadas);
                                primerMinero = true;
                            }else{
                                moveX(1);
                                orientEast();
                                moveX(menasAcabadas);
                                for (int i=0; i<50;i++){
                                    pickBeeper();
                                    minaActual = minaActual + 1;
                                }
                                orientWest();
                            }
                            try {
                                semaforos.semaforoMinero.acquire();
                                soltar();
                                System.out.println("el minero se mueve por el semaforo");
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                System.out.println("El hilo del Minero fue interrumpido.");
                            } finally {
                                semaforos.semaforoTrenMinero.release();
                                System.out.println("Minero libera la posición crítica");
                            }
                        }finally{
                            segundoLock.unlock();
                        }
                        orientEast(); 
                        moveF();
                        orientNorth();
                    }else if (identificador == 2){
                        segundoLock.lock();
                        try{
                            moveF();
                            orientEast();
                            moveX(menasAcabadas);
                            for (int i=0; i<50;i++){
                                pickBeeper();
                                minaActual = minaActual +1;
                            }
                            orientWest();
                            try {
                                semaforos.semaforoMinero.acquire();
                                soltar();
                                System.out.println("el minero se mueve por el semaforo");
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                System.out.println("El hilo del Minero fue interrumpido.");
                            } finally {
                                semaforos.semaforoTrenMinero.release();
                                System.out.println("Minero libera la posición crítica ");
                            }
                        }finally{
                            segundoLock.unlock();
                        }
                        orientEast();
                        moveF();
                        orientNorth();
                    }
                }else {
                    System.out.println("Cambio de mena");
                        menasAcabadas = menasAcabadas + 1;
                        minaActual = 0;
                        if (menasAcabadas == 6) {
                            System.out.println("Todas las menas han sido minadas.");
                            break;
                        }
                }
            }
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
        moveX(2);
        
        while (true) {
            moveF();
            orientNorth();
            moveX(4);
            try{
                semaforos.semaforoTrenIda.acquire();
                moveX(2);
            }catch(InterruptedException e){
                e.printStackTrace();
            }finally{
                semaforos.semaforoTrenVenida.release();
            }
            moveF();
            orientEast();
            moveX(4);
            try {       // Semáforo para recoger beepers extraídos por Mineros
                semaforos.semaforoTrenMinero.acquire(); // Conseguir la luz verde del semaforo
                System.out.println("el tren adquiere el semaforo");
                moveX(1); // punto de recoleccion
                for (int i = 0; i < 50; i++) { // Cuantos beepers recoge, se hace asi por la eficiencia for > while
                    pickBeeper();
                }
                orientSouth();
                moveX(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaforos.semaforoMinero.release(); // Dar la luz verde a los mineros
            }
        moveF();
        orientWest();
        moveX(4);
        try {
            semaforos.semaforoTrenVenida.acquire();
            moveX(2); // AvanzarF
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaforos.semaforoTrenIda.release();
        }
        moveF();
        orientSouth();
        moveF();
        try {
            semaforos.semaforoTrenExtractor.acquire();
            while (anyBeepersInBeeperBag()) { // Entregar todos los beepers que carga en el punto de recoleccion
                putBeeper();
            }
            orientEast();
            moveF();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaforos.semaforoExtractorTren.release();
        }

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
    private static int bodegasLlenas = 0;
    private static int bodega = 0;
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
        moveF();
        orientEast();
        moveX(1);

        while (true) {
            try {
                semaforos.semaforoExtractorTren.acquire();
                moveX(1); // AvanzarF
                for (int i=0; i<50;i++){
                    pickBeeper();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaforos.semaforoTrenExtractor.release();
            }
            orientWest();
            moveF();
            orientNorth();
            moveF();
            orientEast();
            moveF();
            orientNorth();
            moveX(1);
            orientEast();
            moveX(1);
            orientSouth();
            moveF();
            while (anyBeepersInBeeperBag()) { // Entregar todos los beepers que carga en el punto de recoleccion
                putBeeper();
            }
            orientNorth();
            moveX(2);
            orientWest();
            moveX(1);
            orientSouth();
            moveF();
            orientWest();
            moveF();
            orientSouth();
            moveF();
            orientEast();
            moveX(1);
        }
    }
}