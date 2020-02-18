package redroundrobin.gateway;

import kafka.utils.json.JsonObject;

public class Traduttore {

    //trasformazione della stringa ricevuta in input dai device in una stringa Json da inviare a Kafka
    boolean inJSON(String messaggio) {
        //Struttura JSON1: idDevice, idSensore, dato, idSensore, dato, ... , idDevice, ...
        //Struttura JSON2: idDevice, idSensore, tipoDato, dato, idSensore, tipoDato, dato, ... , idDevice, ...

        return false;
    }

    boolean inStringa(JsonObject json){

        return false;
    }
}
