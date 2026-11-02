package com.example.clockapp.wallpaper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.withClip
import com.example.clockapp.entity.BlockType
import com.example.clockapp.entity.WallpaperEntity
import com.example.clockapp.ui.theme.ExtendedColors
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class BlockContent(
    val title: String,
    val refreshMs: Long = DEFAULT_REFRESH_MS,
    val lines: () -> List<String>
) {
    companion object {
        const val DEFAULT_REFRESH_MS = 60_000L
    }
}

class WallpaperRenderer(private val density: Float) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = RectF()

    @RequiresApi(Build.VERSION_CODES.O)
    private val timeFormat = DateTimeFormatter.ofPattern("HH:mm")
    private val lightFont = Typeface.create("sans-serif-light", Typeface.NORMAL)

    private class Cached(var at: Long = -1L, var lines: List<String> = emptyList())

    private val linesCache = mutableMapOf<BlockType, Cached>()


    private fun cachedLines(type: BlockType, c: BlockContent, now: Long): List<String> {
        val e = linesCache.getOrPut(type) { Cached() }
        if (e.at < 0 || now - e.at >= c.refreshMs) {
            e.lines = c.lines()
            e.at = now
        }
        return e.lines
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun draw(
        canvas: Canvas,
        blocks: List<WallpaperEntity>,
        content: Map<BlockType, BlockContent>,
        colors: ExtendedColors,
        nowMillis: Long
    ) {
        val w = canvas.width.toFloat()
        val h = canvas.height.toFloat()
        canvas.drawColor(colors.wallpaperBackgroundColor.toArgb())

        blocks.filter { it.isVisible }.sortedBy { it.zIndex }.forEach { block ->
            rect.set(
                block.x * w,
                block.y * h,
                (block.x + block.width) * w,
                (block.y + block.height) * h
            )

            paint.style = Paint.Style.FILL
            paint.color = colors.wallpaperBlockColor.toArgb()
            canvas.drawRoundRect(rect, 20f * density, 20f * density, paint)

            canvas.withClip(rect) {
                if (block.type == BlockType.CLOCK) {
                    drawClock(this, block, nowMillis, colors)
                } else {
                    drawTextBlock(this, block.type, content[block.type], nowMillis, colors)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun drawClock(
        canvas: Canvas,
        block: WallpaperEntity,
        nowMillis: Long,
        colors: ExtendedColors
    ) {
        val zone = block.timeZone ?: ZoneId.systemDefault()
        val time = timeFormat.format(Instant.ofEpochMilli(nowMillis).atZone(zone))
        val label = if (block.timeZone == null) ""
        else zone.id.substringAfterLast('/').replace('_', ' ')
        val pad = 16f * density

        paint.typeface = Typeface.DEFAULT
        paint.textAlign = Paint.Align.LEFT
        paint.color = colors.commonContentColor.toArgb()
        paint.textSize = (rect.height() * 0.22f).coerceAtMost(16f * density)
        canvas.drawText(label, rect.left + pad, rect.centerY() + paint.textSize * 0.35f, paint)

        paint.typeface = lightFont
        paint.textAlign = Paint.Align.RIGHT
        paint.color = colors.commonContentColor.toArgb()
        paint.textSize = rect.height() * 0.6f
        canvas.drawText(time, rect.right - pad, rect.centerY() + paint.textSize * 0.35f, paint)
    }

    private fun drawTextBlock(
        canvas: Canvas,
        type: BlockType,
        content: BlockContent?,
        nowMillis: Long,
        colors: ExtendedColors
    ) {
        content ?: return
        val pad = 12f * density
        val titleSize = (rect.height() * 0.2f).coerceAtMost(18f * density)

        paint.typeface = Typeface.DEFAULT
        paint.textAlign = Paint.Align.LEFT
        paint.color = colors.commonContentColor.toArgb()
        paint.textSize = titleSize
        var baseline = rect.top + pad + titleSize
        canvas.drawText(content.title, rect.left + pad, baseline, paint)

        val lineSize = titleSize * 0.85f
        val step = lineSize * 1.3f
        val maxLines = ((rect.bottom - baseline - pad) / step).toInt().coerceAtLeast(0)
        paint.color = colors.commonContentColor.toArgb()
        paint.textSize = lineSize
        cachedLines(type, content, nowMillis).take(maxLines).forEach { line ->
            baseline += step
            canvas.drawText(line, rect.left + pad, baseline, paint)
        }
    }
}