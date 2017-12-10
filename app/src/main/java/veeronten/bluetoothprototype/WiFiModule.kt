package veeronten.bluetoothprototype

import android.content.Context
import android.net.wifi.p2p.WifiP2pManager

object WiFiModule {
    var manager: WifiP2pManager
        private set
    var channel: WifiP2pManager.Channel
        private set

    init{
        val c = AppContextHolder.getContext()
        manager = c.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(c, c.getMainLooper(), null)
    }
}