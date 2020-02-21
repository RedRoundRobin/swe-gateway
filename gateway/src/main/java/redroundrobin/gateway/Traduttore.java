package redroundrobin.gateway;

import com.google.gson.Gson;
import kafka.utils.json.JsonObject;

import java.util.ArrayList;

public class Traduttore {
    public static final int ERR = 64;
    public static final int REQ = 0;
    public static final int RES = 127;

    private ArrayList<Dispositivo> lista;

    public Traduttore() {
        lista = new ArrayList<>();
    }

    /*
        Viene controllato il pacchetto di risposta contiene i dati corretti.
        Se passa i test allora il pacchetto viene aggiunto alla lista di pacchetti.
    */
    public boolean aggiungiSensore( byte[] b ) {
        if( b[1] == ERR || b[1] == REQ || b[1] == RES ) {
            int id = Byte.toUnsignedInt(b[0]);

            int sensore = Byte.toUnsignedInt(b[2]);
            int dato = Byte.toUnsignedInt(b[3]);

            if( lista.stream().anyMatch(d -> d.getId() == id) ) {
                Dispositivo d = lista.stream().filter(i -> i.getId() == id).findFirst().get();

                if( d.getSensori().stream().anyMatch(s -> s.getId() == sensore) ) {
                    d.getSensori().stream().filter(s -> s.getId() == sensore).findFirst().get().setDato(dato);
                } else {
                    d.getSensori().add(new Sensore(sensore, dato));
                }
            } else {
                Dispositivo d = new Dispositivo(id);
                d.getSensori().add(new Sensore(sensore, dato));

                lista.add(d);
            }
            return true;
        }else {
            return false;
        }
    }

    public ArrayList<Dispositivo> getDispositivi() {
        return this.lista;
    }

    /*
    Ritorna la lista dei dati raccolti in formato JSON
    */
    public String getJSON() {
        Gson gson = new Gson();
        return gson.toJson(this.lista);
    }

}
