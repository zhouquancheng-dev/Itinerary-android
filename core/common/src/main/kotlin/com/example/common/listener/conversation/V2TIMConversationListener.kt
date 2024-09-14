package com.example.common.listener.conversation

import android.util.Log
import com.example.common.data.Constants.TIM_TAG
import com.example.common.flowbus.FlowBus
import com.tencent.imsdk.v2.V2TIMConversation
import com.tencent.imsdk.v2.V2TIMConversationListFilter
import com.tencent.imsdk.v2.V2TIMConversationListener
import javax.inject.Inject

/**
 * 获取会话列表变更的通知
 */
class V2TIMConversationListener @Inject constructor() : V2TIMConversationListener() {

    override fun onSyncServerStart() {
        // 同步服务器会话开始
        Log.i(TIM_TAG, "同步服务器会话开始")
    }

    override fun onSyncServerFinish() {
        // 同步服务器会话完成
        Log.i(TIM_TAG, "同步服务器会话完成")
    }

    override fun onSyncServerFailed() {
        // 同步服务器会话失败
        Log.i(TIM_TAG, "同步服务器会话失败")
    }

    override fun onNewConversation(conversationList: List<V2TIMConversation>) {
        // 有会话新增（这个新增指的是会话列表有新增，比如一个新好友发来消息，不是某个会话内容新增）
        Log.i(TIM_TAG, "有会话新增")
        FlowBus.post(NewConversationEvent(conversationList))
    }

    override fun onConversationChanged(conversationList: List<V2TIMConversation>) {
        // 有会话更新，例如未读计数发生变化、最后一条消息被更新等，此时可以重新对会话列表做排序
        Log.i(TIM_TAG, "有会话更新")
        FlowBus.post(ConversationChangedEvent(conversationList))
    }

    override fun onConversationDeleted(conversationIDList: List<String>) {
        // 有会话被删除
        Log.i(TIM_TAG, "有会话被删除")
        FlowBus.post(ConversationDeletedEvent(conversationIDList))
    }

    override fun onTotalUnreadMessageCountChanged(totalUnreadCount: Long) {
        // 会话未读总数变更通知
        Log.i(TIM_TAG, "会话未读总数变更通知")
        FlowBus.post(TotalUnreadMessageCountChangedEvent(totalUnreadCount))
    }

    override fun onUnreadMessageCountChangedByFilter(filter: V2TIMConversationListFilter, totalUnreadCount: Long) {
        // 过滤条件下未读总数变更通知
        Log.i(TIM_TAG, "过滤条件下未读总数变更通知")
        FlowBus.post(UnreadMessageCountChangedByFilterEvent(filter, totalUnreadCount))
    }

}