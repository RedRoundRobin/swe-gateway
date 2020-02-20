package redroundrobin.gateway;

public class Sensore {
    private byte id;
    private byte dato;

    public Sensore(byte id, byte dato) {
        this.id = id;
        this.dato = dato;
    }

    public byte getId() {
        return id;
    }

    public byte getDato() {
        return dato;
    }
}
