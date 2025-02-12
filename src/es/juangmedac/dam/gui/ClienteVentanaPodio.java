package es.juangmedac.dam.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana que muestra el podio final de la carrera.
 */
public class ClienteVentanaPodio extends JFrame {

    /**
     * Constructor.
     * @param posiciones Array con las posiciones finales (por ejemplo, [2, 4, 1, 3]).
     * @param nombres Array con los nombres de los jinetes.
     */
    public ClienteVentanaPodio(int[] posiciones, String[] nombres) {
        super("Podio - Carrera de Camellos");

        // Al cerrar esta ventana, no se cierra toda la aplicación
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setLayout(new GridLayout(4, 1, 5, 5));

        // Se crea un array de strings para mostrar el podio de forma ordenada
        String[] podioStrings = new String[4];
        for (int i = 0; i < 4; i++) {
            // Convertir la posición (1..4) a índice (0..3)
            int lugar = posiciones[i] - 1;
            podioStrings[lugar] = (lugar + 1) + "º: " + nombres[i];
        }

        // Se añaden las etiquetas con cada posición al panel
        for (int i = 0; i < 4; i++) {
            JLabel label = new JLabel(podioStrings[i], SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 14));
            add(label);
        }

        setSize(400, 300);
        setLocationRelativeTo(null);
    }

    /**
     * Método main para probar la ventana del podio de forma independiente.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Ejemplo: posiciones [2,1,4,3] y nombres de clientes
            int[] pos = {2, 1, 4, 3};
            String[] noms = {"Cliente 1", "Cliente 2", "Cliente 3", "Cliente 4"};
            new ClienteVentanaPodio(pos, noms).setVisible(true);
        });
    }
}
