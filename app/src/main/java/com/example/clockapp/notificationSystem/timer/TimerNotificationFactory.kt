package com.example.clockapp.notificationSystem.timer

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.example.clockapp.R
import com.example.clockapp.entity.TimerEntity
import com.example.clockapp.entity.TimerStatus
import com.example.clockapp.entity.TimerType
import com.example.clockapp.notificationSystem.timer.TimerNotificationConfig.ACTION_PAUSE
import com.example.clockapp.notificationSystem.timer.TimerNotificationConfig.ACTION_REFRESH
import com.example.clockapp.notificationSystem.timer.TimerNotificationConfig.ACTION_RESET
import com.example.clockapp.notificationSystem.timer.TimerNotificationConfig.ACTION_RESUME
import com.example.clockapp.notificationSystem.timer.TimerNotificationConfig.CHANNEL_FINISHED
import com.example.clockapp.notificationSystem.timer.TimerNotificationConfig.CHANNEL_RUNNING
import com.example.clockapp.notificationSystem.timer.TimerNotificationConfig.EXTRA_TIMER_ID
import com.example.clockapp.notificationSystem.timer.TimerNotificationConfig.GROUP_STOPWATCHES
import com.example.clockapp.notificationSystem.timer.TimerNotificationConfig.GROUP_TIMERS
import com.example.clockapp.notificationSystem.timerService.TimerActionReceiver

import java.util.Locale

class TimerNotificationFactory(private val context: Context) {


    fun ongoing(t: TimerEntity, now: Long): Notification {
        val isTimer = t.type == TimerType.TIMER
        val isRunning = t.status == TimerStatus.RUNNING
        val title = t.title.ifBlank {
            context.getString(
                if (isTimer) R.string.timer_default_title else R.string.stopwatch_default_title
            )
        }

        val b = baseBuilder()
            .setContentTitle(title)
            .setColor(t.color)
            .setGroup(if (isTimer) GROUP_TIMERS else GROUP_STOPWATCHES)
            .setContentIntent(launchIntent())
            .setDeleteIntent(broadcast(ACTION_REFRESH, t.id))

        if (isRunning) {
            if (isTimer) {
                b.setWhen(now + t.remaining(now).coerceAtLeast(0))
                    .setChronometerCountDown(true)
                    .setContentText(
                        context.getString(
                            R.string.timer_text_running,
                            formatSeconds(t.targetDurationMs / 1000)
                        )
                    )
            } else {
                b.setWhen(now - t.elapsed(now))
                    .setContentText(context.getString(R.string.stopwatch_text_running))
            }
            b.setShowWhen(true).setUsesChronometer(true)
        } else {
            val text = if (isTimer) {
                val sec = (t.remaining(now).coerceAtLeast(0) + 999) / 1000
                context.getString(R.string.timer_text_paused, formatSeconds(sec))
            } else {
                context.getString(
                    R.string.stopwatch_text_paused,
                    formatSeconds(t.elapsed(now) / 1000)
                )
            }
            b.setShowWhen(false).setUsesChronometer(false).setContentText(text)
        }

        b.addAction(
            0,
            context.getString(if (isRunning) R.string.timer_action_pause else R.string.timer_action_resume),
            broadcast(if (isRunning) ACTION_PAUSE else ACTION_RESUME, t.id)
        )
        b.addAction(
            0,
            context.getString(R.string.timer_action_reset),
            broadcast(ACTION_RESET, t.id)
        )
        return b.build()
    }

    fun summary(type: TimerType, count: Int): Notification {
        val isTimer = type == TimerType.TIMER
        val title = context.resources.getQuantityString(
            if (isTimer) R.plurals.timers_active_count else R.plurals.stopwatches_active_count,
            count, count
        )
        return baseBuilder()
            .setContentTitle(title)
            .setGroup(if (isTimer) GROUP_TIMERS else GROUP_STOPWATCHES)
            .setGroupSummary(true)
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
            .setContentIntent(launchIntent())
            .build()
    }

    fun finished(title: String, color: Int?): Notification {
        val b = NotificationCompat.Builder(context, CHANNEL_FINISHED)
            .setSmallIcon(R.drawable.hourglasses)
            .setContentTitle(title.ifBlank { context.getString(R.string.timer_default_title) })
            .setContentText(context.getString(R.string.timer_finished_text))
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(launchIntent())
        color?.let { b.setColor(it) }
        return b.build()
    }

    private fun baseBuilder() = NotificationCompat.Builder(context, CHANNEL_RUNNING)
        .setSmallIcon(R.drawable.ic_stopwatch)
        .setCategory(NotificationCompat.CATEGORY_STATUS)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setOngoing(true)
        .setAutoCancel(false)
        .setOnlyAlertOnce(true)
        .setLocalOnly(true)

    private fun launchIntent(): PendingIntent? {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            ?: return null
        return PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun broadcast(action: String, timerId: Long): PendingIntent {
        val intent = Intent(context, TimerActionReceiver::class.java)
            .setAction(action)
            .setData("clockapp://action/$timerId/$action".toUri())
            .putExtra(EXTRA_TIMER_ID, timerId)
        return PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun formatSeconds(total: Long): String {
        val s = total.coerceAtLeast(0)
        val h = s / 3600
        val m = s % 3600 / 60
        val sec = s % 60
        return if (h > 0) String.format(Locale.getDefault(), "%d:%02d:%02d", h, m, sec)
        else String.format(Locale.getDefault(), "%02d:%02d", m, sec)
    }
}