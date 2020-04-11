package com.redroundrobin.thirema.gateway.utils;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.redroundrobin.thirema.gateway.utils.Utility.*;
import static org.junit.Assert.*;

public class UtilityTest {
    int[] rightPacketToBeConverted;
    List<Byte> rightPacket;
    List<Byte> goodFakePacket;
    List<Byte> badFakePacket;

    @Before
    public void setUp()  {
        rightPacketToBeConverted = new int[] { 1, 2, 3 };
        rightPacket = new ArrayList<>();
        rightPacket.add((byte) 42);
        rightPacket.add((byte) 42);
        rightPacket.add((byte) 42);
        rightPacket.add((byte) 42);
        goodFakePacket = new ArrayList<>();
        goodFakePacket.add((byte) 42);
        goodFakePacket.add((byte) 42);
        goodFakePacket.add((byte) 42);
        goodFakePacket.add((byte) 42);
        goodFakePacket.add((byte) -46);
        badFakePacket = new ArrayList<>();
        badFakePacket.add((byte) 42);
        badFakePacket.add((byte) 42);
        badFakePacket.add((byte) 42);
        badFakePacket.add((byte) 42);
        badFakePacket.add((byte) 69);
    }

    @Test
    public void TestGoodPacketToConvertPacket() {
        byte[] converted = convertPacket(rightPacketToBeConverted);
        assertEquals(1,(int) converted[0]);
        assertEquals(2,(int) converted[1]);
        assertEquals(3,(int) converted[2]);
    }


    @Test
    public void TestCalculateCRC() {
        byte result = calculateCrc(rightPacket);
        assertEquals((byte) -46, result);
    }

    @Test
    public void TestCheckIntegrityGoodPacket() {
        boolean res = checkIntegrity(goodFakePacket);
        assertTrue(res);
    }

    @Test
    public void TestCheckIntegrityBadPacket() {
        boolean res = checkIntegrity(badFakePacket);
        assertFalse(res);
    }
}