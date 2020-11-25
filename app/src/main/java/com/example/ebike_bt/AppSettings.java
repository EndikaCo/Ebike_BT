package com.example.ebike_bt;

import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

public class AppSettings extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);

    }

    public void enableGps(){
        Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent1);
    }
    public static void enableBluetooth() {                                                           //method to enable bluetooth
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
    }

    public void goBack(View v){
        finish();
    }

}