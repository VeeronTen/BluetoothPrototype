package veeronten.bluetoothprototype;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.net.wifi.p2p.WifiP2pInfo;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class WriterThread extends Thread {
    private final Socket mmSocket = new Socket();
    private Context context;
    private InetAddress address;

    public WriterThread(Context context, InetAddress address) {
        this.context = context;
        this.address = address;
    }

    public void run() {
        int size=0;

        try {
            byte[] buffer = new byte[1024];
            InputStream is = context.getAssets().open("2.3mbmin 1min");

            Log.d("VT", "connecting...");

            try {
                mmSocket.bind(null);
            } catch (IOException e) {
                Log.d("VT", "bind fuck...");
                return;
            }
            try {
                mmSocket.connect(new InetSocketAddress(address, 8866),5000);
            } catch (IOException e) {
                Log.d("VT", "connect fuck...", e);
                return;
            }
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
