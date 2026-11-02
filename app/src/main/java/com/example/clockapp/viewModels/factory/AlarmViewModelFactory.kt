package com.example.clockapp.viewModels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.clockapp.database.AlarmDao
import com.example.clockapp.notificationSystem.scheduler.RingScheduler
import com.example.clockapp.viewModels.AlarmViewModel

class AlarmViewModelFactory(
    private val dao: AlarmDao,
    private val scheduler: RingScheduler
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmViewModel::class.java)) {
            return AlarmViewModel(dao, scheduler) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}