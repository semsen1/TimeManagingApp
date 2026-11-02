package com.example.clockapp.ui.components.icons

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.clockapp.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun ReminderIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    val image = AnimatedImageVector.animatedVectorResource(R.drawable.ic_reminder)
    var atEnd by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        while (true) {
            delay(1200)
            atEnd = !atEnd
        }
    }

    Icon(
        painter = rememberAnimatedVectorPainter(animatedImageVector = image, atEnd = atEnd),
        contentDescription = null,
        tint = tint,
        modifier = modifier
    )
}