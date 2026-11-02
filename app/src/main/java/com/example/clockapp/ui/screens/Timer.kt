package com.example.clockapp.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.clockapp.R
import com.example.clockapp.database.AppDatabase
import com.example.clockapp.entity.SettingEntity.Companion.TIMER_SOUND
import com.example.clockapp.entity.TimerStatus
import com.example.clockapp.ui.components.BottomNavBar
import com.example.clockapp.ui.components.CommonScreen
import com.example.clockapp.ui.components.icons.StopwatchIcon
import com.example.clockapp.ui.components.sound.SoundEffect
import com.example.clockapp.ui.components.timer.StopwatchList
import com.example.clockapp.ui.components.timer.TimerList
import com.example.clockapp.ui.modals.SetTimer
import com.example.clockapp.ui.theme.LocalExtendedColors
import com.example.clockapp.viewModels.SettingViewModel
import com.example.clockapp.viewModels.TimerViewModel
import com.example.clockapp.viewModels.factory.SettingViewModelFactory

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Timer(
    navController: NavHostController,
    viewModel: TimerViewModel,
) {
    val backgroundColor = LocalExtendedColors.current.secondBackgroundColor
    val backgroundTransparentColor = LocalExtendedColors.current.backgroundTransparentColor
    val commonColor = LocalExtendedColors.current.commonContentColor

    val stopwatches by viewModel.stopwatches.collectAsStateWithLifecycle()
    val timers by viewModel.timers.collectAsStateWithLifecycle()
    val stopwatchesRun = stopwatches.count { it.status == TimerStatus.RUNNING }
    val timersRun = timers.count { it.status == TimerStatus.RUNNING }


    var runningStopwatch by remember { mutableStateOf(false) }
    if (!stopwatches.isEmpty()) {
        runningStopwatch = true
    } else {
        runningStopwatch = false
    }

    var runningTimer by remember { mutableStateOf(false) }
    if (!timers.isEmpty()) {
        runningTimer = true
    } else {
        runningTimer = false
    }

    val openAddTimer = remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current
    val database = remember(context) {
        AppDatabase.getInstance(context)
    }

    val settingFactory = remember(database) {
        SettingViewModelFactory(database.settingDao())
    }

    val settingViewModel: SettingViewModel = viewModel(factory = settingFactory)

    settingViewModel.load(TIMER_SOUND)

    val timerSoundStatus by settingViewModel.setting.collectAsStateWithLifecycle()

    if (timerSoundStatus?.getValue() == true) {
        SoundEffect(play = (stopwatchesRun > 0 || timersRun > 0), looping = true)
    }


    Scaffold(
        bottomBar = { BottomNavBar(navController = navController, padding = PaddingValues()) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            CommonScreen(R.drawable.hourglasses, R.string.timer_stopwatch)
            Spacer(modifier = Modifier.height(15.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(
                                color = backgroundColor,
                                shape = RoundedCornerShape(16.dp)
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (timers.isEmpty()) {
                            Text(
                                text = stringResource(R.string.no_active_stopwatches),
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        } else {
                            TimerList(
                                timers = timers,
                                onToggle = viewModel::toggle,
                                onStop = viewModel::reset,
                                onDelete = viewModel::delete
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(
                                color = backgroundColor,
                                shape = RoundedCornerShape(16.dp)
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (stopwatches.isEmpty()) {
                            Text(
                                text = stringResource(R.string.no_active_timers),
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        } else {
                            StopwatchList(
                                stopwatches = stopwatches,
                                onTitleChange = viewModel::updateTitle,
                                onToggle = viewModel::toggle,
                                onDelete = viewModel::delete,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 90.dp, top = 15.dp),
                contentAlignment = Alignment.Center
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //timers button
                    Row(
                        modifier = Modifier.background(
                            color =
                                if (runningTimer) {
                                    backgroundTransparentColor
                                } else {
                                    Color.Transparent
                                },
                            shape = CircleShape
                        )
                    ) {
                        IconButton(
                            onClick = {
//                                runningTimer = !runningTimer
                                if (timers.isEmpty()) {
                                    openAddTimer.value = true
                                } else {
                                    viewModel.pauseTimers()
                                }
                            },
                            shape = CircleShape,
                            modifier = Modifier.size(60.dp),
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.hourglasses),
                                contentDescription = null,
                                modifier = Modifier.size(60.dp)
                            )
                        }

                        AnimatedVisibility(
                            visible = runningTimer,
                            enter = fadeIn(tween(300)) +
                                    expandHorizontally(tween(300), expandFrom = Alignment.Start) +
                                    scaleIn(tween(300), initialScale = 0.6f),
                            exit = fadeOut(tween(200)) +
                                    shrinkHorizontally(
                                        tween(300),
                                        shrinkTowards = Alignment.Start
                                    ) +
                                    scaleOut(tween(200), targetScale = 0.6f)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Row(
                                ) {
                                    Button(
                                        onClick = { openAddTimer.value = true },
                                        colors = ButtonDefaults.buttonColors(
                                            contentColor = commonColor,
                                            containerColor = backgroundColor
                                        ),
                                        shape = CircleShape,
                                        modifier = Modifier.size(60.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("+", fontSize = 32.sp)
                                    }
                                }

                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(50.dp))

                    //stopwatches button
                    Row(
                        modifier = Modifier.background(
                            color =
                                if (runningStopwatch) {
                                    backgroundTransparentColor
                                } else {
                                    Color.Transparent
                                },
                            shape = CircleShape
                        )
                    ) {
                        IconButton(
                            onClick = {
                                if (stopwatches.isEmpty()) {
                                    viewModel.createStopwatch()
                                } else {
                                    viewModel.pauseStopwatches()
                                }
//                                runningStopwatch = !runningStopwatch

                            },
                            shape = CircleShape,
                            modifier = Modifier.size(60.dp),
                        ) {
                            StopwatchIcon(isRunning = stopwatchesRun > 0)
                        }

                        AnimatedVisibility(
                            visible = runningStopwatch,
                            enter = fadeIn(tween(300)) +
                                    expandHorizontally(tween(300), expandFrom = Alignment.Start) +
                                    scaleIn(tween(300), initialScale = 0.6f),
                            exit = fadeOut(tween(200)) +
                                    shrinkHorizontally(
                                        tween(300),
                                        shrinkTowards = Alignment.Start
                                    ) +
                                    scaleOut(tween(200), targetScale = 0.6f)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Row(
                                ) {
                                    Button(
                                        onClick = { viewModel.createStopwatch() },
                                        colors = ButtonDefaults.buttonColors(
                                            contentColor = commonColor,
                                            containerColor = backgroundColor
                                        ),
                                        shape = CircleShape,
                                        modifier = Modifier.size(60.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("+", fontSize = 32.sp)
                                    }
                                }

                            }
                        }
                    }

                }
            }
        }
    }

    if (openAddTimer.value) {
        SetTimer(
            onDismiss = { openAddTimer.value = false },
            viewModel = viewModel
        )
    }
}