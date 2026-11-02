package com.example.clockapp.ui.components.fields

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.annotation.RawRes

class TickPlayer(context: Context, @RawRes resId: Int) {

    private val pool = SoundPool.Builder()
        .setMaxStreams(1)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    @Volatile private var loaded = false
    private var streamId = 0
    private val soundId: Int

    init {
        pool.setOnLoadCompleteListener { _, _, status -> loaded = status == 0 }
        soundId = pool.load(context, resId, 1)
    }

    fun play() {
        if (!loaded) return
        pool.stop(streamId)
        streamId = pool.play(soundId, 1f, 1f, 1, 0, 1f)
    }

    fun stop() {
        pool.stop(streamId)
        streamId = 0
    }

    fun release() = pool.release()
}