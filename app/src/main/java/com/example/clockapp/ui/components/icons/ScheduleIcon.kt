package com.example.clockapp.ui.components.icons

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

private const val VIEWPORT = 652.8f
private const val STEP = 163.2f

private val RING = listOf(0 to 0, 1 to 0, 2 to 0, 2 to 1, 1 to 1, 0 to 1)

private const val BODY =
    "M550.8,40.8H469.2V20.4c0,-11.26 -9.14,-20.4 -20.4,-20.4s-20.4,9.14 -20.4,20.4v20.4h-204V20.4C224.4,9.14 215.26,0 204,0s-20.4,9.14 -20.4,20.4v20.4H102c-45.06,0 -81.6,36.54 -81.6,81.6v448.8c0,45.06 36.54,81.6 81.6,81.6h448.8c45.06,0 81.6,-36.54 81.6,-81.6V122.4C632.4,77.34 595.86,40.8 550.8,40.8zM591.6,571.2c0,22.54 -18.26,40.8 -40.8,40.8H102c-22.54,0 -40.8,-18.28 -40.8,-40.8V204h530.4V571.2zM591.6,163.2H61.2v-40.8c0,-22.54 18.26,-40.8 40.8,-40.8h81.6V102c0,11.26 9.14,20.4 20.4,20.4s20.4,-9.14 20.4,-20.4V81.6h204V102c0,11.26 9.14,20.4 20.4,20.4S469.2,113.26 469.2,102V81.6h81.6c22.54,0 40.8,18.26 40.8,40.8V163.2z"

private const val SQUARE =
    "M142.8,408h40.8c22.54,0 40.8,-18.28 40.8,-40.8V326.4c0,-22.54 -18.26,-40.8 -40.8,-40.8h-40.8c-22.54,0 -40.8,18.26 -40.8,40.8v40.8C102,389.72 120.26,408 142.8,408zM142.8,326.4h40.8v40.8h-40.8V326.4z"

@Composable
fun ScheduleIcon(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = LocalContentColor.current
) {
    val body = remember { PathParser().parsePathString(BODY).toPath() }
    val square = remember { PathParser().parsePathString(SQUARE).toPath() }

    val transition = rememberInfiniteTransition(label = "schedule")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1600
                0f at 0 using FastOutSlowInEasing
                1f at 1000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Canvas(modifier) {
        scale(size.minDimension / VIEWPORT, pivot = Offset.Zero) {
            drawPath(body, tint)
            RING.forEachIndexed { i, (x0, y0) ->
                val (x1, y1) = RING[(i + 1) % RING.size]
                translate(
                    left = (x0 + (x1 - x0) * progress) * STEP,
                    top = (y0 + (y1 - y0) * progress) * STEP
                ) {
                    drawPath(square, tint)
                }
            }
        }
    }
}