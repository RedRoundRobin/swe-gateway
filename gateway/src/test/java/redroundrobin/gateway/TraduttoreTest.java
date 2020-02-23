package redroundrobin.gateway;

import org.junit.jupiter.api.Test;

import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TraduttoreTest {

    @Test
    void dispositivoInJSON() throws UnknownHostException {
        Dispositivo d = new Dispositivo(0);
        d.ottieniSensori().add(new Sensore(0, 0));
        d.ottieniSensori().add(new Sensore(1, 1));
        d.ottieniSensori().add(new Sensore(2, 2));
        d.ottieniSensori().add(new Sensore(3, 3));


        Traduttore t = new Traduttore();

        t.ottieniDispositivi().add(d);
        t.ottieniDispositivi().add(d);

        String json = t.ottieniJSON();

        assertEquals("[{\"id\":0,\"sensori\":[{\"id\":0,\"dato\":0},{\"id\":1,\"dato\":1},{\"id\":2,\"dato\":2},{\"id\":3,\"dato\":3}]},{\"id\":0,\"sensori\":[{\"id\":0,\"dato\":0},{\"id\":1,\"dato\":1},{\"id\":2,\"dato\":2},{\"id\":3,\"dato\":3}]}]", json);
    }

    private Traduttore bytesInOggetto() {
        byte[] b1 = {(byte)0,(byte)0,(byte)0,(byte)0};  // Dispositivo 0: Aggiunta Dispositivo 0 e Aggiunta Sensore 0
        byte[] b2 = {(byte)1,(byte)1,(byte)0,(byte)0};  // Dispositivo 1: Aggiunta Dispositivo 1 e Aggiunta Sensore 0
        byte[] b3 = {(byte)2,(byte)2,(byte)0,(byte)0};  // Richiesta non valida da non aggiungere
        byte[] b4 = {(byte)0,(byte)-1,(byte)0,(byte)2}; // Dispositivo 0: Modifica Sensore 0 con 2
        byte[] b5 = {(byte)3,(byte)1,(byte)0,(byte)0};  // Dispositivo 3: Aggiunta Dispositivo 3 e Aggiunta Sensore 0
        byte[] b6 = {(byte)1,(byte)1,(byte)1,(byte)0};  // Dispositivo 1: Aggiungere Sensore 1

        Traduttore t = new Traduttore();
        t.aggiungiSensore(b1);
        t.aggiungiSensore(b2);
        t.aggiungiSensore(b3);
        t.aggiungiSensore(b4);
        t.aggiungiSensore(b5);
        t.aggiungiSensore(b6);

        return t;
    }

    @Test
    void bytesInOggetto1() {
        Traduttore t = bytesInOggetto();
        assertEquals(3, t.ottieniDispositivi().size());
    }

    @Test
    void bytesInOggetto2() {
        Traduttore t = bytesInOggetto();
        assertEquals(1, t.ottieniDispositivi().stream()
                .filter(d -> d.ottieniId() == 0)
                .findFirst()
                .get()
                .ottieniSensori()
                .size()
        );
    }

    @Test
    void bytesInOggetto3() {
        Traduttore t = this.bytesInOggetto();
        assertEquals(2, t.ottieniDispositivi().stream()
                .filter(d -> d.ottieniId() == 1)
                .findFirst()
                .get()
                .ottieniSensori()
                .size()
        );
    }

    @Test
    void bytesInOggetto4() {
        Traduttore t = bytesInOggetto();
        assertEquals(2, t.ottieniDispositivi().stream()
                .filter(d -> d.ottieniId() == 0)
                .findFirst().get()
                .ottieniSensori().stream()
                .filter(s -> s.ottieniId() == 0)
                .findFirst().get()
                .ottieniDato()
        );
    }
}