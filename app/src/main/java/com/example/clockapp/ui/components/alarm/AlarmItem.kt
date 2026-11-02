package com.example.clockapp.ui.components.alarm


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clockapp.R
import com.example.clockapp.entity.AlarmEntity
import com.example.clockapp.ui.modals.SetAlarm
import com.example.clockapp.ui.theme.LocalExtendedColors
import com.example.clockapp.viewModels.AlarmViewModel
import java.time.format.DateTimeFormatter

@Composable
@RequiresApi(Build.VERSION_CODES.O)
fun AlarmItem(
    alarm: AlarmEntity,
    viewModel: AlarmViewModel
) {
    var expanded by remember { mutableStateOf(false) }
    val backgroundColor = LocalExtendedColors.current.backgroundColor
    val buttonBackgroundColor = LocalExtendedColors.current.buttonsBackground
    val commonColor = LocalExtendedColors.current.commonContentColor
    val disabledBackground = LocalExtendedColors.current.disabledBackgroundColor
    val textColor =
        if (alarm.isEnabled) commonColor else LocalExtendedColors.current.disabledContentColor

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(
                color = if (alarm.isEnabled) backgroundColor else disabledBackground,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            if (alarm.title.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = alarm.title,
                        modifier = Modifier.weight(1f, fill = false),
                        color = textColor,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }


            if (alarm.date != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {

                    Text(
                        text = alarm.date.format(
                            DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        ),
                        color = textColor,
                        fontSize = 16.sp,
                        maxLines = 1
                    )
                }
            }


            val repeatText = when {
                alarm.isOnce -> stringResource(R.string.one_time)

                alarm.daysOfWeek.size == 7 ->
                    stringResource(R.string.every_day)

                else -> alarm.daysOfWeek.joinToString(", ") { day ->
                    ""
                }
            }

            val daysLabels = alarm.daysOfWeek.map { day ->
                stringResource(day.labelResId)
            }

            Text(
                text = if (alarm.isOnce || alarm.daysOfWeek.size == 7) {
                    repeatText
                } else {
                    daysLabels.joinToString(", ")
                },
                color = textColor,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier.fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = alarm.time.format(
                    DateTimeFormatter.ofPattern("HH:mm")
                ),
                color = textColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = alarm.isEnabled,
                onCheckedChange = { isEnabled ->
                    viewModel.updateAlarm(
                        alarm.copy(isEnabled = isEnabled)
                    )
                },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = buttonBackgroundColor,
                    uncheckedTrackColor = buttonBackgroundColor,
                    checkedThumbColor = commonColor,
                    uncheckedThumbColor = Color.DarkGray,
                    checkedBorderColor = Color.Transparent,
                    uncheckedBorderColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.width(2.dp))

            Box {
                IconButton(
                    onClick = { expanded = true }
                ) {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.dots_dropdown
                        ),
                        contentDescription = null,
                        tint = textColor
                    )
                }

                DropdownMenuEdit(
                    expanded = expanded,
                    onDismiss = { expanded = false },
                    alarm = alarm,
                    viewModel = viewModel
                )
            }
        }

    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DropdownMenuEdit(
    expanded: Boolean,
    onDismiss: () -> Unit,
    alarm: AlarmEntity,
    viewModel: AlarmViewModel
) {
    val commonColor = LocalExtendedColors.current.commonContentColor
    val openAddAlarmDialog = remember { mutableStateOf(false) }

    if (openAddAlarmDialog.value) {
        SetAlarm(
            onDismiss = { openAddAlarmDialog.value = false },
            viewModel = viewModel,
            alarm = alarm
        )
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(R.string.edit),
                    color = commonColor
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(
                        id = R.drawable.edit_button
                    ),
                    modifier = Modifier.size(30.dp),
                    contentDescription = null,
                    tint = commonColor
                )
            },
            onClick = {
                openAddAlarmDialog.value = !openAddAlarmDialog.value
                onDismiss()

            }
        )

        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(R.string.duplicate),
                    color = commonColor
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(
                        id = R.drawable.duplicate_button
                    ),
                    modifier = Modifier.size(30.dp),
                    contentDescription = null,
                    tint = commonColor
                )
            },
            onClick = {
                viewModel.addAlarm(alarm.copy(id = 0))
                onDismiss()

            }
        )

        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(R.string.delete),
                    color = commonColor
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(
                        id = R.drawable.delete_button
                    ),
                    modifier = Modifier.size(30.dp),
                    contentDescription = null,
                    tint = commonColor
                )
            },
            onClick = {
                viewModel.deleteAlarm(alarm.id)
                onDismiss()

            }
        )
    }
}