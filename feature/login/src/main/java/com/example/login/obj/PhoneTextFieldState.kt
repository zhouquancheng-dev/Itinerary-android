package com.example.login.obj

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

// 手机号(mobile phone)中国(严谨), 根据工信部2019年最新公布的手机号段
private const val PHONE_VALIDATION_REGEX =
    "^(?:(?:\\+|00)86)?1(?:3\\d|4[5-79]|5[0-35-9]|6[5-7]|7[0-8]|8\\d|9[1589])\\d{8}$"

fun isValidPhoneNumber(phoneNumber: String): Boolean {
    return !phoneNumber.matches(Regex(PHONE_VALIDATION_REGEX)) && phoneNumber.isNotBlank()
}

/**
 * @param phoneNumber 手机号
 * @return 正则限定后的字符串
 */
fun formatPhoneNumber(phoneNumber: String): String {
    return phoneNumber.replace(Regex("\\D"), "")
}

/**
 * 对输入的手机号进行 3-4-4 的格式进行转换显示
 */
class PhoneNumberVisualTransformation(private val phoneNumber: String) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val formattedText = buildAnnotatedString {
            var index = 0
            phoneNumber.forEach { char ->
                if (index == 3 || index == 7) {
                    append(" ")
                }
                append(char)
                index++
            }
        }
        return TransformedText(formattedText, PhoneNumberOffsetMapping)
    }
}

/**
 * 在原始文本和转换后的文本之间提供双向偏移映射
 */
private object PhoneNumberOffsetMapping : OffsetMapping {
    override fun originalToTransformed(offset: Int): Int {
        var transformedOffset = offset
        var index = 0

        while (index < offset) {
            if (index == 3 || index == 7) {
                // Account for the extra spaces in the transformed text
                transformedOffset++
            }
            index++
        }
        return transformedOffset
    }

    override fun transformedToOriginal(offset: Int): Int {
        var originalOffset = offset
        var index = 0

        while (index < offset) {
            if (index == 3 || index == 7) {
                // Account for the removed spaces in the original text
                originalOffset--
            }
            index++
        }
        return originalOffset
    }
}