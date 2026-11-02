package com.example.clockapp.viewModels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.clockapp.database.ReminderDao
import com.example.clockapp.notificationSystem.scheduler.RingScheduler
import com.example.clockapp.viewModels.ReminderViewModel

class ReminderViewModelFactory(
    private val dao: ReminderDao,
    private val scheduler: RingScheduler
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
            return ReminderViewModel(dao, scheduler) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}