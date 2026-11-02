package com.example.clockapp.notificationSystem.timer

import com.example.clockapp.database.TimerDao
import com.example.clockapp.entity.TimerEntity
import com.example.clockapp.entity.TimerStatus
import com.example.clockapp.entity.TimerType

object TimerFinisher {
    const val TOLERANCE_MS = 1_500L

    fun isDue(t: TimerEntity, now: Long, toleranceMs: Long = 0L): Boolean =
        t.type == TimerType.TIMER &&
                t.status == TimerStatus.RUNNING &&
                t.remaining(now) <= toleranceMs


    suspend fun finish(dao: TimerDao, t: TimerEntity) {
        if (t.deleteOnFinish) {
            dao.delete(t)
        } else {
            dao.update(
                t.copy(
                    status = TimerStatus.FINISHED,
                    accumulatedMs = t.targetDurationMs,
                    startedAtMs = null
                )
            )
        }
    }
}