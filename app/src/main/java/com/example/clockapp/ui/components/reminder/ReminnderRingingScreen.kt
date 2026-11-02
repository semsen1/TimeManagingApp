package com.example.clockapp.ui.components.reminder

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.clockapp.R
import com.example.clockapp.notificationSystem.core.RingIntents
import com.example.clockapp.notificationSystem.core.Ringable
import com.example.clockapp.notificationSystem.service.RingService
import com.example.clockapp.notificationSystem.ui.AnimatedIcon
import com.example.clockapp.notificationSystem.ui.RingScreenScaffold

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReminderRingScreen(
    target: Ringable,
    onDismiss: () -> Unit,
    onSnooze: () -> Unit,
    modifier: Modifier = Modifier,
    dismissLabel: String = stringResource(R.string.close)
) {
    val context = LocalContext.current
    val muted by RingService.muted.collectAsState()
    RingScreenScaffold(
        snoozeMinutes = target.ringSnoozeMinutes,
        onDismiss = onDismiss,
        onSnooze = onSnooze,
        modifier = modifier,
        dismissLabel = dismissLabel,
        onMuteSound = {
            context.startService(
                Intent(context, RingService::class.java).apply { action = RingIntents.ACTION_MUTE }
            )
        },
        isMuted = muted,
        labelIcon = AnimatedIcon.Reminder,
        soundExists = target.ringSound != null
    )
}
