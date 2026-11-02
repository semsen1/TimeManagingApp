package com.example.clockapp.notificationSystem.core

import androidx.annotation.StringRes
import com.example.clockapp.R

enum class RingType(
    val code: String,
    @StringRes val defaultTitleRes: Int,
    @StringRes val ringingTextRes: Int,

    val showsAsSystemAlarm: Boolean
) {
    ALARM("alarm", R.string.alarm, R.string.ring_alarm_text, true),
    REMINDER("reminder", R.string.reminder, R.string.ring_reminder_text, false),
    SCHEDULE("schedule", R.string.schedule, R.string.ring_schedule_text, false);

    companion object {
        fun fromCode(code: String?): RingType? = entries.firstOrNull { it.code == code }
    }
}