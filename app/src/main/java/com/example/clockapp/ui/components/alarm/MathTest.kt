package com.example.clockapp.ui.components.alarm

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.clockapp.R
import com.example.clockapp.ui.components.fields.CustomTextField
import com.example.clockapp.ui.theme.LocalExtendedColors
import kotlin.random.Random

enum class Difficulty(val title: String) {
    EASY("Easy"),
    HARD("Hard")
}

data class MathTask(
    val question: String,
    val answer: Int
)

fun generateTask(
    difficulty: Difficulty,
    random: Random = Random.Default,
    context: Context
): MathTask =
    when (difficulty) {
        Difficulty.EASY -> generateEasy(random, context)
        Difficulty.HARD -> generateHard(random, context)
    }

private fun generateEasy(random: Random, context: Context): MathTask = when (random.nextInt(5)) {

    0 -> {
        val a = (2..60).random(random)
        val b = (2..40).random(random)
        MathTask("$a + $b = ?", a + b)
    }

    1 -> {
        val a = (10..99).random(random)
        val b = (1..a).random(random)
        MathTask("$a − $b = ?", a - b)
    }

    2 -> {
        val a = (2..9).random(random)
        val b = (2..9).random(random)
        MathTask("$a × $b = ?", a * b)
    }

    3 -> {
        // x + a = b
        val x = (1..50).random(random)
        val a = (1..50).random(random)
        MathTask(context.getString(R.string.find) + " x: x + $a = ${x + a}", x)
    }

    else -> {
        // x - a = b, where b >= 1
        val a = (1..30).random(random)
        val b = (1..30).random(random)
        MathTask(context.getString(R.string.find) + " x: x − $a = $b", a + b)
    }
}

private fun generateHard(random: Random, context: Context): MathTask = when (random.nextInt(4)) {
    0 -> {
        val a = (10..99).random(random)
        val b = (10..99).random(random)
        MathTask("$a × $b = ?", a * b)
    }

    1 -> {
        val divisor = (10..49).random(random)
        val quotient = (2..(99 / divisor)).random(random)
        MathTask("${divisor * quotient} ÷ $divisor = ?", quotient)
    }

    2 -> {
        // a * x = b
        val a = (2..19).random(random)
        val x = (2..20).random(random)
        MathTask(context.getString(R.string.find) + " x: $a × x = ${a * x}", x)
    }

    else -> {
        // x / a = b
        val a = (2..12).random(random)
        val b = (2..20).random(random)
        MathTask(context.getString(R.string.find) + " x: x ÷ $a = $b", a * b)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MathTest(difficulty: Difficulty, testStatus: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MathTaskCard(difficulty = difficulty, testStatus)
    }
}

@Composable
fun MathTaskCard(
    difficulty: Difficulty,
    testStatus: () -> Unit,
    modifier: Modifier = Modifier
) {

    val backgroundColor = LocalExtendedColors.current.backgroundColor
    val commonColor = LocalExtendedColors.current.commonContentColor
    val buttonBackgroundColor = LocalExtendedColors.current.buttonsBackground

    var round by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    val task =
        remember(difficulty, round) { generateTask(difficulty = difficulty, context = context) }

    var input by remember(task) { mutableStateOf("") }
    var isCorrect by remember(task) { mutableStateOf<Boolean?>(null) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = task.question,
            style = MaterialTheme.typography.headlineMedium,
            color = commonColor
        )

        CustomTextField(
            text = input,
            onInputChanged = { value ->
                input = value.filter { it.isDigit() }.take(6)
                isCorrect = null
            },
            label = {
                Text(
                    text = stringResource(R.string.your_answer),
                    color = commonColor
                )
            }
        )

        if (isCorrect == null) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { isCorrect = input.toIntOrNull() == task.answer },
                    enabled = input.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = commonColor,
                        containerColor = backgroundColor
                    ),
                ) {
                    Text(
                        text = stringResource(R.string.check),
                        color = commonColor
                    )
                }

            }
        }

        when (isCorrect) {
            true -> {
                Text(
                    text = stringResource(R.string.correct),
                    color = commonColor
                )
                testStatus()
            }

            false -> Text(
                text = stringResource(R.string.wrong_answer),
                color = MaterialTheme.colorScheme.error
            )

            null -> Unit
        }
    }
}