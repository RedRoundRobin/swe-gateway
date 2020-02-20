package com.devices.simulator;

import com.github.snksoft.crc.CRC;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.ArrayUtils.toPrimitive;

public class connectionManager {

    public static int port = 0;
    private device[] devices = {};
    private ServerSocket server;
    private Socket client;

    public connectionManager(device[] d, int p) {
       devices = d;
       port = p;
    }

    /* Pacchetto di risposta */
    public List<Byte> createResponsePacket(int dispId, int sensorId) {

        List<Byte> packet = new ArrayList<Byte>();

        Optional<device> optdevice = Arrays.stream(this.devices)
                                        .filter(x -> dispId == x.getId())
                                        .findFirst();
        Optional<sensor> optsensor = null;

        if(optdevice.isPresent()) {
            device d = optdevice.get();

              optsensor = Arrays.stream(d.getSensors())
                    .filter(y -> sensorId == y.getId())
                    .findFirst();
        }

        if(optdevice.isPresent() && optsensor.isPresent()){
            packet.add((byte) dispId);
            packet.add((byte) 127); // risposta con dato
            packet.add((byte) sensorId);
            packet.add((byte) optsensor.get().getValue());
            packet.add(calculateChecksum(packet));
            //System.out.print(dispId + " ");
            //System.out.print(127 + " ");
        } else {
            packet.add((byte) dispId);
            packet.add((byte) 64); // risposta con errore
            packet.add((byte) sensorId);
            packet.add(calculateChecksum(packet));
        }
        return packet;
    }

    /* Checksum CRC-8 Bluetooth */
    public static byte calculateChecksum(@NotNull List<Byte> packet){

        Byte[] bytes = packet.toArray(new Byte[packet.size()]);

        byte tmp = (byte) CRC.calculateCRC(
                new CRC.Parameters(8, 0xa7, 0x00, true, true, 0x00),
                toPrimitive(bytes));
        //System.out.println(tmp);
        return tmp;
    }

    // Controllo il checksum del pacchetto ricevuto a meno del checksum
    private boolean checkPacket(@NotNull List<Byte> packet) {
       // System.out.println(packet.size());
        return packet.get(4) == calculateChecksum(packet.subList(0, 4));
    }

    public void startServerBello() throws UnknownHostException {

        try {

            InetAddress hostname = InetAddress.getLocalHost();
            DatagramSocket socket = new DatagramSocket(6969);

            while (true) {
                
                byte[] buffer = new byte[5];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.setSoTimeout(0); // attesa infinita
                socket.receive(request);

                List<Byte> nicePacket = Arrays.asList(ArrayUtils.toObject(buffer));
                if(!checkPacket(nicePacket)){
                    System.out.println("Errore: pacchetto corrotto!");
                    continue;
                }

                List<Byte> ihatejava = createResponsePacket(Byte.toUnsignedInt(nicePacket.get(0)),
                        Byte.toUnsignedInt(nicePacket.get(2)));
                Byte[] ihatejavatwice = ihatejava.toArray(new Byte[ihatejava.size()]);
                byte[] ihatebuffer = toPrimitive(ihatejavatwice);

                DatagramPacket response = new DatagramPacket(ihatebuffer, ihatebuffer.length, request.getAddress(), request.getPort());
                socket.send(response);

                System.out.println("Successo: pacchetto inviato!");

            }

        } catch (SocketTimeoutException ex) {
            System.out.println("Errore di tempo fuori: " + ex.getMessage());
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Errore cliente: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
