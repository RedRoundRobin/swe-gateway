package redroundrobin.gateway;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConvertitoreTest {

    private Convertitore bytesInOggetto() {
        byte[] b1 = {(byte)0,(byte)0,(byte)0,(byte)0};  // Dispositivo 0: Aggiunta Dispositivo 0 e Aggiunta Sensore 0
        byte[] b2 = {(byte)1,(byte)1,(byte)0,(byte)0};  // Dispositivo 1: Aggiunta Dispositivo 1 e Aggiunta Sensore 0
        byte[] b3 = {(byte)2,(byte)2,(byte)0,(byte)0};  // Richiesta non valida da non aggiungere
        byte[] b4 = {(byte)0,(byte)-1,(byte)0,(byte)2}; // Dispositivo 0: Modifica Sensore 0 con 2
        byte[] b5 = {(byte)3,(byte)1,(byte)0,(byte)0};  // Dispositivo 3: Aggiunta Dispositivo 3 e Aggiunta Sensore 0
        byte[] b6 = {(byte)1,(byte)1,(byte)1,(byte)0};  // Dispositivo 1: Aggiungere Sensore 1

        Convertitore c = new Convertitore();
        c.aggiungiSensore(b1);
        c.aggiungiSensore(b2);
        c.aggiungiSensore(b3);
        c.aggiungiSensore(b4);
        c.aggiungiSensore(b5);
        c.aggiungiSensore(b6);

        return c;
    }

    @Test
    void bytesInOggetto1() {
        Convertitore c = bytesInOggetto();
        assertEquals(3, c.getDispositivi().size());
    }

    @Test
    void bytesInOggetto2() {
        Convertitore c = bytesInOggetto();
        assertEquals(1, c.getDispositivi().stream()
            .filter(d -> d.getId() == 0)
            .findFirst()
            .get()
            .getSensori()
            .size()
        );
    }

    @Test
    void bytesInOggetto3() {
        Convertitore c = this.bytesInOggetto();
        assertEquals(2, c.getDispositivi().stream()
            .filter(d -> d.getId() == 1)
            .findFirst()
            .get()
            .getSensori()
            .size()
        );
    }

    @Test
    void bytesInOggetto4() {
        Convertitore c = bytesInOggetto();
        assertEquals(2, c.getDispositivi().stream()
            .filter(d -> d.getId() == 0)
            .findFirst().get()
            .getSensori().stream()
            .filter(s -> s.getId() == 0)
            .findFirst().get()
            .getDato()
        );
    }
}