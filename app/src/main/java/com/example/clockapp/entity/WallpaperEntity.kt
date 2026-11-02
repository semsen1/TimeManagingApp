package com.example.clockapp.entity

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.clockapp.converters.WallpaperConverters


import java.time.ZoneId
enum class BlockType { CLOCK, ALARM, REMINDER, SCHEDULE, TIMER, STOPWATCH }
@TypeConverters(WallpaperConverters::class)
@Entity(tableName = "wallpaper_blocks")
data class WallpaperEntity(
    @PrimaryKey
    val id: String,
    val type: BlockType,
    val isVisible: Boolean,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val zIndex: Int,
    val timeZone: ZoneId? = null
)

object WallpaperBlockDefaults {
    @RequiresApi(Build.VERSION_CODES.O)
    val all = listOf(
        WallpaperEntity("clock_1", BlockType.CLOCK, false, 0.05f, 0.10f, 0.90f, 0.12f, 0),
        WallpaperEntity("clock_2", BlockType.CLOCK, true, 0.05f, 0.23f, 0.90f, 0.08f, 1, ZoneId.of("Europe/London")),
        WallpaperEntity("clock_3", BlockType.CLOCK, true, 0.05f, 0.32f, 0.90f, 0.08f, 2, ZoneId.of("America/New_York")),
        WallpaperEntity("alarm", BlockType.ALARM, true, 0.05f, 0.42f, 0.43f, 0.12f, 3),
        WallpaperEntity("reminder", BlockType.REMINDER, true, 0.52f, 0.42f, 0.43f, 0.12f, 4),
        WallpaperEntity("schedule", BlockType.SCHEDULE, true, 0.05f, 0.56f, 0.90f, 0.12f, 5),
        WallpaperEntity("timer", BlockType.TIMER, true, 0.05f, 0.70f, 0.43f, 0.10f, 6),
        WallpaperEntity("stopwatch", BlockType.STOPWATCH, true, 0.52f, 0.70f, 0.43f, 0.10f, 7)
    )
}