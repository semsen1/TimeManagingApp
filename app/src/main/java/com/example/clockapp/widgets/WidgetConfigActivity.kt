package com.example.clockapp.widgets

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.clockapp.database.widgetSettingDao
import com.example.clockapp.ui.components.fields.TimeZonePicker
import com.example.clockapp.ui.theme.AppTheme
import com.example.clockapp.viewModels.WidgetSettingViewModel

class WidgetConfigActivity : ComponentActivity() {

    private val viewModel: WidgetSettingViewModel by viewModels {
        viewModelFactory {
            initializer { WidgetSettingViewModel(applicationContext.widgetSettingDao) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val appWidgetId = intent?.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_CANCELED, resultValue)
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContent {
            AppTheme {
                TimeZonePicker(
                    onSelected = { zoneId ->
                        viewModel.saveTimeZone(applicationContext, appWidgetId, zoneId) {
                            setResult(
                                Activity.RESULT_OK,
                                Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                            )
                            finish()
                        }
                    },
                    onDismiss = {
                        setResult(
                            Activity.RESULT_CANCELED,
                            Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                        )
                        finish()
                    }
                )
            }
        }
    }
}