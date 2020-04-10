package com.redroundrobin.thirema.gateway;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GatewayTest {
    Gateway gateway;

    @Before
    public void setUp() throws Exception {
        String config = "{\"address\":\"127.0.1.1\",\"port\":6969,\"name\":\"US-GATEWAY-1\",  \"devices\":  [{\"[deviceId\":0,\"timestamp\":0,\"sensors\":[{\"sensorId\":0,\"timestamp\":0,\"data\":0},{\"sensorId\":1,\"timestamp\":0,\"data\":1},{\"sensorId\":2,\"timestamp\":0,\"data\":2},{\"sensorId\":3,\"timestamp\":0,\"data\":3}]},{\"deviceId\":0,\"timestamp\":0,\"sensors\":[{\"sensorId\":0,\"timestamp\":0,\"data\":0},{\"sensorId\":1,\"timestamp\":0,\"data\":1},{\"sensorId\":2,\"timestamp\":0,\"data\":2},{\"sensorId\":3,\"timestamp\":0,\"data\":3}]}],  \"storedPacket\":5,  \"storingTime\":6000}";
        gateway = gateway.BuildFromConfig(config);
    }

    @Test
    public void TestNameGetter() {
        String name = gateway.getName();
        assertEquals("US-GATEWAY-1", name);
    }

    @Test
    public void TestCreateRequestPacket() {

    }
}