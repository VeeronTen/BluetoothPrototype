package veeronten.bluetoothprototype;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.Date;

public class ServerReader extends Thread {
    private BluetoothSocket connection;

    ServerReader(BluetoothSocket connection){
        this.connection = connection;
    }

    @Override
    public void run() {
        Log.d("VT", "runrun");
        int size=0;
        Date date=null;
        if (connection == null){
            Log.d("VT", "connection is null");
            return;
        }
        try {
            int i;
            byte[] buffer = new byte[1024];
            Log.d("VT", "reading was started");
            date  = new Date();
            while((i=connection.getInputStream().read(buffer))!=-1){
                size+=i;
            }
            Log.d("VT", "reading has closed (got -1)");
        } catch (IOException e) {
            Log.d("VT", "reading has stopped, time: "+ (new Date().getTime()-date.getTime())/1000);
        }
        try {
            connection.close();
        } catch (IOException e) {
            Log.d("VT", "xxxxxxxxxxxxxxxxx ");
        }
        Log.d("VT", "size (bytes)= "+size);
    }
}
