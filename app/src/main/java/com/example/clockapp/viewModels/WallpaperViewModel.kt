package com.example.clockapp.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clockapp.database.WallpaperDao
import com.example.clockapp.entity.WallpaperBlockDefaults
import com.example.clockapp.entity.WallpaperEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
class WallpaperViewModel(
    private val dao: WallpaperDao
) : ViewModel() {

    val blocks: StateFlow<List<WallpaperEntity>> = dao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch { dao.insertDefaults(WallpaperBlockDefaults.all) }
    }

    fun setVisible(id: String, visible: Boolean) {
        viewModelScope.launch { dao.setVisible(id, visible) }
    }

    fun setLayout(id: String, x: Float, y: Float, width: Float, height: Float) {
        val w = width.coerceIn(MIN_WIDTH, 1f)
        val h = height.coerceIn(MIN_HEIGHT, 1f)
        viewModelScope.launch {
            dao.updateLayout(id, x.coerceIn(0f, 1f - w), y.coerceIn(0f, 1f - h), w, h)
        }
    }

    fun setTimeZone(id: String, zone: ZoneId?) {
        viewModelScope.launch { dao.setTimeZone(id, zone?.id) }
    }

    fun resetToDefaults() {
        viewModelScope.launch { dao.resetAll(WallpaperBlockDefaults.all) }
    }

    private companion object {
        const val MIN_WIDTH = 0.2f
        const val MIN_HEIGHT = 0.06f
    }
}