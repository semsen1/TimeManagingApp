package com.example.clockapp.notificationSystem.core

import android.content.Intent
import android.net.Uri

data class RingKey(val type: RingType, val id: Int) {

    fun toUri(slot: RingSlot? = null): Uri = Uri.Builder()
        .scheme("ring")
        .authority(type.code)
        .appendPath(id.toString())
        .apply { slot?.let { appendPath(it.code) } }
        .build()

    fun applyTo(intent: Intent): Intent = intent
        .setData(toUri())
        .putExtra(RingIntents.EXTRA_TYPE, type.code)
        .putExtra(RingIntents.EXTRA_ID, id)

    companion object {
        fun from(intent: Intent?): RingKey? {
            intent ?: return null
            val type = RingType.fromCode(intent.getStringExtra(RingIntents.EXTRA_TYPE))
                ?: return null
            val id = intent.getIntExtra(RingIntents.EXTRA_ID, -1)
            return if (id >= 0) RingKey(type, id) else null
        }
    }
}