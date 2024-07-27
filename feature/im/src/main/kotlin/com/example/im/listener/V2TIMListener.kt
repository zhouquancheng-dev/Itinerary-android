package com.example.im.listener

import com.example.im.listener.conversation.V2TIMConversationListener
import com.example.im.listener.sdk.V2TIMSDKListener
import com.tencent.imsdk.v2.V2TIMManager
import javax.inject.Inject

class V2TIMListener @Inject constructor(
    private val v2TIMSDKListener: V2TIMSDKListener,
    private val v2TIMConversationListener: V2TIMConversationListener
) {

    @ListenerMethod
    fun registerV2TIMSDKListener() {
        V2TIMManager.getInstance().addIMSDKListener(v2TIMSDKListener)
        V2TIMManager.getConversationManager().addConversationListener(v2TIMConversationListener)
    }

    @UnregisterMethod
    fun unregisterV2TIMSDKListener() {
        V2TIMManager.getInstance().removeIMSDKListener(v2TIMSDKListener)
        V2TIMManager.getConversationManager().removeConversationListener(v2TIMConversationListener)
    }

}