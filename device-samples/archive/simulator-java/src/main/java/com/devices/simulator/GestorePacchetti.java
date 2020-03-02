package com.devices.simulator;

import com.github.snksoft.crc.CRC;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GestorePacchetti {
    // Conversione dati pacchetto
    public static byte[] convertiPacchetto(@NotNull int[] pacchetto) {
        byte[] pacchettoConvertito = new byte[pacchetto.length];

        for (int i = 0; i < pacchetto.length; i++) {
            pacchettoConvertito[i] = (byte) pacchetto[i];
        }

        return pacchettoConvertito;
    }

    // CRC-8 Bluetooth
    public static byte calcolaCRC(@NotNull List<Byte> pacchetto) {
        return (byte) CRC.calculateCRC(new CRC.Parameters(8, 0xa7, 0x00, true, true, 0x00), convertiPacchetto(pacchetto.stream().mapToInt(Byte::byteValue).toArray()));
    }

    // Controllo il crc del pacchetto ricevuto
    public static boolean controllaIntegrita(@NotNull List<Byte> pacchetto) {
        return pacchetto.get(4) == calcolaCRC(pacchetto.subList(0, 4));
    }

    // Creazione di un pacchetto casuale
    public static byte[] creaPacchettoCasuale()  {
        Random casuale = new Random();
        byte dispositivo = (byte) (1 + casuale.nextInt(1));
        byte codiceOperazione = 0;
        byte sensore = (byte) (1 + casuale.nextInt(2));
        byte valore = 0;

        List<Byte> pacchetto = new ArrayList<>(Arrays.asList(dispositivo, codiceOperazione, sensore, valore));

        return new byte[] {dispositivo, codiceOperazione, sensore, valore, calcolaCRC(pacchetto)};
    }
}
