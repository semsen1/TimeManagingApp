package com.example.clockapp.ui.components.icons

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.PathParser
import com.example.clockapp.ui.theme.LocalExtendedColors
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

private const val GLASS_PATH =
    "M142.5,51.4c-9.4,2.3 -16.7,8.4 -20.9,17.6 -2.3,4.8 -2.6,6.8 -2.6,16 0,9 0.4,11.2 2.5,15.7 4.9,10.6 16.4,18.3 27.3,18.3l4.7,-0 0.1,16.7c0.1,19.3 1.6,28.1 8.6,49.2 4.7,14.1 17,39.3 26.8,54.9 11.3,18.1 29.1,41 46.9,60.5 4.7,5.1 7.1,8.6 7.1,10.2 0,1.6 -3.1,5.8 -9.6,13.2 -43.2,48.4 -68.3,92.2 -77.5,135.3 -1.8,8.3 -2.3,13.8 -2.3,26.6l-0.1,16.2 -6.1,0.5c-11.6,0.9 -22.2,9.1 -26.5,20.6 -3.1,8.3 -2.5,22.4 1.4,30.4 3.1,6.2 8.6,11.5 15.6,14.9l4.6,2.3 163.5,-0 163.5,-0 5.3,-2.4c6.8,-3 14.5,-11.4 16.7,-18 1.8,-5.6 2.1,-20.5 0.5,-26.1 -2.6,-8.9 -12.9,-18.8 -21.8,-20.9l-4.8,-1.2 0.4,-13.4c0.9,-30.8 -9.1,-63 -31.2,-100.3 -11.6,-19.5 -32.4,-47 -49,-64.8 -8.5,-9.1 -10.2,-12.4 -8.4,-15.7 0.6,-1.2 4.6,-5.9 9,-10.5 28.1,-29.8 55.8,-72.2 68.1,-104.3 8.8,-22.7 12.2,-40.6 11.5,-60.8l-0.4,-12.9 5.9,-1.6c4.7,-1.3 6.9,-2.8 11.6,-7.4 7.4,-7.3 10.1,-13.9 10.1,-25.1 0,-15.2 -5.5,-25 -17.4,-31.1l-6.9,-3.5 -160.6,-0.2c-124.6,-0.1 -161.7,0.1 -165.6,1.1z" +
            "M432.4,121.2c0.3,1.3 0.6,6.3 0.6,11.1 0,40.8 -32,98.8 -87.2,157.7 -10,10.6 -11.2,12.9 -11.2,20.5 0,7.8 1.3,10.2 12.9,22.5 45.8,49.1 76.6,99.9 83.9,138.9 1.6,8.2 2.2,25.5 1,28.5 -0.6,1.4 -12.3,1.6 -122.9,1.6l-122.3,-0 -0.7,-6.1c-2.1,-17.9 4.4,-43.1 18.1,-69.9 15.2,-29.9 35.6,-58.3 64.9,-90.5 7,-7.6 13.4,-15.4 14.1,-17.2 2,-4.6 1.7,-12.2 -0.5,-16.5 -1,-2 -6.6,-8.7 -12.4,-15 -44,-47.1 -74.2,-96.7 -82.3,-134.8 -1.8,-8.6 -2.5,-28 -1.2,-31.3 0.7,-1.6 7.5,-1.7 122.7,-1.7l121.9,-0 0.6,2.2z"

private val HOLE_PATH = "M" + GLASS_PATH.substringAfter("zM")

private const val VB = 612f
private val Pivot = Offset(306f, 311f)
private const val SAND_SCALE = 0.85f
private const val NECK_Y = 311f
private const val UPPER_TOP_Y = 175f
private const val FLOOR_Y = 502f
private const val MOUND_H = 172f
private const val SLOPE = 1.1f
private const val STREAM_TOP_Y = NECK_Y - 12f

