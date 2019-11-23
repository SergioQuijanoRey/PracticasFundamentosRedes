import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


/**
 * Clase que representa un cliente del bingo
 *
 * @author Sergio Quijano Rey
 * */
public class Cliente{
    // Atributos de la clase
    //==========================================================================
    // Datos sobre el servidor
    int port = 8989;
    String host = "localhost";

    // Datos sobre el estado del cliente
    Integer ID;
    Boolean connected;
    Boolean inGame;
    ArrayList<Integer> numbers;

    // Conexion al servidor
    Socket servidor;
    PrintWriter out;
    BufferedReader in;

    Cliente(){
        // Establezco algunas variables
        connected = false;
        inGame = false;
        numbers = new ArrayList<Integer>();

        // Conexion al servidor
        connectToBingoServer();

        // Conseguimos los flujos de entrada salida
        try{
            out = new PrintWriter(servidor.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(servidor.getInputStream()));
        } catch(Exception e){
            System.err.println("Error intentando tomar los flujos desde el cliente");
        }

        System.out.println("Cliente inicializado con exito!");
    }

    // Metodos Publicos
    //==========================================================================
    /**
     * El cliente se conecta al servidor
     * */
    public void connectToServer(){
        // Se envia la solicitud
        out.println("100, CONNECT");

        String response;
        Codop codop;
        // Se toma la recepcion
        try{
            // Se lee el mensaje y se procesa
            response = in.readLine();
            codop = new Codop(response);

            System.out.println("Respuesta recibida: " + response);
        

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
            System.err.println("Error recibiendo un mensaje del servidor en Cliente.connectToBingoServer()");
        }

    }

    /**
     * El cliente se une a una partida
     * Si se une a la partida con exito, se entra a la partida automaticamente
     *
     * */
    public void joinGame(){
        // Comprobamos si estamos conectados
        if(!connected){
            System.err.println("No se puede iniciar una partida sin haberse conectado al servidor");
            return;
        }

        System.out.println("Pedimos al servidor entrar a una partida");

        // Se envia la solicitud para entrar a una partida
        out.println("200, JOIN " + ID);

        try{
            // Se toma y procesa el mensaje
            String response = in.readLine();
            Codop codop = new Codop(response);

            if(codop.getCode() == 201){ // Accedemos a la partida

                // Recibimos los numeros del carton del bingo
                numbers = new ArrayList<Integer>();
                response = in.readLine();
                codop = new Codop(response);

                // Se comprueba el mensaje
                if(codop.getCode() == 202){
                    for(int i = 1; i < codop.getArgs().size(); i++){
                        numbers.add(Integer.parseInt(codop.getArg(i)));
                    }

                    System.out.println("Carton de numeros obtenido!");
                    for(Integer num : numbers){
                        System.out.print(num + " ");
                    }
                    System.out.println("");

                    // Entramos al juego
                    inGame = true;
                    this.playGame();
                }else{
                    System.err.println("Se ha leido un codigo de respuesta no valido");
                    System.err.println("No hemos entrado al juego");
                }



            }else if(codop.getCode() == 420){ // Partida con demasiados jugadores
                System.err.println("El servidor ya no puede aceptar mas jugadores");
            }else if(codop.getCode() == 421){
                System.err.println("El servidor no esta en fase de aceptar jugadores");
                System.err.println("O ya hay una partida en marcha o no se ha iniciado la fase de aceptacion");
            }else{
                System.err.println("Se ha leido un codigo de respuesta no valido");
                System.err.println("Codigo de respuesta: " + codop.getCode());

            }
        } catch(Exception e){
            System.err.println("Error leyendo del socket en Cliente.joinGame()");
            System.err.println("Codigo del error: " + e);
        }
    }

    /**
     * El cliente juega una partida, con un carton de numeros ya asignado
     * */
    public void playGame(){
        try{
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
                // Se lee un mensaje
                String response = in.readLine();
                Codop codop = new Codop(response);
                
                if(codop.getCode() == 300){ // Se recibe un numero
                    // Se toma el numero que nos han dado
                    Integer current_number = Integer.parseInt(codop.getArg(1));

                    // Se procesa el numero
                    processIncomingNumber(current_number, numbers);


                }else if(codop.getCode() == 303){ // El servidor notifica que se ha acabado el juego
                    // Recibimos quien es el ganador
                    response = in.readLine();
                    codop = new Codop(response);

                    // Comprobamos la respuesta
                    if(codop.getCode() == 304){
                        Integer winner_id = Integer.parseInt(codop.getArg(1));

                        if(winner_id == this.ID){
                            System.out.println("FELICIDADES, HAS GANADO LA PARTIDA");
                        }else{
                            System.out.println("El ganador de la partida ha sido el cliente con id: " + winner_id);
                        }
                    }else{
                        System.err.println("Codigo recibido desconocido");
                        System.err.println("No se puede saber quien es el ganador");
                    }

                    // Notificamos que ya hemos acabado la partida
                    inGame = false;

                }else{ // No se ha podido recibir un codigo valido
                    System.err.println("No se ha podido recibir un mensaje valido");
                    System.err.println("Se pide al servidor que vuelva a mandar el numero");

                    // Enviar codigo 403 y volver a pedir el numero
                    Boolean conseguido = false;
                    while(conseguido == false){
                        // Se envia el mensaje
                        out.println("430, NOT RECEIVED");

                        // Se espera a recibir contestacion
                        response = in.readLine();
                        codop = new Codop(response);

                        // Se recibe de forma correcta el numero
                        if(codop.getCode() == 300){
                            conseguido = true;
                        }
                    }

                    System.err.println("El servidor ha conseguido volver a enviarnos el numero");

                    // Se procesa el numero
                    Integer current_number = Integer.parseInt(codop.getArgs().get(0));
                    processIncomingNumber(current_number, numbers);
                }
            }

            // Se ha salido de la partida
            inGame = false;
        }catch(Exception e){
            System.err.println("Error en las comunicaciones de Cliente.joinGame()");
            System.err.println("Codigo del error: " + e);
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

    /**
     * Procesa un numero recibido por el servidor en una partida
     * @param number el numero recibido del servidor
     * @param numbers el carton de numeros con el que juganos, SE MODIFICA
     *
     * Se quita el numero del carton en caso de que este
     * Se envia el mensaje de confirmacion al servidor:
     *  - 301, RECEIVED si quedan numeros
     *  - 302, BINGO si no quedan numeros, porque habriamos ganado
     * */
    private void processIncomingNumber(Integer number, ArrayList<Integer> numbers){
                // Lo quitamos del carton
                numbers.removeIf(value -> value == number);

                // Confirmamos al servidor
                if(numbers.size() == 0){
                    // Indicamos que hemos ganado
                    out.println("302, BINGO");
                }else{
                    // Indicamos que hemos recibido el mensaje
                    out.println("301, RECEIVED");
                }
    }

    // Funcion principal
    //==========================================================================
    /**
     * Funcion principal del cliente
     *
     * Sirve de testeo del programa creado
     * */
    public static void main(String[] args){
        // Se crea el cliente
        Cliente cliente = new Cliente();

        // Nos conectamos al servidor
        cliente.connectToServer();

        // Intentamos echar una partida
        cliente.joinGame();

        // Mensajes finales
        System.out.println("GRACIAS POR JUGAR A NUESTRO BINGO!!!");
        System.out.println("ESPERAMOS VERLE PRONTO :D");
    }
}
