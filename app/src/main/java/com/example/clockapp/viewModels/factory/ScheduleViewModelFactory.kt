package com.example.clockapp.viewModels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.clockapp.database.ScheduleDao
import com.example.clockapp.notificationSystem.scheduler.RingScheduler
import com.example.clockapp.viewModels.ScheduleViewModel

class ScheduleViewModelFactory(
    private val dao: ScheduleDao,
    private val scheduler: RingScheduler
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScheduleViewModel::class.java)) {
            return ScheduleViewModel(dao, scheduler) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}