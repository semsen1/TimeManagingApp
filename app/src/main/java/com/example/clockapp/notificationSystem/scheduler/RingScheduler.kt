package com.example.clockapp.notificationSystem.scheduler

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.clockapp.alarm.AlarmDebug
import com.example.clockapp.notificationSystem.core.RingIntents
import com.example.clockapp.notificationSystem.core.RingKey
import com.example.clockapp.notificationSystem.core.RingSlot
import com.example.clockapp.notificationSystem.core.RingType
import com.example.clockapp.notificationSystem.core.Ringable
import java.time.LocalDateTime
import java.time.ZoneId

class RingScheduler(private val context: Context) {


    private val manager = context.getSystemService(AlarmManager::class.java)

    fun canSchedule(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S || manager.canScheduleExactAlarms()


    fun cancel(kind: RingType, id: Int) {
        val key = RingKey(kind, id)
        AlarmDebug.d("Scheduler.cancel key=$key")
        cancelRegular(key)
    }

    private fun cancelRegular(key: RingKey) {
        manager.cancel(RingIntents.firePending(context, key, RingSlot.MAIN))
        manager.cancel(RingIntents.firePending(context, key, RingSlot.PRE))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun LocalDateTime.toEpochMillis(): Long =
        atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    @RequiresApi(Build.VERSION_CODES.O)
    fun schedule(target: Ringable): Boolean {
        val key = target.ringKey
        cancelRegular(key)
        if (!target.isRingEnabled) return true

        val now = LocalDateTime.now()
        var result = true

        target.nextTrigger(now)?.let {
//            AlarmDebug.d("Scheduler.schedule MAIN: $key, at=$it")
            result = scheduleAt(key, RingSlot.MAIN, it.toEpochMillis())
            //&& true

        }
        if (target.ringLeadMinutes > 0) {
            target.nextPreTrigger(now)?.let {
//                AlarmDebug.d("Scheduler.schedule PRE: $key, at=$it")
                result = scheduleAt(key, RingSlot.PRE, it.toEpochMillis()) && result
            }
        }
        return result
    }


    fun snooze(key: RingKey, minutes: Int): Boolean {
        if (minutes <= 0) return false
        return scheduleAt(key, RingSlot.SNOOZE, System.currentTimeMillis() + minutes * 60_000L)
    }

    private fun scheduleAt(key: RingKey, slot: RingSlot, triggerAtMillis: Long): Boolean {
        if (!canSchedule()) {
//            AlarmDebug.e("Scheduler.scheduleAt: There is no permission for the exact alarm, $key")
            return false
        }
        return try {
            val pending = RingIntents.firePending(context, key, slot)
            if (key.type.showsAsSystemAlarm) {
                manager.setAlarmClock(
                    AlarmManager.AlarmClockInfo(
                        triggerAtMillis,
                        RingIntents.activityPending(context, key)
                    ),
                    pending
                )
            } else {
                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending)
            }
//            AlarmDebug.d("Scheduler.scheduleAt OK: $key, slot=$slot, at=$triggerAtMillis")
            true
        } catch (e: Exception) {
//            AlarmDebug.e("Scheduler.scheduleAt FAILED, $key, slot=$slot", e)
            false
        }
    }
}