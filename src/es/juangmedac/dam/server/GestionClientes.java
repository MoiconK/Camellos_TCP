package es.juangmedac.dam.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hilo que gestiona la carrera para un cliente/jinete concreto.
 * Se controla el turno: solo el cliente cuyo id coincide con el turno actual podrá lanzar el dado.
 */
public class GestionClientes extends Thread {

    private Servidor servidor;
    private Socket socket;
    private int idCamello;

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
        try (DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            // 1) Enviar la lista de jinetes al cliente
            String nombres = servidor.getNombresJinetes();
            out.writeUTF(nombres);
            out.flush();

            // 2) Bucle principal de la carrera
            while (!servidor.isFinCarrera()) {
                // Esperar hasta que sea el turno de este camello
                synchronized (servidor) {
                    while (servidor.getTurnoActual() != idCamello && !servidor.isFinCarrera()) {
                        servidor.wait();
                    }
                }

                // Si la carrera ha finalizado, se sale del bucle
                if (servidor.isFinCarrera()) break;

                // Notificar al cliente que es su turno mediante el código especial -2
                out.writeInt(-2);
                out.flush();

                // Se espera el valor del dado (entre 1 y 6) enviado por el cliente
                int dado = in.readInt();

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

                // Se cambia el turno al siguiente camello que aún no haya finalizado
                servidor.siguienteTurno();

                // Pequeña espera para no saturar el ciclo
                Thread.sleep(500);
            }

            // 3) Una vez finalizada la carrera, se envían las posiciones finales
            int[] posiciones = servidor.getPosicionesFinales(idCamello);
            for (int i = 0; i < 4; i++) {
                out.writeInt(posiciones[i]);
            }
            // El código -1 indica el fin de la carrera
            out.writeInt(-1);
            out.flush();

            System.out.println("Jinete " + idCamello + " ha enviado las posiciones finales.");

        } catch (Exception e) {
            Logger.getLogger(GestionClientes.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
