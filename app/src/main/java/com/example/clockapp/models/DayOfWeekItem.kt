package com.example.clockapp.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class DayOfWeekItem(
    @StringRes val label: Int,
    val index: Int
)
