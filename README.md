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

## 5. Preguntas

1. **¿Qué IP has tenido que poner en el código del Cliente? ¿Es una IP pública o privada?**

-Hemos usado la IP: 10.192.117.164. 
-Esta es una IP pública dentro de una red Privada, la cual es la del wifi del instituto.


2. **Si el Árbitro cierra su portátil a mitad de carrera, ¿qué excepción salta en tu pantalla?**

-El error que aparece es una SocketException la cual te dice que la conexión al host, no responde y no se puede seguis jugando.

<img width="882" height="294" alt="{F860911B-3106-4AAD-B210-49FD3E6F5261}" src="https://github.com/user-attachments/assets/b65e047a-ba94-42ba-ab1e-184a11319a79" />


3. **¿Por qué es necesario que todos estéis conectados a la misma red (mismo Router/Switch) para que esto funcione con IPs tipo 192.168.x.x?**

-Porque el servidor está dentro de una red privada y esto requiere que todos estén conectados a esta. Además es necesario estar conectado al router o switch ya que estos realizan la función de puente entre todos los dispositivos y el servidor.

---

## 6. Capturas

-Imagen de la carrera:

<img width="512" height="349" alt="image" src="https://github.com/user-attachments/assets/3c1c3138-c5ff-408b-9415-daa3d31b231d" />


-Imagen de cuando te sale una puntución de cuando tiras el dado:

<img width="260" height="110" alt="image" src="https://github.com/user-attachments/assets/cff12204-4153-487c-b7ad-ebb9bae76b60" />

-Imagen de cuando finaliza la carrera

<img width="264" height="124" alt="{ADD64079-DB8D-414E-82F0-B1D2C88CF631}" src="https://github.com/user-attachments/assets/2512ce17-bc67-40e2-ad07-7408a5a1ea31" />

-Imagen del podio

<img width="386" height="293" alt="{1A3088C3-CC7B-4148-A297-8127324CD5C4}" src="https://github.com/user-attachments/assets/07eef1c2-0e88-41ca-b5bb-324879ca62de" />

---




