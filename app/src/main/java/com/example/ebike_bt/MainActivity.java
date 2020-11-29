package com.example.ebike_bt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static java.lang.Float.parseFloat;

public class MainActivity extends AppCompatActivity {

    //Views
    TextView tvVoltageBattery, tvPercentBattery, tvWatts, tvAmperes;
    ProgressBar  batteryProgressBar;

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
    }

    private void findViews() {

        myToolbar=findViewById(R.id.id_tool_bar);
        tvVoltageBattery =findViewById(R.id.id_text_volts);
        tvAmperes=findViewById(R.id.id_amperes);
        tvWatts=findViewById(R.id.id_watts);
        batteryProgressBar=findViewById(R.id.id_battery_bar);
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
                        //dataBt(readBuf);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {                 //TURN ON BLUETOOTH
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.BT_DEVICES_ACTIVITY_REQUEST_CODE) {
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


}