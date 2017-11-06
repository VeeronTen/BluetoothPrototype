package veeronten.bluetoothprototype;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import java.util.ArrayList;
import java.util.Iterator;

import veeronten.bluetoothprototype.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private BroadcastReceiver mReceiver;
    private BluetoothAdapter adapter;
    private ArrayList<BluetoothDevice> devices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        devices = new ArrayList<>();
        adapter = BluetoothAdapter.getDefaultAdapter();
        if(adapter==null){
            Toast.makeText(getApplicationContext(), "your phone does not support BT", Toast.LENGTH_SHORT).show();
        }
        Log.d("VT", "main");

        binding.nameTv.setText(adapter.getName()+" ("+adapter.getAddress()+")");

        if(adapter.isEnabled()){
            binding.btIsEnabledTb.setChecked(true);
            unlockUI();
        }else{
            binding.btIsEnabledTb.setChecked(false);
            lockUI();
        }

        binding.btIsEnabledTb.setOnCheckedChangeListener((v, isChecked)-> {
            if(isChecked){
                adapter.enable();
                unlockUI();
            }else{
                adapter.disable();
                lockUI();
            }
        });


        binding.makeDiscoverableBtn.setOnClickListener((v)->makeDiscoverableMyself());
        binding.discoverBtn.setOnClickListener((v)->discover());
        binding.connectBtn.setOnClickListener((v)->connect());
        binding.waitBtn.setOnClickListener((v)->waitConnection());
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
        devices.clear();
        if(mReceiver!=null){
            stopDiscover();
        }
        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    devices.add(device);
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress();
                    Log.d("VT", deviceName+" ("+deviceHardwareAddress+")");

                    Toast.makeText(getApplicationContext(), deviceName+" "+deviceHardwareAddress, Toast.LENGTH_SHORT).show();
                }
            }
        };

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        Log.d("VT", "Bluetooth searching was started");
        registerReceiver(mReceiver, filter);
        adapter.startDiscovery();
    }
    private void stopDiscover(){
        unregisterReceiver(mReceiver);
        adapter.cancelDiscovery();
    }

    private void connect(){
        String nameToConnect = binding.connectToEt.getText().toString();
        Iterator<BluetoothDevice> iter = devices.iterator();
        BluetoothDevice d;
        while(iter.hasNext()){
            d = iter.next();
            if(d.getName()!=null&&d.getName().equals(nameToConnect)){
                Log.d("VT", "has device");
                new WriterThread(d, adapter, getApplicationContext()).start();
                break;
            }
        }
    }

    private void waitConnection(){
        new ServerWaiter(adapter).start();
        //binding.waitBtn.setEnabled(false);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopDiscover();
    }

    private void lockUI(){
        binding.makeDiscoverableBtn.setEnabled(false);
        binding.discoverBtn.setEnabled(false);
    }
    private void unlockUI(){
        binding.makeDiscoverableBtn.setEnabled(true);
        binding.discoverBtn.setEnabled(true);
    }
}
