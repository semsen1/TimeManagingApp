package com.example.clockapp.wallpaper

import android.content.Context
import android.os.Build
import android.text.format.DateFormat
import androidx.annotation.RequiresApi
import com.example.clockapp.R
import com.example.clockapp.database.AppDatabase
import com.example.clockapp.entity.BlockType
import com.example.clockapp.entity.TimerEntity
import com.example.clockapp.entity.TimerStatus
import com.example.clockapp.entity.TimerType
import com.example.clockapp.notificationSystem.core.Ringable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BlockContentProvider(
    private val context: Context
) {

    private val alarmDao = AppDatabase.getInstance(this.context).alarmDao()
    private val reminderDao = AppDatabase.getInstance(this.context).reminderDao()

    private val scheduleDao = AppDatabase.getInstance(this.context).scheduleDao()

    private val timerDao = AppDatabase.getInstance(this.context).timerDao()


    @RequiresApi(Build.VERSION_CODES.O)
    fun observe(): Flow<Map<BlockType, BlockContent>> = combine(
        alarms(), reminders(), schedules(), timers(), stopwatch()
    ) { alarm, reminder, schedule, timer, stopwatch ->
        mapOf(
            BlockType.ALARM to alarm,
            BlockType.REMINDER to reminder,
            BlockType.SCHEDULE to schedule,
            BlockType.TIMER to timer,
            BlockType.STOPWATCH to stopwatch
        )
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun alarms(): Flow<BlockContent> =
        alarmDao.observeAll().map { list ->
            BlockContent(context.getString(R.string.nearest_alarm)) {
                val now = System.currentTimeMillis()
                val next = list.minByOrNull { Next.nextTrigger(it, now) }
                if (next == null) {
                    listOf(context.getString(R.string.no_active))
                } else {
                    val trigger = Next.nextTrigger(next, now)
                    val time = DateFormat.getTimeFormat(context).format(Date(trigger.timeInMillis))
                    listOf(
                        next.title,
                        time
                    )
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun reminders(): Flow<BlockContent> =
        reminderDao.observeAll().map { list ->

            BlockContent(context.getString(R.string.nearest_reminder)) {
                val now = System.currentTimeMillis()
                val next = list.minByOrNull { Next.nextTrigger(it, now) }
                if (next == null) {
                    listOf(context.getString(R.string.no_active))
                } else {
                    val trigger = Next.nextTrigger(next, now)
                    val time = DateFormat.getTimeFormat(context).format(Date(trigger.timeInMillis))
                    listOf(
                        next.title,
                        time
                    )
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun schedules(): Flow<BlockContent> =
        scheduleDao.observeAll().map { list ->

            BlockContent(context.getString(R.string.nearest_schedule)) {
                val now = System.currentTimeMillis()
                val next = list.minByOrNull { Next.nextTrigger(it, now) }
                if (next == null) {
                    listOf(context.getString(R.string.no_active))
                } else {
                    val scheduleDuration =
                        next.timeStart.toString() + " - " + next.timeEnd.toString()
                    listOf(
                        next.title,
                        scheduleDuration
                    )
                }
            }
        }

    private fun timers(): Flow<BlockContent> =
        timerDao.observeAll().map { all ->
            val active = all
                .filter { it.type == TimerType.TIMER && it.status != TimerStatus.IDLE }
                .sortedBy { if (it.status == TimerStatus.RUNNING) 0 else 1 }
            val ticking = active.any { it.status == TimerStatus.RUNNING }

            BlockContent(
                context.getString(R.string.timer),
                refreshMs = if (ticking) 1_000L else BlockContent.DEFAULT_REFRESH_MS
            ) {
                val now = System.currentTimeMillis()
                if (active.isEmpty()) listOf(context.getString(R.string.not_launched))
                else active.take(MAX_ROWS)
                    .map { "${it.label(context.getString(R.string.timer))}  ${timerText(it, now)}" }
            }
        }

    private fun stopwatch(): Flow<BlockContent> =
        timerDao.observeAll().map { all ->
            val active = all
                .filter { it.type == TimerType.STOPWATCH && it.status != TimerStatus.IDLE }
                .sortedBy { if (it.status == TimerStatus.RUNNING) 0 else 1 }
            val ticking = active.any { it.status == TimerStatus.RUNNING }

            BlockContent(
                context.getString(R.string.stopwatch),
                refreshMs = if (ticking) 100L else BlockContent.DEFAULT_REFRESH_MS
            ) {
                val now = System.currentTimeMillis()
                if (active.isEmpty()) listOf("00:00.0")
                else active.take(MAX_ROWS).map {
                    "${it.label(context.getString(R.string.stopwatch))}  ${
                        formatTime(
                            it.elapsed(now),
                            tenths = true
                        )
                    }"
                }
            }
        }

    private fun TimerEntity.label(fallback: String) = title.ifBlank { fallback }

    private fun timerText(t: TimerEntity, now: Long): String {
        if (t.status == TimerStatus.FINISHED) return context.getString(R.string.finished)
        val rem = t.remaining(now)
        return if (rem >= 0) formatTime((rem + 999) / 1000 * 1000) else "+" + formatTime(-rem)
    }

    private fun formatTime(ms: Long, tenths: Boolean = false): String {
        val total = ms / 1000
        val h = total / 3600
        val m = total % 3600 / 60
        val s = total % 60
        val base = if (h > 0) String.format(Locale.ROOT, "%d:%02d:%02d", h, m, s)
        else String.format(Locale.ROOT, "%02d:%02d", m, s)
        return if (tenths) "$base.${ms % 1000 / 100}" else base
    }

    private companion object {
        const val MAX_ROWS = 3
    }
}

object Next {
    @RequiresApi(Build.VERSION_CODES.O)
    fun nextTrigger(entity: Ringable, now: Long): Calendar {
        val cal = Calendar.getInstance().apply {
            timeInMillis = now
            set(Calendar.HOUR_OF_DAY, entity.ringTime.hour)
            set(Calendar.MINUTE, entity.ringTime.hour)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (cal.timeInMillis <= now) cal.add(Calendar.DAY_OF_YEAR, 1)
        return cal
    }
}