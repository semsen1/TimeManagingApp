package com.example.clockapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "widget_settings")
data class WidgetSettingEntity(
    @PrimaryKey val appWidgetId: Int,
    val timeZoneId: String
)