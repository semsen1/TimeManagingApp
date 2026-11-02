package com.example.clockapp.converters

import androidx.room.TypeConverter
import com.example.clockapp.entity.TimerStatus
import com.example.clockapp.entity.TimerType

class TimerConverter {

    @TypeConverter
    fun fromType(value: TimerType): String = value.name

    @TypeConverter
    fun toType(value: String): TimerType =
        runCatching { enumValueOf<TimerType>(value) }.getOrDefault(TimerType.STOPWATCH)

    @TypeConverter
    fun fromStatus(value: TimerStatus): String = value.name

    @TypeConverter
    fun toStatus(value: String): TimerStatus =
        runCatching { enumValueOf<TimerStatus>(value) }.getOrDefault(TimerStatus.IDLE)
}