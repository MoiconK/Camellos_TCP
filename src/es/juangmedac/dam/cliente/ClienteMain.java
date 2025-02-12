package es.juangmedac.dam.cliente;

/**
 * Clase principal para arrancar los clientes.
 * Se lanzan 4 instancias de Cliente, con un breve retardo entre cada uno.
 */
public class ClienteMain {
    public static void main(String[] args) {
        // Se crean y se inician los 4 clientes
        Cliente c1 = new Cliente("Cliente 1");
        c1.start();

        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        Cliente c2 = new Cliente("Cliente 2");
        c2.start();

        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        Cliente c3 = new Cliente("Cliente 3");
        c3.start();

        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        Cliente c4 = new Cliente("Cliente 4");
        c4.start();
    }
}
