package com.devices.simulator;

import com.github.snksoft.crc.CRC;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        } else {
            packet.add((byte) dispId);
            packet.add((byte) 64); // risposta con errore
            packet.add((byte) sensorId);
            packet.add(calculateChecksum(packet));
        }
        return packet;
    }

    /* Checksum CRC-8 Bluetooth */
    private byte calculateChecksum(@NotNull List<Byte> packet){

        Byte[] bytes = packet.toArray(new Byte[packet.size()]);

        return (byte) CRC.calculateCRC(
                new CRC.Parameters(8, 0xa7, 0x00, true, true, 0x00),
                ArrayUtils.toPrimitive(bytes));
    }

    // Controllo il checksum del pacchetto ricevuto a meno del checksum
    private boolean checkPacket(@NotNull List<Byte> packet) {
            return packet.get(2) == calculateChecksum(packet.subList(0, (packet.size() == 4 ? 2 : 3)));
    }

    protected void startServer()
    {
        try
        {
            while (true){
                server = new ServerSocket(port);
                client = server.accept();
                server.close();
                PrintWriter out = new PrintWriter(client.getOutputStream(),true);
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
                String line;
                while((line = in.readLine()) != null)
                {
                    byte[] packet = line.getBytes();
                    List<Byte> nicePacket = Arrays.asList(ArrayUtils.toObject(packet));
                    if(!checkPacket(nicePacket)){
                        System.out.println("Errore: pacchetto corrotto!");
                        continue;
                    }

                    out.println(
                            createResponsePacket(Byte.toUnsignedInt(nicePacket.get(1)),
                                                    Byte.toUnsignedInt(nicePacket.get(3)))
                    );
                }
            }
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
    }

}
