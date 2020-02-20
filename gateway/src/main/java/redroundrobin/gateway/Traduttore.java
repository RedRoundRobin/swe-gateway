package redroundrobin.gateway;

import com.google.gson.Gson;
import kafka.utils.json.JsonObject;

import java.util.ArrayList;

public class Traduttore {

    //Metodo che trasforma la stringa ricevuta in input in una stringa Json (da inviare a Kafka)
    boolean inJSON(String messaggio) {
        //Struttura JSON1: idDevice, idSensore, dato, idSensore, dato, ... , idDevice, ...

        return false;
    }

    /*
    * Metodo che dato un JsonObject lo trasforma in una stringa
    */
    boolean inStringa(JsonObject json){
        return false;
    }

    public String inJSON(ArrayList<Dispositivo> lista) {
        Gson gson = new Gson();
        return gson.toJson(lista);
    }


}
