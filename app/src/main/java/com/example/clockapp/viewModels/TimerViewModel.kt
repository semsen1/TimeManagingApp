package com.example.clockapp.viewModels
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clockapp.database.TimerDao
import com.example.clockapp.entity.TimerEntity
import com.example.clockapp.entity.TimerStatus
import com.example.clockapp.entity.TimerType
import com.example.clockapp.models.TimerPalette
import com.example.clockapp.notificationSystem.scheduler.TimerAlarmScheduler
import com.example.clockapp.notificationSystem.scheduler.TimerEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class TimerViewModel(
    private val dao: TimerDao,
    private val scheduler: TimerAlarmScheduler
) : ViewModel() {

    val stopwatches: StateFlow<List<TimerEntity>> = dao.observeByType(TimerType.STOPWATCH)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val timers: StateFlow<List<TimerEntity>> = dao.observeByType(TimerType.TIMER)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val now: StateFlow<Long> = dao.observeRunning()
        .map { it.isNotEmpty() }
        .distinctUntilChanged()
        .flatMapLatest { active ->
            if (active) ticker(TICK_MS) else flowOf(System.currentTimeMillis())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), System.currentTimeMillis())


    private val eventsChannel = Channel<TimerEvent>(Channel.BUFFERED)
    val events = eventsChannel.receiveAsFlow()

    private val mutex = Mutex()

    init {
        watchFinish()
    }

    fun createStopwatch(title: String = "") {
        viewModelScope.launch {
            dao.insert(
                TimerEntity(
                    title = title.trim(),
                    type = TimerType.STOPWATCH,
                    color = TimerPalette.random(),
                    status = TimerStatus.RUNNING,
                    startedAtMs = System.currentTimeMillis()
                )
            )
        }
    }

    fun createTimer(title: String = "", durationMs: Long, deleteOnFinish: Boolean = false) {
        if (durationMs <= 0) return
        viewModelScope.launch {
            dao.insert(
                TimerEntity(
                    title = title.trim(),
                    type = TimerType.TIMER,
                    targetDurationMs = durationMs,
                    deleteOnFinish = deleteOnFinish,
                    color = TimerPalette.random()
                )
            )
        }
    }

    fun updateTimer(id: Long, title: String, durationMs: Long, deleteOnFinish: Boolean) {
        if (durationMs <= 0) return
        viewModelScope.launch {
            dao.updateSettings(id, title.trim(), durationMs, deleteOnFinish)
        }
    }


    fun start(id: Long) = mutate(id) { t, now ->
        if (t.status == TimerStatus.FINISHED) t.reset().start(now) else t.start(now)
    }

    fun pause(id: Long) = mutate(id) { t, now -> t.pause(now) }

    fun reset(id: Long) = mutate(id) { t, _ -> t.reset() }

    fun toggle(id: Long) = mutate(id) { t, now ->
        when (t.status) {
            TimerStatus.RUNNING -> t.pause(now)
            TimerStatus.FINISHED -> t.reset().start(now)
            else -> t.start(now)
        }
    }

    fun edit(id: Long, title: String, durationMs: Long, deleteOnFinish: Boolean) =
        mutate(id) { t, _ ->
            t.copy(
                title = title.trim(),
                targetDurationMs = if (t.type == TimerType.TIMER && durationMs > 0) durationMs
                else t.targetDurationMs,
                deleteOnFinish = deleteOnFinish
            )
        }

    fun delete(id: Long) {
        viewModelScope.launch {
            mutex.withLock {
                scheduler.cancel(id)
                dao.deleteById(id)
            }
        }
    }

    private fun mutate(id: Long, transform: (TimerEntity, Long) -> TimerEntity) {
        viewModelScope.launch {
            mutex.withLock {
                val current = dao.getById(id) ?: return@launch
                val now = System.currentTimeMillis()
                val updated = transform(current, now)
                if (updated != current) {
                    dao.update(updated)
                    syncAlarm(updated, now)
                }
            }
        }
    }

    private fun syncAlarm(t: TimerEntity, now: Long) {
        if (t.type == TimerType.TIMER && t.status == TimerStatus.RUNNING) {
            scheduler.schedule(t.id, t.title, now + t.remaining(now).coerceAtLeast(0))
        } else {
            scheduler.cancel(t.id)
        }
    }

    private fun watchFinish() {
        viewModelScope.launch {
            dao.observeRunning().collectLatest { running ->
                val nextMs = running
                    .filter { it.type == TimerType.TIMER }
                    .minOfOrNull { it.remaining() }
                    ?: return@collectLatest
                delay(nextMs.coerceAtLeast(0) + 1)
                finishDue()
            }
        }
    }

    private suspend fun finishDue() = mutex.withLock {
        val now = System.currentTimeMillis()
        dao.getRunning().filter { it.isFinished(now) }.forEach { t ->
            val deleted = t.shouldBeDeleted(now)
            if (deleted) {
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
            eventsChannel.trySend(TimerEvent.Finished(t.id, t.title, deleted))
        }
    }

    private fun ticker(periodMs: Long) = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(periodMs)
        }
    }

    private companion object {
        const val TICK_MS = 250L
    }

    fun updateTitle(id: Long, title: String) {
        viewModelScope.launch {
            mutex.withLock { dao.updateTitle(id, title.trim()) }
        }
    }

    fun pauseTimers() {
        viewModelScope.launch {
            mutex.withLock {
                val ids = dao.getRunning().filter { it.type == TimerType.TIMER }.map { it.id }
                dao.pauseTimers(System.currentTimeMillis())
                ids.forEach { scheduler.cancel(it) }
            }
        }
    }

    fun pauseStopwatches() {
        viewModelScope.launch {
            mutex.withLock { dao.pauseStopwatches(System.currentTimeMillis()) }
        }
    }
}