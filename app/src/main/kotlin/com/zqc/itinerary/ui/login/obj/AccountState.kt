package com.zqc.itinerary.ui.login.obj

/*
* 用户账号正则
* 匹配数字
*/
private const val EMAIL_VALIDATION_REGEX = "^\\d+\$"

class UserState :
    TextFieldState(validator = ::isEmailValid, errorFor = emailValidationError())

private fun emailValidationError(): String {
    return ""
}

private fun isEmailValid(email: String): Boolean {
    return email.isNotBlank() && email.matches(Regex(EMAIL_VALIDATION_REGEX))
}