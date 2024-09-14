package com.example.login.state

internal enum class DialogType(val dialogText: String) {
    NONE(""),
    PULL_AUTH("拉起登录中"),
    LOGIN("正在登录")
}
