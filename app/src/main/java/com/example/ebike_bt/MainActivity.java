package com.example.ebike_bt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    ToggleButton gearSelector;
    ImageButton lightSelector, powerSelector , ThrottleSelector;
    int state_throttle, state_light=0, state_power;

    //toolbar
    Toolbar myToolbar;
    MenuItem menuThemeButton;
    int nightMode;

    //handler
    Handler handler;

    //bluetooth
    byte[] bytes = new byte[0];
    private BluetoothAdapter bluetoothadapter;
    private String mConnectedDeviceName;
    BluetoothDevice device;
    MyBluetoothService service;
    MyBluetoothService.ConnectThread connect;

    DataBt bikeInfo;
    SlopeSensor slope;
    GpsLocation gpsact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        findViews();

        bluetoothadapter =BluetoothAdapter.getDefaultAdapter();

        setSupportActionBar(myToolbar);

        theHandler();

        service = new MyBluetoothService(handler);

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_UNSPECIFIED){  //if unspecified light mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        
        buttons();

        slope= new SlopeSensor(MainActivity.this);
        
        slope.prueba(this);

        bikeInfo = new DataBt(MainActivity.this);

        gpsact = new GpsLocation(MainActivity.this);

        requestPermissions();
    }

    public void requestPermissions(){

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.PERMISSION_FINE_CODE);
        }

        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 5, gpsact);// 5 metres min distance to refresh
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == Constants.PERMISSION_FINE_CODE)
        {
            //Do something based on grantResults
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "fine location permission granted", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "fine location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void enableButtons(boolean e) {
        gearSelector.setEnabled(e);
        powerSelector.setEnabled(e);
        lightSelector.setEnabled(e);
        ThrottleSelector.setEnabled(e);
    }

    private void buttons() {

        lightSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state_light++;
                switch(state_light) {
                    case 0 :
                        lightSelector.setImageResource(R.drawable.boton_lights_off);
                        sendData("l");;
                        break;

                    case 1 :
                        lightSelector.setImageResource(R.drawable.boton_pos_on);
                        sendData("k");
                        break;

                    case 2 :
                        lightSelector.setImageResource(R.drawable.boton_cruce);
                        sendData("j");
                        break;

                    default:
                        state_light=0;
                        lightSelector.setImageResource(R.drawable.boton_lights_off);
                        sendData("l");
                }
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////GEAR
        gearSelector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sendData("d");
                } else {
                    sendData("r");
                }
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////THROTTLE
        ThrottleSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state_throttle++;
                switch(state_throttle)
                {
                    case 0 :
                        sendData("i");//pass off th off
                        ThrottleSelector.setImageResource(R.drawable.button_pedals);
                        break;
                    case 1 :
                        ThrottleSelector.setImageResource(R.drawable.botton_pas);
                        sendData("o");//pas on th of
                        break;
                    case 2 :
                        ThrottleSelector.setImageResource(R.drawable.botton_throttle);
                        sendData("p");//thr on pas of

                        break;

                    default:
                        state_throttle=0;
                        sendData("i");//pass off th off
                        ThrottleSelector.setImageResource(R.drawable.button_pedals);
                }
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////POWER
        powerSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state_power++;
                switch(state_power)
                {
                    case 0 :
                        powerSelector.setImageResource(R.drawable.botton_80);

                        break;
                    case 1 :
                        powerSelector.setImageResource(R.drawable.botton_100);

                        break;
                    case 2 :
                        powerSelector.setImageResource(R.drawable.botton_120);

                        break;

                    default:
                        state_power=0;
                        powerSelector.setImageResource(R.drawable.botton_80);
                }
            }
        });
    }

    private void sendData(String data){
        bytes = data.getBytes();
        connect.sendToDevice(bytes);
    }

    private void findViews() {

        myToolbar=findViewById(R.id.id_tool_bar);

        //buttons
        gearSelector =findViewById(R.id.id_toggle_gear);  //toggle button gear
        lightSelector =findViewById(R.id.id_light_button);
        ThrottleSelector=findViewById(R.id.id_throttle);
        powerSelector=findViewById(R.id.id_power_selector);

    }

    @SuppressLint("HandlerLeak")                                                                    //HANDLER
    private void theHandler() {

        handler = new Handler() {
            public void handleMessage(@NonNull android.os.Message msg) {
                switch (msg.what) {
                    case Constants.MESSAGE_STATE_CHANGE:
                        switch (msg.arg1) {
                            case MyBluetoothService.STATE_CONNECTED:
                                setStatus(R.string.title_connected);
                                break;
                            case MyBluetoothService.STATE_NONE:
                               setStatus(R.string.title_not_connected);
                                break;
                        }
                        break;

                    case Constants.MESSAGE_READ:

                        byte[] readBuf = (byte[]) msg.obj;

                        bikeInfo.splitBtInfo(readBuf);

                        break;

                    case Constants.MESSAGE_DEVICE_NAME:
                        mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);      // save the connected device's name
                        Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
    }


    @Override                                                                                       //RESULT CODES
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.BT_DEVICES_ACTIVITY_REQUEST_CODE) {//TURN ON BLUETOOTH
            if (resultCode == RESULT_OK) {

                // Get String data from Intent
                String address = data.getStringExtra(BtDevices.EXTRA_DEVICE_ADDRESS);
                device = bluetoothadapter.getRemoteDevice(address);
                connect= service.new ConnectThread(device);

            }
        }

    }
                                                                                                    //SET BLUETOOTH STATUS
    private void setStatus(int status) {

        myToolbar.setSubtitle(status);

        if(status==R.string.title_connected) {

            myToolbar.setTitle(mConnectedDeviceName);
        }

        if(status==R.string.title_not_connected) {

            myToolbar.setTitle("not connected");
        }

    }

    @Override                                                                                       //MENU CREATION
    public boolean onCreateOptionsMenu(Menu mMenu) {

        getMenuInflater().inflate(R.menu.app_menu, mMenu);
        menuThemeButton = mMenu.findItem(R.id.menu_theme_button);

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO){           //change theme icon
            menuThemeButton.setIcon(R.drawable.ic_moon);
        }
        else   menuThemeButton.setIcon(R.drawable.ic_sun);

        return true;
    }

    @Override                                                                                       //MENU ITEMS SELECT
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        int id = menuItem.getItemId();

        if (id == R.id.menu_config) {

             Intent intent = new Intent(this, AppSettings.class);
             startActivityForResult(intent, Constants.CONFIG_ACTIVITY_REQUEST_CODE);                //start config activity

            return true;
        }

        if (id == R.id.menu_bt) {

                if(connect!=null) connect.cancelConnection();                                                 //disconnects from device if connected

                Intent intent = new Intent(this, BtDevices.class);
                startActivityForResult(intent, Constants.BT_DEVICES_ACTIVITY_REQUEST_CODE);         //start bt devices activity

            return true;
        }

        if (id == R.id.menu_theme_button) {                                                         //change theme

              nightMode = AppCompatDelegate.getDefaultNightMode();

            if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {                                    //change to light mode

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            else {

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);            //change to dark mode
            }

            return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    public void onResume() {
        super.onResume();
    }



}