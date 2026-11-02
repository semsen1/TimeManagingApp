package com.example.clockapp.entity

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.clockapp.converters.AlarmConverters
import com.example.clockapp.models.DayOfWeek
import com.example.clockapp.notificationSystem.core.RingKey
import com.example.clockapp.notificationSystem.core.RingRecurrence
import com.example.clockapp.notificationSystem.core.RingType
import com.example.clockapp.notificationSystem.core.Ringable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


@TypeConverters(AlarmConverters::class)
@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val time: LocalTime,
    val date: LocalDate? = null,
    val isOnce: Boolean = false,
    val daysOfWeek: Set<DayOfWeek> = emptySet(),
    val replay: Int,
    override val wakeUpTest: Int,
    val isEnabled: Boolean = true,
    val sound: Uri? = null,
) : Ringable {

    override val ringKey: RingKey get() = RingKey(RingType.ALARM, id)
    override val ringTitle: String get() = title
    override val ringText: String get() = ""
    override val ringTime: LocalTime get() = time
    override val ringSound: Uri? get() = sound
    override val ringSnoozeMinutes: Int get() = replay
    override val isRingEnabled: Boolean get() = isEnabled
    override val isOneShot: Boolean get() = isOnce || date != null

    override val ringLeadMinutes: Int get() = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun nextTrigger(now: LocalDateTime): LocalDateTime? {
        date?.let { d ->
            return d.atTime(time).takeIf { it.isAfter(now) }
        }
        if (isOnce) {
            val today = now.toLocalDate().atTime(time)
            return if (today.isAfter(now)) today else today.plusDays(1)
        }
        return RingRecurrence.nextWeekly(time, now, daysOfWeek)
    }

}
