package com.example.smartsecuritysystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    AdapterRelay adapter;
    List<ModelRelay> models;
    Context context;
    FirebaseAuth mAuth;
    Button btn_logout, btn_R1, btn_R2, btn_switch;
    int swipe;
    TextView temp;
    String uid;
    DatabaseReference db;
    int Relay[]= new int[8];
    String Rid[] = {"R1","R2","R3","R4","R5","R6","R7","R8"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        models = new ArrayList<>();
        for (int i=0;i <8;i++){
            models.add(new ModelRelay(Rid[i], "RELAY " + Integer.toString(i+1), 1));
        }
//        models.add(new ModelRelay("R1", "RELAY 1", 1));
//        models.add(new ModelRelay("R2", "RELAY 2", 1));
//        models.add(new ModelRelay("R3", "RELAY 3", 1));

        adapter = new AdapterRelay(models, this);

        viewPager = findViewById(R.id.swiperelay);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(20,0,20,0);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();

        db = FirebaseDatabase.getInstance().getReference("smart_security").child(uid);

        btn_logout = findViewById(R.id.logout);
        temp = findViewById(R.id.temp);
        temp.setText( "19" + (char) 0x00B0 );
        btn_switch = findViewById(R.id.btn_switch);

        btn_logout.setOnClickListener(v -> logout());
        btn_switch.setOnClickListener(v -> relayclick());

//        btn_setdb.setOnClickListener(v -> setdb());

//        btn_R1.setOnClickListener(v -> setR1());
//        btn_R2.setOnClickListener(v -> setR2());

//        db.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Relay[0] = dataSnapshot.child("R1").getValue(int.class);
//                Relay[1] = dataSnapshot.child("R2").getValue(int.class);
//                btn_R1.setText(Integer.toString(Relay[0]));
//                btn_R2.setText(Integer.toString(Relay[1]));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()){
                    setdb();
                }
                else {
                    try {
//                        Relay[0] = dataSnapshot.child("R1").getValue(int.class);
//                        Relay[1] = dataSnapshot.child("R2").getValue(int.class);
                        for (int i=0;i <8;i++){
                            Relay[i] = dataSnapshot.child(Rid[i]).getValue(int.class);
                        }

//                        btn_R1.setText(Integer.toString(Relay[0]));
//                        btn_R2.setText(Integer.toString(Relay[1]));

                        if (Relay[swipe] == 1){
                            btn_switch.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_switch_on));
                        }
                        else {
                            btn_switch.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_switch_off));
                        }
                    }

                    catch (Exception e){
//                        Toast.makeText(MainActivity.this, "Database Berhas" , Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        db.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                Toast.makeText(MainActivity.this, "CHILD ADDED", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
////                Relay[0] = dataSnapshot.child("R1").getValue(int.class);
////                Relay[1] = dataSnapshot.child("R2").getValue(int.class);
////                btn_R1.setText(Integer.toString(Relay[0]));
////                btn_R2.setText(Integer.toString(Relay[1]));
//                Toast.makeText(MainActivity.this, "CHILD CHANGE" , Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });




        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                if (position < (adapter.getCount()-1) && position < (colo))
            }

            @Override
            public void onPageSelected(int position) {
                swipe = position;
                if (Relay[position] == 1){
                    btn_switch.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_switch_on));
                }
                else {
                    btn_switch.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_switch_off));
                }

//                Toast.makeText(MainActivity.this, Integer.toString(position) , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        String email = mAuth.getCurrentUser().getEmail();
        Toast.makeText(this, email,Toast.LENGTH_SHORT).show();
    }

    private void logout(){
        AuthActivity.Logout();
        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

    private void setdb(){
        for (int i = 0; i<8; i++){
            db.child(Rid[i]).setValue(0);
        }
//        db.child("R1").setValue(1);
//        db.child("R2").setValue(1);
    }

    private void relayclick(){
        if (Relay[swipe] == 1){
            db.child(Rid[swipe]).setValue(0);
        }
        else {
            db.child(Rid[swipe]).setValue(1);
        }
    }
}
