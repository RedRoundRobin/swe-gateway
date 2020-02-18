package com.devices.simulator;
import java.util.Random;

public class sensor {
    String name = "";
    int value = 0;
    int id = 0;

    public sensor(String n, int v, int id)
    {
        name = n;
        value = v;
    }

    public String getName() {
        return name;
    }

    public byte getId() {
        return ((byte) id);
    }

    public byte getValue() {
        // Il valore viene randomizzato
        Random rand = new Random();
        return (byte) (rand.nextBoolean() ? value + rand.nextInt(2) : value - rand.nextInt(2));
    }
}

