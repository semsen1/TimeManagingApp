package com.example.clockapp.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.clockapp.database.AppDatabase
import com.example.clockapp.notificationSystem.scheduler.AndroidTimerAlarmScheduler
import com.example.clockapp.notificationSystem.scheduler.RingScheduler
import com.example.clockapp.ui.screens.Alarms
import com.example.clockapp.ui.screens.Home
import com.example.clockapp.ui.screens.Reminders
import com.example.clockapp.ui.screens.Schedules
import com.example.clockapp.ui.screens.Settings
import com.example.clockapp.ui.screens.Timer
import com.example.clockapp.ui.screens.WallpaperSettings
import com.example.clockapp.ui.screens.Widgets
import com.example.clockapp.viewModels.AlarmViewModel
import com.example.clockapp.viewModels.ReminderViewModel
import com.example.clockapp.viewModels.ScheduleViewModel
import com.example.clockapp.viewModels.SettingViewModel
import com.example.clockapp.viewModels.TimerViewModel
import com.example.clockapp.viewModels.WallpaperViewModel
import com.example.clockapp.viewModels.WidgetSettingViewModel
import com.example.clockapp.viewModels.factory.AlarmViewModelFactory
import com.example.clockapp.viewModels.factory.ReminderViewModelFactory
import com.example.clockapp.viewModels.factory.ScheduleViewModelFactory
import com.example.clockapp.viewModels.factory.SettingViewModelFactory
import com.example.clockapp.viewModels.factory.TimerViewModelFactory
import com.example.clockapp.viewModels.factory.WallpaperViewModelFactory
import com.example.clockapp.viewModels.factory.WidgetSettingViewModelFactory

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(
    navHostController: NavHostController
) {

    val context = LocalContext.current
    val applicationContext = context.applicationContext

    val database = remember(applicationContext) {
        AppDatabase.getInstance(applicationContext)
    }

    val alarmFactory = remember(database) {
        AlarmViewModelFactory(database.alarmDao(), scheduler = RingScheduler(context))
    }

    val scheduleFactory = remember(database) {
        ScheduleViewModelFactory(database.scheduleDao(), scheduler = RingScheduler(context))
    }

    val reminderFactory = remember(database) {
        ReminderViewModelFactory(database.reminderDao(), scheduler = RingScheduler(context))
    }

    val widgetSettingFactory = remember(database) {
        WidgetSettingViewModelFactory(database.widgetSettingDao())
    }

    val timerFactory = remember(database) {
        TimerViewModelFactory(database.timerDao(), scheduler = AndroidTimerAlarmScheduler(context))
    }

    val wallpaperFactory = remember(database) {
        WallpaperViewModelFactory(database.wallpaperDao())
    }

    val settingFactory = remember(database) {
        SettingViewModelFactory(database.settingDao())
    }


    NavHost(
        navController = navHostController,
        startDestination = Routes.Home.route
    ) {
        composable(Routes.Home.route) { backStackEntry ->
            val alarmViewModel: AlarmViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = alarmFactory
            )
            val scheduleViewModel: ScheduleViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = scheduleFactory
            )

            val reminderViewModel: ReminderViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = reminderFactory
            )


            Home(
                navHostController,
                alarmViewModel,
                scheduleViewModel,
                reminderViewModel,
            )
        }

        composable(Routes.Alarms.route) { backStackEntry ->
            val alarmViewModel: AlarmViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = alarmFactory
            )

            Alarms(
                navController = navHostController,
                viewModel = alarmViewModel
            )
        }

        composable(Routes.Schedules.route) { backStackEntry ->
            val scheduleViewModel: ScheduleViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = scheduleFactory
            )
            Schedules(
                navController = navHostController,
                viewModel = scheduleViewModel
            )
        }

        composable(Routes.Reminders.route) { backStackEntry ->
            val reminderViewModel: ReminderViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = reminderFactory
            )
            Reminders(navHostController, reminderViewModel)
        }

        composable(Routes.Widgets.route) { backStackEntry ->
            val widgetSettingViewModel: WidgetSettingViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = widgetSettingFactory
            )
            Widgets(navHostController, widgetSettingViewModel)
        }

        composable(Routes.Wallpaper.route) { backStackEntry ->
            val wallpaperViewModel: WallpaperViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = wallpaperFactory
            )
            WallpaperSettings(navHostController, wallpaperViewModel)
        }

        composable(Routes.Timer.route) { backStackEntry ->
            val timerViewModel: TimerViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = timerFactory
            )
            Timer(navHostController, timerViewModel)
        }

        composable(Routes.Settings.route) { backStackEntry ->
            val settingViewModel: SettingViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = settingFactory
            )

            Settings(navHostController, settingViewModel)
        }
    }
}