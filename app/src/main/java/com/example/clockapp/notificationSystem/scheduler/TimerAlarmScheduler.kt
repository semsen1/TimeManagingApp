package com.example.clockapp.notificationSystem.scheduler

import android.content.Context
import com.example.clockapp.notificationSystem.scheduler.AndroidTimerAlarmScheduler

interface TimerAlarmScheduler {
    fun schedule(timerId: Long, title: String, triggerAtMs: Long)

    fun cancel(timerId: Long)
}


fun TimerAlarmScheduler(context: Context): TimerAlarmScheduler =
    AndroidTimerAlarmScheduler(context.applicationContext)

sealed interface TimerEvent {
    data class Finished(
        val timerId: Long,
        val title: String,
        val deleted: Boolean
    ) : TimerEvent
}