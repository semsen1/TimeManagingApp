package com.example.clockapp.entity

import android.content.Context
import android.content.Intent
import android.net.Uri

enum class TriggerKind {
    REGULAR,
    SNOOZE
}

object EventIntents {
    const val EXTRA_TYPE = "event_type"
    const val EXTRA_ID = "event_id"
    const val EXTRA_SLOT = "event_slot"
    const val EXTRA_TRIGGER_KIND = "event_trigger_kind"
}

fun Intent.putEventKey(key: EventKey): Intent = apply {
    putExtra(EventIntents.EXTRA_TYPE, key.type.name)
    putExtra(EventIntents.EXTRA_ID, key.entityId)
    putExtra(EventIntents.EXTRA_SLOT, key.slot.name)
}

fun Intent.readEventKey(): EventKey? {
    val id = getIntExtra(EventIntents.EXTRA_ID, -1)
    if (id <= 0) return null

    return runCatching {
        EventKey(
            type = EventType.valueOf(
                requireNotNull(getStringExtra(EventIntents.EXTRA_TYPE))
            ),
            entityId = id,
            slot = EventSlot.valueOf(
                requireNotNull(getStringExtra(EventIntents.EXTRA_SLOT))
            )
        )
    }.getOrNull()
}

fun Intent.readTriggerKind(): TriggerKind? =
    runCatching {
        TriggerKind.valueOf(
            requireNotNull(
                getStringExtra(EventIntents.EXTRA_TRIGGER_KIND)
            )
        )
    }.getOrNull()

fun eventUri(
    context: Context,
    key: EventKey,
    purpose: String
): Uri =
    Uri.Builder()
        .scheme("app-event")
        .authority(context.packageName)
        .appendPath(key.type.name)
        .appendPath(key.entityId.toString())
        .appendPath(key.slot.name)
        .appendPath(purpose)
        .build()