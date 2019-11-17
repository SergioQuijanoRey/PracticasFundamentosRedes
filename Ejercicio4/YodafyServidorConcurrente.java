/**
 * @file YodafyServidorConcurrente.java
 * @author Sergio Quijano Rey
 * @brief Servidor Iterativo
 *
 * A partir del codigo de jjramos
 * (CC) jjramos, 2012
 * */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.DatagramSocket;
import java.net.DatagramPacket;

public class YodafyServidorConcurrente {

	public static void main(String[] args) {

        // Informacion del servidor
		int port=8989;                  //> Puerto de escucha

        // Recepcion del mensaje
		byte []buffer=new byte[256];    //> array de bytes auxiliar para recibir o enviar datos.
		int bytesLeidos=0;              //> Numero de bytes leidos

        // Conexion UDP
        DatagramSocket socketServidor;
        DatagramSocket socketServicio;
        DatagramPacket paquete;

        // Conexion
        try{
            do{
                
                // Creamos una conexion UDP
                socketServicio = new DatagramSocket(port);

                // Esperamos a leer un paquete
                paquete = new DatagramPacket(buffer, buffer.length);
                socketServidor.receive(paquete);

                // Mostramos un mensaje por pantalla
                System.out.println("Paquete recibido en el servidor!");

                // Creamos un objeto de la clase ProcesadorYodafy pasandole el paquete leido
                ProcesadorYodafy procesador = new ProcesadorYodafy(paquete);
                procesador.start();

            }while(true);

          // Excepciones
        } catch (IOException e){
            System.err.println("Error al escuchar en el puerto " + port);
        }
	}
}
