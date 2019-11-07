/**
 * @file YodafyServidorIterativo.java
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

public class YodafyServidorIterativo {

	public static void main(String[] args) {
		int port=8989;                  //> Puerto de escucha
		byte []buffer=new byte[256];    //> array de bytes auxiliar para recibir o enviar datos.
		int bytesLeidos=0;              //> Numero de bytes leidos
        ServerSocket socketServidor;    //> Shocket TCP para el servidor, abierto en modo pasivo
        Socket socketServicio;          //> Shocket de cada conexion que se establece
		
		try {
            socketServidor = new ServerSocket(port);
			
			do {
                socketServicio = null;
                try{
                    socketServicio = socketServidor.accept();
                }catch(IOException e){
                    System.err.println("Error al aceptar una peticion de conexion");
                }

                System.out.println("Peticion de procesamiento recibida!");

				
				// Creamos un objeto de la clase ProcesadorYodafy, pasándole como 
				// argumento el nuevo socket, para que realice el procesamiento
				// Este esquema permite que se puedan usar hebras más fácilmente.
				ProcesadorYodafy procesador=new ProcesadorYodafy(socketServicio);
				procesador.procesa();
				
			} while (true);
			
		} catch (IOException e) {
			System.err.println("Error al escuchar en el puerto "+port);
		}
	}
}
