#include <Arduino.h>
#include "ESP8266WiFi.h"
 
// PIN
#define LED_RED D1
#define LED_YEL D2
#define LED_GRE D3

// Valori di partenza
int power = 0;
long duration;
int distance;
int nwifi;

// Inizializzazione..
void setup() {
  Serial.begin(115200);
  delay(1000);
  pinMode(LED_BUILTIN, OUTPUT); 
  pinMode(LED_RED, OUTPUT);
  pinMode(LED_YEL, OUTPUT);
  pinMode(LED_GRE, OUTPUT);
  pinMode(SR_TRIG, OUTPUT);
  pinMode(SR_ECHO, INPUT); 
  digitalWrite(LED_BUILTIN, LOW);
}

// Loop cycle
void loop() {

  // Potenziometro

  power = analogRead(A0);

  if (power > 900) {
    digitalWrite(LED_GRE, HIGH);
    digitalWrite(LED_YEL, HIGH);
    digitalWrite(LED_RED, HIGH);
  }
  else if (power > 500) {
    digitalWrite(LED_GRE, HIGH);
    digitalWrite(LED_YEL, HIGH);
    digitalWrite(LED_RED, LOW);
  }
  else if (power > 120) {
    digitalWrite(LED_GRE, HIGH);
    digitalWrite(LED_YEL, LOW);
    digitalWrite(LED_RED, LOW);
  } 
  else {
    digitalWrite(LED_GRE, LOW);
    digitalWrite(LED_YEL, LOW);
    digitalWrite(LED_RED, LOW);
  }

  nwifi = WiFi.scanNetworks();  

  // == DEBUG == 

  Serial.print("Power: ");
  Serial.print(power);
  Serial.print(" - N. di WIFI: ");
  Serial.println(nwifi);
  delay(500);
}