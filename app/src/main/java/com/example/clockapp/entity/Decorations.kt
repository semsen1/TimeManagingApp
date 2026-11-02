package com.example.clockapp.entity

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.clockapp.models.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime

enum class EventType {
    ALARM,
    REMINDER,
    SCHEDULE
}

enum class EventSlot {
    START,
    END,
    BEFORE
}

data class EventKey(
    val type: EventType,
    val entityId: Int,
    val slot: EventSlot = EventSlot.START
)


