package com.example.clockapp.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clockapp.ui.theme.LocalExtendedColors

@Composable
fun AddEntityButton(modalState: MutableState<Boolean>) {
    val backgroundColor = LocalExtendedColors.current.backgroundColor
    val commonColor = LocalExtendedColors.current.commonContentColor

    Row() {
        Button(
            onClick = { modalState.value = true },
            colors = ButtonDefaults.buttonColors(
                contentColor = commonColor,
                containerColor = backgroundColor
            ),
            shape = CircleShape,
            modifier = Modifier.size(60.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text("+", fontSize = 32.sp)
        }
    }

}