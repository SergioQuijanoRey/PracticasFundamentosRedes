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
		
		byte []buferEnvio;
		byte []buferRecepcion=new byte[256];
		int bytesLeidos=0;

		String host="localhost"; //> Nombre del host donde se ejecuta el servidor:
		int port=8989; //> Puerto en el que espera el servidor:
		Socket socketServicio=null; //> Socket para la conexión TCP
		
		try {
            // Creamos un socket que conecta a host con port
            socketServicio = new Socket(host, port);
            
            // Obtenemos los flujos de entrada y salida del socket
			InputStream inputStream = socketServicio.getInputStream();
			OutputStream outputStream = socketServicio.getOutputStream();
			
			// Si queremos enviar una cadena de caracteres por un OutputStream, hay que pasarla primero
			// a un array de bytes:
			buferEnvio="Al monte del volcan debes ir sin demora".getBytes();
			
			// Enviamos el array por el outputStream;
		    outputStream.write(buferEnvio, 0, buferEnvio.length);	
			
			// Aunque le indiquemos a TCP que queremos enviar varios arrays de bytes, sólo
			// los enviará efectivamente cuando considere que tiene suficientes datos que enviar...
			// Podemos usar "flush()" para obligar a TCP a que no espere para hacer el envío:
            // TODO --> hay que comprobar que esta linea funciona correctamente
            outputStream.flush();
			
			// Leemos la respuesta del servidor. Para ello le pasamos un array de bytes, que intentará
			// rellenar. El método "read(...)" devolverá el número de bytes leídos.
            bytesLeidos = inputStream.read(buferRecepcion);

			
			// Mostremos la cadena de caracteres recibidos:
			System.out.println("Recibido: ");
			for(int i=0;i<bytesLeidos;i++){
				System.out.print((char)buferRecepcion[i]);
			}
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
