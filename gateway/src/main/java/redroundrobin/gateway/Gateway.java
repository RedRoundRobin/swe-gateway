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
import static redroundrobin.gateway.Utilita.calcolaChecksum;


public class Gateway {

    private String nome;
    private InetAddress indirizzo;
    private int porta;
    private List<Dispositivo> dispositivi = new ArrayList<>(); //Da prendere dalla configurazione del gateway
    private short accumuloPacchetti; //Da prendere dalla configurazione del gateway
    private short tempoDiAccumulo; //Da prendere dalla configurazione del gateway in millisecondi

    public Gateway(InetAddress indirizzo, int porta, String nome) {
        this.indirizzo = indirizzo;
        this.porta = porta;
        this.nome = nome;
        this.tempoDiAccumulo = 10000; //tempo di accumulo di default
        this.accumuloPacchetti = 5;   //accumulo di pacchetti di default

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

    public void setAccumuloPacchetti(short accumuloPacchetti) {
        this.accumuloPacchetti = accumuloPacchetti;
    }

    public void setTempoDiAccumulo(short tempoDiAccumulo) {
        this.tempoDiAccumulo = tempoDiAccumulo;
    }

    public void setDispositivi(List<Dispositivo> dispositivi) {
        this.dispositivi = dispositivi;
    }

    /*
     * Metodo che reperisce i dati dai dispositivi e dopo averne accumulati "accumuloPacchetti"
     *  o aver aspettato "tempoDiAccumulo" millisecondi li invia al topic di Kafka specificato
     *
     */
    void riceviDati() {
        try {

            DatagramSocket socket = new DatagramSocket();
            Traduttore traduttore = new Traduttore();
            long tempo = System.currentTimeMillis();
            short numeroPacchetti = 0;
            Produttore produttore = new Produttore(nome, "localhost:29092");

            while (true) {
                //Ci sarà un ciclo in cui vengono effettuate tutte le richieste per ogni sensore
                byte[] pacchettoGenerato = creaPacchettoRichiestaCasuale();

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
                socket.setSoTimeout(150000);
                socket.receive(response);


                List<Byte> pacchettoRicevuto = Arrays.asList(ArrayUtils.toObject(buffer));
                if(Utilita.controllaPacchetto(pacchettoRicevuto)) {

                   boolean flag = traduttore.aggiungiSensore(buffer);
                    if(flag) {
                        numeroPacchetti++;
                    }
                }

                long tempoTrascorso = System.currentTimeMillis() - tempo;

                if(numeroPacchetti > accumuloPacchetti || tempoTrascorso > tempoDiAccumulo){
                    String dato = traduttore.getJSON();
                    Produttore.eseguiProduttore(nome, dato, produttore);
                    tempo = System.currentTimeMillis();
                    numeroPacchetti = 0;

                }

                System.out.print("< RES: ");
                for (int i = 0; i < buffer.length; ++i)
                    System.out.print(buffer[i] + " ");
                System.out.println();
                //Arrays.asList(buffer).stream().forEach(x -> System.out.println(x));
                Thread.sleep(1000); //Da tenere solo per testing
            }
            //produttore.chiudiProduttore();
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

    // Creazione di un pacchetto di richiesta dati per uno dei dispositivi disponibili nel Server
    public byte[] creaPacchettoRichiestaCasuale() {
        Random rand = new Random();
        int numDispositivi = dispositivi.size();
        int indiceDispositivo = rand.nextInt(numDispositivi);
        int numSensori = dispositivi.get(indiceDispositivo).getSensori().size();
        int indiceSensore = rand.nextInt(numSensori);

        byte disp = (byte) (dispositivi.get(indiceDispositivo).getId());   //prendo uno tra gli id
        byte codiceOperazione = 0;
        byte sensore = (byte)(dispositivi.get(indiceDispositivo).getSensori().get(indiceSensore).getId()); //prendo uno dei sensori del dispositivo
        byte valore = 0;

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


    public static void main(String args[]) throws UnknownHostException {
        Gateway gateway = new Gateway(InetAddress.getLocalHost(), 6969, "GatewayDiTest");

        //Aggiungo al gateway la configurazione

        Sensore[] sensori1 = {
                new Sensore(1, 0),
                new Sensore(2, 0),
                new Sensore(3, 0),
                new Sensore(4, 0)
        };
        Dispositivo dispositivo1 = new Dispositivo(1);
        dispositivo1.getSensori().addAll(Arrays.asList(sensori1));

        Sensore[] sensori2 = {
                new Sensore(1, 0),
                new Sensore(2, 0),
                new Sensore(3, 0)
        };
        Dispositivo dispositivo2 = new Dispositivo(2);
        dispositivo2.getSensori().addAll(Arrays.asList(sensori2));

        Sensore[] sensori3 = {
                new Sensore(1, 0),
                new Sensore(2, 0),
                new Sensore(3, 0),
                new Sensore(4, 0)
        };
        Dispositivo dispositivo3 = new Dispositivo(3);
        dispositivo3.getSensori().addAll(Arrays.asList(sensori3));

        Sensore[] sensori4 = {
                new Sensore(1, 0),
                new Sensore(2, 0),
                new Sensore(3, 0),
                new Sensore(4, 0)
        };
        Dispositivo dispositivo4 = new Dispositivo(4);
        dispositivo4.getSensori().addAll(Arrays.asList(sensori4));

        Sensore[] sensori5 = {
                new Sensore(1, 0),
                new Sensore(2, 0),
                new Sensore(3, 0),
                new Sensore(4, 0)
        };
        Dispositivo dispositivo5 = new Dispositivo(5);
        dispositivo5.getSensori().addAll(Arrays.asList(sensori5));

        Sensore[] sensori6 = {
                new Sensore(1, 0),
                new Sensore(2, 0),
                new Sensore(3, 0),
                new Sensore(4, 0)
        };
        Dispositivo dispositivo6 = new Dispositivo(6);
        dispositivo6.getSensori().addAll(Arrays.asList(sensori6));

        List<Dispositivo> dispositiviSalvati = new ArrayList<>();

        dispositiviSalvati.addAll(Arrays.asList(dispositivo1));
        dispositiviSalvati.addAll(Arrays.asList(dispositivo2));
        dispositiviSalvati.addAll(Arrays.asList(dispositivo3));
        dispositiviSalvati.addAll(Arrays.asList(dispositivo4));
        dispositiviSalvati.addAll(Arrays.asList(dispositivo5));
        dispositiviSalvati.addAll(Arrays.asList(dispositivo6));
        gateway.setDispositivi(dispositiviSalvati);

        gateway.setAccumuloPacchetti((short) 5);
        gateway.setTempoDiAccumulo((short) 6000);

        //Comincio a ricevere i dati dei dispositivi
        gateway.riceviDati();

    }
}
