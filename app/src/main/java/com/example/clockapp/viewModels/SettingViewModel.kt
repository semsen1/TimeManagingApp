package com.example.clockapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.clockapp.database.SettingDao
import com.example.clockapp.entity.SettingEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingViewModel(
    private val settingDao: SettingDao
) : ViewModel() {

    private val settingId = MutableStateFlow<String?>(null)
    @OptIn(ExperimentalCoroutinesApi::class)
    val setting: StateFlow<SettingEntity?> = settingId
        .flatMapLatest { id ->
            if (id == null) flowOf(null) else settingDao.observeById(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun load(id: String) {
        settingId.value = id
    }

    val settings: StateFlow<List<SettingEntity>> = settingDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch { settingDao.syncDefaults() }
    }

    fun setValue(id: String, value: String?) {
        viewModelScope.launch { settingDao.setValue(id, value) }
    }

    fun resetValue(id: String) {
        viewModelScope.launch { settingDao.resetValue(id) }
    }


    companion object {
        fun factory(settingDao: SettingDao): ViewModelProvider.Factory = viewModelFactory {
            initializer { SettingViewModel(settingDao) }
        }
    }
}