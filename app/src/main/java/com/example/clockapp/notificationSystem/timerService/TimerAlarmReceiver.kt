package com.example.clockapp.notificationSystem.timerService

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.clockapp.database.timerDao
import com.example.clockapp.entity.TimerStatus
import com.example.clockapp.notificationSystem.timer.TimerFinisher
import com.example.clockapp.notificationSystem.timer.TimerNotificationConfig.ACTION_FINISHED
import com.example.clockapp.notificationSystem.timer.TimerNotificationConfig.EXTRA_TIMER_ID
import com.example.clockapp.notificationSystem.timer.TimerNotificationConfig.EXTRA_TITLE
import com.example.clockapp.notificationSystem.timer.TimerNotificationPublisher
import com.example.clockapp.notificationSystem.timer.runAsync

class TimerAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_FINISHED) return
        val id = intent.getLongExtra(EXTRA_TIMER_ID, -1L)
        if (id < 0) return
        val fallbackTitle = intent.getStringExtra(EXTRA_TITLE).orEmpty()
        val app = context.applicationContext

        runAsync {
            val dao = app.timerDao
            val publisher = TimerNotificationPublisher(app)
            val now = System.currentTimeMillis()
            val timer = dao.getById(id)

            val shouldNotify = when {
                timer == null -> true
                TimerFinisher.isDue(timer, now, TimerFinisher.TOLERANCE_MS) -> {
                    TimerFinisher.finish(dao, timer)
                    true
                }

                else -> timer.status == TimerStatus.FINISHED
            }

            if (shouldNotify) {
                publisher.notifyFinished(id, timer?.title ?: fallbackTitle, timer?.color)
            }
            publisher.sync(dao.getAll())
        }
    }
}