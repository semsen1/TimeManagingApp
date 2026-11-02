package com.example.clockapp.reminder

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.clockapp.R
import com.example.clockapp.database.reminderDao
import com.example.clockapp.entity.ReminderEntity
import com.example.clockapp.notificationSystem.core.RingHandler
import com.example.clockapp.notificationSystem.core.Ringable
import com.example.clockapp.ui.components.reminder.ReminderRingScreen

class ReminderRingHandler(private val context: Context) : RingHandler {
    private val dao get() = context.reminderDao

    override suspend fun getById(id: Int): ReminderEntity? = dao.getById(id)
    override suspend fun getAll(): List<ReminderEntity> = dao.getAll()
    override suspend fun disable(id: Int) {
        dao.setEnabled(id, false)
    }


    override suspend fun onDismissed(target: Ringable) {
        val r = target as? ReminderEntity ?: return
        if (r.isOneShot && r.isAutoConfirm) dao.setConfirmed(r.id, true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    override fun Screen(target: Ringable, onDismiss: () -> Unit, onSnooze: () -> Unit) {
        ReminderRingScreen(
            target, onDismiss, onSnooze, dismissLabel = stringResource(R.string.cancel),
        )
    }
}