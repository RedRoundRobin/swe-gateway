package redroundrobin.gateway;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TraduttoreTest {

    @Test
    void dispositivoInJSON() throws UnknownHostException {
        ArrayList<Dispositivo> lista = new ArrayList<>();

        Dispositivo d = new Dispositivo(0, InetAddress.getLocalHost(), 0);
        d.getSensori().add(new Sensore(0, 0));
        d.getSensori().add(new Sensore(1, 1));
        d.getSensori().add(new Sensore(2, 2));
        d.getSensori().add(new Sensore(3, 3));

        lista.add(d);
        lista.add(d);

        Traduttore t = new Traduttore();
        String json = t.inJSON(lista);

        assertEquals("", json);
    }
}