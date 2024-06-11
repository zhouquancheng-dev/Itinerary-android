package com.example.common.config

/**
 * @property IS_DEBUG 是否处于debug环境
 * @property APPLICATION_ID 应用包名
 * @property PRIVACY_URL 隐私协议
 * @property USER_PROTOCOL_URL 用户协议
 * @property FILING_NO 备案号
 * @property JIGUANG_APPKEY 极光认证appkey
 */
object AppConfig {
    var IS_DEBUG: Boolean = true
    var APPLICATION_ID: String = ""
    var PRIVACY_URL: String = ""
    var USER_PROTOCOL_URL: String = ""
    var FILING_NO: String = ""
    var JIGUANG_APPKEY: String = ""
}