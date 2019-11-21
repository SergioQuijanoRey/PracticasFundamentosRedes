package Bingo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.Thread;


/**
 * Clase que representa el servidor del juego del bingo
 *
 * @author Sergio Quijano Rey
 * */
public class Servidor extends Thread{

    // Atributos de la clase
    //==========================================================================
    int num_jugadores;  //> Indica cuantos jugadores se necesitan para jugar una partida
    int port = 8989 ;
 
    // Constructores
    //==========================================================================

    /**
     * Constructor de la clase
     * @param num_jugadores el numero de jugadores que se necesitan para jugar una partida
     * */
    Servidor(int num_jugadores){
        this.num_jugadores = num_jugadores;
    }

    // Metodos publicos
    //==========================================================================
    @Override
    public void run(){
        try {
			
                        ServerSocket serverSocket = null;
                        try{
                            serverSocket= new ServerSocket(port);
                        } catch (IOException e){
                            System.out.println("Error: no se pudo atender en el puerto "+port);
                        }
			
			do {
				
				
                                Socket socketServicio;
                                socketServicio = serverSocket.accept();
				
				ProcesadorBingo procesador=new ProcesadorBingo(socketServicio,num_jugadores);
				procesador.run();
				
			} while (true);
			
		} catch (IOException e) {
			System.err.println("Error al escuchar en el puerto "+port);
		}
    }


    // Metodos privados
    //==========================================================================

    // Ejecucion del programa
    //==========================================================================

    public static void main(String[] args){

    
        Servidor server = new Servidor();
        server.run(port);
		

    }

}
