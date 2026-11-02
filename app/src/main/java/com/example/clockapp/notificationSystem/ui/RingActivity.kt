package com.example.clockapp.notificationSystem.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.clockapp.alarm.AlarmDebug
import com.example.clockapp.notificationSystem.core.RingIntents
import com.example.clockapp.notificationSystem.core.RingKey
import com.example.clockapp.notificationSystem.core.RingRegistry
import com.example.clockapp.notificationSystem.core.Ringable
import com.example.clockapp.notificationSystem.service.RingService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

class RingActivity : ComponentActivity() {

    private val ringKey = MutableStateFlow<RingKey?>(null)
    val testPassed: Boolean = false

    override fun onStart() {
        super.onStart()
        val key = RingKey.from(intent) ?: ringKey.value
        ringKey.value = key
        if (key != null) {
            try {
                startService(RingIntents.service(this, key, RingIntents.ACTION_HIDE_NOTIFICATION))
                startService(RingIntents.suppressVibration(this, key))
            } catch (e: Exception) {
//                AlarmDebug.e("Activity: hide notification failed", e)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        val key = RingKey.from(intent) ?: ringKey.value
        ringKey.value = key
        if (key != null) {
        if (!isFinishing && !isChangingConfigurations && !testPassed) {
            try {
                startService(RingIntents.service(this, key, RingIntents.ACTION_SHOW_NOTIFICATION))
                startService(RingIntents.resumeVibration(this, key))
            } catch (e: Exception) {
//                AlarmDebug.e("Activity: show notification failed", e)
            }
        }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureRingWindow()
        ringKey.value = RingKey.from(intent)

        setContent {
            MaterialTheme {
                val key by ringKey.collectAsStateWithLifecycle()
                RingHost(
                    key = key,
                    onAction = ::sendAction,
                    onClose = ::finish
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        ringKey.value = RingKey.from(intent)
    }

    private fun sendAction(key: RingKey, action: String) {
        sendBroadcast(RingIntents.receiver(this, key, action))
    }

    private fun configureRingWindow() {
    }
}

@Composable
private fun RingHost(
    key: RingKey?,
    onAction: (RingKey, String) -> Unit,
    onClose: () -> Unit
) {
    if (key == null) {
        LaunchedEffect(Unit) { onClose() }
        return
    }

    val appContext = LocalContext.current.applicationContext
    val handler = remember(key.type) { RingRegistry.handlerOf(appContext, key.type) }

    val target by produceState<Ringable?>(initialValue = null, key) {
        value = null
        value = withContext(Dispatchers.IO) { handler.getById(key.id) }
    }

    val ringing by RingService.ringingKey.collectAsStateWithLifecycle()
    var hasBeenRinging by remember(key) { mutableStateOf(false) }
    LaunchedEffect(ringing, key) {
        if (ringing == key) hasBeenRinging = true
        else if (hasBeenRinging) onClose()
    }

    val current = target
    if (current == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        handler.Screen(
            target = current,
            onDismiss = { onAction(key, RingIntents.ACTION_DISMISS) },
            onSnooze = { onAction(key, RingIntents.ACTION_SNOOZE) }
        )
    }
}