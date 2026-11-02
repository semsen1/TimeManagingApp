package com.example.clockapp.ui.modals

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.clockapp.R
import com.example.clockapp.ui.theme.LocalExtendedColors

private data class AlarmPermissionsState(
    val notifications: Boolean,
    val exactAlarms: Boolean,
    val fullScreenIntent: Boolean,
    val batteryOptimization: Boolean
) {
    val allGranted: Boolean
        get() = notifications &&
                exactAlarms &&
                fullScreenIntent &&
                batteryOptimization
}

private fun Context.readAlarmPermissions(): AlarmPermissionsState {
    val notifications =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

    val exactAlarms =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSystemService(AlarmManager::class.java)
                .canScheduleExactAlarms()
        } else {
            true
        }

    val fullScreenIntent =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            getSystemService(NotificationManager::class.java)
                .canUseFullScreenIntent()
        } else {
            true
        }

    val batteryOptimization =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getSystemService(PowerManager::class.java)
                .isIgnoringBatteryOptimizations(packageName)
        } else {
            true
        }

    return AlarmPermissionsState(
        notifications = notifications,
        exactAlarms = exactAlarms,
        fullScreenIntent = fullScreenIntent,
        batteryOptimization = batteryOptimization
    )
}

@Composable
fun PermissionList(
    onDismiss: () -> Unit,
    onPermissionsChanged: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val backgroundColor = LocalExtendedColors.current.backgroundColor
    val commonColor = LocalExtendedColors.current.commonContentColor

    var permissions by remember(context) {
        mutableStateOf(context.readAlarmPermissions())
    }

    val currentOnPermissionsChanged by rememberUpdatedState(
        onPermissionsChanged
    )

    val allPermissionsGranted = permissions.allGranted

    LaunchedEffect(allPermissionsGranted) {
        currentOnPermissionsChanged(allPermissionsGranted)
    }

    DisposableEffect(lifecycleOwner, context) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                permissions = context.readAlarmPermissions()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val notificationsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        permissions = context.readAlarmPermissions()
    }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.permissions))

                if (!permissions.notifications) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = commonColor,
                            containerColor = backgroundColor
                        ),
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationsLauncher.launch(
                                    Manifest.permission.POST_NOTIFICATIONS
                                )
                            }
                        }
                    ) {
                        Text(stringResource(R.string.allow_notifications))
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                }

                if (!permissions.exactAlarms) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = commonColor,
                            containerColor = backgroundColor
                        ),
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                context.startActivity(
                                    Intent(
                                        Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                                        "package:${context.packageName}".toUri()
                                    )
                                )
                            }
                        }
                    ) {

                        Text(stringResource(R.string.allow_accurate_alarm_clocks))
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                }

                if (!permissions.fullScreenIntent) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = commonColor,
                            containerColor = backgroundColor
                        ),
                        onClick = {
                            if (
                                Build.VERSION.SDK_INT >=
                                Build.VERSION_CODES.UPSIDE_DOWN_CAKE
                            ) {
                                context.startActivity(
                                    Intent(
                                        Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT,
                                        "package:${context.packageName}".toUri()
                                    )
                                )
                            }
                        }
                    ) {
                        Text(stringResource(R.string.allow_full_screen_alarm))
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                }

                if (!permissions.batteryOptimization) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = commonColor,
                            containerColor = backgroundColor
                        ),
                        onClick = {
                            context.startActivity(
                                Intent(
                                    Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
                                )
                            )
                        }
                    ) {
                        Text(stringResource(R.string.disable_battery_optimization))
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                }

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (permissions.notifications) {
                        Text(stringResource(R.string.notification_permission_received))
                        Spacer(modifier = Modifier.height(5.dp))
                    }

                    if (permissions.exactAlarms) {
                        Text(stringResource(R.string.accurate_alarm_permission_received))
                        Spacer(modifier = Modifier.height(5.dp))
                    }

                    if (permissions.fullScreenIntent) {
                        Text(stringResource(R.string.full_screen_alarm_permission_received))
                        Spacer(modifier = Modifier.height(5.dp))
                    }

                    if (permissions.batteryOptimization) {
                        Text(stringResource(R.string.battery_optimization_disabled))
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                }


                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        contentColor = commonColor,
                        containerColor = backgroundColor
                    ),
                ) {
                    Text(stringResource(R.string.close))
                }
            }
        }
    }
}

