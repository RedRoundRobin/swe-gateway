package com.redroundrobin.thirema.simulation;

import com.redroundrobin.thirema.gateway.models.Device;
import com.redroundrobin.thirema.gateway.models.Sensor;
import com.redroundrobin.thirema.gateway.utils.Utility;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DeviceSimulator {
    private int port;
    private List<Device> devices;

    public DeviceSimulator(int port, List<Device> devices) {
        this.port = port;
        this.devices = devices;
    }

    // Pacchetto di risposta
    public List<Byte> createResponsePacket(int idDevice, int idSensor) {
        List<Byte> packet = new ArrayList<>();

        Optional<Device> optionalDevice = devices.stream()
                .filter(device -> idDevice == device.getId())
                .findFirst();
        Optional<Sensor> optionalSensor = Optional.empty();

        if(optionalDevice.isPresent()) {
            Device device = optionalDevice.get();

            optionalSensor = device.getSensors().stream()
                    .filter(sensor -> idSensor == sensor.getId())
                    .findFirst();
        }

        if(optionalDevice.isPresent() && optionalSensor.isPresent()){
            packet.add((byte) idDevice);
            packet.add((byte) 1); // risposta con dato
            packet.add((byte) idSensor);
            packet.add((byte) optionalSensor.get().getData());
            packet.add(Utility.calculateCRC(packet));
        } else {
            packet.add((byte) idDevice);
            packet.add((byte) -1); // risposta con errore
            packet.add((byte) idSensor);
            packet.add(Utility.calculateCRC(packet));
        }

        return packet;
    }

    public void start() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            while (true) {
                byte[] requestBuffer = new byte[5];

                DatagramPacket requestDatagram = new DatagramPacket(requestBuffer, requestBuffer.length);
                socket.setSoTimeout(0); // attesa infinita
                socket.receive(requestDatagram);

                List<Byte> receivedPacket = Arrays.asList(ArrayUtils.toObject(requestBuffer));
                if(!Utility.checkIntegrity(receivedPacket)){
                    System.out.println("Error: corrupted packet!");
                    continue;
                }

                List<Byte> responsePacket = createResponsePacket(Byte.toUnsignedInt(receivedPacket.get(0)), Byte.toUnsignedInt(receivedPacket.get(2)));
                byte[] responseBuffer = Utility.convertPacket(responsePacket.stream().mapToInt(Byte::byteValue).toArray());

                DatagramPacket responseDatagram = new DatagramPacket(responseBuffer, responseBuffer.length, requestDatagram.getAddress(), requestDatagram.getPort());
                socket.send(responseDatagram);

                System.out.println("Success: packet sent!");
            }

        } catch (SocketTimeoutException exception) {
            System.out.println("Timeout error: " + exception.getMessage());
            exception.printStackTrace();
        } catch (IOException exception) {
            System.out.println("Client error: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //Imposto i sensori disponibili per la simulazione
        List<Sensor> sensors1 = new ArrayList<>(Arrays.asList(new Sensor(1, 21), new Sensor(2, 50), new Sensor(3, 4), new Sensor(4, 150)));
        Device device1 = new Device(1, sensors1);

        List<Sensor> sensors2 = new ArrayList<>(Arrays.asList(new Sensor(1, 234), new Sensor(2, 21), new Sensor(3, 32)));
        Device device2 = new Device(2, sensors2);

        List<Sensor> sensors3 = new ArrayList<>(Arrays.asList(new Sensor(1, 21), new Sensor(2, 23), new Sensor(3, 34), new Sensor(4, 54)));
        Device device3 = new Device(3, sensors3);

        List<Sensor> sensors4 = new ArrayList<>(Arrays.asList(new Sensor(1, 13), new Sensor(2, 22), new Sensor(3, 33), new Sensor(4, 44)));
        Device device4 = new Device(4, sensors4);

        List<Sensor> sensors5 = new ArrayList<>(Arrays.asList(new Sensor(1, 17), new Sensor(2, 62), new Sensor(3, 73), new Sensor(4, 47)));
        Device device5 = new Device(5, sensors5);

        List<Sensor> sensors6 = new ArrayList<>(Arrays.asList(new Sensor(1, 61), new Sensor(2, 27), new Sensor(3, 43), new Sensor(4, 46)));
        Device device6 = new Device(6, sensors6);

        List<Device> devices = new ArrayList<>(Arrays.asList(device1, device2, device3, device4, device5, device6));

        DeviceSimulator deviceSimulator = new DeviceSimulator(6969, devices);

        // Avvio del server che aspetta le richieste del gateway
        deviceSimulator.start();
    }
}