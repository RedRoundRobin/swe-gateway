package com.redroundrobin.thirema.simulation;

import com.redroundrobin.thirema.gateway.models.Dispositivo;
import com.redroundrobin.thirema.gateway.models.Sensore;
import com.redroundrobin.thirema.gateway.utils.Utilita;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SimulatoreDispositivi {
    private int porta;
    private List<Dispositivo> dispositivi;

    public SimulatoreDispositivi(int porta, List<Dispositivo> dispositivi) {
        this.porta = porta;
        this.dispositivi = dispositivi;
    }

    // Pacchetto di risposta
    public List<Byte> creaPacchettoDiRisposta(int idDispositivo, int idSensore) {
        List<Byte> pacchetto = new ArrayList<>();

        Optional<Dispositivo> dispositivoOpzionale = dispositivi.stream()
                .filter(dispositivo -> idDispositivo == dispositivo.ottieniId())
                .findFirst();
        Optional<Sensore> sensoreOpzionale = Optional.empty();

        if(dispositivoOpzionale.isPresent()) {
            Dispositivo dispositivo = dispositivoOpzionale.get();

            sensoreOpzionale = dispositivo.ottieniSensori().stream()
                    .filter(sensore -> idSensore == sensore.ottieniId())
                    .findFirst();
        }

        if(dispositivoOpzionale.isPresent() && sensoreOpzionale.isPresent()){
            pacchetto.add((byte) idDispositivo);
            pacchetto.add((byte) 1); // risposta con dato
            pacchetto.add((byte) idSensore);
            pacchetto.add((byte) sensoreOpzionale.get().ottieniDato());
            pacchetto.add(Utilita.calcolaCRC(pacchetto));
        } else {
            pacchetto.add((byte) idDispositivo);
            pacchetto.add((byte) -1); // risposta con errore
            pacchetto.add((byte) idSensore);
            pacchetto.add(Utilita.calcolaCRC(pacchetto));
        }

        return pacchetto;
    }

    public void avviaServer() {
        try (DatagramSocket socket = new DatagramSocket(porta)) {
            while (true) {
                byte[] pacchettoRichiesta = new byte[5];

                DatagramPacket richiesta = new DatagramPacket(pacchettoRichiesta, pacchettoRichiesta.length);
                socket.setSoTimeout(0); // attesa infinita
                socket.receive(richiesta);

                List<Byte> pacchettoRicevuto = Arrays.asList(ArrayUtils.toObject(pacchettoRichiesta));
                if(!Utilita.controllaIntegrita(pacchettoRicevuto)){
                    System.out.println("Errore: pacchetto corrotto!");
                    continue;
                }

                List<Byte> pacchettoRisposta = creaPacchettoDiRisposta(Byte.toUnsignedInt(pacchettoRicevuto.get(0)), Byte.toUnsignedInt(pacchettoRicevuto.get(2)));
                byte[] pacchettoDaInviare = Utilita.convertiPacchetto(pacchettoRisposta.stream().mapToInt(Byte::byteValue).toArray());

                DatagramPacket risposta = new DatagramPacket(pacchettoDaInviare, pacchettoDaInviare.length, richiesta.getAddress(), richiesta.getPort());
                socket.send(risposta);

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

    public static void main(String[] args) {
        //Imposto i sensori disponibili per la simulazione
        List<Sensore> sensori1 = new ArrayList<>(Arrays.asList(new Sensore(1, 21), new Sensore(2, 50), new Sensore(3, 4), new Sensore(4, 150)));
        Dispositivo dispositivo1 = new Dispositivo(1, sensori1);

        List<Sensore> sensori2 = new ArrayList<>(Arrays.asList(new Sensore(1, 234), new Sensore(2, 21), new Sensore(3, 32)));
        Dispositivo dispositivo2 = new Dispositivo(2, sensori2);

        List<Sensore> sensori3 = new ArrayList<>(Arrays.asList(new Sensore(1, 21), new Sensore(2, 23), new Sensore(3, 34), new Sensore(4, 54)));
        Dispositivo dispositivo3 = new Dispositivo(3, sensori3);

        List<Sensore> sensori4 = new ArrayList<>(Arrays.asList(new Sensore(1, 13), new Sensore(2, 22), new Sensore(3, 33), new Sensore(4, 44)));
        Dispositivo dispositivo4 = new Dispositivo(4, sensori4);

        List<Sensore> sensori5 = new ArrayList<>(Arrays.asList(new Sensore(1, 17), new Sensore(2, 62), new Sensore(3, 73), new Sensore(4, 47)));
        Dispositivo dispositivo5 = new Dispositivo(5, sensori5);

        List<Sensore> sensori6 = new ArrayList<>(Arrays.asList(new Sensore(1, 61), new Sensore(2, 27), new Sensore(3, 43), new Sensore(4, 46)));
        Dispositivo dispositivo6 = new Dispositivo(6, sensori6);

        List<Dispositivo> dispositivi1 = new ArrayList<>(Arrays.asList(dispositivo1, dispositivo2, dispositivo3, dispositivo4, dispositivo5, dispositivo6));

        SimulatoreDispositivi simulatoreDispositivi = new SimulatoreDispositivi(6969, dispositivi1);

        // Avvio del server che aspetta le richieste del gateway
        simulatoreDispositivi.avviaServer();
    }
}