package veeronten.bluetoothprototype;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class ServerWaiter extends Thread {
    private BluetoothServerSocket server;
    private BluetoothSocket newConnection;
    private BluetoothAdapter adapter;
    public ServerWaiter(BluetoothAdapter adapter) {
        this.adapter = adapter;
    }

    public void run() {
        Log.d("VT", "server is online");
        while (true) {
            try {
                BluetoothServerSocket tmp = null;
                try {
                    tmp = adapter.listenUsingInsecureRfcommWithServiceRecord("NAME", UUID.fromString("fdfc9e6d-de86-46c0-805b-e539acbf3693"));
                } catch (IOException e) {
                    Log.d("VT", "Socket's listen() method failed", e);
                }
                server = tmp;
                Log.d("VT", "waiting...");
                newConnection = server.accept();
                Log.d("VT", "starting...");
                new ServerReader(newConnection).start();
                server.close();
            } catch (IOException e) {
                Log.d("VT", "Socket's accept() method failed", e);
                break;
            }
        }
    }
}
