package com.example.smartsecuritysystem;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;

public class KeyPassActivity extends AppCompatActivity {

    Button n1, n2, n3, n4, n5, n6, n7, n8, n9, n0, del, ok;
    String key[];
    TextView tv;
    int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_pass);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        count = 0;
        key = new String[6];
        n1  = findViewById(R.id.num1);
        n2  = findViewById(R.id.num2);
        n3  = findViewById(R.id.num3);
        n4  = findViewById(R.id.num4);
        n5  = findViewById(R.id.num5);
        n6  = findViewById(R.id.num6);
        n7  = findViewById(R.id.num7);
        n8  = findViewById(R.id.num8);
        n9  = findViewById(R.id.num9);
        n0  = findViewById(R.id.num0);
        del = findViewById(R.id.del);
        ok  = findViewById(R.id.enter);

        tv = findViewById(R.id.tv);

        del.setOnClickListener(v -> delete());
        ok.setOnClickListener(v -> enter());

        n1.setOnClickListener(v -> num((Button) v));
        n2.setOnClickListener(v -> num((Button) v));
        n3.setOnClickListener(v -> num((Button) v));
        n4.setOnClickListener(v -> num((Button) v));
        n5.setOnClickListener(v -> num((Button) v));
        n6.setOnClickListener(v -> num((Button) v));
        n7.setOnClickListener(v -> num((Button) v));
        n8.setOnClickListener(v -> num((Button) v));
        n9.setOnClickListener(v -> num((Button) v));
        n0.setOnClickListener(v -> num((Button) v));
    }

    private void num(Button v){
        String s = v.getText().toString();
        if (count<6){
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
            key[count] = s;
            count++;
        }
        else {
            Toast.makeText(this, "CLEAR", Toast.LENGTH_SHORT).show();
            tv.setText("");
            for (int i=0; i<6; i++){
                key[i] = "";
            }
            count=0;
            key[count] = s;
            count++;
        }
        printkey();
    }

    void printkey(){
        String s = "";
        for (int i=0; i< count; i++){
            s = s + key[i];
        }

        tv.setText(s);
    }

    void enter(){

    }

    void delete(){
        key[count] = "";
        count--;
        if (count<0){
            count = 0;
        }
        printkey();
    }
}
