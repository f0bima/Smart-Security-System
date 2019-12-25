// HOST TANPA https:// dan / belakang
#define FIREBASE_HOST "smart-security-system-933bb.firebaseio.com"
#define FIREBASE_AUTH "2BhdNVWPyPM1YJ0CIZ9fpoRLeBX3Yf6C4asKgPJH"
#define ssid "3S"
#define pass = "admin123";
#define led 13
#define magnet 12

#define ssid_add  10
#define pass_add  100
#define pass_ssid_add  150
#define pb_add    200
#define DHTPIN 14
#define DHTTYPE DHT11

#define setEEPROM false
boolean lockstate = false;
boolean lockchange = false;

String field[]  = {"/R1","/R2","/R3","/R4","/R5","/R6","/R7","/kunci"};
float temp, humadity;

unsigned long interval=10000;
unsigned long previousMillis=0;

//ssid "3S"
//pass "admin123"
//
//setting wifi sama project bucket 11.3.20.19 
//
//apk = https://github.com/f0bima/Smart-Security-System
