package veeronten.bluetoothprototype

class Application : android.app.Application(){
    override fun onCreate() {
        super.onCreate()
        AppContextHolder.init(applicationContext)
    }
}