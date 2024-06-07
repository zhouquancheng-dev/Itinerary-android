package com.example.login.obj

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

open class TextFieldState(
    private val validator: (String) -> Boolean = { true },
    private val errorFor: String = ""
) {
    var text: String by mutableStateOf("")

    private val isTextNull: Boolean
        get() {
            return text.isNotEmpty()
        }

    open val isValid: Boolean
        get() = validator(text)

    fun showErrors(): Boolean {
        return !isValid && isTextNull
    }

    open fun getError(): String? {
        return if (showErrors()) {
            errorFor
        } else {
            null
        }
    }
}