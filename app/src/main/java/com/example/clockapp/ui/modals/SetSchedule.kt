package com.example.clockapp.ui.modals

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow.Companion.Clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.clockapp.R
import com.example.clockapp.entity.ScheduleEntity
import com.example.clockapp.models.DayOfWeek
import com.example.clockapp.models.SelectItem
import com.example.clockapp.ui.components.fields.CustomTextField
import com.example.clockapp.ui.components.fields.SelectField
import com.example.clockapp.ui.components.fields.SoundSelector
import com.example.clockapp.ui.components.fields.VerticalTimePicker
import com.example.clockapp.ui.theme.LocalExtendedColors
import com.example.clockapp.viewModels.ScheduleViewModel
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetSchedule(
    onDismiss: () -> Unit,
    viewModel: ScheduleViewModel,
    schedule: ScheduleEntity? = null
) {

    val backgroundColor = LocalExtendedColors.current.backgroundColor
    val commonColor = LocalExtendedColors.current.commonContentColor

    var scheduleName by remember { mutableStateOf(schedule?.title ?: "") }

    val daysOfWeek = schedule?.daysOfWeek

    val checkedDays = remember(daysOfWeek) {
        mutableStateListOf<Boolean>().apply {
            addAll(
                DayOfWeek.entries
                    .sortedBy { it.index }
                    .map { day ->
                        daysOfWeek?.contains(day) ?: true
                    }
            )
        }
    }

    val allDaysChecked by remember {
        derivedStateOf { checkedDays.all { it } }
    }

    var selectedHourStart by remember { mutableStateOf(schedule?.timeStart?.hour ?: 0) }
    var selectedMinuteStart by remember { mutableStateOf(schedule?.timeStart?.minute ?: 0) }

    var selectedHourEnd by remember { mutableStateOf(schedule?.timeEnd?.hour ?: 0) }
    var selectedMinuteEnd by remember { mutableStateOf(schedule?.timeEnd?.minute ?: 0) }

    val notifications = listOf<SelectItem>(
        SelectItem(label = stringResource(id = R.string.not_notify), value = 0),
        SelectItem(label = stringResource(id = R.string.one_minute), value = 1),
        SelectItem(label = stringResource(id = R.string.five_minutes), value = 5),
        SelectItem(label = stringResource(id = R.string.ten_minutes), value = 10),
        SelectItem(label = stringResource(id = R.string.thirty_minutes), value = 30),
        SelectItem(label = stringResource(id = R.string.one_hour), value = 60),
    )

    val selectedNotification =
        remember {
            mutableStateOf(notifications.find { it.value == schedule?.notify }
                ?: notifications.first())
        }


    val dayOfWeek = DayOfWeek.getDaysOfWeek()

    var selectedDateStart by remember {
        mutableStateOf(schedule?.dateStart)
    }

    var selectedDateEnd by remember {
        mutableStateOf(schedule?.dateEnd)
    }

    var selectedSound by remember { mutableStateOf<Uri?>(schedule?.sound) }

    var expandedDescription by remember { mutableStateOf(false) }
    var text by rememberSaveable { mutableStateOf(schedule?.description ?: "") }


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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (schedule == null) {
                        stringResource(id = R.string.add_schedule)
                    } else {
                        stringResource(id = R.string.update_schedule)
                    },
                    fontSize = 22.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                CustomTextField(
                    text = scheduleName,
                    onInputChanged = { scheduleName = it },
                    label = {
                        Text(
                            text = stringResource(id = R.string.title),
                            color = commonColor
                        )
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))


                Box() {
                    Button(
                        onClick = { expandedDescription = !expandedDescription },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = commonColor,
                            containerColor = backgroundColor
                        )
                    )

                    {
                        Text(
                            if (text.isEmpty()) {
                                stringResource(id = R.string.add_description)
                            } else {
                                stringResource(id = R.string.change_description)
                            }

                        )
                    }
                    TextareaDropdown(
                        expanded = expandedDescription,
                        value = text,
                        onValueChange = { text = it },
                        onDismissRequest = { expandedDescription = false },
                        onConfirm = {
                            expandedDescription = false
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.start),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row {
                            VerticalTimePicker(
                                title = stringResource(id = R.string.hours),
                                minValue = 0,
                                maxValue = 23,
                                selectedValue = selectedHourStart,
                                onValueChange = { selectedHourStart = it },
                                pickerSize = 60.dp,
                                headingSize = 14.sp
                            )

                            VerticalTimePicker(
                                title = stringResource(id = R.string.minutes),
                                minValue = 0,
                                maxValue = 59,
                                selectedValue = selectedMinuteStart,
                                onValueChange = { selectedMinuteStart = it },
                                pickerSize = 60.dp,
                                headingSize = 14.sp
                            )
                        }
                    }

                    Text(
                        text = "-",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 60.dp)
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.end),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row {
                            VerticalTimePicker(
                                title = stringResource(id = R.string.hours),
                                minValue = 0,
                                maxValue = 23,
                                selectedValue = selectedHourEnd,
                                onValueChange = { selectedHourEnd = it },
                                pickerSize = 60.dp,
                                headingSize = 14.sp
                            )

                            VerticalTimePicker(
                                title = stringResource(id = R.string.minutes),
                                minValue = 0,
                                maxValue = 59,
                                selectedValue = selectedMinuteEnd,
                                onValueChange = { selectedMinuteEnd = it },
                                pickerSize = 60.dp,
                                headingSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))


                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.days_of_week),
                        fontSize = 20.sp,
                        color = commonColor,
                        maxLines = 1,
                        overflow = Clip
                    )
                    Text(
                        text = if (allDaysChecked) {
                            stringResource(id = R.string.every_day)
                        } else {
                            ""
                        },
                        fontSize = 20.sp,
                        color = commonColor,
                        maxLines = 1,
                        overflow = Clip
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    maxItemsInEachRow = 7
                ) {
                    dayOfWeek.forEach { day ->
                        Row() {
                            Button(
                                onClick = {
                                    checkedDays[day.index] =
                                        !checkedDays[day.index]
                                },
                                modifier = Modifier.size(40.dp),
                                shape = CircleShape,
                                contentPadding = PaddingValues(0.dp),
                                colors = buttonColors(
                                    containerColor = if (checkedDays[day.index]) {
                                        backgroundColor
                                    } else {
                                        Color.Transparent
                                    },
                                    contentColor = commonColor
                                )
                            ) {
                                Text(
                                    text = stringResource(id = day.label),
                                    fontSize = 18.sp,
                                    color = commonColor
                                )
                            }
                            if (day != dayOfWeek.last()) {
                                Spacer(modifier = Modifier.width(5.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                DatePickerDropdown(
                    date = selectedDateStart,
                    onDateSelected = { selectedDateStart = it },
                    label = stringResource(id = R.string.start_date) + " (" + stringResource(id = R.string.optional) + ")"
                )

                Spacer(modifier = Modifier.height(16.dp))

                DatePickerDropdown(
                    date = selectedDateEnd,
                    onDateSelected = { selectedDateEnd = it },
                    label = stringResource(id = R.string.end_date) + " (" + stringResource(id = R.string.optional) + ")"
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.notify),
                        fontSize = 20.sp,
                        color = commonColor,
                        maxLines = 1,
                        overflow = Clip
                    )

                    SelectField(selectedNotification, notifications)
                }

                Spacer(modifier = Modifier.height(16.dp))

                SoundSelector(
                    selectedUri = selectedSound,
                    onSoundSelected = { uri ->
                        selectedSound = uri
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {

                    TextButton(
                        onClick = onDismiss,
                        colors = buttonColors(
                            contentColor = commonColor,
                            containerColor = Color.Transparent
                        )
                    ) {
                        Text(
                            text = stringResource(id = R.string.cancel),
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val selectedDays: List<DayOfWeek> = DayOfWeek.entries.filter { day ->
                                checkedDays.getOrNull(day.index) == true
                            }
                            if (schedule == null) {
                                viewModel.addSchedule(
                                    ScheduleEntity(
                                        title = scheduleName,
                                        description = text,
                                        dateStart = selectedDateStart,
                                        dateEnd = selectedDateEnd,
                                        daysOfWeek = selectedDays.toSet(),
                                        sound = selectedSound,
                                        timeStart = LocalTime.of(
                                            selectedHourStart,
                                            selectedMinuteStart
                                        ),
                                        timeEnd = LocalTime.of(selectedHourEnd, selectedMinuteEnd),
                                        notify = selectedNotification.value.value,
                                        isEnabled = true,
                                    )
                                )
                            } else {
                                viewModel.updateSchedule(
                                    schedule.copy(
                                        title = scheduleName,
                                        description = text,
                                        dateStart = selectedDateStart,
                                        dateEnd = selectedDateEnd,
                                        daysOfWeek = selectedDays.toSet(),
                                        sound = selectedSound,
                                        timeStart = LocalTime.of(
                                            selectedHourStart,
                                            selectedMinuteStart
                                        ),
                                        timeEnd = LocalTime.of(selectedHourEnd, selectedMinuteEnd),
                                        notify = selectedNotification.value.value,
                                        isEnabled = true,
                                    )
                                )
                            }

                            onDismiss()
                        },
                        colors = buttonColors(
                            contentColor = commonColor,
                            containerColor = backgroundColor
                        )

                    ) {
                        Text(
                            text = stringResource(id = R.string.save),
                            fontSize = 20.sp
                        )
                    }

                }
            }
        }
    }
}

