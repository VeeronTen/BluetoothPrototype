package veeronten.bluetoothprototype

import android.util.Log

object Logger{
    val TAG = "VT"

    fun v(msg: String?){
        Log.v(TAG, msg)
    }
    fun v(speaker: Any, msg: String?){
        Log.v(TAG, msg(speaker, msg))
    }

    fun d(msg: String?){
        Log.d(TAG, msg)
    }
    fun d(speaker: Any, msg: String?){
        Log.d(TAG, msg(speaker, msg))
    }

    fun i(msg: String?){
        Log.i(TAG, msg)
    }
    fun i(speaker: Any, msg: String?){
        Log.i(TAG, msg(speaker, msg))
    }

    fun w(msg: String?){
        Log.w(TAG, msg)
    }
    fun w(speaker: Any, msg: String?){
        Log.w(TAG, msg(speaker, msg))
    }

    fun e(msg: String?){
        Log.e(TAG, msg)
    }
    fun e(speaker: Any, msg: String?){
        Log.e(TAG, msg(speaker, msg))
    }

    private fun msg(speaker: Any, msg: String?): String{
        return "${nameOf(speaker)}: $msg"
    }

    private fun nameOf(speaker: Any): String{
        return speaker.javaClass.kotlin.simpleName ?: "noname"
    }
}