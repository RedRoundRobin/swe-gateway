package com.redroundrobin.thirema.gateway;

import com.redroundrobin.thirema.gateway.models.Device;
import com.redroundrobin.thirema.gateway.models.Gateway;
import com.redroundrobin.thirema.gateway.models.Sensor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GatewayManagerTest {
    GatewayManager gatewayManager;
    String config;
    String otherConfig;
    String wrongConfig;
    private final PrintStream OriginalTestStartShouldThrowSocketTimeOutException = System.out;
    private final ByteArrayOutputStream TestStartShouldThrowSocketTimeOutExceptionContent = new ByteArrayOutputStream();

    @Before
    public void setUp() {
        System.setOut(new PrintStream(TestStartShouldThrowSocketTimeOutExceptionContent));
        config = "{\"address\":\"127.0.1.1\",\"port\":6969,\"name\":\"US-GATEWAY-1\",  \"devices\":  [{\"[deviceId\":0,\"frequency\":0,\"sensors\":[{\"sensorId\":0,\"data\":0},{\"sensorId\":1,\"data\":1},{\"sensorId\":2,\"data\":2},{\"sensorId\":3,\"data\":3}]},{\"deviceId\":1,\"sensors\":[{\"sensorId\":0,\"data\":0},{\"sensorId\":1,\"data\":1},{\"sensorId\":2,\"data\":2},{\"sensorId\":3,\"data\":3}]}],  \"maxStoredPacket\":5,  \"maxStoringTime\":6000}";
        otherConfig ="{\"address\":\"127.0.1.1\",\"port\":6969,\"name\":\"US-GATEWAY-1\",  \"devices\":  [{\"[deviceId\":0,\"frequency\":0,\"sensors\":[{\"sensorId\":0,\"data\":0}]}],  \"maxStoredPacket\":5,  \"maxStoringTime\":6000}";

        wrongConfig = "";
        Gateway gateway = Gateway.buildFromConfig(config);
        gatewayManager = new GatewayManager(gateway, 10, 10);
    }

    @After
    public void restoreStreams() {
        System.setOut(OriginalTestStartShouldThrowSocketTimeOutException);
    }

    @Test
    public void TestNameGetter() throws UnknownHostException {
        Gateway gateway = new Gateway(InetAddress.getByName("127.0.0.1"), 6969, "US-GATEWAY-1", new ArrayList<>());
        GatewayManager test = new GatewayManager(gateway, 5, 3);
        assertEquals("US-GATEWAY-1", test.getName());
    }

    @Test
    public void TestCreateRequestPacket() {
        Device d = gatewayManager.getDevices().get(0);
        if (d != null) {
            Sensor s = d.getSensors().get(0);
            byte[] packet = gatewayManager.createRequestPacket(d, s);
            int device = packet[0]; // prendo uno tra gli id
            int operation = packet[1];
            int sensor = packet[3]; // prendo uno dei sensori del dispositivo
            assertEquals(0, device);
            assertEquals(0, operation);
            assertEquals(0, sensor);
        } else {
            assertTrue(false);
        }

    }

    @Test
    public void TestBuildFromRightConfig() {
        Gateway gateway = Gateway.buildFromConfig(config);
        GatewayManager rightGatewayManager = new GatewayManager(gateway, 1, 1);
        assertEquals("US-GATEWAY-1", rightGatewayManager.getName());
    }

    @Test(expected = NullPointerException.class)
    public void TestBuildFromWrongConfig() {
        Gateway gateway = Gateway.buildFromConfig(wrongConfig);
        GatewayManager wrong = new GatewayManager(gateway, 1, 1);
        wrong.getName();
    }
/*
    @Test
    public void TestInitShouldntFindAnySensor() {
        Gateway gateway = Gateway.buildFromConfig(otherConfig);
        GatewayManager gatewayManager = new GatewayManager(gateway, 1, 1);
        gatewayManager.init();
        assertTrue(gatewayManager.getDevices().isEmpty());
        //assertEquals("", TestStartShouldThrowSocketTimeOutExceptionContent.toString());
    }

    @Test
    public void TestStartShouldThrowSocketTimeout() throws UnknownHostException {
        List<Sensor> sens = new ArrayList<>();
        sens.add(new Sensor(1, 0));
        List<Device> devs = new ArrayList<>();
        devs.add(new Device(1, sens));

        Gateway gateway = new Gateway(InetAddress.getByName("127.0.0.1"), 6969, "US-GATEWAY-1", devs);
        GatewayManager test = new GatewayManager(gateway, 5, 3);
        test.start();
        assertTrue(true);
    }*/
}
