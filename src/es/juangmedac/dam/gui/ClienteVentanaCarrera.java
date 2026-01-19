package es.juangmedac.dam.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Ventana principal del cliente para visualizar la carrera.
 * Muestra barras de progreso para cada camello y permite lanzar el dado en turno.
 */
public class ClienteVentanaCarrera extends JFrame {

    private String[] nombresJinetes; // Array con los nombres de los camellos
    private int[] posicionesFinales;  // Posiciones finales de los camellos

    private JProgressBar[] barras;    // Barras de progreso para cada camello
    private JLabel[] etiquetasNombres; // Etiquetas con los nombres de los camellos
    private JLabel etiquetaJugador;    // Muestra el nombre del jugador
    private JButton botonPodio;        // Botón para ver el podio
    private JButton botonTirar;        // Botón para lanzar el dado

    // Variables para gestionar la tirada del dado
    private int resultadoDado;
    private boolean dadoLanzado;

    /**
     * Constructor.
     * @param nombreJugador Nombre del jugador.
     */
    public ClienteVentanaCarrera(String nombreJugador) {
        super("Carrera de Camellos - Jugador: " + nombreJugador);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setLocationRelativeTo(null);
        initComponents(nombreJugador);
    }

    /**
     * Inicializa los componentes de la ventana.
     */
    private void initComponents(String nombreJugador) {
        setLayout(new BorderLayout());

        // Panel superior: muestra el nombre del jugador
        etiquetaJugador = new JLabel("Jugador: " + nombreJugador, SwingConstants.CENTER);
        etiquetaJugador.setFont(new Font("Arial", Font.BOLD, 16));
        add(etiquetaJugador, BorderLayout.NORTH);

        // Panel central: barras de progreso y nombres de los camellos
        JPanel panelBarras = new JPanel(new GridLayout(4, 2, 5, 5));
        barras = new JProgressBar[4];
        etiquetasNombres = new JLabel[4];
        for (int i = 0; i < 4; i++) {
            etiquetasNombres[i] = new JLabel("Camello " + (i + 1), SwingConstants.CENTER);
            barras[i] = new JProgressBar(0, 100);
            barras[i].setStringPainted(true);
            panelBarras.add(etiquetasNombres[i]);
            panelBarras.add(barras[i]);
        }
        add(panelBarras, BorderLayout.CENTER);

        // Panel inferior: botones para lanzar el dado y ver el podio
        JPanel panelInferior = new JPanel(new FlowLayout());

        // Botón para lanzar el dado (deshabilitado inicialmente)
        botonTirar = new JButton("Lanzar Dado");
        botonTirar.setEnabled(false);
        botonTirar.addActionListener((ActionEvent e) -> {
            // Al pulsar el botón, se genera un número aleatorio entre 1 y 6
            resultadoDado = 1 + (int)(Math.random() * 6);
            JOptionPane.showMessageDialog(this, "Has obtenido: " + resultadoDado);
            // Se marca que la tirada se ha realizado y se notifica al hilo bloqueado
            synchronized(this) {
                dadoLanzado = true;
                notifyAll();
            }
            // Se deshabilita el botón hasta la próxima activación
            botonTirar.setEnabled(false);
        });
        panelInferior.add(botonTirar);

        // Botón para ver el podio
        botonPodio = new JButton("Ver Podio");
        botonPodio.addActionListener((ActionEvent e) -> {
            if (posicionesFinales != null && nombresJinetes != null) {
                ClienteVentanaPodio podio = new ClienteVentanaPodio(posicionesFinales, nombresJinetes);
                podio.setLocationRelativeTo(this);
                podio.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Aún no hay posiciones finales disponibles.",
                        "Información",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        panelInferior.add(botonPodio);

        add(panelInferior, BorderLayout.SOUTH);

        setSize(600, 400);
    }

    /**
     * Establece los nombres de los jinetes en las etiquetas.
     * @param nombres Cadena de nombres separados por comas.
     */
    public void setNombresJinetes(String nombres) {
        this.nombresJinetes = nombres.split(",");
        for (int i = 0; i < nombresJinetes.length && i < etiquetasNombres.length; i++) {
            etiquetasNombres[i].setText(nombresJinetes[i]);
        }
    }

    /**
     * Actualiza los avances de los camellos en las barras de progreso.
     * @param avances Array de avances.
     */
    public void avance(int[] avances) {
        for (int i = 0; i < 4; i++) {
            int val = Math.min(avances[i], 100);
            barras[i].setValue(val);
        }
    }

    /**
     * Establece las posiciones finales de los camellos.
     * @param posiciones Array con las posiciones finales.
     */
    public void setPosicionesFinales(int[] posiciones) {
        this.posicionesFinales = posiciones;
    }

    /**
     * Activa el botón de "Lanzar Dado" para que el jugador pueda realizar su tirada.
     * Se llama cuando el servidor notifica que es el turno del jugador.
     */
    public void activarTirada() {
        SwingUtilities.invokeLater(() -> {
            botonTirar.setEnabled(true);
            // Opcional: cambiar el color del texto para indicar el turno
            etiquetaJugador.setForeground(Color.BLUE);
        });
    }

    /**
     * Método bloqueante que espera a que el jugador lance el dado.
     * @return El resultado obtenido en la tirada (entre 1 y 6).
     */
    public int esperarTirada() {
        // Activar la tirada en la interfaz
        activarTirada();
        synchronized(this) {
            while (!dadoLanzado) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            // Reiniciar la bandera para la siguiente tirada
            dadoLanzado = false;
            // Restaurar el color original del jugador
            etiquetaJugador.setForeground(Color.BLACK);
            return resultadoDado;
        }
    }

    /**
     * Muestra la ventana del podio con las posiciones finales.
     * @param posiciones Array con las posiciones finales.
     * @param nombres Array con los nombres de los jinetes.
     */
    public void mostrarPodio(int[] posiciones, String[] nombres) {
        System.out.println("Mostrando podio...");
        System.out.println("Posiciones: " + java.util.Arrays.toString(posiciones));
        System.out.println("Nombres: " + java.util.Arrays.toString(nombres));

        SwingUtilities.invokeLater(() -> {
            // Crear y mostrar la ventana del podio
            ClienteVentanaPodio podio = new ClienteVentanaPodio(posiciones, nombres);
            podio.setLocationRelativeTo(this);
            podio.setVisible(true);

            // También mostrar mensaje de confirmación
            JOptionPane.showMessageDialog(this,
                    "¡La carrera ha terminado!\nSe muestra el podio.",
                    "Fin de la carrera",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }

    /**
     * Método main para probar la ventana de la carrera de forma independiente.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClienteVentanaCarrera ventana = new ClienteVentanaCarrera("Jugador 1");
            ventana.setNombresJinetes("Camello 1,Camello 2,Camello 3,Camello 4");
            ventana.setVisible(true);
        });
    }
}
