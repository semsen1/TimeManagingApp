package com.example.clockapp.notificationSystem.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.clockapp.notificationSystem.core.RingHandler
import com.example.clockapp.notificationSystem.core.RingIntents
import com.example.clockapp.notificationSystem.core.RingKey
import com.example.clockapp.notificationSystem.core.RingRegistry
import com.example.clockapp.notificationSystem.scheduler.RingScheduler
import com.example.clockapp.alarm.AlarmDebug
import com.example.clockapp.notificationSystem.core.RingSlot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class RingReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {

//        AlarmDebug.d("Receiver.onReceive: action=${intent.action}, data=${intent.data}")
        val key = RingKey.from(intent)
        if (key == null) {
//            AlarmDebug.e("Receiver: incorrect intent, action=${intent.action}")
            return
        }

        val result = goAsync()
        val appContext = context.applicationContext
        val slot = RingSlot.from(intent)

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val handler = RingRegistry.handlerOf(appContext, key.type)
                val scheduler = RingScheduler(appContext)

                when (intent.action) {
                    RingIntents.ACTION_FIRE -> onFire(appContext, key, slot, handler, scheduler)

                    RingIntents.ACTION_SNOOZE -> {
                        val target = handler.getById(key.id) ?: return@launch
                        if (scheduler.snooze(key, target.ringSnoozeMinutes)) {
                            stopRinging(appContext, key)
                        }
                    }

                    RingIntents.ACTION_DISMISS -> {
                        handler.getById(key.id)?.let { handler.onDismissed(it) }
                        stopRinging(appContext, key)
                    }

//                    else -> AlarmDebug.w("Receiver: unknown action=${intent.action}")
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
//                AlarmDebug.e("Receiver: error action=${intent.action}, $key", e)
            } finally {
                result.finish()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun onFire(
        context: Context,
        key: RingKey,
        slot: RingSlot,
        handler: RingHandler,
        scheduler: RingScheduler
    ) {
        val target = handler.getById(key.id)
        if (target == null) {
//            AlarmDebug.e("Receiver.FIRE: record $key not found")
            return
        }
        if (!target.isRingEnabled && slot != RingSlot.SNOOZE) {
//            AlarmDebug.w("Receiver.FIRE: $key disabled")
            return
        }

        ContextCompat.startForegroundService(
            context, RingIntents.service(context, key, slot = slot)
        )

        when {
            slot == RingSlot.SNOOZE -> Unit
            slot == RingSlot.PRE && target.isOneShot -> Unit
            target.isOneShot -> handler.disable(key.id)
            else -> scheduler.schedule(target)
        }
    }

    private fun stopRinging(context: Context, key: RingKey) {
        context.startService(
            RingIntents.service(context, key, RingIntents.ACTION_DISMISS)
        )
    }
}