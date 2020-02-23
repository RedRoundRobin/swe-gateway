package redroundrobin.gateway;

public class Sensore {
    private int id;
    private int dato;

    public Sensore(int id, int dato) {
        this.id = id;
        this.dato = dato;
    }

    public int ottieniId() {
        return id;
    }

    public int ottieniDato() {
        return dato;
    }

    public void impostaDato(int dato) {
        this.dato = dato;
    }
}
