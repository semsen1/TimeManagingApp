package com.example.clockapp.notificationSystem.core

import android.content.Context
import com.example.clockapp.alarm.AlarmRingHandler
import com.example.clockapp.reminder.ReminderRingHandler
import com.example.clockapp.schedule.ScheduleRingHandler

object RingRegistry {
    fun handlerOf(context: Context, type: RingType): RingHandler {
        val app = context.applicationContext
        return when (type) {
            RingType.ALARM -> AlarmRingHandler(app)
            RingType.REMINDER -> ReminderRingHandler(app)
            RingType.SCHEDULE -> ScheduleRingHandler(app)
        }
    }
}