package redroundrobin.gateway;


import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class Dispositivo {
    private int id;
    private transient InetAddress indirizzo;
    private transient int porta;
    private List<Sensore> sensori = new ArrayList<>();

    public Dispositivo(int id, InetAddress indirizzo , int porta) {
        this.id = id;
        this.indirizzo = indirizzo;
        this.porta = porta;
    }

    public Dispositivo(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public InetAddress getIndirizzo() {
        return indirizzo;
    }

    public int getPorta() {
        return porta;
    }

    public List<Sensore> getSensori() {
        return sensori;
    }


}
