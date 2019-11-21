import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.ServerSocket;
import java.lang.Thread;
import java.util.ArrayList;


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

    ServerSocket socketServidor;    //> Socket del servidor
    ArrayList<Socket> conexiones;   //> Array con las conexiones a los jugadores

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
        this.conexiones = new ArrayList<Socket>();

        // Se pone el servidor a escuchar
        try{
            socketServidor = new ServerSocket(port);
        }catch(Exception e){
            syserr("Error al crear el socket del servidor");
        }
    }

    // Metodos publicos
    //==========================================================================
    /**
     * Se ejecuta el proceso del servidor
     * */
    public void run(){
        while(true){
            Socket current_conexion = socketServidor.accept();
        }
    }

    @Deprecated
    public void run_daniel(){
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
