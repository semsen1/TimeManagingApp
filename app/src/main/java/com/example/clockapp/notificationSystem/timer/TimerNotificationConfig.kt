package com.example.clockapp.notificationSystem.timer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.provider.Settings
import com.example.clockapp.R

object TimerNotificationConfig {
    const val CHANNEL_RUNNING = "timers_running_v1"
    const val CHANNEL_FINISHED = "timers_finished_v1"

    const val GROUP_TIMERS = "group_timers_running"
    const val GROUP_STOPWATCHES = "group_stopwatches_running"

    const val TAG_TIMER = "timer_running"
    const val TAG_STOPWATCH = "stopwatch_running"
    const val TAG_TIMER_SUMMARY = "timer_running_summary"
    const val TAG_STOPWATCH_SUMMARY = "stopwatch_running_summary"
    const val TAG_FINISHED = "timer_finished"
    const val SUMMARY_ID = 0

    val RUNNING_TAGS = setOf(TAG_TIMER, TAG_STOPWATCH, TAG_TIMER_SUMMARY, TAG_STOPWATCH_SUMMARY)

    const val ACTION_PAUSE = "com.example.clockapp.timer.action.PAUSE"
    const val ACTION_RESUME = "com.example.clockapp.timer.action.RESUME"
    const val ACTION_RESET = "com.example.clockapp.timer.action.RESET"
    const val ACTION_REFRESH = "com.example.clockapp.timer.action.REFRESH"
    const val ACTION_FINISHED = "com.example.clockapp.timer.action.FINISHED"

    const val EXTRA_TIMER_ID = "extra_timer_id"
    const val EXTRA_TITLE = "extra_title"

    fun ensureChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val nm = context.getSystemService(NotificationManager::class.java)

        val running = NotificationChannel(
            CHANNEL_RUNNING,
            context.getString(R.string.timer_channel_running_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = context.getString(R.string.timer_channel_running_desc)
            setShowBadge(false)
            setSound(null, null)
            enableVibration(false)
            lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
        }

        val finished = NotificationChannel(
            CHANNEL_FINISHED,
            context.getString(R.string.timer_channel_finished_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            setSound(
                Settings.System.DEFAULT_ALARM_ALERT_URI,
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            enableVibration(true)
        }

        nm.createNotificationChannels(listOf(running, finished))
    }
}