package veeronten.bluetoothprototype

import android.util.Log

object Logger{
    val TAG = "VT"

    fun v(msg: String?){
        Log.v(TAG, msg)
    }
    fun d(msg: String?){
        Log.d(TAG, msg)
    }
    fun i(msg: String?){
        Log.i(TAG, msg)
    }
    fun w(msg: String?){
        Log.w(TAG, msg)
    }
    fun e(msg: String?){
        Log.e(TAG, msg)
    }
}