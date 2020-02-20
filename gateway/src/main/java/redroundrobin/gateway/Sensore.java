package redroundrobin.gateway;

public class Sensore {
    private int id;
    private int dato;

    public Sensore(int id, int dato) {
        this.id = id;
        this.dato = dato;
    }

    public int getId() {
        return id;
    }

    public int getDato() {
        return dato;
    }

    public void setDato(int dato) {
        this.dato = dato;
    }
}
