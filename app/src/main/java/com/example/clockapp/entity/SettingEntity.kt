package com.example.clockapp.entity

import androidx.annotation.StringRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.clockapp.R
import com.example.clockapp.converters.SettingConverter

enum class SettingType { BOOL, STRING, INT }

@TypeConverters(SettingConverter::class)
@Entity(tableName = "settings")
data class SettingEntity(
    @PrimaryKey val id: String,
    @StringRes val label: Int,
    val type: SettingType,
    val value: String? = null,
    val defaultValue: String
) {

    fun getValue(): Any? = parse(value) ?: parse(defaultValue)

    private fun parse(raw: String?): Any? = when {
        raw == null -> null
        else -> when (type) {
            SettingType.BOOL -> raw.toBooleanStrictOrNull()
            SettingType.INT -> raw.toIntOrNull()
            SettingType.STRING -> raw
        }
    }

    companion object {
        const val TIMER_SOUND = "timer_sound"
        const val TICKER_SOUND = "ticker_sound"

        val defaults: List<SettingEntity> = listOf(
            SettingEntity(
                id = TIMER_SOUND,
                label = R.string.setting_timer_sound,
                type = SettingType.BOOL,
                value = null,
                defaultValue = "true"
            ),
            SettingEntity(
                id = TICKER_SOUND,
                label = R.string.ticker_sound,
                type = SettingType.BOOL,
                value = null,
                defaultValue = "true"
            )
        )
    }
}