package com.example.clockapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.clockapp.converters.ScreenSaverConverters
import com.example.clockapp.converters.ScreenSaverLayout

@TypeConverters(ScreenSaverConverters::class)
@Entity(tableName = "screen_savers")
data class ScreenSaverEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val isActive: Boolean = false,
    val layout: ScreenSaverLayout = ScreenSaverLayout(),
    val updatedAt: Long = System.currentTimeMillis()
)