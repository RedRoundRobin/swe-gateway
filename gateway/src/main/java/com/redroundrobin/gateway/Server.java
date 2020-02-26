package com.redroundrobin.gateway;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Server {
    public static void main(String[] args) {
        //Imposto i sensori disponibili per la simulazione
        List<Sensore> sensori1 = new ArrayList<>(Arrays.asList(new Sensore(1, 21), new Sensore(2, 50), new Sensore(3, 4), new Sensore(4, 150)));
        Dispositivo dispositivo1 = new Dispositivo(1, sensori1);

        List<Sensore> sensori2 = new ArrayList<>(Arrays.asList(new Sensore(1, 234), new Sensore(2, 21), new Sensore(3, 32)));
        Dispositivo dispositivo2 = new Dispositivo(2, sensori2);

        List<Sensore> sensori3 = new ArrayList<>(Arrays.asList(new Sensore(1, 21), new Sensore(2, 23), new Sensore(3, 34), new Sensore(4, 54)));
        Dispositivo dispositivo3 = new Dispositivo(3, sensori3);

        List<Sensore> sensori4 = new ArrayList<>(Arrays.asList(new Sensore(1, 13), new Sensore(2, 22), new Sensore(3, 33), new Sensore(4, 44)));
        Dispositivo dispositivo4 = new Dispositivo(4, sensori4);

        List<Sensore> sensori5 = new ArrayList<>(Arrays.asList(new Sensore(1, 17), new Sensore(2, 62), new Sensore(3, 73), new Sensore(4, 47)));
        Dispositivo dispositivo5 = new Dispositivo(5, sensori5);

        List<Sensore> sensori6 = new ArrayList<>(Arrays.asList(new Sensore(1, 61), new Sensore(2, 27), new Sensore(3, 43), new Sensore(4, 46)));
        Dispositivo dispositivo6 = new Dispositivo(6, sensori6);

        List<Dispositivo> dispositivi1 = new ArrayList<>(Arrays.asList(dispositivo1, dispositivo2, dispositivo3, dispositivo4, dispositivo5, dispositivo6));

        Dispositivi dispositivi = new Dispositivi(6969, dispositivi1);

        // Avvio del server che aspetta le richieste del gateway
        dispositivi.avviaServer();
    }
}