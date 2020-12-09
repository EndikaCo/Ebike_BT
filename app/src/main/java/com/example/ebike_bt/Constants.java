package com.example.ebike_bt;

public interface Constants {

    int BT_DEVICES_ACTIVITY_REQUEST_CODE =0;
    int CONFIG_ACTIVITY_REQUEST_CODE = 1;

    // Message types sent from the BluetoothChatService Handler
    int MESSAGE_STATE_CHANGE = 1;
    int MESSAGE_READ = 2;
    int MESSAGE_DEVICE_NAME = 4;
    String DEVICE_NAME = "device_name";

    int PERMISSION_FINE_CODE = 2;
}
