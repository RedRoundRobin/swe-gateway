package com.redroundrobin.thirema.gateway;

import com.redroundrobin.thirema.gateway.models.Device;
import com.redroundrobin.thirema.gateway.models.Sensor;
import com.redroundrobin.thirema.gateway.utils.Translator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TranslatorTest {
    private Translator bytesInOggetto() {
        byte[] pacchetto1 = {(byte) 0, (byte) 0, (byte) 0, (byte) 0};  // Device 0: Aggiunta Device 0 e Aggiunta Sensor 0
        byte[] pacchetto2 = {(byte) 1, (byte) 1, (byte) 0, (byte) 0};  // Device 1: Aggiunta Device 1 e Aggiunta Sensor 0
        byte[] pacchetto3 = {(byte) 2, (byte) 2, (byte) 0, (byte) 0};  // Richiesta non valida da non aggiungere
        byte[] pacchetto4 = {(byte) 0, (byte) -1, (byte) 0, (byte) 2}; // Device 0: Modifica Sensor 0 con 2
        byte[] pacchetto5 = {(byte) 3, (byte) 1, (byte) 0, (byte) 0};  // Device 3: Aggiunta Device 3 e Aggiunta Sensor 0
        byte[] pacchetto6 = {(byte) 1, (byte) 1, (byte) 1, (byte) 0};  // Device 1: Aggiungere Sensor 1

        Translator translator = new Translator();

        translator.addSensor(pacchetto1);
        translator.addSensor(pacchetto2);
        translator.addSensor(pacchetto3);
        translator.addSensor(pacchetto4);
        translator.addSensor(pacchetto5);
        translator.addSensor(pacchetto6);

        return translator;
    }

    @Test
    public void dispositivoInJSON() {
        List<Sensor> sensori = new ArrayList<>(Arrays.asList(new Sensor(0, 0), new Sensor(1, 1), new Sensor(2, 2), new Sensor(3, 3)));
        Device device = new Device(0, sensori);

        Translator translator = new Translator();

        translator.getDevices().add(device);
        translator.getDevices().add(device);

        String json = translator.getJSON();
        assertEquals("[{\"id\":0,\"sensori\":[{\"id\":0,\"dato\":0},{\"id\":1,\"dato\":1},{\"id\":2,\"dato\":2},{\"id\":3,\"dato\":3}]},{\"id\":0,\"sensori\":[{\"id\":0,\"dato\":0},{\"id\":1,\"dato\":1},{\"id\":2,\"dato\":2},{\"id\":3,\"dato\":3}]}]", json);
    }

    @Test
    public void bytesInOggetto1() {
        Translator translator = bytesInOggetto();
        assertEquals(3, translator.getDevices().size());
    }

    @Test
    public void bytesInOggetto2() {
        Translator translator = bytesInOggetto();
        assertEquals(1, translator.getDevices().stream()
                .filter(device -> device.getId() == 0)
                .findFirst()
                .get()
                .getSensors()
                .size()
        );
    }

    @Test
    public void bytesInOggetto3() {
        Translator translator = bytesInOggetto();
        assertEquals(2, translator.getDevices().stream()
                .filter(device -> device.getId() == 1)
                .findFirst()
                .get()
                .getSensors()
                .size()
        );
    }

    @Test
    public void bytesInOggetto4() {
        Translator translator = bytesInOggetto();
        assertEquals(2, translator.getDevices().stream()
                .filter(device -> device.getId() == 0)
                .findFirst().get()
                .getSensors().stream()
                .filter(sensore -> sensore.getId() == 0)
                .findFirst().get()
                .getData()
        );
    }
}