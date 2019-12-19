package com.example.smartsecuritysystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    AdapterRelay adapter;
    List<ModelRelay> models;
    Context context;
    FirebaseAuth mAuth;
    Button btn_logout, btn_R1, btn_R2, btn_switch;
    int swipe, hg, celcius;
    TextView temp, nama, humadity;
    String uid;
    DatabaseReference db;
    CircleImageView photo;
    int Relay[]= new int[8];
    String Rid[] = {"R1","R2","R3","R4","R5","R6","R7","R8"};
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    Executor executor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        executor = new MainThreadExecutor();

        if (biometricPrompt == null)
            biometricPrompt = new BiometricPrompt(this, executor, callback);

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

        Glide.with(this).load(mAuth.getCurrentUser().getPhotoUrl()).into(photo);
        Log.e("photo", String.valueOf(mAuth.getCurrentUser().getPhotoUrl()));
        temp = findViewById(R.id.temp);
        humadity = findViewById(R.id.humidity);

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
                        for (int i=0;i <8;i++){
                            Relay[i] = dataSnapshot.child(Rid[i]).getValue(int.class);
                        }

                        celcius = dataSnapshot.child("temp").getValue(int.class);
                        hg = dataSnapshot.child("humadity").getValue(int.class);
                        temp.setText( Integer.toString(celcius) + (char) 0x00B0 );
                        humadity.setText(Integer.toString(hg));


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
        db.child("temp").setValue(22);
        db.child("humadity").setValue(40);
        db.child("kunci").setValue(1);
        db.child("magnet").setValue(0);

    }

    private void relayclick(){
        if (Relay[swipe] == 1){
            db.child(Rid[swipe]).setValue(0);
        }
        else {
            db.child(Rid[swipe]).setValue(1);
        }
    }

    private BiometricPrompt.PromptInfo buildBiometricPrompt() {
        return new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login")
                .setSubtitle("Login into your account")
                .setDescription("Touch your finger on the finger print sensor to authorise your account.")
                .setNegativeButtonText("Cancel")
                .build();
    }

    private BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {  // 1
        @Override
        public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {  // 2
//            if (errorCode == ERROR_NEGATIVE_BUTTON && biometricPrompt != null)
//                biometricPrompt.cancelAuthentication();  // 3
//            toast(errString.toString());
        }
        @Override
        public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) { // 4
            super.onAuthenticationSucceeded(result);
            toast("Authentication succeed");
        }

        @Override
        public void onAuthenticationFailed() {  // 5
            super.onAuthenticationFailed();
            toast("\"Application did not recognize the placed finger print. Please try again!\"");

        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;
            switch (menuItem.getItemId()){
                case R.id.item0:
                    Toast.makeText(MainActivity.this, "Home" , Toast.LENGTH_LONG).show();
                    break;

                case R.id.item1:
                    Toast.makeText(MainActivity.this, "Switch" , Toast.LENGTH_LONG).show();
                    break;

                case R.id.item2:
                    promptInfo = buildBiometricPrompt();
                    biometricPrompt.authenticate(promptInfo);
                    break;
            }
//            getSupportFragmentManager().beginTransaction().replace(R.id.Fragment, selectedFragment).commit();
            return true;
        }
    };

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
