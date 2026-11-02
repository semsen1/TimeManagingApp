package com.example.clockapp.ui.components.schedule

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.clockapp.R
import com.example.clockapp.entity.ScheduleEntity
import com.example.clockapp.ui.modals.ScheduleDayDetails
import com.example.clockapp.ui.theme.LocalExtendedColors
import com.example.clockapp.viewModels.ScheduleViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayScheduleItem(
    day: LocalDate,
    schedules: List<ScheduleEntity>,
    viewModel: ScheduleViewModel,
    modifier: Modifier = Modifier,
) {
    val daySchedules = schedules
        .filter { it.isScheduledOn(day) }
        .sortedWith(
            compareBy<ScheduleEntity> { it.timeStart }
                .thenBy { it.id }
        )

    val visibleSchedules = daySchedules.take(3)
    val hiddenCount = daySchedules.size - visibleSchedules.size
    val openScheduleDayDetailsDialog = remember { mutableStateOf(false) }

    val backgroundColor = LocalExtendedColors.current.backgroundColor
    val commonColor = LocalExtendedColors.current.commonContentColor

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { openScheduleDayDetailsDialog.value =  true }
    ) {
        Text(
            text = day.format(DateTimeFormatter.ofPattern("dd.MM")),
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 8.dp
            ),
            color = commonColor
        )

        HorizontalDivider(
            thickness = 2.dp,
            color = commonColor
        )

        if (visibleSchedules.isEmpty()) {
            Text(
                text = stringResource(R.string.no_schedules),
                modifier = Modifier.padding(12.dp),
                color = commonColor
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                visibleSchedules.forEach { schedule ->
                    ScheduleCard(
                        schedule = schedule,
                        modifier = Modifier.weight(1f),
                        viewModel = viewModel
                    )
                }

                if (hiddenCount > 0) {
                    Text(
                        text = "+$hiddenCount",
                        modifier = Modifier.padding(horizontal = 4.dp),
                        color = commonColor,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
        if(openScheduleDayDetailsDialog.value){
            ScheduleDayDetails(
                onDismiss = { openScheduleDayDetailsDialog.value = false },
                viewModel = viewModel,
                day = day
            )
        }
    }
}

