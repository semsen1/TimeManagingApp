package com.example.clockapp.viewModels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.clockapp.database.WidgetSettingDao
import com.example.clockapp.viewModels.WidgetSettingViewModel

class WidgetSettingViewModelFactory(
    private val dao: WidgetSettingDao,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WidgetSettingViewModel::class.java)) {
            return WidgetSettingViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}