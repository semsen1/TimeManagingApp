package com.example.clockapp.ui.modals

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.getValue
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
import com.example.clockapp.entity.ReminderEntity
import com.example.clockapp.models.SelectItem
import com.example.clockapp.ui.components.fields.CustomTextField
import com.example.clockapp.ui.components.fields.SelectField
import com.example.clockapp.ui.components.fields.SoundSelector
import com.example.clockapp.ui.components.fields.VerticalTimePicker
import com.example.clockapp.ui.theme.LocalExtendedColors
import com.example.clockapp.viewModels.ReminderViewModel
import java.time.LocalDate
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetReminder(
    onDismiss: () -> Unit,
    viewModel: ReminderViewModel,
    reminder: ReminderEntity? = null
) {

    val backgroundColor = LocalExtendedColors.current.backgroundColor
    val commonColor = LocalExtendedColors.current.commonContentColor
    val buttonBackgroundColor = LocalExtendedColors.current.buttonsBackground

    var reminderName by remember { mutableStateOf(reminder?.title ?: "") }


    var selectedHour by remember { mutableStateOf(reminder?.time?.hour ?: 0) }
    var selectedMinute by remember { mutableStateOf(reminder?.time?.minute ?: 0) }

    val notifications = listOf<SelectItem>(
        SelectItem(label = stringResource(id = R.string.not_notify), value = 0),
        SelectItem(label = stringResource(id = R.string.one_minute), value = 1),
        SelectItem(label = stringResource(id = R.string.five_minutes), value = 5),
        SelectItem(label = stringResource(id = R.string.ten_minutes), value = 10),
        SelectItem(label = stringResource(id = R.string.thirty_minutes), value = 30),
        SelectItem(label = stringResource(id = R.string.one_hour), value = 60),
    )

    var selectedDate by remember {
        mutableStateOf(reminder?.date ?: LocalDate.now().plusDays(1))
    }

    val selectedNotification =
        remember {
            mutableStateOf(notifications.find { it.value == reminder?.notify }
                ?: notifications.first())
        }

    var selectedSound by remember { mutableStateOf<Uri?>(reminder?.sound) }

    var expandedDescription by remember { mutableStateOf(false) }
    var text by rememberSaveable { mutableStateOf(reminder?.description ?: "") }

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
                    text = if (reminder == null) {
                        stringResource(id = R.string.add_reminder)
                    } else {
                        stringResource(id = R.string.update_reminder)
                    },
                    fontSize = 22.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                CustomTextField(
                    text = reminderName,
                    onInputChanged = { reminderName = it },
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
                Spacer(modifier = Modifier.height(16.dp))
                DatePickerDropdown(
                    date = selectedDate,
                    onDateSelected = { selectedDate = it },
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

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {

                            if (reminder == null) {
                                viewModel.addReminder(
                                    ReminderEntity(
                                        title = reminderName,
                                        description = text,
                                        time = LocalTime.of(selectedHour, selectedMinute),
                                        date = selectedDate,
                                        notify = selectedNotification.value.value,
                                        sound = selectedSound,
                                    )
                                )
                            } else {
                                viewModel.updateReminder(
                                    reminder.copy(
                                        title = reminderName,
                                        description = text,
                                        time = LocalTime.of(selectedHour, selectedMinute),
                                        date = selectedDate,
                                        notify = selectedNotification.value.value,
                                        sound = selectedSound,
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

