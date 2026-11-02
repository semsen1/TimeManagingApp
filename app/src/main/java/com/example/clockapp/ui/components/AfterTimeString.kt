package com.example.clockapp.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.clockapp.R
import com.example.clockapp.viewModels.UpcomingEntity

@Composable
fun AfterTimeString(upcoming: UpcomingEntity<*>) {
    val d = upcoming.minutesLeft / 60 / 24
    val h = upcoming.minutesLeft / 60 % 24
    val m = upcoming.minutesLeft % 60

    val entityTitle = upcoming.entity.ringTitle

    val title =
        (if (upcoming.entity.ringTitle.isNotEmpty()) "$entityTitle : " else "") + stringResource(
            R.string.after_title
        )
    val days = if (d > 0) stringResource(R.string.after_days, d) else null
    val hours = if (h > 0) stringResource(R.string.after_hours, h) else null
    val minutes = if (m > 0) stringResource(R.string.after_minutes, m) else null

    Text(text = listOfNotNull(title, days, hours, minutes).joinToString(" "))
}