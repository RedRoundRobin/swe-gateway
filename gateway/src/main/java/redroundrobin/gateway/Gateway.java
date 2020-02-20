package redroundrobin.gateway;


import com.github.snksoft.crc.CRC;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.apache.commons.lang3.ArrayUtils.toPrimitive;


public class Gateway {

    private InetAddress indirizzo;
    private int porta;
    private List<Dispositivo> dispositivi = new ArrayList<>();
    private short accumuloPacchetti = 5; //Da prendere dalla configurazione del gateway
    private short tempoDiAccumulo = 10000; //Da prendere dalla configurazione del gateway in millisecondi

    public Gateway(InetAddress indirizzo, int porta) {
        this.indirizzo = indirizzo;
        this.porta = porta;

    }

    public InetAddress getIndirizzo() {
        return indirizzo;
    }

    public int getPorta() {
        return porta;
    }

    public List<Dispositivo> getDispositivi() {
        return dispositivi;
    }

    /*
     * Metodo che reperisce i dati dai dispositivi e dopo averne accumulati alcuni li invia al topic di Kafka specificato*/
    void riceviDati() {
        try {

            DatagramSocket socket = new DatagramSocket();
            Traduttore traduttore = new Traduttore();
            long tempo = System.currentTimeMillis();
            short numeroPacchetti = 0;

            while (true) {
                byte[] pacchettoGenerato = creaPacchettoCasuale();

                DatagramPacket richiesta = new DatagramPacket(pacchettoGenerato, pacchettoGenerato.length, InetAddress.getLocalHost(), 6969);
                socket.send(richiesta);
                System.out.print("> REQ: ");
                System.out.print("[ ");
                for (byte elemento : pacchettoGenerato) {
                    System.out.print(elemento + " ");
                }
                System.out.println("]");


                byte[] buffer = new byte[5];
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                socket.setSoTimeout(1000);
                socket.receive(response);


                List<Byte> pacchettoRicevuto = Arrays.asList(ArrayUtils.toObject(buffer));
                if(controllaPacchetto(pacchettoRicevuto) && traduttore.aggiungiSensore(buffer)){
                    numeroPacchetti++;
                }

                long tempoTrascorso = System.currentTimeMillis() - tempo;

                if(numeroPacchetti > accumuloPacchetti || tempoTrascorso > tempoDiAccumulo){
                    traduttore.getJSON();
                    Produttore.eseguiProduttore("Gateway-Test", traduttore.getJSON(), new Produttore("Gateway-Test", "localhost:29092"));
                }

                System.out.print("< RES: ");
                for (int i = 0; i < buffer.length; ++i)
                    System.out.print(buffer[i] + " ");
                System.out.println();
                //Arrays.asList(buffer).stream().forEach(x -> System.out.println(x));
                Thread.sleep(1000);
            }

        } catch (SocketTimeoutException e) {
            System.out.println("Errore di tempo fuori: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Errore cliente: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Errore cliente: " + e.getMessage());
            e.printStackTrace();

        }
    }


    // Creazione di un pacchetto random
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

    public static void main(String args[]) throws UnknownHostException {
        Gateway gateway = new Gateway(InetAddress.getLocalHost(), 6969);
        gateway.riceviDati();

    }
}
