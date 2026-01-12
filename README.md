# Camellos\_TCP

Este proyecto implementa una simulación de una carrera de camellos utilizando un modelo cliente-servidor basado en el protocolo TCP. Está diseñado como una guía práctica para entender la comunicación entre múltiples clientes y un servidor, así como el manejo de interfaces gráficas en Java.

---

## 1. Estructura del proyecto

La estructura del proyecto está organizada en paquetes para facilitar la comprensión y el mantenimiento del código:

```
Camellos_TCP
|
|-- src
    |-- es.juangmedac.dam
        |-- cliente
        |   |-- Cliente.java
        |   |-- ClienteMain.java
        |
        |-- gui
        |   |-- ClienteVentanaCarrera.java
        |   |-- ClienteVentanaPodio.java
        |
        |-- server
            |-- Servidor.java
            |-- ServidorMain.java
            |-- GestionClientes.java
```

### Descripción de los paquetes y clases:

#### **cliente**

- `Cliente.java`: Define la lógica de un cliente en la simulación.
- `ClienteMain.java`: Inicia múltiples instancias de clientes simulando varios jugadores.

#### **gui**

- `ClienteVentanaCarrera.java`: Muestra el progreso de la carrera para un jugador.
- `ClienteVentanaPodio.java`: Muestra los resultados finales de la carrera (podio).

#### **server**

- `Servidor.java`: Gestiona la lógica principal del servidor y controla la carrera.
- `ServidorMain.java`: Inicia la ejecución del servidor.
- `GestionClientes.java`: Maneja la comunicación con cada cliente conectado al servidor.

---

## 2. Lógica del proyecto

### **Servidor**

1. **Inicia el servidor**:
   - Escucha en un puerto específico (por defecto, `5555`).
   - Acepta conexiones de hasta 4 clientes.
2. **Gestión de clientes**:
   - Cada cliente se gestiona en un hilo separado mediante la clase `GestionClientes`.
   - Se envían datos de avance de los camellos y las posiciones finales a los clientes.
3. **Simulación de la carrera**:
   - Cada camello avanza en base a tiradas aleatorias simuladas en el servidor.
   - Cuando todos los camellos terminan, se calculan las posiciones finales.

### **Cliente**

1. **Se conecta al servidor**:
   - Envía el nombre del jugador al servidor para registrarse.
   - Recibe confirmación de aceptación del servidor.
2. **Visualiza la carrera**:
   - La clase `ClienteVentanaCarrera` muestra el avance de cada camello en tiempo real mediante barras de progreso.
3. **Visualiza el podio**:
   - Una vez terminada la carrera, el cliente muestra los resultados finales usando la clase `ClienteVentanaPodio`.

---

## 3. Detalles de implementación

### **Clases principales**

#### **Servidor.java**

- **Métodos importantes**:
  - `ejecutarServidor()`: Inicia el servidor y maneja la lógica de conexión de clientes y carrera.
  - `realizarAvance(int posicion, int avance)`: Actualiza el progreso de un camello.
  - `getAvances()`: Devuelve los avances actuales de los camellos.
  - `getPosicionesFinales(int idCamello)`: Calcula y devuelve las posiciones finales de los camellos.

#### **GestionClientes.java**

- **Responsabilidad**:
  - Maneja la interacción entre el servidor y un cliente específico.
  - Envia los avances de la carrera y las posiciones finales al cliente.
- **Método destacado**:
  - `run()`: Ejecuta la comunicación en un hilo separado.

#### **Cliente.java**

- **Responsabilidad**:
  - Se conecta al servidor.
  - Recibe datos de la carrera y los muestra en las interfaces gráficas.
- **Métodos importantes**:
  - `run()`: Ejecuta la lógica principal del cliente.

#### **ClienteVentanaCarrera.java**

- **Función**:
  - Muestra el avance de los camellos en barras de progreso.
- **Métodos clave**:
  - `setNombresJinetes(String nombres)`: Configura los nombres de los camellos.
  - `avance(int[] avances)`: Actualiza las barras de progreso con los avances actuales.

#### **ClienteVentanaPodio.java**

- **Función**:
  - Muestra el podio final con las posiciones de los camellos.
- **Método clave**:
  - `ClienteVentanaPodio(int[] posiciones, String[] nombres)`: Configura y muestra las posiciones finales.

---

## 4. Ejecución del proyecto

### **Requisitos previos**

- JDK 16 o superior.
- IntelliJ IDEA (opcional, para facilitar el desarrollo).

### **Pasos para ejecutar**

1. **Iniciar el servidor**:

   - Ejecuta `ServidorMain.java`.
   - El servidor comenzará a escuchar en el puerto `5555`.

2. **Iniciar los clientes**:

   - Ejecuta `ClienteMain.java`.
   - Esto iniciará 4 instancias de clientes.

3. **Simulación de la carrera**:

   - Observa cómo los camellos avanzan en las ventanas de los clientes.
   - Una vez finalizada la carrera, se mostrará el podio con los resultados.

---

## 5. Notas

- **Modularidad:** Observa cómo se organiza el proyecto en paquetes y clases, cada uno con responsabilidades claras.
- **Hilos:** Analiza cómo se gestionan los hilos tanto en el cliente como en el servidor.
- **Interfaces gráficas:** Aprende cómo se integran las interfaces gráficas con la lógica del cliente.
- **Protocolo TCP:** Comprende cómo se utiliza este protocolo para la comunicación cliente-servidor.

---

¡Diviértete explorando y entendiendo este proyecto de cliente-servidor!

