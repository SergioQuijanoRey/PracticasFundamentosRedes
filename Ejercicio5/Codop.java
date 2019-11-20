import java.util.ArrayList;
package Bingo;

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

        String[] partes = codop.split(",");
        try{
            this.code = Integer.parseInt(partes[0]);
        } catch(Exception e){
            System.out.println("No se ha podido parsear el codop");
            System.err.println("Se genera codop vacio");
        }

        try{
            this.args = partes[1].split(" ");
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
        return String(this.code) + String(this.string);
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
}
