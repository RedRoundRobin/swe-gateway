package com.devices.simulator;

public class device {

    private int deviceId = 0;

    private sensor[] sensors = {};

    public device(int id, sensor[] ss){
        deviceId = id;
        sensors = ss;
    }

    public int getId() {
        return deviceId;
    }

    public sensor[] getSensors() {
        return sensors;
    }
}
