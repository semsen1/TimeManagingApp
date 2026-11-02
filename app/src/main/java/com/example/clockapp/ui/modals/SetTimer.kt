package com.example.clockapp.ui.modals

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

import com.example.clockapp.R
import com.example.clockapp.entity.TimerEntity
import com.example.clockapp.ui.components.fields.CustomTextField
import com.example.clockapp.ui.components.fields.VerticalTimePicker
import com.example.clockapp.ui.theme.LocalExtendedColors
import com.example.clockapp.viewModels.TimerViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetTimer(
    onDismiss: () -> Unit,
    viewModel: TimerViewModel,
    timer: TimerEntity? = null
) {

    val backgroundColor = LocalExtendedColors.current.backgroundColor
    val commonColor = LocalExtendedColors.current.commonContentColor
    val buttonBackgroundColor = LocalExtendedColors.current.buttonsBackground

    var timerName by remember(timer) { mutableStateOf(timer?.title.orEmpty()) }

    val initialMs = timer?.targetDurationMs ?: 0L
    var selectedHour by remember(timer) { mutableIntStateOf((initialMs / 3_600_000L).toInt().coerceIn(0, 23)) }
    var selectedMinute by remember(timer) { mutableIntStateOf(((initialMs / 60_000L) % 60).toInt()) }
    var selectedSecond by remember(timer) { mutableIntStateOf(((initialMs / 1_000L) % 60).toInt()) }
    var deleteOnFinish by remember(timer) { mutableStateOf(timer?.deleteOnFinish ?: false) }

    val durationMs = ((selectedHour * 60L + selectedMinute) * 60L + selectedSecond) * 1000L


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
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(
                        if (timer == null) R.string.add_timer else R.string.update_timer
                    ),
                    fontSize = 22.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    text = timerName,
                    onInputChanged = { timerName = it },
                    label = {
                        Text(text = stringResource(id = R.string.title), color = commonColor)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    VerticalTimePicker(
                        title = stringResource(id = R.string.hours),
                        minValue = 0,
                        maxValue = 23,
                        selectedValue = selectedHour,
                        onValueChange = { selectedHour = it },
                        pickerSize = 60.dp,
                        headingSize = 14.sp
                    )
                    VerticalTimePicker(
                        title = stringResource(id = R.string.minutes),
                        minValue = 0,
                        maxValue = 59,
                        selectedValue = selectedMinute,
                        onValueChange = { selectedMinute = it },
                        pickerSize = 60.dp,
                        headingSize = 14.sp
                    )
                    VerticalTimePicker(
                        title = stringResource(id = R.string.seconds),
                        minValue = 0,
                        maxValue = 59, // было 60
                        selectedValue = selectedSecond,
                        onValueChange = { selectedSecond = it },
                        pickerSize = 60.dp,
                        headingSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.delete_after_finish),
                        modifier = Modifier.weight(1f),
                        color = commonColor,
                        fontSize = 16.sp
                    )
                    Switch(
                        checked = deleteOnFinish,
                        onCheckedChange = { deleteOnFinish = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = commonColor)
                    )
                }
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
                        Text(text = stringResource(id = R.string.cancel), fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        enabled = durationMs > 0,
                        onClick = {
                            if (timer == null) {
                                viewModel.createTimer(timerName, durationMs, deleteOnFinish)
                            } else {
                                viewModel.updateTimer(timer.id, timerName, durationMs, deleteOnFinish)
                            }
                            onDismiss()
                        },
                        colors = buttonColors(
                            contentColor = commonColor,
                            containerColor = backgroundColor
                        )
                    ) {
                        Text(text = stringResource(id = R.string.save), fontSize = 20.sp)
                    }
                }
            }
        }
    }
}

