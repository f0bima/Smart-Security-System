#include <ESP8266WiFi.h>
#include <WiFiClient.h> 
#include <ESP8266WebServer.h>
#include <FirebaseArduino.h>
#include "Setting.h"
#include "web.h"
#include <EEPROM.h>
#include <ShiftRegister74HC595.h>
#include "DHT.h"
 
void write_String(char add,String data);
String read_String(char add);

ShiftRegister74HC595<1> sr(2, 0, 4);
DHT dht(DHTPIN, DHTTYPE);
ESP8266WebServer server(80);
IPAddress ip(11, 3, 20, 19);
IPAddress gateway(11, 3, 20, 1);
IPAddress subnet(255, 255, 255, 0);

void setup() { 
  Serial.begin(9600);
  EEPROM.begin(512);
  dht.begin();
  
  if(setEEPROM){
    write_String(ssid_add, "SSID");
    write_String(pass_add, "PASSWORD");
    write_String(pb_add, "PROJECT BUCKET");
  }
  
  pinMode(led, OUTPUT);

  WiFi.mode(WIFI_AP_STA);      
  WiFi.begin(string2char(read_String(ssid_add)), string2char(read_String(pass_add))); 
  
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  
  server.on("/", handleRoot);  
  server.on("/login", handleLogin);
  server.on("/setting", handleSetting);  
  server.on("/pb_set", handlePb_set);  
  server.on("/projectbucket", handleProjectbucket);  
  server.on("/style.css", handleCss);  
    
  const char * headerkeys[] = {"User-Agent","Cookie"} ;
  size_t headerkeyssize = sizeof(headerkeys)/sizeof(char*);  
  server.collectHeaders(headerkeys, headerkeyssize );
  
  server.begin();
  WiFi.softAPConfig(ip, gateway, subnet);  
  WiFi.softAP(ssid, pass);
}


void loop() {
  if(WiFi.status() != WL_CONNECTED) {
    pinMode(led, 1);
    delay(500);
    pinMode(led, 0);
    delay(500);
  }
  
  else{  
    pinMode(led, 0);

    for(int i = 0; i<8; i++){
      String str = "smart_security/" + read_String(pb_add) + field[i];     
      int stt = Firebase.getInt(str);
      sr.set(i, stt);
    }
    
    temp = dht.readTemperature();
    humadity = dht.readHumidity();        
    
    if (isnan(humadity) || isnan(temp)) {
      Firebase.setInt("smart_security/" + read_String(pb_add) + "/temp", -99);
      Firebase.setInt("smart_security/" + read_String(pb_add) + "/humadity", -99);
      return;
    }
    else{
      Firebase.setInt("smart_security/" + read_String(pb_add) + "/temp", round(temp));
      Firebase.setInt("smart_security/" + read_String(pb_add) + "/humadity", round(humadity));    
    }
    
    sr.updateRegisters();    
  }  
  
  server.handleClient();
  delay(100);  
}
