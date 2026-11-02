package com.example.clockapp.ui.components.alarm

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.clockapp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.random.Random

private enum class PatternPhase { Showing, Input, Success, Error }

private const val GRID = 3

private fun generatePattern(): List<Int> {
    val length = Random.nextInt(5, 7)

    fun crossesNode(a: Int, b: Int): Boolean {
        val dr = abs(a / GRID - b / GRID)
        val dc = abs(a % GRID - b % GRID)
        return dr % 2 == 0 && dc % 2 == 0
    }

    fun build(path: List<Int>): List<Int>? {
        if (path.size == length) return path
        val candidates = (0 until GRID * GRID)
            .filter { it !in path && (path.isEmpty() || !crossesNode(path.last(), it)) }
            .shuffled()
        for (c in candidates) build(path + c)?.let { return it }
        return null
    }
    return build(emptyList())!!
}

private fun nodeCenter(index: Int, width: Float, height: Float) = Offset(
    x = (index % GRID + 0.5f) * width / GRID,
    y = (index / GRID + 0.5f) * height / GRID
)

@Composable
fun GraphicTest(
    testStatus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val onSuccess by rememberUpdatedState(testStatus)

    var pattern by remember { mutableStateOf(generatePattern()) }
    var phase by remember { mutableStateOf(PatternPhase.Showing) }
    var attempt by remember { mutableIntStateOf(0) }
    var showHint by remember { mutableStateOf(false) }
    var fingerPos by remember { mutableStateOf<Offset?>(null) }
    val userPath = remember { mutableStateListOf<Int>() }
    val progress = remember { Animatable(0f) }


    LaunchedEffect(pattern, attempt) {
        phase = PatternPhase.Showing
        userPath.clear()
        fingerPos = null
        showHint = false
        progress.snapTo(0f)
        delay(500)
        showHint = true
        progress.animateTo(
            targetValue = (pattern.size - 1).toFloat(),
            animationSpec = tween(durationMillis = 500 * (pattern.size - 1), easing = LinearEasing)
        )
        delay(600)
        showHint = false
        progress.snapTo(0f)
        phase = PatternPhase.Input
    }

    val primary = MaterialTheme.colorScheme.primary
    val error = MaterialTheme.colorScheme.error
    val idle = MaterialTheme.colorScheme.outline
    val success = Color(0xFF2E7D32)

    val statusText = when (phase) {
        PatternPhase.Showing -> stringResource(R.string.remember_key)
        PatternPhase.Input -> stringResource(R.string.repeat_key)
        PatternPhase.Success -> stringResource(R.string.correct)
        PatternPhase.Error -> stringResource(R.string.wrong_answer)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .semantics { contentDescription = statusText },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = statusText, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(24.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .pointerInput(phase) {
                    if (phase != PatternPhase.Input) return@pointerInput

                    val w = size.width.toFloat()
                    val h = size.height.toFloat()
                    val hitRadius = w / GRID * 0.3f

                    fun hitNode(pos: Offset): Int? =
                        (0 until GRID * GRID).firstOrNull {
                            (nodeCenter(it, w, h) - pos).getDistance() <= hitRadius
                        }

                    fun addNode(pos: Offset) {
                        val node = hitNode(pos) ?: return
                        if (node !in userPath) userPath.add(node)
                    }

                    detectDragGestures(
                        onDragStart = { pos ->
                            userPath.clear()
                            fingerPos = pos
                            addNode(pos)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            fingerPos = change.position
                            addNode(change.position)
                        },
                        onDragEnd = {
                            fingerPos = null
                            when {
                                userPath.size < 2 -> userPath.clear()
                                userPath.toList() == pattern ||
                                        userPath.toList() == pattern.reversed() -> {
                                    phase = PatternPhase.Success
                                    onSuccess()
                                }

                                else -> {
                                    phase = PatternPhase.Error
                                    scope.launch {
                                        delay(900)
                                        attempt++
                                    }
                                }
                            }
                        },
                        onDragCancel = {
                            fingerPos = null
                            userPath.clear()
                        }
                    )
                }
        ) {
            val strokeWidth = 8.dp.toPx()
            val dotRadius = 8.dp.toPx()
            val ringRadius = 18.dp.toPx()

            val lineColor = when (phase) {
                PatternPhase.Error -> error
                PatternPhase.Success -> success
                else -> primary
            }

            val activeNodes: Set<Int>
            if (phase == PatternPhase.Showing) {
                if (showHint) {
                    val p = progress.value
                    val full = p.toInt().coerceAtMost(pattern.size - 1)
                    activeNodes = pattern.take(full + 1).toSet()
                    for (i in 0 until full) {
                        drawSegment(
                            nodeCenter(pattern[i], size.width, size.height),
                            nodeCenter(pattern[i + 1], size.width, size.height),
                            lineColor,
                            strokeWidth
                        )
                    }
                    if (full < pattern.size - 1) {
                        val s = nodeCenter(pattern[full], size.width, size.height)
                        val e = nodeCenter(pattern[full + 1], size.width, size.height)
                        drawSegment(s, lerp(s, e, p - full), lineColor, strokeWidth)
                    }
                } else {
                    activeNodes = emptySet()
                }
            } else {
                activeNodes = userPath.toSet()
                for (i in 0 until userPath.size - 1) {
                    drawSegment(
                        nodeCenter(userPath[i], size.width, size.height),
                        nodeCenter(userPath[i + 1], size.width, size.height), lineColor, strokeWidth
                    )
                }
                val finger = fingerPos
                if (phase == PatternPhase.Input && userPath.isNotEmpty() && finger != null) {
                    drawSegment(
                        nodeCenter(userPath.last(), size.width, size.height),
                        finger, lineColor.copy(alpha = 0.5f), strokeWidth
                    )
                }
            }

            for (i in 0 until GRID * GRID) {
                val c = nodeCenter(i, size.width, size.height)
                val active = i in activeNodes
                drawCircle(
                    color = if (active) lineColor else idle,
                    radius = if (active) dotRadius * 1.3f else dotRadius,
                    center = c
                )
                if (active) {
                    drawCircle(
                        color = lineColor.copy(alpha = 0.4f),
                        radius = ringRadius,
                        center = c,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawSegment(start: Offset, end: Offset, color: Color, width: Float) {
    drawLine(color = color, start = start, end = end, strokeWidth = width, cap = StrokeCap.Round)
}