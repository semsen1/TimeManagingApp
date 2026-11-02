package com.example.clockapp.notificationSystem.timerService

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.clockapp.database.timerDao
import com.example.clockapp.entity.TimerStatus
import com.example.clockapp.entity.TimerType
import com.example.clockapp.notificationSystem.scheduler.TimerAlarmScheduler
import com.example.clockapp.notificationSystem.timer.TimerNotificationConfig.ACTION_PAUSE
import com.example.clockapp.notificationSystem.timer.TimerNotificationConfig.ACTION_REFRESH
import com.example.clockapp.notificationSystem.timer.TimerNotificationConfig.ACTION_RESET
import com.example.clockapp.notificationSystem.timer.TimerNotificationConfig.ACTION_RESUME
import com.example.clockapp.notificationSystem.timer.TimerNotificationConfig.EXTRA_TIMER_ID
import com.example.clockapp.notificationSystem.timer.TimerNotificationPublisher
import com.example.clockapp.notificationSystem.timer.runAsync

class TimerActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val id = intent.getLongExtra(EXTRA_TIMER_ID, -1L)
        if (id < 0) return
        val app = context.applicationContext

        runAsync {
            val dao = app.timerDao
            val scheduler = TimerAlarmScheduler(app)
            val now = System.currentTimeMillis()

            dao.getById(id)?.let { current ->
                val updated = when (action) {
                    ACTION_PAUSE -> current.pause(now)
                    ACTION_RESUME -> current.start(now)
                    ACTION_RESET -> current.reset()
                    ACTION_REFRESH -> current
                    else -> current
                }
                if (updated != current) {
                    dao.update(updated)
                    if (updated.type == TimerType.TIMER && updated.status == TimerStatus.RUNNING) {
                        scheduler.schedule(
                            updated.id, updated.title, now + updated.remaining(now).coerceAtLeast(0)
                        )
                    } else {
                        scheduler.cancel(updated.id)
                    }
                }
            }
            TimerNotificationPublisher(app).sync(dao.getAll())
        }
    }
}