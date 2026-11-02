package com.example.clockapp.converters

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.example.clockapp.entity.BlockType
import java.time.ZoneId

class WallpaperConverters {
    @TypeConverter
    fun blockTypeToString(value: BlockType): String = value.name

    @TypeConverter
    fun stringToBlockType(value: String): BlockType = BlockType.valueOf(value)

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun zoneToString(value: ZoneId?): String? = value?.id

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun stringToZone(value: String?): ZoneId? =
        value?.let { runCatching { ZoneId.of(it) }.getOrNull() }
}