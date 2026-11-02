package com.example.clockapp.viewModels

import com.example.clockapp.entity.AlarmEntity
import com.example.clockapp.notificationSystem.core.Ringable
import java.time.LocalDateTime

data class UpcomingEntity<T : Ringable>(
    val entity: T,
    val triggerAt: LocalDateTime,
    val minutesLeft: Long,
)