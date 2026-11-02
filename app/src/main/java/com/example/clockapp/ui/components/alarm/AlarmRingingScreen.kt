package com.example.clockapp.ui.components.alarm

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clockapp.entity.AlarmEntity
import com.example.clockapp.notificationSystem.core.RingIntents
import com.example.clockapp.notificationSystem.service.RingService
import com.example.clockapp.notificationSystem.ui.AnimatedIcon
import com.example.clockapp.notificationSystem.ui.RingScreenScaffold

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AlarmRingingScreen(
    alarm: AlarmEntity,
    onDismiss: () -> Unit,
    onSnooze: () -> Unit,
    modifier: Modifier = Modifier
) {
    val testStatus = remember {
        mutableStateOf(
            if (alarm.wakeUpTest == 0) {
                true
            } else {
                false
            }
        )
    }
    val testExists: Boolean = if (alarm.wakeUpTest == 0) {
        false
    } else {
        true
    }
    val context = LocalContext.current
    val muted by RingService.muted.collectAsState()
    RingScreenScaffold(
        snoozeMinutes = alarm.replay,
        onDismiss = onDismiss,
        onSnooze = onSnooze,
        modifier = modifier,
        onMuteSound = {
            context.startService(
                Intent(context, RingService::class.java).apply { action = RingIntents.ACTION_MUTE }
            )
        },
        isMuted = muted,
        testStatus = testStatus.value,
        testExists = testExists,
        labelIcon = AnimatedIcon.Alarm,
        soundExists = alarm.sound != null
    ) {
        if (!alarm.title.isEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = alarm.title,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        WakeUpTest(alarm.wakeUpTest) { testStatus.value = true }
    }
}


