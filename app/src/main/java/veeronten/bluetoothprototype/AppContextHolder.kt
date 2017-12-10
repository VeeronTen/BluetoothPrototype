package veeronten.bluetoothprototype

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object AppContextHolder{

    var context: Context? = null
        private set

    fun init(context: Context){
        if(this.context==null){
            this.context = context
        }
    }
}