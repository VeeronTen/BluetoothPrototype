package veeronten.bluetoothprototype;

import android.content.Context;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import veeronten.bluetoothprototype.databinding.ActivityControlPanelBinding;

public class ControlPanelActivity extends AppCompatActivity implements WifiP2pManager.ConnectionInfoListener{

    private ActivityControlPanelBinding b;

    private InetAddress mEmmiterAddress = null;

    private List<Pair<String, WifiP2pDevice>> mPeers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_control_panel);

        mPeers = new LinkedList<>();

        WifiReceiver.Registrar.register(this);
        b.createBtn.setOnClickListener((v)->{
            createService();
            b.ServiceNameEt.setEnabled(false);
            b.createBtn.setEnabled(false);
        });
        b.discoverBtn.setOnClickListener( v -> discover());
        b.listenBtn.setOnClickListener(v -> {
            listen();
            b.toListenEt.setEnabled(false);
        });

        b.getBtn.setEnabled(false);
        b.getBtn.setOnClickListener(v -> new WriterThread(getApplicationContext(), mEmmiterAddress).start());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WifiReceiver.Registrar.unregister();
    }

    private void createService(){
        String name = b.ServiceNameEt.getText().toString();

        Map<String, String> record = new HashMap();
        record.put("name", name);

        WifiP2pDnsSdServiceInfo serviceInfo =
                WifiP2pDnsSdServiceInfo.newInstance(name+"_radio", "_presence._tcp", record);

        WiFiModule.INSTANCE.getManager().addLocalService(WiFiModule.INSTANCE.getChannel(), serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("VT", "service was created ");
            }
            @Override
            public void onFailure(int arg0) {
                Log.e("VT", "can't create service!");
            }
        });
        new ServerWaiter().start();
    }

    private void discover(){
        WifiP2pManager.DnsSdTxtRecordListener txtListener = (String fullDomain, Map<String, String> record, WifiP2pDevice device)->{
            String name = record.get("name");
            Boolean newPeer = true;
            for(int i = 0; i< mPeers.size(); i++){
                if(mPeers.get(i).first.equals(name)){
                    newPeer = false;
                    break;
                }
            }
            if(newPeer){
                b.serviciesTv.append(name);
                mPeers.add(Pair.create(name, device));
            }
        };
        WifiP2pManager.DnsSdServiceResponseListener servListener = (String instanceName, String registrationType,
                WifiP2pDevice resourceType) ->{};
        WiFiModule.INSTANCE.getManager().setDnsSdResponseListeners(WiFiModule.INSTANCE.getChannel(), servListener, txtListener);

        WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        WiFiModule.INSTANCE.getManager().addServiceRequest(WiFiModule.INSTANCE.getChannel(),
                serviceRequest,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {}
                    @Override
                    public void onFailure(int code) {
                        Log.e("VT", "cant add request service" + code);
                    }
                });
        WiFiModule.INSTANCE.getManager().discoverServices(WiFiModule.INSTANCE.getChannel(), new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {}
            @Override
            public void onFailure(int code) {
                Log.e("VT", "cant't discover services");
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                if (code == WifiP2pManager.P2P_UNSUPPORTED)
                    Log.e("VT", "P2P isn't supported on this device.");
                else
                    Log.e("VT", "P2P GOOD FOR YA.");
            }
        });
    }
    private void listen(){
        // Picking the first device found on the network.
        WifiP2pDevice peerToListen = null;
        String toListen = b.toListenEt.getText().toString();
        for(int i = 0; i<mPeers.size(); i++){
            if(mPeers.get(i).first.equals(toListen)){
                peerToListen = mPeers.get(i).second;
                break;
            }
        }

        WifiP2pConfig config = new WifiP2pConfig();
        config.groupOwnerIntent = 0;
        config.deviceAddress = peerToListen.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        WiFiModule.INSTANCE.getManager().connect(WiFiModule.INSTANCE.getChannel(), config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {}
            @Override
            public void onFailure(int reason) {
                Log.e("VT", "Can't connect");
            }
        });
    }
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        mEmmiterAddress = wifiP2pInfo.groupOwnerAddress;
        b.getBtn.setEnabled(true);
    }
}
