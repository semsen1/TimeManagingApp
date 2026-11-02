package com.example.clockapp.notificationSystem.core

import android.content.Intent

enum class RingSlot(val code: String) {
    MAIN("main"),
    PRE("pre"),
    SNOOZE("snooze");

    companion object {
        fun from(intent: Intent?): RingSlot =
            entries.firstOrNull {
                it.code == intent?.getStringExtra(RingIntents.EXTRA_SLOT)
            } ?: MAIN
    }
}