package com.example.clockapp.notificationSystem.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import com.example.clockapp.R
import com.example.clockapp.alarm.AlarmDebug
import com.example.clockapp.notificationSystem.core.RingIntents
import com.example.clockapp.notificationSystem.core.RingKey
import com.example.clockapp.notificationSystem.core.RingRegistry
import com.example.clockapp.notificationSystem.core.RingSlot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class RingService : Service() {

    companion object {
        private const val CHANNEL_ID = "alarm_ringing_v5"
        private const val NOTIFICATION_ID = 1001

        private val _ringingKey = MutableStateFlow<RingKey?>(null)
        val ringingKey: StateFlow<RingKey?> = _ringingKey.asStateFlow()


        private val _muted = MutableStateFlow(false)
        val muted: StateFlow<Boolean> = _muted.asStateFlow()
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var player: MediaPlayer? = null
    private var loadJob: Job? = null
    private var activeKey: RingKey? = null

    private var lastNotification: Notification? = null
    private var notificationHidden = false

    private var currentSound: Uri? = null
    private var soundReady = false

    private var vibrating = false

    private var vibrationSuppressed = false

//    private fun hideNotification() {
//        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_DETACH)
//        NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID)
//    }
//
//    private fun showNotification(
//        key: RingKey,
//        title: String,
//        text: String,
//        snoozeMinutes: Int,
//        wakeUpTest: Int = 0
//    ) {
//
//        startForeground(
//            NOTIFICATION_ID,
//            buildNotification(key, title, text, snoozeMinutes, wakeUpTest)
//        )
//    }


    private fun vibrator(): Vibrator? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSystemService(VibratorManager::class.java)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startVibration() {
        if (vibrationSuppressed) return
        if (_muted.value) return
        val v = vibrator() ?: return
        if (!v.hasVibrator()) return

        val effect = VibrationEffect.createWaveform(longArrayOf(0, 600, 400), 0)
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        @Suppress("DEPRECATION")
        v.vibrate(effect, attrs)
        vibrating = true
    }

    private fun stopVibration() {
        if (!vibrating) return
        vibrator()?.cancel()
        vibrating = false
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun postNotification(n: Notification) {
        lastNotification = n
        if (notificationHidden) return
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, n)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val slot = RingSlot.from(intent)
        val key = RingKey.from(intent)

        val action = intent?.action

        if (action == RingIntents.ACTION_DISMISS) {
            if (key == null || activeKey == null || activeKey == key) stopRinging()
            return START_NOT_STICKY
        }

        if (activeKey == null && (
                    action == RingIntents.ACTION_HIDE_NOTIFICATION ||
                            action == RingIntents.ACTION_SHOW_NOTIFICATION ||
                            action == RingIntents.ACTION_MUTE ||
                            action == RingIntents.ACTION_UNMUTE ||
                            action == RingIntents.ACTION_SUPPRESS_VIBRATION ||
                            action == RingIntents.ACTION_RESUME_VIBRATION)
        ) {
            stopSelf()
            return START_NOT_STICKY
        }

        if (action == RingIntents.ACTION_SUPPRESS_VIBRATION) {
            if (key == null || key == activeKey) {
                vibrationSuppressed = true
                stopVibration()
            }
            return START_NOT_STICKY
        }

        if (action == RingIntents.ACTION_RESUME_VIBRATION) {
            if (key == null || key == activeKey) {
                vibrationSuppressed = false
                startVibration()
            }
            return START_NOT_STICKY
        }

        if (action == RingIntents.ACTION_HIDE_NOTIFICATION) {
            if (key != null && key == activeKey && !notificationHidden) {
                notificationHidden = true
                ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
            }
            return START_NOT_STICKY
        }


        if (action == RingIntents.ACTION_MUTE) {
            if (key == null || key == activeKey) {
                _muted.value = true
                releasePlayer()
            }
            return START_NOT_STICKY
        }

        if (action == RingIntents.ACTION_UNMUTE) {
            if (key == null || key == activeKey) resumeSound()
            return START_NOT_STICKY
        }


        if (action == RingIntents.ACTION_SHOW_NOTIFICATION) {
            if (key == null || key == activeKey) {
                val n = lastNotification
                if (notificationHidden && n != null) {
                    notificationHidden = false
                    try {
                        startForeground(NOTIFICATION_ID, n)
                    } catch (e: Exception) {
//                        AlarmDebug.e("Service: re-startForeground FAILED, $key", e)
                        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, n)
                    }
                }
                resumeSound()
            }
            return START_NOT_STICKY
        }

        if (key == null) {
            stopSelf()
            return START_NOT_STICKY
        }

        activeKey = key
        _muted.value = false
        soundReady = false
        currentSound = null
        notificationHidden = false
        lastNotification = null



        ensureChannel()
        try {
            val first = buildNotification(
                key,
                getString(key.type.defaultTitleRes),
                getString(key.type.ringingTextRes),
                0
            )
            lastNotification = first
            startForeground(NOTIFICATION_ID, first)
        } catch (e: Exception) {
//            AlarmDebug.e("Service: startForeground FAILED, $key", e)
            stopSelf()
            return START_NOT_STICKY
        }

        loadJob?.cancel()
        releasePlayer()

        loadJob = scope.launch {
            try {
                val handler = RingRegistry.handlerOf(applicationContext, key.type)
                val target = withContext(Dispatchers.IO) { handler.getById(key.id) }

                if (target == null) {
//                    AlarmDebug.e("Service: record $key not found")
                    stopRinging()
                    return@launch
                }

                _ringingKey.value = key

                val text = if (slot == RingSlot.PRE && target.ringLeadMinutes > 0)
                    getString(R.string.ring_pre_text, target.ringLeadMinutes)
                else
                    getString(key.type.ringingTextRes)

                postNotification(
                    buildNotification(
                        key,
                        target.ringTitle.ifBlank { getString(key.type.defaultTitleRes) },
                        text,
                        if (slot == RingSlot.PRE) 0 else target.ringSnoozeMinutes,
                        wakeUpTest = target.wakeUpTest
                    )
                )
                playSound(target.ringSound)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
//                AlarmDebug.e("Service: loading/launch error, $key", e)
                stopRinging()
            }
        }
        return START_NOT_STICKY
    }

    @SuppressLint("FullScreenIntentPolicy")
    private fun buildNotification(
        key: RingKey, title: String, text: String, snoozeMinutes: Int, wakeUpTest: Int = 0
    ): Notification {
        val screenIntent = RingIntents.activityPending(this, key)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle(title)
            .setContentText(text)
            .setContentText(getString(key.type.ringingTextRes))
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setContentIntent(screenIntent)
            .setFullScreenIntent(screenIntent, true)
            .apply {
                if (wakeUpTest == 0) {
                    addAction(
                        0, getString(R.string.close),
                        RingIntents.broadcastPending(
                            this@RingService,
                            key,
                            RingIntents.ACTION_DISMISS
                        )
                    )
                } else {

                    addAction(
                        0, getString(R.string.take_test),
                        screenIntent
                    )
                }
            }
            .apply {
                if (snoozeMinutes > 0) {
                    addAction(
                        0, getString(R.string.postpone_for, snoozeMinutes),
                        RingIntents.broadcastPending(
                            this@RingService,
                            key,
                            RingIntents.ACTION_SNOOZE
                        )
                    )
                }
            }
            .build()
    }

    private fun stopRinging() {
        loadJob?.cancel()
        releasePlayer()
        _ringingKey.value = null
        _muted.value = false
        soundReady = false
        currentSound = null
        activeKey = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        notificationHidden = false
        lastNotification = null
        vibrationSuppressed = false
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        scope.cancel()
        releasePlayer()
        _ringingKey.value = null
        _muted.value = false
        super.onDestroy()
    }

    private fun releasePlayer() {
        player?.release()
        player = null
        stopVibration()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun resumeSound() {
        val wasMuted = _muted.value
        _muted.value = false
        if (soundReady && (wasMuted || (player == null && currentSound != null))) {
            playSound(currentSound)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun playSound(customUri: Uri?) {


        startVibration()

        if (customUri == null) {
            return
        }
        currentSound = customUri
        soundReady = true
        if (_muted.value) return
        releasePlayer()

        if (_muted.value) return
        val defaultUri =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(
                    RingtoneManager.TYPE_NOTIFICATION
                )

        fun create(uri: Uri): MediaPlayer {
            val mediaPlayer = MediaPlayer()

            try {
                mediaPlayer.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(
                            AudioAttributes.CONTENT_TYPE_SONIFICATION
                        )
                        .build()
                )

                mediaPlayer.setWakeMode(
                    applicationContext,
                    PowerManager.PARTIAL_WAKE_LOCK
                )

                mediaPlayer.isLooping = true
                mediaPlayer.setDataSource(this, uri)

                mediaPlayer.setOnPreparedListener { it.start() }

                mediaPlayer.setOnErrorListener { mp, _, _ ->
                    if (player === mp) {
                        player = null
                    }
                    mp.release()
                    true
                }

                mediaPlayer.prepareAsync()
                return mediaPlayer
            } catch (e: Exception) {
                mediaPlayer.release()
                throw e
            }
        }

        player = try {
            create(customUri ?: defaultUri)
        } catch (_: Exception) {
            runCatching { create(defaultUri) }.getOrNull()
        }
    }


    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val notificationManager = getSystemService(NotificationManager::class.java)

        notificationManager.deleteNotificationChannel("alarm_ringing_v4")

        if (notificationManager.getNotificationChannel(CHANNEL_ID) != null) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            "start",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "destruction"
            setSound(null, null)
            enableVibration(false)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setBypassDnd(true)
        }
        notificationManager.createNotificationChannel(channel)
    }
}

