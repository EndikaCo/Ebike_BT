package com.example.ebike_bt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MyBluetoothService {
    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    private int mState;

    private static final String TAG = "MY_APP_DEBUG_TAG";
    public final Handler handler; // handler that gets info from Bluetooth service
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public MyBluetoothService( Handler mHandler) {

        handler=mHandler;
        mState = STATE_NONE;

    }

    ConnectedThread connected;

    private synchronized void setState(int state) {                                                 //setter del estado
        mState = state;
        handler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, mState, -1).sendToTarget();     // send state to UI
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////DEVICE CONNECTION
    public class ConnectThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        public  ConnectThread(BluetoothDevice device) {

            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e("Error", "Socket's create() method failed", e);
            }
            mmSocket = tmp;
            start();
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            }
            catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                    setState(STATE_NONE);
                } catch (IOException closeException) {
                    Log.e("TAG", "Could not close the client socket", closeException);
                }
                return;
            }

            // the connection in a separate thread.
            manageMyConnectedSocket(mmSocket);

        }

        private synchronized void manageMyConnectedSocket(BluetoothSocket mmSocket) {

            connected = new ConnectedThread(mmSocket);
            connected.start();

            // Send the name of the connected device back to the UI Activity
            Message msg = handler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
            Bundle bundle = new Bundle();
            bundle.putString(Constants.DEVICE_NAME, mmDevice.getName());
            msg.setData(bundle);
            handler.sendMessage(msg);
            setState(STATE_CONNECTED);
        }

        public void sendToDevice(byte[] data) {
            connected.write(data);
        }

        public void cancelConnection() {
            try {
                mmSocket.close();
                setState(STATE_NONE);
            } catch (IOException e) {
                Log.e("TAG", "Could not close the client socket", e);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////CONNECTED//////////////////////////////////////
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {

                    numBytes = mmInStream.read(mmBuffer);         //read bytes from input buffer

                    // Send the obtained bytes to the UI Activity via handler
                    Message readMsg =  handler.obtainMessage(Constants.MESSAGE_READ, numBytes, -1, mmBuffer);
                    readMsg.sendToTarget();

                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
                // Share the sent message with the UI activity.
                //Message writtenMsg = handler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, bytes);
                // writtenMsg.sendToTarget();

            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);
                // Send a failure message back to the activity.
                // Message writeErrorMsg = handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                // Bundle bundle = new Bundle();
                // bundle.putString("toast","Couldn't send data to the other device");
                // writeErrorMsg.setData(bundle);
                // handler.sendMessage(writeErrorMsg);
            }
        }

    }
}