package redroundrobin.gateway;

import com.github.snksoft.crc.CRC;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Utilita {
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
}
