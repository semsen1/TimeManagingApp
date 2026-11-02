package com.example.clockapp.database

import android.content.Context


val Context.alarmDao: AlarmDao
    get() = AppDatabase
        .getInstance(applicationContext)
        .alarmDao()

val Context.reminderDao: ReminderDao
    get() = AppDatabase
        .getInstance(applicationContext)
        .reminderDao()

val Context.scheduleDao: ScheduleDao
    get() = AppDatabase
        .getInstance(applicationContext)
        .scheduleDao()

val Context.widgetSettingDao: WidgetSettingDao
    get() = AppDatabase
        .getInstance(applicationContext)
        .widgetSettingDao()

val Context.timerDao: TimerDao
    get() = AppDatabase
        .getInstance(applicationContext)
        .timerDao()

val Context.screenSaverDao: ScreenSaverDao
    get() = AppDatabase
        .getInstance(applicationContext)
        .screenSaverDao()

val Context.wallpaperDao: WallpaperDao
    get() = AppDatabase
        .getInstance(applicationContext)
        .wallpaperDao()