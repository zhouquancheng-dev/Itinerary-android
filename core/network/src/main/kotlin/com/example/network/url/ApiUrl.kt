package com.example.network.url

const val APP_LOCAL_URL = "https://192.168.31.173:8080"
const val APP_BASE_URL = "https://api.zyuxr.top"

// 行为验证码二次核验
const val ALI_CAPTCHA = "/validate/aliCaptcha"

// 发送验证码
const val ALI_SEND_CODE = "/login/sendSmsCode"

// 校验验证码
const val ALI_VERIFY_CODE = "/login/verifyCode"

// 极光一键登录验证
const val JG_AUTH_LOGIN = "/login/tokenVerify"

// TIM登录票据
const val TIM_USER_SIG = "/tim/userSig"