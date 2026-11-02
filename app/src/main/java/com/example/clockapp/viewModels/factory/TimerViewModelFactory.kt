package com.example.clockapp.viewModels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.clockapp.database.TimerDao
import com.example.clockapp.notificationSystem.scheduler.TimerAlarmScheduler
import com.example.clockapp.viewModels.TimerViewModel

class TimerViewModelFactory(
    private val dao: TimerDao,
    private val scheduler: TimerAlarmScheduler
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
            return TimerViewModel(dao, scheduler) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}