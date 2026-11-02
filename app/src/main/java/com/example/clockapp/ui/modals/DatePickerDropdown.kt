package com.example.clockapp.ui.modals

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.clockapp.R
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDropdown(
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    date: LocalDate? = LocalDate.now().plusDays(1),
    label: String = stringResource(R.string.date),
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedDate by remember(date) { mutableStateOf(date) }

    val formatter = remember {
        DateTimeFormatter.ofPattern("yyyy.MM.dd")
    }
    val currentOnDateSelected by rememberUpdatedState(onDateSelected)

    Box(modifier = modifier) {

        Box {
            OutlinedTextField(
                value = if (selectedDate != null) {
                    selectedDate!!.format(formatter)
                } else {
                    stringResource(R.string.set_date)
                },
                onValueChange = {},
                readOnly = true,
                label = {
                    Text(
                        text = label,
                        fontSize = 16.sp

                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 20.sp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        val strokeWidth = 4.dp.toPx()
                        val y = size.height - strokeWidth / 2

                        drawLine(
                            color = Color.Gray,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = strokeWidth
                        )
                    }
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { expanded = !expanded }
            )
        }

        if (expanded) {
            val initialSelectedMillis = remember {
                selectedDate
                    ?.atStartOfDay(ZoneOffset.UTC)
                    ?.toInstant()
                    ?.toEpochMilli()
            }

            val initialDisplayedMillis = remember {
                initialSelectedMillis
                    ?: LocalDate.now()
                        .atStartOfDay(ZoneOffset.UTC)
                        .toInstant()
                        .toEpochMilli()
            }

            val pickerState = rememberDatePickerState(
                initialSelectedDateMillis = initialSelectedMillis,
                initialDisplayedMonthMillis = initialDisplayedMillis,
            )

            LaunchedEffect(pickerState) {
                snapshotFlow { pickerState.selectedDateMillis }
                    .filterNotNull()
                    .distinctUntilChanged()
                    .collect { millis ->
                        val newDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()

                        if (newDate != selectedDate) {
                            selectedDate = newDate
                            expanded = false
                            currentOnDateSelected(newDate)
                        }
                    }
            }

            Popup(
                alignment = Alignment.Center,
                offset = IntOffset.Zero,
                onDismissRequest = { expanded = false },
                properties = PopupProperties(
                    focusable = true,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                ),
            ) {
                Surface(
                    modifier = Modifier.width(360.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp,
                ) {
                    DatePicker(
                        state = pickerState,
                        showModeToggle = false,
                        title = null,
                        headline = null,
                    )
                }
            }
        }
    }
}