package es.juangmedac.dam.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hilo que gestiona la carrera para un cliente/jinete concreto.
 */
public class GestionClientes extends Thread {

    private Servidor servidor;
    private Socket socket;
    private int idCamello;
    private DataInputStream in;
    private DataOutputStream out;

    /**
     * Constructor.
     * @param servidor Referencia al servidor para acceder a la carrera.
     * @param socket Socket de comunicación con el cliente.
     * @param idCamello Identificador del camello/jinete.
     */
    public GestionClientes(Servidor servidor, Socket socket, int idCamello) {
        this.servidor = servidor;
        this.socket = socket;
        this.idCamello = idCamello;
    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            // 1) Enviar la lista de jinetes al cliente
            String nombres = servidor.getNombresJinetes();
            out.writeUTF(nombres);
            out.flush();
            System.out.println("Enviada lista de jinetes a cliente " + idCamello);

            // 2) Bucle principal de la carrera
            while (!servidor.isFinCarrera()) {
                synchronized (servidor) {
                    // Esperar hasta que sea el turno de este camello
                    while (servidor.getTurnoActual() != idCamello && !servidor.isFinCarrera()) {
                        System.out.println("Cliente " + idCamello + " esperando turno...");
                        servidor.wait();
                    }
                }

                // Si la carrera ha finalizado, se sale del bucle
                if (servidor.isFinCarrera()) {
                    System.out.println("Cliente " + idCamello + ": carrera finalizada, saliendo del bucle");
                    break;
                }

                // Notificar al cliente que es su turno mediante el código especial -2
                System.out.println("Cliente " + idCamello + ": es su turno, notificando...");
                out.writeInt(-2);
                out.flush();

                // Se espera el valor del dado (entre 1 y 6) enviado por el cliente
                int dado = in.readInt();
                System.out.println("Cliente " + idCamello + " ha lanzado el dado: " + dado);

                // Actualizar el avance del camello con el valor del dado
                servidor.realizarAvance(idCamello, dado);

                // Se envían los avances actuales de todos los camellos
                int[] todosAvances = servidor.getAvances();
                for (int i = 0; i < 4; i++) {
                    out.writeInt(todosAvances[i]);
                }
                // Se envía un código de control (0) que indica "carrera en curso"
                out.writeInt(0);
                out.flush();
                System.out.println("Cliente " + idCamello + ": avances enviados");

                // Se cambia el turno al siguiente camello que aún no haya finalizado
                servidor.siguienteTurno();

                // Pequeña espera para no saturar el ciclo
                Thread.sleep(1000);
            }

            // 3) Esperar a que todos terminen
            synchronized (servidor) {
                while (!servidor.isFinCarrera()) {
                    servidor.wait();
                }
            }

            // 4) Una vez finalizada la carrera, se envían las posiciones finales
            System.out.println("Cliente " + idCamello + ": enviando posiciones finales");
            int[] posiciones = servidor.getPosicionesFinales();

            // El código -1 indica el fin de la carrera
            out.writeInt(-1);
            // Enviar las posiciones finales
            for (int i = 0; i < 4; i++) {
                out.writeInt(posiciones[i]);
            }
            out.flush();
            System.out.println("Cliente " + idCamello + ": posiciones finales enviadas");

            // Esperar a que el cliente procese los datos
            Thread.sleep(2000);

        } catch (Exception e) {
            System.out.println("Error en hilo cliente " + idCamello + ": " + e.getMessage());
            Logger.getLogger(GestionClientes.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            // Cerrar el socket solo después de enviar todos los datos
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                    System.out.println("Socket del cliente " + idCamello + " cerrado");
                }
            } catch (Exception e) {
                System.out.println("Error cerrando recursos del cliente " + idCamello + ": " + e.getMessage());
            }
        }
    }
}
