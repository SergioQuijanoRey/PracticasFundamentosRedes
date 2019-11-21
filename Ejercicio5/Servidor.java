import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.ServerSocket;
import java.lang.Thread;
import java.util.ArrayList;
import java.net.SocketTimeoutException;


/**
* Clase que representa el servidor del juego del bingo
*
* @author Sergio Quijano Rey
* */
public class Servidor{

    // Atributos de la clase
    //==========================================================================
    int num_jugadores;  //> Indica cuantos jugadores se necesitan para jugar una partida
    int port = 8989 ;
    private static int timeout = 100;

    ServerSocket socketServidor;    //> Socket del servidor
    ArrayList<ProcesadorBingo> procesadores;    //> Todos los clientes que tenemos conectados

    // Constructores
    //==========================================================================

    /**
     * Constructor de la clase
     * Crea el socket del servidor ademas de establecer los atributos
     * @param num_jugadores el numero de jugadores que se necesitan para jugar una partida
     *
     * */
    Servidor(int num_jugadores){
        // Se establece el numero de jugadores
        this.num_jugadores = num_jugadores;

        // Al inicio no hay conexiones a los clientes
        this.procesadores = new ArrayList<ProcesadorBingo>();

        // Establezco el timeout para aceptar conexiones
        socketServidor.setSoTimeout(timeout);

        // Ejecutamos el codigo del servidor iterativo
        this.run();

    }

    // Metodos publicos
    //==========================================================================
    /**
     * Se ejecuta el proceso del servidor
     * */
    public void run(){
        while(true){
            // Se pone el servidor a escuchar
            try{
                socketServidor = new ServerSocket(port);
            }catch(SocketTimeoutException e){
                // No hacemos nada, pasa el timeout
            }catch(Exception e){
                System.err.println("Error al crear el socket del servidor");
            }

            try{
                // Espero a recibir una conexion
                Socket current_conexion = socketServidor.accept();

                // Asignamos un procesador a la conexion
                ProcesadorBingo current_procesador = new ProcesadorBingo(current_conexion, num_jugadores);
                procesadores.add(current_procesador);

                // Lanzamos la hebra del procesador
                current_procesador.start();

            }catch(Exception e){
                System.err.println("Error recibiendo una conexion socket en Servidor.run()");
            }
        }
    }

    // Ejecucion del programa
    //==========================================================================

    /**
     * Metodo que lanza el programa del servidor
     * */
    public static void main(String[] args){
        // Lanzamos el servidor (el run no tiene nada que ver con los Threads de Java)
        Servidor server = new Servidor(2);
        server.run();
    }
}
