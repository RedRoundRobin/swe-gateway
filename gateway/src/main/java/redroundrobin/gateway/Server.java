package redroundrobin.gateway;


import java.net.UnknownHostException;
import java.util.Arrays;

public class Server {

    public static void main(String[] args) throws UnknownHostException {

        Sensore[] sensors1 = {
                new Sensore(21, 1),
                new Sensore(50, 2),
                new Sensore(4, 3),
                new Sensore(150, 4)
        };
        Dispositivo dispositivo1 = new Dispositivo(1);
        dispositivo1.getSensori().addAll(Arrays.asList(sensors1));

        Sensore[] sensors2 = {
                new Sensore(234, 1),
                new Sensore(21, 2),
                new Sensore(22, 3)
        };
        Dispositivo dispositivo2 = new Dispositivo(2);
        dispositivo2.getSensori().addAll(Arrays.asList(sensors2));

        Dispositivo[] dispositivi = {dispositivo1, dispositivo2};

        connectionManager man = new connectionManager(dispositivi, 6969);

        man.startServerBello();

        // System.out.println(man.createResponsePacket(1, 1));

    }
}