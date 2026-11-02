package com.example.clockapp.ui.modals

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.clockapp.R
import com.example.clockapp.models.SelectItem
import com.example.clockapp.ui.theme.LocalExtendedColors
import com.example.clockapp.viewModels.ScheduleViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@SuppressLint("WeekBasedYear")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleDayDetails(
    onDismiss: () -> Unit,
    viewModel: ScheduleViewModel,
    day: LocalDate
) {

    LaunchedEffect(day, viewModel) {
        viewModel.selectDate(day)
    }

    val backgroundColor = LocalExtendedColors.current.backgroundColor
    val commonColor = LocalExtendedColors.current.commonContentColor

    val openScheduleEditDialog = remember { mutableStateOf(false) }

    val notifications = listOf<SelectItem>(
        SelectItem(label = stringResource(id = R.string.not_notify), value = 0),
        SelectItem(label = stringResource(id = R.string.one_minute), value = 1),
        SelectItem(label = stringResource(id = R.string.five_minutes), value = 5),
        SelectItem(label = stringResource(id = R.string.ten_minutes), value = 10),
        SelectItem(label = stringResource(id = R.string.thirty_minutes), value = 30),
        SelectItem(label = stringResource(id = R.string.one_hour), value = 60),
    )

    val schedules by viewModel.schedulesByDate.collectAsStateWithLifecycle()

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 10.dp)
                    ) {
                        Text(
                            text = day.format(DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy")),
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = stringResource(R.string.tasks)
                                    + ": " + schedules.count().toString(),
                        )
                    }


                }
                HorizontalDivider(
                    thickness = 2.dp,
                    color = commonColor
                )

                LazyColumn(
                    modifier = Modifier
                        .padding(10.dp)
                ) {
                    items(schedules) { schedule ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(backgroundColor)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = schedule.title,
                                fontSize = 16.sp,
                                overflow = TextOverflow.Ellipsis
                            )

                            val start =
                                schedule.timeStart.format(DateTimeFormatter.ofPattern("HH:mm"))
                            val end = schedule.timeEnd
                                ?.format(DateTimeFormatter.ofPattern("HH:mm"))
                                ?.takeUnless { it == "00:00" }
                                ?: stringResource(R.string.end_of_day)


                            Text(
                                text = "$start — $end",
                                fontSize = 14.sp,
                                overflow = TextOverflow.Ellipsis
                            )

                            val repeatText = when {
                                schedule.daysOfWeek.size == 7 ->
                                    stringResource(R.string.every_day)

                                else -> schedule.daysOfWeek.joinToString(", ") { day ->
                                    ""
                                }
                            }

                            val daysLabels = schedule.daysOfWeek.map { day ->
                                stringResource(day.labelResId)
                            }

                            Spacer(modifier = Modifier.height(5.dp))

                            Text(
                                text = if (schedule.daysOfWeek.size == 7) {
                                    repeatText
                                } else {
                                    daysLabels.joinToString(", ")
                                },
                                color = Color.White,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(5.dp))

                            if (schedule.description.isNotBlank()) {
                                Text(text = schedule.description)
                            }

                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

            }
        }
    }
}