package com.example.clockapp.converters

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json


class ScreenSaverConverters {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    @TypeConverter
    fun layoutToString(layout: ScreenSaverLayout): String = json.encodeToString(layout)

    @TypeConverter
    fun stringToLayout(value: String): ScreenSaverLayout = json.decodeFromString(value)
}