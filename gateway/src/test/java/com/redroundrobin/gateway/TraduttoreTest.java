package com.redroundrobin.gateway;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TraduttoreTest {
    private Traduttore bytesInOggetto() {
        byte[] pacchetto1 = {(byte) 0, (byte) 0, (byte) 0, (byte) 0};  // Dispositivo 0: Aggiunta Dispositivo 0 e Aggiunta Sensore 0
        byte[] pacchetto2 = {(byte) 1, (byte) 1, (byte) 0, (byte) 0};  // Dispositivo 1: Aggiunta Dispositivo 1 e Aggiunta Sensore 0
        byte[] pacchetto3 = {(byte) 2, (byte) 2, (byte) 0, (byte) 0};  // Richiesta non valida da non aggiungere
        byte[] pacchetto4 = {(byte) 0, (byte) -1, (byte) 0, (byte) 2}; // Dispositivo 0: Modifica Sensore 0 con 2
        byte[] pacchetto5 = {(byte) 3, (byte) 1, (byte) 0, (byte) 0};  // Dispositivo 3: Aggiunta Dispositivo 3 e Aggiunta Sensore 0
        byte[] pacchetto6 = {(byte) 1, (byte) 1, (byte) 1, (byte) 0};  // Dispositivo 1: Aggiungere Sensore 1

        Traduttore traduttore = new Traduttore();

        traduttore.aggiungiSensore(pacchetto1);
        traduttore.aggiungiSensore(pacchetto2);
        traduttore.aggiungiSensore(pacchetto3);
        traduttore.aggiungiSensore(pacchetto4);
        traduttore.aggiungiSensore(pacchetto5);
        traduttore.aggiungiSensore(pacchetto6);

        return traduttore;
    }

    @Test
    public void dispositivoInJSON() {
        List<Sensore> sensori = new ArrayList<>(Arrays.asList(new Sensore(0, 0), new Sensore(1, 1), new Sensore(2, 2), new Sensore(3, 3)));
        Dispositivo dispositivo = new Dispositivo(0, sensori);

        Traduttore traduttore = new Traduttore();

        traduttore.ottieniDispositivi().add(dispositivo);
        traduttore.ottieniDispositivi().add(dispositivo);

        String json = traduttore.ottieniJSON();
        assertEquals("[{\"id\":0,\"sensori\":[{\"id\":0,\"dato\":0},{\"id\":1,\"dato\":1},{\"id\":2,\"dato\":2},{\"id\":3,\"dato\":3}]},{\"id\":0,\"sensori\":[{\"id\":0,\"dato\":0},{\"id\":1,\"dato\":1},{\"id\":2,\"dato\":2},{\"id\":3,\"dato\":3}]}]", json);
    }

    @Test
    public void bytesInOggetto1() {
        Traduttore traduttore = bytesInOggetto();
        assertEquals(3, traduttore.ottieniDispositivi().size());
    }

    @Test
    public void bytesInOggetto2() {
        Traduttore traduttore = bytesInOggetto();
        assertEquals(1, traduttore.ottieniDispositivi().stream()
                .filter(dispositivo -> dispositivo.ottieniId() == 0)
                .findFirst()
                .get()
                .ottieniSensori()
                .size()
        );
    }

    @Test
    public void bytesInOggetto3() {
        Traduttore traduttore = bytesInOggetto();
        assertEquals(2, traduttore.ottieniDispositivi().stream()
                .filter(dispositivo -> dispositivo.ottieniId() == 1)
                .findFirst()
                .get()
                .ottieniSensori()
                .size()
        );
    }

    @Test
    public void bytesInOggetto4() {
        Traduttore traduttore = bytesInOggetto();
        assertEquals(2, traduttore.ottieniDispositivi().stream()
                .filter(dispositivo -> dispositivo.ottieniId() == 0)
                .findFirst().get()
                .ottieniSensori().stream()
                .filter(sensore -> sensore.ottieniId() == 0)
                .findFirst().get()
                .ottieniDato()
        );
    }
}