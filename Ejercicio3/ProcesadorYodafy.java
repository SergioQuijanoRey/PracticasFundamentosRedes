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
public class ProcesadorYodafy extends Thread{
    // Atributos de la clase
    //==========================================================================
	// Referencia a un socket para enviar/recibir las peticiones/respuestas
	private Socket socketServicio;
	// stream de lectura (por aquí se recibe lo que envía el cliente)
	private InputStream inputStream;
	// stream de escritura (por aquí se envía los datos al cliente)
	private OutputStream outputStream;
	
	// Para que la respuesta sea siempre diferente, usamos un generador de números aleatorios.
	private Random random;
	
    // Constructores
    //==========================================================================
    /**
     * Constructor de la clase
     * @param socketServicio referencia la socket abierto por otra clase
     * */
	public ProcesadorYodafy(Socket socketServicio) {
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
		
        // Strings para recibir el mensaje y guardar los datos procesados
        String datosRecibidos;
        String datosEnviar;
		
		
		try {
            // Flujos de lectura y escritura con las clases del ejercicio 2
            PrintWriter outPrinter = new PrintWriter(socketServicio.getOutputStream(),true);        
            BufferedReader inReader = new BufferedReader(new InputStreamReader(socketServicio.getInputStream()));
			
			// Lee la frase a Yodaficar:
            datosRecibidos = inReader.readLine();
			
			// Yoda hace su magia:
			// Yoda reinterpreta el mensaje:
			String respuesta=yodaDo(datosRecibidos);
			
			// Enviamos la traducción de Yoda:
            outPrinter.println(respuesta);

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
