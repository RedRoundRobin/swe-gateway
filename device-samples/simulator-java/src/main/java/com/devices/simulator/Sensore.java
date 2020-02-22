package com.devices.simulator;
import java.util.Random;

public class Sensore {
    private int id;
    private int valore;

    public Sensore(int id, int valore) {
        this.id = id;
        this.valore = valore;
    }

    public int ottieniId() {
        return id;
    }

    // Il valore viene generato causalmente in aggiunta o in differenza
    public int ottieniValore() {
        Random casuale = new Random();
        return casuale.nextBoolean() ? valore + casuale.nextInt(2) : valore - casuale.nextInt(2);
    }
}

