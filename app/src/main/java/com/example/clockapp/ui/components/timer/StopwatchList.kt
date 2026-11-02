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
fun StopwatchList(
    stopwatches: List<TimerEntity>,
    onTitleChange: (id: Long, title: String) -> Unit,
    onToggle: (id: Long) -> Unit,
    onDelete: (id: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val now = rememberNow(stopwatches.any { it.status == TimerStatus.RUNNING }, periodMs = 100)

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(stopwatches, key = { it.id }) { sw ->
            StopwatchItem(
                stopwatch = sw,
                now = now,
                onTitleChange = onTitleChange,
                onToggle = onToggle,
                onDelete = onDelete
            )
        }
    }
}