package com.devices.simulator;

import java.util.List;

public class Dispositivo {
    private int id;
    private List<Sensore> sensori;

    public Dispositivo(int id, List<Sensore> sensori) {
        this.id = id;
        this.sensori = sensori;
    }

    public int ottieniId() {
        return id;
    }

    public List<Sensore> ottieniSensori() {
        return sensori;
    }
}
