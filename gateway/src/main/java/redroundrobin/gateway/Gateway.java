package redroundrobin.gateway;


import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class Gateway {

    private InetAddress indirizzo;
    private int porta;
    private List<Dispositivo> dispositivi = new ArrayList<>();

    public Gateway(InetAddress indirizzo, int porta) {
        this.indirizzo = indirizzo;
        this.porta = porta;

    }

    public InetAddress getIndirizzo() {
        return indirizzo;
    }

    public int getPorta() {
        return porta;
    }

    public List<Dispositivo> getDispositivi() {
        return dispositivi;
    }

    /*
     * Metodo che restituisce i dati prodotti da un dispositivo*/
    String riceviDati(InetAddress indirizzo, int porta) {
        ExecutorService esecutori = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        while (true){
            esecutori.
        }

        return "";
    }

    public static void main(String args[]){

    }


}
