package redroundrobin.gateway;

import kafka.utils.json.JsonObject;

public class Traduttore {

    //Metodo che trasforma la stringa ricevuta in input in una stringa Json (da inviare a Kafka)
    boolean inJSON(String messaggio) {
        //Struttura JSON1: idDevice, idSensore, dato, idSensore, dato, ... , idDevice, ...
        //Struttura JSON2: idDevice, idSensore, tipoDato, dato, idSensore, tipoDato, dato, ... , idDevice, ...

        return false;
    }

    /*
    * Metodo che dato un JsonObject lo trasforma in una stringa*/
    boolean inStringa(JsonObject json){

        return false;
    }
}
