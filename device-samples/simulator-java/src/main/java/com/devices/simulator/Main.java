package com.devices.simulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Sensore> sensori1 = new ArrayList<>(Arrays.asList(new Sensore(1, 21), new Sensore(2, 50), new Sensore(3, 4), new Sensore(4, 150)));
        Dispositivo dispositivo1 = new Dispositivo(1, sensori1);

        List<Sensore> sensori2 = new ArrayList<>(Arrays.asList(new Sensore(1, 234), new Sensore(2, 21), new Sensore(3, 22)));
        Dispositivo dispositivo2 = new Dispositivo(2, sensori2);

        List<Dispositivo> dispositivi1 = new ArrayList<>(Arrays.asList(dispositivo1, dispositivo2));

        Dispositivi dispositivi = new Dispositivi(6969, dispositivi1);
        dispositivi.avviaServer();
    }
}
