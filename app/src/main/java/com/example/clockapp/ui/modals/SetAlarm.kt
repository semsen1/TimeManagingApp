package com.example.clockapp.ui.modals

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.clockapp.entity.AlarmEntity
import com.example.clockapp.models.DayOfWeek
import com.example.clockapp.models.SelectItem
import com.example.clockapp.ui.components.fields.CustomTextField
import com.example.clockapp.ui.components.fields.SelectField
import com.example.clockapp.ui.components.fields.SoundSelector
import com.example.clockapp.ui.components.fields.VerticalTimePicker
import com.example.clockapp.ui.theme.LocalExtendedColors
import com.example.clockapp.viewModels.AlarmViewModel
import java.time.LocalDate
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetAlarm(
    onDismiss: () -> Unit,
    viewModel: AlarmViewModel,
    alarm: AlarmEntity? = null
) {

    val backgroundColor = LocalExtendedColors.current.backgroundColor
    val commonColor = LocalExtendedColors.current.commonContentColor
    val buttonBackgroundColor = LocalExtendedColors.current.buttonsBackground

    var alarmName by remember { mutableStateOf(alarm?.title ?: "") }
    var isOnce by remember { mutableStateOf(alarm?.isOnce ?: false) }

    val daysOfWeek = alarm?.daysOfWeek

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

    var selectedHour by remember { mutableStateOf(alarm?.time?.hour ?: 0) }
    var selectedMinute by remember { mutableStateOf(alarm?.time?.minute ?: 0) }

    val intervals = listOf<SelectItem>(
        SelectItem(label = stringResource(id = R.string.not_repeat), value = 0),
        SelectItem(label = stringResource(id = R.string.one_minute), value = 1),
        SelectItem(label = stringResource(id = R.string.five_minutes), value = 5),
        SelectItem(label = stringResource(id = R.string.ten_minutes), value = 10),
        SelectItem(label = stringResource(id = R.string.thirty_minutes), value = 30),
    )

    val selectedInterval =
        remember {
            mutableStateOf(intervals.find { it.value == alarm?.replay } ?: intervals.first())
        }

    val wakeUpTests = listOf<SelectItem>(
        SelectItem(label = stringResource(id = R.string.no), value = 0),
        SelectItem(label = stringResource(id = R.string.math), value = 1),
        SelectItem(label = stringResource(id = R.string.advanced_math), value = 2),
        SelectItem(label = stringResource(id = R.string.graphic_key), value = 3),
    )
    val selectedWakeUp = remember {
        mutableStateOf(wakeUpTests.find { it.value == alarm?.wakeUpTest } ?: wakeUpTests.first())
    }

    val dayOfWeek = DayOfWeek.getDaysOfWeek()

    var selectedDate by remember {
        mutableStateOf(alarm?.date ?: LocalDate.now().plusDays(1))
    }

    var selectedSound by remember { mutableStateOf<Uri?>(alarm?.sound) }


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
                    text = if (alarm == null) {
                        stringResource(id = R.string.add_alarm)
                    } else {
                        stringResource(id = R.string.update_alarm)
                    },
                    fontSize = 22.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                CustomTextField(
                    text = alarmName,
                    onInputChanged = { alarmName = it },
                    label = {
                        Text(
                            text = stringResource(id = R.string.title),
                            color = commonColor
                        )
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    VerticalTimePicker(
                        title = stringResource(id = R.string.hours),
                        minValue = 0,
                        maxValue = 23,
                        selectedValue = selectedHour,
                        onValueChange = { selectedHour = it }
                    )
                    VerticalTimePicker(
                        title = stringResource(id = R.string.minutes),
                        minValue = 0,
                        maxValue = 59,
                        selectedValue = selectedMinute,
                        onValueChange = { selectedMinute = it }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(id = R.string.one_time),
                        fontSize = 20.sp,
                        color = commonColor,
                    )
                    Switch(
                        checked = isOnce,
                        onCheckedChange = { isOnce = !isOnce },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = buttonBackgroundColor,
                            uncheckedTrackColor = buttonBackgroundColor,
                            checkedThumbColor = commonColor,
                            uncheckedThumbColor = Color.DarkGray,
                            checkedBorderColor = Color.Transparent,
                            uncheckedBorderColor = Color.Transparent
                        ),
                    )
                }
                if (isOnce) {
                    DatePickerDropdown(
                        date = selectedDate,
                        onDateSelected = { selectedDate = it },
                    )
                }

                if (!isOnce) {
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
                }
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.repeat),
                        fontSize = 20.sp,
                        color = commonColor,
                        maxLines = 1,
                        overflow = Clip
                    )

                    SelectField(selectedInterval, intervals)
                }

                Spacer(modifier = Modifier.height(16.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    itemVerticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.wake_up_test),
                        fontSize = 20.sp,
                        color = commonColor
                    )

                    SelectField(selectedWakeUp, wakeUpTests)
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
                        colors = ButtonDefaults.buttonColors(
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
                            if (alarm == null) {
                                val newAlarm =
                                    AlarmEntity(
                                        title = alarmName,
                                        time = LocalTime.of(selectedHour, selectedMinute),
                                        date = if (isOnce) {
                                            selectedDate
                                        } else {
                                            null
                                        },
                                        daysOfWeek = selectedDays.toSet(),
                                        isOnce = isOnce,
                                        sound = selectedSound,
                                        replay = selectedInterval.value.value,
                                        wakeUpTest = selectedWakeUp.value.value
                                    )
                                viewModel.addAlarm(
                                    newAlarm
                                )
                            } else {
                                val updatedAlarm =
                                    alarm.copy(
                                        title = alarmName,
                                        time = LocalTime.of(selectedHour, selectedMinute),
                                        date = if (isOnce) {
                                            selectedDate
                                        } else {
                                            null
                                        },
                                        daysOfWeek = selectedDays.toSet(),
                                        isOnce = isOnce,
                                        sound = selectedSound,
                                        replay = selectedInterval.value.value,
                                        wakeUpTest = selectedWakeUp.value.value
                                    )
                                viewModel.updateAlarm(
                                    updatedAlarm
                                )

                            }

                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
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

