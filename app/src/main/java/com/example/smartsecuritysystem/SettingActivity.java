package com.example.smartsecuritysystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {

    Button btn_saveAll,btn_updateKeypass,btn_updateKeySSID,btn_setSSID;
    EditText txt_updateKeypass,txt_updateKeySSID,txt_SSID,txt_keySSID;
    TextView tv_ssid;

    FirebaseAuth mAuth;
    DatabaseReference db;
    String uid;

    private ModelSetting modelSetting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
        db = FirebaseDatabase.getInstance().getReference("smart_security").child(uid);

        btn_setSSID = (Button) findViewById(R.id.btn_setSSID);
        btn_updateKeypass = (Button) findViewById(R.id.btn_updateKeyPass);
        btn_updateKeySSID = (Button) findViewById(R.id.btn_updateSSIDKey);
        btn_saveAll = (Button) findViewById(R.id.btn_UpdateAll);

        txt_updateKeypass = (EditText) findViewById(R.id.txt_UpdatepassApp);
        txt_updateKeySSID = (EditText) findViewById(R.id.txt_updatepassUid);
        txt_SSID = (EditText) findViewById(R.id.txt_UserID);
        txt_keySSID = (EditText) findViewById(R.id.txt_passUid);

        modelSetting = new ModelSetting();

        btn_updateKeypass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txt_updateKeypass.length() != 0) {
                    modelSetting.setTxt_updateKeypass(txt_updateKeypass.getText().toString());
                    db.child("pass").setValue(modelSetting.getTxt_updateKeypass());
                    txt_updateKeypass.setText("");
                    toast(txt_updateKeypass.getText().toString());
                }else{
                    toast("KeyPass Belum di Isi");
                }
            }
        });

        btn_updateKeySSID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txt_updateKeySSID.getText().length() != 0){
                    modelSetting.setTxt_updateKeySSID(txt_updateKeySSID.getText().toString());
                    //ga mudeng syntax nggo connect ning key ne bim pie
                } else {
                    toast("Key SSID Belum di Isi");
                }
            }
        });

        btn_setSSID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((txt_SSID.getText().length() != 0) &&
                        (txt_keySSID.length() != 0) ){

                    modelSetting.setTxt_SSID(txt_SSID.getText().toString());
                    modelSetting.setTxt_keySSID(txt_keySSID.getText().toString());
                    //ga mudeng syntax nggo connect ning key ne bim pie
                } else {
                    toast("SSID atau Key SSID Belum di Isi");
                }
            }
        });

        btn_saveAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txt_updateKeypass.length() != 0){
                    btn_updateKeypass.performClick();
                };
                if (txt_updateKeySSID.length() != 0){
                    btn_updateKeySSID.performClick();
                };
                if ((txt_SSID.getText().length() != 0) &&
                        (txt_keySSID.length() != 0) ){
                    btn_setSSID.performClick();
                };
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();

                //Menampilkan Pop Up
//                Intent i = new Intent(getApplicationContext(), PopValidasiActivity.class);
//                startActivity(i);
            }
        });
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}
