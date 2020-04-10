package com.redroundrobin.thirema.gateway.utils;

import com.github.snksoft.crc.CRC;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Utility {

    private Utility() {}
    // Conversione dati pacchetto
    public static byte[] convertPacket(@NotNull int[] packet) {
        byte[] convertedPacket = new byte[packet.length];

        for (int i = 0; i < packet.length; i++) {
            convertedPacket[i] = (byte) packet[i];
        }

        return convertedPacket;
    }

    // CRC-8 Bluetooth
    public static byte calculateCRC(@NotNull List<Byte> packet) {
        return (byte) CRC.calculateCRC(new CRC.Parameters(8, 0xa7, 0x00, true, true, 0x00), convertPacket(packet.stream().mapToInt(Byte::byteValue).toArray()));
    }

    // Controllo il crc del pacchetto ricevuto
    public static boolean checkIntegrity(@NotNull List<Byte> packet) {
        return packet.get(4) == calculateCRC(packet.subList(0, 4));
    }
}
