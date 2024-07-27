package com.example.network.captcha

import com.example.model.captcha.SuccessResponse

/**
 * 给行为验证码提供的验证结果回调接口
 * @property onSuccess 验证成功
 * @property onFailure 验证失败
 * @property onError 验证错误
 */
interface CaptchaListener {
    fun onSuccess(response: SuccessResponse)
    fun onFailure(message: String) {}
    fun onError(error: String) {}
}