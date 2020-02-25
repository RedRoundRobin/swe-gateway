package redroundrobin.gateway;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Traduttore {
    private static final int ERR = -1;
    private static final int REQ = 0;
    private static final int RES = 1;

    private List<Dispositivo> lista;

    public Traduttore() {
        lista = new ArrayList<>();
    }

    // Ritorna la lista dei dispositivi
    public List<Dispositivo> ottieniDispositivi() {
        return lista;
    }

    // Ritorna la lista dei dati raccolti in formato JSON
    public String ottieniJSON() {
        return (new Gson()).toJson(lista);
    }

    // Viene controllato il pacchetto di risposta contiene i dati corretti, se passa i test allora il pacchetto viene aggiunto alla lista di pacchetti
    public boolean aggiungiSensore(byte[] pacchetto) {
        if (pacchetto[1] == ERR || pacchetto[1] == REQ || pacchetto[1] == RES) {
            int id = Byte.toUnsignedInt(pacchetto[0]);
            int idSensore = Byte.toUnsignedInt(pacchetto[2]);
            int dato = Byte.toUnsignedInt(pacchetto[3]);
            long tempo = System.currentTimeMillis();

            Optional<Dispositivo> dispositivoOpzionale = lista.stream().filter(dispositivoAttuale -> dispositivoAttuale.ottieniId() == id).findFirst();
            //Controllo se il dispositivo è già presente nella lista dei dispositivi accumulati dal traduttore
            if (dispositivoOpzionale.isPresent()) {
                Dispositivo dispositivo = dispositivoOpzionale.get();
                dispositivo.impostaTimestamp(tempo);

                Optional<Sensore> sensoreOpzionale = dispositivo.ottieniSensori().stream().filter(sensoreAttuale -> sensoreAttuale.ottieniId() == idSensore).findFirst();
                //Controllo se il sensore è già stato aggiunto alla lista
                if (sensoreOpzionale.isPresent()) {
                    sensoreOpzionale.get().impostaDato(dato);
                    sensoreOpzionale.get().impostaTimestamp(tempo);
                } else {
                    Sensore sensore = new Sensore(idSensore, dato);
                    sensore.impostaTimestamp(tempo);
                    dispositivo.ottieniSensori().add(sensore);
                }
            } else {//Il dispositivo non è ancora presente nella lista
                Dispositivo dispositivo = new Dispositivo(id);
                dispositivo.impostaTimestamp(tempo);
                Sensore sensore = new Sensore(idSensore, dato);
                sensore.impostaTimestamp(tempo);
                dispositivo.ottieniSensori().add(sensore);
                lista.add(dispositivo);
            }

            return true;
        } else {
            return false;
        }
    }
}
