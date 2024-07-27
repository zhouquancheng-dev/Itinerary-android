package com.example.login.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.login.R
import com.example.ui.components.symbols.rememberCheckCircle
import com.example.ui.components.symbols.rememberRadioButtonUnchecked
import com.example.ui.dialog.TermsAndConditions

@Composable
fun PrivacyContent(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val selectedIcon = rememberCheckCircle()
    val unselectedIcon = rememberRadioButtonUnchecked()
    val haptic = LocalHapticFeedback.current

    val fullText = stringResource(R.string.full_terms)
    val firstTag = stringResource(R.string.first_terms)
    val secondTag = stringResource(R.string.second_terms)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally)
    ) {
        Icon(
            imageVector = if (checked) selectedIcon else unselectedIcon,
            contentDescription = null,
            modifier = Modifier
                .padding(top = 3.dp)
                .clip(CircleShape)
                .size(26.dp)
                .toggleable(
                    value = checked,
                    onValueChange = {
                        onCheckedChange(it)
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    role = Role.RadioButton
                ),
            tint = MaterialTheme.colorScheme.primary
        )

        TermsAndConditions(fullText, firstTag, secondTag)
    }
}
