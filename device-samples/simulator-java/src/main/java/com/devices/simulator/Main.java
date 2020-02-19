package com.devices.simulator;
public class Main {

    public static void main(String[] args) {

        sensor[] sensors1 = {
                new sensor(21, 1),
                new sensor(50, 2),
                new sensor(4, 3),
                new sensor(150, 4)
        };
        device dispositivo1 = new device(1, sensors1);

        sensor[] sensors2 = {
                new sensor(234, 1),
                new sensor(21, 2),
                new sensor(22, 3)
        };
        device dispositivo2 = new device(2, sensors2);

        device[] dispositivi = {dispositivo1, dispositivo2};

        connectionManager man = new connectionManager(dispositivi, 6969);

        System.out.println(man.createResponsePacket(1, 1));
    }
}
