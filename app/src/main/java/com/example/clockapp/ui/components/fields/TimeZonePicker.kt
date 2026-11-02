package com.example.clockapp.ui.components.fields

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.clockapp.R
import com.example.clockapp.ui.theme.LocalExtendedColors
import java.util.TimeZone

private val allZones: List<String> by lazy {
    TimeZone.getAvailableIDs()
        .filter { it.contains('/') && !it.startsWith("Etc/") && !it.startsWith("SystemV/") }
        .sorted()
}

private fun offsetLabel(id: String): String {
    val offsetMin = TimeZone.getTimeZone(id).rawOffset / 60_000
    val sign = if (offsetMin >= 0) "+" else "-"
    val abs = kotlin.math.abs(offsetMin)
    return "UTC%s%02d:%02d".format(sign, abs / 60, abs % 60)
}

@Composable
fun TimeZonePicker(
    title: String = stringResource(R.string.time_zone),
    onSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    onReset: (() -> Unit)? = null
) {
    val backgroundColor = LocalExtendedColors.current.secondBackgroundColor
    val commonColor = LocalExtendedColors.current.commonContentColor
    var query by remember { mutableStateOf("") }
    val filtered = remember(query) {
        if (query.isBlank()) allZones
        else allZones.filter { it.contains(query.trim(), ignoreCase = true) }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 10.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                color = commonColor
            )
            IconButton(
                onClick = { onDismiss() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = commonColor
                )
            }


        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text(stringResource(R.string.time_zone_search)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = commonColor,
                focusedLabelColor = commonColor
            ),
        )
        Spacer(Modifier.height(8.dp))
        LazyColumn(Modifier.weight(1f)) {
            if (onReset != null && query.isBlank()) {
                item(key = "local") {
                    ListItem(
                        headlineContent = { Text("Локальное") },
                        modifier = Modifier.clickable { onReset() }
                    )
                    HorizontalDivider()
                }
            }
            items(filtered, key = { it }) { id ->
                ListItem(
                    headlineContent = { Text(id.replace('_', ' ')) },
                    supportingContent = { Text(offsetLabel(id)) },
                    modifier = Modifier.clickable { onSelected(id) }
                )
                HorizontalDivider()
            }
        }
    }
}