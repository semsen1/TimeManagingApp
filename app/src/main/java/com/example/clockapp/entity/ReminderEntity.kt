package com.example.clockapp.entity

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.clockapp.converters.ReminderConverters
import com.example.clockapp.notificationSystem.core.RingKey
import com.example.clockapp.notificationSystem.core.RingRecurrence
import com.example.clockapp.notificationSystem.core.RingType
import com.example.clockapp.notificationSystem.core.Ringable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@TypeConverters(ReminderConverters::class)
@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val time: LocalTime,
    val date: LocalDate? = null,
    val notify: Int = 0,
    val postponedTime: Int = 0,
    val notificationType: Int = 1,
    val isAutoConfirm: Boolean = true,
    val isConfirmed: Boolean = false,
    val isEnabled: Boolean = true,
    val sound: Uri? = null,
) : Ringable {

    override val ringKey: RingKey get() = RingKey(RingType.REMINDER, id)
    override val ringTitle: String get() = title
    override val ringText: String get() = description
    override val ringTime: LocalTime get() = time
    override val ringSound: Uri? get() = sound
    override val ringSnoozeMinutes: Int get() = postponedTime
    override val isRingEnabled: Boolean get() = isEnabled && !isConfirmed
    override val isOneShot: Boolean get() = date != null

    override val ringLeadMinutes: Int get() = notify

    @RequiresApi(Build.VERSION_CODES.O)
    override fun nextTrigger(now: LocalDateTime): LocalDateTime? {
        if (!isEnabled) return null

        return if (date != null) {
            LocalDateTime.of(date, time).takeIf { it.isAfter(now) }
        } else {
            val today = LocalDateTime.of(now.toLocalDate(), time)
            if (today.isAfter(now)) today else today.plusDays(1)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun nextPreTrigger(now: LocalDateTime): LocalDateTime? {
        val lead = notify.toLong()
        if (lead <= 0) return null
        val d = date ?: return RingRecurrence.nextWeekly(time, now, leadMinutes = lead)
        return d.atTime(time).minusMinutes(lead).takeIf { it.isAfter(now) }
    }

}
