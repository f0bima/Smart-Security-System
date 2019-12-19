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
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    AdapterRelay adapter;
    List<ModelRelay> models;
    Context context;
    FirebaseAuth mAuth;
    Button btn_logout, btn_R1, btn_R2, btn_switch;
    int swipe;
    TextView temp, nama;
    String uid;
    DatabaseReference db;
    CircleImageView photo;
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

        adapter = new AdapterRelay(models, this);

        viewPager = findViewById(R.id.swiperelay);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(20,0,20,0);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();

        db = FirebaseDatabase.getInstance().getReference("smart_security").child(uid);

        btn_logout = findViewById(R.id.logout);
        nama = findViewById(R.id.nama);
        photo = findViewById(R.id.profile_image);

        nama.setText(mAuth.getCurrentUser().getDisplayName());

//        photo.setImageURI(Uri.parse("https:/lh3.googleusercontent.com/-nly3UUOwTuE/AAAAAAAAAAI/AAAAAAAAAAA/ACHi3rdL9mQhAwkSWlGN9v3oahrpa0gXsg/s96-c/photo.jpg"));
        Glide.with(this).load(mAuth.getCurrentUser().getPhotoUrl()).into(photo);
        Log.e("photo", String.valueOf(mAuth.getCurrentUser().getPhotoUrl()));
        temp = findViewById(R.id.temp);
        temp.setText( "19" + (char) 0x00B0 );
        btn_switch = findViewById(R.id.btn_switch);

        btn_logout.setOnClickListener(v -> logout());
        btn_switch.setOnClickListener(v -> relayclick());

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
