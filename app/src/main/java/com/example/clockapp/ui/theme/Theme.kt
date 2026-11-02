package com.example.clockapp.ui.theme

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color


@Immutable
data class ExtendedColors(
    val backgroundColor: Color,
    val backgroundTransparentColor: Color,
    val secondBackgroundColor: Color,
    val buttonsBackground: Color,
    val commonContentColor: Color,
    val secondContentColor: Color,
    val disabledContentColor: Color,
    val disabledBackgroundColor: Color,
    val wallpaperBackgroundColor: Color,
    val wallpaperBlockColor: Color
)
//0F141E
val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        backgroundColor = Color.Unspecified,
        backgroundTransparentColor = Color.Unspecified,
        secondBackgroundColor = Color.Unspecified,
        buttonsBackground = Color.Unspecified,
        commonContentColor = Color.Unspecified,
        secondContentColor = Color.Unspecified,
        disabledContentColor = Color.Unspecified,
        disabledBackgroundColor = Color.Unspecified,
        wallpaperBackgroundColor = Color.Unspecified,
        wallpaperBlockColor = Color.Unspecified
    )
}


internal val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color(0xFF1B1B1B),

    )

internal val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)


internal val LightExtendedColors = ExtendedColors(
    backgroundColor = Color(0xFFcccccc),
    backgroundTransparentColor = Color(0xFFcccccc).copy(alpha = 0.7f),
    secondBackgroundColor = Color(0xFFE6E6E6),
    buttonsBackground = Color(0xFFD0D0D0),
    commonContentColor = Color(0xFF1A1A1A),
    secondContentColor = Color(0xFF4D4D4D),
    disabledContentColor = Color(0xFF8C8C8C),
    disabledBackgroundColor = Color(0xFFDCDCDC),
    wallpaperBackgroundColor = Color(0xFFe5e5e6),
    wallpaperBlockColor = Color(0xFFc9cbcf)
)

internal val DarkExtendedColors = ExtendedColors(
    backgroundColor = Color(0xFF404040),
    backgroundTransparentColor = Color(0xFF404040).copy(alpha = 0.7f),
    secondBackgroundColor = Color(0xFF333333),
    buttonsBackground = Color(0xFF666666),
    commonContentColor = Color.White,
    secondContentColor = Color(0xFFd9d9d9),
    disabledContentColor = Color(0xFFCCCCCC),
    disabledBackgroundColor = Color(0xFF191919),
    wallpaperBackgroundColor = Color(0xFF0F141E),
    wallpaperBlockColor = Color(0xFF232C3C)
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(
        LocalExtendedColors provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = colors,
            typography = Typography,
            content = content
        )
    }
}

fun Context.currentExtendedColors(): ExtendedColors {
    val night = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
            Configuration.UI_MODE_NIGHT_YES
    return if (night) DarkExtendedColors else LightExtendedColors
}