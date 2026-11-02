package com.example.clockapp.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clockapp.database.ScheduleDao
import com.example.clockapp.entity.ScheduleEntity
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.temporal.ChronoUnit


data class CalendarRange(
    val startDate: LocalDate,
    val endDate: LocalDate
)

data class YearBounds(
    val minYear: Int,
    val maxYear: Int,
)

class ScheduleViewModel(
    private val scheduleDao: ScheduleDao,
    private val scheduler: RingScheduler,
) : ViewModel() {

    val schedules: StateFlow<List<ScheduleEntity>> =
        scheduleDao.observeAll()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    //query on date start
    @RequiresApi(Build.VERSION_CODES.O)
    private val _selectedDate = MutableStateFlow(LocalDate.now())

    @RequiresApi(Build.VERSION_CODES.O)
    val selectedDate: StateFlow<LocalDate> =
        _selectedDate.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    val schedulesByDate: StateFlow<List<ScheduleEntity>> =
        _selectedDate
            .flatMapLatest { date ->
                scheduleDao.getByDate(
                    date = date,
                    dayOfWeek = date.dayOfWeek.name
                )
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

    //query on date end

    //count on date start

    @RequiresApi(Build.VERSION_CODES.O)
    private val _calendarRange =
        MutableStateFlow(monthRange(YearMonth.now()))

    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    val scheduleCounts: StateFlow<Map<LocalDate, Int>> =
        _calendarRange
            .flatMapLatest { range ->
                scheduleDao.getCountsByDateRange(
                    startDate = range.startDate,
                    endDate = range.endDate
                )
            }
            .map { counts ->
                counts.associate { it.date to it.count }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyMap()
            )

    @RequiresApi(Build.VERSION_CODES.O)
    fun showMonth(month: YearMonth) {
        _calendarRange.value = monthRange(month)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showYear(year: Int) {
        _calendarRange.value = CalendarRange(
            startDate = LocalDate.of(year, 1, 1),
            endDate = LocalDate.of(year, 12, 31)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun monthRange(month: YearMonth): CalendarRange =
        CalendarRange(
            startDate = month.atDay(1),
            endDate = month.atEndOfMonth()
        )

    //count on date end

    @RequiresApi(Build.VERSION_CODES.O)
    private fun currentYear(): Int = LocalDate.now().year

    @RequiresApi(Build.VERSION_CODES.O)
    private val initialYear = currentYear()

    @RequiresApi(Build.VERSION_CODES.O)
    val yearBounds: StateFlow<YearBounds> =
        scheduleDao.observeYearBounds()
            .map { bounds ->
                val current = currentYear()

                YearBounds(
                    minYear = bounds.minYear ?: current,
                    maxYear = maxOf(bounds.maxYear ?: current, current),
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = YearBounds(
                    minYear = initialYear,
                    maxYear = initialYear,
                ),
            )


    @RequiresApi(Build.VERSION_CODES.O)
    fun addSchedule(schedule: ScheduleEntity) {
        viewModelScope.launch {
            val newId = scheduleDao.insert(schedule).toInt()
            scheduler.schedule(schedule.copy(id = newId))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateSchedule(schedule: ScheduleEntity) {
        viewModelScope.launch {
            scheduleDao.update(schedule)
            scheduler.schedule(schedule)
        }
    }

    fun deleteSchedule(id: Int) {
        viewModelScope.launch {
            scheduler.cancel(RingType.SCHEDULE, id)
            scheduleDao.deleteById(id)
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
    val nextSchedule: StateFlow<UpcomingEntity<ScheduleEntity>?> =
        combine(scheduleDao.observeAll(), minuteTicker) { list, now ->
            list.nextUpcoming(now)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )


}
