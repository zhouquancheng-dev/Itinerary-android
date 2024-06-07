package com.example.login.obj

/*
 * 密码强度正则
 * 大写字母
 * 小写字母
 * 数字
 * 特殊字符 (. , ; : ! @ # $ % ^ & < > ( ) * ? = + -)
 * 6位以上且包含任意3项及以上的类型
 */
private const val PASSWORD_VALIDATION_REGEX =
    "^(?=.*[A-Z])|(?=.*[a-z])|(?=.*\\d)|(?=.*[.,;:!@#$%^&<>()*?=+-])\\S{6,}\$"

class PasswordState :
    TextFieldState(validator = ::isPasswordValid, errorFor = passwordValidationError())

class ConfirmPasswordState(private val passwordState: PasswordState) : TextFieldState() {
    override val isValid
        get() = isPasswordAndConfirmationValid(passwordState.text, text)

    override fun getError(): String? {
        return if (showErrors()) {
            passwordConfirmationError()
        } else {
            null
        }
    }
}

private fun isPasswordValid(password: String): Boolean {
    return password.isNotBlank() && password.matches(Regex(PASSWORD_VALIDATION_REGEX))
}

private fun isPasswordAndConfirmationValid(password: String, confirmedPassword: String): Boolean {
    return isPasswordValid(password) && password == confirmedPassword
}

private fun passwordValidationError(): String {
    return ""
}

private fun passwordConfirmationError(): String {
    return ""
}