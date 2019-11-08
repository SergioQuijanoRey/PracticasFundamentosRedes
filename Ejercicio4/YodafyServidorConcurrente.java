/**
 * @file YodafyServidorConcurrente.java
 * @author Sergio Quijano Rey
 * @brief Servidor concurrente que usa UDP
 *
 * A partir del codigo de jjramos
 * (CC) jjramos, 2012
 * */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

// Para el uso de UDP
import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.DatagramPacket;

public class YodafyServidorConcurrente {

	public static void main(String[] args) {
		int port=8989;                  //> Puerto de escucha
		byte []buffer=new byte[256];    //> array de bytes auxiliar para recibir o enviar datos.
		int bytesLeidos=0;              //> Numero de bytes leidos
        
        // Para la conexion UDP
        InetAddress direccion;
        DatagramPacket paquete;
        DatagramSocket socketServidor;
		
		try {
            // Abro el socket UDP
            socketServidor = new DatagramSocket(port);

            // Recibo constantemente los mensajes por UDP
            do{
                // Se recibe un paquete con la peticion
                paquete = new DatagramPacket(buffer, buffer.length);
                socketServidor.receive(paquete);
                paquete.getData();
                paquete.getAddress();
                paquete.getPort();

                // Se muestra que se ha recibido la peticion
                System.out.println("Peticion de procesamiento recibida!");

                // Creo el socket servicio
                DatagramSocket socketServicio = new DatagramSocket(paquete.getPort());
                
                // Creamos el objeto de la clase ProcesadorYodafy sobre el socket servicio? 
                ProcesadorYodafy procesador = new ProcesadorYodafy(socketServicio);
                procesador.start();

            }while(true);

		} catch (IOException e) {
			System.err.println("Error al escuchar en el puerto "+port);
		}
	}
}
