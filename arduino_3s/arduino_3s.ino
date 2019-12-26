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
    write_String(pass_ssid_add, "admin123");        
  }
  
  pinMode(led, OUTPUT);
  pinMode(magnet, INPUT_PULLUP);

  WiFi.mode(WIFI_AP_STA);      
  WiFi.begin(string2char(read_String(ssid_add)), string2char(read_String(pass_add))); 
  
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  
  server.on("/", handleRoot);  
  server.on("/login", handleLogin);
  server.on("/sett_ssid", handleSetting);  
  server.on("/set_pass", handleset_pass);  
  server.on("/setpass", handlepass);  
  server.on("/pb_set", handlePb_set);  
  server.on("/projectbucket", handleProjectbucket);  
  server.on("/style.css", handleCss);  
    
  const char * headerkeys[] = {"User-Agent","Cookie"} ;
  size_t headerkeyssize = sizeof(headerkeys)/sizeof(char*);  
  server.collectHeaders(headerkeys, headerkeyssize );
  pbstr = read_String(pb_add);
  server.begin();
  WiFi.softAPConfig(ip, gateway, subnet);  
  WiFi.softAP(ssid, string2char(read_String(pass_ssid_add)));
}


void loop() {
  unsigned long currentMillis = millis();
  if(WiFi.status() != WL_CONNECTED) {
    digitalWrite(led, 1);
    delay(500);
    digitalWrite(led, 0);
    delay(500);
  }
  
  else{      
    digitalWrite(led, 0);
    temp = dht.readTemperature();
    humadity = dht.readHumidity();        
    
    if (!isnan(humadity) || !isnan(temp)) {
//      Firebase.setInt("smart_security/" + read_String(pb_add) + "/temp", -99);
//      Firebase.setInt("smart_security/" + read_String(pb_add) + "/humadity", -99);
//      return;
//    }
//    else{
      Firebase.setInt("smart_security/" + pbstr + "/temp", round(temp));
      Firebase.setInt("smart_security/" + pbstr + "/humadity", round(humadity));    
    }

    for(int i = 0; i<7; i++){
      String str = "smart_security/" + pbstr + field[i];     
      int stt = Firebase.getInt(str);
      sr.set(i, stt);
    }

    String str = "smart_security/" + pbstr + field[7];     
    int stt_lock = Firebase.getInt(str);
    if (stt_lock==1 && lockchange==false){
      sr.set(7, stt_lock);
      lockstate = true;
      previousMillis = millis();
      lockchange = true;
//      Serial.println("UNLOCK");
    }     

//    Serial.println((unsigned long)(currentMillis - previousMillis));
    unsigned long dl = (unsigned long)(currentMillis - previousMillis);
    if (lockstate==true && (unsigned long)dl >= interval && (unsigned long) dl <= 20000) {         
//      Serial.println("Lock");
      sr.set(7, 0);
      Firebase.setInt("smart_security/" + pbstr + "/kunci", 0);
      lockstate = false;
      lockchange = false;
    }      
    
    Firebase.setInt("smart_security/" + pbstr + "/magnet", digitalRead(magnet));
    
    
    
    sr.updateRegisters();    
  }  
  
  server.handleClient();
//  delay(100);  
}
