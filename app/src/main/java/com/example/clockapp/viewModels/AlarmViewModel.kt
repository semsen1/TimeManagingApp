package com.example.clockapp.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clockapp.database.AlarmDao
import com.example.clockapp.entity.AlarmEntity
import com.example.clockapp.notificationSystem.core.RingType
import com.example.clockapp.notificationSystem.core.Ringable
import com.example.clockapp.notificationSystem.scheduler.RingScheduler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit


class AlarmViewModel(
    private val alarmDao: AlarmDao,
    private val scheduler: RingScheduler,
) : ViewModel() {

    val alarms: StateFlow<List<AlarmEntity>> =
        alarmDao.observeAll()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    @RequiresApi(Build.VERSION_CODES.O)
    fun addAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            val newId = alarmDao.insert(alarm).toInt()
            scheduler.schedule(alarm.copy(id = newId))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            alarmDao.update(alarm)
            scheduler.schedule(alarm)
        }
    }

    fun deleteAlarm(id: Int) {
        viewModelScope.launch {
            scheduler.cancel(RingType.ALARM, id)
            alarmDao.deleteById(id)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val minuteTicker: Flow<LocalDateTime> = flow {
        while (true) {
            val now = LocalDateTime.now()
            emit(now.truncatedTo(ChronoUnit.MINUTES))
            val nextMinute = now.truncatedTo(ChronoUnit.MINUTES).plusMinutes(1)
            delay(Duration.between(now, nextMinute).toMillis() + 50)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val nextAlarm: StateFlow<UpcomingEntity<AlarmEntity>?> =
        combine(alarmDao.observeAll(), minuteTicker) { list, now ->
            list.nextUpcoming(now)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )
}

