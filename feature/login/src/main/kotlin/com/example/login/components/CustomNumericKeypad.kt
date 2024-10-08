package com.example.login.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.login.R
import com.example.ui.components.ThemePreviews
import com.example.ui.components.resolveColor
import com.example.ui.theme.JetItineraryTheme

@Composable
fun CustomNumericKeypad(
    modifier: Modifier = Modifier,
    onKeyPress: (KeypadAction) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        items(listOf("1", "2", "3", "4", "5", "6", "7", "8", "9")) { key ->
            KeyButton(
                key = key,
                haptic = haptic,
                onKeyPress = onKeyPress
            )
        }
        item(span = { GridItemSpan(2) }) {
            KeyButton(
                key = "0",
                haptic = haptic,
                onKeyPress = onKeyPress
            )
        }
        item {
            IconKeyButton(
                icon = painterResource(R.drawable.ic_delete),
                haptic = haptic,
                onKeyPress = onKeyPress
            )
        }
    }
}

@Composable
private fun KeyButton(key: String, haptic: HapticFeedback, onKeyPress: (KeypadAction) -> Unit) {
    Button(
        onClick = {
            onKeyPress(KeypadAction.fromKey(key))
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        },
        modifier = Modifier.height(60.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = resolveColor(Color(0xFF4C4C4C), Color(0xFF606060)),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(10)
    ) {
        Text(
            text = key,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Composable
private fun IconKeyButton(icon: Painter, haptic: HapticFeedback, onKeyPress: (KeypadAction) -> Unit) {
    FilledIconButton(
        onClick = {
            onKeyPress(KeypadAction.Delete)
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        },
        modifier = Modifier.height(60.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = resolveColor(Color(0xFF4C4C4C), Color(0xFF606060)),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(10)
    ) {
        Icon(painter = icon, contentDescription = "Delete", modifier = Modifier.size(28.dp))
    }
}

enum class KeypadAction(val value: String) {
    One("1"), Two("2"), Three("3"), Four("4"), Five("5"),
    Six("6"), Seven("7"), Eight("8"), Nine("9"), Zero("0"), Delete("");

    companion object {
        fun fromKey(key: String): KeypadAction {
            return when (key) {
                "1" -> One
                "2" -> Two
                "3" -> Three
                "4" -> Four
                "5" -> Five
                "6" -> Six
                "7" -> Seven
                "8" -> Eight
                "9" -> Nine
                "0" -> Zero
                "delete" -> Delete
                else -> throw IllegalArgumentException("Unknown key: $key")
            }
        }
    }
}

@ThemePreviews
@Composable
private fun CustomNumericKeypadPreview() {
    JetItineraryTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            CustomNumericKeypad(onKeyPress = {})
        }
    }
}