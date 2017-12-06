package veeronten.bluetoothprototype;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.util.Collection;

public class MyReceiver extends BroadcastReceiver{

    WifiP2pManager.ConnectionInfoListener infoListener;
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
     WifiP2pManager.PeerListListener pl;
    public MyReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, WifiP2pManager.ConnectionInfoListener infoListener/*, WifiP2pManager.PeerListListener pl*/){
        this.infoListener = infoListener;
        this.manager = manager;
        this. channel = channel;
        this.pl = pl;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Log.d("VT", "P2P enabled");
                ;//activity.setIsWifiP2pEnabled(true);
            } else {
                Log.d("VT", "P2P disabled");
                manager.clearLocalServices(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d("VT", "P2P clear ok");
                    }

                    @Override
                    public void onFailure(int i) {
                        Log.d("VT", "P2P clear cock");
                    }
                });
                ;//activity.setIsWifiP2pEnabled(false);
            }
        }






        else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
//            Log.d("VT", "P2P peers changed");
            //we don't need peers info
//            if (manager != null) {
//                manager.requestPeers(channel, pl);
//            }
        }






        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            // Connection state changed!  We should probably do something about
            // that.
            Log.d("VT", "P2P connection state changed");
            if (manager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {

                // We are connected with the other device, request connection
                // info to find group owner IP

                manager.requestConnectionInfo(channel, infoListener);
            }
        }




        else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            Log.d("VT", "i was changed");
        }
    }


}
