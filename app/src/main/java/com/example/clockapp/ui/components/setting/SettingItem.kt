package com.example.clockapp.ui.components.setting

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.clockapp.entity.SettingEntity
import com.example.clockapp.entity.SettingType
import com.example.clockapp.ui.components.fields.CustomTextField
import com.example.clockapp.ui.theme.LocalExtendedColors
import com.example.clockapp.viewModels.SettingViewModel

@Composable
fun SettingItem(
    setting: SettingEntity,
    viewModel: SettingViewModel
) {

    val backgroundColor = LocalExtendedColors.current.backgroundColor
    val buttonBackgroundColor = LocalExtendedColors.current.buttonsBackground
    val commonColor = LocalExtendedColors.current.commonContentColor
    val disabledBackground = LocalExtendedColors.current.disabledBackgroundColor

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (setting.type) {
            SettingType.BOOL -> {
                Text(
                    text = stringResource(setting.label),
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = setting.getValue() as? Boolean ?: false,
                    onCheckedChange = { viewModel.setValue(setting.id, it.toString()) },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = buttonBackgroundColor,
                        uncheckedTrackColor = buttonBackgroundColor,
                        checkedThumbColor = commonColor,
                        uncheckedThumbColor = Color.DarkGray,
                        checkedBorderColor = Color.Transparent,
                        uncheckedBorderColor = Color.Transparent
                    )
                )
            }

            SettingType.INT, SettingType.STRING -> {
                var text by remember(setting.id) {
                    mutableStateOf(setting.value ?: setting.defaultValue)
                }

                CustomTextField(
                    text = text,
                    onInputChanged = { input ->
                        if (setting.type == SettingType.INT) {
                            val digits = input.filter { it.isDigit() }
                            text = digits
                            if (digits.toIntOrNull() != null) {
                                viewModel.setValue(setting.id, digits)
                            } else if (digits.isEmpty()) {
                                viewModel.resetValue(setting.id)
                            }
                        } else {
                            text = input
                            viewModel.setValue(setting.id, input)
                        }
                    },
                    label = { Text(stringResource(setting.label)) }
                )
            }
        }
    }
}