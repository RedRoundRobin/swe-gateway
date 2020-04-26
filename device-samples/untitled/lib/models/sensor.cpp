//
// Created by Fritz on 24/04/2020.
//

#include "sensor.h"
sensor::sensor(int sensorId, int arduinoPort) : sensorId(sensorId), arduinoPort(arduinoPort) {}

sensor::sensor(int sensorId, int arduinoPort, bool cmdEnabled, bool toggleable) : sensorId(sensorId),
  arduinoPort(arduinoPort), cmdEnabled(cmdEnabled), toggleable(toggleable) {}

int sensor::getArduinoPort() const {
  return arduinoPort;
}

int sensor::getData() const {
  return data;
}

int sensor::getSensorId() const {
  return sensorId;
}

bool sensor::isCmdEnabled() const {
  return cmdEnabled;
}

bool sensor::isToggleable() const {
  return toggleable;
}

void sensor::setData(int data) {
  sensor::data = data;
}
