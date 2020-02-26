package com.redroundrobin.thirema.gateway;

import com.redroundrobin.thirema.gateway.models.Device;
import com.redroundrobin.thirema.gateway.utils.Producer;
import com.redroundrobin.thirema.gateway.utils.Translator;
import com.redroundrobin.thirema.gateway.utils.Utility;
import org.apache.commons.lang3.ArrayUtils;

import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.redroundrobin.thirema.gateway.utils.Utility.calculateCRC;

public class Gateway {
    private InetAddress address;
    private int port;

    private String name;
    private List<Device> devices; // Da prendere dalla configurazione del gateway

    private int storedPacket; // Da prendere dalla configurazione del gateway
    private int storingTime; // Da prendere dalla configurazione del gateway in millisecondi

    public Gateway(InetAddress address, int port, String name, List<Device> devices, int storedPacket, int storingTime) {
        this.address = address;
        this.port = port;

        this.name = name;
        this.devices = devices;

        this.storedPacket = storedPacket; // Accumulo di pacchetti di default
        this.storingTime = storingTime; // Tempo di accumulo di default
    }

    // Metodo che reperisce i dati dai dispositivi e dopo averne accumulati "storedPacket" o aver aspettato "storingTime" millisecondi li invia al topic di Kafka specificato
    public void start() {
        try (DatagramSocket socket = new DatagramSocket(); Producer producer = new Producer(name, "localhost:29092")) {
            Translator translator = new Translator();

            long timestamp = System.currentTimeMillis();
            int packetNumber = 0;
            int deviceNumber = devices.size();

            // Ciclo in cui vengono effettuate tutte le richieste per ogni sensore
            while (true) {
                for(int disp = 0; disp < deviceNumber; disp++){
                     for(int sens = 0; sens < devices.get(disp).getSensors().size(); sens++){

                         byte[] requestBuffer = createRequestPacket(disp, sens);
                         DatagramPacket requestDatagram = new DatagramPacket(requestBuffer, requestBuffer.length, address, port);
                         socket.send(requestDatagram);

                         System.out.print("> REQ: [ ");
                         for (byte field : requestBuffer) {
                             System.out.print(field + " ");
                         }
                         System.out.println("]");

                         byte[] responseBuffer = new byte[5];
                         DatagramPacket responseDatagram = new DatagramPacket(responseBuffer, responseBuffer.length);
                         socket.setSoTimeout(150000);
                         socket.receive(responseDatagram);

                         List<Byte> responsePacket = Arrays.asList(ArrayUtils.toObject(responseBuffer));

                         if (Utility.checkIntegrity(responsePacket)) {
                             if (translator.addSensor(responseBuffer)) {
                                 packetNumber++;
                             }
                         }

                         long timeSpent = System.currentTimeMillis() - timestamp;

                         if (packetNumber > storedPacket || timeSpent > storingTime) {
                             String data = translator.getJSON();
                             producer.executeProducer(name, data);
                             timestamp = System.currentTimeMillis();
                             packetNumber = 0;
                         }

                         System.out.print("< RES: [ ");
                         for (byte field : responseBuffer) {
                             System.out.print(field + " ");
                         }
                         System.out.println("]");

                         Thread.sleep(250); // Da tenere solo per fare test
                     }
                }
            }
        }
        catch (SocketTimeoutException exception) {
            System.out.println("< RES: []");
        }
        catch (InterruptedException ignored) {}
        catch (Exception exception) {
            System.out.println("Error " + exception.getClass() + ": " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    // Creazione di un pacchetto di richiesta dati per uno dei dispositivi disponibili nel Server
    public byte[] createRequestPacket(int devIndex, int senIndex) {
        int deviceId = devices.get(devIndex).getId();
        int sensorId = devices.get(devIndex).getSensors().get(senIndex).getId();

        byte device = (byte) (deviceId); // prendo uno tra gli id
        byte operation = 0;
        byte sensor = (byte) (sensorId); // prendo uno dei sensori del dispositivo
        byte data = 0;

        List<Byte> packet = new ArrayList<>(Arrays.asList(device, operation, sensor, data));

        return new byte[] {device, operation, sensor, data, calculateCRC(packet)};
    }


}
