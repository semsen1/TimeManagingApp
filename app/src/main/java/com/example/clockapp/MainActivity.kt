package com.example.clockapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.clockapp.navigation.AppNavHost
import com.example.clockapp.notificationSystem.timer.TimerNotificationController
import com.example.clockapp.ui.theme.AppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class MainActivity : ComponentActivity() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        TimerNotificationController(this, appScope).start()
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val navController = rememberNavController()
                Surface() {
                    Scaffold(
                        content = { padding: PaddingValues ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .padding(
                                        PaddingValues(
                                            top = padding.calculateTopPadding(),
                                            bottom = padding.calculateBottomPadding()
                                        )
                                    )
                                    .fillMaxSize()
                            ) {
                                AppNavHost(navController)
                            }
                        }
                    )
                }
            }
        }
    }
}

