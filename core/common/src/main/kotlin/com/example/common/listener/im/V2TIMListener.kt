package com.example.common.listener.im

import com.example.common.listener.im.conversation.V2TIMConversationListener
import com.tencent.imsdk.v2.V2TIMManager
import javax.inject.Inject

class V2TIMListener @Inject constructor(
    private val v2TIMConversationListener: V2TIMConversationListener
) {

    @ListenerMethod
    fun registerV2TIMSDKListener() {
        V2TIMManager.getConversationManager().addConversationListener(v2TIMConversationListener)
    }

    @UnregisterMethod
    fun unregisterV2TIMSDKListener() {
        V2TIMManager.getConversationManager().removeConversationListener(v2TIMConversationListener)
    }

}