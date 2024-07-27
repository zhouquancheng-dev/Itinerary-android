package com.example.login.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.view.ToasterUtil.ToastStatus.ERROR
import com.example.ui.view.ToasterUtil.showCustomToaster
import com.example.login.R
import com.example.login.state.isValidPhoneNumber
import com.example.ui.components.symbols.rememberClose
import com.example.ui.theme.JetItineraryTheme

@Composable
fun PhoneNumberTextField(
    value: String,
    onValueChange: (String) -> Unit,
    visualTransformation: VisualTransformation,
    onClearValue: () -> Unit
) {
    val isError = remember(value) { !isValidPhoneNumber(value) && value.length == 11 }
    if (isError) {
        showCustomToaster(stringResource(R.string.phoneNumber_error), ERROR)
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        textStyle = TextStyle(fontSize = 21.sp, fontWeight = FontWeight.SemiBold),
        placeholder = {
            Text(stringResource(R.string.text_field_hint))
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onClearValue() }) {
                    Icon(
                        imageVector = rememberClose(),
                        contentDescription = "clear",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        prefix = {
            Text("+86", fontSize = 16.sp)
        },
        isError = isError,
        shape = RoundedCornerShape(48.dp),
        singleLine = true,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedPlaceholderColor = colorResource(R.color.placeholder_text),
            unfocusedPlaceholderColor = colorResource(R.color.placeholder_text),
            errorContainerColor = Color.Transparent,
            errorTextColor = MaterialTheme.colorScheme.error,
            errorPrefixColor = MaterialTheme.colorScheme.onSurfaceVariant,
            errorTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    )
}

@PreviewLightDark
@Composable
private fun PhoneNumberTextFieldPreview() {
    val value by remember { mutableStateOf("") }
    JetItineraryTheme {
        PhoneNumberTextField(
            value = value,
            onValueChange = {},
            visualTransformation = VisualTransformation.None,
            onClearValue = {}
        )
    }
}