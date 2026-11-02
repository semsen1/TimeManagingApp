package com.example.clockapp.models

import com.example.clockapp.R
import com.example.clockapp.navigation.Routes

enum class NavButtons(val labelResId: Int, val iconResId: Int, val route: String) {
    HOME(R.string.home, R.drawable.home_nav_button, Routes.Home.route),
    ALARMS(R.string.alarms, R.drawable.ic_alarm, Routes.Alarms.route),
    SCHEDULES(R.string.schedules, R.drawable.ic_schedule, Routes.Schedules.route),
    REMINDERS(R.string.reminders, R.drawable.ic_reminder_vector, Routes.Reminders.route),
    TIMER(R.string.timer_stopwatch, R.drawable.hourglasses, Routes.Timer.route);

    //    WIDGETS(R.string.widgets, R.drawable.widget_nav_button, Routes.Widgets.route);
    companion object {
        fun getNavButtons(): List<BottomNavItem> {
            return entries.map {
                BottomNavItem(
                    label = it.labelResId,
                    icon = it.iconResId,
                    route = it.route
                )
            }
        }
    }
}