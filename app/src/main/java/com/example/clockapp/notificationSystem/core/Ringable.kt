package com.example.clockapp.notificationSystem.core

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.LocalTime

interface Ringable {
    val ringKey: RingKey
    val ringTitle: String
    val ringText: String
    val ringTime: LocalTime
    val ringSound: Uri?
    val ringSnoozeMinutes: Int
    val isRingEnabled: Boolean
    val isOneShot: Boolean

    val ringLeadMinutes: Int get() = 0

    val wakeUpTest: Int
        get() = 0

    @RequiresApi(Build.VERSION_CODES.O)
    fun nextTrigger(now: LocalDateTime = LocalDateTime.now()): LocalDateTime?

    @RequiresApi(Build.VERSION_CODES.O)
    fun nextPreTrigger(now: LocalDateTime = LocalDateTime.now()): LocalDateTime? = null
}