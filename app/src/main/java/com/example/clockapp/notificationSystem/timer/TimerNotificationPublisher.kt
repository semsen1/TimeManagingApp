package com.example.clockapp.notificationSystem.timer

import android.app.NotificationManager
import android.content.Context
import com.example.clockapp.entity.TimerEntity
import com.example.clockapp.entity.TimerStatus
import com.example.clockapp.entity.TimerType
import com.example.clockapp.notificationSystem.timer.TimerNotificationConfig.RUNNING_TAGS
import com.example.clockapp.notificationSystem.timer.TimerNotificationConfig.SUMMARY_ID
import com.example.clockapp.notificationSystem.timer.TimerNotificationConfig.TAG_FINISHED
import com.example.clockapp.notificationSystem.timer.TimerNotificationConfig.TAG_STOPWATCH
import com.example.clockapp.notificationSystem.timer.TimerNotificationConfig.TAG_STOPWATCH_SUMMARY
import com.example.clockapp.notificationSystem.timer.TimerNotificationConfig.TAG_TIMER
import com.example.clockapp.notificationSystem.timer.TimerNotificationConfig.TAG_TIMER_SUMMARY

class TimerNotificationPublisher(context: Context) {

    private val context = context.applicationContext
    private val manager = this.context.getSystemService(NotificationManager::class.java)
    private val factory = TimerNotificationFactory(this.context)


    fun sync(all: List<TimerEntity>, now: Long = System.currentTimeMillis()) {
        TimerNotificationConfig.ensureChannels(context)

        val active = all.filter { it.status == TimerStatus.RUNNING || it.status == TimerStatus.PAUSED }
        val desired = HashSet<Pair<String, Int>>()

        for (type in TimerType.entries) {
            val isTimer = type == TimerType.TIMER
            val tag = if (isTimer) TAG_TIMER else TAG_STOPWATCH
            val summaryTag = if (isTimer) TAG_TIMER_SUMMARY else TAG_STOPWATCH_SUMMARY
            val items = active.filter { it.type == type }.sortedBy { it.createdAt }

            if (items.isNotEmpty()) {
                manager.notify(summaryTag, SUMMARY_ID, factory.summary(type, items.size))
                desired += summaryTag to SUMMARY_ID
            }

            items.forEach { t ->
                val id = t.id.toInt()
                manager.notify(tag, id, factory.ongoing(t, now))
                desired += tag to id
            }
            if (items.size >= 2) {
                manager.notify(summaryTag, SUMMARY_ID, factory.summary(type, items.size))
                desired += summaryTag to SUMMARY_ID
            }
        }

        manager.activeNotifications
            .filter { it.tag in RUNNING_TAGS && (it.tag to it.id) !in desired }
            .forEach { manager.cancel(it.tag, it.id) }
    }

    fun notifyFinished(timerId: Long, title: String, color: Int?) {
        TimerNotificationConfig.ensureChannels(context)
        manager.notify(TAG_FINISHED, timerId.toInt(), factory.finished(title, color))
    }
}