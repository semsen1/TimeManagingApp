package com.example.clockapp.alarm

import android.util.Log

object AlarmDebug {

    const val TAG = "AlarmDebug"

    fun d(message: String) {
        Log.d(TAG, message)
    }

    fun w(message: String) {
        Log.w(TAG, message)
    }

    fun e(message: String, error: Throwable? = null) {
        Log.e(TAG, message, error)
    }
}