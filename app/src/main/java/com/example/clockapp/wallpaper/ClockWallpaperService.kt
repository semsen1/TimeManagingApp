package com.example.clockapp.wallpaper

import android.graphics.Canvas
import android.os.Build
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import androidx.annotation.RequiresApi
import com.example.clockapp.database.wallpaperDao
import com.example.clockapp.entity.BlockType
import com.example.clockapp.entity.WallpaperBlockDefaults
import com.example.clockapp.entity.WallpaperEntity
import com.example.clockapp.ui.theme.currentExtendedColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ClockWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine = ClockEngine()

    inner class ClockEngine : Engine() {

        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        private val renderer = WallpaperRenderer(resources.displayMetrics.density)
        private val dao = applicationContext.wallpaperDao
        private val contentProvider = BlockContentProvider(applicationContext)

        private var blocks: List<WallpaperEntity> = emptyList()
        private var content: Map<BlockType, BlockContent> = emptyMap()
        private var updatesJob: Job? = null

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            scope.launch { dao.insertDefaults(WallpaperBlockDefaults.all) }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onVisibilityChanged(visible: Boolean) {
            if (visible) startUpdates() else stopUpdates()
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            drawFrame()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            stopUpdates()
            super.onSurfaceDestroyed(holder)
        }

        override fun onDestroy() {
            scope.cancel()
            super.onDestroy()
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun startUpdates() {
            stopUpdates()
            updatesJob = scope.launch {
                combine(dao.observeAll(), contentProvider.observe()) { b, c -> b to c }
                    .collectLatest { (b, c) ->
                        blocks = b
                        content = c
                        drawFrame()
                        while (true) {
                            delay(nextDelay())
                            drawFrame()
                        }
                    }
            }
        }

        private fun stopUpdates() {
            updatesJob?.cancel()
            updatesJob = null
        }

        private fun nextDelay(): Long {
            val period = blocks.asSequence()
                .filter { it.isVisible }
                .mapNotNull { content[it.type]?.refreshMs }
                .minOrNull()
                ?: BlockContent.DEFAULT_REFRESH_MS
            return period - System.currentTimeMillis() % period
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun drawFrame() {
            val holder = surfaceHolder
            if (!holder.surface.isValid) return
            var canvas: Canvas? = null
            try {
                canvas = holder.lockCanvas()
                canvas?.let {
                    renderer.draw(
                        it, blocks, content,
                        this@ClockWallpaperService.currentExtendedColors(),
                        System.currentTimeMillis()
                    )
                }
            } finally {
                canvas?.let { holder.unlockCanvasAndPost(it) }
            }
        }
    }
}