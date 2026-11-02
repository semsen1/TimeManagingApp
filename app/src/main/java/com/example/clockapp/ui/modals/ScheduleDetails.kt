package com.example.clockapp.ui.modals

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.clockapp.R
import com.example.clockapp.entity.ScheduleEntity
import com.example.clockapp.models.SelectItem
import com.example.clockapp.ui.theme.LocalExtendedColors
import com.example.clockapp.viewModels.ScheduleViewModel
import java.time.format.DateTimeFormatter

@SuppressLint("WeekBasedYear")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleDetails(
    onDismiss: () -> Unit,
    viewModel: ScheduleViewModel,
    schedule: ScheduleEntity
) {

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

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(20.dp),
                horizontalAlignment = Alignment.Start
            ) {
                if (schedule.title.isNotBlank()) {
                    Text(text = schedule.title)
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row() {
                    Text(
                        text = schedule.timeStart.format(
                            DateTimeFormatter.ofPattern("HH:mm")
                        )
                    )
                    Text(text = " - ")
                    Text(
                        text = schedule.timeEnd
                            ?.format(DateTimeFormatter.ofPattern("HH:mm"))
                            ?.takeUnless { it == "00:00" }
                            ?: stringResource(R.string.end_of_day).lowercase()
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row() {
                    Text(
                        text = schedule.dateStart
                            ?.format(DateTimeFormatter.ofPattern("YYYY-MM-dd"))
                            ?: stringResource(R.string.not_established)
                    )
                    Text(text = " - ")
                    Text(
                        text = schedule.dateEnd
                            ?.format(DateTimeFormatter.ofPattern("YYYY-MM-dd"))
                            ?: stringResource(R.string.not_established)
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                Row() {
                    if (schedule.notify != 0) {
                        Text(text = stringResource(R.string.notify_in_advance) + ": ")
                    }
                    Text(notifications.find { it.value == schedule.notify }?.label.toString())
                }


                Spacer(modifier = Modifier.height(5.dp))

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

                Spacer(modifier = Modifier.height(5.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Button(
                        onClick = { openScheduleEditDialog.value = true },
                        shape = CircleShape,
                        modifier = Modifier,
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = commonColor,
                            containerColor = Color.Transparent
                        )
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.edit_button),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp),
                                tint = commonColor,
                            )
                        }

                    }

                    Button(
                        onClick = {
                            viewModel.deleteSchedule(schedule.id)
                            onDismiss()
                        },
                        shape = CircleShape,
                        modifier = Modifier,
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = commonColor,
                            containerColor = Color.Transparent
                        )
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.delete_button),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp),
                                tint = commonColor,
                            )
                        }

                    }
                }
            }
            if (openScheduleEditDialog.value) {
                SetSchedule(
                    onDismiss = { openScheduleEditDialog.value = false },
                    viewModel = viewModel,
                    schedule = schedule
                )
            }
        }
    }
}

