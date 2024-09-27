package com.example.common.listener.im.sdk

import android.util.Log
import com.example.common.data.Constants.TIM_TAG
import com.example.common.flowbus.FlowBus
import com.tencent.imsdk.v2.V2TIMSDKListener
import com.tencent.imsdk.v2.V2TIMUserFullInfo
import javax.inject.Inject

class V2TIMSDKListener @Inject constructor() : V2TIMSDKListener() {

    override fun onConnecting() {
        Log.i(TIM_TAG, "正在连接到腾讯云服务器")
    }

    override fun onConnectSuccess() {
        Log.i(TIM_TAG, "已经成功连接到腾讯云服务器")
    }

    override fun onConnectFailed(code: Int, error: String?) {
        Log.i(TIM_TAG, "连接腾讯云服务器失败")
        FlowBus.post(ConnectFailed(code, error))
    }

    override fun onKickedOffline() {
        Log.i(TIM_TAG, "当前用户被踢下线")
        FlowBus.post(KickedOffline("当前用户被踢下线"))
    }

    override fun onUserSigExpired() {
        // 如果收到 onUserSigExpired 回调，说明您登录用的 UserSig 票据已经过期，请使用新签发的 UserSig 进行重新登录。
        // 如果继续使用过期的 UserSig，会导致 IM SDK 登录进入死循环。
        Log.i(TIM_TAG, "登录票据已经过期")
        FlowBus.post(UserSigExpired("登录票据已经过期"))
    }

    override fun onSelfInfoUpdated(info: V2TIMUserFullInfo) {
        Log.i(TIM_TAG, "当前用户的资料发生了更新")
        FlowBus.post(SelfInfoUpdated(info))
    }

}