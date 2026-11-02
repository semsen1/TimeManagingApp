package com.example.clockapp.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.clockapp.R
import com.example.clockapp.entity.BlockType
import com.example.clockapp.entity.WallpaperEntity
import com.example.clockapp.navigation.Routes
import com.example.clockapp.ui.components.fields.TimeZonePicker
import com.example.clockapp.ui.theme.LocalExtendedColors
import com.example.clockapp.viewModels.WallpaperViewModel
import java.time.ZoneId
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WallpaperSettings(
    navController: NavHostController,
    viewModel: WallpaperViewModel,
) {
    val blocks by viewModel.blocks.collectAsStateWithLifecycle()
    val commonColor = LocalExtendedColors.current.commonContentColor
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(
                onClick = { navController.navigate(Routes.Home.route) }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = commonColor
                )
            }
        }
        WallpaperPreview(
            blocks = blocks,
            onLayoutChanged = viewModel::setLayout,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterHorizontally)
        )
        LazyColumn(Modifier.weight(1f)) {
            items(blocks, key = { it.id }) { block ->
                BlockSettingsRow(
                    block = block,
                    onVisibleChange = { viewModel.setVisible(block.id, it) },
                    onZoneChange = { viewModel.setTimeZone(block.id, it) }
                )
            }
            item {
                TextButton(
                    onClick = viewModel::resetToDefaults,
                )
                {
                    Text(
                        text = stringResource(R.string.reset_layout),
                        color = commonColor
                    )
                }
            }
        }
    }
}

private data class BlockFrame(val x: Float, val y: Float, val w: Float, val h: Float)

@Composable
private fun WallpaperPreview(
    blocks: List<WallpaperEntity>,
    onLayoutChanged: (String, Float, Float, Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val metrics = LocalResources.current.displayMetrics
    val ratio = metrics.widthPixels.toFloat() / metrics.heightPixels
    val widgetBackground = LocalExtendedColors.current.wallpaperBackgroundColor

    BoxWithConstraints(
        modifier
            .aspectRatio(ratio)
            .background(widgetBackground, RoundedCornerShape(12.dp))
    ) {
        val wPx = constraints.maxWidth.toFloat()
        val hPx = constraints.maxHeight.toFloat()
        blocks.filter { it.isVisible }.forEach { block ->
            key(block.id) { PreviewBlock(block, wPx, hPx, onLayoutChanged) }
        }
    }
}

@Composable
private fun PreviewBlock(
    block: WallpaperEntity,
    wPx: Float,
    hPx: Float,
    onCommit: (String, Float, Float, Float, Float) -> Unit
) {

    val commonColor = LocalExtendedColors.current.commonContentColor
    val blockBackground = LocalExtendedColors.current.wallpaperBlockColor

    var frame by remember(block.id) {
        mutableStateOf(BlockFrame(block.x, block.y, block.width, block.height))
    }
    var dragging by remember(block.id) { mutableStateOf(false) }

    LaunchedEffect(block.x, block.y, block.width, block.height) {
        if (!dragging) frame = BlockFrame(block.x, block.y, block.width, block.height)
    }

    val minW = 0.2f
    val minH = 0.06f
    val title = stringResource(block.type.title())
    val shape = remember { RoundedCornerShape(8.dp) }
    val currentOnCommit by rememberUpdatedState(onCommit)

    fun endDrag() {
        dragging = false
        currentOnCommit(block.id, frame.x, frame.y, frame.w, frame.h)
    }

    Box(
        Modifier
            .offset { IntOffset((frame.x * wPx).roundToInt(), (frame.y * hPx).roundToInt()) }
            .layout { measurable, _ ->
                val w = (frame.w * wPx).roundToInt()
                val h = (frame.h * hPx).roundToInt()
                val placeable = measurable.measure(Constraints.fixed(w, h))
                layout(w, h) { placeable.place(0, 0) }
            }
            .background(blockBackground, shape)
            .pointerInput(block.id, wPx, hPx) {
                detectDragGestures(
                    onDragStart = { dragging = true },
                    onDragEnd = { endDrag() },
                    onDragCancel = { endDrag() }
                ) { change, drag ->
                    change.consume()
                    val f = frame
                    frame = f.copy(
                        x = (f.x + drag.x / wPx).coerceIn(0f, 1f - f.w),
                        y = (f.y + drag.y / hPx).coerceIn(0f, 1f - f.h)
                    )
                }
            }
    ) {
        Text(
            title,
            color = commonColor,
            fontSize = 11.sp,
            modifier = Modifier.padding(6.dp)
        )
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .size(40.dp)
                .pointerInput(block.id, wPx, hPx) {
                    detectDragGestures(
                        onDragStart = { dragging = true },
                        onDragEnd = { endDrag() },
                        onDragCancel = { endDrag() }
                    ) { change, drag ->
                        change.consume()
                        val f = frame
                        frame = f.copy(
                            w = (f.w + drag.x / wPx).coerceIn(minW, 1f - f.x),
                            h = (f.h + drag.y / hPx).coerceIn(minH, 1f - f.y)
                        )
                    }
                }
        ) {
            Box(
                Modifier
                    .align(Alignment.BottomEnd)

                    .size(20.dp)
                    .background(Color(0x66FFFFFF), RoundedCornerShape(8.dp))
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun BlockSettingsRow(
    block: WallpaperEntity,
    onVisibleChange: (Boolean) -> Unit,
    onZoneChange: (ZoneId?) -> Unit
) {
    var pickerOpen by remember { mutableStateOf(false) }
    val commonColor = LocalExtendedColors.current.commonContentColor
    val buttonBackgroundColor = LocalExtendedColors.current.buttonsBackground

    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(stringResource(block.type.title()), Modifier.weight(1f))
        if (block.type == BlockType.CLOCK) {
            TextButton(onClick = { pickerOpen = true }) {
                Text(
                    text = block.timeZone?.id ?: stringResource(R.string.local),
                    color = commonColor
                )
            }
        }
        Switch(
            checked = block.isVisible,
            onCheckedChange = onVisibleChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = buttonBackgroundColor,
                uncheckedTrackColor = buttonBackgroundColor,
                checkedThumbColor = commonColor,
                uncheckedThumbColor = Color.DarkGray,
                checkedBorderColor = Color.Transparent,
                uncheckedBorderColor = Color.Transparent
            ),
        )
    }

    if (pickerOpen) {
        Dialog(
            onDismissRequest = { pickerOpen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            TimeZonePicker(
                title = stringResource(R.string.time_zone),
                onSelected = { id ->
                    onZoneChange(ZoneId.of(id))
                    pickerOpen = false
                },
                onReset = {
                    onZoneChange(null)
                    pickerOpen = false
                },
                onDismiss = { pickerOpen = false }
            )
        }
    }
}

private fun BlockType.title(): Int = when (this) {
    BlockType.CLOCK -> R.string.clock
    BlockType.ALARM -> R.string.alarm
    BlockType.REMINDER -> R.string.reminder
    BlockType.SCHEDULE -> R.string.schedule
    BlockType.TIMER -> R.string.timer
    BlockType.STOPWATCH -> R.string.stopwatch
}