package com.devices.simulator;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.apache.commons.lang3.ArrayUtils.toPrimitive;

public class ClientExample {

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException {

        try {

            InetAddress hostname = InetAddress.getLocalHost();
            DatagramSocket socket = new DatagramSocket();

            while (true) {
                byte[] pacchettoGenerato =  creaPacchettoCasuale();

                DatagramPacket richiesta = new DatagramPacket(pacchettoGenerato, pacchettoGenerato.length, InetAddress.getLocalHost(), 6969);
                socket.send(richiesta);
                System.out.print("> REQ: ");
                System.out.println(pacchettoGenerato);

                byte[] buffer = new byte[5];
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                socket.setSoTimeout(1000);
                socket.receive(response);

                System.out.print("< RES: ");
                for (int i = 0; i < buffer.length; ++i)
                    System.out.print(buffer[i] + " ");
                System.out.println();
                //Arrays.asList(buffer).stream().forEach(x -> System.out.println(x));
                Thread.sleep(1000);
            }

        } catch (SocketTimeoutException ex) {
            System.out.println("Errore di tempo fuori: " + ex.getMessage());
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Errore cliente: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Creazione di un pacchetto random
    public static byte[] creaPacchettoCasuale()
    {
        Random rand = new Random();
        byte disp = (byte) (1 + rand.nextInt(1));
        byte codiceOperazione = 0;
        byte sensore = 0;
        byte valore = (byte) (1 + rand.nextInt(2));

        List<Byte> pacchetto = new ArrayList<>();
        pacchetto.add(disp);
        pacchetto.add(codiceOperazione);
        pacchetto.add(sensore);
        pacchetto.add(valore);

        return new byte[]{
               disp, codiceOperazione, sensore, valore,
                connectionManager.calculateChecksum(pacchetto)
        };

    }
}
