//
// Created by Fritz on 24/04/2020.
//

#ifndef UNTITLED_SENSOR_H
#define UNTITLED_SENSOR_H


class sensor {
private:
    int sensorId;
    int arduinoPort;
    bool cmdEnabled = false;
    bool toggleable = false;
    int data;

public:
    sensor() {}
    sensor(int sensorId, int arduinoPort);
    sensor(int sensorId, int arduinoPort, bool cmdEnabled, bool toggleable);

    int getArduinoPort() const;
    int getData() const;
    int getSensorId() const;

    bool isCmdEnabled() const;
    bool isToggleable() const;

    void setData(int data);
};


#endif //UNTITLED_SENSOR_H
