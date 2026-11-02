package com.example.clockapp.viewModels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.clockapp.database.SettingDao
import com.example.clockapp.viewModels.SettingViewModel

class SettingViewModelFactory(
    private val dao: SettingDao,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
            return SettingViewModel(dao) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}