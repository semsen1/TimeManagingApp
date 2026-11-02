package com.example.clockapp.ui.components.schedule

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.clockapp.features.buildCalendarRows
import com.example.clockapp.models.DayOfWeek
import com.example.clockapp.models.SelectItem
import com.example.clockapp.ui.components.fields.SelectField
import com.example.clockapp.ui.modals.ScheduleDayDetails
import com.example.clockapp.ui.theme.LocalExtendedColors
import com.example.clockapp.viewModels.ScheduleViewModel
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleMonthList(
    viewModel: ScheduleViewModel,
) {
    val scheduleList by viewModel.scheduleCounts.collectAsStateWithLifecycle()
    val rows = buildCalendarRows(scheduleList)
    val backgroundColor = LocalExtendedColors.current.backgroundColor
    val commonColor = LocalExtendedColors.current.commonContentColor
    val secondColor = LocalExtendedColors.current.secondContentColor


    val dayOfWeek = DayOfWeek.getDaysOfWeek()

    val openScheduleDayDetailsDialog = remember { mutableStateOf(false) }

    val selectedDay = remember { mutableStateOf(LocalDate.now()) }

    val yearBounds by viewModel.yearBounds.collectAsStateWithLifecycle()
    val yearRange = yearBounds.minYear..yearBounds.maxYear


    val months = Month.entries.map { month ->
        SelectItem(
            label = month.toString().lowercase().replaceFirstChar { it.uppercaseChar() },
            value = month.value
        )
    }

    val years = yearRange.map { year ->
        SelectItem(
            label = year.toString(),
            value = year
        )
    }

    val selectedMonth = remember {
        mutableStateOf(months.find { it.value == LocalDate.now().monthValue } ?: months.first())
    }

    val selectedYear = remember {
        mutableStateOf(years.find { it.value == LocalDate.now().year } ?: years.first())
    }

    val selectedMonthDate = YearMonth.of(selectedYear.value.value, selectedMonth.value.value)


    LaunchedEffect(selectedMonthDate) {
        viewModel.showMonth(selectedMonthDate)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .padding(start = 5.dp),
                horizontalArrangement = Arrangement.Start,
            ) {
                SelectField(selectedYear, years)

            }
            Spacer(modifier = Modifier.width(10.dp))
            Row(
                modifier = Modifier
                    .padding(start = 5.dp),
                horizontalArrangement = Arrangement.Start,
            ) {
                SelectField(selectedMonth, months)

            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            dayOfWeek.forEach { day ->
                Text(
                    text = stringResource(id = day.label),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        rows.forEach { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                week.forEach { day ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 4.dp, horizontal = 2.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(backgroundColor)
                            .height(55.dp)
                            .clickable {
                                if (day != null) {
                                    openScheduleDayDetailsDialog.value = true
                                    selectedDay.value = day.date
                                }

                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (day != null) {
                            Text(
                                text = day.date.dayOfMonth.toString(),
                                color = secondColor,
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp
                            )
                            HorizontalDivider(
                                thickness = 2.dp,
                                color = commonColor
                            )
                            Text(
                                text = day.count.toString(),
                                color = commonColor,
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            Text(
                                text = "—",
                                color = commonColor,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
        if (openScheduleDayDetailsDialog.value) {
            ScheduleDayDetails(
                onDismiss = { openScheduleDayDetailsDialog.value = false },
                viewModel = viewModel,
                day = selectedDay.value
            )
        }
    }
}