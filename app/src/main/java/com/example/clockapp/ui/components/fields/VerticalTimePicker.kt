package com.example.clockapp.ui.components.fields

import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clockapp.R
import com.example.clockapp.database.AppDatabase
import com.example.clockapp.entity.SettingEntity.Companion.TICKER_SOUND
import com.example.clockapp.ui.theme.LocalExtendedColors
import com.example.clockapp.viewModels.SettingViewModel
import com.example.clockapp.viewModels.factory.SettingViewModelFactory
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun VerticalTimePicker(
    title: String,
    minValue: Int,
    maxValue: Int,
    selectedValue: Int,
    onValueChange: (Int) -> Unit,
    pickerSize: Dp = 90.dp,
    headingSize: TextUnit = 18.sp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            fontSize = headingSize,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        VerticalPicker(
            minValue = minValue,
            maxValue = maxValue,
            selectedValue = selectedValue,
            onValueChange = onValueChange,
            pickerSize = pickerSize
        )
    }
}

@Composable
fun VerticalPicker(
    minValue: Int,
    maxValue: Int,
    selectedValue: Int,
    onValueChange: (Int) -> Unit,
    pickerSize: Dp
) {
    val rangeSize = maxValue.toLong() - minValue.toLong() + 1L

    val context = LocalContext.current
    val database = remember(context) {
        AppDatabase.getInstance(context)
    }

    val settingFactory = remember(database) {
        SettingViewModelFactory(database.settingDao())
    }

    val settingViewModel: SettingViewModel = viewModel(factory = settingFactory)

    settingViewModel.load(TICKER_SOUND)

    val tickSoundStatus by settingViewModel.setting.collectAsStateWithLifecycle()


    val itemCount = rangeSize.toInt()
    val virtualItemCount = Int.MAX_VALUE

    val itemHeight = 40.dp
    val pickerHeight = 200.dp
    val verticalPadding = (pickerHeight - itemHeight) / 2
    val commonColor = LocalExtendedColors.current.commonContentColor

    val selectedIndex = selectedValue
        .coerceIn(minValue, maxValue) - minValue



    key(minValue, maxValue) {
        val initialIndex = remember {
            val middle = virtualItemCount / 2


            val cycleStart = middle - middle % itemCount

            (cycleStart.toLong() + selectedIndex)
                .coerceAtMost(virtualItemCount.toLong() - 1L)
                .toInt()
        }
        val context = LocalContext.current
        val haptic = LocalHapticFeedback.current

        val soundPool = remember {
            SoundPool.Builder()
                .setMaxStreams(4)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .build()
        }
        var clickSoundId by remember { mutableIntStateOf(0) }
        var soundLoaded by remember { mutableStateOf(false) }

        DisposableEffect(soundPool) {
            soundPool.setOnLoadCompleteListener { _, _, status ->
                soundLoaded = status == 0
            }
            clickSoundId = soundPool.load(context, R.raw.picker_click, 1)
            onDispose { soundPool.release() }
        }

        val listState = rememberLazyListState(
            initialFirstVisibleItemIndex = initialIndex
        )

        val scope = rememberCoroutineScope()
        val currentOnValueChange by rememberUpdatedState(onValueChange)

        val flingBehavior = rememberSnapFlingBehavior(
            lazyListState = listState,
            snapPosition = SnapPosition.Center
        )

        fun valueAt(index: Int): Int =
            minValue + index % itemCount

        fun centeredItem(): LazyListItemInfo? {
            val layout = listState.layoutInfo
            val center = (
                    layout.viewportStartOffset +
                            layout.viewportEndOffset
                    ) / 2f

            return layout.visibleItemsInfo.minByOrNull { item ->
                abs(item.offset + item.size / 2f - center)
            }
        }

        val currentSelectedValue by rememberUpdatedState(selectedValue)

        fun nearestIndex(
            currentIndex: Int,
            valueIndex: Int
        ): Int {
            val cycleStart =
                currentIndex.toLong() -
                        currentIndex % itemCount

            val candidate = cycleStart + valueIndex
            val lastIndex = virtualItemCount.toLong() - 1L

            return listOf(
                candidate - itemCount,
                candidate,
                candidate + itemCount
            )
                .filter { it in 0L..lastIndex }
                .minByOrNull { abs(it - currentIndex.toLong()) }!!
                .toInt()
        }

        LaunchedEffect(selectedIndex) {
            if (!listState.isScrollInProgress) {
                val currentItem = centeredItem()
                val currentIndex =
                    currentItem?.index
                        ?: listState.firstVisibleItemIndex

                val targetIndex = nearestIndex(
                    currentIndex = currentIndex,
                    valueIndex = selectedIndex
                )

                val layout = listState.layoutInfo
                val center = (layout.viewportStartOffset + layout.viewportEndOffset) / 2f

                val isCentered = currentItem != null && currentItem.index == targetIndex &&
                        abs(currentItem.offset + currentItem.size / 2f - center) <= 1f

                if (!isCentered) {
                    listState.animateScrollToItem(targetIndex)
                }
            }
        }


        val tick = remember { TickPlayer(context, R.raw.picker_click) }
        DisposableEffect(Unit) { onDispose { tick.release() } }

        if (tickSoundStatus?.getValue() == true) {
            LaunchedEffect(listState) {
                launch {
                    snapshotFlow { centeredItem()?.index }
                        .filterNotNull()
                        .distinctUntilChanged()
                        .drop(1)
                        .collect {
                            if (listState.isScrollInProgress) tick.play()
                        }
                }
                launch {
                    snapshotFlow { listState.isScrollInProgress }
                        .distinctUntilChanged()
                        .filter { !it }
                        .collect { tick.stop() }
                }
            }
        }

        LaunchedEffect(listState) {
            snapshotFlow { listState.isScrollInProgress }
                .filter { !it }
                .map { centeredItem()?.index }
                .filterNotNull()
                .collect { index ->
                    val value = valueAt(index)
                    if (value != currentSelectedValue) {
                        currentOnValueChange(value)
                    }
                }
        }

        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            contentPadding = PaddingValues(
                vertical = verticalPadding
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(pickerSize)
                .height(pickerHeight)
        ) {
            items(
                count = virtualItemCount,
                key = { index -> index }
            ) { index ->
                val value = valueAt(index)

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .clickable {
                            scope.launch { listState.animateScrollToItem(index) }
                        }
                ) {
                    Text(
                        text = value.toString().padStart(2, '0'),
                        fontSize = 28.sp,
                        color = if (value == selectedValue) {
                            commonColor
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier.graphicsLayer {
                            val layout = listState.layoutInfo
                            val itemInfo = layout.visibleItemsInfo
                                .firstOrNull { it.index == index }

                            if (itemInfo != null) {
                                val center =
                                    (layout.viewportStartOffset + layout.viewportEndOffset) / 2f

                                val itemCenter = itemInfo.offset + itemInfo.size / 2f

                                val distance = abs(itemCenter - center) / itemInfo.size.toFloat()

                                val scale = (1f - distance * 0.2f).coerceIn(0.5f, 1f)

                                scaleX = scale
                                scaleY = scale
                                alpha = (1f - distance * 0.25f)
                                    .coerceIn(0.3f, 1f)
                            } else {
                                scaleX = 0.5f
                                scaleY = 0.5f
                                alpha = 0.3f
                            }
                        }
                    )
                }
            }
        }
    }
}