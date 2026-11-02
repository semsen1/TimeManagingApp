package com.example.clockapp.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.clockapp.converters.ScheduleConverters
import com.example.clockapp.entity.AlarmEntity
import com.example.clockapp.entity.ReminderEntity
import com.example.clockapp.entity.ScheduleEntity
import com.example.clockapp.entity.ScreenSaverEntity
import com.example.clockapp.entity.SettingEntity
import com.example.clockapp.entity.TimerEntity
import com.example.clockapp.entity.WallpaperEntity
import com.example.clockapp.entity.WidgetSettingEntity

@TypeConverters(ScheduleConverters::class)
@Database(
    entities = [AlarmEntity::class, ScheduleEntity::class, ReminderEntity::class,
        WidgetSettingEntity::class, TimerEntity::class, ScreenSaverEntity::class,
        WallpaperEntity::class, SettingEntity::class],
    version = 14,
    exportSchema = true
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun alarmDao(): AlarmDao
    abstract fun scheduleDao(): ScheduleDao

    abstract fun reminderDao(): ReminderDao

    abstract fun widgetSettingDao(): WidgetSettingDao

    abstract fun timerDao(): TimerDao

    abstract fun screenSaverDao(): ScreenSaverDao

    abstract fun wallpaperDao(): WallpaperDao

    abstract fun settingDao(): SettingDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "clock.db"
                ).build()
                    .also { instance = it }
            }
        }
    }
}


