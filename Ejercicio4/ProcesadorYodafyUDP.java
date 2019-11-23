/**
 * @file ProcesadorYodafy.java
 * @author Sergio Quijano Rey
 * @brief Clase que implementa la funcionalidad de procesar mensajes
 *
 * Partiendo del codigo de jjramos
 * (CC) jjramos, 2012
 * */
package a1;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Random;
import java.net.InetAddress;


/**
 * Clase que representa un procesador de textos que convierte texto normal en 
 * texto al estilo Yoda
 *
 * @author Sergio Quijano Rey
 *
 * A partir del codigo de jjramos
 *
 * Nota: si esta clase extendiera la clase Thread, y el procesamiento lo hiciera el método "run()",
 * ¡Podríamos realizar un procesado concurrente! 
*/
public class ProcesadorYodafyUDP {
    // Atributos de la clase
    //==========================================================================
	// Referencia a un socket para enviar/recibir las peticiones/respuestas
	private DatagramSocket socketServicio;
	
	// Para que la respuesta sea siempre diferente, usamos un generador de números aleatorios.
	private Random random;
	
    // Constructores
    //==========================================================================
    
	public ProcesadorYodafyUDP(DatagramSocket socketServicio) {
		this.socketServicio=socketServicio;
		random=new Random();
	}
	
    // Metodos de la clase
    //==========================================================================
	
    /**
     * Metodo que realiza el procesamiento 
     * */
    
	public void procesa(){
		
		byte [] datosRecibidos = new byte[1024];
		int bytesRecibidos = 0;

		int port;
		InetAddress direccion;
		DatagramPacket paquete;

		byte [] datosEnviar;

        try{
			
			paquete = new DatagramPacket(datosRecibidos, datosRecibidos.length);
			socketServicio.receive(paquete);
			datosRecibidos = paquete.getData();
			bytesRecibidos = paquete.getLength();

			String peticion = new String(datosRecibidos,0,bytesRecibidos);
			String respuesta = yodaDo(peticion);

			datosEnviar = respuesta.getBytes();

			direccion = paquete.getAddress();
			port = paquete.getPort();

			paquete = new DatagramPacket(datosEnviar, datosEnviar.length, direccion, port);
			socketServicio.send(paquete);

			socketServicio.close();

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
