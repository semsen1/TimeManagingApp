package com.example.clockapp.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.clockapp.R
import com.example.clockapp.models.SelectItem
import com.example.clockapp.ui.components.AddEntityButton
import com.example.clockapp.ui.components.BottomNavBar
import com.example.clockapp.ui.components.CommonScreen
import com.example.clockapp.ui.components.fields.SelectField
import com.example.clockapp.ui.components.schedule.ScheduleList
import com.example.clockapp.ui.components.schedule.ScheduleMonthList
import com.example.clockapp.ui.modals.SetSchedule
import com.example.clockapp.viewModels.ScheduleViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Schedules(
    navController: NavHostController,
    viewModel: ScheduleViewModel
) {
    val openAddScheduleDialog = remember { mutableStateOf(false) }
    val navigationPadding = WindowInsets.navigationBars.asPaddingValues()
    val schedulesData by viewModel.schedules.collectAsStateWithLifecycle()

    val PeriodFilters = listOf<SelectItem>(
        SelectItem(label = stringResource(id = R.string.now), value = 1),
        SelectItem(label = stringResource(id = R.string.week), value = 2),
        SelectItem(label = stringResource(id = R.string.month), value = 3),
    )

    val selectedPeriod = remember {
        mutableStateOf(PeriodFilters.first())
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController,
                padding = PaddingValues()
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CommonScreen(
                R.drawable.ic_schedule,
                R.string.schedules
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp),
                horizontalArrangement = Arrangement.Start,
            ) {
                SelectField(selectedPeriod, PeriodFilters)

            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                when (selectedPeriod.value.value) {
                    1 -> ScheduleList(
                        schedules = schedulesData,
                        viewModel = viewModel,
                        currentWeek = false
                    )

                    2 -> ScheduleList(
                        schedules = schedulesData,
                        viewModel = viewModel,
                        currentWeek = true
                    )

                    3 -> ScheduleMonthList(viewModel = viewModel)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 90.dp, top = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                AddEntityButton(openAddScheduleDialog)
            }
        }
    }

    if (openAddScheduleDialog.value) {
        SetSchedule(
            onDismiss = { openAddScheduleDialog.value = false },
            viewModel = viewModel
        )
    }
}