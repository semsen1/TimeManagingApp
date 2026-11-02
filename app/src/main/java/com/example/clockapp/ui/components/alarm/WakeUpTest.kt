package com.example.clockapp.ui.components.alarm

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable

@Composable
fun WakeUpTest(wakeUpTest: Int, testStatus: () -> Unit) {
    Column() {
        when (wakeUpTest) {
            1 -> MathTest(Difficulty.EASY, testStatus)

            2 -> MathTest(Difficulty.HARD, testStatus)

            3 -> GraphicTest(testStatus)
        }

    }
}