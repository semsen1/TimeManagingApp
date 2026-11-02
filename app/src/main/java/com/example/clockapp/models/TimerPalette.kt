package com.example.clockapp.models

import androidx.annotation.ColorInt
import kotlin.random.Random

object TimerPalette {

    private val colors: List<Int> = listOf(
        0xFFF44336, 0xFFE91E63, 0xFF9C27B0, 0xFF673AB7, 0xFF3F51B5,
        0xFF2196F3, 0xFF03A9F4, 0xFF00BCD4, 0xFF009688, 0xFF4CAF50,
        0xFF8BC34A, 0xFFCDDC39, 0xFFFFEB3B, 0xFFFFC107, 0xFFFF9800,
        0xFFFF5722, 0xFFFF4081, 0xFF7C4DFF, 0xFF00E5FF, 0xFF76FF03
    ).map { it.toInt() }

    @ColorInt
    fun random(): Int = colors[Random.nextInt(colors.size)]
}