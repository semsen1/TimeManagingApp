package com.example.clockapp.ui.components.timer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.clockapp.R
import com.example.clockapp.entity.TimerEntity
import com.example.clockapp.entity.TimerStatus
import com.example.clockapp.ui.components.fields.CustomTextField
import com.example.clockapp.ui.theme.LocalExtendedColors
import kotlinx.coroutines.delay

@Composable
fun StopwatchItem(
    stopwatch: TimerEntity,
    now: State<Long>,
    onTitleChange: (Long, String) -> Unit,
    onToggle: (Long) -> Unit,
    onDelete: (Long) -> Unit
) {
    val stored = stopwatch.title.ifBlank { stopwatch.id.toString() }
    var text by remember(stopwatch.id) { mutableStateOf(stored) }

    val backgroundColor = LocalExtendedColors.current.backgroundColor
    val commonColor = LocalExtendedColors.current.commonContentColor

    LaunchedEffect(text) {
        if (text == stored) return@LaunchedEffect
        delay(TITLE_SAVE_DELAY_MS)
        onTitleChange(stopwatch.id, text)
    }

    val running = stopwatch.status == TimerStatus.RUNNING

    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                CustomTextField(
                    text = text,
                    onInputChanged = { text = it },
                    label = {""}
                )

                Text(
                    text = formatTime(stopwatch.elapsed(now.value), tenths = true),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
            IconButton(onClick = { onToggle(stopwatch.id) }) { PlayPauseIcon(running) }
            IconButton(onClick = { onDelete(stopwatch.id) }) {
                Icon(painterResource(R.drawable.delete_button), contentDescription = "Удалить")
            }
        }
    }
}