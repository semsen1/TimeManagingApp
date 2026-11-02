package com.example.clockapp.ui.theme
import androidx.glance.color.ColorProvider
import androidx.glance.material3.ColorProviders
import com.example.clockapp.ui.theme.DarkColorScheme
import com.example.clockapp.ui.theme.DarkExtendedColors
import com.example.clockapp.ui.theme.LightColorScheme
import com.example.clockapp.ui.theme.LightExtendedColors

object WidgetColors {

    val colors = ColorProviders(
        light = LightColorScheme,
        dark = DarkColorScheme
    )
}


object WidgetExtendedColors {
    val background = ColorProvider(
        day = LightExtendedColors.backgroundTransparentColor,
        night = DarkExtendedColors.backgroundTransparentColor
    )
    val secondBackground = ColorProvider(
        day = LightExtendedColors.secondBackgroundColor,
        night = DarkExtendedColors.secondBackgroundColor
    )
    val buttonsBackground = ColorProvider(
        day = LightExtendedColors.buttonsBackground,
        night = DarkExtendedColors.buttonsBackground
    )
    val commonContent = ColorProvider(
        day = LightExtendedColors.commonContentColor,
        night = DarkExtendedColors.commonContentColor
    )
    val secondContent = ColorProvider(
        day = LightExtendedColors.secondContentColor,
        night = DarkExtendedColors.secondContentColor
    )
}