package com.example.clockapp.ui.components.timer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.clockapp.R
import com.example.clockapp.entity.TimerEntity
import com.example.clockapp.entity.TimerStatus

@Composable
fun TimerItem(
    timer: TimerEntity,
    now: State<Long>,
    onToggle: (Long) -> Unit,
    onStop: (Long) -> Unit,
    onDelete: (Long) -> Unit
) {
    val remaining = timer.remaining(now.value).coerceAtLeast(0)
    val fraction = if (timer.targetDurationMs > 0)
        (remaining.toFloat() / timer.targetDurationMs).coerceIn(0f, 1f) else 0f
    val running = timer.status == TimerStatus.RUNNING

    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TimerRing(fraction = fraction, color = Color(timer.color))
            Column(
                Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = timer.title.ifBlank { timer.id.toString() },
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Text(
                    text = formatTime(((remaining + 999) / 1000) * 1000),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            IconButton(onClick = { onToggle(timer.id) }) { PlayPauseIcon(running) }
            IconButton(onClick = { onStop(timer.id) }) { StopIcon() }
            IconButton(onClick = { onDelete(timer.id) }) {
                Icon(painterResource(R.drawable.delete_button), contentDescription = "Удалить")
            }

        }
    }
}