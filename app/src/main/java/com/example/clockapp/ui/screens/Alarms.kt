package com.example.clockapp.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.clockapp.R
import com.example.clockapp.ui.components.AddEntityButton
import com.example.clockapp.ui.components.alarm.AlarmItem
import com.example.clockapp.ui.components.BottomNavBar
import com.example.clockapp.ui.components.CommonScreen
import com.example.clockapp.ui.modals.SetAlarm
import com.example.clockapp.viewModels.AlarmViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Alarms(
    navController: NavHostController,
    viewModel: AlarmViewModel
) {
    val openAddAlarmDialog = remember { mutableStateOf(false) }
    val navigationPadding = WindowInsets.navigationBars.asPaddingValues()
    val alarmsData by viewModel.alarms.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()

    val showBottomGradient by remember {
        derivedStateOf {
            listState.canScrollForward || listState.canScrollBackward
        }
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
                R.drawable.ic_alarm,
                R.string.alarms
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(top = 8.dp)
                ) {
                    items(alarmsData) { alarm ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            AlarmItem(
                                alarm = alarm,
                                viewModel = viewModel
                            )
                        }
                    }
                }

                if (showBottomGradient) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(32.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.25f)
                                    )
                                )
                            )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 90.dp, top = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                AddEntityButton(openAddAlarmDialog)
            }
        }
    }

    if (openAddAlarmDialog.value) {
        SetAlarm(
            onDismiss = { openAddAlarmDialog.value = false },
            viewModel = viewModel
        )
    }
}