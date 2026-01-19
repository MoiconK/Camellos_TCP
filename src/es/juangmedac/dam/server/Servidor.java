package es.juangmedac.dam.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Servidor que gestiona la carrera de camellos vía TCP.
 * Ahora, la carrera es por turnos: cada camello avanza cuando su jugador lanza el dado.
 */
public class Servidor {

    // Número máximo de jinetes/camellos
    private final int NUM_MAX_JINETES = 4;
    // Lista de sockets de los clientes conectados
    private ArrayList<Socket> socketsClientes;

    // Datos de la carrera
    private int contadorPosicionFinal;   // Se usará para asignar posiciones finales (1º, 2º, …)
    private int[] posicionesFinales;       // Posiciones finales de cada camello (índice = id del camello)
    private int[] avances;                 // Avances actuales de cada camello (de 0 a 100)
    private int numJinetesAcabados;        // Número de camellos que han finalizado
    private boolean finCarrera;            // Indica si la carrera ha terminado
    private String nombresJinetes;         // Cadena con los nombres de los jinetes separados por comas

    // Variable para controlar el turno. Indica el id del camello que tiene el turno.
    private int turnoActual;

    /**
     * Constructor. Inicializa las variables necesarias.
     */
    public Servidor() {
        contadorPosicionFinal = 1;
        numJinetesAcabados = 0;
        finCarrera = false;
        nombresJinetes = "";
        turnoActual = 0; // El primer turno se asigna al camello 0.
    }

    /**
     * Método principal que ejecuta el servidor.
     */
    public void ejecutarServidor() {
        // Inicializamos los arrays para almacenar avances y posiciones finales
        posicionesFinales = new int[NUM_MAX_JINETES];
        avances = new int[NUM_MAX_JINETES];

        socketsClientes = new ArrayList<>();
        DataOutputStream[] outputs = new DataOutputStream[NUM_MAX_JINETES]; // Para enviar a todos

        System.out.println("Servidor iniciado en puerto 5555... Esperando " + NUM_MAX_JINETES + " clientes.");

        try (ServerSocket serverSocket = new ServerSocket(5555)) {

            // Esperamos a que se conecten los 4 clientes
            while (socketsClientes.size() < NUM_MAX_JINETES) {
                Socket socketCliente = serverSocket.accept();
                System.out.println("Cliente conectado: " + socketCliente.getInetAddress());

                // Se crea un canal de comunicación para leer el nombre del jinete
                DataInputStream in = new DataInputStream(socketCliente.getInputStream());
                DataOutputStream out = new DataOutputStream(socketCliente.getOutputStream());
                String nombreJinete = in.readUTF();

                // Se acumulan los nombres separados por comas
                if (!nombresJinetes.isEmpty()) {
                    nombresJinetes += ",";
                }
                nombresJinetes += nombreJinete;

                // Se envía un acuse de recibo al cliente
                out.writeUTF("aceptado");
                out.flush();

                // Se guarda el socket del cliente y su output stream
                socketsClientes.add(socketCliente);
                outputs[socketsClientes.size() - 1] = out;
            }

            // Se lanzan los hilos de gestión para cada cliente
            for (int i = 0; i < NUM_MAX_JINETES; i++) {
                Socket s = socketsClientes.get(i);
                GestionClientes hilo = new GestionClientes(this, s, i);
                hilo.start();
            }

            // Espera a que la carrera finalice
            synchronized (this) {
                while (!finCarrera) {
                    wait();
                }
            }

            // Pequeña pausa para asegurar que todos los hilos han terminado
            Thread.sleep(2000);

            System.out.println("Carrera finalizada. Cerrando servidor.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Actualiza el avance de un camello usando el valor obtenido de la tirada del dado.
     * @param idCamello Identificador del camello.
     * @param avance Valor obtenido en la tirada del dado (normalmente de 1 a 6).
     */
    public synchronized void realizarAvance(int idCamello, int avance) {
        // Si el camello ya ha finalizado, no se hace nada.
        if (avances[idCamello] >= 100) {
            return;
        }

        // Actualizamos el avance
        avances[idCamello] += avance;
        if (avances[idCamello] >= 100) {
            avances[idCamello] = 100;
            // Se asigna la posición final
            posicionesFinales[idCamello] = contadorPosicionFinal;
            contadorPosicionFinal++;
            numJinetesAcabados++;
            System.out.println("Camello " + idCamello + " ha terminado en la posición " + posicionesFinales[idCamello]);
        } else {
            System.out.println("Camello " + idCamello + " avanza " + avance + " (Total: " + avances[idCamello] + ")");
        }

        // Si todos han finalizado, se marca el fin de la carrera
        if (numJinetesAcabados == NUM_MAX_JINETES) {
            notificarFinCarrera(); // Usar el nuevo método
        }
    }

    /**
     * Devuelve el array de avances actuales.
     */
    public synchronized int[] getAvances() {
        return avances;
    }

    /**
     * Devuelve el array de posiciones finales. Si el camello aún no tiene asignada su posición,
     * se le asigna en ese momento.
     */
    public synchronized int[] getPosicionesFinales(int idCamello) {
        if (avances[idCamello] >= 100 && posicionesFinales[idCamello] == 0) {
            posicionesFinales[idCamello] = contadorPosicionFinal;
            contadorPosicionFinal++;
        }
        return posicionesFinales;
    }

    /**
     * Indica si la carrera ha finalizado.
     */
    public synchronized boolean isFinCarrera() {
        return finCarrera;
    }

    /**
     * Devuelve la lista de nombres de jinetes.
     */
    public synchronized String getNombresJinetes() {
        return nombresJinetes;
    }

    // *************** Métodos para gestionar el turno ***************

    /**
     * Devuelve el id del camello que tiene el turno actual.
     */
    public synchronized int getTurnoActual() {
        return turnoActual;
    }

    /**
     * Cambia el turno al siguiente camello que aún no haya finalizado.
     * Se utiliza un bucle para saltar a aquellos que ya han terminado.
     */
    public synchronized void siguienteTurno() {
        do {
            turnoActual = (turnoActual + 1) % NUM_MAX_JINETES;
        } while (avances[turnoActual] >= 100 && !finCarrera);
        // Se notifica a todos los hilos que el turno ha cambiado.
        notifyAll();
    }
    // Notifica a todos los clientes que la carrera ha finalizado
    public synchronized void notificarFinCarrera() {
        finCarrera = true;
        notifyAll(); // Despierta a todos los hilos que estén esperando

        // Pequeña pausa para asegurar que todos los hilos reciben la notificación
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
