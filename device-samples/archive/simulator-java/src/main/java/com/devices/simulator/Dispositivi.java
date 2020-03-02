package com.devices.simulator;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Dispositivi {
    private int porta;
    private List<Dispositivo> dispositivi;

    public Dispositivi(int porta, List<Dispositivo> dispositivi) {
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
            pacchetto.add((byte) sensoreOpzionale.get().ottieniValore());
            pacchetto.add(GestorePacchetti.calcolaCRC(pacchetto));
        } else {
            pacchetto.add((byte) idDispositivo);
            pacchetto.add((byte) -1); // risposta con errore
            pacchetto.add((byte) idSensore);
            pacchetto.add(GestorePacchetti.calcolaCRC(pacchetto));
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
                if(!GestorePacchetti.controllaIntegrita(pacchettoRicevuto)){
                    System.out.println("Errore: pacchetto corrotto!");
                    continue;
                }

                List<Byte> pacchettoRisposta = creaPacchettoDiRisposta(Byte.toUnsignedInt(pacchettoRicevuto.get(0)), Byte.toUnsignedInt(pacchettoRicevuto.get(2)));
                byte[] pacchettoDaInviare = GestorePacchetti.convertiPacchetto(pacchettoRisposta.stream().mapToInt(Byte::byteValue).toArray());

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

}
