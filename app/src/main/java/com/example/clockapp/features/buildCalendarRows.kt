package com.example.clockapp.features

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

data class ScheduleDay(
    val date: LocalDate,
    val count: Int
)

@RequiresApi(Build.VERSION_CODES.O)
fun buildCalendarRows(
    scheduleList: Map<LocalDate, Int>
): List<List<ScheduleDay?>> {
    if (scheduleList.isEmpty()) return emptyList()

    val sorted = scheduleList.toSortedMap()

    val firstMonday = sorted.firstKey().with(
        TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
    )
    val lastMonday = sorted.lastKey().with(
        TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
    )

    return generateSequence(firstMonday) { it.plusWeeks(1) }
        .takeWhile { !it.isAfter(lastMonday) }
        .map { monday ->
            (0..6).map { offset ->
                val date = monday.plusDays(offset.toLong())
                sorted[date]?.let { count ->
                    ScheduleDay(date, count)
                }
            }
        }
        .toList()
}