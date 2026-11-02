package com.example.clockapp.notificationSystem.core

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.clockapp.models.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

object RingRecurrence {

    @RequiresApi(Build.VERSION_CODES.O)
    fun nextWeekly(
        time: LocalTime,
        now: LocalDateTime,
        daysOfWeek: Set<DayOfWeek> = emptySet(),
        leadMinutes: Long = 0,
        rangeStart: LocalDate? = null,
        rangeEnd: LocalDate? = null
    ): LocalDateTime? {
        val today = now.toLocalDate()
        val first = if (rangeStart != null && rangeStart.isAfter(today)) rangeStart else today

        return generateSequence(first) { it.plusDays(1) }
            .take(8)
            .takeWhile { rangeEnd == null || !it.isAfter(rangeEnd) }
            .filter { d ->
                daysOfWeek.isEmpty() || daysOfWeek.any { it.index == d.dayOfWeek.value - 1 }
            }
            .map { it.atTime(time).minusMinutes(leadMinutes) }
            .firstOrNull { it.isAfter(now) }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun Set<DayOfWeek>.matches(date: LocalDate): Boolean =
    isEmpty() || any { it.index == date.dayOfWeek.value - 1 }