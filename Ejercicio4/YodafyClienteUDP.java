/**
 * @file YodafyClienteUDP.java
 * @author Sergio Quijano Rey
 * @brief Cliente UDP para el procesador
 *
 * A partir del codigo de jjramos
 * (CC) jjramos, 2012
 * */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.DatagramPacket;

public class YodafyClienteUDP {

	public static void main(String[] args) {
        // Para manejar textos
		byte []buferEnvio;
		byte []buferRecepcion=new byte[256];
		int bytesLeidos=0;

        // Informarcion sobre el servidor
		String host="localhost";    //> Nombre del host donde se ejecuta el servidor:
		int port=8989;              //> Puerto en el que espera el servidor:


        // Para la conexion UDP
        InetAddress direccion;
        DatagramPacket paquete;
        DatagramPacket paqueteRecibido;
        DatagramSocket socket;
		
		try {
            
            // Creo el mensaje que se va a enviar
			buferEnvio="Al monte del volcan debes ir sin demora".getBytes();

            // Creamos la conexion UDP
            socket = new DatagramSocket();
            direccion = InetAddress.getByName(host);

            // Enviamos el paquete usando la conexion UDP
            paquete = new DatagramPacket(buferEnvio, buferEnvio.length, direccion, port);
            socket.send(paquete); 

            // Recibimos el paquete
            paqueteRecibido = new DatagramPacket(buferRecepcion, buferRecepcion.length);
            socket.receive(paqueteRecibido);
            String stringRecibido = new String(paquete.getData());
            // paquete.getAddress(); --> no sirve ahora para nada
            // paquete.getPort(); --> no sirve ahora para nada

            // Mostramos la cadena de datos recibida
			System.out.println("Recibido: ");
            System.out.println(stringRecibido);
            System.out.println();

            // Cerramos la conexion
            socket.close();

		// Excepciones:
		} catch (UnknownHostException e) {
			System.err.println("Error: Nombre de host no encontrado.");
		} catch (IOException e) {
			System.err.println("Error de entrada/salida al abrir el socket.");
		}
	}
}
