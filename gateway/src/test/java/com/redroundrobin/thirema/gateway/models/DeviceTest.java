package com.redroundrobin.thirema.gateway.models;

import org.junit.Test;

import static org.junit.Assert.*;

public class DeviceTest {

    @Test
    public void getDeviceId() {
        Device device = new Device(1);
        assertEquals(1, device.getDeviceId());
    }
}