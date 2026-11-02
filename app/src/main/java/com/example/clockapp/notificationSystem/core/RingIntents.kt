package com.example.clockapp.notificationSystem.core

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.clockapp.notificationSystem.service.RingReceiver
import com.example.clockapp.notificationSystem.service.RingService
import com.example.clockapp.notificationSystem.ui.RingActivity

object RingIntents {
    const val EXTRA_KIND = "extra_kind"
    const val ACTION_FIRE = "ring.FIRE"
    const val ACTION_SNOOZE = "ring.SNOOZE"
    const val ACTION_DISMISS = "ring.DISMISS"

    const val EXTRA_TYPE = "ring_type"
    const val EXTRA_ID = "ring_id"
    const val EXTRA_SLOT = "ring_slot"

    const val ACTION_HIDE_NOTIFICATION = "ring.HIDE_NOTIFICATION"
    const val ACTION_SHOW_NOTIFICATION = "ring.SHOW_NOTIFICATION"

    const val ACTION_MUTE = "ring.ACTION_MUTE"

    const val ACTION_UNMUTE = "ring.UNMUTE"
    const val ACTION_SUPPRESS_VIBRATION = "ring.SUPPRESS_VIBRATION"
    const val ACTION_RESUME_VIBRATION = "ring.RESUME_VIBRATION"

    fun fire(context: Context, key: RingKey, slot: RingSlot): Intent =
        receiver(context, key, ACTION_FIRE)
            .setData(key.toUri(slot))
            .putExtra(EXTRA_SLOT, slot.code)

    fun firePending(context: Context, key: RingKey, slot: RingSlot): PendingIntent =
        PendingIntent.getBroadcast(context, key.id, fire(context, key, slot), PI_FLAGS)

    fun service(
        context: Context,
        key: RingKey,
        action: String? = null,
        slot: RingSlot = RingSlot.MAIN
    ): Intent =
        key.applyTo(Intent(context, RingService::class.java).apply { this.action = action })
            .putExtra(EXTRA_SLOT, slot.code)

    private const val PI_FLAGS = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

    fun receiver(context: Context, key: RingKey, action: String): Intent =
        key.applyTo(Intent(context, RingReceiver::class.java).setAction(action))


    fun activity(context: Context, key: RingKey): Intent =
        key.applyTo(
            Intent(context, RingActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        )

    fun activityPending(context: Context, key: RingKey): PendingIntent =
        PendingIntent.getActivity(context, key.id, activity(context, key), PI_FLAGS)

    fun broadcastPending(context: Context, key: RingKey, action: String): PendingIntent =
        PendingIntent.getBroadcast(context, key.id, receiver(context, key, action), PI_FLAGS)

    fun suppressVibration(context: Context, key: RingKey): Intent =
        service(context, key, ACTION_SUPPRESS_VIBRATION)

    fun resumeVibration(context: Context, key: RingKey): Intent =
        service(context, key, ACTION_RESUME_VIBRATION)
}