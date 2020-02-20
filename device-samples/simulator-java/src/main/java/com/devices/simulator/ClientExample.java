package com.devices.simulator;

import java.io.IOException;
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

                List<Byte> FUCKINGLIST = createRandomRequestPacket();
                Byte[] bytesofmyass = FUCKINGLIST.toArray(new Byte[FUCKINGLIST.size()]);
                byte[] ihatebuffer = toPrimitive(bytesofmyass);

                DatagramPacket request = new DatagramPacket(ihatebuffer, ihatebuffer.length, InetAddress.getLocalHost(), 6969);
                socket.send(request);
                System.out.print("> REQ: ");
                System.out.println(FUCKINGLIST);

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
    public static List<Byte> createRandomRequestPacket()
    {
        List<Byte> packet = new ArrayList<Byte>();
        Random rand = new Random();
        packet.add((byte) (1 + rand.nextInt(1)));
        packet.add((byte) 0); // richiesta
        packet.add((byte) 0); // dati vuoti
        packet.add((byte) (1 + rand.nextInt(2)));
        packet.add(connectionManager.calculateChecksum(packet));
        return packet;
    }
}
