package veeronten.bluetoothprototype;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;

import veeronten.bluetoothprototype.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private BroadcastReceiver mReceiver;
    private BluetoothA2dp audio;
    private BluetoothAdapter adapter;
    BluetoothHeadset mBluetoothHeadset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        adapter = BluetoothAdapter.getDefaultAdapter();
        if(adapter==null){
            Toast.makeText(getApplicationContext(), "your phone does not support BT", Toast.LENGTH_SHORT).show();
        }

        binding.nameTv.setText(adapter.getName()+"_"+adapter.getAddress());

        binding.btIsEnabledTb.setChecked(adapter.isEnabled());
        binding.btIsEnabledTb.setOnCheckedChangeListener((v, isChecked)-> {
            if(isChecked){
                adapter.enable();
            }else{
                adapter.disable();
            }
        });

        binding.makeDiscoverableBtn.setOnClickListener((v)->makeDiscoverableMyself());
        binding.discoverBtn.setOnClickListener((v)->discover());





// Get the default adapter
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        private BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                if (profile == BluetoothProfile.HEADSET) {
                    mBluetoothHeadset = (BluetoothHeadset) proxy;
                }
            }
            public void onServiceDisconnected(int profile) {
                if (profile == BluetoothProfile.HEADSET) {
                    mBluetoothHeadset = null;
                }
            }
        };

// Establish connection to the proxy.
        mBluetoothAdapter.getProfileProxy(getApplicationContext(), mProfileListener, BluetoothProfile.HEADSET);

        mBluetoothHeadset.
// ... call functions on mBluetoothHeadset

// Close proxy connection after use.
        mBluetoothAdapter.closeProfileProxy(mBluetoothHeadset);

    }

    private void audio(){
        BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                if (profile == BluetoothProfile.A2DP) {
                    audio = (BluetoothA2dp) proxy;
                }
            }
            public void onServiceDisconnected(int profile) {
                if (profile == BluetoothProfile.A2DP) {
                    audio = null;
                }
            }
        };

        adapter.getProfileProxy(getApplicationContext(), mProfileListener, BluetoothProfile.A2DP);

        adapter.closeProfileProxy(BluetoothProfile.A2DP, audio);
    }
    private void makeDiscoverableMyself(){
        Method method;
        try {
            method = adapter.getClass().getMethod("setScanMode", int.class, int.class);
            method.invoke(adapter,BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE,120);
            Toast.makeText(getApplicationContext(), "you are discoverable", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
        }
    }
    private void discover(){
        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress();
                    Toast.makeText(getApplicationContext(), deviceName+" "+deviceHardwareAddress, Toast.LENGTH_SHORT).show();
                }
            }
        };

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        Log.d("VT", "Bluetooth searching was started");
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
