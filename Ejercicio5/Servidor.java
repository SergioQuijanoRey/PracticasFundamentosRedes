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
import java.io.InputStreamReader;


/**
* Clase que representa el servidor del juego del bingo
*
* @author Sergio Quijano Rey
* */
public class Servidor{

    // Atributos de la clase
    //==========================================================================

    // Conexiones
    private ServerSocket socketServidor;     //> Socket del servidor
    private ArrayList<Socket> conexiones;    //> Todos los clientes que tenemos conectados
    private ArrayList<BufferedReader> ins;  //> Flujos de entrada
    private ArrayList<PrintWriter> outs;  //> Flujos de entrada
    private int port = 8989 ;
    private static int timeout = 100 * 1000;
    
    // Estado del servidor
    private Boolean inGame;
    private Boolean inStage;
    private Integer current_id;
    private int num_jugadores;  //> Indica cuantos jugadores se necesitan para jugar una partida
    private Bingo bingo;

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
        idx_in_game = new ArrayList<Integer>();

        // Creo un bingo
        bingo = new Bingo(100);

        // Al inicio no hay conexiones a los clientes ni flujos
        this.conexiones = new ArrayList<Socket>();
        ins = new ArrayList<BufferedReader>();
        outs = new ArrayList<PrintWriter>();

        // Establecemos que no estamos en una partida
        inGame = false;
        inStage = false;

        // Creamos el socket servidor con un timeout dado
        try{
            socketServidor = new ServerSocket(port);
            socketServidor.setSoTimeout(timeout);
        }catch(Exception e){
            System.err.println("Error al crear el socket del servidor");
        }

        // Mensajes de informacion
        System.out.println("=== Servidor inicializado correctamente!");
        System.out.println("=== Se pasa a ejecutar el bucle infinito");

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
            System.out.println("=== Se espera a una conexion");
            // Intentamos conectar a un nuevo cliente
            // El proceso del servidor se bloquea durante un tiempo dado por el timeout
            connect_new_client();
            System.out.println("=== Se termina de esperar a una conexion");