@Composable
fun AnimatedHourglass(
    running: Boolean,
    modifier: Modifier = Modifier,
    flowMillis: Int = 2600,
    flipMillis: Int = 900,
) {
    val glass = remember { PathParser().parsePathString(GLASS_PATH).toPath() }
    val hole = remember { PathParser().parsePathString(HOLE_PATH).toPath() }
    val tmp = remember { Path() }
    val progress = remember { Animatable(0f) }
    val cycleMillis = flowMillis + flipMillis
    val flowEnd = flowMillis.toFloat() / cycleMillis

    val color = LocalExtendedColors.current.commonContentColor
    LaunchedEffect(running, cycleMillis) {
        if (!running) return@LaunchedEffect
        while (true) {
            val left = 1f - progress.value
            progress.animateTo(1f, tween((cycleMillis * left).roundToInt(), easing = LinearEasing))
            progress.snapTo(0f)
        }
    }

    Canvas(modifier.aspectRatio(1f)) {
        val p = progress.value
        var angle = 0f
        var upperVol = 0f;
        var upperTurn = 0f;
        var upperAlpha = 1f
        var moundVol = 0f;
        var moundAlpha = 1f
        var stream = -1f

        if (p < flowEnd) {
            val s = p / flowEnd
            upperVol = 1f - s
            moundVol = s
            stream = s
        } else {
            val r = (p - flowEnd) / (1f - flowEnd)
            val fade = smooth((r - 0.45f) / 0.45f)
            angle = 180f * EaseInOutCubic.transform(r)
            moundVol = 1f; moundAlpha = 1f - fade
            upperVol = 1f; upperTurn = 180f; upperAlpha = fade
        }

        val k = size.minDimension / VB
        withTransform({ scale(k, k, Offset.Zero); rotate(angle, Pivot) }) {
            drawPath(glass, color)
            drawMound(hole, tmp, moundVol, moundAlpha, color)
            withTransform({ rotate(upperTurn, Pivot) }) {
                drawUpperSand(hole, upperVol, upperAlpha, color)
            }
            if (stream >= 0f) drawStream(stream, color)
        }
    }
}

private fun smooth(x: Float): Float {
    val t = x.coerceIn(0f, 1f)
    return t * t * (3f - 2f * t)
}

private fun DrawScope.drawUpperSand(hole: Path, vol: Float, alpha: Float, color: Color) {
    if (vol <= 0f || alpha <= 0f) return
    val level = NECK_Y - (NECK_Y - UPPER_TOP_Y) * vol.pow(0.65f)
    withTransform({ scale(SAND_SCALE, SAND_SCALE, Pivot) }) {
        clipPath(hole) {
            drawRect(color, Offset(0f, level), Size(VB, NECK_Y - level), alpha = alpha)
        }
    }
}

private fun DrawScope.drawMound(hole: Path, tmp: Path, vol: Float, alpha: Float, color: Color) {
    if (vol <= 0f || alpha <= 0f) return
    val apex = FLOOR_Y - MOUND_H * vol.pow(0.6f)
    val bottom = FLOOR_Y + 40f
    val w = 150f
    val dx = min(w, (bottom - apex) / SLOPE)
    val edgeY = apex + SLOPE * dx
    val cx = Pivot.x
    tmp.reset()
    tmp.moveTo(cx, apex)
    tmp.lineTo(cx + dx, edgeY)
    tmp.lineTo(cx + w, edgeY)
    tmp.lineTo(cx + w, bottom)
    tmp.lineTo(cx - w, bottom)
    tmp.lineTo(cx - w, edgeY)
    tmp.lineTo(cx - dx, edgeY)
    tmp.close()
    withTransform({ scale(SAND_SCALE, SAND_SCALE, Pivot) }) {
        clipPath(hole) { drawPath(tmp, color, alpha = alpha) }
    }
}

private fun DrawScope.drawStream(s: Float, color: Color) {
    val apex = FLOOR_Y - MOUND_H * s.pow(0.6f)
    val grow = smooth(s / 0.08f)
    val cut = smooth((s - 0.92f) / 0.08f)
    val top = STREAM_TOP_Y + (apex - STREAM_TOP_Y) * cut
    val bottom = STREAM_TOP_Y + (apex - STREAM_TOP_Y) * grow
    if (bottom - top < 1f) return
    withTransform({ scale(SAND_SCALE, SAND_SCALE, Pivot) }) {
        drawLine(color, Offset(Pivot.x, top), Offset(Pivot.x, bottom), strokeWidth = 7f)
    }
}