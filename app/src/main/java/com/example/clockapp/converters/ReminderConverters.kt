package com.example.clockapp.converters

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.example.clockapp.models.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import androidx.core.net.toUri

class ReminderConverters {

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