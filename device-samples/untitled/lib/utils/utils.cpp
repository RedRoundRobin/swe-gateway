//
// Created by Fritz on 23/04/2020.
//
#include "Arduino.h"

class utils {
public:
  static int* stringToInt(String s, int length) {
    char charBuff[s.length()];
    s.toCharArray(charBuff, s.length());
    return new int[3]{charBuff[0] - '0',charBuff[2] - '0', charBuff[4]};
  }

  static String bytesToString(byte* integers, int length) {
    String s = String();
    for (int i = 0 ; i < length ; i++) {
      s += integers[i];
    }
    return s;
  }
};