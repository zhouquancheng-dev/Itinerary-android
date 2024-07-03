package com.example.common.listener

import com.blankj.utilcode.util.LogUtils
import com.example.common.data.Constants.TIM_TAG
import com.tencent.imsdk.v2.V2TIMSDKListener
import com.tencent.imsdk.v2.V2TIMUserFullInfo

class TIMSDKListener : V2TIMSDKListener() {

    override fun onConnecting() {
        LogUtils.iTag(TIM_TAG, "正在连接到腾讯云服务器")
    }

    override fun onConnectSuccess() {
        LogUtils.iTag(TIM_TAG, "已经成功连接到腾讯云服务器")
    }

    override fun onConnectFailed(code: Int, error: String?) {
        LogUtils.iTag(TIM_TAG, "连接腾讯云服务器失败")
    }

    override fun onKickedOffline() {
        LogUtils.iTag(TIM_TAG, "当前用户被踢下线")
    }

    override fun onUserSigExpired() {
        // 如果收到 onUserSigExpired 回调，说明您登录用的 UserSig 票据已经过期，请使用新签发的 UserSig 进行重新登录。
        // 如果继续使用过期的 UserSig，会导致 IM SDK 登录进入死循环。
        LogUtils.iTag(TIM_TAG, "登录票据已经过期")
    }

    override fun onSelfInfoUpdated(info: V2TIMUserFullInfo?) {
        LogUtils.iTag(TIM_TAG, "当前用户的资料发生了更新")
    }

}