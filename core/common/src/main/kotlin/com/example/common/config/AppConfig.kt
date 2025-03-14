package com.example.common.config

/**
 * @property DEBUG 是否处于debug环境
 * @property APP_NAME 应用名称
 * @property APPLICATION_ID 应用包名
 * @property PRIVACY_URL 隐私协议
 * @property USER_PROTOCOL_URL 用户协议
 * @property FILING_NO 备案号
 * @property JIGUANG_APPKEY 极光认证 appkey
 * @property TENCENT_IM_APP_ID 腾讯IM appId
 */
object AppConfig {
    var DEBUG: Boolean = true
    var APP_NAME: String = ""
    var APPLICATION_ID: String = ""
    var PRIVACY_URL: String = ""
    var USER_PROTOCOL_URL: String = ""
    var FILING_NO: String = ""
    var JIGUANG_APPKEY: String = ""
    var TENCENT_IM_APP_ID: Int = 0
}