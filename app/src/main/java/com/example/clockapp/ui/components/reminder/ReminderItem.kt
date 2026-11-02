package com.example.clockapp.ui.components.reminder


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.HorizontalDivider
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
import com.example.clockapp.entity.ReminderEntity
import com.example.clockapp.ui.modals.SetReminder
import com.example.clockapp.ui.theme.LocalExtendedColors
import com.example.clockapp.viewModels.ReminderViewModel
import java.time.format.DateTimeFormatter

@Composable
@RequiresApi(Build.VERSION_CODES.O)
fun ReminderItem(
    reminder: ReminderEntity,
    viewModel: ReminderViewModel,
    isNext: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    val backgroundColor = if (isNext) {
        LocalExtendedColors.current.backgroundColor
    } else {
        LocalExtendedColors.current.secondBackgroundColor
    }
    val buttonBackgroundColor = LocalExtendedColors.current.buttonsBackground
    val commonColor = LocalExtendedColors.current.commonContentColor
    val disabledBackground = LocalExtendedColors.current.disabledBackgroundColor

    val textColor =
        if (reminder.isEnabled) commonColor else LocalExtendedColors.current.disabledContentColor

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (reminder.isEnabled) backgroundColor else disabledBackground,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        if (isNext && reminder.description.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 4.dp)
            ) {
                Text(
                    text = stringResource(R.string.nearest),
                    fontWeight = FontWeight.Medium,
                )
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                if (reminder.title.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = reminder.title,
                            modifier = Modifier.weight(1f, fill = false),
                            color = textColor,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }


                if (reminder.date != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {

                        reminder.date.let { date ->
                            Text(
                                text = date.format(
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                ),
                                color = textColor,
                                fontSize = 16.sp,
                                maxLines = 1
                            )
                        }
                    }
                }


            }

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier.fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = reminder.time.format(
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
                    checked = reminder.isEnabled,
                    onCheckedChange = { isEnabled ->
                        viewModel.updateReminder(
                            reminder.copy(isEnabled = isEnabled)
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
                        reminder = reminder,
                        viewModel = viewModel
                    )
                }
            }
        }

        if (isNext && reminder.description.isNotEmpty()) {
            HorizontalDivider(
                thickness = 2.dp,
                color = commonColor
            )

            Column(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 5.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {

                Text(
                    text = reminder.description,
                    color = textColor.copy(alpha = 0.8f),
                    fontSize = 14.sp
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
    reminder: ReminderEntity,
    viewModel: ReminderViewModel
) {
    val commonColor = LocalExtendedColors.current.commonContentColor
    val openAddReminderDialog = remember { mutableStateOf(false) }

    if (openAddReminderDialog.value) {
        SetReminder(
            onDismiss = { openAddReminderDialog.value = false },
            viewModel = viewModel,
            reminder = reminder
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
                openAddReminderDialog.value = !openAddReminderDialog.value
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
                viewModel.addReminder(reminder.copy(id = 0))
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
                viewModel.deleteReminder(reminder.id)
                onDismiss()

            }
        )
    }
}