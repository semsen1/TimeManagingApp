package com.example.clockapp.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clockapp.database.ReminderDao
import com.example.clockapp.entity.AlarmEntity
import com.example.clockapp.entity.ReminderEntity
import com.example.clockapp.notificationSystem.core.RingType
import com.example.clockapp.notificationSystem.scheduler.RingScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class ReminderViewModel(
    private val reminderDao: ReminderDao,
    private val scheduler: RingScheduler,
) : ViewModel() {

    val reminders: StateFlow<List<ReminderEntity>> =
        reminderDao.observeAll()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    @RequiresApi(Build.VERSION_CODES.O)
    fun addReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            val newId = reminderDao.insert(reminder).toInt()
            scheduler.schedule(reminder.copy(id = newId))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            reminderDao.update(reminder)
            scheduler.schedule(reminder)
        }
    }

    fun deleteReminder(id: Int) {
        viewModelScope.launch {
            scheduler.cancel(RingType.REMINDER, id)
            reminderDao.deleteById(id)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val _selectedDate = MutableStateFlow(LocalDate.now())

    @RequiresApi(Build.VERSION_CODES.O)
    val selectedDate: StateFlow<LocalDate> =
        _selectedDate.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    val remindersByDate: StateFlow<List<ReminderEntity>> =
        _selectedDate
            .flatMapLatest { date ->
                reminderDao.getByDate(date = date)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    @RequiresApi(Build.VERSION_CODES.O)
    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
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
    val nextReminder: StateFlow<UpcomingEntity<ReminderEntity>?> =
        combine(reminderDao.observeAll(), minuteTicker) { list, now ->
            list.nextUpcoming(now)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

}

