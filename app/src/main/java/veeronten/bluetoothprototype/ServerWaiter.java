package veeronten.bluetoothprototype;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

public class ServerWaiter extends Thread {
    private ServerSocket server;
    private Socket newConnection;

    public ServerWaiter() {
        try {
            server = new ServerSocket(8866);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        Log.d("VT", "server is online");
        while (true) {
            try {
                Log.d("VT", "ServerWaiter: waiting...");
                newConnection = server.accept();
                Log.d("VT", "ServerWaiter: starting...");
                new ServerReader(newConnection).start();
                //server.close();
            } catch (IOException e) {
                Log.d("VT", "Socket's accept() method failed", e);
                break;
            }
        }
    }
}
