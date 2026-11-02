package com.example.clockapp.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.clockapp.notificationSystem.core.Ringable
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
fun <T : Ringable> List<T>.nextUpcoming(
    now: LocalDateTime = LocalDateTime.now(),
): UpcomingEntity<T>? {
    val nowMinute = now.truncatedTo(ChronoUnit.MINUTES)
    return asSequence()
        .filter { it.isRingEnabled }
        .mapNotNull { item ->
            item.nextTrigger(now)?.let { trigger ->
                UpcomingEntity(
                    entity = item,
                    triggerAt = trigger,
                    minutesLeft = Duration.between(nowMinute, trigger).toMinutes(),
                )
            }
        }
        .minByOrNull { it.triggerAt }
}