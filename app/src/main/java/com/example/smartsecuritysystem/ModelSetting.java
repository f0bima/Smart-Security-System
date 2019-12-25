package com.example.smartsecuritysystem;

public class ModelSetting {
    String txt_updateKeypass;
    String txt_updateKeySSID;
    String txt_SSID;
    String txt_keySSID;

//    public ModelSetting(String updateKeypass, String updatekeySSID, String SSID, String keySSID){
//        txt_updateKeypass = updateKeypass;
//        txt_updateKeySSID = updatekeySSID;
//        txt_keySSID = keySSID;
//        txt_SSID = SSID;
//    }


    public String getTxt_updateKeypass() {
        return txt_updateKeypass;
    }

    public void setTxt_updateKeypass(String txt_updateKeypass) {
        this.txt_updateKeypass = txt_updateKeypass;
    }

    public String getTxt_updateKeySSID() {
        return txt_updateKeySSID;
    }

    public void setTxt_updateKeySSID(String txt_updateKeySSID) {
        this.txt_updateKeySSID = txt_updateKeySSID;
    }

    public String getTxt_SSID() {
        return txt_SSID;
    }

    public void setTxt_SSID(String txt_SSID) {
        this.txt_SSID = txt_SSID;
    }

    public String getTxt_keySSID() {
        return txt_keySSID;
    }

    public void setTxt_keySSID(String txt_keySSID) {
        this.txt_keySSID = txt_keySSID;
    }
}
