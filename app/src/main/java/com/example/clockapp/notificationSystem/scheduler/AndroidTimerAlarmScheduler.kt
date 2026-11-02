package com.example.clockapp.notificationSystem.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.net.toUri
import com.example.clockapp.notificationSystem.timerService.TimerAlarmReceiver
import com.example.clockapp.notificationSystem.timer.TimerNotificationConfig.ACTION_FINISHED
import com.example.clockapp.notificationSystem.timer.TimerNotificationConfig.EXTRA_TIMER_ID
import com.example.clockapp.notificationSystem.timer.TimerNotificationConfig.EXTRA_TITLE

class AndroidTimerAlarmScheduler(private val context: Context) : TimerAlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(timerId: Long, title: String, triggerAtMs: Long) {
        val pi = pendingIntent(timerId, title, PendingIntent.FLAG_UPDATE_CURRENT) ?: return
        val canExact =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()
        try {
            if (canExact) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMs, pi)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMs, pi)
            }
        } catch (_: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMs, pi)
        }
    }

    override fun cancel(timerId: Long) {
        pendingIntent(timerId, "", PendingIntent.FLAG_NO_CREATE)?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }

    private fun pendingIntent(timerId: Long, title: String, flag: Int): PendingIntent? {
        val intent = Intent(context, TimerAlarmReceiver::class.java)
            .setAction(ACTION_FINISHED)
            .setData("clockapp://alarm/$timerId".toUri())
            .putExtra(EXTRA_TIMER_ID, timerId)
            .putExtra(EXTRA_TITLE, title)
        return PendingIntent.getBroadcast(
            context, timerId.hashCode(), intent, flag or PendingIntent.FLAG_IMMUTABLE
        )
    }
}