package veeronten.bluetoothprototype

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Parcelable
import java.lang.IllegalStateException

class WifiReceiver private constructor(private val infoListener: WifiP2pManager.ConnectionInfoListener) : BroadcastReceiver() {
    private val wifiManager = WiFiModule.manager
    private val wifiChannel = WiFiModule.channel

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        when(action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    Logger.i(javaClass, "new state: Enabled")
                }else{
                    Logger.i(javaClass, "new state: Disabled")
                }
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {}
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                Logger.i(javaClass, "connection state changed")
                val networkInfo = intent.getParcelableExtra<Parcelable>(WifiP2pManager.EXTRA_NETWORK_INFO) as NetworkInfo
                if (networkInfo.isConnected) {
                    wifiManager.requestConnectionInfo(wifiChannel, infoListener)
                }
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                Logger.i(javaClass, "this device was changed")
            }
        }
    }

    companion object Registrar{
        private var receiver: BroadcastReceiver? = null

        fun register(infoListener: WifiP2pManager.ConnectionInfoListener){
            if(receiver!=null) throw IllegalStateException("receiver is already registered")

            receiver = WifiReceiver(infoListener)
            val context = AppContextHolder.getContext()
            val intentFilter = IntentFilter()
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)

            context.registerReceiver(receiver, intentFilter)
        }
        fun unregister(){
            if(receiver==null) throw IllegalStateException("receiver is not registered")

            val context = AppContextHolder.getContext()
            context.unregisterReceiver(receiver)
            receiver = null
        }
    }
}
