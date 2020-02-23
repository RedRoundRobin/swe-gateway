package com.devices.simulator;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class EsempioGateway {
    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket()) {
            while (true) {
                byte[] pacchetto = GestorePacchetti.creaPacchettoCasuale();

                DatagramPacket richiesta = new DatagramPacket(pacchetto, pacchetto.length, InetAddress.getLocalHost(), 6969);
                socket.send(richiesta);

                System.out.print("> REQ: ");
                System.out.print("[ ");
                for (byte elemento : pacchetto) {
                    System.out.print(elemento + " ");
                }
                System.out.println("]");

                byte[] buffer = new byte[5];
                DatagramPacket risposta = new DatagramPacket(buffer, buffer.length);
                socket.setSoTimeout(1000);
                socket.receive(risposta);

                System.out.print("< RES: ");
                System.out.print("[ ");
                for (byte b : buffer) {
                    System.out.print(b + " ");
                }
                System.out.println("]");

                Thread.sleep(1000);
            }
        }
        catch (SocketTimeoutException eccezione) {
            System.out.println("< RES: []");
        }
        catch (InterruptedException ignored) {}
        catch (Exception eccezione) {
            System.out.println("Errore " + eccezione.getClass() + ": " + eccezione.getMessage());
            eccezione.printStackTrace();
        }
    }
}
