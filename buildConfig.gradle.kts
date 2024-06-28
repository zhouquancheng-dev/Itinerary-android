val autoConfig = mapOf(
    "IS_DEBUG" to true,  // 是否在debug环境
    "APPLICATION_ID" to "com.zqc.itinerary",   // 应用包名
    "COMPILE_SDK" to 34,
    "MIN_SDK" to 24,
    "TARGET_SDK" to 34,
    "VERSION_CODE" to 1,
    "VERSION_NAME" to "1.0.0",
    "PRIVACY_URL" to "https://composables.com/privacy", // 隐私政策
    "USER_PROTOCOL_URL" to "https://composables.com/terms", // 用户协议
    "FILING_NO" to "", // app备案号
    "JIGUANG_APPKEY" to "9a6a8d9363893aeafbff867e", // 极光Key
)

// 将配置项添加到 ext
extra["autoConfig"] = autoConfig
