/**
 * @file YodafyClienteTCP.java
 * @author Sergio Quijano Rey
 * @brief Cliente TCP para el procesador
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

public class YodafyClienteTCP {

	public static void main(String[] args) {
		
		String buferEnvio;
		String buferRecepcion;

		String host="localhost"; //> Nombre del host donde se ejecuta el servidor:
		int port=8989; //> Puerto en el que espera el servidor:
		Socket socketServicio=null; //> Socket para la conexión TCP

		
		try {
            // Creamos un socket que conecta a host con port
            socketServicio = new Socket(host, port);

            // Usamos PrintWriter y BufferReader para las lecturas y escrituras
            // sobre el socket
            PrintWriter outPrinter = new PrintWriter(socketServicio.getOutputStream(), true);
            BufferedReader inReader = new BufferedReader(new InputStreamReader(socketServicio.getInputStream()));
			
			// Si queremos enviar una cadena de caracteres por un OutputStream, hay que pasarla primero
			// a un array de bytes:
			buferEnvio="Al monte del volcan debes ir sin demora";
			
			// Enviamos el array por el outPrinter
            outPrinter.println(buferEnvio);
			
			// Aunque le indiquemos a TCP que queremos enviar varios arrays de bytes, sólo
			// los enviará efectivamente cuando considere que tiene suficientes datos que enviar...
			// Podemos usar "flush()" para obligar a TCP a que no espere para hacer el envío:
            // outPrinter.flush();
			
			// Leemos la respuesta del servidor. Para ello le pasamos un array de bytes, que intentará
			// rellenar. El método "read(...)" devolverá el número de bytes leídos.
            buferRecepcion = inReader.readLine();

			
			// Mostremos la cadena de caracteres recibidos:
			System.out.println("Recibido: ");
			System.out.print(buferRecepcion);
            System.out.println();
			
			// Una vez terminado el servicio, cerramos el socket (automáticamente se cierran
			// el inpuStream  y el outputStream)
            socketServicio.close();
			
			// Excepciones:
		} catch (UnknownHostException e) {
			System.err.println("Error: Nombre de host no encontrado.");
		} catch (IOException e) {
			System.err.println("Error de entrada/salida al abrir el socket.");
		}
	}
}
