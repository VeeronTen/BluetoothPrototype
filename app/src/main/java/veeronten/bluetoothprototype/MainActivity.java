package veeronten.bluetoothprototype;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import veeronten.bluetoothprototype.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements WifiP2pManager.ConnectionInfoListener {

    private ActivityMainBinding binding;

    private final IntentFilter intentFilter = new IntentFilter();
    WifiP2pManager.Channel mChannel;
    WifiP2pManager mManager;
    MyReceiver receiver;
    private List<WifiP2pDevice> peers = new ArrayList<>();
    final HashMap<String, String> buddies = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.nameTv.setText("jojo");

        binding.makeDiscoverableBtn.setOnClickListener((v)->makeDiscoverableMyself());
        binding.discoverBtn.setOnClickListener((v)->discover());
        binding.connectBtn.setOnClickListener((v)->connect());
        binding.waitBtn.setOnClickListener((v)->waitConnection());

        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peerList) {
                Collection<WifiP2pDevice> refreshedPeers = peerList.getDeviceList();
                if (!refreshedPeers.equals(peers)) {
                    ;
//                    peers.clear();
//                    peers.addAll(refreshedPeers);
//
//                    // If an AdapterView is backed by this data, notify it
//                    // of the change.  For instance, if you have a ListView of
//                    // available peers, trigger an update.
//
//                    // Perform any other updates needed based on the new list of
//                    // peers connected to the Wi-Fi P2P network.
//                    Log.d("VT", "got new devices");
                }
                if (peers.size() == 0) {
                    Log.d("VT", "No devices found");
                    return;
                }
            }
        };
        receiver = new MyReceiver(mManager, mChannel, this/*, peerListListener*/);
        registerReceiver(receiver, intentFilter);
    }

    private void makeDiscoverableMyself(){
        //  Create a string map containing information about your service.
        Map<String, String> record = new HashMap();
        record.put("listenport", String.valueOf(8866));
        record.put("buddyname", "John Doe");// + (int) (Math.random() * 1000)

        // Service information.  Pass it an instance name, service type
        // _protocol._transportlayer , and the map containing
        // information other devices will want once they connect to this one.
        WifiP2pDnsSdServiceInfo serviceInfo =
                WifiP2pDnsSdServiceInfo.newInstance("_test"+binding.connectToEt.getText(), "_presence._tcp", record);
        // Add the local service, sending the service info, network channel,
        // and listener that will be used to indicate success or failure of
        // the request.
        mManager.addLocalService(mChannel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("VT", "addLocalService onSuccess ");
                // Command successful! Code isn't necessarily needed here,
                // Unless you want to update the UI or add logging statements.
            }

            @Override
            public void onFailure(int arg0) {
                Log.d("VT", "addLocalService onFailure ");
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
            }
        });
    }
    private void discover(){
//        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
//            @Override
//            public void onSuccess() {
//                Log.d("VT", "discover initiation onSuccess ");
//                // Code for when the discovery initiation is successful goes here.
//                // No services have actually been discovered yet, so this method
//                // can often be left blank.  Code for peer discovery goes in the
//                // onReceive method, detailed below.
//            }
//            @Override
//            public void onFailure(int reasonCode) {
//                Log.d("VT", "discover onFailure "+reasonCode);
//                // Code for when the discovery initiation fails goes here.
//                // Alert the user that something went wrong.
//            }
//        });

        //services
        WifiP2pManager.DnsSdTxtRecordListener txtListener = new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
        /* Callback includes:
         * fullDomain: full domain name: e.g "printer._ipp._tcp.local."
         * record: TXT record dta as a map of key/value pairs.
         * device: The device running the advertised service.
         */

            public void onDnsSdTxtRecordAvailable(String fullDomain, Map<String, String> record, WifiP2pDevice device) {
                Log.d("VT", "DnsSdTxtRecord available |"+fullDomain +"|"+ record.toString());
                buddies.put(device.deviceAddress, record.get("buddyname"));
            }
        };

        WifiP2pManager.DnsSdServiceResponseListener servListener = new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String registrationType,
                                                WifiP2pDevice resourceType) {
                peers.clear();
                peers.add(resourceType);
                Log.d("VT", "onBonjourServiceAvailable " + instanceName);
            }
        };

        mManager.setDnsSdResponseListeners(mChannel, servListener, txtListener);

        WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        mManager.addServiceRequest(mChannel,
                serviceRequest,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d("VT", "ActionListener  onSuccess");
                    }

                    @Override
                    public void onFailure(int code) {
                        Log.d("VT", "ActionListener  onFailure" + code);
                        // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                    }
                });
        mManager.discoverServices(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d("VT", "mManager.discoverServices  onSuccess");
            }

            @Override
            public void onFailure(int code) {
                Log.d("VT", "mManager.discoverServices  onFailure");
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                if (code == WifiP2pManager.P2P_UNSUPPORTED)
                    Log.d("VT", "P2P isn't supported on this device.");
                else
                    Log.d("VT", "P2P GOOD FOR YA.");
            }
        });
    }

    private void connect(){
        // Picking the first device found on the network.
        WifiP2pDevice device = peers.get(0);
        WifiP2pConfig config = new WifiP2pConfig();
        //config.groupOwnerIntent = 0;
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(getApplicationContext(), "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void waitConnection(){
        new ServerWaiter().start();
        //binding.waitBtn.setEnabled(false);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        if(binding.connectToEt.getText().toString().equals("send")) {

            new WriterThread(getApplicationContext(), wifiP2pInfo.groupOwnerAddress).start();
        }
        // InetAddress from WifiP2pInfo struct.
        //InetAddress

           String     groupOwnerAddress = wifiP2pInfo.groupOwnerAddress.getHostAddress();
        //TODO ОТКЛЮЧИТЬСЯ ОТ ВАЙФАЯ посмотреть овнера на 3 устройствах
        Log.d("VT", "OWNER "+groupOwnerAddress);
        // After the group negotiation, we can determine the group owner
        // (server).
        if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
            // Do whatever tasks are specific to the group owner.
            // One common case is creating a group owner thread and accepting
            // incoming connections.
        } else if (wifiP2pInfo.groupFormed) {
            // The other device acts as the peer (client). In this case,
            // you'll want to create a peer thread that connects
            // to the group owner.
        }
    }
}
