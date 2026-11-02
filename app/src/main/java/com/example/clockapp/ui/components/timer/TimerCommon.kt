package com.example.clockapp.ui.components.timer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.clockapp.R
import com.example.clockapp.entity.TimerEntity
import com.example.clockapp.entity.TimerStatus

const val TITLE_SAVE_DELAY_MS = 600L

fun formatTime(ms: Long, tenths: Boolean = false): String {
    val total = ms.coerceAtLeast(0)
    val s = total / 1000
    val h = s / 3600
    val m = (s % 3600) / 60
    val sec = s % 60
    val base = if (h > 0) "%d:%02d:%02d".format(h, m, sec) else "%02d:%02d".format(m, sec)
    return if (tenths) base + ".%d".format((total % 1000) / 100) else base
}

@Composable
fun PlayPauseIcon(running: Boolean) {
    if (running) {
        Icon(Icons.Filled.Pause, contentDescription = "Пауза")
    } else {
        Icon(Icons.Filled.PlayArrow, contentDescription = "Старт")
    }
}

@Composable
fun StopIcon() {
    Box(
        Modifier
            .size(18.dp)
            .background(LocalContentColor.current, RoundedCornerShape(3.dp))
    )
}

@Composable
fun TimerRing(fraction: Float, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier.size(64.dp)) {
        val strokeW = 6.dp.toPx()
        val topLeft = Offset(strokeW / 2, strokeW / 2)
        val arcSize = Size(size.width - strokeW, size.height - strokeW)

        drawArc(
            color = color.copy(alpha = 0.15f),
            startAngle = 0f, sweepAngle = 360f, useCenter = false,
            topLeft = topLeft, size = arcSize,
            style = Stroke(width = strokeW)
        )
        if (fraction > 0f) {
            val sweep = 360f * fraction
            drawArc(
                color = color,
                startAngle = -90f + (360f - sweep),
                sweepAngle = sweep,
                useCenter = false,
                topLeft = topLeft, size = arcSize,
                style = Stroke(width = strokeW, cap = StrokeCap.Round)
            )
        }
    }
}