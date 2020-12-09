package com.example.ebike_bt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CompoundButton;

public class AppSettings extends AppCompatActivity {

    SwitchCompat bluetoothSwitchCompat, gpsSwitchCompat;

    BluetoothAdapter bluetoothAdapter;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        bluetoothSwitchCompat = findViewById(R.id.id_bluetooth_switch);
        gpsSwitchCompat = findViewById(R.id.id_gps_switch);

        checkProvidersState();

        //intent bluetooth change state
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothStateReceiver, filter);

        //intent GPS change state
        IntentFilter filter2 = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter2.addAction(Intent.ACTION_PROVIDER_CHANGED);
        getApplicationContext().registerReceiver(gpsStateReceiver, filter2);

        bluetoothSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {   //method to enable bluetooth
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    enableBluetooth();
                } else {
                    disableBluetooth();
                }
            }
        });
        gpsSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {   //method to enable bluetooth
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    enableGps();
                    checkProvidersState();
                } else {
                    disableGps();
                    checkProvidersState();
                }
            }
        });

    }


    private void checkProvidersState() {

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gpsSwitchCompat.setChecked(true);
        }
        else gpsSwitchCompat.setChecked(false);

        if (bluetoothAdapter.isEnabled()) {
            bluetoothSwitchCompat.setChecked(true);
        }
        else  bluetoothSwitchCompat.setChecked(false);
    }


    public void goBack(View v){
        finish();
    }


    public void enableGps() {
        assert locationManager != null;
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent1);
        }
    }
    public void disableGps() {
        assert locationManager != null;
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent1);
        }
    }

    public void enableBluetooth() {

        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
    }

    public void disableBluetooth() {

        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
        }
    }

    private final BroadcastReceiver bluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            assert action != null;
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        bluetoothSwitchCompat.setChecked(false);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        bluetoothSwitchCompat.setChecked(true);
                        break;
                }
            }
        }
    };

    private BroadcastReceiver gpsStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.getAction())) {

                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

                assert locationManager != null;
                boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (isGpsEnabled || isNetworkEnabled) {
                    gpsSwitchCompat.setChecked(true);
                } else {
                    gpsSwitchCompat.setChecked(false);
                }
            }
        }
    };


    @Override
    public void onPause() {
        super.onPause();
    }

}