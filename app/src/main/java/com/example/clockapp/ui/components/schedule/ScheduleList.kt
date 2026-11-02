package com.example.clockapp.ui.components.schedule

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.clockapp.entity.ScheduleEntity
import com.example.clockapp.viewModels.ScheduleViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleList(
    schedules: List<ScheduleEntity>,
    modifier: Modifier = Modifier,
    viewModel: ScheduleViewModel,
    currentWeek: Boolean = false
) {
    val today = LocalDate.now()

    val daysRange = if (!currentWeek) {
        (0 until 5).map { today.plusDays(it.toLong()) }
    } else {
        val startOfWeek = today.with(
            TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
        )

        (0 until 7).map { startOfWeek.plusDays(it.toLong()) }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 5.dp)
        ) {
            daysRange.forEach { day ->
                DayScheduleItem(
                    day = day,
                    schedules = schedules,
                    viewModel = viewModel
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}