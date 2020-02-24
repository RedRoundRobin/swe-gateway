package redroundrobin.gateway;

import org.apache.commons.lang3.ArrayUtils;

import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static redroundrobin.gateway.Utilita.calcolaCRC;

public class Gateway {
    private InetAddress indirizzo;
    private int porta;

    private String nome;
    private List<Dispositivo> dispositivi; // Da prendere dalla configurazione del gateway

    private int accumuloPacchetti; // Da prendere dalla configurazione del gateway
    private int tempoDiAccumulo; // Da prendere dalla configurazione del gateway in millisecondi

    public Gateway(InetAddress indirizzo, int porta, String nome, List<Dispositivo> dispositivi, int accumuloPacchetti, int tempoDiAccumulo) {
        this.indirizzo = indirizzo;
        this.porta = porta;

        this.nome = nome;
        this.dispositivi = dispositivi;

        this.accumuloPacchetti = accumuloPacchetti; // Accumulo di pacchetti di default
        this.tempoDiAccumulo = tempoDiAccumulo; // Tempo di accumulo di default
    }

    // Metodo che reperisce i dati dai dispositivi e dopo averne accumulati "accumuloPacchetti" o aver aspettato "tempoDiAccumulo" millisecondi li invia al topic di Kafka specificato
    public void riceviDati() {
        try (DatagramSocket socket = new DatagramSocket(); Produttore produttore = new Produttore(nome, "localhost:29092")) {
            Traduttore traduttore = new Traduttore();

            long tempo = System.currentTimeMillis();
            int numeroPacchetti = 0;

            // Ciclo in cui vengono effettuate tutte le richieste per ogni sensore
            while (true) {
                byte[] pacchettoGenerato = creaPacchettoRichiestaCasuale();

                DatagramPacket richiesta = new DatagramPacket(pacchettoGenerato, pacchettoGenerato.length, indirizzo, porta);
                socket.send(richiesta);

                System.out.print("> REQ: [ ");
                for (byte elemento : pacchettoGenerato) {
                    System.out.print(elemento + " ");
                }
                System.out.println("]");

                byte[] buffer = new byte[5];
                DatagramPacket risposta = new DatagramPacket(buffer, buffer.length);
                socket.setSoTimeout(150000);
                socket.receive(risposta);

                List<Byte> pacchettoRicevuto = Arrays.asList(ArrayUtils.toObject(buffer));

                if (Utilita.controllaIntegrita(pacchettoRicevuto)) {
                    boolean flag = traduttore.aggiungiSensore(buffer);
                    if (flag) {
                        numeroPacchetti++;
                    }
                }

                long tempoTrascorso = System.currentTimeMillis() - tempo;

                if (numeroPacchetti > accumuloPacchetti || tempoTrascorso > tempoDiAccumulo) {
                    String dato = traduttore.ottieniJSON();
                    produttore.eseguiProduttore(nome, dato);
                    tempo = System.currentTimeMillis();
                    numeroPacchetti = 0;
                }

                System.out.print("< RES: [ ");
                for (byte elemento : buffer) {
                    System.out.print(elemento + " ");
                }
                System.out.println("]");

                Thread.sleep(1000); // Da tenere solo per fare test
            }
        }
        catch (SocketTimeoutException eccezione) {
            System.out.println("< RES: []");
        }
        catch (InterruptedException ignored) {}
        catch (Exception eccezione) {
            System.out.println("Errore " + eccezione.getClass() + ": " + eccezione.getMessage());
            eccezione.printStackTrace();
        }
    }

    // Creazione di un pacchetto di richiesta dati per uno dei dispositivi disponibili nel Server
    public byte[] creaPacchettoRichiestaCasuale() {
        Random casuale = new Random();

        int numeroDispositivi = dispositivi.size();
        int indiceDispositivo = casuale.nextInt(numeroDispositivi);
        int numeroSensori = dispositivi.get(indiceDispositivo).ottieniSensori().size();
        int indiceSensore = casuale.nextInt(numeroSensori);

        byte dispositivo = (byte) (dispositivi.get(indiceDispositivo).ottieniId()); //prendo uno tra gli id
        byte codiceOperazione = 0;
        byte sensore = (byte) (dispositivi.get(indiceDispositivo).ottieniSensori().get(indiceSensore).ottieniId()); //prendo uno dei sensori del dispositivo
        byte valore = 0;

        List<Byte> pacchetto = new ArrayList<>(Arrays.asList(dispositivo, codiceOperazione, sensore, valore));

        return new byte[] {dispositivo, codiceOperazione, sensore, valore, calcolaCRC(pacchetto)};
    }


    public static void main(String[] args) throws UnknownHostException {
        // Creo la configurazione
        List<Sensore> sensori1 = new ArrayList<>(Arrays.asList(new Sensore(1, 0), new Sensore(2, 0), new Sensore(3, 0), new Sensore(4, 0)));
        Dispositivo dispositivo1 = new Dispositivo(1, sensori1);

        List<Sensore> sensori2 = new ArrayList<>(Arrays.asList(new Sensore(1, 0), new Sensore(2, 0), new Sensore(3, 0)));
        Dispositivo dispositivo2 = new Dispositivo(2, sensori2);

        List<Sensore> sensori3 = new ArrayList<>(Arrays.asList(new Sensore(1, 0), new Sensore(2, 0), new Sensore(3, 0), new Sensore(4, 0)));
        Dispositivo dispositivo3 = new Dispositivo(3, sensori3);

        List<Sensore> sensori4 = new ArrayList<>(Arrays.asList(new Sensore(1, 0), new Sensore(2, 0), new Sensore(3, 0), new Sensore(4, 0)));
        Dispositivo dispositivo4 = new Dispositivo(4, sensori4);

        List<Sensore> sensori5 = new ArrayList<>(Arrays.asList(new Sensore(1, 0), new Sensore(2, 0), new Sensore(3, 0), new Sensore(4, 0)));
        Dispositivo dispositivo5 = new Dispositivo(5, sensori5);

        List<Sensore> sensori6 = new ArrayList<>(Arrays.asList(new Sensore(1, 0), new Sensore(2, 0), new Sensore(3, 0), new Sensore(4, 0)));
        Dispositivo dispositivo6 = new Dispositivo(6, sensori6);

        List<Dispositivo> dispositivi = new ArrayList<>(Arrays.asList(dispositivo1, dispositivo2, dispositivo3, dispositivo4, dispositivo5, dispositivo6));

        // Creo il gateway
        Gateway gateway = new Gateway(InetAddress.getLocalHost(), 6969, "GatewayDiTest", dispositivi,5, 6000);

        // Comincio a ricevere i dati dei dispositivi
        gateway.riceviDati();
    }
}
