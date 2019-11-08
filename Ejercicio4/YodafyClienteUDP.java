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
		byte []buferEnvio;
		byte []buferRecepcion=new byte[256];
		int bytesLeidos=0;
		String host="localhost"; //> Nombre del host donde se ejecuta el servidor:
		int port=8989; //> Puerto en el que espera el servidor:


        // Para la conexion UDP
        InetAddress direccion;
        DatagramPacket paquete;
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

            // Cerramos el socket
            socket.close();

            // Abrimos de nuevo la conexion para recibir mensajes
            socket = new DatagramSocket();
            direccion = InetAddress.getByName(host);

            // Recibimos el paquete
            paquete = new DatagramPacket(buferRecepcion, buferRecepcion.length);
            socket.receive(paquete);
            paquete.getData();
            paquete.getAddress();
            paquete.getPort();

            // Cerramos la conexion
            socket.close();

            // Mostramos la cadena de datos recibida
			System.out.println("Recibido: ");
			for(int i=0;i<bytesLeidos;i++){
				System.out.print((char)buferRecepcion[i]);
			}
            System.out.println();

		// Excepciones:
		} catch (UnknownHostException e) {
			System.err.println("Error: Nombre de host no encontrado.");
		} catch (IOException e) {
			System.err.println("Error de entrada/salida al abrir el socket.");
		}
	}
}
