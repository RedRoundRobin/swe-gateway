package redroundrobin.gateway;

import java.util.ArrayList;

/*
    Convertitore da bytes a objects
 */
public class Convertitore {
    public static final int ERR = -1;
    public static final int REQ = 0;
    public static final int RES = 1;

    private ArrayList<Dispositivo> lista;

    public Convertitore() {
        lista = new ArrayList<>();
    }

    public void aggiungiSensore( byte[] b ) {
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
        }
    }

    public ArrayList<Dispositivo> getDispositivi() {
        return this.lista;
    }
}
