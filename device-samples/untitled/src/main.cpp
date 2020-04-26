/**
 * Blink
 *
 * Turns on an LED on for one second,
 * then off for one second, repeatedly.
 */
#include "Arduino.h"
#include "../lib/utils/utils.cpp"
#include "../lib/models/sensor.h"

// Set LED_BUILTIN if it is not defined by Arduino framework
// #define LED_BUILTIN 13

sensor sensors[6];

int inclinationSensor = A5;
int potentiometer1 = A1;
int potentiometer2 = A0;
int led1 = 2;
int led2 = 4;
int led3 = 7;

sensor *getSensorById(int sensorId) {
  for (sensor &s : sensors) {
    if (s.getSensorId() == sensorId) {
      return &s;
    }
  }
  return nullptr;
}

void setup()
{
  // initializa sensors
  sensors[0] = sensor(1, inclinationSensor);
  sensors[1] = sensor(2, potentiometer1);
  sensors[2] = sensor(3, potentiometer2);
  sensors[3] = sensor(4, led1, true, true);
  sensors[4] = sensor(5, led2, true, true);
  sensors[5] = sensor(6, led3, true, true);

  // initialize LED digital pin as an output.
  pinMode(inclinationSensor, INPUT);
  pinMode(potentiometer1, INPUT);
  pinMode(potentiometer2, INPUT);
  pinMode(led1, OUTPUT);
  pinMode(led2, OUTPUT);
  pinMode(led3, OUTPUT);
  digitalWrite(led1, HIGH);
  digitalWrite(led2, LOW);
  digitalWrite(led3, LOW);

  Serial.begin(2000000);

}

byte request[3];
byte response[3];

void loop()
{
  /*
  byte test[3];
  test[0] = 4;
  test[1] = 2;
  test[2] = 0;
  Serial.write(test, 3);
  delay(1000);
  Serial.println();
  */
  if (Serial.available() > 0) {
    Serial.readBytes(request, 3);

    sensor *s = getSensorById(request[0]);
    int reqType = request[1];
    int data = request[2];

    response[0] = request[0];

    if (s != nullptr && (reqType == 0 || (reqType == 2 && s->isCmdEnabled()))) {
      response[1] = 1;

      if (reqType == 0) {
        if (!s->isToggleable()) {
          int val = analogRead(s->getArduinoPort());
          int byteVal = map(val, 0, 1023, 0, 127);
          s->setData(byteVal);
        }
      } else {
        s->setData(data);
        if (s->isToggleable()) {
          digitalWrite(s->getArduinoPort(), s->getData() > 0 ? HIGH : LOW);
        } else {
          analogWrite(s->getArduinoPort(), s->getData());
        }
      }

      response[2] = s->getData();

    } else {
      response[1] = -1;
      response[2] = 0;
    }
    Serial.write(response, 3);
  }
  Serial.flush();
}