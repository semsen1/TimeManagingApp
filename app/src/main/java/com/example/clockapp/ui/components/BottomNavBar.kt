package com.example.clockapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.clockapp.models.NavButtons
import com.example.clockapp.ui.theme.LocalExtendedColors


@Composable
fun BottomNavBar(navController: NavController, padding: PaddingValues) {
    val inactiveColor = Color.Gray
    val commonContentColor = LocalExtendedColors.current.commonContentColor
    val currentRoute = navController.currentDestination?.route
    val BottomNavItems = NavButtons.getNavButtons()
    Box(
        modifier = Modifier
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomNavItems.forEach { it ->
                Button(
                    onClick = { navController.navigate(it.route) },
                    shape = CircleShape,
                    modifier = Modifier
                        .size(50.dp + padding.calculateBottomPadding() + 5.dp)
                        .weight(1f),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = commonContentColor,
                        containerColor = Color.Transparent
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            painter = painterResource(it.icon),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp),
                            tint = if (currentRoute == it.route) commonContentColor else inactiveColor,
                        )
                    }

                }
            }
        }
    }

}