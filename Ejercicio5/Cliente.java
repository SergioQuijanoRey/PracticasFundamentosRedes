import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Clase que representa un cliente del bingo
 *
 * @author Sergio Quijano Rey
 * */
public class Cliente extends Thread{
    // Atributos de la clase
    //==========================================================================
    int port = 8989;
    String host = "localhost";
    int ID;

    // Conexion al servidor
    Socket servidor;
    PrintWriter out;
    BufferedReader in;

    // Constructores
    //==========================================================================
    Cliente(){

        // Conexion al servidor
        connectToServer();

        // Conseguimos los flujos de entrada salida
        PrintWriter out = new PrintWriter(servidor.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(servidor.getInputStream()));
        
    }

    // Metodos
    //==========================================================================
    // Metodos privados
    //==========================================================================
    private void connectToServer(){
        try{
            servidor = new Socket(host, port);
        }catch(Exception e){
            syserror("Error al intentar establecer la conexion con el servidor");
        }
    }

    // Funcion principal
    //==========================================================================
    /**
     * Funcion principal del cliente
     * */
    public static void main(String[] args){

    }
}
