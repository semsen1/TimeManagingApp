package com.example.clockapp.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.clockapp.R
import com.example.clockapp.navigation.Routes
import com.example.clockapp.ui.components.AfterTimeString
import com.example.clockapp.ui.components.BottomNavBar
import com.example.clockapp.ui.components.CommonScreen
import com.example.clockapp.ui.modals.PermissionList
import com.example.clockapp.ui.theme.LocalExtendedColors
import com.example.clockapp.viewModels.AlarmViewModel
import com.example.clockapp.viewModels.ReminderViewModel
import com.example.clockapp.viewModels.ScheduleViewModel
import java.time.format.DateTimeFormatter

@SuppressLint("WeekBasedYear")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Home(
    navController: NavHostController,
    alarmViewModel: AlarmViewModel,
    scheduleViewModel: ScheduleViewModel,
    reminderViewModel: ReminderViewModel,
) {
    val openEd = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    fun checkAllPermissions(): Boolean {
        val notificationsGranted =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

        val exactAlarmsGranted =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                    context.getSystemService(AlarmManager::class.java)
                        .canScheduleExactAlarms()

        val fullScreenIntentGranted =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE ||
                    context.getSystemService(NotificationManager::class.java)
                        .canUseFullScreenIntent()

        val batteryOptimizationGranted =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                    context.getSystemService(PowerManager::class.java)
                        .isIgnoringBatteryOptimizations(context.packageName)

        return notificationsGranted &&
                exactAlarmsGranted &&
                fullScreenIntentGranted &&
                batteryOptimizationGranted
    }

    var allPermissionsGranted by remember(context) {
        mutableStateOf(checkAllPermissions())
    }

    DisposableEffect(lifecycleOwner, context) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                allPermissionsGranted = checkAllPermissions()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val backgroundColor = LocalExtendedColors.current.backgroundColor
    val commonColor = LocalExtendedColors.current.commonContentColor
    val nextAlarm by alarmViewModel.nextAlarm.collectAsStateWithLifecycle()
    val nextSchedule by scheduleViewModel.nextSchedule.collectAsStateWithLifecycle()
    val nextReminder by reminderViewModel.nextReminder.collectAsStateWithLifecycle()
    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController, padding = PaddingValues())
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp)
            ) {

                CommonScreen(R.drawable.home_nav_button, R.string.home)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = { navController.navigate(Routes.Settings.route) }
                    ) {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.ic_settings
                            ),
                            contentDescription = null,
                            tint = commonColor,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp),
            ) {
                if (!allPermissionsGranted) {
                    Button(
                        onClick = { openEd.value = true },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = commonColor,
                            containerColor = backgroundColor
                        ),
                    ) {
                        Text("! " + stringResource(R.string.permissions_required))
                    }
                }
                Row() {
                    IconButton(
                        onClick = { navController.navigate(Routes.Widgets.route) },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.widget_nav_button),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    IconButton(
                        onClick = { navController.navigate(Routes.Wallpaper.route) },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_wallpaper),
                            contentDescription = null,
                            modifier = Modifier.size(35.dp)
                        )
                    }


                }
                Spacer(modifier = Modifier.height(16.dp))
                //nearest alarm start
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = backgroundColor,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 5.dp,
                            top = 5.dp
                        )
                    ) {
                        Text(text = stringResource(R.string.nearest_alarm))
                    }

                    HorizontalDivider(
                        thickness = 2.dp,
                        color = commonColor
                    )
                    Row(
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 5.dp,
                            bottom = 5.dp
                        )
                    ) {
                        nextAlarm?.let { upcoming ->
                            AfterTimeString(upcoming)

                        } ?: Text(stringResource(R.string.no_active_alarms))
                    }

                }

                //nearest alarm end

                //nearest schedule start
                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = backgroundColor,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 5.dp,
                            top = 5.dp
                        )
                    ) {
                        Text(text = stringResource(R.string.nearest_schedule))
                    }

                    HorizontalDivider(
                        thickness = 2.dp,
                        color = commonColor
                    )
                    Column(
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 5.dp,
                            bottom = 5.dp
                        )
                    ) {
                        nextSchedule?.let { upcoming ->
                            AfterTimeString(upcoming)
                            Row() {
                                Text(
                                    text = upcoming.entity.dateStart
                                        ?.format(DateTimeFormatter.ofPattern("YYYY-MM-dd"))
                                        ?: stringResource(R.string.not_established)
                                )
                                Text(text = " - ")
                                Text(
                                    text = upcoming.entity.dateEnd
                                        ?.format(DateTimeFormatter.ofPattern("YYYY-MM-dd"))
                                        ?: stringResource(R.string.not_established)
                                )
                            }
                            Row() {
                                Text(
                                    text = upcoming.entity.timeStart.format(
                                        DateTimeFormatter.ofPattern("HH:mm")
                                    )
                                )
                                Text(text = " - ")
                                Text(
                                    text = upcoming.entity.timeEnd
                                        ?.format(DateTimeFormatter.ofPattern("HH:mm"))
                                        ?.takeUnless { it == "00:00" }
                                        ?: stringResource(R.string.end_of_day).lowercase()
                                )
                            }
                            if (!upcoming.entity.description.isEmpty()) {
                                Row() {
                                    Text(text = upcoming.entity.description)
                                }
                            }
                        } ?: Text(stringResource(R.string.no_active_schedules))
                    }

                }
                //nearest schedule end
                Spacer(modifier = Modifier.height(16.dp))
                //nearest reminder start
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = backgroundColor,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 5.dp,
                            top = 5.dp
                        )
                    ) {
                        Text(text = stringResource(R.string.nearest_reminder))
                    }

                    HorizontalDivider(
                        thickness = 2.dp,
                        color = commonColor
                    )
                    Column(
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 5.dp,
                            bottom = 5.dp
                        )
                    ) {
                        nextReminder?.let { upcoming ->
                            AfterTimeString(upcoming)
                            if (!upcoming.entity.description.isEmpty()) {
                                Row() {
                                    Text(text = upcoming.entity.description)
                                }
                            }
                        } ?: Text(stringResource(R.string.no_active_reminders))
                    }

                }
                //nearest reminder end
            }

        }
        if (openEd.value) {
            PermissionList(
                onDismiss = { openEd.value = false },
                onPermissionsChanged = { granted ->
                    allPermissionsGranted = granted
                }
            )
        }


    }
}


