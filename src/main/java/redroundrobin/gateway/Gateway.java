package redroundrobin.gateway;


import java.net.InetAddress;
import java.util.ArrayList;

public class Gateway {

    String nome;
    InetAddress indirizzoIP;
    int id;
    int porta;
    List<Device> devices = new ArrayList<>();



    Gateway(String nome, int id, int porta) {
        this.id = id;
        this.nome = nome;
        this.porta = porta;
    }

    /*
     * Metodo che restituisce i dati prodotti da un dispositivo*/
    String riceviDati() {
        return "";
    }


}
