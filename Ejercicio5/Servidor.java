import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.ServerSocket;
import java.lang.Thread;
import java.util.ArrayList;
import java.net.SocketTimeoutException;
import java.io.BufferedReader;
import java.io.PrintWriter;


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
    Boolean inStage;
    Integer current_id;

    // Para los clientes que estan en partida, solo guardo los indices de los
    // arrays en los que tengo almacenada la informacion de su conexion
    ArrayList<Integer> idx_in_game; //> Indice de cliente en partida

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

        // No hay ids de procesos en partidas


        // Al inicio no hay conexiones a los clientes ni flujos
        this.conexiones = new ArrayList<Socket>();
        ins = new ArrayLis<BufferedReader>();
        outs = new ArrayList<PrintWriter>();

        // Establecemos que no estamos en una partida
        inGame = false;
        inStage = false;

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
     * Se ejecuta el proceso del servidor indefinidamente
     *
     * 1. Añadimos posibles conexiones
     * 2. Si estamos en una partida, hacemos una iteracion del juego
     * 3. En otro caso, leemos los mensajes de todos los clientes conectados
     * */
    public void run(){
        while(true){

            // Intentamos conectar a un nuevo cliente
            connect_new_client();

            // Si estamos en una partida, hacemos iteraciones del juego hasta que
            // se acabe el juego
            if(inGame){
                Boolean acabado = false;
                while(acabado == false){
                    // Realizamos una iteracion del juego
                    iteration_of_game(acabado);
                }
            }else{
                // Leemos todos los mensajes de los clientes y consideramos todos
                // los posibles mensajes de esta fase
                read_from_all();
            }

        }
    }

    // Metodos privados
    //==========================================================================

    /**
     * Se leen los mensajes de todos los clientes en una ronda
     * Esto se hace cuando no se esta en una partida
     * */
    private void read_from_all(){
        for(int i = 0; i < conexiones.size(); i++){
            // Tomamos el mensaje
            String response = ins.get(i).readLine();
            Codop codop = new Codop(response);

            // Procesamos el mensaje
            process_message(i, codop);
        }
    }

    /**
     * Se procesa un mensaje recibido por un cliente cuando no se esta en una partida
     * @param index el indice del cliente que nos manda el mensaje
     * @param codop el mensaje ya procesado recibido
     * */
    private void process_message(int index, Codop codop){
        switch(codop.getCode()){
            // Mensajes validos fuera de una partida
            //=============================================
            
            // El cliente quiere unirse a una partida
            case 200:
                if(inStage){
                    
                    // Hay hueco, se añade a la partida
                    if(idx_in_game.size() < num_jugadores){

                        // Se añade a la lista de indices de jugadores
                        idx_in_game.add(index);

                        // Se le comunica al cliente
                        outs.get(index).println("201, JOINED");

                        // Comprobamos si hay que iniciar la partida
                        if(idx_in_game.size() == num_jugadores){
                            inGame = true;
                            inStage = false;
                        }

                    // No hay hueco, se comunica
                    }else{
                        outs.get(index).println("420, FULL");
                    }
                }else{
                    // No estamos en stage, se lo comunicamos al cliente
                    outs.get(index).println("421, NOT IN STAGE");
                }
            break;
            
            // El cliente quiere desconectarse
            case 500:
                // Se confirma la desconexion
                outs.get(index).println("501, BYE");
                
                // Se desconecta al cliente
                conexiones.remove(index);
                outs.remove(index);
                ins.remove(index);
            break;

            // Mensajes no validos fuera de una partida!
            //==============================================

            // TODO -- Falta procesar esto!
            
            // El usuario confirma un numero pero no esta en partida
            case 301:
            break;

            // El usuario canta bingo pero no esta en partida
            case 302:
            break;

            // El servidor no recibe un numero, pero no esta en partida
            case 430:
            break;

        }
    }

    /**
     * Intentamos que se conecte un nuevo cliente al servidor
     * Durante el tiempo establecido por el timeout
     * */
    private void connect_new_client(){
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
                    System.err.println("Error, el cliente no nos ha solicitado la conexion");
                    System.err.println("El usuario se quita de las conexiones");

                    // Envio el mensaje de que no se puede conectar
                    current_out.println("410, CANNOT CONNECT");

                    // Quito al cliente de las conexiones
                    conexiones.remove(conexiones.size() - 1);
                    outs.remove(outs.size() - 1);
                    ins.remove(ins.size() - 1);
                }else{
                    // Asigno la ID que le corresponde
                    current_out.println("101, ALLOW + " + current_id);
                    current_id = current_id + 1;

                    // Esperamos que el cliente confirme la conexion
                    response = current_in.readLine();
                    codop = new Codop(response);

                    if(codop.getCode() != 102 || (codop.getCode() == 102 && codop.getArg(1) != Integer.toString(current_id - 1))){
                        System.err.println("ERROR, el cliente no ha confirmado la conexion correctamente");
                        System.err.println("El usuario se quita de las conexiones");
                        
                        // Envio el mensaje de que no se puede conectar
                        current_out.println("410, CANNOT CONNECT");
                        
                        // Quito al cliente de las conexiones
                        conexiones.remove(conexiones.size() - 1);
                        outs.remove(outs.size() - 1);
                        ins.remove(ins.size() - 1);
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
    }

    /**
     * Se hace una iteracion del juego
     * @param acabado booleano que controla si se ha acabado el juego
     * */
    private void iteration_of_game(Boolean acabado){
        // Saco la bola del bingo
        int bola = bingo.getBola();

        // Envio la bola a todos los clientes
        for(int i = 0; i < ingame_outs.size(); i++){
            ingame_outs.get(i).println("300, NUM " + bola);
        }

        // Espero a que todos los clientes me confirmen
        for(int i = 0; i < ingame_outs.size(); i++){
            String response = ingame_ins.get(i).readLine();
            Codop codop = new Codop(response);

            switch(codop.getCode()){
                // Se confirma
                // No hay que hacer nada
                case 301:
                break;

                // Procesar la victoria
                case 302:
                    // Hemos acabado la partida
                    acabado = true;

                    // Se notifica a todos los clientes
                    notify_win();
                break;

                // No se ha recibido
                case 430:
                    procesar_no_recibido();
                break;

                // El codigo recibido no es valido
                default:
                    procesar_no_recibido();
                break;
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
