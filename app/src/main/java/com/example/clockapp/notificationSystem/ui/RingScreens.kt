package com.example.clockapp.notificationSystem.ui

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.clockapp.R
import com.example.clockapp.notificationSystem.core.RingIntents
import com.example.clockapp.notificationSystem.core.Ringable
import com.example.clockapp.notificationSystem.service.RingService
import com.example.clockapp.ui.components.icons.AlarmIcon
import com.example.clockapp.ui.components.icons.ReminderIcon
import com.example.clockapp.ui.components.icons.ScheduleIcon
import com.example.clockapp.ui.theme.AppTheme
import com.example.clockapp.ui.theme.LocalExtendedColors

enum class AnimatedIcon { Alarm, Schedule, Reminder }

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RingScreenScaffold(
    snoozeMinutes: Int,
    onDismiss: () -> Unit,
    onSnooze: () -> Unit,
    modifier: Modifier = Modifier,
    dismissLabel: String = stringResource(R.string.close),
    onMuteSound: (() -> Unit)? = null,
    isMuted: Boolean = false,
    testStatus: Boolean = true,
    testExists: Boolean = false,
    labelIcon: AnimatedIcon? = null,
    soundExists: Boolean = true,
    content: (@Composable ColumnScope.() -> Unit)? = null,
) {
    var soundMuted by rememberSaveable { mutableStateOf(false) }
    if (testExists && testStatus) {
        onMuteSound?.invoke()
    }

    AppTheme {
        val backgroundColor = LocalExtendedColors.current.backgroundColor
        val commonColor = LocalExtendedColors.current.commonContentColor
        Surface(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (labelIcon != null) {
                    when (labelIcon) {
                        AnimatedIcon.Alarm -> AlarmIcon(modifier = Modifier.size(60.dp))
                        AnimatedIcon.Schedule -> ScheduleIcon(modifier = Modifier.size(60.dp))
                        AnimatedIcon.Reminder -> ReminderIcon(modifier = Modifier.size(60.dp))
                    }
                }

                Spacer(Modifier.height(if (soundMuted) 32.dp else 12.dp))


                if (testStatus) {
                    Button(
                        onClick = onDismiss, modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = commonColor,
                            containerColor = backgroundColor
                        ),
                    ) {
                        Text(dismissLabel)
                    }
                }


                if (onMuteSound != null && !isMuted && soundExists) {
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(onClick = onMuteSound, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.mute_sound))
                    }
                }

                if (testExists && !testStatus) {
                    if (snoozeMinutes > 0) {
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(onClick = onSnooze, modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(R.string.postpone_for, snoozeMinutes))
                        }
                    }
                }


                content?.invoke(this)
            }
        }
    }
}

//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun DefaultRingScreen(
//    target: Ringable,
//    onDismiss: () -> Unit,
//    onSnooze: () -> Unit,
//    modifier: Modifier = Modifier,
//    dismissLabel: String = stringResource(R.string.close)
//) {
//    val context = LocalContext.current
//    val muted by RingService.muted.collectAsState()
//    RingScreenScaffold(
//        snoozeMinutes = target.ringSnoozeMinutes,
//        onDismiss = onDismiss,
//        onSnooze = onSnooze,
//        modifier = modifier,
//        dismissLabel = dismissLabel,
//        onMuteSound = {
//            context.startService(
//                Intent(context, RingService::class.java).apply { action = RingIntents.ACTION_MUTE }
//            )
//        },
//        isMuted = muted,
//        labelIcon = null
//    )
//}