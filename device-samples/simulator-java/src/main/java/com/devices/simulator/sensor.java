package com.devices.simulator;
import java.util.Random;

public class sensor {
    int value = 0;
    int id = 0;

    public sensor(int v, int id)
    {
        value = v;
    }

    public int getId() {
        return ((byte) id);
    }

    public int getValue() {
        // Il valore viene randomizzato in aggiunta o in differenza
        Random rand = new Random();
        return (rand.nextBoolean() ? value + rand.nextInt(2) : value - rand.nextInt(2));
    }
}

