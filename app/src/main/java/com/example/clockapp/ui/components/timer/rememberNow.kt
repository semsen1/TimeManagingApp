package com.example.clockapp.ui.components.timer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay

@Composable
fun rememberNow(active: Boolean, periodMs: Long): State<Long> {
    val now = remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(active) {
        now.longValue = System.currentTimeMillis()
        while (active) {
            delay(periodMs)
            now.longValue = System.currentTimeMillis()
        }
    }
    return now
}