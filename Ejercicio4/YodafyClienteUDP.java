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
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.DatagramPacket;

public class YodafyClienteUDP {

	public static void main(String[] args) {
		
        // Informacion para conectarnos al servidor
		String host="localhost"; //> Nombre del host donde se ejecuta el servidor:
		int port=8989; //> Puerto en el que espera el servidor:

        // Mensaje a enviar y recibir
		byte []buferEnvio;
		byte []buferRecepcion=new byte[256];
		int bytesLeidos=0;

        // Conexion UDP
        DatagramSocket socket;
        InetAddress direccion;
        DatagramPacket paquete;

		try {
            
            // Nos conectamos al servidor
            socket = new DatagramSocket();
            direccion = InetAddress.getByName(host);

            // Generamos el mensaje a enviar
			buferEnvio="Al monte del volcan debes ir sin demora".getBytes();
			
            // Generamos el paquete a enviar
            paquete = new DatagramPacket(buferEnvio, buferEnvio.length, direccion, port);

            // Enviamos el paquete por el socket
            socket.send(paquete);
    
			// Una vez terminado el servicio, cerramos el socket 
            socket.close();
			
			// Excepciones:
		} catch (UnknownHostException e) {
			System.err.println("Error: Nombre de host no encontrado.");
		} catch (IOException e) {
			System.err.println("Error de entrada/salida al abrir el socket.");
		}
	}
}
