package com.example.clockapp.navigation

sealed class Routes(val route: String) {

    data object Home : Routes(HOME)
    data object Alarms : Routes(ALARMS)
    data object Schedules : Routes(SCHEDULES)
    data object Reminders : Routes(REMINDERS)
    data object Widgets : Routes(WIDGETS)
    data object Wallpaper : Routes(WALLPAPER)

    data object Timer : Routes(TIMER)

    data object Settings : Routes(SETTINGS)

    companion object {
        private const val HOME = "Home"
        private const val ALARMS = "Alarms"
        private const val SCHEDULES = "Schedules"
        private const val REMINDERS = "Reminders"
        private const val WIDGETS = "Widgets"
        private const val WALLPAPER = "Wallpaper"

        private const val TIMER = "Timer"
        private const val SETTINGS = "Settings"
    }

}