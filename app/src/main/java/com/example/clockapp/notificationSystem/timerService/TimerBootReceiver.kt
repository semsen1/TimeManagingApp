package com.example.clockapp.notificationSystem.timerService

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.clockapp.database.timerDao
import com.example.clockapp.entity.TimerType
import com.example.clockapp.notificationSystem.scheduler.TimerAlarmScheduler
import com.example.clockapp.notificationSystem.timer.TimerFinisher
import com.example.clockapp.notificationSystem.timer.TimerNotificationPublisher
import com.example.clockapp.notificationSystem.timer.runAsync

class TimerBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_MY_PACKAGE_REPLACED
        ) return
        val app = context.applicationContext

        runAsync {
            val dao = app.timerDao
            val scheduler = TimerAlarmScheduler(app)
            val publisher = TimerNotificationPublisher(app)
            val now = System.currentTimeMillis()

            dao.getRunning().filter { it.type == TimerType.TIMER }.forEach { t ->
                if (t.remaining(now) <= 0) {
                    TimerFinisher.finish(dao, t)
                    publisher.notifyFinished(t.id, t.title, t.color)
                } else {
                    scheduler.schedule(t.id, t.title, now + t.remaining(now))
                }
            }
            publisher.sync(dao.getAll())
        }
    }
}