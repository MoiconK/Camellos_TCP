package es.juangmedac.dam.cliente;

import es.juangmedac.dam.gui.ClienteVentanaCarrera;
import es.juangmedac.dam.gui.ClienteVentanaPodio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Cliente que participa en la carrera.
 * Ahora, espera un mensaje del servidor para saber cuándo es su turno para lanzar el dado.
 */
public class Cliente extends Thread {

    private String nombre;
    private boolean fin;
    // Ventana gráfica de la carrera
    private ClienteVentanaCarrera ventana;
    private String[] nombresJinetes; // Para almacenar los nombres de los jinetes
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    /**
     * Constructor.
     * @param nombre Nombre del cliente/jinete.
     */
    public Cliente(String nombre) {
        this.nombre = nombre;
        this.fin = false;
    }

    @Override
    public void run() {
        try {
            // Conexión al servidor (en el localhost y puerto 5555)
            socket = new Socket(InetAddress.getLocalHost(), 5555);
            System.out.println("[" + nombre + "] Conectado al servidor en puerto local: " + socket.getLocalPort());

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            // 1) Enviar el nombre del jugador al servidor
            out.writeUTF(nombre);
            out.flush();

            // 2) Recibir confirmación de aceptación
            String respuesta = in.readUTF();
            if ("aceptado".equals(respuesta)) {
                System.out.println("[" + nombre + "] El servidor me ha aceptado en la carrera.");
            }

            // 3) Recibir la lista de jinetes
            String listaJinetes = in.readUTF();
            System.out.println("[" + nombre + "] Lista de jinetes: " + listaJinetes);

            // Guardar los nombres de los jinetes
            this.nombresJinetes = listaJinetes.split(",");

            // Se muestra la ventana gráfica de la carrera
            ventana = new ClienteVentanaCarrera(nombre);
            ventana.setNombresJinetes(listaJinetes);
            ventana.setVisible(true);

            // 4) Bucle principal de comunicación con el servidor
            while (!fin) {
                try {
                    // Se lee el primer entero enviado por el servidor
                    int codigo = in.readInt();

                    if (codigo == -2) {
                        // Código -2: El servidor indica "¡Es tu turno, lanza el dado!"
                        System.out.println("[" + nombre + "] Es mi turno. Lanza el dado.");
                        // Se espera la tirada en la ventana (método bloqueante)
                        int dado = ventana.esperarTirada();
                        // Se envía el valor del dado al servidor
                        out.writeInt(dado);
                        out.flush();
                    } else if (codigo == 0 || codigo > 0) {
                        // En este caso se reciben los avances de los camellos.
                        // El servidor envía 4 enteros que representan los avances,
                        // y luego un código de control (0 = carrera en curso).
                        int[] avances = new int[4];
                        avances[0] = codigo; // El primer valor ya leído
                        avances[1] = in.readInt();
                        avances[2] = in.readInt();
                        avances[3] = in.readInt();
                        int control = in.readInt(); // control = 0
                        // Se actualiza la interfaz gráfica con los avances
                        ventana.avance(avances);
                    } else if (codigo == -1) {
                        // Código -1: Fin de la carrera.
                        // Se reciben las posiciones finales de los camellos.
                        int[] posiciones = new int[4];
                        posiciones[0] = in.readInt();
                        posiciones[1] = in.readInt();
                        posiciones[2] = in.readInt();
                        posiciones[3] = in.readInt();

                        // Actualizar las posiciones finales en la ventana
                        ventana.setPosicionesFinales(posiciones);

                        // Mostrar el podio automáticamente
                        ventana.mostrarPodio(posiciones, nombresJinetes);

                        fin = true;
                        break; // Salir del bucle
                    }
                } catch (java.io.EOFException e) {
                    System.out.println("[" + nombre + "] Conexión cerrada por el servidor.");
                    fin = true;
                    break;
                }
            }

            // Cerrar recursos
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();

            System.out.println("[" + nombre + "] Cliente finalizado correctamente.");

        } catch (Exception e) {
            System.out.println("Error en cliente " + nombre + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
