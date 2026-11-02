package com.example.clockapp.models

import com.example.clockapp.R

enum class DayOfWeek(val labelResId: Int, val index: Int) {
    MONDAY(R.string.mo, 0),
    TUESDAY(R.string.tu, 1),
    WEDNESDAY(R.string.we, 2),
    THURSDAY(R.string.th, 3),
    FRIDAY(R.string.fr, 4),
    SATURDAY(R.string.sa, 5),
    SUNDAY(R.string.su, 6);
    companion object {
        fun getDaysOfWeek(): List<DayOfWeekItem> {
            return entries.map { DayOfWeekItem(label = it.labelResId, index = it.index) }
        }
    }
}