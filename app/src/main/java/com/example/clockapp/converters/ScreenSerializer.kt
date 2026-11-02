package com.example.clockapp.converters

import kotlinx.serialization.Serializable

@Serializable
data class ScreenSaverLayout(
    val columns: Int = 2,
    val blocks: List<ClockBlock> = emptyList()
)

@Serializable
data class ClockBlock(
    val id: String,
    val timeZoneId: String,
    val label: String? = null,
    val row: Int = 0,
    val column: Int = 0,
    val rowSpan: Int = 1,
    val columnSpan: Int = 1,
    val is24Hour: Boolean = true,
    val showSeconds: Boolean = false
)