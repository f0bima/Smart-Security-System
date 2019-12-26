package com.example.smartsecuritysystem;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {

    Button btn_pb,btn_updateKeypass,btn_setSSID;
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

        btn_updateKeypass = (Button) findViewById(R.id.btn_updateKeyPass);

        btn_pb = (Button) findViewById(R.id.set_projectbucket);

        btn_setSSID = findViewById(R.id.set_ssid);


        txt_updateKeypass = (EditText) findViewById(R.id.txt_UpdatepassApp);

        modelSetting = new ModelSetting();

        btn_updateKeypass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txt_updateKeypass.length() != 0) {
                    modelSetting.setTxt_updateKeypass(txt_updateKeypass.getText().toString());
                    db.child("pass").setValue(Integer.parseInt(modelSetting.getTxt_updateKeypass()));
//                    txt_updateKeypass.setText("");
//                    toast(txt_updateKeypass.getText().toString());
                }else{
                    toast("KeyPass Belum di Isi");
                }
            }
        });


        btn_pb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final android.content.ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Source Text", uid);
                clipboardManager.setPrimaryClip(clipData);

                String url = "http://11.3.20.19/projectbucket";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

                //Menampilkan Pop Up
//                Intent i = new Intent(getApplicationContext(), PopValidasiActivity.class);
//                startActivity(i);
            }
        });

        btn_setSSID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://11.3.20.19/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}
