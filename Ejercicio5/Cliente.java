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
    Boolean connected;
    Boolean inGame;

    // Conexion al servidor
    Socket servidor;
    PrintWriter out;
    BufferedReader in;

    // Constructores
    //==========================================================================
    Cliente(){
        // Establezco algunas variables
        connected = false;
        inGame = false;

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

                // Notamos que estamos conectados
                connected = true;

                // Confirmamos al servidor
                out.println("102, CONNECTED " + this.ID);

                // Mostramos informacion
                System.out.println("Conexion establecida! El ID del cliente es " + this.ID);


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

    /**
     * El cliente se une a una partida
     * */
    public void joinGame(){
        // Comprobamos si estamos conectados
        if(!connected){
            System.err.println("No se puede iniciar una partida sin haberse conectado al servidor");
            return;
        }

        // Se envia la solicitud para entrar a una partida
        out.println("200, JOIN " + ID);

        try{
            // Se toma y procesa el mensaje
            String response = in.readLine();
            Codop codop = new Codop(response);

            if(codop.getCode() == 201){ // Accedemos a la partida
                // Pasamos a jugar
                inGame = true;
                this.playGame();

            }else if(codop.getCode() == 420){ // Partida con demasiados jugadores
                System.err.println("El servidor ya no puede aceptar mas jugadores");
            }else if(codop.getCode() == 421){
                System.err.println("El servidor no esta en fase de aceptar jugadores");
                System.err.println("O ya hay una partida en marcha o no se ha iniciado la fase de aceptacion");
            }else{
                System.err.println("Se ha leido un codigo de respuesta no valido");
                System.err.println("Codigo de respuesta: " + codop.getCode());

            }
        }catch(Exception e){
            System.err.println("Error leyendo del socket en Cliente.joinGame()");
        }

    }

    public void playGame(){
        // Comprobaciones de seguridad
        if(inGame == false){
            System.err.println("No se puede jugar sin haber entrado a una partida!");
            return;
        }
        if(connected == false){
            System.err.println("No se puede jugar sin haberse conectado con un servidor");
            return;
        }

        Boolean finished = false;
        while(!finished){
            // Recibir numeros
        }


        // Se ha salido de la partida
        inGame = false;
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