            // Si estamos en una partida, hacemos iteraciones del juego hasta que
            // se acabe el juego
            if(inGame){
                System.out.println("=== Entramos al bucle del juego");
                while(inGame){
                    System.out.println("=== Se entra a una iteracion del juego");
                    // Realizamos una iteracion del juego
                    iteration_of_game();
                    System.out.println("=== Se sale de una iteracion del juego");
                }
            }else{
                System.out.println("=== Se hace una ronda de leer los mensajes de todos los clientes");
                // Leemos todos los mensajes de los clientes y consideramos todos
                // los posibles mensajes de esta fase
                read_from_all();
                System.out.println("=== Se termina de hacer una ronda de lectura de todos los clientes");
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
            try{
                // Tomamos el mensaje
                String response = ins.get(i).readLine();
                Codop codop = new Codop(response);
                // Procesamos el mensaje
                process_message(i, codop);
            }catch(Exception e){
                System.err.println("Error leyendo del socket del cliente " + i + " en Servidor.read_from_all()");
            }

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
                    
                    // Añadimos al cliente al juego, en caso de que hay hueco
                    add_client_to_game(index);

                // No estamos en stage
                }else{
                    // Si tenemos suficientes jugadores, iniciamos el stage
                    if(conexiones.size() >= num_jugadores){
                        inStage = true;
                        add_client_to_game(index);

                    // No tenemos suficientes jugadores
                    // No iniciamos el stage
                    }else{
                        // No estamos en stage, se lo comunicamos al cliente
                        outs.get(index).println("421, NOT IN STAGE");
                    }   
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

            // El usuario confirma un numero pero no esta en partida
            case 301:

            // El usuario canta bingo pero no esta en partida
            case 302:

            // El servidor no recibe un numero, pero no esta en partida
            case 430:

                System.err.println("ERROR! El cliente ha enviado un mensaje de partida, pero no estamos en una partida!");
                System.err.println("No se hace nada al respecto");

            break;

            // Mensaje desconocido
            default:
                System.err.println("ERROR! Mensaje desconocido");
                System.err.println("No se hace nada la respecto");
            break;

        }
    }

    /**
     * En la fase de Stage, se añade un cliente a la partida
     * @param index el indice del cliente que se añade a la partida
     *
     * El cliente solo se añade si hay hueco. Si no hay hueco, se le envia
     * el mensaje de error oportuno
     *
     * Una vez añadido se le envian los numeros
     * */
    private void add_client_to_game(int index){
        if(idx_in_game.size() < num_jugadores){
            // Se añade a la lista de indices de jugadores
            idx_in_game.add(index);

            // Se le comunica al cliente
            outs.get(index).println("201, JOINED");

            // Se genera el mensaje con el carton de 15 numeros
            Bingo carton = new Bingo(100);
            String response = "202, NUMBERS ";
            for(int i = 0; i < 15; i++){
                response = response + " " + carton.getBola();

            }

            // Se envia el carton
            outs.get(index).println(response);

            // Comprobamos si hay que iniciar la partida
            if(idx_in_game.size() == num_jugadores){
                inGame = true;
                inStage = false;
            }
        }else{
            // No tenemos hueco, se comunica al cliente
            outs.get(index).println("420, FULL");
        }
    }

    /**
     * Intentamos que se conecte un nuevo cliente al servidor
     * Durante el tiempo establecido por el timeout
     * */
    private void connect_new_client(){
            try{
                // Espero a recibir una conexion
                // Se bloquea durante el timeout establecido
                Socket current_conexion = socketServidor.accept();

                // Se procesa la nueva conexion
                process_new_connection(current_conexion);

            }catch(SocketTimeoutException timeout){
                // No hacemos nada por el timeout
                // Para que podamos trabajar como un servidor iterativo
            }catch(Exception e){
                System.err.println("Error al establecer un nuevo socket con el cliente en Servidor.run()");
            }
    }

    /**
     * Se procesa una nueva conexion de un cliente
     * @param current_conexion el socket que acaba de abrir el nuevo cliente
     * */
    private void process_new_connection(Socket current_conexion){
        try{
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
                remove_last_connected();
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
                    remove_last_connected();
                }else{
                    System.out.println("Nuevo cliente conectado con exito");
                }
            }

        }catch(Exception e){
            System.err.println("Error procesando una nueva conexion al servidor");
        }
    }

    /**
     * Se elimina al ultimo usuario que se ha conectado
     * */
    private void remove_last_connected(){
        conexiones.remove(conexiones.size() - 1);
        outs.remove(outs.size() - 1);
        ins.remove(ins.size() - 1);
    }


    /**
     * Se hace una iteracion del juego
     * */
    private void iteration_of_game(){
        // Saco la bola del bingo
        int bola = bingo.getBola();

        // Envio la bola a todos los clientes
        for(Integer current_index : idx_in_game){
            outs.get(current_index).println("300, NUM " + bola);
        }
        
        // Espero a que todos los clientes me confirmen
        for(Integer current_index : idx_in_game){
            try{

            // Tomamos la respuesta del cliente
            String response = ins.get(current_index).readLine();
            Codop codop = new Codop(response);

            // Procesamos la respuesta
            process_message_in_game(current_index, codop, bola);
            }catch(Exception e){
                System.err.println("Error leyendo mensaje del cliente " + current_index + " en Servidor.iteration_of_game()");
            }
        }
    }

    /**
     * Se procesa un mensaje recibido por un cliente cuando esta en una partida
     * @param index el indice del cliente que envia el mensaje
     * @param codop el mensaje recibido ya procesado
     * @param bola el ultimo numero que se ha sacado en esta iteracion
     * */
    private void process_message_in_game(int index, Codop codop, int bola){
        switch(codop.getCode()){
            // El cliente confirma que ha recibido la bola
            // No se hace nada
            case 301:
            break;

            // El cliente confirma que ha ganado
            case 302:
                have_a_winner(index);
            break;

            // El cliente no ha recibido correctamente la bola, hay que volver a mandarla
            case 430:
                resend_number(index, bola);
            break;

            // No hay mas mensajes validos en una iteracion de juego
            // No se hace nada. Esto puede provocar un bloque de todo el servidor
            default:
                System.err.println("El mensaje recibido por el cliente " + index + " no tiene sentido");
                System.err.println("No se hace nada");
            break;
        }

    }

    /**
     * El cliente no ha recibido bien el numero, hay que volverlo a enviar hasta
     * que confirme que lo ha recibido
     * @param index el indice del cliente
     * @param bola el numero que se vuelve a enviar
     * */
    private void resend_number(int index, int bola){
        Boolean confirmed = false;
        while(confirmed == false){
            try{
            outs.get(index).println("300, NUM " + bola);
            
            String response = ins.get(index).readLine();
            Codop codop = new Codop(response);

            // El cliente ha confirmado el numero
            if(codop.getCode() == 301){
                confirmed = true;
            }
            }catch(Exception e){
                System.err.println("Error leyendo la confirmacion del cliente " + index + " en Servidor.resend_number()");
            }
        }

    }

    /**
     * Un cliente ha cantado bingo
     * @param index el indice del cliente que canta bingo
     *
     * Se notifica a todos los jugadores sobre quien ha ganado
     * Se acaba la partida
     * */
    private void have_a_winner(int index){
        inGame = false;

        for(Integer current_index : idx_in_game){
            outs.get(current_index).println("303, END");
            outs.get(current_index).println("304, WINNER " + index);
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
