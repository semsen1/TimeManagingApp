package com.example.clockapp.converters

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.example.clockapp.models.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import androidx.core.net.toUri

class AlarmConverters {

    @TypeConverter
    fun timeToString(time: LocalTime): String {
        return time.toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun stringToTime(value: String): LocalTime {
        return LocalTime.parse(value)
    }

    @TypeConverter
    fun daysOfWeekToString(days: Set<DayOfWeek>): String {
        return days
            .sortedBy { it.index }
            .joinToString(separator = ",") { it.name }
    }

    @TypeConverter
    fun stringToDaysOfWeek(value: String): Set<DayOfWeek> {
        if (value.isEmpty()) {
            return emptySet()
        }

        return value
            .split(",")
            .map { name -> DayOfWeek.valueOf(name) }
            .toSet()
    }

    @TypeConverter
    fun localDateToString(date: LocalDate): String {
        return date.toString()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun stringToLocalDate(value: String): LocalDate {
        return LocalDate.parse(value)
    }

    @TypeConverter
    fun uriToString(uri: Uri): String = uri.toString()

    @TypeConverter
    fun stringToUri(value: String): Uri = value.toUri()
}