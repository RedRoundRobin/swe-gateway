package com.devices.simulator;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws UnknownHostException {
        List<Sensore> sensori1 = new ArrayList<>(Arrays.asList(new Sensore(21, 1), new Sensore(50, 2), new Sensore(4, 3), new Sensore(150, 4)));
        Dispositivo dispositivo1 = new Dispositivo(1, sensori1);

        List<Sensore> sensori2 = new ArrayList<>(Arrays.asList(new Sensore(234, 1), new Sensore(21, 2), new Sensore(22, 3)));
        Dispositivo dispositivo2 = new Dispositivo(2, sensori2);

        List<Dispositivo> dispositivi = new ArrayList<>(Arrays.asList(dispositivo1, dispositivo2));

        Gestore gestore = new Gestore(dispositivi, 6969);
        gestore.startServerBello();
    }
}
