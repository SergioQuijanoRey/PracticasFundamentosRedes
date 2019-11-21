package Bingo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ProcesadorBingo extends Thread{

private Socket socketServicio;
private BufferedReader inReader;
private PrintWriter outPrinter;
private Bingo bingo;
private Boolean fin = false;
private Boolean partida_empezada = false;
private int num_jug_max;
private int num_jug;
private ArrayList<Integer> ids;
private contador_id = 0;
private ArrayList<Integer> jugadores; 

ProcesadorBingo(Socket socketServicio,int num_jug){

    this.socketServicio = socketServicio;
    ids = new ArrayList<Integer>;
    jugadores = ArrayList<Integer>;
    bingo = new Bingo(num_jug * 16); //cada cartón tendrá 16 casillas;
    this.num_jug_max = num_jug;

}

@Override
public void run(){

    String datosRecibidos;
    String datosEnviados;

    try{

        inReader = newBufferedReader(new InputStreamReader(socketServicio.getInputStream()));
        outPrinter = PrintWriter(socketServicio.getOutputStream(),true);

        while(true){
            fin = false;
            while(!partida_empezada){ //Aun no estamos en partida

                datosRecibidos = inReader.readLine();
                Codop codop = new Codop(datosRecibidos);

                int opcion = codop.getCode();

                switch(opcion){

                    case "100"
                        datosEnviados = " 101,ALLOW +"+ contador_id;
                        outPrinter.println(datosEnviados);
                    break;

                    case "102"
                        ids.add(contador_id);   //solo añadimos el ID a la lista tras confirmarse la conexion
                        contador_id = contador_id + 1;
                    break;

                    case "200"
                        if(num_jug<num_jug_max){
                            jugadores.add(datosRecibidos.getArgs.split("+")[1]);
                            datosEnviados = "201,JOINED";
                            num_jug = num_jug +1;
                        }
                        else{
                            datosEnviados = "420,FULL";
                        }
                        outPrinter.println(datosEnviados);
                        
                        if(num_jug == num_jug_max){
                            partida_empezada = true;
                        }
                        
                    break;

                    case "500"
                        int id = datosRecibidos.getArgs.split("+")[1].parseInt();
                        if(ids.contains(id){
                            ids.remove(id);
                            datosEnviados = "501,BYE";
                             
                        }
                        else{
                            datosEnviados = "451, CANNOT DISCONNECT";
                        }
                        outPrinter.println(datosEnviados);
                }

                while(!fin){ // Ya estamos en partida


                }



            }
        }
        



    }catch( IOException e){
        System.err.println("Error al obterner los flujos de entrada/salida.");
    }



}