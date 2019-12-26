package com.example.smartsecuritysystem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;
import com.example.smartsecuritysystem.MainActivity;
import static com.example.smartsecuritysystem.MainActivity.CekConnectionRunning;

public class ConnectivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            try
            {
                if (isOnline(context)) {
                    CekConnectionRunning(true);
                    Log.e("3S", "Online Connect Intenet ");
                    Toast.makeText(context, "Internet Terkoneksi", Toast.LENGTH_SHORT).show();
                } else {
                    CekConnectionRunning(false);
                    Log.e("3S", "Conectivity Failure !!! ");
                    Toast.makeText(context, "Koneksi Gagal, Cek Kembali Koneksi Internet anda", Toast.LENGTH_SHORT).show();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        private boolean isOnline(Context context) {
            try {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                //should check null because in airplane mode it will be null
                return (netInfo != null && netInfo.isConnected());
            } catch (NullPointerException e) {
                e.printStackTrace();
                return false;
            }
        }
}
