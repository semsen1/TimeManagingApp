package com.example.clockapp.notificationSystem.core

import androidx.compose.runtime.Composable

interface RingHandler {
    suspend fun getById(id: Int): Ringable?
    suspend fun getAll(): List<Ringable>
    suspend fun disable(id: Int)
    suspend fun onDismissed(target: Ringable) {}

    @Composable
    fun Screen(target: Ringable, onDismiss: () -> Unit, onSnooze: () -> Unit)
}