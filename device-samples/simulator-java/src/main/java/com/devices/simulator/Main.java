package com.devices.simulator;

public class Main {

    public static void main(String[] args) {

        sensor temperature = new sensor("Temperatura", 21, 1);
        sensor humidity = new sensor("Umidita", 30, 2);
        sensor pression = new sensor("Pressione", 1, 3);
    }
}
