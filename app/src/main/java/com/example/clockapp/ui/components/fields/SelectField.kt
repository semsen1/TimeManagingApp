package com.example.clockapp.ui.components.fields

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clockapp.models.SelectItem
import com.example.clockapp.ui.theme.LocalExtendedColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectField(
    selectedItem: MutableState<SelectItem>,
    values: List<SelectItem>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val contentColor = LocalExtendedColors.current.commonContentColor
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val textStyle = LocalTextStyle.current.copy(
        fontSize = 20.sp,
        color = contentColor
    )
    val labels = values.map { it.label } + selectedItem.value.label
    val maxTextWidthPx = labels.maxOfOrNull { label ->
        textMeasurer.measure(
            text = label,
            style = textStyle,
            softWrap = false,
            maxLines = 1
        ).size.width
    } ?: 0
    val maxTextWidth = with(density) { maxTextWidthPx.toDp() }
    val horizontalPadding = 8.dp
    val iconWidth = 24.dp
    val iconSpacing = 8.dp
    val fieldWidth =
        maxTextWidth + horizontalPadding * 2 + iconSpacing + iconWidth
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.width(fieldWidth)
    ) {
        BasicTextField(
            value = selectedItem.value.label,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            textStyle = textStyle,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable
                )
                .drawBehind {
                    val strokeWidth = 2.dp.toPx()
                    val y = size.height - strokeWidth / 2
                    drawLine(
                        color = Color.Gray,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                },
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = horizontalPadding,
                            vertical = 6.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        innerTextField()
                    }
                    Spacer(Modifier.width(iconSpacing))
                    Box(
                        modifier = Modifier.size(iconWidth),
                        contentAlignment = Alignment.Center
                    ) {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    }
                }
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            values.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item.label,
                            fontSize = 20.sp
                        )
                    },
                    onClick = {
                        selectedItem.value = item
                        expanded = false
                    }
                )
            }
        }
    }
}