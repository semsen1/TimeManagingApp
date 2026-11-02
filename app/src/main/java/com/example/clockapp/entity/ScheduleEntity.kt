package com.example.clockapp.entity

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.clockapp.converters.ScheduleConverters
import com.example.clockapp.models.DayOfWeek
import com.example.clockapp.notificationSystem.core.RingKey
import com.example.clockapp.notificationSystem.core.RingRecurrence
import com.example.clockapp.notificationSystem.core.RingType
import com.example.clockapp.notificationSystem.core.Ringable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@TypeConverters(ScheduleConverters::class)
@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val timeStart: LocalTime,
    val timeEnd: LocalTime? = null,
    val dateStart: LocalDate? = null,
    val dateEnd: LocalDate? = null,
    val notificationType: Int = 1,
    val daysOfWeek: Set<DayOfWeek> = emptySet(),
    val notify: Int = 0,
    val isEnabled: Boolean = true,
    val sound: Uri? = null,
) : Ringable {

    override val ringKey: RingKey get() = RingKey(RingType.SCHEDULE, id)
    override val ringTitle: String get() = title
    override val ringText: String get() = description
    override val ringTime: LocalTime get() = timeStart
    override val ringSound: Uri? get() = sound
    override val ringSnoozeMinutes: Int get() = 0
    override val isRingEnabled: Boolean get() = isEnabled
    override val isOneShot: Boolean get() = false

    override val ringLeadMinutes: Int get() = notify

    @RequiresApi(Build.VERSION_CODES.O)
    override fun nextTrigger(now: LocalDateTime): LocalDateTime? = nextWeekly(now, 0)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun nextPreTrigger(now: LocalDateTime): LocalDateTime? =
        if (notify <= 0) null else nextWeekly(now, notify.toLong())

    @RequiresApi(Build.VERSION_CODES.O)
    private fun nextWeekly(now: LocalDateTime, lead: Long) =
        RingRecurrence.nextWeekly(
            time = timeStart,
            now = now,
            daysOfWeek = daysOfWeek,
            leadMinutes = lead,
            rangeStart = dateStart,
            rangeEnd = dateEnd
        )

    @RequiresApi(Build.VERSION_CODES.O)
    fun isScheduledOn(day: LocalDate): Boolean {
        if (!isEnabled) return false

        if (dateStart?.let { day.isBefore(it) } == true) return false
        if (dateEnd?.let { day.isAfter(it) } == true) return false

        return daysOfWeek.isEmpty() ||
                daysOfWeek.any { it.index == day.dayOfWeek.value - 1 }
    }
}
