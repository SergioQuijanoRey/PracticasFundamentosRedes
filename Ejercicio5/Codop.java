import java.util.ArrayList;
import java.util.Arrays;

/**
 * Clase para que sea mas facil trabajar con los codigos de operacion enviados
 * en los mensajes a traves de TCP
 *
 * @author Sergio Quijano Rey
 * */
public class Codop{
    // Atributos de la clase
    //==========================================================================
    private int code;
    private ArrayList<String> args;

    // Constructores
    //==========================================================================
    /**
     * Genera un codop a traves de un codigo y los argumentos
     * @param code codigo del codop
     * @param args argumentos del codop
     * */
    Codop(int code, ArrayList<String> args){
        this.code = code;
        this.args = args;
    }

    /**
     * Genera un codop a traves de un unico string de codop codificado que hay que formatear
     * @param codop el string con el codop codificado
     * */
    Codop(String codop){
        // Separo el codop en dos partes, el codigo numerico y los argumentos
        String[] partes = codop.split(",");

        // Tomo el codigo del troceo
        try{
            this.code = Integer.parseInt(partes[0]);
        } catch(Exception e){
            System.out.println("No se ha podido parsear el codop");
            System.err.println("Se genera codop vacio");
        }

        try{
            // Troceo los argumentos a su vez separados por espacios
            String[] argumentos = partes[1].split(" ");

            // No quiero strings vacios. Esto puede ocurrir cuando un codificado
            // es: '101, ALLOW ' por el espacio inicial
            argumentos = Arrays.stream(argumentos).filter(string -> string.contains(" ") == false && string.length() > 0).toArray(String[]::new);

            // Creo el array list de argumentos con los argumentos troceados y limpiados
            args = new ArrayList<String>();
            for(String current : argumentos){
                if(current.contains(" ") == false && current != ""){
                    args.add(current);
                }
            }

        } catch(Exception e){
            System.out.println("No se ha podido parsear los argumentos del codop");
            System.err.println("Se genera codop vacio");
        }
    }

    // Metodos 
    //==========================================================================
    /**
     * Obtiene el string del codop codificado
     * @return el string codificado con el codigo y los argumentos
     * */
    public String getString(){
        String codf = Integer.toString(code);
        for(int i = 0; i < args.size(); i++){
            codf = codf + args.get(i);
        }

        return codf;
    }

    /**
     * Obtiene el codigo del codop
     * @return el codigo del codop
     * */
    public int getCode(){
        return this.code;
    }

    /**
     * Obtiene la lista de argumentos del codop
     * @return la lista con los argumentos
     * */
    public ArrayList<String> getArgs(){
        return this.args;
    }

    /**
     * Devuelve un argumento concreto
     * @param index el indice del argumento
     * @pre index < this.args.size()
     * @return el string indicado por el indice
     * */
    public String getArg(int index){
        return args.get(index);
    }

    // Metodo principal
    //==========================================================================
    /**
     * Funcion principal que sirve como testeo de esta clase
     * */
    public static void main(String[] args){
        // Codop creado a mano
        //======================================================================
        ArrayList<String> argumentos = new ArrayList<String>();
        argumentos.add("argumento1");
        argumentos.add("argumento2");
        Codop codop = new Codop(004, argumentos);

        // Se muestra la informacion
        System.out.println("Codigo numerico: " + codop.getCode());
        System.out.println("Argumentos:");
        for(int i = 0; i < codop.getArgs().size(); i++){
            System.out.println("Argumento " + i + ": " + codop.getArg(i));
        }

        System.out.println("");
        System.out.println("");
        System.out.println("");

        // Codop creado a partir de una codificacion
        //======================================================================
        String response = "101, ALLOW 001";
        codop = new Codop(response);

        // Se muestra la informacion
        System.out.println("Codigo numerico: " + codop.getCode());
        System.out.println("Argumentos:");
        for(int i = 0; i < codop.getArgs().size(); i++){
            System.out.println("Argumento " + i + ": " + codop.getArgs().get(i));
        }

        System.out.println("");
        System.out.println("");
        System.out.println("");

        // Codop con unos cuantos argumentos
        //======================================================================
        codop = new Codop("202, NUMBERS 1 2 3 4 5 6 21 23");

        // Se muestra la informacion
        System.out.println("Codigo numerico: " + codop.getCode());
        System.out.println("Argumentos:");
        for(int i = 0; i < codop.getArgs().size(); i++){
            System.out.println("Argumento " + i + ": " + codop.getArgs().get(i));
        }

        System.out.println("");
        System.out.println("");
        System.out.println("");

        // Codop de un BUG
        //======================================================================
        codop = new Codop("102, CONNECTED 0");
        System.out.println("Argumentos:");
        for(int i = 0; i < codop.getArgs().size(); i++){
            System.out.println("Argumento " + i + ": " + codop.getArgs().get(i));
        }
    }
}
