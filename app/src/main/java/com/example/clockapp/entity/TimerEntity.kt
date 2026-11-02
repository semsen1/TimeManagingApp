package com.example.clockapp.entity

import androidx.annotation.ColorInt
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.clockapp.converters.TimerConverter

enum class TimerType { STOPWATCH, TIMER }
enum class TimerStatus { IDLE, RUNNING, PAUSED, FINISHED }

@TypeConverters(TimerConverter::class)
@Entity(tableName = "timers")
data class TimerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "",
    val type: TimerType,
    val status: TimerStatus = TimerStatus.IDLE,
    val targetDurationMs: Long = 0,
    val accumulatedMs: Long = 0,
    val startedAtMs: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val deleteOnFinish: Boolean = false,
    @ColorInt val color: Int
) {
    fun elapsed(now: Long = System.currentTimeMillis()): Long {
        val started = startedAtMs
        return accumulatedMs + if (status == TimerStatus.RUNNING && started != null) {
            (now - started).coerceAtLeast(0)
        } else 0
    }

    fun remaining(now: Long = System.currentTimeMillis()): Long =
        targetDurationMs - elapsed(now)


    fun start(now: Long = System.currentTimeMillis()): TimerEntity =
        if (status == TimerStatus.RUNNING) this
        else copy(status = TimerStatus.RUNNING, startedAtMs = now)

    fun pause(now: Long = System.currentTimeMillis()): TimerEntity =
        if (status != TimerStatus.RUNNING) this
        else copy(
            status = TimerStatus.PAUSED,
            accumulatedMs = elapsed(now),
            startedAtMs = null
        )

    fun reset(): TimerEntity =
        copy(status = TimerStatus.IDLE, accumulatedMs = 0, startedAtMs = null)

    fun isFinished(now: Long = System.currentTimeMillis()): Boolean =
        type == TimerType.TIMER && status == TimerStatus.RUNNING && remaining(now) <= 0

    fun shouldBeDeleted(now: Long = System.currentTimeMillis()): Boolean =
        deleteOnFinish && isFinished(now)
}