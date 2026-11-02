package com.example.clockapp.converters

import androidx.room.TypeConverter
import com.example.clockapp.entity.SettingType

class SettingConverter {
    @TypeConverter
    fun settingTypeToString(type: SettingType): String = type.name

    @TypeConverter
    fun stringToSettingType(name: String): SettingType =
        SettingType.entries.firstOrNull { it.name == name } ?: SettingType.STRING
}