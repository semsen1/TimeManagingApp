package com.example.clockapp.ui.widgets

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.clockapp.R
import com.example.clockapp.database.widgetSettingDao
import com.example.clockapp.ui.theme.WidgetExtendedColors
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.TimeZone

private fun Context.withNightMode(night: Boolean): Context {
    val config = Configuration(resources.configuration)
    val nightFlag = if (night) Configuration.UI_MODE_NIGHT_YES else Configuration.UI_MODE_NIGHT_NO
    config.uiMode = (config.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or nightFlag
    return createConfigurationContext(config)
}

class TimeWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val appWidgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)
        val dao = context.widgetSettingDao
        val defaultZone = TimeZone.getDefault().id

        provideContent {
            val zoneFlow = remember(appWidgetId) {
                dao.observeZone(appWidgetId)
                    .map { it ?: defaultZone }
                    .distinctUntilChanged()
            }
            val zoneId by zoneFlow.collectAsState(initial = defaultZone)

            Clock(zoneId)
        }
    }

    @Composable
    private fun Clock(timeZoneId: String) {
        val context = LocalContext.current
        val cityName = timeZoneId.substringAfterLast('/').replace('_', ' ')

        val dayColor = WidgetExtendedColors.commonContent
            .getColor(context.withNightMode(false)).toArgb()
        val nightColor = WidgetExtendedColors.commonContent
            .getColor(context.withNightMode(true)).toArgb()

        val clockViews = RemoteViews(context.packageName, R.layout.widget_clock).apply {
            setString(R.id.clock_time, "setTimeZone", timeZoneId)
            setString(R.id.clock_date, "setTimeZone", timeZoneId)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                setColorInt(R.id.clock_time, "setTextColor", dayColor, nightColor)
                setColorInt(R.id.clock_date, "setTextColor", dayColor, nightColor)
            } else {
                val current = WidgetExtendedColors.commonContent.getColor(context).toArgb()
                setTextColor(R.id.clock_time, current)
                setTextColor(R.id.clock_date, current)
            }
        }

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(WidgetExtendedColors.background)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = cityName,
                style = TextStyle(color = WidgetExtendedColors.commonContent)
            )
            AndroidRemoteViews(
                remoteViews = clockViews,
                modifier = GlanceModifier.fillMaxWidth()
            )
        }
    }
}