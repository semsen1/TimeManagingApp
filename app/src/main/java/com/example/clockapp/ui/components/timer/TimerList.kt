package com.example.clockapp.ui.components.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.clockapp.entity.TimerEntity
import com.example.clockapp.entity.TimerStatus

@Composable
fun TimerList(
    timers: List<TimerEntity>,
    onToggle: (id: Long) -> Unit,
    onStop: (id: Long) -> Unit,
    modifier: Modifier = Modifier,
    onDelete: (Long) -> Unit
) {
    val now = rememberNow(timers.any { it.status == TimerStatus.RUNNING }, periodMs = 50)

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(timers, key = { it.id }) { timer ->
            TimerItem(
                timer = timer,
                now = now,
                onToggle = onToggle,
                onStop = onStop,
                onDelete = onDelete
            )
        }
    }
}