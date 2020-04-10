package com.redroundrobin.thirema.gateway.models;

import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class SensorTest {

  private Sensor sensor1;

  @Before
  public void setUp() {
    sensor1 = new Sensor(1, 5);
  }

  @Test
  public void getSensorIdTest() {
    int expected = 1;
    int actual = sensor1.getSensorId();
    assertEquals(expected, actual);
  }

  @Test
  public void getDataTest() {
    int actualBaseDataSensor1 = 5;
    int actualRandomRange = 2;
    int data = sensor1.getData();
    int data1 = sensor1.getData();
    int data2 = sensor1.getData();
    int data3 = sensor1.getData();

    assertTrue(data <= actualBaseDataSensor1 + actualRandomRange);
    assertTrue(data1 <= actualBaseDataSensor1 + actualRandomRange);
    assertTrue(data2 <= actualBaseDataSensor1 + actualRandomRange);
    assertTrue(data3 <= actualBaseDataSensor1 + actualRandomRange);
  }

  @Test
  public void setTimestampTest() {
    long expected = new Random().nextLong();
    sensor1.setTimestamp(expected);
    long actual = sensor1.getTimestamp();
    assertEquals(expected, actual);
   }

  @Test
  public void setDataTest() {
    int actualBaseDataSensor1 = new Random().nextInt();
    int actualRandomRange = 2;
    sensor1.setData(actualBaseDataSensor1);
    long actual = sensor1.getData();
    assertTrue(sensor1.getData() <=
        actualBaseDataSensor1 + actualRandomRange
        && actualBaseDataSensor1 >=
        actualBaseDataSensor1 - actualRandomRange);
  }
}
