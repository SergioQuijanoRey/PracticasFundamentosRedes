/**
 * @file YodafyClienteTCP.java
 * @author Sergio Quijano Rey
 * @brief Cliente TCP para el procesador
 *
 * A partir del codigo de jjramos
 * (CC) jjramos, 2012
 * */
package a1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class YodafyClienteUDP {

	public static void main(String[] args) {
		
		byte[] buferEnvio;
		byte[] buferRecepcion= new byte[256];
		int bytesLeidos = 0;

		String host="localhost"; //> Nombre del host donde se ejecuta el servidor:
		int port=8989; //> Puerto en el que espera el servidor:
		DatagramSocket socketServicio=null; //> Socket para la conexión TCP
		DatagramPacket paquete;
		InetAddress direccion;

		try {
			socketServicio = new DatagramSocket();
			direccion = InetAddress.getByName(host);

			buferEnvio = "Al monte del volcan debes ir sin demora".getBytes();

			paquete = new DatagramPacket(buferEnvio, buferEnvio.length,direccion, port );
			socketServicio.send(paquete);

			paquete = new DatagramPacket(buferRecepcion, buferRecepcion.length);
			socketServicio.receive(paquete);

			buferRecepcion = paquete.getData();
			bytesLeidos = paquete.getLength();

			String recibido = new String(buferRecepcion,0,bytesLeidos);

			System.out.println("Recibido: " + recibido);



		} catch (UnknownHostException e) {
			System.err.println("Error: Nombre de host no encontrado.");
		} catch (IOException e) {
			System.err.println("Error de entrada/salida al abrir el socket.");
		}
	}
}
