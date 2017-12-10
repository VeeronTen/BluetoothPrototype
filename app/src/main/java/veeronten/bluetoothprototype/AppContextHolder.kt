package veeronten.bluetoothprototype

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object AppContextHolder{

    private var context: Context? = null

    fun init(context: Context){
        if(this.context==null){
            this.context = context
        }
    }

    fun getContext(): Context{
        return context ?: throw NullPointerException("application context was not gotten by AppContextHolder.init()")
    }
}