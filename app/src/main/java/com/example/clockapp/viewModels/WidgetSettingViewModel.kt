package com.example.clockapp.viewModels

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clockapp.database.WidgetSettingDao
import com.example.clockapp.entity.WidgetSettingEntity
import com.example.clockapp.ui.widgets.TimeWidget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.TimeZone

data class WidgetItem(val appWidgetId: Int, val timeZoneId: String)

data class WidgetsUiState(
    val items: List<WidgetItem> = emptyList(),
    val editingId: Int? = null,
)

class WidgetSettingViewModel(
    private val widgetSettingDao: WidgetSettingDao,
) : ViewModel() {

    private val placedIds = MutableStateFlow<List<Int>>(emptyList())
    private val editingId = MutableStateFlow<Int?>(null)

    val uiState: StateFlow<WidgetsUiState> = combine(
        placedIds,
        widgetSettingDao.observeAll(),
        editingId,
    ) { ids, settings, editing ->
        val byId = settings.associateBy { it.appWidgetId }
        WidgetsUiState(
            items = ids.map { id ->
                WidgetItem(id, byId[id]?.timeZoneId ?: TimeZone.getDefault().id)
            },
            editingId = editing,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WidgetsUiState())

    fun refreshPlacedWidgets(context: Context) {
        val appContext = context.applicationContext
        viewModelScope.launch {
            val manager = GlanceAppWidgetManager(appContext)
            placedIds.value = manager
                .getGlanceIds(TimeWidget::class.java)
                .map { manager.getAppWidgetId(it) }
        }
    }

    fun startEditing(appWidgetId: Int) {
        editingId.value = appWidgetId
    }

    fun dismissEditing() {
        editingId.value = null
    }

    fun saveTimeZone(
        context: Context,
        appWidgetId: Int,
        timeZoneId: String,
        onDone: () -> Unit = {},
    ) {
        val appContext = context.applicationContext
        viewModelScope.launch {
            widgetSettingDao.upsert(WidgetSettingEntity(appWidgetId, timeZoneId))
            TimeWidget().updateAll(appContext)
            editingId.value = null
            onDone()
        }
    }


}

