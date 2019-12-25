package com.example.smartsecuritysystem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    AdapterRelay adapter;
    List<ModelRelay> models;
    Context context;
    FirebaseAuth mAuth;
    Button btn_logout, btn_R1, btn_R2, btn_switch;
    int swipe, hg, celcius;
    TextView temp, nama, humadity, mydate, sapaan, magnet_text, kunci_text;
    String uid;
    DatabaseReference db;
    CircleImageView photo;
    int Relay[]= new int[8];
    int kunci, magnet, pass;
    String Rid[] = {"R1","R2","R3","R4","R5","R6","R7"};
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    Executor executor;
    ImageView magnet_img, kunci_img;


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
        for (int i=0;i <7;i++){
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
        magnet_text = findViewById(R.id.magnet);
        kunci_text = findViewById(R.id.doorlock);
        magnet_img = findViewById(R.id.magnet_img);
        kunci_img = findViewById(R.id.doorlock_img);

        nama.setText(mAuth.getCurrentUser().getDisplayName());

        Glide.with(this).load(mAuth.getCurrentUser().getPhotoUrl()).into(photo);
        Log.e("photo", String.valueOf(mAuth.getCurrentUser().getPhotoUrl()));

        temp = findViewById(R.id.temp);
        humadity = findViewById(R.id.humidity);

        btn_switch = findViewById(R.id.btn_switch);


        btn_switch.setOnClickListener(v -> relayclick());
        photo.setOnClickListener(v -> logout());


        Date anotherCurDate = new Date();
        SimpleDateFormat formatjam = new SimpleDateFormat("H");
        int Jam = Integer.parseInt(formatjam.format(anotherCurDate));
        sapaan = (TextView) findViewById(R.id.txt_sapaanWaktu);
        sapaan.setText("Good " + myTimes(Jam) );


        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy '|' H:mm");
        String formattedDateString = formatter.format(anotherCurDate);
        mydate = findViewById(R.id.waktu);
        mydate.setText(formattedDateString);




        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()){
                    setdb();
                }
                else {
                    try {
                        for (int i=0;i <7;i++){
                            Relay[i] = dataSnapshot.child(Rid[i]).getValue(int.class);
                        }

                        celcius = dataSnapshot.child("temp").getValue(int.class);
                        hg = dataSnapshot.child("humadity").getValue(int.class);
                        kunci = dataSnapshot.child("kunci").getValue(int.class);
                        magnet = dataSnapshot.child("magnet").getValue(int.class);
                        temp.setText( Integer.toString(celcius) + (char) 0x00B0 );
                        humadity.setText(Integer.toString(hg));
                        pass = dataSnapshot.child("pass").getValue(int.class);

                        if (magnet == 0){
                            magnet_img.setImageResource(R.drawable.close_door);
                            magnet_text.setText("Closed");
                        }
                        else {
                            magnet_img.setImageResource(R.drawable.open_door);
                            magnet_text.setText("Open");
                        }

                        if (kunci == 1){
                            kunci_img.setImageResource(R.drawable.ic_lock_open_foreground);
                            magnet_text.setText("Unlocked");
                        }
                        else {
                            kunci_img.setImageResource(R.drawable.ic_lock_foreground);
                            kunci_text.setText("Locked");
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
        for (int i = 0; i<7; i++){
            db.child(Rid[i]).setValue(0);
        }
        db.child("temp").setValue(22);
        db.child("humadity").setValue(40);
        db.child("kunci").setValue(1);
        db.child("magnet").setValue(0);
        db.child("pass").setValue(0);
    }

    private void relayclick(){
        if (Relay[swipe] == 1){
            db.child(Rid[swipe]).setValue(0);
        }
        else {
            db.child(Rid[swipe]).setValue(1);
        }
    }

    private void kunci(){
        if (kunci == 1) {
            db.child("kunci").setValue(0);
        }
        else {
            db.child("kunci").setValue(1);
        }
    }

    private BiometricPrompt.PromptInfo buildBiometricPrompt() {
        return new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Buka Pintu")
                .setSubtitle("Biometric Mode")
                .setNegativeButtonText("Batal")
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
            kunci();
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
                    Intent intent = new Intent(MainActivity.this, KeyPassActivity.class);
                    startActivity(intent);
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

    public String myTimes(int i){
        String x = "Morning";

        if ((i > 3) && (i < 13)){
            x = "Morning";
        }else if ((i > 12) && (i < 18)){
            x = "Afternoon";
        }else if ((i > 17) && (i < 21)){
            x = "Evening";
        }else {
            x = "Night";
        }
        return x;
    };

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }


}
