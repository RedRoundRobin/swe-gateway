package com.devices.simulator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class ClientExample {

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException{
        //get the localhost IP address, if server is running on some other IP, you need to use that
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        for(int i=0; i<5;i++){
            
            //establish socket connection to server
            socket = new Socket(host.getHostName(), 6969);

            //write to socket using ObjectOutputStream
            oos = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Sending request to Socket Server");
            if(i==4)oos.writeObject("exit");
            else oos.writeObject(ClientExample.createRandomRequestPacket());

            //read the server response message
            ois = new ObjectInputStream(socket.getInputStream());
            String message = (String) ois.readObject();
            System.out.println("Message: " + message);

            //close resources
            ois.close();
            oos.close();
            Thread.sleep(100);
        }
    }

    // Creazione di un pacchetto random
    public static List<Byte> createRandomRequestPacket()
    {
        List<Byte> packet = new ArrayList<Byte>();
        Random rand = new Random();
        packet.add((byte) (1 + rand.nextInt(1)));
        packet.add((byte) 0); // richiesta
        packet.add((byte) (1 + rand.nextInt(2)));
        packet.add(connectionManager.calculateChecksum(packet));
        return packet;
    }
}
