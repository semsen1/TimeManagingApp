package com.example.clockapp.alarm

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.example.clockapp.database.alarmDao
import com.example.clockapp.notificationSystem.core.RingHandler
import com.example.clockapp.notificationSystem.core.Ringable
import com.example.clockapp.entity.AlarmEntity
import com.example.clockapp.ui.components.alarm.AlarmRingingScreen

class AlarmRingHandler(private val context: Context) : RingHandler {
    private val dao get() = context.alarmDao

    override suspend fun getById(id: Int): AlarmEntity? = dao.getById(id)
    override suspend fun getAll(): List<AlarmEntity> = dao.getAll()
    override suspend fun disable(id: Int) { dao.setEnabled(id, false) }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    override fun Screen(target: Ringable, onDismiss: () -> Unit, onSnooze: () -> Unit) {
        val alarm = target as? AlarmEntity ?: return
        AlarmRingingScreen(alarm, onDismiss, onSnooze)
    }
}