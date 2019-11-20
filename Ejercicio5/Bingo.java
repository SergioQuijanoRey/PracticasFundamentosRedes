import java.util.ArrayList;
import java.util.Random;
package Bingo;

/**
 * Clase que representa el juego del bingo
 *
 * El proposito de esta clase es que vaya sacando numeros aleatorios entre un rango
 * dado sin que estos numeros se repitan
 *
 * @author Sergio Quijano Rey
 * */
public class Bingo{
    // Atributos de la clase
    //==========================================================================
    ArrayList<int> bolas;
    int num_bolas;
    Random random;

    // Constructores
    //==========================================================================
    /**
     * Construye un bingo con un numero de bolas fijo
     * @param num numero maximo de bolas
     * */
    Bingo(int num){

        // Tomo el numero maximo de bolas
        num_bolas = num;

        // Uso el metodo privado
        createBolas(num_bolas);

        // Generador de numeros aleatorios
        random = new Random();
    }

    /**
     * Construye un bingo que por defecto tiene 100 numeros
     * */
    Bingo(){
        this(100);
    }

    // Metodos
    //==========================================================================
    public void recreate(){
        createBolas(num_bolas);
    }

    public int getBola(){
        // Tomo un valor del array y lo saco
        int index = random.nextInt();
        int value = bolas.remove(index);

        // Devuelvo el valor
        return value;
    }

    // Metodos privados
    //==========================================================================
    private void createBolas(int num){
        this.bolas = IntStream.rangeClosed(0, num).boxed().collect(Collectors.toArrayList());
    }

    // Testeo de la clase

}
