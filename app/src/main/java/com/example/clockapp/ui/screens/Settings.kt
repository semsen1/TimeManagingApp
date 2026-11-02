package com.example.clockapp.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.clockapp.R
import com.example.clockapp.ui.components.BottomNavBar
import com.example.clockapp.ui.components.CommonScreen
import com.example.clockapp.ui.components.setting.SettingItem
import com.example.clockapp.viewModels.SettingViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Settings(
    navController: NavHostController,
    viewModel: SettingViewModel
) {
    val listState = rememberLazyListState()
    val settingsData by viewModel.settings.collectAsStateWithLifecycle()
    Scaffold(  bottomBar = { BottomNavBar(navController = navController, padding = PaddingValues()) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CommonScreen(
                R.drawable.ic_settings,
                R.string.settings
            )

            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(top = 8.dp)
                ) {
                    items(settingsData) { setting ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            SettingItem(
                                setting = setting,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }

}