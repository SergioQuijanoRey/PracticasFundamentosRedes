/**
 * @file YodafyServidorConcurrente.java
 * @author Sergio Quijano Rey
 * @brief Servidor Iterativo
 *
 * A partir del codigo de jjramos
 * (CC) jjramos, 2012
 * */
package a1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.DatagramPacket;



public class YodafyServidorIterativoUDP {

	public static void main(String[] args) {
		int port = 8989;
		DatagramSocket serverSocket = null;
	
			
			do{
                try{
					serverSocket = new DatagramSocket(port);
				}catch(IOException e){
					System.out.println("Error: no se pudo atender en el puerto"+port);
				}
				ProcesadorYodafyUDP procesador = new ProcesadorYodafyUDP(serverSocket);
				procesador.procesa();
			
				
				
			}while (true);
			
	}
}
