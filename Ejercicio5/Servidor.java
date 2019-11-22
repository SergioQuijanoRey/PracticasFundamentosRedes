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

    // Conexiones
    private ServerSocket socketServidor;     //> Socket del servidor
    private ArrayList<Socket> conexiones;    //> Todos los clientes que tenemos conectados
    private ArrayList<BufferedReader> ins;  //> Flujos de entrada
    private ArrayList<PrintWriter> outs;  //> Flujos de entrada
    


    // Estado del servidor
    Boolean inGame;
    Integer current_id;

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

        // Se establece el contador de identificadores
        current_id = 0;

        // Al inicio no hay conexiones a los clientes ni flujos
        this.conexiones = new ArrayList<Socket>();
        ins = new ArrayLis<BufferedReader>();
        outs = new ArrayList<PrintWriter>();

        // Establecemos que no estamos en una partida
        inGame = false;

        // Establezco el timeout para aceptar conexiones
        socketServidor.setSoTimeout(timeout);

        // Creamos el socket servidor
        try{
            socketServidor = new ServerSocket(port);
        }catch(Exception e){
            System.err.println("Error al crear el socket del servidor");
        }

        // Ejecutamos el codigo del servidor iterativo
        this.run();

    }

    // Metodos publicos
    //==========================================================================
    /**
     * Se ejecuta el proceso del servidor
     * */
    public void run(){

        // 1 - Miramos la conexion
        // 2 - Si no estamos en una partida, iniciar el proceso de partida
        // 3 - Si estamos en partida
        //      3.1 - Mandar numero
        //      3.2 - Esperar a recibir todas las confirmaciones
        while(true){

            // Intentamos conectar a un nuevo cliente
            try{
                // Espero a recibir una conexion
                Socket current_conexion = socketServidor.accept();

                // Lo añadimos a nuestras conexiones
                conexiones.add(current_conexion);

                // Creamos los flujos y los añadimos
                PrintWriter current_out = new PrintWriter(current_conexion.getOutputStream(), true);
                BufferedReader current_in = new BufferedReader(new InputStreamReader(current_conexion.getInputStream()));
                outs.add(current_out);
                ins.add(current_in);

                // Exigimos que nos envie la peticion de conexion por nuestro protocolo
                String response = current_in.readLine();
                Codop codop = new Codop(response);

                if(codop.getCode() != 100){
                    // Muestro el mensaje
                    syserr("Error, el cliente no nos ha solicitado la conexion");
                    syserr("El usuario se quita de las conexiones")

                    // Quito al cliente de las conexiones
                    conexiones.pop();
                    outs.pop();
                    ins.pop();
                }else{
                    // Asigno la ID que le corresponde
                    current_out.println("101, ALLOW + " + current_id);
                    current_id = current_id + 1;

                    // Esperamos que el cliente confirme la conexion
                    response = current_in.readLine();
                    codop = new Codop(response);

                    if(codop.getCode() != 102 || (codop.getCode() == 102 && codop.getArg(1) != Integer.toString(current_id - 1))){
                        syserr("ERROR, el cliente no ha confirmado la conexion correctamente");
                        syserr("El usuario se quita de las conexiones");
                        
                        // Quito al cliente de las conexiones
                        conexiones.pop();
                        outs.pop();
                        ins.pop();
                    }else{
                        sysout("Nuevo cliente conectado con exito");
                    }
                }
                

            }catch(SocketTimeoutException timeout){
                // No hacemos nada por el timeout
                // Para que podamos trabajar como un servidor iterativo
            }catch(Exception e){
                System.err.println("Error al establecer un nuevo socket con el cliente en Servidor.run()");
            }

            // Tenemos que lanzar una nueva partida
            if(inGame == false && conexiones.size() >= num_jugadores){
                
            // Realizamos la iteracion de la partida
            }else if(inGame == true){

            }else{

            }
        }
    }

    // Ejecucion del programa
    //==========================================================================

    /**
     * Metodo que lanza el programa del servidor
     * */
    public static void main(String[] args){
        // Lanzamos el servidor y lo ejecutamos
        Servidor server = new Servidor(2);
        server.run();
    }
}
            PrintWriter outPrinter = new PrintWriter(socketServicio.getOutputStream(), true);
            BufferedReader inReader = new BufferedReader(new InputStreamReader(socketServicio.getInputStream()));
