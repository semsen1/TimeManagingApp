package com.example.clockapp.ui.components.schedule

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.clockapp.R
import com.example.clockapp.entity.ScheduleEntity
import com.example.clockapp.ui.modals.ScheduleDetails
import com.example.clockapp.viewModels.ScheduleViewModel
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleCard(
    schedule: ScheduleEntity,
    modifier: Modifier = Modifier,
    viewModel: ScheduleViewModel
) {

    val openScheduleDetailsDialog = remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .combinedClickable(
                onClick = {
                    openScheduleDetailsDialog.value = true
                },
                onLongClick = {

                }
            ),


        ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = schedule.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            val start = schedule.timeStart.format(DateTimeFormatter.ofPattern("HH:mm"))
            val end = schedule.timeEnd
                ?.format(DateTimeFormatter.ofPattern("HH:mm"))
                ?.takeUnless { it == "00:00" }
                ?: stringResource(R.string.end_of_day)


            Text(
                text = "$start — $end",
                style = MaterialTheme.typography.labelSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
    if (openScheduleDetailsDialog.value) {
        ScheduleDetails(
            onDismiss = { openScheduleDetailsDialog.value = false },
            viewModel = viewModel,
            schedule = schedule
        )
    }

}