val autoConfig = mapOf(
    "IS_DEBUG" to true,  // 是否在debug环境
    "APP_NAME" to "驴游",
    "APPLICATION_ID" to "com.zqc.itinerary",   // 应用包名
    "COMPILE_SDK" to 35,
    "MIN_SDK" to 26,
    "TARGET_SDK" to 35,
    "VERSION_CODE" to 1,
    "VERSION_NAME" to "1.0.0",
    "PRIVACY_URL" to "https://zyuxr.top", // 隐私政策
    "USER_PROTOCOL_URL" to "https://zyuxr.top", // 用户协议
    "FILING_NO" to "", // app备案号
    "JIGUANG_APPKEY" to "da67f3b5b28e13d654e90573", // 极光Key
    "TENCENT_IM_APP_ID" to "1600042812" // 腾讯IM AppId
)

// 将配置项添加到 ext
extra["autoConfig"] = autoConfig
