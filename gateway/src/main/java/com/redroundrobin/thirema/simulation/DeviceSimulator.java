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
                .filter(device -> idDevice == device.getDeviceId())
                .findFirst();
        Optional<Sensor> optionalSensor = Optional.empty();

        if(optionalDevice.isPresent()) {
            Device device = optionalDevice.get();

            optionalSensor = device.getSensors().stream()
                    .filter(sensor -> idSensor == sensor.getSensorId())
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

}