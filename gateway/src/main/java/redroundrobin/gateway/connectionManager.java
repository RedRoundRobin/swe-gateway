package redroundrobin.gateway;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.ArrayUtils.toPrimitive;

public class connectionManager {

    public static int port = 0;
    private Dispositivo[] devices = {};
    private ServerSocket server;
    private Socket client;

    public connectionManager(Dispositivo[] d, int p) {
        devices = d;
        port = p;
    }

    /*
    Pacchetto di risposta con i dati dei dispositivi
    Le informazioni vengono reperite dal Server di simulazione dei dispositivi
     */
    public List<Byte> createResponsePacket(int dispId, int sensorId) {

        List<Byte> packet = new ArrayList<>();

        Optional<Dispositivo> optdevice = Arrays.stream(this.devices)
                .filter(x -> dispId == x.getId())
                .findFirst();
        Optional<Sensore> optsensor = null;

        if(optdevice.isPresent()) {
            Dispositivo d = optdevice.get();

            optsensor = d.getSensori().stream()
                    .filter(y -> sensorId == y.getId())
                    .findFirst();
        }

        if(optdevice.isPresent() && optsensor.isPresent()){
            packet.add((byte) dispId);
            packet.add((byte) 127); // risposta con dato
            packet.add((byte) sensorId);
            packet.add((byte) optsensor.get().getDato());
            packet.add(Utilita.calcolaChecksum(packet));
            //System.out.print(dispId + " ");
            //System.out.print(127 + " ");
        } else {
            packet.add((byte) dispId);
            packet.add((byte) 64); // risposta con errore
            packet.add((byte) sensorId);
            packet.add(Utilita.calcolaChecksum(packet));
        }


        return packet;
    }
    /*
        Creazione del datagramSocket che resta in attesa di ricevere richieste da parte del gateway.
        Quando riceve una richiesta reperisce il valore del sensore richiesto e la inoltra al gateway.
     */

    public void startServerBello() throws UnknownHostException {

        try {

            InetAddress hostname = InetAddress.getLocalHost();
            DatagramSocket socket = new DatagramSocket(6969);

            while (true) {

                byte[] buffer = new byte[5];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.setSoTimeout(0); // attesa infinita
                socket.receive(request);

                List<Byte> nicePacket = Arrays.asList(ArrayUtils.toObject(buffer));

                if(!Utilita.controllaPacchetto(nicePacket)){
                    System.out.println("Errore: pacchetto corrotto!");
                    continue;
                }

                List<Byte> ihatejava = createResponsePacket(Byte.toUnsignedInt(nicePacket.get(0)),
                        Byte.toUnsignedInt(nicePacket.get(2)));
                Byte[] ihatejavatwice = ihatejava.toArray(new Byte[ihatejava.size()]);
                byte[] ihatebuffer = toPrimitive(ihatejavatwice);

                DatagramPacket response = new DatagramPacket(ihatebuffer, ihatebuffer.length, request.getAddress(), request.getPort());
                socket.send(response);

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