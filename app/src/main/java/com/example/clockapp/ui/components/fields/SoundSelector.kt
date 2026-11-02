package com.example.clockapp.ui.components.fields

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RawRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import com.example.clockapp.R
import com.example.clockapp.ui.theme.LocalExtendedColors

data class SoundItem(
    val title: String,
    val uri: Uri
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundSelector(
    selectedUri: Uri?,
    onSoundSelected: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val commonColor = LocalExtendedColors.current.commonContentColor
    val alarmSound = stringResource(R.string.alarm)
    val fileAccessError = stringResource(R.string.file_access_error)
    val sounds = remember(context.packageName) {
        fun rawUri(@RawRes resourceId: Int): Uri {
            return "android.resource://${context.packageName}/$resourceId".toUri()
        }

        listOf(
            SoundItem(alarmSound, rawUri(R.raw.sound1)),
        )
    }

    val openSoundPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                onSoundSelected(uri)
            } catch (_: SecurityException) {
                Toast.makeText(
                    context,
                    fileAccessError,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    val selectedTitle = when (selectedUri) {
        null -> stringResource(R.string.select_sound)

        else -> sounds.firstOrNull { it.uri == selectedUri }?.title
            ?: stringResource(R.string.uploaded_file)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedTitle,
            onValueChange = {},
            readOnly = true,
            label = {
                Text(stringResource(R.string.sound))
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            modifier = Modifier
                .menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable
                )

                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = commonColor,
                focusedLabelColor = commonColor
            ),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            sounds.forEach { sound ->
                DropdownMenuItem(
                    text = { Text(sound.title) },
                    onClick = {
                        expanded = false
                        onSoundSelected(sound.uri)
                    }
                )
            }

            HorizontalDivider()

            DropdownMenuItem(
                text = { Text(stringResource(R.string.upload)) },
                onClick = {
                    expanded = false
                    openSoundPicker.launch(arrayOf("audio/*"))
                }
            )
        }
    }
}