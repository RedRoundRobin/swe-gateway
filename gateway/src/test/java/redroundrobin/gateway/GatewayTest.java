package redroundrobin.gateway;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GatewayTest {

    @Test
    void connessioneKafka() {
    }

    @Test
    void dispositivoInJSON() throws UnknownHostException {
        ArrayList<Dispositivo> lista = new ArrayList<>();

        Dispositivo d = new Dispositivo(Byte.parseByte("00000000"), InetAddress.getLocalHost(), 0);
        d.getSensori().add(new Sensore(Byte.parseByte("00000000"), Byte.parseByte("000000000")));
        d.getSensori().add(new Sensore(Byte.parseByte("00000001"), Byte.parseByte("000000001")));
        d.getSensori().add(new Sensore(Byte.parseByte("00000010"), Byte.parseByte("000000010")));
        d.getSensori().add(new Sensore(Byte.parseByte("00000011"), Byte.parseByte("000000011")));

        lista.add(d);
        lista.add(d);

        Traduttore t = new Traduttore();
        String json = t.inJSON(lista);

        assertEquals("", json);
    }
}