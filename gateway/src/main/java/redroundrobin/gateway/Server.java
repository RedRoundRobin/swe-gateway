package redroundrobin.gateway;


import java.net.UnknownHostException;
import java.util.Arrays;

public class Server {


    public static void main(String[] args) throws UnknownHostException {

        //Imposto i sensori disponibili per la simulazione
        Sensore[] sensori1 = {
                new Sensore(1, 21),
                new Sensore(2, 50),
                new Sensore(3, 4),
                new Sensore(4, 150)
        };
        Dispositivo dispositivo1 = new Dispositivo(1);
        dispositivo1.getSensori().addAll(Arrays.asList(sensori1));

        Sensore[] sensori2 = {
                new Sensore(1, 234),
                new Sensore(2, 21),
                new Sensore(3, 32)
        };
        Dispositivo dispositivo2 = new Dispositivo(2);
        dispositivo2.getSensori().addAll(Arrays.asList(sensori2));

        Sensore[] sensori3 = {
                new Sensore(1, 21),
                new Sensore(2, 23),
                new Sensore(3, 34),
                new Sensore(4, 54)
        };
        Dispositivo dispositivo3 = new Dispositivo(3);
        dispositivo3.getSensori().addAll(Arrays.asList(sensori3));

        Sensore[] sensori4 = {
                new Sensore(1, 13),
                new Sensore(2, 22),
                new Sensore(3, 33),
                new Sensore(4, 44)
        };
        Dispositivo dispositivo4 = new Dispositivo(4);
        dispositivo4.getSensori().addAll(Arrays.asList(sensori4));

        Sensore[] sensori5 = {
                new Sensore(1, 17),
                new Sensore(2, 62),
                new Sensore(3, 73),
                new Sensore(4, 47)
        };
        Dispositivo dispositivo5 = new Dispositivo(5);
        dispositivo5.getSensori().addAll(Arrays.asList(sensori5));

        Sensore[] sensori6 = {
                new Sensore(1, 61),
                new Sensore(2, 27),
                new Sensore(3, 43),
                new Sensore(4, 46)
        };
        Dispositivo dispositivo6 = new Dispositivo(6);
        dispositivo6.getSensori().addAll(Arrays.asList(sensori6));



        Dispositivo[] dispositivi = {dispositivo1, dispositivo2, dispositivo3, dispositivo4, dispositivo5, dispositivo6};

        connectionManager man = new connectionManager(dispositivi, 6969);

        //Avvio del server che aspetta le richieste del gateway
        man.startServerBello();

        // System.out.println(man.createResponsePacket(1, 1));

    }
}