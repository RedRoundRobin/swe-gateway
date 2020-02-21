package redroundrobin.gateway;

import com.github.snksoft.crc.CRC;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.apache.commons.lang3.ArrayUtils.toPrimitive;

public class Utilita {

    //Calcola il checksum per il pacchetto specificato
    static byte calcolaChecksum(List<Byte> pacchetto) {
        Byte[] bytes = pacchetto.toArray(new Byte[pacchetto.size()]);

        byte tmp = (byte) CRC.calculateCRC(
                new CRC.Parameters(8, 0xa7, 0x00, true, true, 0x00),
                toPrimitive(bytes));
        //System.out.println(tmp);
        return tmp;
    }

    // Controllo il checksum del pacchetto ricevuto a meno del checksum
    public static boolean controllaPacchetto(@NotNull List<Byte> packet) {
        // System.out.println(packet.size());
        return packet.get(4) == calcolaChecksum(packet.subList(0, 4));
    }

    // Creazione di un pacchetto casuale
    public static byte[] creaPacchettoCasuale() {
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
                calcolaChecksum(pacchetto)
        };

    }


}
