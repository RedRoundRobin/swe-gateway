package com.devices.simulator;

import com.github.snksoft.crc.CRC;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class connectionManager {

    public static int port = 0;
    private device[] devices = {};

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

    public byte calculateChecksum(@NotNull List<Byte> packet){

        Byte[] bytes = packet.toArray(new Byte[packet.size()]);

        return (byte) CRC.calculateCRC(
                new CRC.Parameters(8, 0xa7, 0x00, true, true, 0x00),
                ArrayUtils.toPrimitive(bytes));
    }

}
