package com.example.clockapp.notificationSystem.service

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import androidx.annotation.RequiresApi
import com.example.clockapp.notificationSystem.core.RingIntents
import com.example.clockapp.notificationSystem.core.RingRegistry
import com.example.clockapp.notificationSystem.core.RingType
import com.example.clockapp.notificationSystem.scheduler.RingScheduler
import com.example.clockapp.alarm.AlarmDebug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import kotlin.coroutines.cancellation.CancellationException

class BootReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {

//        val id = intent.getIntExtra(
//            RingIntents.EXTRA_ID,
//            -1
//        )
//
//        AlarmDebug.d(
//            "Receiver.onReceive: " +
//                    "action=${intent.action}, " +
//                    "id=$id, " +
//                    "at=${ZonedDateTime.now()}, " +
//                    "elapsed=${SystemClock.elapsedRealtime()}"
//        )
        val supportedActions = mutableSetOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_MY_PACKAGE_REPLACED
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            supportedActions += AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED
        }
        if (intent.action !in supportedActions) return

        val result = goAsync()
        val appContext = context.applicationContext

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val scheduler = RingScheduler(appContext)
                RingType.entries.forEach { type ->
                    try {
                        RingRegistry.handlerOf(appContext, type).getAll()
                            .filter { it.isRingEnabled }
                            .forEach { scheduler.schedule(it) }
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
//                        AlarmDebug.e("BootReceiver: recovery error $type", e)
                    }
                }
            } finally {
                result.finish()
            }
        }
    }
}