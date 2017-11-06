package veeronten.bluetoothprototype;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class WriterThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final BluetoothAdapter adapter;
    private Context context;

    public WriterThread(BluetoothDevice device, BluetoothAdapter adapter, Context context) {
        this.context = context;
        BluetoothSocket tmp = null;
        mmDevice = device;
        this.adapter = adapter;

        try {
            tmp = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString("fdfc9e6d-de86-46c0-805b-e539acbf3693"));
        } catch (IOException e) {
            Log.d("VT", "Socket's create() method failed", e);
        }
        mmSocket = tmp;
        Log.d("VT", "connect to "+mmDevice.getName()+"...");
    }

    public void run() {
        adapter.cancelDiscovery();
        int size=0;

        try {
            byte[] buffer = new byte[1024];
            InputStream is = context.getAssets().open("2.3mbmin 1min");

            Log.d("VT", "connecting...");
            mmSocket.connect();
            Log.d("VT", "connected");

            Log.d("VT", "start sending");
            int i;
            Date date  = new Date();
            while((i=is.read(buffer))!=-1){
                mmSocket.getOutputStream().write(buffer, 0, i);
                size+=i;
            }
            Log.d("VT", "was sent, time: "+ (new Date().getTime()-date.getTime())/1000);

            mmSocket.getOutputStream().flush();
            mmSocket.getOutputStream().close();
            mmSocket.close();
            Log.d("VT", "was closed");
        } catch (IOException connectException) {
            try {
                Log.d("VT", "close with EXCEPTION...", connectException);
                mmSocket.close();
            } catch (IOException closeException) {
                Log.d("VT", "Could not close the client socket", closeException);
            }
        }
        Log.d("VT", "size (bytes)= "+size);
    }
}
