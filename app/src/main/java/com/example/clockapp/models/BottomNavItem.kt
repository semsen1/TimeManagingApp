package com.example.clockapp.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.painter.Painter

data class BottomNavItem(
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
    val route: String

)
