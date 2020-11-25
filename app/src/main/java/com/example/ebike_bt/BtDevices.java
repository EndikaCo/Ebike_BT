package com.example.ebike_bt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Set;

public class BtDevices extends AppCompatActivity {

    private BluetoothAdapter bluetoothadapter;
    ListView listPairedDevices;
    SwitchCompat autoConnectSwitch;

    private ArrayList<String> arrayPairedNames = new ArrayList<>();
    private ArrayList<BluetoothDevice> arrayDevices = new ArrayList<>();

    private BluetoothDevice device;

    public static String EXTRA_DEVICE_ADDRESS = "device_address"; // EXTRA string send to mainActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_devices);

        bluetoothadapter = BluetoothAdapter.getDefaultAdapter();
        listPairedDevices = findViewById(R.id.id_paired_devices);
        autoConnectSwitch = findViewById(R.id.id_autoconnect_switch);

        getPairedDevices();
                                                                                                    //click to connect
        listPairedDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                device = arrayDevices.get(position);
                String address = device.getAddress();
                Intent intent = new Intent();
                intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
                setResult(RESULT_OK, intent);
                finish();

            }
        }
        );

        autoConnectSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {   //method to enable bluetooth
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    // guardar device en shared pref

                } else {

                }
            }
        });

    }
                                                                                                    //paired device add to list
    private  void getPairedDevices(){
        Set<BluetoothDevice> btBondedDevices= bluetoothadapter.getBondedDevices();
        if (btBondedDevices.size() >0){

            for (BluetoothDevice btDevice :btBondedDevices){
                arrayDevices.add(btDevice);
                arrayPairedNames.add(btDevice.getName() + "\n" +  btDevice.getAddress() );
                listPairedDevices.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayPairedNames));
            }
        }
    }

    public void goBack(View v){
finish();
    }

}