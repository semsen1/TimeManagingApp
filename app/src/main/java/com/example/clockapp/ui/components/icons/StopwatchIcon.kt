@file:JvmName("StopwatchIconKt")

package com.example.clockapp.ui.components.icons

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.clockapp.R
import com.example.clockapp.ui.theme.LocalExtendedColors


private val HandPivot = TransformOrigin(pivotFractionX = 256f / 512f, pivotFractionY = 296f / 512f)

@Composable
fun StopwatchIcon(
    isRunning: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    periodMillis: Int = 6000
) {
    val rotation = remember { Animatable(0f) }
    val commonColor = LocalExtendedColors.current.commonContentColor

    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (true) {
                val remaining = ((360f - rotation.value) / 360f * periodMillis)
                    .toInt()
                    .coerceAtLeast(1)
                rotation.animateTo(
                    targetValue = 360f,
                    animationSpec = tween(durationMillis = remaining, easing = LinearEasing)
                )
                rotation.snapTo(0f)
            }
        }
    }

    Box(modifier = modifier.size(size)) {
        Icon(
            painter = painterResource(R.drawable.ic_stopwatch_body),
            contentDescription = null,
            modifier = Modifier.size(size),
            tint = commonColor
        )
        Icon(
            painter = painterResource(R.drawable.ic_stopwatch_hand),
            contentDescription = null,
            modifier = Modifier
                .size(size)
                .graphicsLayer {
                    rotationZ = rotation.value
                    transformOrigin = HandPivot
                },
            tint = commonColor
        )
    }
}