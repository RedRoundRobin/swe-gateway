package com.redroundrobin.gateway;

import java.util.ArrayList;
import java.util.List;

public class Dispositivo {
    private int id;
    private long timestamp = 0;
    private List<Sensore> sensori;

    public Dispositivo(int id) {
        this.id = id;
        this.sensori = new ArrayList<>();
    }

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

    public void impostaTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
