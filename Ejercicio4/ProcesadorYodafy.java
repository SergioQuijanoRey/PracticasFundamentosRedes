/**
 * @file ProcesadorYodafy.java
 * @author Sergio Quijano Rey
 * @brief Clase que implementa la funcionalidad de procesar mensajes
 *
 * Partiendo del codigo de jjramos
 * (CC) jjramos, 2012
 * */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;

// Para usar las nuevas clases
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;

// Para el uso de UDP
import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.DatagramPacket;

/**
 * Clase que representa un procesador de textos que convierte texto normal en 
 * texto al estilo Yoda, usando UDP
 *
 * @author Sergio Quijano Rey
 *
 * A partir del codigo de jjramos
*/
public class ProcesadorYodafy extends Thread{
    // Atributos de la clase
    //==========================================================================
	// Referencia a un socket para enviar/recibir las peticiones/respuestas
	private DatagramSocket socketServicio;
	
	// Para que la respuesta sea siempre diferente, usamos un generador de números aleatorios.
	private Random random;
	
    // Constructores
    //==========================================================================
    /**
     * Constructor de la clase
     * @param socketServicio referencia la socket abierto por otra clase
     * */
	public ProcesadorYodafy(DatagramSocket socketServicio) {
		this.socketServicio=socketServicio;
		random=new Random();
	}
	
    // Metodos de la clase
    //==========================================================================
	
    /**
     * Metodo que realiza el procesamiento 
     * */
    @Override
	public void run(){

		// Como máximo leeremos un bloque de 1024 bytes. Esto se puede modificar.
		byte [] datosRecibidos=new byte[1024];
		int bytesRecibidos=0;
		
		// Array de bytes para enviar la respuesta. Podemos reservar memoria cuando vayamos a enviarka:
		byte [] datosEnviar;
		
		
		try {
			// Lee la frase a Yodaficar:
            DatagramPacket paquete = new DatagramPacket(datosRecibidos, datosRecibidos.length);
            socketServicio.receive(paquete);
            paquete.getData();
            paquete.getAddress();
            paquete.getPort();

			
			// Yoda hace su magia:
			// Creamos un String a partir de un array de bytes de tamaño "bytesRecibidos":
			String peticion=new String(datosRecibidos,0,bytesRecibidos);
			// Yoda reinterpreta el mensaje:
			String respuesta=yodaDo(peticion);
			// Convertimos el String de respuesta en una array de bytes:
			datosEnviar=respuesta.getBytes();
			
			// Enviamos la traducción de Yoda:
            // TODO --> posible bug en el envio del paquete con el paquete.getPort
            InetAddress direccion = InetAddress.getByName("localhost");
            DatagramPacket paqueteEnvio = new DatagramPacket(datosEnviar, datosEnviar.length, direccion, paquete.getPort());
            socketServicio.send(paqueteEnvio);

            // Cerramos el socket
            socketServicio.close();

        // Excepciones
		} catch (IOException e) {
			System.err.println("Error al obtener los flujso de entrada/salida.");
		}
	}

    /**
     * Se convierte una cadena de texto en dialecto normal a dialecto de Yoda
     * @param peticion string con la cadena en dialecto normal
     * @return string con la cadena en dialecto yoda
     * */
	private String yodaDo(String peticion) {
		// Desordenamos las palabras:
		String[] s = peticion.split(" ");
		String resultado="";
		
		for(int i=0;i<s.length;i++){
			int j=random.nextInt(s.length);
			int k=random.nextInt(s.length);
			String tmp=s[j];
			
			s[j]=s[k];
			s[k]=tmp;
		}
		
		resultado=s[0];
		for(int i=1;i<s.length;i++){
		  resultado+=" "+s[i];
		}
		
		return resultado;
	}
}
