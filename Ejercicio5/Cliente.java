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
    Integer ID;

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
        try{
            PrintWriter out = new PrintWriter(servidor.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(servidor.getInputStream()));
        } catch(Exception e){
            System.err.println("Error intentando tomar los flujos desde el cliente");
        }
    }

    // Metodos
    //==========================================================================
    /**
     * El cliente se conecta al servidor
     * */
    public void connectToServer(){
        // Se envia la solicitud
        out.println("100, CONNECT");

        // Se toma la recepcion
        try{
            // Se lee el mensaje y se procesa
            String response = in.readLine();
            Codop codop = new Codop(response);

            if(codop.getCode() == 101){ // Se admite la conexion
                // Tomamos el id dado por el servidor
                this.ID = Integer.parseInt(codop.getArgs().get(1));

                // Confirmamos al servidor
                out.println("102, CONNECTED " + this.ID);

                // Mostramos informacion
                sysout("Conexion establecida! El ID del cliente es " + this.ID);


            }else if(codop.getCode() == 410){ // Se rechaza la conexion
                System.err.println("El servidor ha rechazado nuestra conexion"); 

            }else{
                System.err.println("Error al recibir la respuesta del servidor: codigo desconocido");
                System.err.println("Codigo de error recibido: " + codop.getCode());
            }

        }catch(Exception e){
            System.err.println("Error leyendo la respuesta del servidor en Cliente.connectToBingoServer()");
        }
    }
    

    // Metodos privados
    //==========================================================================
    /**
     * Se establece una conexion socket a traves de TCP
     * */
    private void connectToBingoServer(){
        try{
            servidor = new Socket(host, port);
        }catch(Exception e){
            System.err.println("Error al intentar establecer la conexion con el servidor");
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
