package com.example.clockapp.notificationSystem.timer

import android.content.Context
import com.example.clockapp.database.timerDao
import com.example.clockapp.entity.TimerStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class TimerNotificationController(
    context: Context,
    private val scope: CoroutineScope
) {
    private val appContext = context.applicationContext
    private val publisher = TimerNotificationPublisher(appContext)

    fun start() {
        scope.launch(Dispatchers.Default) {
            appContext.timerDao.observeAll()
                .map { list ->
                    list.filter { it.status == TimerStatus.RUNNING || it.status == TimerStatus.PAUSED }
                }
                .distinctUntilChanged()
                .collect { publisher.sync(it) }
        }
    }
}